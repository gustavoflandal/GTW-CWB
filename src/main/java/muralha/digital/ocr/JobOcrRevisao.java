package muralha.digital.ocr;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.util.List;

/**
 * Avalia as passagens novas pelo score de OCR e encaminha as de baixa confiabilidade para a
 * fila de revisão manual (TR 5.7.4.3, 5.2.2.5). Roda a cada 5 minutos (Agendador).
 */
@DisallowConcurrentExecution
public class JobOcrRevisao implements Job {

    private static final Logger logger = Logger.getLogger(JobOcrRevisao.class);
    private static volatile boolean avisouSemEstrutura;

    @Override
    public void execute(JobExecutionContext context) {
        try (Connection conn = Conexao.getConexao()) {
            JdbcRepositorioOcr repo = new JdbcRepositorioOcr(conn);
            if (!repo.estruturaInstalada()) {
                if (!avisouSemEstrutura) {
                    avisouSemEstrutura = true;
                    logger.warn("JobOcrRevisao: tabelas de OCR não existem; execute 20261016_ocr_score_revisao_placas.sql.");
                }
                return;
            }
            ParametrosOcr p = ParametrosOcr.carregar(conn);
            List<PassagemPendente> pendentes = repo.pendentes(p.getJanelaHoras(), p.getLote());
            if (pendentes.isEmpty()) return;
            ProcessadorOcr.Resumo r = new ProcessadorOcr(repo).processar(pendentes, p, repo.configuracoes(), System.currentTimeMillis());
            logger.info("JobOcrRevisao: " + r);
        } catch (Exception e) {
            logger.error("Erro no JobOcrRevisao", e);
        }
    }
}
