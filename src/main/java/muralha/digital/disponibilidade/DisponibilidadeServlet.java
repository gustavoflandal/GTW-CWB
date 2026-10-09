package muralha.digital.disponibilidade;

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

@WebServlet("/MuralhaDigital/Disponibilidade")
public class DisponibilidadeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) {
            new Mensagem(resp).showErro("Acesso negado", "/login/abertura-sistemas.jsp");
            return;
        }

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;

        try {
            conn = Conexao.getConexao();

            JsonArray status = new JsonArray();
            String sqlStatus =
                "SELECT ed.id_local, lv.nome, ed.disponivel, " +
                "  CONVERT(VARCHAR, ed.ultima_passagem, 120) AS ultima, " +
                "  ed.minutos_offline " +
                "FROM muralha.equipamento_disponibilidade ed " +
                "INNER JOIN local_vigente lv ON lv.id_local = ed.id_local " +
                "WHERE ed.dt_verificacao = (" +
                "  SELECT MAX(dt_verificacao) " +
                "  FROM muralha.equipamento_disponibilidade " +
                "  WHERE id_local = ed.id_local" +
                ") ORDER BY lv.nome";
            try (PreparedStatement ps = conn.prepareStatement(sqlStatus);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("idLocal", rs.getInt("id_local"));
                    row.addProperty("nome", rs.getString("nome"));
                    row.addProperty("disponivel", rs.getBoolean("disponivel"));
                    row.addProperty("ultima", rs.getString("ultima"));
                    row.addProperty("minOffline", rs.getInt("minutos_offline"));
                    status.add(row);
                }
            }
            result.addProperty("ok", true);
            result.add("status", status);

            JsonArray uptime = new JsonArray();
            String sqlUptime =
                "SELECT ed.id_local, lv.nome, " +
                "  COUNT(*) AS total_verificacoes, " +
                "  SUM(CAST(ed.disponivel AS INT)) AS disponivel_count, " +
                "  CAST(SUM(CAST(ed.disponivel AS FLOAT)) * 100 " +
                "    / NULLIF(COUNT(*), 0) AS DECIMAL(5,1)) AS uptime_pct " +
                "FROM muralha.equipamento_disponibilidade ed " +
                "INNER JOIN local_vigente lv ON lv.id_local = ed.id_local " +
                "WHERE ed.dt_verificacao >= DATEADD(HOUR, -24, SYSDATETIME()) " +
                "GROUP BY ed.id_local, lv.nome " +
                "ORDER BY uptime_pct";
            try (PreparedStatement ps = conn.prepareStatement(sqlUptime);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("idLocal", rs.getInt("id_local"));
                    row.addProperty("nome", rs.getString("nome"));
                    row.addProperty("uptime", rs.getDouble("uptime_pct"));
                    row.addProperty("total", rs.getInt("total_verificacoes"));
                    uptime.add(row);
                }
            }
            result.add("uptime", uptime);

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
