package muralha.digital.dashboards;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Le os corpos JSON da API de dashboards como UTF-8.
 *
 * O fetch envia "application/json" sem charset e o Tomcat decodifica o corpo como
 * ISO-8859-1; sem este filtro rotulos, titulos e valores de filtro com acento
 * ("Classificação") seriam gravados e consultados corrompidos.
 */
@WebFilter("/api/dash/*")
public class CodificacaoUtf8Filter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // sem configuracao
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding("UTF-8");
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
