package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/** Configuracao do Sistema de Chamados: restrita ao perfil ADMIN. */
@WebServlet("/MuralhaDigital/ChamadoConfig")
public class ChamadoConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChamadoConfigServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        try (Connection conn = Conexao.getConexao()) {
            ctx.exigir(ctx.operador.perfis.contains(PerfilChamado.ADMIN));
            String acao = req.getParameter("acao");
            JsonObject r = new JsonObject();
            if ("listar".equals(acao)) {
                JsonArray pri = new JsonArray();
                ChamadoConfigDao.prioridades(conn, false).forEach(p -> pri.add(p.json()));
                JsonArray cat = new JsonArray();
                ChamadoConfigDao.categorias(conn, false).forEach(c -> cat.add(c.json()));
                r.add("prioridades", pri);
                r.add("categorias", cat);
            } else if ("parametros".equals(acao)) {
                r = ChamadoConfigDao.parametrosSla(conn);
            } else if ("perfis".equals(acao)) {
                r.add("membros", ChamadoConfigDao.membrosPerfis(conn));
            } else if ("buscarUsuarios".equals(acao)) {
                r.add("usuarios", ChamadoConfigDao.buscarUsuarios(conn, req.getParameter("termo")));
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
            ContextoRequisicao.json(resp, r);
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        req.setCharacterEncoding("UTF-8");
        try (Connection conn = Conexao.getConexao()) {
            ctx.exigir(ctx.operador.perfis.contains(PerfilChamado.ADMIN));
            String acao = req.getParameter("acao");
            if ("salvarPrioridade".equals(acao)) {
                Prioridade p = new Prioridade();
                p.id = inteiro(req, "id", 0);
                p.codigo = req.getParameter("codigo");
                p.nome = req.getParameter("nome");
                p.ordem = inteiro(req, "ordem", 0);
                p.minutosAtendimento = inteiro(req, "minutosAtendimento", 0);
                p.minutosResolucao = inteiro(req, "minutosResolucao", 0);
                p.cor = req.getParameter("cor");
                ChamadoConfigDao.salvarPrioridade(conn, p);
                AuditoriaService.registrar(req, "Chamado", "SALVAR_PRIORIDADE", p.codigo,
                        p.nome + " " + p.minutosAtendimento + "/" + p.minutosResolucao + " min");
            } else if ("salvarCategoria".equals(acao)) {
                Categoria c = new Categoria();
                c.id = inteiro(req, "id", 0);
                c.nome = req.getParameter("nome");
                int idPai = inteiro(req, "idPai", 0);
                c.idPai = idPai == 0 ? null : idPai;
                c.exigeEquipamento = "true".equals(req.getParameter("exigeEquipamento"));
                ChamadoConfigDao.salvarCategoria(conn, c);
                AuditoriaService.registrar(req, "Chamado", "SALVAR_CATEGORIA", String.valueOf(c.id), c.nome);
            } else if ("alternarAtivo".equals(acao)) {
                String tipo = req.getParameter("tipo");
                int id = inteiro(req, "id", 0);
                ChamadoConfigDao.alternarAtivo(conn, tipo, id);
                AuditoriaService.registrar(req, "Chamado", "ALTERNAR_ATIVO", tipo + "#" + id, "");
            } else if ("salvarParametros".equals(acao)) {
                salvarParametros(ctx, conn, req);
            } else if ("adicionarMembro".equals(acao) || "removerMembro".equals(acao)) {
                alterarMembro(ctx, conn, req, "adicionarMembro".equals(acao));
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
            ContextoRequisicao.json(resp, new JsonObject());
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    private static void salvarParametros(ContextoRequisicao ctx, Connection conn, HttpServletRequest req)
            throws Exception {
        int risco = inteiro(req, "riscoPercentual", 0);
        int dias = inteiro(req, "fechamentoAutoDias", 0);
        JsonObject antes = ChamadoConfigDao.parametrosSla(conn);
        ChamadoConfigDao.salvarParametrosSla(conn, risco, dias, ctx.usuario.getId());
        String descricao = "risco " + antes.get("riscoPercentual").getAsInt() + "->" + risco
                + ", fechamento " + antes.get("fechamentoAutoDias").getAsInt() + "->" + dias;
        AuditoriaService.registrar(req, "Chamado", "SALVAR_PARAMETROS_SLA", "SLA", descricao);
        logger.info("Parametros de SLA alterados por usuario " + ctx.usuario.getId() + ": " + descricao);
    }

    private static void alterarMembro(ContextoRequisicao ctx, Connection conn, HttpServletRequest req,
                                      boolean adicionar) throws Exception {
        PerfilChamado perfil;
        try {
            perfil = PerfilChamado.valueOf(String.valueOf(req.getParameter("perfil")));
        } catch (IllegalArgumentException e) {
            throw new RegraChamadoException("Perfil inválido.");
        }
        int idUsuario = inteiro(req, "idUsuario", 0);
        if (idUsuario <= 0) throw new RegraChamadoException("Usuário inválido.");
        String alvo = ChamadoConfigDao.nomeLogin(conn, idUsuario);
        if (adicionar) ChamadoConfigDao.adicionarMembro(conn, perfil, idUsuario);
        else ChamadoConfigDao.removerMembro(conn, perfil, idUsuario);
        String descricao = perfil + " - " + (alvo == null ? "usuário " + idUsuario : alvo);
        AuditoriaService.registrar(req, "Chamado", adicionar ? "ADICIONAR_PERFIL" : "REMOVER_PERFIL",
                String.valueOf(idUsuario), descricao);
        logger.info((adicionar ? "Perfil adicionado" : "Perfil removido") + " por usuario "
                + ctx.usuario.getId() + ": " + descricao);
    }

    private static int inteiro(HttpServletRequest req, String nome, int padrao) {
        String v = req.getParameter(nome);
        if (v == null || v.trim().isEmpty()) return padrao;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new RegraChamadoException("Valor inválido em " + nome + ".");
        }
    }
}
