package muralha.digital.analise;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import com.google.gson.JsonObject;
import muralha.digital.acessos.ControleAcesso;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Gera guest tokens JWT para embedding de dashboards do Superset
 * em telas do GTW (iframe). O token permite acesso somente leitura
 * ao dashboard especificado, sem necessidade de login no Superset.
 *
 * Endpoint: /MuralhaDigital/AnaliseGuestToken?dashboardId=N
 */
@WebServlet("/MuralhaDigital/AnaliseGuestToken")
public class GuestTokenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GuestTokenServlet.class.getName());
    private static final String HMAC_ALGO = "HmacSHA256";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = obterUsuario(req);
        if (usuario == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"erro\":\"Sessão inválida\"}");
            return;
        }

        if (!ControleAcesso.exigir(req, resp)) return;

        String dashboardId = req.getParameter("dashboardId");
        if (dashboardId == null || dashboardId.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"erro\":\"dashboardId obrigatório\"}");
            return;
        }

        String segredo = obterSegredo();
        if (segredo == null || segredo.isEmpty()) {
            LOG.severe("[Muralha] AnaliseGuestToken: SUPERSET_SECRET_KEY não configurada");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"erro\":\"Configuração ausente\"}");
            return;
        }

        try {
            String token = gerarGuestToken(usuario, dashboardId, segredo);

            resp.setContentType("application/json; charset=UTF-8");
            JsonObject json = new JsonObject();
            json.addProperty("token", token);
            resp.getWriter().write(json.toString());

            LOG.fine("[Muralha] Guest token gerado para " + usuario.getUsuario()
                    + ", dashboard=" + dashboardId);
        } catch (Exception e) {
            LOG.severe("[Muralha] Erro ao gerar guest token: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"erro\":\"Erro interno\"}");
        }
    }

    private String gerarGuestToken(Usuario usuario, String dashboardId, String segredo)
            throws Exception {

        long agora = System.currentTimeMillis() / 1000;
        long expiracao = agora + 3600;

        String header = base64url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");

        JsonObject payload = new JsonObject();
        payload.addProperty("user", usuario.getUsuario());
        payload.addProperty("iat", agora);
        payload.addProperty("exp", expiracao);
        payload.addProperty("aud", "superset");
        payload.addProperty("type", "guest");

        JsonObject resources = new JsonObject();
        resources.addProperty("type", "dashboard");
        resources.addProperty("id", dashboardId);

        com.google.gson.JsonArray resourcesArr = new com.google.gson.JsonArray();
        resourcesArr.add(resources);
        payload.add("resources", resourcesArr);

        JsonObject userObj = new JsonObject();
        userObj.addProperty("username", usuario.getUsuario());
        userObj.addProperty("first_name", usuario.getUsuario());
        userObj.addProperty("last_name", "");
        payload.add("user", userObj);

        String payloadB64 = base64url(payload.toString());

        String assinatura = hmacSha256(header + "." + payloadB64, segredo);

        return header + "." + payloadB64 + "." + assinatura;
    }

    private String base64url(String input) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data, String segredo) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec spec = new SecretKeySpec(
                segredo.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
        mac.init(spec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private Usuario obterUsuario(HttpServletRequest req) {
        if (req.getSession(false) == null) return null;
        return (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
    }

    private String obterSegredo() {
        String chave = System.getenv("SUPERSET_SECRET_KEY");
        if (chave != null && !chave.isEmpty()) return chave;

        return System.getProperty("superset.secret.key", "");
    }
}
