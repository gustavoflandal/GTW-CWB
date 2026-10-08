# Tabelas — schema `mobilidade` — grupo `waze`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.waze_corredor

Linhas: ~15

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | via_nome | nvarchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_waze_corredor` (CLUSTERED): id

**Referenciada por:**
- mobilidade.waze_corredor_alias.corredor_id
- mobilidade.waze_corredor_sentido.corredor_id

## mobilidade.waze_corredor_alias

Linhas: ~46

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | corredor_id | int | N |  |  |  |
| 3 | alias | nvarchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_waze_corredor_alias` (CLUSTERED): id

**FKs (saída):**
- corredor_id → mobilidade.waze_corredor.id

## mobilidade.waze_corredor_sentido

Linhas: ~30

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | corredor_id | int | N |  |  |  |
| 3 | bearing_min | int | S |  |  |  |
| 4 | bearing_max | int | S |  |  |  |
| 5 | nome | nvarchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_waze_corredor_sentido` (CLUSTERED): id

**FKs (saída):**
- corredor_id → mobilidade.waze_corredor.id

**Referenciada por:**
- mobilidade.waze_corredor_sentido_segmento.sentido_id

## mobilidade.waze_corredor_sentido_segmento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | sentido_id | int | N |  |  |  |
| 3 | segmento_id | int | N |  |  |  |
| 4 | is_forward | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_waze_corredor_sentido_segmento` (CLUSTERED): id
- UNIQUE `UQ_wcss` (NONCLUSTERED): sentido_id, segmento_id

**FKs (saída):**
- segmento_id → mobilidade.segmentos_malha.id
- sentido_id → mobilidade.waze_corredor_sentido.id

