# Correções de segurança e funcionais (fases 1 e 2) — Plano de Implementação

> **Status:** Executado (2026-10-19, PR #54): ver [Entrega 35](../entregas/35-correcoes-seguranca-funcionais.md). Testes no navegador pendentes.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Corrigir os itens das fases 1 e 2 do guia de correções: SEG-01, SEG-03, SEG-04, SEG-05, SEG-09, SEG-10 e FUN-01 a FUN-05.

**Architecture:**
- **Verificação de direito:** uma classe nova `muralha.digital.acessos.ControleAcesso` faz a verificação **estrita**, que nega quando a funcionalidade não está cadastrada (*fail-closed*), sobre `dbo.fcn_VerificaAcesso`. Os servlets dos módulos novos passam a usá-la no lugar de `Acesso.verificaAcesso`, que continua *fail-open* para o legado.
- **SQL dos KPIs:** é validado por `ValidadorSqlKpi`.
- **Relatórios:** os dois jobs de relatório passam a ler tabelas e colunas que existem.
- **KPIs sem histórico:** cinco KPIs têm o SQL corrigido por migração.
- **Camada `analise`:** um job Quartz novo recarrega as tabelas materializadas.

**Tech Stack:** Java 13, Servlets, JSP, Bootstrap 5.3, JDBC (SQL Server 2016), Quartz, TestNG + Mockito.

**Spec:** [docs/referencia/guia-correcoes-analise-kpis-lotes.md](../referencia/guia-correcoes-analise-kpis-lotes.md). Itens SEG-01, 03, 04, 05, 09 e 10 e FUN-01 a 05; decisões do responsável em 2026-10-19.

## Origem

- Guia de correções (spec acima), §3 a §6.
- Decisões de 2026-10-19:
  - escopo nas fases 1 e 2;
  - FUN-02 usa os dados existentes (`infracao_analise` e `vtr_status_analise`), sem colunas novas;
  - FUN-03 renomeia "Enviar para DETRAN" para "Marcar como enviado";
  - execução por subagentes.
- Já aplicado antes deste plano: `20261019_registro_modulos_novos.sql` (menus 397–438 e direitos). **Todo servlet citado aqui já tem a URL registrada em `sis_menu` com direitos.** É isso que permite a verificação estrita sem bloquear quem tem direito.

## Global Constraints

- Não alterar `.setup-gtw\` nem `pom.xml`; sem bibliotecas novas (CLAUDE.md).
- SQL parametrizado; recursos em try-with-resources; UTF-8; textos para o usuário em PT-BR.
- Banco `GTW_MURALHA_DEV`:
  - DDL/DML só como script em `docs/banco-de-dados/migracoes/` (`AAAAMMDD_descricao.sql`, idempotente, com rollback no cabeçalho), registrado em `docs/banco-de-dados/migracoes/README.md`;
  - consultas de verificação somente `SELECT`;
  - a senha nunca vai para arquivo versionado. Use `$env:SQLCMDPASSWORD`, que o controlador informa na dispatch.
- Padrão visual: `docs/requisitos/05-padrao-visual.md` (Bootstrap 5.3, Bootstrap Icons, SweetAlert2).
- Ação de menu da Configuração de KPIs: `/muralha-digital/pages/admin/kpis/index.jsp` (menu 372). Da Gestão de Lotes: `/muralha-digital/pages/processamento/lotes/index.jsp` (menu 370).
- Respostas JSON de negação:
  - `401 {"ok":false,"erro":"Sessão expirada. Faça login novamente."}`;
  - `403 {"ok":false,"erro":"Sem permissão para esta funcionalidade."}`.
- Commits em português, no imperativo, terminando com a linha `Co-Authored-By` informada na dispatch.
- **Testes:** `& <workspace>\run-tests.ps1 <classes>` (TestNG direto; o provider do surefire não está no cache offline). **Compilação:** `mvn -o -s .setup-gtw\settings-legacy-archiva.xml '-Dexec.skip=true' test-compile`, com `JAVA_HOME=D:\GTW-CWB\.setup-gtw\jdk-13.0.1`.

## Review Focus

1. **Requisição AJAX sem sessão ou sem direito:** deve receber 401 ou 403 em JSON, não HTML de redirecionamento. As telas devem mostrar a mensagem do campo `erro`. Os testes de `ControleAcessoTest` cobrem os dois casos.
2. **SQL legítimo de KPI recusado pelo validador:** quebras de linha, `WITH (NOLOCK)`, `CASE`, literais com palavras proibidas (`'DELETE'`) e colunas como `data_alt` precisam passar. O `ValidadorSqlKpiTest` usa as consultas reais dos KPIs 1–12.
3. **Gestão de Lotes com entrada ruim:**
   - `ids` com UUID inválido, vazio ou acima de 500;
   - lote que não está em RASCUNHO;
   - infração já em outro lote ou que não está PRE_APROVADA.
   Nada pode ser gravado pela metade. `LoteRegrasTest` e a transação da Task 5 cobrem isso.
4. **Nome ou descrição com aspas ou HTML:** em KPI, lote, relatório ou log de auditoria, deve aparecer como texto, inclusive em `onclick`. A Task 7 troca o JS inline com dados por `data-*`.
5. **Relatório com período sem dados:** sem divisão por zero nem `NullPointerException` em AVG nulo. As Tasks 9 e 10 usam `rs.getObject`/`wasNull`.

---

### Task 1: ControleAcesso — verificação estrita de direito

**Files:**
- Create: `src/main/java/muralha/digital/acessos/ControleAcesso.java`
- Create: `src/test/java/muralha/digital/acessos/ControleAcessoTest.java`
- Modify: `src/main/java/muralha/digital/analise/SsoAnaliseServlet.java` (método `podeAbrirSuperset` passa a usar `ControleAcesso.possuiDireito`)

**Interfaces:**
- Produces (usado pelas Tasks 2, 3, 5 e 6):
  - `public static boolean ControleAcesso.possuiDireito(String acao, int idUsuario)`;
  - `public static boolean ControleAcesso.exigir(HttpServletRequest, HttpServletResponse) throws IOException`, que verifica o direito ao próprio `req.getServletPath()` e responde em JSON;
  - `public static boolean ControleAcesso.exigir(HttpServletRequest, HttpServletResponse, String acao) throws IOException`;
  - `public static boolean ControleAcesso.exigirTela(HttpServletRequest, HttpServletResponse) throws IOException`, que redireciona como o legado, para servlets que encaminham para JSP;
  - `public static Usuario ControleAcesso.usuario(HttpServletRequest)`;
  - `public static void ControleAcesso.definirParaTestes(VerificadorDireito v, Consumer<HttpServletRequest> registrador)` e `public static void restaurarPadrao()`.

- [ ] **Step 1: Escrever o teste que falha**

```java
package muralha.digital.acessos;

import com.consilux.infra.SessaoConstantes;
import com.consilux.model.Usuario;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ControleAcessoTest {

    @AfterMethod
    public void limpar() { ControleAcesso.restaurarPadrao(); }

    private static HttpServletRequest pedido(Usuario usuario, String servletPath) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession sessao = mock(HttpSession.class);
        when(sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO)).thenReturn(usuario);
        when(req.getSession(false)).thenReturn(sessao);
        when(req.getSession()).thenReturn(sessao);
        when(req.getServletPath()).thenReturn(servletPath);
        return req;
    }

    private static Usuario usuario(int id) {
        Usuario u = mock(Usuario.class);
        when(u.getId()).thenReturn(id);
        return u;
    }

    @Test
    public void exigirComDireitoLiberaSemEscreverResposta() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> "/MuralhaDigital/Lote".equals(acao) && id == 7, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertTrue(ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), resp));
        verify(resp, never()).setStatus(anyInt());
    }

    @Test
    public void exigirSemDireitoResponde403EmJson() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> false, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter corpo = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(corpo));
        assertFalse(ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), resp));
        verify(resp).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(corpo.toString().contains("\"ok\":false"));
        assertTrue(corpo.toString().contains("Sem permissão"));
    }

    @Test
    public void exigirSemSessaoResponde401() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> {});
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        assertFalse(ControleAcesso.exigir(req, resp));
        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void exigirComAcaoExplicitaUsaAcaoInformada() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> "/muralha-digital/pages/admin/kpis/index.jsp".equals(acao), r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        HttpServletRequest req = pedido(usuario(1), "/MuralhaDigital/Kpi");
        assertTrue(ControleAcesso.exigir(req, resp, "/muralha-digital/pages/admin/kpis/index.jsp"));
        assertFalse(ControleAcesso.exigir(req, resp, "/outra/acao"));
    }

    @Test
    public void exigirTelaSemDireitoRedirecionaParaPrincipal() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> false, r -> {});
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertFalse(ControleAcesso.exigirTela(pedido(usuario(3), "/MuralhaDigital/RelatorioGerencial"), resp));
        verify(resp).sendRedirect("/login/gtw_principal.jsp");
    }

    @Test
    public void exigirTelaSemSessaoRedirecionaParaLogin() throws Exception {
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> {});
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        assertFalse(ControleAcesso.exigirTela(req, resp));
        verify(resp).sendRedirect("/login/login.jsp");
    }

    @Test
    public void registraLogDeAcessoUmaVezPorVerificacao() throws Exception {
        int[] chamadas = {0};
        ControleAcesso.definirParaTestes((acao, id) -> true, r -> chamadas[0]++);
        ControleAcesso.exigir(pedido(usuario(7), "/MuralhaDigital/Lote"), mock(HttpServletResponse.class));
        assertEquals(chamadas[0], 1);
    }
}
```

- [ ] **Step 2: Rodar e ver falhar.** Run: `& <workspace>\run-tests.ps1 muralha.digital.acessos.ControleAcessoTest`. Expected: `test-compile` falha com "cannot find symbol ControleAcesso".

- [ ] **Step 3: Implementar**

```java
package muralha.digital.acessos;

