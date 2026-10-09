# KPIs Configuráveis — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Permitir que administradores configurem quais KPIs aparecem no dashboard principal, com thresholds de alerta personalizáveis; exibir os KPIs selecionados em cards coloridos com indicador de status.

**Architecture:** Tabela `muralha.kpi_config` armazena nome, query SQL parametrizada, threshold e cor. Servlet executa as queries e retorna valores. JSP dashboard renderiza cards dinamicamente. CRUD de configuração de KPIs em tela de administração.

**Tech Stack:** SQL Server · Java Servlet · Bootstrap 5.3 · Chart.js (cdnjs)

## Global Constraints

- Queries de KPI executam somente SELECT; bloquear DML/DDL na execução
- Sem nova dependência Maven
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_kpi_config.sql` |
| Criar | `src/main/java/muralha/digital/kpi/KpiServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/administracao/kpis/index.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/dashboard/index.jsp` (ou modificar existente) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_kpi_config.sql
-- ROLLBACK: DROP TABLE muralha.kpi_config;

CREATE TABLE muralha.kpi_config (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    nome          VARCHAR(80)  NOT NULL,
    descricao     VARCHAR(200) NULL,
    query_sql     VARCHAR(MAX) NOT NULL,   -- deve retornar coluna "valor" NUMERIC
    unidade       VARCHAR(20)  NULL,        -- ex.: "ms", "%", "registros"
    threshold_ok  FLOAT        NULL,        -- valor <= threshold_ok → verde
    threshold_warn FLOAT       NULL,        -- valor <= threshold_warn → amarelo; acima → vermelho
    ordem         INT          NOT NULL DEFAULT 0,
    ativo         BIT          NOT NULL DEFAULT 1
);

-- KPIs padrão
INSERT INTO muralha.kpi_config (nome, descricao, query_sql, unidade, threshold_ok, threshold_warn, ordem) VALUES
('Latência Média (5 min)',
 'Latência média de recepção de passagens nos últimos 5 minutos',
 'SELECT CAST(AVG(CAST(latencia_ms AS FLOAT)) AS NUMERIC(10,1)) valor FROM muralha.veiculo_tempo_real WHERE dt_recepcao_servidor >= DATEADD(MINUTE,-5,SYSDATETIME()) AND latencia_ms IS NOT NULL',
 'ms', 2000, 4000, 1),

('Passagens Pendentes',
 'Infrações ainda não analisadas',
 'SELECT CAST(COUNT(*) AS NUMERIC) valor FROM muralha.veiculo_tempo_real WHERE status_analise IS NULL OR status_analise=''PENDENTE''',
 'registros', 100, 500, 2),

('Taxa de Concordância (7d)',
 'Percentual de concordância na dupla análise nos últimos 7 dias',
 'SELECT CAST(SUM(CASE WHEN status_analise=''PRE_APROVADA'' THEN 1.0 ELSE 0 END)*100/NULLIF(COUNT(*),0) AS NUMERIC(5,1)) valor FROM muralha.veiculo_tempo_real WHERE dt_recepcao_servidor >= DATEADD(DAY,-7,SYSDATETIME()) AND status_analise IS NOT NULL',
 '%', 80, 60, 3),

('Violações de SLA (24h)',
 'Lotes de 5 min com P95 acima do threshold nas últimas 24 horas',
 'SELECT CAST(COUNT(*) AS NUMERIC) valor FROM muralha.alerta_sla WHERE violacao=1 AND dt_alerta >= DATEADD(HOUR,-24,SYSDATETIME())',
 'alertas', 0, 3, 4);
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_kpi_config.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_kpi_config.sql
git commit -m "Cria tabela kpi_config com KPIs padrão configuráveis

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: KpiServlet

- [ ] **Criar `KpiServlet.java`**

```java
// src/main/java/muralha/digital/kpi/KpiServlet.java
package muralha.digital.kpi;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Kpi")
public class KpiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        String acao = req.getParameter("acao");
        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            if ("valores".equals(acao) || acao == null) {
                JsonArray kpis = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, nome, descricao, query_sql, unidade, " +
                        "threshold_ok, threshold_warn FROM muralha.kpi_config " +
                        "WHERE ativo=1 ORDER BY ordem");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject kpi = new JsonObject();
                        kpi.addProperty("id",       rs.getInt("id"));
                        kpi.addProperty("nome",     rs.getString("nome"));
                        kpi.addProperty("descricao",rs.getString("descricao"));
                        kpi.addProperty("unidade",  rs.getString("unidade"));
                        double thOk   = rs.getDouble("threshold_ok");
                        double thWarn = rs.getDouble("threshold_warn");
                        kpi.addProperty("thOk",   thOk);
                        kpi.addProperty("thWarn", thWarn);

                        // Executar query do KPI — validar que é SELECT
                        String sql = rs.getString("query_sql").trim();
                        if (!sql.toUpperCase().startsWith("SELECT")) {
                            kpi.addProperty("valor",  (Double) null);
                            kpi.addProperty("status", "ERRO");
                            kpis.add(kpi); continue;
                        }
                        try (PreparedStatement kpiPs = conn.prepareStatement(sql);
                             ResultSet kpiRs = kpiPs.executeQuery()) {
                            if (kpiRs.next()) {
                                double valor = kpiRs.getDouble("valor");
                                kpi.addProperty("valor", valor);
                                // Determinar status (lógica: valor deve ser MENOR que threshold)
                                String status;
                                if (thOk > thWarn) {  // maior é melhor (ex.: taxa concordância)
                                    status = valor >= thOk ? "OK" : valor >= thWarn ? "WARN" : "CRIT";
                                } else {              // menor é melhor (ex.: latência, violações)
                                    status = valor <= thOk ? "OK" : valor <= thWarn ? "WARN" : "CRIT";
                                }
                                kpi.addProperty("status", status);
                            }
                        } catch (Exception e) {
                            kpi.addProperty("valor",  (Double) null);
                            kpi.addProperty("status", "ERRO");
                        }
                        kpis.add(kpi);
                    }
                }
                result.addProperty("ok", true);
                result.add("kpis", kpis);

            } else if ("listarConfig".equals(acao)) {
                JsonArray configs = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id, nome, descricao, query_sql, unidade, " +
                        "threshold_ok, threshold_warn, ordem, ativo " +
                        "FROM muralha.kpi_config ORDER BY ordem");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject c = new JsonObject();
                        c.addProperty("id",        rs.getInt("id"));
                        c.addProperty("nome",      rs.getString("nome"));
                        c.addProperty("descricao", rs.getString("descricao"));
                        c.addProperty("querySql",  rs.getString("query_sql"));
                        c.addProperty("unidade",   rs.getString("unidade"));
                        c.addProperty("thOk",      rs.getDouble("threshold_ok"));
                        c.addProperty("thWarn",    rs.getDouble("threshold_warn"));
                        c.addProperty("ordem",     rs.getInt("ordem"));
                        c.addProperty("ativo",     rs.getBoolean("ativo"));
                        configs.add(c);
                    }
                }
                result.addProperty("ok", true);
                result.add("configs", configs);
            } else {
                result.addProperty("ok", false); result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        String acao = req.getParameter("acao");
        Conexao conn = Conexao.getConexao();
        try {
            if ("salvar".equals(acao)) {
                String idStr = req.getParameter("id");
                String sql = req.getParameter("querySql").trim();
                if (!sql.toUpperCase().startsWith("SELECT"))
                    throw new IllegalArgumentException("query_sql deve ser SELECT");

                if (idStr != null && !idStr.isEmpty()) {
                    // UPDATE
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE muralha.kpi_config SET nome=?,descricao=?,query_sql=?," +
                            "unidade=?,threshold_ok=?,threshold_warn=?,ordem=?,ativo=? WHERE id=?")) {
                        ps.setString(1, req.getParameter("nome"));
                        ps.setString(2, req.getParameter("descricao"));
                        ps.setString(3, sql);
                        ps.setString(4, req.getParameter("unidade"));
                        ps.setDouble(5, Double.parseDouble(req.getParameter("thOk")));
                        ps.setDouble(6, Double.parseDouble(req.getParameter("thWarn")));
                        ps.setInt(7, Integer.parseInt(req.getParameter("ordem")));
                        ps.setBoolean(8, "true".equals(req.getParameter("ativo")));
                        ps.setInt(9, Integer.parseInt(idStr));
                        ps.executeUpdate();
                    }
                } else {
                    // INSERT
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO muralha.kpi_config (nome,descricao,query_sql,unidade," +
                            "threshold_ok,threshold_warn,ordem) VALUES (?,?,?,?,?,?,?)")) {
                        ps.setString(1, req.getParameter("nome"));
                        ps.setString(2, req.getParameter("descricao"));
                        ps.setString(3, sql);
                        ps.setString(4, req.getParameter("unidade"));
                        ps.setDouble(5, Double.parseDouble(req.getParameter("thOk")));
                        ps.setDouble(6, Double.parseDouble(req.getParameter("thWarn")));
                        ps.setInt(7, Integer.parseInt(req.getParameter("ordem")));
                        ps.executeUpdate();
                    }
                }
                result.addProperty("ok", true);
            } else {
                result.addProperty("ok", false); result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/kpi/KpiServlet.java
git commit -m "Adiciona KpiServlet com execução segura de queries de KPI configuráveis

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Dashboard e tela de configuração

- [ ] **Criar `index.jsp` para o dashboard de KPIs**

```html
<%-- src/main/webapp/muralha-digital/pages/dashboard/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3">
    <h4 class="mb-0"><i class="bi bi-speedometer2 me-2"></i>Dashboard</h4>
    <small class="text-muted ms-2" id="lblAtualizacao"></small>
    <a href="/muralha-digital/pages/administracao/kpis/index.jsp"
       class="btn btn-sm btn-outline-secondary ms-auto">
      <i class="bi bi-gear me-1"></i>Configurar KPIs
    </a>
  </div>
  <div class="row g-3" id="kpiCards"></div>
</div>

<script>
const COR = { OK: 'success', WARN: 'warning', CRIT: 'danger', ERRO: 'secondary' };

async function carregar() {
  const r = await fetch('/MuralhaDigital/Kpi');
  const d = await r.json();
  if (!d.ok) return;
  const container = document.getElementById('kpiCards');
  container.innerHTML = '';
  d.kpis.forEach(kpi => {
    const cor = COR[kpi.status] || 'secondary';
    const valor = kpi.valor != null ? kpi.valor.toLocaleString('pt-BR') : '—';
    container.insertAdjacentHTML('beforeend',
      `<div class="col-md-3 col-sm-6">
        <div class="card border-${cor}">
          <div class="card-body text-center">
            <h6 class="card-title text-muted">${kpi.nome}</h6>
            <h2 class="fw-bold text-${cor}">${valor}</h2>
            <small class="text-muted">${kpi.unidade||''}</small>
            <div class="mt-1">
              <span class="badge bg-${cor}">${kpi.status}</span>
            </div>
          </div>
        </div>
      </div>`);
  });
  document.getElementById('lblAtualizacao').textContent =
    'Atualizado: ' + new Date().toLocaleTimeString('pt-BR');
}

carregar();
setInterval(carregar, 30_000);
</script>
```

- [ ] **Criar tela de configuração de KPIs** (`administracao/kpis/index.jsp`)

A tela lista os KPIs configurados com botão de edição. O formulário de edição usa um `<textarea>` para `query_sql` e campos para nome, unidade, thresholds.

```html
<%-- src/main/webapp/muralha-digital/pages/administracao/kpis/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex mb-3">
    <h4 class="mb-0"><i class="bi bi-gear me-2"></i>Configuração de KPIs</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="abrirForm(null)">
      <i class="bi bi-plus me-1"></i>Novo KPI
    </button>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-sm mb-0">
        <thead><tr><th>Ordem</th><th>Nome</th><th>Unidade</th><th>Thresh. OK</th><th>Thresh. Warn</th><th>Ativo</th><th></th></tr></thead>
        <tbody id="tblKpis"></tbody>
      </table>
    </div>
  </div>
</div>

<!-- Offcanvas de edição -->
<div class="offcanvas offcanvas-end" style="width:500px" id="ocKpi" tabindex="-1">
  <div class="offcanvas-header">
    <h5 class="offcanvas-title">KPI</h5>
    <button class="btn-close" data-bs-dismiss="offcanvas"></button>
  </div>
  <div class="offcanvas-body">
    <input type="hidden" id="kpiId">
    <div class="mb-2"><label class="form-label">Nome</label>
      <input id="kpiNome" class="form-control"></div>
    <div class="mb-2"><label class="form-label">Descrição</label>
      <input id="kpiDescricao" class="form-control"></div>
    <div class="mb-2"><label class="form-label">SQL (deve retornar coluna "valor")</label>
      <textarea id="kpiSql" class="form-control font-monospace" rows="4"></textarea></div>
    <div class="row mb-2">
      <div class="col"><label class="form-label">Unidade</label>
        <input id="kpiUnidade" class="form-control"></div>
      <div class="col"><label class="form-label">Ordem</label>
        <input type="number" id="kpiOrdem" class="form-control" value="0"></div>
    </div>
    <div class="row mb-2">
      <div class="col"><label class="form-label">Threshold OK</label>
        <input type="number" step="any" id="kpiThOk" class="form-control"></div>
      <div class="col"><label class="form-label">Threshold Warn</label>
        <input type="number" step="any" id="kpiThWarn" class="form-control"></div>
    </div>
    <div class="mb-3 form-check">
      <input type="checkbox" class="form-check-input" id="kpiAtivo">
      <label class="form-check-label" for="kpiAtivo">Ativo</label>
    </div>
    <button class="btn btn-primary w-100" onclick="salvar()">Salvar</button>
  </div>
</div>

<script>
async function carregar() {
  const r = await fetch('/MuralhaDigital/Kpi?acao=listarConfig');
  const d = await r.json();
  const tbody = document.getElementById('tblKpis');
  tbody.innerHTML = '';
  d.configs.forEach(c => {
    tbody.insertAdjacentHTML('beforeend',
      `<tr>
        <td>${c.ordem}</td><td>${c.nome}</td><td>${c.unidade||''}</td>
        <td>${c.thOk}</td><td>${c.thWarn}</td>
        <td><span class="badge bg-${c.ativo?'success':'secondary'}">${c.ativo?'Sim':'Não'}</span></td>
        <td><button class="btn btn-xs btn-outline-primary" onclick='abrirForm(${JSON.stringify(c)})'>
          <i class="bi bi-pencil"></i></button></td>
      </tr>`);
  });
}

function abrirForm(c) {
  document.getElementById('kpiId').value       = c?.id || '';
  document.getElementById('kpiNome').value     = c?.nome || '';
  document.getElementById('kpiDescricao').value= c?.descricao || '';
  document.getElementById('kpiSql').value      = c?.querySql || '';
  document.getElementById('kpiUnidade').value  = c?.unidade || '';
  document.getElementById('kpiOrdem').value    = c?.ordem || 0;
  document.getElementById('kpiThOk').value     = c?.thOk ?? '';
  document.getElementById('kpiThWarn').value   = c?.thWarn ?? '';
  document.getElementById('kpiAtivo').checked  = c?.ativo !== false;
  new bootstrap.Offcanvas(document.getElementById('ocKpi')).show();
}

async function salvar() {
  const body = new URLSearchParams({
    acao: 'salvar',
    id:        document.getElementById('kpiId').value,
    nome:      document.getElementById('kpiNome').value,
    descricao: document.getElementById('kpiDescricao').value,
    querySql:  document.getElementById('kpiSql').value,
    unidade:   document.getElementById('kpiUnidade').value,
    ordem:     document.getElementById('kpiOrdem').value,
    thOk:      document.getElementById('kpiThOk').value,
    thWarn:    document.getElementById('kpiThWarn').value,
    ativo:     document.getElementById('kpiAtivo').checked
  });
  const r = await fetch('/MuralhaDigital/Kpi', { method: 'POST', body });
  const d = await r.json();
  if (d.ok) {
    bootstrap.Offcanvas.getInstance(document.getElementById('ocKpi')).hide();
    carregar();
  } else Swal.fire('Erro', d.erro, 'error');
}

carregar();
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/dashboard/index.jsp`
2. Verificar 4 cards com valores e cores corretas
3. Acessar configuração de KPIs → editar threshold de um KPI → verificar reflexo no dashboard

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/pages/dashboard/index.jsp
git add src/main/webapp/muralha-digital/pages/administracao/kpis/index.jsp
git commit -m "Adiciona dashboard de KPIs configuráveis e tela de administração

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
