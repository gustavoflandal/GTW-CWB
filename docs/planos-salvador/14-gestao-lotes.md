# Gestão de Lotes de Infrações — Plano de Implementação

> **Depende de:** plano `05-dupla-analise.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Implementar agrupamento de infrações pré-aprovadas em lotes para encaminhamento ao DETRAN/DENATRAN; painel de gestão de lotes com criação, visualização, envio e acompanhamento de status.

**Architecture:** Tabela `muralha.lote_infracao` agrupa IDs de infrações PRE_APROVADA. Servlet gerencia ciclo de vida: RASCUNHO → ENVIADO → CONFIRMADO. JSP com tabela de lotes e modal de detalhes.

**Tech Stack:** SQL Server · Java Servlet · Bootstrap 5.3 · SheetJS (já no projeto)

## Global Constraints

- Infração só entra em lote se `status_analise = 'PRE_APROVADA'`
- Uma infração não pode estar em dois lotes ativos simultaneamente
- Lote ENVIADO não pode ser alterado
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_lotes.sql` |
| Criar | `src/main/java/muralha/digital/lote/LoteServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/processamento/lotes/index.jsp` |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_lotes.sql
-- ROLLBACK:
--   DROP TABLE muralha.lote_infracao_item;
--   DROP TABLE muralha.lote_infracao;

CREATE TABLE muralha.lote_infracao (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo           VARCHAR(30) NOT NULL UNIQUE,   -- ex.: LOTE-2026100801
    descricao        VARCHAR(200) NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',  -- RASCUNHO/ENVIADO/CONFIRMADO/CANCELADO
    dt_criacao       DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    dt_envio         DATETIME2   NULL,
    dt_confirmacao   DATETIME2   NULL,
    id_usuario_criou INT         NOT NULL,
    id_usuario_enviou INT        NULL,
    observacao       VARCHAR(500) NULL
);

CREATE TABLE muralha.lote_infracao_item (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_lote         BIGINT NOT NULL REFERENCES muralha.lote_infracao(id),
    id_infracao     BIGINT NOT NULL,
    dt_inclusao     DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT uq_lote_item UNIQUE (id_infracao)  -- uma infração por lote ativo
);

CREATE INDEX ix_lote_status ON muralha.lote_infracao (status);
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_lotes.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_lotes.sql
git commit -m "Adiciona tabelas de gestão de lotes de infrações

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: LoteServlet

- [ ] **Criar `LoteServlet.java`**

