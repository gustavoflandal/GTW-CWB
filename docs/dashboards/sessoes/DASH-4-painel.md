# SESSAO DASH-4 — Painel Completo

## Pre-requisito
- DASH-1, DASH-2 e DASH-3 concluidas (todos os tipos de widget renderizando, editor funcional).

## Antes de qualquer codigo
1. Leia CLAUDE.md e docs/dashboards/construtor-dashboards-arquitetura.md.
2. Verifique: build OK, editor de widget funcional, multiplos tipos de widget renderizando.
3. Liste em 10 linhas o que vai criar/modificar. Aguarde meu OK.

## Regras de stack (inviolaveis)
- Mesmas regras de DASH-1/DASH-2/DASH-3.
- GridStack.js (MIT, vendor) para posicionar e redimensionar widgets no painel.
- Sem React, sem jQuery novo, sem etapa de build.

## Objetivo
Painel completo onde o usuario posiciona e redimensiona widgets em grid, aplica
filtros globais que propagam para todos os widgets, usa filtro cruzado por clique,
faz drill-down por hierarquia do dataset, compartilha paineis com outros usuarios
e visualiza historico de versoes.

## Escopo

### Backend
- **PainelService**: CRUD completo de paineis (dashboards).
  - `salvar(painel)` — salvar/atualizar painel com layout JSON (posicoes grid).
  - `atualizarLayout(painelId, layoutJson)` — batch update de posicoes dos widgets.
  - `compartilhar(painelId, usuarioOuPerfil, nivel)` — VER ou EDITAR.
  - `listarCompartilhados(usuarioId)` — paineis compartilhados com o usuario.
  - `versionar(painelId)` — snapshot do estado atual em `historico_versao`.
  - `restaurarVersao(painelId, versaoId)` — restaurar snapshot anterior.
  - `duplicar(painelId)` — copia completa (painel + widgets) para o usuario.
- **DashPainelServlet**: endpoints REST sob `/api/dash/paineis`.
  - `GET /` — listar paineis (meus + compartilhados + publicos).
  - `GET /{id}` — painel com widgets e layout.
  - `POST /` — criar painel.
  - `PUT /{id}` — atualizar painel.
  - `PUT /{id}/layout` — atualizar apenas layout (posicoes grid).
  - `DELETE /{id}` — soft delete (so dono ou admin).
  - `POST /{id}/compartilhar` — compartilhar com usuario/perfil.
  - `GET /{id}/versoes` — listar versoes.
  - `POST /{id}/restaurar/{versaoId}` — restaurar versao.
  - `POST /{id}/duplicar` — duplicar painel.
  - `POST /{id}/filtros-globais` — aplicar filtros globais a todos os widgets.

### Frontend — Painel
- **painel.jsp** (evolucao da DASH-1):
  - **GridStack.js** para layout:
    - Grid de 12 colunas responsivo.
    - Drag para mover widgets.
    - Resize nos cantos/bordas.
    - Auto-save do layout apos cada drag/resize (debounce 500ms).
    - Modo compacto (widgets sobem para preencher espacos).
  - **Barra superior**:
    - Titulo editavel (inline).
    - Botao salvar.
    - Botao compartilhar (modal com lista de usuarios/perfis + nivel).
    - Botao duplicar.
    - Botao historico (offcanvas com lista de versoes + restaurar).
    - Seletor de refresh (desligado, 1min, 5min, 15min, 30min).
    - Botao adicionar widget (abre editor de widget DASH-3).
    - Botao tela cheia.
  - **Barra de filtros globais**:
    - Filtros de data (range picker Bootstrap).
    - Filtros de dimensao (selects dinamicos baseados nos datasets dos widgets).
    - Aplicar filtro propaga para todos os widgets do painel.
    - Limpar filtros.
  - **Filtro cruzado por clique**:
    - Clicar em barra/fatia/ponto de um widget filtra os demais pela dimensao clicada.
    - Indicador visual de filtro ativo no widget fonte.
    - Clicar novamente remove o filtro.
  - **Drill-down**:
    - Se o dataset tiver hierarquia (ex: ano > mes > dia), clicar na barra faz drill.
    - Breadcrumb de nivel (ex: 2026 > Outubro > Dia 13).
    - Botao voltar nivel.
  - **Menu de contexto do widget** (tres pontinhos):
    - Editar (abre editor DASH-3).
    - Duplicar.
    - Remover.
    - Atualizar dados.
    - Ver como tabela (toggle).
    - Tela cheia do widget.
  - **Auto-refresh**: `setInterval` configuravel, minimo 1 minuto.
  - **Sidebar de paineis**: lista de paineis do usuario + compartilhados para navegacao rapida.

