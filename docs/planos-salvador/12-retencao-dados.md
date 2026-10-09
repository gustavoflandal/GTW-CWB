# Política de Retenção de Dados — Plano de Implementação

> **Depende de:** plano `01-log-auditoria.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Implementar política de retenção configurável: registros com mais de N anos são arquivados em tabelas `_historico` e removidos das tabelas principais; log de auditoria de todas as operações de expurgo.

**Architecture:** Tabelas `_historico` espelham o schema das originais. Job Quartz mensal copia registros antigos para o histórico e os exclui da tabela principal. Parâmetro `retencao_anos` configurável via `muralha.configuracao`. Servlet exibe status da política e histórico de execuções.

**Tech Stack:** Quartz Scheduler · SQL Server · Bootstrap 5.3

## Global Constraints

- Quartz já no `pom.xml`; sem nova dependência
- Operação de expurgo sempre via transação; rollback em caso de erro
- Registros com pendência de análise (PENDENTE) nunca são expurgados
- Logs de expurgo em `dbo.sis_log_auditoria` (plano 01)
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_retencao.sql` |
| Criar | `src/main/java/muralha/digital/retencao/RetencaoJob.java` |
| Criar | `src/main/java/muralha/digital/retencao/RetencaoServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/administracao/retencao/index.jsp` |
| Modificar | Agendador Quartz |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_retencao.sql
-- ROLLBACK:
--   DROP TABLE muralha.veiculo_tempo_real_historico;
--   DROP TABLE muralha.expurgo_log;

-- Tabela histórico (mesma estrutura da principal — sem índices de performance)
SELECT TOP 0 * INTO muralha.veiculo_tempo_real_historico
FROM muralha.veiculo_tempo_real;

ALTER TABLE muralha.veiculo_tempo_real_historico
    ADD dt_expurgo DATETIME2(3) NOT NULL DEFAULT SYSDATETIME();

-- Log de operações de expurgo
CREATE TABLE muralha.expurgo_log (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    dt_execucao     DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    tabela          VARCHAR(100) NOT NULL,
    registros_movidos INT        NOT NULL,
    retencao_anos   INT          NOT NULL,
    dt_corte        DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL,   -- SUCESSO / ERRO
    mensagem        VARCHAR(500) NULL
);

-- Configuração de retenção
INSERT INTO muralha.configuracao (chave, valor, descricao)
VALUES ('retencao_anos', '5',
        'Período de retenção de passagens em anos antes do expurgo para histórico');
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_retencao.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_retencao.sql
git commit -m "Adiciona tabela histórico e log de expurgo para política de retenção

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: RetencaoJob

- [ ] **Criar `RetencaoJob.java`**

