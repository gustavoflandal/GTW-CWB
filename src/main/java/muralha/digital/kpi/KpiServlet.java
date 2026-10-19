package muralha.digital.kpi;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital.acessos.ControleAcesso;
import muralha.digital.logtecnico.ContextoAlteracao;
import muralha.digital.logtecnico.HistoricoParametros;

import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/MuralhaDigital/Kpi")
public class KpiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(KpiServlet.class);
    static final String ACAO_CONFIG = "/muralha-digital/pages/admin/kpis/index.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String acao = req.getParameter("acao");
        if (!ControleAcesso.exigir(req, resp)) return;
        if ("listarConfig".equals(acao) && !ControleAcesso.exigir(req, resp, ACAO_CONFIG)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            if ("historico".equals(acao)) {
                int idKpi = Integer.parseInt(req.getParameter("idKpi"));
                int dias = 30;
                try { dias = Integer.parseInt(req.getParameter("dias")); } catch (Exception ignored) {}
                JsonArray historico = new JsonArray();
                try (PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT valor, status, dt_coleta FROM muralha.kpi_historico " +
                        "WHERE id_kpi = ? AND dt_coleta >= DATEADD(DAY, -?, GETDATE()) " +
                        "ORDER BY dt_coleta")) {
                    ps2.setInt(1, idKpi);
                    ps2.setInt(2, dias);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            JsonObject h = new JsonObject();
                            h.addProperty("valor", rs2.getDouble("valor"));
                            h.addProperty("status", rs2.getString("status"));
                            h.addProperty("dtColeta", rs2.getString("dt_coleta"));
                            historico.add(h);
                        }
                    }
                }
                JsonObject metaObj = new JsonObject();
                try (PreparedStatement ps3 = conn.prepareStatement(
                        "SELECT meta, meta_minima, categoria FROM muralha.kpi_config_meta WHERE id_kpi = ?")) {
                    ps3.setInt(1, idKpi);
                    try (ResultSet rs3 = ps3.executeQuery()) {
                        if (rs3.next()) {
                            if (rs3.getObject("meta") != null) metaObj.addProperty("meta", rs3.getDouble("meta"));
                            if (rs3.getObject("meta_minima") != null) metaObj.addProperty("metaMinima", rs3.getDouble("meta_minima"));
                            metaObj.addProperty("categoria", rs3.getString("categoria"));
                        }
                    }
                }
                result.addProperty("ok", true);
                result.add("historico", historico);
                result.add("meta", metaObj);

            } else if ("listarConfig".equals(acao)) {
                JsonArray configs = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, nome, descricao, query_sql, unidade, " +
                        "threshold_ok, threshold_warn, ordem, ativo, tipo_visual, cor_personalizada " +
                        "FROM muralha.kpi_config ORDER BY ordem");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject c = new JsonObject();
                        c.addProperty("id", rs.getInt("id"));
                        c.addProperty("nome", rs.getString("nome"));
                        c.addProperty("descricao", rs.getString("descricao"));
                        c.addProperty("querySql", rs.getString("query_sql"));
                        c.addProperty("unidade", rs.getString("unidade"));
                        c.addProperty("thOk", rs.getDouble("threshold_ok"));
                        c.addProperty("thWarn", rs.getDouble("threshold_warn"));
                        c.addProperty("ordem", rs.getInt("ordem"));
                        c.addProperty("ativo", rs.getBoolean("ativo"));
                        c.addProperty("tipoVisual", rs.getString("tipo_visual"));
                        c.addProperty("corPersonalizada", rs.getString("cor_personalizada"));
                        configs.add(c);
                    }
                }
                result.addProperty("ok", true);
                result.add("configs", configs);

            } else {
                JsonArray kpis = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, nome, descricao, query_sql, unidade, " +
                        "threshold_ok, threshold_warn, tipo_visual, cor_personalizada FROM muralha.kpi_config " +
                        "WHERE ativo = 1 ORDER BY ordem");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject kpi = new JsonObject();
                        kpi.addProperty("id", rs.getInt("id"));
                        kpi.addProperty("nome", rs.getString("nome"));
                        kpi.addProperty("descricao", rs.getString("descricao"));
                        kpi.addProperty("unidade", rs.getString("unidade"));
                        kpi.addProperty("tipoVisual", rs.getString("tipo_visual"));
                        kpi.addProperty("corPersonalizada", rs.getString("cor_personalizada"));
                        double thOk = rs.getDouble("threshold_ok");
                        double thWarn = rs.getDouble("threshold_warn");
                        kpi.addProperty("thOk", thOk);
                        kpi.addProperty("thWarn", thWarn);

                        String sql = rs.getString("query_sql");
                        if (ValidadorSqlKpi.validar(sql) != null) {
                            kpi.addProperty("status", "ERRO");
                            kpis.add(kpi);
                            continue;
                        }
                        try (PreparedStatement kpiPs = conn.prepareStatement(sql.trim())) {
                            kpiPs.setQueryTimeout(ValidadorSqlKpi.TIMEOUT_SEGUNDOS);
                            try (ResultSet kpiRs = kpiPs.executeQuery()) {
                                if (kpiRs.next()) {
                                    double valor = kpiRs.getDouble("valor");
                                    kpi.addProperty("valor", valor);
                                    String status;
                                    if (thOk > thWarn) {
                                        status = valor >= thOk ? "OK" : valor >= thWarn ? "WARN" : "CRIT";
                                    } else {
                                        status = valor <= thOk ? "OK" : valor <= thWarn ? "WARN" : "CRIT";
                                    }
                                    kpi.addProperty("status", status);
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("Erro ao executar KPI id=" + kpi.get("id").getAsInt() + ": " + e.getMessage());
                            kpi.addProperty("status", "ERRO");
                        }
                        kpis.add(kpi);
                    }
                }
                result.addProperty("ok", true);
                result.add("kpis", kpis);
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ControleAcesso.exigir(req, resp) || !ControleAcesso.exigir(req, resp, ACAO_CONFIG)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            String acao = req.getParameter("acao");

            if ("salvar".equals(acao)) {
                String sql = req.getParameter("querySql");
                String erroSql = ValidadorSqlKpi.validar(sql);
                if (erroSql != null) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", erroSql);
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
                sql = sql.trim();

                String idStr = req.getParameter("id");
                String tipoVisual = req.getParameter("tipoVisual");
                if (tipoVisual == null || tipoVisual.isEmpty()) tipoVisual = "card";
                String corPersonalizada = req.getParameter("corPersonalizada");
                if (corPersonalizada != null && corPersonalizada.isEmpty()) corPersonalizada = null;

                Map<String, String> novo = new LinkedHashMap<>();
                novo.put("nome", req.getParameter("nome"));
                novo.put("descricao", req.getParameter("descricao"));
                novo.put("query_sql", sql);
                novo.put("unidade", req.getParameter("unidade"));
                novo.put("threshold_ok", String.valueOf(Double.parseDouble(req.getParameter("thOk"))));
                novo.put("threshold_warn", String.valueOf(Double.parseDouble(req.getParameter("thWarn"))));
                novo.put("ordem", String.valueOf(Integer.parseInt(req.getParameter("ordem"))));
                novo.put("tipo_visual", tipoVisual);
                novo.put("cor_personalizada", corPersonalizada);

                if (idStr != null && !idStr.isEmpty()) {
                    novo.put("ativo", String.valueOf("true".equals(req.getParameter("ativo"))));
                    Map<String, String> antes = lerKpi(conn, Integer.parseInt(idStr));
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE muralha.kpi_config SET nome = ?, descricao = ?, query_sql = ?, " +
                            "unidade = ?, threshold_ok = ?, threshold_warn = ?, ordem = ?, ativo = ?, " +
                            "tipo_visual = ?, cor_personalizada = ? WHERE id = ?")) {
                        ps.setString(1, req.getParameter("nome"));
                        ps.setString(2, req.getParameter("descricao"));
                        ps.setString(3, sql);
                        ps.setString(4, req.getParameter("unidade"));
                        ps.setDouble(5, Double.parseDouble(req.getParameter("thOk")));
                        ps.setDouble(6, Double.parseDouble(req.getParameter("thWarn")));
                        ps.setInt(7, Integer.parseInt(req.getParameter("ordem")));
                        ps.setBoolean(8, "true".equals(req.getParameter("ativo")));
                        ps.setString(9, tipoVisual);
                        ps.setString(10, corPersonalizada);
                        ps.setInt(11, Integer.parseInt(idStr));
                        ps.executeUpdate();
                    }
                    HistoricoParametros.registrarSemFalhar(conn, HistoricoParametros.RECURSO_KPI, idStr,
                        HistoricoParametros.comparar(antes, novo), ContextoAlteracao.de(req, "Configuracao de KPIs"));
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, " +
                            "threshold_ok, threshold_warn, ordem, tipo_visual, cor_personalizada) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, req.getParameter("nome"));
                        ps.setString(2, req.getParameter("descricao"));
                        ps.setString(3, sql);
                        ps.setString(4, req.getParameter("unidade"));
                        ps.setDouble(5, Double.parseDouble(req.getParameter("thOk")));
                        ps.setDouble(6, Double.parseDouble(req.getParameter("thWarn")));
                        ps.setInt(7, Integer.parseInt(req.getParameter("ordem")));
                        ps.setString(8, tipoVisual);
                        ps.setString(9, corPersonalizada);
                        ps.executeUpdate();
                        try (ResultSet chaves = ps.getGeneratedKeys()) {
                            if (chaves.next()) {
                                HistoricoParametros.registrarSemFalhar(conn, HistoricoParametros.RECURSO_KPI,
                                    chaves.getString(1), HistoricoParametros.comparar(Collections.emptyMap(), novo),
                                    ContextoAlteracao.de(req, "Configuracao de KPIs"));
                            }
                        }
                    }
                }
                result.addProperty("ok", true);

            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "Ação inválida");
            }

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    /** Valores atuais do KPI, no mesmo formato do mapa gravado (para o histórico de parâmetros). */
    private static Map<String, String> lerKpi(Connection conn, int id) throws SQLException {
        Map<String, String> m = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem, ativo, " +
                "tipo_visual, cor_personalizada FROM muralha.kpi_config WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m.put("nome", rs.getString("nome"));
                    m.put("descricao", rs.getString("descricao"));
                    m.put("query_sql", rs.getString("query_sql"));
                    m.put("unidade", rs.getString("unidade"));
                    m.put("threshold_ok", String.valueOf(rs.getDouble("threshold_ok")));
                    m.put("threshold_warn", String.valueOf(rs.getDouble("threshold_warn")));
                    m.put("ordem", String.valueOf(rs.getInt("ordem")));
                    m.put("ativo", String.valueOf(rs.getBoolean("ativo")));
                    m.put("tipo_visual", rs.getString("tipo_visual"));
                    m.put("cor_personalizada", rs.getString("cor_personalizada"));
                }
            }
        }
        return m;
    }
}
