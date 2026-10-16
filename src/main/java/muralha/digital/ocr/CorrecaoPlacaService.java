package muralha.digital.ocr;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Correção manual de placa com justificativa obrigatória (TR 5.7.5.2), única para os três
 * caminhos: fila de revisão, correção em lote e consulta de veículos. Grava, numa transação,
 * veiculo_tempo_real_correcao, a justificativa, as leituras MANUAL (inclusive placas adicionais,
 * 5.7.1.4), a passagem (placa/placa_frontal) e a situação na fila de revisão.
 */
public final class CorrecaoPlacaService {

    private static final Logger logger = Logger.getLogger(CorrecaoPlacaService.class);

    public enum OrigemIntervencao { FILA, LOTE, CONSULTA }

    /** Motivo de correção (muralha.motivo_correcao_placa). */
    public static final class Motivo {
        public final int id;
        public final String descricao;
        public final boolean exigeTexto;
        public final boolean confirmacao;

        Motivo(int id, String descricao, boolean exigeTexto, boolean confirmacao) {
            this.id = id;
            this.descricao = descricao;
            this.exigeTexto = exigeTexto;
            this.confirmacao = confirmacao;
        }
    }

    /** Pedido de correção. Placa nula = mantém a atual. */
    public static final class Pedido {
        final String idVeiculo;
        final String placa;
        final String placaFrontal;
        final List<LeituraPlaca> adicionais;
        final Integer idMotivo;
        final String justificativa;
        final OrigemIntervencao origem;

        public Pedido(String idVeiculo, String placa, String placaFrontal, List<LeituraPlaca> adicionais,
                Integer idMotivo, String justificativa, OrigemIntervencao origem) {
            this.idVeiculo = idVeiculo;
            this.placa = placa;
            this.placaFrontal = placaFrontal;
            this.adicionais = adicionais == null ? Collections.emptyList() : adicionais;
            this.idMotivo = idMotivo;
            this.justificativa = justificativa;
            this.origem = origem;
        }
    }

    /** Resultado, com a descrição para o log de auditoria. */
    public static final class Resultado {
        public final int idCorrecao;
        public final boolean confirmacao;
        public final String descricao;

        Resultado(int idCorrecao, boolean confirmacao, String descricao) {
            this.idCorrecao = idCorrecao;
            this.confirmacao = confirmacao;
            this.descricao = descricao;
        }
    }

    private CorrecaoPlacaService() {}

