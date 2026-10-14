package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@WebServlet("/api/dash/exportar")
public class ExportacaoDashboardServlet extends HttpServlet {

    private final ExportacaoService exportacaoService = new ExportacaoService();
    private final PivotService pivotService = new PivotService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        String acao = req.getParameter("acao");
        Connection conn = null;

        try {
            conn = Conexao.getConexao();
            int idUsuario = acesso.getUsuario().getId();

            if ("csvWidget".equals(acao)) {
                int idWidget = Integer.parseInt(req.getParameter("id"));
                exportarCsvWidget(conn, resp, idWidget);

            } else if ("xlsxWidget".equals(acao)) {
                int idWidget = Integer.parseInt(req.getParameter("id"));
                exportarXlsxWidget(conn, resp, idWidget);

            } else if ("csvPainel".equals(acao)) {
                int idPainel = Integer.parseInt(req.getParameter("id"));
                exportarCsvPainel(conn, resp, idPainel);

            } else {
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().print("{\"ok\":false,\"erro\":\"acao invalida\"}");
            }

        } catch (Exception e) {
            if (!resp.isCommitted()) {
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().print("{\"ok\":false,\"erro\":\"" +
                        e.getMessage().replace("\"", "'") + "\"}");
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            StringBuilder body = new StringBuilder();
            String line;
            var reader = req.getReader();
            while ((line = reader.readLine()) != null) body.append(line);

            JsonObject dados = JsonParser.parseString(body.toString()).getAsJsonObject();
            String acao = dados.has("acao") ? dados.get("acao").getAsString() : "";

            JsonObject result;
            if ("pivotar".equals(acao)) {
                result = pivotar(dados);
            } else {
                result = new JsonObject();
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }

            resp.getWriter().print(new Gson().toJson(result));
        } catch (Exception e) {
            JsonObject err = new JsonObject();
            err.addProperty("ok", false);
            err.addProperty("erro", e.getMessage());
            resp.getWriter().print(new Gson().toJson(err));
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private JsonObject pivotar(JsonObject dados) {
        JsonArray dadosBrutos = dados.has("dados") ? dados.getAsJsonArray("dados") : new JsonArray();
        JsonObject config = dados.has("config") ? dados.getAsJsonObject("config") : new JsonObject();
        return pivotService.pivotar(dadosBrutos, config);
    }

    private void exportarCsvWidget(Connection conn, HttpServletResponse resp, int idWidget)
            throws Exception {
        WidgetComDados wcd = obterWidgetComDados(conn, idWidget);

        byte[] csv = exportacaoService.exportarCsv(wcd.dados, wcd.cabecalhos, ";");

        String nomeArquivo = sanitizar(wcd.titulo) + ".csv";
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
        resp.setContentLength(csv.length);

        try (OutputStream os = resp.getOutputStream()) {
            os.write(csv);
        }
    }

    private void exportarXlsxWidget(Connection conn, HttpServletResponse resp, int idWidget)
            throws Exception {
        WidgetComDados wcd = obterWidgetComDados(conn, idWidget);

        byte[] xlsx = exportacaoService.exportarXlsx(wcd.dados, wcd.cabecalhos, wcd.rotulos, wcd.titulo);

        String nomeArquivo = sanitizar(wcd.titulo) + ".xlsx";
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
        resp.setContentLength(xlsx.length);

        try (OutputStream os = resp.getOutputStream()) {
            os.write(xlsx);
        }
    }

    private void exportarCsvPainel(Connection conn, HttpServletResponse resp, int idPainel)
            throws Exception {
        String nomePainel = "painel";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome FROM dash.dashboard WHERE id=? AND ativo=1")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nomePainel = rs.getString("nome");
            }
        }

        List<int[]> widgetIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM dash.widget WHERE id_dashboard=? ORDER BY ordem, id")) {
            ps.setInt(1, idPainel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) widgetIds.add(new int[]{rs.getInt("id")});
            }
        }

        Map<String, JsonArray> widgetsDados = new LinkedHashMap<>();
        Map<String, List<String>> widgetsCabecalhos = new LinkedHashMap<>();

        for (int[] wid : widgetIds) {
            try {
                WidgetComDados wcd = obterWidgetComDados(conn, wid[0]);
                widgetsDados.put(wcd.titulo, wcd.dados);
                widgetsCabecalhos.put(wcd.titulo, wcd.cabecalhos);
            } catch (Exception ignored) {}
        }

        byte[] zip = exportacaoService.exportarPainelZip(widgetsDados, widgetsCabecalhos);

        String nomeArquivo = sanitizar(nomePainel) + ".zip";
        resp.setContentType("application/zip");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
        resp.setContentLength(zip.length);

        try (OutputStream os = resp.getOutputStream()) {
            os.write(zip);
        }
    }

    private WidgetComDados obterWidgetComDados(Connection conn, int idWidget) throws Exception {
        WidgetComDados result = new WidgetComDados();

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT w.titulo, w.consulta_json, w.id_dataset " +
                "FROM dash.widget w WHERE w.id=?")) {
            ps.setInt(1, idWidget);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new Exception("Widget nao encontrado");
                result.titulo = rs.getString("titulo");
                result.consultaJson = rs.getString("consulta_json");
                result.idDataset = rs.getInt("id_dataset");
            }
        }

        Map<String, String> aliasRotulo = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome_alias, rotulo FROM dash.dataset_campo WHERE id_dataset=? ORDER BY ordem")) {
            ps.setInt(1, result.idDataset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aliasRotulo.put(rs.getString("nome_alias"), rs.getString("rotulo"));
                }
            }
        }

        JsonObject consulta = JsonParser.parseString(result.consultaJson).getAsJsonObject();

        List<String> cabecalhos = new ArrayList<>();
        if (consulta.has("dimensions")) {
            for (var el : consulta.getAsJsonArray("dimensions")) cabecalhos.add(el.getAsString());
        }
        if (consulta.has("measures")) {
            for (var el : consulta.getAsJsonArray("measures")) cabecalhos.add(el.getAsString());
        }
        if (consulta.has("timeDimensions")) {
            for (var el : consulta.getAsJsonArray("timeDimensions")) {
                String dim = el.getAsJsonObject().get("dimension").getAsString();
                if (!cabecalhos.contains(dim)) cabecalhos.add(dim);
            }
        }

        result.cabecalhos = cabecalhos;
        result.rotulos = new ArrayList<>();
        for (String c : cabecalhos) {
            result.rotulos.add(aliasRotulo.getOrDefault(c, c));
        }

        result.dados = new JsonArray();

        return result;
    }

    private String sanitizar(String nome) {
        if (nome == null) return "dados";
        return nome.replaceAll("[^a-zA-Z0-9_\\-\\s]", "")
                   .replaceAll("\\s+", "_")
                   .substring(0, Math.min(nome.length(), 50));
    }

    private static class WidgetComDados {
        String titulo;
        String consultaJson;
        int idDataset;
        JsonArray dados;
        List<String> cabecalhos;
        List<String> rotulos;
    }
}
