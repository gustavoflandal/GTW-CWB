package muralha.digital.sla;

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

@WebServlet("/MuralhaDigital/SlaPreprocessamento")
public class SlaPreprocessamentoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        String acao = req.getParameter("acao");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            if ("historico".equals(acao)) {
                result.add("historico", obterHistorico(conn));
                result.addProperty("ok", true);
            } else {
                obterAtual(conn, result);
                result.addProperty("ok", true);
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private void obterAtual(Connection conn, JsonObject result) throws Exception {
        String sqlFaixas =
            "SELECT COUNT(*) AS total_pendentes, " +
            "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) AS acima_72h, " +
            "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) BETWEEN 49 AND 72 THEN 1 ELSE 0 END) AS entre_48_72h, " +
            "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) BETWEEN 25 AND 48 THEN 1 ELSE 0 END) AS entre_24_48h, " +
            "  SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) <= 24 THEN 1 ELSE 0 END) AS ate_24h " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "LEFT JOIN muralha.vtr_status_analise vsa " +
            "  ON vsa.id_veiculo_tempo_real = vtr.id " +
            "WHERE COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') = 'AGUARDANDO_ANALISE' " +
            "  AND vtr.data IS NOT NULL";

        try (PreparedStatement ps = conn.prepareStatement(sqlFaixas);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result.addProperty("totalPendentes", rs.getInt("total_pendentes"));
                result.addProperty("acima72h", rs.getInt("acima_72h"));
                result.addProperty("entre4872h", rs.getInt("entre_48_72h"));
                result.addProperty("entre2448h", rs.getInt("entre_24_48h"));
                result.addProperty("ate24h", rs.getInt("ate_24h"));
            }
        }

        String sqlFila =
            "SELECT vtr.id_local, COUNT(*) AS total, " +
            "  MAX(DATEDIFF(HOUR, vtr.data, SYSDATETIME())) AS max_horas " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "LEFT JOIN muralha.vtr_status_analise vsa " +
            "  ON vsa.id_veiculo_tempo_real = vtr.id " +
            "WHERE COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') = 'AGUARDANDO_ANALISE' " +
            "  AND vtr.data IS NOT NULL " +
            "GROUP BY vtr.id_local " +
            "ORDER BY max_horas DESC";

        JsonArray fila = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(sqlFila);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject row = new JsonObject();
                row.addProperty("idLocal", rs.getInt("id_local"));
                row.addProperty("total", rs.getInt("total"));
                row.addProperty("maxHoras", rs.getInt("max_horas"));
                fila.add(row);
            }
        }
        result.add("fila", fila);
    }

    private JsonArray obterHistorico(Connection conn) throws Exception {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP 168 id, dt_alerta, total_pendentes, " +
                "acima_72h, acima_48h, acima_24h " +
                "FROM muralha.alerta_sla_preproc ORDER BY dt_alerta DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject row = new JsonObject();
                row.addProperty("id", rs.getLong("id"));
                row.addProperty("dt", rs.getString("dt_alerta"));
                row.addProperty("totalPendentes", rs.getInt("total_pendentes"));
                row.addProperty("acima72h", rs.getInt("acima_72h"));
                row.addProperty("acima48h", rs.getInt("acima_48h"));
                row.addProperty("acima24h", rs.getInt("acima_24h"));
                arr.add(row);
            }
        }
        return arr;
    }
}
