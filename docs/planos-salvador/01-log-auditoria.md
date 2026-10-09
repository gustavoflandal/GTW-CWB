# Log de Auditoria Centralizado — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans` para executar tarefa por tarefa com checkpoints.

**Goal:** Criar serviço centralizado de auditoria que registre usuário, IP, timestamp, funcionalidade, operação e ID do registro em todas as ações críticas do sistema, com tela de consulta filtrada e exportação PDF/CSV/XLS.

**Architecture:** Nova tabela `dbo.sis_log_auditoria` + classe utilitária estática `AuditoriaService` invocada nos servlets existentes. Tela de consulta em JSP com servlet dedicado. Exportação via SheetJS (XLS/CSV já disponível no projeto) e jsPDF (já disponível).

**Tech Stack:** Java 13 · SQL Server · JDBC puro · Bootstrap 5.3 · SheetJS · jsPDF+autotable

## Global Constraints

- `Conexao.getConexao()` para todas as conexões; fechar em `finally`
- `PreparedStatement` com parâmetros posicionais — sem concatenação de entrada do usuário
- IP via `request.getHeader("X-Forwarded-For")` com fallback para `request.getRemoteAddr()`
- Novo servlet em `src/main/java/muralha/digital/auditoria/AuditoriaServlet.java`
- Nova JSP em `src/main/webapp/muralha-digital/pages/auditoria/consulta-log.jsp`
- Migração SQL em `docs/banco-de-dados/migracoes/20261008_log_auditoria.sql`
- Build: `..\.setup-gtw\build.ps1` → `BUILD SUCCESS`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_log_auditoria.sql` |
| Criar | `src/main/java/muralha/digital/auditoria/AuditoriaService.java` |
| Criar | `src/main/java/muralha/digital/auditoria/AuditoriaServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/auditoria/consulta-log.jsp` |
| Criar | `src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js` |
| Modificar | `src/main/java/muralha/digital/processamento/ProcessamentoServlet.java` (exemplo) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar o script de migração**

```sql
-- docs/banco-de-dados/migracoes/20261008_log_auditoria.sql
-- ROLLBACK: DROP TABLE dbo.sis_log_auditoria; DROP INDEX IF EXISTS ...

CREATE TABLE dbo.sis_log_auditoria (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_usuario      INT              NULL,
    login           VARCHAR(50)      NULL,
    dt_operacao     DATETIME2(3)     NOT NULL DEFAULT SYSDATETIME(),
    ip_terminal     VARCHAR(45)      NULL,
    funcionalidade  VARCHAR(100)     NOT NULL,
    operacao        VARCHAR(50)      NOT NULL,
    id_registro     VARCHAR(100)     NULL,
    descricao       VARCHAR(500)     NULL
);

CREATE INDEX ix_sla_dt       ON dbo.sis_log_auditoria (dt_operacao);
CREATE INDEX ix_sla_usuario  ON dbo.sis_log_auditoria (id_usuario);
CREATE INDEX ix_sla_func     ON dbo.sis_log_auditoria (funcionalidade);
```

- [ ] **Executar no banco dev**

```powershell
# No SQL Server Management Studio ou via sqlcmd:
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_log_auditoria.sql"
```

Esperado: `(0 rows affected)` sem erros.

- [ ] **Verificar tabela criada**

```sql
SELECT TOP 1 * FROM dbo.sis_log_auditoria;
-- Esperado: "0 rows" sem erro (tabela existe e está acessível)
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_log_auditoria.sql
git commit -m "Adiciona tabela sis_log_auditoria para auditoria centralizada

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: AuditoriaService (classe utilitária)

- [ ] **Criar `AuditoriaService.java`**

