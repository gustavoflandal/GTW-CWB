package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/dash/health")
public class HealthCheckServlet extends HttpServlet {

    private static final int TIMEOUT_BANCO_SEG = 5;

    private final ConsultaLogService consultaLogService = new ConsultaLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        JsonObject result = new JsonObject();

        try (Connection conn = Conexao.getConexao()) {
            JsonObject banco = verificarBanco(conn);
            result.add("banco", banco);

            // Estatísticas de consultas (última hora)
            result.add("consultas", consultaLogService.obterEstatisticas(conn, 1));

            boolean bancoOk = "ok".equals(banco.get("status").getAsString());
            result.addProperty("status", bancoOk ? "OK" : "DEGRADED");
            result.addProperty("ok", true);

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("status", "ERROR");
            result.addProperty("erro", e.getMessage());
        }

        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonObject verificarBanco(Connection conn) {
        JsonObject status = new JsonObject();
        long inicio = System.currentTimeMillis();
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1")) {
            ps.setQueryTimeout(TIMEOUT_BANCO_SEG);
            ps.executeQuery().close();
            status.addProperty("status", "ok");
        } catch (Exception e) {
            status.addProperty("status", "offline");
            status.addProperty("erro", e.getMessage());
        }
        status.addProperty("tempoMs", System.currentTimeMillis() - inicio);
        return status;
    }
}
