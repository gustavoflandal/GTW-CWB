# SLA de Latência ≤4s — Plano de Implementação

> **Depende de:** plano `01-log-auditoria.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Monitorar latência entre captura no equipamento e recepção no servidor; gerar alerta operacional quando a média dos últimos 5 minutos superar 4 segundos; exibir painel de SLA em tempo real.

**Architecture:** A coluna `latencia_ms` é calculada na inserção (diferença entre `dt_captura_equipamento` e `dt_recepcao_servidor`). Um job Quartz agendado a cada 5 minutos verifica o percentil 95 e insere alerta em `muralha.alerta_sla` se violado. Um servlet/JSP exibe histórico de cumprimento do SLA.

**Tech Stack:** Quartz Scheduler (já no projeto) · SQL Server · Bootstrap 5.3 · Chart.js (cdnjs)

## Global Constraints

- Quartz já está no `pom.xml`; não adicionar nova dependência
- `dt_captura_equipamento` vem do SOAP da passagem; se nulo → latência NULL (não violar SLA)
- Threshold padrão: 4000ms; configurável via `muralha.configuracao`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_sla_latencia.sql` |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaJob.java` |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/sla-latencia/index.jsp` |
| Modificar | Arquivo de configuração Quartz (ex.: `QuartzJobScheduler.java` ou equivalente) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_sla_latencia.sql
-- ROLLBACK:
--   ALTER TABLE muralha.veiculo_tempo_real DROP COLUMN latencia_ms;
--   DROP TABLE muralha.alerta_sla;

-- 1. Coluna de latência na tabela principal de passagens
ALTER TABLE muralha.veiculo_tempo_real
    ADD latencia_ms INT NULL;   -- milissegundos; NULL se dt_captura_equipamento ausente

-- 2. Tabela de alertas de SLA
CREATE TABLE muralha.alerta_sla (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    dt_alerta        DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    janela_inicio    DATETIME2   NOT NULL,
    janela_fim       DATETIME2   NOT NULL,
    total_passagens  INT         NOT NULL,
    percentil95_ms   INT         NOT NULL,
    threshold_ms     INT         NOT NULL,
    violacao         BIT         NOT NULL,
    equipamentos_top VARCHAR(500) NULL   -- JSON: top 3 equipamentos com maior latência
);

-- 3. Índice para consultas de painel
CREATE INDEX ix_vtr_latencia_ms ON muralha.veiculo_tempo_real (latencia_ms)
WHERE latencia_ms IS NOT NULL;
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_sla_latencia.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_sla_latencia.sql
git commit -m "Adiciona coluna latencia_ms e tabela alerta_sla para SLA de latência

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: Calcular latência na recepção

- [ ] **Localizar ponto de inserção de `veiculo_tempo_real`**

```bash
grep -r "veiculo_tempo_real" src/main/java --include="*.java" -l
```

- [ ] **Adicionar cálculo no método de inserção**

```java
// No DAO/Service que faz INSERT em muralha.veiculo_tempo_real:
// Após obter dtCapturaEquipamento (Timestamp) e antes do INSERT:

Integer latenciaMs = null;
if (dtCapturaEquipamento != null) {
    long capturaMs  = dtCapturaEquipamento.getTime();
    long recepcaoMs = System.currentTimeMillis();
    long diff = recepcaoMs - capturaMs;
    // Aceitar apenas valores razoáveis (0ms a 10min)
    if (diff >= 0 && diff < 600_000L) {
        latenciaMs = (int) diff;
    }
}

// Na query INSERT, incluir a coluna latencia_ms:
// INSERT INTO muralha.veiculo_tempo_real (..., latencia_ms) VALUES (..., ?)
// ps.setObject(N, latenciaMs);  // setObject aceita null corretamente
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Verificar latência gravada**

```sql
SELECT TOP 10 id, dt_captura_equipamento, dt_recepcao_servidor, latencia_ms
FROM muralha.veiculo_tempo_real
ORDER BY id DESC;
```

- [ ] **Commit**

```bash
git add src/main/java/
git commit -m "Calcula e persiste latencia_ms na recepção de passagens

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: SlaLatenciaJob (Quartz)

- [ ] **Criar `SlaLatenciaJob.java`**

