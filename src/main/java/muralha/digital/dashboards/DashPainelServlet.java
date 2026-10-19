package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.google.gson.*;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * CRUD de dashboards (paineis) e widgets.
 */
@WebServlet("/api/dash/paineis")
public class DashPainelServlet extends HttpServlet {

    private final PainelService painelService = new PainelService();
    private final CompartilhamentoService compartilhamentoService = new CompartilhamentoService();
    private final HistoricoVersaoService historicoService = new HistoricoVersaoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        if (!ControleAcesso.exigir(req, resp)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int idUsuario = ControleAcesso.usuario(req).getId();
            String acao = req.getParameter("acao");

            if ("listar".equals(acao)) {
                result = listarPaineis(conn, idUsuario);

            } else if ("obter".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = obterPainel(conn, id, idUsuario);

            } else if ("listarDatasets".equals(acao)) {
                result = listarDatasets(conn);

            } else if ("obterMetaDataset".equals(acao)) {
                int idDataset = Integer.parseInt(req.getParameter("id"));
                result = obterMetaDataset(conn, idDataset);

            } else if ("listarCompartilhamentos".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = compartilhamentoService.listar(conn, "dashboard", id);

            } else if ("listarVersoes".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = historicoService.listarVersoes(conn, "dashboard", id);

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
        if (!ControleAcesso.exigir(req, resp)) return;

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            int idUsuario = ControleAcesso.usuario(req).getId();

            StringBuilder body = new StringBuilder();
            String line;
            var reader = req.getReader();
            while ((line = reader.readLine()) != null) body.append(line);

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();
            String acao = dados.has("acao") ? dados.get("acao").getAsString() : "";

            if ("salvarPainel".equals(acao)) {
                result = salvarPainel(conn, dados, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    String idReg = result.has("id") ? result.get("id").getAsString() : null;
                    AuditoriaService.registrar(req, "dashboard-painel", "salvar", idReg, "Painel salvo");
                }

            } else if ("salvarWidget".equals(acao)) {
                result = salvarWidget(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "salvar-widget", null, "Widget salvo no painel");
                }

            } else if ("excluirWidget".equals(acao)) {
                int idWidget = dados.get("id").getAsInt();
                result = excluirWidget(conn, idWidget);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "excluir-widget",
                            String.valueOf(idWidget), "Widget excluido do painel");
                }

            } else if ("atualizarLayout".equals(acao)) {
                result = atualizarLayout(conn, dados);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "atualizar-layout", null, "Layout do painel atualizado");
                }

            } else if ("compartilhar".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                int idDestino = dados.get("idUsuarioDestino").getAsInt();
                String nivel = dados.get("nivel").getAsString();
                result = compartilhamentoService.compartilhar(conn, "dashboard", idPainel, idDestino, nivel);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "compartilhar",
                            String.valueOf(idPainel), "Painel compartilhado com usuario " + idDestino + " nivel " + nivel);
                }

            } else if ("revogarCompartilhamento".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                int idDestino = dados.get("idUsuarioDestino").getAsInt();
                result = compartilhamentoService.revogar(conn, "dashboard", idPainel, idDestino);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "revogar-compartilhamento",
                            String.valueOf(idPainel), "Compartilhamento revogado para usuario " + idDestino);
                }

            } else if ("versionar".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                result = painelService.versionar(conn, idPainel, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "versionar",
                            String.valueOf(idPainel), "Versao do painel criada");
                }

            } else if ("restaurarVersao".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                long idVersao = dados.get("idVersao").getAsLong();
                result = painelService.restaurarVersao(conn, idPainel, idVersao, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "restaurar-versao",
                            String.valueOf(idPainel), "Versao " + idVersao + " restaurada");
                }

            } else if ("duplicar".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                result = painelService.duplicar(conn, idPainel, idUsuario);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    String idReg = result.has("id") ? result.get("id").getAsString() : null;
                    AuditoriaService.registrar(req, "dashboard-painel", "duplicar",
                            idReg, "Painel duplicado a partir de " + idPainel);
                }

            } else if ("salvarFiltrosGlobais".equals(acao)) {
                int idPainel = dados.get("idPainel").getAsInt();
                JsonElement filtros = dados.has("filtros") ? dados.get("filtros") : null;
                result = painelService.salvarFiltrosGlobais(conn, idPainel, filtros);
                if (result.has("ok") && result.get("ok").getAsBoolean()) {
                    AuditoriaService.registrar(req, "dashboard-painel", "salvar-filtros-globais",
                            String.valueOf(idPainel), "Filtros globais salvos");
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

    private JsonObject listarPaineis(Connection conn, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome, descricao, id_usuario, refresh_seg, versao, criado_em, atualizado_em " +
                "FROM dash.dashboard WHERE ativo = 1 AND (id_usuario = ? OR id IN (" +
                "  SELECT id_recurso FROM dash.compartilhamento " +
                "  WHERE tipo_recurso = 'dashboard' AND id_usuario = ?)) " +
                "ORDER BY atualizado_em DESC, criado_em DESC")) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject p = new JsonObject();
                    p.addProperty("id", rs.getInt("id"));
                    p.addProperty("nome", rs.getString("nome"));
                    p.addProperty("descricao", rs.getString("descricao"));
                    p.addProperty("idUsuario", rs.getInt("id_usuario"));
                    p.addProperty("refreshSeg", rs.getInt("refresh_seg"));
                    p.addProperty("versao", rs.getInt("versao"));
                    p.addProperty("proprio", rs.getInt("id_usuario") == idUsuario);
                    arr.add(p);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("paineis", arr);
        return result;
    }

    private JsonObject obterPainel(Connection conn, int idPainel, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();
        JsonObject painel = new JsonObject();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, nome, descricao, id_usuario, layout_json, filtros_json, " +
                "       refresh_seg, versao " +
                "FROM dash.dashboard WHERE id = ? AND ativo = 1")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Painel nao encontrado");
                    return result;
                }
                painel.addProperty("id", rs.getInt("id"));
                painel.addProperty("nome", rs.getString("nome"));
                painel.addProperty("descricao", rs.getString("descricao"));
                painel.addProperty("idUsuario", rs.getInt("id_usuario"));
                painel.addProperty("refreshSeg", rs.getInt("refresh_seg"));
                painel.addProperty("versao", rs.getInt("versao"));

                String layoutJson = rs.getString("layout_json");
                if (layoutJson != null) {
                    painel.add("layout", JsonParser.parseString(layoutJson));
                }
                String filtrosJson = rs.getString("filtros_json");
                if (filtrosJson != null) {
                    painel.add("filtros", JsonParser.parseString(filtrosJson));
                }
            }
        }

        // Widgets do painel
        JsonArray widgets = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT w.id, w.id_dataset, w.titulo, w.tipo, w.consulta_json, " +
                "       w.visual_json, w.grid_x, w.grid_y, w.grid_w, w.grid_h, w.ordem " +
                "FROM dash.widget w WHERE w.id_dashboard = ? ORDER BY w.ordem, w.id")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject w = new JsonObject();
                    w.addProperty("id", rs.getInt("id"));
                    w.addProperty("idDataset", rs.getInt("id_dataset"));
                    w.addProperty("titulo", rs.getString("titulo"));
                    w.addProperty("tipo", rs.getString("tipo"));
                    w.add("consulta", JsonParser.parseString(rs.getString("consulta_json")));
                    String vj = rs.getString("visual_json");
                    if (vj != null) {
                        w.add("visual", JsonParser.parseString(vj));
                    }
                    w.addProperty("gridX", rs.getInt("grid_x"));
                    w.addProperty("gridY", rs.getInt("grid_y"));
                    w.addProperty("gridW", rs.getInt("grid_w"));
                    w.addProperty("gridH", rs.getInt("grid_h"));
                    w.addProperty("ordem", rs.getInt("ordem"));
                    widgets.add(w);
                }
            }
        }
        painel.add("widgets", widgets);

        result.addProperty("ok", true);
        result.add("painel", painel);
        return result;
    }

    private JsonObject listarDatasets(Connection conn) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT d.id, d.nome, d.descricao, d.versao, ct.rotulo AS tabelaBase " +
                "FROM dash.dataset d " +
                "JOIN dash.catalogo_tabela ct ON d.id_tabela_base = ct.id " +
                "WHERE d.status = 'publicado' ORDER BY d.nome")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject ds = new JsonObject();
                    ds.addProperty("id", rs.getInt("id"));
                    ds.addProperty("nome", rs.getString("nome"));
                    ds.addProperty("descricao", rs.getString("descricao"));
                    ds.addProperty("versao", rs.getInt("versao"));
                    ds.addProperty("tabelaBase", rs.getString("tabelaBase"));
                    arr.add(ds);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("datasets", arr);
        return result;
    }

    private JsonObject obterMetaDataset(Connection conn, int idDataset) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray campos = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dc.nome_alias, dc.rotulo, dc.agregacao, dc.tipo_campo " +
                "FROM dash.dataset_campo dc WHERE dc.id_dataset = ? ORDER BY dc.ordem")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject c = new JsonObject();
                    c.addProperty("alias", rs.getString("nome_alias"));
                    c.addProperty("rotulo", rs.getString("rotulo"));
                    c.addProperty("agregacao", rs.getString("agregacao"));
                    c.addProperty("tipo", rs.getString("tipo_campo"));
                    campos.add(c);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("campos", campos);
        return result;
    }

    private JsonObject salvarPainel(Connection conn, JsonObject dados, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();
        boolean novo = !dados.has("id") || dados.get("id").getAsInt() == 0;
        String nome = dados.get("nome").getAsString();
        String descricao = dados.has("descricao") ? dados.get("descricao").getAsString() : null;
        int refreshSeg = dados.has("refreshSeg") ? dados.get("refreshSeg").getAsInt() : 60;

        if (novo) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.dashboard (nome, descricao, id_usuario, tenant_id, refresh_seg) " +
                    "VALUES (?, ?, ?, 1, ?); SELECT SCOPE_IDENTITY();")) {
                ps.setString(1, nome);
                ps.setString(2, descricao);
                ps.setInt(3, idUsuario);
                ps.setInt(4, refreshSeg);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.addProperty("id", rs.getInt(1));
                    }
                }
            }
        } else {
            int id = dados.get("id").getAsInt();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.dashboard SET nome=?, descricao=?, refresh_seg=?, " +
                    "atualizado_em=SYSDATETIME() WHERE id=? AND id_usuario=?")) {
                ps.setString(1, nome);
                ps.setString(2, descricao);
                ps.setInt(3, refreshSeg);
                ps.setInt(4, id);
                ps.setInt(5, idUsuario);
                ps.executeUpdate();
            }
            result.addProperty("id", id);
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject salvarWidget(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        boolean novo = !dados.has("id") || dados.get("id").getAsInt() == 0;

        int idDashboard = dados.get("idDashboard").getAsInt();
        int idDataset = dados.get("idDataset").getAsInt();
        String titulo = dados.get("titulo").getAsString();
        String tipo = dados.get("tipo").getAsString();
        String consultaJson = new Gson().toJson(dados.get("consulta"));
        String visualJson = dados.has("visual") ? new Gson().toJson(dados.get("visual")) : null;
        int gridX = dados.has("gridX") ? dados.get("gridX").getAsInt() : 0;
        int gridY = dados.has("gridY") ? dados.get("gridY").getAsInt() : 0;
        int gridW = dados.has("gridW") ? dados.get("gridW").getAsInt() : 4;
        int gridH = dados.has("gridH") ? dados.get("gridH").getAsInt() : 3;

        if (novo) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.widget " +
                    "(id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, " +
                    " grid_x, grid_y, grid_w, grid_h) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();")) {
                ps.setInt(1, idDashboard);
                ps.setInt(2, idDataset);
                ps.setString(3, titulo);
                ps.setString(4, tipo);
                ps.setString(5, consultaJson);
                ps.setString(6, visualJson);
                ps.setInt(7, gridX);
                ps.setInt(8, gridY);
                ps.setInt(9, gridW);
                ps.setInt(10, gridH);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) result.addProperty("id", rs.getInt(1));
                }
            }
        } else {
            int id = dados.get("id").getAsInt();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.widget SET titulo=?, tipo=?, consulta_json=?, visual_json=?, " +
                    "id_dataset=? WHERE id=?")) {
                ps.setString(1, titulo);
                ps.setString(2, tipo);
                ps.setString(3, consultaJson);
                ps.setString(4, visualJson);
                ps.setInt(5, idDataset);
                ps.setInt(6, id);
                ps.executeUpdate();
            }
            result.addProperty("id", id);
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject excluirWidget(Connection conn, int idWidget) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dash.widget WHERE id = ?")) {
            ps.setInt(1, idWidget);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject atualizarLayout(Connection conn, JsonObject dados) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray widgets = dados.getAsJsonArray("widgets");
        for (var el : widgets) {
            JsonObject w = el.getAsJsonObject();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.widget SET grid_x=?, grid_y=?, grid_w=?, grid_h=? WHERE id=?")) {
                ps.setInt(1, w.get("gridX").getAsInt());
                ps.setInt(2, w.get("gridY").getAsInt());
                ps.setInt(3, w.get("gridW").getAsInt());
                ps.setInt(4, w.get("gridH").getAsInt());
                ps.setInt(5, w.get("id").getAsInt());
                ps.executeUpdate();
            }
        }
        result.addProperty("ok", true);
        return result;
    }
}
