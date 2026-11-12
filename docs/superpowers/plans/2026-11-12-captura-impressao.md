# Captura da ação de Imprimir — Plano de Implementação

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Registrar a ação de imprimir como evento próprio `IMPRESSAO` no Motor de Logs, com formato e tela de origem, para documentos gerados no servidor (PDF) e para impressão pelo navegador.

**Architecture:** O caminho server-side é capturado no `CapturaLogFilter`/`AuditoriaService` lendo o `Content-Type` da resposta (nenhum relatório é alterado). O caminho navegador usa um gancho JS no cabeçalho comum que faz `POST` a um endpoint novo `/MuralhaDigital/Impressao`, que registra via `AuditoriaService`. Tudo é governado por `sis_log_tipo_evento` (liga/desliga global) e `sis_log_regra` (regras por rota). Os campos formato/documento/telaOrigem vão em `dados_extra` (que já integra a cadeia de hash) e aparecem no detalhe da consulta.

**Tech Stack:** Java 13, servlet (`@WebServlet`/`@WebFilter`), SQL Server, Gson, jQuery/Bootstrap 5.3, TestNG.

**Spec:** [docs/captura-logs/design-captura-impressao.md](../../captura-logs/design-captura-impressao.md)

## Global Constraints

- Sem novas bibliotecas/frameworks; sem alterar `pom.xml` nem `.setup-gtw`.
- DDL/DML só como script em `docs/banco-de-dados/migracoes/`, idempotente, com bloco ROLLBACK (padrão dos scripts existentes). Banco só `localhost` (`GTW_MURALHA_LOCAL`).
- Captura assíncrona e **blindada**: nenhuma falha de captura pode quebrar a requisição, atrasar a resposta ou impedir a impressão (try/catch em toda a borda, como já faz o motor).
- `IMPRESSAO` = `application/pdf`; CSV/XLSX permanecem `EXPORTACAO`. Regra com nível explícito sempre prevalece sobre a derivação.
- Não criar nova coluna/filtro na grade da consulta; exibir só no **detalhe** do evento.
- SQL parametrizado, recursos fechados (try-with-resources), UTF-8, PT-BR.
- A função de hash `AuditoriaService.hashEvento` **não muda**; `dados_extra` entra na cadeia como já ocorre.

## Review Focus

- **Content-Type com sufixo** (`application/pdf;charset=...` ou `;name=`): a derivação deve casar por prefixo, senão o PDF cai em `EXPORTACAO`/`VISUALIZACAO`. → coberto no Task 2.
- **Content-Type nulo** (resposta sem tipo definido): a derivação não pode lançar NPE. → coberto no Task 2.
- **CSV/XLSX não viram IMPRESSAO**: fronteira do §9.4 — planilha é exportação. → coberto no Task 2.
- **POST ao endpoint sem sessão** (usuário deslogado / sessão expirada): responde sem registrar e sem erro visível ao front. → coberto no Task 4.
- **`beforeprint` disparado múltiplas vezes** (comportamento normal do navegador): não pode gerar eventos duplicados por impressão. → deduplicação no Task 5 (verificação manual; sem harness JS no projeto).

---

### Task 1: Migração — tipo `IMPRESSAO` e regra do endpoint

**Files:**
- Create: `docs/banco-de-dados/migracoes/20261112_impressao_captura_logs.sql`

**Interfaces:**
- Produces: código `IMPRESSAO` em `dbo.sis_log_tipo_evento`; regra ativa em `dbo.sis_log_regra` com `padrao_rota = '/MuralhaDigital/Impressao'`, `nivel = 'IMPRESSAO'`, `capturar = 1`.

