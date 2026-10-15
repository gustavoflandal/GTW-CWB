# SESSAO DASH-3 — Editor de Widget e Tipos de Visualizacao

## Pre-requisito
- DASH-1 e DASH-2 concluidas (Cube, proxy, datasets com publicacao funcionando).

## Antes de qualquer codigo
1. Leia CLAUDE.md e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Verifique: build OK, dataset publicado, widget de barras de DASH-1 renderizando.
3. Liste em 10 linhas o que vai criar/modificar. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Mesmas regras de DASH-1/DASH-2.
- Frontend: JSP + Bootstrap 5.3 + ES modules + ECharts (vendor).
- SortableJS para arrastar campos para eixos (MIT, vendor).
- Sem React, sem jQuery novo, sem etapa de build.

## Objetivo
Editor visual de widgets onde o usuario arrasta medidas e dimensoes para eixos,
configura filtros, escolhe tipo de visualizacao e ve o resultado em tempo real.
Todos os tipos de grafico ECharts + KPI (HTML) + tabela detalhada (AG Grid Community ou Bootstrap).

## Escopo

### Backend
- **WidgetService**: CRUD de widgets dentro de um dashboard.
  - `salvar(widget)` — valida que medidas e dimensoes pertencem ao dataset e
    que o usuario tem permissao no dataset.
  - `executar(widgetId)` — monta consulta Cube a partir do JSON do widget,
    valida membros, chama proxy, retorna dados formatados.
  - `preVisualizar(consultaJson)` — executa sem salvar, para preview no editor.
- **WidgetServlet**: endpoints REST sob `/api/dash/widgets`.
  - `POST /` — criar widget.
  - `PUT /{id}` — atualizar widget.
  - `DELETE /{id}` — remover widget.
  - `POST /executar` — executar consulta do widget (dados).
  - `POST /pre-visualizar` — preview sem salvar.

### Frontend — Editor de Widget
- **widget-editor.jsp** — modal ou pagina dedicada com:
  1. **Selecao de dataset** (dropdown).
  2. **Zonas de arrastar** (SortableJS):
     - Eixo X / Dimensoes (drop zone).
     - Eixo Y / Medidas (drop zone).
     - Cores / Series (drop zone opcional).
     - Lista de campos disponiveis (fonte do drag).
  3. **Tipo de visualizacao** (radio com icone preview):
     - Barras (vertical/horizontal, agrupado/empilhado).
     - Linhas.
     - Area.
     - Pizza / Rosca.
     - Dispersao (scatter).
     - Combinado (barra + linha).
     - Funil.
     - Gauge.
     - Heatmap.
     - KPI (card com numero grande).
     - Tabela detalhada.
  4. **Configuracao de filtros** (editor-filtros.js):
     - Adicionar condicao: coluna, operador (=, !=, >, <, >=, <=, IN, NOT IN,
       BETWEEN, LIKE, IS NULL, IS NOT NULL), valor(es).
     - Grupos E/OU (AND/OR) com nesting de ate 2 niveis.
     - Componente proprio em Bootstrap (sem jQuery).
  5. **Ordenacao e limite** (top N).
  6. **Granularidade de data** (se houver dimensao temporal): dia, semana, mes, trimestre, ano.
  7. **Opcoes visuais**: titulo, paleta, rotulos, legenda, eixos.
  8. **Preview ao vivo**: renderiza o grafico conforme o usuario muda configuracoes.

### Frontend — Renderizadores de widget
Cada tipo em seu proprio modulo ES (`static/js/dashboards/widgets/`):
- `barras.js` — ja existe de DASH-1; expandir para horizontal e empilhado.
- `linhas.js` — grafico de linhas ECharts.
- `area.js` — grafico de area ECharts.
- `pizza.js` — pizza e rosca ECharts.
- `dispersao.js` — scatter ECharts.
- `combinado.js` — barra + linha ECharts.
- `funil.js` — funil ECharts.
- `gauge.js` — gauge ECharts.
- `heatmap.js` — heatmap ECharts.
- `kpi.js` — card HTML com numero, icone, badge de status (thresholds via cores).
- `tabela.js` — tabela Bootstrap paginada (ou AG Grid Community).

Todos exportam a mesma interface: `render(elemento, especificacao, dados)`.
Todos tratam estados: carregando (spinner), vazio (mensagem), erro (mensagem).
Cada grafico tem botao "ver como tabela" que alterna para tabela Bootstrap.

- **static/js/dashboards/editor-widget.js** — logica do editor (SortableJS zones, preview).
- **static/js/dashboards/editor-filtros.js** — componente de filtros AND/OR.
- **static/js/dashboards/widget-registry.js** — registro central que mapeia tipo -> modulo.

### Vendor libs
- SortableJS (versao fixada, ex: 1.15.0) em `static/vendor/sortablejs/`.

## Ponto de decisao
> Ao final de DASH-3, avaliar: o editor em JS puro atende bem?
> Se ficar complexo demais, DASH-4 pode migrar o editor para uma ilha React
> montada em `<div>` da JSP, consumindo o mesmo `api.js` e JSON.
> Visualizacao do painel continua em JS puro de qualquer forma.

## Testes obrigatorios
1. Criar widget de barras empilhadas com 2 medidas — renderiza corretamente.
2. Criar widget de linhas com granularidade mensal — eixo X mostra meses.
3. Criar widget KPI com thresholds — badge verde/amarelo/vermelho conforme valor.
4. Criar widget de tabela — paginacao funcional com 100+ linhas.
5. Filtro com grupo AND + OR — consulta retorna subset correto.
6. Widget com membro nao pertencente ao dataset — rejeitado (400).
7. Arrastar campo de medidas para dimensoes e vice-versa — editor atualiza.
8. Botao "ver como tabela" em grafico de barras — alterna para tabela.
9. Preview ao vivo: mudar tipo de barras para linhas atualiza o grafico sem salvar.
10. Estado vazio: widget sem dados mostra mensagem, nao grafico em branco.

## Proibicoes
- SQL concatenado.
- Renderizar tipo de widget nao registrado (fallback para mensagem de erro).
- jQuery para drag-and-drop (usar SortableJS ou HTML5 Drag API).
- Implementar itens de DASH-4 em diante (GridStack, filtros globais, compartilhamento).

## Definition of Done
- Build com JDK 13 OK.
- Todos os 11 tipos de widget renderizam com dados de exemplo.
- Editor de widget funcional: arrastar, filtros, preview, salvar.
- Testes unitarios + integracao, zero ignorados.
- docs/relatorios/SESSAO-DASH-3-relatorio.md.

## Arquivos esperados

| Acao | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/dashboards/WidgetService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/WidgetServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/analises/widgets/editor.jsp` |
| Criar | `src/main/webapp/static/js/dashboards/editor-widget.js` |
| Criar | `src/main/webapp/static/js/dashboards/editor-filtros.js` |
| Criar | `src/main/webapp/static/js/dashboards/widget-registry.js` |
| Modificar | `src/main/webapp/static/js/dashboards/widgets/barras.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/linhas.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/area.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/pizza.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/dispersao.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/combinado.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/funil.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/gauge.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/heatmap.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/kpi.js` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/tabela.js` |
| Criar | `src/main/webapp/static/vendor/sortablejs/Sortable.min.js` |
| Criar | `src/test/java/muralha/digital/dashboards/WidgetServiceTest.java` |
| Criar | `docs/relatorios/SESSAO-DASH-3-relatorio.md` |
