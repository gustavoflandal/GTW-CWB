package muralha.digital.dashboards.consulta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Executa o SQL montado (somente leitura, com timeout) e converte o resultado para o JSON do painel.
 * Mantem um cache curto em memoria para refreshes simultaneos do mesmo widget.
 */
public final class ExecutorConsulta {

    public static final int TIMEOUT_SEG = 30;
    private static final long TTL_CACHE_MS = 60_000;
    private static final int MAX_CACHE = 200;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final Map<String, EntradaCache> CACHE = new ConcurrentHashMap<>();

    private static final class EntradaCache {
        final String json;
        final long criadoEm;

        EntradaCache(String json, long criadoEm) {
            this.json = json;
            this.criadoEm = criadoEm;
        }
    }

    public static final class Resultado {
        public final JsonArray dados;
        public final boolean cacheHit;

        Resultado(JsonArray dados, boolean cacheHit) {
            this.dados = dados;
            this.cacheHit = cacheHit;
        }
    }

    public Resultado executar(Connection conn, SqlMontado montado) throws SQLException {
        String chave = chaveCache(montado);
        long agora = System.currentTimeMillis();
        EntradaCache entrada = CACHE.get(chave);
        if (entrada != null && agora - entrada.criadoEm < TTL_CACHE_MS) {
            return new Resultado(JsonParser.parseString(entrada.json).getAsJsonArray(), true);
        }

        JsonArray dados = consultar(conn, montado);
        guardar(chave, dados.toString(), agora);
        return new Resultado(dados, false);
    }

    private JsonArray consultar(Connection conn, SqlMontado montado) throws SQLException {
        int isolamentoOriginal = conn.getTransactionIsolation();
        boolean somenteLeituraOriginal = conn.isReadOnly();
        try {
            conn.setReadOnly(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try (PreparedStatement ps = conn.prepareStatement(montado.sql)) {
                ps.setQueryTimeout(TIMEOUT_SEG);
                ps.setFetchSize(500);
                int i = 1;
                for (Object p : montado.parametros) {
                    vincular(ps, i++, p);
                }
                JsonArray linhas = new JsonArray();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject linha = new JsonObject();
                        for (SqlMontado.ColunaSaida c : montado.colunas) {
                            JsonElement valor = paraJson(rs.getObject(c.aliasSql));
                            for (String k : c.chaves) {
                                linha.add(k, valor);
                            }
                        }
                        linhas.add(linha);
                    }
                }
                return linhas;
            }
        } finally {
            try {
                conn.setTransactionIsolation(isolamentoOriginal);
                conn.setReadOnly(somenteLeituraOriginal);
            } catch (SQLException ignorado) {
                // a conexao sera validada pelo pool na devolucao
            }
        }
    }

    private static void vincular(PreparedStatement ps, int i, Object p) throws SQLException {
        if (p instanceof Integer) ps.setInt(i, (Integer) p);
        else if (p instanceof Long) ps.setLong(i, (Long) p);
        else if (p instanceof BigDecimal) ps.setBigDecimal(i, (BigDecimal) p);
        else if (p instanceof Timestamp) ps.setTimestamp(i, (Timestamp) p);
        else if (p instanceof Boolean) ps.setBoolean(i, (Boolean) p);
        else ps.setString(i, String.valueOf(p));
    }

    static JsonElement paraJson(Object v) {
        if (v == null) return JsonNull.INSTANCE;
        if (v instanceof Number) return new JsonPrimitive((Number) v);
        if (v instanceof Boolean) return new JsonPrimitive((Boolean) v);
        if (v instanceof Timestamp) return new JsonPrimitive(((Timestamp) v).toLocalDateTime().format(FORMATO_DATA));
        if (v instanceof java.sql.Date) return new JsonPrimitive(((java.sql.Date) v).toLocalDate().atStartOfDay().format(FORMATO_DATA));
        if (v instanceof LocalDateTime) return new JsonPrimitive(((LocalDateTime) v).format(FORMATO_DATA));
        if (v instanceof LocalDate) return new JsonPrimitive(((LocalDate) v).atStartOfDay().format(FORMATO_DATA));
        return new JsonPrimitive(v.toString());
    }

    private static void guardar(String chave, String json, long agora) {
        if (CACHE.size() >= MAX_CACHE) {
            CACHE.entrySet().removeIf(e -> agora - e.getValue().criadoEm >= TTL_CACHE_MS);
            if (CACHE.size() >= MAX_CACHE) {
                CACHE.entrySet().stream()
                        .min((a, b) -> Long.compare(a.getValue().criadoEm, b.getValue().criadoEm))
                        .ifPresent(e -> CACHE.remove(e.getKey()));
            }
        }
        CACHE.put(chave, new EntradaCache(json, agora));
    }

    static String chaveCache(SqlMontado montado) {
        StringBuilder sb = new StringBuilder(montado.sql);
        for (Object p : montado.parametros) {
            sb.append('\u0001').append(p == null ? "null" : p.getClass().getSimpleName() + ":" + p);
        }
        for (SqlMontado.ColunaSaida c : montado.colunas) {
            sb.append('\u0002').append(String.join("\u0003", c.chaves));
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
