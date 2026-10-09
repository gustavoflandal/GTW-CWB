# Exportação GIS (GeoJSON/KML) — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Exportar localização dos equipamentos de fiscalização e contagem de infrações em formato GeoJSON (para QGIS/ArcGIS) e KML (para Google Earth), permitindo análise geoespacial da cobertura de monitoramento.

**Architecture:** Tabela `muralha.equipamento_gps` armazena coordenadas dos equipamentos. Servlet gera GeoJSON/KML em stream. Tela de mapa usa Leaflet.js (cdnjs) para visualização inline.

**Tech Stack:** SQL Server · Java Servlet · Leaflet.js (cdnjs) · Bootstrap 5.3

## Global Constraints

- Leaflet.js de cdnjs.cloudflare.com
- Sem nova dependência Maven
- GeoJSON RFC 7946 compliant
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_equipamento_gps.sql` |
| Criar | `src/main/java/muralha/digital/gis/GisServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp` |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_equipamento_gps.sql
-- ROLLBACK: DROP TABLE muralha.equipamento_gps;

CREATE TABLE muralha.equipamento_gps (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    codigo          VARCHAR(50)  NOT NULL UNIQUE,   -- mesmo código usado em veiculo_tempo_real.equipamento
    nome            VARCHAR(100) NOT NULL,
    latitude        DECIMAL(10,7) NOT NULL,
    longitude       DECIMAL(10,7) NOT NULL,
    tipo            VARCHAR(30)  NOT NULL DEFAULT 'RADAR', -- RADAR, SEMAFORO, LOMBADA
    ativo           BIT          NOT NULL DEFAULT 1,
    dt_atualizacao  DATETIME2    NOT NULL DEFAULT SYSDATETIME()
);

-- Dados de exemplo para Salvador (coordenadas fictícias)
INSERT INTO muralha.equipamento_gps (codigo, nome, latitude, longitude, tipo) VALUES
('EQ-001', 'Av. Paralela km 5',     -12.9231, -38.4531, 'RADAR'),
('EQ-002', 'Av. Garibaldi s/n',     -12.9748, -38.4792, 'RADAR'),
('EQ-003', 'Rua da Bahia x Pça',    -12.9712, -38.5093, 'SEMAFORO');
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_equipamento_gps.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_equipamento_gps.sql
git commit -m "Cria tabela equipamento_gps com coordenadas dos pontos de monitoramento

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: GisServlet

- [ ] **Criar `GisServlet.java`**

```java
// src/main/java/muralha/digital/gis/GisServlet.java
package muralha.digital.gis;

