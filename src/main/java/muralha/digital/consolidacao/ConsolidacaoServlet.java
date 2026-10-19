package muralha.digital.consolidacao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import muralha.digital.acessos.ControleAcesso;

@WebServlet("/MuralhaDigital/Consolidacao")
public class ConsolidacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ConsolidacaoServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Usuario usuario = (Usuario) req.getSession()
                .getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(401); return; }
        if (!ControleAcesso.exigir(req, resp)) { return; }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");
        JsonObject result = new JsonObject();

        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            if ("resumo".equals(acao)) {
                result = obterResumo(conn);
            } else if ("consultar".equals(acao)) {
                String fonte    = req.getParameter("fonte");
                String dtInicio = req.getParameter("dtInicio");
                String dtFim    = req.getParameter("dtFim");
                int limite      = 200;
                try { limite = Integer.parseInt(req.getParameter("limite")); } catch (Exception ignored) {}
                result = consultar(conn, fonte, dtInicio, dtFim, limite);
            } else if ("fontes".equals(acao)) {
                result = listarFontes(conn);
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "Ação inválida. Use: resumo, consultar, fontes.");
            }
        } catch (Exception e) {
            logger.error("[Muralha] Erro na consolidação.", e);
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }

        resp.getWriter().write(gson.toJson(result));
    }

    private JsonObject obterResumo(Connection conn) throws SQLException {
        JsonObject result = new JsonObject();
        JsonArray fontes = new JsonArray();

        String sql =
            "SELECT fonte, COUNT(*) AS total, MIN(dt_evento) AS primeiro, MAX(dt_evento) AS ultimo " +
            "FROM muralha.v_dados_consolidados " +
            "GROUP BY fonte ORDER BY fonte";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject f = new JsonObject();
                f.addProperty("fonte",    rs.getString("fonte"));
                f.addProperty("total",    rs.getLong("total"));
                f.addProperty("primeiro", rs.getString("primeiro"));
                f.addProperty("ultimo",   rs.getString("ultimo"));
                fontes.add(f);
            }
        }
        result.addProperty("ok", true);
        result.add("fontes", fontes);
        return result;
    }

    private JsonObject consultar(Connection conn, String fonte, String dtInicio, String dtFim, int limite)
            throws SQLException {
        JsonObject result = new JsonObject();
        JsonArray registros = new JsonArray();

        StringBuilder sql = new StringBuilder(
            "SELECT TOP (?) fonte, id_registro, equipamento, dt_evento, placa, tipo_evento, " +
            "latitude, longitude, descricao FROM muralha.v_dados_consolidados WHERE 1=1 ");

        if (fonte != null && !fonte.isEmpty()) sql.append("AND fonte = ? ");
        if (dtInicio != null && !dtInicio.isEmpty()) sql.append("AND dt_evento >= ? ");
        if (dtFim != null && !dtFim.isEmpty()) sql.append("AND dt_evento <= ? ");
        sql.append("ORDER BY dt_evento DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, Math.min(limite, 1000));
            if (fonte != null && !fonte.isEmpty()) ps.setString(idx++, fonte);
            if (dtInicio != null && !dtInicio.isEmpty()) ps.setString(idx++, dtInicio);
            if (dtFim != null && !dtFim.isEmpty()) ps.setString(idx++, dtFim);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("fonte",      rs.getString("fonte"));
                    r.addProperty("id",         rs.getString("id_registro"));
                    r.addProperty("equipamento", rs.getString("equipamento"));
                    r.addProperty("dtEvento",   rs.getString("dt_evento"));
                    r.addProperty("placa",      rs.getString("placa"));
                    r.addProperty("tipoEvento", rs.getString("tipo_evento"));
                    double lat = rs.getDouble("latitude");
                    if (!rs.wasNull()) r.addProperty("latitude", lat);
                    double lon = rs.getDouble("longitude");
                    if (!rs.wasNull()) r.addProperty("longitude", lon);
                    r.addProperty("descricao",  rs.getString("descricao"));
                    registros.add(r);
                }
            }
        }
        result.addProperty("ok", true);
        result.addProperty("total", registros.size());
        result.add("registros", registros);
        return result;
    }

    private JsonObject listarFontes(Connection conn) throws SQLException {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        String sql = "SELECT DISTINCT fonte FROM muralha.v_dados_consolidados ORDER BY fonte";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) arr.add(rs.getString("fonte"));
        }
        result.addProperty("ok", true);
        result.add("fontes", arr);
        return result;
    }
}
