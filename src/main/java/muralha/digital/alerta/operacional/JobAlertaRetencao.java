package muralha.digital.alerta.operacional;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;

@DisallowConcurrentExecution
public class JobAlertaRetencao implements Job {

    private static final Logger logger = Logger.getLogger(JobAlertaRetencao.class);

    @Override
    public void execute(JobExecutionContext ctx) {
        try (Connection conn = Conexao.getConexao()) {
            ParametrosRetencao params = ParametrosRetencao.carregar(conn);
            RepositorioRetencao repo = new JdbcRepositorioRetencao(conn);
            ProcessadorRetencao.Resumo resumo = new ProcessadorRetencao(repo).processar(params);
            if (resumo.getAlertasGerados() > 0 || resumo.getFalhas() > 0) {
                logger.info(String.format("Retenção: %d verificados, %d alertas, %d já alertados, %d falhas",
                    resumo.getVerificados(), resumo.getAlertasGerados(), resumo.getJaAlertados(), resumo.getFalhas()));
            }
        } catch (Exception e) {
            logger.error("Erro no job de retenção: " + e.getMessage(), e);
        }
    }
}
