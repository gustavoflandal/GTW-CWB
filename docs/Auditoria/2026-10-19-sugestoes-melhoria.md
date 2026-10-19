# Sugestões de Melhoria — GTW-CWB

> **Data:** 2026-10-19
> **Base:** [Relatório de Auditoria de 2026-10-19](2026-10-19-relatorio-auditoria.md). Os IDs entre parênteses remetem aos achados de lá.
> **Restrições respeitadas:** stack de `.setup-gtw` e `pom.xml` imutáveis sem aprovação explícita, nenhuma biblioteca nova, DDL/DML só como script em `docs/banco-de-dados/migracoes/` ([`CLAUDE.md`](../../CLAUDE.md)). Toda sugestão que dependa de mudar dependência aparece como **pedido de aprovação**.

---

## 1. Visão geral

O sistema não precisa de reescrita para sair do estado atual. O caminho é atacar **pontos de estrangulamento** com poucas peças reutilizáveis, que corrigem dezenas de achados de uma vez:

| Peça | Corrige de uma vez | Esforço |
|---|---|---|
| Filtro de sessão *fail-closed* + `Acesso` *fail-closed* | ~25 endpoints sem login, o bypass `?sistema=` e boa parte do risco de SQLi e path traversal sem login | Médio |
| `ServletBase`/`ServletUtil` (sessão, permissão, JSON, erro genérico) | Duplicação em 134 servlets, `e.getMessage()` ao cliente, JSON concatenado | Médio |
| `esc()` global no JS + `<c:out>`/`fn:escapeXml` nas JSPs | XSS em ~110 arquivos JS e em `includes/*.jsp` | Médio |
| Configuração de sessão/cookies/cabeçalhos (`web.xml` + nginx) | `HttpOnly`/`Secure`/`SameSite`, CSP, HSTS, `error-page` | Baixo |
| Pool com `removeAbandoned` + timeouts | Queda por esgotamento do pool | Baixo |
| Suíte TestNG funcionando | 396 testes passam a proteger as correções | Baixo |

---

## 2. Segurança

### 2.1 Controle de acesso centralizado (SEC-AUTH-01/02/03, S2–S4)

1. **`ValidaSessao` fail-closed**:
   - Usar `request.getServletPath()` (já normalizado) e uma **lista exata** de rotas públicas: `/login/login.jsp`, `/login/login_action.jsp`, `/login/mfa_verificar.jsp`, `/login/recuperar-senha…`, recursos estáticos sob prefixos conhecidos.
   - Sem sessão em rota protegida: `401` + JSON para `/MuralhaDigital/*` e requisições AJAX (`X-Requested-With`/`Accept: application/json`); `302` para o login nos demais casos.
   - Escrever um teste unitário com `HttpServletRequest` simulado (Mockito já está no `pom.xml`).
2. **`Acesso.verificaAcesso` fail-closed**:
   - Uma exceção deve negar o acesso.
   - Uma URL não cadastrada em `sis_menu` deve negar o acesso, exceto rotas listadas numa tabela/config explícita de "autenticado sem menu".
   - Antes de ligar, gerar um **inventário de rotas sem cadastro**: cruzar `docs/referencia/servlets-e-endpoints.md` com `sis_menu`, via consulta de leitura. Assim nenhuma tela legítima fica bloqueada.
3. **Eliminar o bypass `?sistema=`** (48 servlets, SEC-SQL-02). Chamadas internas (jobs, relatórios agendados) devem chamar a camada de modelo diretamente, e não o servlet por HTTP. Se o HTTP for inevitável, usar um cabeçalho com segredo de ambiente aceito apenas de `127.0.0.1`.
4. **WebSocket** (SEC-AUTH-13): um `ServerEndpointConfig.Configurator` que copie a `HttpSession` para `userProperties` no handshake. O `idUsuario` passa a vir sempre da sessão.
5. **Regra única de identidade**: nenhum servlet lê `idUsuario` do request para decidir de quem é o dado (SEC-AUTH-39, SEC-AUTH-08). Pode virar um item de checklist de PR.

### 2.2 Senhas, recuperação e MFA (SEC-AUTH-04…07, 15…26, 30)

- **Recuperação de senha**: refazer o fluxo inteiro em `NotificacaoServlet`/`RecuperacaoSenha`:
  1. o usuário informa login ou e-mail, e a resposta é sempre genérica;
  2. o token (UUID v4/SecureRandom, já existe) vai **só** ao contato cadastrado;
  3. o token expira em 30 min e é de uso único;
  4. `atualizarsenha` recebe apenas `token` + `novaSenha`, deriva o usuário do token, aplica `SenhaService.validarComplexidade` e o histórico, e invalida o token.
