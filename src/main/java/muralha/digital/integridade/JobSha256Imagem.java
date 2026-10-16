package muralha.digital.integridade;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.Connection;
import java.util.List;

/**
 * Confere a integridade das imagens recebidas (TR 5.2.1.2): para cada imagem das passagens
 * recentes ainda não conferidas, obtém o hash de origem conforme o equipamento
 * (muralha.equipamento_integridade_config ou INTEGRIDADE_ORIGEM_HASH), compara com o SHA-256
 * calculado e grava INTEGRO, DIVERGENTE, SEM_CONFERENCIA ou ERRO_LEITURA em
 * muralha.vtr_imagem_integridade. Divergência gera alerta operacional e registro de auditoria.
 * Roda a cada 5 minutos (Agendador).
 */
@DisallowConcurrentExecution
public class JobSha256Imagem implements Job {

    private static final Logger logger = Logger.getLogger(JobSha256Imagem.class);
    private static volatile boolean avisouSemEstrutura;

    @Override
    public void execute(JobExecutionContext context) {
        try (Connection conn = Conexao.getConexao()) {
            ParametrosIntegridade parametros = ParametrosIntegridade.carregar(conn);
            JdbcRepositorioIntegridade repositorio = new JdbcRepositorioIntegridade(conn);
            if (!repositorio.estruturaInstalada()) {
                if (!avisouSemEstrutura) {
                    avisouSemEstrutura = true;
                    logger.warn("JobSha256Imagem: muralha.vtr_imagem_integridade não existe; "
                        + "execute 20261016_integridade_sha256_recepcao.sql. Nenhuma imagem será conferida até lá.");
                }
                return;
            }

            List<ImagemPendente> pendentes = repositorio.pendentes(parametros.getJanelaHoras(), parametros.getLote());
            if (pendentes.isEmpty()) return;

            if (parametros.isAlertaHabilitado() && !repositorio.tipoAlertaCadastrado()) {
                logger.warn("JobSha256Imagem: tipo de alerta INTEGRIDADE não cadastrado; "
                    + "execute 20261016_integridade_sha256_recepcao.sql. Divergências serão gravadas sem alerta.");
            }

            ProcessadorIntegridade.Resumo resumo = new ProcessadorIntegridade(repositorio).processar(
                pendentes, parametros, repositorio.origensPorEquipamento(), System.currentTimeMillis());
            logger.info("JobSha256Imagem: " + resumo);
        } catch (Exception e) {
            logger.error("Erro no JobSha256Imagem", e);
        }
    }
}