import com.consilux.lib.Conexao;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/MuralhaDigital/Gis")
public class GisServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();

        String formato = req.getParameter("formato"); // geojson, kml

        if ("kml".equals(formato)) {
            exportarKml(req, resp);
        } else {
            exportarGeoJson(req, resp);
        }
    }

    private void exportarGeoJson(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        resp.setContentType("application/geo+json; charset=UTF-8");
        resp.setHeader("Content-Disposition",
            "attachment; filename=\"equipamentos_" + ts + ".geojson\"");

        PrintWriter w = resp.getWriter();
        w.print("{\"type\":\"FeatureCollection\",\"features\":[");
        Conexao conn = Conexao.getConexao();
        try {
            String sql =
                "SELECT eg.codigo, eg.nome, eg.latitude, eg.longitude, eg.tipo, " +
                "  COUNT(vtr.id) infracoes_total, " +
                "  SUM(CASE WHEN vtr.status_analise='PRE_APROVADA' THEN 1 ELSE 0 END) pre_aprovadas " +
                "FROM muralha.equipamento_gps eg " +
                "LEFT JOIN muralha.veiculo_tempo_real vtr ON vtr.equipamento = eg.codigo " +
                "WHERE eg.ativo=1 " +
                "GROUP BY eg.codigo, eg.nome, eg.latitude, eg.longitude, eg.tipo";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) w.print(",");
                    first = false;
                    w.printf(
                        "{\"type\":\"Feature\"," +
                        "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                        "\"properties\":{\"codigo\":\"%s\",\"nome\":\"%s\",\"tipo\":\"%s\"," +
                        "\"infracoes\":%d,\"pre_aprovadas\":%d}}",
                        rs.getString("longitude"), rs.getString("latitude"),
                        esc(rs.getString("codigo")), esc(rs.getString("nome")),
                        esc(rs.getString("tipo")),
                        rs.getInt("infracoes_total"), rs.getInt("pre_aprovadas"));
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        w.print("]}");
    }

    private void exportarKml(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        resp.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
        resp.setHeader("Content-Disposition",
            "attachment; filename=\"equipamentos_" + ts + ".kml\"");

        PrintWriter w = resp.getWriter();
        w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        w.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\">");
        w.println("<Document><name>Equipamentos GTW-CWB</name>");

        Conexao conn = Conexao.getConexao();
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT codigo, nome, latitude, longitude, tipo " +
                    "FROM muralha.equipamento_gps WHERE ativo=1");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    w.printf(
                        "<Placemark><name>%s</name>" +
                        "<description>%s — %s</description>" +
                        "<Point><coordinates>%s,%s,0</coordinates></Point></Placemark>%n",
                        escXml(rs.getString("nome")),
                        escXml(rs.getString("codigo")),
                        escXml(rs.getString("tipo")),
                        rs.getString("longitude"), rs.getString("latitude"));
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        w.println("</Document></kml>");
    }

    private String esc(String s)    { return s == null ? "" : s.replace("\\","\\\\").replace("\"","\\\""); }
    private String escXml(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/gis/GisServlet.java
git commit -m "Adiciona GisServlet com exportação GeoJSON e KML dos equipamentos

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Tela de mapa com Leaflet

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css">

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3">
    <h4 class="mb-0"><i class="bi bi-map me-2"></i>Mapa de Equipamentos</h4>
    <div class="ms-auto d-flex gap-2">
      <a href="/MuralhaDigital/Gis?formato=geojson" class="btn btn-sm btn-outline-primary">
        <i class="bi bi-download me-1"></i>GeoJSON
      </a>
      <a href="/MuralhaDigital/Gis?formato=kml" class="btn btn-sm btn-outline-success">
        <i class="bi bi-download me-1"></i>KML
      </a>
    </div>
  </div>
  <div id="mapa" style="height:70vh;border-radius:8px;"></div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
<script>
const map = L.map('mapa').setView([-12.97, -38.50], 12);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  attribution: '© OpenStreetMap contributors'
}).addTo(map);

fetch('/MuralhaDigital/Gis?formato=geojson')
  .then(r => r.json())
  .then(gj => {
    L.geoJSON(gj, {
      pointToLayer: (feature, latlng) => {
        const cor = feature.properties.infracoes > 100 ? 'red' : '#0d75bf';
        return L.circleMarker(latlng, {
          radius: 10, fillColor: cor, color: '#fff',
          weight: 2, opacity: 1, fillOpacity: 0.85
        });
      },
      onEachFeature: (feature, layer) => {
        const p = feature.properties;
        layer.bindPopup(
          `<strong>${p.nome}</strong><br>` +
          `Código: ${p.codigo}<br>` +
          `Tipo: ${p.tipo}<br>` +
          `Infrações: ${p.infracoes}<br>` +
          `Pré-aprovadas: ${p.pre_aprovadas}`
        );
      }
    }).addTo(map);
  });
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/mapa/index.jsp`
2. Verificar mapa com pontos dos equipamentos
3. Clicar em um ponto → popup com dados
4. Baixar GeoJSON → abrir no QGIS ou geojson.io
5. Baixar KML → abrir no Google Earth

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp
git commit -m "Adiciona mapa de equipamentos com Leaflet e exportação GeoJSON/KML

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
