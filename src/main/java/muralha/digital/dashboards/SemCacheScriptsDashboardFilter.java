package muralha.digital.dashboards;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Obriga o navegador a revalidar os modulos JavaScript do dashboard a cada carga.
 *
 * Os modulos ES se importam entre si pelo caminho fixo (editor-widget.js -> widget-registry.js
 * -> widgets/*.js), sem versao na URL. Com o cache heuristico do navegador, apos uma
 * atualizacao uma parte podia vir nova e outra velha, e o editor quebrava. Com
 * "no-cache" o navegador pergunta ao servidor (ETag/Last-Modified) e recebe 304 quando
 * nada mudou, entao o custo e minimo.
 */
@WebFilter("/static/js/dashboards/*")
public class SemCacheScriptsDashboardFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // sem configuracao
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if (resp instanceof HttpServletResponse) {
            HttpServletResponse http = (HttpServletResponse) resp;
            http.setHeader("Cache-Control", "no-cache, must-revalidate");
            http.setHeader("Expires", "0");
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
