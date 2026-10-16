# Sessoes do Construtor de Dashboards

Prompts de execucao para cada sessao do projeto de construtor de dashboards.
Referencia: [`construtor-dashboards-arquitetura.md`](../construtor-dashboards-arquitetura.md)

> **Atualizacao 2026-10-16:** o Cube Core foi removido em 2026-10-14 pelo [Plano 21](../../planos-salvador/21-dashboards-consulta-sql-sem-cube.md). As consultas agora sao montadas pelo Java a partir do catalogo e executadas direto no SQL Server. Os prompts das sessoes DASH-1 a DASH-6 foram escritos antes disso e ainda citam o Cube; a coluna "Escopo principal" abaixo descreve o que vale hoje.

## Plano de sessoes

| Sessao | Titulo | Escopo principal | Arquivo |
|---|---|---|---|
| DASH-1 | Fundacao ponta a ponta | Schema `dash`, catalogo e 1 widget de barras (a camada Cube/YAML/proxy desta sessao foi substituida pelo Plano 21) | [DASH-1-fundacao.md](DASH-1-fundacao.md) |
| DASH-2 | Editor de dataset | Arvore de relacoes, campos, medidas, calculados, publicacao | [DASH-2-editor-dataset.md](DASH-2-editor-dataset.md) |
| DASH-2B | Modelador visual de datasets | Modelador estilo ERD (AntV X6 + dagre) sobre `dash.dataset_relacao` e `dash.dataset_modelador` | [DASH-2B-modelador-visual.md](DASH-2B-modelador-visual.md) |
| DASH-3 | Editor de widget e tipos | SortableJS, filtros, todos os graficos ECharts, KPI, tabela | [DASH-3-editor-widget.md](DASH-3-editor-widget.md) |
| DASH-4 | Painel completo | GridStack, filtros globais, filtro cruzado, drill-down, compartilhamento | [DASH-4-painel.md](DASH-4-painel.md) |
| DASH-5 | Tabela dinamica e exportacao | Pivot, subtotais, CSV/XLSX, PDF via print | [DASH-5-pivot-exportacao.md](DASH-5-pivot-exportacao.md) |
| DASH-6 | Performance e observabilidade | Log de consultas, estatisticas de dataset, teste de carga (as pre-agregacoes do Cube foram descartadas pelo Plano 21) | [DASH-6-performance.md](DASH-6-performance.md) |

## Ordem de execucao

Executar na ordem DASH-1 -> DASH-2 -> DASH-2B -> DASH-3 -> ... -> DASH-6. Cada sessao depende das anteriores.

```
DASH-1 (fundacao)
  └── DASH-2 (datasets)
        └── DASH-2B (modelador visual)
        └── DASH-3 (widgets)
              └── DASH-4 (painel)
                    └── DASH-5 (pivot/export)
                          └── DASH-6 (performance)
```

## Stack

- **Backend**: Java 13.0.1, Servlet puro, SQL Server 2016 (consulta SQL propria, sem Cube — Plano 21)
- **Frontend**: JSP + Bootstrap 5.3 + ES modules (JS puro)
- **Libs vendor** (`src/main/webapp/static/vendor/`, excecao aprovada em 2026-10-16 — ver `CLAUDE.md`): ECharts, GridStack.js, SortableJS, AG Grid Community, AntV X6, dagre
- **Restricao**: somente componentes open source ou 100% gratuitos

## Como usar

Cole o conteudo do prompt da sessao no Claude Code na raiz do repositorio.
Cada sessao segue o padrao:
1. Ler CLAUDE.md e arquitetura
2. Verificar pre-requisitos
3. Listar arquivos a criar/modificar
4. Aguardar OK do usuario
5. Implementar
6. Testes
7. Relatorio + commit
