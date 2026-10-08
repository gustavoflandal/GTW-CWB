# 02 — Arquitetura

## 1. Visão em camadas

```
 Navegador (JSP renderizado no servidor + jQuery/Bootstrap 5 | GWT/GXT legado)
        │  HTTP (form / $.ajax JSON) · WebSocket /ClientesWebSocket · SOAP /services/*
        ▼
 nginx 1.26 (opcional, porta 8185 → Tomcat 64000; proxy de câmeras e WebSocket)
        ▼
 Tomcat (WAR "ROOT", contexto "/")
   ├─ Filtros:      ValidaSessao (@WebFilter "/*"), CacheFilter (*.png *.css *.gif *.html)
   ├─ Listeners:    SessoesAtivas, muralha.digital._ini.Inicializacao
   ├─ Servlets:     194 em web.xml + 118 @WebServlet  (padrão "servlet com ?acao=")
   ├─ JSP:          paginas e fragmentos (include de cabeçalho/menus/modais)
   ├─ JAX-RS:       Jersey em /rest/*  (pacote com.consilux.rest.resources)
   ├─ SOAP (Axis):  /services/*  (InfoEquipamento, StatusEquipamento, ConfigEquip(WS), CSXEventsWS, VeiculoMonitoradoWS, Version)
   ├─ WebSocket:    @ServerEndpoint("/ClientesWebSocket")
   ├─ Jobs Quartz:  Agendador (init-on-startup 4)
   └─ Threads próprias: Alertas (2 s), VeiculoTempoReal (1 s), BlitzEletronica, Blitzes.timer
        ▼
 Camada "model": classes de negócio + JDBC puro (PreparedStatement / CallableStatement)
        ▼
 com.consilux.lib.Conexao  (singleton, commons-dbcp BasicDataSource, SQL Server)
        ▼
 SQL Server GTW_MURALHA_DEV  (tabelas dbo/muralha, procedures `spu_*`, funções `fcn_*`, views `vw_*`)
```

Não existe framework MVC, ORM, injeção de dependência, nem camada de serviço formal. A regra de negócio mistura-se entre JSP (legado), servlets, classes "model" e **procedures/funções SQL** (parte relevante da lógica — ex.: geração de alertas — mora no banco).

## 2. Ciclo de uma requisição (Muralha Digital)

1. O navegador abre uma página `/muralha-digital/pages/<modulo>/<tela>.jsp`.
2. A JSP inclui `utils/credenciais/cabecalho_bootstrap_simples.jsp`, que: carrega Bootstrap/FA/jQuery/Tempus Dominus; instancia `new Acesso(request, response, true)` e chama `verificaAcesso()` (redireciona ao login se não autenticado/sem direito); monta os menus do usuário (`UsuarioServlet.ObterListaMenusAcesso/Relatorio/Graficos`); desenha a navbar.
3. O JS da tela (`assets/js/<tela>.js`) chama o back-end por `$.ajax` em `/MuralhaDigital/<Entidade>` com `acao=<nome>` (GET para leituras, POST para escritas) e recebe **JSON (Gson)** — alguns pontos usam XML (WebSocket e alguns endpoints antigos).
4. O servlet (`muralha.digital.<modulo>.<Entidade>Servlet`) despacha por `if/else` sobre `acao`, valida parâmetros, chama a classe de domínio (`Entidade`/`Entidades`) que abre `Conexao.getConexao()`, executa SQL/procedure, fecha tudo em `finally` e devolve JSON ou `sendError`.
5. Na tela, `Swal.fire`/`bs_alert`/`toastr` reportam sucesso ou erro.

Fluxo GTW clássico: JSP → `include cabecalho.jsp` → GWT widgets (`GtwMenu.nocache.js`, `GtwWidgets/*`) → serviços GWT-RPC (`com.consilux.ui.server.*ServiceImpl`) ou servlets `/ajax/*`, `/processo/*`, `/relatorio/*`, `/remessa/*`.

## 3. Autenticação e sessão

