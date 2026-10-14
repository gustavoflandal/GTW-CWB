package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.google.gson.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Log assíncrono de consultas — não bloqueia a resposta ao usuário.
 */
public class ConsultaLogService {

    private static final int THRESHOLD_LENTA_MS = 5000;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "dash-consulta-log");
        t.setDaemon(true);
        return t;
    });

    public void registrarAsync(int usuarioId, int datasetId, int widgetId, int painelId,
                                String consultaJson, int tempoMs, int linhasRetornadas,
                                boolean cacheHit, String erro) {
        executor.submit(() -> {
            try {
                registrar(usuarioId, datasetId, widgetId, painelId,
                          consultaJson, tempoMs, linhasRetornadas, cacheHit, erro);
            } catch (Exception e) {
                System.err.println("[ConsultaLog] Erro ao gravar log: " + e.getMessage());
            }
        });
    }

    void registrar(int usuarioId, int datasetId, int widgetId, int painelId,
                   String consultaJson, int tempoMs, int linhasRetornadas,
                   boolean cacheHit, String erro) throws Exception {
        boolean lenta = tempoMs > THRESHOLD_LENTA_MS;

        try (Connection conn = Conexao.getConexao()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO dash.consulta_log " +
                    "(usuario_id, dataset_id, widget_id, painel_id, consulta_json, " +
                    " tempo_ms, linhas_retornadas, cache_hit, lenta, erro) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)")) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, datasetId);
                ps.setInt(3, widgetId > 0 ? widgetId : 0);
                ps.setInt(4, painelId > 0 ? painelId : 0);
                ps.setString(5, consultaJson);
                ps.setInt(6, tempoMs);
                ps.setInt(7, linhasRetornadas);
                ps.setBoolean(8, cacheHit);
                ps.setBoolean(9, lenta);
                ps.setString(10, erro);
                ps.executeUpdate();
            }

            atualizarEstatistica(conn, datasetId, tempoMs, cacheHit, lenta);
        }
    }

    private void atualizarEstatistica(Connection conn, int datasetId,
                                       int tempoMs, boolean cacheHit, boolean lenta)
            throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "IF EXISTS (SELECT 1 FROM dash.dataset_estatistica WHERE dataset_id=?) " +
                "UPDATE dash.dataset_estatistica SET " +
                "  total_consultas = total_consultas + 1, " +
                "  tempo_medio_ms = (tempo_medio_ms * (total_consultas - 1) + ?) / total_consultas, " +
                "  cache_hit_count = cache_hit_count + CASE WHEN ?=1 THEN 1 ELSE 0 END, " +
                "  consultas_lentas = consultas_lentas + CASE WHEN ?=1 THEN 1 ELSE 0 END, " +
                "  ultima_consulta = SYSDATETIME(), " +
                "  atualizado_em = SYSDATETIME() " +
                "WHERE dataset_id=? " +
                "ELSE " +
                "INSERT INTO dash.dataset_estatistica " +
                "(dataset_id, total_consultas, tempo_medio_ms, cache_hit_count, consultas_lentas, ultima_consulta) " +
                "VALUES (?,1,?,CASE WHEN ?=1 THEN 1 ELSE 0 END,CASE WHEN ?=1 THEN 1 ELSE 0 END,SYSDATETIME())")) {
            ps.setInt(1, datasetId);
            ps.setInt(2, tempoMs);
            ps.setBoolean(3, cacheHit);
            ps.setBoolean(4, lenta);
            ps.setInt(5, datasetId);
            ps.setInt(6, datasetId);
            ps.setInt(7, tempoMs);
            ps.setBoolean(8, cacheHit);
            ps.setBoolean(9, lenta);
            ps.executeUpdate();
        }
    }

    public JsonObject obterEstatisticas(Connection conn, int ultimasHoras) throws Exception {
        JsonObject result = new JsonObject();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS total, " +
                "  AVG(tempo_ms) AS tempo_medio, " +
                "  SUM(CASE WHEN cache_hit = 1 THEN 1 ELSE 0 END) AS cache_hits, " +
                "  SUM(CASE WHEN lenta = 1 THEN 1 ELSE 0 END) AS lentas, " +
                "  SUM(CASE WHEN erro IS NOT NULL THEN 1 ELSE 0 END) AS erros " +
                "FROM dash.consulta_log " +
                "WHERE criado_em >= DATEADD(HOUR, -?, SYSDATETIME())")) {
            ps.setInt(1, ultimasHoras);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    result.addProperty("totalConsultas", total);
                    result.addProperty("tempoMedio", rs.getInt("tempo_medio"));
                    result.addProperty("cacheHits", rs.getInt("cache_hits"));
                    result.addProperty("consultasLentas", rs.getInt("lentas"));
                    result.addProperty("erros", rs.getInt("erros"));
                    result.addProperty("cacheHitRate",
                            total > 0 ? Math.round((double) rs.getInt("cache_hits") / total * 100) : 0);
                }
            }
        }

        result.addProperty("ok", true);
        return result;
    }

    public JsonObject obterConsultasLentas(Connection conn, int limite) throws Exception {
        JsonObject result = new JsonObject();
        JsonArray arr = new JsonArray();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT TOP (?) cl.id, cl.dataset_id, cl.tempo_ms, cl.linhas_retornadas, " +
                "cl.criado_em, d.nome AS dataset_nome " +
                "FROM dash.consulta_log cl " +
                "LEFT JOIN dash.dataset d ON cl.dataset_id = d.id " +
                "WHERE cl.lenta = 1 " +
                "ORDER BY cl.criado_em DESC")) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", rs.getLong("id"));
                    item.addProperty("datasetId", rs.getInt("dataset_id"));
                    item.addProperty("datasetNome", rs.getString("dataset_nome"));
                    item.addProperty("tempoMs", rs.getInt("tempo_ms"));
                    item.addProperty("linhas", rs.getInt("linhas_retornadas"));
                    item.addProperty("criadoEm", rs.getString("criado_em"));
                    arr.add(item);
                }
            }
        }

        result.addProperty("ok", true);
        result.add("consultasLentas", arr);
        return result;
    }

    public static int getThresholdLentaMs() {
        return THRESHOLD_LENTA_MS;
    }
}
