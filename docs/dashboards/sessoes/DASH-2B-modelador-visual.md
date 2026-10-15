# SESSAO DASH-2B — Modelador Visual de Datasets

## Pre-requisito
- DASH-2 concluida (editor wizard de dataset funcional).
- Tabelas `dash.dataset_relacao` e `dash.dataset_modelador` criadas (migracao 20261013).

## Objetivo
Complementar o editor wizard (DASH-2) com um modelador visual estilo ERD, usando
canvas interativo para arrastar tabelas, criar relacoes via drag-and-drop entre
portas de colunas, selecionar campos e pre-visualizar resultados.

## Escopo

### Backend (DatasetServlet / DashCatalogoServlet)
- **sugestoesRelacao**: 3 fontes — FKs reais (sys.foreign_keys), catalogo, nome (id_X).
- **validarRelacao**: self-join, compatibilidade de tipos, unicidade de caminho.
- **verificarCardinalidade**: deteccao real via GROUP BY HAVING com timeout 5s.
- **salvarModelador**: transacao unica — tabelas + relacoes + campos + layout JSON.
- **promoverRelacao**: copia relacao de dataset_relacao para catalogo_relacao.
- **obter** (DatasetService): inclui relacoes do dataset e layout do modelador.

### Frontend (ES modules em static/js/dashboards/modelador/)
- **canvas.js**: AntV X6 2.18.1 — grafo com nos (tabelas) e arestas (relacoes).
  - Cards de tabela com borda colorida por schema, portas PK/FK nas colunas.
  - Conexao por drag entre portas com validacao async no servidor.
  - Sugestoes como arestas tracejadas (banco/catalogo/nome) confirmaveis por clique.
  - Auto-layout via dagre (LR), undo/redo, zoom.
- **painel-tabelas.js**: lista lateral com busca, drag-and-drop, tooltip de amostra.
- **campos.js**: painel direito com checkboxes por tabela, badges de papel (D/M/T/G).
- **previa.js**: painel inferior com preview debounced (600ms) via consulta Cube.
- **modelador.jsp**: pagina 3 colunas integrando os 4 modulos, toolbar, modal de relacao.

### Bibliotecas vendor (static/vendor/)
- AntV X6 2.18.1 (x6.js + x6.css) — canvas de grafos.
- dagre 0.8.5 (dagre.min.js) — auto-layout hierarquico.

### Migracao SQL
- `20261013_dash_modelador_visual.sql` — tabelas `dash.dataset_relacao` e `dash.dataset_modelador`.

### Navegacao
- Editor wizard (editor.jsp) → botao "Modelador" abre modelador.jsp.
- Modelador (modelador.jsp) → botao "Wizard" volta para editor.jsp.
- Ambas vistas compartilham o mesmo dataset (passam ?id=).

## Criterios de aceite
1. Arrastar tabela do painel para o canvas cria card com colunas e portas.
2. Conectar portas de colunas compativeis cria relacao validada pelo servidor.
3. Sugestoes de FK/catalogo/nome aparecem como arestas tracejadas, confirmaveis.
4. Campos selecionados geram preview com dados reais.
5. Salvar persiste tabelas, relacoes, campos e layout JSON (restauravel ao reabrir).
6. Build compila sem erros.

## Status: CONCLUIDO