- **Login**: `login/login.jsp` → `login/login_action.jsp` (POST `login`, `senha`, `ip`, `retUrl`, `manterConectado`). Valida formato (`[A-Z0-9.]{2,30}`), busca `Usuario.buscaUsuarioPor(usuario, ativo=1)`, compara senha (`Usuario.comparaSenha`), invalida a sessão anterior, cria nova sessão e grava o objeto `Usuario` no atributo `[usuario]` (`SessaoConstantes.SESSAO_USUARIO`), registra `LogonLogoff` (tabela de logon/logoff) e registra `SessaoFinalizaManager` para encerramento.
- Inatividade: valores vêm de `ConfiguracaoInatividadeServlet` (config `LoginTempoInatividade` / `LoginNuncaBloqueia`) — com "nunca bloqueia" a sessão fica sem timeout (`manterConectado=true`). `web.xml` define 240 min como padrão do container.
- Outras vias de entrada: login Google OAuth (`muralha.digital.google.LoginGoogle`, servlet `/callback`, `login_google_token`), acesso por token (`acessoViaToken`, procs `spu_ppv_sis_usuario_token_*`), apps móveis (`login_action_app.jsp`, cookie `isMobileApp`).
- **Redirecionamento pós-login**: `retUrl` → troca de senha obrigatória (`/login/login_change.jsp`) → tela de alertas (`/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp`, se `ConfiguracaoSons.buscarLoginRedirecionaTelaAlerta()` for verdadeiro e `Alertas.buscarAlertasPendentes()` encontrar pendências) → `/login/abertura-sistemas.jsp`.
- **Filtro `ValidaSessao`** (`/*`): libera `/login/`, `/Abertura/`, `/ClientesWebSocket`, `erro*.jsp`, `resources/`, `assets/`, `favicon`; se há sessão, revalida a cada 5 min se o usuário continua ativo (`UsuarioServlet.obterStatusUsuario`). **Atenção: se não há sessão (`getSession(false)==null`) o filtro deixa a requisição passar** — a proteção real depende de cada JSP/servlet chamar `Acesso.verificaAcesso()`. Ver `12-riscos-e-debitos-tecnicos.md`.

## 4. Autorização (resumo)

`Acesso.verificaAcesso()` consulta `dbo.fcn_VerificaAcesso(url/ação, id_usuario)`: URLs não cadastradas em `sis_menu.acao` são **liberadas**; cadastradas exigem direito via `sis_menu_direitos` (por grupo ou diretamente por usuário). Menus dinâmicos vêm de `sis_menu_infos`, `sis_menu_relatorio_infos`, `sis_menu_graficos_infos`. Detalhe completo em `09-seguranca-e-permissoes.md` e dados reais em `referencia/menus-e-permissoes.md`.

## 5. Acesso a dados

- `com.consilux.lib.Conexao` (singleton): `BasicDataSource` do commons-dbcp 1.4, `jdbc:sqlserver://<host>`, catálogo = banco configurado, pool inicial 20 / máx. 400, `maxWait 2000 ms`, `validationQuery SELECT 1`, `packetSize 16384`. Usuário/senha de `confGTW.xml` (`armazenamento/servidor_sql`); sem usuário usa autenticação integrada do Windows.
- `Conexao.getConexao()` devolve um `ConnectionAdapter` (auto-commit `true`; `close()` devolve ao pool). **Quem abre fecha** — sempre em `finally`.
- Padrão de DAO: **métodos estáticos nas próprias classes de domínio** (`Guarnicao.listarTodas()`, `Alertas.obter…`), SQL em `StringBuilder`/strings concatenadas, `PreparedStatement` com parâmetros posicionais, procedures via `EXEC schema.proc ?` ou `{call …}`.
- Transações explícitas só onde necessário (`setAutoCommit(false)`).
- Esquemas: `dbo` (GTW clássico e cadastros de sistema), `muralha` (Muralha Digital), `mobilidade` (dados Waze/mobilidade; não usado pelo Java), `ia` (classificação de características do veículo por IA: `veiculo_caracteristica`, `cad_cor/marca/modelo`).

## 6. Tempo real

