# Testes — Plano 10: SLA de Pré-processamento 72h

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_sla_preproc.sql` e `20261008_tabelas_complementares.sql` executadas
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Painel carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/sla-preproc/index.jsp`
2. Verificar que cards, gráficos e tabela carregam sem erros

**Resultado esperado:** Quatro cards (Total Pendentes, Acima 72h, Entre 48-72h, Entre 24-48h), gráfico doughnut, tabela de fila por local. Sem erro 500.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Endpoint retorna aging atual

**Procedimento:**
```
GET /MuralhaDigital/SlaPreprocessamento
```

**Resultado esperado:** JSON com `{"ok":true,"totalPendentes":N,"acima72h":N,"entre4872h":N,"entre2448h":N,"ate24h":N,"fila":[...]}`.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Endpoint retorna histórico

**Procedimento:**
```
GET /MuralhaDigital/SlaPreprocessamento?acao=historico
```

**Resultado esperado:** JSON com `{"ok":true,"historico":[...]}` contendo snapshots da tabela `alerta_sla_preproc` (requer que o Job tenha executado ao menos uma vez).

**Status:** ⏳ Aguarda teste manual

---

## T04 — Job Quartz executa e grava snapshot

**Procedimento:**
1. Verificar log do servidor por mensagem `Job SLA Pré-processamento agendada a cada 60 minutos`
2. Aguardar execução (ou forçar via JMX/debug)
3. Verificar no banco:
```sql
SELECT TOP 5 * FROM muralha.alerta_sla_preproc ORDER BY id DESC;
```

**Resultado esperado:** Registro inserido com total_pendentes, acima_72h, acima_48h, acima_24h coerentes.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Tabela de fila ordena por aging

**Procedimento:**
1. Verificar tabela de fila no painel

**Resultado esperado:** Linhas ordenadas por aging máximo DESC. Locais com > 72h têm linha vermelha e badge "Violado". Locais com 48-72h têm linha amarela e badge "Atenção". Demais têm badge "OK".

**Status:** ⏳ Aguarda teste manual

---

## T06 — Gráfico de evolução renderiza

**Procedimento:**
1. Verificar gráfico de linha (após ao menos 2 snapshots do Job)

**Resultado esperado:** Três linhas (> 72h vermelho, > 48h amarelo, > 24h azul) mostrando evolução dos totais ao longo do tempo.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Auto-refresh a cada 60s

**Procedimento:**
1. Abrir painel e aguardar ~65 segundos
2. Observar console de rede do browser

**Resultado esperado:** Nova requisição ao endpoint a cada 60 segundos; cards e tabela atualizam automaticamente.

**Status:** ⏳ Aguarda teste manual
