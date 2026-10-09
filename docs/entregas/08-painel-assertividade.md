# Entrega — Plano 08: Painel de Assertividade da Dupla Análise

**Data:** 2026-10-08  
**Status:** ✅ Entregue²

## Escopo

Painel de KPIs da assertividade do processo de dupla análise, exibindo taxa de concordância entre analistas, distribuição por status (PRE_APROVADA / DESEMPATE / REPROVADA), evolução diária e ranking de analistas.

## Requisito do TR atendido

- §5.2.2.6 — Painel de controle operacional com indicadores em tempo real: taxa de assertividade, volume processado, divergências identificadas e registros encaminhados para revisão manual.

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `src/main/java/muralha/digital/assertividade/AssertividadeServlet.java` | Servlet com 4 queries: totais por status, taxa de concordância, ranking de analistas, evolução diária |
| `src/main/webapp/muralha-digital/pages/monitoramento/assertividade/index.jsp` | JSP com cards KPI, gráfico doughnut (Chart.js), gráfico de evolução diária, tabela de ranking, exportação CSV |

## Arquitetura

- **Servlet** (`/MuralhaDigital/Assertividade`): recebe `dtInicio` e `dtFim` como parâmetros; retorna JSON com totais, taxa de concordância, ranking e evolução
- **Queries**: usam tabelas complementares (`muralha.vtr_status_analise` + `muralha.infracao_analise`), sem ALTER TABLE
- **JSP**: Bootstrap 5.3, Chart.js 4.4.1 (cdnjs), filtros por período (hoje/7d/30d/customizado), exportação CSV nativa via JavaScript

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Painel carrega e exibe KPIs corretos
- [ ] Filtros de período funcionam
- [ ] Gráficos renderizam corretamente
- [ ] Exportação CSV gera arquivo válido
- [ ] Ranking ordena por volume DESC

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/08-painel-assertividade.md`)
