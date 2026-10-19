package muralha.digital.alerta.operacional;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import muralha.digital.acessos.ControleAcesso;
import muralha.digital.logtecnico.AlteracaoParametro;
import muralha.digital.logtecnico.ContextoAlteracao;
import muralha.digital.logtecnico.HistoricoParametros;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Configuração dos tipos de alerta (Plano 28): criticidade, cor, ícone e tratamentos.
 * Sem "acao" abre a tela; "aparencia" alimenta o popup de notificação de todos os operadores.
 */
@WebServlet("/MuralhaDigital/TipoAlertaConfig")
public class TipoAlertaConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(TipoAlertaConfigServlet.class);
    private static final Gson gson = new Gson();
    private static final String TELA = "/muralha-digital/pages/tipos-alerta/index.jsp";
    private static final String RECURSO_TIPO = "TIPO_ALERTA";
    private static final String RECURSO_TRATAMENTO = "TIPO_ALERTA_TRATAMENTO";
    private static final int MAX_PARAMETROS = 2000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String acao = req.getParameter("acao");
        // A aparência (cor e ícone) é lida pelo popup de notificação de qualquer operador logado; o restante exige o direito da tela.
        if (acao == null || acao.isEmpty()) {
            if (!ControleAcesso.exigirTela(req, resp)) return;
            req.getRequestDispatcher(TELA).forward(req, resp);
            return;
        }
        if ("aparencia".equals(acao) ? !ControleAcesso.exigirSessao(req, resp) : !ControleAcesso.exigir(req, resp)) return;

        resp.setContentType("application/json;charset=UTF-8");
        try (Connection conn = Conexao.getConexao()) {
            switch (acao) {
                case "aparencia":
                    resp.getWriter().write(gson.toJson(aparencia(listarConfigs(conn))));
                    break;
                case "listar":
                    resp.getWriter().write(gson.toJson(listarConfigs(conn)));
                    break;
                case "tratamentos": {
                    UUID idTipo = uuid(req.getParameter("idTipo"));
                    if (idTipo == null) { resp.sendError(400, "idTipo inválido"); return; }
                    resp.getWriter().write(gson.toJson(listarTratamentos(conn, idTipo)));
                    break;
                }
                case "catalogo":
                    resp.getWriter().write(gson.toJson(catalogo()));
                    break;
                default:
                    resp.sendError(400, "Ação desconhecida");
            }
        } catch (Exception e) {
            logger.error("Erro em TipoAlertaConfigServlet GET: " + e.getMessage(), e);
            resp.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp)) return;

        resp.setContentType("application/json;charset=UTF-8");
        String acao = req.getParameter("acao");
        if (acao == null) { resp.sendError(400, "acao obrigatória"); return; }

        try (Connection conn = Conexao.getConexao()) {
            ContextoAlteracao ctx = ContextoAlteracao.de(req, "TipoAlertaConfig");
            Map<String, Object> resultado = new HashMap<>();

            switch (acao) {
                case "salvarConfig": {
                    UUID idTipo = uuid(req.getParameter("idTipo"));
                    boolean critico = "1".equals(req.getParameter("critico"));
                    String cor = vazioParaNulo(req.getParameter("cor"));
                    String icone = vazioParaNulo(req.getParameter("icone"));

                    if (idTipo == null) { resp.sendError(400, "idTipo inválido"); return; }
                    if (!ConfigTipoAlerta.corValida(cor)) { resp.sendError(400, "Cor inválida"); return; }
                    if (!ConfigTipoAlerta.iconeValido(icone)) { resp.sendError(400, "Ícone inválido"); return; }
                    if (!tipoExiste(conn, idTipo)) { resp.sendError(404, "Tipo de alerta não encontrado"); return; }

                    salvarConfig(conn, idTipo, critico, cor, icone, ctx);
                    resultado.put("ok", true);
                    AuditoriaService.registrar(req, "tipo-alerta-config", "salvar",
                        idTipo.toString(), "critico=" + critico + " cor=" + cor + " icone=" + icone);
                    break;
                }
                case "salvarTratamento": {
                    UUID idTipo = uuid(req.getParameter("idTipo"));
                    String tratamento = req.getParameter("tratamento");
                    boolean automatico = "1".equals(req.getParameter("automatico"));
                    String parametrosJson = vazioParaNulo(req.getParameter("parametrosJson"));
                    boolean ativo = !"0".equals(req.getParameter("ativo"));

                    if (idTipo == null || tratamento == null) {
                        resp.sendError(400, "idTipo e tratamento obrigatórios"); return;
                    }
                    TratamentoAlerta t = TratamentoAlerta.interpretar(tratamento);
                    if (t == null) { resp.sendError(400, "Tratamento obrigatório"); return; }
                    if (automatico && !t.isExecutavel()) {
                        resp.sendError(400, "Este tratamento depende do operador e não pode ser automático"); return;
                    }
                    if (!parametrosValidos(parametrosJson)) {
                        resp.sendError(400, "Parâmetros devem ser um objeto JSON de até " + MAX_PARAMETROS + " caracteres"); return;
                    }
                    if (!tipoExiste(conn, idTipo)) { resp.sendError(404, "Tipo de alerta não encontrado"); return; }

                    salvarTratamento(conn, idTipo, t.name(), automatico, parametrosJson, ativo, ctx);
                    resultado.put("ok", true);
                    AuditoriaService.registrar(req, "tipo-alerta-tratamento", "salvar",
                        idTipo.toString(), t.name() + " ativo=" + ativo + " auto=" + automatico);
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
                    resp.sendError(400, "Ação desconhecida");
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

    /** Mantém só o que o popup de notificação precisa: tipo, cor e ícone. */
    static List<Map<String, Object>> aparencia(List<Map<String, Object>> configs) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Map<String, Object> c : configs) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", c.get("tipo"));
            m.put("cor", c.get("cor"));
            m.put("icone", c.get("icone"));
            lista.add(m);
        }
        return lista;
    }

    static List<Map<String, Object>> listarConfigs(Connection conn) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT t.id, t.tipo, t.descricao, t.prioridade, t.tarefa_ativa, "
            + "ISNULL(c.critico, 0) AS critico, c.cor, c.icone, "
            + "(SELECT COUNT(*) FROM muralha.tipo_alerta_tratamento x "
            + "  WHERE x.id_tipo_alerta_ocorrencia = t.id AND x.ativo = 1) AS tratamentos, "
            + "(SELECT COUNT(*) FROM muralha.tipo_alerta_tratamento x "
            + "  WHERE x.id_tipo_alerta_ocorrencia = t.id AND x.ativo = 1 AND x.automatico = 1) AS automaticos "
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
                m.put("tratamentos", rs.getInt("tratamentos"));
                m.put("automaticos", rs.getInt("automaticos"));
                lista.add(m);
            }
        }
        return lista;
    }

    private static Map<String, Object> catalogo() {
        List<Map<String, Object>> tratamentos = new ArrayList<>();
        for (TratamentoAlerta t : TratamentoAlerta.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("codigo", t.name());
            m.put("descricao", t.getDescricao());
            m.put("executavel", t.isExecutavel());
            tratamentos.add(m);
        }
        List<Map<String, String>> cores = new ArrayList<>();
        for (Map.Entry<String, String> e : ConfigTipoAlerta.CORES.entrySet()) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("codigo", e.getKey());
            m.put("rotulo", e.getValue());
            cores.add(m);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("tratamentos", tratamentos);
        r.put("cores", cores);
        return r;
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
        Map<String, String> antes = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT critico, cor, icone FROM muralha.tipo_alerta_config WHERE id_tipo_alerta_ocorrencia = ?")) {
            ps.setString(1, idTipo.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    antes.put("critico", String.valueOf(rs.getBoolean("critico")));
                    antes.put("cor", rs.getString("cor"));
                    antes.put("icone", rs.getString("icone"));
                }
            }
        }

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
            ps.setObject(5, ctx.getIdUsuario());
            ps.setBoolean(6, critico);
            ps.setString(7, cor);
            ps.setString(8, icone);
            ps.setObject(9, ctx.getIdUsuario());
            ps.executeUpdate();
        }

        Map<String, String> depois = new LinkedHashMap<>();
        depois.put("critico", String.valueOf(critico));
        depois.put("cor", cor);
        depois.put("icone", icone);
        List<AlteracaoParametro> alteracoes = HistoricoParametros.comparar(antes, depois);
        HistoricoParametros.registrarSemFalhar(conn, RECURSO_TIPO, idTipo.toString(), alteracoes, ctx);
    }

    private static void salvarTratamento(Connection conn, UUID idTipo, String tratamento, boolean automatico,
                                         String parametrosJson, boolean ativo, ContextoAlteracao ctx) throws SQLException {
        Map<String, String> antes = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT automatico, parametros_json, ativo FROM muralha.tipo_alerta_tratamento "
                + "WHERE id_tipo_alerta_ocorrencia = ? AND tratamento = ?")) {
            ps.setString(1, idTipo.toString());
            ps.setString(2, tratamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    antes.put(tratamento + ".ativo", String.valueOf(rs.getBoolean("ativo")));
                    antes.put(tratamento + ".automatico", String.valueOf(rs.getBoolean("automatico")));
                    antes.put(tratamento + ".parametros", rs.getString("parametros_json"));
                }
            }
        }

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

        Map<String, String> depois = new LinkedHashMap<>();
        depois.put(tratamento + ".ativo", String.valueOf(ativo));
        depois.put(tratamento + ".automatico", String.valueOf(automatico));
        depois.put(tratamento + ".parametros", parametrosJson);
        HistoricoParametros.registrarSemFalhar(conn, RECURSO_TRATAMENTO, idTipo.toString(),
            HistoricoParametros.comparar(antes, depois), ctx);
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

    private static boolean tipoExiste(Connection conn, UUID idTipo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM muralha.tipo_alerta_ocorrencia WHERE id = ?")) {
            ps.setString(1, idTipo.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    static boolean parametrosValidos(String json) {
        if (json == null) return true;
        if (json.length() > MAX_PARAMETROS) return false;
        try {
            return JsonParser.parseString(json).isJsonObject();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static UUID uuid(String valor) {
        if (valor == null) return null;
        try {
            return UUID.fromString(valor.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String vazioParaNulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }
}
