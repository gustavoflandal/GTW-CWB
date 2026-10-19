package muralha.digital.integridade;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.logtecnico.ContextoAlteracao;
import muralha.digital.logtecnico.HistoricoParametros;
import muralha.digital.util.ConfigChaveValor;
import muralha.digital.util.Resultado;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Configuração da origem do hash de integridade: padrão global (INTEGRIDADE_ORIGEM_HASH em
 * muralha.config_chave_valor) e exceções por equipamento (muralha.equipamento_integridade_config).
 * GET listar; POST salvarPadrao, salvarEquipamento.
 */
@WebServlet("/MuralhaDigital/IntegridadeConfig")
public class IntegridadeConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(IntegridadeConfigServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!ControleAcesso.exigir(req, resp)) { return; }

        if (!"listar".equals(req.getParameter("acao"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            return;
        }
        try (Connection conn = Conexao.getConexao()) {
            ParametrosIntegridade p = ParametrosIntegridade.carregar(conn);
            JsonObject res = new JsonObject();
            res.addProperty("origem_padrao", p.getOrigemPadrao().getTipo().name());
            res.addProperty("campo_padrao", p.getOrigemPadrao().getCampo());
            res.addProperty("lote", p.getLote());
            res.addProperty("janela_horas", p.getJanelaHoras());
            res.addProperty("espera_hash_min", p.getEsperaHashMin());
            res.addProperty("alerta_habilitado", p.isAlertaHabilitado());

            JsonArray equipamentos = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT lv.id_local, RTRIM(lv.nome) AS nome, RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) AS serie, " +
                    "       c.origem_hash, c.campo, c.ativo " +
                    "FROM local_vigente lv " +
                    "LEFT JOIN muralha.equipamento_integridade_config c ON c.id_local = lv.id_local " +
                    "ORDER BY lv.nome");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id_local", rs.getInt("id_local"));
                    o.addProperty("equipamento", rs.getString("nome"));
                    o.addProperty("serie", rs.getString("serie"));
                    o.addProperty("origem_hash", rs.getString("origem_hash"));
                    o.addProperty("campo", rs.getString("campo"));
                    boolean configurado = rs.getString("origem_hash") != null;
                    o.addProperty("ativo", configurado && rs.getBoolean("ativo"));
                    equipamentos.add(o);
                }
            }
            res.add("equipamentos", equipamentos);
            json(resp, res);
        } catch (Exception e) {
            logger.error("Erro ao listar a configuração de integridade", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!ControleAcesso.exigir(req, resp)) { return; }

        String acao = req.getParameter("acao");
        OrigemHash origem;
        try {
            origem = OrigemHash.de(OrigemHash.tipoDe(req.getParameter("origem")), req.getParameter("campo"));
        } catch (IllegalArgumentException e) {
            json(resp, new Resultado(false, e.getMessage()));
            return;
        }

        try (Connection conn = Conexao.getConexao()) {
            if ("salvarPadrao".equals(acao)) {
                String anterior = ConfigChaveValor.obter(conn, ParametrosIntegridade.CHAVE_ORIGEM, OrigemHash.AUSENTE.formatar());
                ConfigChaveValor.gravar(conn, ParametrosIntegridade.CHAVE_ORIGEM, origem.formatar(),
                    ContextoAlteracao.de(req, "Integridade das imagens"));
                AuditoriaService.registrar(req, "Integridade de imagens", "config-origem-padrao",
                    ParametrosIntegridade.CHAVE_ORIGEM, "De " + anterior + " para " + origem.formatar());
                json(resp, new Resultado(true, "Origem padrão salva."));

            } else if ("salvarEquipamento".equals(acao)) {
                int idLocal;
                try {
                    idLocal = Integer.parseInt(req.getParameter("idLocal"));
                } catch (NumberFormatException e) {
                    json(resp, new Resultado(false, "Equipamento inválido."));
                    return;
                }
                boolean ativo = !"0".equals(req.getParameter("ativo"));
                String anterior = origemAtual(conn, idLocal);
                try (PreparedStatement ps = conn.prepareStatement(
                        "MERGE muralha.equipamento_integridade_config AS t " +
                        "USING (SELECT ? id_local, ? origem_hash, ? campo, ? ativo, ? id_usuario) AS s " +
                        "ON t.id_local = s.id_local " +
                        "WHEN MATCHED THEN UPDATE SET origem_hash = s.origem_hash, campo = s.campo, ativo = s.ativo, " +
                        "  id_usuario = s.id_usuario, data_modificacao = GETDATE() " +
                        "WHEN NOT MATCHED THEN INSERT (id_local, origem_hash, campo, ativo, id_usuario, data_modificacao) " +
                        "VALUES (s.id_local, s.origem_hash, s.campo, s.ativo, s.id_usuario, GETDATE());")) {
                    ps.setInt(1, idLocal);
                    ps.setString(2, origem.getTipo().name());
                    ps.setString(3, origem.getCampo());
                    ps.setBoolean(4, ativo);
                    ps.setObject(5, usuario.getId(), java.sql.Types.INTEGER);
                    ps.executeUpdate();
                }
                HistoricoParametros.registrarSemFalhar(conn, HistoricoParametros.RECURSO_EQUIPAMENTO, String.valueOf(idLocal),
                    HistoricoParametros.comparar("integridade.origem_hash", anterior,
                        ativo ? origem.formatar() : null), ContextoAlteracao.de(req, "Integridade das imagens"));
                AuditoriaService.registrar(req, "Integridade de imagens", "config-origem-equipamento",
                    String.valueOf(idLocal), "De " + (anterior == null ? "padrão" : anterior)
                        + " para " + (ativo ? origem.formatar() : "padrão (configuração desativada)"));
                json(resp, new Resultado(true, "Configuração do equipamento salva."));

            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar a configuração de integridade, acao=" + acao, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    /** Origem ativa do equipamento no formato do parâmetro, ou nulo se usa o padrão. */
    private static String origemAtual(Connection conn, int idLocal) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT origem_hash, campo, ativo FROM muralha.equipamento_integridade_config WHERE id_local = ?")) {
            ps.setInt(1, idLocal);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !rs.getBoolean("ativo")) return null;
                String campo = rs.getString("campo");
                return rs.getString("origem_hash") + (campo == null ? "" : ":" + campo);
            }
        }
    }

    private static void json(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(obj));
    }
}