```java
// src/main/java/muralha/digital/lote/LoteServlet.java
package muralha.digital.lote;

import com.consilux.lib.Conexao;
import com.google.gson.*;
import muralha.digital._ini.Acesso;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@WebServlet("/MuralhaDigital/Lote")
public class LoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        Conexao conn = Conexao.getConexao();
        JsonObject result = new JsonObject();
        try {
            String acao = req.getParameter("acao");

            if ("listar".equals(acao)) {
                JsonArray lotes = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT l.*, " +
                        "  (SELECT COUNT(*) FROM muralha.lote_infracao_item WHERE id_lote=l.id) qtd " +
                        "FROM muralha.lote_infracao l " +
                        "ORDER BY l.id DESC");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        JsonObject l = new JsonObject();
                        l.addProperty("id",       rs.getLong("id"));
                        l.addProperty("codigo",   rs.getString("codigo"));
                        l.addProperty("status",   rs.getString("status"));
                        l.addProperty("dtCriacao",rs.getString("dt_criacao"));
                        l.addProperty("dtEnvio",  rs.getString("dt_envio"));
                        l.addProperty("qtd",      rs.getInt("qtd"));
                        l.addProperty("descricao",rs.getString("descricao"));
                        lotes.add(l);
                    }
                }
                result.addProperty("ok", true);
                result.add("lotes", lotes);

            } else if ("itens".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                JsonArray itens = new JsonArray();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT li.id_infracao, vtr.placa, vtr.equipamento, " +
                        "  CONVERT(VARCHAR,vtr.dt_captura_equipamento,120) dt_captura " +
                        "FROM muralha.lote_infracao_item li " +
                        "JOIN muralha.veiculo_tempo_real vtr ON vtr.id=li.id_infracao " +
                        "WHERE li.id_lote=?")) {
                    ps.setLong(1, idLote);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            JsonObject item = new JsonObject();
                            item.addProperty("id",        rs.getLong("id_infracao"));
                            item.addProperty("placa",     rs.getString("placa"));
                            item.addProperty("equip",     rs.getString("equipamento"));
                            item.addProperty("dtCaptura", rs.getString("dt_captura"));
                            itens.add(item);
                        }
                    }
                }
                result.addProperty("ok", true);
                result.add("itens", itens);

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
        Gson gson = new Gson();
        JsonObject result = new JsonObject();
        String acao = req.getParameter("acao");
        Conexao conn = Conexao.getConexao();
        try {
            int idUsuario = obterIdUsuario(req);

            if ("criar".equals(acao)) {
                String codigo = "LOTE-" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                String descricao = req.getParameter("descricao");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO muralha.lote_infracao (codigo, descricao, id_usuario_criou) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, codigo); ps.setString(2, descricao); ps.setInt(3, idUsuario);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        result.addProperty("idLote", keys.getLong(1));
                    }
                }
                result.addProperty("ok", true); result.addProperty("codigo", codigo);

            } else if ("adicionarInfracoes".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                // idsInfracoes é JSON array de longs
                long[] ids = gson.fromJson(req.getParameter("ids"), long[].class);
                conn.setAutoCommit(false);
                try {
                    int adicionados = 0;
                    for (long idInf : ids) {
                        try (PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO muralha.lote_infracao_item (id_lote, id_infracao) VALUES (?,?)")) {
                            ps.setLong(1, idLote); ps.setLong(2, idInf);
                            adicionados += ps.executeUpdate();
                        }
                    }
                    conn.commit();
                    result.addProperty("ok", true); result.addProperty("adicionados", adicionados);
                } catch (Exception e) { conn.rollback(); throw e; }
                finally { conn.setAutoCommit(true); }

            } else if ("enviar".equals(acao)) {
                long idLote = Long.parseLong(req.getParameter("idLote"));
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE muralha.lote_infracao SET status='ENVIADO', " +
                        "dt_envio=SYSDATETIME(), id_usuario_enviou=? WHERE id=? AND status='RASCUNHO'")) {
                    ps.setInt(1, idUsuario); ps.setLong(2, idLote);
                    int rows = ps.executeUpdate();
                    result.addProperty("ok", rows > 0);
                }
                AuditoriaService.registrar(req, "Lote", "enviar",
                    String.valueOf(idLote), "Lote enviado para DETRAN");

            } else {
                result.addProperty("ok", false); result.addProperty("erro", "acao invalida");
            }
        } catch (Exception e) {
            result.addProperty("ok", false); result.addProperty("erro", e.getMessage());
        } finally {
            try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(gson.toJson(result));
    }

    private int obterIdUsuario(HttpServletRequest req) throws Exception {
        Object u = req.getSession().getAttribute("usuario");
        return (Integer) u.getClass().getMethod("getId").invoke(u);
    }
}
```

- [ ] **Build e commit**

```powershell
..\.setup-gtw\build.ps1
```

```bash
git add src/main/java/muralha/digital/lote/LoteServlet.java
git commit -m "Adiciona LoteServlet para gestão de lotes de infrações pré-aprovadas

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: JSP de gestão de lotes

- [ ] **Criar `index.jsp`**

```html
<%-- src/main/webapp/muralha-digital/pages/processamento/lotes/index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3">
    <h4 class="mb-0"><i class="bi bi-collection me-2"></i>Gestão de Lotes</h4>
    <button class="btn btn-sm btn-primary ms-auto" onclick="criarLote()">
      <i class="bi bi-plus-circle me-1"></i>Novo Lote
    </button>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-sm table-hover mb-0">
        <thead>
          <tr><th>Código</th><th>Descrição</th><th>Status</th><th>Criação</th><th>Envio</th><th>Infrações</th><th></th></tr>
        </thead>
        <tbody id="tblLotes"></tbody>
      </table>
    </div>
  </div>
</div>