```java
// src/main/java/muralha/digital/sla/SlaLatenciaJob.java
package muralha.digital.sla;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.*;

public class SlaLatenciaJob implements Job {

    private static final int DEFAULT_THRESHOLD_MS = 4000;

    @Override
    public void execute(JobExecutionContext ctx) {
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            int threshold = obterThreshold(conn);

            // Percentil 95 das passagens dos últimos 5 minutos
            String sql =
                "WITH cte AS (" +
                "  SELECT latencia_ms, " +
                "    PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY latencia_ms) " +
                "    OVER () AS p95, " +
                "    COUNT(*) OVER () AS total " +
                "  FROM muralha.veiculo_tempo_real " +
                "  WHERE dt_recepcao_servidor >= DATEADD(MINUTE, -5, SYSDATETIME()) " +
                "    AND latencia_ms IS NOT NULL " +
                ") " +
                "SELECT TOP 1 CAST(p95 AS INT) p95, total FROM cte";

            int p95 = 0, total = 0;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p95   = rs.getInt("p95");
                    total = rs.getInt("total");
                }
            }

            boolean violacao = p95 > threshold;

            // Equipamentos com maior latência média nos últimos 5 min
            String topEquip = obterTopEquipamentos(conn);

            // Registrar alerta
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.alerta_sla " +
                    "(janela_inicio, janela_fim, total_passagens, percentil95_ms, " +
                    " threshold_ms, violacao, equipamentos_top) " +
                    "VALUES (DATEADD(MINUTE,-5,SYSDATETIME()), SYSDATETIME(), ?,?,?,?,?)")) {
                ps.setInt(1, total);
                ps.setInt(2, p95);
                ps.setInt(3, threshold);
                ps.setBoolean(4, violacao);
                ps.setString(5, topEquip);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            // Job nunca deve falhar silenciosamente — logar sem propagar
            System.err.println("[SlaLatenciaJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private int obterThreshold(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.configuracao WHERE chave = 'sla_latencia_threshold_ms'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_THRESHOLD_MS;
    }

    private String obterTopEquipamentos(Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("[");
        String sql =
            "SELECT TOP 3 equipamento, CAST(AVG(CAST(latencia_ms AS FLOAT)) AS INT) avg_ms " +
            "FROM muralha.veiculo_tempo_real " +
            "WHERE dt_recepcao_servidor >= DATEADD(MINUTE,-5,SYSDATETIME()) " +
            "  AND latencia_ms IS NOT NULL " +
            "GROUP BY equipamento ORDER BY avg_ms DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                sb.append("{\"eq\":\"").append(rs.getString("equipamento"))
                  .append("\",\"avg\":").append(rs.getInt("avg_ms")).append("}");
                first = false;
            }
        }
        return sb.append("]").toString();
    }
}
```

- [ ] **Registrar o job no agendador Quartz do projeto**

```java
// No arquivo que inicializa os jobs Quartz (buscar: implements ServletContextListener
// com scheduler.scheduleJob, ou applicationContext.xml, ou QuartzInitializerListener):

JobDetail slaJob = JobBuilder.newJob(SlaLatenciaJob.class)
    .withIdentity("slaLatenciaJob", "monitoramento")
    .build();

Trigger slaTrigger = TriggerBuilder.newTrigger()
    .withIdentity("slaLatenciaTrigger", "monitoramento")
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMinutes(5)
        .repeatForever())
    .startNow()
    .build();

scheduler.scheduleJob(slaJob, slaTrigger);
```

- [ ] **Inserir configuração de threshold no banco**

