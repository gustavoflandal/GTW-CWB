package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
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
        if (!ControleAcesso.exigir(req, resp)) return;

        String acao = req.getParameter("acao");
        if (acao == null) { resp.sendError(400); return; }

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        if ("consultar".equals(acao)) {
            out.print(consultar(req));
        } else if ("listarFiltros".equals(acao)) {
            out.print(listarFiltros());
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    private String listarFiltros() {
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            result.add("logins", buscarDistintos(conn, "login"));
            result.add("funcionalidades", buscarDistintos(conn, "funcionalidade"));
            result.add("operacoes", buscarDistintos(conn, "operacao"));
            result.add("perfis", buscarPerfis(conn));
            result.add("equipamentos", buscarEquipamentos(conn));
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception e2) {}
        }
        return gson.toJson(result);
    }

    private JsonArray buscarDistintos(Connection conn, String coluna) throws Exception {
        JsonArray arr = new JsonArray();
        String sql = "SELECT DISTINCT " + coluna + " FROM dbo.sis_log_auditoria "
                   + "WHERE " + coluna + " IS NOT NULL AND " + coluna + " <> '' "
                   + "ORDER BY " + coluna;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                arr.add(rs.getString(1));
            }
        }
        return arr;
    }

    private JsonArray buscarPerfis(Connection conn) throws Exception {
        JsonArray arr = new JsonArray();
        String sql = "SELECT DISTINCT sg.id_grupo, RTRIM(sg.descricao) AS descricao " +
                     "FROM dbo.sis_grupo sg " +
                     "JOIN dbo.sis_usuario_grupo sug ON sug.id_grupo = sg.id_grupo " +
                     "JOIN dbo.sis_log_auditoria la ON la.id_usuario = sug.id_usuario " +
                     "ORDER BY descricao";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", rs.getInt("id_grupo"));
                obj.addProperty("descricao", rs.getString("descricao"));
                arr.add(obj);
            }
        }
        return arr;
    }

    private JsonArray buscarEquipamentos(Connection conn) throws Exception {
        JsonArray arr = new JsonArray();
        String sql = "SELECT DISTINCT lv.id_local, RTRIM(lv.nome) AS nome " +
                     "FROM local_vigente lv " +
                     "WHERE lv.desativado = 0 " +
                     "ORDER BY nome";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", rs.getInt("id_local"));
                obj.addProperty("nome", rs.getString("nome"));
                arr.add(obj);
            }
        }
        return arr;
    }

    private String consultar(HttpServletRequest req) {
        String login     = req.getParameter("login");
        String funcional = req.getParameter("funcionalidade");
        String operacao  = req.getParameter("operacao");
        String dtInicio  = req.getParameter("dtInicio");
        String dtFim     = req.getParameter("dtFim");
        String perfil    = req.getParameter("perfil");
        String equip     = req.getParameter("equipamento");
        String pagStr    = req.getParameter("pagina");
        int pagina       = pagStr != null ? Integer.parseInt(pagStr) : 1;
        int tamPag       = 50;

        boolean temPerfil = perfil != null && !perfil.isEmpty();
        boolean temEquip  = equip  != null && !equip.isEmpty();

        StringBuilder sql = new StringBuilder(
            "SELECT la.id, la.id_usuario, la.login, la.dt_operacao, la.ip_terminal, " +
            "la.funcionalidade, la.operacao, la.id_registro, la.descricao");
        if (temPerfil) {
            sql.append(", RTRIM(sg.descricao) AS perfil_grupo");
        }
        sql.append(" FROM dbo.sis_log_auditoria la ");

        if (temPerfil) {
            sql.append("JOIN dbo.sis_usuario_grupo sug ON sug.id_usuario = la.id_usuario ");
            sql.append("JOIN dbo.sis_grupo sg ON sg.id_grupo = sug.id_grupo ");
        }
        if (temEquip) {
            sql.append("JOIN dbo.sis_usuario su_eq ON su_eq.id_usuario = la.id_usuario ");
        }

        sql.append("WHERE 1=1 ");
        if (login     != null && !login.isEmpty())     sql.append("AND la.login = ? ");
        if (funcional != null && !funcional.isEmpty()) sql.append("AND la.funcionalidade = ? ");
        if (operacao  != null && !operacao.isEmpty())  sql.append("AND la.operacao = ? ");
        if (dtInicio  != null && !dtInicio.isEmpty())  sql.append("AND la.dt_operacao >= ? ");
        if (dtFim     != null && !dtFim.isEmpty())     sql.append("AND la.dt_operacao < DATEADD(day,1,?) ");
        if (temPerfil)                                 sql.append("AND sg.id_grupo = ? ");
        if (temEquip)                                  sql.append("AND la.descricao LIKE ? ");
        sql.append("ORDER BY la.dt_operacao DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        JsonObject result = new JsonObject();
        JsonArray rows    = new JsonArray();
        Connection conn   = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int i = 1;
                if (login     != null && !login.isEmpty())     ps.setString(i++, login);
                if (funcional != null && !funcional.isEmpty()) ps.setString(i++, funcional);
                if (operacao  != null && !operacao.isEmpty())  ps.setString(i++, operacao);
                if (dtInicio  != null && !dtInicio.isEmpty())  ps.setString(i++, dtInicio);
                if (dtFim     != null && !dtFim.isEmpty())     ps.setString(i++, dtFim);
                if (temPerfil)                                 ps.setInt(i++, Integer.parseInt(perfil));
                if (temEquip)                                  ps.setString(i++, "%" + equip + "%");
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
                        if (temPerfil) {
                            row.addProperty("perfil",  rs.getString("perfil_grupo"));
                        }
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
