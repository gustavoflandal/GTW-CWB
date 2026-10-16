package muralha.digital.gis;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet("/MuralhaDigital/Gis")
public class GisServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        String formato = req.getParameter("formato");
        String camada  = req.getParameter("camada");
        if (camada == null || camada.isEmpty()) camada = "equipamentos";

        if ("kml".equals(formato)) {
            exportarKml(resp, camada);
        } else if ("geojson".equals(formato) || formato == null) {
            boolean inline = "1".equals(req.getParameter("inline"));
            exportarGeoJson(resp, inline, camada);
        } else {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"ok\":false,\"erro\":\"Formato inválido. Use geojson ou kml.\"}");
        }
    }

    private void exportarGeoJson(HttpServletResponse resp, boolean inline, String camada) throws IOException {
        String ts = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        resp.setContentType("application/geo+json; charset=UTF-8");
        if (!inline) {
            resp.setHeader("Content-Disposition",
                    "attachment; filename=\"" + camada + "_" + ts + ".geojson\"");
        }

        PrintWriter w = resp.getWriter();
        w.print("{\"type\":\"FeatureCollection\",\"features\":[");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            switch (camada) {
                case "alertas":
                    exportarAlertasGeoJson(conn, w);
                    break;
                case "infracoes":
                    exportarInfracoesGeoJson(conn, w);
                    break;
                default:
                    exportarEquipamentosGeoJson(conn, w);
                    break;
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        w.print("]}");
    }

    private void exportarEquipamentosGeoJson(Connection conn, PrintWriter w) throws Exception {
        String sql =
            "SELECT lv.id_local, lv.nome, lv.posicao_lat, lv.posicao_lon, " +
            "COUNT(vtr.id) AS total_passagens " +
            "FROM local_vigente lv " +
            "LEFT JOIN muralha.veiculo_tempo_real vtr WITH (NOLOCK) ON vtr.id_local = lv.id_local " +
            "WHERE lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL AND lv.desativado = 0 " +
            "GROUP BY lv.id_local, lv.nome, lv.posicao_lat, lv.posicao_lon";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) w.print(",");
                first = false;
                w.printf(
                    "{\"type\":\"Feature\"," +
                    "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                    "\"properties\":{\"id_local\":%d,\"nome\":\"%s\",\"total_passagens\":%d}}",
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"),
                    rs.getInt("id_local"),
                    escJson(rs.getString("nome")),
                    rs.getInt("total_passagens"));
            }
        }
    }

    private void exportarAlertasGeoJson(Connection conn, PrintWriter w) throws Exception {
        String sql =
            "SELECT a.id, a.data, tao.descricao AS tipo_alerta, " +
            "vtr.placa, lv.id_local, lv.posicao_lat, lv.posicao_lon, " +
            "RTRIM(lv.nome) AS nome_local " +
            "FROM muralha.alerta a " +
            "JOIN muralha.alerta_veiculo av ON av.id_alerta = a.id " +
            "JOIN muralha.veiculo_tempo_real vtr WITH (NOLOCK) ON vtr.id = av.id_veiculo_tempo_real " +
            "JOIN local_vigente lv ON lv.id_local = vtr.id_local " +
            "LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia " +
            "WHERE lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL " +
            "AND a.data >= DATEADD(DAY, -30, GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) w.print(",");
                first = false;
                w.printf(
                    "{\"type\":\"Feature\"," +
                    "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                    "\"properties\":{\"id\":%d,\"data\":\"%s\",\"tipo\":\"%s\"," +
                    "\"placa\":\"%s\",\"id_local\":%d,\"nome_local\":\"%s\"}}",
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"),
                    rs.getInt("id"),
                    escJson(String.valueOf(rs.getTimestamp("data"))),
                    escJson(rs.getString("tipo_alerta")),
                    escJson(rs.getString("placa")),
                    rs.getInt("id_local"),
                    escJson(rs.getString("nome_local")));
            }
        }
    }

    private void exportarInfracoesGeoJson(Connection conn, PrintWriter w) throws Exception {
        String sql =
            "SELECT i.id_infracao, i.placa, i.data, i.id_local, " +
            "lv.posicao_lat, lv.posicao_lon, RTRIM(lv.nome) AS nome_local " +
            "FROM dbo.infracao i " +
            "JOIN local_vigente lv ON lv.id_local = i.id_local " +
            "WHERE lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL " +
            "AND i.data >= DATEADD(DAY, -30, GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) w.print(",");
                first = false;
                w.printf(
                    "{\"type\":\"Feature\"," +
                    "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                    "\"properties\":{\"id_infracao\":%d,\"placa\":\"%s\",\"data\":\"%s\"," +
                    "\"id_local\":%d,\"nome_local\":\"%s\"}}",
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"),
                    rs.getInt("id_infracao"),
                    escJson(rs.getString("placa")),
                    escJson(String.valueOf(rs.getTimestamp("data"))),
                    rs.getInt("id_local"),
                    escJson(rs.getString("nome_local")));
            }
        }
    }

    private void exportarKml(HttpServletResponse resp, String camada) throws IOException {
        String ts = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        resp.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + camada + "_" + ts + ".kml\"");

        PrintWriter w = resp.getWriter();
        w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        w.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            switch (camada) {
                case "alertas":
                    exportarAlertasKml(conn, w);
                    break;
                case "infracoes":
                    exportarInfracoesKml(conn, w);
                    break;
                default:
                    exportarEquipamentosKml(conn, w);
                    break;
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        w.println("</Document></kml>");
    }

    private void exportarEquipamentosKml(Connection conn, PrintWriter w) throws Exception {
        w.println("<Document><name>Equipamentos Muralha Digital</name>");
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_local, nome, posicao_lat, posicao_lon " +
                "FROM local_vigente " +
                "WHERE posicao_lat IS NOT NULL AND posicao_lon IS NOT NULL AND desativado = 0 " +
                "ORDER BY id_local");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                w.printf(
                    "<Placemark><name>%s</name>" +
                    "<description>Local %d</description>" +
                    "<Point><coordinates>%s,%s,0</coordinates></Point></Placemark>%n",
                    escXml(rs.getString("nome")),
                    rs.getInt("id_local"),
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"));
            }
        }
    }

    private void exportarAlertasKml(Connection conn, PrintWriter w) throws Exception {
        w.println("<Document><name>Alertas Muralha Digital</name>");
        String sql =
            "SELECT a.id, a.data, tao.descricao AS tipo_alerta, " +
            "vtr.placa, lv.posicao_lat, lv.posicao_lon, RTRIM(lv.nome) AS nome_local " +
            "FROM muralha.alerta a " +
            "JOIN muralha.alerta_veiculo av ON av.id_alerta = a.id " +
            "JOIN muralha.veiculo_tempo_real vtr WITH (NOLOCK) ON vtr.id = av.id_veiculo_tempo_real " +
            "JOIN local_vigente lv ON lv.id_local = vtr.id_local " +
            "LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia " +
            "WHERE lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL " +
            "AND a.data >= DATEADD(DAY, -30, GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                w.printf(
                    "<Placemark><name>Alerta %d - %s</name>" +
                    "<description>%s | %s | %s</description>" +
                    "<Point><coordinates>%s,%s,0</coordinates></Point></Placemark>%n",
                    rs.getInt("id"),
                    escXml(rs.getString("placa")),
                    escXml(String.valueOf(rs.getTimestamp("data"))),
                    escXml(rs.getString("tipo_alerta")),
                    escXml(rs.getString("nome_local")),
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"));
            }
        }
    }

    private void exportarInfracoesKml(Connection conn, PrintWriter w) throws Exception {
        w.println("<Document><name>Infrações Muralha Digital</name>");
        String sql =
            "SELECT i.id_infracao, i.placa, i.data, " +
            "lv.posicao_lat, lv.posicao_lon, RTRIM(lv.nome) AS nome_local " +
            "FROM dbo.infracao i " +
            "JOIN local_vigente lv ON lv.id_local = i.id_local " +
            "WHERE lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL " +
            "AND i.data >= DATEADD(DAY, -30, GETDATE())";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                w.printf(
                    "<Placemark><name>Infração %d - %s</name>" +
                    "<description>%s | %s</description>" +
                    "<Point><coordinates>%s,%s,0</coordinates></Point></Placemark>%n",
                    rs.getInt("id_infracao"),
                    escXml(rs.getString("placa")),
                    escXml(String.valueOf(rs.getTimestamp("data"))),
                    escXml(rs.getString("nome_local")),
                    rs.getString("posicao_lon"),
                    rs.getString("posicao_lat"));
            }
        }
    }

    private String escJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String escXml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
