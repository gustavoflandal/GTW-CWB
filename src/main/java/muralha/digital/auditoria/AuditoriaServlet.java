package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/MuralhaDigital/Auditoria")
public class AuditoriaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        String acao = req.getParameter("acao");
        if (acao == null) { resp.sendError(400); return; }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if ("consultar".equals(acao)) {
            out.print(consultar(req));
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    private String consultar(HttpServletRequest req) {
        String login     = req.getParameter("login");
        String funcional = req.getParameter("funcionalidade");
        String operacao  = req.getParameter("operacao");
        String dtInicio  = req.getParameter("dtInicio");
        String dtFim     = req.getParameter("dtFim");
        String pagStr    = req.getParameter("pagina");
        int pagina       = pagStr != null ? Integer.parseInt(pagStr) : 1;
        int tamPag       = 50;

        StringBuilder sql = new StringBuilder(
            "SELECT id, id_usuario, login, dt_operacao, ip_terminal, " +
            "funcionalidade, operacao, id_registro, descricao " +
            "FROM dbo.sis_log_auditoria WHERE 1=1 ");
        if (login     != null && !login.isEmpty())     sql.append("AND login LIKE ? ");
        if (funcional != null && !funcional.isEmpty()) sql.append("AND funcionalidade = ? ");
        if (operacao  != null && !operacao.isEmpty())  sql.append("AND operacao = ? ");
        if (dtInicio  != null && !dtInicio.isEmpty())  sql.append("AND dt_operacao >= ? ");
        if (dtFim     != null && !dtFim.isEmpty())     sql.append("AND dt_operacao < DATEADD(day,1,?) ");
        sql.append("ORDER BY dt_operacao DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        JsonObject result = new JsonObject();
        JsonArray rows    = new JsonArray();
        Connection conn   = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int i = 1;
                if (login     != null && !login.isEmpty())     ps.setString(i++, "%" + login + "%");
                if (funcional != null && !funcional.isEmpty()) ps.setString(i++, funcional);
                if (operacao  != null && !operacao.isEmpty())  ps.setString(i++, operacao);
                if (dtInicio  != null && !dtInicio.isEmpty())  ps.setString(i++, dtInicio);
                if (dtFim     != null && !dtFim.isEmpty())     ps.setString(i++, dtFim);
                ps.setInt(i++, (pagina - 1) * tamPag);
                ps.setInt(i,   tamPag);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id",          rs.getLong("id"));
                        row.addProperty("login",       rs.getString("login"));
                        row.addProperty("dtOperacao",  String.valueOf(rs.getTimestamp("dt_operacao")));
                        row.addProperty("ip",          rs.getString("ip_terminal"));
                        row.addProperty("funcional",   rs.getString("funcionalidade"));
                        row.addProperty("operacao",    rs.getString("operacao"));
                        row.addProperty("idRegistro",  rs.getString("id_registro"));
                        row.addProperty("descricao",   rs.getString("descricao"));
                        rows.add(row);
                    }
                }
            }
            result.addProperty("ok", true);
            result.add("registros", rows);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return gson.toJson(result);
    }
}
