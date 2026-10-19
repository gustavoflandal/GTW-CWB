package muralha.digital.logtecnico;

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
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Repositório central de logs técnicos (TR 5.4.7) e histórico de parâmetros (5.4.7.6).
 * GET: opcoes, listar, exportarCsv, linhaTempo, correlacionar, relatorioEquipamento, historicoParametros.
 */
@WebServlet("/MuralhaDigital/LogTecnico")
public class LogTecnicoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LogTecnicoServlet.class);
    private static final Gson gson = new Gson();

    private static final int TAMANHO_PAGINA_MAX = 100;
    private static final int MAX_LINHA_TEMPO = 5000;
    private static final int MAX_CORRELACAO = 500;
    private static final int MAX_RELATORIO = 50_000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
        if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
        if (!ControleAcesso.exigir(req, resp)) { return; }

        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            ParametrosLogTecnico p = ParametrosLogTecnico.carregar(conn);
            ServicoLogTecnico servico = new ServicoLogTecnico(conn, p);
            if ("opcoes".equals(acao)) {
                opcoes(conn, servico, resp);
            } else if ("listar".equals(acao)) {
                listar(servico, filtro(req, p), req, resp);
            } else if ("exportarCsv".equals(acao)) {
                FiltroLogTecnico f = filtro(req, p);
                AuditoriaService.registrar(req, "Logs técnicos", "exportar-csv", null, descreverFiltro(f));
                exportarCsv(servico, f, resp);
            } else if ("linhaTempo".equals(acao)) {
                linhaTempo(servico, req, p, resp);
            } else if ("correlacionar".equals(acao)) {
                correlacionar(servico, req, p, resp);
            } else if ("relatorioEquipamento".equals(acao)) {
                relatorio(servico, req, p, resp);
            } else if ("historicoParametros".equals(acao)) {
                historicoParametros(conn, req, p, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
            }
        } catch (IllegalArgumentException e) {
            if (!resp.isCommitted()) resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Erro em LogTecnicoServlet, acao=" + acao, e);
            if (!resp.isCommitted()) resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }

    private static FiltroLogTecnico filtro(HttpServletRequest req, ParametrosLogTecnico p) {
        return FiltroLogTecnico.de(req.getParameter("dtInicio"), req.getParameter("dtFim"), req.getParameter("idLocais"),
            req.getParameter("idLocalidades"), req.getParameter("categorias"), req.getParameter("severidades"),
            p.getPeriodoMaxDias());
    }

    private void opcoes(Connection conn, ServicoLogTecnico servico, HttpServletResponse resp) throws Exception {
        JsonObject r = new JsonObject();
        JsonArray cats = new JsonArray();
        for (CategoriaLog c : CategoriaLog.values()) {
            JsonObject o = new JsonObject();
            o.addProperty("valor", c.name());
            o.addProperty("rotulo", c.getRotulo());
            cats.add(o);
        }
        r.add("categorias", cats);
        JsonArray sevs = new JsonArray();
        for (SeveridadeLog s : SeveridadeLog.values()) sevs.add(s.name());
        r.add("severidades", sevs);
        JsonArray locs = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_localidade, MAX(RTRIM(localidade_desc)) AS descricao FROM local_vigente " +
                "WHERE id_localidade IS NOT NULL GROUP BY id_localidade ORDER BY descricao");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject o = new JsonObject();
                o.addProperty("id_localidade", rs.getInt(1));
                String d = rs.getString(2);
                o.addProperty("descricao", d == null || d.isEmpty() ? "Localidade " + rs.getInt(1) : d);
                locs.add(o);
            }
        }
        r.add("localidades", locs);
        r.addProperty("periodo_max_dias", servico.getParametros().getPeriodoMaxDias());
        r.addProperty("janela_correlacao_min", servico.getParametros().getJanelaCorrelacaoMin());
        r.add("fontes_ausentes", gson.toJsonTree(servico.getFontesAusentes()));
        json(resp, r);
    }

    private void listar(ServicoLogTecnico servico, FiltroLogTecnico f, HttpServletRequest req,
            HttpServletResponse resp) throws Exception {
        int pagina = inteiro(req.getParameter("pagina"), 1, 1, 10_000);
        int tamanho = inteiro(req.getParameter("tamanho"), 50, 1, TAMANHO_PAGINA_MAX);
        ServicoLogTecnico.Pagina pg = servico.listar(f, pagina, tamanho);
        Map<Integer, String> nomes = servico.nomesEquipamentos();
        JsonObject r = new JsonObject();
        r.addProperty("total", pg.total);
        r.addProperty("pagina", pagina);
        r.addProperty("tamanho", tamanho);
        r.addProperty("profundidade_max", ServicoLogTecnico.MAX_PROFUNDIDADE);
        r.add("linhas", linhas(pg.registros, nomes));
        r.add("fontes_ausentes", gson.toJsonTree(servico.getFontesAusentes()));
        json(resp, r);
    }

    private void exportarCsv(ServicoLogTecnico servico, FiltroLogTecnico f, HttpServletResponse resp) throws Exception {
        int max = servico.getParametros().getExportacaoMax();
        List<RegistroLogTecnico> regs = servico.buscar(f, max, false);
        Map<Integer, String> nomes = servico.nomesEquipamentos();

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"logs_tecnicos_" + ts + ".csv\"");
        PrintWriter w = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8));
        w.print('﻿');
        w.println("# GTW-CWB — Logs técnicos");
        w.println("# Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        w.println("# Filtro: " + descreverFiltro(f));
        w.println("Data/hora;Fim;Equipamento;ID local;Origem;Categoria;Severidade;Descrição;Usuário;Referência");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (RegistroLogTecnico r : regs) {
            String linha = String.join(";", csv(formatar(fmt, r.getDataHora())), csv(formatar(fmt, r.getDataHoraFim())),
                csv(r.getIdLocal() == null ? "" : nomes.get(r.getIdLocal())), csv(r.getIdLocal() == null ? "" : r.getIdLocal().toString()),
                csv(r.getOrigem()), csv(r.getCategoria().getRotulo()), csv(r.getSeveridade().name()),
                csv(r.getDescricao()), csv(r.getUsuario()), csv(r.getIdReferencia()));
            md.update((linha + "\n").getBytes(StandardCharsets.UTF_8));
            w.println(linha);
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : md.digest()) hex.append(String.format("%02x", b));
        w.println("# Total de registros: " + regs.size() + (regs.size() >= max ? " (limite atingido; reduza o período)" : ""));
        if (!servico.getFontesAusentes().isEmpty()) w.println("# Fontes ausentes neste banco: " + servico.getFontesAusentes());
        w.println("# SHA-256: " + hex);
        w.flush();
    }

    /** Eventos e períodos de um equipamento, em ordem crescente (TR 5.4.7.5 b). */
    private void linhaTempo(ServicoLogTecnico servico, HttpServletRequest req, ParametrosLogTecnico p,
            HttpServletResponse resp) throws Exception {
        FiltroLogTecnico f = filtroEquipamento(req, p);
        List<RegistroLogTecnico> regs = servico.buscar(f, MAX_LINHA_TEMPO, true);
        Map<Integer, String> nomes = servico.nomesEquipamentos();
        JsonObject r = new JsonObject();
        r.addProperty("inicio", f.getInicio().getTime());
        r.addProperty("fim", f.getFim().getTime());
        r.add("eventos", linhas(regs, nomes));
        r.addProperty("limitado", regs.size() >= MAX_LINHA_TEMPO);
        JsonArray ints = new JsonArray();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (CorrelacionadorIntervalos.Intervalo i : CorrelacionadorIntervalos.montar(regs)) {
            JsonObject o = new JsonObject();
            o.addProperty("grupo", i.getGrupo());
            o.addProperty("origem", i.getOrigem());
            o.addProperty("categoria", i.getCategoria().name());
            o.addProperty("inicio", i.getInicio().getTime());
            o.addProperty("fim", i.getFim() == null ? null : i.getFim().getTime());
            o.addProperty("inicio_formatado", fmt.format(i.getInicio()));
            o.addProperty("fim_formatado", i.getFim() == null ? null : fmt.format(i.getFim()));
            o.addProperty("duracao_min", i.duracaoMs(f.getFim()) / 60000);
            o.addProperty("descricao_inicio", i.getDescricaoInicio());
            o.addProperty("descricao_fim", i.getDescricaoFim());
            ints.add(o);
        }
        r.add("intervalos", ints);
        json(resp, r);
    }

    /** Eventos de todos os equipamentos na janela em torno de um instante (correlação temporal). */
    private void correlacionar(ServicoLogTecnico servico, HttpServletRequest req, ParametrosLogTecnico p,
            HttpServletResponse resp) throws Exception {
        Timestamp t;
        try {
            t = Timestamp.valueOf(LocalDateTime.parse(req.getParameter("dataHora")));
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Data/hora inválida.");
        }
        int janela = inteiro(req.getParameter("janelaMin"), p.getJanelaCorrelacaoMin(), 1, 24 * 60);
        FiltroLogTecnico f = new FiltroLogTecnico(new Timestamp(t.getTime() - janela * 60_000L),
            new Timestamp(t.getTime() + janela * 60_000L + 1), Collections.emptySet(), Collections.emptySet(), null, null);
        List<RegistroLogTecnico> regs = servico.buscar(f, MAX_CORRELACAO, true);
        JsonObject r = new JsonObject();
        r.addProperty("janela_min", janela);
        r.add("eventos", linhas(regs, servico.nomesEquipamentos()));
        r.addProperty("limitado", regs.size() >= MAX_CORRELACAO);
        json(resp, r);
    }

    /** Relatório técnico de um equipamento no período (TR 5.4.7.5 d). */
    private void relatorio(ServicoLogTecnico servico, HttpServletRequest req, ParametrosLogTecnico p,
            HttpServletResponse resp) throws Exception {
        FiltroLogTecnico f = filtroEquipamento(req, p);
        List<RegistroLogTecnico> regs = servico.buscar(f, MAX_RELATORIO, true);
        RelatorioTecnico rel = RelatorioTecnico.montar(regs, f.getInicio(), f.getFim());
        Map<Integer, String> nomes = servico.nomesEquipamentos();
        Integer idLocal = f.getIdLocais().iterator().next();

        JsonObject r = new JsonObject();
        r.addProperty("id_local", idLocal);
        r.addProperty("equipamento", nomes.get(idLocal));
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        r.addProperty("periodo", fmt.format(f.getInicio()) + " a " + fmt.format(new Timestamp(f.getFim().getTime() - 60_000)));
        r.addProperty("total", rel.getTotal());
        r.addProperty("limitado", regs.size() >= MAX_RELATORIO);
        JsonArray cont = new JsonArray();
        for (Map.Entry<CategoriaLog, Map<SeveridadeLog, Integer>> e : rel.getContagem().entrySet()) {
            JsonObject o = new JsonObject();
            o.addProperty("categoria", e.getKey().getRotulo());
            for (SeveridadeLog s : SeveridadeLog.values()) o.addProperty(s.name(), e.getValue().getOrDefault(s, 0));
            cont.add(o);
        }
        r.add("contagem", cont);
        JsonArray grupos = new JsonArray();
        for (RelatorioTecnico.ResumoGrupo g : rel.getGrupos().values()) {
            JsonObject o = new JsonObject();
            o.addProperty("grupo", g.grupo);
            o.addProperty("quantidade", g.quantidade);
            o.addProperty("duracao_min", g.duracaoMs / 60000);
            o.addProperty("em_aberto", g.emAberto);
            grupos.add(o);
        }
        r.add("periodos", grupos);
        r.add("parametros", linhas(rel.getParametros(), nomes));
        r.add("ocorrencias", linhas(rel.getOcorrencias(), nomes));
        AuditoriaService.registrar(req, "Logs técnicos", "relatorio-equipamento", String.valueOf(idLocal), r.get("periodo").getAsString());
        json(resp, r);
    }

    private static FiltroLogTecnico filtroEquipamento(HttpServletRequest req, ParametrosLogTecnico p) {
        String idLocal = req.getParameter("idLocal");
        if (idLocal == null || !idLocal.trim().matches("\\d{1,9}")) {
            throw new IllegalArgumentException("Selecione um equipamento.");
        }
        return FiltroLogTecnico.de(req.getParameter("dtInicio"), req.getParameter("dtFim"), idLocal.trim(), null,
            req.getParameter("categorias"), req.getParameter("severidades"), p.getPeriodoMaxDias());
    }

    /** Histórico de parâmetros com valor anterior e novo (muralha.parametro_historico). */
    private void historicoParametros(Connection conn, HttpServletRequest req, ParametrosLogTecnico p,
            HttpServletResponse resp) throws Exception {
        FiltroLogTecnico f = FiltroLogTecnico.de(req.getParameter("dtInicio"), req.getParameter("dtFim"), null, null,
            null, null, p.getPeriodoMaxDias());
        int pagina = inteiro(req.getParameter("pagina"), 1, 1, 100_000);
        int tamanho = inteiro(req.getParameter("tamanho"), 50, 1, TAMANHO_PAGINA_MAX);
        FonteLog.Where w = new FonteLog.Where().e("ph.dt >= ?", f.getInicio()).e("ph.dt < ?", f.getFim());
        String tipo = req.getParameter("tipoRecurso");
        if (tipo != null && !tipo.trim().isEmpty()) w.e("ph.tipo_recurso = ?", tipo.trim());
        String idRecurso = req.getParameter("idRecurso");
        if (idRecurso != null && !idRecurso.trim().isEmpty()) w.e("ph.id_recurso = ?", idRecurso.trim());
        String parametro = req.getParameter("parametro");
        if (parametro != null && !parametro.trim().isEmpty()) {
            w.e("ph.parametro LIKE ?", "%" + parametro.trim().replace("[", "[[]").replace("%", "[%]").replace("_", "[_]") + "%");
        }
        FonteLog.Consulta c = w.montar("FROM muralha.parametro_historico ph");

        JsonObject r = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement("SELECT OBJECT_ID('muralha.parametro_historico', 'U')");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next() || rs.getObject(1) == null) {
                r.addProperty("total", 0);
                r.add("linhas", new JsonArray());
                r.addProperty("aviso", "Tabela muralha.parametro_historico não existe: execute a migração do Plano 26.");
                json(resp, r);
                return;
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) " + c.sql)) {
            ServicoLogTecnico.definir(ps, c.parametros);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); r.addProperty("total", rs.getLong(1)); }
        }
        JsonArray linhas = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ph.id, ph.dt, ph.tipo_recurso, ph.id_recurso, ph.parametro, ph.valor_anterior, ph.valor_novo, " +
                "ph.login, ph.ip, ph.origem " + c.sql + " ORDER BY ph.dt DESC, ph.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY")) {
            ServicoLogTecnico.definir(ps, c.parametros);
            ps.setInt(c.parametros.size() + 1, (pagina - 1) * tamanho);
            ps.setInt(c.parametros.size() + 2, tamanho);
            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                while (rs.next()) {
                    JsonObject o = new JsonObject();
                    o.addProperty("data_hora_formatado", formatar(fmt, rs.getTimestamp("dt")));
                    o.addProperty("tipo_recurso", rs.getString("tipo_recurso"));
                    o.addProperty("id_recurso", rs.getString("id_recurso"));
                    o.addProperty("parametro", rs.getString("parametro"));
                    o.addProperty("valor_anterior", rs.getString("valor_anterior"));
                    o.addProperty("valor_novo", rs.getString("valor_novo"));
                    o.addProperty("usuario", rs.getString("login"));
                    o.addProperty("ip", rs.getString("ip"));
                    o.addProperty("origem", rs.getString("origem"));
                    linhas.add(o);
                }
            }
        }
        r.add("linhas", linhas);
        r.addProperty("pagina", pagina);
        r.addProperty("tamanho", tamanho);
        json(resp, r);
    }

    // ------------------------------------------------------------------ utilitários

    static JsonArray linhas(List<RegistroLogTecnico> regs, Map<Integer, String> nomes) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter iso = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        JsonArray a = new JsonArray();
        for (RegistroLogTecnico r : regs) {
            JsonObject o = new JsonObject();
            o.addProperty("data_hora_formatado", formatar(fmt, r.getDataHora()));
            o.addProperty("data_hora_iso", r.getDataHora() == null ? null : r.getDataHora().toLocalDateTime().format(iso));
            o.addProperty("data_hora_ms", r.getDataHora() == null ? null : r.getDataHora().getTime());
            o.addProperty("fim_formatado", formatar(fmt, r.getDataHoraFim()));
            o.addProperty("id_local", r.getIdLocal());
            o.addProperty("equipamento", r.getIdLocal() == null ? null : nomes.getOrDefault(r.getIdLocal(), "Local " + r.getIdLocal()));
            o.addProperty("origem", r.getOrigem());
            o.addProperty("categoria", r.getCategoria().name());
            o.addProperty("categoria_rotulo", r.getCategoria().getRotulo());
            o.addProperty("severidade", r.getSeveridade().name());
            o.addProperty("descricao", r.getDescricao());
            o.addProperty("usuario", r.getUsuario());
            o.addProperty("id_referencia", r.getIdReferencia());
            o.addProperty("transicao", r.getTransicao() == null ? null : r.getTransicao().name());
            o.addProperty("grupo", r.getGrupo());
            a.add(o);
        }
        return a;
    }

    static String descreverFiltro(FiltroLogTecnico f) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return "Período " + fmt.format(f.getInicio()) + " a " + fmt.format(new Timestamp(f.getFim().getTime() - 1))
            + (f.getIdLocais().isEmpty() ? "" : "; equipamentos " + f.getIdLocais())
            + (f.getIdLocalidades().isEmpty() ? "" : "; localidades " + f.getIdLocalidades())
            + (f.getCategorias().isEmpty() ? "" : "; categorias " + f.getCategorias())
            + (f.getSeveridades().isEmpty() ? "" : "; severidades " + f.getSeveridades());
    }

    private static String formatar(SimpleDateFormat fmt, Timestamp t) {
        return t == null ? null : fmt.format(t);
    }

    static String csv(String v) {
        if (v == null) return "";
        String s = v.replace('\r', ' ').replace('\n', ' ');
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) s = "'" + s;
        if (s.contains(";") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
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
