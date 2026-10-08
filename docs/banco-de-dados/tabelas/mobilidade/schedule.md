# Tabelas — schema `mobilidade` — grupo `schedule`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.schedule_wazedirect

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze.id

## mobilidade.schedule_wazedirect_test

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | intervalo_minutos | int | S |  |  |  |
| 6 | ativo | bit | N |  | ((1)) |  |
| 7 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test.id

## mobilidade.schedule_wazedirect_test2

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | intervalo_minutos | int | S |  |  |  |
| 6 | ativo | bit | N |  | ((1)) |  |
| 7 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test2` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test2.id

## mobilidade.schedule_wazedirect_test3

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | intervalo_minutos | int | S |  |  |  |
| 6 | ativo | bit | N |  | ((1)) |  |
| 7 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test3` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test3.id

## mobilidade.schedule_wazedirect_test4

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test4` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test4.id

## mobilidade.schedule_wazedirect_test5

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test5` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test5.id

## mobilidade.schedule_wazedirect_test6

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | horarios | varchar(200) | S |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_wazedirect_test6` (CLUSTERED): id

**FKs (saída):**
- token_id → mobilidade.tokens_waze_test6.id

## mobilidade.schedule_webninja

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja.id_area
- token_id → mobilidade.tokens_waze.id

## mobilidade.schedule_webninja_test

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test.id_area
- token_id → mobilidade.tokens_waze_test.id

## mobilidade.schedule_webninja_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test2` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test2.id_area
- token_id → mobilidade.tokens_waze_test2.id

## mobilidade.schedule_webninja_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test3` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test3.id_area
- token_id → mobilidade.tokens_waze_test3.id

## mobilidade.schedule_webninja_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test4` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test4.id_area
- token_id → mobilidade.tokens_waze_test4.id

## mobilidade.schedule_webninja_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test5` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test5.id_area
- token_id → mobilidade.tokens_waze_test5.id

## mobilidade.schedule_webninja_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area | int | N |  |  |  |
| 3 | token_id | int | N |  |  |  |
| 4 | tipo | varchar(10) | N |  |  |  |
| 5 | horarios | varchar(200) | S |  |  |  |
| 6 | intervalo_minutos | int | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | descricao | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_schedule_webninja_test6` (CLUSTERED): id

**FKs (saída):**
- id_area → mobilidade.area_webninja_test6.id_area
- token_id → mobilidade.tokens_waze_test6.id

