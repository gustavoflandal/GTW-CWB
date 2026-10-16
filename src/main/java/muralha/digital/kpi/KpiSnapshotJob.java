package muralha.digital.kpi;

import com.consilux.lib.Conexao;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KpiSnapshotJob implements Job {

    private static final Logger logger = Logger.getLogger(KpiSnapshotJob.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        try {
            coletarSnapshots();
        } catch (Exception e) {
            logger.error("Erro ao coletar snapshots de KPI", e);
        }
    }

    private void coletarSnapshots() throws Exception {
        Connection conn = Conexao.getConexao();
        try {
            int retencaoDias = ConfigChaveValor.obterInt(conn, "KPI_SNAPSHOT_RETENCAO_DIAS", 90);
            int cooldownMin = ConfigChaveValor.obterInt(conn, "KPI_ALERTA_COOLDOWN_MIN", 360);

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT k.id, k.nome, k.query_sql, k.threshold_ok, k.threshold_warn, " +
                    "m.meta, m.meta_minima, m.gerar_alerta " +
                    "FROM muralha.kpi_config k " +
                    "LEFT JOIN muralha.kpi_config_meta m ON m.id_kpi = k.id " +
                    "WHERE k.ativo = 1 ORDER BY k.ordem");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idKpi = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String sql = rs.getString("query_sql");
                    double thOk = rs.getDouble("threshold_ok");
                    double thWarn = rs.getDouble("threshold_warn");
                    Double meta = rs.getObject("meta") != null ? rs.getDouble("meta") : null;
                    Double metaMin = rs.getObject("meta_minima") != null ? rs.getDouble("meta_minima") : null;
                    boolean gerarAlerta = rs.getObject("gerar_alerta") == null || rs.getBoolean("gerar_alerta");

                    if (sql == null || !sql.trim().toUpperCase().startsWith("SELECT")) continue;

                    try {
                        double valor = executarQuery(conn, sql.trim());
                        String status = calcularStatus(valor, thOk, thWarn);
                        gravarSnapshot(conn, idKpi, valor, status);

                        if (gerarAlerta && !"OK".equals(status)) {
                            verificarEGerarAlerta(conn, idKpi, nome, valor, status, meta, metaMin, cooldownMin);
                        }
                    } catch (Exception e) {
                        logger.warn("Erro ao coletar KPI id=" + idKpi + " (" + nome + "): " + e.getMessage());
                    }
                }
            }

            limparHistoricoAntigo(conn, retencaoDias);
        } finally {
            conn.close();
        }
    }

    private double executarQuery(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("valor");
        }
        return 0;
    }

    static String calcularStatus(double valor, double thOk, double thWarn) {
        if (thOk > thWarn) {
            return valor >= thOk ? "OK" : valor >= thWarn ? "WARN" : "CRIT";
        } else {
            return valor <= thOk ? "OK" : valor <= thWarn ? "WARN" : "CRIT";
        }
    }

    private void gravarSnapshot(Connection conn, int idKpi, double valor, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.kpi_historico (id_kpi, valor, status, dt_coleta) " +
                "VALUES (?, ?, ?, GETDATE())")) {
            ps.setInt(1, idKpi);
            ps.setDouble(2, valor);
            ps.setString(3, status);
            ps.executeUpdate();
        }
    }

    private void verificarEGerarAlerta(Connection conn, int idKpi, String nome, double valor,
                                        String status, Double meta, Double metaMin, int cooldownMin)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS cnt FROM muralha.kpi_historico " +
                "WHERE id_kpi = ? AND status <> 'OK' " +
                "AND dt_coleta >= DATEADD(MINUTE, -?, GETDATE())")) {
            ps.setInt(1, idKpi);
            ps.setInt(2, cooldownMin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") > 1) return;
            }
        }

        String descricao = String.format("KPI '%s' em status %s (valor=%.2f", nome, status, valor);
        if (meta != null) descricao += String.format(", meta=%.2f", meta);
        descricao += ")";

        AuditoriaService.registrarSistema("KPI", "ALERTA_ANALITICO",
                String.valueOf(idKpi), descricao);
        logger.info("Alerta analítico gerado para KPI " + idKpi + ": " + descricao);
    }

    private void limparHistoricoAntigo(Connection conn, int retencaoDias) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM muralha.kpi_historico WHERE dt_coleta < DATEADD(DAY, -?, GETDATE())")) {
            ps.setInt(1, retencaoDias);
            int removidos = ps.executeUpdate();
            if (removidos > 0) logger.info("Removidos " + removidos + " snapshots antigos (>" + retencaoDias + " dias).");
        }
    }
}
