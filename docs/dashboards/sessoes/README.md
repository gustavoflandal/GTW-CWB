# Sessoes do Construtor de Dashboards

Prompts de execucao para cada sessao do projeto de construtor de dashboards.
Referencia: [`construtor-dashboards-arquitetura.md`](../construtor-dashboards-arquitetura.md)

## Plano de sessoes

| Sessao | Titulo | Escopo principal | Arquivo |
|---|---|---|---|
| DASH-1 | Fundacao ponta a ponta | Cube + catalogo + YAML + proxy + 1 widget barras | [DASH-1-fundacao.md](DASH-1-fundacao.md) |
| DASH-2 | Editor de dataset | Arvore de relacoes, campos, medidas, calculados, publicacao | [DASH-2-editor-dataset.md](DASH-2-editor-dataset.md) |
| DASH-3 | Editor de widget e tipos | SortableJS, filtros, todos os graficos ECharts, KPI, tabela | [DASH-3-editor-widget.md](DASH-3-editor-widget.md) |
| DASH-4 | Painel completo | GridStack, filtros globais, filtro cruzado, drill-down, compartilhamento | [DASH-4-painel.md](DASH-4-painel.md) |
| DASH-5 | Tabela dinamica e exportacao | Pivot via Cube, subtotais, CSV/XLSX, PDF via print | [DASH-5-pivot-exportacao.md](DASH-5-pivot-exportacao.md) |
| DASH-6 | Performance e observabilidade | Pre-agregacoes, refreshKey, log de consultas, teste de carga | [DASH-6-performance.md](DASH-6-performance.md) |

## Ordem de execucao

Executar na ordem DASH-1 -> DASH-2 -> ... -> DASH-6. Cada sessao depende das anteriores.

```
DASH-1 (fundacao)
  └── DASH-2 (datasets)
        └── DASH-3 (widgets)
              └── DASH-4 (painel)
                    └── DASH-5 (pivot/export)
                          └── DASH-6 (performance)
```

## Stack

- **Backend**: Java 13.0.1, Servlet puro, SQL Server 2016, Cube Core (mssql)
- **Frontend**: JSP + Bootstrap 5.3 + ES modules (JS puro)
- **Libs vendor**: ECharts, GridStack.js, SortableJS, AG Grid Community (todas MIT/Apache 2.0)
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