- **Hash de senha** (S5): `PBKDF2WithHmacSHA256` do próprio JDK (sem dependência nova), com sal de 16 bytes, ≥ 210.000 iterações e formato `pbkdf2$iter$salt$hash` na coluna `senha_hash`, que já existe em `dbo.sis_usuario`.
  - **Migração transparente**: no login, se `senha_hash` estiver vazia, validar pelo MD5 legado e regravar em PBKDF2.
  - Comparar com `MessageDigest.isEqual`.
  - Remover o login por hash (`UsuarioServlet`, SEC-AUTH-17).
- **Bloqueio por tentativas**: corrigir `u.login` → `u.usuario` em `SenhaService` (SEC-AUTH-16) e negar o login se a checagem falhar. Criar um teste que prove o bloqueio após N falhas.
- **Ordem do login**: IP permitido → credencial → bloqueio → MFA → troca obrigatória → criação da sessão → redirecionamento validado. Hoje a sessão nasce antes da checagem de IP (SEC-AUTH-15) e o `retUrl` pula a troca obrigatória (SEC-AUTH-20).
- **Login Google e login do app**: passar pelos mesmos passos (bloqueio, `ativo`, MFA). Parametrizar `REDIRECT_URI` por ambiente (S11).
- **MFA**: até 5 tentativas por sessão pendente; guardar o último *time-step* aceito (antirreuso); exigir o TOTP atual para desativar.
- **Limite da senha nova**: de 10 para ≥ 64 caracteres (SEC-AUTH-30).

### 2.3 Sessão, cookies, CSRF e cabeçalhos (SEC-AUTH-31…33, FE-10, CFG-26, LOG-04)

- `web.xml`:
  ```xml
  <session-config>
      <session-timeout>240</session-timeout>
      <cookie-config><http-only>true</http-only><secure>true</secure></cookie-config>
      <tracking-mode>COOKIE</tracking-mode>
  </session-config>
  <error-page><error-code>500</error-code><location>/includes/erro_generico.jsp</location></error-page>
  <error-page><exception-type>java.lang.Throwable</exception-type><location>/includes/erro_generico.jsp</location></error-page>
  ```
  `/includes/erro_generico.jsp` é uma página **nova, a criar**, sem parâmetros refletidos.
- Remover os cookies `JSESSIONID`/`JSESSIONID_HTTP` criados à mão e a variável `jsessionid` dos cabeçalhos JSP.
- **CSRF**:
  - um filtro gera o token por sessão;
  - o cabeçalho JSP o expõe numa `<meta name="csrf-token">`;
  - um `$.ajaxSetup`/wrapper de `fetch` em `utils/utilidades.js` o envia em `X-CSRF-Token`;
  - o filtro valida POST/PUT/DELETE;
  - `SameSite=Lax` via `Rfc6265CookieProcessor` no `context.xml` (configuração do Tomcat, não muda versão).
- **Escrita por GET**: proibir (ex.: `ConfigurarEquipamento`, `desbloquear`, `PontoInteresse`).
- **Cabeçalhos no nginx**: `Strict-Transport-Security`, `X-Content-Type-Options: nosniff`, `X-Frame-Options: SAMEORIGIN` (ou `frame-ancestors`), `Referrer-Policy`, `server_tokens off`. CSP primeiro em modo `Report-Only`, por causa dos scripts inline do legado.
- **Erros**: mensagem genérica + id de correlação (UUID registrado no log com a *stack*). No `server.xml`, `ErrorReportValve showReport=false showServerInfo=false`.

### 2.4 Arquivos, comandos e XML (SEC-AUTH-09, 14, 36, 37; CFG-28/29)

- **Utilitário único `ArquivoSeguro`**, em `muralha/digital/util/` (classe nova):
  - `resolverDentroDe(base, nomeRelativo)`: `getCanonicalPath` + `startsWith(base)`;
  - `nomeSeguro(extensaoPermitida)`: UUID + extensão de lista branca;
  - `validarMagicBytes`.
