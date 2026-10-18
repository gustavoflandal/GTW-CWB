# Entrega 21 — Dashboard Builder Self-Service

**Data:** 2026-10-13 (revisao da camada de consulta em 2026-10-14)  
**Status:** 🟡 Implementado — aguardando teste manual no navegador (último critério de aceite; revisão de 2026-10-16)

> **Atualização de 2026-10-18** ([`20261018_remove_cards_duplicados.sql`](../banco-de-dados/migracoes/20261018_remove_cards_duplicados.sql)): O Catálogo de Dados não tem mais card em Cadastro e Configuração; é aberto pelo botão **Catálogo de Dados** do Catálogo de Datasets (mesmas permissões).

## Escopo

Modulo de criacao de dashboards dinamicos onde qualquer usuario autorizado (grupos 11/41/42) pode montar paineis personalizados com widgets configuraveis: cards KPI, graficos Chart.js (barra, linha, pizza, rosca, area, dispersao, funil, gauge, heatmap, combinado), tabelas de dados, tabelas pivot e mapas Leaflet. Os dados vem de um catalogo de datasets, sem que o usuario final precise escrever SQL. As consultas sao montadas pelo Java a partir do catalogo (SQL parametrizado, executado direto no SQL Server), com auditoria e observabilidade.

## Origem

- Plano [`20-dashboard-builder.md`](../planos-salvador/20-dashboard-builder.md)
- Plano [`21-dashboards-consulta-sql-sem-cube.md`](../planos-salvador/21-dashboards-consulta-sql-sem-cube.md) — revisao de 2026-10-14 que substituiu o Cube Core pela camada de consulta propria
- **Requisitos TR:** §5.4.2.4a-b (filtros dinamicos), §5.4.2.5 (dashboards personalizados pela CONTRATANTE), §5.4.5 (relatorios analiticos), §5.5.3.1 (painel operacional)
- **Gap amarelo:** itens 13 e 14 de [`gaps-amarelos-pendentes.md`](gaps-amarelos-pendentes.md)
- **Analise de aderencia:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §4

## Arquivos produzidos

### Java — `src/main/java/muralha/digital/dashboards/` (16 arquivos)

> Em 2026-10-14 foram removidos `DashInternoServlet.java`, `JwtService.java`, `GeradorModeloCube.java`, a pasta `cube/` e o `docker-compose.cube.yml` (Tarefa 9 do Plano 21).

| Arquivo | Descricao |
|---|---|
| `DashCatalogoServlet.java` | CRUD do catalogo de tabelas/colunas/relacoes |
| `DatasetServlet.java` | CRUD de datasets com publicacao e versionamento |
| `DatasetService.java` | Logica de negocio de datasets |
| `DashConsultaServlet.java` | Consultas dos widgets: valida permissao, monta e executa SQL, audita |
| `DashPainelServlet.java` | CRUD de paineis e widgets |
| `PainelService.java` | Logica de negocio de paineis |
| `WidgetServlet.java` | CRUD de widgets individuais |
| `WidgetService.java` | Logica de negocio de widgets |
| `ExportacaoDashboardServlet.java` | Exportacao CSV/XLSX |
| `ExportacaoService.java` | Logica de exportacao |
| `HealthCheckServlet.java` | Saude do banco e estatisticas de consultas |
| `CompartilhamentoService.java` | Compartilhamento de paineis entre usuarios |
| `HistoricoVersaoService.java` | Versionamento de datasets e paineis |
| `PivotService.java` | Logica de tabelas pivot |
| `GramaticaCampoCalculado.java` | Parser de campos calculados em datasets |
| `ConsultaLogService.java` | Auditoria de consultas executadas |

### Java — `src/main/java/muralha/digital/dashboards/consulta/` (revisao 2026-10-14)

| Arquivo | Descricao |
|---|---|
| `ConsultaDashboard.java` | Le e valida a consulta do navegador (measures, dimensions, timeDimensions, filters, order, limit) |
| `ResolvedorDataset.java` | Carrega do schema `dash` as tabelas, colunas, campos, relacoes e filtros fixos do dataset |
| `DatasetResolvido.java` | Metadados do dataset usados na montagem |
| `MontadorSql.java` | Gera o SELECT parametrizado (lista branca do catalogo, joins minimos, bloqueio de 1:N, pre-agregacao) |
| `SqlMontado.java` | SQL, parametros e mapeamento das colunas da resposta |
| `ExecutorConsulta.java` | Executa com timeout de 30 s e cache de 60 s; converte para JSON |
| `PeriodoRelativo.java` | Converte "today", "last 12 months" etc. em intervalo de datas |
| `ErroConsulta.java` | Erro de validacao exibido ao usuario |

