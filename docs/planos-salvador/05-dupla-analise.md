# Dupla Análise Independente de Infrações — Plano de Implementação

> **Depende de:** plano `01-log-auditoria.md` concluído.  
> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Implementar fluxo obrigatório de dupla análise independente: operador A classifica sem ver resultados de B; sistema bloqueia o mesmo operador de analisar duas vezes; divergência entre A e B envia automaticamente para um terceiro desempatador; todo ato gera registro em log.

**Architecture:** Nova tabela `muralha.infracao_analise`. Servlet `InfracaoAnaliseServlet` com fila de trabalho por usuário (excluindo registros já analisados). JSP de análise que oculta classificações anteriores. Lógica de desempate automático na gravação da segunda análise. Status de infração atualizado conforme o fluxo avança.

**Tech Stack:** Java 13 · SQL Server · Bootstrap 5.3 · SweetAlert2

## Global Constraints

- Um operador jamais vê a classificação do outro enquanto o segundo ainda não respondeu
- O mesmo `id_usuario` não pode ter duas linhas para o mesmo `id_infracao` em `infracao_analise`
- Constraint `UNIQUE (id_infracao, id_usuario)` garante isso no banco (nunca confiar só na aplicação)
- Sequência: 1=primeira análise, 2=segunda análise, 3=desempate
- Status da infração: `AGUARDANDO_ANALISE` → `PRIMEIRA_ANALISE` → `SEGUNDA_ANALISE` / `DESEMPATE` → `PRE_APROVADA` / `REPROVADA`
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_dupla_analise.sql` |
| Criar | `src/main/java/muralha/digital/processamento/InfracaoAnaliseServlet.java` |
| Criar | `src/main/java/muralha/digital/processamento/InfracaoAnaliseDAO.java` |
| Criar | `src/main/webapp/muralha-digital/pages/processamento/dupla-analise/fila.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp` |
| Criar | `src/main/webapp/muralha-digital/assets/js/processamento/dupla-analise.js` |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_dupla_analise.sql
-- ROLLBACK: DROP TABLE muralha.infracao_analise;
--           ALTER TABLE muralha.veiculo_tempo_real DROP COLUMN status_analise;

-- Tabela de análises individuais
CREATE TABLE muralha.infracao_analise (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_infracao     BIGINT       NOT NULL,   -- FK para muralha.veiculo_tempo_real (ou infracao, conforme tabela usada)
    id_usuario      INT          NOT NULL,
    sequencia       TINYINT      NOT NULL,   -- 1, 2 ou 3
    classificacao   VARCHAR(20)  NOT NULL,   -- 'VALIDA', 'INVALIDA', 'DUVIDA'
    justificativa   VARCHAR(500) NULL,
    dt_analise      DATETIME2(3) NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT uq_infracao_usuario UNIQUE (id_infracao, id_usuario)
);

CREATE INDEX ix_ia_infracao ON muralha.infracao_analise (id_infracao);

-- Status de análise na tabela principal de infrações
-- (ajustar o nome da tabela conforme qual é usada no fluxo de processamento)
ALTER TABLE muralha.veiculo_tempo_real ADD
    status_analise VARCHAR(25) NOT NULL DEFAULT 'AGUARDANDO_ANALISE';

CREATE INDEX ix_vtr_status_analise ON muralha.veiculo_tempo_real (status_analise);
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_dupla_analise.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_dupla_analise.sql
git commit -m "Adiciona tabela infracao_analise e coluna status_analise para dupla análise

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: InfracaoAnaliseDAO

- [ ] **Criar `InfracaoAnaliseDAO.java`**

```java
// src/main/java/muralha/digital/processamento/InfracaoAnaliseDAO.java
package muralha.digital.processamento;

import com.consilux.lib.Conexao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;

public final class InfracaoAnaliseDAO {

    private InfracaoAnaliseDAO() {}

