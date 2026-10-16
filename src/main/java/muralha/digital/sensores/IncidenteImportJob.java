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
            for (Incidente inc : extrair(root, System.currentTimeMillis())) {
                importados += inserirIncidente(conn, "WAZE", inc.idExterno, inc.tipo, inc.descricao,
                    inc.latitude, inc.longitude, inc.severidade, inc.epochMs);
            }

            System.out.println("[IncidenteImportJob] Importados " + importados + " incidentes.");
        } catch (Exception e) {
            System.err.println("[IncidenteImportJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    /** Incidente lido do feed da Waze, antes de gravar em muralha.incidente_externo. */
    static final class Incidente {
        final String idExterno;
        final String tipo;
        final String descricao;
        final double latitude;
        final double longitude;
        final int severidade;
        final long epochMs;

        Incidente(String idExterno, String tipo, String descricao, double latitude,
                double longitude, int severidade, long epochMs) {
            this.idExterno = idExterno;
            this.tipo = tipo;
            this.descricao = descricao;
            this.latitude = latitude;
            this.longitude = longitude;
            this.severidade = severidade;
            this.epochMs = epochMs;
        }
    }

    static final int MAX_DESCRICAO = 500;

    /**
     * Converte os arrays "alerts" e "jams" do feed da Waze em incidentes.
     * A data é o pubMillis do item (agora, se ausente), para que a chave única
     * (fonte, id_externo, dt_ocorrencia) evite regravar o mesmo item a cada execução.
     */
    static java.util.List<Incidente> extrair(JsonObject root, long agoraMs) {
        java.util.List<Incidente> lista = new java.util.ArrayList<>();
        if (root == null) return lista;

        if (root.has("alerts") && root.get("alerts").isJsonArray()) {
            for (JsonElement el : root.getAsJsonArray("alerts")) {
                if (!el.isJsonObject()) continue;
                JsonObject alert = el.getAsJsonObject();
                String tipo = texto(alert, "type");
                double lat = 0, lng = 0;
                if (alert.has("location") && alert.get("location").isJsonObject()) {
                    JsonObject loc = alert.getAsJsonObject("location");
                    lat = numero(loc, "y");
                    lng = numero(loc, "x");
                }
                lista.add(new Incidente(texto(alert, "uuid"), tipo != null ? tipo : "ALERT",
                    texto(alert, "subtype"), lat, lng, (int) numero(alert, "reportRating"),
                    data(alert, agoraMs)));
            }
        }

        if (root.has("jams") && root.get("jams").isJsonArray()) {
            for (JsonElement el : root.getAsJsonArray("jams")) {
                if (!el.isJsonObject()) continue;
                JsonObject jam = el.getAsJsonObject();
                // Ponto central da linha do congestionamento
                double lat = 0, lng = 0;
                if (jam.has("line") && jam.get("line").isJsonArray() && jam.getAsJsonArray("line").size() > 0) {
                    JsonArray linha = jam.getAsJsonArray("line");
                    JsonElement meio = linha.get(linha.size() / 2);
                    if (meio.isJsonObject()) {
                        lat = numero(meio.getAsJsonObject(), "y");
                        lng = numero(meio.getAsJsonObject(), "x");
                    }
                }
                String id = texto(jam, "uuid");
                if (id == null) id = texto(jam, "id");
                lista.add(new Incidente(id, "JAM", descricaoJam(jam), lat, lng,
                    (int) numero(jam, "level"), data(jam, agoraMs)));
            }
        }
        return lista;
    }

    /** Ex.: "Av. Paralela, Salvador — nível 4, 12 km/h, atraso 240 s, 1500 m". */
    static String descricaoJam(JsonObject jam) {
        StringBuilder sb = new StringBuilder();
        String rua = texto(jam, "street");
        String cidade = texto(jam, "city");
        if (rua != null) sb.append(rua);
        if (cidade != null) sb.append(sb.length() > 0 ? ", " : "").append(cidade);
        java.util.List<String> partes = new java.util.ArrayList<>();
        if (jam.has("level")) partes.add("nível " + (int) numero(jam, "level"));
        if (jam.has("speedKMH")) partes.add(Math.round(numero(jam, "speedKMH")) + " km/h");
        if (jam.has("delay")) partes.add("atraso " + (int) numero(jam, "delay") + " s");
        if (jam.has("length")) partes.add((int) numero(jam, "length") + " m");
        if (!partes.isEmpty()) sb.append(sb.length() > 0 ? " — " : "").append(String.join(", ", partes));
        String d = sb.toString();
        return d.isEmpty() ? null : (d.length() > MAX_DESCRICAO ? d.substring(0, MAX_DESCRICAO) : d);
    }

    private static String texto(JsonObject o, String campo) {
        JsonElement e = o.get(campo);
        return (e != null && e.isJsonPrimitive()) ? e.getAsString() : null;
    }

    private static double numero(JsonObject o, String campo) {
        JsonElement e = o.get(campo);
        try {
            return (e != null && e.isJsonPrimitive()) ? e.getAsDouble() : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static long data(JsonObject o, long agoraMs) {
        long pub = (long) numero(o, "pubMillis");
        return pub > 0 ? pub : agoraMs;
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
