# Entrega — Plano 22: Sistema de Gerenciamento de Chamados

**Data:** 2026-10-15  
**Status:** Implementado — aguardando execução da migração e testes integrados

## Escopo

Módulo de chamados (central de suporte técnico) integrado ao Muralha Digital: abertura com classificação de prioridade, acompanhamento por status com atualização a cada 30 s, histórico integral, relatórios por período/categoria/equipamento/prioridade, avaliação do atendimento, exportação CSV/XLSX/PDF e número de protocolo único com confirmação automática por e-mail. CONTRATANTE (perfis Solicitante e Gestor do contrato) e CONTRATADA (perfil Equipe técnica) usam o mesmo sistema. SLA em horas corridas 24x7, com pausa enquanto o chamado aguarda o solicitante.

Os scripts SQL ainda **não foram executados** no banco (aguardam autorização do responsável), por isso as verificações no navegador ficaram adiadas. O código compila e os testes automatizados passam (ver Critérios de aceite).

## Origem

- Plano [`22-sistema-chamados.md`](../planos-salvador/22-sistema-chamados.md) — Sistema de Gerenciamento de Chamados
- **Spec:** [`2026-10-15-sistema-chamados-design.md`](../superpowers/specs/2026-10-15-sistema-chamados-design.md)
- **Requisitos do edital:** itens 7.2.3 (a–f) e 7.2.4 da aba "Sist. Chamado" de [`Atividades Edital Salvador 2026.xlsx`](../../docs-editais/edital-salvador/Atividades%20Edital%20Salvador%202026.xlsx)

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados.sql` | Migração idempotente: tabelas `muralha.chamado_prioridade`, `chamado_categoria`, `chamado`, `chamado_historico`, `chamado_anexo`, `chamado_notificacao` e `chamado_protocolo` |
| `docs/banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql` | Seed: quatro prioridades, categorias/subcategorias, três grupos em `dbo.sis_grupo`, chaves `chamado_*` em `config_chave_valor` e menus 379–382 (`sis_menu`, `sis_menu_infos`, `sis_menu_direitos`) |
| `src/main/java/muralha/digital/chamado/` | Pacote com 26 classes: regras puras (`StatusChamado`, `AcaoChamado`, `PerfilChamado`, `MaquinaEstados`, `CalculadoraSla`, `GeradorProtocolo`), `ArmazenamentoAnexo`, `ExportacaoChamados`, `EmailChamado`, DAOs, `ChamadoService`, três servlets e dois jobs |
| `src/main/java/muralha/digital/chamado/ChamadoServlet.java` | `/MuralhaDigital/Chamado` — perfil, listagem, detalhe, opções, anexo e ações do chamado |
| `src/main/java/muralha/digital/chamado/ChamadoRelatorioServlet.java` | `/MuralhaDigital/ChamadoRelatorio` — indicadores e exportação |
| `src/main/java/muralha/digital/chamado/ChamadoConfigServlet.java` | `/MuralhaDigital/ChamadoConfig` — CRUD de prioridades e categorias |
| `src/main/java/muralha/digital/chamado/ChamadoSlaJob.java` | Job Quartz (5 min): marcas de SLA e fechamento automático |
| `src/main/java/muralha/digital/chamado/ChamadoEmailJob.java` | Job Quartz (1 min): envia a fila `chamado_notificacao` |
| `src/main/webapp/muralha-digital/pages/chamados/index.jsp` | Lista, filtros, exportação e modal "Novo chamado" |
| `src/main/webapp/muralha-digital/pages/chamados/detalhe.jsp` | Detalhe, linha do tempo e ações por perfil e status |
| `src/main/webapp/muralha-digital/pages/chamados/relatorios.jsp` | Indicadores com Chart.js, tabela e exportação |
| `src/main/webapp/muralha-digital/pages/chamados/configuracao.jsp` | Prioridades/prazos e categorias (equipe) |
| `src/main/java/muralha/digital/acessos/ReferenciaEdital.java` | Itens 7.2.3 e 7.2.4 para os menus 379–382 |
| `src/test/java/muralha/digital/chamado/` | Testes TestNG das regras do módulo (76 testes) |
| `docs/testes/22-sistema-chamados.md` | Roteiro de testes manuais |

Também foram alterados `Agendador.java` (registro dos dois jobs) e `ServicoEmailMuralha.java` (método `enviarEmailChamado`).

## Arquitetura

- **Perfis:** `SOLICITANTE`, `GESTOR` e `EQUIPE`, resolvidos no servidor a partir de `dbo.sis_usuario_grupo` (IDs dos grupos em `config_chave_valor`), com união das permissões. Solicitante vê só os próprios chamados; protocolo alheio responde "não encontrado".
- **Máquina de estados** pura (`MaquinaEstados`): `ABERTO`, `EM_ATENDIMENTO`, `AGUARDANDO_SOLICITANTE`, `RESOLVIDO`, `FECHADO`, `CANCELADO`; toda transição exige observação e grava histórico e auditoria.
- **Transação por ação** em `ChamadoService`, com controle otimista (`UPDATE … WHERE status e dt_atualizacao`); falha ao gravar anexo desfaz a operação e remove os arquivos já gravados.
- **SLA 24x7** (`CalculadoraSla`): prazos por prioridade, pausa em `AGUARDANDO_SOLICITANTE` e `RESOLVIDO`, recálculo na reclassificação, marcas `NO_PRAZO` → `EM_RISCO` → `VENCIDO` e `CUMPRIDO`/`VIOLADO`. Prazos iniciais (atendimento/resolução): Crítica 1 h/4 h, Alta 2 h/8 h, Média 4 h/24 h, Baixa 8 h/72 h, todos configuráveis.
- **Protocolo** `CH-AAAA-NNNNNN` gerado na transação da abertura a partir de `muralha.chamado_protocolo`, com contador reiniciado a cada ano.
- **Anexos:** PDF/PNG/JPG validados pela assinatura do conteúdo, gravados fora do webapp com nome aleatório e SHA-256, baixados só por `acao=anexo` com checagem de visibilidade.
- **E-mail:** fila `chamado_notificacao` consumida pelo `ChamadoEmailJob` (até 5 tentativas); apenas confirmação de abertura e reclassificação. Usuário sem e-mail gera `ERRO` imediato sem impedir a abertura.
- **Exportação:** CSV (UTF-8 com BOM) e XLSX no servidor com SHA-256 do bloco de dados e proteção contra injeção de fórmula; PDF no navegador (jsPDF) a partir de `formato=json`. Toda exportação é auditada e respeita a visibilidade do perfil.
- **Tempo real:** polling de 30 s nas telas de lista e detalhe.
- **Desvio da spec (§3.3):** a negação por perfil responde HTTP 200 com `{ok:false}`, em vez de 403 previsto na spec; o front trata a mensagem.

## Critérios de aceite

Verificados:

- [x] Build compila sem erros
- [x] 80 testes TestNG passando (76 do módulo `chamado` + 4 de `ReferenciaEditalTest`)
- [x] Menus 379–382 com referência aos itens do edital em `ReferenciaEdital`

Dependem da migração e do navegador (desmarcados):

- [ ] 7.2.3.a — Abertura de chamado com classificação de prioridade
- [ ] 7.2.3.b — Acompanhamento do status em tempo real (polling de 30 s)
- [ ] 7.2.3.c — Registro e consulta do histórico integral
- [ ] 7.2.3.d — Relatórios por período, categoria e equipamento
- [ ] 7.2.3.e — Avaliação da qualidade do atendimento (nota 1–5 e comentário)
- [ ] 7.2.3.f — Exportação em PDF, CSV e XLS
- [ ] 7.2.4 — Protocolo único com confirmação automática (protocolo, prioridade e prazos) na tela e por e-mail
- [ ] Jobs `ChamadoSlaJob` e `ChamadoEmailJob` registrados e executando
- [ ] Roteiro [`22-sistema-chamados.md`](../testes/22-sistema-chamados.md) executado com os três perfis

## Pendencias

- Executar os scripts `20261015_sistema_chamados.sql` e `20261015_sistema_chamados_seed.sql` com `sqlcmd -b -f 65001` (UTF-8 para preservar os acentos do seed; `-b` para o `sqlcmd` encerrar com erro se uma verificação falhar). Exige autorização do responsável.
- Executar as verificações no navegador adiadas por falta das tabelas (Tarefas 10 a 12) e o roteiro completo de testes.
- Associar os três usuários de teste aos grupos de chamados e criar o diretório `chamado_diretorio_anexos`.
- Os testes automatizados foram rodados direto pelo TestNG, porque o provider `surefire-testng` não está no cache Maven offline; `mvn test` com `-Dtest` não funciona neste ambiente.
- O Capítulo 7 do TR não está no repositório: os prazos por prioridade e as categorias são os sugeridos na spec e podem ser ajustados pela tela de configuração.
- Canais de abertura por e-mail (7.2.2.c) e por aplicativo de mensagens (7.2.2.d) ficaram fora do escopo; a coluna `origem` está preparada.
- Simplificação do polling: a lista recarrega a página atual a cada 30 s, sem o parâmetro `desde` previsto na spec.
- Itens menores conhecidos: o seed usa `MAX(id_grupo)+1` sem `ISNULL` (falha apenas se `sis_grupo` estiver vazia) e fixa o grupo 11 (Administradores) nos direitos; mensagens de erro do servidor são exibidas como HTML em alguns `Swal.fire` do front.
