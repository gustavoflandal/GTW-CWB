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
import java.sql.Statement;

/**
 * Administração das regras de captura de log (dbo.sis_log_regra).
 *
 * Acesso estrito (grupo Administradores). Após qualquer alteração, recarrega o
 * cache de {@link MotorRegras} para valer imediatamente, sem redeploy.
 */
@WebServlet("/MuralhaDigital/RegraLog")
public class RegraLogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();
    /** Ação usada no controle de acesso (tela administrativa). */
    private static final String ACAO_TELA = "/muralha-digital/pages/admin/logs-regras/index.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp, ACAO_TELA)) return;
        json(resp);
        PrintWriter out = resp.getWriter();
        String acao = req.getParameter("acao");
        if ("listar".equals(acao)) {
            out.print(listar());
        } else if ("listarTipos".equals(acao)) {
            out.print(listarTipos());
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
        } else if ("recarregar".equals(acao)) {
            MotorRegras.recarregar();
            TiposEvento.recarregar();
            out.print("{\"ok\":true}");
        } else if ("salvarTipos".equals(acao)) {
            out.print(salvarTipos(req));
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    private String listarTipos() {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT codigo, descricao, capturar, ordem FROM dbo.sis_log_tipo_evento ORDER BY ordem, codigo");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("codigo", rs.getString("codigo"));
                    o.addProperty("descricao", rs.getString("descricao"));
                    o.addProperty("capturar", rs.getBoolean("capturar"));
                    arr.add(o);
                }
            }
            result.addProperty("ok", true);
            result.add("tipos", arr);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private String salvarTipos(HttpServletRequest req) {
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            String ativos = str(req, "ativos"); // CSV dos codigos habilitados
            java.util.Set<String> set = new java.util.HashSet<>();
            if (ativos != null) {
                for (String c : ativos.split(",")) {
                    String s = c.trim().toUpperCase();
                    if (!s.isEmpty()) set.add(s);
                }
            }
            conn = Conexao.getConexao();
            java.util.List<String> codigos = new java.util.ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT codigo FROM dbo.sis_log_tipo_evento");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) codigos.add(rs.getString("codigo"));
            }
            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE dbo.sis_log_tipo_evento SET capturar=? WHERE codigo=?")) {
                for (String cod : codigos) {
                    upd.setBoolean(1, set.contains(cod.toUpperCase()));
                    upd.setString(2, cod);
                    upd.addBatch();
                }
                upd.executeBatch();
            }
            TiposEvento.recarregar();
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private String listar() {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String sql = "SELECT id, ordem, descricao, padrao_rota, metodo, capturar, nivel, " +
                         "funcionalidade, registrar_parametros, mascarar_campos, garantido, ativo " +
                         "FROM dbo.sis_log_regra ORDER BY ordem, id";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getInt("id"));
                    o.addProperty("ordem", rs.getInt("ordem"));
                    o.addProperty("descricao", rs.getString("descricao"));
                    o.addProperty("padraoRota", rs.getString("padrao_rota"));
                    o.addProperty("metodo", rs.getString("metodo"));
                    o.addProperty("capturar", rs.getBoolean("capturar"));
                    o.addProperty("nivel", rs.getString("nivel"));
                    o.addProperty("funcionalidade", rs.getString("funcionalidade"));
                    o.addProperty("registrarParametros", rs.getBoolean("registrar_parametros"));
                    o.addProperty("mascararCampos", rs.getString("mascarar_campos"));
                    o.addProperty("garantido", rs.getBoolean("garantido"));
                    o.addProperty("ativo", rs.getBoolean("ativo"));
                    arr.add(o);
                }
            }
            result.addProperty("ok", true);
            result.add("regras", arr);
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
            conn = Conexao.getConexao();
            if (id > 0) {
                String sql = "UPDATE dbo.sis_log_regra SET ordem=?, descricao=?, padrao_rota=?, metodo=?, " +
                             "capturar=?, nivel=?, funcionalidade=?, registrar_parametros=?, mascarar_campos=?, " +
                             "garantido=?, ativo=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bind(ps, req);
                    ps.setInt(12, id);
                    ps.executeUpdate();
                }
            } else {
                String sql = "INSERT INTO dbo.sis_log_regra (ordem, descricao, padrao_rota, metodo, capturar, " +
                             "nivel, funcionalidade, registrar_parametros, mascarar_campos, garantido, ativo, login_criacao) " +
                             "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bind(ps, req);
                    ps.setString(12, loginUsuario(req));
                    ps.executeUpdate();
                }
            }
            MotorRegras.recarregar();
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            fechar(conn);
        }
        return gson.toJson(result);
    }

    private void bind(PreparedStatement ps, HttpServletRequest req) throws Exception {
        ps.setInt(1, intParam(req, "ordem", 100));
        ps.setString(2, str(req, "descricao"));
        ps.setString(3, str(req, "padraoRota"));
        String metodo = str(req, "metodo");
        if (metodo == null || metodo.isEmpty()) ps.setNull(4, java.sql.Types.VARCHAR); else ps.setString(4, metodo);
        ps.setBoolean(5, boolParam(req, "capturar"));
        ps.setString(6, strOr(req, "nivel", "AUTO"));
        String func = str(req, "funcionalidade");
        if (func == null || func.isEmpty()) ps.setNull(7, java.sql.Types.VARCHAR); else ps.setString(7, func);
        ps.setBoolean(8, boolParam(req, "registrarParametros"));
        String masc = str(req, "mascararCampos");
        if (masc == null || masc.isEmpty()) ps.setNull(9, java.sql.Types.VARCHAR); else ps.setString(9, masc);
        ps.setBoolean(10, boolParam(req, "garantido"));
        ps.setBoolean(11, boolParam(req, "ativo"));
    }

    private String excluir(HttpServletRequest req) {
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            int id = intParam(req, "id", 0);
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dbo.sis_log_regra WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            MotorRegras.recarregar();
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
    private static String strOr(HttpServletRequest r, String n, String d) { String v = str(r, n); return (v == null || v.isEmpty()) ? d : v; }
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