import com.consilux.infra.SessaoConstantes;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.Usuario;
import com.google.gson.JsonObject;
import muralha.digital.auditoria.AuditoriaService;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * Verificacao ESTRITA de direito para os modulos novos do Muralha.
 * Diferente de Acesso.verificaAcesso (fail-open), nega quando a funcionalidade
 * nao esta cadastrada em sis_menu/sis_menu_direitos.
 */
public final class ControleAcesso {

    private static final Logger logger = Logger.getLogger(ControleAcesso.class);

    /** Consulta de direito (troca nos testes). */
    public interface VerificadorDireito { boolean possui(String acao, int idUsuario); }

    private static final VerificadorDireito VERIFICADOR_PADRAO = ControleAcesso::consultarBanco;
    /** Mantem o log de acesso que o Acesso legado grava no construtor. */
    private static final Consumer<HttpServletRequest> REGISTRADOR_PADRAO = r -> new Acesso(r, null, true);

    private static volatile VerificadorDireito verificador = VERIFICADOR_PADRAO;
    private static volatile Consumer<HttpServletRequest> registrador = REGISTRADOR_PADRAO;

    private ControleAcesso() {}

    public static void definirParaTestes(VerificadorDireito v, Consumer<HttpServletRequest> r) {
        verificador = v;
        registrador = r;
    }

    public static void restaurarPadrao() {
        verificador = VERIFICADOR_PADRAO;
        registrador = REGISTRADOR_PADRAO;
    }

    public static boolean possuiDireito(String acao, int idUsuario) {
        return verificador.possui(acao, idUsuario);
    }

