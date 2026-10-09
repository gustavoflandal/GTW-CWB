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
        if ("kml".equals(formato)) {
            exportarKml(resp);
        } else if ("geojson".equals(formato) || formato == null) {
            boolean inline = "1".equals(req.getParameter("inline"));
            exportarGeoJson(resp, inline);
        } else {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"ok\":false,\"erro\":\"Formato inválido. Use geojson ou kml.\"}");
        }
    }

    private void exportarGeoJson(HttpServletResponse resp, boolean inline) throws IOException {
        String ts = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        resp.setContentType("application/geo+json; charset=UTF-8");
        if (!inline) {
            resp.setHeader("Content-Disposition",
                    "attachment; filename=\"equipamentos_" + ts + ".geojson\"");
        }

        PrintWriter w = resp.getWriter();
        w.print("{\"type\":\"FeatureCollection\",\"features\":[");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
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
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        w.print("]}");
    }

    private void exportarKml(HttpServletResponse resp) throws IOException {
        String ts = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        resp.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"equipamentos_" + ts + ".kml\"");

        PrintWriter w = resp.getWriter();
        w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        w.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
        w.println("<Document><name>Equipamentos Muralha Digital</name>");

        Connection conn = null;
        try {
            conn = Conexao.getConexao();
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
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        w.println("</Document></kml>");
    }

    private String escJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String escXml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
