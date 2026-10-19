package muralha.digital.integridade;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.consilux.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Consulta da conferência de integridade das imagens recebidas (TR 5.2.1.2 e 5.2.1.3 a-f).
 * Ações (GET): resumo, listar, exportarCsv.
 */
@WebServlet("/MuralhaDigital/IntegridadeRecepcao")
public class IntegridadeRecepcaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(IntegridadeRecepcaoServlet.class);
    private static final Gson gson = new Gson();

    private static final int MAX_LINHAS_RESUMO = 5000;
    private static final int MAX_LINHAS_CSV = 50000;
    private static final int TAMANHO_PAGINA_MAX = 100;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!ControleAcesso.exigir(req, resp)) { return; }

        String acao = req.getParameter("acao");
        FiltroIntegridade filtro;
        try {
            filtro = FiltroIntegridade.de(req.getParameter("dtInicio"), req.getParameter("dtFim"),
                req.getParameter("idLocais"), req.getParameter("status"));
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        try {
            if ("resumo".equals(acao)) {
                resumo(filtro, resp);
            } else if ("listar".equals(acao)) {
                listar(filtro, req, resp);
            } else if ("exportarCsv".equals(acao)) {
                AuditoriaService.registrar(req, "Integridade de imagens", "exportar-csv", null,
                    "Período " + filtro.getInicio() + " a " + filtro.getFim()
                        + (filtro.getStatus() != null ? ", status " + filtro.getStatus() : "")
                        + (filtro.getIdLocais().isEmpty() ? "" : ", equipamentos " + filtro.getIdLocais()));
                exportarCsv(filtro, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (Exception e) {
            logger.error("Erro em IntegridadeRecepcaoServlet, acao=" + acao, e);
            if (!resp.isCommitted()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
            }
        }
    }

    /** Totais do período e contadores por equipamento e dia (view muralha.v_integridade_recepcao). */
    private void resumo(FiltroIntegridade filtro, HttpServletResponse resp) throws Exception {
        FiltroIntegridade.Clausula c = filtro.clausula("r.dia", "r.id_local", null);
        String sql =
            "SELECT TOP (" + MAX_LINHAS_RESUMO + ") r.id_local, RTRIM(lv.nome) AS nome, r.dia, r.recebidos, r.integros, " +
            "       r.divergentes, r.sem_conferencia, r.erro_leitura, r.rejeitados, r.ultima_verificacao " +
            "FROM muralha.v_integridade_recepcao r " +
            "LEFT JOIN local_vigente lv ON lv.id_local = r.id_local " +
            "WHERE " + c.sql + " ORDER BY r.dia DESC, lv.nome";

        JsonObject res = new JsonObject();
        JsonArray linhas = new JsonArray();
        long recebidos = 0, integros = 0, divergentes = 0, semConferencia = 0, erroLeitura = 0;
        Timestamp ultima = null;
        SimpleDateFormat fmtDia = new SimpleDateFormat("dd/MM/yyyy");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            definir(ps, c.parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("id_local", rs.getInt("id_local"));
                    o.addProperty("equipamento", rs.getString("nome"));
                    o.addProperty("dia_formatado", fmtDia.format(rs.getDate("dia")));
                    o.addProperty("recebidos", rs.getLong("recebidos"));
                    o.addProperty("integros", rs.getLong("integros"));
                    o.addProperty("divergentes", rs.getLong("divergentes"));
                    o.addProperty("sem_conferencia", rs.getLong("sem_conferencia"));
                    o.addProperty("erro_leitura", rs.getLong("erro_leitura"));
                    o.addProperty("rejeitados", rs.getLong("rejeitados"));
                    linhas.add(o);
                    recebidos += rs.getLong("recebidos");
                    integros += rs.getLong("integros");
                    divergentes += rs.getLong("divergentes");
                    semConferencia += rs.getLong("sem_conferencia");
                    erroLeitura += rs.getLong("erro_leitura");
                    Timestamp u = rs.getTimestamp("ultima_verificacao");
                    if (u != null && (ultima == null || u.after(ultima))) ultima = u;
                }
            }
        }
        JsonObject totais = new JsonObject();
        totais.addProperty("recebidos", recebidos);
        totais.addProperty("integros", integros);
        totais.addProperty("divergentes", divergentes);
        totais.addProperty("sem_conferencia", semConferencia);
        totais.addProperty("erro_leitura", erroLeitura);
        totais.addProperty("rejeitados", divergentes + erroLeitura);
        totais.addProperty("ultima_verificacao_formatado",
            ultima == null ? null : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(ultima));
        res.add("totais", totais);
        res.add("linhas", linhas);
        res.addProperty("limitado", linhas.size() >= MAX_LINHAS_RESUMO);
        json(resp, res);
    }

    /** Registros conferidos, paginados no servidor. */
    private void listar(FiltroIntegridade filtro, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int pagina = inteiro(req.getParameter("pagina"), 1, 1, 100000);
        int tamanho = inteiro(req.getParameter("tamanho"), 50, 1, TAMANHO_PAGINA_MAX);
        FiltroIntegridade.Clausula c = filtro.clausula("g.dt_captura", "g.id_local", "g.status_integridade");

        JsonObject res = new JsonObject();
        try (Connection conn = Conexao.getConexao()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM muralha.vtr_imagem_integridade g WHERE " + c.sql)) {
                definir(ps, c.parametros);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    res.addProperty("total", rs.getLong(1));
                }
            }
            String sql = sqlDetalhe(c) + " ORDER BY g.dt_captura DESC, g.id_imagem " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            JsonArray linhas = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int i = definir(ps, c.parametros);
                ps.setInt(i++, (pagina - 1) * tamanho);
                ps.setInt(i, tamanho);
                try (ResultSet rs = ps.executeQuery()) {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    while (rs.next()) linhas.add(linhaDetalhe(rs, fmt));
                }
            }
            res.add("linhas", linhas);
        }
        res.addProperty("pagina", pagina);
        res.addProperty("tamanho", tamanho);
        json(resp, res);
    }

    private void exportarCsv(FiltroIntegridade filtro, HttpServletResponse resp) throws Exception {
        FiltroIntegridade.Clausula c = filtro.clausula("g.dt_captura", "g.id_local", "g.status_integridade");
        String sql = sqlDetalhe(c).replaceFirst("SELECT ", "SELECT TOP (" + MAX_LINHAS_CSV + ") ")
            + " ORDER BY g.dt_captura, g.id_imagem";

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"integridade_imagens_" + ts + ".csv\"");
        PrintWriter w = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8));
        w.print('﻿');
        w.println("# GTW-CWB — Integridade das imagens recebidas (SHA-256)");
        w.println("# Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        w.println("# Período: " + filtro.getInicio() + " a " + filtro.getFim()
            + (filtro.getStatus() != null ? "; status " + filtro.getStatus() : ""));
        w.println("Equipamento;ID local;Captura;Recepção;Verificação;Imagem;Status;Origem do hash;"
            + "SHA-256 calculado;Hash de origem;Motivo;ID imagem;ID passagem");

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int total = 0;
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            definir(ps, c.parametros);
            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                while (rs.next()) {
                    JsonObject o = linhaDetalhe(rs, fmt);
                    String linha = String.join(";",
                        csv(o, "equipamento"), csv(o, "id_local"), csv(o, "dt_captura_formatado"),
                        csv(o, "dt_recepcao_formatado"), csv(o, "dt_verificacao_formatado"), csv(o, "indice_imagem"),
                        csv(o, "status_integridade"), csv(o, "origem_hash"), csv(o, "sha256_calculado"),
                        csv(o, "hash_origem"), csv(o, "motivo"), csv(o, "id_imagem"), csv(o, "id_veiculo_tempo_real"));
                    md.update((linha + "\n").getBytes(StandardCharsets.UTF_8));
                    w.println(linha);
                    total++;
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao exportar CSV de integridade", e);
            w.println("# ERRO: a exportação foi interrompida.");
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : md.digest()) hex.append(String.format("%02x", b));
        w.println("# Total de registros: " + total + (total >= MAX_LINHAS_CSV ? " (limite atingido; reduza o período)" : ""));
        w.println("# SHA-256: " + hex);
        w.flush();
    }

    private static String sqlDetalhe(FiltroIntegridade.Clausula c) {
        return "SELECT g.id_imagem, g.id_veiculo_tempo_real, g.indice_imagem, g.id_local, RTRIM(lv.nome) AS nome, " +
            "       g.dt_captura, g.dt_recepcao, g.dt_verificacao, g.status_integridade, g.origem_hash, " +
            "       g.sha256_calculado, g.hash_origem, g.motivo " +
            "FROM muralha.vtr_imagem_integridade g " +
            "LEFT JOIN local_vigente lv ON lv.id_local = g.id_local " +
            "WHERE " + c.sql;
    }

    private static JsonObject linhaDetalhe(ResultSet rs, SimpleDateFormat fmt) throws Exception {
        JsonObject o = new JsonObject();
        o.addProperty("id_imagem", rs.getString("id_imagem"));
        o.addProperty("id_veiculo_tempo_real", rs.getString("id_veiculo_tempo_real"));
        o.addProperty("indice_imagem", rs.getInt("indice_imagem"));
        o.addProperty("id_local", rs.getInt("id_local"));
        o.addProperty("equipamento", rs.getString("nome"));
        o.addProperty("dt_captura_formatado", formatar(fmt, rs.getTimestamp("dt_captura")));
        o.addProperty("dt_recepcao_formatado", formatar(fmt, rs.getTimestamp("dt_recepcao")));
        o.addProperty("dt_verificacao_formatado", formatar(fmt, rs.getTimestamp("dt_verificacao")));
        o.addProperty("status_integridade", rs.getString("status_integridade"));
        o.addProperty("origem_hash", rs.getString("origem_hash"));
        o.addProperty("sha256_calculado", rs.getString("sha256_calculado"));
        o.addProperty("hash_origem", rs.getString("hash_origem"));
        o.addProperty("motivo", rs.getString("motivo"));
        return o;
    }

    private static String formatar(SimpleDateFormat fmt, Timestamp t) {
        return t == null ? null : fmt.format(t);
    }

    private static String csv(JsonObject o, String campo) {
        if (!o.has(campo) || o.get(campo).isJsonNull()) return "";
        String v = o.get(campo).getAsString();
        if (v.contains(";") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /** Define os parâmetros e devolve o próximo índice livre. */
    private static int definir(PreparedStatement ps, List<Object> parametros) throws Exception {
        int i = 1;
        for (Object p : parametros) ps.setObject(i++, p);
        return i;
    }

    private static int inteiro(String s, int padrao, int min, int max) {
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(s)));
        } catch (Exception e) {
            return padrao;
        }
    }

    private static void json(HttpServletResponse resp, JsonObject obj) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(obj));
    }
}
