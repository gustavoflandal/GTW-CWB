package muralha.digital.analise;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/MuralhaDigital/SsoAnalise")
public class SsoAnaliseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SsoAnaliseServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");

        if ("validar".equals(acao)) {
            validar(req, resp);
        } else if ("abrir".equals(acao)) {
            abrir(req, resp);
        } else {
            validar(req, resp);
        }
    }

    private void validar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession(false) != null
                ? (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO)
                : null;

        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        resp.setHeader("X-GTW-Usuario", usuario.getUsuario());
        resp.setHeader("X-GTW-UserId", String.valueOf(usuario.getId()));
        resp.setHeader("X-GTW-Grupos", obterGrupos(req));

        LOG.fine("[Muralha] SSO Analise: validacao OK para " + usuario.getUsuario());
    }

    private void abrir(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) {
            resp.sendRedirect(req.getContextPath() + "/login/abertura-sistemas.jsp");
            return;
        }

        Usuario usuario = (Usuario) req.getSession()
                .getAttribute(SessaoConstantes.SESSAO_USUARIO);

        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + "/login/abertura-sistemas.jsp");
            return;
        }

        String supersetUrl = req.getParameter("destino");
        if (supersetUrl == null || supersetUrl.isEmpty()) {
            supersetUrl = "/";
        }

        LOG.info("[Muralha] SSO Analise: usuario " + usuario.getUsuario()
                + " acessando analises, destino=" + supersetUrl);

        resp.sendRedirect(supersetUrl);
    }

    private String obterGrupos(HttpServletRequest req) {
        try {
            Object gruposAttr = req.getSession().getAttribute("gruposUsuario");
            if (gruposAttr != null) {
                return gruposAttr.toString();
            }
        } catch (Exception e) {
            LOG.warning("[Muralha] SSO Analise: erro ao obter grupos — " + e.getMessage());
        }
        return "";
    }
}
