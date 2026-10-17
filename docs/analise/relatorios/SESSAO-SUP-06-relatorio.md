# Relatorio — SUP-6: Auditoria e observabilidade

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Configuracao de logging estruturado, metricas StatsD opcionais, action log
visivel na UI, query logger e limpeza automatica de registros antigos.

## Configuracoes adicionadas

| Item | Arquivo |
|---|---|
| LOG_FORMAT, LOG_LEVEL, QUERY_LOGGER | superset_config.py |
| STATSD_HOST/PORT (opcional) | superset_config.py + .env.exemplo |
| SUPERSET_LOG_VIEW | superset_config.py |
| _limpar_action_log (90 dias) | provisionar.py |
| --cleanup-log | provisionar.py CLI |
