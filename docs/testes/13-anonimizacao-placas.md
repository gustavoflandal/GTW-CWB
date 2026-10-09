# Testes — Plano 13: Anonimização de Placas (LGPD)

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_retencao_anonimizacao.sql` executada (view + config)
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Tela carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp`

**Resultado esperado:** Formulário com filtros (período, local, anonimização) e botão Consultar. Badge "Anonimizado" verde. Tabela vazia.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Consulta com anonimização ativa

**Procedimento:**
1. Informar período com dados
2. Selecionar "Ativada" em Anonimização
3. Clicar Consultar

**Resultado esperado:** Tabela preenchida. Placas mascaradas (ex: `ABC****`). Badge verde "Anonimizado".

**Status:** ⏳ Aguarda teste manual

---

## T03 — Consulta sem anonimização

**Procedimento:**
1. Informar período com dados
2. Selecionar "Desativada" em Anonimização
3. Clicar Consultar

**Resultado esperado:** Tabela preenchida. Placas completas exibidas. Badge amarelo "Dados completos".

**Status:** ⏳ Aguarda teste manual

---

## T04 — Consulta com config padrão

**Procedimento:**
1. Informar período com dados
2. Manter "Padrão (config)" em Anonimização
3. Clicar Consultar

**Resultado esperado:** Comportamento conforme valor de `anonimizar_placa_padrao` em `config_chave_valor` (padrão: 1 = anonimizado).

**Status:** ⏳ Aguarda teste manual

---

## T05 — Filtro por local

**Procedimento:**
1. Informar período e um ID Local específico
2. Clicar Consultar

**Resultado esperado:** Apenas passagens daquele local aparecem.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Badge de status por tipo

**Procedimento:**
1. Consultar período com variedade de status

**Resultado esperado:** Badges coloridos: verde (Pré-aprovada), azul (1ª Análise), vermelho (Reprovada), cinza (Aguardando).

**Status:** ⏳ Aguarda teste manual

---

## T07 — Acesso restrito (sem sessão)

**Procedimento:**
1. Abrir aba anônima
2. Acessar `http://localhost:8080/MuralhaDigital/ConsultaAnonimizada`

**Resultado esperado:** Redirecionamento para login ou resposta de acesso negado.

**Status:** ⏳ Aguarda teste manual
