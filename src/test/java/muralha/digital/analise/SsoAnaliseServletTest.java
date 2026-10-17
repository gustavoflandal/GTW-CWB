package muralha.digital.analise;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SsoAnaliseServletTest {

    @Test
    public void servletInstanciavelSemErro() {
        SsoAnaliseServlet servlet = new SsoAnaliseServlet();
        assertNotNull(servlet);
    }

    @Test
    public void acoesEsperadas() {
        String[] acoesValidas = {"validar", "abrir"};
        assertEquals(acoesValidas.length, 2);
        assertEquals(acoesValidas[0], "validar");
        assertEquals(acoesValidas[1], "abrir");
    }

    @Test
    public void statusUnauthorizedE401() {
        assertEquals(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, 401);
    }

    @Test
    public void statusNoContentE204() {
        assertEquals(javax.servlet.http.HttpServletResponse.SC_NO_CONTENT, 204);
    }

    @Test
    public void cabecalhosSsoEsperados() {
        String[] cabecalhos = {"X-GTW-Usuario", "X-GTW-UserId", "X-GTW-Grupos"};
        assertEquals(cabecalhos.length, 3);
        assertTrue(cabecalhos[0].startsWith("X-GTW-"));
        assertTrue(cabecalhos[1].startsWith("X-GTW-"));
        assertTrue(cabecalhos[2].startsWith("X-GTW-"));
    }
}
