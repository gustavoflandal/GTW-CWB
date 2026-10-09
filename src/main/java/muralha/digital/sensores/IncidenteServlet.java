package muralha.digital.sensores;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
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

@WebServlet("/MuralhaDigital/Incidente")
public class IncidenteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) {
            new Mensagem(resp).showErro("Acesso negado", "/login/abertura-sistemas.jsp");
            return;
        }

        String acao = req.getParameter("acao");
        Connection conn = null;

        try {
            conn = Conexao.getConexao();

            if ("geojson".equals(acao)) {
                resp.setContentType("application/json; charset=UTF-8");
                StringBuilder sb = new StringBuilder();
                sb.append("{\"type\":\"FeatureCollection\",\"features\":[");

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, tipo, descricao, latitude, longitude, " +
                        "  severidade, " +
                        "  CONVERT(VARCHAR, dt_ocorrencia, 120) AS dt_ocorrencia " +
                        "FROM muralha.incidente_externo " +
                        "WHERE ativo = 1 " +
                        "  AND dt_ocorrencia >= DATEADD(HOUR, -24, SYSDATETIME()) " +
                        "ORDER BY dt_ocorrencia DESC");
                     ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) sb.append(",");
                        first = false;
                        sb.append(String.format(
                            "{\"type\":\"Feature\"," +
                            "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                            "\"properties\":{\"id\":%d,\"tipo\":\"%s\",\"desc\":\"%s\"," +
                            "\"sev\":%d,\"dt\":\"%s\"}}",
                            rs.getString("longitude"),
                            rs.getString("latitude"),
                            rs.getLong("id"),
                            escJson(rs.getString("tipo")),
                            escJson(rs.getString("descricao")),
                            rs.getInt("severidade"),
                            rs.getString("dt_ocorrencia")));
                    }
                }
                sb.append("]}");
                resp.getWriter().print(sb);
                return;
            }

            if ("listar".equals(acao)) {
                resp.setContentType("application/json; charset=UTF-8");
                JsonObject result = new JsonObject();
                JsonArray items = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT TOP 200 id, fonte, tipo, descricao, latitude, longitude, " +
                        "  severidade, " +
                        "  CONVERT(VARCHAR, dt_ocorrencia, 120) AS dt_ocorrencia, " +
                        "  ativo " +
                        "FROM muralha.incidente_externo " +
                        "ORDER BY dt_ocorrencia DESC");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id", rs.getLong("id"));
                        row.addProperty("fonte", rs.getString("fonte"));
                        row.addProperty("tipo", rs.getString("tipo"));
                        row.addProperty("descricao", rs.getString("descricao"));
                        row.addProperty("lat", rs.getDouble("latitude"));
                        row.addProperty("lng", rs.getDouble("longitude"));
                        row.addProperty("severidade", rs.getInt("severidade"));
                        row.addProperty("dtOcorrencia", rs.getString("dt_ocorrencia"));
                        row.addProperty("ativo", rs.getBoolean("ativo"));
                        items.add(row);
                    }
                }
                result.addProperty("ok", true);
                result.add("incidentes", items);
                resp.getWriter().print(new Gson().toJson(result));
                return;
            }

            resp.setContentType("application/json; charset=UTF-8");
            JsonObject result = new JsonObject();
            result.addProperty("ok", false);
            result.addProperty("erro", "acao invalida");
            resp.getWriter().print(new Gson().toJson(result));

        } catch (Exception e) {
            resp.setContentType("application/json; charset=UTF-8");
            JsonObject result = new JsonObject();
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
            resp.getWriter().print(new Gson().toJson(result));
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private String escJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", " ").replace("\r", "");
    }
}
