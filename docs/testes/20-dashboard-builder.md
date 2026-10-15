# Testes — Plano 20: Dashboard Builder Self-Service

Data: 2026-10-09

## Pre-requisitos

- Migracao `20261010_dashboard_builder.sql` executada
- Datasets iniciais cadastrados (v_passagens_diarias, v_alertas_resumo, v_disponibilidade_equipamentos)
- Servidor rodando em `http://localhost:8080/`
- Usuario autenticado (grupos 41 ou 42)

---

## T01 — Tela admin de datasets carrega

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/admin/datasets/index.jsp`

**Resultado esperado:** Tabela com datasets cadastrados (nome, view, status ativo/inativo).

**Status:** ⏳ Aguarda teste manual

---

## T02 — Cadastrar novo dataset

**Procedimento:**
1. Na tela admin de datasets, clicar "Novo Dataset"
2. Preencher nome, descricao, nome da view
3. Clicar "Detectar Colunas"
4. Verificar que colunas foram detectadas com tipos corretos
5. Salvar

**Resultado esperado:** Dataset aparece na tabela. Colunas detectadas automaticamente.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Criar novo painel

**Procedimento:**
1. Acessar o Dashboard Builder
2. Clicar "+ Novo Painel"
3. Preencher nome, descricao, refresh = 60s
4. Salvar

**Resultado esperado:** Painel criado, grid vazio exibido na area central.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Adicionar widget CARD

**Procedimento:**
1. No painel criado, clicar "+ Widget"
2. Selecionar tipo CARD
3. Selecionar dataset "Passagens Diarias"
4. Selecionar coluna_valor = "total_passagens", agregacao = "SUM"
5. Definir threshold OK = 1000, threshold Warn = 100
6. Salvar

**Resultado esperado:** Card aparece no grid com numero grande, badge de status colorido.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Adicionar widget CHART (barra)

**Procedimento:**
1. Clicar "+ Widget"
2. Selecionar tipo CHART
3. Selecionar dataset "Passagens Diarias"
4. coluna_label = "data", coluna_valor = "total_passagens", agregacao = "SUM"
5. tipo_grafico = "bar"
6. Salvar

**Resultado esperado:** Grafico de barras aparece no grid com dados reais.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Adicionar widget CHART (linha, pizza, rosca)

**Procedimento:**
1. Repetir T05 com tipo_grafico = "line"
2. Repetir T05 com tipo_grafico = "pie"
3. Repetir T05 com tipo_grafico = "doughnut"

**Resultado esperado:** Cada grafico renderiza corretamente com o tipo selecionado.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Adicionar widget TABLE

**Procedimento:**
1. Clicar "+ Widget"
2. Selecionar tipo TABLE
3. Selecionar dataset "Alertas Resumo"
4. Selecionar colunas a exibir
5. Salvar

**Resultado esperado:** Tabela paginada aparece no grid com dados do dataset.

**Status:** ⏳ Aguarda teste manual

---

## T08 — Adicionar widget MAP

**Procedimento:**
1. Clicar "+ Widget"
2. Selecionar tipo MAP
3. Selecionar dataset que tenha colunas lat/lng
4. Mapear coluna_lat, coluna_lng, coluna_label
5. Salvar

**Resultado esperado:** Mapa Leaflet aparece com markers nos pontos do dataset.

**Status:** ⏳ Aguarda teste manual

---

## T09 — Drag-and-drop de widgets

**Procedimento:**
1. Em um painel com 3+ widgets
2. Arrastar um widget para outra posicao no grid
3. Recarregar a pagina

**Resultado esperado:** Widget muda de posicao. Apos reload, posicao esta salva.

**Status:** ⏳ Aguarda teste manual

---

## T10 — Resize de widget

**Procedimento:**
1. Arrastar a borda de um widget para redimensionar
2. Recarregar a pagina

**Resultado esperado:** Widget muda de tamanho. Grafico/mapa se adapta. Apos reload, tamanho esta salvo.

**Status:** ⏳ Aguarda teste manual

---

## T11 — Auto-refresh

**Procedimento:**
1. Criar painel com refresh = 30s
2. Adicionar widget CARD
3. Aguardar 35 segundos

**Resultado esperado:** Timestamp de atualizacao muda. Valor do card e re-consultado.

**Status:** ⏳ Aguarda teste manual

---

## T12 — Painel publico vs pessoal

**Procedimento:**
1. Criar painel pessoal (publico = false)
2. Logar com outro usuario — verificar que painel NAO aparece
3. Marcar painel como publico
4. Logar com outro usuario — verificar que painel APARECE (somente leitura)

**Resultado esperado:** Paineis pessoais visiveis apenas pelo criador. Paineis publicos visiveis por todos.

**Status:** ⏳ Aguarda teste manual

---

## T13 — Excluir widget

**Procedimento:**
1. Em um painel com widgets, clicar no botao excluir de um widget
2. Confirmar exclusao

**Resultado esperado:** Widget removido do grid e do banco.

**Status:** ⏳ Aguarda teste manual

---

## T14 — Excluir painel

**Procedimento:**
1. Na lista de paineis, clicar excluir em um painel
2. Confirmar exclusao

**Resultado esperado:** Painel removido da lista. Widgets associados tambem removidos.

**Status:** ⏳ Aguarda teste manual

---

## T15 — Filtros no widget

**Procedimento:**
1. Criar widget CHART com filtro (ex: equipamento = "Local X")
2. Verificar que os dados exibidos respeitam o filtro

**Resultado esperado:** Grafico mostra apenas dados filtrados.

**Status:** ⏳ Aguarda teste manual

---

## T16 — Seguranca — usuario nao edita painel alheio

**Procedimento:**
1. Com usuario A, criar painel publico
2. Com usuario B, tentar editar/excluir o painel de A

**Resultado esperado:** Operacao negada. Mensagem de erro.

**Status:** ⏳ Aguarda teste manual
