package muralha.digital.auditoria;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inicialização do Motor de Captura de Logs.
 *
 * <ul>
 *   <li>Na subida: aquece o cache de regras ({@link MotorRegras}) e agenda o
 *       job diário do selo de integridade ({@link SeloService}).</li>
 *   <li>Na parada: encerra o agendador.</li>
 * </ul>
 *
 * O expurgo NÃO é executado automaticamente por padrão (operação destrutiva):
 * é disparado pela tela de administração. Pode-se habilitar execução automática
 * via {@code -Dgtw.logs.expurgo.auto=true}.
 */
@WebListener
public class MotorLogListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(MotorLogListener.class.getName());

    private ScheduledThreadPoolExecutor agendador;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // O cache de regras é carregado preguiçosamente na primeira requisição
        // (quando a conexão já está configurada), evitando falha no startup.

        final boolean expurgoAuto = "true".equalsIgnoreCase(System.getProperty("gtw.logs.expurgo.auto", "false"));

        agendador = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r, "motor-logs-selo");
            t.setDaemon(true);
            return t;
        });

        // Selo de integridade: 2 min após subir e a cada 24h.
        agendador.scheduleAtFixedRate(() -> {
            try {
                SeloService.gerarSelosPendentes();
                if (expurgoAuto) {
                    ExpurgoService.executar("sistema", false);
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, "[MotorLogs] Falha no job de selo/expurgo", t);
            }
        }, 2, 24 * 60, TimeUnit.MINUTES);

        log.info("[MotorLogs] Motor de captura iniciado (selo diario agendado; expurgo auto=" + expurgoAuto + ").");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (agendador != null) {
            agendador.shutdownNow();
        }
    }
}
