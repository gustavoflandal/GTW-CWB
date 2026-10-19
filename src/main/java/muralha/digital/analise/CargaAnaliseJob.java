package muralha.digital.analise;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Recarrega as tabelas materializadas da plataforma de Analises (schema analise).
 * Mesmas procedures de analise/scripts/refresh-materialized.sh, com as janelas padrao
 * (25 h de passagens, 7 dias de infracoes). Cada procedure registra em analise.carga_log.
 */
@DisallowConcurrentExecution
public class CargaAnaliseJob implements Job {

    private static final Logger logger = Logger.getLogger(CargaAnaliseJob.class);

    static final String[] PROCEDURES = {
        "analise.spu_carga_passagens_hora",
        "analise.spu_carga_infracoes_dia",
        "analise.spu_carga_areas_monitoradas"
    };

    @Override
    public void execute(JobExecutionContext ctx) {
        for (String proc : PROCEDURES) {
            long inicio = System.currentTimeMillis();
            try (Connection conn = Conexao.getConexao();
                 CallableStatement cs = conn.prepareCall("{call " + proc + "}")) {
                cs.setQueryTimeout(600);
                cs.execute();
                logger.info("Carga " + proc + " concluida em " + (System.currentTimeMillis() - inicio) + " ms.");
            } catch (Exception e) {
                logger.error("Erro na carga " + proc + ": " + e.getMessage(), e);
            }
        }
    }
}