    private static boolean consultarBanco(String acao, int idUsuario) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement("SELECT cnt, acesso FROM dbo.fcn_VerificaAcesso(?, ?)")) {
            ps.setString(1, acao);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0 && rs.getInt("acesso") > 0;
            }
        } catch (Exception e) {
            logger.warn("Falha ao verificar o direito '" + acao + "' do usuario " + idUsuario + ": " + e.getMessage());
            return false;
        }
    }

    public static Usuario usuario(HttpServletRequest req) {
        HttpSession sessao = req.getSession(false);
        return sessao == null ? null : (Usuario) sessao.getAttribute(SessaoConstantes.SESSAO_USUARIO);
    }

    /** Exige direito ao proprio servlet (servletPath). Responde 401/403 em JSON. */
    public static boolean exigir(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return exigir(req, resp, req.getServletPath());
    }

    /** Exige direito a acao informada (ex.: acao de menu de uma tela). Responde 401/403 em JSON. */
    public static boolean exigir(HttpServletRequest req, HttpServletResponse resp, String acao) throws IOException {
        Usuario usuario = usuario(req);
        if (usuario == null) {
            responder(resp, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada. Faça login novamente.");
            return false;
        }
        registrador.accept(req);
        if (possuiDireito(acao, usuario.getId())) return true;
        registrarNegacao(req, acao, usuario);
        responder(resp, HttpServletResponse.SC_FORBIDDEN, "Sem permissão para esta funcionalidade.");
        return false;
    }

    /** Para servlets que encaminham para JSP quando chamados sem acao. */
    public static boolean exigirTela(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario usuario = usuario(req);
        if (usuario == null) { resp.sendRedirect("/login/login.jsp"); return false; }
        registrador.accept(req);
        String acao = req.getServletPath();
        if (possuiDireito(acao, usuario.getId())) return true;
        registrarNegacao(req, acao, usuario);
        resp.sendRedirect("/login/gtw_principal.jsp");
        return false;
    }

    private static void registrarNegacao(HttpServletRequest req, String acao, Usuario usuario) {
        try {
            AuditoriaService.registrar(req, "acesso", "negado", acao,
                    "Acesso negado a " + acao + " para o usuario " + usuario.getId());
        } catch (Exception e) {
            logger.warn("Falha ao registrar negacao de acesso: " + e.getMessage());
        }
    }

    private static void responder(HttpServletResponse resp, int status, String mensagem) throws IOException {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("ok", false);
        corpo.addProperty("erro", mensagem);
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().print(corpo.toString());
    }
}
```

Em `SsoAnaliseServlet.podeAbrirSuperset`, troque o corpo por `return ControleAcesso.possuiDireito(ACAO_ABRIR_SUPERSET, usuario.getId());` e remova os imports que ficarem sem uso. O comportamento continua o mesmo: nega quando a funcionalidade não está cadastrada.

> Se `AuditoriaService.registrar` chamar o banco de forma síncrona e falhar nos testes, o `try/catch` acima já absorve. Confira a assinatura real (`registrar(HttpServletRequest, String, String, String, String)`) antes de compilar.

- [ ] **Step 4: Rodar e ver passar.** Run: `& <workspace>\run-tests.ps1 muralha.digital.acessos.ControleAcessoTest,muralha.digital.analise.SsoAnaliseServletTest`. Expected: todos passam.

- [ ] **Step 5: Commit.** `git add` dos três arquivos; mensagem: "Adiciona a verificacao estrita de direito para os modulos novos".

---

### Task 2: Verificação estrita nos servlets dos módulos novos (SEG-01)

**Files (Modify):** troque a verificação no início de `doGet`, `doPost` e de outros `do*` que existirem.

| Servlet | Arquivo |
|---|---|
| IntegridadeRecepcao, IntegridadeConfig | `src/main/java/muralha/digital/integridade/IntegridadeRecepcaoServlet.java`, `IntegridadeConfigServlet.java` |
| LogTecnico | `src/main/java/muralha/digital/logtecnico/LogTecnicoServlet.java` |
| RevisaoPlaca, OcrConfig | `src/main/java/muralha/digital/ocr/RevisaoPlacaServlet.java`, `OcrConfigServlet.java` |
| RelatorioGerencial | `src/main/java/muralha/digital/relatorio/RelatorioGerencialServlet.java` |
| PreProcessamento | `src/main/java/muralha/digital/preprocessamento/PreProcessamentoServlet.java` |
| Retencao | `src/main/java/muralha/digital/retencao/RetencaoServlet.java` |
| UsuarioCpfMatricula | `src/main/java/muralha/digital/usuario/UsuarioCpfMatriculaServlet.java` |
| DashboardBuilder, Dataset (anterior) | `src/main/java/muralha/digital/dashboard/DashboardBuilderServlet.java`, `DatasetServlet.java` |
| Exportacao | `src/main/java/muralha/digital/exportacao/ExportacaoServlet.java` |
| Consolidacao | `src/main/java/muralha/digital/consolidacao/ConsolidacaoServlet.java` |
| ConsultaAnonimizada | `src/main/java/muralha/digital/anonimizacao/ConsultaAnonimizadaServlet.java` |
| InfracaoAnalise | `src/main/java/muralha/digital/processamento/InfracaoAnaliseServlet.java` |
| Obliteracao | `src/main/java/muralha/digital/imagem/ObliteracaoServlet.java` |
| `/api/dash/*` (6) | `src/main/java/muralha/digital/dashboards/{DashCatalogo,DashConsulta,DashPainel,Dataset,Widget,ExportacaoDashboard}Servlet.java` |
| Assertividade, Auditoria, Disponibilidade | `src/main/java/muralha/digital/{assertividade/AssertividadeServlet,auditoria/AuditoriaServlet,disponibilidade/DisponibilidadeServlet}.java` |
| SlaLatencia, SlaPreprocessamento | `src/main/java/muralha/digital/sla/SlaLatenciaServlet.java`, `SlaPreprocessamentoServlet.java` |
| Timelapse, Gis, Incidente | `src/main/java/muralha/digital/{timelapse/TimelapseServlet,gis/GisServlet,sensores/IncidenteServlet}.java` |
| AnaliseGuestToken | `src/main/java/muralha/digital/analise/GuestTokenServlet.java` |

**Fora desta task:**
- `KpiServlet` (Task 3), `LoteServlet` (Task 5), `TipoAlertaConfigServlet` (Task 6);
- `SsoAnaliseServlet`, porque `acao=validar` é chamado pelo nginx e tem tratamento próprio;
- servlets do Sistema de Chamados (controle por perfil), `MfaConfigServlet`, `HealthCheckServlet` e `MapaConfigServlet` (fora do registro, ver guia §7.4).

**Interfaces:** Consumes `ControleAcesso.exigir(req, resp)` e `ControleAcesso.exigirTela(req, resp)` da Task 1.

- [ ] **Step 1: Testes que falham (dois servlets representativos).** Crie os testes **no pacote de cada servlet**, porque `doGet` é `protected`:
  - `src/test/java/muralha/digital/exportacao/ExportacaoServletAcessoTest.java`;
  - `src/test/java/muralha/digital/disponibilidade/DisponibilidadeServletAcessoTest.java`.

  Em cada um, com `ControleAcesso.definirParaTestes((a, id) -> false, r -> {})`, um usuário em sessão (mock como em `ControleAcessoTest`) e `getServletPath()` igual à URL do servlet, chame `doGet` e verifique `resp.setStatus(403)` e o corpo com `"ok":false`. A resposta é escrita pelo `ControleAcesso` antes de qualquer acesso ao banco. Use `@AfterMethod` com `ControleAcesso.restaurarPadrao()`.

- [ ] **Step 2: Rodar e ver falhar.** Hoje o servlet chama `Acesso.verificaAcesso`, que consulta o banco, e não devolve 403.

- [ ] **Step 3: Implementar.** Em cada arquivo da tabela:
  - Substitua `if (!new Acesso(req, resp, true).verificaAcesso(false)) return;`, e as variantes com variável `acesso`, por `if (!ControleAcesso.exigir(req, resp)) return;`.
  - Nos servlets que **encaminham para JSP quando `acao` é nula ou vazia** (`RelatorioGerencialServlet` e `PreProcessamentoServlet`; confira com `getRequestDispatcher` em cada arquivo):

    ```java
    String acao = req.getParameter("acao");
    boolean tela = acao == null || acao.isEmpty();
    if (tela ? !ControleAcesso.exigirTela(req, resp) : !ControleAcesso.exigir(req, resp)) return;
    ```

  - Remova o import de `com.consilux.model.Acesso` quando ficar sem uso.
  - Mantenha as checagens extras que já existem, como a de administrador em `DashCatalogoServlet`.

- [ ] **Step 4: Verificar registro.** Rode o `SELECT` abaixo (somente leitura). Esperado: `cnt > 0` em todas as linhas.

```sql
SELECT u.acao, (SELECT cnt FROM dbo.fcn_VerificaAcesso(u.acao, 1)) AS cnt
FROM (VALUES ('/MuralhaDigital/IntegridadeRecepcao'),('/MuralhaDigital/IntegridadeConfig'),('/MuralhaDigital/LogTecnico'),
 ('/MuralhaDigital/RevisaoPlaca'),('/MuralhaDigital/OcrConfig'),('/MuralhaDigital/RelatorioGerencial'),('/MuralhaDigital/PreProcessamento'),
 ('/MuralhaDigital/Retencao'),('/MuralhaDigital/UsuarioCpfMatricula'),('/MuralhaDigital/DashboardBuilder'),('/MuralhaDigital/Dataset'),
 ('/MuralhaDigital/Exportacao'),('/MuralhaDigital/Consolidacao'),('/MuralhaDigital/ConsultaAnonimizada'),('/MuralhaDigital/InfracaoAnalise'),
 ('/MuralhaDigital/Obliteracao'),('/api/dash/catalogo'),('/api/dash/consultas'),('/api/dash/paineis'),('/api/dash/datasets'),
 ('/api/dash/widgets'),('/api/dash/exportar'),('/MuralhaDigital/Assertividade'),('/MuralhaDigital/Auditoria'),
 ('/MuralhaDigital/Disponibilidade'),('/MuralhaDigital/SlaLatencia'),('/MuralhaDigital/SlaPreprocessamento'),
 ('/MuralhaDigital/Timelapse'),('/MuralhaDigital/Gis'),('/MuralhaDigital/Incidente'),('/MuralhaDigital/AnaliseGuestToken')) u(acao);
```

- [ ] **Step 5: Rodar os testes.** Os dois testes do Step 1 e os testes existentes dos servlets alterados: `ConsolidacaoServletTest`, `AssertividadeServletTest` e outros que existirem em `src/test/java/muralha/digital/**` para esses servlets. Se um teste existente quebrar porque agora passa pelo `ControleAcesso`, ajuste o teste para chamar `ControleAcesso.definirParaTestes((a, id) -> true, r -> {})` no `@BeforeMethod` e `restaurarPadrao()` no `@AfterMethod`.

- [ ] **Step 6: Commit.** Mensagem: "Aplica a verificacao estrita de direito nos servlets dos modulos novos".

---

### Task 3: KPIs — SQL validado, timeout e direito da Configuração (SEG-03 e SEG-04)

**Files:**
- Create: `src/main/java/muralha/digital/kpi/ValidadorSqlKpi.java`
- Create: `src/test/java/muralha/digital/kpi/ValidadorSqlKpiTest.java`
- Modify: `src/main/java/muralha/digital/kpi/KpiServlet.java`
- Modify: `src/main/java/muralha/digital/kpi/KpiSnapshotJob.java`

**Interfaces:**
- Consumes `ControleAcesso.exigir` (Task 1).
- Produces:
  - `public static String ValidadorSqlKpi.validar(String sql)`, que devolve `null` se o SQL é válido e, senão, a mensagem;
  - `public static final int ValidadorSqlKpi.TIMEOUT_SEGUNDOS = 30`.

- [ ] **Step 1: Teste que falha**

```java
package muralha.digital.kpi;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ValidadorSqlKpiTest {

    @DataProvider
    public Object[][] validas() {
        return new Object[][] {
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real WITH (NOLOCK) WHERE CAST(data AS DATE) = CAST(SYSDATETIME() AS DATE)"},
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor FROM muralha.veiculo_tempo_real vtr WITH (NOLOCK) LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = vtr.id WHERE vsa.status_analise IS NULL OR vsa.status_analise = 'AGUARDANDO_ANALISE'"},
            {"SELECT CAST(COUNT(*) AS NUMERIC) AS valor\nFROM muralha.vtr_status_analise vsa\nWHERE vsa.status_analise = 'PRE_APROVADA'\n  AND EXISTS (\n    SELECT 1 FROM muralha.infracao_analise ia\n    WHERE ia.id_infracao = vsa.id_veiculo_tempo_real\n      AND ia.dt_analise >= DATEADD(DAY, -7, SYSDATETIME())\n  )"},
            {"SELECT COUNT(*) AS valor FROM muralha.alerta a JOIN muralha.tipo_alerta_ocorrencia t ON a.id_tipo_alerta_ocorrencia = t.id WHERE t.nomeinterno = 'CONGESTIONAMENTO' AND a.data >= DATEADD(DAY,-7,GETDATE())"},
            {"SELECT TOP 1 percentil95_ms AS valor FROM muralha.alerta_sla ORDER BY janela_fim DESC"},
            {"  select 'DELETE; DROP' AS rotulo, 1 AS valor  "},
            {"SELECT data_alt AS valor FROM muralha.veiculo_tempo_real"},
            {"SELECT COUNT(*) AS valor FROM muralha.status_alerta WHERE UPPER(descricao) <> 'PENDENTE'"}
        };
    }

    @Test(dataProvider = "validas")
    public void aceitaConsultasDeKpi(String sql) {
        assertNull(ValidadorSqlKpi.validar(sql), sql);
    }

    @DataProvider
    public Object[][] invalidas() {
        return new Object[][] {
            {null}, {""}, {"   "},
            {"SELECT 1 AS valor; DELETE FROM muralha.kpi_config"},
            {"SELECT * INTO muralha.copia FROM muralha.kpi_config"},
            {"EXEC sp_who"},
            {"SELECT 1 AS valor -- comentario"},
            {"SELECT 1 AS valor /* x */"},
            {"UPDATE muralha.kpi_config SET ativo = 0"},
            {"SELECT 1 AS valor WHERE 1 = 1 AND EXISTS (SELECT 1 FROM OPENROWSET('a','b','c'))"},
            {"SELECT 'aberto AS valor"},
            {"SELECT xp_cmdshell AS valor"},
            {"WITH c AS (SELECT 1 AS valor) SELECT valor FROM c"},
            {"SELECT 1 AS valor WAITFOR DELAY '00:00:10'"}
        };
    }

    @Test(dataProvider = "invalidas")
    public void recusaConsultasPerigosasOuMalFormadas(String sql) {
        assertNotNull(ValidadorSqlKpi.validar(sql), String.valueOf(sql));
    }

    @Test
    public void recusaConsultaMuitoLonga() {
        StringBuilder sb = new StringBuilder("SELECT 1 AS valor FROM muralha.kpi_config WHERE nome = '");
        while (sb.length() < 4100) sb.append('x');
        sb.append('\'');
        assertNotNull(ValidadorSqlKpi.validar(sb.toString()));
    }
}
```

- [ ] **Step 2: Rodar e ver falhar** (classe inexistente).

- [ ] **Step 3: Implementar o validador**

```java
package muralha.digital.kpi;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Valida o SQL de um KPI antes de gravar e de executar: uma unica consulta SELECT, sem comandos que alterem dados. */
public final class ValidadorSqlKpi {

