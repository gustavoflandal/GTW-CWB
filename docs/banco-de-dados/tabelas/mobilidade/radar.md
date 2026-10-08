# Tabelas — schema `mobilidade` — grupo `radar`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.radar

Linhas: ~1188

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_radar | int | N | S |  |  |
| 2 | id_local_origem | int | N |  |  |  |
| 3 | cidade_id | int | S |  |  |  |
| 4 | descricao | varchar(150) | N |  |  |  |
| 5 | localidade | varchar(50) | S |  |  |  |
| 6 | latitude | decimal(19,17) | N |  |  |  |
| 7 | longitude | decimal(19,17) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_radar` (CLUSTERED): id_radar
- UNIQUE `UQ_radar_local_origem` (NONCLUSTERED): id_local_origem

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade

**Referenciada por:**
- mobilidade.radar_faixa.id_radar

## mobilidade.radar_enquadramento

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_radar_enquadramento | int | N | S |  |  |
| 2 | extensao | varchar(2) | N |  |  |  |
| 3 | descricao_apait | varchar(100) | N |  |  |  |
| 4 | apelido | varchar(30) | S |  |  |  |
| 5 | codigo_enquadramento | int | N |  |  |  |
| 6 | descricao | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_radar_enquadramento` (CLUSTERED): id_radar_enquadramento
- UNIQUE `UQ_radar_enquadramento` (NONCLUSTERED): extensao, codigo_enquadramento

## mobilidade.radar_faixa

Linhas: ~2818

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_radar_faixa | int | N | S |  |  |
| 2 | id_radar | int | N |  |  |  |
| 3 | id_equipamento_origem | int | N |  |  |  |
| 4 | numero_serie | varchar(15) | S |  |  |  |
| 5 | id_pista | tinyint | S |  |  |  |
| 6 | faixa | int | S |  |  |  |
| 7 | descricao | varchar(100) | S |  |  |  |
| 8 | faixa_exclusiva | bit | S |  |  |  |
| 9 | entre_faixa | int | S |  |  |  |
| 10 | velocidade_leve | smallint | S |  |  |  |
| 11 | velocidade_pesado | smallint | S |  |  |  |
| 12 | tipo_equipamento | int | S |  |  |  |
| 13 | data_inicio_operacao | datetime | S |  |  |  |
| 14 | data_fim_operacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_radar_faixa` (CLUSTERED): id_radar_faixa
- UNIQUE `UQ_radar_faixa_equip_origem` (NONCLUSTERED): id_equipamento_origem

**FKs (saída):**
- id_radar → mobilidade.radar.id_radar
- tipo_equipamento → mobilidade.radar_tipo_equipamento.id_tipo_equipamento

**Referenciada por:**
- mobilidade.radar_faixa_extensao.id_radar_faixa

## mobilidade.radar_faixa_extensao

Linhas: ~7232

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_radar_faixa | int | N |  |  |  |
| 2 | extensao | varchar(2) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_radar_faixa_extensao` (CLUSTERED): id_radar_faixa, extensao

**FKs (saída):**
- id_radar_faixa → mobilidade.radar_faixa.id_radar_faixa

## mobilidade.radar_test

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade

## mobilidade.radar_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade

## mobilidade.radar_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade

## mobilidade.radar_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade

## mobilidade.radar_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade

## mobilidade.radar_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | latitude | float | N |  |  |  |
| 4 | longitude | float | N |  |  |  |
| 5 | rua | nvarchar(255) | N |  |  |  |
| 6 | velocidade_maxima | int | N |  |  |  |
| 7 | tipo | nvarchar(50) | N |  |  |  |
| 8 | sentido | nvarchar(100) | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_radar_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade

## mobilidade.radar_tipo_equipamento

Linhas: ~10

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_equipamento | int | N |  |  |  |
| 2 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_radar_tipo_equipamento` (CLUSTERED): id_tipo_equipamento

**Referenciada por:**
- mobilidade.radar_faixa.tipo_equipamento

