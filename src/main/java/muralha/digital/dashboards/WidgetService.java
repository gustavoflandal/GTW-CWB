package muralha.digital.dashboards;

import com.google.gson.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class WidgetService {

    public JsonObject salvar(Connection conn, JsonObject dados, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();

        int idDashboard = dados.get("idDashboard").getAsInt();
        int idDataset = dados.get("idDataset").getAsInt();
        String titulo = dados.get("titulo").getAsString();
        String tipo = dados.get("tipo").getAsString();
        JsonObject consulta = dados.getAsJsonObject("consulta");
        JsonObject visual = dados.has("visual") ? dados.getAsJsonObject("visual") : null;

        if (!validarDonoPainel(conn, idDashboard, idUsuario)) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Sem permissao neste painel");
            return result;
        }

        Set<String> erros = validarMembros(conn, idDataset, consulta);
        if (!erros.isEmpty()) {
            result.addProperty("ok", false);
            result.addProperty("erro", "Membros invalidos: " + String.join(", ", erros));
            return result;
        }

        String consultaJson = new Gson().toJson(consulta);
        String visualJson = visual != null ? new Gson().toJson(visual) : null;

        boolean novo = !dados.has("id") || dados.get("id").getAsInt() == 0;
        int gridX = dados.has("gridX") ? dados.get("gridX").getAsInt() : 0;
        int gridY = dados.has("gridY") ? dados.get("gridY").getAsInt() : 0;
        int gridW = dados.has("gridW") ? dados.get("gridW").getAsInt() : 4;
        int gridH = dados.has("gridH") ? dados.get("gridH").getAsInt() : 3;
        int ordem = dados.has("ordem") ? dados.get("ordem").getAsInt() : 0;

        if (novo) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.widget " +
                    "(id_dashboard, id_dataset, titulo, tipo, consulta_json, visual_json, " +
                    " grid_x, grid_y, grid_w, grid_h, ordem) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();")) {
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
                ps.setInt(11, ordem);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) result.addProperty("id", rs.getInt(1));
                }
            }
        } else {
            int id = dados.get("id").getAsInt();
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE dash.widget SET titulo=?, tipo=?, consulta_json=?, visual_json=?, " +
                    "id_dataset=?, grid_x=?, grid_y=?, grid_w=?, grid_h=?, ordem=? WHERE id=?")) {
                ps.setString(1, titulo);
                ps.setString(2, tipo);
                ps.setString(3, consultaJson);
                ps.setString(4, visualJson);
                ps.setInt(5, idDataset);
                ps.setInt(6, gridX);
                ps.setInt(7, gridY);
                ps.setInt(8, gridW);
                ps.setInt(9, gridH);
                ps.setInt(10, ordem);
                ps.setInt(11, id);
                ps.executeUpdate();
            }
            result.addProperty("id", id);
        }
        result.addProperty("ok", true);
        return result;
    }

    public JsonObject obter(Connection conn, int idWidget) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT w.id, w.id_dashboard, w.id_dataset, w.titulo, w.tipo, " +
                "w.consulta_json, w.visual_json, w.grid_x, w.grid_y, w.grid_w, w.grid_h, w.ordem " +
                "FROM dash.widget w WHERE w.id = ?")) {
            ps.setInt(1, idWidget);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Widget nao encontrado");
                    return result;
                }
                JsonObject w = new JsonObject();
                w.addProperty("id", rs.getInt("id"));
                w.addProperty("idDashboard", rs.getInt("id_dashboard"));
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

                result.addProperty("ok", true);
                result.add("widget", w);
            }
        }
        return result;
    }

    public JsonObject excluir(Connection conn, int idWidget, int idUsuario) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dash.widget WHERE id = ? AND id_dashboard IN " +
                "(SELECT id FROM dash.dashboard WHERE id_usuario = ?)")) {
            ps.setInt(1, idWidget);
            ps.setInt(2, idUsuario);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                result.addProperty("ok", false);
                result.addProperty("erro", "Widget nao encontrado ou sem permissao");
                return result;
            }
        }
        result.addProperty("ok", true);
        return result;
    }

    public Set<String> validarMembros(Connection conn, int idDataset, JsonObject consulta) throws Exception {
        Set<String> aliasesValidos = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome_alias FROM dash.dataset_campo WHERE id_dataset = ?")) {
            ps.setInt(1, idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aliasesValidos.add(rs.getString("nome_alias"));
                }
            }
        }

        Set<String> erros = new HashSet<>();

        if (consulta.has("measures")) {
            for (var el : consulta.getAsJsonArray("measures")) {
                String m = el.getAsString();
                if (!aliasesValidos.contains(m)) erros.add(m);
            }
        }
        if (consulta.has("dimensions")) {
            for (var el : consulta.getAsJsonArray("dimensions")) {
                String d = el.getAsString();
                if (!aliasesValidos.contains(d)) erros.add(d);
            }
        }
        if (consulta.has("timeDimensions")) {
            for (var el : consulta.getAsJsonArray("timeDimensions")) {
                JsonObject td = el.getAsJsonObject();
                if (td.has("dimension")) {
                    String dim = td.get("dimension").getAsString();
                    if (!aliasesValidos.contains(dim)) erros.add(dim);
                }
            }
        }
        return erros;
    }

    private boolean validarDonoPainel(Connection conn, int idDashboard, int idUsuario) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM dash.dashboard WHERE id = ? AND (id_usuario = ? OR id IN " +
                "(SELECT id_recurso FROM dash.compartilhamento " +
                " WHERE tipo_recurso='dashboard' AND id_usuario=? AND nivel='editar'))")) {
            ps.setInt(1, idDashboard);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
