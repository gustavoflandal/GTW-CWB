# KPIs com Histórico, Alertas Analíticos e Relatório Mensal — Plano 29 (Fase 7 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16): ver [Entrega 29](../entregas/29-kpis-relatorio-mensal.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (§1, §2, §7, §10, §13, §14 e §15 aplicadas).
> **Depende de:** [Plano 28](28-alertas-operacionais.md) (infra de alertas operacionais), [Plano 15](15-kpis-configuráveis.md) (KPIs configuráveis).

**Objetivo:** completar a funcionalidade de indicadores operacionais com histórico periódico, metas parametrizáveis, alertas analíticos automáticos, relatório gerencial periódico com comparação, e relatório mensal consolidado de pré-processamento (gap vermelho 5.5.3.3).

## Referência ao Edital (TR TRANSALVADOR)

Texto literal de [`TR-Salvador-Sistema.md`](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md):

> 5.4.3.5. A funcionalidade deverá gerar alertas analíticos automáticos quando identificadas variações relevantes nos indicadores monitorados, conforme parâmetros definidos pela CONTRATANTE.
>
> 5.4.3.6. Os alertas analíticos poderão compreender, entre outros: a) aumento atípico de infrações; b) degradação de desempenho operacional; c) redução de disponibilidade dos equipamentos; d) aumento de retenções viárias; e) alterações relevantes nos padrões operacionais monitorados.
>
> 5.4.3.7. O sistema deverá permitir emissão automática de relatórios gerenciais periódicos contendo: a) resumo executivo dos indicadores monitorados; b) comparação com períodos anteriores; c) evolução dos principais indicadores; d) alertas analíticos identificados; e) informações destinadas ao acompanhamento da execução contratual.
>
> 5.4.5.1. A funcionalidade deverá permitir geração de relatórios analíticos e gerenciais relacionados, no mínimo: a) ao volume de tráfego; b) à velocidade média operacional; c) à taxa de ocupação viária; d) ao comportamento infracional; e) ao desempenho dos equipamentos; f) aos indicadores de aproveitamento; g) às operações de fiscalização; h) à efetividade das ações operacionais; i) às funcionalidades integradas da solução.
>
> 5.4.5.2. Os relatórios deverão permitir: a) filtros parametrizáveis; b) segmentação por equipamento, período, localidade, faixa de rolamento, horário, enquadramento e classificação veicular; c) exportação em PDF, CSV e XLS; d) emissão automática periódica; e) geração sob demanda pela CONTRATANTE.
>
> 5.4.6.2. A funcionalidade deverá suportar, no mínimo: a) indicadores de infração por volume de tráfego; b) indicadores de variação temporal de infrações; c) indicadores de disponibilidade operacional; d) indicadores de aproveitamento dos registros; e) indicadores de efetividade das ações de fiscalização; f) indicadores de segurança viária; g) indicadores de conformidade operacional; h) indicadores relacionados às funcionalidades integradas da solução.
>
> 5.4.6.3. Os indicadores deverão ser parametrizáveis pela CONTRATANTE, permitindo: a) definição de metas; b) definição de limites de atenção e criticidade; c) geração automática de alertas; d) acompanhamento histórico; e) visualização gráfica comparativa.
>
> 5.5.3.1. A CONTRATADA deverá disponibilizar relatórios operacionais e gerenciais relativos ao serviço de pré-processamento, contemplando, no mínimo: a) volume de registros recebidos; b) volume de registros pré-processados; c) volume de registros disponibilizados para validação; d) volume de registros descartados; e) índice de aproveitamento dos registros; f) tempo médio de processamento por registro e por lote; g) produtividade operacional por operador; h) taxa de divergência entre operadores; i) taxa de retrabalho operacional; j) quantidade de registros pendentes de processamento; k) backlog operacional; l) cumprimento dos níveis mínimos de serviço (SLA); m) motivos de descarte ou inconsistência classificados por categoria; n) histórico de classificação, reclassificação e validação operacional.
>
> 5.5.3.2. Os relatórios deverão permitir segmentação por: a) período; b) equipamento; c) tipo de infração; d) operador; e) lote; f) local monitorado; g) faixa de rolamento; h) qualquer combinação dos parâmetros anteriores.
>
> 5.5.3.3. A CONTRATADA deverá apresentar, até o 5o dia útil de cada mês, relatório consolidado referente ao mês imediatamente anterior.

## Situação encontrada (investigação de 2026-10-16, consultas só de leitura no DEV)