- **Download por id, nunca por caminho**: vídeos, logs e documentos são resolvidos a partir do id no banco.
- **ffmpeg**: `ProcessBuilder(List<String>)`, `-protocol_whitelist file`, entradas validadas contra a pasta de vídeos, temporário apagado em `finally`, resposta em streaming com suporte a `Range` (PERF-01).
- **XML**: uma `XmlSeguro.novoDocumentBuilder()` com `disallow-doctype-decl`, `FEATURE_SECURE_PROCESSING` e entidades externas desligadas, usada por `SerializadorXML`, `AjaxXMLLeitor` e qualquer novo parser (modelo: `HistoricoParametros.java:74`).
- **Uploads**: limite de tamanho por endpoint e `maxPartCount` no Connector do Tomcat (mitiga CVE-2023-24998 sem mudar a versão).

### 2.5 Segredos (CFG-01…06, SEC-AUTH-11)

1. **Rotacionar fora do código**: keystore de assinatura, Facilita Móvel, câmeras, FTP, Google Maps, Axis admin. Trate todos como comprometidos.
2. **Remover do HEAD**:
   - ler tudo de variáveis de ambiente ou do `confGTW.xml` local, conforme [`07-configuracao-e-segredos.md`](../requisitos/07-configuracao-e-segredos.md);
   - `AssinaturaConfig` deve **falhar** sem `SIGN_KS_PATH`/`SIGN_KS_PASSWORD`;
   - câmeras via proxy autenticado no servidor (o navegador nunca recebe usuário e senha).
3. **Limpar o histórico** (git filter-repo/BFG), com aprovação do dono do repositório e aviso aos clones.
4. **Ampliar o `.gitignore`**: `*.p12 *.jks *.pfx *.pem *.key *.bak`.
5. **Varredura preventiva**: o secret scanning do GitHub já bloqueia o push. Complementar com uma verificação local antes de commitar (`git grep -nE "AIza|password=|senha="`), documentada no guia [`10-guia-de-implementacao.md`](../requisitos/10-guia-de-implementacao.md).

### 2.6 Infraestrutura (CFG-07, 08, 15…27)

- **nginx**:
  - substituir `proxy_pass http://$http_self_targetip` e `$camera_ip:$camera_port` por um `map` fechado de câmeras conhecidas;
  - `auth_request /MuralhaDigital/AuthCheck` (endpoint leve, a criar, que só confere a sessão);
  - `deny all` para `/services/AdminService`, `/servlet/AdminServlet`, `/servlet/AxisServlet`, `*.jws`;
  - TLS na porta pública;
  - `proxy_read_timeout 86400` só nas `location` de WebSocket;
  - `limit_req` no login.
- **Axis**: remover o `AdminService` do `server-config.wsdd` e o `AdminServlet` do `web.xml`; restringir `/services/*` por `RemoteAddrFilter` aos IPs dos equipamentos.
- **Superset**:
  - porta DEV só em `127.0.0.1`;
  - produção com TLS e sem a porta 8088;
  - segredo de guest token separado do `SECRET_KEY`;
  - HMAC nos cabeçalhos `X-GTW-*`, usando o `GTW_SSO_HMAC_SECRET` que já está documentado;
  - Talisman ligado;
  - login SQL somente-leitura, por script de migração.

---

## 3. Dados e banco

### 3.1 SQL (SEC-SQL-01…09)

- **Regra sem exceção**: valor do usuário entra sempre como `?`.
  - Listas `IN`: um utilitário `SqlUtil.placeholders(n)` + `setInt` em laço.
  - Identificadores dinâmicos: só de lista branca (o `MontadorSql` já é o modelo).
- **Procedures com `EXEC(@sql)`** (23 versionadas): reescrever com `sp_executesql` parametrizado, como scripts em `docs/banco-de-dados/migracoes/`. Até lá, validar as entradas no Java (regex numérica para listas, tamanho máximo para texto).
- **Dashboard antigo** (`muralha/digital/dashboard`): aposentar em favor de `dashboards/`, ou restringir `nomeView` ao catálogo.
- **Conta somente-leitura** para o Dashboard e o Superset, com `GRANT SELECT` só nas tabelas do catálogo e um pool separado (sem dependência nova: mais um `BasicDataSource`).

### 3.2 Pool e recursos (DADOS-01…06, CONF-01)

Configuração sugerida em `Conexao.java` (mesma versão do commons-dbcp 1.4):

