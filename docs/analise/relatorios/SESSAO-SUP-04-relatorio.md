# Relatorio — SUP-4: Paineis operacionais padrao

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Sessao focada na criacao de 9 dashboards operacionais declarativos e na
extensao do provisioner (`provisionar.py`) para criar objetos Dashboard e
Slice (graficos) no Superset a partir de YAMLs.

## Artefatos criados

### Dashboards YAML (analise/superset/dashboards/)

| Arquivo | Dashboard | Graficos |
|---|---|---|
| `01_kpis_operacionais.yaml` | KPIs Operacionais | 9 |
| `02_assertividade.yaml` | Assertividade | 5 |
| `03_sla_latencia.yaml` | SLA de Latencia | 6 |
| `04_disponibilidade.yaml` | Disponibilidade de Equipamentos | 5 |
| `05_preprocessamento.yaml` | Pre-processamento | 8 |
| `06_alertas.yaml` | Alertas | 8 |
| `07_infracoes.yaml` | Infracoes | 7 |
| `08_passagens.yaml` | Passagens Veiculares | 8 |
| `09_mapa.yaml` | Mapa — Equipamentos e Infracoes | 4 |

### Provisioner (analise/superset/provisionar.py)

Funcoes adicionadas:
- `_provisionar_dashboards()` — orquestrador de dashboards
- `_provisionar_dashboard()` — cria/atualiza Dashboard com UUID estavel
- `_provisionar_chart()` — cria/atualiza Slice vinculado ao dataset
- `_montar_json_metadata()` — native filters, cross-filter, refresh
- `_montar_chart_params()` — parametros de cada tipo de visualizacao
- `_montar_position_json()` — layout grid 12 colunas automatico
- `_map_filter_type()` — mapeamento de tipos de filtro YAML -> Superset

Nova flag `--dashboards-only` para provisionar apenas dashboards.

### Dockerfile

Adicionada linha `COPY superset/dashboards/ /app/dashboards/`.

## Metricas

| Item | Quantidade |
|---|---|
| Dashboards | 9 |
| Graficos totais | 60 |
| Tipos de visualizacao | 8 |
| Funcoes Python novas | 7 |
| Linhas adicionadas (provisionar.py) | ~260 |

## Proximos passos

- SUP-5: Permissoes, colecoes e governanca
- Testar provisionamento completo com Docker
- Validar layouts e interacoes no navegador