- **KPIs existentes:** 4 KPIs configuráveis (`kpi_config`): Passagens Hoje, Pendentes de Análise, Pré-aprovadas (7d), Equipamentos Ativos (24h). Sem histórico, sem metas, sem alertas analíticos.
- **Indicadores obrigatórios:** dos 8 itens de 5.4.6.2, apenas "disponibilidade" e "aproveitamento" tinham correspondência parcial nos KPIs existentes.
- **Relatório gerencial periódico:** não existe; 5.4.3.7 marcado como "Não implementado" na matriz.
- **Relatório mensal de pré-processamento (5.5.3.3):** gap vermelho; não há geração automática mensal; os relatórios de SLA existentes (`alerta_sla`, `alerta_sla_preproc`) cobrem janelas individuais mas não o consolidado mensal.
- **Tabelas complementares:** nenhuma tabela `kpi_historico` ou `kpi_config_meta` ou `relatorio_periodico`.

## Decisões

- **Tabela complementar `kpi_config_meta`** (regra "nunca ALTER TABLE"): FK 1:1 para `kpi_config`, com `meta`, `meta_minima`, `categoria`, `gerar_alerta`. Carregada pelo KpiSnapshotJob ao avaliar alertas e pelo endpoint de histórico.
- **Tabela `kpi_historico`**: snapshots periódicos (id_kpi, valor, status, dt_coleta). Índice em (id_kpi, dt_coleta). Limpeza automática por retenção configurável.
- **Tabela `relatorio_periodico`**: armazena relatórios gerados (tipo, título, período, dados_json). Compartilhada entre os tipos GERENCIAL e PRE_PROCESSAMENTO.
- **KPI Snapshot Job** a cada 60 min: coleta valor de cada KPI ativo, grava snapshot, gera alerta analítico (via `AuditoriaService.registrarSistema`) se status != OK e cooldown respeitado.
- **8 KPIs obrigatórios** (5.4.6.2 a-h) como seed na migração, com queries reais sobre `veiculo_sumarizado`, `veiculo_tempo_real`, `alerta`, `local_vigente`, `alerta_sla`.
- **Relatório Gerencial Job** diário (verifica dia configurável, default 1): gera relatório mensal com resumo de KPIs, comparação, alertas analíticos, volume de tráfego, disponibilidade.
- **Relatório Pré-Processamento Job** diário (verifica 5o dia útil): coleta as 14 métricas de 5.5.3.1 a-n, grava como tipo PRE_PROCESSAMENTO.
- **Exportação CSV/XLSX** via `ExportacaoService` existente (Apache POI, UTF-8 BOM).
- **Histórico na tela de KPIs**: modal com gráfico Chart.js (linha temporal), seletor de período, linha de meta.

## Tarefas

- [x] 1. Migração: `kpi_config_meta`, `kpi_historico`, `relatorio_periodico`, seed de 8 KPIs obrigatórios, metas, parâmetros, menu.
- [x] 2. `KpiSnapshotJob`: coleta horária, alertas analíticos, limpeza por retenção.
- [x] 3. `RelatorioGerencialJob`: relatório gerencial mensal automático com 5 seções de dados.
- [x] 4. `RelatorioPreProcMensalJob`: relatório mensal de pré-processamento (14 métricas, 5o dia útil).
- [x] 5. `RelatorioGerencialServlet`: listar, detalhar, exportar CSV/XLSX.
- [x] 6. Endpoint `historico` no `KpiServlet`: histórico + meta por KPI.
- [x] 7. Página JSP de relatórios gerenciais com modal de detalhe e exportação.
- [x] 8. Dashboard de KPIs: modal de histórico com Chart.js, botão em cada card.
- [x] 9. Agendador: registro dos 3 jobs (KpiSnapshot, RelatorioGerencial, RelatorioPreProc).
- [x] 10. Testes TestNG: cálculo de status (8 cenários), dia útil (8 cenários).

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Testes: KpiSnapshotJobTest 8, RelatorioPreProcMensalJobTest 8. Total novos 16; com fases anteriores, 385/385 (5 skips de infra).
3. Migração executada (com autorização) e roteiro [`docs/testes/29-kpis-relatorio-mensal.md`](../testes/29-kpis-relatorio-mensal.md) no navegador.
4. `python -I docs/referencia/scripts/validar_aderencia.py` sem erro.

## Fora do escopo

- Relatório em formato PDF nativo (5.5.3.4): o sistema exporta CSV e XLS; PDF pode ser gerado pelo navegador (Ctrl+P) a partir da visualização em tela. Implementação de PDF programático fica para a Fase 9.
- Filtros de segmentação completos (5.5.3.2 a-h): a segmentação por período está implementada no job; filtros adicionais por equipamento, operador etc. ficam para a Fase 9 (acabamentos).
- Relatórios extraordinários sob demanda (5.5.3.5): a tela permite visualizar qualquer relatório gerado; a geração sob demanda fica para a Fase 9.
- Comparação pré/pós implantação de equipamento (5.4.3.4): requer metadados de implantação que não existem; fica para a Fase 9.
