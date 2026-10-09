package muralha.digital.sla;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/SlaLatencia")
public class SlaLatenciaServlet extends HttpServlet {

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
                JsonArray rows = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT TOP 288 id, dt_alerta, percentil95_ms, threshold_ms, " +
                        "violacao, total_passagens, locais_top " +
                        "FROM muralha.alerta_sla ORDER BY dt_alerta DESC");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id",        rs.getLong("id"));
                        row.addProperty("dt",        rs.getString("dt_alerta"));
                        row.addProperty("p95",       rs.getInt("percentil95_ms"));
                        row.addProperty("threshold", rs.getInt("threshold_ms"));
                        row.addProperty("violacao",  rs.getBoolean("violacao"));
                        row.addProperty("total",     rs.getInt("total_passagens"));
                        row.addProperty("locaisTop", rs.getString("locais_top"));
                        rows.add(row);
                    }
                }
                result.addProperty("ok", true);
                result.add("historico", rows);

            } else if ("atual".equals(acao)) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) total, " +
                        "AVG(CAST(DATEDIFF(MILLISECOND, data, data_importado) AS FLOAT)) media, " +
                        "MAX(DATEDIFF(MILLISECOND, data, data_importado)) maximo " +
                        "FROM muralha.veiculo_tempo_real " +
                        "WHERE data_importado >= DATEADD(MINUTE,-5,SYSDATETIME()) " +
                        "  AND data_importado IS NOT NULL " +
                        "  AND DATEDIFF(MILLISECOND, data, data_importado) BETWEEN 0 AND 600000");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.addProperty("ok",    true);
                        result.addProperty("total", rs.getInt("total"));
                        result.addProperty("media", rs.getDouble("media"));
                        result.addProperty("maximo",rs.getInt("maximo"));
                    }
                }
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
