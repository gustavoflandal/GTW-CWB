# Relatório DASH-5 — Tabela Dinâmica e Exportação

## Origem

Plano: [DASH-5-pivot-exportacao.md](../sessoes/DASH-5-pivot-exportacao.md)

**Data**: 2026-10-13
**Status**: Concluído
**Build**: SUCCESS (36s, 1015 sources, 13 tests)

## Objetivo

Tabela dinâmica (pivot) com dimensões em linhas/colunas e medidas como valores,
com subtotais. Exportação CSV (UTF-8 com BOM), XLSX (Apache POI SXSSFWorkbook)
por widget e ZIP do painel completo. Impressão/PDF via CSS `@media print`.

## Arquivos Criados/Modificados

### Backend (Java)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `PivotService.java` | Novo | Monta consulta pivot (linhas/colunas→dimensions), pivota resultado com subtotais |
| `ExportacaoService.java` | Novo | CSV (RFC 4180, BOM UTF-8, limite 50K), XLSX (POI 4.1.1 SXSSFWorkbook, limite 100K), ZIP multi-widget |
| `ExportacaoDashboardServlet.java` | Novo | `@WebServlet("/api/dash/exportar")`, GET: csvWidget/xlsxWidget/csvPainel, POST: pivotar |

### Frontend (JS)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `widgets/pivot.js` | Novo | Renderizador pivot: envia dados ao backend para pivotar, exibe em AG Grid Community ou tabela HTML fallback |
| `widget-registry.js` | Modificado | +pivot no registro e tiposDisponiveis() |
| `editor-widget.js` | Modificado | +estado pivotConfig, toggleZonasPivot(), zonas Linhas/Colunas/Valores com SortableJS, visual.pivot no salvar |
| `painel.js` | Modificado | +exportarCsvWidget(), exportarXlsxWidget(), exportarCsvPainel(), imprimirPainel(), botões CSV/XLSX no menu do widget |

### Frontend (JSP)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `paineis/index.jsp` | Modificado | +print.css, +AG Grid vendor, +botões "Exportar todos (CSV)" e "Imprimir/PDF", +cabeçalho impressão |
| `widgets/editor.jsp` | Modificado | +painelPivot (zonas Linhas/Colunas/Valores + checkbox subtotais), +AG Grid vendor |

### CSS

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `css/dashboards/print.css` | Novo | `@media print`: oculta sidebar/navbar/botões, layout linear, page-break-inside:avoid, A4 landscape |

### Vendor (Stubs)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `vendor/ag-grid/ag-grid-community.min.js` | Novo | Stub AG Grid Community 31.3.2 (tabela Bootstrap com sticky header, pagination, subtotal styling) |

### Testes (TestNG)

| Arquivo | Testes |
|---------|--------|
| `ExportacaoServiceTest.java` | 6 testes: BOM UTF-8, cabecalho+linhas CSV, escape separador, XLSX bytes válidos (PK signature), CSV vazio, ZIP multi-arquivo |
| `PivotServiceTest.java` | 5 testes: consulta com todas dimensões, cabecalhos dinâmicos, linha subtotal, pivot vazio, pivot sem colunas |

## Arquitetura de Exportação

```
Widget dropdown → "Exportar CSV"
  └─ GET /api/dash/exportar?acao=csvWidget&id=X
       └─ ExportacaoDashboardServlet
            └─ ExportacaoService.exportarCsv()
                 → byte[] com BOM + cabecalhos + dados (RFC 4180)
                 → Content-Disposition: attachment

Painel dropdown → "Exportar todos (CSV)"
  └─ GET /api/dash/exportar?acao=csvPainel&id=X
       └─ ExportacaoService.exportarPainelZip()
            → ZipOutputStream com 1 CSV por widget

Painel dropdown → "Imprimir / PDF"
  └─ window.print() + print.css
       → A4 landscape, widgets lineares, sem UI chrome
```

## Arquitetura de Pivot

```
Editor: tipo "pivot" → zonas Linhas/Colunas/Valores
  └─ montarConsulta() junta dimensões (linhas+colunas)
  └─ salvar com visual.pivot = {linhas, colunas, valores, subtotais}

Painel: renderizarWidget() → pivot.js
  └─ POST /api/dash/exportar {acao: "pivotar", dados, config}
       └─ PivotService.pivotar() → cabecalhos dinâmicos + dados pivotados
  └─ AG Grid Community renderiza resultado
```

## Pendências

- [ ] Substituir stub AG Grid pelo JS real (download CDN)
- [ ] Integrar exportação com dados reais (Cube query)
- [ ] Testes de integração com banco real
