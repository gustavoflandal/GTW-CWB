# Testes — Plano 15: KPIs Configuráveis

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_lotes_kpis.sql` executada (tabela kpi_config com KPIs padrão)
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Dashboard carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/dashboard/kpis/index.jsp`

**Resultado esperado:** 4 cards de KPI com valores numéricos, cores (verde/amarelo/vermelho) e badges de status.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Auto-refresh do dashboard

**Procedimento:**
1. Acessar o dashboard
2. Aguardar 60 segundos

**Resultado esperado:** Horário de atualização muda automaticamente. Valores podem mudar se houver atividade.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Tela de configuração lista KPIs

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/admin/kpis/index.jsp`

**Resultado esperado:** Tabela com 4 KPIs padrão (ordem, nome, unidade, thresholds, ativo).

**Status:** ⏳ Aguarda teste manual

---

## T04 — Editar KPI existente

**Procedimento:**
1. Clicar no botão de edição de um KPI
2. Alterar threshold OK
3. Clicar Salvar

**Resultado esperado:** Offcanvas fecha. Tabela atualiza com novo valor. Dashboard reflete mudança na cor.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Criar novo KPI

**Procedimento:**
1. Clicar "Novo KPI"
2. Preencher nome, SQL (`SELECT CAST(1 AS NUMERIC) AS valor`), unidade, thresholds
3. Salvar

**Resultado esperado:** KPI aparece na tabela e no dashboard.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Rejeitar query não-SELECT

**Procedimento:**
1. Editar um KPI
2. Alterar SQL para `DELETE FROM muralha.kpi_config`
3. Salvar

**Resultado esperado:** SweetAlert de erro "query_sql deve iniciar com SELECT".

**Status:** ⏳ Aguarda teste manual

---

## T07 — Desativar KPI

**Procedimento:**
1. Editar um KPI, desmarcar "Ativo", salvar

**Resultado esperado:** Badge muda para "Não". KPI não aparece no dashboard.

**Status:** ⏳ Aguarda teste manual

---

## T08 — Acesso restrito (sem sessão)

**Procedimento:**
1. Abrir aba anônima
2. Acessar `http://localhost:8080/MuralhaDigital/Kpi`

**Resultado esperado:** Redirecionamento para login ou resposta de acesso negado.

**Status:** ⏳ Aguarda teste manual