```java
// src/main/java/muralha/digital/auditoria/AuditoriaService.java
package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;

public final class AuditoriaService {

    private AuditoriaService() {}

    /**
     * Registra uma ação auditável. Nunca lança exceção — falha silenciosa
     * para não interromper o fluxo principal.
     */
    public static void registrar(
            HttpServletRequest request,
            String funcionalidade,
            String operacao,
            String idRegistro,
            String descricao) {

        String login = null;
        Integer idUsuario = null;
        try {
            HttpSession sess = request.getSession(false);
            if (sess != null) {
                Object u = sess.getAttribute("usuario");
                if (u != null) {
                    // Reflexão para não criar dependência circular com a classe Usuario legada
                    login      = (String) u.getClass().getMethod("getLogin").invoke(u);
                    idUsuario  = (Integer) u.getClass().getMethod("getId").invoke(u);
                }
            }
        } catch (Exception ignored) {}

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();

        final String loginFinal     = login;
        final Integer idUsuarioFinal = idUsuario;
        final String ipFinal        = ip;

        // Assíncrono: não bloqueia o thread da requisição
        new Thread(() -> {
            Conexao conn = null;
            try {
                conn = Conexao.getConexao();
                String sql = "INSERT INTO dbo.sis_log_auditoria " +
                             "(id_usuario, login, ip_terminal, funcionalidade, operacao, id_registro, descricao) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setObject(1, idUsuarioFinal);   // aceita null
                    ps.setString(2, loginFinal);
                    ps.setString(3, ipFinal);
                    ps.setString(4, funcionalidade);
                    ps.setString(5, operacao);
                    ps.setString(6, idRegistro);
                    ps.setString(7, descricao);
                    ps.executeUpdate();
                }
            } catch (Exception ignored) {
            } finally {
                if (conn != null) try { conn.close(); } catch (Exception ignored) {}
            }
        }).start();
    }
}
```

- [ ] **Verificar compilação**

```powershell
..\.setup-gtw\build.ps1
```

Esperado: `BUILD SUCCESS`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/auditoria/AuditoriaService.java
git commit -m "Adiciona AuditoriaService para registro centralizado de auditoria

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: AuditoriaServlet (consulta)

- [ ] **Criar `AuditoriaServlet.java`**