    public static final int TAMANHO_MAXIMO = 4000;
    public static final int TIMEOUT_SEGUNDOS = 30;

    private static final Pattern PROIBIDAS = Pattern.compile(
            "\\b(INSERT|UPDATE|DELETE|MERGE|DROP|ALTER|CREATE|TRUNCATE|EXEC|EXECUTE|GRANT|REVOKE|DENY|INTO|"
            + "OPENROWSET|OPENQUERY|OPENDATASOURCE|OPENXML|BULK|SHUTDOWN|WAITFOR|DBCC|BACKUP|RESTORE|KILL|RECONFIGURE|USE)\\b"
            + "|\\b(XP|SP)_\\w*",
            Pattern.CASE_INSENSITIVE);

    private ValidadorSqlKpi() {}

    /** @return null se valido; senao a mensagem para o usuario. */
    public static String validar(String sql) {
        if (sql == null || sql.trim().isEmpty()) return "Informe a consulta SQL.";
        String s = sql.trim();
        if (s.length() > TAMANHO_MAXIMO) return "A consulta passa de " + TAMANHO_MAXIMO + " caracteres.";
        String semLiterais = removerLiterais(s);
        if (semLiterais == null) return "Há um texto entre aspas sem fechamento.";
        if (semLiterais.contains("--") || semLiterais.contains("/*")) return "Comentários não são permitidos na consulta.";
        if (!semLiterais.toUpperCase(Locale.ROOT).startsWith("SELECT")) return "A consulta deve começar com SELECT.";
        if (semLiterais.indexOf(';') >= 0) return "Use uma única consulta, sem ponto e vírgula.";
        Matcher m = PROIBIDAS.matcher(semLiterais);
        if (m.find()) return "Comando não permitido na consulta: " + m.group().toUpperCase(Locale.ROOT);
        return null;
    }