```java
// src/main/java/muralha/digital/retencao/RetencaoJob.java
package muralha.digital.retencao;

import com.consilux.lib.Conexao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.*;
import java.time.LocalDate;

public class RetencaoJob implements Job {

    private static final int DEFAULT_RETENCAO_ANOS = 5;

    @Override
    public void execute(JobExecutionContext ctx) {
        Conexao conn = null;
        int movidos = 0;
        String status = "SUCESSO";
        String mensagem = null;
        int anosRetencao = DEFAULT_RETENCAO_ANOS;

        try {
            conn = Conexao.getConexao();
            anosRetencao = obterRetencaoAnos(conn);
            LocalDate dtCorte = LocalDate.now().minusYears(anosRetencao);

            conn.setAutoCommit(false);
            try {
                // Inserir no histórico registros com status_analise finalizado e antigos
                String sqlInsert =
                    "INSERT INTO muralha.veiculo_tempo_real_historico " +
                    "SELECT *, SYSDATETIME() " +
                    "FROM muralha.veiculo_tempo_real " +
                    "WHERE CAST(dt_recepcao_servidor AS DATE) < '" + dtCorte + "' " +
                    "  AND status_analise IN ('PRE_APROVADA','REPROVADA','DESEMPATE') " +
                    "  AND status_analise IS NOT NULL";

                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    movidos = ps.executeUpdate();
                }

                if (movidos > 0) {
                    // Remover da tabela principal
                    String sqlDelete =
                        "DELETE FROM muralha.veiculo_tempo_real " +
                        "WHERE CAST(dt_recepcao_servidor AS DATE) < '" + dtCorte + "' " +
                        "  AND status_analise IN ('PRE_APROVADA','REPROVADA','DESEMPATE') " +
                        "  AND status_analise IS NOT NULL";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                        ps.executeUpdate();
                    }
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                status = "ERRO";
                mensagem = e.getMessage();
                movidos = 0;
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.err.println("[RetencaoJob] Erro: " + e.getMessage());
            status = "ERRO";
            mensagem = e.getMessage();
        } finally {
            // Registrar log de expurgo
            if (conn != null) {
                try {
                    LocalDate dtCorte = LocalDate.now().minusYears(anosRetencao);
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO muralha.expurgo_log " +
                            "(tabela, registros_movidos, retencao_anos, dt_corte, status, mensagem) " +
                            "VALUES ('veiculo_tempo_real',?,?,?,?,?)")) {
                        ps.setInt(1, movidos);
                        ps.setInt(2, anosRetencao);
                        ps.setString(3, dtCorte.toString());
                        ps.setString(4, status);
                        ps.setString(5, mensagem);
                        ps.executeUpdate();
                    }
                } catch (Exception ignored) {}
                try { conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    private int obterRetencaoAnos(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT valor FROM muralha.configuracao WHERE chave = 'retencao_anos'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Integer.parseInt(rs.getString("valor"));
        }
        return DEFAULT_RETENCAO_ANOS;
    }
}
```

- [ ] **Registrar job no agendador Quartz (rodar mensalmente, no dia 1 à meia-noite)**

