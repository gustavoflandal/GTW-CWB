package muralha.digital.ocr;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import muralha.digital.logtecnico.AlteracaoParametro;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuração do score de OCR (TR 5.7.4.2): origem do score e limiar, padrão e por equipamento.
 * GET listar; POST salvarPadrao, salvarEquipamento. Alterações vão para parametro_historico.
 */
@WebServlet("/MuralhaDigital/OcrConfig")
public class OcrConfigServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(OcrConfigServlet.class);
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }
        if (!"listar".equals(req.getParameter("acao"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            return;
        }
        try (Connection conn = Conexao.getConexao()) {
            ParametrosOcr p = ParametrosOcr.carregar(conn);
            JsonObject r = new JsonObject();
            r.addProperty("origem_padrao", p.getOrigemPadrao().getTipo().name());
            r.addProperty("campo_padrao", p.getOrigemPadrao().getCampo());
            r.addProperty("limiar", p.getLimiar());
            r.addProperty("escala", p.getEscala());
            r.addProperty("revisar_sem_score", p.isRevisarSemScore());
            r.addProperty("revisar_sem_leitura", p.isRevisarSemLeitura());
            r.addProperty("janela_horas", p.getJanelaHoras());
            r.addProperty("lote", p.getLote());
            r.addProperty("espera_score_min", p.getEsperaScoreMin());
            JsonArray eqs = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT lv.id_local, RTRIM(lv.nome) AS nome, RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) AS serie, " +
                    "       c.origem_score, c.campo, c.limiar, c.ativo " +
                    "FROM local_vigente lv LEFT JOIN muralha.equipamento_ocr_config c ON c.id_local = lv.id_local ORDER BY lv.nome");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    boolean ativo = rs.getObject("ativo") != null && rs.getBoolean("ativo");
                    JsonObject o = new JsonObject();
                    o.addProperty("id_local", rs.getInt("id_local"));
                    o.addProperty("equipamento", rs.getString("nome"));
                    o.addProperty("serie", rs.getString("serie"));
                    o.addProperty("origem_score", ativo ? rs.getString("origem_score") : null);
                    o.addProperty("campo", ativo ? rs.getString("campo") : null);
                    BigDecimal l = rs.getBigDecimal("limiar");
                    o.addProperty("limiar", ativo && l != null ? l.doubleValue() : null);
                    eqs.add(o);
                }
            }
            r.add("equipamentos", eqs);
            json(resp, r);
        } catch (Exception e) {
            logger.error("Erro ao listar a configuração de OCR", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }

        String acao = req.getParameter("acao");
        ContextoAlteracao ctx = ContextoAlteracao.de(req, "Configuracao de score de OCR");
        try (Connection conn = Conexao.getConexao()) {
            if ("salvarPadrao".equals(acao)) {
                OrigemScore origem = OrigemScore.de(OrigemScore.tipoDe(req.getParameter("origem")), req.getParameter("campo"));
                Double limiar = limiar(req.getParameter("limiar"), false);
                ConfigChaveValor.gravar(conn, ParametrosOcr.CHAVE_ORIGEM, origem.formatar(), ctx);
                ConfigChaveValor.gravar(conn, ParametrosOcr.CHAVE_LIMIAR, RegraRevisao.formatar(limiar), ctx);
                AuditoriaService.registrar(req, "Score de OCR", "config-padrao", null,
                    "Origem " + origem.formatar() + ", limiar " + RegraRevisao.formatar(limiar));
                json(resp, new Resultado(true, "Configuração padrão salva."));
            } else if ("salvarEquipamento".equals(acao)) {
                int idLocal;
                try { idLocal = Integer.parseInt(req.getParameter("idLocal")); }
                catch (NumberFormatException e) { throw new IllegalArgumentException("Equipamento inválido."); }
                String tipo = req.getParameter("origem");
                OrigemScore origem = tipo == null || tipo.trim().isEmpty() ? null
                    : OrigemScore.de(OrigemScore.tipoDe(tipo), req.getParameter("campo"));
                Double limiar = limiar(req.getParameter("limiar"), true);
                Map<String, String> antes = atual(conn, idLocal);
                try (PreparedStatement ps = conn.prepareStatement(
                        "MERGE muralha.equipamento_ocr_config AS t " +
                        "USING (SELECT ? id_local, ? origem_score, ? campo, ? limiar, ? id_usuario) AS s ON t.id_local = s.id_local " +
                        "WHEN MATCHED THEN UPDATE SET origem_score = s.origem_score, campo = s.campo, limiar = s.limiar, ativo = 1, " +
                        "  id_usuario = s.id_usuario, data_modificacao = GETDATE() " +
                        "WHEN NOT MATCHED THEN INSERT (id_local, origem_score, campo, limiar, ativo, id_usuario, data_modificacao) " +
                        "VALUES (s.id_local, s.origem_score, s.campo, s.limiar, 1, s.id_usuario, GETDATE());")) {
                    ps.setInt(1, idLocal);
                    ps.setString(2, origem == null ? null : origem.getTipo().name());
                    ps.setString(3, origem == null ? null : origem.getCampo());
                    if (limiar == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, BigDecimal.valueOf(limiar));
                    ps.setObject(5, usuario.getId(), Types.INTEGER);
                    ps.executeUpdate();
                }
                Map<String, String> depois = new LinkedHashMap<>();
                depois.put("ocr.origem_score", origem == null ? null : origem.formatar());
                depois.put("ocr.limiar", limiar == null ? null : RegraRevisao.formatar(limiar));
                List<AlteracaoParametro> alts = HistoricoParametros.comparar(antes, depois);
                HistoricoParametros.registrarSemFalhar(conn, HistoricoParametros.RECURSO_EQUIPAMENTO, String.valueOf(idLocal), alts, ctx);
                AuditoriaService.registrar(req, "Score de OCR", "config-equipamento", String.valueOf(idLocal), alts.toString());
                json(resp, new Resultado(true, "Configuração do equipamento salva."));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (IllegalArgumentException e) {
            json(resp, new Resultado(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao salvar a configuração de OCR, acao=" + acao, e);
            json(resp, new Resultado(false, "Erro ao salvar a configuração."));
        }
    }

    /** Limiar 0-100; vazio só é aceito quando {@code opcional} (= usa o padrão). */
    static Double limiar(String s, boolean opcional) {
        if (s == null || s.trim().isEmpty()) {
            if (opcional) return null;
            throw new IllegalArgumentException("Informe o limiar (0 a 100).");
        }
        double v;
        try { v = Double.parseDouble(s.trim().replace(',', '.')); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Limiar inválido."); }
        if (!ParametrosOcr.limiarValido(v)) throw new IllegalArgumentException("O limiar deve estar entre 0 e 100.");
        return v;
    }

    private static Map<String, String> atual(Connection conn, int idLocal) throws Exception {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("ocr.origem_score", null);
        m.put("ocr.limiar", null);
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT origem_score, campo, limiar FROM muralha.equipamento_ocr_config WHERE id_local = ? AND ativo = 1")) {
            ps.setInt(1, idLocal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String o = rs.getString("origem_score");
                    m.put("ocr.origem_score", o == null ? null : (rs.getString("campo") == null ? o : o + ":" + rs.getString("campo")));
                    BigDecimal l = rs.getBigDecimal("limiar");
                    m.put("ocr.limiar", l == null ? null : RegraRevisao.formatar(l.doubleValue()));
                }
            }
        }
        return m;
    }

    private static void json(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(obj));
    }
}
