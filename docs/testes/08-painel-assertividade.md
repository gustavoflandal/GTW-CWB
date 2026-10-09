# Testes — Plano 08: Painel de Assertividade

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_complementar.sql` e `20261008_dupla_analise.sql` executadas no banco GTW_MURALHA_DEV
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado
- Ao menos algumas infrações analisadas pelo fluxo de dupla análise (Plano 05)

---

## T01 — Painel carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/assertividade/index.jsp`
2. Verificar que os cards, gráficos e tabela carregam sem erros de console

**Resultado esperado:** Quatro cards (Taxa de Concordância, Pré-aprovadas, Desempates, Reprovadas); gráfico doughnut; gráfico de evolução diária; tabela de ranking. Sem erro 500.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Endpoint retorna dados

**Procedimento:**
```
GET /MuralhaDigital/Assertividade?dtInicio=2026-10-01&dtFim=2026-10-08
```

**Resultado esperado:** JSON com `{"ok":true,"totais":{...},"taxaConcordancia":N,"ranking":[...],"evolucao":[...]}`. Totais refletem contagens de `vtr_status_analise`; ranking lista analistas da `infracao_analise`.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Filtros de período funcionam

**Procedimento:**
1. Selecionar "Hoje" → clicar atualizar
2. Selecionar "7 dias" → clicar atualizar
3. Selecionar "30 dias" → clicar atualizar
4. Selecionar "Customizado" → definir datas → clicar atualizar

**Resultado esperado:** Cada seleção atualiza cards, gráficos e tabela. Ao selecionar "Customizado", campos de data aparecem.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Gráfico doughnut exibe distribuição

**Procedimento:**
1. Com dados de análise disponíveis, verificar o gráfico doughnut

**Resultado esperado:** Três fatias (Pré-aprovada verde, Desempate amarelo, Reprovada vermelho) com proporções corretas.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Evolução diária renderiza

**Procedimento:**
1. Selecionar período de 30 dias
2. Verificar o gráfico de linha

**Resultado esperado:** Linha azul mostrando taxa de concordância (%) por dia, eixo Y de 0 a 100.

**Status:** ⏳ Aguarda teste manual

---

## T06 — Ranking de analistas

**Procedimento:**
1. Verificar tabela de ranking abaixo dos gráficos

**Resultado esperado:** Tabela com colunas #, Nome, Total analisado, Concordantes, Taxa (%). Ordenado por total DESC. Nomes vindos de `sis_usuario.nome`.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Exportação CSV

**Procedimento:**
1. Clicar no botão "CSV"
2. Abrir o arquivo baixado

**Resultado esperado:** Arquivo `assertividade.csv` com cabeçalho `Nome,Total,Concordantes,Taxa` e dados consistentes com a tabela de ranking.

**Status:** ⏳ Aguarda teste manual

---

## T08 — Sem dados retorna graciosamente

**Procedimento:**
1. Selecionar período sem dados (ex.: datas futuras)

**Resultado esperado:** Cards mostram 0, gráficos vazios, tabela sem linhas. Sem erro JavaScript.

**Status:** ⏳ Aguarda teste manual
