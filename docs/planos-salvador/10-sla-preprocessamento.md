# SLA de Pré-processamento 72h — Plano de Implementação

> **Depende de:** plano `01-log-auditoria.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Monitorar o prazo de 72 horas entre a captura de uma passagem e sua pré-classificação; gerar alerta para registros que ultrapassem esse limite; exibir painel de filas com aging.

**Architecture:** Coluna `dt_preclass` em `muralha.veiculo_tempo_real` marcada quando o status sai de PENDENTE. Job Quartz diário consulta registros PENDENTE com `dt_captura_equipamento < NOW - 72h` e insere em `muralha.alerta_sla_preproc`. Servlet retorna contagem de aging por faixa para dashboard.

**Tech Stack:** Quartz Scheduler (já no projeto) · SQL Server · Bootstrap 5.3

## Global Constraints

- Quartz já no `pom.xml`; sem nova dependência
- Threshold 72h padrão; configurável via `muralha.configuracao`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_sla_preproc.sql` |
| Criar | `src/main/java/muralha/digital/sla/SlaPreprocessamentoJob.java` |
| Criar | `src/main/java/muralha/digital/sla/SlaPreprocessamentoServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/sla-preproc/index.jsp` |
| Modificar | Agendador Quartz (mesma classe do plano 07) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_sla_preproc.sql
-- ROLLBACK:
--   ALTER TABLE muralha.veiculo_tempo_real DROP COLUMN dt_preclass;
--   DROP TABLE muralha.alerta_sla_preproc;

ALTER TABLE muralha.veiculo_tempo_real
    ADD dt_preclass DATETIME2 NULL;  -- preenchida quando sai do status PENDENTE

CREATE TABLE muralha.alerta_sla_preproc (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    dt_alerta       DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    total_pendentes INT          NOT NULL,
    acima_72h       INT          NOT NULL,
    acima_48h       INT          NOT NULL,
    acima_24h       INT          NOT NULL
);

CREATE INDEX ix_vtr_preclass_status ON muralha.veiculo_tempo_real (status_analise, dt_captura_equipamento)
WHERE status_analise = 'PENDENTE' OR status_analise IS NULL;
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_sla_preproc.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_sla_preproc.sql
git commit -m "Adiciona estrutura para SLA de pré-processamento 72h

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: Marcar dt_preclass na análise

- [ ] **No `InfracaoAnaliseDAO.registrarAnalise()` (plano 05), ao mudar status de PENDENTE:**

```java
// Após o UPDATE de status_analise em veiculo_tempo_real:
// Adicionar SET dt_preclass = SYSDATETIME() quando status_analise sai de NULL/PENDENTE

// Modificar o UPDATE existente de:
//   UPDATE muralha.veiculo_tempo_real SET status_analise=? WHERE id=?
// Para:
//   UPDATE muralha.veiculo_tempo_real
//   SET status_analise=?,
//       dt_preclass = CASE WHEN dt_preclass IS NULL THEN SYSDATETIME() ELSE dt_preclass END
//   WHERE id=?
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/processamento/InfracaoAnaliseDAO.java
git commit -m "Marca dt_preclass na primeira análise de cada passagem

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: SlaPreprocessamentoJob

- [ ] **Criar `SlaPreprocessamentoJob.java`**

```java
// src/main/java/muralha/digital/sla/SlaPreprocessamentoJob.java
package muralha.digital.sla;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.*;

public class SlaPreprocessamentoJob implements Job {

    @Override
    public void execute(JobExecutionContext ctx) {
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();

            String sql =
                "SELECT " +
                "  COUNT(*) total_pendentes, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) acima_72h, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) > 48 THEN 1 ELSE 0 END) acima_48h, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) > 24 THEN 1 ELSE 0 END) acima_24h " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE (status_analise IS NULL OR status_analise = 'PENDENTE') " +
                "  AND dt_captura_equipamento IS NOT NULL";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO muralha.alerta_sla_preproc " +
                            "(total_pendentes, acima_72h, acima_48h, acima_24h) VALUES (?,?,?,?)")) {
                        ins.setInt(1, rs.getInt("total_pendentes"));
                        ins.setInt(2, rs.getInt("acima_72h"));
                        ins.setInt(3, rs.getInt("acima_48h"));
                        ins.setInt(4, rs.getInt("acima_24h"));
                        ins.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SlaPreprocessamentoJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }
}
```

- [ ] **Registrar job no agendador Quartz (rodar 1x por hora)**

```java
JobDetail preprocJob = JobBuilder.newJob(SlaPreprocessamentoJob.class)
    .withIdentity("slaPreprocessamentoJob", "monitoramento")
    .build();

Trigger preprocTrigger = TriggerBuilder.newTrigger()
    .withIdentity("slaPreprocessamentoTrigger", "monitoramento")
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInHours(1)
        .repeatForever())
    .startNow()
    .build();

scheduler.scheduleJob(preprocJob, preprocTrigger);
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/sla/SlaPreprocessamentoJob.java
git add src/main/java/   # agendador modificado
git commit -m "Adiciona SlaPreprocessamentoJob para monitorar SLA de 72h

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Servlet e painel

