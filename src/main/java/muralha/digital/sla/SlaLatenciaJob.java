package muralha.digital.sla;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.*;

public class SlaLatenciaJob implements Job {

    private static final int DEFAULT_THRESHOLD_MS = 4000;

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            verificarSla();
        } catch (Exception e) {
            System.err.println("[SlaLatenciaJob] Erro: " + e.getMessage());
        }
    }

    private void verificarSla() throws Exception {
        Connection conn = Conexao.getConexao();
        try {
            int threshold = obterThreshold(conn);

            // Percentil 95 das latências dos últimos 5 minutos
            String sqlP95 =
                "WITH cte AS (" +
                "  SELECT DATEDIFF(MILLISECOND, data, data_importado) AS lat_ms, " +
                "    COUNT(*) OVER () AS total " +
                "  FROM muralha.veiculo_tempo_real " +
                "  WHERE data_importado >= DATEADD(MINUTE,-5,SYSDATETIME()) " +
                "    AND data_importado IS NOT NULL " +
                "    AND DATEDIFF(MILLISECOND, data, data_importado) BETWEEN 0 AND 600000 " +
                ") " +
                "SELECT TOP 1 total, " +
                "  PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY lat_ms) OVER () AS p95 " +
                "FROM cte";

            int p95 = 0, total = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlP95);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                    p95   = (int) rs.getDouble("p95");
                }
            }

            boolean violacao = p95 > threshold;
            String locaisTop = obterTopLocais(conn);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta_sla " +
                    "(janela_inicio, janela_fim, total_passagens, percentil95_ms, " +
                    " threshold_ms, violacao, locais_top) " +
                    "VALUES (DATEADD(MINUTE,-5,SYSDATETIME()), SYSDATETIME(), ?,?,?,?,?)")) {
                ps.setInt(1, total);
                ps.setInt(2, p95);
                ps.setInt(3, threshold);
                ps.setBoolean(4, violacao);
                ps.setString(5, locaisTop);
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }
    }

    private int obterThreshold(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.config_chave_valor WHERE chave='sla_latencia_threshold_ms'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_THRESHOLD_MS;
    }

    private String obterTopLocais(Connection conn) throws Exception {
        StringBuilder sb = new StringBuilder("[");
        String sql =
            "SELECT TOP 3 id_local, " +
            "  CAST(AVG(CAST(DATEDIFF(MILLISECOND, data, data_importado) AS FLOAT)) AS INT) avg_ms " +
            "FROM muralha.veiculo_tempo_real " +
            "WHERE data_importado >= DATEADD(MINUTE,-5,SYSDATETIME()) " +
            "  AND data_importado IS NOT NULL " +
            "  AND DATEDIFF(MILLISECOND, data, data_importado) BETWEEN 0 AND 600000 " +
            "GROUP BY id_local ORDER BY avg_ms DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                sb.append("{\"local\":").append(rs.getInt("id_local"))
                  .append(",\"avg\":").append(rs.getInt("avg_ms")).append("}");
                first = false;
            }
        }
        return sb.append("]").toString();
    }
}