- [ ] **Step 1: Escrever o script idempotente** seguindo o padrão de `20261105_tipos_evento_log.sql` (SET NOCOUNT ON; blocos `IF NOT EXISTS ... BEGIN ... END` + `GO`; `PRINT`; comentário ROLLBACK).
  - Inserir em `sis_log_tipo_evento`: `('IMPRESSAO', 'Impressao de documento ou de tela', 1, 65)` apenas se o código ainda não existir.
  - Inserir em `sis_log_regra` (se ainda não existir regra com esse `padrao_rota`): `ordem`=50, `descricao`='Impressao de tela (navegador)', `padrao_rota`='/MuralhaDigital/Impressao', `metodo`='POST', `capturar`=1, `nivel`='IMPRESSAO', `ativo`=1 (demais colunas no default). Consultar as colunas reais de `sis_log_regra` no script `20261105_motor_captura_logs.sql` antes de escrever o INSERT.
  - Bloco ROLLBACK comentado: `DELETE FROM sis_log_tipo_evento WHERE codigo='IMPRESSAO';` e `DELETE FROM sis_log_regra WHERE padrao_rota='/MuralhaDigital/Impressao';`.
- [ ] **Step 2: Aplicar no banco local** `GTW_MURALHA_LOCAL` e conferir os `PRINT` de sucesso.
  Expected: cria o tipo e a regra; sem erro.
- [ ] **Step 3: Rodar o script uma segunda vez** para confirmar idempotência.
  Expected: imprime as mensagens de "ja existe"/"ignorado"; nenhuma duplicação.
- [ ] **Step 4: Commit**
```bash
git add docs/banco-de-dados/migracoes/20261112_impressao_captura_logs.sql
git commit -m "Adiciona tipo de evento IMPRESSAO e regra do endpoint de impressao"
```

---

### Task 2: Derivação de nível e formato por Content-Type (lógica pura)

**Files:**
- Modify: `src/main/java/muralha/digital/auditoria/AuditoriaService.java`
- Test: `src/test/java/muralha/digital/auditoria/AuditoriaDerivacaoTest.java`

**Interfaces:**
- Produces (package-private, estáticos, para teste e uso interno):
  - `String derivarNivel(String metodo, String acao, int status, String contentType)` — passa a existir com o 4º parâmetro; mantém a ordem semântica atual, mas quando `contentType` indica PDF retorna `"IMPRESSAO"`.
  - `String derivarFormato(String contentType)` — `"PDF"` para PDF; `null` quando não for documento imprimível.
  - `boolean ehPdf(String contentType)` — true se `contentType` (ignorando caixa/espaços) começa com `application/pdf`.

- [ ] **Step 1: Escrever os testes que falham** em `AuditoriaDerivacaoTest` (TestNG, estilo de `CalculadoraSlaTest`: `import org.testng.annotations.Test;` + `static org.testng.Assert.*`).

```java
@Test public void pdfViraImpressao() {
    assertEquals(AuditoriaService.derivarNivel("GET", null, 200, "application/pdf"), "IMPRESSAO");
    assertEquals(AuditoriaService.derivarNivel("GET", "relatorio", 200, "application/pdf;charset=UTF-8"), "IMPRESSAO");
}
@Test public void planilhaContinuaExportacao() {
    assertEquals(AuditoriaService.derivarNivel("GET", "exportar", 200, "text/csv"), "EXPORTACAO");
    assertEquals(AuditoriaService.derivarNivel("GET", "xls", 200,
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "EXPORTACAO");
}
@Test public void contentTypeNuloNaoQuebra() {
    assertEquals(AuditoriaService.derivarNivel("GET", "consultar", 200, null), "VISUALIZACAO");
}
@Test public void formatoDerivado() {
    assertEquals(AuditoriaService.derivarFormato("application/pdf; name=auto.pdf"), "PDF");
    assertNull(AuditoriaService.derivarFormato("text/csv"));
    assertNull(AuditoriaService.derivarFormato(null));
}
```

- [ ] **Step 2: Rodar os testes e confirmar a falha.**
  Run: `& "D:\GTW-CWB\.setup-gtw\setup-java-maven.ps1"; mvn -s "D:\GTW-CWB\.setup-gtw\settings-legacy-archiva.xml" test -Dtest=AuditoriaDerivacaoTest`
  Expected: FALHA de compilação/execução (método com 4 args / `derivarFormato` inexistentes).