    /** Troca o conteudo dos literais '...' (com '' escapado) por espacos; null se um literal nao fechar. */
    static String removerLiterais(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        boolean dentro = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                if (dentro && i + 1 < s.length() && s.charAt(i + 1) == '\'') { sb.append("  "); i++; continue; }
                dentro = !dentro;
                sb.append('\'');
            } else {
                sb.append(dentro ? ' ' : c);
            }
        }
        return dentro ? null : sb.toString();
    }
}
```

- [ ] **Step 4: Usar no servlet e no job.** Em `KpiServlet`:
  - Adicione `static final String ACAO_CONFIG = "/muralha-digital/pages/admin/kpis/index.jsp";`.
  - **`doGet`:** troque a verificação por:

    ```java
    String acao = req.getParameter("acao");
    if (!ControleAcesso.exigir(req, resp)) return;
    if ("listarConfig".equals(acao) && !ControleAcesso.exigir(req, resp, ACAO_CONFIG)) return;
    ```

    Antes, faça `resp.setContentType(...)` só depois dessas linhas. Remova a leitura duplicada de `acao` dentro do `try`.
  - **Execução do painel (laço de KPIs):** troque `if (!sql.toUpperCase().startsWith("SELECT"))` por `if (ValidadorSqlKpi.validar(sql) != null)`. Antes de `executeQuery`, chame `kpiPs.setQueryTimeout(ValidadorSqlKpi.TIMEOUT_SEGUNDOS);`. Use `rs.getString("query_sql")` sem `.trim()` direto, para evitar NPE se for nulo.
  - **`doPost`:** use `if (!ControleAcesso.exigir(req, resp) || !ControleAcesso.exigir(req, resp, ACAO_CONFIG)) return;`. No `salvar`, troque a validação por:

    ```java
    String sql = req.getParameter("querySql");
    String erroSql = ValidadorSqlKpi.validar(sql);
    if (erroSql != null) {
        result.addProperty("ok", false);
        result.addProperty("erro", erroSql);
        resp.getWriter().print(new Gson().toJson(result));
        return;
    }
    sql = sql.trim();
    ```

  Em `KpiSnapshotJob`:
  - troque `if (sql == null || !sql.trim().toUpperCase().startsWith("SELECT")) continue;` por:

    ```java
    String erroSql = ValidadorSqlKpi.validar(sql);
    if (erroSql != null) { logger.warn("KPI id=" + idKpi + " (" + nome + ") ignorado: " + erroSql); continue; }
    ```

  - em `executarQuery`, chame `ps.setQueryTimeout(ValidadorSqlKpi.TIMEOUT_SEGUNDOS);` antes de `executeQuery`.

- [ ] **Step 5: Rodar.** `& <workspace>\run-tests.ps1 muralha.digital.kpi.ValidadorSqlKpiTest,muralha.digital.acessos.ControleAcessoTest`. Esperado: tudo passa.
- [ ] **Step 6: Conferir as 12 consultas reais.** Leia `SELECT id, query_sql FROM muralha.kpi_config` (somente leitura). Os KPIs 1–4, 6, 10 e 12 devem passar no validador; aplique-o num teste descartável ou em `jshell`, sem versionar. Os KPIs 5, 7, 8, 9 e 11 são corrigidos na Task 4.
- [ ] **Step 7: Commit.** Mensagem: "Valida o SQL dos KPIs e exige o direito da Configuracao de KPIs".

---

### Task 4: Migração — corrige o SQL dos KPIs 5, 7, 8, 9 e 11 (FUN-04)

**Causa (verificada em 2026-10-19):** as consultas usam colunas que não existem nesta base:
- `veiculo_tempo_real.resultado_ia` (KPIs 5 e 8);
- `local_vigente.ativo` (KPIs 7 e 11; a coluna real é `desativado`, int 0/1);
- `status_alerta.status` (KPI 9; a coluna real é `descricao`, com OCORRÊNCIA, PENDENTE, DESCARTADO e VINCULADO).

O `KpiSnapshotJob` registra `warn` e pula esses KPIs, por isso eles não têm histórico.

**Files:**
- Create: `docs/banco-de-dados/migracoes/20261019_kpis_corrige_consultas.sql`
- Modify: `docs/banco-de-dados/migracoes/README.md` (nova linha)

- [ ] **Step 1: Escrever o script** (idempotente: só altera o KPI cujo SQL ainda tem a coluna inexistente; grava o histórico em `muralha.parametro_historico` antes de alterar):

```sql
-- =============================================================================
-- Migracao: corrige o SQL dos KPIs 5, 7, 8, 9 e 11 (FUN-04)
-- Data: 2026-10-19 — idempotente.
-- Origem: docs/planos-salvador/35-correcoes-seguranca-funcionais.md (Task 4).
-- As consultas usavam colunas inexistentes (veiculo_tempo_real.resultado_ia,
-- local_vigente.ativo, status_alerta.status); o KpiSnapshotJob pulava esses KPIs.
--   5  Taxa de Infracao por Trafego: infracoes (dbo.infracao, sem imagem de teste)
--      / trafego (SUM de veiculo_sumarizado.trafego) nas ultimas 24 h.
--   7  Disponibilidade Operacional e 11 Conformidade Operacional: local_vigente.desativado = 0.
--   8  Aproveitamento de Registros: disponibilizados / recebidos de dbo.infracao em 7 dias
--      (mesma regra de analise.v_preprocessamento).
--   9  Efetividade de Fiscalizacao: alertas tratados (status diferente de PENDENTE) / gerados em 30 dias.
-- ROLLBACK: os valores anteriores ficam em muralha.parametro_historico
--   (tipo_recurso = 'KPI', parametro = 'query_sql', origem = 'migracao 20261019_kpis_corrige_consultas').
-- =============================================================================
SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @novos TABLE (id INT PRIMARY KEY, marcador VARCHAR(50), query_sql NVARCHAR(MAX));
INSERT INTO @novos (id, marcador, query_sql) VALUES
(5,  'resultado_ia', N'SELECT CASE WHEN ISNULL(t.total,0)=0 THEN 0 ELSE ROUND(CAST(i.total AS FLOAT)/t.total*100,2) END AS valor FROM (SELECT SUM(CAST(trafego AS BIGINT)) total FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE)) t CROSS JOIN (SELECT COUNT(*) total FROM dbo.infracao WHERE data >= DATEADD(DAY,-1,GETDATE()) AND id_enquadramento <> 1) i'),
(7,  'WHERE ativo=1', N'SELECT CASE WHEN total.cnt=0 THEN 0 ELSE ROUND(CAST(ativos.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE desativado=0) total CROSS JOIN (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.veiculo_sumarizado WHERE data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND trafego > 0) ativos'),
(8,  'resultado_ia', N'SELECT CASE WHEN COUNT(*)=0 THEN 100 ELSE ROUND(100.0*SUM(CASE WHEN id_processo_concluido IS NOT NULL AND ISNULL(id_inconsistencia,0)=0 THEN 1 ELSE 0 END)/COUNT(*),1) END AS valor FROM dbo.infracao WHERE data >= DATEADD(DAY,-7,GETDATE()) AND id_enquadramento <> 1'),
(9,  'UPPER(status)', N'SELECT CASE WHEN ISNULL(gerados.total,0)=0 THEN 100 ELSE ROUND(CAST(tratados.total AS FLOAT)/gerados.total*100,1) END AS valor FROM (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE())) gerados CROSS JOIN (SELECT COUNT(*) total FROM muralha.alerta WHERE data >= DATEADD(DAY,-30,GETDATE()) AND id_status_alerta IN (SELECT id FROM muralha.status_alerta WHERE UPPER(descricao) <> ''PENDENTE'')) tratados'),
(11, 'WHERE ativo=1', N'SELECT CASE WHEN total.cnt=0 THEN 100 ELSE ROUND(CAST(ok.cnt AS FLOAT)/total.cnt*100,1) END AS valor FROM (SELECT COUNT(DISTINCT id_local) cnt FROM dbo.local_vigente WHERE desativado=0) total CROSS JOIN (SELECT COUNT(DISTINCT vs.id_local) cnt FROM dbo.veiculo_sumarizado vs WHERE vs.data >= CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND vs.trafego > 0 AND vs.media_velocidade > 0) ok');

BEGIN TRANSACTION;

INSERT INTO muralha.parametro_historico (tipo_recurso, id_recurso, parametro, valor_anterior, valor_novo, id_usuario, login, ip, origem)
SELECT 'KPI', CAST(k.id AS VARCHAR(20)), 'query_sql', k.query_sql, n.query_sql, NULL, 'migracao', NULL,
       'migracao 20261019_kpis_corrige_consultas'
FROM muralha.kpi_config k JOIN @novos n ON n.id = k.id
WHERE CHARINDEX(n.marcador, k.query_sql) > 0;
PRINT CONCAT('Historico gravado: ', @@ROWCOUNT);

UPDATE k SET k.query_sql = n.query_sql
FROM muralha.kpi_config k JOIN @novos n ON n.id = k.id
WHERE CHARINDEX(n.marcador, k.query_sql) > 0;
PRINT CONCAT('KPIs corrigidos: ', @@ROWCOUNT);

COMMIT TRANSACTION;

SELECT id, nome, LEFT(query_sql, 120) AS inicio_sql FROM muralha.kpi_config WHERE id IN (5,7,8,9,11) ORDER BY id;
```

> Antes de rodar, confira os marcadores com `SELECT id, query_sql FROM muralha.kpi_config WHERE id IN (5,7,8,9,11)`: a consulta do KPI 9 contém `UPPER(status)` e as dos KPIs 7 e 11 contêm `WHERE ativo=1`. Se o texto real for diferente (por exemplo, com espaços), ajuste o marcador antes de executar.

- [ ] **Step 2: Executar no DEV** (autorizado para as correções de 2026-10-19): `sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -b -I -f 65001 -i <script>`. Esperado: "KPIs corrigidos: 5".
- [ ] **Step 3: Verificar.** Execute cada um dos cinco `query_sql` novos (somente leitura): todos devem devolver uma linha com `valor`, sem erro. Rode também o `ValidadorSqlKpi` sobre eles: todos válidos. Rodar o script de novo deve imprimir "KPIs corrigidos: 0".
- [ ] **Step 4: README das migrações** — linha com a origem (este plano), o conteúdo e "Executado (2026-10-19)".
- [ ] **Step 5: Commit.** Mensagem: "Corrige o SQL dos KPIs que usavam colunas inexistentes".

---

### Task 5: Gestão de Lotes — direito, inclusão de infrações e regras (SEG-03 e FUN-03)

**Files:**
- Create: `src/main/java/muralha/digital/lote/LoteRegras.java`
- Create: `src/test/java/muralha/digital/lote/LoteRegrasTest.java`
- Modify: `src/main/java/muralha/digital/lote/LoteServlet.java`
- Modify: `src/main/webapp/muralha-digital/pages/processamento/lotes/index.jsp`

**Interfaces:**
- Consumes `ControleAcesso.exigir(req, resp, String)` (Task 1).
- Produces:
  - `public static final String LoteRegras.ACAO_TELA = "/muralha-digital/pages/processamento/lotes/index.jsp"`;
  - `public static final int LoteRegras.MAX_ITENS = 500`;
  - `public static List<UUID> LoteRegras.idsValidos(String csv)`, que lança `IllegalArgumentException` com mensagem em PT-BR.

**Regras:**
- Todas as ações exigem o direito da tela 370 (`ACAO_TELA`).
- Só entram em lote infrações com `muralha.vtr_status_analise.status_analise = 'PRE_APROVADA'` que ainda não estão em nenhum lote. A unicidade é garantida pelo índice `uq_lote_item`.
- Só lote em `RASCUNHO` recebe itens, é enviado ou é cancelado.
- Lote sem itens não pode ser marcado como enviado.
- Não há FK para `veiculo_tempo_real`: os dados de dupla análise do DEV não correspondem a passagens. A listagem usa `LEFT JOIN` com a passagem.
- Auditoria (`AuditoriaService.registrar(req, "Lote", <operacao>, <id>, <descricao>)`) em criar, adicionar, enviar e cancelar, só quando algo mudou.

- [ ] **Step 1: Teste que falha**

```java
package muralha.digital.lote;

