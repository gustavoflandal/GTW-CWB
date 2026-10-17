# SUP-3 — Datasets, metricas e semantica no Superset

## Escopo

Provisionar datasets declarativos no Superset a partir de arquivos YAML,
com rotulos em PT-BR, metricas nomeadas e certificadas, e restricao de
acesso ao dataset bruto.

## Objetos criados

### Datasets por dominio

**Passagens**
- `analise.mat_passagens_hora` — 5 metricas (total passagens, velocidade media ponderada, passagens/faixa, equipamentos ativos, % Mercosul)

**Infracoes**
- `analise.mat_infracoes_dia` — 4 metricas (total infracoes, excesso medio ponderado, tipos de enquadramento, equipamentos com infracoes)

**Alertas**
- `analise.v_alertas` — 5 metricas (total alertas, criticos, % descartados, % tratados, pendentes)

**Equipamentos**
- `analise.v_equipamentos` — 3 metricas (total, ativos 24h, % ativos)
- `analise.v_areas_monitoradas_geojson` — 1 metrica (total areas)

**SLA**
- `analise.v_disponibilidade_dia` — 4 metricas (media disponibilidade, total verificacoes, verificacoes OK, disponibilidade global)
- `analise.v_sla_latencia_dia` — 3 metricas (SLA cumprido %, latencia P95, violacoes)
- `analise.v_sla_preproc_dia` — 3 metricas (SLA preproc medio, media pendentes, pior backlog 72h)

**Pre-processamento**
- `analise.v_assertividade_dia` — 4 metricas (assertividade media, total analisadas, desempates, % desempate)
- `analise.v_preprocessamento` — 8 metricas certificadas cobrindo as 14 alineas do TR 5.5.3.1

**Administracao (restrito)**
- `muralha.veiculo_tempo_real` — 2 metricas, somente Admin, com warning markdown

### Provisioner

- `provisionar.py` reescrito: le YAMLs, cria/atualiza datasets, colunas e metricas, restringe datasets admin-only
- Suporta `--datasets-only`, `--dry-run`
- Idempotente: cria se nao existe, atualiza se ja existe

## Tarefas

- [x] Criar branch `feat/sup-03-datasets-semantica`
- [x] Criar 11 YAMLs de datasets em `analise/superset/datasets/`
- [x] Reescrever `provisionar.py` com carga declarativa
- [x] Adicionar `pyyaml==6.0.2` ao Dockerfile
- [x] Copiar pasta `datasets/` no Dockerfile
- [x] Build do GTW verde
- [x] Criar documentacao da sessao
- [ ] Testar provisionamento com Docker (requer `docker compose up`)
- [ ] Validar metricas contra SQL de referencia

## Criterios de aceite

| Criterio | Status |
|---|---|
| YAMLs para todos os objetos da SUP-2 + dataset bruto | Concluido |
| Rotulos em PT-BR e descricoes em todos os campos | Concluido |
| Metricas certificadas (certified_by = Consilux / Edital Salvador) | Concluido |
| Dataset bruto restrito a Admin com warning_markdown | Concluido |
| provisionar.py idempotente | Concluido |
| Build GTW verde | Concluido |
| Teste de provisionamento no Docker | Pendente — requer docker compose up |
| Metricas validadas contra SQL de referencia | Pendente — requer dados no Superset |

## Metricas certificadas (resumo)

| Dataset | Metricas certificadas |
|---|---|
| mat_passagens_hora | total_passagens, velocidade_media_ponderada, pct_mercosul |
| mat_infracoes_dia | total_infracoes, excesso_medio_ponderado |
| v_alertas | total_alertas, alertas_criticos, pct_descartados, pct_tratados |
| v_equipamentos | total_equipamentos, equipamentos_ativos |
| v_disponibilidade_dia | media_disponibilidade, pct_disponibilidade_global |
| v_sla_latencia_dia | pct_sla_cumprido, media_p95 |
| v_sla_preproc_dia | sla_preproc_medio |
| v_assertividade_dia | assertividade_media, pct_desempate |
| v_preprocessamento | total_recebidos, total_preprocessados, total_descartados, aproveitamento_global, tempo_medio_global, produtividade_media, sla_72h_global |
