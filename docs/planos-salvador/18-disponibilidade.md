# Auditoria de Disponibilidade dos Equipamentos — Plano de Implementação

> **Depende de:** plano `07-sla-latencia.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Monitorar e registrar a disponibilidade de cada equipamento de captura: detectar quando um equipamento para de enviar passagens por mais de N minutos, gerar alerta e exibir painel de uptime histórico.

**Architecture:** Job Quartz a cada 10 minutos verifica a última passagem de cada equipamento. Se ausente por mais que o threshold, insere registro em `muralha.equipamento_disponibilidade`. Servlet calcula percentual de uptime por período. JSP exibe painel com semáforo por equipamento.

**Tech Stack:** Quartz Scheduler · SQL Server · Bootstrap 5.3 · Chart.js (cdnjs)

## Global Constraints

- Quartz já no `pom.xml`; sem nova dependência
- Threshold de inatividade: 15 minutos (configurável via `muralha.configuracao`)
- Uptime calculado sobre janelas de 10 minutos: `(janelas_ativas / total_janelas) * 100`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_disponibilidade.sql` |
| Criar | `src/main/java/muralha/digital/disponibilidade/DisponibilidadeJob.java` |
| Criar | `src/main/java/muralha/digital/disponibilidade/DisponibilidadeServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/disponibilidade/index.jsp` |
| Modificar | Agendador Quartz |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_disponibilidade.sql
-- ROLLBACK:
--   DROP TABLE muralha.equipamento_disponibilidade;

CREATE TABLE muralha.equipamento_disponibilidade (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    dt_verificacao  DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    equipamento     VARCHAR(50)  NOT NULL,
    disponivel      BIT          NOT NULL,    -- 1=enviou passagem nos últimos N min; 0=offline
    ultima_passagem DATETIME2    NULL,
    minutos_offline INT          NULL         -- NULL se disponivel=1
);

CREATE INDEX ix_eq_disp_equip ON muralha.equipamento_disponibilidade (equipamento, dt_verificacao DESC);

-- Configuração de threshold
INSERT INTO muralha.configuracao (chave, valor, descricao)
VALUES ('disponibilidade_threshold_min', '15',
        'Minutos sem passagem para considerar equipamento offline');
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_disponibilidade.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_disponibilidade.sql
git commit -m "Adiciona tabela de disponibilidade de equipamentos para auditoria de uptime

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: DisponibilidadeJob

- [ ] **Criar `DisponibilidadeJob.java`**

```java
// src/main/java/muralha/digital/disponibilidade/DisponibilidadeJob.java
package muralha.digital.disponibilidade;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.*;

public class DisponibilidadeJob implements Job {

    private static final int DEFAULT_THRESHOLD_MIN = 15;

    @Override
    public void execute(JobExecutionContext ctx) {
        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            int threshold = obterThreshold(conn);

            // Para cada equipamento conhecido, verificar última passagem
            String sqlEquipamentos =
                "SELECT DISTINCT equipamento " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE equipamento IS NOT NULL " +
                "  AND dt_recepcao_servidor >= DATEADD(DAY,-7,SYSDATETIME())";

            try (PreparedStatement ps = conn.prepareStatement(sqlEquipamentos);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String equipamento = rs.getString("equipamento");
                    verificarEquipamento(conn, equipamento, threshold);
                }
            }
        } catch (Exception e) {
            System.err.println("[DisponibilidadeJob] Erro: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    private void verificarEquipamento(Connection conn, String equipamento, int threshold)
            throws SQLException {
        String sql =
            "SELECT MAX(dt_recepcao_servidor) ultima_passagem " +
            "FROM muralha.veiculo_tempo_real WHERE equipamento=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                Timestamp ultima = rs.getTimestamp("ultima_passagem");
                boolean disponivel = true;
                Integer minutosOffline = null;

                if (ultima != null) {
                    long diffMin = (System.currentTimeMillis() - ultima.getTime()) / 60_000;
                    if (diffMin > threshold) {
                        disponivel = false;
                        minutosOffline = (int) diffMin;
                    }
                } else {
                    disponivel = false;
                }

                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO muralha.equipamento_disponibilidade " +
                        "(equipamento, disponivel, ultima_passagem, minutos_offline) VALUES (?,?,?,?)")) {
                    ins.setString(1, equipamento);
                    ins.setBoolean(2, disponivel);
                    ins.setTimestamp(3, ultima);
                    if (minutosOffline != null) ins.setInt(4, minutosOffline);
                    else ins.setNull(4, java.sql.Types.INTEGER);
                    ins.executeUpdate();
                }
            }
        }
    }

    private int obterThreshold(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.configuracao WHERE chave='disponibilidade_threshold_min'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_THRESHOLD_MIN;
    }
}
```

- [ ] **Registrar no Quartz (a cada 10 minutos)**

