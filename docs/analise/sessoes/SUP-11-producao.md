# SUP-11 — Producao: implantacao, backup e operacao

## Objetivo

Preparar o stack Superset para implantacao em producao com scripts de deploy, backup/restore, health-check, TLS e operacao diaria.

## Artefatos

| Arquivo | Descricao |
|---|---|
| `analise/scripts/deploy.sh` | Script de deploy com validacao de .env, backup pre-deploy, build, provisionamento |
| `analise/scripts/backup.sh` | Backup PostgreSQL + YAMLs + configs com rotacao 30 dias |
| `analise/scripts/restore.sh` | Restauracao completa a partir de backup .tar.gz |
| `analise/scripts/healthcheck.sh` | Verificacao de saude de todos os servicos (texto ou JSON) |
| `analise/scripts/refresh-materialized.sh` | Recarga das tabelas materializadas via stored procedures |
| `analise/nginx/ssl.conf.exemplo` | Configuracao nginx com TLS (TLSv1.2+, HSTS preparado) |
| `analise/docker-compose.prod.yml` | Atualizado com volumes para certificados TLS |

## Procedimento de deploy

1. Copiar `analise/` para o servidor de producao
2. Copiar `.env.exemplo` para `.env` e preencher
3. Colocar certificados em `analise/nginx/ssl/`
4. Executar `./scripts/deploy.sh`
5. Verificar com `./scripts/healthcheck.sh`

## Operacao diaria

- Backup: `crontab` com `backup.sh` as 02h00
- Recarga materializadas: `crontab` com `refresh-materialized.sh` a cada 30min
- Monitoramento: `healthcheck.sh --json` via sistema de monitoramento
