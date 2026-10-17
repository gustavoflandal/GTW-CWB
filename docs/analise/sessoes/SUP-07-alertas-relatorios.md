# SUP-7 — Alertas e relatórios agendados

## Escopo

Configurar o sistema de alertas e relatórios agendados do Superset
(Celery + Redis) com definições declarativas em YAML. Adicionar
serviços celery-worker e celery-beat ao Docker Compose.

## Alertas criados

| Alerta | Condição | Frequência | Destinatário |
|---|---|---|---|
| Disponibilidade < 95% | AVG(pct_disponibilidade) < 95 | A cada 15min | operacao@ |
| SLA latência < 95% | % janelas dentro SLA < 95 | A cada 15min | operacao@ |
| Assertividade < 80% | AVG(pct_assertividade) < 80 | A cada 30min | preprocessamento@ |
| Alertas críticos pendentes | COUNT criticos sem tratamento > 0 | A cada 10min | operacao@ |

## Relatórios agendados

| Relatório | Dashboard | Frequência | Destinatário |
|---|---|---|---|
| Resumo Diário — KPIs | kpis-operacionais | 07:00 diário | gestao@ |
| Semanal — Infrações | infracoes | 08:00 segunda | gestao@ |
| Semanal — SLA | sla-latencia | 08:30 segunda | gestao@ |

## Infraestrutura Docker

Novos serviços no docker-compose.yml:

| Serviço | Comando | Memória (prod) |
|---|---|---|
| celery-worker | celery worker --concurrency=2 | 1G |
| celery-beat | celery beat | 256M |

CeleryConfig já existia em superset_config.py (SUP-1) com beat_schedule
para `reports.scheduler` e `reports.prune_log`.

## Provisioner

- `_provisionar_alertas()` — orquestra alertas e relatórios
- `_provisionar_alerta()` — cria/atualiza ReportSchedule com ReportRecipients
- Flag CLI: `--alertas-only`
- Suporta tipos: alert (SQL + validator) e report (dashboard screenshot)

## Feature flags

- `ALERT_REPORTS: True` — já existia desde SUP-1
- `ALERT_REPORT_TABS: True` — adicionado para abas na interface

## Tarefas

- [x] Criar 4 YAMLs de alertas em `analise/superset/alertas/`
- [x] Criar 3 YAMLs de relatórios agendados
- [x] Adicionar celery-worker e celery-beat ao docker-compose.yml
- [x] Adicionar overrides de produção no docker-compose.prod.yml
- [x] Habilitar ALERT_REPORT_TABS no superset_config.py
- [x] Implementar funções de provisionamento de alertas
- [x] Atualizar Dockerfile para copiar `alertas/`
- [x] Build verde
- [ ] Testar disparo de alertas com Docker
- [ ] Testar envio de relatório por e-mail (requer SMTP)

## Critérios de aceite

| Critério | Status |
|---|---|
| 4 alertas declarativos com SQL e threshold | Concluído |
| 3 relatórios agendados com screenshot de dashboard | Concluído |
| Celery worker e beat no Compose | Concluído |
| Overrides de produção (restart, memória, healthcheck) | Concluído |
| Provisioner idempotente para alertas | Concluído |
| Teste de disparo de alerta | Pendente — requer Docker + SMTP |
| Teste de envio de relatório por e-mail | Pendente — requer Docker + SMTP |
