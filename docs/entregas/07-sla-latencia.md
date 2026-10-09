# Entrega 07 — SLA de Latência

**Data:** 2026-10-08  
**Status:** ✅ Implementado — migração SQL pendente de execução manual

---

## Escopo

Monitora a latência entre o momento de passagem do veículo (`data`) e a importação no servidor (`data_importado`). A latência é calculada em runtime via `DATEDIFF(MILLISECOND, data, data_importado)` — sem coluna adicional na tabela principal. Um job Quartz executa a cada 5 minutos, calcula o percentil 95 e registra alerta em `muralha.alerta_sla` com flag de violação quando P95 > threshold. O painel exibe indicadores em tempo real e histórico com gráfico.

---

## Arquivos produzidos

| Ação | Arquivo |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_complementar.sql` (tabela `alerta_sla` + config `sla_latencia_threshold_ms`) |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaJob.java` |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/sla-latencia/index.jsp` |
| Modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |

> Migração antiga `20261008_sla_latencia.sql` marcada como OBSOLETA (usava ALTER TABLE proibido).

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Latência calculada em runtime de dados existentes (data → data_importado), sem coluna adicional | ✅ |
| 2 | Job Quartz a cada 5 min registra P95 em alerta_sla | ✅ |
| 3 | Threshold configurável via muralha.config_chave_valor | ✅ |
| 4 | Painel Bootstrap com gráfico Chart.js e tabela de violações | ✅ |
| 5 | Build bem-sucedido | ✅ |

---

## Decisões técnicas

- **Sem coluna `latencia_ms`:** em vez de ALTER TABLE em `veiculo_tempo_real` (~8M linhas), a latência é computada em runtime como `DATEDIFF(MILLISECOND, data, data_importado)`. Filtro: 0–600.000 ms para excluir valores absurdos.
- **`muralha.config_chave_valor`** usado em vez de `muralha.configuracao` (inexistente).
- **`id_local`** usado nos top locais em vez de `equipamento` (coluna inexistente).
- **Tabelas novas apenas:** `alerta_sla` e config INSERT — nenhuma tabela existente é alterada.

---

## Pendências

- Migração `20261008_complementar.sql` pendente de execução manual:
  ```powershell
  sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_complementar.sql"
  ```

---

## Testes

Roteiro em [`docs/testes/07-sla-latencia.md`](../testes/07-sla-latencia.md)
