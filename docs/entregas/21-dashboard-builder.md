# Entrega 21 — Dashboard Builder Self-Service

**Data:** 2026-10-10  
**Status:** ✅ Implementado

## Escopo

Modulo de criacao de dashboards dinamicos onde qualquer usuario autorizado (grupos 11/41/42) pode montar paineis personalizados com widgets configuraveis: cards KPI, graficos Chart.js (barra, linha, pizza, rosca), tabelas de dados e mapas Leaflet. Os dados vem de um catalogo de datasets (views SQL pre-cadastradas pelo admin), sem que o usuario final precise escrever SQL.

## Origem

- Plano [`20-dashboard-builder.md`](../planos-salvador/20-dashboard-builder.md)
- **Requisitos TR:** §5.4.2.4a-b (filtros dinamicos), §5.4.2.5 (dashboards personalizados pela CONTRATANTE), §5.4.5 (relatorios analiticos), §5.5.3.1 (painel operacional)
- **Gap amarelo:** itens 13 e 14 de [`gaps-amarelos-pendentes.md`](gaps-amarelos-pendentes.md)
- **Analise de aderencia:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §4

## Arquivos produzidos

| Arquivo | Descricao |
|---|---|
| `docs/banco-de-dados/migracoes/20261010_dashboard_builder.sql` | Script SQL: 3 tabelas + 2 views + 3 datasets iniciais + menus + permissoes |
| `src/main/java/muralha/digital/dashboard/DashboardBuilderServlet.java` | CRUD de paineis/widgets + execucao de queries |
| `src/main/java/muralha/digital/dashboard/DatasetServlet.java` | CRUD admin de datasets |
| `src/main/webapp/muralha-digital/pages/dashboard/builder/index.jsp` | Pagina principal do builder com grid drag-and-drop |
| `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/dashboard-grid.js` | Grid CSS 12 colunas + HTML5 Drag API |
| `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/widget-renderer.js` | Renderizador de widgets (CARD/CHART/TABLE/MAP) |
| `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/js/widget-config.js` | Modal de configuracao de widget |
| `src/main/webapp/muralha-digital/pages/dashboard/builder/assets/css/builder.css` | Estilos do grid e widgets |
| `src/main/webapp/muralha-digital/pages/admin/datasets/index.jsp` | Tela admin para cadastro de datasets |

## Objetos de banco criados

| Objeto | Schema | Tipo |
|---|---|---|
| `dashboard_dataset` | muralha | Tabela (catalogo de views SQL) |
| `dashboard_painel` | muralha | Tabela (paineis do usuario) |
| `dashboard_widget` | muralha | Tabela (widgets de cada painel) |
| `v_dashboard_passagens_diarias` | muralha | View (passagens por equipamento/dia) |
| `v_dashboard_alertas_resumo` | muralha | View (alertas por tipo/status/dia) |
| id_menu 376 | dbo.sis_menu | Dashboard Builder |
| id_menu 377 | dbo.sis_menu | Admin Datasets |
| id_infos 43, 44 | dbo.sis_menu_infos | Entradas nos cards do menu |

## Datasets iniciais

| ID | Nome | View SQL | Colunas |
|---|---|---|---|
| 1 | Passagens Diarias | `muralha.v_dashboard_passagens_diarias` | data, id_local, equipamento, latitude, longitude, total_passagens |
| 2 | Alertas Resumo | `muralha.v_dashboard_alertas_resumo` | data, tipo_alerta, status_alerta, total_alertas |
| 3 | Veiculos Tempo Real | `muralha.v_veiculo_tempo_real` | placa, data, nome, latitude, longitude, velocidade, classificacao |

## Arquitetura

- **3 tabelas novas** no schema `muralha`: `dashboard_dataset`, `dashboard_painel`, `dashboard_widget`
- **Datasets = views SQL** pre-cadastradas pelo admin — usuario final seleciona visualmente colunas, agregacao e tipo de widget
- **Query montada internamente** pelo servlet (nunca SQL do usuario): `SELECT col_label, AGREGACAO(col_valor) FROM view WHERE filtros GROUP BY col_label`
- **Layout persistido no banco**: posicao (grid_col, grid_row) e tamanho (grid_width, grid_height) de cada widget
- **Drag-and-drop nativo**: HTML5 Drag API + CSS Grid 12 colunas, sem biblioteca externa
- **Auto-refresh configuravel**: intervalo definido por painel (30s a 5min)
- **Paineis pessoais ou publicos**: cada usuario cria seus paineis; pode marcar como publico para compartilhar

## Tipos de widget

| Tipo | Descricao | Dados necessarios |
|---|---|---|
| CARD | Numero grande com badge de status (OK/WARN/CRIT) | coluna_valor + agregacao + thresholds |
| CHART | Grafico Chart.js (bar/line/pie/doughnut) | coluna_label + coluna_valor + agregacao + tipo_grafico |
| TABLE | Tabela Bootstrap paginada | selecao de colunas do dataset |
| MAP | Mapa Leaflet com markers | coluna_lat + coluna_lng + coluna_label |

## Criterios de aceite

- [x] Build compila sem erros (998 arquivos compilados)
- [x] Migracao SQL executa sem erros no GTW_MURALHA_DEV
- [x] Admin consegue cadastrar datasets (views SQL)
- [x] Usuario consegue criar painel com nome e refresh
- [x] Usuario consegue adicionar widget tipo CARD com dataset
- [x] Usuario consegue adicionar widget tipo CHART (bar, line, pie, doughnut)
- [x] Usuario consegue adicionar widget tipo TABLE
- [x] Usuario consegue adicionar widget tipo MAP (dataset Veiculos Tempo Real disponivel)
- [x] Drag-and-drop de widgets funciona (reposicionar no grid)
- [x] Resize de widgets funciona
- [x] Layout persiste apos salvar e recarregar
- [x] Auto-refresh atualiza dados no intervalo configurado
- [x] Paineis publicos sao visiveis por outros usuarios
- [x] Paineis pessoais sao visiveis apenas pelo criador

## Evidencias

- Screenshot do Dashboard Builder com dados reais: [`21-dashboard-builder.png`](../screenshots/21-dashboard-builder.png)
- Screenshot do Admin Datasets: [`22-admin-datasets.png`](../screenshots/22-admin-datasets.png)
- APIs testadas via Playwright: Dataset (listar, listarTodos, detectarColunas, salvar), DashboardBuilder (listarPaineis, salvarPainel, salvarWidget, executarWidget, atualizarLayout)
