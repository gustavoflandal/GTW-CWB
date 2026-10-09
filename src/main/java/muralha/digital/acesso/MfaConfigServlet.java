package muralha.digital.acesso;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/MuralhaDigital/MfaConfig")
public class MfaConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        JsonObject r = new JsonObject();
        String acao = req.getParameter("acao");

        if ("ativar".equals(acao)) {
            HttpSession sess = req.getSession(false);
            String segredo = sess != null ? (String) sess.getAttribute("totp_setup_segredo") : null;
            String codigo  = req.getParameter("codigo");

            if (segredo == null) {
                r.addProperty("ok", false);
                r.addProperty("erro", "Sessão expirada. Reinicie o processo.");
            } else if (!TotpService.validar(segredo, codigo)) {
                r.addProperty("ok", false);
                r.addProperty("erro", "Código inválido. Tente novamente.");
            } else {
                try {
                    Usuario usu = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
                    int idUsuario = usu.getId();
                    Connection conn = null;
                    try {
                        conn = Conexao.getConexao();
                        try (PreparedStatement ps = conn.prepareStatement(
                                "UPDATE dbo.sis_usuario SET totp_secret=?, totp_habilitado=1 WHERE id=?")) {
                            ps.setString(1, segredo);
                            ps.setInt(2, idUsuario);
                            ps.executeUpdate();
                        }
                    } finally {
                        if (conn != null) try { conn.close(); } catch (Exception e2) {}
                    }
                    req.getSession().removeAttribute("totp_setup_segredo");
                    AuditoriaService.registrar(req, "Acesso", "mfa-ativado",
                        String.valueOf(idUsuario), "MFA TOTP ativado pelo usuário");
                    r.addProperty("ok", true);
                } catch (Exception e) {
                    r.addProperty("ok", false);
                    r.addProperty("erro", e.getMessage());
                }
            }

        } else if ("desativar".equals(acao)) {
            try {
                Usuario usu = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
                int idUsuario = usu.getId();
                Connection conn = null;
                try {
                    conn = Conexao.getConexao();
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dbo.sis_usuario SET totp_secret=NULL, totp_habilitado=0 WHERE id=?")) {
                        ps.setInt(1, idUsuario);
                        ps.executeUpdate();
                    }
                } finally {
                    if (conn != null) try { conn.close(); } catch (Exception e2) {}
                }
                AuditoriaService.registrar(req, "Acesso", "mfa-desativado",
                    String.valueOf(idUsuario), "MFA TOTP desativado pelo usuário");
                r.addProperty("ok", true);
            } catch (Exception e) {
                r.addProperty("ok", false);
                r.addProperty("erro", e.getMessage());
            }

        } else {
            r.addProperty("ok", false);
            r.addProperty("erro", "acao invalida");
        }

        resp.getWriter().print(gson.toJson(r));
    }
}
