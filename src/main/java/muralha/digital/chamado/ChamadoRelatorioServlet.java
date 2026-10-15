package muralha.digital.chamado;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/MuralhaDigital/ChamadoRelatorio")
public class ChamadoRelatorioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter ARQ = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ContextoRequisicao ctx = ContextoRequisicao.de(req, resp);
        if (ctx == null) return;
        String acao = req.getParameter("acao");
        try (Connection conn = Conexao.getConexao()) {
            FiltroChamados f = FiltroChamados.deRequest(req);
            String agrupamento = req.getParameter("agrupamento") == null ? "mes" : req.getParameter("agrupamento");
            if ("indicadores".equals(acao)) {
                ctx.exigir(ctx.operador.veTodos());
                JsonObject r = new JsonObject();
                r.add("linhas", ChamadoDao.indicadores(conn, f, agrupamento));
                ContextoRequisicao.json(resp, r);
            } else if ("exportar".equals(acao)) {
                boolean relatorio = "relatorio".equals(req.getParameter("origem"));
                if (relatorio) ctx.exigir(ctx.operador.veTodos());
                else if (!ctx.operador.veTodos()) f.idSolicitante = ctx.operador.idUsuario;
                exportar(conn, ctx, req, resp, f, relatorio, agrupamento);
            } else {
                throw new RegraChamadoException("Ação inválida.");
            }
        } catch (Exception e) {
            ChamadoServlet.tratar(resp, e);
        }
    }

    private void exportar(Connection conn, ContextoRequisicao ctx, HttpServletRequest req, HttpServletResponse resp,
                          FiltroChamados f, boolean relatorio, String agrupamento) throws Exception {
        List<String> chaves = relatorio ? ExportacaoChamados.INDICADORES_CHAVES : ExportacaoChamados.LISTA_CHAVES;
        List<String> rotulos = relatorio ? ExportacaoChamados.INDICADORES_ROTULOS : ExportacaoChamados.LISTA_ROTULOS;
        JsonArray linhas = relatorio ? ChamadoDao.indicadores(conn, f, agrupamento) : ChamadoDao.listarParaExportacao(conn, f);
        String titulo = relatorio ? "Relatório de chamados por " + agrupamento : "Chamados";
        String filtros = f.descricao();
        String usuario = ctx.usuario.getUsuario() == null ? "" : ctx.usuario.getUsuario().trim();
        LocalDateTime agora = LocalDateTime.now();
        String formato = req.getParameter("formato");
        String base = (relatorio ? "relatorio_chamados_" : "chamados_") + agora.format(ARQ);

        AuditoriaService.registrar(req, "Chamado", "EXPORTAR", formato, titulo + " — " + filtros);

        if ("csv".equals(formato)) {
            enviarArquivo(resp, "text/csv; charset=UTF-8", base + ".csv",
                    ExportacaoChamados.csv(chaves, rotulos, linhas, titulo, filtros, usuario, agora));
        } else if ("xlsx".equals(formato)) {
            enviarArquivo(resp, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", base + ".xlsx",
                    ExportacaoChamados.xlsx(chaves, rotulos, linhas, titulo, filtros, usuario, agora));
        } else if ("json".equals(formato)) {
            // Dados para o PDF gerado no navegador (jsPDF), com o mesmo hash do CSV/XLSX
            JsonObject r = new JsonObject();
            r.addProperty("titulo", titulo);
            r.addProperty("filtros", filtros);
            r.addProperty("usuario", usuario);
            r.addProperty("arquivo", base + ".pdf");
            JsonArray cab = new JsonArray();
            rotulos.forEach(rotulo -> cab.add(rotulo));
            r.add("rotulos", cab);
            JsonArray corpo = new JsonArray();
            for (JsonElement e : linhas) {
                JsonObject o = e.getAsJsonObject();
                JsonArray linha = new JsonArray();
                for (String k : chaves) linha.add(o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : "");
                corpo.add(linha);
            }
            r.add("linhas", corpo);
            r.addProperty("sha256", ExportacaoChamados.hashDados(chaves, rotulos, linhas));
            ContextoRequisicao.json(resp, r);
        } else {
            throw new RegraChamadoException("Formato inválido.");
        }
    }

    private static void enviarArquivo(HttpServletResponse resp, String tipo, String nome, byte[] dados)
            throws IOException {
        resp.setContentType(tipo);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nome + "\"");
        resp.setContentLength(dados.length);
        resp.getOutputStream().write(dados);
    }
}