```sql
INSERT INTO muralha.configuracao (chave, valor, descricao)
VALUES ('sla_latencia_threshold_ms', '4000',
        'Threshold de latência em ms para alerta de SLA (padrão: 4000)');
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/sla/SlaLatenciaJob.java
git add src/main/java/   # arquivo do agendador modificado
git commit -m "Adiciona SlaLatenciaJob Quartz para monitoramento de SLA a cada 5 minutos

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Servlet e painel de SLA

- [ ] **Criar `SlaLatenciaServlet.java`**

```java
// src/main/java/muralha/digital/sla/SlaLatenciaServlet.java
package muralha.digital.sla;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/SlaLatencia")
public class SlaLatenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        String acao = req.getParameter("acao");
        JsonObject result = new JsonObject();
        Conexao conn = Conexao.getConexao();
        try {
            if ("historico".equals(acao)) {
                // Últimas 24 horas de alertas (paginado)
                JsonArray rows = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT TOP 288 id, dt_alerta, percentil95_ms, threshold_ms, " +
                        "violacao, total_passagens, equipamentos_top " +
                        "FROM muralha.alerta_sla " +
                        "ORDER BY dt_alerta DESC");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id",          rs.getLong("id"));
                        row.addProperty("dt",          rs.getString("dt_alerta"));
                        row.addProperty("p95",         rs.getInt("percentil95_ms"));
                        row.addProperty("threshold",   rs.getInt("threshold_ms"));
                        row.addProperty("violacao",    rs.getBoolean("violacao"));
                        row.addProperty("total",       rs.getInt("total_passagens"));
                        row.addProperty("topEquip",    rs.getString("equipamentos_top"));
                        rows.add(row);
                    }
                }
                result.addProperty("ok", true);
                result.add("historico", rows);

            } else if ("atual".equals(acao)) {
                // Latência média dos últimos 5 min agora (para dashboard)
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) total, " +
                        "AVG(CAST(latencia_ms AS FLOAT)) media, " +
                        "MAX(latencia_ms) maximo " +
                        "FROM muralha.veiculo_tempo_real " +
                        "WHERE dt_recepcao_servidor >= DATEADD(MINUTE,-5,SYSDATETIME()) " +
                        "  AND latencia_ms IS NOT NULL");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.addProperty("ok",    true);
                        result.addProperty("total", rs.getInt("total"));
                        result.addProperty("media", rs.getDouble("media"));
                        result.addProperty("maximo",rs.getInt("maximo"));
                    }
                }
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
```

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/monitoramento/sla-latencia/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-speedometer2 me-2"></i>SLA de Latência — Equipamentos</h4>

  <!-- Cartões de status atual -->
  <div class="row g-3 mb-4" id="cardsAtual">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted">Latência Média (5 min)</h6>
          <h2 id="lblMedia" class="fw-bold">—</h2>
          <small class="text-muted">ms</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted">Latência Máxima (5 min)</h6>
          <h2 id="lblMaximo" class="fw-bold">—</h2>
          <small class="text-muted">ms</small>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="card-title text-muted">Passagens (5 min)</h6>
          <h2 id="lblTotal" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center" id="cardStatus">
        <div class="card-body">
          <h6 class="card-title text-muted">Status SLA</h6>
          <h2 id="lblStatus" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
  </div>

  <!-- Gráfico histórico -->
  <div class="card mb-4">
    <div class="card-header">Histórico P95 (últimas 24h)</div>
    <div class="card-body"><canvas id="chartSla" height="80"></canvas></div>
  </div>

  <!-- Tabela de violações -->
  <div class="card">
    <div class="card-header">Violações recentes</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0">
        <thead><tr><th>Data/Hora</th><th>P95 (ms)</th><th>Threshold</th><th>Passagens</th><th>Top equipamentos</th></tr></thead>
        <tbody id="tblViolacoes"></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
const THRESHOLD = 4000;
let chart;

async function carregarAtual() {
  const r = await fetch('/MuralhaDigital/SlaLatencia?acao=atual');
  const d = await r.json();
  if (!d.ok) return;
  document.getElementById('lblMedia').textContent  = Math.round(d.media);
  document.getElementById('lblMaximo').textContent = d.maximo;
  document.getElementById('lblTotal').textContent  = d.total;
  const ok = d.media <= THRESHOLD;
  document.getElementById('lblStatus').textContent  = ok ? '✅ OK' : '⚠️ Violado';
  document.getElementById('cardStatus').className  =
    'card text-center border-' + (ok ? 'success' : 'danger');
}

async function carregarHistorico() {
  const r = await fetch('/MuralhaDigital/SlaLatencia?acao=historico');
  const d = await r.json();
  if (!d.ok) return;
  const hist = d.historico.reverse();

  // Gráfico
  const labels = hist.map(h => h.dt.substring(11,16));
  const values = hist.map(h => h.p95);
  if (chart) chart.destroy();
  chart = new Chart(document.getElementById('chartSla'), {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'P95 (ms)', data: values,
        borderColor: '#0d75bf', tension: 0.3, fill: false
      }, {
        label: 'Threshold', data: hist.map(() => THRESHOLD),
        borderColor: '#dc3545', borderDash: [5,5], pointRadius: 0
      }]
    },
    options: { plugins: { legend: { position: 'top' } } }
  });

  // Tabela de violações
  const tbody = document.getElementById('tblViolacoes');
  tbody.innerHTML = '';
  d.historico.filter(h => h.violacao).slice(0,20).forEach(h => {
    const top = JSON.parse(h.topEquip || '[]').map(e => e.eq + ' ' + e.avg + 'ms').join(', ');
    tbody.insertAdjacentHTML('beforeend',
      `<tr class="table-danger">
        <td>${h.dt}</td><td>${h.p95}</td><td>${h.threshold}</td>
        <td>${h.total}</td><td>${top}</td>
      </tr>`);
  });
}

carregarAtual();
carregarHistorico();
setInterval(carregarAtual, 30_000);
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/sla-latencia/index.jsp`
2. Verificar que cards e gráfico carregam
3. Verificar `SELECT TOP 5 * FROM muralha.alerta_sla ORDER BY id DESC;` após 5 minutos

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/sla/SlaLatenciaServlet.java
git add src/main/webapp/muralha-digital/pages/monitoramento/sla-latencia/index.jsp
git commit -m "Adiciona painel de SLA de latência com gráfico histórico e tabela de violações

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
