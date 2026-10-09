# Entrega — Plano 10: SLA de Pré-processamento 72h

**Data:** 2026-10-08  
**Status:** ✅ Entregue²

## Escopo

Monitoramento do prazo de 72 horas entre a captura de uma passagem e sua pré-classificação. Job Quartz horário grava snapshots de aging; servlet e painel exibem situação atual e histórico.

## Origem

- Plano [`10-sla-preprocessamento.md`](../planos-salvador/10-sla-preprocessamento.md) — SLA de Pré-processamento 72h
- **Requisitos TR:** §5.5.2.1 (prazo máximo 72h por lote de pré-processamento)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §5

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `docs/banco-de-dados/migracoes/20261008_sla_preproc.sql` | CREATE TABLE `muralha.alerta_sla_preproc` para snapshots horários |
| `src/main/java/muralha/digital/sla/SlaPreprocessamentoJob.java` | Job Quartz: calcula aging de registros pendentes e grava snapshot |
| `src/main/java/muralha/digital/sla/SlaPreprocessamentoServlet.java` | Servlet GET com ações `atual` (aging em tempo real + fila por local) e `historico` (snapshots) |
| `src/main/webapp/muralha-digital/pages/monitoramento/sla-preproc/index.jsp` | Painel com cards KPI, doughnut, gráfico de evolução, tabela de fila com badges de status |
| `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` | Registro do Job no agendador Quartz (a cada 60 min) |

## Arquitetura

- **Sem ALTER TABLE**: "pendente" = registro em `veiculo_tempo_real` sem entrada em `vtr_status_analise` (ou status = AGUARDANDO_ANALISE). Aging calculado inline via `DATEDIFF(HOUR, vtr.data, SYSDATETIME())`
- **Job Quartz** (`SlaPreprocessamentoJob`): roda a cada 60 minutos, insere snapshot em `alerta_sla_preproc`
- **Servlet** (`/MuralhaDigital/SlaPreprocessamento`): aging atual com fila por `id_local` + histórico de snapshots
- **JSP**: Bootstrap 5.3, Chart.js 4.4.1, auto-refresh 60s

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Painel carrega com cards e gráficos
- [ ] Job Quartz executa e grava snapshots
- [ ] Tabela de fila ordena por aging máximo
- [ ] Registros acima de 72h destacados em vermelho

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/10-sla-preprocessamento.md`)
