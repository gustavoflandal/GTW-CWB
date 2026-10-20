# Entrega — Plano 29: KPIs com Histórico, Alertas Analíticos e Relatório Mensal

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — migração executada, aguardando teste no navegador.
> **Fase:** 7 de 11 da correção da auditoria de 2026-10-16.
>
> **Atualização de 2026-10-19** ([Entrega 35](35-correcoes-seguranca-funcionais.md), FUN-01, FUN-02 e FUN-04): o `RelatorioGerencialJob` passou a ler `dbo.sis_log_auditoria` e a usar `desativado = 0` na disponibilidade; o `RelatorioPreProcMensalJob` foi reescrito sobre `infracao_analise` e `vtr_status_analise`; o SQL dos KPIs 5, 7, 8, 9 e 11 foi corrigido por migração.

## Escopo

Histórico periódico de KPIs com snapshots horários, metas parametrizáveis por KPI, 8 indicadores obrigatórios do TR (5.4.6.2 a-h), alertas analíticos automáticos quando KPI em status não-OK, relatório gerencial periódico mensal com resumo, comparação e alertas, relatório mensal consolidado de pré-processamento com 14 métricas (gap vermelho 5.5.3.3), página de visualização com exportação CSV/XLSX, e gráfico de histórico no dashboard de KPIs.

## Origem

- Plano: [`29-kpis-relatorio-mensal.md`](../planos-salvador/29-kpis-relatorio-mensal.md)
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §1/§2 (sessão e acesso), §7 (jobs Quartz), §10 (`AuditoriaService`), §14 (Agendador), §15 (`config_chave_valor`)

## O que mudou

- **KPIs obrigatórios** (5.4.6.2 a-h): 8 novos KPIs em `muralha.kpi_config` — Taxa de Infração por Tráfego, Variação Semanal de Infrações, Disponibilidade Operacional, Aproveitamento de Registros, Efetividade de Fiscalização, Alertas de Congestionamento (7d), Conformidade Operacional, SLA de Latência P95. Queries sobre dados reais do sistema.
- **Metas parametrizáveis** (5.4.6.3-a): tabela complementar `muralha.kpi_config_meta` (FK para `kpi_config`) com `meta`, `meta_minima`, `categoria`, `gerar_alerta`. Seed de metas para todos os 12 KPIs.
- **Histórico periódico** (5.4.6.3-d): tabela `muralha.kpi_historico` com snapshots horários. `KpiSnapshotJob` (Quartz, 60 min) coleta valor e status de cada KPI ativo, com limpeza automática por retenção (default 90 dias).
- **Alertas analíticos** (5.4.3.5-6): quando um KPI muda para WARN ou CRIT, o `KpiSnapshotJob` gera um alerta via `AuditoriaService.registrarSistema`, com cooldown configurável (default 360 min) para evitar spam.
- **Relatório gerencial periódico** (5.4.3.7): `RelatorioGerencialJob` (Quartz, diário, verifica dia configurável) gera relatório mensal em `muralha.relatorio_periodico` com: resumo de KPIs (média, mín, máx, contagem por status), comparação com mês anterior, alertas analíticos do período, volume de tráfego, disponibilidade de equipamentos.
- **Relatório mensal de pré-processamento** (5.5.3.1-3 — gap vermelho): `RelatorioPreProcMensalJob` (Quartz, diário, verifica 5o dia útil) coleta as 14 métricas de 5.5.3.1 a-n: volumes (recebidos, processados, disponibilizados, descartados), aproveitamento, tempo médio, produtividade por operador, divergência, retrabalho, pendentes, backlog, SLA, motivos de descarte, histórico de classificação.
- **Relatórios analíticos/gerenciais** (5.4.5.1-2): `RelatorioGerencialServlet` serve a listagem, detalhe e exportação CSV/XLSX dos relatórios periódicos. Página JSP com tabela, modal de detalhe rico, filtro por tipo, botões de exportação.
- **Histórico visual** (5.4.6.3-e): modal no dashboard de KPIs com gráfico Chart.js (linha temporal), seletor de período (7/30/90 dias), linha de meta via chartjs-plugin-annotation. Endpoint `historico` no `KpiServlet`.
- **Parâmetros** (`config_chave_valor`): `KPI_SNAPSHOT_INTERVALO_MIN`, `KPI_SNAPSHOT_RETENCAO_DIAS`, `KPI_ALERTA_COOLDOWN_MIN`, `RELATORIO_GERENCIAL_DIA`, `RELATORIO_PREPROC_DIA_UTIL`.

## Arquivos

