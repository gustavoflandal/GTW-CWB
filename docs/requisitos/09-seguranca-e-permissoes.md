# 09 — Segurança, Autenticação e Permissões

## 1. Modelo de dados de acesso

```
sis_usuario (42) ──< sis_usuario_grupo (203) >── sis_grupo (35, hierárquico por id_grupo_pai; flag config_muralha)
                                                      │
sis_menu (245: id_menu, descricao, acao, tipo 'U', nivel, id_pai_menu, menu, Ativo, nome_sistema 'GTW'|'Muralha-Digital')
        ▲ id_menu                                      │
sis_menu_direitos (id_menu_direito, id_menu, id_grupo, id_usuario)   ← direito por GRUPO **ou** por USUÁRIO
        ▲
sis_menu_infos / sis_menu_relatorio_infos / sis_menu_graficos_infos  ← o que aparece nos menus (href, ordenação, menu_pai)
```
- Contagens do DEV em 2026-10-20.
- `sis_menu.acao` guarda a **URL/ação protegida** (ex.: `/muralha-digital/pages/guarnicao/listagem-guarnicoes.jsp`), sem query string. Há funcionalidades **sem card** (sem linha em `sis_menu_infos`): servlets chamados pelas telas novas (ex.: 428 `/MuralhaDigital/Lote`, 429 `/MuralhaDigital/Kpi`, 422–427 `/api/dash/*`), JSPs encaminhadas por servlet (397–399) e recursos dentro de uma tela (396 `/MuralhaDigital/SsoAnalise/abrir-superset`, botão "Abrir no Superset"); cadastradas por `docs/banco-de-dados/migracoes/20261019_registro_modulos_novos.sql` e `20261019_analise_permissao_abrir_superset.sql`. Pais são linhas com `acao` vazia (ex.: "Processamentos", "Monitoramento", "Anel de Segurança" = raiz das telas Muralha).
- Dados reais (grupos, funcionalidades, menus): `../referencia/menus-e-permissoes.md`. Grupos do Muralha: Administradores (11 — sem acesso implícito: precisa de linha em `sis_menu_direitos` como os demais), Anel de Segurança (41), Supervisores (42), Agente Central Atendimento (43), Agente de Guarnição (44), App Alerta Furtado (47), App Alerta Monitorado (48), PM (38), Monitoramento (40), Consilux (39), Esperando permissão (46)…
- Permissões finas do Muralha (quem enxerga quais tipos de alerta/ocorrência, notificações): `muralha.config_grupo_permissao` (+ histórico) e `registro_fato_usuario_grupo` (visibilidade de registros de fato).

## 2. Fluxo de verificação

### 2.1 Legado — `com.consilux.model.Acesso` (GTW clássico, servlets Muralha antigos, cabeçalho das JSPs)
1. JSP/servlet instancia `new Acesso(request, response, gravaLog)` → lê `session["[usuario]"]`.
2. `verificaAcesso(gotoLogin)`: sem usuário → redireciona a `/login/login.jsp?p=<retUrl>` (ou responde "Usuário não autenticado!"); com usuário → `UsuarioServlet.verificaAcesso(url, usuario)` → `SELECT cnt, acesso FROM fcn_VerificaAcesso(?, ?)`.
3. Resultado: **`cnt == 0` (URL não cadastrada em `sis_menu.acao`) ⇒ acesso LIBERADO**; `cnt > 0` ⇒ exige que o usuário tenha direito (direto ou via grupo). Consequência prática: *para proteger uma tela nova é obrigatório cadastrá-la em `sis_menu`*.

### 2.2 Módulos novos — `muralha.digital.acessos.ControleAcesso` (verificação estrita)
Usado pelos servlets dos módulos novos (lista e direito de cada um em `../referencia/servlets-e-endpoints.md` §3). Consulta a mesma `fcn_VerificaAcesso`, mas **só libera com `cnt > 0` e `acesso > 0`**: URL não cadastrada, sem direito ou erro na consulta ⇒ **negado** (*fail-closed*).
- `exigir(req, resp)` — direito ao próprio servlet (`getServletPath()`); `exigir(req, resp, acao)` — direito a outra funcionalidade (ex.: `LoteServlet` exige o da tela de Lotes, 370; `KpiServlet` exige também o da Configuração de KPIs, 372, para `listarConfig` e gravação). Sem sessão ⇒ **401**; sem direito ⇒ **403**; ambos em JSON `{"ok":false,"erro":"…"}`, que a tela mostra.
- `exigirTela(req, resp)` — servlet chamado sem `acao` que encaminha para JSP (`TipoAlertaConfig`, `PreProcessamento`, `RelatorioGerencial`): sem sessão redireciona a `/login/login.jsp`, sem direito a `/login/gtw_principal.jsp`.
- `exigirSessao(req, resp)` — só sessão, para ação usada por qualquer operador logado (`TipoAlertaConfig?acao=aparencia`, lida pelo popup de notificação).
- `possuiDireito(acao, idUsuario)` — teste sem resposta HTTP, para liberar recurso dentro da tela (botão "Abrir no Superset" exige o 396; a tela de Análises em si, 395, segue no `Acesso` legado).
- Como no legado, grava o log de acesso também quando nega; a negação é registrada ainda na auditoria (`AuditoriaService`, ação `negado`).
- Fora do `ControleAcesso` por decisão: `/MuralhaDigital/MapaConfig` (usado por telas de mapa legadas), `MfaConfig` e `/api/dash/health` (todos os usuários) e o Sistema de Chamados (`Acesso` legado + perfis próprios do módulo).