- [ ] **Step 3: Implementar** em `AuditoriaService`: tornar `derivarNivel` package-private com o 4º parâmetro `contentType`; no início, se `ehPdf(contentType)` retornar `"IMPRESSAO"` (antes das demais regras, pois o PDF é o sinal mais forte); adicionar `derivarFormato` e `ehPdf` (package-private, estáticos, null-safe). Atualizar a única chamada interna em `capturarAutomatico` para passar o Content-Type (ver Task 3).
- [ ] **Step 4: Rodar os testes e confirmar o verde.**
  Expected: PASS.
- [ ] **Step 5: Commit**
```bash
git add src/main/java/muralha/digital/auditoria/AuditoriaService.java src/test/java/muralha/digital/auditoria/AuditoriaDerivacaoTest.java
git commit -m "Deriva nivel IMPRESSAO e formato a partir do Content-Type"
```

---

### Task 3: Capturar formato/documento/tela de origem no fluxo server-side

**Files:**
- Modify: `src/main/java/muralha/digital/auditoria/AuditoriaService.java`
- Test: `src/test/java/muralha/digital/auditoria/DadosImpressaoTest.java`

**Interfaces:**
- Consumes: `derivarFormato`, `derivarNivel` (Task 2).
- Produces (package-private, estáticos):
  - `String montarDadosImpressao(String formato, String documento, String telaOrigem)` — JSON Gson `{"formato":...,"documento":...,"telaOrigem":...}`, omitindo chaves nulas, truncado a 4000.
  - `String derivarTelaOrigem(String referer, String rotaFallback)` — caminho do `Referer` (sem host/esquema/query); se ausente, usa `rotaFallback`.

- [ ] **Step 1: Escrever os testes que falham** em `DadosImpressaoTest`:

```java
@Test public void montaJsonComFormatoDocumentoOrigem() {
    String j = AuditoriaService.montarDadosImpressao("PDF", "RelatorioValidacao", "/muralha-digital/pages/relatorios/x.jsp");
    assertTrue(j.contains("\"formato\":\"PDF\""));
    assertTrue(j.contains("\"documento\":\"RelatorioValidacao\""));
    assertTrue(j.contains("\"telaOrigem\":\"/muralha-digital/pages/relatorios/x.jsp\""));
}
@Test public void telaOrigemDoRefererSemHostNemQuery() {
    assertEquals(AuditoriaService.derivarTelaOrigem(
        "http://localhost:8080/muralha-digital/pages/relatorios/x.jsp?a=1", "/rota"),
        "/muralha-digital/pages/relatorios/x.jsp");
}
@Test public void telaOrigemCaiNoFallbackSemReferer() {
    assertEquals(AuditoriaService.derivarTelaOrigem(null, "/MuralhaDigital/Relatorio"), "/MuralhaDigital/Relatorio");
}
```

- [ ] **Step 2: Rodar e confirmar a falha.**
  Run: `mvn ... test -Dtest=DadosImpressaoTest`
  Expected: FALHA (métodos inexistentes).
- [ ] **Step 3: Implementar** os dois helpers e ligá-los em `capturarAutomatico`: quando `ev.nivel` for `"IMPRESSAO"`, calcular `formato = derivarFormato(response.getContentType())`, `documento = ev.funcionalidade`, `telaOrigem = derivarTelaOrigem(request.getHeader("Referer"), rota)` e setar `ev.dadosExtra = montarDadosImpressao(...)` (sem sobrescrever o payload de mutação, que não ocorre para IMPRESSAO). Ler `response.getContentType()` dentro do try/catch existente. `capturarAutomatico` já recebe `response`; passar `response.getContentType()` para `derivarNivel`.
- [ ] **Step 4: Rodar e confirmar o verde.**
  Expected: PASS (inclui os testes do Task 2).