    /**
     * Retorna a próxima infração disponível para análise pelo usuário informado.
     * Exclui: já analisadas por este usuário, em desempate sem ser desempatador,
     * já com status PRE_APROVADA ou REPROVADA.
     */
    public static JsonObject obterProxima(int idUsuario) throws SQLException {
        String sql =
            "SELECT TOP 1 vtr.id, vtr.placa, vtr.dt_passagem, vtr.status_analise, " +
            "  vtr.id_equipamento, vtr.faixa " +
            "FROM muralha.veiculo_tempo_real vtr " +
            "WHERE vtr.status_analise IN ('AGUARDANDO_ANALISE','PRIMEIRA_ANALISE','DESEMPATE') " +
            "  AND vtr.id NOT IN (" +
            "    SELECT id_infracao FROM muralha.infracao_analise WHERE id_usuario = ?" +
            "  ) " +
            "ORDER BY vtr.dt_passagem ASC";

        Conexao conn = Conexao.getConexao();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JsonObject r = new JsonObject();
                    r.addProperty("id",          rs.getLong("id"));
                    r.addProperty("placa",       rs.getString("placa"));
                    r.addProperty("dtPassagem",  String.valueOf(rs.getTimestamp("dt_passagem")));
                    r.addProperty("statusAnalise", rs.getString("status_analise"));
                    r.addProperty("equipamento", rs.getString("id_equipamento"));
                    r.addProperty("faixa",       rs.getString("faixa"));
                    return r;
                }
                return null;
            }
        } finally { conn.close(); }
    }

    /**
     * Grava a análise do operador e avança o status da infração conforme a lógica de negócio.
     * Retorna o novo status da infração.
     */
    public static String registrarAnalise(long idInfracao, int idUsuario,
            String classificacao, String justificativa) throws SQLException {

        Conexao conn = Conexao.getConexao();
        conn.setAutoCommit(false);
        try {
            // Determinar sequência
            int sequencia;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM muralha.infracao_analise WHERE id_infracao = ?")) {
                ps.setLong(1, idInfracao);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); sequencia = rs.getInt(1) + 1; }
            }

            // Verificar que não é re-análise
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM muralha.infracao_analise WHERE id_infracao=? AND id_usuario=?")) {
                ps.setLong(1, idInfracao); ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) throw new IllegalStateException("Operador já analisou esta infração.");
                }
            }

            // Inserir análise
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.infracao_analise " +
                    "(id_infracao,id_usuario,sequencia,classificacao,justificativa) " +
                    "VALUES (?,?,?,?,?)")) {
                ps.setLong(1, idInfracao); ps.setInt(2, idUsuario);
                ps.setInt(3, sequencia);   ps.setString(4, classificacao);
                ps.setString(5, justificativa);
                ps.executeUpdate();
            }

            // Determinar novo status
            String novoStatus = determinarNovoStatus(conn, idInfracao, sequencia, classificacao);

            // Atualizar status na infração
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE muralha.veiculo_tempo_real SET status_analise=? WHERE id=?")) {
                ps.setString(1, novoStatus); ps.setLong(2, idInfracao);
                ps.executeUpdate();
            }

            conn.commit();
            return novoStatus;
        } catch (Exception e) {
            conn.rollback(); throw new SQLException(e.getMessage(), e);
        } finally {
            conn.setAutoCommit(true); conn.close();
        }
    }

    private static String determinarNovoStatus(Connection conn, long idInfracao,
            int sequencia, String classificacaoAtual) throws SQLException {
        if (sequencia == 1) return "PRIMEIRA_ANALISE";
        if (sequencia == 2) {
            // Buscar classificação da primeira análise
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT classificacao FROM muralha.infracao_analise " +
                    "WHERE id_infracao=? AND sequencia=1")) {
                ps.setLong(1, idInfracao);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String primaClassif = rs.getString("classificacao");
                        return primaClassif.equals(classificacaoAtual) ? "PRE_APROVADA" : "DESEMPATE";
                    }
                }
            }
        }
        if (sequencia == 3) {
            // Desempate: classificação do desempatador prevalece
            return "VALIDA".equals(classificacaoAtual) ? "PRE_APROVADA" : "REPROVADA";
        }
        return "AGUARDANDO_ANALISE";
    }

    public static JsonArray obterIndicadores() throws SQLException {
        String sql =
            "SELECT status_analise, COUNT(*) qtde " +
            "FROM muralha.veiculo_tempo_real " +
            "GROUP BY status_analise ORDER BY status_analise";
        JsonArray arr = new JsonArray();
        Conexao conn = Conexao.getConexao();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject r = new JsonObject();
                r.addProperty("status", rs.getString("status_analise"));
                r.addProperty("qtde",   rs.getInt("qtde"));
                arr.add(r);
            }
        } finally { conn.close(); }
        return arr;
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/processamento/InfracaoAnaliseDAO.java
git commit -m "Adiciona InfracaoAnaliseDAO com fila, registro e determinação de status

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: InfracaoAnaliseServlet

