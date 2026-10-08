# Tabelas — schema `mobilidade` — grupo `regra`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.regra_trafego

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego.id

## mobilidade.regra_trafego_test

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test.id

## mobilidade.regra_trafego_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test2.id

## mobilidade.regra_trafego_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test3.id

## mobilidade.regra_trafego_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test4.id

## mobilidade.regra_trafego_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test5.id

## mobilidade.regra_trafego_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo_regra_id | int | N |  |  |  |
| 4 | detalhes | nvarchar(255) | N |  |  |  |
| 5 | horarios | nvarchar(50) | S |  |  |  |
| 6 | geometria | nvarchar(max) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_regra_trafego_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade
- tipo_regra_id → mobilidade.tipo_regra_trafego_test6.id

