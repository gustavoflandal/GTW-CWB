# 06 — Padrões de Código

> Regras para **código novo**. Elas descrevem o padrão dominante do Muralha Digital (observado em `GuarnicaoServlet`, `Guarnicao`, `AlertaServlet`, `UsuarioServlet`) corrigindo falhas recorrentes. Código antigo que fuja destas regras **não é exemplo** — veja "Anti-padrões" no fim.

## 1. Java — Servlet (camada HTTP)

```java
@WebServlet("/MuralhaDigital/Entidade")            // sempre /MuralhaDigital/<Entidade> (PascalCase, singular)
public class EntidadeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EntidadeServlet.class);   // log4j 1.2
    private static final Gson gson = new Gson();                                   // Gson 2.8.8

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. Autenticação/autorização (OBRIGATÓRIO – ver §1.1)
        // 2. Dispatch por ação
        String acao = req.getParameter("acao");
        try {
            if ("listar".equals(acao)) listar(resp);
            else if ("obterPorId".equals(acao)) obterPorId(req, resp);
            else resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
        } catch (Exception e) {
            logger.error("Erro no doGet de EntidadeServlet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        }
    }
    // doPost análogo para cadastrar/atualizar/excluir
}
```

Regras:
- **GET = leitura; POST = escrita.** Ações em `camelCase` verbo+objeto (`listarGuarnicoes`, `cadastrarInfoDiaria`, `deletarGuarnicao`). Comparar com literal primeiro (`"x".equals(acao)`) para evitar NPE.
- **Resposta JSON**: `resp.setContentType("application/json; charset=UTF-8"); resp.setCharacterEncoding("UTF-8"); resp.getWriter().write(gson.toJson(obj));`. Nomes de campos em `snake_case` quando vierem de colunas (padrão do front existente); datas já formatadas em campo `*_formatado` (`data_criacao_formatado`) quando a tela as exibe.
- **Erros**: `sendError(400)` para parâmetro inválido (mensagem em português), `404` para não encontrado, `500` genérico — **nunca devolver stack trace ou mensagem de SQL ao cliente**. Para resultado de comando use `{"sucesso":true|false,"mensagem":"…"}` (`muralha.digital.util.Resultado`).
- Validar **todo** parâmetro (`null/empty`, `Integer.parseInt` em `try/catch NumberFormatException` → 400). Limitar tamanhos de texto e listas.
- Upload: `commons-fileupload`; validar extensão/tamanho/tipo (`jpg, jpeg, png, pdf` nos módulos existentes) e nunca usar o nome do arquivo do cliente como caminho.
- Download de arquivo: `Content-Disposition` com nome sanitizado; autorizar por registro (não só por login).

### 1.1 Autenticação e autorização (obrigatórias em todo servlet)
O filtro `ValidaSessao` **não bloqueia requisição sem sessão**, e ~11 servlets Muralha existentes não verificam acesso (`GuarnicaoServlet`, `NotificacaoServlet`, `RelatorioAuditoriaServlet`…). **Todo servlet novo deve** abrir assim:

```java
Usuario usuario = (Usuario) req.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);   // "[usuario]"
if (usuario == null) { resp.sendError(HttpServletResponse.SC_UNAUTHORIZED); return; }
// permissão por funcionalidade (cadastrada em sis_menu.acao + sis_menu_direitos):
if (!new Acesso(req, resp, true).verificaAcesso(false)) { return; }   // padrão de AlertaServlet
```
Use `req.getSession(false)` quando não quiser criar sessão. Registre a nova ação em `sis_menu` (ver `10-guia-de-implementacao.md` §4).

## 2. Java — Classe de domínio / acesso a dados