<!-- Modal de detalhes -->
<div class="modal fade" id="modalLote" tabindex="-1">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalLoteTitulo">Lote</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <table class="table table-sm">
          <thead><tr><th>ID</th><th>Placa</th><th>Equipamento</th><th>Data Captura</th></tr></thead>
          <tbody id="tblItens"></tbody>
        </table>
      </div>
      <div class="modal-footer">
        <button class="btn btn-success" id="btnEnviar" onclick="enviarLote()">
          <i class="bi bi-send me-1"></i>Enviar para DETRAN
        </button>
        <button class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>

<script>
let loteAtual = null;

const BADGE = {
  RASCUNHO:  'secondary', ENVIADO:   'primary',
  CONFIRMADO:'success',   CANCELADO: 'danger'
};

async function carregar() {
  const r = await fetch('/MuralhaDigital/Lote?acao=listar');
  const d = await r.json();
  if (!d.ok) return;
  const tbody = document.getElementById('tblLotes');
  tbody.innerHTML = '';
  d.lotes.forEach(l => {
    tbody.insertAdjacentHTML('beforeend',
      `<tr>
        <td><strong>${l.codigo}</strong></td>
        <td>${l.descricao||''}</td>
        <td><span class="badge bg-${BADGE[l.status]||'secondary'}">${l.status}</span></td>
        <td>${l.dtCriacao?.substring(0,16)||''}</td>
        <td>${l.dtEnvio?.substring(0,16)||'—'}</td>
        <td>${l.qtd}</td>
        <td>
          <button class="btn btn-xs btn-outline-info" onclick="verItens(${l.id},'${l.codigo}','${l.status}')">
            <i class="bi bi-eye"></i>
          </button>
        </td>
      </tr>`);
  });
}

async function criarLote() {
  const { value: desc } = await Swal.fire({
    title: 'Novo Lote', input: 'text', inputLabel: 'Descrição (opcional)',
    showCancelButton: true
  });
  if (desc === undefined) return;
  const r = await fetch('/MuralhaDigital/Lote', {
    method: 'POST', body: new URLSearchParams({ acao: 'criar', descricao: desc })
  });
  const d = await r.json();
  if (d.ok) { Swal.fire('Lote criado: ' + d.codigo, '', 'success'); carregar(); }
  else       Swal.fire('Erro', d.erro, 'error');
}

async function verItens(idLote, codigo, status) {
  loteAtual = { id: idLote, status };
  document.getElementById('modalLoteTitulo').textContent = codigo;
  document.getElementById('btnEnviar').style.display = status === 'RASCUNHO' ? '' : 'none';

  const r = await fetch('/MuralhaDigital/Lote?acao=itens&idLote=' + idLote);
  const d = await r.json();
  const tbody = document.getElementById('tblItens');
  tbody.innerHTML = '';
  (d.itens||[]).forEach(item => {
    tbody.insertAdjacentHTML('beforeend',
      `<tr><td>${item.id}</td><td>${item.placa}</td><td>${item.equip}</td><td>${item.dtCaptura}</td></tr>`);
  });
  new bootstrap.Modal(document.getElementById('modalLote')).show();
}

async function enviarLote() {
  if (!loteAtual) return;
  const conf = await Swal.fire({ title: 'Confirmar envio?', icon: 'question', showCancelButton: true });
  if (!conf.isConfirmed) return;
  const r = await fetch('/MuralhaDigital/Lote', {
    method: 'POST', body: new URLSearchParams({ acao: 'enviar', idLote: loteAtual.id })
  });
  const d = await r.json();
  if (d.ok) {
    bootstrap.Modal.getInstance(document.getElementById('modalLote')).hide();
    Swal.fire('Lote enviado!', '', 'success');
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

1. Acessar `http://localhost:8080/muralha-digital/pages/processamento/lotes/index.jsp`
2. Criar novo lote → verificar código gerado no banco
3. Ver itens de um lote existente
4. Enviar lote → status muda para ENVIADO

- [ ] **Commit**

```bash
git add src/main/webapp/muralha-digital/pages/processamento/lotes/index.jsp
git commit -m "Adiciona painel de gestão de lotes de infrações com criação, visualização e envio

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
