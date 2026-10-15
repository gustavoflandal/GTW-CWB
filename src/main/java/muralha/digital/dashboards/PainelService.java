package muralha.digital.dashboards;

import com.google.gson.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PainelService {

    private final CompartilhamentoService compartilhamentoService = new CompartilhamentoService();
    private final HistoricoVersaoService historicoService = new HistoricoVersaoService();

    public JsonObject versionar(Connection conn, int idPainel, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        JsonObject snapshot = montarSnapshot(conn, idPainel);
        if (snapshot == null) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Painel nao encontrado");
            return result;
        }

        int versaoAtual = obterVersaoAtual(conn, idPainel);
        int novaVersao = versaoAtual + 1;

        historicoService.salvarVersao(conn, "dashboard", idPainel,
                novaVersao, new Gson().toJson(snapshot), idUsuario);

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.dashboard SET versao=?, atualizado_em=SYSDATETIME() WHERE id=?")) {
            ps.setInt(1, novaVersao);
            ps.setInt(2, idPainel);
            ps.executeUpdate();
        }

        result.addProperty("ok", true);
        result.addProperty("versao", novaVersao);
        return result;
    }

    public JsonObject restaurarVersao(Connection conn, int idPainel, long idVersao, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        JsonObject snapResult = historicoService.obterSnapshot(conn, idVersao);
        if (!snapResult.get("ok").getAsBoolean()) return snapResult;

        JsonObject snapshot = snapResult.getAsJsonObject("snapshot");

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.dashboard SET nome=?, descricao=?, layout_json=?, filtros_json=?, " +
                "refresh_seg=?, atualizado_em=SYSDATETIME() WHERE id=?")) {
            ps.setString(1, snapshot.has("nome") ? snapshot.get("nome").getAsString() : "Painel");
            ps.setString(2, snapshot.has("descricao") ? snapshot.get("descricao").getAsString() : null);
            ps.setString(3, snapshot.has("layout") ? new Gson().toJson(snapshot.get("layout")) : null);
            ps.setString(4, snapshot.has("filtros") ? new Gson().toJson(snapshot.get("filtros")) : null);
            ps.setInt(5, snapshot.has("refreshSeg") ? snapshot.get("refreshSeg").getAsInt() : 60);
            ps.setInt(6, idPainel);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dash.widget WHERE id_dashboard=?")) {
            ps.setInt(1, idPainel);
            ps.executeUpdate();
        }

        if (snapshot.has("widgets")) {
            for (var el : snapshot.getAsJsonArray("widgets")) {
                JsonObject w = el.getAsJsonObject();
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.widget (id_dashboard, id_dataset, titulo, tipo, consulta_json, " +
                        "visual_json, grid_x, grid_y, grid_w, grid_h, ordem) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {
                    ps.setInt(1, idPainel);
                    ps.setInt(2, w.get("idDataset").getAsInt());
                    ps.setString(3, w.get("titulo").getAsString());
                    ps.setString(4, w.get("tipo").getAsString());
                    ps.setString(5, new Gson().toJson(w.get("consulta")));
                    ps.setString(6, w.has("visual") ? new Gson().toJson(w.get("visual")) : null);
                    ps.setInt(7, w.has("gridX") ? w.get("gridX").getAsInt() : 0);
                    ps.setInt(8, w.has("gridY") ? w.get("gridY").getAsInt() : 0);
                    ps.setInt(9, w.has("gridW") ? w.get("gridW").getAsInt() : 4);
                    ps.setInt(10, w.has("gridH") ? w.get("gridH").getAsInt() : 3);
                    ps.setInt(11, w.has("ordem") ? w.get("ordem").getAsInt() : 0);
                    ps.executeUpdate();
                }
            }
        }

        result.addProperty("ok", true);
        return result;
    }

    public JsonObject duplicar(Connection conn, int idPainel, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        JsonObject snapshot = montarSnapshot(conn, idPainel);
        if (snapshot == null) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Painel nao encontrado");
            return result;
        }

        int novoId;
        String novoNome = snapshot.get("nome").getAsString() + " (copia)";
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.dashboard (nome, descricao, id_usuario, tenant_id, layout_json, " +
                "filtros_json, refresh_seg) VALUES (?,?,?,1,?,?,?); SELECT SCOPE_IDENTITY();")) {
            ps.setString(1, novoNome);
            ps.setString(2, snapshot.has("descricao") ? snapshot.get("descricao").getAsString() : null);
            ps.setInt(3, idUsuario);
            ps.setString(4, snapshot.has("layout") ? new Gson().toJson(snapshot.get("layout")) : null);
            ps.setString(5, snapshot.has("filtros") ? new Gson().toJson(snapshot.get("filtros")) : null);
            ps.setInt(6, snapshot.has("refreshSeg") ? snapshot.get("refreshSeg").getAsInt() : 60);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Falha ao duplicar");
                    return result;
                }
                novoId = rs.getInt(1);
            }
        }

        if (snapshot.has("widgets")) {
            for (var el : snapshot.getAsJsonArray("widgets")) {
                JsonObject w = el.getAsJsonObject();
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dash.widget (id_dashboard, id_dataset, titulo, tipo, consulta_json, " +
                        "visual_json, grid_x, grid_y, grid_w, grid_h, ordem) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {
                    ps.setInt(1, novoId);
                    ps.setInt(2, w.get("idDataset").getAsInt());
                    ps.setString(3, w.get("titulo").getAsString());
                    ps.setString(4, w.get("tipo").getAsString());
                    ps.setString(5, new Gson().toJson(w.get("consulta")));
                    ps.setString(6, w.has("visual") ? new Gson().toJson(w.get("visual")) : null);
                    ps.setInt(7, w.has("gridX") ? w.get("gridX").getAsInt() : 0);
                    ps.setInt(8, w.has("gridY") ? w.get("gridY").getAsInt() : 0);
                    ps.setInt(9, w.has("gridW") ? w.get("gridW").getAsInt() : 4);
                    ps.setInt(10, w.has("gridH") ? w.get("gridH").getAsInt() : 3);
                    ps.setInt(11, w.has("ordem") ? w.get("ordem").getAsInt() : 0);
                    ps.executeUpdate();
                }
            }
        }

        result.addProperty("ok", true);
        result.addProperty("id", novoId);
        result.addProperty("nome", novoNome);
        return result;
    }

    public JsonObject salvarFiltrosGlobais(Connection conn, int idPainel, JsonElement filtrosJson) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE dash.dashboard SET filtros_json=?, atualizado_em=SYSDATETIME() WHERE id=?")) {
            ps.setString(1, filtrosJson != null ? new Gson().toJson(filtrosJson) : null);
            ps.setInt(2, idPainel);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    private JsonObject montarSnapshot(Connection conn, int idPainel) throws Exception {
        JsonObject snap = null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome, descricao, layout_json, filtros_json, refresh_seg " +
                "FROM dash.dashboard WHERE id=? AND ativo=1")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                snap = new JsonObject();
                snap.addProperty("nome", rs.getString("nome"));
                snap.addProperty("descricao", rs.getString("descricao"));
                String lj = rs.getString("layout_json");
                if (lj != null) snap.add("layout", JsonParser.parseString(lj));
                String fj = rs.getString("filtros_json");
                if (fj != null) snap.add("filtros", JsonParser.parseString(fj));
                snap.addProperty("refreshSeg", rs.getInt("refresh_seg"));
            }
        }

        JsonArray widgets = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, id_dataset, titulo, tipo, consulta_json, visual_json, " +
                "grid_x, grid_y, grid_w, grid_h, ordem FROM dash.widget WHERE id_dashboard=?")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject w = new JsonObject();
                    w.addProperty("idDataset", rs.getInt("id_dataset"));
                    w.addProperty("titulo", rs.getString("titulo"));
                    w.addProperty("tipo", rs.getString("tipo"));
                    w.add("consulta", JsonParser.parseString(rs.getString("consulta_json")));
                    String vj = rs.getString("visual_json");
                    if (vj != null) w.add("visual", JsonParser.parseString(vj));
                    w.addProperty("gridX", rs.getInt("grid_x"));
                    w.addProperty("gridY", rs.getInt("grid_y"));
                    w.addProperty("gridW", rs.getInt("grid_w"));
                    w.addProperty("gridH", rs.getInt("grid_h"));
                    w.addProperty("ordem", rs.getInt("ordem"));
                    widgets.add(w);
                }
            }
        }
        snap.add("widgets", widgets);
        return snap;
    }

    private int obterVersaoAtual(Connection conn, int idPainel) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT versao FROM dash.dashboard WHERE id=?")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("versao") : 0;
            }
        }
    }
}
