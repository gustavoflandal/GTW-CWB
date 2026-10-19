package muralha.digital.preprocessamento;

import com.consilux.lib.Conexao;
import muralha.digital.acessos.ControleAcesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/MuralhaDigital/PreProcessamento")
public class PreProcessamentoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        boolean tela = acao == null || acao.isEmpty();
        if (tela ? !ControleAcesso.exigirTela(req, resp) : !ControleAcesso.exigir(req, resp)) return;
        if (tela) {
            req.setAttribute("titulo", "Painel de Pré-Processamento");
            try {
                req.getRequestDispatcher("/muralha-digital/pages/monitoramento/pre-processamento/index.jsp")
                   .forward(req, resp);
            } catch (Exception e) {
                resp.sendError(500, e.getMessage());
            }
            return;
        }

        resp.setContentType("application/json; charset=UTF-8");
        String dtInicio = req.getParameter("dtInicio");
        String dtFim = req.getParameter("dtFim");

        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            if ("metricas".equals(acao)) {
                result.add("volumes", obterVolumes(conn, dtInicio, dtFim));
                result.add("sla", obterSla(conn, dtInicio, dtFim));
                result.add("pendentes", obterPendentes(conn));
                result.add("produtividade", obterProdutividade(conn, dtInicio, dtFim));
                result.add("classificacao", obterClassificacao(conn, dtInicio, dtFim));
                result.add("evolucaoDiaria", obterEvolucaoDiaria(conn, dtInicio, dtFim));
                result.addProperty("ok", true);
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "Ação desconhecida: " + acao);
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonObject obterVolumes(Connection conn, String dtInicio, String dtFim) throws Exception {
        String sql =
            "SELECT " +
            "  COUNT(*) AS recebidos, " +
            "  SUM(CASE WHEN vsa.status_analise IS NOT NULL " +
            "       AND vsa.status_analise NOT IN ('AGUARDANDO_ANALISE') THEN 1 ELSE 0 END) AS processados, " +
            "  SUM(CASE WHEN vsa.status_analise IN ('PRE_APROVADA','APROVADA') THEN 1 ELSE 0 END) AS disponibilizados, " +
            "  SUM(CASE WHEN vsa.status_analise = 'DESCARTADA' THEN 1 ELSE 0 END) AS descartados " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
            "WHERE vtr.data >= COALESCE(CAST(? AS DATE), CAST(DATEADD(DAY,-30,GETDATE()) AS DATE)) " +
            "  AND vtr.data < DATEADD(DAY, 1, COALESCE(CAST(? AS DATE), CAST(GETDATE() AS DATE)))";

        JsonObject vol = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long recebidos = rs.getLong("recebidos");
                    long disponibilizados = rs.getLong("disponibilizados");
                    vol.addProperty("recebidos", recebidos);
                    vol.addProperty("processados", rs.getLong("processados"));
                    vol.addProperty("disponibilizados", disponibilizados);
                    vol.addProperty("descartados", rs.getLong("descartados"));
                    vol.addProperty("aproveitamento",
                            recebidos == 0 ? 100.0 : Math.round((double) disponibilizados / recebidos * 10000.0) / 100.0);
                }
            }
        }
        return vol;
    }

    private JsonObject obterSla(Connection conn, String dtInicio, String dtFim) throws Exception {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS janelas, " +
                "SUM(CASE WHEN violacao = 1 THEN 1 ELSE 0 END) AS violacoes " +
                "FROM muralha.alerta_sla " +
                "WHERE janela_inicio >= COALESCE(CAST(? AS DATE), CAST(DATEADD(DAY,-30,GETDATE()) AS DATE)) " +
                "  AND janela_fim < DATEADD(DAY, 1, COALESCE(CAST(? AS DATE), CAST(GETDATE() AS DATE)))")) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int janelas = rs.getInt("janelas");
                    int violacoes = rs.getInt("violacoes");
                    obj.addProperty("janelas", janelas);
                    obj.addProperty("violacoes", violacoes);
                    obj.addProperty("cumprimento",
                            janelas == 0 ? 100.0 : Math.round((double) (janelas - violacoes) / janelas * 10000.0) / 100.0);
                }
            }
        }
        return obj;
    }

    private JsonObject obterPendentes(Connection conn) throws Exception {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS pendentes, " +
                "SUM(CASE WHEN DATEDIFF(HOUR, vtr.data, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) AS backlog_72h " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE vsa.status_analise IS NULL OR vsa.status_analise = 'AGUARDANDO_ANALISE'")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    obj.addProperty("pendentes", rs.getLong("pendentes"));
                    obj.addProperty("backlog72h", rs.getLong("backlog_72h"));
                }
            }
        }
        return obj;
    }

    private JsonObject obterProdutividade(Connection conn, String dtInicio, String dtFim) throws Exception {
        JsonObject obj = new JsonObject();
        JsonArray operadores = new JsonArray();
        int totalGeral = 0;
        int qtdOperadores = 0;

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT u.nome, COUNT(DISTINCT ia.id_infracao) AS total " +
                "FROM muralha.infracao_analise ia " +
                "JOIN dbo.sis_usuario u ON u.id_usuario = ia.id_usuario " +
                "WHERE CAST(ia.dt_analise AS DATE) BETWEEN " +
                "  COALESCE(CAST(? AS DATE), CAST(DATEADD(DAY,-30,GETDATE()) AS DATE)) AND " +
                "  COALESCE(CAST(? AS DATE), CAST(GETDATE() AS DATE)) " +
                "GROUP BY u.nome ORDER BY total DESC")) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    int total = rs.getInt("total");
                    row.addProperty("nome", rs.getString("nome"));
                    row.addProperty("total", total);
                    operadores.add(row);
                    totalGeral += total;
                    qtdOperadores++;
                }
            }
        }
        obj.addProperty("totalAnalisados", totalGeral);
        obj.addProperty("qtdOperadores", qtdOperadores);
        obj.addProperty("mediaPorOperador", qtdOperadores == 0 ? 0 : totalGeral / qtdOperadores);
        obj.add("operadores", operadores);
        return obj;
    }

    private JsonObject obterClassificacao(Connection conn, String dtInicio, String dtFim) throws Exception {
        JsonObject obj = new JsonObject();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT vsa.status_analise, COUNT(*) AS total " +
                "FROM muralha.vtr_status_analise vsa " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real " +
                "WHERE vtr.data >= COALESCE(CAST(? AS DATE), CAST(DATEADD(DAY,-30,GETDATE()) AS DATE)) " +
                "  AND vtr.data < DATEADD(DAY, 1, COALESCE(CAST(? AS DATE), CAST(GETDATE() AS DATE))) " +
                "GROUP BY vsa.status_analise ORDER BY total DESC")) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    obj.addProperty(rs.getString("status_analise"), rs.getInt("total"));
                }
            }
        }
        return obj;
    }

    private JsonArray obterEvolucaoDiaria(Connection conn, String dtInicio, String dtFim) throws Exception {
        JsonArray arr = new JsonArray();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CAST(vtr.data AS DATE) AS dia, " +
                "  COUNT(*) AS recebidos, " +
                "  SUM(CASE WHEN vsa.status_analise IS NOT NULL " +
                "       AND vsa.status_analise NOT IN ('AGUARDANDO_ANALISE') THEN 1 ELSE 0 END) AS processados " +
                "FROM muralha.veiculo_tempo_real vtr " +
                "LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id " +
                "WHERE vtr.data >= COALESCE(CAST(? AS DATE), CAST(DATEADD(DAY,-30,GETDATE()) AS DATE)) " +
                "  AND vtr.data < DATEADD(DAY, 1, COALESCE(CAST(? AS DATE), CAST(GETDATE() AS DATE))) " +
                "GROUP BY CAST(vtr.data AS DATE) ORDER BY dia")) {
            ps.setString(1, dtInicio);
            ps.setString(2, dtFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("dia", rs.getString("dia"));
                    row.addProperty("recebidos", rs.getLong("recebidos"));
                    row.addProperty("processados", rs.getLong("processados"));
                    arr.add(row);
                }
            }
        }
        return arr;
    }
}
