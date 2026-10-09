# Testes — Plano 14: Gestão de Lotes de Infrações

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_lotes_kpis.sql` executada
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Tela carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/processamento/lotes/index.jsp`

**Resultado esperado:** Tabela de lotes (vazia ou com dados), botão "Novo Lote".

**Status:** ⏳ Aguarda teste manual

---

## T02 — Criar novo lote

**Procedimento:**
1. Clicar "Novo Lote"
2. Informar descrição (opcional)
3. Confirmar

**Resultado esperado:** SweetAlert de sucesso com código do lote (ex: LOTE-20261008231600). Lote aparece na tabela com status RASCUNHO.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Visualizar itens de um lote

**Procedimento:**
1. Clicar no botão de visualização (olho) de um lote

**Resultado esperado:** Modal com tabela de itens (ID, placa, local, pista, data captura). Se vazio, tabela sem registros.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Enviar lote para DETRAN

**Procedimento:**
1. Clicar no olho de um lote RASCUNHO
2. Clicar "Enviar para DETRAN"
3. Confirmar

**Resultado esperado:** SweetAlert de sucesso. Status muda para ENVIADO. Data de envio preenchida. Botão Enviar desaparece.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Cancelar lote rascunho

**Procedimento:**
1. Clicar no olho de um lote RASCUNHO
2. Clicar "Cancelar Lote"
3. Confirmar

**Resultado esperado:** Status muda para CANCELADO.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Lote enviado não pode ser alterado

**Procedimento:**
1. Clicar no olho de um lote ENVIADO

**Resultado esperado:** Modal exibe itens, mas botões Enviar e Cancelar não aparecem.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Acesso restrito (sem sessão)

**Procedimento:**
1. Abrir aba anônima
2. Acessar `http://localhost:8080/MuralhaDigital/Lote?acao=listar`

**Resultado esperado:** Redirecionamento para login ou resposta de acesso negado.

**Status:** ⏳ Aguarda teste manual
