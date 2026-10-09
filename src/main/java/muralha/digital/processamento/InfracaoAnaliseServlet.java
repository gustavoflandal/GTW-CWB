package muralha.digital.processamento;

import com.consilux.infra.SessaoConstantes;
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
import java.io.IOException;

@WebServlet("/MuralhaDigital/InfracaoAnalise")
public class InfracaoAnaliseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
        JsonObject r = new JsonObject();

        try {
            int idUsuario = obterIdUsuario(req);
            if ("proximaFila".equals(acao)) {
                JsonObject prox = InfracaoAnaliseDAO.obterProxima(idUsuario);
                if (prox != null) {
                    r.addProperty("ok", true);
                    r.add("infracao", prox);
                } else {
                    r.addProperty("ok", true);
                    r.addProperty("filaVazia", true);
                }
            } else if ("indicadores".equals(acao)) {
                r.addProperty("ok", true);
                r.add("dados", InfracaoAnaliseDAO.obterIndicadores());
            } else {
                resp.sendError(400, "acao invalida");
                return;
            }
        } catch (Exception e) {
            r.addProperty("ok", false);
            r.addProperty("erro", e.getMessage());
        }
        resp.getWriter().print(gson.toJson(r));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        JsonObject r = new JsonObject();

        try {
            int    idUsuario     = obterIdUsuario(req);
            String idInfracao    = req.getParameter("idInfracao");
            String classificacao = req.getParameter("classificacao");
            String justificativa = req.getParameter("justificativa");

            if (idInfracao == null || idInfracao.isEmpty()) {
                r.addProperty("ok", false);
                r.addProperty("erro", "idInfracao obrigatorio");
                resp.getWriter().print(gson.toJson(r));
                return;
            }

            if (!"VALIDA".equals(classificacao) && !"INVALIDA".equals(classificacao)
                    && !"DUVIDA".equals(classificacao)) {
                r.addProperty("ok", false);
                r.addProperty("erro", "Classificação inválida.");
                resp.getWriter().print(gson.toJson(r));
                return;
            }

            String novoStatus = InfracaoAnaliseDAO.registrarAnalise(
                    idInfracao, idUsuario, classificacao, justificativa);

            AuditoriaService.registrar(req, "DuplaAnalise", "registrar-analise",
                idInfracao,
                "Classificação: " + classificacao + " | Status: " + novoStatus);

            r.addProperty("ok", true);
            r.addProperty("novoStatus", novoStatus);

        } catch (IllegalStateException e) {
            r.addProperty("ok", false);
            r.addProperty("erro", e.getMessage());
        } catch (Exception e) {
            r.addProperty("ok", false);
            r.addProperty("erro", "Erro interno: " + e.getMessage());
        }
        resp.getWriter().print(gson.toJson(r));
    }

    private int obterIdUsuario(HttpServletRequest req) {
        Usuario usu = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        return usu.getId();
    }
}
