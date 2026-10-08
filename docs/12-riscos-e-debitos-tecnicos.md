# 12 — Riscos e Débitos Técnicos Conhecidos

> Levantamento de 2026-10-08 a partir de leitura de código e do banco. Prioridade: **A** (agir/avaliar já), **M** (planejar), **B** (oportunista). Nada aqui foi corrigido automaticamente — são itens para decisão do responsável.

## 1. Segurança

| # | Item | Prio | Evidência | Recomendação |
|---|---|---|---|---|
| S1 | **Credenciais já expostas em arquivos do projeto** (Twilio, SendGrid, Google OAuth, Bitly, Comtele, SmsDev, Facilita Móvel, senha do banco, senha de e-mail, chave Google Maps) | A | Estavam em `muralha-digital-config.xml`, `confGTW.xml`, `LoginGoogle`, `ServicoEmailMuralha`, `ServicoSMS`, `confGTW-teste.xml` (agora removidos do repositório GitHub, mas existem nas máquinas) | **Revogar/rotacionar todas**; manter apenas em config local/ambiente |
| S2 | `ValidaSessao` não bloqueia requisições sem sessão | A | `session == null` segue para `chain.doFilter` | Redirecionar/401 quando não há sessão em rotas não públicas; revisar lista `paginaPublica` (`contains("assets/")`, `contains("/login/")` são amplos) |
| S3 | 11 servlets Muralha sem verificação de sessão/permissão | A | `GuarnicaoServlet`, `NotificacaoServlet`, `PerfilComportamentalServlet`, `PesquisaRapidaAutocompleteServlet`, `CorrecaoPlacaServlet`, `AssinaturaImagemServlet`, `RelatorioAuditoriaServlet`, `RelatorioAcoesAlarmesServlet`, `RelatorioSessaoUsuarioServlet`, `RelatoriosExportarImagensServlet`, `RelatorioPlacasVeicularesServlet` | Adicionar o padrão de `06` §1.1 |
| S4 | `fcn_VerificaAcesso`: URL não cadastrada = liberada (fail-open) | A | `cnt == 0 → acesso` | Mudar para fail-closed ou garantir cadastro de todas as rotas; auditar `sis_menu` |
| S5 | Senhas com **MD5 sem sal** | A | `Funcoes.geraMD5`, `Usuario.comparaSenha` | Migrar para PBKDF2/bcrypt/Argon2 com re-hash no login; sem alterar a stack (JDK tem PBKDF2) |
| S6 | Sem proteção **CSRF**; POST de escrita só por cookie de sessão | M | Nenhum token/`SameSite` | Token por sessão + header; cookies `HttpOnly`/`Secure`/`SameSite` (hoje `JSESSIONID_HTTP` sem `Secure` quando HTTP) |
| S7 | Web services SOAP (`/services/*`, `AdminServlet`, `/servlet/AdminServlet`) expostos | A | `web.xml` | Restringir por rede/IP (`ip_permitidos.txt`), desabilitar AdminService em produção |
| S8 | `Maven` configurado com `allowInsecure/ssl.allowall` e repositório HTTP | M | `.setup-gtw` | Stack imutável — apenas ciência; não replicar em CI |
| S9 | Possível SQL concatenado e `Statement` | M | `UsuarioServlet`, `Alertas` | Revisão dirigida (`agentes/agente-revisor-seguranca.md`) |
| S10 | XSS: JS monta HTML com dados do servidor sem escape | M | `listagem-guarnicoes.js` (`${guarnicao.nome}`) | Função `esc()`/`.text()` |
| S11 | `REDIRECT_URI` do Google fixo em `localhost:8080` | M | `LoginGoogle` | Parametrizar por ambiente |
| S12 | Chave Google Maps visível no cliente | B | `maps-config.js` | Restringir por referrer/domínio no console Google |

## 2. Confiabilidade e desempenho

