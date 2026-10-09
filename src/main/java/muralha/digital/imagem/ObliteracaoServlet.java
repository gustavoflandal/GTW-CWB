package muralha.digital.imagem;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/MuralhaDigital/Obliteracao")
public class ObliteracaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        JsonObject r = new JsonObject();
        String acao = req.getParameter("acao");

        try {
            Usuario usu = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
            int idUsuario = usu.getId();

            if ("aplicar".equals(acao)) {
                long idImagem = Long.parseLong(req.getParameter("idImagem"));
                String coords = req.getParameter("coordenadas");
                long idObliterada = ObliteracaoService.aplicar(idImagem, coords, idUsuario);
                AuditoriaService.registrar(req, "Imagem", "obliterar",
                    String.valueOf(idImagem), "Obliteração aplicada → id_obliterada=" + idObliterada);
                r.addProperty("ok", true);
                r.addProperty("idObliterada", idObliterada);

            } else if ("reverter".equals(acao)) {
                long idImagem = Long.parseLong(req.getParameter("idImagem"));
                String justif = req.getParameter("justificativa");
                if (justif == null || justif.trim().isEmpty()) {
                    r.addProperty("ok", false);
                    r.addProperty("erro", "Justificativa obrigatória para reversão.");
                } else {
                    ObliteracaoService.reverter(idImagem, idUsuario, justif.trim());
                    AuditoriaService.registrar(req, "Imagem", "reverter-obliteracao",
                        String.valueOf(idImagem), "Reversão: " + justif);
                    r.addProperty("ok", true);
                }
            } else {
                r.addProperty("ok", false);
                r.addProperty("erro", "acao invalida");
            }
        } catch (IllegalArgumentException e) {
            r.addProperty("ok", false);
            r.addProperty("erro", e.getMessage());
        } catch (Exception e) {
            r.addProperty("ok", false);
            r.addProperty("erro", "Erro interno: " + e.getMessage());
        }
        resp.getWriter().print(gson.toJson(r));
    }
}
