package muralha.digital.exportacao;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/MuralhaDigital/Exportacao")
public class ExportacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String[] HEADERS = {
        "ID", "Placa", "Local", "Pista", "Data Captura",
        "Data Recepção", "Status", "Latência (ms)"
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        String formato = req.getParameter("formato");
        String dtInicio = req.getParameter("dtInicio");
        String dtFim = req.getParameter("dtFim");
        String idLocal = req.getParameter("idLocal");
        String status = req.getParameter("status");

        if ("csv".equals(formato)) {
            exportarCsv(resp, dtInicio, dtFim, idLocal, status);
        } else {
            exportarJson(resp, dtInicio, dtFim, idLocal, status);
        }
    }

    private void exportarCsv(HttpServletResponse resp,
            String dtInicio, String dtFim, String idLocal, String status) throws IOException {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"infracoes_" + ts + ".csv\"");

        PrintWriter w = new PrintWriter(
            new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8));
        w.print('﻿');

        w.println("# GTW-CWB — Relatório de Passagens");
        w.println("# Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        w.println("# Período: " + (dtInicio != null ? dtInicio : "—") + " a " + (dtFim != null ? dtFim : "—"));
        w.println(String.join(";", HEADERS));

        StringBuilder conteudo = new StringBuilder();
        int total = 0;
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            QueryParams qp = construirQuery(dtInicio, dtFim, idLocal, status);
            try (PreparedStatement ps = conn.prepareStatement(qp.sql)) {
                for (int i = 0; i < qp.params.size(); i++) {
                    ps.setString(i + 1, qp.params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String linha = escCsv(rs.getString("id")) + ";"
                            + escCsv(rs.getString("placa")) + ";"
                            + rs.getInt("id_local") + ";"
                            + rs.getInt("id_pista") + ";"
                            + escCsv(rs.getString("dt_captura")) + ";"
                            + escCsv(rs.getString("dt_recepcao")) + ";"
                            + escCsv(rs.getString("status_analise")) + ";"
                            + rs.getInt("latencia_ms");
                        conteudo.append(linha).append("\n");
                        w.println(linha);
                        total++;
                    }
                }
            }
        } catch (Exception e) {
            w.println("# ERRO: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }

        String hash = sha256(conteudo.toString());
        w.println("# Total de registros: " + total);
        w.println("# SHA-256: " + hash);
        w.flush();
    }

    private void exportarJson(HttpServletResponse resp,
            String dtInicio, String dtFim, String idLocal, String status) throws IOException {

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        JsonArray headers = new JsonArray();
        for (String h : HEADERS) headers.add(h);
        result.add("headers", headers);

        JsonArray rows = new JsonArray();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            QueryParams qp = construirQuery(dtInicio, dtFim, idLocal, status);
            try (PreparedStatement ps = conn.prepareStatement(qp.sql)) {
                for (int i = 0; i < qp.params.size(); i++) {
                    ps.setString(i + 1, qp.params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id", rs.getString("id"));
                        row.addProperty("placa", rs.getString("placa"));
                        row.addProperty("idLocal", rs.getInt("id_local"));
                        row.addProperty("idPista", rs.getInt("id_pista"));
                        row.addProperty("dtCaptura", rs.getString("dt_captura"));
                        row.addProperty("dtRecepcao", rs.getString("dt_recepcao"));
                        row.addProperty("status", rs.getString("status_analise"));
                        row.addProperty("latenciaMs", rs.getInt("latencia_ms"));
                        rows.add(row);
                    }
                }
            }
            result.add("rows", rows);
            result.addProperty("total", rows.size());
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private QueryParams construirQuery(String dtInicio, String dtFim, String idLocal, String status) {
        List<String> params = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT TOP 10000 vtr.id, vtr.placa, vtr.id_local, vtr.id_pista, " +
            "  CONVERT(VARCHAR(19), vtr.data, 120) AS dt_captura, " +
            "  CONVERT(VARCHAR(19), vtr.data_importado, 120) AS dt_recepcao, " +
            "  COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') AS status_analise, " +
            "  ISNULL(DATEDIFF(MILLISECOND, vtr.data, vtr.data_importado), 0) AS latencia_ms " +
            "FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK) " +
            "LEFT JOIN muralha.vtr_status_analise vsa " +
            "  ON vsa.id_veiculo_tempo_real = vtr.id " +
            "WHERE 1=1");

        if (dtInicio != null && !dtInicio.isEmpty()) {
            sb.append(" AND CAST(vtr.data AS DATE) >= ?");
            params.add(dtInicio);
        }
        if (dtFim != null && !dtFim.isEmpty()) {
            sb.append(" AND CAST(vtr.data AS DATE) <= ?");
            params.add(dtFim);
        }
        if (idLocal != null && !idLocal.isEmpty()) {
            sb.append(" AND vtr.id_local = ?");
            params.add(idLocal);
        }
        if (status != null && !status.isEmpty()) {
            sb.append(" AND COALESCE(vsa.status_analise, 'AGUARDANDO_ANALISE') = ?");
            params.add(status);
        }

        sb.append(" ORDER BY vtr.data DESC");
        return new QueryParams(sb.toString(), params);
    }

    private String escCsv(String s) {
        if (s == null) return "";
        return s.contains(";") || s.contains("\"") ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return "erro";
        }
    }

    private static class QueryParams {
        final String sql;
        final List<String> params;
        QueryParams(String sql, List<String> params) {
            this.sql = sql;
            this.params = params;
        }
    }
}
