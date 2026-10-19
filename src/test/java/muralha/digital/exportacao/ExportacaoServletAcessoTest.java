package muralha.digital.exportacao;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import muralha.digital.acessos.ControleAcesso;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ExportacaoServletAcessoTest {

    @AfterMethod
    public void limpar() { ControleAcesso.restaurarPadrao(); }

    @Test
    public void semDireitoResponde403SemTocarNoBanco() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> false, r -> {});
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(7);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession sessao = mock(HttpSession.class);
        when(sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO)).thenReturn(usuario);
        when(req.getSession(false)).thenReturn(sessao);
        when(req.getSession()).thenReturn(sessao);
        when(req.getServletPath()).thenReturn("/MuralhaDigital/Exportacao");
        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter corpo = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(corpo));

        new ExportacaoServlet().doGet(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(corpo.toString().contains("\"ok\":false"));
    }
}
