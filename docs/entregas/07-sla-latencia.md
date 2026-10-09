# Entrega 07 — SLA de Latência

**Data:** 2026-10-08  
**Status:** ✅ Implementado — migração SQL pendente de execução manual

---

## Escopo

Monitora a latência entre o momento de passagem do veículo (`data`) e a importação no servidor (`data_importado`). Um job Quartz executa a cada 5 minutos, calcula o percentil 95 e registra alerta em `muralha.alerta_sla` com flag de violação quando P95 > threshold. O painel exibe indicadores em tempo real e histórico com gráfico.

---

## Arquivos produzidos

| Ação | Arquivo |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_sla_latencia.sql` |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaJob.java` |
| Criar | `src/main/java/muralha/digital/sla/SlaLatenciaServlet.java` |
| Criar | `src/main/webapp/muralha-digital/pages/monitoramento/sla-latencia/index.jsp` |
| Modificar | `src/main/java/com/consilux/servlet/ferramentas/Agendador.java` |

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Latência calculada de dados existentes (data → data_importado) | ✅ |
| 2 | Job Quartz a cada 5 min registra P95 em alerta_sla | ✅ |
| 3 | Threshold configurável via muralha.config_chave_valor | ✅ |
| 4 | Painel Bootstrap com gráfico Chart.js e tabela de violações | ✅ |
| 5 | Build bem-sucedido | ✅ |

---

## Decisões técnicas

- **Colunas existentes:** `veiculo_tempo_real` não tem `dt_captura_equipamento`; a latência é calculada como `DATEDIFF(MILLISECOND, data, data_importado)` — diferença entre captura e importação no servidor. Filtro: 0–600.000 ms para excluir valores absurdos.
- **`muralha.config_chave_valor`** usado em vez de `muralha.configuracao` (inexistente).
- **`id_local`** usado nos top locais em vez de `equipamento` (coluna inexistente).
- **Correção Plan 06 (junto a este commit):** `veiculo_tempo_real_imagem.id` é `uniqueidentifier` — corrigida a migração (`id_original UNIQUEIDENTIFIER`), `ObliteracaoService` (ids como String/UUID) e `InfracaoAnaliseDAO` (idImagem como String).

---

## Pendências

- Migração `20261008_sla_latencia.sql` pendente de execução manual:
  ```powershell
  sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "consiluxsql263" -i "docs\banco-de-dados\migracoes\20261008_sla_latencia.sql"
  ```
- Coluna `latencia_ms` pode ser preenchida retroativamente em passagens antigas:
  ```sql
  UPDATE muralha.veiculo_tempo_real
  SET latencia_ms = DATEDIFF(MILLISECOND, data, data_importado)
  WHERE latencia_ms IS NULL AND data_importado IS NOT NULL
    AND DATEDIFF(MILLISECOND, data, data_importado) BETWEEN 0 AND 600000;
  ```

---

## Testes

Roteiro em [`docs/testes/07-sla-latencia.md`](../testes/07-sla-latencia.md)
