# Tabelas — schema `mobilidade` — grupo `congestionamentos`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.congestionamentos

Linhas: ~4883

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congestionamentos_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- IDX `IX_congestionamentos_controle` (NONCLUSTERED): controle_id
- PK `PK_congestionamentos` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- controle_id → mobilidade.controle_importacao.id
- id_fonte → mobilidade.sis_fontes_mapa.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas.congestionamento_id
- mobilidade.congestionamentos_segmentos.congestionamento_id

## mobilidade.congestionamentos_atual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | is_forward | bit | S |  |  |  |
| 28 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 29 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congs_atual_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- PK `PK_congestionamentos_atual` (CLUSTERED): id
- UNIQUE `UQ_congs_atual_jam_id` (NONCLUSTERED): cidade_id, jam_id

## mobilidade.congestionamentos_backup

Linhas: ~2539

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | fonte | varchar(20) | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_cong_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- IDX `ix_cong_fonte` (NONCLUSTERED): fonte
- PK `PK_congestionamentos_backup` (CLUSTERED): id

## mobilidade.congestionamentos_coordenadas

Linhas: ~36915

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coordenadas_congestionamento` (NONCLUSTERED): congestionamento_id, ordem
- PK `PK_cong_coordenadas` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos.id

## mobilidade.congestionamentos_coordenadas_atual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coord_atual_cong_id` (NONCLUSTERED): congestionamento_id
- PK `PK_congestionamentos_coordenadas_atual` (CLUSTERED): id

## mobilidade.congestionamentos_coordenadas_backup

Linhas: ~20767

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_coordenadas_backup` (CLUSTERED): id

## mobilidade.congestionamentos_coordenadas_test

Linhas: ~1627640

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_coordenadas_test` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test.id

## mobilidade.congestionamentos_coordenadas_test2

Linhas: ~85867

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_coordenadas_test2` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test2.id

## mobilidade.congestionamentos_coordenadas_test3

Linhas: ~25946

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coordenadas_test3_congestionamento` (NONCLUSTERED): congestionamento_id, ordem
- PK `PK_cong_coordenadas_test3` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test3.id

## mobilidade.congestionamentos_coordenadas_test4

Linhas: ~55226

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coordenadas_test4_congestionamento` (NONCLUSTERED): congestionamento_id, ordem
- PK `PK_cong_coordenadas_test4` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test4.id

## mobilidade.congestionamentos_coordenadas_test5

Linhas: ~204766

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coordenadas_test5_congestionamento` (NONCLUSTERED): congestionamento_id, ordem
- PK `PK_cong_coordenadas_test5` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test5.id

## mobilidade.congestionamentos_coordenadas_test6

Linhas: ~303929

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_coordenadas_test6_congestionamento` (NONCLUSTERED): congestionamento_id, ordem
- PK `PK_cong_coordenadas_test6` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test6.id

## mobilidade.congestionamentos_coordenadas_waze_backup

Linhas: ~587625

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | latitude | float | S |  |  |  |
| 4 | longitude | float | S |  |  |  |
| 5 | ordem | int | S |  |  |  |
| 6 | created_at | datetime2 | S |  | (sysutcdatetime()) |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK__congesti__3213E83F5E2DA316` (CLUSTERED): id

## mobilidade.congestionamentos_segmentos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos.id

## mobilidade.congestionamentos_segmentos_atual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cong_seg_atual_cong_id` (NONCLUSTERED): congestionamento_id
- PK `PK_congestionamentos_segmentos_atual` (CLUSTERED): id

## mobilidade.congestionamentos_segmentos_test

Linhas: ~95235

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test.id

## mobilidade.congestionamentos_segmentos_test2

Linhas: ~44837

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test2` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test2.id

## mobilidade.congestionamentos_segmentos_test3

Linhas: ~13487

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test3` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test3.id

## mobilidade.congestionamentos_segmentos_test4

Linhas: ~29345

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test4` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test4.id

## mobilidade.congestionamentos_segmentos_test5

Linhas: ~106616

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test5` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test5.id

## mobilidade.congestionamentos_segmentos_test6