- [ ] **Step 5: Commit**
```bash
git add src/main/java/muralha/digital/auditoria/AuditoriaService.java src/test/java/muralha/digital/auditoria/DadosImpressaoTest.java
git commit -m "Registra formato, documento e tela de origem nos eventos de impressao server-side"
```

---

### Task 4: Endpoint `ImpressaoServlet` (impressão de navegador)

**Files:**
- Create: `src/main/java/muralha/digital/auditoria/ImpressaoServlet.java`
- Modify: `src/main/java/muralha/digital/auditoria/AuditoriaService.java`
- Test: `src/test/java/muralha/digital/auditoria/FormatoImpressaoTest.java`

**Interfaces:**
- Consumes: `montarDadosImpressao` (Task 3).
- Produces:
  - `AuditoriaService.registrarImpressao(HttpServletRequest request, String formato, String telaOrigem)` — registra evento nível `IMPRESSAO`, `tipoCaptura="NAVEGADOR"`, marcando `ATTR_LOG_MANUAL` para o filtro não duplicar; preenche usuário/IP como os demais; `funcionalidade` derivada de `telaOrigem`.
  - `String formatoOuPadrao(String formato)` (package-private, estático) — devolve o formato recebido ou `"Impressao de tela"` quando vazio/nulo.
  - Servlet em `@WebServlet("/MuralhaDigital/Impressao")`, `doPost`: exige sessão (sem sessão → `HttpServletResponse.SC_NO_CONTENT` 204, sem registrar); lê params `telaOrigem` e `formato`; chama `registrarImpressao`; responde 204.

- [ ] **Step 1: Escrever o teste que falha** em `FormatoImpressaoTest`:

```java
@Test public void formatoPadraoQuandoVazio() {
    assertEquals(AuditoriaService.formatoOuPadrao(null), "Impressao de tela");
    assertEquals(AuditoriaService.formatoOuPadrao("  "), "Impressao de tela");
    assertEquals(AuditoriaService.formatoOuPadrao("PDF"), "PDF");
}
```

- [ ] **Step 2: Rodar e confirmar a falha.**
  Run: `mvn ... test -Dtest=FormatoImpressaoTest`
  Expected: FALHA (método inexistente).
- [ ] **Step 3: Implementar** `formatoOuPadrao` e `registrarImpressao` em `AuditoriaService` (reusando `enfileirar`/`preencherUsuario`/`derivarTelaOrigem`/`montarDadosImpressao`), e criar `ImpressaoServlet`. A verificação de sessão usa `request.getSession(false)` + presença do usuário (padrão de `preencherUsuario`). O servlet é endpoint interno de log: **não** registrar em `sis_menu` nem usar `ControleAcesso` estrito (decisão §6.6 do desenho).
- [ ] **Step 4: Rodar e confirmar o verde.**
  Expected: PASS.
- [ ] **Step 5: Verificação manual do endpoint** (após build/run): `POST /MuralhaDigital/Impressao` autenticado com `telaOrigem=/x&formato=Impressao de tela` → responde 204 e gera um evento `IMPRESSAO` na consulta; mesmo POST sem sessão → 204 e **nenhum** evento.
- [ ] **Step 6: Commit**
```bash
git add src/main/java/muralha/digital/auditoria/ImpressaoServlet.java src/main/java/muralha/digital/auditoria/AuditoriaService.java src/test/java/muralha/digital/auditoria/FormatoImpressaoTest.java
git commit -m "Adiciona endpoint de registro de impressao de tela"
```

---

### Task 5: Gancho JS de impressão de tela + inclusão no cabeçalho

**Files:**
- Create: `src/main/webapp/muralha-digital/assets/js/auditoria/impressao-captura.js`
- Modify: `src/main/webapp/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp`

**Interfaces:**
- Consumes: endpoint `/MuralhaDigital/Impressao` (Task 4); variável global `urlRoot` já definida no cabeçalho.

