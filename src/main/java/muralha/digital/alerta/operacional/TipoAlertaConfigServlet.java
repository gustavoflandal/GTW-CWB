package muralha.digital.alerta.operacional;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.consilux.infra.SessaoConstantes;
import com.google.gson.Gson;
import muralha.digital.logtecnico.ContextoAlteracao;
import muralha.digital.logtecnico.HistoricoParametros;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/MuralhaDigital/TipoAlertaConfig")
public class TipoAlertaConfigServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TipoAlertaConfigServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json;charset=UTF-8");
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";

        try (Connection conn = Conexao.getConexao()) {
            switch (acao) {
                case "listar":
                    resp.getWriter().write(gson.toJson(listarConfigs(conn)));
                    break;
                case "tratamentos":
                    String idTipo = req.getParameter("idTipo");
                    if (idTipo == null) { resp.sendError(400, "idTipo obrigatório"); return; }
                    resp.getWriter().write(gson.toJson(listarTratamentos(conn, UUID.fromString(idTipo))));
                    break;
                default:
                    resp.sendError(400, "Ação desconhecida: " + acao);
            }
        } catch (Exception e) {
            logger.error("Erro em TipoAlertaConfigServlet GET: " + e.getMessage(), e);
            resp.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json;charset=UTF-8");
        String acao = req.getParameter("acao");
        if (acao == null) { resp.sendError(400, "acao obrigatória"); return; }

        try (Connection conn = Conexao.getConexao()) {
            ContextoAlteracao ctx = ContextoAlteracao.de(req, "TipoAlertaConfig");
            Map<String, Object> resultado = new HashMap<>();

            switch (acao) {
                case "salvarConfig": {
                    String idTipo = req.getParameter("idTipo");
                    boolean critico = "1".equals(req.getParameter("critico"));
                    String cor = req.getParameter("cor");
                    String icone = req.getParameter("icone");

                    if (idTipo == null) { resp.sendError(400, "idTipo obrigatório"); return; }
                    if (!ConfigTipoAlerta.corValida(cor)) { resp.sendError(400, "Cor inválida"); return; }

                    salvarConfig(conn, UUID.fromString(idTipo), critico, cor, icone, ctx);
                    resultado.put("ok", true);
                    AuditoriaService.registrar(req, "tipo-alerta-config", "salvar",
                        idTipo, "critico=" + critico + " cor=" + cor + " icone=" + icone);
                    break;
                }
                case "salvarTratamento": {
                    String idTipo = req.getParameter("idTipo");
                    String tratamento = req.getParameter("tratamento");
                    boolean automatico = "1".equals(req.getParameter("automatico"));
                    String parametrosJson = req.getParameter("parametrosJson");
                    boolean ativo = !"0".equals(req.getParameter("ativo"));

                    if (idTipo == null || tratamento == null) {
                        resp.sendError(400, "idTipo e tratamento obrigatórios"); return;
                    }
                    TratamentoAlerta.interpretar(tratamento);

                    salvarTratamento(conn, UUID.fromString(idTipo), tratamento, automatico, parametrosJson, ativo);
                    resultado.put("ok", true);
                    AuditoriaService.registrar(req, "tipo-alerta-tratamento", "salvar",
                        idTipo, tratamento + " auto=" + automatico);
                    break;
                }
                case "novoTipo": {
                    String tipo = req.getParameter("tipo");
                    String descricao = req.getParameter("descricao");
                    if (tipo == null || tipo.trim().isEmpty()) { resp.sendError(400, "tipo obrigatório"); return; }
                    if (descricao == null || descricao.trim().isEmpty()) { resp.sendError(400, "descricao obrigatória"); return; }

                    UUID id = criarTipo(conn, tipo.trim(), descricao.trim());
                    resultado.put("ok", true);
                    resultado.put("id", id.toString());
                    AuditoriaService.registrar(req, "tipo-alerta-novo", "criar",
                        id.toString(), tipo + " — " + descricao);
                    break;
                }
                default:
                    resp.sendError(400, "Ação desconhecida: " + acao);
                    return;
            }

            resp.getWriter().write(gson.toJson(resultado));
        } catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro em TipoAlertaConfigServlet POST: " + e.getMessage(), e);
            resp.sendError(500);
        }
    }

    static List<Map<String, Object>> listarConfigs(Connection conn) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT t.id, t.tipo, t.descricao, t.prioridade, t.tarefa_ativa, "
            + "ISNULL(c.critico, 0) AS critico, c.cor, c.icone "
            + "FROM muralha.tipo_alerta_ocorrencia t "
            + "LEFT JOIN muralha.tipo_alerta_config c ON c.id_tipo_alerta_ocorrencia = t.id "
            + "ORDER BY t.prioridade, t.tipo";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getString("id"));
                m.put("tipo", rs.getString("tipo"));
                m.put("descricao", rs.getString("descricao"));
                m.put("prioridade", rs.getInt("prioridade"));
                m.put("ativo", rs.getBoolean("tarefa_ativa"));
                m.put("critico", rs.getBoolean("critico"));
                m.put("cor", rs.getString("cor"));
                m.put("icone", rs.getString("icone"));
                lista.add(m);
            }
        }
        return lista;
    }

    private static List<Map<String, Object>> listarTratamentos(Connection conn, UUID idTipo) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, tratamento, automatico, parametros_json, ativo "
                + "FROM muralha.tipo_alerta_tratamento WHERE id_tipo_alerta_ocorrencia = ? ORDER BY tratamento")) {
            ps.setString(1, idTipo.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("tratamento", rs.getString("tratamento"));
                    m.put("automatico", rs.getBoolean("automatico"));
                    m.put("parametrosJson", rs.getString("parametros_json"));
                    m.put("ativo", rs.getBoolean("ativo"));
                    lista.add(m);
                }
            }
        }
        return lista;
    }

    private static void salvarConfig(Connection conn, UUID idTipo, boolean critico,
                                     String cor, String icone, ContextoAlteracao ctx) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE muralha.tipo_alerta_config AS t "
                + "USING (SELECT ? AS id_tipo) AS s ON t.id_tipo_alerta_ocorrencia = CAST(s.id_tipo AS uniqueidentifier) "
                + "WHEN MATCHED THEN UPDATE SET critico = ?, cor = ?, icone = ?, dt_atualizacao = GETDATE(), id_usuario = ? "
                + "WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, critico, cor, icone, dt_atualizacao, id_usuario) "
                + "VALUES (CAST(s.id_tipo AS uniqueidentifier), ?, ?, ?, GETDATE(), ?);")) {
            ps.setString(1, idTipo.toString());
            ps.setBoolean(2, critico);
            ps.setString(3, cor);
            ps.setString(4, icone);
            ps.setInt(5, ctx.getIdUsuario());
            ps.setBoolean(6, critico);
            ps.setString(7, cor);
            ps.setString(8, icone);
            ps.setInt(9, ctx.getIdUsuario());
            ps.executeUpdate();
        }
        HistoricoParametros.registrarSemFalhar(conn, "TIPO_ALERTA", idTipo.toString(),
            HistoricoParametros.comparar("critico", null, String.valueOf(critico)), ctx);
    }

    private static void salvarTratamento(Connection conn, UUID idTipo, String tratamento,
                                         boolean automatico, String parametrosJson, boolean ativo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE muralha.tipo_alerta_tratamento AS t "
                + "USING (SELECT ? AS id_tipo, ? AS trat) AS s "
                + "ON t.id_tipo_alerta_ocorrencia = CAST(s.id_tipo AS uniqueidentifier) AND t.tratamento = s.trat "
                + "WHEN MATCHED THEN UPDATE SET automatico = ?, parametros_json = ?, ativo = ? "
                + "WHEN NOT MATCHED THEN INSERT (id_tipo_alerta_ocorrencia, tratamento, automatico, parametros_json, ativo, dt_cadastro) "
                + "VALUES (CAST(s.id_tipo AS uniqueidentifier), ?, ?, ?, ?, GETDATE());")) {
            ps.setString(1, idTipo.toString());
            ps.setString(2, tratamento);
            ps.setBoolean(3, automatico);
            ps.setString(4, parametrosJson);
            ps.setBoolean(5, ativo);
            ps.setString(6, tratamento);
            ps.setBoolean(7, automatico);
            ps.setString(8, parametrosJson);
            ps.setBoolean(9, ativo);
            ps.executeUpdate();
        }
    }

    private static UUID criarTipo(Connection conn, String tipo, String descricao) throws SQLException {
        UUID id = UUID.randomUUID();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.tipo_alerta_ocorrencia (id, tipo, descricao, tarefa_ativa, prioridade) "
                + "VALUES (?, ?, ?, 1, (SELECT ISNULL(MAX(prioridade),0)+1 FROM muralha.tipo_alerta_ocorrencia))")) {
            ps.setString(1, id.toString());
            ps.setString(2, tipo);
            ps.setString(3, descricao);
            ps.executeUpdate();
        }
        return id;
    }
}