import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class LoteRegrasTest {

    @Test
    public void aceitaListaComEspacosERepetidos() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        List<UUID> ids = LoteRegras.idsValidos(" " + a + " ," + b + "," + a + ",");
        assertEquals(ids.size(), 2);
        assertEquals(ids.get(0), a);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Nenhuma infração.*")
    public void recusaListaVazia() { LoteRegras.idsValidos(" , "); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Nenhuma infração.*")
    public void recusaNulo() { LoteRegras.idsValidos(null); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*inválido.*")
    public void recusaUuidInvalido() { LoteRegras.idsValidos(UUID.randomUUID() + ",abc"); }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*500.*")
    public void recusaAcimaDoLimite() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LoteRegras.MAX_ITENS + 1; i++) sb.append(UUID.randomUUID()).append(',');
        LoteRegras.idsValidos(sb.toString());
    }
}
```

- [ ] **Step 2: Rodar e ver falhar.**

- [ ] **Step 3: Implementar `LoteRegras`**

```java
package muralha.digital.lote;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Regras da Gestao de Lotes que nao dependem do banco. */
public final class LoteRegras {

    public static final String ACAO_TELA = "/muralha-digital/pages/processamento/lotes/index.jsp";
    public static final int MAX_ITENS = 500;

    private LoteRegras() {}

    public static List<UUID> idsValidos(String csv) {
        Set<UUID> ids = new LinkedHashSet<>();
        if (csv != null) {
            for (String parte : csv.split(",")) {
                String id = parte.trim();
                if (id.isEmpty()) continue;
                try {
                    ids.add(UUID.fromString(id));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Identificador de infração inválido: " + id);
                }
            }
        }
        if (ids.isEmpty()) throw new IllegalArgumentException("Nenhuma infração selecionada.");
        if (ids.size() > MAX_ITENS) throw new IllegalArgumentException("Selecione no máximo " + MAX_ITENS + " infrações por vez.");
        return new ArrayList<>(ids);
    }
}
```

- [ ] **Step 4: Alterar o `LoteServlet`**
  - Em `doGet` e `doPost`, troque a verificação por `if (!ControleAcesso.exigir(req, resp, LoteRegras.ACAO_TELA)) return;`.
  - **GET `itens`:** troque `JOIN muralha.veiculo_tempo_real` por `LEFT JOIN`. Use `rs.getObject` para `id_local` e `id_pista`, que podem vir nulos, e devolva `null` no JSON nesse caso.
  - **GET `disponiveis` (nova):**

    ```sql
    SELECT TOP 500 CAST(vsa.id_veiculo_tempo_real AS VARCHAR(36)) AS id, vtr.placa, vtr.id_local,
           CONVERT(VARCHAR(19), vtr.data, 120) AS dt_captura
    FROM muralha.vtr_status_analise vsa
    LEFT JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vsa.id_veiculo_tempo_real
    WHERE vsa.status_analise = 'PRE_APROVADA'
      AND NOT EXISTS (SELECT 1 FROM muralha.lote_infracao_item li WHERE li.id_infracao = vsa.id_veiculo_tempo_real)
    ORDER BY vtr.data
    ```

    Resposta: `{"ok":true,"itens":[{"id","placa","idLocal","dtCaptura"}]}`.
  - **POST `adicionarInfracoes`:**
    1. `List<UUID> ids = LoteRegras.idsValidos(req.getParameter("ids"));`. Em caso de `IllegalArgumentException`, responda `ok:false` com a mensagem.
    2. Confira que o lote está em `RASCUNHO` (`SELECT status FROM muralha.lote_infracao WHERE id = ?`); senão, responda "Só é possível incluir infrações em lote em rascunho."
    3. Em transação (`conn.setAutoCommit(false)`, `commit` no fim e `rollback` no `catch`), para cada id:

       ```sql
       INSERT INTO muralha.lote_infracao_item (id_lote, id_infracao)
       SELECT ?, ? WHERE EXISTS (SELECT 1 FROM muralha.vtr_status_analise
                                 WHERE id_veiculo_tempo_real = ? AND status_analise = 'PRE_APROVADA')
         AND NOT EXISTS (SELECT 1 FROM muralha.lote_infracao_item WHERE id_infracao = ?)
       ```

       Passe o UUID com `ps.setString(n, id.toString())`.
    4. Responda `adicionados` e `ignorados` (`ids.size() - adicionados`).
    5. Registre a auditoria "adicionar" com "N infrações incluídas" quando `adicionados > 0`.
  - **POST `enviar`:** antes do `UPDATE`, conte os itens. Se forem zero, responda `ok:false`, "Lote sem infrações não pode ser marcado como enviado.". A auditoria passa a ser registrada só quando `rows > 0`, com a descrição "Lote marcado como enviado (sem integração com o DETRAN)".
  - **POST `cancelar`:** registre a auditoria "cancelar" quando `rows > 0`.
  - **POST `criar`:** mantenha como está, com o `descricao` limitado a 200 caracteres (`substring`) se vier maior.

- [ ] **Step 5: Alterar a tela `lotes/index.jsp`**
  - Renomeie o botão e as confirmações "Enviar para DETRAN" para **"Marcar como enviado"** (ícone `bi-check2-square`). O texto da confirmação explica: "O lote será marcado como enviado. A integração com o DETRAN ainda não está disponível."
  - Para lotes em RASCUNHO, adicione o botão **"Adicionar infrações"** (`bi-plus-square`). Ele abre um modal Bootstrap que:
    - carrega `?acao=disponiveis`;
    - mostra uma tabela com checkbox, ID, placa, local e data (vazios aparecem como "—");
    - tem filtro de texto por placa ou ID, feito no cliente;
    - tem "Selecionar todos";
    - envia `POST acao=adicionarInfracoes&idLote=…&ids=<csv>`;
    - mostra `adicionados` e `ignorados` em SweetAlert2 e recarrega a lista.
  - Todo valor vindo do servidor entra no HTML por `escaparHtml()`: defina `function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML; }` no script da página. Passe ids para os handlers por `data-id` e listener, não por `onclick="...('<id>')"`.
  - Quando a resposta vier com `ok:false` (inclusive 401 ou 403), mostre `d.erro` em SweetAlert2.

- [ ] **Step 6: Rodar.** `& <workspace>\run-tests.ps1 muralha.digital.lote.LoteRegrasTest`. Depois compile com `test-compile`.
- [ ] **Step 7: Conferir o SQL de `disponiveis`** com `sqlcmd` (somente leitura). Esperado: as 20 PRE_APROVADA do DEV, com placa nula.
- [ ] **Step 8: Commit.** Mensagem: "Permite incluir infracoes nos lotes e restringe as acoes da Gestao de Lotes".

---

### Task 6: Tipos de Alerta — aparência para o popup e verificação estrita (SEG-10)

**Files:**
- Modify: `src/main/java/muralha/digital/alerta/operacional/TipoAlertaConfigServlet.java`
- Modify: `src/main/webapp/muralha-digital/utils/notificacao/js/notificacao.js`
- Test: `src/test/java/muralha/digital/alerta/operacional/TipoAlertaAparenciaTest.java`

**Interfaces:**
- Consumes `ControleAcesso.exigir`/`exigirTela` (Task 1).
- Produces `static List<Map<String,Object>> TipoAlertaConfigServlet.aparencia(List<Map<String,Object>> configs)`, que devolve só `tipo`, `cor` e `icone`.

- [ ] **Step 1: Teste que falha.** `aparencia(...)` recebe a lista no formato de `listarConfigs`, com `id`, `tipo`, `descricao`, `prioridade`, `ativo`, `critico`, `cor`, `icone`, `tratamentos` e `automaticos`. Ela devolve mapas só com as chaves `tipo`, `cor` e `icone`, na mesma ordem.
- [ ] **Step 2: Rodar e ver falhar.**
- [ ] **Step 3: Implementar**
  - Crie `static List<Map<String, Object>> aparencia(List<Map<String, Object>> configs)`, que copia só as três chaves.
  - **GET:**
    - mantenha a checagem de sessão;
    - `acao=aparencia` exige **só sessão** e responde `gson.toJson(aparencia(listarConfigs(conn)))`;
    - sem `acao`, use `ControleAcesso.exigirTela(req, resp)` e encaminhe para a JSP;
    - para as outras ações, inclusive `listar`, use `ControleAcesso.exigir(req, resp)`;
    - atualize o comentário: "A aparência (cor e ícone) é lida pelo popup de notificação de qualquer operador logado; o restante exige o direito da tela."
  - **POST:** troque `Acesso.verificaAcesso` por `ControleAcesso.exigir(req, resp)`.
  - **`notificacao.js`:** troque a URL para `/MuralhaDigital/TipoAlertaConfig?acao=aparencia`. O código já usa só `t.tipo`, `t.cor` e `t.icone`.
- [ ] **Step 4: Rodar** o teste e os de `muralha.digital.alerta.operacional`: `ConfigETratamentoTest`, `ProcessadorRetencaoTest` e `RegraRetencaoTest`.
- [ ] **Step 5: Commit.** Mensagem: "Separa a aparencia dos tipos de alerta e exige o direito nas demais acoes".

---

### Task 7: XSS — escapar dados do servidor nas telas (SEG-05)

**Files (Modify):**
- `src/main/webapp/muralha-digital/pages/dashboard/kpis/index.jsp`: `renderCard`, `renderCompacto`, `renderMedidor`, `renderProgresso`, título e histórico.
- `src/main/webapp/muralha-digital/pages/admin/kpis/index.jsp`: tabela de configuração (linha ~111) e preenchimento do formulário.
- `src/main/webapp/muralha-digital/pages/relatorios/gerencial/index.jsp`: tabela (linhas ~87–107) e `renderDetalhe` (linha ~116).
- `src/main/webapp/muralha-digital/assets/js/auditoria/consulta-log.js`: linhas da tabela do Log de Auditoria.

- [ ] **Step 1: Inventário.** Em cada arquivo, liste toda concatenação de HTML (`innerHTML`, `insertAdjacentHTML`, `+ '<td>' + ...`, template strings) que inclua campos vindos do servidor:
  - nome, descricao, unidade, titulo e tipo;
  - login, usuario, funcionalidade, operacao, idRegistro, descricao e ip;
  - textos de relatório;
  - `corPersonalizada`.
- [ ] **Step 2: Corrigir.**
  - Em cada arquivo, defina (ou reutilize, se já houver) `function escaparHtml(v) { var d = document.createElement('div'); d.textContent = v == null ? '' : String(v); return d.innerHTML; }` e aplique a todos os campos do inventário.
  - **Cores** usadas em `style` (ex.: `corPersonalizada`): aceite só `/^#[0-9a-fA-F]{3,8}$/`. Fora disso, use a cor padrão já existente.
  - **Handlers com dados** (ex.: `onclick="abrirHistorico(' + id + ',\'' + nome + '\')"`): troque por `data-id`/`data-nome` com `addEventListener`. O `nome` vem de `dataset` e é atribuído com `textContent`.
  - Não altere a lógica nem o visual.