```java
JobDetail dispJob = JobBuilder.newJob(DisponibilidadeJob.class)
    .withIdentity("disponibilidadeJob", "monitoramento")
    .build();

Trigger dispTrigger = TriggerBuilder.newTrigger()
    .withIdentity("disponibilidadeTrigger", "monitoramento")
    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        .withIntervalInMinutes(10)
        .repeatForever())
    .startNow()
    .build();

scheduler.scheduleJob(dispJob, dispTrigger);
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/disponibilidade/DisponibilidadeJob.java
git add src/main/java/   # agendador modificado
git commit -m "Adiciona DisponibilidadeJob Quartz para auditoria de uptime dos equipamentos

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Servlet e painel de disponibilidade

- [ ] **Criar `DisponibilidadeServlet.java`**

```java
// src/main/java/muralha/digital/disponibilidade/DisponibilidadeServlet.java
package muralha.digital.disponibilidade;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Disponibilidade")
public class DisponibilidadeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            // Status atual de cada equipamento
            JsonArray status = new JsonArray();
            String sqlStatus =
                "SELECT equipamento, disponivel, ultima_passagem, minutos_offline " +
                "FROM muralha.equipamento_disponibilidade ed " +
                "WHERE dt_verificacao = (" +
                "  SELECT MAX(dt_verificacao) FROM muralha.equipamento_disponibilidade " +
                "  WHERE equipamento = ed.equipamento" +
                ") ORDER BY equipamento";
            try (PreparedStatement ps = conn.prepareStatement(sqlStatus);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("equipamento",  rs.getString("equipamento"));
                    row.addProperty("disponivel",   rs.getBoolean("disponivel"));
                    row.addProperty("ultima",       rs.getString("ultima_passagem"));
                    row.addProperty("minOffline",   rs.getInt("minutos_offline"));
                    status.add(row);
                }
            }
            result.addProperty("ok", true);
            result.add("status", status);

            // Uptime últimas 24h por equipamento
            JsonArray uptime = new JsonArray();
            String sqlUptime =
                "SELECT equipamento, " +
                "  COUNT(*) total_verificacoes, " +
                "  SUM(CAST(disponivel AS INT)) disponivel_count, " +
                "  CAST(SUM(CAST(disponivel AS FLOAT))*100/NULLIF(COUNT(*),0) AS DECIMAL(5,1)) uptime_pct " +
                "FROM muralha.equipamento_disponibilidade " +
                "WHERE dt_verificacao >= DATEADD(HOUR,-24,SYSDATETIME()) " +
                "GROUP BY equipamento ORDER BY uptime_pct";
            try (PreparedStatement ps = conn.prepareStatement(sqlUptime);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("equipamento", rs.getString("equipamento"));
                    row.addProperty("uptime",      rs.getDouble("uptime_pct"));
                    row.addProperty("total",       rs.getInt("total_verificacoes"));
                    uptime.add(row);
                }
            }
            result.add("uptime", uptime);

        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
```

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/monitoramento/disponibilidade/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-hdd-network me-2"></i>Disponibilidade dos Equipamentos</h4>

  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center border-success">
        <div class="card-body">
          <h6 class="text-muted">Online agora</h6>
          <h2 id="lblOnline" class="fw-bold text-success">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center border-danger">
        <div class="card-body">
          <h6 class="text-muted">Offline agora</h6>
          <h2 id="lblOffline" class="fw-bold text-danger">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Uptime médio (24h)</h6>
          <h2 id="lblUptimeMedio" class="fw-bold">—</h2>
          <small class="text-muted">%</small>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Status atual</div>
        <div class="card-body p-0">
          <table class="table table-sm mb-0">
            <thead><tr><th>Equipamento</th><th>Status</th><th>Última passagem</th><th>Offline há</th></tr></thead>
            <tbody id="tblStatus"></tbody>
          </table>
        </div>
      </div>
    </div>
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">Uptime últimas 24h</div>
        <div class="card-body"><canvas id="chartUptime" height="180"></canvas></div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
let chartU;

async function carregar() {
  const r = await fetch('/MuralhaDigital/Disponibilidade');
  const d = await r.json();
  if (!d.ok) return;

  let online = 0, offline = 0;
  const tbody = document.getElementById('tblStatus');
  tbody.innerHTML = '';
  d.status.forEach(s => {
    if (s.disponivel) online++; else offline++;
    tbody.insertAdjacentHTML('beforeend',
      `<tr class="${s.disponivel?'':'table-danger'}">
        <td>${s.equipamento}</td>
        <td><span class="badge bg-${s.disponivel?'success':'danger'}">${s.disponivel?'Online':'Offline'}</span></td>
        <td>${s.ultima?.substring(0,16)||'—'}</td>
        <td>${s.disponivel?'—':s.minOffline+' min'}</td>
      </tr>`);
  });
  document.getElementById('lblOnline').textContent  = online;
  document.getElementById('lblOffline').textContent = offline;

  const uptimeTotal = d.uptime.reduce((acc,u) => acc + u.uptime, 0) / (d.uptime.length||1);
  document.getElementById('lblUptimeMedio').textContent = uptimeTotal.toFixed(1);

  if (chartU) chartU.destroy();
  chartU = new Chart(document.getElementById('chartUptime'), {
    type: 'bar',
    data: {
      labels: d.uptime.map(u => u.equipamento),
      datasets: [{
        label: 'Uptime (%)',
        data: d.uptime.map(u => u.uptime),
        backgroundColor: d.uptime.map(u => u.uptime >= 95 ? '#198754' : u.uptime >= 80 ? '#ffc107' : '#dc3545')
      }]
    },
    options: {
      indexAxis: 'y',
      plugins: { legend: { display: false } },
      scales: { x: { max: 100 } }
    }
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

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/disponibilidade/index.jsp`
2. Verificar cards de online/offline e gráfico de uptime
3. Aguardar 10 minutos e verificar `SELECT TOP 20 * FROM muralha.equipamento_disponibilidade ORDER BY id DESC;`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/disponibilidade/DisponibilidadeServlet.java
git add src/main/webapp/muralha-digital/pages/monitoramento/disponibilidade/index.jsp
git commit -m "Adiciona painel de disponibilidade de equipamentos com uptime histórico

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
