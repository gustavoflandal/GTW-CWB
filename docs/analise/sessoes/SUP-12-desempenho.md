# SUP-12 — Desempenho e carga

## Objetivo

Otimizar o Superset para carga operacional com tuning de cache, pool de conexoes, consultas assincronas e limites de auto-refresh.

## Configuracoes aplicadas

### Cache Redis

| Cache | Timeout | Prefixo |
|---|---|---|
| DATA_CACHE | 60s | `superset_dados_` |
| FILTER_STATE_CACHE | 300s | `superset_filtro_` |
| EXPLORE_FORM_DATA_CACHE | 300s | `superset_explorar_` |
| THUMBNAIL_CACHE | 3600s | `superset_thumb_` |

### Pool de conexoes (SQLAlchemy)

| Parametro | Valor |
|---|---|
| pool_size | 5 |
| max_overflow | 10 |
| pool_timeout | 30s |
| pool_recycle | 1800s |
| pool_pre_ping | True |

### Limites de consulta

| Parametro | Valor |
|---|---|
| ROW_LIMIT | 10.000 |
| SQL_MAX_ROW | 10.000 |
| SQLLAB_TIMEOUT | 30s |
| SQLLAB_ASYNC_TIME_LIMIT_SEC | 120s |
| SUPERSET_WEBSERVER_TIMEOUT | 60s (configuravel) |

### Auto-refresh controlado

Intervalos disponiveis: sem refresh, 1min, 5min, 15min.
Impede refresh agressivo (<60s) que sobrecarregaria o SQL Server.

## Limites de memoria (docker-compose.prod.yml)

| Servico | Limite |
|---|---|
| Superset | 2 GB |
| Celery Worker | 1 GB |
| PostgreSQL | 512 MB |
| Redis | 256 MB |
| Celery Beat | 256 MB |
| Nginx | 128 MB |
| **Total** | **~4,2 GB** |

## Recomendacoes para o servidor

- Minimo: 8 GB RAM, 4 vCPUs, 50 GB disco
- Recomendado: 16 GB RAM, 8 vCPUs, 100 GB SSD
- Rede: acesso ao SQL Server 10.0.0.200 porta 1433
