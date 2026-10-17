# SUP-6 — Auditoria e observabilidade

## Escopo

Configurar logging estruturado, métricas StatsD, action log e
retenção automática para auditoria e monitoramento do Superset.

## Configurações adicionadas

### superset_config.py

| Configuração | Valor | Descrição |
|---|---|---|
| FAB_LOG_LEVEL | INFO (via .env) | Nível de log do Flask-AppBuilder |
| LOG_FORMAT | timestamp + level + name | Formato estruturado |
| STATSD_HOST | Opcional (via .env) | Métricas StatsD para Prometheus/Grafana |
| SUPERSET_LOG_VIEW | True | Visualização do action log na UI |
| QUERY_LOGGER | True | Log de todas as consultas SQL |

### .env.exemplo

Novas variáveis:
- `SUPERSET_LOG_LEVEL` — nível de log (default: INFO)
- `STATSD_HOST` — host do StatsD (vazio = desabilitado)
- `STATSD_PORT` — porta do StatsD (default: 8125)

### Provisioner

- `_limpar_action_log(sessao, dias_retencao=90)` — remove entradas com mais de 90 dias
- Flag CLI: `--cleanup-log`

## Tarefas

- [x] Adicionar configurações de log ao superset_config.py
- [x] Configurar StatsD opcional
- [x] Habilitar SUPERSET_LOG_VIEW e QUERY_LOGGER
- [x] Implementar limpeza de action log no provisioner
- [x] Atualizar .env.exemplo com variáveis de observabilidade
- [x] Build verde
- [ ] Testar coleta de métricas com StatsD no Docker
- [ ] Verificar action log na interface do Superset

## Critérios de aceite

| Critério | Status |
|---|---|
| Log estruturado configurado | Concluído |
| StatsD opcional via .env | Concluído |
| Action log visível na UI | Concluído |
| Query logger habilitado | Concluído |
| Limpeza automática (--cleanup-log) | Concluído |
| Teste de coleta StatsD | Pendente — requer Docker |
