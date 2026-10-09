# Testes — Plano 07: SLA de Latência

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_complementar.sql` executada no banco GTW_MURALHA_DEV
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado

---

## T01 — Estrutura do banco criada

**Procedimento:** Executar no SSMS:
```sql
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA='muralha' AND TABLE_NAME='alerta_sla';

SELECT valor FROM muralha.config_chave_valor WHERE chave='sla_latencia_threshold_ms';
```

**Resultado esperado:** Tabela `alerta_sla` existente; `valor='4000'`.

**Status:** ⏳ Aguarda execução da migração

---

## T02 — Painel carrega no browser

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/monitoramento/sla-latencia/index.jsp`
2. Verificar que os cards e o gráfico carregam sem erros de console

**Resultado esperado:** Quatro cards exibidos (Média, Máxima, Passagens, Status); gráfico renderizado; sem erro 500.

**Status:** ⏳ Aguarda teste manual

---

## T03 — Endpoint `atual` retorna dados

**Procedimento:**
```
GET /MuralhaDigital/SlaLatencia?acao=atual
```

**Resultado esperado:** JSON `{"ok":true,"total":N,"media":M,"maximo":X}` onde valores refletem passagens dos últimos 5 min com `data_importado IS NOT NULL`. Latência calculada em runtime via `DATEDIFF(MILLISECOND, data, data_importado)`.

**Status:** ⏳ Aguarda teste manual

---

## T04 — Job SlaLatenciaJob registrado no Quartz

**Procedimento:**
1. Após subir o servidor, aguardar 5 minutos
2. Verificar:
```sql
SELECT TOP 5 * FROM muralha.alerta_sla ORDER BY id DESC;
```

**Resultado esperado:** Pelo menos 1 registro inserido com `dt_alerta` recente; `total_passagens`, `percentil95_ms` e `violacao` preenchidos.

**Status:** ⏳ Aguarda teste manual

---

## T05 — Indicador de violação muda para vermelho

**Procedimento:**
1. Forçar threshold baixo temporariamente:
```sql
UPDATE muralha.config_chave_valor SET valor='1' WHERE chave='sla_latencia_threshold_ms';
```
2. Aguardar próxima execução do job (≤5 min)
3. Recarregar o painel

**Resultado esperado:** Card "Status SLA" exibe "Violado" com borda vermelha; linha aparece na tabela de violações.

4. Restaurar threshold:
```sql
UPDATE muralha.config_chave_valor SET valor='4000' WHERE chave='sla_latencia_threshold_ms';
```

**Status:** ⏳ Aguarda teste manual

---

## T06 — Tabela de violações lista corretamente

**Procedimento:**
1. Com pelo menos um alerta de violação registrado, verificar tabela no painel

**Resultado esperado:** Linhas vermelhas com data/hora, P95, threshold, total de passagens e top locais.

**Status:** ⏳ Aguarda teste manual