- [ ] **Criar `InfracaoAnaliseServlet.java`**

```java
// src/main/java/muralha/digital/processamento/InfracaoAnaliseServlet.java
package muralha.digital.processamento;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import muralha.digital._ini.Acesso;
import muralha.digital.auditoria.AuditoriaService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/MuralhaDigital/InfracaoAnalise")
public class InfracaoAnaliseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        String acao = req.getParameter("acao");
        Gson gson = new Gson();

        try {
            int idUsuario = obterIdUsuario(req);
            if ("proximaFila".equals(acao)) {
                JsonObject prox = InfracaoAnaliseDAO.obterProxima(idUsuario);
                JsonObject r = new JsonObject();
                if (prox != null) { r.addProperty("ok", true); r.add("infracao", prox); }
                else              { r.addProperty("ok", true); r.addProperty("filaVazia", true); }
                resp.getWriter().print(gson.toJson(r));
            } else if ("indicadores".equals(acao)) {
                JsonObject r = new JsonObject();
                r.addProperty("ok", true);
                r.add("dados", InfracaoAnaliseDAO.obterIndicadores());
                resp.getWriter().print(gson.toJson(r));
            } else {
                resp.sendError(400, "acao invalida");
            }
        } catch (Exception e) {
            JsonObject err = new JsonObject();
            err.addProperty("ok", false);
            err.addProperty("erro", e.getMessage());
            resp.getWriter().print(gson.toJson(err));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        new Acesso(req, resp, true).verificaAcesso();
        resp.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        JsonObject r = new JsonObject();

        try {
            int    idUsuario      = obterIdUsuario(req);
            long   idInfracao     = Long.parseLong(req.getParameter("idInfracao"));
            String classificacao  = req.getParameter("classificacao");
            String justificativa  = req.getParameter("justificativa");

            if (!"VALIDA".equals(classificacao) && !"INVALIDA".equals(classificacao)
                    && !"DUVIDA".equals(classificacao)) {
                r.addProperty("ok", false); r.addProperty("erro", "Classificação inválida.");
                resp.getWriter().print(gson.toJson(r)); return;
            }

            String novoStatus = InfracaoAnaliseDAO.registrarAnalise(
                    idInfracao, idUsuario, classificacao, justificativa);

            AuditoriaService.registrar(req, "DuplaAnalise", "registrar-analise",
                String.valueOf(idInfracao),
                "Classificação: " + classificacao + " | Status: " + novoStatus);

            r.addProperty("ok", true);
            r.addProperty("novoStatus", novoStatus);
        } catch (IllegalStateException e) {
            r.addProperty("ok", false); r.addProperty("erro", e.getMessage());
        } catch (Exception e) {
            r.addProperty("ok", false); r.addProperty("erro", "Erro interno: " + e.getMessage());
        }
        resp.getWriter().print(gson.toJson(r));
    }

    private int obterIdUsuario(HttpServletRequest req) throws Exception {
        Object u = req.getSession().getAttribute("usuario");
        return (Integer) u.getClass().getMethod("getId").invoke(u);
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/processamento/InfracaoAnaliseServlet.java
git commit -m "Adiciona InfracaoAnaliseServlet com fila e registro de análise

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Telas de fila e análise

- [ ] **Criar `fila.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/processamento/dupla-analise/fila.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-person-check me-2"></i>Fila de Análise de Infrações</h4>

  <div class="row g-2 mb-3" id="indicadores">
    <!-- preenchido via JS -->
  </div>

  <div class="d-flex gap-2 mb-3">
    <button class="btn btn-primary" onclick="proxima()">
      <i class="bi bi-play-circle me-1"></i>Próxima Infração
    </button>
    <span id="msgFila" class="align-self-center text-muted"></span>
  </div>