```java
JobDetail retencaoJob = JobBuilder.newJob(RetencaoJob.class)
    .withIdentity("retencaoJob", "administracao")
    .build();

Trigger retencaoTrigger = TriggerBuilder.newTrigger()
    .withIdentity("retencaoTrigger", "administracao")
    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1 * ?")) // 02:00 no dia 1 de cada mês
    .build();

scheduler.scheduleJob(retencaoJob, retencaoTrigger);
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/retencao/RetencaoJob.java
git add src/main/java/   # agendador modificado
git commit -m "Adiciona RetencaoJob Quartz para expurgo mensal de registros antigos

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Servlet e painel de administração

- [ ] **Criar `RetencaoServlet.java`**

```java
// src/main/java/muralha/digital/retencao/RetencaoServlet.java
package muralha.digital.retencao;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/MuralhaDigital/Retencao")
public class RetencaoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");

        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            // Configuração atual
            String valorAtual = "5";
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT valor FROM muralha.configuracao WHERE chave = 'retencao_anos'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) valorAtual = rs.getString("valor");
            }
            result.addProperty("retencaoAnos", Integer.parseInt(valorAtual));

            // Estimativa de registros que seriam expurgados
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) cnt FROM muralha.veiculo_tempo_real " +
                    "WHERE CAST(dt_recepcao_servidor AS DATE) < DATEADD(YEAR,-" + valorAtual + ",GETDATE()) " +
                    "  AND status_analise IN ('PRE_APROVADA','REPROVADA','DESEMPATE')");
                 ResultSet rs = ps.executeQuery()) {
                result.addProperty("estimativaExpurgo", rs.next() ? rs.getInt("cnt") : 0);
            }

            // Total no histórico
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) cnt FROM muralha.veiculo_tempo_real_historico");
                 ResultSet rs = ps.executeQuery()) {
                result.addProperty("totalHistorico", rs.next() ? rs.getInt("cnt") : 0);
            }

            // Últimas 10 execuções de expurgo
            JsonArray logs = new JsonArray();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOP 10 * FROM muralha.expurgo_log ORDER BY id DESC");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JsonObject row = new JsonObject();
                    row.addProperty("dt",      rs.getString("dt_execucao"));
                    row.addProperty("movidos", rs.getInt("registros_movidos"));
                    row.addProperty("corte",   rs.getString("dt_corte"));
                    row.addProperty("status",  rs.getString("status"));
                    row.addProperty("msg",     rs.getString("mensagem"));
                    logs.add(row);
                }
            }
            result.add("logs", logs);
            result.addProperty("ok", true);

        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
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
            if ("configurar".equals(acao)) {
                int anos = Integer.parseInt(req.getParameter("anos"));
                if (anos < 1 || anos > 20) throw new IllegalArgumentException("Período inválido (1-20 anos)");
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.configuracao SET valor=? WHERE chave='retencao_anos'")) {
                    ps.setString(1, String.valueOf(anos));
                    ps.executeUpdate();
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

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/administracao/retencao/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-archive me-2"></i>Política de Retenção de Dados</h4>

  <div class="row g-3 mb-4">
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Retenção atual</h6>
          <h2 id="lblAnos" class="fw-bold">—</h2>
          <small class="text-muted">anos</small>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Estimativa para próximo expurgo</h6>
          <h2 id="lblEstimativa" class="fw-bold text-warning">—</h2>
          <small class="text-muted">registros</small>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h6 class="text-muted">Total no histórico</h6>
          <h2 id="lblHistorico" class="fw-bold">—</h2>
          <small class="text-muted">registros</small>
        </div>
      </div>
    </div>
  </div>

  <div class="card mb-3">
    <div class="card-header">Configurar retenção</div>
    <div class="card-body d-flex gap-2 align-items-end">
      <div>
        <label class="form-label">Período de retenção (anos)</label>
        <input type="number" id="inputAnos" class="form-control" min="1" max="20" style="width:100px">
      </div>
      <button class="btn btn-primary" onclick="salvarConfig()">Salvar</button>
    </div>
  </div>

  <div class="card">
    <div class="card-header">Histórico de execuções</div>
    <div class="card-body p-0">
      <table class="table table-sm table-striped mb-0">
        <thead><tr><th>Data/Hora</th><th>Registros movidos</th><th>Data corte</th><th>Status</th><th>Mensagem</th></tr></thead>
        <tbody id="tblLogs"></tbody>
      </table>
    </div>
  </div>
</div>

<script>
async function carregar() {
  const r = await fetch('/MuralhaDigital/Retencao');
  const d = await r.json();
  if (!d.ok) return;
  document.getElementById('lblAnos').textContent       = d.retencaoAnos;
  document.getElementById('lblEstimativa').textContent = d.estimativaExpurgo;
  document.getElementById('lblHistorico').textContent  = d.totalHistorico;
  document.getElementById('inputAnos').value           = d.retencaoAnos;

  const tbody = document.getElementById('tblLogs');
  tbody.innerHTML = '';
  d.logs.forEach(l => {
    tbody.insertAdjacentHTML('beforeend',
      `<tr class="${l.status==='ERRO'?'table-danger':''}">
        <td>${l.dt}</td><td>${l.movidos}</td><td>${l.corte}</td>
        <td><span class="badge bg-${l.status==='SUCESSO'?'success':'danger'}">${l.status}</span></td>
        <td>${l.msg||''}</td>
      </tr>`);
  });
}

async function salvarConfig() {
  const anos = document.getElementById('inputAnos').value;
  const r = await fetch('/MuralhaDigital/Retencao', {
    method: 'POST', body: new URLSearchParams({ acao: 'configurar', anos })
  });
  const d = await r.json();
  if (d.ok) { Swal.fire('Configuração salva!', '', 'success'); carregar(); }
  else      { Swal.fire('Erro', d.erro, 'error'); }
}

carregar();
</script>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar `http://localhost:8080/muralha-digital/pages/administracao/retencao/index.jsp`
2. Verificar cards de status
3. Alterar período de retenção e salvar
4. Verificar `SELECT valor FROM muralha.configuracao WHERE chave='retencao_anos';`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/retencao/RetencaoServlet.java
git add src/main/webapp/muralha-digital/pages/administracao/retencao/index.jsp
git commit -m "Adiciona painel de administração da política de retenção de dados

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
