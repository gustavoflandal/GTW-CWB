package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Administração das políticas de retenção e disparo do expurgo de logs.
 * Acesso estrito (grupo Administradores).
 */
@WebServlet("/MuralhaDigital/RetencaoLog")
public class RetencaoLogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();
    private static final String ACAO_TELA = "/muralha-digital/pages/admin/logs-retencao/index.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp, ACAO_TELA)) return;
        json(resp);
        PrintWriter out = resp.getWriter();
        String acao = req.getParameter("acao");
        if ("listar".equals(acao)) {
            out.print(listarPoliticas());
        } else if ("listarExpurgos".equals(acao)) {
            out.print(listarExpurgos());
        } else if ("simular".equals(acao)) {
            out.print(gson.toJson(ExpurgoService.executar(loginUsuario(req), true)));
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp, ACAO_TELA)) return;
        json(resp);
        PrintWriter out = resp.getWriter();
        String acao = req.getParameter("acao");
        if ("salvar".equals(acao)) {
            out.print(salvar(req));
        } else if ("excluir".equals(acao)) {
            out.print(excluir(req));
        } else if ("executarExpurgo".equals(acao)) {
            out.print(gson.toJson(ExpurgoService.executar(loginUsuario(req), false)));
        } else if ("gerarSelo".equals(acao)) {
            int n = SeloService.gerarSelosPendentes();
            JsonObject r = new JsonObject();
            r.addProperty("ok", true);
            r.addProperty("selosGerados", n);
            out.print(gson.toJson(r));
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    private String listarPoliticas() {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String sql = "SELECT id, nivel, dias_retencao, arquivar_antes, hold_legal, ativo, descricao " +
                         "FROM dbo.sis_log_retencao ORDER BY CASE WHEN nivel IS NULL THEN 1 ELSE 0 END, nivel";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getInt("id"));
                    o.addProperty("nivel", rs.getString("nivel"));
                    o.addProperty("diasRetencao", rs.getInt("dias_retencao"));
                    o.addProperty("arquivarAntes", rs.getBoolean("arquivar_antes"));
                    o.addProperty("holdLegal", rs.getBoolean("hold_legal"));
                    o.addProperty("ativo", rs.getBoolean("ativo"));
                    o.addProperty("descricao", rs.getString("descricao"));
                    arr.add(o);
                }
            }
            result.addProperty("ok", true);
            result.addProperty("diretorioArquivo", ExpurgoService.diretorioArquivo());
            result.add("politicas", arr);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private String listarExpurgos() {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String sql = "SELECT TOP 100 id, dt_execucao, nivel, qtd_removida, arquivo_gerado, login, observacao " +
                         "FROM dbo.sis_log_expurgo ORDER BY dt_execucao DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getInt("id"));
                    o.addProperty("dtExecucao", String.valueOf(rs.getTimestamp("dt_execucao")));
                    o.addProperty("nivel", rs.getString("nivel"));
                    o.addProperty("qtdRemovida", rs.getLong("qtd_removida"));
                    o.addProperty("arquivo", rs.getString("arquivo_gerado"));
                    o.addProperty("login", rs.getString("login"));
                    o.addProperty("observacao", rs.getString("observacao"));
                    arr.add(o);
                }
            }
            result.addProperty("ok", true);
            result.add("expurgos", arr);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private String salvar(HttpServletRequest req) {
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            int id = intParam(req, "id", 0);
            String nivel = str(req, "nivel");
            boolean nivelNulo = (nivel == null || nivel.isEmpty() || "(padrao)".equalsIgnoreCase(nivel));
            conn = Conexao.getConexao();
            if (id > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dbo.sis_log_retencao SET nivel=?, dias_retencao=?, arquivar_antes=?, " +
                        "hold_legal=?, ativo=?, descricao=? WHERE id=?")) {
                    if (nivelNulo) ps.setNull(1, java.sql.Types.VARCHAR); else ps.setString(1, nivel);
                    ps.setInt(2, intParam(req, "diasRetencao", 730));
                    ps.setBoolean(3, boolParam(req, "arquivarAntes"));
                    ps.setBoolean(4, boolParam(req, "holdLegal"));
                    ps.setBoolean(5, boolParam(req, "ativo"));
                    ps.setString(6, str(req, "descricao"));
                    ps.setInt(7, id);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dbo.sis_log_retencao (nivel, dias_retencao, arquivar_antes, hold_legal, ativo, descricao, login_criacao) " +
                        "VALUES (?,?,?,?,?,?,?)")) {
                    if (nivelNulo) ps.setNull(1, java.sql.Types.VARCHAR); else ps.setString(1, nivel);
                    ps.setInt(2, intParam(req, "diasRetencao", 730));
                    ps.setBoolean(3, boolParam(req, "arquivarAntes"));
                    ps.setBoolean(4, boolParam(req, "holdLegal"));
                    ps.setBoolean(5, boolParam(req, "ativo"));
                    ps.setString(6, str(req, "descricao"));
                    ps.setString(7, loginUsuario(req));
                    ps.executeUpdate();
                }
            }
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private String excluir(HttpServletRequest req) {
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dbo.sis_log_retencao WHERE id=?")) {
                ps.setInt(1, intParam(req, "id", 0));
                ps.executeUpdate();
            }
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    // helpers
    private static void json(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }
    private static void fechar(Connection c) { if (c != null) try { c.close(); } catch (Exception e) {} }
    private static String str(HttpServletRequest r, String n) { String v = r.getParameter(n); return v == null ? null : v.trim(); }
    private static int intParam(HttpServletRequest r, String n, int d) {
        try { return Integer.parseInt(r.getParameter(n)); } catch (Exception e) { return d; }
    }
    private static boolean boolParam(HttpServletRequest r, String n) {
        String v = r.getParameter(n);
        return "1".equals(v) || "true".equalsIgnoreCase(v) || "on".equalsIgnoreCase(v);
    }
    private static String loginUsuario(HttpServletRequest req) {
        try {
            com.consilux.model.Usuario u = ControleAcesso.usuario(req);
            return u != null ? u.getUsuario() : null;
        } catch (Exception e) { return null; }
    }
}
