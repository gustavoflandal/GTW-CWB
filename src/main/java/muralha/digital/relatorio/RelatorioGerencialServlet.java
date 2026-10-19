package muralha.digital.relatorio;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import muralha.digital.dashboards.ExportacaoService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

@WebServlet("/MuralhaDigital/RelatorioGerencial")
public class RelatorioGerencialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "";
        boolean tela = acao.isEmpty();
        if (tela ? !ControleAcesso.exigirTela(req, resp) : !ControleAcesso.exigir(req, resp)) return;

        switch (acao) {
            case "listar": listar(req, resp); break;
            case "detalhe": detalhe(req, resp); break;
            case "exportarCsv": exportar(req, resp, "csv"); break;
            case "exportarXlsx": exportar(req, resp, "xlsx"); break;
            default:
                req.setAttribute("pageTitle", "Relatórios Gerenciais");
                try {
                    req.getRequestDispatcher("/muralha-digital/pages/relatorios/gerencial/index.jsp")
                       .forward(req, resp);
                } catch (Exception e) {
                    resp.sendError(500, e.getMessage());
                }
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String tipo = req.getParameter("tipo");
            String sql = "SELECT id, tipo, titulo, periodo_inicio, periodo_fim, dt_geracao, gerado_por " +
                    "FROM muralha.relatorio_periodico";
            if (tipo != null && !tipo.isEmpty()) sql += " WHERE tipo = ?";
            sql += " ORDER BY dt_geracao DESC";

            JsonArray relatorios = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (tipo != null && !tipo.isEmpty()) ps.setString(1, tipo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject r = new JsonObject();
                        r.addProperty("id", rs.getLong("id"));
                        r.addProperty("tipo", rs.getString("tipo"));
                        r.addProperty("titulo", rs.getString("titulo"));
                        r.addProperty("periodoInicio", rs.getString("periodo_inicio"));
                        r.addProperty("periodoFim", rs.getString("periodo_fim"));
                        r.addProperty("dtGeracao", rs.getString("dt_geracao"));
                        r.addProperty("geradoPor", rs.getString("gerado_por"));
                        relatorios.add(r);
                    }
                }
            }
            result.addProperty("ok", true);
            result.add("relatorios", relatorios);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(gson.toJson(result));
    }

    private void detalhe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            long id = Long.parseLong(req.getParameter("id"));
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, tipo, titulo, periodo_inicio, periodo_fim, dados_json, dt_geracao, gerado_por " +
                    "FROM muralha.relatorio_periodico WHERE id = ?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.addProperty("ok", true);
                        result.addProperty("id", rs.getLong("id"));
                        result.addProperty("tipo", rs.getString("tipo"));
                        result.addProperty("titulo", rs.getString("titulo"));
                        result.addProperty("periodoInicio", rs.getString("periodo_inicio"));
                        result.addProperty("periodoFim", rs.getString("periodo_fim"));
                        result.addProperty("dtGeracao", rs.getString("dt_geracao"));
                        result.addProperty("geradoPor", rs.getString("gerado_por"));
                        result.add("dados", JsonParser.parseString(rs.getString("dados_json")));
                    } else {
                        result.addProperty("ok", false);
                        result.addProperty("erro", "Relatório não encontrado");
                    }
                }
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(gson.toJson(result));
    }

    private void exportar(HttpServletRequest req, HttpServletResponse resp, String formato) throws IOException {
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            long id = Long.parseLong(req.getParameter("id"));
            String dadosJson;
            String titulo;
            String tipo;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT tipo, titulo, dados_json FROM muralha.relatorio_periodico WHERE id = ?")) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { resp.sendError(404); return; }
                    tipo = rs.getString("tipo");
                    titulo = rs.getString("titulo");
                    dadosJson = rs.getString("dados_json");
                }
            }

            JsonObject dados = JsonParser.parseString(dadosJson).getAsJsonObject();
            JsonArray linhas = montarLinhasExport(dados, tipo);
            List<String> cabecalhos = Arrays.asList("Metrica", "Valor", "Unidade");

            ExportacaoService svc = new ExportacaoService();
            byte[] bytes;
            String nomeArquivo;
            String contentType;

            if ("xlsx".equals(formato)) {
                bytes = svc.exportarXlsx(linhas, cabecalhos, cabecalhos, titulo);
                nomeArquivo = sanitizar(titulo) + ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else {
                bytes = svc.exportarCsv(linhas, cabecalhos, ";");
                nomeArquivo = sanitizar(titulo) + ".csv";
                contentType = "text/csv; charset=UTF-8";
            }

            resp.setContentType(contentType);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
            resp.getOutputStream().write(bytes);
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private JsonArray montarLinhasExport(JsonObject dados, String tipo) {
        JsonArray linhas = new JsonArray();
        if ("PRE_PROCESSAMENTO".equals(tipo)) {
            addLinha(linhas, "Recebidos", dados, "recebidos", "registros");
            addLinha(linhas, "Processados", dados, "processados", "registros");
            addLinha(linhas, "Disponibilizados", dados, "disponibilizados", "registros");
            addLinha(linhas, "Descartados", dados, "descartados", "registros");
            addLinha(linhas, "Índice de Aproveitamento", dados, "indiceAproveitamento", "%");
            addLinha(linhas, "Tempo Médio de Processamento", dados, "tempoMedioProcessamentoSeg", "seg");
            if (dados.has("pendentesEBacklog")) {
                JsonObject pb = dados.getAsJsonObject("pendentesEBacklog");
                addLinha(linhas, "Pendentes", pb, "pendentes", "registros");
                addLinha(linhas, "Backlog > 72h", pb, "backlog72h", "registros");
            }
            if (dados.has("sla")) {
                JsonObject sla = dados.getAsJsonObject("sla");
                addLinha(linhas, "SLA - Cumprimento", sla, "cumprimento", "%");
                addLinha(linhas, "SLA - Violações", sla, "violacoes", "janelas");
            }
        } else {
            if (dados.has("volumeTrafego")) {
                JsonObject vt = dados.getAsJsonObject("volumeTrafego");
                addLinha(linhas, "Total de Passagens", vt, "totalPassagens", "registros");
                addLinha(linhas, "Locais Ativos", vt, "locaisAtivos", "locais");
                addLinha(linhas, "Tráfego Médio/Hora", vt, "trafegoMedioHora", "veículos/h");
            }
            if (dados.has("disponibilidade")) {
                JsonObject disp = dados.getAsJsonObject("disponibilidade");
                addLinha(linhas, "Equipamentos Ativos", disp, "equipamentosAtivos", "equip.");
                addLinha(linhas, "Total Equipamentos", disp, "totalEquipamentos", "equip.");
            }
            if (dados.has("resumoKpis") && dados.get("resumoKpis").isJsonArray()) {
                for (var el : dados.getAsJsonArray("resumoKpis")) {
                    JsonObject kpi = el.getAsJsonObject();
                    String nome = kpi.get("nome").getAsString();
                    addLinha(linhas, nome + " (média)", kpi, "media", kpi.has("unidade") ? kpi.get("unidade").getAsString() : "");
                }
            }
        }
        return linhas;
    }

    private void addLinha(JsonArray linhas, String metrica, JsonObject obj, String campo, String unidade) {
        JsonObject linha = new JsonObject();
        linha.addProperty("Metrica", metrica);
        linha.addProperty("Valor", obj.has(campo) ? obj.get(campo).getAsString() : "—");
        linha.addProperty("Unidade", unidade);
        linhas.add(linha);
    }

    private String sanitizar(String nome) {
        return nome.replaceAll("[^a-zA-Z0-9_\\-àáâãçéêíóôõú ]", "").replaceAll("\\s+", "_");
    }
}
