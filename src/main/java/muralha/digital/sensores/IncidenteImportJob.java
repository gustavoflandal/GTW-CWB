package muralha.digital.sensores;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IncidenteImportJob implements Job {

    @Override
    public void execute(JobExecutionContext ctx) {
        String apiKey = System.getenv("WAZE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[IncidenteImportJob] WAZE_API_KEY nao configurada; importacao ignorada.");
            return;
        }

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String apiUrl = obterConfig(conn, "waze_api_url");
            String bbox = obterConfig(conn, "waze_area_bbox");

            if (apiUrl.isEmpty()) {
                System.out.println("[IncidenteImportJob] waze_api_url nao configurada.");
                return;
            }

            String url = apiUrl + "?types=alerts,jams&format=1&polygon="
                + URLEncoder.encode(bbox, "UTF-8");
            String json = httpGet(url, apiKey);

            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            int importados = 0;

            if (root.has("alerts")) {
                JsonArray alerts = root.getAsJsonArray("alerts");
                for (JsonElement el : alerts) {
                    JsonObject alert = el.getAsJsonObject();
                    String uuid = alert.has("uuid") ? alert.get("uuid").getAsString() : null;
                    String tipo = alert.has("type") ? alert.get("type").getAsString() : "ALERT";
                    String desc = alert.has("subtype") ? alert.get("subtype").getAsString() : null;
                    double lat = 0, lng = 0;
                    if (alert.has("location")) {
                        JsonObject loc = alert.getAsJsonObject("location");
                        lat = loc.get("y").getAsDouble();
                        lng = loc.get("x").getAsDouble();
                    }
                    int sev = alert.has("reportRating") ? alert.get("reportRating").getAsInt() : 0;
                    importados += inserirIncidente(conn, "WAZE", uuid, tipo, desc,
                        lat, lng, sev, System.currentTimeMillis());
                }
            }

            System.out.println("[IncidenteImportJob] Importados " + importados + " incidentes.");
        } catch (Exception e) {
            System.err.println("[IncidenteImportJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private int inserirIncidente(Connection conn, String fonte, String idExt,
            String tipo, String desc, double lat, double lng, int severidade,
            long epochMs) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.incidente_externo " +
                "(fonte, id_externo, tipo, descricao, latitude, longitude, " +
                " severidade, dt_ocorrencia) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, DATEADD(MILLISECOND, ? % 1000, " +
                "  DATEADD(SECOND, ? / 1000, '1970-01-01')))")) {
            ps.setString(1, fonte);
            ps.setString(2, idExt);
            ps.setString(3, tipo);
            ps.setString(4, desc);
            ps.setDouble(5, lat);
            ps.setDouble(6, lng);
            ps.setInt(7, severidade);
            ps.setLong(8, epochMs);
            ps.setLong(9, epochMs);
            return ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("uq_incidente"))
                return 0;
            throw e;
        }
    }

    private String httpGet(String url, String apiKey) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setConnectTimeout(10_000);
        con.setReadTimeout(15_000);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private String obterConfig(Connection conn, String chave) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave = ?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("valor");
            }
        }
        return "";
    }
}
