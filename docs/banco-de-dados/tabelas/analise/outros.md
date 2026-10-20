# Tabelas — schema `analise` — grupo `outros`

Gerado de GTW_MURALHA_DEV em 2026-10-20. Linhas aproximadas.

## analise.carga_log

Linhas: ~58

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | processo | varchar(100) | N |  |  |  |
| 3 | dt_inicio | datetime2 | N |  |  |  |
| 4 | dt_fim | datetime2 | S |  |  |  |
| 5 | linhas | int | S |  |  |  |
| 6 | erro | varchar(2000) | S |  |  |  |
| 7 | sucesso | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_carga_log_dt` (NONCLUSTERED): dt_inicio
- PK `PK_carga_log` (CLUSTERED): id

## analise.mat_areas_monitoradas

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | nome | nvarchar(200) | N |  |  |  |
| 3 | cor | varchar(20) | S |  |  |  |
| 4 | poligono | nvarchar(max) | S |  |  |  |
| 5 | dt_carga | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- PK `PK_mat_areas_monitoradas` (CLUSTERED): id

## analise.mat_infracoes_dia

Linhas: ~1020

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dia | date | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | local_nome | varchar(100) | N |  |  |  |
| 4 | faixa | tinyint | N |  |  |  |
| 5 | id_enquadramento | int | N |  |  |  |
| 6 | enquadramento | varchar(200) | N |  |  |  |
| 7 | limite | int | S |  |  |  |
| 8 | total | int | N |  |  |  |
| 9 | excesso_medio | decimal(6,2) | S |  |  |  |
| 10 | lat | decimal(10,7) | S |  |  |  |
| 11 | lon | decimal(10,7) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_mid_local` (NONCLUSTERED): id_local, dia
- PK `PK_mat_infracoes_dia` (CLUSTERED): dia, id_local, faixa, id_enquadramento

## analise.mat_passagens_hora

Linhas: ~62116

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | hora | datetime2 | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | local_nome | varchar(100) | N |  |  |  |
| 4 | faixa | tinyint | N |  |  |  |
| 5 | classificacao | char(1) | N |  |  |  |
| 6 | placa_mercosul | bit | N |  |  |  |
| 7 | passagens | int | N |  |  |  |
| 8 | soma_velocidade | bigint | N |  |  |  |
| 9 | velocidade_media | decimal(21,13) | S |  |  |  |
| 10 | lat | decimal(10,7) | S |  |  |  |
| 11 | lon | decimal(10,7) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_mph_local` (NONCLUSTERED): id_local, hora
- PK `PK_mat_passagens_hora` (CLUSTERED): hora, id_local, faixa, classificacao, placa_mercosul

