package muralha.digital.integridade;

import muralha.digital.auditoria.AuditoriaService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementação JDBC do {@link RepositorioIntegridade}. A conexão é do chamador. */
public class JdbcRepositorioIntegridade implements RepositorioIntegridade {

    /** nomeinterno do tipo criado por 20261016_integridade_sha256_recepcao.sql */
    static final String NOME_INTERNO_TIPO_ALERTA = "INTEGRIDADE";
    /** muralha.status_alerta "PENDENTE" */
    static final String DESCRICAO_STATUS_PENDENTE = "PENDENTE";
    static final String ORIGEM_ALERTA = "INTEGRIDADE_SHA256";

    private final Connection conn;
    private String idTipoAlerta;
    private String idStatusPendente;
    private boolean tiposCarregados;

    public JdbcRepositorioIntegridade(Connection conn) {
        this.conn = conn;
    }

    /**
     * Imagens de passagens capturadas nas últimas {@code janelaHoras} horas que ainda não têm
     * conferência, das mais antigas para as mais novas.
     */
    public List<ImagemPendente> pendentes(int janelaHoras, int lote) throws SQLException {
        String sql =
            "SELECT TOP (?) i.id, i.id_veiculo_tempo_real, i.indice_imagem, i.imagem, " +
            "       v.id_local, v.data, v.data_importado, v.id_captura " +
            "FROM muralha.veiculo_tempo_real v " +
            "JOIN muralha.veiculo_tempo_real_imagem i ON i.id_veiculo_tempo_real = v.id " +
            "WHERE v.data >= DATEADD(HOUR, -?, GETDATE()) " +
            "  AND NOT EXISTS (SELECT 1 FROM muralha.vtr_imagem_integridade g WHERE g.id_imagem = i.id) " +
            "ORDER BY v.data";
        List<ImagemPendente> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lote);
            ps.setInt(2, janelaHoras);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ImagemPendente(
                        rs.getString("id"),
                        rs.getString("id_veiculo_tempo_real"),
                        rs.getInt("indice_imagem"),
                        rs.getInt("id_local"),
                        rs.getTimestamp("data"),
                        rs.getTimestamp("data_importado"),
                        rs.getString("id_captura"),
                        rs.getBytes("imagem")));
                }
            }
        }
        return lista;
    }

    /** Origem configurada por equipamento (somente linhas ativas e válidas). */
    public Map<Integer, OrigemHash> origensPorEquipamento() throws SQLException {
        Map<Integer, OrigemHash> mapa = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, origem_hash, campo FROM muralha.equipamento_integridade_config WHERE ativo = 1");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    mapa.put(rs.getInt("id_local"),
                        OrigemHash.de(OrigemHash.tipoDe(rs.getString("origem_hash")), rs.getString("campo")));
                } catch (IllegalArgumentException ignorada) {
                    // configuração inválida: o equipamento usa a origem padrão
                }
            }
        }
        return mapa;
    }

    @Override
    public String hashPayload(String idImagem) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT sha256_origem FROM muralha.vtr_imagem_hash_origem WHERE id_imagem = CAST(? AS UNIQUEIDENTIFIER)")) {
            ps.setString(1, idImagem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
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
    public void gravar(ImagemPendente img, OrigemHash origem, ResultadoConferencia res) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "IF NOT EXISTS (SELECT 1 FROM muralha.vtr_imagem_integridade WHERE id_imagem = CAST(? AS UNIQUEIDENTIFIER)) " +
                    "INSERT INTO muralha.vtr_imagem_integridade " +
                    "(id_imagem, id_veiculo_tempo_real, indice_imagem, id_local, dt_captura, dt_recepcao, " +
                    " status_integridade, origem_hash, sha256_calculado, hash_origem, motivo) " +
                    "VALUES (CAST(? AS UNIQUEIDENTIFIER), CAST(? AS UNIQUEIDENTIFIER), ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 1;
                ps.setString(i++, img.getIdImagem());
                ps.setString(i++, img.getIdImagem());
                ps.setString(i++, img.getIdVeiculoTempoReal());
                ps.setInt(i++, img.getIndiceImagem());
                ps.setInt(i++, img.getIdLocal());
                ps.setTimestamp(i++, img.getDtCaptura());
                ps.setTimestamp(i++, img.getDtRecepcao());
                ps.setString(i++, res.getStatus().name());
                ps.setString(i++, origem.formatar());
                ps.setString(i++, res.getSha256Calculado());
                ps.setString(i++, truncar(res.getHashOrigem(), 128));
                ps.setString(i, truncar(res.getMotivo(), 200));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE muralha.vtr_imagem_complemento AS t " +
                    "USING (SELECT CAST(? AS UNIQUEIDENTIFIER) id_imagem, ? sha256, ? status_integridade) AS s " +
                    "ON t.id_imagem = s.id_imagem " +
                    "WHEN MATCHED THEN UPDATE SET sha256 = COALESCE(s.sha256, t.sha256), status_integridade = s.status_integridade " +
                    "WHEN NOT MATCHED THEN INSERT (id_imagem, sha256, status_integridade) " +
                    "VALUES (s.id_imagem, s.sha256, s.status_integridade);")) {
                ps.setString(1, img.getIdImagem());
                if (res.getSha256Calculado() != null) ps.setString(2, res.getSha256Calculado());
                else ps.setNull(2, Types.CHAR);
                ps.setString(3, res.getStatus().name());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    @Override
    public boolean existeAlerta(String idVeiculoTempoReal) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 1 1 FROM muralha.alerta_veiculo av " +
                "JOIN muralha.alerta a ON a.id = av.id_alerta " +
                "WHERE av.id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER) AND a.origem = ?")) {
            ps.setString(1, idVeiculoTempoReal);
            ps.setString(2, ORIGEM_ALERTA);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean gerarAlerta(ImagemPendente img, ResultadoConferencia res) throws SQLException {
        carregarTipos();
        if (idTipoAlerta == null || idStatusPendente == null) return false;

        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            String idAlerta;
            try (PreparedStatement ps = conn.prepareStatement("SELECT CAST(NEWID() AS VARCHAR(36))");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                idAlerta = rs.getString(1);
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta (id, id_tipo_alerta_ocorrencia, id_status_alerta, data, observacao, origem) " +
                    "VALUES (CAST(? AS UNIQUEIDENTIFIER), CAST(? AS UNIQUEIDENTIFIER), CAST(? AS UNIQUEIDENTIFIER), GETDATE(), ?, ?)")) {
                ps.setString(1, idAlerta);
                ps.setString(2, idTipoAlerta);
                ps.setString(3, idStatusPendente);
                ps.setString(4, truncar("Imagem " + img.getIndiceImagem() + " do equipamento " + img.getIdLocal()
                    + ": " + res.getMotivo(), 200));
                ps.setString(5, ORIGEM_ALERTA);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta_veiculo (id_alerta, id_veiculo_tempo_real) " +
                    "VALUES (CAST(? AS UNIQUEIDENTIFIER), CAST(? AS UNIQUEIDENTIFIER))")) {
                ps.setString(1, idAlerta);
                ps.setString(2, img.getIdVeiculoTempoReal());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void auditar(ImagemPendente img, ResultadoConferencia res) {
        AuditoriaService.registrarSistema("Integridade de imagens", "sha256-divergente", img.getIdImagem(),
            truncar("Equipamento " + img.getIdLocal() + ", passagem " + img.getIdVeiculoTempoReal()
                + ", imagem " + img.getIndiceImagem() + ": " + res.getMotivo()
                + " Calculado " + res.getSha256Calculado() + "; origem " + res.getHashOrigem() + ".", 500));
    }

    private void carregarTipos() throws SQLException {
        if (tiposCarregados) return;
        tiposCarregados = true;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 1 CAST(id AS VARCHAR(36)) FROM muralha.tipo_alerta_ocorrencia WHERE RTRIM(nomeinterno) = ?")) {
            ps.setString(1, NOME_INTERNO_TIPO_ALERTA);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idTipoAlerta = rs.getString(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 1 CAST(id AS VARCHAR(36)) FROM muralha.status_alerta WHERE descricao = ?")) {
            ps.setString(1, DESCRICAO_STATUS_PENDENTE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idStatusPendente = rs.getString(1);
            }
        }
    }

    /** Se a migração 20261016_integridade_sha256_recepcao.sql já foi executada. */
    boolean estruturaInstalada() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CASE WHEN OBJECT_ID('muralha.vtr_imagem_integridade', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.equipamento_integridade_config', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_imagem_hash_origem', 'U') IS NOT NULL THEN 1 ELSE 0 END");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    boolean tipoAlertaCadastrado() throws SQLException {
        carregarTipos();
        return idTipoAlerta != null;
    }

    private static String truncar(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
