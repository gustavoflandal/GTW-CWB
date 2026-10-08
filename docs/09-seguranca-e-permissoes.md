# 09 — Segurança, Autenticação e Permissões

## 1. Modelo de dados de acesso

```
sis_usuario (37) ──< sis_usuario_grupo (192) >── sis_grupo (27, hierárquico por id_grupo_pai; flag config_muralha)
                                                      │
sis_menu (172: id_menu, descricao, acao, tipo 'U', nivel, id_pai_menu, menu, Ativo, nome_sistema 'GTW'|'Muralha-Digital')
        ▲ id_menu                                      │
sis_menu_direitos (id_menu_direito, id_menu, id_grupo, id_usuario)   ← direito por GRUPO **ou** por USUÁRIO
        ▲
sis_menu_infos / sis_menu_relatorio_infos / sis_menu_graficos_infos  ← o que aparece nos menus (href, ordenação, menu_pai)
```
- `sis_menu.acao` guarda a **URL/ação protegida** (ex.: `/muralha-digital/pages/guarnicao/listagem-guarnicoes.jsp`). Pais são linhas com `acao` vazia (ex.: "Processamentos", "Monitoramento", "Anel de Segurança" = raiz das telas Muralha).
- Dados reais (grupos, funcionalidades, menus): `referencia/menus-e-permissoes.md`. Grupos do Muralha: Anel de Segurança (41), Supervisores (42), Agente Central Atendimento (43), Agente de Guarnição (44), App Alerta Furtado (47), App Alerta Monitorado (48), PM (38), Monitoramento (40), Consilux (39), Esperando permissão (46)…
- Permissões finas do Muralha (quem enxerga quais tipos de alerta/ocorrência, notificações): `muralha.config_grupo_permissao` (+ histórico) e `registro_fato_usuario_grupo` (visibilidade de registros de fato).

## 2. Fluxo de verificação
1. JSP/servlet instancia `new Acesso(request, response, gravaLog)` → lê `session["[usuario]"]`.
2. `verificaAcesso(gotoLogin)`: sem usuário → redireciona a `/login/login.jsp?p=<retUrl>` (ou responde "Usuário não autenticado!"); com usuário → `UsuarioServlet.verificaAcesso(url, usuario)` → `SELECT cnt, acesso FROM fcn_VerificaAcesso(?, ?)`.
3. Resultado: **`cnt == 0` (URL não cadastrada em `sis_menu.acao`) ⇒ acesso LIBERADO**; `cnt > 0` ⇒ exige que o usuário tenha direito (direto ou via grupo). Consequência prática: *para proteger uma tela nova é obrigatório cadastrá-la em `sis_menu`*.
4. `ValidaSessao` (filtro global) revalida a cada 5 min se o usuário segue ativo.
5. Sessão: timeout configurável (`ConfiguracaoInatividade`); "manter conectado" desliga o timeout. Logon/logoff auditados (`LogonLogoff`, `sis_log`, relatório de sessão).

## 3. Autenticação
| Via | Detalhe |
|---|---|
| Usuário/senha | `login_action.jsp`; usuário `[A-Z0-9.]{2,30}`, senha imprimível ≤ 20; **senha armazenada como MD5 sem sal** (`Funcoes.geraMD5`) |
| Google OAuth | `/callback` (`LoginGoogle`) — e-mail precisa existir em `sis_usuario` |
| Token (apps/integração) | `spu_ppv_sis_usuario_token_valida/encerra`, `acessoViaToken`; tabela `sis_usuario_token` |
| Recuperação de senha | e-mail/SMS com código (`sis_usuario_recupera_senha`, `RecuperacaoSenha`, `codigo_verificacao`) |
| Troca obrigatória | `usuario.isAlterarSenha()` → `/login/login_change.jsp` |
| Web services SOAP | `/services/*` sem autenticação própria evidente — proteger por rede (ver `12-…`) |

## 4. Requisitos para qualquer código novo
- Servlet: verificar sessão **e** funcionalidade (`06-padroes-de-codigo.md` §1.1). Nunca confiar em `idUsuario` vindo do cliente; usar o da sessão.
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
INSERT INTO dbo.sis_menu_direitos (id_menu, id_grupo) VALUES (SCOPE_IDENTITY(), 42);
-- 3) item de menu (se aparecer na navbar)
INSERT INTO dbo.sis_menu_infos (id_menu, descricao, href, ordenacao, menu_pai) VALUES (...);
```
(222 = nó pai "Anel de Segurança" (`nome_sistema='Muralha-Digital'`) em `sis_menu`; o grupo 42 é "Anel de Segurança - Supervisores". Confirmar IDs em `referencia/menus-e-permissoes.md`. Executar **somente com aprovação**, em DEV.)
