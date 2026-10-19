# Relatório de Auditoria de Código — GTW-CWB

> **Data:** 2026-10-19
> **Base auditada:** branch `main` em `c4ebedc` (Merge PR #50)
> **Tipo:** auditoria estática (leitura de código, configuração, scripts SQL versionados e documentação). Nada foi executado contra o banco nem contra a aplicação em execução, e nenhum código foi alterado.
> **Documentos relacionados:** [Sugestões de melhoria](2026-10-19-sugestoes-melhoria.md) · [Prompt para code review e correção](2026-10-19-prompt-code-review-correcoes.md) · auditoria anterior: [`12-riscos-e-debitos-tecnicos.md`](../requisitos/12-riscos-e-debitos-tecnicos.md)

---

## 1. Sumário executivo

O código novo da Muralha Digital (`muralha.digital.*`) segue, em boa parte, os padrões de [`06-padroes-de-codigo.md`](../requisitos/06-padroes-de-codigo.md): SQL parametrizado, try-with-resources nos módulos recentes e um motor de consulta do Dashboard Builder bem blindado. Mesmo assim, a auditoria encontrou **falhas críticas exploráveis sem login**. Elas se somam porque o filtro global de sessão continua *fail-open*.

**Os cinco riscos mais graves:**

1. **Tomada de qualquer conta sem login.**
   - `NotificacaoServlet` (`acao=atualizarsenha`) troca a senha de qualquer `idUsuario` recebido por parâmetro, sem token e sem sessão (SEC-AUTH-04).
   - O fluxo de recuperação envia o token de qualquer usuário a um destinatário que o próprio atacante escolhe (SEC-AUTH-05).
2. **O filtro `ValidaSessao` não bloqueia requisições sem sessão** (SEC-AUTH-01). Por isso dezenas de servlets e JSPs ficam abertos, entre eles:
   - relatório com SQL injection (SEC-SQL-01);
   - leitura de qualquer arquivo do servidor e injeção de argumentos no ffmpeg (SEC-AUTH-09);
   - assinatura de imagens com a chave institucional (SEC-AUTH-10);
   - download de infrações (SEC-AUTH-12).
3. **SQL injection sem autenticação.**
   - `RelatorioSessao`, com `Statement` concatenado (SEC-SQL-01).
   - `RelatorioInfracoesCAV`, por meio do *bypass* `?sistema=`, que existe em 48 servlets (SEC-SQL-02).
4. **Segredos e chaves no repositório.**
   - Keystore PKCS12 com chave privada de assinatura e senha padrão no código (CFG-01).
   - Credenciais de SMS (CFG-03), de câmeras (CFG-04), de FTP (SEC-AUTH-11), chave do Google Maps (CFG-05) e senha do Axis admin (CFG-06).
5. **Proxy nginx aberto (SSRF).** O destino é escolhido pelo cliente via cabeçalho ou URL (CFG-07, CFG-25). Somado ao AdminService do Axis 1.4 publicado atrás do proxy (CFG-08), o risco é de comprometimento do servidor.

**Outros problemas relevantes:**
- O **job de retenção LGPD não apaga nada** e registra "SUCESSO" (CONF-09).
- O **bloqueio por tentativas de login não funciona**, porque a consulta usa uma coluna inexistente, `u.login` (SEC-AUTH-16).
- **Os 396 testes automatizados nunca são executados** (TST-01).
- Há **vazamento de conexão** a cada troca de grupo de câmeras e a cada troca de senha, num pool sem `removeAbandoned` (CONF-01, DADOS-01, DADOS-06).

### 1.1 Achados por severidade

| Severidade | Qtde. | Exemplos |
|---|---|---|
| **Crítica** | 16 | SEC-AUTH-01, 04, 05, 08, 09, 11, 13 · SEC-SQL-01, 02 · CFG-01, 04, 07, 08 · CONF-09 · FE-01 · SEC-AUTH-36 |
| **Alta** | 53 | Bloqueio de login quebrado, pass-the-hash, XXE, SQLi autenticado, servlets com estado compartilhado, CVEs de jQuery/Axis/log4j, testes que não rodam |
| **Média** | 66 | CSRF, cookies sem `HttpOnly`, XSS armazenado no JS, pool, N+1, `alert()` nativo, encoding |
| **Baixa** | 28 | Código morto, TODOs, acessibilidade, SRI |

> Contagem por linha das tabelas das seções 2 a 7 (163 linhas, mais 1 informativa). Duplicatas entre frentes foram unificadas (por exemplo, SEC-AUTH-09 = CFG-30/31; CFG-04 = FE-17/18). Linhas agrupadas (ex.: DEP-04…09, FE-34/35) contam uma vez.

### 1.2 Métricas do código

| Métrica | Valor |
|---|---|
| Arquivos `.java` / linhas | 1.120 / 240.602 (590 em `muralha.digital`, 109.824 linhas) |
| JSPs / linhas | 487 / 74.233 |
| Servlets (`web.xml` / `@WebServlet`) | 195 / 159 (33 entradas do `web.xml` apontam para classes inexistentes) |
| Chamadas `Conexao.getConexao()` / com try-with-resources | ~1.136 / ~135 (12%). O restante usa `finally`, salvo os vazamentos listados |
| `catch (Exception …)` / `catch` vazio | 2.124 / 220 |
| `printStackTrace()` / `System.out\|err` | 144 / 76 |
| `e.getMessage()` devolvido ao cliente | ~40 a 145, conforme o critério (respostas JSON + `sendError`) |
| TODO/FIXME (Java / webapp Muralha) | 538 / 521 |
| Testes | 58 classes, 396 `@Test` (TestNG), **nenhum executado** pelo build |
| JS próprio com `innerHTML`/`.html()` sem escape | 110 de 137 arquivos |
| Versões distintas de jQuery / Bootstrap carregadas | 10 / 7 |
| Arquivos não UTF-8 em `webapp/muralha-digital` | 48 |
| Jobs Quartz | 31, com 4 threads, `RAMJobStore` |

### 1.3 Método e limitações

- Cinco frentes paralelas: autenticação e autorização; SQL e acesso a dados; front-end e XSS; segredos, configuração, infraestrutura e dependências; confiabilidade e qualidade. Os achados críticos foram conferidos de novo, manualmente, no código (ex.: `NotificacaoServlet.java:174-183`, `SenhaService.java:145,228` contra o catálogo `docs/banco-de-dados/tabelas/dbo/sis.md`, `RetencaoJob.java:36-51`, `ValidaSessao.java:45-47`).
- **Não foi possível rodar o Maven**: as dependências `com.consilux.gtw:*` vêm de um repositório interno que não é acessível do ambiente da auditoria. Também não houve teste dinâmico (DAST) nem acesso ao banco.
- As procedures foram analisadas pelos scripts versionados em `docs/banco-de-dados/codigo-sql/`. A versão em produção pode ser diferente.
- O clone local tem histórico parcial, então a varredura de segredos no histórico é parcial.
- **Segredos aparecem sempre mascarados** neste documento.

---

## 2. Achados — Autenticação, autorização e sessão

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| SEC-AUTH-01 | **Crítica** | `com/consilux/infra/ValidaSessao.java:45-47,86` | Com `session == null`, o filtro chama `chain.doFilter`. Requisição sem cookie chega a qualquer servlet/JSP. | Fail-closed: 401 para `/MuralhaDigital/*` e AJAX, redirecionamento ao login nos demais. |
| SEC-AUTH-02 | Média | `ValidaSessao.java:33-41` | Lista pública com `contains("/login/")`, `contains("assets/")` sobre a URI bruta (aceita `;param`). Todo `/login/*` é público. | `getServletPath()` + `startsWith` sobre uma lista exata. |
| SEC-AUTH-03 | Alta | `com/consilux/model/Acesso.java:143-145,161` | `catch` vazio e `cnt == 0` liberam o acesso: erro de banco ou URL não cadastrada dão acesso (agrava o S4). | Negar em caso de exceção e de URL não cadastrada, com lista explícita de exceções. |
| SEC-AUTH-04 | **Crítica** | `muralha/digital/notificacao/NotificacaoServlet.java:174-183` | `acao=atualizarsenha` usa `idUsuario` e `novaSenha` vindos do cliente, sem token, sessão nem política de complexidade. | Exigir token de recuperação válido, derivar o usuário do token, consumir o token, aplicar `SenhaService.validarComplexidade`. |
| SEC-AUTH-05 | **Crítica** | `NotificacaoServlet.java:91-98,124-133` | O token de recuperação de qualquer `idUsuario` é enviado ao `destinatario` informado pelo cliente (e-mail ou SMS). | Enviar só ao contato cadastrado e não aceitar `idUsuario`/`destinatario` do cliente. |
| SEC-AUTH-06 | Alta | `notificacao/RecuperacaoSenha.java:77` | Token de recuperação sem expiração. | Filtro por `data_criacao` (ex.: 30 min) e um único token ativo. |
| SEC-AUTH-07 | Média | `NotificacaoServlet.java:148`, `RecuperacaoSenha.java:25-31` | `buscarusuario` devolve id, e-mail e telefone sem login (enumeração + dados pessoais). | Resposta genérica. |
| SEC-AUTH-08 | **Crítica** | `acessos/UsuarioServlet.java:113,1207-1218`, `acessos/Usuario.java:285-307` | `acessoViaToken` gera token de acesso à Mobilidade Urbana para qualquer `idUsuario`, sem login e com data arbitrária. | Sessão obrigatória, id da sessão, data gerada no servidor. |
| SEC-AUTH-09 | **Crítica** | `monitoramento/VerVideoMonitoramentoServlet.java:55,77-112,205-231`, `model/AcessoDiretorio.java:117-122` | Sem login: `enderecoVideo` vai direto para `FileUtils` (leitura de qualquer arquivo) e é concatenado no comando do ffmpeg executado por `Runtime.exec` (injeção de argumentos, leitura e gravação de arquivos, SSRF). Também carrega o vídeo inteiro em memória (PERF-01). | Receber id do vídeo e resolver o caminho no banco; `canonicalPath` dentro da base; `ProcessBuilder(List)`; `-protocol_whitelist file`; sessão + `Acesso`. |
| SEC-AUTH-10 | Alta | `assinatura/AssinaturaImagemServlet.java:48-59` | `/assinar` sem login assina JPEG com a chave institucional. | Sessão + permissão em `/assinar`; `/verificar` pode continuar público. |
| SEC-AUTH-11 | **Crítica** | `com/consilux/servlet/ajax/ArquivoIntegracao.java:40-53` | Credencial FTP literal no código. O servlet faz proxy FTP de qualquer caminho, sem login. | Revogar a credencial, ler de variável de ambiente, sessão, lista branca de caminhos. |
| SEC-AUTH-12 | Alta | `ferramentas/DownloadInfracao.java:91-104` | ZIP de imagens, placa e vídeos por `id_infracao` sequencial, sem login. | Sessão + `Acesso` + escopo por grupo de equipamento. |
| SEC-AUTH-13 | **Crítica** | `muralha/digital/websocket/ControleAcessoEnvio.java:24-29,99-102` | O WebSocket `/ClientesWebSocket` não valida a sessão no handshake. O cliente declara o próprio `idUsuario` e recebe alertas de qualquer usuário. | `Configurator.modifyHandshake` copiando a `HttpSession`; rejeitar conexão sem usuário. |
| SEC-AUTH-14 | Média | `muralha/digital/consulta/BaixaLogFtp.java:27-41` | Sem login; `idEquip` concatenado no caminho (`..\`), o que permite gerar ZIP de qualquer diretório. | Sessão, `idEquip` numérico, `canonicalPath`. |
| SEC-AUTH-15 | Alta | `webapp/login/login_action.jsp:145-151`, `mfa_verificar.jsp:56-62`, `com/consilux/infra/LogonLogoff.java:110,315-332` | O usuário já está na sessão antes da checagem de IP. O IP verificado vem de parâmetro do cliente. `ip_permitidos.txt` vazio ou ausente libera tudo. | Checar `getRemoteAddr()` (ou `X-Real-IP` de proxy confiável) antes de criar a sessão; fail-closed. |
| SEC-AUTH-16 | Alta | `muralha/digital/acesso/SenhaService.java:145,171,177,228` | `WHERE u.login=?`: a coluna não existe em `dbo.sis_usuario` (o catálogo tem `usuario`). A exceção é engolida em `login_action.jsp:69,72`, então **o bloqueio por tentativas não funciona**. | Trocar por `u.usuario`; em caso de falha, negar o login. Criar teste. |
| SEC-AUTH-17 | Alta | `acessos/UsuarioServlet.java:79-90,190,218`, `acessos/Usuario.java:161-168` | Login de app aceita o MD5 da senha (*pass-the-hash*), devolve o hash na resposta, grava em log e usa `Access-Control-Allow-Origin: *`. Sem MFA nem bloqueio. | Receber a senha e usar o mesmo fluxo de `login_action`; remover eco, log e CORS `*`. |
| SEC-AUTH-18 | Alta | `UsuarioServlet.java:50-66,943-962` | GET `desbloquear` e `obterListaUsuariosAtivos` sem login. | Sessão + permissão de administrador + POST. |
| SEC-AUTH-19 | Alta | `com/consilux/model/Usuario.java:1264-1269`, `notificacao/ServicoEmailMuralha.java:284-285` | Senha em MD5 sem sal, com comparação não constante (S5). | PBKDF2WithHmacSHA256 (JDK) com sal, re-hash no login, `MessageDigest.isEqual`. |
| SEC-AUTH-20 | Média | `login_action.jsp:176-186` | Com `retUrl` preenchido, a troca obrigatória de senha é pulada. | Avaliar a troca obrigatória antes de redirecionar. |
| SEC-AUTH-21 | Média | `login_action.jsp:176-197`, `mfa_verificar.jsp:69-72` | *Open redirect* pós-login via `retUrl`. | Aceitar só caminho relativo (`/…`, sem `//`). |
| SEC-AUTH-22 | Alta | `webapp/login/login_action_app.jsp:28,55-58,94` | `System.out.println` de `secretKey`, `nonce`, `tag` e MD5 da senha; `urlService` refletido em JS (XSS + open redirect). | Remover os prints; lista branca e escape de `urlService`. |
| SEC-AUTH-23 | Média | `acesso/TotpService.java:31`, `login/mfa_verificar.jsp:25-29` | TOTP sem limite de tentativas nem proteção contra reutilização; segredo em texto puro. | Limitar tentativas e guardar o último *step*. |
| SEC-AUTH-24 | Média | `acesso/MfaConfigServlet.java:79-91` | MFA desativado sem reautenticação e sem CSRF. | Exigir o TOTP atual ou a senha. |
| SEC-AUTH-25 | Média | `google/LoginGoogle.java:239-259`, `UsuarioServlet.java:130-160` | O login Google ignora MFA, bloqueio e `ativo`. | Mesmas checagens do login normal. |
| SEC-AUTH-26 | Média | `LoginGoogle.java:160-212` | Autocadastro anônimo a partir de JSON com `googleId`/`email` não verificados, com CORS `*`. | Usar só o `userinfo` validado no callback. |
| SEC-AUTH-27 | Média | `analise/GuestTokenServlet.java:42,93-94` | Guest token para qualquer dashboard do Superset (IDOR). | Lista de dashboards por grupo. |
| SEC-AUTH-28 | Alta | `ui/server/UsuarioServiceImpl.java:77-97` | Qualquer usuário logado cria ou altera usuários e grupos via GWT-RPC (escalada de privilégio). | `Acesso` à funcionalidade de cadastro. |
| SEC-AUTH-29 | Média | `servlet/gwt/GwtBaseServlet.java:92-95` | `checkPermutationStrongName()` sobrescrito vazio, o que desliga a defesa anti-XSRF do GWT. | Remover o override. |
| SEC-AUTH-30 | Baixa | `login/login_change_action.jsp:29,33` | Nova senha limitada a 10 caracteres. | Limite de ≥ 64. |
| SEC-AUTH-31 | Média | Projeto todo | Não há token CSRF (o comentário em `static/js/dashboards/api.js:3` não corresponde à realidade); sem `SameSite`. | Filtro CSRF (token por sessão + cabeçalho) e `SameSite=Lax`. |
| SEC-AUTH-32 | Média | `login_action.jsp:131-143`, `mfa_verificar.jsp:44-54`, `cabecalho_bootstrap_simples.jsp:90` | Cookies `JSESSIONID`/`JSESSIONID_HTTP` criados à mão sem `HttpOnly`/`Secure`. O ID de sessão é copiado para uma variável JS em todas as telas (FE-10). | `<cookie-config>` no `web.xml`; remover os cookies manuais e a variável JS. |
| SEC-AUTH-33 | Média | `web.xml:14`, `login_action.jsp:124-126` | Sessão de 240 min; `LoginNuncaBloqueia` → sessão sem expiração. | Teto absoluto (ex.: 8 h). |
| SEC-AUTH-34 | Alta | `web.xml:340-368`, `WEB-INF/server-config.wsdd` | `/services/*`, `*.jws` e `AdminServlet` expostos. `CSXEventsWS`, `StatusEquipamento` e `VeiculoMonitoradoWS` não têm autenticação. `ConfigEquipWS.validarUsuario` funciona como oráculo de senha (S7). | Restringir por IP (`RemoteAddrFilter`), remover `AdminServlet` e `*.jws`. |
| SEC-AUTH-35 | Baixa | `web.xml:99,388-395` | Mapeamentos mortos (`/teste`, Jersey sem *resource*, 33 classes inexistentes). | Remover. |
| SEC-AUTH-36 | **Crítica** | `registroDeFato/RegistroDeFatoServlet.java:1667-1668`, `boletim/BoletimServlet.java:500-501` | Nome do arquivo vindo do JSON concatenado no caminho de gravação: escrita em qualquer pasta (ex.: JSP no webroot, ou seja, RCE). Exige login. | Nome UUID, lista branca de extensões, `canonicalPath`, limite de tamanho. |
| SEC-AUTH-37 | Média | `atendimento/AtendimentoAnexoServlet.java:135-152` | Extensão de anexo sem lista branca. | Lista branca + *magic bytes*. |
| SEC-AUTH-38 | Alta | `registroDeFato/RegistroDeFatoDocumentoServlet.java:88,128`, `boletim/BoletimDocumentoServlet.java` | Qualquer usuário logado baixa documento de qualquer registro (IDOR). | Filtrar por `registro_fato_usuario_grupo`. |
| SEC-AUTH-39 | Alta | `guarnicao/GuarnicaoServlet.java:182,216` | `idUsuario` lido do cliente, em vez da sessão (contra o doc 09 §4). | Usar o id da sessão. |

**Servlets sem verificação de sessão (lista para correção em lote):**

- **Muralha:**
  - `GuarnicaoServlet`, `NotificacaoServlet`, `PerfilComportamentalServlet`, `PesquisaRapidaAutocompleteServlet`, `AssinaturaImagemServlet`
  - `RelatorioAuditoriaServlet`, `RelatorioAcoesAlarmesServlet`, `RelatorioSessaoUsuarioServlet`, `RelatoriosExportarImagensServlet`, `RelatorioPlacasVeicularesServlet`
  - `BaixaLogFtp`, `ImgVeiculoTempoReal`, `VerVideoMonitoramentoServlet`
  - `RelatorioDistribuicaoFatos`, `RelatorioEvolucaoSemanal`, `RelatorioPendenciasRegistroFato`, `RelatorioPermanenciaVeiculoAreaMonitoradaNew` (GET)
  - Apenas o GET: `ConfiguracaoInatividadeServlet`, `ConfiguracaoRadaresServlet`, `ConfiguracaoTempoOcrBlitzServlet`
- **Legado:**
  - `DownloadInfracao`, `ArquivoIntegracao`, `CriarPreRelatorioServlet`, `ImpTxtServiceImpl`
  - `relatorio/rj/*`: `RelatoriosFluxoVeicular`, `RelatoriosTempoPercurso`, `Relatorio7TempoPercurso`, `Relatorio8TempoPercurso`, `RelatorioSinalizacao`, `RelatorioFuncionamentoRJ`, `PlanilhaAcompanhamento`
- **Bypass `?sistema=`:** 48 servlets pulam a verificação quando o parâmetro `sistema` está presente (ver SEC-SQL-02).
- **Só sessão, sem `Acesso`:** os serviços GWT `*ServiceImpl` (10) e o `GuestTokenServlet`.

---

## 3. Achados — SQL e acesso a dados

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| SEC-SQL-01 | **Crítica** | `relatorios/RelatorioSessao.java:46-47,80-82`, `RelatorioSessaoUsuarioServlet.java:31-33,67,72` | `dataInicial`/`dataFinal` concatenados em `Statement`, sem login, com `e.getMessage()` na resposta. | `PreparedStatement` + `Timestamp`, sessão + `Acesso`, erro genérico. |
| SEC-SQL-02 | **Crítica** | `servlet/relatorio/RelatorioInfracoesCAV.java:53-61,80-86`, `model/medicao/DadosRelatorioInfracoesCAV.java:209-234` | `?sistema=1` pula a autenticação; seis parâmetros `sel_*` concatenados em `IN (...)`. | Remover o bypass `sistema` (48 arquivos); `?` dinâmicos ou regex `^\d+(,\d+)*$`. |
| SEC-SQL-03 | Alta | `muralha/configuracaoequipamento/ConfiguracaoEquipamentos.java:333,679-697,763`, `ConfiguracaoEquipamentoServlet.java:54,155-160` | Horários, tempo e quantidade de passagens concatenados, numa escrita feita por GET (CSRF). Um `UPDATE` sem `WHERE` (`:332-335`). | `?`, parse tipado com 400, POST, revisar o `WHERE`. |
| SEC-SQL-04 | Alta | `pontointeresse/PontosInteresse.java:53-55,84-86` | `nome`, `desc` e `equi` concatenados em INSERT. | `?` + lista de inteiros + transação. |
| SEC-SQL-05 | Alta | `veiculo/Veiculos.java:918-920,972-974` (`VeiculoServlet.java:1248`) | `idsLocais` em `IN (...)` e `TOP` com `quantidade` sem teto. | Lista de `Integer` com `?`; teto de 100. |
| SEC-SQL-06 | Alta | `docs/banco-de-dados/codigo-sql/procedures/muralha.spu_obterVeiculosPorFiltros.sql:209-356`, `dbo.spu_obterVeiculosGtwPorFiltros.sql:156-315` (+21 procedures com `EXEC(@sql)`) | Injeção de segunda ordem: o Java parametriza, mas a procedure concatena `@equipamentos`, `@renavam` e outros em SQL dinâmico. | `sp_executesql` com parâmetros, `STRING_SPLIT`/TVP para listas (script em `migracoes/`); validar no Java enquanto isso. |
| SEC-SQL-07 | Média | `model/RecursosC006_C008.java:134,138,146` | `placa`, `serie` e `tipoRemessa` concatenados (legado, exige login). | `?`. |
| SEC-SQL-08 | Média | `dashboard/DatasetServlet.java:71,79`, `DashboardBuilderServlet.java:519-590` | O Dashboard antigo aceita qualquer `nomeView` que case com a regex, o que permite ler `dbo.sis_usuario` (hashes, segredos TOTP). Sem timeout. | Aposentar ou exigir presença em `dash.catalogo_tabela`; timeout. |
| SEC-SQL-09 | Baixa | `dashboards/consulta/*`, `DashCatalogoServlet.java:220-227`, `ExecutorConsulta.java:16` | O motor novo está bem blindado (ver §9). Ressalvas: a importação do catálogo lista todas as tabelas; o cache não leva em conta o usuário; `setReadOnly` não tem efeito no mssql-jdbc. | Login SQL somente-leitura para o Dashboard; excluir tabelas sensíveis do catálogo; chave de cache com escopo. |
| DADOS-01 | Alta | `com/consilux/model/Usuario.java:364-370` | `alteraUsuario()` só fecha a conexão em caso de erro: **toda troca de senha vaza uma conexão**. | try-with-resources e propagar a exceção. |
| DADOS-02 | Alta | `com/consilux/model/VeiculoMonitorado.java:447-461` | `getRemoveStatement()` abre uma conexão que nunca é fechada. | Receber a `Connection` como parâmetro. |
| DADOS-03 | Média | `ExportaAmostras.java:36-47`, `medicao/ExportaComprovacaoImagem.java:30-41`, `relatorio/FluxoTempo.java:115`, `ImagensEmProcessamento.java:63`, `ConfiguracaoEquipamentoBroker.java:92` | Conexões entregues a iteradores ou guardadas em campos; vazam em caso de erro. | Fechamento determinístico. |
| DADOS-04 | Baixa | `PontosInteresse.java:96`, `ConfiguracaoEquipamentos.java:715-727`, `AITPDF.java:203-211`, `BlitzEletronicaVeicIrregular.java:125-129` | Statements reabertos sem fechar o anterior. | try-with-resources. |
| DADOS-05 | Média | `ConfiguracaoEquipamentos.configClandestino`, `PontosInteresse.inserirPontoInteresse`, `dashboards/PainelService.restaurarVersao:50-88` | Várias escritas sem transação. `restaurarVersao` não verifica permissão e não liga a versão ao painel (possível IDOR). | Transação explícita; checar `CompartilhamentoService.verificarPermissao`. |
| DADOS-06 | Alta | `com/consilux/lib/Conexao.java:165-174,212,235-238` | Pool de 400 sem `removeAbandoned`/`logAbandoned`, sem socket/query timeout, `maxIdle=400`, singleton sem sincronização, `getStackTrace()` a cada conexão. | Ligar `removeAbandoned(300)` + `logAbandoned`, timeouts, `maxIdle` entre 50 e 100, holder sincronizado, `isTraceEnabled()`. |
| DADOS-07 | Média | `WEB-INF/confGTW.xml.example` | `cript_chave`/`cript_iv` em hexadecimal e `senha_ftp=teste`; uma conta única (`consilux`) para tudo. | Esvaziar os valores; separar contas de leitura e escrita. |
| DADOS-08 | Alta | `gis/GisServlet.java:78-82` | `COUNT` sobre `veiculo_tempo_real` inteira a cada exportação GIS. | Filtro de período ou tabela consolidada. |
| DADOS-09 | Média | `util/Paginacao.java:59,77` | `itensPorPagina` sem teto, com `COUNT(*) OVER()`. | `Math.min(itens, 500)`. |
| DADOS-10 | Média | `consulta/AlertasOcorrencias.java:545,653` | N+1 com uma segunda conexão por linha. | JOIN/`OUTER APPLY` ou busca em lote. |
| DADOS-11 | Baixa | 17 arquivos sobre `veiculo_tempo_real` sem `TOP`/`OFFSET` | Possíveis consultas sem limite. | Revisão dirigida. |

---

## 4. Achados — Front-end e XSS

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| FE-01 | **Crítica** | `webapp/includes/erro_muralha.jsp:14-15,32,34`, `includes/erro.jsp:14-15,28,32` | XSS refletido **sem login** (`m`/`p` escritos com `<%= %>`); `p` aceita `javascript:` (open redirect). | `fn:escapeXml`; `p` só caminho relativo de uma lista. |
| FE-02 | Alta | `includes/erro_vazio.jsp`, `sucesso*.jsp`, `confirma.jsp`, `sim_nao.jsp` (destinos de `Mensagem.java:85-155`) | Mesmo padrão, inclusive dentro de string JS. | Escape HTML/JS. |
| FE-03 | Alta | `muralha-digital/pages/boletim/consulta.jsp:19-23` | `boletimId` dentro de `<script>`. | Validar e serializar com Gson. |
| FE-04 | Alta | `video/player/Player*.jsp`, `FlashPlayer.jsp` | `id_veiculo` refletido em atributo, em página sem acesso controlado. | `Integer.parseInt` + escape. |
| FE-05 | Média | `utils/credenciais/muralha_abertura_bootstrap.jsp:62,312` | `desc` refletido. | `<c:out>`. |
| FE-06 | Média | `boletim/botao-visualizar/*.jsp:14,21`, `registro_fato/comBoletim/botao-editar/*.jsp:14,21` | `id` em `onclick` inline. | Validar como número. |
| FE-07 | Média | `login/login_action_app.jsp:28,94` | Ver SEC-AUTH-22. | — |
| FE-08 | Alta | `pages/assinatura/base-template.jsp:83,94` | `<jsp:include page="${param.contentPage}">` (inclusão arbitrária de recurso do webapp, **potencialmente** `WEB-INF`) e `alternatePageUrl` com `javascript:`. Página sem cabeçalho nem acesso. | Mover para `WEB-INF`; lista fixa de páginas. |
| FE-09 | Média | `cabecalho_bootstrap_simples.jsp:268` | Nome do usuário sem escape em todas as telas (XSS armazenado). | `<c:out>`. |
| FE-10 | Média | `cabecalho_bootstrap_simples.jsp:90` (+3 cabeçalhos) | `var jsessionid = "${jsessionid}"` anula o `HttpOnly`. | Remover. |
| FE-11 | Baixa | `csxfileupload/csxfileupload.jsp`, `WEB-INF/templates/documentos/anexar_doc.jsp` | Parâmetros refletidos (legado). | `fn:escapeXml`. |
| FE-12 | Baixa | `AproveitamentoImagensAgrupado.java:47-65`, `notificacao/Notificacoes.java:287-303` | HTML concatenado com dados do banco (relatório e e-mail). | Escapar, como em `EmailChamado.esc()`. |
| FE-13 | Alta | `pages/blitz-abordagem/assets/js/detalhes-abordagem.js:171-232` | Nome de arquivo enviado pelo usuário dentro de `onclick` inline (XSS armazenado). | `data-*` + `addEventListener` + `esc()`. |
| FE-14 | Alta | `registro_fato/historico/js/registro-fato-modal-historico.js:111-362` | Observação, descrição e nome do banco sem escape. | `esc()`. |
| FE-15 | Alta | `atendimento-ocorrencias/assets/js/atendimento-ocorrencia.js:488-497,867` | Histórico em `innerHTML` sem escape. | `esc()`. |
| FE-16 | Média | `grafo-modal.js`, `modal-registroDeFato.js`, `sessao-usuario.js`, `listagem-abordagem.js`, `criar-abordagem.js`, `mancha-monitorada/assets/js/mapa.js`, `interacoes.js`, `consulta-veiculo/js/consulta.js` | ~61 interpolações de texto livre sem escape; 110 de 137 arquivos que inserem HTML não usam escape; existem ~20 cópias locais de `escapeHtml` e nenhum utilitário global. | `esc()` único em `utils/utilidades.js`, nos moldes de `Chamados.esc`. |
| FE-19 | Alta | 17 JSPs (ex.: `blitz/listagem-blitz.jsp:22`, `mapa-calor/mapa-calor.jsp:57`, `dashboards/dashboard.jsp:22`) | Chave Google Maps fixa (`AIza…`), apesar de `maps-config.js` já ter sido saneado. | Ver CFG-05. |
| FE-20 | Média | `video-ao-vivo/assets/js/video-ao-vivo.js:8`, `video-ao-vivo.jsp:115,121`, `monitoramento/monitoramento_pelco.jsp:33-116` | IPs internos e públicos fixos em HTTP; polling a cada 150 ms. | Configurável + proxy no servidor. |
| FE-22 | Média | `visualizacao-cameras/camera-api/controles.js:989,1001` | `eval(method + "()")` com valor de atributo. | Mapa `{nome: função}`. |
| FE-23 | Baixa | `js/calendario.js:234,239`, `js/funcoes.js:36` | `setInterval` com string. | Passar uma função. |
| FE-24 | Baixa | `assinatura/js/resultados.js:206`, `assets/js/processamento/dupla-analise.js:52` | Dados de assinatura e placa em `localStorage`/`sessionStorage` depois do logout. | Limpar no logout. |
| FE-25 | Alta | `/js/jquery.js` (1.7.2, 36 JSPs), freewall 1.10.2, 2.1.3, 3.2.0, MDB 3.4.1 | jQuery < 3.5 (CVE-2020-11022/11023, CVE-2019-11358); 10 versões. jQuery UI 1.12.1 (CVE-2021-41182/3/4). | Padronizar em 3.7.x (já usada no projeto). |
| FE-26 | Média | `configuracoes/parametros.jsp:11,15` e outras | Bootstrap 4.0.0 (CVE-2018-14040/1/2, CVE-2019-8331); 7 versões distintas. | Padronizar 5.3.x. |
| FE-27 | Média | ~25 JSPs (ex.: `relatorios/relatorio-detalhado-alertas.jsp:15`) | Chart.js do CDN sem versão fixa; moment 2.24 e 2.27 carregados juntos no cabeçalho; SheetJS 0.18.5 (CVE-2023-30533). | Fixar versões e atualizar. |
| FE-28 | Baixa | 174 de 215 `<script src="https://…">` | Sem SRI (`integrity`). | SRI ou vendorizar. |
| FE-29 | Alta | `pages/configuracoes/parametros.jsp:1-20` | Tela de configuração sem cabeçalho e sem verificação de acesso; Bootstrap 4 CSS + 3 JS. | Cabeçalho padrão + `Acesso`. |
| FE-30 | Média | `video-ao-vivo/video-ao-vivo.jsp`, `registro_fato/semBoletim/detalhes-fato.jsp`, `assinatura/base-template.jsp` | Sem cabeçalho padrão e sem verificação de acesso. | Idem. |
| FE-31 | Média | `atendimento-ocorrencias/abrir-ocorrencia.jsp:7-11`, `guarnicao/listagem-guarnicoes.jsp`, `registro_fato/menu_registro_fato.jsp`, 8 relatórios | Bootstrap/jQuery recarregados por cima do cabeçalho (dois `bootstrap.bundle`). | Usar só o cabeçalho. |
| FE-32 | Média | ~176 `alert()` em JS e 58 em JSP; 15 `confirm()` (ex.: `configuracao-monitoramento/assets/js/configuracao.js`, `static/js/dashboards/dataset-editor.js`, `painel.js:412`) | Fora do padrão visual (SweetAlert2), inclusive em código novo. | SweetAlert2. |
| FE-33 | Baixa | 44 JSPs | `<html lang="en">`. | `lang="pt-BR"`. |
| FE-34/35 | Média | 66 JSPs sem `pageEncoding="UTF-8"`; 48 arquivos não UTF-8 (ex.: `utils/notificacao/notificacao.jsp`, `pages/perguntasRespostas/*`, `pages/registro_fato/**`) | Acentuação corrompida. | Converter para UTF-8 e declarar `pageEncoding`. |
| FE-36 | Baixa | `pages/**/*.jsp` | 24 de 34 `<img>` sem `alt`; labels sem `for`; botões só com ícone sem `aria-label`. | Ajustar. |

> FE-17, FE-18 e FE-21 foram consolidados em CFG-04 e SEC-AUTH-22.

---

## 5. Achados — Segredos, configuração, infraestrutura e dependências

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| CFG-01 | **Crítica** | `muralha/digital/assinatura/keystore/muralha-keystore.p12`, `AssinaturaConfig.java:69-70`, `src/test/.../AssinaturaImagemTeste.java:436` | Chave privada de assinatura de evidências versionada; senha padrão fixa no código e usada em silêncio quando `SIGN_KS_PATH`/`SIGN_KS_PASSWORD` não estão definidas. | Revogar e gerar nova chave fora do git; falhar sem configuração; limpar o histórico (com aprovação). |
| CFG-02 | Alta | `assinatura/KeystoreAutoGenerator.java:60,70-71,89-94` | Senha na linha de comando do `keytool` e no texto de exceção. | Não expor; não gerar automaticamente em produção. |
| CFG-03 | Alta | `notificacao/ServicoSMS.java:454-456` | Hash e senha do gateway Facilita Móvel no código (o S1 não foi concluído). | Rotacionar; ler de `Inicializacao`. |
| CFG-04 | **Crítica** | `pages/video-passagem-semaforo/video-passagem-semaforo.jsp:61`, `assets/query/alterar_cameras_ao_vivo.sql:9,12`, `assets/images/index.html`, `visualizacao-cameras/assets/js/visualizacao-cameras.js:149`, `visualizacao-cameras-original.html:25-30` | Usuário e senha de câmeras (inclusive com IP público) enviados ao navegador e servidos sem login em `assets/`. | Trocar as senhas das câmeras; proxy autenticado; remover os arquivos `-original`, `.html` e `.sql` do webroot. |
| CFG-05 | Média | 16 JSPs + `ponto-interesse.jsp.bak` | Chave Google Maps fixa. | Restringir por referrer/API, rotacionar, injetar a partir da configuração. |
| CFG-06 | Média | `WEB-INF/server-config.wsdd:5` | `adminPassword` padrão do Axis. | Trocar e remover o AdminService. |
| CFG-07 | **Crítica** | `nginx-1.26.2/conf/nginx.conf` (`location ~ /RPC2…`) | `proxy_pass http://$http_self_targetip`: o destino vem de um cabeçalho do cliente (SSRF / proxy aberto para a rede interna); regex sem âncora. | `map` com lista fixa de câmeras + `auth_request` ao GTW. |
| CFG-08 | **Crítica (condicional)** | `nginx.conf` (`location /`), `server-config.wsdd:101-105`, `web.xml:340-368` | Atrás do proxy, o Tomcat vê `127.0.0.1`, então o `enableRemoteAdmin=false` do Axis não protege. Isso abre caminho conhecido de RCE no Axis 1.4 (não testado). | `deny all` no nginx para `AdminService`, `AdminServlet`, `AxisServlet`, `*.jws`; remover do `wsdd`/`web.xml`. |
| CFG-09 | Baixa | `ServicoSMS.java:128,177-178,211-214` | Celular pessoal fixo (LGPD). | Remover. |
| CFG-10 | Baixa | `confGTW.xml.example:111` | `senha_ftp=teste`. | Vazio. |
| CFG-11 | Info | `.gitignore` | Configurações locais e `.env` corretamente ignorados. | Acrescentar `*.p12`, `*.jks`, `*.pfx`, `*.pem`, `*.key`, `*.bak`. |
| CFG-12 | Média | `nginx-1.26.2/nginx.exe`, `RunHiddenConsole.exe`, `webapp/configuracoesequipamento/configEquipApp.exe` | Executáveis versionados; um deles servido no webroot. | Verificar hash/origem; distribuir fora do git. |
| CFG-13 | Média | `ip_permitidos.txt` (vazio), `LogonLogoff.java:315-332` | Controle de IP desligado na prática (fail-open). | Ver SEC-AUTH-15. |
| CFG-14 | Baixa | `*.bak`, `*-original.*` no webroot | Expostos sem login. | Remover. |
| CFG-15 | Alta | `analise/docker-compose.yml:156-163`, `analise/nginx/default.dev.conf:171-206` | DEV publica `8088` em todas as interfaces e injeta `X-GTW-Usuario: administrador`, ou seja, Admin do Superset + SQL Lab para quem alcançar o host. | Publicar em `127.0.0.1:8088`. |
| CFG-16 | Alta | `analise/docker-compose.prod.yml:131-137` | O *override* de produção mantém `8088:80` e TLS comentado. | Habilitar TLS e redirecionar 80→443. |
| CFG-17 | Média | `analise/superset/superset_config.py:285-288` | `TALISMAN_ENABLED=False`, `X-Frame-Options: ALLOWALL`. | Talisman + `frame-ancestors` restrito. |
| CFG-18 | Média | `superset_config.py:201`, `analise/GuestTokenServlet.java:133` | Segredo do guest token = `SECRET_KEY` do Superset, e esse valor é entregue ao Tomcat. `GTW_SSO_HMAC_SECRET` está documentado, mas não é usado. | Segredos separados. |
| CFG-19 | Média | `analise/superset/gtw_security.py:78-84` | Identidade baseada só em cabeçalhos `X-GTW-*`. | Assinatura HMAC dos cabeçalhos. |
| CFG-20 | Média | `analise/.env.exemplo:30`, `provisionar.py:82` | O Superset usa a conta da aplicação. | Login somente-leitura em `analise.*` (script em `migracoes/`). |
| CFG-21 | Média | `superset_config.py:144` | `ENABLE_TEMPLATE_PROCESSING=True`. | Avaliar necessidade. |
| CFG-22 | Baixa | `superset_config.py:243-248` | `SESSION_COOKIE_SECURE` ausente; API de charts isenta de CSRF. | Ajustar. |
| CFG-23 | Baixa | `analise/docker-compose.yml:89-95,156` | Redis sem senha; `nginx:1.26-alpine` com tag flutuante. | `requirepass`; versão fixa. |
| CFG-24 | Baixa | `analise/scripts/refresh-materialized.sh:30` | `sqlcmd -P` (senha visível no `ps`). | `SQLCMDPASSWORD`. |
| CFG-25 | Alta | `nginx.conf` (`…/rtspoverwebsocket`, `/cam/realmonitor`) | `proxy_pass http://$camera_ip:$camera_port` com valores da URL (SSRF). | Lista fixa + `auth_request`. |
| CFG-26 | Alta | `nginx.conf` (`listen 8185`) | Sem TLS, sem `server_tokens off`, sem cabeçalhos de segurança. | TLS + HSTS/CSP/XFO/nosniff. |
| CFG-27 | Média | `nginx.conf` (`location /`) | `proxy_read_timeout 86400` global; sem `limit_req`/`client_max_body_size`. | Timeout longo só no WebSocket; rate limit no login. |
| CFG-28 | Alta | `infra/SerializadorXML.java:113` ← `ws/server/CSXEventsWS.java:23-25` | XXE em XML recebido via SOAP. | `disallow-doctype-decl` + `FEATURE_SECURE_PROCESSING` (como em `HistoricoParametros.java:74`). |
| CFG-29 | Alta | `infra/AjaxXMLLeitor.java:52` ← `servlet/descarga/ConfirmarDescarga.java:78-79` | XXE em upload de XML. | Idem. |
| CFG-33 | Média | `ServicoSMS.java:226,346-347`, `EncurtadorURL.java:38-39`, `LoginGoogle.java:78-104`, `Blitzes.java:2956-2957` | HTTP sem timeout (R7). O geocoding roda dentro de `synchronized` com `sleep`. | Connect 5 s, read 15-30 s. |
| DEP-01 | Alta | `pom.xml` — `axis` 1.4 (declarado 2×) | EOL; CVE-2019-0227, CVE-2018-8032, CVE-2012-5784/2014-3596. | Mitigar por rede (CFG-08); plano de migração do SOAP. |
| DEP-02 | Alta | `log4j` 1.2.17 (2×) | EOL; CVE-2019-17571, CVE-2021-4104, CVE-2022-23302/5/7. A configuração atual não usa os appenders vulneráveis. | Proteger escrita do `log4j.xml`; pedir aprovação para `reload4j`. |
| DEP-03 | Alta | `commons-fileupload` 1.4 | CVE-2023-24998 (DoS). | `maxPartCount` no Connector; pedir 1.5. |
| DEP-04…09 | Média | `jackson-databind` 2.12.5, `gson` 2.8.8, `commons-configuration2` 2.6, `httpclient` 4.5.10, `commons-io` 2.6, `commons-net` 3.6, `commons-lang3` 3.9, `jsch` 0.1.55 + `StrictHostKeyChecking=no` (`AcessoSFTP.java:148-304`) | CVEs conhecidas (DoS, interpolação, MITM). | Pedir *patches*; `known_hosts`. |
| DEP-10 | Média | `taglibs:standard` 1.1.2, `itext` 2.1.7/4.2.2 (conflito), `derby` (sem uso), `hibernate-validator` 4.0.2, `poi` 4.1.1 | XXE e outras. | Remover o Derby, deduplicar o iText; não usar `x:parse`. |
| DEP-11 | Média | `gwt-servlet` 2.1.0, `gxt` 2.2.1 | EOL; fontes ausentes (M1). | Mitigar (autenticação antes do RPC). |
| DEP-12 | Baixa | `jasperreports` 6.10, `quartz` 2.3.2, `mssql-jdbc` 9.2.1 | Antigos. | Monitorar; `skipUpdateCheck=true`. |
| DEP-13 | Média | `pom.xml:306-311` | Repositório Maven interno em HTTP com `updatePolicy always`. | HTTPS (pedido de aprovação). |
| LOG-01 | Alta | `acessos/Usuario.java:186` | Token de acesso registrado em INFO. | Mascarar. |
| LOG-02 | Alta | `UsuarioServlet.java:85,202,257,333,422,468,519,913` | Hash MD5 da senha e tokens em DEBUG (gravados de fato, ver LOG-05). | Remover. |
| LOG-03 | Média | `blitz/Blitzes.java:2313` | CPF em log. | Mascarar. |
| LOG-04 | Média | ~40 a 145 pontos (ex.: `MfaConfigServlet.java:75,103`, `TipoAlertaConfigServlet.java:163`, `RelatorioPendenciasRegistroFato.java:156`) + `web.xml` sem `<error-page>` | Mensagens de JDBC e *stack traces* do Tomcat expostos; JSON de erro concatenado sem escape. | Mensagem genérica + id de correlação; `<error-page>`; `ErrorReportValve showReport=false`. |
| LOG-05 | Média | `src/main/resources/log4j.xml:5,17,75-79` | `<root>` sem nível (DEBUG), appender TRACE para stdout, rotação de 5×1 MB. | Root INFO; rotação maior. |
| LOG-06 | Baixa | `Funcoes.java:856-880` + 144 `printStackTrace` | Linha de comando completa registrada (pode conter senha de `net use`). | Mascarar argumentos. |
| LOG-07 | Baixa | `ServicoSMS.java:220-226` | Chave do SmsDev na query string. | Enviar no corpo ou no cabeçalho. |

**Pontos verificados e corretos:**
- `.env.exemplo` e os `*.example` têm segredos vazios.
- Twilio, SendGrid e OAuth são lidos de variáveis de ambiente.
- O `Dockerfile` do Superset roda como usuário não-root e as versões pip estão fixadas.
- Não há `ObjectInputStream`/`XMLDecoder`/`XStream` nem *default typing* do Jackson.
- Não há `TrustManager` permissivo.
- O log4j não usa Socket, JMS nem JDBC appender.

---

## 6. Achados — Confiabilidade, concorrência e desempenho

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| CONF-01 | Alta | `monitoramento/CamerasMonitoramentoAoVivo.java:107` ← `MonitoramentoAoVivoServlet.java:282,303` | Conexão aberta sem `close`, a cada troca de grupo de câmeras. | try-with-resources. |
| CONF-02 | Alta | `MonitoramentoAoVivoServlet.java:279,303-304`, `_ini/Inicializacao.java:36` | Uma requisição altera o estado global (grupo em exibição, lista sincronizada trocada por `ArrayList`). | Estado por sessão; publicação atômica (`CopyOnWriteArrayList`/imutável). |
| CONF-03 | Alta | `servlet/ajax/processamento/Processar.java:141-158,279-288,402-404`, `ExportarImagensServlet.java:25,56-90`, `ExportarDescargaServlet.java:22`, `CadastrarFiltroServlet.java:47`, `DesabilitaFiltroServlet.java:33`, `ReprovarRemessa.java:43-44,76` | Campos de instância mutáveis em servlets (singletons): uma resposta pode sair misturada com a de outra requisição. | Variáveis locais ou objeto de resultado. |
| CONF-04 | Média | `RelatorioPlacasVeicularesServlet.java:53`, `Remessa.java:76-78`, `InfracaoCompletaLista.java:80`, `IntegracaoSequenciaImagem.java:32`, `CadastrarInibicaoServlet.java:39-40`, `VisualizarInibicaoServlet.java:30-31` | `SimpleDateFormat` compartilhado. | `DateTimeFormatter`. |
| CONF-05 | Média | `websocket/ClienteSessoes.java:72-81,142-188,227-237` | Iteração por índice com remoção concorrente; `getBasicRemote()` síncrono na thread produtora. | `CopyOnWriteArrayList` + `getAsyncRemote()` com timeout. |
| CONF-06 | Alta | `ClienteSessoes.java:46-52`, `VeiculoTempoReal.java:39`, `BlitzEletronica.java:37`, `AuditoriaService.java:18`, `LiberarMovimentosLote.java:37-38`, `ProcessamentoMultiplas.java:36`, `GenericCache.java:26` | Threads e executores não param no `contextDestroyed`; flags não são `volatile`. | Listener de desligamento único. |
| CONF-07 | Média | `VeiculoTempoReal.java:64-70` (1 s), `BlitzEletronica.java:60-71` (100 ms), `Alertas.java:83-90` (2 s) | Polling ao banco mesmo sem clientes (R2). | Checar `ObterQtdeSessoesPorTipo > 0`, como em `Blitzes.iniciarTimer`. |
| CONF-08 | Alta | `src/main/resources/quartz.properties`, `servlet/ferramentas/Agendador.java` | 31 jobs em 4 threads, `RAMJobStore`; 8 jobs sem `@DisallowConcurrentExecution`. | `threadCount` 10, anotar os jobs, avaliar `JobStoreTX`. |
| CONF-09 | **Crítica** | `retencao/RetencaoJob.java:36-51` | O job de retenção **só conta** registros e grava `SUCESSO` com `registros_movidos = contagem`; nada é apagado (falsa conformidade LGPD). `CAST(data AS DATE)` impede o uso de índice. | Expurgo em lotes por script aprovado; log fiel ao que foi feito. |
| CONF-10 | Média | `RetencaoJob:21`, `DisponibilidadeJob:37`, `IncidenteImportJob:27-55`, `SlaPreprocessamentoJob:18`, `SlaLatenciaJob:18` | Erros só em `System.err`. | `logger.error(…, e)`. |
| CONF-11 | Média | `VeiculosCorrelacionados.java:132-135`, `CamerasMonitoramentoAoVivo.java:153-156` + 220 `catch` vazios | Exceção engolida que vira "resultado vazio". | Propagar ou responder 500. |
| CONF-14 | Baixa | `notificacao/JobRetryNotificacao.java:58-85` | Retry a intervalo fixo e N+1. | Backoff exponencial. |
| CONF-15 | Média | `auditoria/AuditoriaService.java:18,55,87` | Fila sem limite, sem `shutdown`; `X-Forwarded-For` aceito do cliente (IP auditado forjável). | Fila limitada; cabeçalho só de proxy confiável. |
| PERF-01 | Alta | `VerVideoMonitoramentoServlet.java:87-189` | Vídeo inteiro em `byte[]`; temporário não removido em caso de erro; `==` em String. | Streaming + `Range` + `finally`. |
| PERF-02 | Média | `painelInformacoes/Cameras.java:288-296` | `COUNT_BIG(*)` sobre tabelas de 8 a 11 milhões de linhas. | `sys.dm_db_partition_stats` ou snapshot. |
| PERF-03 | Média | 52 × `CAST(… AS DATE)` em filtros (ex.: `ExportacaoServlet:166,170`, `PreProcessamentoServlet:202-210`) | Impede o uso de índice. | `data >= ? AND data < ?`. |
| PERF-04 | Média | `alerta/Alertas.java:96`, `RespostaRequisicaoXML.java:266` | `JAXBContext.newInstance` a cada ciclo. | `static final`. |
| PERF-05 | Baixa | `VeiculoTempoReal.AdicionaEquipamentosTempoReal:106-117`, `exportacao/ExportacaoService.java:21,124,135` | Consulta por iteração; arquivos SXSSF gerados inteiros em memória. | Lote; streaming. |

---

## 7. Achados — Manutenibilidade, testes e build

| ID | Sev. | Local | Descrição | Recomendação |
|---|---|---|---|---|
| TST-01 | Alta | `src/test/testng-customsuite.xml`, `pom.xml:203-212` | A suíte só inclui o grupo `completo` e está com `<classes>` comentado: **nenhum dos 396 testes roda**. O `build.ps1` usa `-DskipTests` e não há CI. | Suíte por pacote (`muralha.digital.*`), excluindo testes de integração; documentar `mvn test`. |
| TST-02 | Alta | — | Sem testes para `Acesso`, `ValidaSessao`, login, MD5/senha, MFA (`TotpService`, `MfaConfigServlet`), `AuditoriaService`, `RetencaoJob`, `JobSha256Imagem`. | Criar testes de regressão junto com as correções. |
| TST-03 | Baixa | `pom.xml` | `mockito-core` + `mockito-all` 1.10.19 juntos; `-XX:MaxPermSize` (inexistente desde o JDK 8). | Pedir ajuste. |
| MAN-01 | Média | `blitz/Blitzes.java` (3.130 linhas), `DadosMedicao` (2.876), `BlitzServlet` (2.382), `RegistroDeFatos` (2.377)…; JSPs de 1.300 a 1.400 linhas | Classes e páginas gigantes. | Extrair serviços ao tocar no código. |
| MAN-02 | Média | 134 servlets com bloco de autenticação copiado; 23 respostas `{"erro":…}` concatenadas; `formatar(...)` triplicado | Duplicação de código. | `ServletBase`/`JsonResposta` (ver Sugestões). |
| MAN-03 | Baixa | `web.xml:388-395` (`/teste`), `*-original.*`, `*.bak`, `* - Copia.*`, `cabecalho_antigo.png`, `VeiculoTempoReal extends HttpServlet` | Código morto (M5). | Remover. |
| MAN-04 | Média | 76 `System.out\|err`, 144 `printStackTrace`, 4 classes com `java.util.logging`, 40 `==` em String | Logging inconsistente e bugs latentes. | log4j + `equals`. |
| MAN-05 | Baixa | 538 TODO/FIXME (Java) e 521 (webapp Muralha) | Dívida não rastreada. | Triagem. |
| MAN-07 | Baixa | `web.xml:39-41,108-112` | `Inicializacao` declarada como `listener` e servlet, mas não implementa `ServletContextListener`. | Corrigir a declaração e o doc 02. |
| MAN-08 | Baixa | `pom.xml:76-77,139-142,265-285` | `maven.compiler.*=14` contra plugin em 13; `xdelta` no `install`; antrun sem versão; Jersey sem dependência visível. | Pedir ajuste (stack imutável). |

---

## 8. Situação dos itens da auditoria anterior ([`12-riscos-e-debitos-tecnicos.md`](../requisitos/12-riscos-e-debitos-tecnicos.md))

| Item | Situação em 2026-10-19 | Evidência |
|---|---|---|
| S1 Credenciais expostas | **Parcial**: ainda há segredos no HEAD | CFG-01, 03, 04, 05, 06; SEC-AUTH-11 |
| S2 `ValidaSessao` fail-open | **Aberto** | SEC-AUTH-01/02 |
| S3 11 servlets sem sessão | **Parcial (1 de 11)**: só `CorrecaoPlacaServlet` foi corrigido; ~25 outros endpoints identificados | §2 |
| S4 `fcn_VerificaAcesso` fail-open | **Aberto e agravado** (`catch` vazio) | SEC-AUTH-03 |
| S5 MD5 sem sal | **Aberto** (+ pass-the-hash) | SEC-AUTH-17/19 |
| S6 CSRF/cookies | **Aberto** | SEC-AUTH-31/32 |
| S7 SOAP/AdminServlet | **Parcial** | SEC-AUTH-34, CFG-08 |
| S9 SQL concatenado | **Aberto** | SEC-SQL-01…07 |
| S10 XSS no JS | **Aberto e generalizado** | FE-13…16 |
| S11 `REDIRECT_URI` localhost | **Aberto** | `LoginGoogle.java:39` |
| S12 Chave Maps no cliente | **Parcial**: `maps-config.js` saneado, 17 JSPs não | CFG-05 |
| R1 Pool/vazamentos | **Aberto** | CONF-01, DADOS-01/02/06 |
| R2 Polling sem clientes | **Aberto** | CONF-07 |
| R3 Retenção | **Aberto, com controle enganoso** | CONF-09 |
| R5 `RAMJobStore` | **Aberto** | CONF-08 |
| R6 XML no WebSocket | **Parcial** (JAXB/JSON, não string) | — |
| R7 Timeouts HTTP | **Aberto** (só `IncidenteImportJob` corrigido) | CFG-33 |
| M1 Fontes GWT ausentes | **Aberto** | — |
| M2 Bibliotecas duplicadas | **Aberto** | FE-25/26/31 |
| M4 Poucos testes | **Parcial**: 396 testes existem, mas não rodam | TST-01 |
| M5 Código morto | **Aberto** | MAN-03 |
| M6 Encoding misto | **Parcial**: Java OK; 48 arquivos na Muralha | FE-35 |
| M7 Dependências antigas | **Aberto** | DEP-01…13 |
| M8 `pom` 14×13 / Jersey | **Aberto** | MAN-08 |
| M9 `Inicializacao` estática | **Aberto e pior** (alterada em tempo de execução) | CONF-02 |
| M10 Valores de produção nos exemplos | **Parcial** | DADOS-07 |

> R4 e M3 dependem do banco e não foram verificáveis.

---

## 9. Pontos positivos

- **Dashboard Builder novo** (`muralha/digital/dashboards/consulta`): identificadores só do catálogo com regex e colchetes, valores sempre via `?`, `IN` limitado a 500, campo calculado com gramática fechada, timeout de 30 s e `TOP` máximo de 10.000.
- **Módulo de Chamados**: escape consistente (`Chamados.esc`, `EmailChamado.esc`), modelo de perfis próprio, `nosniff`.
- **Login principal**: tratamento de fixação de sessão (`invalidate()` + nova sessão) em `login_action.jsp`, `mfa_verificar.jsp` e `LoginGoogle`. Token de recuperação gerado com `SecureRandom` (UUID v4).
- **Parsers seguros** já existem como referência (`HistoricoParametros.java:74`).
- `IncidenteImportJob` já define timeouts HTTP; `Blitzes.iniciarTimer` liga e desliga o polling conforme os clientes conectados.
- Cerca de 98% das aquisições de conexão têm `finally` ou try-with-resources.
- Os `*.example` e o `.env.exemplo` estão sem segredos; o Superset roda como não-root com versões fixadas.
- Existem 396 testes TestNG prontos: o problema é só a configuração da suíte.

---

## 10. Matriz de priorização

| Prioridade | Prazo sugerido | Itens |
|---|---|---|
| **P0 — Emergencial** (fora do código + bloqueios) | Imediato | Rotacionar/revogar: keystore (CFG-01), Facilita Móvel (CFG-03), câmeras (CFG-04), FTP (SEC-AUTH-11), chave Maps (CFG-05), Axis admin (CFG-06). Bloquear no nginx: `RPC2`, rotas de câmera dinâmicas, Axis admin/`*.jws` (CFG-07, 25, 08). Publicar o Superset DEV só em `127.0.0.1` (CFG-15). |
| **P1 — Crítico** | 1ª sprint | SEC-AUTH-04, 05, 06, 07 (recuperação de senha); SEC-AUTH-01, 02, 03 (filtro e `Acesso` fail-closed); SEC-AUTH-08, 09, 10, 12, 13, 14, 18; SEC-SQL-01, 02 (+ remoção do bypass `sistema`); SEC-AUTH-36; FE-01, FE-10; CONF-09 |
| **P2 — Alto** | 2ª–3ª sprint | SEC-AUTH-15, 16, 17, 19, 22, 28, 38, 39; SEC-SQL-03…06; CFG-28/29 (XXE); DADOS-01, 02, 06; CONF-01, 02, 03, 06, 08; LOG-01, 02, 05; FE-02…04, 08, 13…15, 29; TST-01, 02; PERF-01 |
| **P3 — Médio** | Planejar | CSRF/cookies (SEC-AUTH-31…33), MFA (23, 24), Google (25, 26), Superset (CFG-16…22), timeouts (CFG-33), demais FE, DADOS, CONF e PERF; pedidos de aprovação de dependências (DEP-*) |
| **P4 — Baixo** | Oportunista | MAN-*, FE-23/24/28/33/36, CFG-09/10/14/23/24, LOG-06/07 |

Plano de ação detalhado: [Sugestões de melhoria](2026-10-19-sugestoes-melhoria.md). Execução assistida: [Prompt para code review e correção](2026-10-19-prompt-code-review-correcoes.md).
