# Relatório DASH-6 — Performance, Cache e Observabilidade

## Origem

Plano: [DASH-6-performance.md](../sessoes/DASH-6-performance.md)

**Data**: 2026-10-13
**Status**: Concluído
**Build**: SUCCESS (36s, 1017 sources, 15 test files)

## Objetivo

Otimizar performance do construtor de dashboards para produção:
pré-agregações no Cube, cache inteligente, refreshKey, log assíncrono de consultas,
health check, indicadores de performance no frontend, e script de teste de carga.

## Arquivos Criados/Modificados

### Migração SQL

| Arquivo | Descrição |
|---------|-----------|
| `docs/banco-de-dados/migracoes/20261013_dashboards_observabilidade.sql` | Tabelas `dash.consulta_log` e `dash.dataset_estatistica` + 3 índices |

### Backend (Java)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `ConsultaLogService.java` | Novo | Log assíncrono (daemon thread, fire-and-forget), UPSERT em dataset_estatistica, threshold 5000ms para lenta |
| `HealthCheckServlet.java` | Novo | `@WebServlet("/api/dash/health")`: status Cube (/readyz), estatísticas última hora, status OK/DEGRADED/ERROR |
| `DashConsultaServlet.java` | Modificado | +ConsultaLogService, detecção cache hit via slowQuery, registrarAsync no sucesso e no erro |
| `GeradorModeloCube.java` | Modificado | +pre_aggregations (measures, dimensions, granularity:day, refresh_key:5min) no YAML de cada cube |

### Frontend (JS)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `painel.js` | Modificado | +indicador tempo/cache hit/lenta por widget no footer, +badge "Atualizado há X min" com cores (verde/amarelo/vermelho) |

### Frontend (JSP)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `paineis/index.jsp` | Modificado | +span `badgeAtualizado` na barra do painel |

### Script de Teste

| Arquivo | Descrição |
|---------|-----------|
| `scripts/teste-carga-dashboards.ps1` | 4 cenários: N concorrentes, refresh simultâneo, 50 sequenciais (cache), exportação pesada. Mede média/P95/P99/cache hit rate |

### Testes (TestNG)

| Arquivo | Testes |
|---------|--------|
| `ConsultaLogServiceTest.java` | 5 testes: threshold padrão 5000ms, instanciação, registrarAsync com nulls, registrarAsync com erro, threshold positivo |
| `HealthCheckTest.java` | 5 testes: instanciação servlet, ConsultaLogService embutido, threshold consistente, instanciação independente, endpoint @WebServlet |

### Documentação

| Arquivo | Descrição |
|---------|-----------|
| `docs/dashboards/relatorios/carga-dash-6.md` | Template para resultados de teste de carga (executar e colar saída) |

## Arquitetura de Observabilidade

```
Consulta (browser)
  └─ POST /api/dash/consultas
       └─ DashConsultaServlet
            ├─ Cube proxy (chamarCube)
            │    └─ Cube Core → SQL Server
            ├─ Detecta: cacheHit = !slowQuery
            ├─ ConsultaLogService.registrarAsync()
            │    └─ daemon thread (fire-and-forget)
            │         ├─ INSERT dash.consulta_log
            │         └─ UPSERT dash.dataset_estatistica
            └─ Resposta JSON: data + tempoMs + cacheHit

Health Check (admin)
  └─ GET /api/dash/health
       ├─ Cube /readyz (timeout 5s)
       └─ obterEstatisticas(conn, 1h)
            → totalConsultas, tempoMedio, cacheHits, lentas, erros, cacheHitRate

Frontend (painel.js)
  └─ Widget footer: [324ms] [⚡ cache] [⚠ lenta]
  └─ Badge global: [🕐 Atualizado há 3 min] (verde/amarelo/vermelho)
```

## Indicadores de Performance no Frontend

| Indicador | Localização | Condição |
|-----------|-------------|----------|
| Tempo (ms) | Footer do widget | Sempre visível |
| ⚡ Cache hit | Footer do widget | Quando `cacheHit: true` |
| ⚠ Consulta lenta | Footer do widget | Quando tempo > 5000ms |
| Atualizado há X min | Barra do painel | Verde <5min, Amarelo <15min, Vermelho >15min |

## Pré-agregações Cube

Cada cube gerado pelo `GeradorModeloCube` agora inclui:
- `pre_aggregations.main`: todas as measures (count + _avg + _sum) e dimensions
- `granularity: day`
- `refresh_key: every: 5 minutes`

## Pendências

- [ ] Executar teste de carga e documentar resultados em `carga-dash-6.md`
- [ ] Substituir stubs vendor pelos JS reais (GridStack, SortableJS, AG Grid, ECharts)
- [ ] Dashboard de monitoramento pré-configurado (seed) para perfil CATALOGO_ADMIN
- [ ] Testes de integração com banco real e Cube rodando
- [ ] Executar migração SQL no banco GTW_MURALHA_DEV

## Origem

Plano: [DASH-6-performance.md](../../dashboards/sessoes/DASH-6-performance.md)
