# SESSAO DASH-5 — Tabela Dinamica e Exportacao

## Pre-requisito
- DASH-1 a DASH-4 concluidas (painel completo funcional com todos os tipos de widget).

## Antes de qualquer codigo
1. Leia CLAUDE.md e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Verifique: build OK, painel com filtros globais e drill-down funcionando.
3. Liste em 10 linhas o que vai criar/modificar. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Mesmas regras das sessoes anteriores.
- Apache POI para exportacao XLSX (Apache 2.0, ja presente ou a adicionar no pom.xml).
- CSV sem biblioteca (StringBuilder + RFC 4180).
- PDF via impressao do navegador com CSS de impressao (sem biblioteca servidor).
- AG Grid Community para tabela dinamica — pivot calculado no Cube, NAO no AG Grid
  (pivot do AG Grid e Enterprise/pago, proibido).

## Objetivo
Tabela dinamica (pivot table) onde o usuario arrasta dimensoes para linhas e colunas,
medidas para valores, e ve o resultado com subtotais. Exportacao CSV/XLSX por widget
e PDF do painel inteiro pela impressao do navegador.

## Escopo

### Backend
- **ExportacaoService**: gera arquivos de exportacao.
  - `exportarCsv(widgetId, filtros)` — CSV (RFC 4180, UTF-8 com BOM para Excel).
    Limite de linhas configuravel (padrao 50.000).
  - `exportarXlsx(widgetId, filtros)` — XLSX via Apache POI.
    Cabecalho com rotulos em portugues do dataset.
    Formatacao basica: cabecalho negrito, numeros formatados, datas formatadas.
    Limite de linhas configuravel (padrao 100.000; POI SXSSFWorkbook para streaming).
  - `exportarPainelCsv(painelId, filtros)` — ZIP com um CSV por widget.
- **ExportacaoDashboardServlet**: endpoints REST.
  - `GET /api/dash/exportar/widget/{id}/csv` — download CSV.
  - `GET /api/dash/exportar/widget/{id}/xlsx` — download XLSX.
  - `GET /api/dash/exportar/painel/{id}/csv` — download ZIP com CSVs.
  - Todos com `Content-Disposition: attachment`.

### Backend — Pivot via Cube
- **PivotService**: traduz configuracao de pivot para `pivotConfig` do Cube.
  - Dimensoes em linhas, dimensoes em colunas, medidas como valores.
  - Subtotais calculados no Cube (propriedade `total: true` no query).
  - O AG Grid Community renderiza o resultado ja pivotado — nao usa o recurso
    de pivot do AG Grid (que e Enterprise).
  - Formato de saida: array de linhas com cabecalhos dinamicos.

### Frontend — Tabela dinamica
- **static/js/dashboards/widgets/pivot.js**:
  - Renderiza em AG Grid Community (versao JavaScript pura, vendor).
  - Recebe dados ja pivotados do backend.
  - Cabecalhos dinamicos gerados a partir das dimensoes de coluna.
  - Subtotais em linhas destacadas (negrito/fundo cinza).
  - Paginacao client-side para datasets grandes.
  - Scroll horizontal para muitas colunas.
- **Configuracao de pivot no editor de widget** (extensao de DASH-3):
  - Zonas de arrastar adicionais: "Linhas", "Colunas", "Valores".
  - Opcao de subtotais (sim/nao).
  - Preview do pivot no editor.

### Frontend — Exportacao
- **Botao de exportacao por widget** (menu tres pontinhos):
  - "Exportar CSV" — chama endpoint e faz download.
  - "Exportar XLSX" — idem.
  - Indicador de loading durante geracao.
  - Mensagem se exceder limite de linhas.
- **Botao de exportacao do painel**:
  - "Exportar todos (CSV)" — download ZIP.
  - "Imprimir / PDF" — `window.print()` com CSS `@media print`:
    - Esconde sidebar, barra superior, botoes.
    - Widgets em layout linear (sem grid).
    - Cada widget em bloco com titulo.
    - Graficos em tamanho fixo para caber na pagina.
    - `page-break-inside: avoid` por widget.
    - Cabecalho com nome do painel e data de impressao.

### CSS de impressao
- **static/css/dashboards/print.css**:
  - `@media print { ... }` com regras especificas.
  - Carregado na JSP com `media="print"`.

### Vendor libs (se necessario)
- AG Grid Community JS (versao fixada) em `static/vendor/ag-grid/` (se nao adicionado em DASH-3).

## Testes obrigatorios
1. Pivot com 2 dimensoes em linhas, 1 em colunas, 1 medida — cabecalhos dinamicos corretos.
2. Pivot com subtotais — linhas de subtotal presentes e valores corretos.
3. Exportar CSV de widget — arquivo valido, UTF-8 com BOM, separador correto.
4. Exportar XLSX de widget — arquivo abre no Excel, cabecalhos com rotulos, numeros formatados.
5. Exportar CSV acima do limite — mensagem de truncamento no arquivo.
6. Exportar painel ZIP — contem um CSV por widget, todos validos.
7. CSS de impressao: graficos visiveis, sem sidebar, sem botoes, page-break correto.
8. Pivot vazio (sem dados) — mensagem "Sem dados", nao tabela em branco.
9. XLSX com 50.000 linhas — gerado em menos de 30s (SXSSFWorkbook streaming).
10. Download so para usuarios com permissao no painel/widget.

## Proibicoes
- AG Grid Enterprise (pivot features, row grouping enterprise) — proibido.
- Gerar PDF no servidor (sem wkhtmltopdf, Puppeteer, etc.) — usar impressao do navegador.
- CSV sem BOM (Excel nao abre UTF-8 corretamente sem BOM).
- Exportacao sem limite de linhas (proteger contra OOM).
- Implementar itens de DASH-6.

## Definition of Done
- Build com JDK 13 OK.
- Pivot funcional com subtotais, dados reais.
- CSV e XLSX exportam corretamente.
- Print CSS gera PDF legivel pelo navegador.
- Testes unitarios + integracao, zero ignorados.
- docs/relatorios/SESSAO-DASH-5-relatorio.md.

## Arquivos esperados

| Acao | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/dashboards/ExportacaoService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/ExportacaoDashboardServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/PivotService.java` |
| Criar | `src/main/webapp/static/js/dashboards/widgets/pivot.js` |
| Criar | `src/main/webapp/static/css/dashboards/print.css` |
| Modificar | `src/main/webapp/static/js/dashboards/editor-widget.js` (zonas pivot) |
| Modificar | `src/main/webapp/static/js/dashboards/painel.js` (botoes exportacao) |
| Criar | `src/main/webapp/static/vendor/ag-grid/ag-grid-community.min.js` (se nao existir) |
| Criar | `src/test/java/muralha/digital/dashboards/ExportacaoServiceTest.java` |
| Criar | `src/test/java/muralha/digital/dashboards/PivotServiceTest.java` |
| Criar | `docs/relatorios/SESSAO-DASH-5-relatorio.md` |