```java
ds.setMaxActive(150);              // dimensionar com o DBA; 400 é excessivo
ds.setMaxIdle(50);
ds.setMinIdle(10);
ds.setMaxWait(5000);
ds.setRemoveAbandoned(true);
ds.setRemoveAbandonedTimeout(300); // segundos
ds.setLogAbandoned(true);
ds.addConnectionProperty("socketTimeout", "120000");
ds.addConnectionProperty("queryTimeout", "120");
```

- Singleton do pool com *holder* estático ou `synchronized`.
- `getStackTrace()` só dentro de `if (logger.isTraceEnabled())`.
- **Migração gradual para try-with-resources**: cada arquivo tocado sai convertido. Corrigir já os vazamentos comprovados: `Usuario.alteraUsuario`, `VeiculoMonitorado.getRemoveStatement`, `CamerasMonitoramentoAoVivo`, `ExportaAmostras`, `ExportaComprovacaoImagem`.
- **Transações** nas operações com várias escritas (`PontosInteresse`, `ConfiguracaoEquipamentos`, `PainelService.restaurarVersao`), seguindo o §2 de [`06-padroes-de-codigo.md`](../requisitos/06-padroes-de-codigo.md).

### 3.3 Desempenho em tabelas grandes (CONF-09, PERF-02/03, DADOS-08…10, R3)

- **Retenção real**: expurgo em lotes (`DELETE TOP (5000) … WHERE data < @corte` em laço, com `WAITFOR DELAY` curto) ou movimentação para tabela de arquivo. Requer script aprovado e decisão do dono dos dados. O `expurgo_log` deve registrar o que **de fato** foi removido. Enquanto não houver expurgo, o job deve registrar `SOMENTE_CONTAGEM` e não `SUCESSO`.
- Trocar `CAST(data AS DATE) = ?` por intervalos semiabertos (52 ocorrências).
- **Contagens** de `veiculo_tempo_real*`: `sys.dm_db_partition_stats` ou snapshot gerado por job.
- **Teto de paginação** (`Paginacao`: 500) e de `TOP` vindo do cliente.
- **N+1** em `AlertasOcorrencias`: `OUTER APPLY` ou busca em lote.
- Avaliar **particionamento por data** de `veiculo_tempo_real` e `_imagem` com o DBA. É decisão de banco, fora do código.

---

## 4. Confiabilidade e concorrência

- **Servlets sem estado**: transformar os campos de instância de `Processar`, `ExportarImagensServlet`, `ExportarDescargaServlet`, `CadastrarFiltroServlet`, `DesabilitaFiltroServlet` e `ReprovarRemessa` em variáveis locais (CONF-03). Regra de revisão: **servlet não tem campo mutável**.
- **Estado global**: tirar de `Inicializacao` o que é por usuário (grupo de câmeras em exibição). Coleções compartilhadas: `CopyOnWriteArrayList` ou referência `volatile` para lista imutável (CONF-02, M9).
- **Ciclo de vida**: um `ServletContextListener` (`muralha.digital._ini.Desligamento`, novo) que, no `contextDestroyed`:
  - para as threads de polling (flags `volatile` + `interrupt`);
  - faz `shutdown`/`awaitTermination` dos executores (`AuditoriaService`, `LiberarMovimentosLote`, `ProcessamentoMultiplas`, `GenericCache`);
  - desliga o Quartz (CONF-06).

  Corrigir também a declaração dupla de `Inicializacao` no `web.xml` (MAN-07).
- **Polling sob demanda**: consultar o banco só se `ClienteSessoes.ObterQtdeSessoesPorTipo(tipo) > 0` (modelo: `Blitzes.iniciarTimer`). Subir a blitz eletrônica de 100 ms para ≥ 1 s (CONF-07). Cachear o `JAXBContext` (PERF-04).
- **WebSocket**: envio com `getAsyncRemote()` e timeout; remoção com `removeIf` (CONF-05).
- **Quartz**:
  - `threadCount=10`;
  - `@DisallowConcurrentExecution` em todos os jobs;
  - `logger.error` em vez de `System.err`;
  - avaliar o `JobStoreTX` (as tabelas `qrtz_*` já existem) se houver mais de uma instância (CONF-08, R5).
- **HTTP de saída**: um `HttpUtil` com connect de 5 s e read de 20 s para `HttpURLConnection`; `Unirest.setTimeouts(5000, 20000)` na inicialização. Tirar o geocoding de dentro do `synchronized` (CFG-33).
- **`AuditoriaService`**: fila limitada (`ThreadPoolExecutor` com `ArrayBlockingQueue` + `CallerRunsPolicy`), log4j e IP do `X-Real-IP` apenas quando `remoteAddr` for o nginx (CONF-15).

