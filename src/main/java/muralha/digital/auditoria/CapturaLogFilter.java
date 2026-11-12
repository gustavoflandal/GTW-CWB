package muralha.digital.auditoria;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Captura automática de log de operação para TODAS as telas do sistema
 * (Muralha Digital e legado GTW clássico), substituindo a instrumentação
 * manual como mecanismo principal de cobertura.
 *
 * <p>Mapeado em {@code /*}: intercepta toda requisição, delega ao restante da
 * cadeia e, ao final, envia o evento ao {@link AuditoriaService}, que decide
 * (via {@link MotorRegras}) se e como registrar. A captura é assíncrona e,
 * envolvida em try/finally + try/catch, NUNCA interfere na resposta ao usuário.
 */
@WebFilter("/*")
public class CapturaLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // sem inicialização
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest request  = (HttpServletRequest) req;
        RespostaComStatus response = new RespostaComStatus((HttpServletResponse) res);

        // Garante que a ABERTURA DE TELAS (navegação HTML) sempre chegue ao servidor,
        // para registrar o acesso mesmo quando nada é feito na tela e mesmo em reaberturas
        // (impede o navegador de servir a página do cache, o que esconderia o acesso).
        try {
            String accept = request.getHeader("Accept");
            if ("GET".equalsIgnoreCase(request.getMethod()) && accept != null && accept.contains("text/html")) {
                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            }
        } catch (Exception ignore) {}

        long inicio = System.currentTimeMillis();
        int status = 200;
        try {
            chain.doFilter(request, response);
            status = response.getStatusCapturado();
        } catch (IOException | ServletException | RuntimeException e) {
            status = 500; // falha não tratada: será registrada como ERRO
            throw e;
        } finally {
            try {
                long duracao = System.currentTimeMillis() - inicio;
                String rota = extrairRota(request);
                AuditoriaService.capturarAutomatico(request, response,
                        rota, request.getMethod(), status, duracao);
            } catch (Throwable t) {
                // Blindagem total: a captura jamais compromete a requisição.
            }
        }
    }

    /** Caminho sem o context-path e sem a query string. */
    private String extrairRota(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            uri = uri.substring(ctx.length());
        }
        return uri;
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