```java
public static List<Guarnicao> listarTodas() throws SQLException, ConexaoException {
    Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
    try {
        conn = Conexao.getConexao();
        ps = conn.prepareStatement("SELECT id, nome FROM muralha.guarnicao WHERE ativo = ? ORDER BY nome");
        ps.setInt(1, 1);
        rs = ps.executeQuery();
        List<Guarnicao> lista = new ArrayList<>();
        while (rs.next()) { /* mapear */ }
        return lista;
    } finally {
        if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignore) {}
        if (conn != null) try { conn.close(); } catch (SQLException ignore) {}     // devolve ao pool
    }
}
```
- **Sempre** `Conexao.getConexao()` e fechar no `finally` (pool máx. 400, `maxWait 2 s`: vazamento derruba o sistema). Preferir **try-with-resources** (Java 13) em código novo.
- **Sempre `PreparedStatement` com `?`**. Nunca concatenar parâmetros do usuário em SQL (há concatenação de colunas fixas em código antigo — não copiar). Para `IN (…)` gere os `?` dinamicamente.
- Procedures: `EXEC muralha.spu_Nome ?, ?` via `prepareStatement` (resultset) ou `prepareCall("{call …}")`. Reutilize `spu_*`/`fcn_*`/`v_*` existentes antes de escrever SQL novo (catálogo em `banco-de-dados/objetos-programaveis.md`).
- Qualificar o schema: `muralha.` ou `dbo.`. Tabelas grandes (`veiculo_tempo_real`, `veiculo_tempo_real_imagem`) **sempre** com filtro de período/equipamento e `TOP`/paginação; usar `WITH (NOLOCK)` apenas em leituras de tela já assim (como `fcn_VerificaAcesso`).
- Transação: `conn.setAutoCommit(false)` → `commit/rollback` → restaurar. Auto-commit é o padrão do pool.
- Exclusão: **soft delete** (`ativo = 0`) é a norma (`deletarGuarnicao`/`acaoSoft`); histórico em tabelas `*_historico` quando existir.
- Mapear `null` de colunas (`rs.getObject`, `wasNull`). Datas: `java.sql.Timestamp`; formatar em `SimpleDateFormat("dd/MM/yyyy HH:mm:ss")` (não compartilhar instância entre threads).
- Não usar `Statement` cru, `System.out`, `printStackTrace()`; usar `logger`.

## 3. Java — estilo
- Pacote `muralha.digital.<modulo>` (minúsculas, sem hífen); classes `PascalCase` em português; `<Entidade>` (item/POJO), `<Entidades>` (coleção/consultas), `<Entidade>Servlet`, `<Entidade>DTO` para payloads compostos.
- Indentação por **tab**, chaves como no arquivo vizinho; `UTF-8` em todos os arquivos (`project.build.sourceEncoding`).
- Logs: `logger.info/error` com contexto (ids, usuário). **Nunca logar senha, token, CPF completo ou chave de API.**
- Constantes de sessão/atributos em classes de constantes (`SessaoConstantes`, `Constantes`), não literais espalhados.
- Sem Lombok, sem Java > 13 (sem `record`, sem *text blocks*, sem `switch` arrow) — o JDK é o 13.0.1.
- Datas/valores com bibliotecas já presentes (`commons-lang3`, `Funcoes`). Não adicionar dependências.

## 4. JSP
- Cabeçalho obrigatório: `<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>` e `<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>` (faz login, menus e navbar).
- **Sem lógica de negócio nem SQL em JSP nova.** JSP só monta HTML, inclui modais e carrega scripts; dados via `$.ajax`.
- Taglib: `<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>`; use `<c:out>`/`${fn:escapeXml(...)}` para qualquer valor dinâmico (evitar XSS). Evitar `<%= %>` com dados do usuário.
- Um diretório por tela em `muralha-digital/pages/<kebab>/`; modais em `modal-*.jsp`; reaproveitar com `<%@ include %>`.
- Caminhos absolutos a partir da raiz (`/muralha-digital/...`) para assets compartilhados; relativos (`assets/js/x.js`) para assets da própria tela.

## 5. JavaScript (jQuery)
- Um arquivo por tela: `assets/js/<tela>.js`; carregar no fim do `<body>`. Código em `$(document).ready(function () { … })`; funções nomeadas em `camelCase` (`listarGuarnicoes`).
- Chamadas ao servidor:
  ```js
  $.ajax({ type: "GET", url: "/MuralhaDigital/Guarnicao", dataType: "json", data: { acao: "listar" },
           success: function (data) { … }, error: function (jqXHR, textStatus, errorThrown) { /* mensagem ao usuário */ } });
  ```
  Enviar parâmetros por objeto `data` (não concatenar na URL). POST para escrita.
