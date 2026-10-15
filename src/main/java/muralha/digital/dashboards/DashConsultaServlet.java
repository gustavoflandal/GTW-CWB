package muralha.digital.dashboards;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.*;
import muralha.digital.dashboards.consulta.ConsultaDashboard;
import muralha.digital.dashboards.consulta.DatasetResolvido;
import muralha.digital.dashboards.consulta.ErroConsulta;
import muralha.digital.dashboards.consulta.ExecutorConsulta;
import muralha.digital.dashboards.consulta.MontadorSql;
import muralha.digital.dashboards.consulta.PeriodoRelativo;
import muralha.digital.dashboards.consulta.ResolvedorDataset;
import muralha.digital.dashboards.consulta.SqlMontado;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLTimeoutException;

/**
 * Consultas dos dashboards: navegador → Java → SQL Server.
 * Valida permissao no dataset, monta SQL parametrizado a partir do catalogo, executa e registra log.
 */
@WebServlet("/api/dash/consultas")
public class DashConsultaServlet extends HttpServlet {

    private final ConsultaLogService consultaLogService = new ConsultaLogService();
    private final MontadorSql montador = new MontadorSql(new PeriodoRelativo());
    private final ExecutorConsulta executor = new ExecutorConsulta();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();

        Acesso acesso = new Acesso(req, resp, true);
        if (!acesso.verificaAcesso(false)) return;

        int idUsuario = acesso.getUsuario().getId();
        int idDataset = 0;
        int idWidget = 0;
        String consultaJson = null;
        long inicio = System.currentTimeMillis();

        try (Connection conn = Conexao.getConexao()) {
            JsonObject pedido = lerPedido(req);
            idWidget = inteiro(pedido, "widgetId");
            idDataset = inteiro(pedido, "datasetId");
            if (idDataset == 0) idDataset = inteiro(pedido, "idDataset");
            JsonObject query = objeto(pedido, "query");
            if (query == null) query = objeto(pedido, "consulta");
            if (idDataset <= 0) throw new ErroConsulta("Dataset nao informado");
            if (query == null) throw new ErroConsulta("Consulta ausente");
            consultaJson = query.toString();

            int tenantId = obterTenantUsuario(idUsuario);
            if (!validarAcessoDataset(conn, idDataset, tenantId, idUsuario)) {
                resp.setStatus(403);
                result.addProperty("ok", false);
                result.addProperty("erro", "Sem permissao neste dataset");
                resp.getWriter().print(result);
                return;
            }

            ConsultaDashboard consulta = ConsultaDashboard.deJson(query, idDataset);
            DatasetResolvido ds = ResolvedorDataset.carregar(conn, idDataset);
            SqlMontado sql = montador.montar(ds, consulta);
            ExecutorConsulta.Resultado r = executor.executar(conn, sql);
            int tempoMs = (int) (System.currentTimeMillis() - inicio);

            result.addProperty("ok", true);
            result.add("data", r.dados);
            result.addProperty("tempoMs", tempoMs);
            result.addProperty("cacheHit", r.cacheHit);
            result.add("annotation", anotacao(sql));

            consultaLogService.registrarAsync(idUsuario, idDataset, idWidget, 0,
                    consultaJson, tempoMs, r.dados.size(), r.cacheHit, null);

        } catch (ErroConsulta e) {
            responderErro(resp, result, 400, e.getMessage());
            registrarErro(idUsuario, idDataset, idWidget, consultaJson, inicio, e.getMessage());
        } catch (SQLTimeoutException e) {
            String msg = "A consulta excedeu " + ExecutorConsulta.TIMEOUT_SEG
                    + " s. Reduza o periodo ou a quantidade de campos.";
            responderErro(resp, result, 504, msg);
            registrarErro(idUsuario, idDataset, idWidget, consultaJson, inicio, msg);
        } catch (Exception e) {
            System.err.println("[DashConsulta] dataset=" + idDataset + " widget=" + idWidget + ": " + e);
            responderErro(resp, result, 500, "Erro ao executar a consulta");
            registrarErro(idUsuario, idDataset, idWidget, consultaJson, inicio, e.getMessage());
        }

        resp.getWriter().print(result);
    }

    private static JsonObject lerPedido(HttpServletRequest req) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        var reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        try {
            JsonElement el = JsonParser.parseString(body.toString());
            if (!el.isJsonObject()) throw new ErroConsulta("Requisicao invalida");
            return el.getAsJsonObject();
        } catch (JsonParseException e) {
            throw new ErroConsulta("Requisicao com JSON invalido");
        }
    }

    private static int inteiro(JsonObject o, String chave) {
        if (!o.has(chave) || !o.get(chave).isJsonPrimitive()) return 0;
        try {
            return o.get(chave).getAsInt();
        } catch (NumberFormatException e) {
            throw new ErroConsulta("'" + chave + "' invalido");
        }
    }

    private static JsonObject objeto(JsonObject o, String chave) {
        return o.has(chave) && o.get(chave).isJsonObject() ? o.getAsJsonObject(chave) : null;
    }

    private static JsonObject anotacao(SqlMontado sql) {
        JsonObject medidas = new JsonObject();
        JsonObject dimensoes = new JsonObject();
        JsonObject tempos = new JsonObject();
        for (SqlMontado.ColunaSaida c : sql.colunas) {
            JsonObject info = new JsonObject();
            info.addProperty("title", c.rotulo);
            info.addProperty("shortTitle", c.rotulo);
            if ("medida".equals(c.papel)) {
                info.addProperty("type", "number");
                medidas.add(c.chaves.get(0), info);
            } else if ("tempo".equals(c.papel)) {
                info.addProperty("type", "time");
                for (String k : c.chaves) tempos.add(k, info);
            } else {
                info.addProperty("type", "string");
                dimensoes.add(c.chaves.get(0), info);
            }
        }
        JsonObject a = new JsonObject();
        a.add("measures", medidas);
        a.add("dimensions", dimensoes);
        a.add("timeDimensions", tempos);
        return a;
    }

    private static void responderErro(HttpServletResponse resp, JsonObject result, int status, String msg) {
        resp.setStatus(status);
        result.addProperty("ok", false);
        result.addProperty("erro", msg);
    }

    private void registrarErro(int idUsuario, int idDataset, int idWidget, String consultaJson,
                               long inicio, String erro) {
        consultaLogService.registrarAsync(idUsuario, idDataset, idWidget, 0, consultaJson,
                (int) (System.currentTimeMillis() - inicio), 0, false, erro);
    }

    /** Publicado no tenant (ou publico), ou rascunho do proprio autor para a previa do editor. */
    private boolean validarAcessoDataset(Connection conn, int idDataset, int tenantId, int idUsuario)
            throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM dash.dataset " +
                "WHERE id = ? AND status <> 'arquivado' " +
                "AND ((status = 'publicado' AND (tenant_id = ? OR visibilidade = 'publico')) " +
                "     OR id_usuario = ?)")) {
            ps.setInt(1, idDataset);
            ps.setInt(2, tenantId);
            ps.setInt(3, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int obterTenantUsuario(int idUsuario) {
        // Single-tenant: quando houver vinculo usuario-tenant, buscar aqui.
        return 1;
    }
}