```java
// src/main/java/muralha/digital/auditoria/AuditoriaServlet.java
package muralha.digital.auditoria;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muralha.digital._ini.Acesso;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/MuralhaDigital/Auditoria")
public class AuditoriaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String acao = req.getParameter("acao");
        if (acao == null) { resp.sendError(400); return; }

        if ("consultar".equals(acao)) {
            out.print(consultar(req));
        } else {
            resp.sendError(400, "acao invalida");
        }
    }

    private String consultar(HttpServletRequest req) {
        String login        = req.getParameter("login");
        String funcional    = req.getParameter("funcionalidade");
        String operacao     = req.getParameter("operacao");
        String dtInicio     = req.getParameter("dtInicio");  // yyyy-MM-dd
        String dtFim        = req.getParameter("dtFim");
        String pagStr       = req.getParameter("pagina");
        int pagina          = pagStr != null ? Integer.parseInt(pagStr) : 1;
        int tamPag          = 50;

        StringBuilder sql = new StringBuilder(
            "SELECT id, id_usuario, login, dt_operacao, ip_terminal, " +
            "funcionalidade, operacao, id_registro, descricao " +
            "FROM dbo.sis_log_auditoria WHERE 1=1 ");
        if (login != null && !login.isEmpty())     sql.append("AND login LIKE ? ");
        if (funcional != null && !funcional.isEmpty()) sql.append("AND funcionalidade = ? ");
        if (operacao != null && !operacao.isEmpty())   sql.append("AND operacao = ? ");
        if (dtInicio != null && !dtInicio.isEmpty())   sql.append("AND dt_operacao >= ? ");
        if (dtFim    != null && !dtFim.isEmpty())      sql.append("AND dt_operacao < DATEADD(day,1,?) ");
        sql.append("ORDER BY dt_operacao DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        JsonObject result = new JsonObject();
        JsonArray rows    = new JsonArray();

        Conexao conn = null;
        try {
            conn = Conexao.getConexao();
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int i = 1;
                if (login != null && !login.isEmpty())     ps.setString(i++, "%" + login + "%");
                if (funcional != null && !funcional.isEmpty()) ps.setString(i++, funcional);
                if (operacao != null && !operacao.isEmpty())   ps.setString(i++, operacao);
                if (dtInicio != null && !dtInicio.isEmpty())   ps.setString(i++, dtInicio);
                if (dtFim    != null && !dtFim.isEmpty())      ps.setString(i++, dtFim);
                ps.setInt(i++, (pagina - 1) * tamPag);
                ps.setInt(i,   tamPag);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject row = new JsonObject();
                        row.addProperty("id",           rs.getLong("id"));
                        row.addProperty("login",        rs.getString("login"));
                        row.addProperty("dtOperacao",   String.valueOf(rs.getTimestamp("dt_operacao")));
                        row.addProperty("ip",           rs.getString("ip_terminal"));
                        row.addProperty("funcional",    rs.getString("funcionalidade"));
                        row.addProperty("operacao",     rs.getString("operacao"));
                        row.addProperty("idRegistro",   rs.getString("id_registro"));
                        row.addProperty("descricao",    rs.getString("descricao"));
                        rows.add(row);
                    }
                }
            }
            result.addProperty("ok", true);
            result.add("registros", rows);
        } catch (SQLException e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        return new Gson().toJson(result);
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

Esperado: `BUILD SUCCESS`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/auditoria/AuditoriaServlet.java
git commit -m "Adiciona AuditoriaServlet com acao=consultar paginada

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Tela de Consulta de Log

- [ ] **Criar `consulta-log.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/auditoria/consulta-log.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-shield-lock me-2"></i>Consulta de Log de Auditoria</h4>

  <div class="card mb-3">
    <div class="card-body row g-2">
      <div class="col-md-2">
        <label class="form-label">Login</label>
        <input id="fLogin" class="form-control form-control-sm" placeholder="usuário">
      </div>
      <div class="col-md-2">
        <label class="form-label">Funcionalidade</label>
        <input id="fFunc" class="form-control form-control-sm">
      </div>
      <div class="col-md-2">
        <label class="form-label">Operação</label>
        <input id="fOper" class="form-control form-control-sm">
      </div>
      <div class="col-md-2">
        <label class="form-label">De</label>
        <input id="fDtIni" type="date" class="form-control form-control-sm">
      </div>
      <div class="col-md-2">
        <label class="form-label">Até</label>
        <input id="fDtFim" type="date" class="form-control form-control-sm">
      </div>
      <div class="col-md-2 d-flex align-items-end gap-1">
        <button class="btn btn-primary btn-sm" onclick="buscar(1)">
          <i class="bi bi-search"></i> Buscar
        </button>
        <button class="btn btn-outline-secondary btn-sm" onclick="exportarCSV()">CSV</button>
        <button class="btn btn-outline-secondary btn-sm" onclick="exportarXLS()">XLS</button>
      </div>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-sm table-hover" id="tblLog">
      <thead class="table-dark">
        <tr>
          <th>Data/Hora</th><th>Login</th><th>IP</th>
          <th>Funcionalidade</th><th>Operação</th><th>ID Registro</th><th>Descrição</th>
        </tr>
      </thead>
      <tbody id="tbodyLog"></tbody>
    </table>
  </div>
  <div id="paginacao" class="d-flex gap-2 mt-2"></div>
</div>
<script src="/muralha-digital/assets/js/auditoria/consulta-log.js"></script>
<%@ include file="/muralha-digital/utils/credenciais/rodape.jsp" %>
```

- [ ] **Criar `consulta-log.js`**

```javascript
// src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js
let paginaAtual = 1;
let ultimaResposta = [];

function buscar(pag) {
  paginaAtual = pag || 1;
  $.get('/MuralhaDigital/Auditoria', {
    acao: 'consultar',
    login:          $('#fLogin').val(),
    funcionalidade: $('#fFunc').val(),
    operacao:       $('#fOper').val(),
    dtInicio:       $('#fDtIni').val(),
    dtFim:          $('#fDtFim').val(),
    pagina:         paginaAtual
  }, function(data) {
    if (!data.ok) { bs_alert('Erro ao consultar: ' + data.erro); return; }
    ultimaResposta = data.registros;
    const tbody = $('#tbodyLog').empty();
    data.registros.forEach(r => {
      tbody.append(`<tr>
        <td>${r.dtOperacao}</td><td>${r.login||''}</td><td>${r.ip||''}</td>
        <td>${r.funcional}</td><td>${r.operacao}</td>
        <td>${r.idRegistro||''}</td><td>${r.descricao||''}</td>
      </tr>`);
    });
    $('#paginacao').html(
      paginaAtual > 1
        ? `<button class="btn btn-sm btn-outline-secondary" onclick="buscar(${paginaAtual-1})">← Anterior</button>`
        : ''
    ).append(
      data.registros.length === 50
        ? `<button class="btn btn-sm btn-outline-secondary" onclick="buscar(${paginaAtual+1})">Próxima →</button>`
        : ''
    );
  });
}

