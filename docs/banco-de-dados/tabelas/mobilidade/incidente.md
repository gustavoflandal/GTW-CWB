# Tabelas — schema `mobilidade` — grupo `incidente`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.incidente

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade

## mobilidade.incidente_test

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade

## mobilidade.incidente_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade

## mobilidade.incidente_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade

## mobilidade.incidente_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade

## mobilidade.incidente_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade

## mobilidade.incidente_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | tipo | nvarchar(50) | N |  |  |  |
| 4 | data | nvarchar(50) | N |  |  |  |
| 5 | detalhes | nvarchar(255) | N |  |  |  |
| 6 | gravidade | nvarchar(50) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | endereco | nvarchar(255) | N |  |  |  |
| 10 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_incidente_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade

