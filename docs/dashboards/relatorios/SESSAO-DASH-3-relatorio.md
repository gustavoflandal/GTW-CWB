# Relatorio DASH-3 — Editor de Widget e Tipos de Visualizacao

## Origem

Plano: [DASH-3-editor-widget.md](../sessoes/DASH-3-editor-widget.md)

Data: 2026-10-13

## Resumo

Editor visual de widgets com arrastar-e-soltar (SortableJS), 11 tipos de visualizacao
ECharts + KPI HTML + tabela Bootstrap paginada, editor de filtros AND/OR, preview ao vivo,
e registry central de tipos.

## Matriz Requisito → Arquivo

| Requisito | Arquivo | Status |
|---|---|---|
| CRUD de widgets com validacao | `src/.../dashboards/WidgetService.java` | Criado |
| Servlet REST /api/dash/widgets | `src/.../dashboards/WidgetServlet.java` | Criado |
| Registry central de tipos | `src/.../static/js/dashboards/widget-registry.js` | Criado |
| Editor de widget (SortableJS) | `src/.../static/js/dashboards/editor-widget.js` | Criado |
| Editor de filtros AND/OR | `src/.../static/js/dashboards/editor-filtros.js` | Criado |
| JSP do editor | `src/.../analises/widgets/editor.jsp` | Criado |
| Widget barras (expandido) | `src/.../static/js/dashboards/widgets/barras.js` | Ja suporta H+V, empilhado |
| Widget linhas | `src/.../static/js/dashboards/widgets/linhas.js` | Criado |
| Widget area | `src/.../static/js/dashboards/widgets/area.js` | Criado |
| Widget pizza/rosca | `src/.../static/js/dashboards/widgets/pizza.js` | Criado |
| Widget dispersao | `src/.../static/js/dashboards/widgets/dispersao.js` | Criado |
| Widget combinado (barra+linha) | `src/.../static/js/dashboards/widgets/combinado.js` | Criado |
| Widget funil | `src/.../static/js/dashboards/widgets/funil.js` | Criado |
| Widget gauge | `src/.../static/js/dashboards/widgets/gauge.js` | Criado |
| Widget heatmap | `src/.../static/js/dashboards/widgets/heatmap.js` | Criado |
| Widget KPI (card HTML) | `src/.../static/js/dashboards/widgets/kpi.js` | Criado |
| Widget tabela (Bootstrap paginada) | `src/.../static/js/dashboards/widgets/tabela.js` | Criado |
| SortableJS vendor | `src/.../static/vendor/sortablejs/Sortable.min.js` | Stub funcional |
| Testes WidgetService | `src/test/.../dashboards/WidgetServiceTest.java` | Criado |

## Tipos de Visualizacao (11+1)

| Tipo | Tecnologia | Funcionalidades |
|---|---|---|
| barras | ECharts | Vertical, horizontal, empilhado, multiplas series |
| linhas | ECharts | Suave, multiplas series |
| area | ECharts | Suave, empilhado, transparencia |
| pizza | ECharts | Rotulos com percentual, legenda scroll |
| rosca | ECharts | Mesmo que pizza com raio interno |
| dispersao | ECharts | Eixos X/Y numericos |
| combinado | ECharts | Barra + linha, 2 eixos Y |
| funil | ECharts | Ordenacao decrescente automatica |
| gauge | ECharts | Faixas de cor configuraveis |
| heatmap | ECharts | 2 dimensoes + valor, escala visual |
| kpi | HTML | Valor grande, badge com thresholds, comparacao % |
| tabela | Bootstrap | Paginacao, ordenacao, responsiva |

Todos implementam a interface `render(elemento, widget, dados)`.
Todos tratam: estado vazio (mensagem), erro de renderizacao (mensagem).

## Editor de Widget

### Funcionalidades
- Selecao de dataset (dropdown de datasets publicados)
- Zonas drag-and-drop (SortableJS): Dimensoes, Medidas, Series/Cores
- 12 tipos de visualizacao com radio + icone
- Editor de filtros AND/OR com operadores: =, !=, >, <, >=, <=, IN, NOT IN, BETWEEN, LIKE, IS NULL, IS NOT NULL
- Dimensao temporal: granularidade (dia/semana/mes/trimestre/ano) e periodo
- Limite (Top N)
- Opcoes visuais: paleta, orientacao, rotulos, empilhado
- Preview ao vivo (atualiza automaticamente ao mudar configuracoes)
- Botao "ver como tabela" para qualquer tipo de grafico
- Edicao de widget existente (carrega via ?id=)

### Endpoints REST

| Metodo | Acao | Descricao |
|---|---|---|
| GET | `obter` | Detalhes de um widget |
| GET | `metaDataset` | Dimensoes, medidas e tempos de um dataset |
| GET | `datasetsPublicados` | Lista datasets publicados para selecao |
| POST | `salvar` | Criar/atualizar widget com validacao de membros |
| POST | `excluir` | Remover widget (verifica permissao) |

### Validacao de membros
O `WidgetService.validarMembros()` verifica que todas as measures, dimensions e
timeDimensions pertencem ao dataset selecionado. Membros invalidos sao rejeitados
com mensagem listando quais alias nao foram encontrados.

## Build

```
java version "13.0.1" 2019-10-15
Compiling 1009 source files (main) + 9 test files
BUILD SUCCESS (31.972s)
```

## Lacunas

- [LACUNA: SortableJS] Arquivo vendor e um stub funcional; substituir pelo Sortable.min.js real (1.15.0).
- [LACUNA: Menu] Pagina do editor de widget nao tem entrada no menu da navbar.
- [LACUNA: Cube] Preview depende do Cube Core rodando (Docker).

## Debitos

- [DEBITO: AG Grid → DASH-5] Tabela detalhada usa Bootstrap paginada; AG Grid Community para pivot.
- [DEBITO: Testes integracao] Testes requerem banco real; unitarios cobrem estrutura.
- [DEBITO: Texto tipo] Widget tipo 'texto' nao implementado (nao era prioridade).
- [DEBITO: Pivot tipo] Widget tipo 'pivot' adiado para DASH-5.

## Decisoes

1. **Stub SortableJS funcional**: Implementacao minima com HTML5 Drag API para nao bloquear desenvolvimento.
2. **Widget registry**: Mapa centralizado tipo → funcao evita switch/case espalhado.
3. **Pizza reutilizada para rosca**: Mesmo renderizador com parametro de raio interno.
4. **KPI em HTML puro**: Sem dependencia de ECharts para cards simples.
5. **Tabela com paginacao Bootstrap**: Sem AG Grid para manter leveza; AG Grid entra no DASH-5 (pivot).
6. **barras.js nao modificado**: Ja suportava orientacao horizontal e empilhado.