Linhas: ~153800

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | congestionamento_id | int | N |  |  |  |
| 3 | ordem | int | S |  |  |  |
| 4 | from_node | bigint | S |  |  |  |
| 5 | to_node | bigint | S |  |  |  |
| 6 | is_forward | bit | S |  |  |  |
| 7 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cong_segmentos_test6` (CLUSTERED): id

**FKs (saída):**
- congestionamento_id → mobilidade.congestionamentos_test6.id

## mobilidade.congestionamentos_test

Linhas: ~163558

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_cong_test_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- IDX `ix_cong_test_fonte` (NONCLUSTERED): id_fonte
- PK `PK_congestionamentos_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade
- controle_id → mobilidade.controle_importacao_test.id
- id_fonte → mobilidade.sis_fontes_mapa_test.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test.congestionamento_id
- mobilidade.congestionamentos_segmentos_test.congestionamento_id

## mobilidade.congestionamentos_test2

Linhas: ~7695

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- PK `PK_congestionamentos_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade
- controle_id → mobilidade.controle_importacao_test2.id
- id_fonte → mobilidade.sis_fontes_mapa_test2.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test2.congestionamento_id
- mobilidade.congestionamentos_segmentos_test2.congestionamento_id

## mobilidade.congestionamentos_test3

Linhas: ~2994

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congestionamentos_test3_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- IDX `IX_congestionamentos_test3_controle` (NONCLUSTERED): controle_id
- PK `PK_congestionamentos_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade
- controle_id → mobilidade.controle_importacao_test3.id
- id_fonte → mobilidade.sis_fontes_mapa_test3.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test3.congestionamento_id
- mobilidade.congestionamentos_segmentos_test3.congestionamento_id

## mobilidade.congestionamentos_test4

Linhas: ~5607

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congestionamentos_test4_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- IDX `IX_congestionamentos_test4_controle` (NONCLUSTERED): controle_id
- PK `PK_congestionamentos_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade
- controle_id → mobilidade.controle_importacao_test4.id
- id_fonte → mobilidade.sis_fontes_mapa_test4.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test4.congestionamento_id
- mobilidade.congestionamentos_segmentos_test4.congestionamento_id

## mobilidade.congestionamentos_test5

Linhas: ~18818

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congestionamentos_test5_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- IDX `IX_congestionamentos_test5_controle` (NONCLUSTERED): controle_id
- PK `PK_congestionamentos_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade
- controle_id → mobilidade.controle_importacao_test5.id
- id_fonte → mobilidade.sis_fontes_mapa_test5.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test5.congestionamento_id
- mobilidade.congestionamentos_segmentos_test5.congestionamento_id

## mobilidade.congestionamentos_test6

Linhas: ~34254

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | jam_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | level | int | S |  |  |  |
| 8 | severity | int | S |  |  |  |
| 9 | speed_kmh | float | S |  |  |  |
| 10 | length_meters | int | S |  |  |  |
| 11 | delay_seconds | int | S |  |  |  |
| 12 | publish_datetime_utc | datetime2 | S |  |  |  |
| 13 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 14 | update_datetime_utc | datetime2 | S |  |  |  |
| 15 | update_datetime_brasilia | datetime2 | S |  |  |  |
| 16 | country | varchar(50) | S |  |  |  |
| 17 | city | varchar(100) | S |  |  |  |
| 18 | street | nvarchar(200) | S |  |  |  |
| 19 | block_alert_id | varchar(100) | S |  |  |  |
| 20 | block_alert_type | varchar(100) | S |  |  |  |
| 21 | block_alert_description | nvarchar(500) | S |  |  |  |
| 22 | block_start_datetime_utc | datetime2 | S |  |  |  |
| 23 | block_start_datetime_brasilia | datetime2 | S |  |  |  |
| 24 | start_node | nvarchar(255) | S |  |  |  |
| 25 | end_node | nvarchar(255) | S |  |  |  |
| 26 | road_type | int | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |
| 29 | is_forward | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_congestionamentos_test6_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- IDX `IX_congestionamentos_test6_controle` (NONCLUSTERED): controle_id
- PK `PK_congestionamentos_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade
- controle_id → mobilidade.controle_importacao_test6.id
- id_fonte → mobilidade.sis_fontes_mapa_test6.id_fonte

**Referenciada por:**
- mobilidade.congestionamentos_coordenadas_test6.congestionamento_id
- mobilidade.congestionamentos_segmentos_test6.congestionamento_id

