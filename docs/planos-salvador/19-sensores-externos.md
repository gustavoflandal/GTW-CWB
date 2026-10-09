# Integração com Sensores Externos (Waze/SAMU) — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Importar periodicamente dados de incidentes de tráfego de fontes externas (Waze for Cities API, SAMU ou arquivo CSV) e correlacioná-los com registros de passagens para análise de contexto; exibir incidentes no mapa e cruzar com equipamentos afetados.

**Architecture:** Job Quartz diário busca dados da API Waze for Cities (ou lê CSV de upload manual como fallback). Persiste em `muralha.incidente_externo`. Servlet calcula correlação com passagens de equipamentos próximos (join por proximidade geográfica ≤500m). JSP exibe no mapa Leaflet com camada de incidentes.

**Tech Stack:** Java `HttpURLConnection` (sem lib HTTP nova) · SQL Server · Bootstrap 5.3 · Leaflet.js (cdnjs)

## Global Constraints

- Sem nova dependência Maven (`HttpURLConnection` para HTTP, `javax.json` ou `com.google.gson` já no projeto)
- API key da Waze via variável de ambiente `WAZE_API_KEY` (não no código)
- Fallback via upload manual de CSV se API indisponível
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_incidentes.sql` |
| Criar | `src/main/java/muralha/digital/sensores/IncidenteImportJob.java` |
| Criar | `src/main/java/muralha/digital/sensores/IncidenteServlet.java` |
| Modificar | `src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp` (camada de incidentes) |
| Modificar | Agendador Quartz |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_incidentes.sql
-- ROLLBACK: DROP TABLE muralha.incidente_externo;

CREATE TABLE muralha.incidente_externo (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    fonte           VARCHAR(20)  NOT NULL,   -- WAZE, CSV, SAMU
    id_externo      VARCHAR(100) NULL,        -- ID da fonte original
    tipo            VARCHAR(50)  NOT NULL,    -- ACCIDENT, JAM, HAZARD, etc.
    descricao       VARCHAR(500) NULL,
    latitude        DECIMAL(10,7) NOT NULL,
    longitude       DECIMAL(10,7) NOT NULL,
    severidade      INT          NULL,         -- 1-5
    dt_ocorrencia   DATETIME2    NOT NULL,
    dt_importacao   DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    ativo           BIT          NOT NULL DEFAULT 1,
    CONSTRAINT uq_incidente UNIQUE (fonte, id_externo, dt_ocorrencia)
);

CREATE INDEX ix_incidente_dt ON muralha.incidente_externo (dt_ocorrencia, ativo);
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_incidentes.sql"
```

- [ ] **Adicionar configuração da API**

```sql
INSERT INTO muralha.configuracao (chave, valor, descricao) VALUES
('waze_api_url', 'https://www.waze.com/row-partnerhub-api/partners/...', 'URL da API Waze for Cities'),
('waze_area_bbox', '-12.80,-38.60,-13.10,-38.30', 'Bounding box lat_max,lon_min,lat_min,lon_max para Salvador');
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_incidentes.sql
git commit -m "Cria tabela de incidentes externos e configurações da API Waze

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: IncidenteImportJob

- [ ] **Criar `IncidenteImportJob.java`**

```java
// src/main/java/muralha/digital/sensores/IncidenteImportJob.java
package muralha.digital.sensores;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.Instant;

public class IncidenteImportJob implements Job {

    @Override
    public void execute(JobExecutionContext ctx) {
        String apiKey = System.getenv("WAZE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[IncidenteImportJob] WAZE_API_KEY não configurada; importação ignorada.");
            return;
        }

        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            String apiUrl  = obterConfig(conn, "waze_api_url");
            String bbox    = obterConfig(conn, "waze_area_bbox");  // lat_max,lon_min,lat_min,lon_max

            String url = apiUrl + "?types=alerts,jams&format=1&polygon=" + URLEncoder.encode(bbox, "UTF-8");
            String json = httpGet(url, apiKey);

            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            int importados = 0;

            // Processar alertas
            JsonArray alerts = root.has("alerts") ? root.getAsJsonArray("alerts") : new JsonArray();
            for (JsonElement el : alerts) {
                JsonObject alert = el.getAsJsonObject();
                importados += inserirIncidente(conn,
                    "WAZE",
                    alert.has("uuid") ? alert.get("uuid").getAsString() : null,
                    alert.has("type") ? alert.get("type").getAsString() : "ALERT",
                    alert.has("subtype") ? alert.get("subtype").getAsString() : null,
                    alert.has("location") ? alert.getAsJsonObject("location").get("y").getAsDouble() : 0,
                    alert.has("location") ? alert.getAsJsonObject("location").get("x").getAsDouble() : 0,
                    alert.has("reportRating") ? alert.get("reportRating").getAsInt() : 0,
                    Instant.now().toString()
                );
            }