- [ ] **Step 1: Escrever o `impressao-captura.js`**: no evento `window.addEventListener('beforeprint', ...)`, enviar `navigator.sendBeacon(urlRoot + 'MuralhaDigital/Impressao', body)` com `telaOrigem = location.pathname` e `document.title`, `formato = 'Impressao de tela'` (body via `URLSearchParams`/`FormData`). **Deduplicar**: ignorar disparos num intervalo curto (ex.: flag com `setTimeout` de ~1s) para o navegador não gerar eventos repetidos. Envolver tudo em try/catch; nunca lançar. Fallback `fetch(..., {method:'POST', keepalive:true})` se `sendBeacon` ausente.
- [ ] **Step 2: Incluir o script no cabeçalho** `cabecalho_bootstrap_simples.jsp`, junto aos demais `<script>` do `<head>` (após `ajuda-contexto.js`, ~linha 46): `<script src="/muralha-digital/assets/js/auditoria/impressao-captura.js"></script>`.
- [ ] **Step 3: Verificação manual** (após build/run): abrir uma tela nova (ex.: consulta de logs), acionar Ctrl+P e cancelar o diálogo → aparece **um** evento `IMPRESSAO` (formato "Impressao de tela", tela de origem = a tela) na consulta; acionar duas impressões rápidas → sem duplicação indevida.
- [ ] **Step 4: Commit**
```bash
git add src/main/webapp/muralha-digital/assets/js/auditoria/impressao-captura.js src/main/webapp/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp
git commit -m "Captura impressao de tela pelo navegador e envia ao motor de logs"
```

---

### Task 6: Nível IMPRESSAO na tela de regras e detalhe na consulta

**Files:**
- Modify: `src/main/webapp/muralha-digital/pages/admin/logs-regras/index.jsp`
- Modify: `src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js`

**Interfaces:**
- Consumes: eventos com `dados_extra` contendo `formato`/`documento`/`telaOrigem` (Tasks 3–4).

- [ ] **Step 1: Adicionar a opção na tela de regras** `logs-regras/index.jsp`: no `<select>` de nível (hoje com ACESSO…ERRO, ~linha 83-84), incluir `<option>IMPRESSAO</option>`.
- [ ] **Step 2: Exibir os campos de impressão no detalhe** da consulta (`consulta-log.js`): no bloco de montagem do modal de detalhe, quando `r.dadosExtra` parsear um objeto com `formato`/`documento`/`telaOrigem` (e não for o par antes/depois de escrita), renderizar uma seção "Impressão" com esses três campos usando o helper `linhaDet` já existente. Manter o comportamento atual para eventos de escrita.
- [ ] **Step 3: Verificação manual** (após build/run): na tela de regras, criar/editar uma regra com nível IMPRESSAO e salvar (recarrega o cache sem redeploy); na consulta, abrir um evento IMPRESSAO e ver formato, documento e tela de origem no detalhe.
- [ ] **Step 4: Commit**
```bash
git add src/main/webapp/muralha-digital/pages/admin/logs-regras/index.jsp src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js
git commit -m "Expoe nivel IMPRESSAO na config de regras e detalha a impressao na consulta"
```

---

### Task 7: Documentação de entrega

**Files:**
- Create: `docs/entregas/<data>-captura-impressao.md`
- Modify: `docs/captura-logs/motor-captura-logs.md` (seção do novo tipo IMPRESSAO)

- [ ] **Step 1: Escrever a entrega** com a seção **"## Origem"** apontando (link relativo) para [design-captura-impressao.md](../captura-logs/design-captura-impressao.md). Descrever os dois caminhos, a configuração por regras/tipos, e as limitações conhecidas (§10 do desenho). Não inventar referências a documentos inexistentes.
- [ ] **Step 2: Atualizar** `motor-captura-logs.md` registrando o tipo `IMPRESSAO` e os campos formato/documento/telaOrigem.
- [ ] **Step 3: Commit**
```bash
git add docs/entregas/ docs/captura-logs/motor-captura-logs.md
git commit -m "Documenta a captura da acao de imprimir no motor de logs"
```
