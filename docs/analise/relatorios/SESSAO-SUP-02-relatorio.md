# Relatorio SUP-2 — Camada de dados analitica

## Resumo

Criados 2 tabelas materializadas, 8 views e 2 stored procedures no schema
`analise` do SQL Server. Todos os objetos sao somente leitura sobre as tabelas
existentes do GTW — nenhuma tabela legada foi alterada.

## O que foi feito

1. **Migracao** `20261017_analise_camada_dados.sql` com 12 objetos idempotentes
2. **Execucao** no banco DEV (10.0.0.200) — todos criados com sucesso
3. **Validacao** das procedures de carga e contagem de linhas nas views

## Validacao de dados

| Objeto | Linhas | Observacao |
|---|---|---|
| mat_passagens_hora | 0 (producao) / 100 (teste manual) | Ultima passagem em 01/10; janela automatica vazia |
| mat_infracoes_dia | 29 | Infracoes dos ultimos 7 dias |
| v_alertas | 11.555 | Todos os alertas do sistema |
| v_equipamentos | 72 | Locais com area monitorada via JOIN |
| v_areas_monitoradas_geojson | 4 | Areas ativas com GeoJSON |
| v_disponibilidade_dia | 0 | Tabela fonte sem dados (job nao rodou) |
| v_sla_latencia_dia | 6 | 6 dias com dados de SLA |
| v_sla_preproc_dia | 6 | 6 dias com snapshots |
| v_assertividade_dia | 7 | 7 dias (dados de teste do Plano 05) |
| v_preprocessamento | 0 | UUIDs de teste nao existem no VTR real |

## LGPD

Nenhuma view expoe placa, CPF ou dado pessoal. A `v_alertas` exclui
explicitamente o campo placa. As materializacoes agregam dados sem placa.

## Decisoes tomadas nesta sessao

- Procedure de passagens usa janela de 25h (24h reprocesso + 1h corrente)
- Procedure de infracoes usa janela de 7 dias
- `velocidade_media` como coluna computada PERSISTED (eficiente para leitura)
- Views usam LEFT JOIN para tabelas opcionais (config, area, usuario)
- WITH (NOLOCK) em consultas analiticas sobre VTR para nao bloquear pipeline

## Pendencias

- Agendamento: SQL Server Agent job ou Quartz `CargaAnaliseJob` (depende de
  infraestrutura disponivel — pode ser feito na SUP-3 ou sessao dedicada)
- v_preprocessamento: sem dados reais ate que analistas usem o sistema
- v_disponibilidade_dia: sem dados ate que o job de disponibilidade rode

## Build

BUILD SUCCESS (34s)
