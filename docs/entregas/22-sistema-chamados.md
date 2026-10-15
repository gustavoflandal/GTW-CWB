# Entrega — Plano 22: Sistema de Gerenciamento de Chamados

**Data:** 2026-10-15  
**Status:** Implementado — migração executada no `GTW_MURALHA_DEV` em 2026-10-15 e teste integrado executado em 2026-10-15 (10 de 10 casos passaram)

## Escopo

Módulo de chamados (central de suporte técnico) integrado ao Muralha Digital: abertura com classificação de prioridade, acompanhamento por status com atualização a cada 30 s, histórico integral, relatórios por período/categoria/equipamento/prioridade, avaliação do atendimento, exportação CSV/XLSX/PDF e número de protocolo único com confirmação automática por e-mail. CONTRATANTE (perfis Solicitante e Gestor do contrato) e CONTRATADA (perfil Equipe técnica) usam o mesmo sistema. SLA em horas corridas 24x7, com pausa enquanto o chamado aguarda o solicitante.

**Complemento (2026-10-15)**, pedido pelo responsável após a primeira entrega:

- Perfil `ADMIN`, mapeado do grupo em `chamado_grupo_admin` (padrão 11, Administradores). A tela e a API de Configuração exigem `ADMIN` (a equipe deixa de configurar); o administrador enxerga todos os chamados em modo somente leitura, a menos que também tenha outro perfil.
- Aba "Parâmetros de SLA": percentual de risco (`chamado_sla_risco_percentual`, 1–99) e dias para o fechamento automático (`chamado_fechamento_auto_dias`, 1–60), com histórico em `muralha.config_chave_valor_hist`.
- Aba "Perfis de acesso": o administrador inclui ou remove usuários dos grupos Solicitante, Gestor e Equipe (mesma tabela `dbo.sis_usuario_grupo` da tela legada de usuários; os grupos 107–109 passam a ter `id_grupo_pai = 11`).
- Categoria "Sistema de Chamados" na tela inicial (`sis_menu` 383, `sis_menu_infos` 49), com os cards Chamados, Relatórios e Configuração dentro dela e ícones SVG próprios no estilo dos menus.
- Todas as alterações (ações nos chamados, exportações, parâmetros e perfis) registradas pelo motor de logs (`AuditoriaService` → `dbo.sis_log_auditoria`, funcionalidade "Chamado").
- Aba "E-mail (SMTP)" (Tarefa 19): o administrador define servidor, porta, usuário, senha, remetente, STARTTLS e o liga/desliga dos e-mails dos chamados, e envia um e-mail de teste para si. A senha é gravada cifrada (AES-GCM do JDK) com a chave da variável de ambiente `GTW_CHAMADO_CHAVE_SMTP` e nunca volta para a tela; com o servidor em branco, os chamados continuam usando o `confGTW.xml`. Auditoria `SALVAR_SMTP` e `TESTAR_SMTP`.

