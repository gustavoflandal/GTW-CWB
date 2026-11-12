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
        // Marca a requisicao para o CapturaLogFilter NUNCA registrar este endpoint
        // de forma generica (evita eventos vagos "HTTP 204 /MuralhaDigital/Impressao"
        // sem origem quando nao ha sessao). O registro real, com a tela de origem,
        // e feito abaixo por registrarEventoCliente quando ha usuario autenticado.
        try { request.setAttribute(AuditoriaService.ATTR_LOG_MANUAL, Boolean.TRUE); } catch (Exception ignore) {}
        try {
            request.setCharacterEncoding("UTF-8");
            if (origemCruzada(request)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return; // cross-origin forjado: nao registra
            }
            if (AuditoriaService.temUsuarioAutenticado(request)) {
                AuditoriaService.registrarEventoCliente(request,
                        request.getParameter("nivel"),
                        request.getParameter("formato"),
                        request.getParameter("telaOrigem"));
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "[MotorLogs] Falha no endpoint de impressao", e);
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /** True somente se houver Origin de outra authority que o Host; Origin ausente/malformado nao bloqueia. */
    private static boolean origemCruzada(HttpServletRequest request) {
        try {
            String origin = request.getHeader("Origin");
            if (origin != null && !origin.isEmpty()) {
                String oHost = new java.net.URI(origin).getAuthority();
                String host = request.getHeader("Host");
                return oHost != null && host != null && !oHost.equalsIgnoreCase(host);
            }
        } catch (Exception ignore) { /* Origin malformado: nao bloqueia */ }
        return false;
    }
}
