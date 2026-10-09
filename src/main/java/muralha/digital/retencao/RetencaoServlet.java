package muralha.digital.retencao;

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

@WebServlet("/MuralhaDigital/Retencao")
public class RetencaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            int retencaoAnos = 5;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT valor FROM muralha.config_chave_valor WHERE chave = 'retencao_anos'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) retencaoAnos = Integer.parseInt(rs.getString("valor"));
            }
            result.addProperty("retencaoAnos", retencaoAnos);

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS cnt " +
                    "FROM muralha.veiculo_tempo_real vtr " +
                    "INNER JOIN muralha.vtr_status_analise vsa " +
                    "  ON vsa.id_veiculo_tempo_real = vtr.id " +
                    "WHERE CAST(vtr.data AS DATE) < CAST(? AS DATE) " +
                    "  AND vsa.status_analise IN ('PRE_APROVADA','REPROVADA')")) {
                ps.setString(1, java.time.LocalDate.now().minusYears(retencaoAnos).toString());
                try (ResultSet rs = ps.executeQuery()) {
                    result.addProperty("estimativaExpurgo", rs.next() ? rs.getInt("cnt") : 0);
                }
            }

            JsonArray logs = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOP 20 id, dt_execucao, registros_movidos, " +
                    "retencao_anos, dt_corte, status, mensagem " +
                    "FROM muralha.expurgo_log ORDER BY id DESC");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("id", rs.getLong("id"));
                    row.addProperty("dt", rs.getString("dt_execucao"));
                    row.addProperty("movidos", rs.getInt("registros_movidos"));
                    row.addProperty("anos", rs.getInt("retencao_anos"));
                    row.addProperty("corte", rs.getString("dt_corte"));
                    row.addProperty("status", rs.getString("status"));
                    row.addProperty("msg", rs.getString("mensagem"));
                    logs.add(row);
                }
            }
            result.add("logs", logs);
            result.addProperty("ok", true);

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int anos = Integer.parseInt(req.getParameter("anos"));
            if (anos < 1 || anos > 20) {
                result.addProperty("ok", false);
                result.addProperty("erro", "Período deve ser entre 1 e 20 anos");
                resp.getWriter().print(new Gson().toJson(result));
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE muralha.config_chave_valor SET valor = ? WHERE chave = 'retencao_anos'")) {
                ps.setString(1, String.valueOf(anos));
                ps.executeUpdate();
            }
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