- **WebSocket** `/ClientesWebSocket`: o cliente envia `X_Y_<TIPO>-<SUBTIPO>-<contexto>-<info>[-…]`; `ControleAcessoEnvio` valida (≥ 3 partes) e registra o `Cliente` em `ClienteSessoes`. Tipos (`Constantes.ClientesWS`): `ALERTA-NOTIFICACAO`, `VEICULO-TEMPOREAL`, `BLITZ-DIGITAL`, `BLITZ-ELETRONICA`.
- **Produtores** (iniciados em `Inicializacao.init` → `ClienteSessoes.iniciaListaClientesWebSockets`): `Alertas` (thread, ciclo 2 s, chama `EXEC muralha.spu_ObterNovosAlertas`), `VeiculoTempoReal` (thread, ciclo 1 s), `BlitzEletronica`, e `Blitzes.iniciarTimer()` que roda só enquanto houver cliente `BLITZ-DIGITAL`. Mensagens são **XML** enviadas por `ClienteSessoes.EnviaTextoClientes(tipo, xml)`.
- Uma thread (`RemoveClientesDesconectados`, 2 s) limpa sessões mortas.
- Em produção o nginx precisa repassar `Upgrade/Connection` (já configurado em `nginx.conf`).

## 7. Jobs agendados (Quartz)

`com.consilux.servlet.ferramentas.Agendador` (servlet `load-on-startup 4`) registra, conforme `confGTW.xml`/banco: Envio de e-mails, Veículo monitorado importado, Tráfego tempo real, Exportação de imagens, Importação de ConfigEquip pendentes, Processamento automático de infrações, Processamento agendado, Envio de movimentos validados, Busca de remessas pendentes, Exportação de descarga e os jobs do Muralha: e-mail/SMS de **Alertas** e **Ocorrências** (`muralha.digital.notificacao.JobEnvia*`). `RAMJobStore` — agendamentos não persistem entre reinicializações (as tabelas `qrtz_*` existem no banco, porém o JDBC store está comentado).

## 8. Web Services

- **SOAP (Axis 1.4)**: serviços expostos em `/services/*` por `WEB-INF/server-config.wsdd` — `InfoEquipamento`, `StatusEquipamento`, `ConfigEquip`, `ConfigEquipWS`, `CSXEventsWS`, `VeiculoMonitoradoWS`, `Version`, `AdminService`. Clientes Axis para consumo externo em `com.consilux.ws.client`. Usados pelos equipamentos/PCL e pelo app de configuração de equipamentos.
- **REST (Jersey)**: `/rest/*`; hoje praticamente sem recursos ativos (`InfoInfracaoResource` com `@Path` comentado).
- **GWT-RPC**: `/GtwWidgets/*Service`, `/GtwMenu/MenuService` (UI legada).

## 9. Configuração

Dois arquivos XML em `WEB-INF` (não versionados): `confGTW.xml` (lido por `com.consilux.conf.ConfiguracaoXML` via `ConfiguracaoServlet`, load-on-startup 2) e `muralha-digital-config.xml` (lido por `Inicializacao.ObterConfiguracao`, que popula campos `public static` da própria classe `Inicializacao`: `AlertasAtivo`, `emailAtivo`, `smsAtivo`, `AccountSID`, `BitlyToken`, `DirFFMPEG`, `LimiteConsultaAtivo`, `ExigirPlacaCompleta`, …; tags vazias viram `null`). Parâmetros dinâmicos de negócio ficam no banco (`muralha.config_chave_valor`, `config_grupo_permissao`, `dbo.configuracao*`).

## 10. Logs

log4j 1.2 (`log4j.xml`): appenders Console, `RollingFileAppender` debug/info/error em `${catalina.base}/logs/GTW_MURALHA_DIGITAL-{debug,info,error}.log` (1000 KB), `SMTPAppender` para erros e `AsyncAppender`. Em código: `private static final Logger logger = Logger.getLogger(X.class)`; mensagens em português.

## 11. Diagrama de dependências de pacotes (resumido)

```
muralha.digital.*Servlet ──► muralha.digital.<mod>.<Entidade(s)> ──► com.consilux.lib.Conexao ──► SQL Server
        │                              │
        │                              └──► com.consilux.model.* (Usuario, Acesso, Mensagem …)
        └──► com.consilux.infra.* (SessaoConstantes, Funcoes, ServicoEmail …)
muralha.digital._ini.Inicializacao ──► ClienteSessoes ──► Alertas / VeiculoTempoReal / BlitzEletronica
com.consilux.servlet.* (legado) ──► com.consilux.model.* ──► Conexao
```
