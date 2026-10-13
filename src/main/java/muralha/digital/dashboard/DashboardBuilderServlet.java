package muralha.digital.dashboard;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/MuralhaDigital/DashboardBuilder")
public class DashboardBuilderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Set<String> AGREGACOES_VALIDAS = new HashSet<>();
    static {
        AGREGACOES_VALIDAS.add("SUM");
        AGREGACOES_VALIDAS.add("AVG");
        AGREGACOES_VALIDAS.add("COUNT");
        AGREGACOES_VALIDAS.add("MAX");
        AGREGACOES_VALIDAS.add("MIN");
        AGREGACOES_VALIDAS.add("NONE");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;
        int idUsuario = acesso.getUsuario().getId();

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("listarPaineis".equals(acao)) {
                JsonArray paineis = new JsonArray();
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = conn.prepareStatement(
                            "SELECT id, nome, descricao, id_usuario, refresh_segundos, publico, " +
                            "criado_em, atualizado_em FROM muralha.dashboard_painel " +
                            "WHERE id_usuario = ? OR publico = 1 ORDER BY nome");
                    ps.setInt(1, idUsuario);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        JsonObject p = new JsonObject();
                        p.addProperty("id", rs.getInt("id"));
                        p.addProperty("nome", rs.getString("nome"));
                        p.addProperty("descricao", rs.getString("descricao"));
                        p.addProperty("idUsuario", rs.getInt("id_usuario"));
                        p.addProperty("refreshSegundos", rs.getInt("refresh_segundos"));
                        p.addProperty("publico", rs.getBoolean("publico"));
                        p.addProperty("proprio", rs.getInt("id_usuario") == idUsuario);
                        if (rs.getTimestamp("criado_em") != null)
                            p.addProperty("criadoEm", rs.getTimestamp("criado_em").toString());
                        if (rs.getTimestamp("atualizado_em") != null)
                            p.addProperty("atualizadoEm", rs.getTimestamp("atualizado_em").toString());
                        paineis.add(p);
                    }
                } finally {
                    if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                    if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                }
                result.addProperty("ok", true);
                result.add("paineis", paineis);

            } else if ("obterPainel".equals(acao)) {
                String idStr = req.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro id obrigatorio");
                } else {
                    int idPainel = Integer.parseInt(idStr);
                    JsonObject painel = obterPainel(conn, idPainel, idUsuario);
                    if (painel == null) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Painel nao encontrado ou sem acesso");
                    } else {
                        result.addProperty("ok", true);
                        result.add("painel", painel);
                    }
                }

            } else if ("executarWidget".equals(acao)) {
                String idStr = req.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro id obrigatorio");
                } else {
                    JsonObject dados = executarWidget(conn, Integer.parseInt(idStr), idUsuario);
                    result.addProperty("ok", true);
                    result.add("dados", dados);
                }

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;
        int idUsuario = acesso.getUsuario().getId();

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("salvarPainel".equals(acao)) {
                String nome = req.getParameter("nome");
                if (nome == null || nome.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Nome obrigatorio");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
                String descricao = req.getParameter("descricao");
                int refresh = 60;
                try { refresh = Integer.parseInt(req.getParameter("refreshSegundos")); } catch (Exception ignored) {}
                if (refresh < 30) refresh = 30;
                if (refresh > 300) refresh = 300;
                boolean publico = "true".equals(req.getParameter("publico"));

                String idStr = req.getParameter("id");
                if (idStr != null && !idStr.isEmpty()) {
                    int idPainel = Integer.parseInt(idStr);
                    if (!verificaDono(conn, idPainel, idUsuario)) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Sem permissao para editar este painel");
                        resp.getWriter().print(new Gson().toJson(result));
                        return;
                    }
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement(
                                "UPDATE muralha.dashboard_painel SET nome = ?, descricao = ?, " +
                                "refresh_segundos = ?, publico = ?, atualizado_em = GETDATE() WHERE id = ?");
                        ps.setString(1, nome);
                        ps.setString(2, descricao);
                        ps.setInt(3, refresh);
                        ps.setBoolean(4, publico);
                        ps.setInt(5, idPainel);
                        ps.executeUpdate();
                    } finally {
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                    result.addProperty("ok", true);
                    result.addProperty("id", idPainel);
                } else {
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        ps = conn.prepareStatement(
                                "INSERT INTO muralha.dashboard_painel " +
                                "(nome, descricao, id_usuario, refresh_segundos, publico) " +
                                "VALUES (?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, nome);
                        ps.setString(2, descricao);
                        ps.setInt(3, idUsuario);
                        ps.setInt(4, refresh);
                        ps.setBoolean(5, publico);
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        int novoId = 0;
                        if (rs.next()) novoId = rs.getInt(1);
                        result.addProperty("ok", true);
                        result.addProperty("id", novoId);
                    } finally {
                        if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                }

            } else if ("excluirPainel".equals(acao)) {
                String idStr = req.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro id obrigatorio");
                } else {
                    int idPainel = Integer.parseInt(idStr);
                    if (!verificaDono(conn, idPainel, idUsuario)) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Sem permissao para excluir este painel");
                    } else {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement(
                                    "DELETE FROM muralha.dashboard_painel WHERE id = ?");
                            ps.setInt(1, idPainel);
                            ps.executeUpdate();
                        } finally {
                            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                        }
                        result.addProperty("ok", true);
                    }
                }

            } else if ("salvarWidget".equals(acao)) {
                JsonObject body = lerJsonBody(req);
                if (body == null) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Body JSON invalido");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
                int idPainel = body.get("idPainel").getAsInt();
                if (!verificaDono(conn, idPainel, idUsuario)) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Sem permissao para editar este painel");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }

                String tipo = body.get("tipo").getAsString();
                String titulo = body.get("titulo").getAsString();
                int idDataset = body.get("idDataset").getAsInt();
                String colunaValor = getStr(body, "colunaValor");
                String colunaLabel = getStr(body, "colunaLabel");
                String colunaLat = getStr(body, "colunaLat");
                String colunaLng = getStr(body, "colunaLng");
                String agregacao = getStr(body, "agregacao");
                if (agregacao == null || !AGREGACOES_VALIDAS.contains(agregacao)) agregacao = "NONE";
                String filtrosJson = getStr(body, "filtrosJson");
                String tipoGrafico = getStr(body, "tipoGrafico");
                String colunasTabela = getStr(body, "colunasTabela");
                Double thOk = body.has("thresholdOk") && !body.get("thresholdOk").isJsonNull()
                        ? body.get("thresholdOk").getAsDouble() : null;
                Double thWarn = body.has("thresholdWarn") && !body.get("thresholdWarn").isJsonNull()
                        ? body.get("thresholdWarn").getAsDouble() : null;
                int gridCol = body.has("gridCol") ? body.get("gridCol").getAsInt() : 1;
                int gridRow = body.has("gridRow") ? body.get("gridRow").getAsInt() : 1;
                int gridWidth = body.has("gridWidth") ? body.get("gridWidth").getAsInt() : 4;
                int gridHeight = body.has("gridHeight") ? body.get("gridHeight").getAsInt() : 2;

                JsonElement idEl = body.get("id");
                if (idEl != null && !idEl.isJsonNull() && idEl.getAsInt() > 0) {
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement(
                                "UPDATE muralha.dashboard_widget SET tipo=?, titulo=?, id_dataset=?, " +
                                "coluna_valor=?, coluna_label=?, coluna_lat=?, coluna_lng=?, " +
                                "agregacao=?, filtros_json=?, tipo_grafico=?, colunas_tabela=?, " +
                                "threshold_ok=?, threshold_warn=?, " +
                                "grid_col=?, grid_row=?, grid_width=?, grid_height=? WHERE id=?");
                        int nextIdx = setWidgetParams(ps, 1, tipo, titulo, idDataset, colunaValor, colunaLabel,
                                colunaLat, colunaLng, agregacao, filtrosJson, tipoGrafico, colunasTabela,
                                thOk, thWarn, gridCol, gridRow, gridWidth, gridHeight);
                        ps.setInt(nextIdx, idEl.getAsInt());
                        ps.executeUpdate();
                    } finally {
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                    result.addProperty("ok", true);
                    result.addProperty("id", idEl.getAsInt());
                } else {
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try {
                        ps = conn.prepareStatement(
                                "INSERT INTO muralha.dashboard_widget " +
                                "(id_painel, tipo, titulo, id_dataset, coluna_valor, coluna_label, " +
                                "coluna_lat, coluna_lng, agregacao, filtros_json, tipo_grafico, " +
                                "colunas_tabela, threshold_ok, threshold_warn, " +
                                "grid_col, grid_row, grid_width, grid_height) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, idPainel);
                        setWidgetParams(ps, 2, tipo, titulo, idDataset, colunaValor, colunaLabel,
                                colunaLat, colunaLng, agregacao, filtrosJson, tipoGrafico, colunasTabela,
                                thOk, thWarn, gridCol, gridRow, gridWidth, gridHeight);
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        int novoId = 0;
                        if (rs.next()) novoId = rs.getInt(1);
                        result.addProperty("ok", true);
                        result.addProperty("id", novoId);
                    } finally {
                        if (rs != null) try { rs.close(); } catch (Exception ignored) {}
                        if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                    }
                }

            } else if ("excluirWidget".equals(acao)) {
                String idStr = req.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Parametro id obrigatorio");
                } else {
                    int idWidget = Integer.parseInt(idStr);
                    int donoPainel = obterDonoPainelDoWidget(conn, idWidget);
                    if (donoPainel != idUsuario) {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Sem permissao");
                    } else {
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement(
                                    "DELETE FROM muralha.dashboard_widget WHERE id = ?");
                            ps.setInt(1, idWidget);
                            ps.executeUpdate();
                        } finally {
                            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
                        }
                        result.addProperty("ok", true);
                    }
                }

            } else if ("atualizarLayout".equals(acao)) {
                JsonArray items = lerJsonArrayBody(req);
                if (items == null || items.size() == 0) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Body JSON array invalido");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(
                            "UPDATE muralha.dashboard_widget SET grid_col=?, grid_row=?, " +
                            "grid_width=?, grid_height=? WHERE id=?");
                    for (JsonElement el : items) {
                        JsonObject item = el.getAsJsonObject();
                        int wId = item.get("id").getAsInt();
                        int donoPainel = obterDonoPainelDoWidget(conn, wId);
                        if (donoPainel != idUsuario) continue;
                        ps.setInt(1, item.get("gridCol").getAsInt());
                        ps.setInt(2, item.get("gridRow").getAsInt());
                        ps.setInt(3, item.get("gridWidth").getAsInt());
                        ps.setInt(4, item.get("gridHeight").getAsInt());
                        ps.setInt(5, wId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                } finally {
                    if (ps != null) try { ps.close(); } catch (Exception ignored) {}
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

    private int setWidgetParams(PreparedStatement ps, int startIdx, String tipo, String titulo,
            int idDataset, String colunaValor, String colunaLabel,
            String colunaLat, String colunaLng, String agregacao,
            String filtrosJson, String tipoGrafico, String colunasTabela,
            Double thOk, Double thWarn,
            int gridCol, int gridRow, int gridWidth, int gridHeight) throws Exception {
        int i = startIdx;
        ps.setString(i++, tipo);
        ps.setString(i++, titulo);
        ps.setInt(i++, idDataset);
        ps.setString(i++, colunaValor);
        ps.setString(i++, colunaLabel);
        ps.setString(i++, colunaLat);
        ps.setString(i++, colunaLng);
        ps.setString(i++, agregacao);
        ps.setString(i++, filtrosJson);
        ps.setString(i++, tipoGrafico);
        ps.setString(i++, colunasTabela);
        if (thOk != null) ps.setDouble(i++, thOk); else ps.setNull(i++, java.sql.Types.DOUBLE);
        if (thWarn != null) ps.setDouble(i++, thWarn); else ps.setNull(i++, java.sql.Types.DOUBLE);
        ps.setInt(i++, gridCol);
        ps.setInt(i++, gridRow);
        ps.setInt(i++, gridWidth);
        ps.setInt(i++, gridHeight);
        return i;
    }

    private JsonObject obterPainel(Connection conn, int idPainel, int idUsuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT id, nome, descricao, id_usuario, refresh_segundos, publico " +
                    "FROM muralha.dashboard_painel WHERE id = ? AND (id_usuario = ? OR publico = 1)");
            ps.setInt(1, idPainel);
            ps.setInt(2, idUsuario);
            rs = ps.executeQuery();
            if (!rs.next()) return null;

            JsonObject p = new JsonObject();
            p.addProperty("id", rs.getInt("id"));
            p.addProperty("nome", rs.getString("nome"));
            p.addProperty("descricao", rs.getString("descricao"));
            p.addProperty("idUsuario", rs.getInt("id_usuario"));
            p.addProperty("refreshSegundos", rs.getInt("refresh_segundos"));
            p.addProperty("publico", rs.getBoolean("publico"));
            p.addProperty("proprio", rs.getInt("id_usuario") == idUsuario);

            rs.close();
            ps.close();

            ps = conn.prepareStatement(
                    "SELECT w.id, w.tipo, w.titulo, w.id_dataset, w.coluna_valor, w.coluna_label, " +
                    "w.coluna_lat, w.coluna_lng, w.agregacao, w.filtros_json, w.tipo_grafico, " +
                    "w.colunas_tabela, w.threshold_ok, w.threshold_warn, " +
                    "w.grid_col, w.grid_row, w.grid_width, w.grid_height, " +
                    "d.nome AS dataset_nome " +
                    "FROM muralha.dashboard_widget w " +
                    "JOIN muralha.dashboard_dataset d ON w.id_dataset = d.id " +
                    "WHERE w.id_painel = ? ORDER BY w.grid_row, w.grid_col");
            ps.setInt(1, idPainel);
            rs = ps.executeQuery();
            JsonArray widgets = new JsonArray();
            while (rs.next()) {
                JsonObject w = new JsonObject();
                w.addProperty("id", rs.getInt("id"));
                w.addProperty("tipo", rs.getString("tipo"));
                w.addProperty("titulo", rs.getString("titulo"));
                w.addProperty("idDataset", rs.getInt("id_dataset"));
                w.addProperty("datasetNome", rs.getString("dataset_nome"));
                w.addProperty("colunaValor", rs.getString("coluna_valor"));
                w.addProperty("colunaLabel", rs.getString("coluna_label"));
                w.addProperty("colunaLat", rs.getString("coluna_lat"));
                w.addProperty("colunaLng", rs.getString("coluna_lng"));
                w.addProperty("agregacao", rs.getString("agregacao"));
                w.addProperty("filtrosJson", rs.getString("filtros_json"));
                w.addProperty("tipoGrafico", rs.getString("tipo_grafico"));
                w.addProperty("colunasTabela", rs.getString("colunas_tabela"));
                double thOk = rs.getDouble("threshold_ok");
                if (!rs.wasNull()) w.addProperty("thresholdOk", thOk);
                double thWarn = rs.getDouble("threshold_warn");
                if (!rs.wasNull()) w.addProperty("thresholdWarn", thWarn);
                w.addProperty("gridCol", rs.getInt("grid_col"));
                w.addProperty("gridRow", rs.getInt("grid_row"));
                w.addProperty("gridWidth", rs.getInt("grid_width"));
                w.addProperty("gridHeight", rs.getInt("grid_height"));
                widgets.add(w);
            }
            p.add("widgets", widgets);
            return p;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
    }

    private JsonObject executarWidget(Connection conn, int idWidget, int idUsuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT w.tipo, w.coluna_valor, w.coluna_label, w.coluna_lat, w.coluna_lng, " +
                    "w.agregacao, w.filtros_json, w.colunas_tabela, d.nome_view, d.colunas_json " +
                    "FROM muralha.dashboard_widget w " +
                    "JOIN muralha.dashboard_dataset d ON w.id_dataset = d.id " +
                    "JOIN muralha.dashboard_painel p ON w.id_painel = p.id " +
                    "WHERE w.id = ? AND (p.id_usuario = ? OR p.publico = 1)");
            ps.setInt(1, idWidget);
            ps.setInt(2, idUsuario);
            rs = ps.executeQuery();
            if (!rs.next()) throw new Exception("Widget nao encontrado ou sem acesso");

            String tipo = rs.getString("tipo");
            String colunaValor = rs.getString("coluna_valor");
            String colunaLabel = rs.getString("coluna_label");
            String colunaLat = rs.getString("coluna_lat");
            String colunaLng = rs.getString("coluna_lng");
            String agregacao = rs.getString("agregacao");
            String filtrosJson = rs.getString("filtros_json");
            String colunasTabela = rs.getString("colunas_tabela");
            String nomeView = rs.getString("nome_view");
            String colunasJsonStr = rs.getString("colunas_json");

            rs.close();
            ps.close();

            Set<String> colunasValidas = extrairColunasValidas(colunasJsonStr);
            StringBuilder sql = new StringBuilder();

            if ("TABLE".equals(tipo)) {
                sql.append("SELECT TOP 500 ");
                if (colunasTabela != null && !colunasTabela.isEmpty()) {
                    String[] cols = colunasTabela.split(",");
                    boolean first = true;
                    for (String c : cols) {
                        String col = c.trim();
                        if (!colunasValidas.contains(col)) continue;
                        if (!first) sql.append(", ");
                        sql.append(col);
                        first = false;
                    }
                } else {
                    sql.append("*");
                }
                sql.append(" FROM ").append(nomeView);

            } else if ("MAP".equals(tipo)) {
                sql.append("SELECT TOP 1000 ");
                if (colunaLat != null && colunasValidas.contains(colunaLat)
                        && colunaLng != null && colunasValidas.contains(colunaLng)) {
                    sql.append(colunaLat).append(", ").append(colunaLng);
                    if (colunaLabel != null && colunasValidas.contains(colunaLabel)) {
                        sql.append(", ").append(colunaLabel);
                    }
                } else {
                    sql.append("*");
                }
                sql.append(" FROM ").append(nomeView);

            } else if ("CARD".equals(tipo)) {
                if (colunaValor != null && colunasValidas.contains(colunaValor)
                        && !"NONE".equals(agregacao)) {
                    sql.append("SELECT ").append(agregacao).append("(").append(colunaValor)
                       .append(") AS valor FROM ").append(nomeView);
                } else {
                    sql.append("SELECT COUNT(*) AS valor FROM ").append(nomeView);
                }

            } else {
                if (colunaLabel != null && colunasValidas.contains(colunaLabel)
                        && colunaValor != null && colunasValidas.contains(colunaValor)
                        && !"NONE".equals(agregacao)) {
                    sql.append("SELECT ").append(colunaLabel).append(" AS label, ")
                       .append(agregacao).append("(").append(colunaValor).append(") AS valor")
                       .append(" FROM ").append(nomeView);

                    String whereClause = buildWhereClause(filtrosJson, colunasValidas);
                    if (whereClause != null) sql.append(" WHERE ").append(whereClause);

                    sql.append(" GROUP BY ").append(colunaLabel)
                       .append(" ORDER BY ").append(colunaLabel);
                } else {
                    sql.append("SELECT TOP 100 * FROM ").append(nomeView);
                }
            }

            if (!"CHART".equals(tipo)) {
                String whereClause = buildWhereClause(filtrosJson, colunasValidas);
                if (whereClause != null) {
                    if (sql.toString().contains(" WHERE ")) {
                        sql.append(" AND ").append(whereClause);
                    } else {
                        sql.append(" WHERE ").append(whereClause);
                    }
                }
            }

            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            JsonObject dados = new JsonObject();
            JsonArray headers = new JsonArray();
            for (int i = 1; i <= colCount; i++) {
                headers.add(meta.getColumnLabel(i));
            }
            dados.add("headers", headers);

            JsonArray rows = new JsonArray();
            while (rs.next()) {
                JsonArray row = new JsonArray();
                for (int i = 1; i <= colCount; i++) {
                    Object val = rs.getObject(i);
                    if (val == null) {
                        row.add((String) null);
                    } else if (val instanceof Number) {
                        row.add(((Number) val).doubleValue());
                    } else {
                        row.add(val.toString());
                    }
                }
                rows.add(row);
            }
            dados.add("rows", rows);
            return dados;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
    }

    private Set<String> extrairColunasValidas(String colunasJsonStr) {
        Set<String> validas = new HashSet<>();
        try {
            JsonArray arr = JsonParser.parseString(colunasJsonStr).getAsJsonArray();
            for (JsonElement el : arr) {
                validas.add(el.getAsJsonObject().get("nome").getAsString());
            }
        } catch (Exception ignored) {}
        return validas;
    }

    private String buildWhereClause(String filtrosJson, Set<String> colunasValidas) {
        if (filtrosJson == null || filtrosJson.isEmpty()) return null;
        try {
            JsonArray filtros = JsonParser.parseString(filtrosJson).getAsJsonArray();
            StringBuilder where = new StringBuilder();
            for (JsonElement el : filtros) {
                JsonObject f = el.getAsJsonObject();
                String col = f.get("coluna").getAsString();
                if (!colunasValidas.contains(col)) continue;
                String op = f.get("operador").getAsString();
                String val = f.get("valor").getAsString();
                if (!op.matches("^(=|!=|<|>|<=|>=|LIKE)$")) continue;

                if (where.length() > 0) where.append(" AND ");
                where.append(col).append(" ").append(op).append(" '")
                     .append(val.replace("'", "''")).append("'");
            }
            return where.length() > 0 ? where.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean verificaDono(Connection conn, int idPainel, int idUsuario) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT id_usuario FROM muralha.dashboard_painel WHERE id = ?");
            ps.setInt(1, idPainel);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt("id_usuario") == idUsuario;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
    }

    private int obterDonoPainelDoWidget(Connection conn, int idWidget) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT p.id_usuario FROM muralha.dashboard_widget w " +
                    "JOIN muralha.dashboard_painel p ON w.id_painel = p.id WHERE w.id = ?");
            ps.setInt(1, idWidget);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id_usuario") : -1;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (ps != null) try { ps.close(); } catch (Exception ignored) {}
        }
    }

    private JsonObject lerJsonBody(HttpServletRequest req) {
        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return JsonParser.parseString(sb.toString()).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    private JsonArray lerJsonArrayBody(HttpServletRequest req) {
        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return JsonParser.parseString(sb.toString()).getAsJsonArray();
        } catch (Exception e) {
            return null;
        }
    }

    private String getStr(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return null;
        String val = obj.get(key).getAsString();
        return val.isEmpty() ? null : val;
    }
}