- [ ] **Step 3: Verificar no navegador.**
  - Com o Tomcat rodando, crie pela Configuração de KPIs um KPI de teste com nome `<img src=x onerror=alert(1)>`, descrição `"'><b>x</b>` e SQL `SELECT 1 AS valor`.
  - Abra o painel de KPIs: o texto aparece literal e nenhum alerta é disparado. Abra o histórico desse KPI: o título aparece literal.
  - **Depois, desative o KPI de teste** pela tela.
  - Se o Tomcat não estiver disponível ao subagente, faça a verificação estática do inventário: nenhum campo do servidor fica fora de `escaparHtml`. Registre no relatório que a verificação foi estática.
- [ ] **Step 4: Commit.** Mensagem: "Escapa os dados do servidor nas telas de KPIs, Relatorios Gerenciais e Log de Auditoria".

---

### Task 8: Login sem hash de senha no log nem na resposta (SEG-09)

**Files:** Modify `src/main/java/muralha/digital/acessos/UsuarioServlet.java` (linhas ~85 e ~218).

- [ ] **Step 1:** Na linha ~85, troque o `logger.debug("doPost - " + usuario + " - " + senhaMD5 + " - " + acao)` por `logger.debug("doPost - " + usuario + " - " + acao)`.
- [ ] **Step 2:** Na linha ~218, remova `myObj.addProperty("senhaMD5", senhaMD5);`. Verificação feita em 2026-10-19: nenhum JS fora de `GtwWidgets/` lê `.senhaMD5`. Confirme com `git grep -n "senhaMD5" -- src/main/webapp ':!src/main/webapp/GtwWidgets'`; se o grep encontrar uso, **não** remova o campo e registre no relatório.
- [ ] **Step 3:** Compile com `test-compile`.
- [ ] **Step 4: Commit.** Mensagem: "Remove o hash da senha do log e da resposta do login".

---

### Task 9: RelatorioGerencialJob — tabela e colunas corretas (FUN-01)

**Files:** Modify `src/main/java/muralha/digital/relatorio/RelatorioGerencialJob.java`.

- [ ] **Step 1: Corrigir `alertasAnaliticos`** (linhas ~170–191). A consulta passa a ser:

```java
"SELECT id_registro, descricao, CONVERT(VARCHAR(19), dt_operacao, 120) AS data " +
"FROM dbo.sis_log_auditoria " +
"WHERE funcionalidade = 'KPI' AND operacao = 'ALERTA_ANALITICO' " +
"AND dt_operacao >= ? AND dt_operacao < DATEADD(DAY, 1, ?) " +
"ORDER BY dt_operacao DESC"
```

O `KpiSnapshotJob` grava esses alertas com `AuditoriaService.registrarSistema("KPI", "ALERTA_ANALITICO", ...)`.

- [ ] **Step 2: Corrigir `disponibilidade`** (linha ~228): `FROM dbo.local_vigente WHERE desativado = 0`. A coluna `ativo` não existe; `desativado` é int 0/1.
- [ ] **Step 3: Revisar o resto do job.** Para cada consulta do arquivo, substitua os `?` por datas literais (`'2026-09-01'`, `'2026-09-30'`) e execute com `sqlcmd` (somente leitura). Todas devem rodar sem erro. Corrija qualquer outra coluna inexistente encontrada e registre no relatório.
- [ ] **Step 4: Valores nulos.** Onde houver `rs.getDouble` de `AVG`, use `rs.getObject(...) == null ? 0 : rs.getDouble(...)`, ou mantenha `getDouble`, que devolve 0 para nulo. Não pode haver `NullPointerException` com período vazio.
- [ ] **Step 5:** Compile com `test-compile`. Commit: "Corrige as consultas do relatorio gerencial".

---

### Task 10: RelatorioPreProcMensalJob — dados existentes (FUN-02)

**Files:** Modify `src/main/java/muralha/digital/relatorio/RelatorioPreProcMensalJob.java`.

**Dados reais (2026-10-19):**
- `muralha.vtr_status_analise` tem só `id_veiculo_tempo_real` e `status_analise`, com os valores DESEMPATE, PRE_APROVADA e REPROVADA;
- `muralha.infracao_analise` tem `id_infracao`, `id_usuario`, `sequencia` (1, 2…), `classificacao`, `justificativa` e `dt_analise`.

Mantenha as chaves do JSON gravado: `recebidos`, `processados`, `disponibilizados`, `descartados`, `indiceAproveitamento`, `tempoMedioProcessamentoSeg`, `produtividadePorOperador`, `divergencia`, `retrabalho`, `pendentesEBacklog`, `sla`, `motivosDescarte` e `historicoClassificacao`. A tela de Relatórios Gerenciais lê essas chaves.