| Tipo | Arquivos |
|---|---|
| Jobs | `src/main/java/muralha/digital/kpi/KpiSnapshotJob.java`, `src/main/java/muralha/digital/relatorio/RelatorioGerencialJob.java`, `RelatorioPreProcMensalJob.java` |
| Servlet | `src/main/java/muralha/digital/relatorio/RelatorioGerencialServlet.java` (`/MuralhaDigital/RelatorioGerencial`) |
| KPI (alterado) | `src/main/java/muralha/digital/kpi/KpiServlet.java` (endpoint `historico`) |
| Frontend | `src/main/webapp/muralha-digital/pages/dashboard/kpis/index.jsp` (modal histórico), `src/main/webapp/muralha-digital/pages/relatorios/gerencial/index.jsp` |
| Agendador (alterado) | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` (3 novos jobs) |
| Testes | `src/test/java/muralha/digital/kpi/KpiSnapshotJobTest.java`, `src/test/java/muralha/digital/relatorio/RelatorioPreProcMensalJobTest.java` |
| Banco | `docs/banco-de-dados/migracoes/20261016_kpis_relatorio_mensal.sql` |
| Correção Fase 6 | `docs/banco-de-dados/migracoes/20261016_alertas_operacionais.sql` (seção 8 corrigida), `docs/entregas/28-alertas-operacionais.md` (status atualizado) |

## Migrações

| Script | Situação |
|---|---|
| `20261016_kpis_relatorio_mensal.sql` | **Executado (2026-10-16)** — 3 tabelas (`kpi_config_meta`, `kpi_historico`, `relatorio_periodico`), 8 KPIs obrigatórios, 12 metas, 5 parâmetros, menu criado (id_menu=393, id_infos=54). |

## Parâmetros (`muralha.config_chave_valor`)

| Chave | Padrão | Uso |
|---|---|---|
| `KPI_SNAPSHOT_INTERVALO_MIN` | `60` | Intervalo entre snapshots (referência; o job roda a cada 60 min) |
| `KPI_SNAPSHOT_RETENCAO_DIAS` | `90` | Dias de retenção do histórico |
| `KPI_ALERTA_COOLDOWN_MIN` | `360` | Cooldown entre alertas analíticos do mesmo KPI |
| `RELATORIO_GERENCIAL_DIA` | `1` | Dia do mês para gerar o relatório gerencial |
| `RELATORIO_PREPROC_DIA_UTIL` | `5` | N-ésimo dia útil para gerar o relatório de pré-processamento |

## Critérios de aceite

- [x] 1. `..\.setup-gtw\build.ps1` compila sem erro (Windows, 2026-10-16).
- [x] 2. Testes TestNG (Windows, 2026-10-16): `KpiSnapshotJobTest` 8, `RelatorioPreProcMensalJobTest` 8. Total 16/16 novos; com as fases anteriores, 385/385 (5 skips de infra).
- [ ] 3. Migração executada (com autorização) e roteiro [`docs/testes/29-kpis-relatorio-mensal.md`](../testes/29-kpis-relatorio-mensal.md) no navegador.
- [x] 4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Situação dos itens do TR

| Item | Antes | Depois | Por quê |
|---|---|---|---|
| 5.4.3.5 | ⚠️ Parcial | ⚠️ | Alertas analíticos automáticos via KpiSnapshotJob; migração executada |
| 5.4.3.6 | ⚠️ Parcial | ⚠️ | 5 tipos de alerta analítico implementados (a-e); migração executada |
| 5.4.3.7 | ❌ Não implementado | ⚠️ | Relatório gerencial periódico com a-e; migração executada |
| 5.4.5.1 | ⚠️ Parcial | ⚠️ | Relatórios gerenciais cobrem a,c,e,f,h,i; migração executada |
| 5.4.5.2 | ⚠️ Parcial | ⚠️ | Emissão automática periódica + exportação CSV/XLS; migração executada |
| 5.4.6.1 | ✅ | ✅ | Mantido |
| 5.4.6.2 | ⚠️ Parcial | ⚠️ | 8 KPIs obrigatórios (a-h) implementados; migração executada |
| 5.4.6.3 | ⚠️ Parcial | ⚠️ | Metas (a), limites (b), alertas (c), histórico (d), gráfico (e); migração executada |
| 5.5.3.1 | ⚠️ Parcial | ⚠️ | 14 métricas (a-n) coletadas pelo job; migração executada |
| 5.5.3.2 | ⚠️ Parcial | ⚠️ | Segmentação por período; filtros adicionais na Fase 9 |
| 5.5.3.3 | ❌ Não implementado | ⚠️ | Relatório mensal automático no 5o dia útil; migração executada |

## Limitações conhecidas

- O relatório de pré-processamento depende de dados em `vtr_status_analise`; se a coluna `revisado` não existir, a taxa de retrabalho retorna 0.
- A segmentação (5.5.3.2) implementa apenas período; segmentar por equipamento, operador etc. requer filtros adicionais (Fase 9).
- O PDF programático (5.5.3.4) não está implementado; o usuário pode usar Ctrl+P no navegador. PDF nativo fica para a Fase 9.
- A geração de relatórios sob demanda (5.5.3.5) requer endpoint adicional (Fase 9).
