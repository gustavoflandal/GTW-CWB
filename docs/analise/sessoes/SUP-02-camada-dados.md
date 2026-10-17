# SUP-2 — Camada de dados analitica (views e materializacoes)

## Escopo

Criar objetos no SQL Server (schema `analise`) que substituem o catalogo e o
`MontadorSql` do builder por objetos legiveis por qualquer ferramenta (Superset,
Excel, Power BI, sqlcmd).

## Objetos criados

### Tabelas materializadas
- `analise.mat_passagens_hora` — agregacao horaria de `muralha.veiculo_tempo_real` por local/faixa/classificacao
- `analise.mat_infracoes_dia` — agregacao diaria de `dbo.infracao` por local/faixa/enquadramento

### Views
- `analise.v_alertas` — alertas sem dados pessoais (LGPD: sem placa)
- `analise.v_equipamentos` — cadastro de locais com coordenadas e area monitorada
- `analise.v_areas_monitoradas_geojson` — poligonos para camada deck.gl
- `analise.v_disponibilidade_dia` — disponibilidade diaria (Plano 18)
- `analise.v_sla_latencia_dia` — SLA de latencia (Plano 07)
- `analise.v_sla_preproc_dia` — SLA pre-processamento 72h (Plano 10)
- `analise.v_assertividade_dia` — taxa de concordancia dupla analise (Plano 05)
- `analise.v_preprocessamento` — 14 alineas do TR 5.5.3.1

### Stored Procedures
- `analise.spu_carga_passagens_hora` — carga incremental (25h janela)
- `analise.spu_carga_infracoes_dia` — carga incremental (7 dias janela)

## Tarefas

- [x] Criar branch `feat/sup-02-camada-dados`
- [x] Criar migracao `20261017_analise_camada_dados.sql`
- [x] Executar migracao no DEV (10.0.0.200)
- [x] Validar procedures de carga
- [x] Validar contagem de linhas nas views
- [x] Build do GTW verde
- [x] Criar documentacao da sessao
- [x] Commit e PR

## Criterios de aceite

| Criterio | Status |
|---|---|
| Scripts idempotentes (IF NOT EXISTS / DROP IF EXISTS) | Concluido |
| Bloco -- TESTE no script | Concluido |
| Nenhum campo de dado pessoal (placa, CPF) nas views | Concluido |
| Estimativa de tempo de carga documentada | Concluido — <1s para ambas procedures |
| Todas as views retornam dados | Concluido (v_disponibilidade vazia: fonte sem dados) |
| Build GTW verde | Concluido |

## Estimativa de tempo de carga

| Procedure | Linhas (teste) | Tempo |
|---|---|---|
| spu_carga_passagens_hora | 100 (1 semana real) | <1s |
| spu_carga_infracoes_dia | 29 (7 dias) | <1s |

Em producao com dados continuos, a carga horaria deve levar 1-3s (estimativa
com ~44 locais x 2 faixas x 10 classificacoes x 2 mercosul = ~1.760 combinacoes/hora).
