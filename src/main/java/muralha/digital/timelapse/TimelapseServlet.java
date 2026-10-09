package muralha.digital.timelapse;

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

@WebServlet("/MuralhaDigital/Timelapse")
public class TimelapseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int MAX_FRAMES = 200;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) {
            new Mensagem(resp).showErro("Acesso negado", "/login/abertura-sistemas.jsp");
            return;
        }

        resp.setContentType("application/json; charset=UTF-8");
        String acao = req.getParameter("acao");
        JsonObject result = new JsonObject();
        Connection conn = null;

        try {
            conn = Conexao.getConexao();

            if ("equipamentos".equals(acao)) {
                JsonArray equips = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT lv.id_local, lv.nome " +
                        "FROM local_vigente lv " +
                        "WHERE lv.desativado = 0 " +
                        "ORDER BY lv.nome");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject eq = new JsonObject();
                        eq.addProperty("idLocal", rs.getInt("id_local"));
                        eq.addProperty("nome", rs.getString("nome"));
                        equips.add(eq);
                    }
                }
                result.addProperty("ok", true);
                result.add("equipamentos", equips);

            } else if ("frames".equals(acao)) {
                int idLocal = Integer.parseInt(req.getParameter("idLocal"));
                String dtInicio = req.getParameter("dtInicio");
                String dtFim = req.getParameter("dtFim");

                JsonArray frames = new JsonArray();
                String sql =
                    "SELECT TOP " + MAX_FRAMES +
                    " vtr.id, vtri.id AS id_imagem, " +
                    "  CONVERT(VARCHAR, vtr.data, 120) AS dt_captura, " +
                    "  vtr.placa " +
                    "FROM muralha.veiculo_tempo_real vtr " +
                    "INNER JOIN muralha.veiculo_tempo_real_imagem vtri " +
                    "  ON vtri.id_veiculo_tempo_real = vtr.id " +
                    "WHERE vtr.id_local = ? " +
                    (dtInicio != null && !dtInicio.isEmpty()
                        ? "  AND vtr.data >= ? " : "") +
                    (dtFim != null && !dtFim.isEmpty()
                        ? "  AND vtr.data <= ? " : "") +
                    "ORDER BY vtr.data";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int idx = 1;
                    ps.setInt(idx++, idLocal);
                    if (dtInicio != null && !dtInicio.isEmpty())
                        ps.setString(idx++, dtInicio);
                    if (dtFim != null && !dtFim.isEmpty())
                        ps.setString(idx++, dtFim);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            JsonObject frame = new JsonObject();
                            frame.addProperty("id", rs.getString("id"));
                            frame.addProperty("idImagem", rs.getString("id_imagem"));
                            frame.addProperty("dtCaptura", rs.getString("dt_captura"));
                            frame.addProperty("placa", rs.getString("placa"));
                            frames.add(frame);
                        }
                    }
                }
                result.addProperty("ok", true);
                result.addProperty("total", frames.size());
                result.add("frames", frames);

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
