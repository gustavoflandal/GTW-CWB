# Relatorio — SUP-7: Alertas e relatorios agendados

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Criacao de 4 alertas e 3 relatorios agendados declarativos em YAML,
servicos celery-worker e celery-beat no Docker Compose, e extensao
do provisioner para criar ReportSchedule e ReportRecipients.

## Artefatos criados

### Alertas (analise/superset/alertas/)

| Arquivo | Tipo | Frequencia |
|---|---|---|
| alerta_disponibilidade.yaml | alert | */15 min |
| alerta_sla_latencia.yaml | alert | */15 min |
| alerta_assertividade.yaml | alert | */30 min |
| alerta_alertas_criticos.yaml | alert | */10 min |
| relatorio_diario_operacional.yaml | report | 07:00 diario |
| relatorio_semanal_infracoes.yaml | report | 08:00 seg |
| relatorio_semanal_sla.yaml | report | 08:30 seg |

### Docker Compose

- celery-worker (prefork, 2 workers, 1G prod)
- celery-beat (agendador, 256M prod)
- Healthcheck no worker (celery inspect ping)

## Funcoes adicionadas ao provisioner

- `_provisionar_alertas()` — orquestrador
- `_provisionar_alerta()` — cria ReportSchedule (alert ou report)
- Flag CLI: `--alertas-only`
