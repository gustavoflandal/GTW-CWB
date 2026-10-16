# Entrega 07 — SLA de Latência

**Data:** 2026-10-08  
**Status:** ✅ Implementado — migração `20261008_complementar.sql` executada no `GTW_MURALHA_DEV` (conferido em 2026-10-15; ver [`migracoes/README.md`](../banco-de-dados/migracoes/README.md))

---

## Origem

- Plano [`07-sla-latencia.md`](../planos-salvador/07-sla-latencia.md) — SLA de Latência
- **Requisitos TR:** §5.2.7.1–3 (telemetria operacional, monitoramento de equipamentos), §5.6.2.3 (tempo máximo 4s para eventos críticos), §5.7.9.1 (latência LAP→alerta)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §2, §6 e §7
- **Errata aplicada:** [`00c-errata-correcoes.md`](../planos-salvador/00c-errata-correcoes.md) §5, §11 e §15 (`config_chave_valor`; colunas `dt_captura_equipamento`/`dt_recepcao_servidor`/`equipamento` não existem)

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

- ~~Migração `20261008_complementar.sql`~~ executada no `GTW_MURALHA_DEV` (conferido em 2026-10-15).

---

## Testes

Roteiro em [`docs/testes/07-sla-latencia.md`](../testes/07-sla-latencia.md)
