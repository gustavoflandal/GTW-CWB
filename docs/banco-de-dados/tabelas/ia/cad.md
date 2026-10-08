# Tabelas — schema `ia` — grupo `cad`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## ia.cad_cor

Linhas: ~14

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cor` (CLUSTERED): id

**Referenciada por:**
- ia.veiculo_caracteristica.id_cor

## ia.cad_marca

Linhas: ~26

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_marca` (CLUSTERED): id

**Referenciada por:**
- ia.veiculo_caracteristica.id_marca

## ia.cad_modelo

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_modelo` (CLUSTERED): id

**Referenciada por:**
- ia.veiculo_caracteristica.id_modelo

## ia.cad_tipo_especial

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_especial` (CLUSTERED): id

**Referenciada por:**
- ia.veiculo_caracteristica.id_tipo_especial