function exportarCSV() {
  if (!ultimaResposta.length) { bs_alert('Busque antes de exportar.'); return; }
  const cols = ['dtOperacao','login','ip','funcional','operacao','idRegistro','descricao'];
  const header = 'Data/Hora,Login,IP,Funcionalidade,Operação,ID Registro,Descrição\n';
  const rows = ultimaResposta.map(r => cols.map(c => '"'+(r[c]||'')+'"').join(',')).join('\n');
  const blob = new Blob(['﻿'+header+rows], {type:'text/csv;charset=utf-8'});
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'log_auditoria.csv';
  a.click();
}

function exportarXLS() {
  if (!ultimaResposta.length) { bs_alert('Busque antes de exportar.'); return; }
  // Usa SheetJS (XLSX) já disponível no projeto
  const ws_data = [['Data/Hora','Login','IP','Funcionalidade','Operação','ID Registro','Descrição']];
  ultimaResposta.forEach(r =>
    ws_data.push([r.dtOperacao,r.login,r.ip,r.funcional,r.operacao,r.idRegistro,r.descricao])
  );
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, XLSX.utils.aoa_to_sheet(ws_data), 'Log');
  XLSX.writeFile(wb, 'log_auditoria.xlsx');
}
```

- [ ] **Cadastrar menu e permissão no banco**

```sql
-- Executar no GTW_MURALHA_DEV
-- 1. Inserir menu (ajustar id_pai conforme menu de configuração)
INSERT INTO dbo.sis_menu_infos (nm_menu, ds_acao, id_menu_pai, nr_ordem, fl_ativo)
VALUES ('Log de Auditoria', '/muralha-digital/pages/auditoria/consulta-log.jsp', 16, 99, 1);

-- 2. Dar acesso ao grupo de administradores (id_grupo = 1, ajustar conforme banco)
INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo)
SELECT SCOPE_IDENTITY(), 1;
```

- [ ] **Subir servidor e testar manualmente**

```powershell
..\.setup-gtw\run.ps1
```

Abrir `http://localhost:8080/muralha-digital/pages/auditoria/consulta-log.jsp` — deve exibir a tela sem erros no console Tomcat.

Clicar "Buscar" com filtros vazios → deve retornar lista vazia (tabela ainda sem dados).

- [ ] **Build final e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/webapp/muralha-digital/pages/auditoria/
git add src/main/webapp/muralha-digital/assets/js/auditoria/
git commit -m "Adiciona tela de consulta de log de auditoria com exportação CSV/XLS

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 5: Instrumentar Servlets Críticos

Invocar `AuditoriaService.registrar(...)` nos pontos de maior risco. Prioridade para PoC:

- [ ] **Adicionar chamada em `ProcessamentoServlet`** (validação de infrações)

```java
// Localizar o trecho de validação final no ProcessamentoServlet (acao=validar ou similar)
// Após o UPDATE de status, adicionar:
AuditoriaService.registrar(
    request,
    "Processamento",       // funcionalidade
    "validar-infracao",    // operacao
    String.valueOf(idInfracao),
    "Infração " + idInfracao + " validada como " + classificacao
);
```

- [ ] **Adicionar chamada no login** (`login_action.jsp` ou `UsuarioServlet`)

```java
// Após login bem-sucedido:
AuditoriaService.registrar(request, "Acesso", "login", String.valueOf(idUsuario), "Login realizado");

// Após logout:
AuditoriaService.registrar(request, "Acesso", "logout", String.valueOf(idUsuario), "Logout realizado");
```

- [ ] **Adicionar chamada em exportações de relatório**

```java
// Em cada RelatorioServlet, antes de devolver o arquivo:
AuditoriaService.registrar(request, "Relatorio", "exportar-" + tipoRelatorio,
    null, "Exportação: " + filtros.toString());
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Teste de fumaça**: fazer login, validar uma infração, exportar um relatório → consultar log de auditoria → verificar que os 3 eventos aparecem na tela.

- [ ] **Commit**

```bash
git add src/main/java/
git commit -m "Instrumenta servlets críticos com registro de auditoria

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
