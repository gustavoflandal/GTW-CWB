package muralha.digital.ocr;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementação JDBC do {@link RepositorioOcr}. A conexão é do chamador. */
public class JdbcRepositorioOcr implements RepositorioOcr {

    private final Connection conn;

    public JdbcRepositorioOcr(Connection conn) {
        this.conn = conn;
    }

    /** Se a migração 20261016_ocr_score_revisao_placas.sql já foi executada. */
    public boolean estruturaInstalada() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CASE WHEN OBJECT_ID('muralha.vtr_ocr_avaliacao', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_revisao_placa', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_placa_leitura', 'U') IS NOT NULL THEN 1 ELSE 0 END");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    /** Passagens das últimas {@code janelaHoras} horas ainda não avaliadas, das mais antigas para as mais novas. */
    public List<PassagemPendente> pendentes(int janelaHoras, int lote) throws SQLException {
        List<PassagemPendente> r = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP (?) v.id, v.id_local, v.data, v.data_importado, v.id_captura, v.placa, v.placa_frontal " +
                "FROM muralha.veiculo_tempo_real v " +
                "WHERE v.data >= DATEADD(HOUR, -?, GETDATE()) " +
                "  AND NOT EXISTS (SELECT 1 FROM muralha.vtr_ocr_avaliacao a WHERE a.id_veiculo_tempo_real = v.id) " +
                "ORDER BY v.data")) {
            ps.setInt(1, lote);
            ps.setInt(2, janelaHoras);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    r.add(new PassagemPendente(rs.getString("id"), rs.getInt("id_local"), rs.getTimestamp("data"),
                        rs.getTimestamp("data_importado"), rs.getString("id_captura"), rs.getString("placa"),
                        rs.getString("placa_frontal")));
                }
            }
        }
        return r;
    }

    /** Configuração ativa por equipamento (origem e limiar opcionais). */
    public Map<Integer, ProcessadorOcr.ConfigEquipamento> configuracoes() throws SQLException {
        Map<Integer, ProcessadorOcr.ConfigEquipamento> r = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, origem_score, campo, limiar FROM muralha.equipamento_ocr_config WHERE ativo = 1");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrigemScore origem = null;
                if (rs.getString("origem_score") != null) {
                    try {
                        origem = OrigemScore.de(OrigemScore.tipoDe(rs.getString("origem_score")), rs.getString("campo"));
                    } catch (IllegalArgumentException ignorada) {
                        // configuração inválida: usa a origem padrão
                    }
                }
                BigDecimal limiar = rs.getBigDecimal("limiar");
                r.put(rs.getInt("id_local"), new ProcessadorOcr.ConfigEquipamento(origem, limiar == null ? null : limiar.doubleValue()));
            }
        }
        return r;
    }

    @Override
    public List<LeituraPlaca> leiturasGravadas(String idVeiculoTempoReal) throws SQLException {
        return leituras(conn, idVeiculoTempoReal);
    }

    /** Todas as leituras da passagem, por origem e ordem. */
    public static List<LeituraPlaca> leituras(Connection conn, String idVeiculoTempoReal) throws SQLException {
        List<LeituraPlaca> r = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ordem, tipo, placa, score, origem FROM muralha.vtr_placa_leitura " +
                "WHERE id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER) ORDER BY origem, ordem")) {
            ps.setString(1, idVeiculoTempoReal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal score = rs.getBigDecimal("score");
                    r.add(new LeituraPlaca(rs.getInt("ordem"), LeituraPlaca.Tipo.valueOf(rs.getString("tipo")),
                        rs.getString("placa"), score == null ? null : score.doubleValue(),
                        LeituraPlaca.Origem.valueOf(rs.getString("origem"))));
                }
            }
        }
        return r;
    }

    @Override
    public String dadoBruto(String idCaptura) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dado_bruto FROM ia.veiculo_caracteristica WHERE id_captura = CAST(? AS UNIQUEIDENTIFIER)")) {
            ps.setString(1, idCaptura);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    @Override
    public void gravarLeituras(PassagemPendente p, List<LeituraPlaca> leituras) throws SQLException {
        gravar(conn, p.getIdVeiculoTempoReal(), leituras, null);
    }

    /** MERGE por (passagem, origem, ordem): atualiza a placa/score se já existir. */
    public static void gravar(Connection conn, String idVtr, List<LeituraPlaca> leituras, Integer idUsuario) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE muralha.vtr_placa_leitura AS t " +
                "USING (SELECT CAST(? AS UNIQUEIDENTIFIER) id_vtr, ? ordem, ? tipo, ? placa, ? score, ? origem, ? id_usuario) AS s " +
                "ON t.id_veiculo_tempo_real = s.id_vtr AND t.origem = s.origem AND t.ordem = s.ordem " +
                "WHEN MATCHED THEN UPDATE SET tipo = s.tipo, placa = s.placa, score = s.score, dt = SYSDATETIME(), id_usuario = s.id_usuario " +
                "WHEN NOT MATCHED THEN INSERT (id_veiculo_tempo_real, ordem, tipo, placa, score, origem, id_usuario) " +
                "VALUES (s.id_vtr, s.ordem, s.tipo, s.placa, s.score, s.origem, s.id_usuario);")) {
            for (LeituraPlaca l : leituras) {
                ps.setString(1, idVtr);
                ps.setInt(2, l.getOrdem());
                ps.setString(3, l.getTipo().name());
                ps.setString(4, l.getPlaca());
                if (l.getScore() != null) ps.setBigDecimal(5, BigDecimal.valueOf(l.getScore()));
                else ps.setNull(5, Types.DECIMAL);
                ps.setString(6, l.getOrigem().name());
                if (idUsuario != null) ps.setInt(7, idUsuario);
                else ps.setNull(7, Types.INTEGER);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void registrarAvaliacao(PassagemPendente p, RegraRevisao.Avaliacao a, double limiar) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "IF NOT EXISTS (SELECT 1 FROM muralha.vtr_ocr_avaliacao WHERE id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER)) " +
                    "INSERT INTO muralha.vtr_ocr_avaliacao (id_veiculo_tempo_real, id_local, dt_passagem, score_minimo, limiar, motivo) " +
                    "VALUES (CAST(? AS UNIQUEIDENTIFIER), ?, ?, ?, ?, ?)")) {
                ps.setString(1, p.getIdVeiculoTempoReal());
                ps.setString(2, p.getIdVeiculoTempoReal());
                ps.setInt(3, p.getIdLocal());
                ps.setTimestamp(4, p.getData());
                decimal(ps, 5, a.getScoreMinimo());
                ps.setBigDecimal(6, BigDecimal.valueOf(limiar));
                ps.setString(7, a.getMotivo() == null ? null : a.getMotivo().name());
                ps.executeUpdate();
            }
            if (a.encaminhar()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "IF NOT EXISTS (SELECT 1 FROM muralha.vtr_revisao_placa WHERE id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER)) " +
                        "INSERT INTO muralha.vtr_revisao_placa (id_veiculo_tempo_real, id_local, dt_passagem, motivo, score_minimo, limiar, detalhe) " +
                        "VALUES (CAST(? AS UNIQUEIDENTIFIER), ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, p.getIdVeiculoTempoReal());
                    ps.setString(2, p.getIdVeiculoTempoReal());
                    ps.setInt(3, p.getIdLocal());
                    ps.setTimestamp(4, p.getData());
                    ps.setString(5, a.getMotivo().name());
                    decimal(ps, 6, a.getScoreMinimo());
                    ps.setBigDecimal(7, BigDecimal.valueOf(limiar));
                    ps.setString(8, a.getDetalhe() == null ? null : a.getDetalhe().length() > 200 ? a.getDetalhe().substring(0, 200) : a.getDetalhe());
                    ps.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    private static void decimal(PreparedStatement ps, int i, Double v) throws SQLException {
        if (v == null) ps.setNull(i, Types.DECIMAL);
        else ps.setBigDecimal(i, BigDecimal.valueOf(Math.round(v * 100) / 100.0));
    }
}