---

## 5. Front-end

- **`esc()` global**: em `muralha-digital/utils/utilidades.js`, a partir de `Chamados.esc` (escapa `& < > " '`). Usar em toda interpolação de dados em HTML. Para handlers, trocar `onclick="f('${x}')"` por `data-*` + `addEventListener`. Prioridade: texto livre (observação, descrição, nome, nome de arquivo).
- **JSP**:
  - `<c:out>`/`fn:escapeXml` em todo dado de request ou banco;
  - em contexto JS, serializar com Gson em `data-*`;
  - corrigir primeiro `includes/erro*.jsp`, `sucesso*.jsp`, `confirma.jsp` e `sim_nao.jsp` (FE-01/02).
- **Bibliotecas** (sem dependência nova; só alinhar ao que já está no projeto):
  - **jQuery**: uma versão ≥ 3.5 (a 3.7.1 já existe) e remover a 1.7.2 do legado à medida que as telas forem tocadas; `jquery-migrate` não é necessário nas telas novas.
  - **Bootstrap**: 5.3.x pelo cabeçalho, sem recarregar por página.
  - Fixar versões de Chart.js e SweetAlert2 e adicionar SRI; remover o moment duplicado.
- **Padrão visual**: trocar `alert()`/`confirm()` por SweetAlert2. Criar `Ui.alerta(msg)`/`Ui.confirmar(msg)` em `utilidades.js` para facilitar. Incluir o cabeçalho padrão e `Acesso` em `parametros.jsp`, `video-ao-vivo.jsp`, `detalhes-fato.jsp` e `base-template.jsp`.
- **Encoding**: converter os 48 arquivos para UTF-8 (`iconv -f ISO-8859-1 -t UTF-8`), declarar `pageEncoding="UTF-8"`, `lang="pt-BR"`, e conferir visualmente os acentos.
- **Acessibilidade**: `alt` nas imagens, `for` nos `label`, `aria-label` nos botões só com ícone.

---

## 6. Qualidade, testes e processo

- **Fazer a suíte rodar** (TST-01):
  - `testng-customsuite.xml` com `<packages><package name="muralha.digital.*"/></packages>`;
  - excluir o grupo `completo`/integração (HtmlUnit, que exige servidor);
  - documentar `..\.setup-gtw\build.ps1` e um comando de teste em [`01-stack-e-ambiente.md`](../requisitos/01-stack-e-ambiente.md).

  Alterar o `pom.xml` (surefire) **exige aprovação**. A troca do arquivo de suíte não exige.
- **Testes de regressão de segurança** (TST-02), a escrever junto com cada correção: `ValidaSessao` (sem sessão → 401), `Acesso` (exceção → nega), recuperação de senha (sem token → nega; token expirado → nega), `SenhaService` (bloqueio após N falhas), PBKDF2 (re-hash), `ArquivoSeguro` (`..` → rejeita), `XmlSeguro` (DOCTYPE → rejeita), `RetencaoJob`.
- **CI**: não existe `.github/`. Propor (com aprovação) um workflow que compile e rode os testes unitários. A ressalva é que as dependências `com.consilux.gtw:*` vêm do repositório interno; pode ser preciso um runner auto-hospedado na rede da Consilux.
- **Checklist de PR**, para acrescentar a [`10-guia-de-implementacao.md`](../requisitos/10-guia-de-implementacao.md) na Definição de Pronto:
  1. sessão e `Acesso` no servlet;
  2. `idUsuario` só da sessão;
  3. SQL com `?`;
  4. try-with-resources;
  5. sem campo mutável em servlet;
  6. sem `e.getMessage()` ao cliente;
  7. `esc()`/`<c:out>`;
  8. SweetAlert2;
  9. UTF-8;
  10. teste novo.
- **Logging**:
  - root do log4j em INFO;
  - nunca registrar senha, hash, token, código MFA ou CPF completo;
  - `logger` em vez de `System.out`/`printStackTrace`;
  - rotação compatível com a trilha de auditoria (LOG-01/02/05/06).
- **Código morto**: remover `/teste`, as 33 entradas do `web.xml` sem classe, `*-original.*`, `*.bak`, `* - Copia.*` e o `.sql` sob `assets/` (MAN-03, SEC-AUTH-35).
- **Refatoração oportunista**: ao tocar em classes gigantes (`Blitzes`, `BlitzServlet`, `RegistroDeFatos`…), extrair serviços coesos. Não fazer refatoração em massa sem cobertura de testes.

