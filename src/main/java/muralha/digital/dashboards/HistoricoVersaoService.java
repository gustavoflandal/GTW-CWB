package muralha.digital.dashboards;

import com.google.gson.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoricoVersaoService {

    public JsonObject salvarVersao(Connection conn, String tipoRecurso, int idRecurso,
                                    int versao, String snapshotJson, int idAutor) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dash.historico_versao (tipo_recurso, id_recurso, versao, snapshot_json, id_autor) " +
                "VALUES (?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();")) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            ps.setInt(3, versao);
            ps.setString(4, snapshotJson);
            ps.setInt(5, idAutor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) result.addProperty("idVersao", rs.getLong(1));
            }
        }
        result.addProperty("ok", true);
        return result;
    }

    public JsonObject listarVersoes(Connection conn, String tipoRecurso, int idRecurso) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT hv.id, hv.versao, hv.id_autor, hv.criado_em, RTRIM(u.nome) AS autor_nome " +
                "FROM dash.historico_versao hv " +
                "LEFT JOIN dbo.sis_usuario u ON hv.id_autor = u.id_usuario " +
                "WHERE hv.tipo_recurso=? AND hv.id_recurso=? ORDER BY hv.versao DESC")) {
            ps.setString(1, tipoRecurso);
            ps.setInt(2, idRecurso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("id", rs.getLong("id"));
                    v.addProperty("versao", rs.getInt("versao"));
                    v.addProperty("idAutor", rs.getInt("id_autor"));
                    v.addProperty("autorNome", rs.getString("autor_nome"));
                    v.addProperty("criadoEm", rs.getString("criado_em"));
                    arr.add(v);
                }
            }
        }
        result.addProperty("ok", true);
        result.add("versoes", arr);
        return result;
    }

    public JsonObject obterSnapshot(Connection conn, long idVersao) throws Exception {
        JsonObject result = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT snapshot_json FROM dash.historico_versao WHERE id = ?")) {
            ps.setLong(1, idVersao);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Versao nao encontrada");
                    return result;
                }
                result.addProperty("ok", true);
                result.add("snapshot", JsonParser.parseString(rs.getString("snapshot_json")));
            }
        }
        return result;
    }
}
