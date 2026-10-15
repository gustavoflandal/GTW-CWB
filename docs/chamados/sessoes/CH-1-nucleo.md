# SESSAO CH-1 — Núcleo do Sistema de Chamados

## Antes de qualquer código
1. Ler `CLAUDE.md`, [`sistema-chamados-arquitetura.md`](../sistema-chamados-arquitetura.md) (decisões D1–D14) e o plano.
2. Confirmar a stack: Java 13, Servlet puro, JDBC, Quartz, JavaMail, Apache POI (todos já no `pom.xml`). Se algo divergir, parar e reportar.

## Objetivo
Entregar o módulo `muralha.digital.chamado` completo, integrado à Muralha Digital, atendendo os itens 7.2.3 (a–f) e 7.2.4: abertura com prioridade, acompanhamento, histórico, relatórios, avaliação, exportação e protocolo único com confirmação por e-mail.

## Tarefas do plano
Detalhamento em [`22-sistema-chamados.md`](../../planos-salvador/22-sistema-chamados.md) (Tarefas 1 a 13; o código de cada passo está lá e não é repetido aqui).

| Tarefa | Tema |
|---|---|
| 1 | Migração SQL (estrutura e seed): tabelas `muralha.chamado*`, prioridades, categorias, parâmetros, três grupos de perfil e menus 379–382 |
| 2 | Domínio: estados, ações, perfis e `MaquinaEstados` (pura) |
| 3 | `CalculadoraSla` (24x7, pausa, reclassificação, marcas) e `GeradorProtocolo` (`CH-AAAA-NNNNNN`) |
| 4 | `ArmazenamentoAnexo` (extensão, assinatura, tamanho, sem *path traversal*) |
| 5 | `ExportacaoChamados` (CSV/XLSX com SHA-256) e `EmailChamado` |
| 6 | Modelos, `FiltroChamados` e DAOs (`ChamadoDao`, `ChamadoConfigDao`) |
| 7 | `ChamadoService`: uma transação por ação, controle otimista, histórico, notificação e auditoria |
| 8 | Servlets `Chamado`, `ChamadoRelatorio` e `ChamadoConfig` |
| 9 | Jobs Quartz `ChamadoSlaJob` e `ChamadoEmailJob` e registro no `Agendador` |
| 10 | Tela de lista e abertura (`index.jsp`) |
| 11 | Tela de detalhe e ações (`detalhe.jsp`) |
| 12 | Relatórios e configuração (`relatorios.jsp`, `configuracao.jsp`) |
| 13 | `ReferenciaEdital` (menus 379–382), roteiro de testes e documentação de entrega |

## Escopo
- Perfis `SOLICITANTE`, `GESTOR` e `EQUIPE` por grupo (IDs em `config_chave_valor`); visibilidade aplicada no SQL.
- Ciclo de vida de seis estados com observação obrigatória, histórico e auditoria.
- Anexos fora do webapp, e-mails somente de confirmação e reclassificação, polling de 30 s.
- Fora do escopo: abertura por e-mail ou mensageria (7.2.2.c/d), comentários internos, chamado automático por indisponibilidade, WebSocket.

## Arquivos
Pacote `src/main/java/muralha/digital/chamado/` (regras puras, DAOs, serviço, servlets, jobs); telas em `src/main/webapp/muralha-digital/pages/chamados/` e scripts em `assets/js/chamados/`; migrações `20261015_sistema_chamados.sql` e `_seed.sql` em `docs/banco-de-dados/migracoes/`; testes em `src/test/java/muralha/digital/chamado/`; `docs/testes/22-sistema-chamados.md` e `docs/entregas/22-sistema-chamados.md`. A lista completa por tarefa está na seção "Arquivos" do plano.

## Critérios de aceite
- Build `BUILD SUCCESS` com JDK 13 e sem alteração em `pom.xml` ou `.setup-gtw/`.
- Testes das regras (máquina de estados, SLA, protocolo, anexos, exportação, e-mail, filtros, cadastros e regras do serviço) passando.
- Toda transição grava histórico e auditoria; solicitante só acessa os próprios chamados; protocolo alheio responde "não encontrado".
- Exportações CSV/XLSX com o mesmo SHA-256 para o mesmo filtro; PDF montado no cliente.
- Itens 7.2.3 e 7.2.4 referenciados nos menus 379–382 (`ReferenciaEdital`).
- Roteiro de testes manuais e documento de entrega com seção "## Origem".

## Proibições
- Alterar `pom.xml`, `.setup-gtw/` ou adicionar dependências.
- Executar DDL/DML fora dos scripts versionados; guardar segredos no repositório.
- SQL concatenado com entrada do usuário; `innerHTML` com texto do servidor sem escape.

## Resultado
Ver [`SESSAO-CH-1-relatorio.md`](../relatorios/SESSAO-CH-1-relatorio.md).
