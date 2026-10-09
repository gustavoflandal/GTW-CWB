# Testes — Plano 11: Exportação Padronizada

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_tabelas_complementares.sql` executada
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Tela carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp`

**Resultado esperado:** Formulário com filtros (período, local, status) e botões CSV/XLS/PDF. Tabela vazia até consultar.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Consulta retorna dados na tabela

**Procedimento:**
1. Informar período com dados
2. Clicar Consultar

**Resultado esperado:** Tabela preenchida com passagens; label mostra total de registros.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Exportação CSV

**Procedimento:**
1. Consultar com filtros
2. Clicar CSV

**Resultado esperado:** Arquivo `.csv` baixado com BOM UTF-8, cabeçalho institucional, separador `;`, rodapé com total e hash SHA-256.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Exportação XLS

**Procedimento:**
1. Consultar com filtros (tabela preenchida)
2. Clicar XLS

**Resultado esperado:** Arquivo `.xlsx` baixado, abre no Excel com cabeçalhos corretos e dados da tabela.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Exportação PDF

**Procedimento:**
1. Consultar com filtros (tabela preenchida)
2. Clicar PDF

**Resultado esperado:** Arquivo `.pdf` baixado com tabela formatada via jsPDF autoTable.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Filtro por status funciona

**Procedimento:**
1. Selecionar status "Pré-aprovada"
2. Consultar

**Resultado esperado:** Apenas registros com status PRE_APROVADA aparecem. Status colorido em verde.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Filtro por local funciona

**Procedimento:**
1. Informar um ID Local específico
2. Consultar

**Resultado esperado:** Apenas registros daquele local aparecem.

**Status:** ⏳ Aguarda teste manual

---

## T08 — Período obrigatório

**Procedimento:**
1. Clicar Consultar sem informar datas
2. Clicar CSV sem informar datas

**Resultado esperado:** SweetAlert de atenção pedindo o período.

**Status:** ⏳ Aguarda teste manual