Testes TestNG em `src/test/java/muralha/digital/dashboards/consulta/`: `MontadorSqlTest`, `PeriodoRelativoTest` e `ConsultaIntegracaoTest` (somente leitura no banco de desenvolvimento; pulado sem as variaveis `DASH_TESTE_*`).

### JSPs — `src/main/webapp/muralha-digital/pages/analises/` (7 arquivos)

| Arquivo | Descricao |
|---|---|
| `catalogo/index.jsp` | Gerenciamento do catalogo de tabelas, colunas e relacoes (admin) |
| `paineis/lista.jsp` | Listagem de paineis (meus + publicos) |
| `paineis/index.jsp` | Visualizacao/edicao de painel com grid de widgets |
| `datasets/index.jsp` | Listagem e gestao de datasets |
| `datasets/editor.jsp` | Editor wizard de dataset (colunas, campos calculados) |
| `datasets/modelador.jsp` | Modelador visual de dataset (canvas ERD com AntV X6) |
| `widgets/editor.jsp` | Editor visual de widget (tipo, dataset, configuracao) |

### JavaScript — `src/main/webapp/static/js/dashboards/` (27 modulos ES)

| Arquivo | Descricao |
|---|---|
| `api.js` | Cliente HTTP para endpoints REST |
| `estado.js` | Gerenciamento de estado da aplicacao |
| `painel.js` | Logica do painel (renderizacao, layout, refresh) |
| `editor-widget.js` | Modal de configuracao de widget |
| `dataset-editor.js` | Logica do editor de dataset |
| `editor-filtros.js` | Editor de filtros por widget |
| `filtros-globais.js` | Filtros globais do painel |
| `filtro-cruzado.js` | Filtro cruzado entre widgets |
| `drill-down.js` | Navegacao drill-down em dados |
| `gramatica-validador.js` | Validador de campos calculados |
| `widget-registry.js` | Registro e fabrica de tipos de widget |
| `widgets/barras.js` | Widget de grafico de barras |
| `widgets/linhas.js` | Widget de grafico de linhas |
| `widgets/area.js` | Widget de grafico de area |
| `widgets/pizza.js` | Widget de grafico de pizza |
| `widgets/dispersao.js` | Widget de grafico de dispersao |
| `widgets/combinado.js` | Widget de grafico combinado |
| `widgets/funil.js` | Widget de grafico de funil |
| `widgets/gauge.js` | Widget de gauge/velocimetro |
| `widgets/heatmap.js` | Widget de mapa de calor |
| `widgets/kpi.js` | Widget de card KPI |
| `widgets/tabela.js` | Widget de tabela de dados |
| `widgets/pivot.js` | Widget de tabela pivot |
| `modelador/canvas.js` | Canvas AntV X6 com tabelas, relacoes, undo/redo, auto-layout |
| `modelador/painel-tabelas.js` | Painel lateral de tabelas com busca e drag-and-drop |
| `modelador/campos.js` | Painel de selecao de campos com badges de papel |
| `modelador/previa.js` | Preview debounced de resultados do dataset |

### Bibliotecas vendor — `src/main/webapp/static/vendor/`

| Arquivo | Descricao |
|---|---|
| `x6/x6.js` | AntV X6 2.18.1 — canvas de grafos UMD |
| `x6/x6.css` | Estilos do AntV X6 |
| `dagre/dagre.min.js` | dagre 0.8.5 — auto-layout hierarquico |

### CSS

| Arquivo | Descricao |
|---|---|
| `src/main/webapp/static/css/dashboards/print.css` | Estilos para impressao de dashboards |

### Migracoes SQL — `docs/banco-de-dados/migracoes/`

| Script | Descricao |
|---|---|
| `20261013_dash_fundacao.sql` | Schema `dash`, tabelas de catalogo, dataset, dashboard, widget, compartilhamento, historico |
| `20261013_dash2_dataset_editor.sql` | Campos adicionais de dataset, estatisticas, campos calculados |
| `20261013_dashboards_observabilidade.sql` | Tabela `dash.consulta_log`, `dash.dataset_estatistica` |
| `20261013_atualizar_menus_dashboard_builder.sql` | Menus e permissoes do Dashboard Builder |
| `20261013_menu_catalogo_dados.sql` | Menu 378 e info 45 para tela de Catalogo de Dados |
| `20261013_dash_modelador_visual.sql` | Tabelas `dash.dataset_relacao` e `dash.dataset_modelador` para modelador visual |

## Objetos de banco criados

**15 tabelas no schema `dash`:**

