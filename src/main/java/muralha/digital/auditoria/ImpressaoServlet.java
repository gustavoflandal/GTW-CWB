package muralha.digital.auditoria;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Endpoint interno do Motor de Captura de Logs: registra a impressão de uma tela
 * pelo navegador (window.print). Sem sessão autenticada não registra; responde sempre 204.
 * Não usa ControleAcesso estrito nem sis_menu (endpoint de log, não uma tela).
 */
@WebServlet("/MuralhaDigital/Impressao")
public class ImpressaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ImpressaoServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            if (AuditoriaService.temUsuarioAutenticado(request)) {
                AuditoriaService.registrarImpressao(request,
                        AuditoriaService.formatoOuPadrao(request.getParameter("formato")),
                        request.getParameter("telaOrigem"));
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "[MotorLogs] Falha no endpoint de impressao", e);
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