- [ ] **Criar `SlaPreprocessamentoServlet.java`**

```java
// src/main/java/muralha/digital/sla/SlaPreprocessamentoServlet.java
package muralha.digital.sla;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/SlaPreprocessamento")
public class SlaPreprocessamentoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            // Aging atual
            String sqlAging =
                "SELECT equipamento, " +
                "  COUNT(*) total, " +
                "  MAX(DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME())) max_horas " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE (status_analise IS NULL OR status_analise='PENDENTE') " +
                "  AND dt_captura_equipamento IS NOT NULL " +
                "GROUP BY equipamento " +
                "ORDER BY max_horas DESC";

            JsonArray fila = new JsonArray();
            int total72h = 0, total48h = 0, total24h = 0, totalPend = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlAging);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("equipamento", rs.getString("equipamento"));
                    row.addProperty("total",       rs.getInt("total"));
                    row.addProperty("maxHoras",    rs.getInt("max_horas"));
                    fila.add(row);
                    totalPend += rs.getInt("total");
                }
            }

            // Contagens por faixa
            String sqlFaixas =
                "SELECT " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) > 72 THEN 1 ELSE 0 END) f72, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) BETWEEN 49 AND 72 THEN 1 ELSE 0 END) f48, " +
                "  SUM(CASE WHEN DATEDIFF(HOUR, dt_captura_equipamento, SYSDATETIME()) BETWEEN 25 AND 48 THEN 1 ELSE 0 END) f24 " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE (status_analise IS NULL OR status_analise='PENDENTE') " +
                "  AND dt_captura_equipamento IS NOT NULL";
            try (PreparedStatement ps = conn.prepareStatement(sqlFaixas);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total72h = rs.getInt("f72");
                    total48h = rs.getInt("f48");
                    total24h = rs.getInt("f24");
                }
            }

            result.addProperty("ok",          true);
            result.addProperty("totalPend",    totalPend);
            result.addProperty("acima72h",     total72h);
            result.addProperty("entre4872h",   total48h);
            result.addProperty("entre2448h",   total24h);
            result.add("fila", fila);

        } catch (Exception e) {
            result.addProperty("ok",   false);
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
<%-- src/main/webapp/muralha-digital/pages/monitoramento/sla-preproc/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-hourglass-split me-2"></i>SLA de Pré-processamento (72h)</h4>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center border-secondary">
        <div class="card-body">
          <h6 class="text-muted">Total Pendentes</h6>
          <h2 id="lblTotal" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-danger">
        <div class="card-body">
          <h6 class="text-muted text-danger">Acima de 72h ⚠️</h6>
          <h2 id="lblAcima72" class="fw-bold text-danger">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-warning">
        <div class="card-body">
          <h6 class="text-muted">Entre 48h e 72h</h6>
          <h2 id="lblEntre4872" class="fw-bold text-warning">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-info">
        <div class="card-body">
          <h6 class="text-muted">Entre 24h e 48h</h6>
          <h2 id="lblEntre2448" class="fw-bold text-info">—</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="card">
    <div class="card-header">Fila pendente por equipamento</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0">
        <thead><tr><th>Equipamento</th><th>Pendentes</th><th>Aging máx. (h)</th><th>Status</th></tr></thead>
        <tbody id="tblFila"></tbody>
      </table>
    </div>
  </div>
</div>

<script>
async function carregar() {
  const r = await fetch('/MuralhaDigital/SlaPreprocessamento');
  const d = await r.json();
  if (!d.ok) return;
  document.getElementById('lblTotal').textContent     = d.totalPend;
  document.getElementById('lblAcima72').textContent   = d.acima72h;
  document.getElementById('lblEntre4872').textContent = d.entre4872h;
  document.getElementById('lblEntre2448').textContent = d.entre2448h;

  const tbody = document.getElementById('tblFila');
  tbody.innerHTML = '';
  d.fila.forEach(row => {
    const cls = row.maxHoras > 72 ? 'table-danger' : row.maxHoras > 48 ? 'table-warning' : '';
    const badge = row.maxHoras > 72
      ? '<span class="badge bg-danger">Violado</span>'
      : row.maxHoras > 48
      ? '<span class="badge bg-warning text-dark">Atenção</span>'
      : '<span class="badge bg-success">OK</span>';
    tbody.insertAdjacentHTML('beforeend',
      `<tr class="${cls}">
        <td>${row.equipamento}</td><td>${row.total}</td>
        <td>${row.maxHoras}h</td><td>${badge}</td>
      </tr>`);
  });
}
carregar();
setInterval(carregar, 60_000);
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/sla-preproc/index.jsp`
2. Verificar cards e tabela de fila
3. Verificar `SELECT * FROM muralha.alerta_sla_preproc ORDER BY id DESC;`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/sla/SlaPreprocessamentoServlet.java
git add src/main/webapp/muralha-digital/pages/monitoramento/sla-preproc/index.jsp
git commit -m "Adiciona painel e servlet de SLA de pré-processamento 72h

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
