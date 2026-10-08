# Tabelas — schema `mobilidade` — grupo `outros`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.bloqueio_faixa_posicao

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | tinyint | N |  |  |  |
| 2 | descricao | nvarchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_bloqueio_faixa_posicao` (CLUSTERED): id

**Referenciada por:**
- mobilidade.evento_segmento_faixa.faixa_posicao_id

## mobilidade.bloqueio_tipo

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | tinyint | N |  |  |  |
| 2 | descricao | nvarchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_bloqueio_tipo` (CLUSTERED): id

**Referenciada por:**
- mobilidade.evento_mapa_segmento.tipo_bloqueio_id

## mobilidade.estudo_jam

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | jam_id | nvarchar(255) | N |  |  |  |
| 3 | street | nvarchar(500) | S |  |  |  |
| 4 | is_forward | bit | S |  |  |  |
| 5 | speed_kmh | float | S |  |  |  |
| 6 | length_meters | int | S |  |  |  |
| 7 | severity | int | S |  |  |  |
| 8 | recebido_em | datetime2 | N |  | (getdate()) |  |
| 9 | payload_json | nvarchar(max) | S |  |  |  |
| 10 | payload_waze_json | nvarchar(max) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_estudo_jam_recebido_em` (NONCLUSTERED): recebido_em
- PK `PK_estudo_jam` (CLUSTERED): id

**Referenciada por:**
- mobilidade.estudo_seta.jam_ref

## mobilidade.estudo_seta

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | jam_ref | int | N |  |  |  |
| 3 | ordem | int | N |  |  |  |
| 4 | bearing | float | N |  |  |  |
| 5 | latitude | float | N |  |  |  |
| 6 | longitude | float | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_estudo_seta_jam_ref` (NONCLUSTERED): jam_ref
- PK `PK_estudo_seta` (CLUSTERED): id

**FKs (saída):**
- jam_ref → mobilidade.estudo_jam.id

## mobilidade.segmentos_malha

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | codlog | nvarchar(20) | S |  |  |  |
| 3 | lg_seg_id | nvarchar(50) | S |  |  |  |
| 4 | nome | nvarchar(500) | S |  |  |  |
| 5 | tipo | nvarchar(50) | S |  |  |  |
| 6 | sentido | nvarchar(200) | S |  |  |  |
| 7 | azimute | float | S |  |  |  |
| 8 | classvia | nvarchar(100) | S |  |  |  |
| 9 | distrito_sigla | nvarchar(10) | S |  |  |  |
| 10 | geometry_json | nvarchar(max) | S |  |  |  |
| 11 | lat_min | float | N |  |  |  |
| 12 | lat_max | float | N |  |  |  |
| 13 | lng_min | float | N |  |  |  |
| 14 | lng_max | float | N |  |  |  |
| 15 | fonte | nvarchar(20) | N |  | ('CODLOG') |  |
| 16 | ativo | bit | N |  | ((1)) |  |
| 17 | importado_em | datetime2 | N |  | (getdate()) |  |

**Índices/Chaves:**
- IDX `IX_segmentos_malha_bbox` (NONCLUSTERED): lat_min, lat_max, lng_min, lng_max
- IDX `IX_segmentos_malha_codlog` (NONCLUSTERED): codlog
- PK `PK_segmentos_malha` (CLUSTERED): id

**Referenciada por:**
- mobilidade.evento_mapa_segmento.segmento_id
- mobilidade.waze_corredor_sentido_segmento.segmento_id

