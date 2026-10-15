package muralha.digital.chamado;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/** A cada 5 min: marcas de SLA e fechamento automatico (spec §3.5). */
@DisallowConcurrentExecution
public class ChamadoSlaJob implements Job {

    private static final Logger logger = Logger.getLogger(ChamadoSlaJob.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        ChamadoService service = new ChamadoService();
        try {
            service.atualizarSla();
        } catch (Exception e) {
            logger.error("ChamadoSlaJob: falha ao atualizar SLA", e);
        }
        try {
            int fechados = service.fecharResolvidosAntigos();
            if (fechados > 0) logger.info("ChamadoSlaJob: " + fechados + " chamado(s) fechado(s) automaticamente.");
        } catch (Exception e) {
            logger.error("ChamadoSlaJob: falha no fechamento automatico", e);
        }
    }
}