### 2.3 Sessão
1. `ValidaSessao` (filtro global) revalida a cada 5 min se o usuário segue ativo.
2. Sessão: timeout configurável (`ConfiguracaoInatividade`); "manter conectado" desliga o timeout. Logon/logoff auditados (`LogonLogoff`, `sis_log`, relatório de sessão).

## 3. Autenticação
| Via | Detalhe |
|---|---|
| Usuário/senha | `login_action.jsp`; usuário `[A-Z0-9.]{2,30}`, senha imprimível ≤ 20; **senha armazenada como MD5 sem sal** (`Funcoes.geraMD5`); o hash não vai para o log nem para a resposta JSON do login (`UsuarioServlet`, desde 2026-10-19) |
| Google OAuth | `/callback` (`LoginGoogle`) — e-mail precisa existir em `sis_usuario` |
| Token (apps/integração) | `spu_ppv_sis_usuario_token_valida/encerra`, `acessoViaToken`; tabela `sis_usuario_token` |
| Recuperação de senha | e-mail/SMS com código (`sis_usuario_recupera_senha`, `RecuperacaoSenha`, `codigo_verificacao`) |
| Troca obrigatória | `usuario.isAlterarSenha()` → `/login/login_change.jsp` |
| Web services SOAP | `/services/*` sem autenticação própria evidente — proteger por rede (ver `12-…`) |

## 4. Requisitos para qualquer código novo
- Servlet: verificar sessão **e** funcionalidade com `ControleAcesso` (§2.2 e `06-padroes-de-codigo.md` §1.1), e cadastrar o servlet em `sis_menu`/`sis_menu_direitos` — sem cadastro ele nega a todos. Nunca confiar em `idUsuario` vindo do cliente; usar o da sessão.
- Dados por escopo: aplicar o grupo/escopo do usuário nas consultas (ex.: `registro_fato_usuario_grupo`, equipamentos permitidos, `privado`).
- Consultas de veículos/relatórios: exigir e registrar **motivo da solicitação** (`motivo_solicitacao_relatorio`) e logar quem consultou (auditoria exigida por LGPD/órgão).
- Saída: escapar HTML (JSP: `<c:out>`; JS: `.text()`), JSON sempre via Gson. Entrada: `PreparedStatement`, validação de tamanho/formato.
- Upload/download: validar tipo, tamanho, nome; caminhos fixos; checar autorização por registro.
- Segredos: `07-configuracao-e-segredos.md`. Logs: sem dados sensíveis.
- Dados pessoais (placa vinculada a pessoa, CPF, antecedentes, imagens): tratar como sensíveis — não expor em URLs (GET), não logar completos, limitar exportação.

## 5. Como cadastrar uma funcionalidade protegida (resumo; passo a passo em `10-…` §4)
```sql
-- 1) funcionalidade
INSERT INTO dbo.sis_menu (descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
VALUES ('Minha Tela', '/muralha-digital/pages/minha-tela/minha-tela.jsp', 'U', 1, 222, 'Minha Tela', 1, 'Muralha-Digital');
-- 2) direito ao grupo
INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (SCOPE_IDENTITY(), 42);   -- repetir para o 11 (Administradores) e demais grupos
-- 3) servlet chamado pela tela (funcionalidade sem card, mesmos grupos): repetir 1) e 2) com acao = '/MuralhaDigital/MinhaEntidade'
-- 4) item de menu (se aparecer na navbar)
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai) VALUES (...);
```
(222 = nó pai "Anel de Segurança" (`nome_sistema='Muralha-Digital'`) em `sis_menu`; o grupo 42 é "Anel de Segurança - Supervisores". Confirmar IDs em `../referencia/menus-e-permissoes.md`. Executar **somente com aprovação**, em DEV.)
