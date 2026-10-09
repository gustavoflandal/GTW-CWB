# Painel de Assertividade da Pré-classificação — Plano de Implementação

> **Depende de:** plano `05-dupla-analise.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Exibir painel de KPIs de assertividade do processo de dupla análise: taxa de concordância entre os dois analistas, distribuição de infrações por resultado (PRE_APROVADA / DESEMPATE / REPROVADA) e ranking de analistas.

**Architecture:** Servlet agrega dados de `muralha.infracao_analise` e `muralha.veiculo_tempo_real`. JSP exibe gráficos Chart.js (doughnut + bar) e tabela de ranking. Sem estado no servidor — todo agregado via SQL por período selecionado.

**Tech Stack:** SQL Server · Java Servlet · Bootstrap 5.3 · Chart.js (cdnjs)

## Global Constraints

- Sem nova dependência no `pom.xml`
- Filtros por período (hoje / 7 dias / 30 dias / customizado)
- Exportação CSV nativa via JavaScript (SheetJS já no projeto)
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/assertividade/AssertividadeServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp` |

---

### Tarefa 1: AssertividadeServlet

- [ ] **Criar `AssertividadeServlet.java`**

```java
// src/main/java/muralha/digital/assertividade/AssertividadeServlet.java
package muralha.digital.assertividade;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Assertividade")
public class AssertividadeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        // Parâmetros de período
        String dtInicio = req.getParameter("dtInicio"); // YYYY-MM-DD
        String dtFim    = req.getParameter("dtFim");

        if (dtInicio == null) dtInicio = "CAST(DATEADD(DAY,-7,GETDATE()) AS DATE)";
        else                  dtInicio = "'" + dtInicio + "'";

        if (dtFim == null) dtFim = "CAST(GETDATE() AS DATE)";
        else               dtFim = "'" + dtFim + "'";

        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            // 1. Totais por status
            JsonObject totais = new JsonObject();
            String sqlStatus =
                "SELECT status_analise, COUNT(*) cnt " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE CAST(dt_recepcao_servidor AS DATE) BETWEEN " + dtInicio + " AND " + dtFim +
                "  AND status_analise IS NOT NULL " +
                "GROUP BY status_analise";
            try (PreparedStatement ps = conn.prepareStatement(sqlStatus);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) totais.addProperty(rs.getString("status_analise"), rs.getInt("cnt"));
            }
            result.add("totais", totais);

            // 2. Taxa de concordância (PRE_APROVADA sobre total com duas análises)
            String sqlConcordancia =
                "SELECT " +
                "  SUM(CASE WHEN status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END) * 100.0 " +
                "    / NULLIF(SUM(CASE WHEN status_analise IN ('PRE_APROVADA','DESEMPATE','REPROVADA') THEN 1 ELSE 0 END),0) taxa " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE CAST(dt_recepcao_servidor AS DATE) BETWEEN " + dtInicio + " AND " + dtFim;
            try (PreparedStatement ps = conn.prepareStatement(sqlConcordancia);
                 ResultSet rs = ps.executeQuery()) {
                result.addProperty("taxaConcordancia", rs.next() ? rs.getDouble("taxa") : 0.0);
            }

            // 3. Ranking de analistas (por volume e taxa de concordância individual)
            JsonArray ranking = new JsonArray();
            String sqlRanking =
                "SELECT u.login, " +
                "  COUNT(DISTINCT ia.id_infracao) total, " +
                "  SUM(CASE WHEN vtr.status_analise = 'PRE_APROVADA' THEN 1 ELSE 0 END) concordantes " +
                "FROM muralha.infracao_analise ia " +
                "JOIN dbo.sis_usuario u ON u.id = ia.id_usuario " +
                "JOIN muralha.veiculo_tempo_real vtr ON vtr.id = ia.id_infracao " +
                "WHERE CAST(ia.dt_analise AS DATE) BETWEEN " + dtInicio + " AND " + dtFim +
                "GROUP BY u.login " +
                "ORDER BY total DESC";
            try (PreparedStatement ps = conn.prepareStatement(sqlRanking);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    int total = rs.getInt("total");
                    int conc  = rs.getInt("concordantes");
                    row.addProperty("login",       rs.getString("login"));
                    row.addProperty("total",       total);
                    row.addProperty("concordantes",conc);
                    row.addProperty("taxa",        total > 0 ? (conc * 100.0 / total) : 0.0);
                    ranking.add(row);
                }
            }
            result.add("ranking", ranking);

            // 4. Evolução diária (últimos 30 dias)
            JsonArray evolucao = new JsonArray();
            String sqlEvolucao =
                "SELECT CAST(dt_recepcao_servidor AS DATE) dia, " +
                "  COUNT(*) total, " +
                "  SUM(CASE WHEN status_analise='PRE_APROVADA' THEN 1 ELSE 0 END) concordantes " +
                "FROM muralha.veiculo_tempo_real " +
                "WHERE CAST(dt_recepcao_servidor AS DATE) BETWEEN " + dtInicio + " AND " + dtFim +
                "  AND status_analise IS NOT NULL " +
                "GROUP BY CAST(dt_recepcao_servidor AS DATE) " +
                "ORDER BY dia";
            try (PreparedStatement ps = conn.prepareStatement(sqlEvolucao);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("dia",   rs.getString("dia"));
                    row.addProperty("total", rs.getInt("total"));
                    row.addProperty("taxa",  rs.getInt("total") > 0
                        ? (rs.getInt("concordantes") * 100.0 / rs.getInt("total")) : 0.0);
                    evolucao.add(row);
                }
            }
            result.add("evolucao", evolucao);
            result.addProperty("ok", true);

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

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/assertividade/AssertividadeServlet.java
git commit -m "Adiciona AssertividadeServlet com KPIs de concordância por período

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: JSP do painel

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2">
    <h4 class="mb-0"><i class="bi bi-bar-chart-line me-2"></i>Assertividade da Dupla Análise</h4>
    <div class="ms-auto d-flex gap-2 align-items-center">
      <select id="selPeriodo" class="form-select form-select-sm" style="width:140px">
        <option value="0">Hoje</option>
        <option value="7" selected>7 dias</option>
        <option value="30">30 dias</option>
        <option value="-1">Customizado</option>
      </select>
      <span id="datesCustom" class="d-none gap-1">
        <input type="date" id="dtInicio" class="form-control form-control-sm">
        <input type="date" id="dtFim"    class="form-control form-control-sm">
      </span>
      <button class="btn btn-sm btn-primary" onclick="carregar()">
        <i class="bi bi-arrow-clockwise"></i>
      </button>
      <button class="btn btn-sm btn-outline-secondary" onclick="exportarCSV()">
        <i class="bi bi-download me-1"></i>CSV
      </button>
    </div>
  </div>

  <!-- KPI cards -->
  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Taxa de Concordância</h6>
          <h2 id="lblConcordancia" class="fw-bold text-success">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Total Pré-aprovadas</h6>
          <h2 id="lblPreAprovada" class="fw-bold">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Desempates</h6>
          <h2 id="lblDesempate" class="fw-bold text-warning">—</h2>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="card text-center">
        <div class="card-body">
          <h6 class="text-muted">Reprovadas</h6>
          <h2 id="lblReprovada" class="fw-bold text-danger">—</h2>
        </div>
      </div>
    </div>
  </div>

  <div class="row g-3 mb-4">
    <!-- Doughnut -->
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Distribuição por status</div>
        <div class="card-body d-flex align-items-center justify-content-center">
          <canvas id="chartDoughnut" width="260" height="260"></canvas>
        </div>
      </div>
    </div>
    <!-- Evolução diária -->
    <div class="col-md-8">
      <div class="card h-100">
        <div class="card-header">Evolução diária da taxa de concordância (%)</div>
        <div class="card-body"><canvas id="chartEvolucao" height="100"></canvas></div>
      </div>
    </div>
  </div>

  <!-- Ranking de analistas -->
  <div class="card">
    <div class="card-header">Ranking de analistas</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0" id="tblRanking">
        <thead>
          <tr>
            <th>#</th><th>Login</th>
            <th>Total analisado</th><th>Concordantes</th><th>Taxa (%)</th>
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"></script>
<script>
let chartD, chartE, dadosGlobais;

document.getElementById('selPeriodo').addEventListener('change', function() {
  document.getElementById('datesCustom').classList.toggle('d-none', this.value !== '-1');
});

function periodoParams() {
  const p = document.getElementById('selPeriodo').value;
  if (p === '-1') {
    return '&dtInicio=' + document.getElementById('dtInicio').value +
           '&dtFim='    + document.getElementById('dtFim').value;
  }
  const hoje = new Date();
  const fim  = hoje.toISOString().substring(0, 10);
  const ini  = new Date(hoje - (parseInt(p)||0) * 864e5).toISOString().substring(0,10);
  return '&dtInicio=' + ini + '&dtFim=' + fim;
}

async function carregar() {
  const r = await fetch('/MuralhaDigital/Assertividade?acao=kpis' + periodoParams());
  const d = await r.json();
  if (!d.ok) { alert('Erro: ' + d.erro); return; }
  dadosGlobais = d;

  document.getElementById('lblConcordancia').textContent = d.taxaConcordancia.toFixed(1) + '%';
  document.getElementById('lblPreAprovada').textContent  = d.totais.PRE_APROVADA   || 0;
  document.getElementById('lblDesempate').textContent    = d.totais.DESEMPATE       || 0;
  document.getElementById('lblReprovada').textContent    = d.totais.REPROVADA       || 0;

  // Doughnut
  if (chartD) chartD.destroy();
  chartD = new Chart(document.getElementById('chartDoughnut'), {
    type: 'doughnut',
    data: {
      labels: ['Pré-aprovada','Desempate','Reprovada'],
      datasets: [{ data: [
        d.totais.PRE_APROVADA||0,
        d.totais.DESEMPATE||0,
        d.totais.REPROVADA||0
      ], backgroundColor: ['#198754','#ffc107','#dc3545'] }]
    }
  });

  // Evolução
  if (chartE) chartE.destroy();
  chartE = new Chart(document.getElementById('chartEvolucao'), {
    type: 'line',
    data: {
      labels: d.evolucao.map(e => e.dia),
      datasets: [{
        label: 'Concordância (%)',
        data: d.evolucao.map(e => e.taxa.toFixed(1)),
        borderColor: '#0d75bf', tension: 0.3
      }]
    }
  });

  // Ranking
  const tbody = document.querySelector('#tblRanking tbody');
  tbody.innerHTML = '';
  d.ranking.forEach((row, i) => {
    tbody.insertAdjacentHTML('beforeend',
      `<tr>
        <td>${i+1}</td><td>${row.login}</td>
        <td>${row.total}</td><td>${row.concordantes}</td>
        <td>${row.taxa.toFixed(1)}</td>
      </tr>`);
  });
}

function exportarCSV() {
  if (!dadosGlobais) return;
  let csv = 'Login,Total,Concordantes,Taxa\n';
  dadosGlobais.ranking.forEach(r =>
    csv += `${r.login},${r.total},${r.concordantes},${r.taxa.toFixed(1)}\n`);
  const a = document.createElement('a');
  a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
  a.download = 'assertividade.csv';
  a.click();
}

carregar();
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/assertividade/index.jsp`
2. Verificar cards, doughnut, gráfico de evolução e tabela de ranking
3. Testar filtros de período (7 dias, 30 dias, customizado)
4. Exportar CSV e verificar conteúdo

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp
git commit -m "Adiciona painel de assertividade da dupla análise com gráficos e ranking

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
