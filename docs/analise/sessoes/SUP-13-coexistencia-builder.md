# SUP-13 — Coexistencia: Dashboard Builder e Apache Superset

## Objetivo

Documentar a coexistencia entre o Dashboard Builder proprio (Planos 20-21) e o Apache Superset (Plano 23), ambos ativos e funcionais para avaliacao do cliente.

## Decisao

O Dashboard Builder antigo **NAO sera removido nem desativado**. Ambos os sistemas coexistem ate que o cliente (TRANSALVADOR) aprove a infraestrutura Docker do Superset e autorize explicitamente a migracao.

## Modulos do Dashboard Builder (preservados)

### Java

| Pacote | Descricao |
|---|---|
| `muralha.digital.dashboard.DashboardBuilderServlet` | Servlet principal do builder |
| `muralha.digital.dashboards.*` | 23 classes (servlets, services, consulta) |
| `muralha.digital.dashboards.consulta.MontadorSql` | Motor de consulta SQL |

### Frontend

| Caminho | Descricao |
|---|---|
| `pages/dashboard/builder/` | Construtor visual (GridStack, ECharts) |
| `pages/dashboards/` | Paineis legados (dashboard.jsp, calendario) |
| `static/vendor/` | Bibliotecas vendorizadas (ECharts, GridStack, AG Grid, etc.) |

### Banco de dados

| Schema/Tabela | Descricao |
|---|---|
| `dash.*` | Schema do builder (dash_dataset, dash_painel, dash_widget, etc.) |

## Modulos do Superset (novos)

| Componente | Descricao |
|---|---|
| `analise/` (Docker) | Stack completo (Superset + Postgres + Redis + nginx + Celery) |
| `muralha.digital.analise.*` | SsoAnaliseServlet, GuestTokenServlet |
| `pages/analise/` | Embedding JSP |
| `analise.mat_*`, `analise.v_*` | Tabelas e views analiticas |

## Acessos no menu

Ambos os sistemas possuem entradas no menu do GTW:

| Menu | Destino |
|---|---|
| Dashboard Builder | `/muralha-digital/pages/dashboard/builder/` |
| Dashboards Legados | `/muralha-digital/pages/dashboards/dashboard.jsp` |
| Analises (Superset) | Redirecionamento via SsoAnaliseServlet para nginx:8088 |

## Criterios para desligamento do Builder

O desligamento so ocorrera quando **todos** os criterios forem atendidos:

1. Cliente aprova infraestrutura Docker em producao
2. Todos os paineis do builder estao replicados no Superset
3. Usuarios validaram os paineis Superset por pelo menos 30 dias
4. Cliente autoriza formalmente a desativacao

## Caminho de migracao (futuro)

Quando autorizado:

1. Remover entradas de menu do builder no banco
2. Marcar servlets do builder como `@Deprecated`
3. Manter codigo-fonte no repositorio (sem excluir) por 90 dias
4. Apos 90 dias sem uso, remover em PR dedicada

## Impacto atual

- **Nenhum conflito**: os dois sistemas operam em namespaces separados
  - Builder: `dash.*` (SQL Server), `muralha.digital.dashboard[s].*` (Java)
  - Superset: `analise.*` (SQL Server), Docker isolado, `muralha.digital.analise.*` (Java)
- **Menus independentes**: entradas separadas, sem sobreposicao
- **Banco compartilhado**: ambos leem de `GTW_MURALHA_DEV`, mas em schemas distintos
