package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.*;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/api/dash/widgets")
public class WidgetServlet extends HttpServlet {

    private final WidgetService service = new WidgetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("obter".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = service.obter(conn, id);

            } else if ("metaDataset".equals(acao)) {
                int idDataset = Integer.parseInt(req.getParameter("idDataset"));
                result = obterMetaDataset(conn, idDataset);

            } else if ("datasetsPublicados".equals(acao)) {
                result = listarDatasetsPublicados(conn);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int idUsuario = acesso.getUsuario().getId();

            StringBuilder body = new StringBuilder();
            String line;
            var reader = req.getReader();
            while ((line = reader.readLine()) != null) body.append(line);

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();
            String acao = dados.has("acao") ? dados.get("acao").getAsString() : "";

            if ("salvar".equals(acao)) {
                result = service.salvar(conn, dados, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    String idReg = result.has("id") ? result.get("id").getAsString() : null;
                    AuditoriaService.registrar(req, "dashboard-widget", "salvar", idReg, "Widget salvo");
                }

            } else if ("excluir".equals(acao)) {
                int idWidget = dados.get("id").getAsInt();
                result = service.excluir(conn, idWidget, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-widget", "excluir",
                            String.valueOf(idWidget), "Widget excluido");
                }

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

    private JsonObject obterMetaDataset(Connection conn, int idDataset) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray dimensoes = new JsonArray();
        JsonArray medidas = new JsonArray();
        JsonArray tempos = new JsonArray();

        try (var ps = conn.prepareStatement(
                "SELECT nome_alias, rotulo, agregacao, tipo_campo, expressao " +
                "FROM dash.dataset_campo WHERE id_dataset = ? ORDER BY ordem")) {
            ps.setInt(1, idDataset);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipoCampo = rs.getString("tipo_campo");
                    String agregacao = rs.getString("agregacao");

                    JsonObject c = new JsonObject();
                    c.addProperty("alias", rs.getString("nome_alias"));
                    c.addProperty("rotulo", rs.getString("rotulo"));
                    c.addProperty("agregacao", agregacao);
                    c.addProperty("tipo", tipoCampo);
                    c.addProperty("calculado", rs.getString("expressao") != null);

                    if ("medida".equals(tipoCampo)) medidas.add(c);
                    else if ("tempo".equals(tipoCampo)) tempos.add(c);
                    else dimensoes.add(c);
                }
            }
        }

        result.addProperty("ok", true);
        result.add("dimensoes", dimensoes);
        result.add("medidas", medidas);
        result.add("tempos", tempos);
        return result;
    }

    private JsonObject listarDatasetsPublicados(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (var ps = conn.prepareStatement(
                "SELECT d.id, d.nome, d.descricao, ct.rotulo AS tabelaBase " +
                "FROM dash.dataset d " +
                "JOIN dash.catalogo_tabela ct ON d.id_tabela_base = ct.id " +
                "WHERE d.status = 'publicado' ORDER BY d.nome")) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject ds = new JsonObject();
                    ds.addProperty("id", rs.getInt("id"));
                    ds.addProperty("nome", rs.getString("nome"));
                    ds.addProperty("descricao", rs.getString("descricao"));
                    ds.addProperty("tabelaBase", rs.getString("tabelaBase"));
                    arr.add(ds);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("datasets", arr);
        return result;
    }
}
