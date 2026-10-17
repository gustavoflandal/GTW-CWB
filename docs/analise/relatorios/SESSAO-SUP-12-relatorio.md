# Relatorio — SUP-12: Desempenho e carga

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Tuning de desempenho no superset_config.py: cache de thumbnails, pool de
conexoes SQLAlchemy, consultas assincronas, auto-refresh controlado e
timeout configuravel via variavel de ambiente.

## Artefatos

| Arquivo | Descricao |
|---|---|
| `analise/superset/superset_config.py` | THUMBNAIL_CACHE, SQLALCHEMY_ENGINE_OPTIONS, auto-refresh |
| `analise/.env.exemplo` | SUPERSET_WEBSERVER_TIMEOUT |
| `analise/docker-compose.prod.yml` | Limites de memoria por servico |
