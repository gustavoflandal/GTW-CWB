package muralha.digital.processamento;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class InfracaoAnaliseDAO {

    private InfracaoAnaliseDAO() {}

    /**
     * Retorna a próxima infração disponível para análise pelo usuário informado.
     * Exclui: já analisadas por este usuário; status PRE_APROVADA/REPROVADA.
     */
    public static JsonObject obterProxima(int idUsuario) throws Exception {
        String sql =
            "SELECT TOP 1 vtr.id, vtr.placa, vtr.data, vtr.status_analise, " +
            "  vtr.id_local, vtr.id_pista, " +
            "  (SELECT TOP 1 vi.id FROM muralha.veiculo_tempo_real_imagem vi " +
            "   WHERE vi.id_veiculo_tempo_real = vtr.id AND vi.obliterada = 0 " +
            "   ORDER BY vi.id DESC) AS id_imagem " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "WHERE vtr.status_analise IN ('AGUARDANDO_ANALISE','PRIMEIRA_ANALISE','DESEMPATE') " +
            "  AND vtr.id NOT IN (" +
            "    SELECT id_infracao FROM muralha.infracao_analise WHERE id_usuario = ?" +
            "  ) " +
            "ORDER BY vtr.data ASC";

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        JsonObject r = new JsonObject();
                        r.addProperty("id",          rs.getString("id"));
                        r.addProperty("placa",       rs.getString("placa"));
                        r.addProperty("data",        String.valueOf(rs.getTimestamp("data")));
                        r.addProperty("statusAnalise", rs.getString("status_analise"));
                        r.addProperty("idLocal",     rs.getInt("id_local"));
                        r.addProperty("pista",       rs.getInt("id_pista"));
                        long idImg = rs.getLong("id_imagem");
                        if (!rs.wasNull()) r.addProperty("idImagem", idImg);
                        return r;
                    }
                    return null;
                }
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
    }

    /**
     * Grava a análise do operador e avança o status da infração conforme a lógica de negócio.
     * Retorna o novo status da infração.
     */
    public static String registrarAnalise(String idInfracao, int idUsuario,
            String classificacao, String justificativa) throws Exception {

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);

            // Verificar que não é re-análise
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM muralha.infracao_analise WHERE id_infracao=? AND id_usuario=?")) {
                ps.setString(1, idInfracao);
                ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) throw new IllegalStateException("Operador já analisou esta infração.");
                }
            }

            // Determinar sequência
            int sequencia;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM muralha.infracao_analise WHERE id_infracao = ?")) {
                ps.setString(1, idInfracao);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); sequencia = rs.getInt(1) + 1; }
            }

            // Inserir análise
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.infracao_analise " +
                    "(id_infracao, id_usuario, sequencia, classificacao, justificativa) " +
                    "VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, idInfracao);
                ps.setInt(2, idUsuario);
                ps.setInt(3, sequencia);
                ps.setString(4, classificacao);
                ps.setString(5, justificativa);
                ps.executeUpdate();
            }

            // Determinar novo status
            String novoStatus = determinarNovoStatus(conn, idInfracao, sequencia, classificacao);

            // Atualizar status na infração
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE muralha.veiculo_tempo_real SET status_analise=? WHERE id=?")) {
                ps.setString(1, novoStatus);
                ps.setString(2, idInfracao);
                ps.executeUpdate();
            }

            conn.commit();
            return novoStatus;

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception er) {}
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception e2) {}
                try { conn.close(); } catch (Exception e2) {}
            }
        }
    }

    private static String determinarNovoStatus(Connection conn, String idInfracao,
            int sequencia, String classificacaoAtual) throws SQLException {
        if (sequencia == 1) return "PRIMEIRA_ANALISE";
        if (sequencia == 2) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT classificacao FROM muralha.infracao_analise " +
                    "WHERE id_infracao=? AND sequencia=1")) {
                ps.setString(1, idInfracao);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String primaClassif = rs.getString("classificacao");
                        return primaClassif.equals(classificacaoAtual) ? "PRE_APROVADA" : "DESEMPATE";
                    }
                }
            }
        }
        if (sequencia == 3) {
            return "VALIDA".equals(classificacaoAtual) ? "PRE_APROVADA" : "REPROVADA";
        }
        return "AGUARDANDO_ANALISE";
    }

    public static JsonArray obterIndicadores() throws Exception {
        String sql =
            "SELECT status_analise, COUNT(*) qtde " +
            "FROM muralha.veiculo_tempo_real " +
            "GROUP BY status_analise " +
            "HAVING status_analise IN ('AGUARDANDO_ANALISE','PRIMEIRA_ANALISE'," +
            "                          'DESEMPATE','PRE_APROVADA','REPROVADA') " +
            "ORDER BY status_analise";
        JsonArray arr = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("status", rs.getString("status_analise"));
                    r.addProperty("qtde",   rs.getInt("qtde"));
                    arr.add(r);
                }
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return arr;
    }
}