    /** Se a migração 20261016_ocr_score_revisao_placas.sql já foi executada. */
    public static boolean estruturaInstalada(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CASE WHEN OBJECT_ID('muralha.motivo_correcao_placa', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_correcao_justificativa', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_revisao_placa', 'U') IS NOT NULL " +
                "AND OBJECT_ID('muralha.vtr_placa_leitura', 'U') IS NOT NULL THEN 1 ELSE 0 END");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    /**
     * Motivos ativos. Sem a migração, só existe o motivo 0 "Outro (descrever)", que exige texto
     * (a justificativa continua obrigatória e vai para o log de auditoria).
     */
    public static List<Motivo> motivos(Connection conn) throws SQLException {
        List<Motivo> r = new ArrayList<>();
        if (!estruturaInstalada(conn)) {
            r.add(new Motivo(0, "Outro (descrever)", true, false));
            return r;
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, descricao, exige_texto, confirmacao FROM muralha.motivo_correcao_placa WHERE ativo = 1 ORDER BY ordem, descricao");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                r.add(new Motivo(rs.getInt("id"), rs.getString("descricao"), rs.getBoolean("exige_texto"), rs.getBoolean("confirmacao")));
            }
        }
        return r;
    }

    /** Motivo de conferência (sem mudança de placa), ou nulo. */
    static Motivo motivoConfirmacao(List<Motivo> motivos) {
        for (Motivo m : motivos) if (m.confirmacao) return m;
        return null;
    }

    private static Motivo motivo(List<Motivo> motivos, Integer id) {
        if (id == null) return null;
        for (Motivo m : motivos) if (m.id == id) return m;
        throw new IllegalArgumentException("Motivo de correção inválido ou inativo.");
    }

    /**
     * Aplica a correção. Lança {@link IllegalArgumentException} (mensagem para o usuário) se a placa,
     * o motivo ou a justificativa forem inválidos, ou se a passagem não existir.
     * A transação é do chamador quando {@code conn.getAutoCommit()} é falso.
     */
    public static Resultado corrigir(Connection conn, Pedido p, Integer idUsuario, String ip) throws SQLException {
        boolean estrutura = estruturaInstalada(conn);
        List<Motivo> motivos = motivos(conn);
        Motivo motivo = motivo(motivos, p.idMotivo);
        String justificativa = ValidacaoCorrecao.justificativa(motivo == null ? null : motivo.id,
            motivo != null && motivo.exigeTexto, p.justificativa);

        String placaAtual, frontalAtual;
        int idLocal;
        Timestamp dataPassagem;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT placa, placa_frontal, id_local, data FROM muralha.veiculo_tempo_real WHERE id = CAST(? AS UNIQUEIDENTIFIER)")) {
            ps.setString(1, p.idVeiculo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Passagem não encontrada.");
                placaAtual = limpar(rs.getString("placa"));
                frontalAtual = limpar(rs.getString("placa_frontal"));
                idLocal = rs.getInt("id_local");
                dataPassagem = rs.getTimestamp("data");
            }
        }

        String novaPlaca = limpar(p.placa) == null ? placaAtual : ValidacaoCorrecao.placa(p.placa);
        if (novaPlaca == null) throw new IllegalArgumentException("Informe a placa do veículo.");
        String novaFrontal = limpar(p.placaFrontal) == null ? frontalAtual : ValidacaoCorrecao.placa(p.placaFrontal);
        List<LeituraPlaca> adicionais = new ArrayList<>();
        int ordem = 3;
        for (LeituraPlaca a : p.adicionais) {
            if (a.getTipo() != LeituraPlaca.Tipo.REBOQUE && a.getTipo() != LeituraPlaca.Tipo.SEMIRREBOQUE) {
                throw new IllegalArgumentException("Placa adicional deve ser de reboque ou semirreboque.");
            }
            adicionais.add(new LeituraPlaca(ordem++, a.getTipo(), ValidacaoCorrecao.placa(a.getPlaca()), null, LeituraPlaca.Origem.MANUAL));
        }
        boolean mudouTracao = !novaPlaca.equals(placaAtual);
        boolean mudouFrontal = !Objects.equals(novaFrontal, frontalAtual);
        boolean confirmacao = !mudouTracao && !mudouFrontal && adicionais.isEmpty();

        boolean autoCommit = conn.getAutoCommit();
        if (autoCommit) conn.setAutoCommit(false);
        try {
            int idCorrecao;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.veiculo_tempo_real_correcao (id_veiculo, placa_original, placa_digitada, id_usuario, data) " +
                    "OUTPUT INSERTED.id VALUES (CAST(? AS UNIQUEIDENTIFIER), ?, ?, ?, GETDATE())")) {
                ps.setString(1, p.idVeiculo);
                ps.setString(2, placaAtual == null ? "" : placaAtual);
                ps.setString(3, novaPlaca);
                if (idUsuario != null) ps.setInt(4, idUsuario); else ps.setNull(4, Types.INTEGER);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idCorrecao = rs.getInt(1);
                }
            }
            if (mudouTracao || mudouFrontal) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.veiculo_tempo_real SET placa = ?, placa_frontal = ?, id_usuario_alt = ?, " +
                        "data_alt = GETDATE(), dados_alt_orig = ? WHERE id = CAST(? AS UNIQUEIDENTIFIER)")) {
                    ps.setString(1, novaPlaca);
                    ps.setString(2, novaFrontal);
                    if (idUsuario != null) ps.setInt(3, idUsuario); else ps.setNull(3, Types.INTEGER);
                    ps.setString(4, placaAtual);
                    ps.setString(5, p.idVeiculo);
                    ps.executeUpdate();
                }
            }
            if (estrutura) {
                Long idRevisao = situacaoRevisao(conn, p, idLocal, dataPassagem, confirmacao, idUsuario, idCorrecao);
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO muralha.vtr_correcao_justificativa (id_correcao, id_motivo, justificativa, origem_intervencao, id_revisao, ip) " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, idCorrecao);
                    ps.setInt(2, motivo.id);
                    ps.setString(3, justificativa);
                    ps.setString(4, p.origem.name());
                    if (idRevisao != null) ps.setLong(5, idRevisao); else ps.setNull(5, Types.BIGINT);
                    ps.setString(6, ip);
                    ps.executeUpdate();
                }
                List<LeituraPlaca> manuais = new ArrayList<>();
                manuais.add(new LeituraPlaca(1, LeituraPlaca.Tipo.TRACAO, novaPlaca, null, LeituraPlaca.Origem.MANUAL));
                if (novaFrontal != null) manuais.add(new LeituraPlaca(2, LeituraPlaca.Tipo.FRONTAL, novaFrontal, null, LeituraPlaca.Origem.MANUAL));
                manuais.addAll(adicionais);
                JdbcRepositorioOcr.gravar(conn, p.idVeiculo, manuais, idUsuario);
            }
            if (autoCommit) conn.commit();
            StringBuilder d = new StringBuilder(confirmacao ? "Placa conferida " : "Placa corrigida ");
            d.append(placaAtual == null ? "(sem leitura)" : placaAtual).append(" -> ").append(novaPlaca);
            if (mudouFrontal) d.append("; frontal ").append(frontalAtual).append(" -> ").append(novaFrontal);
            for (LeituraPlaca a : adicionais) d.append("; ").append(a.getTipo()).append(' ').append(a.getPlaca());
            d.append(". Motivo: ").append(motivo == null ? "?" : motivo.descricao);
            if (justificativa != null) d.append(". Justificativa: ").append(justificativa);
            if (!estrutura) d.append(" (migração do Plano 27 pendente: justificativa só no log)");
            return new Resultado(idCorrecao, confirmacao, d.toString());
        } catch (SQLException | RuntimeException e) {
            if (autoCommit) conn.rollback();
            throw e;
        } finally {
            if (autoCommit) conn.setAutoCommit(true);
        }
    }

    /** Fecha o item da fila (ou cria o registro da intervenção feita fora dela). */
    private static Long situacaoRevisao(Connection conn, Pedido p, int idLocal, Timestamp dataPassagem,
            boolean confirmacao, Integer idUsuario, int idCorrecao) throws SQLException {
        String situacao = confirmacao ? "CONFIRMADA" : "CORRIGIDA";
        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE muralha.vtr_revisao_placa AS t " +
                "USING (SELECT CAST(? AS UNIQUEIDENTIFIER) id_vtr) AS s ON t.id_veiculo_tempo_real = s.id_vtr " +
                "WHEN MATCHED THEN UPDATE SET situacao = ?, dt_tratamento = SYSDATETIME(), id_usuario_tratamento = ?, " +
                "  origem_intervencao = ?, id_correcao = ? " +
                "WHEN NOT MATCHED THEN INSERT (id_veiculo_tempo_real, id_local, dt_passagem, motivo, situacao, dt_tratamento, " +
                "  id_usuario_tratamento, origem_intervencao, id_correcao) " +
                "VALUES (s.id_vtr, ?, ?, 'MANUAL', ?, SYSDATETIME(), ?, ?, ?) " +
                "OUTPUT INSERTED.id;")) {
            int i = 1;
            ps.setString(i++, p.idVeiculo);
            ps.setString(i++, situacao);
            if (idUsuario != null) ps.setInt(i++, idUsuario); else ps.setNull(i++, Types.INTEGER);
            ps.setString(i++, p.origem.name());
            ps.setInt(i++, idCorrecao);
            ps.setInt(i++, idLocal);
            ps.setTimestamp(i++, dataPassagem);
            ps.setString(i++, situacao);
            if (idUsuario != null) ps.setInt(i++, idUsuario); else ps.setNull(i++, Types.INTEGER);
            ps.setString(i++, p.origem.name());
            ps.setInt(i, idCorrecao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : null;
            }
        } catch (SQLException e) {
            logger.error("Falha ao registrar a intervenção na fila de revisão da passagem " + p.idVeiculo, e);
            throw e;
        }
    }

    static String limpar(String placa) {
        if (placa == null) return null;
        String t = placa.trim().toUpperCase();
        return t.isEmpty() ? null : t;
    }

    /**
     * Placas adicionais no formato "TIPO:PLACA,TIPO:PLACA" (ex.: "REBOQUE:ABC1D23"). No máximo 5.
     */
    public static List<LeituraPlaca> adicionais(String texto) {
        List<LeituraPlaca> r = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) return r;
        for (String parte : texto.split(",")) {
            String t = parte.trim();
            if (t.isEmpty()) continue;
            int sep = t.indexOf(':');
            if (sep < 0) throw new IllegalArgumentException("Placa adicional inválida: " + t);
            LeituraPlaca.Tipo tipo;
            try {
                tipo = LeituraPlaca.Tipo.valueOf(t.substring(0, sep).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de placa adicional inválido: " + t.substring(0, sep));
            }
            r.add(new LeituraPlaca(0, tipo, t.substring(sep + 1), null, LeituraPlaca.Origem.MANUAL));
        }
        if (r.size() > 5) throw new IllegalArgumentException("Informe no máximo 5 placas adicionais.");
        return r;
    }
}