Os scripts SQL foram executados no banco de desenvolvimento e o roteiro integrado foi executado por HTTP com usuários de teste criados e removidos para isso (ver [Execução de 2026-10-15](../testes/22-sistema-chamados.md#execução-de-2026-10-15)). As verificações visuais no navegador (polling, gráficos, PDF gerado no cliente) não fizeram parte dessa execução.

## Origem

- Plano [`22-sistema-chamados.md`](../planos-salvador/22-sistema-chamados.md) — Sistema de Gerenciamento de Chamados
- **Spec:** [`2026-10-15-sistema-chamados-design.md`](../superpowers/specs/2026-10-15-sistema-chamados-design.md)
- **Requisitos do edital:** itens 7.2.3 (a–f) e 7.2.4 da aba "Sist. Chamado" de [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx)

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados.sql` | Migração idempotente: tabelas `muralha.chamado_prioridade`, `chamado_categoria`, `chamado`, `chamado_historico`, `chamado_anexo`, `chamado_notificacao` e `chamado_protocolo` |
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql` | Seed: quatro prioridades, categorias/subcategorias, três grupos em `dbo.sis_grupo`, chaves `chamado_*` em `config_chave_valor` e menus 379–382 (`sis_menu`, `sis_menu_infos`, `sis_menu_direitos`) |
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados_admin.sql` | Complemento: chave `chamado_grupo_admin`, grupos 107–109 abaixo do grupo 11, categoria 383/49, cards 46–48 dentro da categoria e direito do menu 382 só para o administrador |
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados_smtp.sql` | Complemento (Tarefa 19): chaves `chamado_smtp_*` vazias em `config_chave_valor` (vazio = SMTP do `confGTW.xml`) |
| `docs/banco-de-dados/migracoes/20261015_usuarios_teste_chamados.sql` | Usuários de teste do banco de desenvolvimento (administrador e solicitante), com a seção de limpeza no cabeçalho |
| `src/main/webapp/muralha-digital/assets/images/menu-icons/chamados*.svg` | Ícones da categoria e dos cards Chamados, Relatórios e Configuração |
| `src/main/java/muralha/digital/chamado/` | Pacote com 28 classes: regras puras (`StatusChamado`, `AcaoChamado`, `PerfilChamado`, `MaquinaEstados`, `CalculadoraSla`, `GeradorProtocolo`), `ArmazenamentoAnexo`, `ExportacaoChamados`, `EmailChamado` (envio JavaMail próprio), `CifraSegredo` e `ConfiguracaoSmtpChamado` (SMTP dos chamados), DAOs, `ChamadoService`, três servlets e dois jobs |
| `src/main/java/muralha/digital/chamado/ChamadoServlet.java` | `/MuralhaDigital/Chamado` — perfil, listagem, detalhe, opções, anexo e ações do chamado |
| `src/main/java/muralha/digital/chamado/ChamadoRelatorioServlet.java` | `/MuralhaDigital/ChamadoRelatorio` — indicadores e exportação |
| `src/main/java/muralha/digital/chamado/ChamadoConfigServlet.java` | `/MuralhaDigital/ChamadoConfig` — restrito ao `ADMIN`: CRUD de prioridades e categorias, parâmetros de SLA, perfis de acesso e SMTP dos chamados (`smtp`, `salvarSmtp`, `testarSmtp`) |
| `src/main/java/muralha/digital/chamado/ChamadoSlaJob.java` | Job Quartz (5 min): marcas de SLA e fechamento automático |
| `src/main/java/muralha/digital/chamado/ChamadoEmailJob.java` | Job Quartz (1 min): envia a fila `chamado_notificacao` |
| `src/main/webapp/muralha-digital/pages/chamados/index.jsp` | Lista, filtros, exportação e modal "Novo chamado" |
| `src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp` | Detalhe, linha do tempo e ações por perfil e status |
| `src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp` | Indicadores com Chart.js, tabela e exportação |
| `src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp` | Abas Parâmetros de SLA, Perfis de acesso, Prioridades e prazos, Categorias e E-mail (SMTP) (administrador) |
| `src/main/webapp/muralha-digital/assets/js/chamados/` | Scripts das telas (`chamados-comum.js`, `-lista`, `-detalhe`, `-relatorios`, `-configuracao`) |
| `src/main/java/muralha/digital/acessos/ReferenciaEdital.java` | Itens 7.2.3 e 7.2.4 para os menus 379–382 e a categoria 383 |
| `src/test/java/muralha/digital/chamado/` | Testes TestNG das regras do módulo |
| `docs/testes/22-sistema-chamados.md` | Roteiro de testes manuais e registro da execução de 2026-10-15 |

Também foi alterado `Agendador.java` (registro dos dois jobs). O método `enviarEmailChamado` acrescentado a `ServicoEmailMuralha.java` na primeira entrega foi removido na Tarefa 19 (o envio passou para `EmailChamado`), e o arquivo voltou ao estado anterior ao plano. `docs/requisitos/07-configuracao-e-segredos.md` documenta a variável `GTW_CHAMADO_CHAVE_SMTP`.

## Arquitetura

- **Perfis:** `SOLICITANTE`, `GESTOR`, `EQUIPE` e `ADMIN`, resolvidos no servidor a partir de `dbo.sis_usuario_grupo` (IDs dos grupos em `config_chave_valor`), com união das permissões. Solicitante vê só os próprios chamados; protocolo alheio responde "não encontrado". `ADMIN` configura o módulo e vê todos os chamados sem ações sobre eles.
- **Máquina de estados** pura (`MaquinaEstados`): `ABERTO`, `EM_ATENDIMENTO`, `AGUARDANDO_SOLICITANTE`, `RESOLVIDO`, `FECHADO`, `CANCELADO`; toda transição exige observação e grava histórico e auditoria.
- **Transação por ação** em `ChamadoService`, com controle otimista (`UPDATE … WHERE status e dt_atualizacao`); falha ao gravar anexo desfaz a operação e remove os arquivos já gravados.
- **SLA 24x7** (`CalculadoraSla`): prazos por prioridade, pausa em `AGUARDANDO_SOLICITANTE` e `RESOLVIDO`, recálculo na reclassificação, marcas `NO_PRAZO` → `EM_RISCO` → `VENCIDO` e `CUMPRIDO`/`VIOLADO`. Prazos iniciais (atendimento/resolução): Crítica 1 h/4 h, Alta 2 h/8 h, Média 4 h/24 h, Baixa 8 h/72 h, todos configuráveis.
- **Protocolo** `CH-AAAA-NNNNNN` gerado na transação da abertura a partir de `muralha.chamado_protocolo`, com contador reiniciado a cada ano.
- **Anexos:** PDF/PNG/JPG validados pela assinatura do conteúdo, gravados fora do webapp com nome aleatório e SHA-256, baixados só por `acao=anexo` com checagem de visibilidade.
- **E-mail:** fila `chamado_notificacao` consumida pelo `ChamadoEmailJob` (até 5 tentativas); apenas confirmação de abertura e reclassificação. Usuário sem e-mail gera `ERRO` imediato sem impedir a abertura. O SMTP vem de `ConfiguracaoSmtpChamado.resolver`: chaves `chamado_smtp_*` quando o servidor está preenchido (senha decifrada com `GTW_CHAMADO_CHAVE_SMTP`), senão o primeiro servidor do `confGTW.xml`; tempo-limite de conexão e leitura de 10 s; o erro gravado na fila traz a causa (ex.: host desconhecido, credencial recusada).
- **Exportação:** CSV (UTF-8 com BOM) e XLSX no servidor com SHA-256 do bloco de dados e proteção contra injeção de fórmula; PDF no navegador (jsPDF) a partir de `formato=json`. Toda exportação é auditada e respeita a visibilidade do perfil.
- **Tempo real:** polling de 30 s nas telas de lista e detalhe.
- **Desvio da spec (§3.3):** a negação por perfil responde HTTP 200 com `{ok:false}`, em vez de 403 previsto na spec; o front trata a mensagem.

## Critérios de aceite

Verificados:

- [x] Build compila sem erros
- [x] 123 testes TestNG passando (módulo `chamado`, inclusive `CifraSegredoTest` e `ConfiguracaoSmtpChamadoTest`, + 5 de `ReferenciaEditalTest`)
- [x] Menus 379–382 e categoria 383 com referência aos itens do edital em `ReferenciaEdital`
- [x] Migrações `20261015_sistema_chamados.sql`, `_seed.sql`, `_admin.sql` e `_smtp.sql` executadas no `GTW_MURALHA_DEV`
- [x] SMTP dos chamados pelo administrador (Tarefa 19, teste por HTTP em 2026-10-15): senha gravada como `v1:…`, nunca devolvida pelo GET; e-mail de teste com host inválido devolve erro legível; sem a chave no servidor, senha nova recusada; configuração devolvida ao `confGTW.xml` ao final ([caso T20](../testes/22-sistema-chamados.md#t20--e-mail-smtp-dos-chamados-complemento))

Teste integrado por HTTP em 2026-10-15 ([execução](../testes/22-sistema-chamados.md#execução-de-2026-10-15)):

- [x] 7.2.3.a — Abertura de chamado com classificação de prioridade (protocolo, prazos +1 h/+4 h para Crítica, anexo validado pela assinatura)
- [ ] 7.2.3.b — Acompanhamento do status em tempo real: status e ações conferidos pela API; o polling de 30 s no navegador não foi verificado
- [x] 7.2.3.c — Registro e consulta do histórico integral (abertura, transições, resposta, reclassificação e avaliação)
- [x] 7.2.3.d — Relatórios por período, categoria, equipamento e prioridade (`indicadores` com `mes`, `semana`, `categoria`, `equipamento` e `prioridade`)
- [x] 7.2.3.e — Avaliação da qualidade do atendimento (nota 4 → `FECHADO`)
- [ ] 7.2.3.f — Exportação: CSV e XLSX baixados com o mesmo SHA-256 do `formato=json`; o PDF montado no navegador (jsPDF) não foi verificado
- [ ] 7.2.4 — Protocolo único com confirmação na tela (resposta da abertura) e notificação enfileirada; o e-mail não saiu porque o SMTP local recusa a autenticação
- [x] Jobs `ChamadoSlaJob` e `ChamadoEmailJob` registrados e executando (fila de e-mail processada a cada minuto até `ERRO`)
- [x] Complemento: configuração só para o administrador, parâmetros de SLA, perfis de acesso, categoria e ícones, motor de logs
- [ ] Roteiro [`22-sistema-chamados.md`](../testes/22-sistema-chamados.md) executado com os quatro perfis: executado com Administrador, Equipe e Solicitante; o perfil Gestor e as telas no navegador ficaram para a homologação

## Pendencias

- Em outros ambientes (homologação/produção), executar `20261015_sistema_chamados.sql`, `20261015_sistema_chamados_seed.sql`, `20261015_sistema_chamados_admin.sql` e `20261015_sistema_chamados_smtp.sql`, nessa ordem, com `sqlcmd -b -f 65001` (UTF-8 para preservar os acentos; `-b` para o `sqlcmd` encerrar com erro se uma verificação falhar). Exige autorização do responsável. `20261015_usuarios_teste_chamados.sql` é só para desenvolvimento.
- Verificar no navegador o polling de 30 s, os gráficos dos relatórios, o PDF gerado no cliente e o fluxo com o perfil Gestor.
- Configurar um SMTP válido (na aba "E-mail (SMTP)" ou no `confGTW.xml`) para confirmar o envio real dos e-mails; no ambiente local o servidor do `confGTW.xml` recusa a autenticação (`535 5.7.8 BadCredentials`, confirmado de novo pelo "Enviar e-mail de teste" em 2026-10-15). Para gravar a senha pela tela, definir `GTW_CHAMADO_CHAVE_SMTP` no serviço do Tomcat ([07 — Configuração e segredos](../requisitos/07-configuracao-e-segredos.md)).
- O nome de alguns equipamentos em `local_vigente` começa com espaço e aparece assim na exportação e no agrupamento por equipamento (dado de origem; o código aplica só `RTRIM`).
- Os testes automatizados foram rodados direto pelo TestNG, porque o provider `surefire-testng` não está no cache Maven offline; `mvn test` com `-Dtest` não funciona neste ambiente.
- O Capítulo 7 do TR não está no repositório: os prazos por prioridade e as categorias são os sugeridos na spec e podem ser ajustados pela tela de configuração.
- Canais de abertura por e-mail (7.2.2.c) e por aplicativo de mensagens (7.2.2.d) ficaram fora do escopo; a coluna `origem` está preparada.
- Simplificação do polling: a lista recarrega a página atual a cada 30 s, sem o parâmetro `desde` previsto na spec.
- Itens menores conhecidos: o seed usa `MAX(id_grupo)+1` sem `ISNULL` (falha apenas se `sis_grupo` estiver vazia) e fixa o grupo 11 (Administradores) nos direitos; mensagens de erro do servidor são exibidas como HTML em alguns `Swal.fire` do front.
