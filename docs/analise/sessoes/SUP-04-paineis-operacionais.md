# SUP-4 — Paineis operacionais padrao

## Escopo

Criar 9 dashboards operacionais declarativos em YAML e estender o
provisioner para criar Dashboard e Slice (graficos) automaticamente
a partir dessas definicoes. Cada dashboard inclui native filters,
conditional formatting e reference lines para metas do TR.

## Dashboards criados

| # | Dashboard | Slug | Graficos | Refresh |
|---|---|---|---|---|
| 01 | KPIs Operacionais | kpis-operacionais | 9 | 60s |
| 02 | Assertividade | assertividade | 5 | 60s |
| 03 | SLA de Latencia | sla-latencia | 6 | 60s |
| 04 | Disponibilidade de Equipamentos | disponibilidade-equipamentos | 5 | 60s |
| 05 | Pre-processamento | preprocessamento | 8 | 60s |
| 06 | Alertas | alertas | 8 | 30s |
| 07 | Infracoes | infracoes | 7 | 60s |
| 08 | Passagens Veiculares | passagens-veiculares | 8 | 60s |
| 09 | Mapa — Equipamentos e Infracoes | mapa-equipamentos-infracoes | 4 | 120s |

**Total: 9 dashboards, 60 graficos**

## Tipos de visualizacao utilizados

- `big_number_total` — KPIs e metricas de destaque
- `echarts_timeseries_bar` — evolucao temporal (barras)
- `echarts_timeseries_line` — evolucao temporal (linhas)
- `pie` — distribuicao por categoria
- `table` — ranking e tabelas detalhadas
- `deck_scatter` — mapa de pontos (equipamentos)
- `deck_screengrid` — heatmap georreferenciado (infracoes)
- `deck_polygon` — poligonos GeoJSON (areas monitoradas)

## Native filters padrao

Todos os dashboards possuem pelo menos:
- **Periodo** (time_range, obrigatorio, default 7 dias)
- Filtro(s) especificos do dominio (equipamento, tipo, status etc.)
- Cross-filter habilitado entre graficos

## Reference lines e conditional formatting

| Dashboard | Metrica | Meta TR | Tipo |
|---|---|---|---|
| Assertividade | assertividade_media | >= 80% | Reference line + conditional |
| SLA Latencia | pct_sla_cumprido | >= 95% | Reference line + conditional |
| SLA Latencia | media_p95 | <= 4000ms | Conditional formatting |
| Disponibilidade | media_disponibilidade | >= 95% | Reference line + conditional |

## Provisioner

- Novas funcoes: `_provisionar_dashboards`, `_provisionar_dashboard`, `_provisionar_chart`
- Auxiliares: `_montar_json_metadata`, `_montar_chart_params`, `_montar_position_json`
- Nova flag CLI: `--dashboards-only`
- Idempotente: busca por slug (dashboards) e por nome+dataset (graficos)
- Gera `position_json` automatico com layout responsivo (grid 12 colunas)

## Tarefas

- [x] Criar branch `feat/sup-04-paineis-operacionais`
- [x] Criar 9 YAMLs de dashboards em `analise/superset/dashboards/`
- [x] Implementar funcoes de provisionamento de dashboards no `provisionar.py`
- [x] Adicionar `--dashboards-only` ao CLI
- [x] Atualizar Dockerfile para copiar `dashboards/`
- [x] Build do GTW verde
- [x] Criar documentacao da sessao
- [ ] Testar provisionamento com Docker (`docker compose up` + `python /app/provisionar.py`)
- [ ] Validar layout dos dashboards no navegador

## Criterios de aceite

| Criterio | Status |
|---|---|
| 9 dashboards declarativos em YAML | Concluido |
| Tipos de visualizacao corretos por contexto | Concluido |
| Native filters em todos os dashboards | Concluido |
| Reference lines para metas do TR | Concluido |
| Conditional formatting para alertas de threshold | Concluido |
| provisionar.py cria dashboards e graficos | Concluido |
| Idempotente (slug + nome) | Concluido |
| Layout position_json gerado automaticamente | Concluido |
| Build GTW verde | Concluido |
| Teste de provisionamento no Docker | Pendente — requer docker compose up |
| Validacao visual no navegador | Pendente — requer docker compose up |
