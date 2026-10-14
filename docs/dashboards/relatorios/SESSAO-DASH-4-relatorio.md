# Relatório DASH-4 — Painel Interativo

## Origem

Plano: [DASH-4-painel.md](../sessoes/DASH-4-painel.md)

**Data**: 2026-10-13
**Status**: Concluído
**Build**: SUCCESS (31s)

## Objetivo

Implementar o painel completo com layout drag-and-drop (GridStack), filtros globais,
filtro cruzado entre widgets, drill-down temporal, compartilhamento, versionamento
e listagem de painéis.

## Arquivos Criados/Modificados

### Backend (Java)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `PainelService.java` | Novo | Versionamento (snapshot JSON), restauração, duplicação, filtros globais |
| `CompartilhamentoService.java` | Novo | Compartilhar/revogar/listar/verificar permissão |
| `HistoricoVersaoService.java` | Novo | Salvar/listar/obter snapshots de versão |
| `DashPainelServlet.java` | Modificado | +6 ações GET/POST: compartilhamentos, versões, versionar, restaurar, duplicar, filtros globais |

### Frontend (JS — ES Modules)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `painel.js` | Novo | Orquestrador principal: GridStack, widget-registry, filtros, cross-filter, drill-down |
| `filtros-globais.js` | Novo | Barra de filtros globais (data, dimensões), aplica a todas as consultas |
| `filtro-cruzado.js` | Novo | Clique em widget filtra todos os outros pela dimensão selecionada |
| `drill-down.js` | Novo | Navegação temporal: year→quarter→month→week→day, breadcrumb |

### Frontend (JSP)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `paineis/index.jsp` | Reescrito | GridStack layout, dropdown de ações (versionar/duplicar/compartilhar/histórico), filtros globais |
| `paineis/lista.jsp` | Novo | Listagem cards com tabs (Meus/Compartilhados/Todos), criação rápida |

### Vendor (Stubs)

| Arquivo | Tipo | Descrição |
|---------|------|-----------|
| `vendor/gridstack/gridstack-all.min.js` | Novo | Stub funcional GridStack 10.1.2 (substituir pelo real) |
| `vendor/gridstack/gridstack.min.css` | Novo | CSS stub com grid 12 colunas |

### Testes (TestNG)

| Arquivo | Testes |
|---------|--------|
| `PainelServiceTest.java` | 5 testes: instanciação, snapshot JSON, serialização, incremento versão, sufixo cópia |
| `CompartilhamentoServiceTest.java` | 5 testes: instanciação, níveis de acesso, tipos recurso, estrutura JSON, verificação permissão |

## Arquitetura

```
index.jsp
  └─ painel.js (orquestrador)
       ├─ GridStack.init() → drag & resize → auto-save layout
       ├─ widget-registry.js → renderizarWidget() → 12 tipos
       ├─ filtros-globais.js → aplicarFiltrosAConsulta()
       ├─ filtro-cruzado.js → clique → aplicarFiltroCruzadoAConsulta()
       └─ drill-down.js → clique temporal → aplicarDrillAConsulta()
```

## Fluxo de Dados

1. Usuário abre painel → `GET /api/dash/paineis?acao=obter&id=X`
2. GridStack renderiza widgets nas posições `gridX/gridY/gridW/gridH`
3. Cada widget consulta `POST /api/dash/consultas` com query Cube
4. Filtros globais/cruzados/drill modificam a query antes do envio
5. Drag/resize → debounce 1.5s → `POST atualizarLayout`
6. Versionamento → snapshot JSON completo (painel + widgets) → `historico_versao`

## Pendências

- [ ] Substituir stubs GridStack pelo JS/CSS real (download CDN)
- [ ] Migração SQL já incluída na fundação (9 tabelas)
- [ ] Menu lateral do sistema (entrada para painéis)
- [ ] Testes de integração com banco real