</div>
<script src="/muralha-digital/assets/js/processamento/dupla-analise.js"></script>
<%@ include file="/muralha-digital/utils/credenciais/rodape.jsp" %>
```

- [ ] **Criar `analisar.jsp`**

```jsp
<%-- src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>
<div class="container py-3">
  <h5><i class="bi bi-image me-2"></i>Análise de Infração — <span id="lblPlaca"></span></h5>

  <div class="row mt-3">
    <div class="col-md-8">
      <div id="containerImagem" class="border rounded p-2 text-center bg-dark">
        <img id="imgInfracao" src="" class="img-fluid" alt="Imagem da infração"
             style="max-height:480px">
      </div>
      <!-- Controles de zoom/brilho (client-side, sem alterar original) -->
      <div class="mt-2 d-flex gap-3 align-items-center">
        <label>Brilho: <input type="range" id="brilho" min="0.3" max="2" step="0.1"
                              value="1" oninput="ajustarFiltro()"></label>
        <label>Contraste: <input type="range" id="contraste" min="0.3" max="2" step="0.1"
                                 value="1" oninput="ajustarFiltro()"></label>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card p-3">
        <h6>Dados da Passagem</h6>
        <p class="mb-1"><strong>Data/Hora:</strong> <span id="lblDt"></span></p>
        <p class="mb-1"><strong>Equipamento:</strong> <span id="lblEquip"></span></p>
        <p class="mb-3"><strong>Faixa:</strong> <span id="lblFaixa"></span></p>
        <hr>
        <h6>Sua Classificação</h6>
        <div class="mb-2">
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="VALIDA" id="rValida">
            <label class="form-check-label" for="rValida">✅ Válida</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="INVALIDA" id="rInvalida">
            <label class="form-check-label" for="rInvalida">❌ Inválida</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" name="classif" value="DUVIDA" id="rDuvida">
            <label class="form-check-label" for="rDuvida">❓ Dúvida</label>
          </div>
        </div>
        <textarea id="justificativa" class="form-control mb-3" rows="3"
                  placeholder="Justificativa (obrigatória para Inválida/Dúvida)"></textarea>
        <button class="btn btn-success w-100" onclick="salvar()">
          <i class="bi bi-check-lg me-1"></i>Confirmar Análise
        </button>
      </div>
    </div>
  </div>
</div>
<script src="/muralha-digital/assets/js/processamento/dupla-analise.js"></script>
<%@ include file="/muralha-digital/utils/credenciais/rodape.jsp" %>
```

- [ ] **Criar `dupla-analise.js`**

```javascript
// src/main/webapp/muralha-digital/assets/js/processamento/dupla-analise.js
let infracaoAtual = null;

function carregarIndicadores() {
  $.get('/MuralhaDigital/InfracaoAnalise', { acao: 'indicadores' }, function(r) {
    if (!r.ok) return;
    const map = {};
    r.dados.forEach(d => map[d.status] = d.qtde);
    const labels = {
      AGUARDANDO_ANALISE: 'Aguardando', PRIMEIRA_ANALISE: '1ª Análise',
      DESEMPATE: 'Desempate',           PRE_APROVADA: 'Pré-aprovada',
      REPROVADA: 'Reprovada'
    };
    const cores = {
      AGUARDANDO_ANALISE: 'secondary', PRIMEIRA_ANALISE: 'warning',
      DESEMPATE: 'danger',             PRE_APROVADA: 'success', REPROVADA: 'dark'
    };
    const html = Object.keys(labels).map(k =>
      `<div class="col-auto">
         <div class="card text-center border-${cores[k]||'secondary'}" style="min-width:110px">
           <div class="card-body py-2">
             <div class="fs-4 fw-bold">${map[k]||0}</div>
             <small>${labels[k]}</small>
           </div>
         </div>
       </div>`
    ).join('');
    $('#indicadores').html(html);
  });
}

function proxima() {
  $.get('/MuralhaDigital/InfracaoAnalise', { acao: 'proximaFila' }, function(r) {
    if (!r.ok) { bs_alert(r.erro); return; }
    if (r.filaVazia) { $('#msgFila').text('Não há infrações aguardando análise no momento.'); return; }
    infracaoAtual = r.infracao;
    preencherTela(r.infracao);
  });
}