| # | Item | Prio | Detalhe |
|---|---|---|---|
| R1 | Pool de 400 conexões, `maxWait 2s`; vazamentos derrubam o sistema | A | Padrão de fechamento irregular (`close` fora de ordem); revisar com try-with-resources |
| R2 | Threads de polling (Alertas 2 s, Tempo real 1 s) executam procedures a cada ciclo mesmo sem clientes? | M | Verificar `ClienteSessoes.ObterQtdeSessoes()` antes de consultar |
| R3 | Tabelas de 8–11 milhões de linhas (`veiculo_tempo_real*`) sem política de retenção | A | Definir particionamento/arquivamento; várias tabelas `_bkp`, `_copiar_dev`, `_test*` no schema |
| R4 | Schema `mobilidade` com ~190 tabelas `_test…_test6` e `_backup` | M | Limpeza com o dono do módulo Waze |
| R5 | Quartz `RAMJobStore`: agendamentos perdidos em reinício; execuções duplicadas se houver 2 instâncias | M | Habilitar JDBC store (tabelas `qrtz_*` já existem) ou usar `controla_execucao_job` |
| R6 | Mensagens WebSocket em XML montado por string | B | Documentar esquema; considerar JSON no futuro |
| R7 | Sem timeouts explícitos nas integrações HTTP (Unirest/HttpClient) | M | Definir connect/read timeouts |

## 3. Manutenibilidade

| # | Item | Prio |
|---|---|---|
| M1 | Fontes do **GWT/GXT não estão no repositório** (apenas saída compilada em `webapp/gxt`, `GtwMenu`, `GtwWidgets`); a UI clássica só pode receber correções via JSP/servlets | A |
| M2 | Duplicação massiva: 3 versões de Bootstrap, dois jQuery, MDB + Bootstrap, FA 4/5/6 | M |
| M3 | Regras de negócio dentro de procedures (388) e funções (302) sem testes; 8 triggers | A |
| M4 | Praticamente sem testes automatizados (6 arquivos em `src/test`) | M |
| M5 | Código morto/experimental: servlet `teste` (`/teste`), tabelas `cad_veiculo_monitorado_c037`, `cad_isento_ant`, `*_bkp*`, páginas `*-original.js`, `cabecalho_antigo.png` | B |
| M6 | Codificação mista (ISO-8859-1 em CSS/JSP antigos vs UTF-8) | B — usar sempre UTF-8 |
| M7 | Dependências antigas: log4j 1.2.17 (EOL), Axis 1.4, GWT 2.1, jQuery 1.10 (freewall), hibernate-validator 4.0.2, commons-dbcp 1.4 | M — **stack imutável**; registrar CVEs conhecidas e mitigar por rede |
| M8 | `pom.xml`: `maven.compiler.*`=14 mas compilador usa 13; Jersey usado em `web.xml` sem dependência declarada visível | B |
| M9 | `Inicializacao` concentra configuração como campos `static` mutáveis | B |
| M10 | `confGTW.xml`/`muralha-digital-config.xml` contêm valores de produção (`E:\Consilux\...`, IPs de câmeras) misturados com DEV | M |

## 4. Dados e conformidade
- Dados pessoais e de localização (placas, imagens, CPF em `blitz_pessoa_envolvida`/`antecedentes_criminais`, `agente_localizacao_hist`) exigem controle de acesso, trilha de auditoria e retenção (LGPD). Existem `sis_log`, `motivo_solicitacao_relatorio`, `relatorio_imagens_exportadas` — validar cobertura.
- `sis_usuario_recupera_senha` e `codigo_verificacao` guardam códigos — garantir expiração.
- Evitar copiar dados do banco para ambientes não controlados (`veiculo_tempo_real_imagem_copiar_dev` indica cópia para DEV).

## 5. Armadilhas operacionais
1. O push ao GitHub é bloqueado pelo *secret scanning* se houver segredo — nunca usar o bypass.
2. `build.ps1` sem `-Dexec.skip=true` falha em `xdelta`.
3. `ffmpeg.exe` (108 MB) não está no git.
4. `target/` e WAR antigos podem confundir; faça `clean`.
5. Páginas fora de `login/` exigem sessão **e** podem exigir cadastro em `sis_menu`; teste com usuário de perfil real.
6. O repositório local `D:\GTW-CWB` (pai) tem um `.git` sem commits e **não** é o repositório do GitHub; o git do projeto é `D:\GTW-CWB\GTW-CWB\.git`.
