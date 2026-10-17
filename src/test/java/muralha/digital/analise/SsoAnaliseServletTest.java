package muralha.digital.analise;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

public class SsoAnaliseServletTest {

    private static HttpServletRequest pedidoValidar(HttpSession sessao) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter("acao")).thenReturn("validar");
        when(req.getSession(false)).thenReturn(sessao);
        when(req.getSession()).thenReturn(sessao);
        return req;
    }

    @Test
    public void validarComSessaoAutenticaERepassaUsuario() throws Exception {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getUsuario()).thenReturn("operador");
        when(usuario.getId()).thenReturn(42);
        HttpSession sessao = mock(HttpSession.class);
        when(sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO)).thenReturn(usuario);
        when(sessao.getAttribute("gruposUsuario")).thenReturn("41");
        HttpServletResponse resp = mock(HttpServletResponse.class);

        new SsoAnaliseServlet().doGet(pedidoValidar(sessao), resp);

        verify(resp).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(resp).setHeader("X-GTW-Usuario", "operador");
        verify(resp).setHeader("X-GTW-UserId", "42");
        verify(resp).setHeader("X-GTW-Grupos", "41");
    }

    @Test
    public void validarSemSessaoResponde401() throws Exception {
        HttpServletResponse resp = mock(HttpServletResponse.class);

        new SsoAnaliseServlet().doGet(pedidoValidar(null), resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(resp, never()).setHeader(anyString(), anyString());
    }

    @Test
    public void validarComSessaoSemUsuarioResponde401() throws Exception {
        HttpServletResponse resp = mock(HttpServletResponse.class);

        new SsoAnaliseServlet().doGet(pedidoValidar(mock(HttpSession.class)), resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
