# Tabelas — schema `mobilidade` — grupo `alertas`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.alertas

Linhas: ~5365

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- PK `PK_alertas` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- controle_id → mobilidade.controle_importacao.id
- id_fonte → mobilidade.sis_fontes_mapa.id_fonte

## mobilidade.alertas_atual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_atual_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- PK `PK_alertas_atual` (CLUSTERED): id
- UNIQUE `UQ_alertas_atual_alert_id` (NONCLUSTERED): cidade_id, alert_id

## mobilidade.alertas_backup

Linhas: ~2366

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | fonte | varchar(20) | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_alertas_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- IDX `ix_alertas_fonte` (NONCLUSTERED): fonte
- PK `PK_alertas_backup` (CLUSTERED): id

## mobilidade.alertas_test

Linhas: ~260336

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_alertas_test_cidade_controle` (NONCLUSTERED): cidade_id, controle_id
- IDX `ix_alertas_test_fonte` (NONCLUSTERED): id_fonte
- PK `PK_alertas_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade
- controle_id → mobilidade.controle_importacao_test.id
- id_fonte → mobilidade.sis_fontes_mapa_test.id_fonte

## mobilidade.alertas_test2

Linhas: ~7509

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_alertas_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade
- controle_id → mobilidade.controle_importacao_test2.id
- id_fonte → mobilidade.sis_fontes_mapa_test2.id_fonte

## mobilidade.alertas_test3

Linhas: ~6687

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_test3_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- PK `PK_alertas_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade
- controle_id → mobilidade.controle_importacao_test3.id
- id_fonte → mobilidade.sis_fontes_mapa_test3.id_fonte

## mobilidade.alertas_test4

Linhas: ~9172

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_test4_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- PK `PK_alertas_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade
- controle_id → mobilidade.controle_importacao_test4.id
- id_fonte → mobilidade.sis_fontes_mapa_test4.id_fonte

## mobilidade.alertas_test5

Linhas: ~29362

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_test5_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- PK `PK_alertas_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade
- controle_id → mobilidade.controle_importacao_test5.id
- id_fonte → mobilidade.sis_fontes_mapa_test5.id_fonte

## mobilidade.alertas_test6

Linhas: ~59415

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | controle_id | int | S |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | id_fonte | int | S |  |  |  |
| 5 | alert_id | varchar(100) | S |  |  |  |
| 6 | type | varchar(50) | S |  |  |  |
| 7 | subtype | varchar(100) | S |  |  |  |
| 8 | reported_by | varchar(100) | S |  |  |  |
| 9 | description | nvarchar(500) | S |  |  |  |
| 10 | image | nvarchar(500) | S |  |  |  |
| 11 | publish_datetime_utc | datetime2 | S |  |  |  |
| 12 | publish_datetime_brasilia | datetime2 | S |  |  |  |
| 13 | country | varchar(50) | S |  |  |  |
| 14 | city | varchar(100) | S |  |  |  |
| 15 | street | nvarchar(200) | S |  |  |  |
| 16 | latitude | float | S |  |  |  |
| 17 | longitude | float | S |  |  |  |
| 18 | num_thumbs_up | int | S |  |  |  |
| 19 | alert_reliability | int | S |  |  |  |
| 20 | alert_confidence | int | S |  |  |  |
| 21 | near_by | nvarchar(200) | S |  |  |  |
| 22 | num_comments | int | S |  |  |  |
| 23 | road_type | int | S |  |  |  |
| 24 | magvar | int | S |  |  |  |
| 25 | severity | nvarchar(20) | S |  |  |  |
| 26 | report_description | nvarchar(500) | S |  |  |  |
| 27 | created_at | datetime2 | N |  | (sysutcdatetime()) |  |
| 28 | created_at_brasilia | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alertas_test6_cidade_fonte_controle` (NONCLUSTERED): cidade_id, id_fonte, controle_id
- PK `PK_alertas_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade
- controle_id → mobilidade.controle_importacao_test6.id
- id_fonte → mobilidade.sis_fontes_mapa_test6.id_fonte