| Objeto | Tipo |
|---|---|
| `dash.catalogo_tabela` | Tabela (catalogo de tabelas/views disponiveis) |
| `dash.catalogo_coluna` | Tabela (colunas de cada tabela do catalogo) |
| `dash.catalogo_relacao` | Tabela (relacoes entre tabelas do catalogo) |
| `dash.dataset` | Tabela (datasets criados com versionamento) |
| `dash.dataset_campo` | Tabela (campos de cada dataset) |
| `dash.dataset_tabela` | Tabela (tabelas incluidas em cada dataset) |
| `dash.dataset_relacao` | Tabela (relacoes entre tabelas de um dataset, modelador visual) |
| `dash.dataset_modelador` | Tabela (layout JSON do canvas do modelador visual) |
| `dash.dataset_filtro_fixo` | Tabela (filtros fixos pre-definidos no dataset) |
| `dash.dashboard` | Tabela (paineis do usuario) |
| `dash.widget` | Tabela (widgets de cada painel) |
| `dash.compartilhamento` | Tabela (compartilhamento de paineis entre usuarios) |
| `dash.historico_versao` | Tabela (historico de versoes de datasets e paineis) |
| `dash.consulta_log` | Tabela (auditoria de consultas executadas) |
| `dash.dataset_estatistica` | Tabela (estatisticas de uso de datasets) |

## Arquitetura

- **15 tabelas** no schema `dash` (catalogo, datasets, dashboards, widgets, compartilhamento, historico, observabilidade, modelador visual)
- **Datasets = views SQL** pre-cadastradas pelo admin — usuario final seleciona visualmente colunas, agregacao e tipo de widget
- **Consultas pelo Java** direto no SQL Server: SQL parametrizado, identificadores so do catalogo, timeout de 30 s, limite de 10.000 linhas e cache de 60 s
- **Pre-agregacao automatica**: medidas da tabela base com joins N:1 sao agregadas antes do join (ex.: contagem por equipamento em 8 milhoes de passagens caiu de 80 s para cerca de 2 s)
- **Campos calculados** com gramatica propria validada no backend
- **Versionamento** de datasets e paineis
- **Compartilhamento** granular entre usuarios
- **Observabilidade** via log de consultas e estatisticas de uso
- **Exportacao** CSV/XLSX de dados de widgets
- **Modelador visual**: canvas AntV X6 com tabelas arrastáveis, relacoes por drag entre portas, sugestoes inteligentes (FK/catalogo/nome), auto-layout dagre
- **Drag-and-drop nativo**: HTML5 Drag API + CSS Grid, sem biblioteca externa
- **Auto-refresh configuravel**: intervalo definido por painel
- **Paineis pessoais ou compartilhados**: cada usuario cria seus paineis; pode compartilhar com outros

## Tipos de widget

| Tipo | Descricao |
|---|---|
| KPI | Card com numero grande e indicador de status |
| Barras | Grafico de barras (vertical/horizontal) |
| Linhas | Grafico de linhas com series temporais |
| Area | Grafico de area empilhada |
| Pizza | Grafico de pizza/rosca |
| Dispersao | Grafico de dispersao (scatter) |
| Combinado | Grafico combinado (barras + linhas) |
| Funil | Grafico de funil |
| Gauge | Velocimetro/gauge |
| Heatmap | Mapa de calor |
| Tabela | Tabela de dados paginada |
| Pivot | Tabela pivot com agregacoes dinamicas |

## Criterios de aceite

- [x] Build compila sem erros
- [x] 4 migracoes SQL executam sem erros no GTW_MURALHA_DEV
- [x] Admin consegue cadastrar datasets e campos
- [x] Usuario consegue criar painel com nome e refresh
- [x] Usuario consegue adicionar widgets de todos os tipos
- [x] Drag-and-drop de widgets funciona
- [x] Layout persiste apos salvar e recarregar
- [x] Auto-refresh atualiza dados no intervalo configurado
- [x] Compartilhamento de paineis funciona
- [x] Exportacao CSV/XLSX funciona
- [x] Filtros globais e cruzados funcionam
- [x] Drill-down funciona
- [x] Campos calculados sao validados pela gramatica

### Revisao 2026-10-14 (Plano 21)

- [x] Consultas sem Cube: 101 testes TestNG do modulo passando, incluindo 4 de integracao no GTW_MURALHA_DEV
- [x] Campo inexistente gera erro 400 com o nome do campo (antes era descartado em silencio)
- [x] Filtros com operadores SQL (`=`, `LIKE`, `IN`...) e do formato Cube (`equals`, `contains`...) funcionam
- [x] Publicacao de dataset valida todos os campos e relacoes
- [x] Datasets 1 e 2 existentes validos sem script de correcao
- [ ] Teste manual no navegador de todos os tipos de widget, filtros, drill-down e exportacao
