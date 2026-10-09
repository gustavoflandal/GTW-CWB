package muralha.digital.assertividade;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/MuralhaDigital/Assertividade")
public class AssertividadeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;
        resp.setContentType("application/json; charset=UTF-8");

        String dtInicio = req.getParameter("dtInicio");
        String dtFim = req.getParameter("dtFim");

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            JsonObject totais = obterTotais(conn, dtInicio, dtFim);
            result.add("totais", totais);

            result.addProperty("taxaConcordancia", calcularTaxaConcordancia(totais));

            result.add("ranking", obterRanking(conn, dtInicio, dtFim));

            result.add("evolucao", obterEvolucao(conn, dtInicio, dtFim));

            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonObject obterTotais(Connection conn, String dtInicio, String dtFim) throws Exception {
        String sql =
            "SELECT vsa.status_analise, COUNT(*) cnt " +
            "FROM muralha.vtr_status_analise vsa " +
            "WHERE vsa.status_analise IN ('PRE_APROVADA','DESEMPATE','REPROVADA','PRIMEIRA_ANALISE') " +
            "  AND EXISTS (" +
            "    SELECT 1 FROM muralha.infracao_analise ia " +
            "    WHERE ia.id_infracao = vsa.id_veiculo_tempo_real " +
            "      AND CAST(ia.dt_analise AS DATE) BETWEEN COALESCE(?, CAST(DATEADD(DAY,-7,GETDATE()) AS DATE)) " +
            "                                          AND COALESCE(?, CAST(GETDATE() AS DATE))" +
            "  ) " +
            "GROUP BY vsa.status_analise";

        JsonObject totais = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totais.addProperty(rs.getString("status_analise"), rs.getInt("cnt"));
                }
            }
        }
        return totais;
    }

    private double calcularTaxaConcordancia(JsonObject totais) {
        int aprovadas = totais.has("PRE_APROVADA") ? totais.get("PRE_APROVADA").getAsInt() : 0;
        int desempate = totais.has("DESEMPATE") ? totais.get("DESEMPATE").getAsInt() : 0;
        int reprovadas = totais.has("REPROVADA") ? totais.get("REPROVADA").getAsInt() : 0;
        int total = aprovadas + desempate + reprovadas;
        return total > 0 ? (aprovadas * 100.0 / total) : 0.0;
    }

    private JsonArray obterRanking(Connection conn, String dtInicio, String dtFim) throws Exception {
        String sql =
            "SELECT su.nome, " +
            "  COUNT(DISTINCT ia.id_infracao) total, " +
            "  SUM(CASE WHEN vsa.status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END) concordantes " +
            "FROM muralha.infracao_analise ia " +
            "JOIN dbo.sis_usuario su ON su.id_usuario = ia.id_usuario " +
            "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = ia.id_infracao " +
            "WHERE CAST(ia.dt_analise AS DATE) BETWEEN COALESCE(?, CAST(DATEADD(DAY,-7,GETDATE()) AS DATE)) " +
            "                                      AND COALESCE(?, CAST(GETDATE() AS DATE)) " +
            "GROUP BY su.nome " +
            "ORDER BY total DESC";

        JsonArray ranking = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    int total = rs.getInt("total");
                    int conc = rs.getInt("concordantes");
                    row.addProperty("nome", rs.getString("nome"));
                    row.addProperty("total", total);
                    row.addProperty("concordantes", conc);
                    row.addProperty("taxa", total > 0 ? (conc * 100.0 / total) : 0.0);
                    ranking.add(row);
                }
            }
        }
        return ranking;
    }

    private JsonArray obterEvolucao(Connection conn, String dtInicio, String dtFim) throws Exception {
        String sql =
            "SELECT CAST(ia.dt_analise AS DATE) dia, " +
            "  COUNT(DISTINCT ia.id_infracao) total, " +
            "  SUM(CASE WHEN vsa.status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END) concordantes " +
            "FROM muralha.infracao_analise ia " +
            "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = ia.id_infracao " +
            "WHERE CAST(ia.dt_analise AS DATE) BETWEEN COALESCE(?, CAST(DATEADD(DAY,-7,GETDATE()) AS DATE)) " +
            "                                      AND COALESCE(?, CAST(GETDATE() AS DATE)) " +
            "GROUP BY CAST(ia.dt_analise AS DATE) " +
            "ORDER BY dia";

        JsonArray evolucao = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    int total = rs.getInt("total");
                    int conc = rs.getInt("concordantes");
                    row.addProperty("dia", rs.getString("dia"));
                    row.addProperty("total", total);
                    row.addProperty("taxa", total > 0 ? (conc * 100.0 / total) : 0.0);
                    evolucao.add(row);
                }
            }
        }
        return evolucao;
    }
}
