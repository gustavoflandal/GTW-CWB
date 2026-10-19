package muralha.digital.ocr;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.util.Resultado;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Fila de revisão de placas (TR 5.7.4.3, 5.7.5.2, 5.7.5.4, 5.7.1.4).
 * GET: motivos, operadores, listar, passagem. POST: corrigir.
 */
@WebServlet("/MuralhaDigital/RevisaoPlaca")
public class RevisaoPlacaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RevisaoPlacaServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp)) { return; }

        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            if ("motivos".equals(acao)) {
                JsonArray a = new JsonArray();
                for (CorrecaoPlacaService.Motivo m : CorrecaoPlacaService.motivos(conn)) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", m.id);
                    o.addProperty("descricao", m.descricao);
                    o.addProperty("exige_texto", m.exigeTexto);
                    o.addProperty("confirmacao", m.confirmacao);
                    a.add(o);
                }
                JsonObject r = new JsonObject();
                r.add("motivos", a);
                r.addProperty("justificativa_min", ValidacaoCorrecao.JUSTIFICATIVA_MIN);
                r.addProperty("estrutura_instalada", CorrecaoPlacaService.estruturaInstalada(conn));
                json(resp, r);
            } else if (!CorrecaoPlacaService.estruturaInstalada(conn)) {
                JsonObject r = new JsonObject();
                r.addProperty("aviso", "Fila de revisão indisponível: execute a migração 20261016_ocr_score_revisao_placas.sql.");
                r.add("linhas", new JsonArray());
                r.addProperty("total", 0);
                json(resp, r);
            } else if ("operadores".equals(acao)) {
                operadores(conn, resp);
            } else if ("listar".equals(acao)) {
                listar(conn, req, resp);
            } else if ("passagem".equals(acao)) {
                passagem(conn, req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro em RevisaoPlacaServlet, acao=" + acao, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp)) { return; }
        Usuario usuario = ControleAcesso.usuario(req);

        if (!"corrigir".equals(req.getParameter("acao"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            return;
        }
        try (Connection conn = Conexao.getConexao()) {
            String idVeiculo = uuid(req.getParameter("idVeiculo"));
            Integer idMotivo = null;
            try { idMotivo = Integer.valueOf(req.getParameter("idMotivo")); } catch (Exception e) { /* validado pelo serviço */ }
            CorrecaoPlacaService.Resultado r = CorrecaoPlacaService.corrigir(conn, new CorrecaoPlacaService.Pedido(
                idVeiculo, req.getParameter("placa"), req.getParameter("placaFrontal"),
                CorrecaoPlacaService.adicionais(req.getParameter("adicionais")), idMotivo, req.getParameter("justificativa"),
                CorrecaoPlacaService.OrigemIntervencao.FILA), usuario.getId(), req.getRemoteAddr());
            AuditoriaService.registrar(req, "Revisao de placas", r.confirmacao ? "conferir-placa" : "corrigir-placa", idVeiculo, r.descricao);
            json(resp, new Resultado(true, r.confirmacao ? "Placa conferida." : "Placa corrigida."));
        } catch (IllegalArgumentException e) {
            json(resp, new Resultado(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao corrigir placa na fila de revisão", e);
            json(resp, new Resultado(false, "Erro ao gravar a correção."));
        }
    }

    private void listar(Connection conn, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FiltroRevisao f = FiltroRevisao.de(req.getParameter("dtInicio"), req.getParameter("dtFim"), req.getParameter("idLocais"),
            req.getParameter("situacao"), req.getParameter("motivo"), req.getParameter("scoreMin"), req.getParameter("scoreMax"),
            req.getParameter("intervencao"), req.getParameter("idOperador"));
        int pagina = inteiro(req.getParameter("pagina"), 1, 1, 100_000);
        int tamanho = inteiro(req.getParameter("tamanho"), 50, 1, 100);
        JsonObject r = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM muralha.vtr_revisao_placa r WHERE " + f.getSql())) {
            definir(ps, f);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); r.addProperty("total", rs.getLong(1)); }
        }
        JsonArray linhas = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT r.id, CAST(r.id_veiculo_tempo_real AS VARCHAR(36)) AS id_vtr, r.id_local, RTRIM(lv.nome) AS equipamento, " +
                "       r.dt_passagem, v.placa, v.placa_frontal, r.motivo, r.score_minimo, r.limiar, r.detalhe, r.situacao, " +
                "       r.dt_entrada, r.dt_tratamento, r.origem_intervencao, u.usuario AS operador, " +
                "       (SELECT COUNT(*) FROM muralha.vtr_placa_leitura l WHERE l.id_veiculo_tempo_real = r.id_veiculo_tempo_real " +
                "          AND l.ordem > 2) AS adicionais " +
                "FROM muralha.vtr_revisao_placa r " +
                "LEFT JOIN muralha.veiculo_tempo_real v ON v.id = r.id_veiculo_tempo_real " +
                "LEFT JOIN local_vigente lv ON lv.id_local = r.id_local " +
                "LEFT JOIN dbo.sis_usuario u ON u.id_usuario = r.id_usuario_tratamento " +
                "WHERE " + f.getSql() + " ORDER BY CASE WHEN r.situacao = 'PENDENTE' THEN 0 ELSE 1 END, r.score_minimo, r.dt_passagem DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            int i = definir(ps, f);
            ps.setInt(i++, (pagina - 1) * tamanho);
            ps.setInt(i, tamanho);
            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", rs.getLong("id"));
                    o.addProperty("id_veiculo", rs.getString("id_vtr"));
                    o.addProperty("id_local", rs.getInt("id_local"));
                    o.addProperty("equipamento", rs.getString("equipamento"));
                    o.addProperty("dt_passagem_formatado", formatar(fmt, rs.getTimestamp("dt_passagem")));
                    o.addProperty("placa", aparar(rs.getString("placa")));
                    o.addProperty("placa_frontal", aparar(rs.getString("placa_frontal")));
                    o.addProperty("adicionais", rs.getInt("adicionais"));
                    o.addProperty("motivo", rs.getString("motivo"));
                    o.addProperty("score_minimo", numero(rs.getBigDecimal("score_minimo")));
                    o.addProperty("limiar", numero(rs.getBigDecimal("limiar")));
                    o.addProperty("detalhe", rs.getString("detalhe"));
                    o.addProperty("situacao", rs.getString("situacao"));
                    o.addProperty("dt_entrada_formatado", formatar(fmt, rs.getTimestamp("dt_entrada")));
                    o.addProperty("dt_tratamento_formatado", formatar(fmt, rs.getTimestamp("dt_tratamento")));
                    o.addProperty("intervencao", rs.getString("origem_intervencao"));
                    o.addProperty("operador", rs.getString("operador"));
                    linhas.add(o);
                }
            }
        }
        r.add("linhas", linhas);
        r.addProperty("pagina", pagina);
        r.addProperty("tamanho", tamanho);
        json(resp, r);
    }

    /** Leituras (todas as placas, com score e origem), imagens e histórico de correções da passagem. */
    private void passagem(Connection conn, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String id = uuid(req.getParameter("idVeiculo"));
        JsonObject r = new JsonObject();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT v.placa, v.placa_frontal, v.data, v.id_local, RTRIM(lv.nome) AS equipamento, v.id_pista, v.velocidade " +
                "FROM muralha.veiculo_tempo_real v LEFT JOIN local_vigente lv ON lv.id_local = v.id_local " +
                "WHERE v.id = CAST(? AS UNIQUEIDENTIFIER)")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Passagem não encontrada.");
                r.addProperty("placa", aparar(rs.getString("placa")));
                r.addProperty("placa_frontal", aparar(rs.getString("placa_frontal")));
                r.addProperty("data_formatado", formatar(fmt, rs.getTimestamp("data")));
                r.addProperty("equipamento", rs.getString("equipamento"));
                r.addProperty("faixa", rs.getInt("id_pista"));
                r.addProperty("velocidade", rs.getInt("velocidade"));
            }
        }
        JsonArray leituras = new JsonArray();
        for (LeituraPlaca l : JdbcRepositorioOcr.leituras(conn, id)) {
            JsonObject o = new JsonObject();
            o.addProperty("ordem", l.getOrdem());
            o.addProperty("tipo", l.getTipo().name());
            o.addProperty("placa", l.getPlaca());
            o.addProperty("score", l.getScore());
            o.addProperty("origem", l.getOrigem().name());
            leituras.add(o);
        }
        r.add("leituras", leituras);
        JsonArray imagens = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CAST(id AS VARCHAR(36)) AS id, indice_imagem FROM muralha.veiculo_tempo_real_imagem " +
                "WHERE id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER) ORDER BY indice_imagem")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) imagens.add(rs.getString("id"));
            }
        }
        r.add("imagens", imagens);
        JsonArray historico = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT c.data, c.placa_original, c.placa_digitada, u.usuario, m.descricao AS motivo, j.justificativa, j.origem_intervencao " +
                "FROM muralha.veiculo_tempo_real_correcao c " +
                "LEFT JOIN muralha.vtr_correcao_justificativa j ON j.id_correcao = c.id " +
                "LEFT JOIN muralha.motivo_correcao_placa m ON m.id = j.id_motivo " +
                "LEFT JOIN dbo.sis_usuario u ON u.id_usuario = c.id_usuario " +
                "WHERE c.id_veiculo = CAST(? AS UNIQUEIDENTIFIER) ORDER BY c.data DESC")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("data_formatado", formatar(fmt, rs.getTimestamp("data")));
                    o.addProperty("placa_original", aparar(rs.getString("placa_original")));
                    o.addProperty("placa_digitada", aparar(rs.getString("placa_digitada")));
                    o.addProperty("usuario", rs.getString("usuario"));
                    o.addProperty("motivo", rs.getString("motivo"));
                    o.addProperty("justificativa", rs.getString("justificativa"));
                    o.addProperty("intervencao", rs.getString("origem_intervencao"));
                    historico.add(o);
                }
            }
        }
        r.add("historico", historico);
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT motivo, score_minimo, limiar, detalhe, situacao FROM muralha.vtr_revisao_placa " +
                "WHERE id_veiculo_tempo_real = CAST(? AS UNIQUEIDENTIFIER)")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("motivo", rs.getString("motivo"));
                    o.addProperty("score_minimo", numero(rs.getBigDecimal("score_minimo")));
                    o.addProperty("limiar", numero(rs.getBigDecimal("limiar")));
                    o.addProperty("detalhe", rs.getString("detalhe"));
                    o.addProperty("situacao", rs.getString("situacao"));
                    r.add("revisao", o);
                }
            }
        }
        json(resp, r);
    }

    private void operadores(Connection conn, HttpServletResponse resp) throws Exception {
        JsonArray a = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT u.id_usuario, u.usuario FROM muralha.vtr_revisao_placa r " +
                "JOIN dbo.sis_usuario u ON u.id_usuario = r.id_usuario_tratamento ORDER BY u.usuario");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("id", rs.getInt(1));
                o.addProperty("usuario", rs.getString(2));
                a.add(o);
            }
        }
        JsonObject r = new JsonObject();
        r.add("operadores", a);
        json(resp, r);
    }

    static String uuid(String s) {
        try {
            return UUID.fromString(s.trim()).toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Passagem inválida.");
        }
    }

    private static int definir(PreparedStatement ps, FiltroRevisao f) throws Exception {
        int i = 1;
        for (Object p : f.getParametros()) ps.setObject(i++, p);
        return i;
    }

    private static Double numero(BigDecimal b) { return b == null ? null : b.doubleValue(); }

    private static String aparar(String s) { return s == null || s.trim().isEmpty() ? null : s.trim(); }

    private static String formatar(SimpleDateFormat fmt, Timestamp t) { return t == null ? null : fmt.format(t); }

    private static int inteiro(String s, int padrao, int min, int max) {
        try { return Math.max(min, Math.min(max, Integer.parseInt(s))); } catch (Exception e) { return padrao; }
    }

    private static void json(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(obj));
    }
}
