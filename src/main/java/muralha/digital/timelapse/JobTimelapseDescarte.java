package muralha.digital.timelapse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.lib.Conexao;
import muralha.digital.util.ConfigChaveValor;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobTimelapseDescarte implements Job {

    private static final Logger logger = Logger.getLogger(JobTimelapseDescarte.class);

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            int retencaoHoras = ConfigChaveValor.obterInt(conn, "TIMELAPSE_RETENCAO_HORAS", 72);
            boolean habilitado = "true".equalsIgnoreCase(
                ConfigChaveValor.obter(conn, "TIMELAPSE_DESCARTE_HABILITADO", "true"));

            if (!habilitado) {
                logger.debug("[Muralha] Descarte de timelapse desabilitado via config.");
                return;
            }

            int descartados = descartar(conn, retencaoHoras);
            if (descartados > 0) {
                logger.info(String.format(
                    "[Muralha] Descarte timelapse: %d registros de cache removidos (retenção = %dh).",
                    descartados, retencaoHoras));
            }
        } catch (Exception e) {
            logger.error("[Muralha] Erro no descarte de timelapse.", e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }
    }

    private int descartar(Connection conn, int retencaoHoras) throws SQLException {
        String sql =
            "DELETE FROM muralha.timelapse_cache " +
            "WHERE dt_geracao < DATEADD(HOUR, ?, SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, -retencaoHoras);
            return ps.executeUpdate();
        }
    }
}