---

## 7. Dependências — pedidos de aprovação (DEP-*)

Itens que exigem aprovação explícita, por alterarem o `pom.xml`. Todos são *patch*/*minor* da mesma linha, salvo indicação:

| Dependência | De → Para | Motivo |
|---|---|---|
| `commons-fileupload` | 1.4 → 1.5 | CVE-2023-24998 (DoS) |
| `jackson-databind` | 2.12.5 → 2.12.7.1 | CVE-2020-36518, CVE-2022-42003/42004 |
| `gson` | 2.8.8 → 2.8.9 | CVE-2022-25647 |
| `httpclient` | 4.5.10 → 4.5.13+ | CVE-2020-13956 |
| `commons-io` / `commons-net` / `commons-lang3` | patches | CVE-2021-29425, CVE-2021-37533, CVE-2024-47554, CVE-2025-48924 |
| `commons-configuration2` | 2.6 → 2.8+ | CVE-2020-1953, CVE-2022-33980 |
| `log4j` 1.2.17 | → `ch.qos.reload4j:reload4j` 1.2.25 | Substituto direto, corrige as CVEs do 1.x |
| `derby` | remover | Sem uso no código |
| `itext` | deduplicar | Duas versões do mesmo GA |
| `mockito-all` | remover (manter `mockito-core`) | Conflito no classpath |
| Repositório Maven interno | HTTP → HTTPS | MITM na cadeia de build |
| `axis` 1.4 / `gwt` 2.1 | plano de saída (longo prazo) | EOL; mitigar por rede enquanto isso |

---

## 8. Roteiro sugerido em fases

| Fase | Conteúdo | Saída |
|---|---|---|
| **0 — Contenção** (fora do código, imediato) | Rotação de todas as credenciais; regras de bloqueio no nginx (RPC2, câmeras, Axis admin); Superset DEV em `127.0.0.1` | Riscos externos fechados |
| **1 — Acesso** | Recuperação de senha; `ValidaSessao` e `Acesso` fail-closed (com inventário de rotas); remover bypass `sistema`; WebSocket com sessão; servlets da lista §2 do relatório | Nenhum endpoint de dados sem login |
| **2 — Injeção e arquivos** | SEC-SQL-01…07; `ArquivoSeguro`; ffmpeg; XXE; uploads | Sem SQLi/traversal/RCE conhecidos |
| **3 — Credenciais e sessão** | PBKDF2 + migração; bloqueio por tentativas; ordem do login; MFA; cookies; CSRF; cabeçalhos; `error-page` | S5/S6 fechados |
| **4 — Estabilidade** | Pool; vazamentos; servlets sem estado; desligamento; polling; Quartz; timeouts; retenção real | R1, R2, R3, R5, R7 fechados |
| **5 — Front-end** | `esc()`; JSPs `includes/*`; jQuery/Bootstrap; SweetAlert2; encoding | S10, M2, M6 fechados |
| **6 — Qualidade contínua** | Suíte de testes; CI; checklist de PR; limpeza de código morto; pedidos de dependências | Regressões detectadas automaticamente |

Cada fase deve virar um plano em `docs/planos-salvador/` e uma entrega em `docs/entregas/` com a seção **"## Origem"** ([`CLAUDE.md`](../../CLAUDE.md)). Ao final de cada fase, atualizar o status dos itens S/R/M em [`12-riscos-e-debitos-tecnicos.md`](../requisitos/12-riscos-e-debitos-tecnicos.md).

---

## 9. Indicadores de acompanhamento

| Indicador | Hoje | Meta |
|---|---|---|
| Endpoints de dados acessíveis sem sessão | ~25 + 48 com bypass | 0 |
| Segredos no HEAD | ≥ 7 | 0 |
| SQL concatenado com entrada do usuário | ≥ 7 pontos + 23 procedures | 0 |
| `getConexao()` com try-with-resources | 12% | ≥ 50% (crescente) |
| Testes executados no build | 0 de 396 | 100% dos unitários |
| Arquivos JS com inserção de HTML sem escape | 110 | 0 nos módulos Muralha |
| Versões de jQuery / Bootstrap carregadas | 10 / 7 | 1–2 / 1 |
| Arquivos não UTF-8 em `muralha-digital` | 48 | 0 |
| `alert()`/`confirm()` nativos | ~250 | 0 nas telas Muralha |
