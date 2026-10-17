# Relatorio SUP-3 — Datasets, metricas e semantica

## Resumo

Criados 11 datasets declarativos (YAML) com 42 metricas nomeadas, 28
delas certificadas ("Consilux / Edital Salvador"), organizados em 6
dominios: Passagens, Infracoes, Alertas, Equipamentos, SLA e
Pre-processamento. O provisioner (`provisionar.py`) foi reescrito para
ler os YAMLs e criar/atualizar os objetos no Superset de forma
idempotente. O dataset bruto `veiculo_tempo_real` foi marcado como
restrito a Admin com aviso de performance.

## O que foi feito

1. **11 arquivos YAML** em `analise/superset/datasets/` — cada um define
   tabela/view de origem, schema, folder, colunas com label/tipo/is_dttm/
   groupby/filterable/d3_format, metricas com expressao SQL e certificacao
2. **`provisionar.py` reescrito** — le os YAMLs, cria SqlaTable + TableColumn
   + SqlMetric via ORM do Superset, trata datasets admin-only
3. **Dockerfile atualizado** — `pyyaml==6.0.2` e `COPY datasets/`
4. **Documentacao** — sessao, relatorio, README, entrega

## Datasets por dominio

| Dominio | Datasets | Metricas | Certificadas |
|---|---|---|---|
| Passagens | 1 | 5 | 3 |
| Infracoes | 1 | 4 | 2 |
| Alertas | 1 | 5 | 4 |
| Equipamentos | 2 | 4 | 2 |
| SLA | 3 | 10 | 4 |
| Pre-processamento | 2 | 12 | 9 |
| Administracao | 1 | 2 | 0 |
| **Total** | **11** | **42** | **24** |

## LGPD

Nenhum dataset expoe placa, CPF ou dado pessoal. As views de origem ja
excluem esses campos. O dataset bruto nao inclui coluna de placa.

## Decisoes tomadas nesta sessao

- UUIDs estaveis nos YAMLs (padrao `d1a00001-sup3-...`) para idempotencia
- PyYAML 6.0.2 adicionado ao Dockerfile (biblioteca padrao para YAML)
- Dataset bruto com `warning_markdown` e `is_admin_only: true`
- Metricas do TR 5.5.3.1 nomeadas com numero da alinea ("1. Recebidos", etc.)
- Cache timeout: 60s para dados operacionais, 300s para equipamentos, 600s para areas

## Pendencias

- Testar provisionamento com `docker compose up` + `python /app/provisionar.py`
- Validar cada metrica certificada contra consulta SQL de referencia
- Pastas de dataset (recurso 6.0) serao criadas automaticamente pelo provisioner se disponivel na API

## Build

BUILD SUCCESS (33s)