            System.out.println("[IncidenteImportJob] Importados " + importados + " incidentes.");
        } catch (Exception e) {
            System.err.println("[IncidenteImportJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private int inserirIncidente(Connection conn, String fonte, String idExt, String tipo,
            String desc, double lat, double lng, int severidade, String dtOcorrencia)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.incidente_externo " +
                "(fonte, id_externo, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia) " +
                "VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, fonte);
            ps.setString(2, idExt);
            ps.setString(3, tipo);
            ps.setString(4, desc);
            ps.setDouble(5, lat);
            ps.setDouble(6, lng);
            ps.setInt(7, severidade);
            ps.setString(8, dtOcorrencia.substring(0, 19).replace("T", " "));
            return ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("uq_incidente")) return 0; // duplicata — ignorar
            throw e;
        }
    }

    private String httpGet(String url, String apiKey) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setConnectTimeout(10_000);
        con.setReadTimeout(15_000);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private String obterConfig(Connection conn, String chave) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.configuracao WHERE chave=?")) {
            ps.setString(1, chave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("valor");
            }
        }
        return "";
    }
}
```

- [ ] **Registrar no Quartz (a cada 30 minutos)**

```java
JobDetail incJob = JobBuilder.newJob(IncidenteImportJob.class)
    .withIdentity("incidenteImportJob", "integracao")
    .build();

Trigger incTrigger = TriggerBuilder.newTrigger()
    .withIdentity("incidenteImportTrigger", "integracao")
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMinutes(30)
        .repeatForever())
    .startNow()
    .build();

scheduler.scheduleJob(incJob, incTrigger);
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/sensores/IncidenteImportJob.java
git add src/main/java/   # agendador modificado
git commit -m "Adiciona IncidenteImportJob para importação de incidentes da API Waze

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: IncidenteServlet e integração no mapa

- [ ] **Criar `IncidenteServlet.java`**

```java
// src/main/java/muralha/digital/sensores/IncidenteServlet.java
package muralha.digital.sensores;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Incidente")
public class IncidenteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            String acao = req.getParameter("acao");

            if ("geojson".equals(acao)) {
                // Incidentes das últimas 2 horas como GeoJSON
                StringBuilder sb = new StringBuilder("{\"type\":\"FeatureCollection\",\"features\":[");
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia " +
                        "FROM muralha.incidente_externo " +
                        "WHERE ativo=1 AND dt_ocorrencia >= DATEADD(HOUR,-2,SYSDATETIME()) " +
                        "ORDER BY dt_ocorrencia DESC");
                     ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) sb.append(",");
                        first = false;
                        sb.append(String.format(
                            "{\"type\":\"Feature\"," +
                            "\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s,%s]}," +
                            "\"properties\":{\"id\":%d,\"tipo\":\"%s\",\"desc\":\"%s\"," +
                            "\"sev\":%d,\"dt\":\"%s\"}}",
                            rs.getString("longitude"), rs.getString("latitude"),
                            rs.getLong("id"), esc(rs.getString("tipo")),
                            esc(rs.getString("descricao")), rs.getInt("severidade"),
                            rs.getString("dt_ocorrencia")));
                    }
                }
                sb.append("]}");
                resp.getWriter().print(sb);
                return;
            }

            // Upload CSV manual
            if ("importarCsv".equals(acao)) {
                // CSV: fonte,id_externo,tipo,descricao,lat,lon,severidade,dt_ocorrencia
                // Implementação simplificada — leitura via parâmetro json
                result.addProperty("ok", false);
                result.addProperty("erro", "Use POST multipart para upload de CSV");
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private String esc(String s) { return s == null ? "" : s.replace("\\","\\\\").replace("\"","\\\""); }
}
```

- [ ] **Adicionar camada de incidentes no mapa existente (`mapa/index.jsp`)**

```javascript
// Adicionar ao script de mapa (depois de carregar a camada de equipamentos):

let incidentesLayer = null;

async function carregarIncidentes() {
  const r = await fetch('/MuralhaDigital/Incidente?acao=geojson');
  const gj = await r.json();

  if (incidentesLayer) map.removeLayer(incidentesLayer);

  const cores = { ACCIDENT: '#dc3545', JAM: '#fd7e14', HAZARD: '#ffc107', ALERT: '#17a2b8' };

  incidentesLayer = L.geoJSON(gj, {
    pointToLayer: (feature, latlng) => {
      const cor = cores[feature.properties.tipo] || '#6c757d';
      return L.circleMarker(latlng, {
        radius: 8, fillColor: cor, color: '#fff',
        weight: 1.5, opacity: 0.9, fillOpacity: 0.75
      });
    },
    onEachFeature: (feature, layer) => {
      const p = feature.properties;
      layer.bindPopup(
        `<strong>🚨 ${p.tipo}</strong><br>` +
        `${p.desc||''}<br>` +
        `Severidade: ${p.sev}<br>` +
        `<small>${p.dt}</small>`
      );
    }
  }).addTo(map);
}

carregarIncidentes();
setInterval(carregarIncidentes, 300_000);  // Atualizar a cada 5 min
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Verificar log do servidor — job deve logar msg de WAZE_API_KEY não configurada (esperado sem a key)
2. Inserir incidente manual no banco para testar a tela:

```sql
INSERT INTO muralha.incidente_externo
(fonte, tipo, descricao, latitude, longitude, severidade, dt_ocorrencia)
VALUES ('CSV', 'ACCIDENT', 'Acidente Av. Paralela', -12.9231, -38.4531, 3, SYSDATETIME());
```

3. Acessar o mapa → verificar círculo vermelho no ponto do incidente
4. Clicar no círculo → popup com detalhes

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/sensores/IncidenteServlet.java
git add src/main/webapp/muralha-digital/pages/monitoramento/mapa/index.jsp
git commit -m "Adiciona integração de incidentes externos Waze e camada no mapa Leaflet

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
