package muralha.digital.dashboards;

import com.google.gson.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompartilhamentoService {

    public JsonObject compartilhar(Connection conn, String tipoRecurso, int idRecurso,
                                    int idUsuarioDestino, String nivel) throws Exception {
        JsonObject result = new JsonObject();

        try (PreparedStatement ps = conn.prepareStatement(
                "IF EXISTS (SELECT 1 FROM dash.compartilhamento " +
                "WHERE tipo_recurso=? AND id_recurso=? AND id_usuario=?) " +
                "UPDATE dash.compartilhamento SET nivel=? WHERE tipo_recurso=? AND id_recurso=? AND id_usuario=? " +
                "ELSE " +
                "INSERT INTO dash.compartilhamento (tipo_recurso, id_recurso, id_usuario, nivel) VALUES (?,?,?,?)")) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            ps.setInt(3, idUsuarioDestino);
            ps.setString(4, nivel);
            ps.setString(5, tipoRecurso);
            ps.setInt(6, idRecurso);
            ps.setInt(7, idUsuarioDestino);
            ps.setString(8, tipoRecurso);
            ps.setInt(9, idRecurso);
            ps.setInt(10, idUsuarioDestino);
            ps.setString(11, nivel);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    public JsonObject revogar(Connection conn, String tipoRecurso, int idRecurso,
                               int idUsuarioDestino) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dash.compartilhamento WHERE tipo_recurso=? AND id_recurso=? AND id_usuario=?")) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            ps.setInt(3, idUsuarioDestino);
            ps.executeUpdate();
        }
        result.addProperty("ok", true);
        return result;
    }

    public JsonObject listar(Connection conn, String tipoRecurso, int idRecurso) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT c.id, c.id_usuario, c.id_perfil, c.nivel, c.criado_em, " +
                "u.nome AS nome_usuario " +
                "FROM dash.compartilhamento c " +
                "LEFT JOIN dbo.sis_usuario u ON c.id_usuario = u.id " +
                "WHERE c.tipo_recurso=? AND c.id_recurso=? ORDER BY c.criado_em")) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", rs.getInt("id"));
                    item.addProperty("idUsuario", rs.getInt("id_usuario"));
                    item.addProperty("nomeUsuario", rs.getString("nome_usuario"));
                    item.addProperty("nivel", rs.getString("nivel"));
                    arr.add(item);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("compartilhamentos", arr);
        return result;
    }

    public boolean verificarPermissao(Connection conn, String tipoRecurso, int idRecurso,
                                       int idUsuario, String nivelMinimo) throws Exception {
        String sql = "SELECT 1 FROM dash.compartilhamento " +
                "WHERE tipo_recurso=? AND id_recurso=? AND id_usuario=?";
        if ("editar".equals(nivelMinimo)) {
            sql += " AND nivel='editar'";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            ps.setInt(3, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
