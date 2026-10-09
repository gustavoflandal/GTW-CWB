# Testes — Plano 12: Política de Retenção de Dados

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_retencao_anonimizacao.sql` executada
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Tela carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/admin/retencao/index.jsp`

**Resultado esperado:** Cards de retenção atual e estimativa exibidos; tabela de histórico vazia ou com registros do job.

**Status:** ⏳ Aguarda teste manual

---

## T02 — GET retorna JSON com config e estimativa

**Procedimento:**
1. Abrir DevTools > Network
2. Acessar a tela de retenção

**Resultado esperado:** Requisição GET para `/MuralhaDigital/Retencao` retorna JSON com `retencaoAnos`, `estimativaExpurgo`, `logs[]` e `ok: true`.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Alterar período de retenção

**Procedimento:**
1. Alterar campo "Período (anos)" para 3
2. Clicar Salvar

**Resultado esperado:** SweetAlert de sucesso. Card atualiza para "3 anos". Estimativa recalculada.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Validação de período inválido

**Procedimento:**
1. Informar 0 ou 25 no campo de anos
2. Clicar Salvar

**Resultado esperado:** SweetAlert de erro com mensagem "Período deve ser entre 1 e 20 anos".

**Status:** ⏳ Aguarda teste manual

---

## T05 — Histórico de execuções do job

**Procedimento:**
1. Aguardar execução do RetencaoJob (ou verificar se há logs)

**Resultado esperado:** Tabela exibe data/hora, registros contados, retenção, data de corte, status (badge verde/vermelho) e mensagem.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Acesso restrito (sem sessão)

**Procedimento:**
1. Abrir aba anônima
2. Acessar `http://localhost:8080/MuralhaDigital/Retencao`

**Resultado esperado:** Redirecionamento para login ou resposta de acesso negado.

**Status:** ⏳ Aguarda teste manual
