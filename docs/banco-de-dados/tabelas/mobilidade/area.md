# Tabelas — schema `mobilidade` — grupo `area`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.area_webninja

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja.id_area

## mobilidade.area_webninja_test

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test.id_area

## mobilidade.area_webninja_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test2` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test2.id_area

## mobilidade.area_webninja_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test3` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test3.id_area

## mobilidade.area_webninja_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test4` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test4.id_area

## mobilidade.area_webninja_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test5` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test5.id_area

## mobilidade.area_webninja_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_area | int | N | S |  |  |
| 2 | cidade_id | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | lat_bottom | float | N |  |  |  |
| 5 | lng_left | float | N |  |  |  |
| 6 | lat_top | float | N |  |  |  |
| 7 | lng_right | float | N |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_area_webninja_test6` (CLUSTERED): id_area

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade

**Referenciada por:**
- mobilidade.schedule_webninja_test6.id_area

