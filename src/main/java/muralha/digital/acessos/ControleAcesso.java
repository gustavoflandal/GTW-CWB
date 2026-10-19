package muralha.digital.acessos;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * Verificacao ESTRITA de direito para os modulos novos do Muralha.
 * Diferente de Acesso.verificaAcesso (fail-open), nega quando a funcionalidade
 * nao esta cadastrada em sis_menu/sis_menu_direitos.
 */
public final class ControleAcesso {

    private static final Logger logger = Logger.getLogger(ControleAcesso.class);

    /** Consulta de direito (troca nos testes). */
    public interface VerificadorDireito { boolean possui(String acao, int idUsuario); }

    /** Auditoria da negacao de acesso (troca nos testes). */
    public interface AuditorNegacao { void registrar(HttpServletRequest req, String acao, Usuario usuario); }

    private static final VerificadorDireito VERIFICADOR_PADRAO = ControleAcesso::consultarBanco;
    private static final AuditorNegacao AUDITOR_PADRAO = ControleAcesso::auditarNegacao;
    /** Mantem o log de acesso que o Acesso legado grava no construtor. */
    private static final Consumer<HttpServletRequest> REGISTRADOR_PADRAO = r -> new Acesso(r, null, true);

    private static volatile VerificadorDireito verificador = VERIFICADOR_PADRAO;
    private static volatile Consumer<HttpServletRequest> registrador = REGISTRADOR_PADRAO;
    private static volatile AuditorNegacao auditor = AUDITOR_PADRAO;

    private ControleAcesso() {}

    /** Troca verificador e registrador; a auditoria de negacao vira no-op. */
    public static void definirParaTestes(VerificadorDireito v, Consumer<HttpServletRequest> r) {
        definirParaTestes(v, r, (req, acao, usuario) -> {});
    }

    public static void definirParaTestes(VerificadorDireito v, Consumer<HttpServletRequest> r, AuditorNegacao a) {
        verificador = v;
        registrador = r;
        auditor = a;
    }

    public static void restaurarPadrao() {
        verificador = VERIFICADOR_PADRAO;
        registrador = REGISTRADOR_PADRAO;
        auditor = AUDITOR_PADRAO;
    }

    public static boolean possuiDireito(String acao, int idUsuario) {
        return verificador.possui(acao, idUsuario);
    }

    private static boolean consultarBanco(String acao, int idUsuario) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT cnt, acesso FROM dbo.fcn_VerificaAcesso(?, ?)")) {
            ps.setString(1, acao);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0 && rs.getInt("acesso") > 0;
            }
        } catch (Exception e) {
            logger.warn("Falha ao verificar o direito '" + acao + "' do usuario " + idUsuario + ": " + e.getMessage());
            return false;
        }
    }

    public static Usuario usuario(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        return sessao == null ? null : (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO);
    }

    /** Exige direito ao proprio servlet (servletPath). Responde 401/403 em JSON. */
    public static boolean exigir(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return exigir(req, resp, req.getServletPath());
    }

    /** Exige direito a acao informada (ex.: acao de menu de uma tela). Responde 401/403 em JSON. */
    public static boolean exigir(HttpServletRequest req, HttpServletResponse resp, String acao) throws IOException {
        Usuario usuario = usuario(req);
        if (usuario == null) {
            responder(resp, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada. Faça login novamente.");
            return false;
        }
        // Como no legado, o log de acesso e gravado tambem quando o acesso e negado.
        registrador.accept(req);
        if (possuiDireito(acao, usuario.getId())) return true;
        auditor.registrar(req, acao, usuario);
        responder(resp, HttpServletResponse.SC_FORBIDDEN, "Sem permissão para esta funcionalidade.");
        return false;
    }

    /** Para servlets que encaminham para JSP quando chamados sem acao. */
    public static boolean exigirTela(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = usuario(req);
        if (usuario == null) { resp.sendRedirect("/login/login.jsp"); return false; }
        // Como no legado, o log de acesso e gravado tambem quando o acesso e negado.
        registrador.accept(req);
        String acao = req.getServletPath();
        if (possuiDireito(acao, usuario.getId())) return true;
        auditor.registrar(req, acao, usuario);
        resp.sendRedirect("/login/gtw_principal.jsp");
        return false;
    }

    private static void auditarNegacao(HttpServletRequest req, String acao, Usuario usuario) {
        try {
            AuditoriaService.registrar(req, "acesso", "negado", acao,
                    "Acesso negado a " + acao + " para o usuario " + usuario.getId());
        } catch (Exception e) {
            logger.warn("Falha ao registrar negacao de acesso: " + e.getMessage());
        }
    }

    private static void responder(HttpServletResponse resp, int status, String mensagem) throws IOException {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("ok", false);
        corpo.addProperty("erro", mensagem);
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().print(corpo.toString());
    }
}