function preencherTela(inf) {
  $('#lblPlaca').text(inf.placa);
  $('#lblDt').text(inf.dtPassagem);
  $('#lblEquip').text(inf.equipamento);
  $('#lblFaixa').text(inf.faixa);
  // Carregar imagem — endpoint de imagem existente no projeto
  $('#imgInfracao').attr('src', '/MuralhaDigital/Veiculo?acao=obterImagem&id=' + inf.id);
  // Navegar para tela de análise se estiver na fila
  if (window.location.pathname.includes('fila')) {
    window.location.href = 'analisar.jsp';
  }
}

function ajustarFiltro() {
  const b = $('#brilho').val(), c = $('#contraste').val();
  $('#imgInfracao').css('filter', `brightness(${b}) contrast(${c})`);
}

function salvar() {
  if (!infracaoAtual) { bs_alert('Nenhuma infração carregada.'); return; }
  const classif = $('input[name=classif]:checked').val();
  if (!classif) { bs_alert('Selecione uma classificação.'); return; }
  const justif = $('#justificativa').val().trim();
  if ((classif === 'INVALIDA' || classif === 'DUVIDA') && !justif) {
    bs_alert('Justificativa obrigatória para Inválida ou Dúvida.'); return;
  }
  Swal.fire({
    title: 'Confirmar análise?',
    text: 'Classificação: ' + classif + (justif ? '\n' + justif : ''),
    icon: 'question', showCancelButton: true,
    confirmButtonText: 'Confirmar', cancelButtonText: 'Cancelar'
  }).then(result => {
    if (!result.isConfirmed) return;
    $.post('/MuralhaDigital/InfracaoAnalise', {
      idInfracao: infracaoAtual.id,
      classificacao: classif,
      justificativa: justif
    }, function(r) {
      if (!r.ok) { bs_alert('Erro: ' + r.erro); return; }
      Swal.fire('Registrado!', 'Status: ' + r.novoStatus, 'success')
        .then(() => window.location.href = 'fila.jsp');
    });
  });
}

// Auto-carregar indicadores se estiver na fila
if (window.location.pathname.includes('fila')) {
  $(document).ready(carregarIndicadores);
}
// Restaurar infração da sessão se estiver na tela de análise
if (window.location.pathname.includes('analisar')) {
  $(document).ready(function() {
    const inf = sessionStorage.getItem('infracaoAtual');
    if (inf) { infracaoAtual = JSON.parse(inf); preencherTela(infracaoAtual); }
    else window.location.href = 'fila.jsp';
  });
}
```

Ajuste: antes de redirecionar para `analisar.jsp`, salvar a infração em `sessionStorage`:

```javascript
// Em proxima(), antes de redirecionar:
sessionStorage.setItem('infracaoAtual', JSON.stringify(r.infracao));
```

- [ ] **Cadastrar menu no banco**

```sql
INSERT INTO dbo.sis_menu_infos (nm_menu, ds_acao, id_menu_pai, nr_ordem, fl_ativo)
VALUES ('Dupla Análise', '/muralha-digital/pages/processamento/dupla-analise/fila.jsp', 1, 10, 1);
```

- [ ] **Build e teste completo com dois usuários**

```powershell
..\.setup-gtw\run.ps1
```

1. Usuário A abre `fila.jsp` → clica "Próxima Infração" → classifica como VALIDA
2. Usuário B abre `fila.jsp` → deve ver a mesma infração na fila (status PRIMEIRA_ANALISE)
3. Usuário B classifica como VALIDA → status deve mudar para PRE_APROVADA
4. Usuário A tenta analisar a mesma infração → sistema deve negar (não deve aparecer na fila)
5. Forçar divergência: usuário A=VALIDA, usuário C=INVALIDA → status deve ir para DESEMPATE
6. Usuário D (desempatador) classifica → status final REPROVADA ou PRE_APROVADA

```sql
-- Para verificar o fluxo:
SELECT id, placa, status_analise FROM muralha.veiculo_tempo_real ORDER BY id DESC;
SELECT * FROM muralha.infracao_analise ORDER BY id DESC;
```

- [ ] **Commit final**

```bash
git add src/main/webapp/muralha-digital/pages/processamento/dupla-analise/
git add src/main/webapp/muralha-digital/assets/js/processamento/dupla-analise.js
git commit -m "Implementa telas de fila e análise para dupla análise independente

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