- Delegação de eventos em tabelas dinâmicas: `$('#tabela tbody').on('click', '.btn[title="Editar"]', …)`.
- Montagem de HTML por template string: **escapar** valores (use `.text()`/`$('<td>').text(v)` ou uma função `esc()`); IDs em `data-id`.
- Feedback com `Swal.fire` (§7.6 de `05-padrao-visual.md`); proibido `alert()/confirm()` em código novo; remover `console.log` de depuração antes do commit.
- Datas: moment/Tempus Dominus; números/placas: máscaras existentes. Sem frameworks adicionais, sem `npm`/bundlers, sem ES modules (browser alvo: Chrome/Edge atuais, mas manter ES2015+ simples).
- Tempo real: `new WebSocket("ws(s)://<host>/ClientesWebSocket")` e enviar `...<TIPO>-<SUBTIPO>-...` conforme `02-arquitetura.md` §6; tratar reconexão.

## 6. SQL (SQL Server 2016)
- Nomes `snake_case` minúsculos; tabelas no singular; PK `id` (`muralha.*`) ou `id_<tabela>` (`dbo.*`); FK `id_<referenciada>`; flags `ativo bit/int`; datas `data_<evento>`; auditoria `data_criacao`, `data_modificacao`, `id_usuario*`.
- Procedures `spu_<Acao>` (padrão atual; `sp_` e `usp_` são raros), funções tabela `fcn_<Nome>`, views `v_<nome>`/`vw_<nome>`. Schema explícito.
- Compatibilidade **SQL Server 2016** (sem `STRING_AGG`, `TRIM`, `CONCAT_WS`, `GENERATE_SERIES`; o banco está em nível de compatibilidade **110**, então também evite `STRING_SPLIT`/`OPENJSON` sem testar).
- Toda mudança de esquema = script versionado em `docs/banco-de-dados/migracoes/AAAAMMDD_descricao.sql`, idempotente (`IF NOT EXISTS`), com rollback comentado e aprovação — **nunca** aplicar DDL direto sem autorização. Ver `agentes/agente-banco-de-dados.md`.

## 7. Tratamento de erros e logs (resumo)
Servidor: log com stack no `error.log`, resposta genérica ao cliente. Cliente: mensagem amigável + `console.error`. Erros previsíveis (validação) → 400 com texto; falha inesperada → 500.

## 8. Testes
Há quase nenhum teste automatizado (TestNG + Mockito + HtmlUnit). Para código novo: teste unitário das regras puras (sem banco); roteiro de teste manual no PR (login → fluxo → conferir banco/log). Não rodar testes contra o banco de produção. Build/teste: `..\.setup-gtw\build.ps1` (`-DskipTests` por padrão; rode testes com `mvn -s … test` quando existirem).

## 9. Git
- Branch `main`; commits em português, imperativo curto (`Adiciona filtro de equipamento na consulta`). Segredos **nunca** (`confGTW.xml` e `muralha-digital-config.xml` estão ignorados).
- Não versionar `target/`, `ffmpeg.exe`, logs, arquivos de IDE, dumps de banco.

## 10. Anti-padrões encontrados (não replicar)
| Anti-padrão | Onde | Correção |
|---|---|---|
| Credenciais/chaves no código | já removidos de `LoginGoogle`, `ServicoEmailMuralha`, `ServicoSMS` | Config local/ambiente |
| Servlet sem verificar sessão | `GuarnicaoServlet`, `NotificacaoServlet`, relatórios de auditoria | §1.1 |
| SQL por concatenação de strings | partes de `UsuarioServlet`, `Alertas` (`sbSQL.append`) | `PreparedStatement` e `?` |
| `Connection` fechada antes de `ResultSet`/`PreparedStatement` e sem try-with-resources | vários `finally` | fechar em ordem inversa / try-with-resources |
| `alert()/confirm()` + `location.reload()` com `setTimeout` | `listagem-guarnicoes.js` | Swal + recarregar a lista via AJAX |
| Lógica em JSP (`<% … %>`) com SQL | JSPs do GTW clássico | mover para servlet/classe |
| `catch (Exception e) {}` silencioso | espalhado | logar e responder |
| MD5 sem sal para senha | `Usuario.comparaSenha`, `Funcoes.geraMD5` | planejar migração (ver `12-…`) |
| Pooling com 400 conexões e `maxWait 2s` | `Conexao` | evitar consultas longas na thread de requisição |
