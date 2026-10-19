package muralha.digital.analise;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import muralha.digital.acessos.ControleAcesso;
import muralha.digital.util.ConfigChaveValor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

@WebServlet("/MuralhaDigital/SsoAnalise")
public class SsoAnaliseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(SsoAnaliseServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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
        HttpSession sessao = req.getSession(false);
        Usuario usuario = sessao != null
                ? (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO)
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

    private void abrir(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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

        String supersetBase = obterBaseUrl();
        req.setAttribute("supersetBaseUrl", supersetBase);
        req.setAttribute("podeAbrirSuperset", podeAbrirSuperset(usuario));

        LOG.info("[Muralha] SSO Analise: usuario " + usuario.getUsuario()
                + " acessando analises, base=" + supersetBase);

        req.getRequestDispatcher("/muralha-digital/pages/analise/embedding.jsp")
           .forward(req, resp);
    }

    private static final String DEFAULT_SUPERSET_URL = "http://localhost:8088";

    /** Funcionalidade em dbo.sis_menu (id 396) que concede o botao "Abrir no Superset". */
    static final String ACAO_ABRIR_SUPERSET = "/MuralhaDigital/SsoAnalise/abrir-superset";

    /**
     * Direito explicito a ACAO_ABRIR_SUPERSET (por grupo ou por usuario em sis_menu_direitos).
     * Ao contrario do padrao de telas, nega quando a funcionalidade nao esta cadastrada ou em erro.
     */
    private boolean podeAbrirSuperset(Usuario usuario) {
        return ControleAcesso.possuiDireito(ACAO_ABRIR_SUPERSET, usuario.getId());
    }

    private String obterBaseUrl() {
        try (Connection conn = Conexao.getConexao()) {
            return ConfigChaveValor.obter(conn, "SUPERSET_BASE_URL", DEFAULT_SUPERSET_URL);
        } catch (Exception e) {
            LOG.warning("[Muralha] SSO Analise: erro ao ler SUPERSET_BASE_URL — usando padrao: " + e.getMessage());
            return DEFAULT_SUPERSET_URL;
        }
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
