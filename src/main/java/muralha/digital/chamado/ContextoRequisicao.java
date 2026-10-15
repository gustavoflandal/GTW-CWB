package muralha.digital.chamado;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

/** Sessao + acesso + perfis do Sistema de Chamados, comum aos tres servlets. */
final class ContextoRequisicao {

    private static final Gson GSON = new Gson();

    final Usuario usuario;
    final ChamadoService.Operador operador;

    private ContextoRequisicao(Usuario usuario, ChamadoService.Operador operador) {
        this.usuario = usuario;
        this.operador = operador;
    }

    /** Retorna null quando a resposta (401/403) ja foi enviada. */
    static ContextoRequisicao de(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sessao = req.getSession(false);
        Usuario usuario = sessao == null ? null : (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return null;
        Set<PerfilChamado> perfis;
        try (Connection conn = Conexao.getConexao()) {
            perfis = ChamadoConfigDao.perfis(conn, usuario.getId());
        } catch (Exception e) {
            throw new IOException("Falha ao obter perfis do Sistema de Chamados", e);
        }
        if (perfis.isEmpty()) {
            erro(resp, HttpServletResponse.SC_FORBIDDEN, "Usuário sem perfil no Sistema de Chamados.");
            return null;
        }
        return new ContextoRequisicao(usuario, new ChamadoService.Operador(usuario.getId(), perfis, req));
    }

    static void json(HttpServletResponse resp, JsonObject corpo) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if (!corpo.has("ok")) corpo.addProperty("ok", true);
        resp.getWriter().write(GSON.toJson(corpo));
    }

    static void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        resp.setStatus(status);
        JsonObject o = new JsonObject();
        o.addProperty("ok", false);
        o.addProperty("erro", mensagem);
        json(resp, o);
    }

    void exigir(boolean condicao) {
        if (!condicao) throw new RegraChamadoException("Seu perfil não permite esta operação.");
    }
}