### Frontend — Lista de paineis
- **paineis-lista.jsp** em `/analises/paineis/`
  - Cards ou tabela com paineis do usuario + compartilhados.
  - Filtros: meus, compartilhados, publicos.
  - Acoes: abrir, editar, compartilhar, duplicar, excluir.
  - Botao "+ Novo Painel".

### Vendor libs
- GridStack.js (versao fixada, ex: 10.1.2) em `static/vendor/gridstack/`.

## Testes obrigatorios
1. Adicionar 3 widgets ao painel, mover/redimensionar — layout salva corretamente.
2. Filtro global de data — todos os widgets atualizam dados.
3. Filtro cruzado: clicar em barra do widget A filtra widgets B e C.
4. Drill-down: ano -> mes -> dia, breadcrumb correto.
5. Compartilhar painel com usuario B (nivel VER) — B ve mas nao edita.
6. Compartilhar com nivel EDITAR — B pode mover widgets.
7. Versionamento: salvar, modificar, restaurar versao anterior.
8. Duplicar painel: copia independente com todos os widgets.
9. Auto-refresh 1 min: widgets atualizam dados.
10. Painel do tenant A nao visivel pelo tenant B.

## Proibicoes
- jQuery para GridStack (usar versao JavaScript puro do GridStack).
- Layout salvo em localStorage (usar API, estado compartilhado).
- Filtro cruzado sem indicador visual (usuario deve saber qual filtro esta ativo).
- Implementar itens de DASH-5 em diante.

## Definition of Done
- Build com JDK 13 OK.
- Painel completo funcional: grid, filtros, drill, compartilhamento, historico.
- Testes unitarios + integracao, zero ignorados.
- Prints de: painel com 4+ widgets, filtro cruzado ativo, drill-down, compartilhamento.
- docs/relatorios/SESSAO-DASH-4-relatorio.md.

## Arquivos esperados

| Acao | Caminho |
|---|---|
| Criar | `src/main/java/muralha/digital/dashboards/PainelService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/DashPainelServlet.java` |
| Criar | `src/main/java/muralha/digital/dashboards/CompartilhamentoService.java` |
| Criar | `src/main/java/muralha/digital/dashboards/HistoricoVersaoService.java` |
| Modificar | `src/main/webapp/muralha-digital/pages/analises/paineis/index.jsp` |
| Criar | `src/main/webapp/muralha-digital/pages/analises/paineis/lista.jsp` |
| Criar | `src/main/webapp/static/js/dashboards/painel.js` |
| Criar | `src/main/webapp/static/js/dashboards/filtros-globais.js` |
| Criar | `src/main/webapp/static/js/dashboards/filtro-cruzado.js` |
| Criar | `src/main/webapp/static/js/dashboards/drill-down.js` |
| Criar | `src/main/webapp/static/vendor/gridstack/gridstack-all.min.js` |
| Criar | `src/main/webapp/static/vendor/gridstack/gridstack.min.css` |
| Criar | `src/test/java/muralha/digital/dashboards/PainelServiceTest.java` |
| Criar | `src/test/java/muralha/digital/dashboards/CompartilhamentoServiceTest.java` |
| Criar | `docs/relatorios/SESSAO-DASH-4-relatorio.md` |
