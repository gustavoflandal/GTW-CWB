package muralha.digital.dashboard;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

@WebServlet("/MuralhaDigital/Dataset")
public class DatasetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("listarTodos".equals(acao)) {
                result.addProperty("ok", true);
                result.add("datasets", listarDatasets(conn, false));

            } else if ("obterColunas".equals(acao)) {
                String idStr = req.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro id obrigatorio");
                } else {
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        ps = conn.prepareStatement(
                                "SELECT colunas_json FROM muralha.dashboard_dataset WHERE id = ?");
                        ps.setInt(1, Integer.parseInt(idStr));
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            result.addProperty("ok", true);
                            result.addProperty("colunas", rs.getString("colunas_json"));
                        } else {
                            result.addProperty("ok", false);
                            result.addProperty("erro", "Dataset nao encontrado");
                        }
                    } finally {
                        if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                }

            } else if ("detectarColunas".equals(acao)) {
                String nomeView = req.getParameter("nomeView");
                if (nomeView == null || nomeView.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro nomeView obrigatorio");
                } else {
                    if (!nomeView.matches("^[a-zA-Z_][a-zA-Z0-9_.]+$")) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Nome de view invalido");
                    } else {
                        PreparedStatement ps = null;
                        ResultSet rs = null;
                        try {
                            ps = conn.prepareStatement(
                                    "SELECT TOP 0 * FROM " + nomeView);
                            rs = ps.executeQuery();
                            ResultSetMetaData meta = rs.getMetaData();
                            JsonArray colunas = new JsonArray();
                            for (int i = 1; i <= meta.getColumnCount(); i++) {
                                JsonObject col = new JsonObject();
                                col.addProperty("nome", meta.getColumnName(i));
                                col.addProperty("tipo", inferirTipo(meta.getColumnTypeName(i)));
                                col.addProperty("label", meta.getColumnLabel(i));
                                colunas.add(col);
                            }
                            result.addProperty("ok", true);
                            result.add("colunas", colunas);
                        } finally {
                            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                        }
                    }
                }

            } else {
                result.addProperty("ok", true);
                result.add("datasets", listarDatasets(conn, true));
            }

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
        if (!ControleAcesso.exigir(req, resp)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("salvar".equals(acao)) {
                String nome = req.getParameter("nome");
                String descricao = req.getParameter("descricao");
                String nomeView = req.getParameter("nomeView");
                String colunasJson = req.getParameter("colunasJson");

                if (nome == null || nome.isEmpty() || nomeView == null || nomeView.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Nome e nomeView obrigatorios");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }

                if (!nomeView.matches("^[a-zA-Z_][a-zA-Z0-9_.]+$")) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Nome de view invalido");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }

                String idStr = req.getParameter("id");
                boolean ativo = !"false".equals(req.getParameter("ativo"));

                if (idStr != null && !idStr.isEmpty()) {
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement(
                                "UPDATE muralha.dashboard_dataset SET nome = ?, descricao = ?, " +
                                "nome_view = ?, colunas_json = ?, ativo = ? WHERE id = ?");
                        ps.setString(1, nome);
                        ps.setString(2, descricao);
                        ps.setString(3, nomeView);
                        ps.setString(4, colunasJson);
                        ps.setBoolean(5, ativo);
                        ps.setInt(6, Integer.parseInt(idStr));
                        ps.executeUpdate();
                    } finally {
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                } else {
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement(
                                "INSERT INTO muralha.dashboard_dataset " +
                                "(nome, descricao, nome_view, colunas_json, ativo) VALUES (?, ?, ?, ?, ?)");
                        ps.setString(1, nome);
                        ps.setString(2, descricao);
                        ps.setString(3, nomeView);
                        ps.setString(4, colunasJson);
                        ps.setBoolean(5, ativo);
                        ps.executeUpdate();
                    } finally {
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                }
                result.addProperty("ok", true);

            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "Acao invalida");
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonArray listarDatasets(Connection conn, boolean apenasAtivos) throws Exception {
        JsonArray arr = new JsonArray();
        String sql = "SELECT id, nome, descricao, nome_view, colunas_json, ativo " +
                     "FROM muralha.dashboard_dataset" +
                     (apenasAtivos ? " WHERE ativo = 1" : "") +
                     " ORDER BY nome";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("id", rs.getInt("id"));
                o.addProperty("nome", rs.getString("nome"));
                o.addProperty("descricao", rs.getString("descricao"));
                o.addProperty("nomeView", rs.getString("nome_view"));
                o.addProperty("colunasJson", rs.getString("colunas_json"));
                o.addProperty("ativo", rs.getBoolean("ativo"));
                arr.add(o);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
        return arr;
    }

    private String inferirTipo(String sqlType) {
        if (sqlType == null) return "texto";
        String upper = sqlType.toUpperCase();
        if (upper.contains("INT") || upper.contains("FLOAT") || upper.contains("DECIMAL")
                || upper.contains("NUMERIC") || upper.contains("REAL") || upper.contains("MONEY")) {
            return "numerico";
        }
        if (upper.contains("DATE") || upper.contains("TIME")) {
            return "data";
        }
        return "texto";
    }
}
