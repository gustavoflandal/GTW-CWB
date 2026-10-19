package muralha.digital.acessos;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ControleAcessoTest {

    @AfterMethod
    public void limpar() { ControleAcesso.restaurarPadrao(); }

    private static HttpServletRequest pedido(Usuario usuario, String servletPath) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession sessao = mock(HttpSession.class);
        when(sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO)).thenReturn(usuario);
        when(req.getSession(false)).thenReturn(sessao);
        when(req.getSession()).thenReturn(sessao);
        when(req.getServletPath()).thenReturn(servletPath);
        return req;
    }

    private static Usuario usuario(int id) {
        Usuario u = mock(Usuario.class);
        when(u.getId()).thenReturn(id);
        return u;
    }

    @Test
    public void exigirComDireitoLiberaSemEscreverResposta() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> "/MuralhaDigital/Lote".equals(acao) && id == 7, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertTrue(ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), resp));
        verify(resp, never()).setStatus(anyInt());
    }

    @Test
    public void exigirSemDireitoResponde403EmJson() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> false, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter corpo = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(corpo));
        assertFalse(ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), resp));
        verify(resp).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(corpo.toString().contains("\"ok\":false"));
        assertTrue(corpo.toString().contains("Sem permissão"));
    }

    @Test
    public void exigirSemSessaoResponde401() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> {});
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        assertFalse(ControleAcesso.exigir(req, resp));
        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void exigirComAcaoExplicitaUsaAcaoInformada() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> "/muralha-digital/pages/admin/kpis/index.jsp".equals(acao), r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        HttpServletRequest req = pedido(usuario(1), "/MuralhaDigital/Kpi");
        assertTrue(ControleAcesso.exigir(req, resp, "/muralha-digital/pages/admin/kpis/index.jsp"));
        assertFalse(ControleAcesso.exigir(req, resp, "/outra/acao"));
    }

    @Test
    public void exigirTelaSemDireitoRedirecionaParaPrincipal() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> false, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertFalse(ControleAcesso.exigirTela(pedido(usuario(3), "/MuralhaDigital/RelatorioGerencial"), resp));
        verify(resp).sendRedirect("/login/gtw_principal.jsp");
    }

    @Test
    public void exigirTelaSemSessaoRedirecionaParaLogin() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> {});
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertFalse(ControleAcesso.exigirTela(req, resp));
        verify(resp).sendRedirect("/login/login.jsp");
    }

    @Test
    public void registraLogDeAcessoUmaVezPorVerificacao() throws Exception {
        int[] chamadas = {0};
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> chamadas[0]++);
        ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), mock(HttpServletResponse.class));
        assertEquals(chamadas[0], 1);
    }
}