- [ ] **Step 1: Trocar as consultas** (todas testadas em 2026-10-19 no DEV, somente leitura; `?` = início e fim do mês):
  - **(a–d) volumes:** mantenha a consulta, trocando os conjuntos de status:
    - `disponibilizados`: `IN ('PRE_APROVADA','APROVADA')`;
    - `descartados`: `IN ('REPROVADA','DESCARTADA')`.
  - **(f) tempo médio:**

    ```sql
    SELECT AVG(CAST(DATEDIFF(SECOND, vtr.data, ia.dt_analise) AS FLOAT)) AS tempo_medio_seg
    FROM muralha.infracao_analise ia
    JOIN muralha.veiculo_tempo_real vtr ON vtr.id = ia.id_infracao
    WHERE ia.sequencia = 1 AND vtr.data >= ? AND vtr.data < DATEADD(DAY, 1, ?)
    ```

    Se o resultado for nulo, grave 0.
  - **(g) produtividade:**

    ```sql
    SELECT ia.id_usuario, COUNT(*) AS total_analisados
    FROM muralha.infracao_analise ia
    WHERE ia.dt_analise >= ? AND ia.dt_analise < DATEADD(DAY, 1, ?)
    GROUP BY ia.id_usuario ORDER BY total_analisados DESC
    ```

  - **(h) divergência** (primeira e segunda análise discordam):

    ```sql
    SELECT COUNT(*) AS total,
           SUM(CASE WHEN a1.classificacao <> a2.classificacao THEN 1 ELSE 0 END) AS divergentes
    FROM muralha.infracao_analise a1
    JOIN muralha.infracao_analise a2 ON a2.id_infracao = a1.id_infracao AND a2.sequencia = 2
    WHERE a1.sequencia = 1 AND a2.dt_analise >= ? AND a2.dt_analise < DATEADD(DAY, 1, ?)
    ```

  - **(i) retrabalho** (precisou de desempate):

    ```sql
    SELECT COUNT(DISTINCT ia.id_infracao) AS total,
           COUNT(DISTINCT CASE WHEN ia.sequencia >= 3 OR vsa.status_analise = 'DESEMPATE' THEN ia.id_infracao END) AS retrabalhados
    FROM muralha.infracao_analise ia
    LEFT JOIN muralha.vtr_status_analise vsa ON vsa.id_veiculo_tempo_real = ia.id_infracao
    WHERE ia.dt_analise >= ? AND ia.dt_analise < DATEADD(DAY, 1, ?)
    ```

  - **(m) motivos de descarte** (justificativa da última análise das reprovadas):

    ```sql
    SELECT COALESCE(NULLIF(LTRIM(RTRIM(ult.justificativa)), ''), 'Sem motivo') AS motivo, COUNT(*) AS total
    FROM muralha.vtr_status_analise vsa
    CROSS APPLY (SELECT TOP 1 ia.justificativa, ia.dt_analise FROM muralha.infracao_analise ia
                 WHERE ia.id_infracao = vsa.id_veiculo_tempo_real ORDER BY ia.sequencia DESC) ult
    WHERE vsa.status_analise IN ('REPROVADA', 'DESCARTADA')
      AND ult.dt_analise >= ? AND ult.dt_analise < DATEADD(DAY, 1, ?)
    GROUP BY COALESCE(NULLIF(LTRIM(RTRIM(ult.justificativa)), ''), 'Sem motivo')
    ORDER BY total DESC
    ```

  - **(n) histórico de classificação** (status final das infrações analisadas no mês):

    ```sql
    SELECT vsa.status_analise AS classificacao, COUNT(*) AS total
    FROM muralha.vtr_status_analise vsa
    WHERE EXISTS (SELECT 1 FROM muralha.infracao_analise ia
                  WHERE ia.id_infracao = vsa.id_veiculo_tempo_real
                    AND ia.dt_analise >= ? AND ia.dt_analise < DATEADD(DAY, 1, ?))
    GROUP BY vsa.status_analise ORDER BY total DESC
    ```

  - **(j–k) pendentes e (l) SLA:** mantenha.
  - Para cada consulta, use `java.sql.Date.valueOf(inicio)` e `java.sql.Date.valueOf(fim)`, como no código atual.
- [ ] **Step 2: Conferir.** Rode as consultas com `sqlcmd` para outubro de 2026 (somente leitura). Valores esperados no DEV em 2026-10-19:
  - produtividade: 3 operadores, 60 análises;
  - divergência: total 30, divergentes 6;
  - retrabalho: total 30, retrabalhados 6;
  - motivos: 4 linhas;
  - classificação: PRE_APROVADA 20, DESEMPATE 6, REPROVADA 4.
- [ ] **Step 3: Teste de unidade** para `ehDiaUtilAlvo`, se ainda não existir: `src/test/java/muralha/digital/relatorio/RelatorioPreProcMensalJobTest.java`. Casos: o 5º dia útil de outubro de 2026 é 2026-10-07; um sábado nunca é dia alvo.
- [ ] **Step 4:** Compile, rode o teste e faça o commit: "Reescreve o relatorio mensal de pre-processamento sobre os dados de analise existentes".

---

### Task 11: Carga periódica da camada `analise` (FUN-05)

**Files:**
- Create: `src/main/java/muralha/digital/analise/CargaAnaliseJob.java`
- Modify: `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` (constantes, método `reajustaJobCargaAnalise()` e a chamada junto dos demais `reajustaJob*` em `verificarJobsGTW`)
- Modify: `docs/analise/guia-administrador.md` (seção de cargas/agendamento)

- [ ] **Step 1: Implementar o job**

```java
package muralha.digital.analise;

import com.consilux.lib.Conexao;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * Recarrega as tabelas materializadas da plataforma de Analises (schema analise).
 * Mesmas procedures de analise/scripts/refresh-materialized.sh, com as janelas padrao
 * (25 h de passagens, 7 dias de infracoes). Cada procedure registra em analise.carga_log.
 */
@DisallowConcurrentExecution
public class CargaAnaliseJob implements Job {

    private static final Logger logger = Logger.getLogger(CargaAnaliseJob.class);

    static final String[] PROCEDURES = {
        "analise.spu_carga_passagens_hora",
        "analise.spu_carga_infracoes_dia",
        "analise.spu_carga_areas_monitoradas"
    };

    @Override
    public void execute(JobExecutionContext ctx) {
        for (String proc : PROCEDURES) {
            long inicio = System.currentTimeMillis();
            try (Connection conn = Conexao.getConexao();
                 CallableStatement cs = conn.prepareCall("{call " + proc + "}")) {
                cs.setQueryTimeout(600);
                cs.execute();
                logger.info("Carga " + proc + " concluida em " + (System.currentTimeMillis() - inicio) + " ms.");
            } catch (Exception e) {
                logger.error("Erro na carga " + proc + ": " + e.getMessage(), e);
            }
        }
    }
}
```

> Confira se `@DisallowConcurrentExecution` existe na versão do Quartz do projeto (`org.quartz.DisallowConcurrentExecution`, Quartz 2.x). Se não existir, remova a anotação e registre no relatório.

- [ ] **Step 2: Agendar** no padrão de `reajustaJobKpiSnapshot` (`Agendador.java` ~1048):
  - constantes `GRUPO_ANALISE = "Analise"` e `JOB_CARGA_ANALISE = "CargaAnalise"`;
  - `repeatMinutelyForever(30)`;
  - `logger.info("Job Carga Analise agendada a cada 30 minutos.")`;
  - chamada no mesmo bloco onde `reajustaJobKpiSnapshot()` é chamado.
- [ ] **Step 3: Documentar** em `docs/analise/guia-administrador.md`:
  - as tabelas materializadas são recarregadas pelo GTW a cada 30 minutos (`CargaAnaliseJob`);
  - o `refresh-materialized.sh` fica para recarga manual ou de período;
  - o log fica em `analise.carga_log`.
- [ ] **Step 4:** Compile com `test-compile`. Commit: "Agenda no GTW a carga periodica das tabelas da plataforma de Analises".

---

### Task 12: Documentação da entrega

**Files:**
- Create: `docs/entregas/35-correcoes-seguranca-funcionais.md` (com "## Origem" apontando para `../planos-salvador/35-correcoes-seguranca-funcionais.md` e para `../referencia/guia-correcoes-analise-kpis-lotes.md`)
- Modify: `docs/referencia/guia-correcoes-analise-kpis-lotes.md`: na tabela do §3, marque SEG-01, 03, 04, 05, 09 e 10 e FUN-01 a 05 como "✅ Corrigido (2026-10-19, plano 35)"; inclua uma nota de situação em cada item do §4.
- Modify: `docs/analise/manual-usuario.md` e o manual da Gestão de Lotes, se houver referência a "Enviar para DETRAN". Procure com `git grep -n "Enviar para DETRAN"`.

- [ ] **Step 1:** Escreva a entrega:
  - o que mudou por item;
  - como verificar;
  - o que ficou de fora: fases 3 e 4, e a integração com o DETRAN.
- [ ] **Step 2:** Atualize o guia e os manuais.
- [ ] **Step 3:** Confira os links (`Test-Path` em cada link relativo).
- [ ] **Step 4: Commit.** Mensagem: "Documenta as correcoes de seguranca e funcionais das fases 1 e 2".
