# Tabelas — schema `mobilidade` — grupo `tokens`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.tokens_api_waze_backup

Linhas: ~52

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token | varchar(100) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | ativo | bit | S |  | ((1)) |  |
| 5 | data_criacao | datetime2 | S |  | (sysutcdatetime()) |  |
| 7 | created_at | datetime2 | S |  | (sysutcdatetime()) |  |
| 8 | limite_requisicoes_mes | int | S |  | ((50)) |  |

**Índices/Chaves:**
- PK `PK__tokens_a__3213E83F7D7EFCB6` (CLUSTERED): id

## mobilidade.tokens_waze

Linhas: ~10

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao.token_id
- mobilidade.schedule_wazedirect.token_id
- mobilidade.schedule_webninja.token_id

## mobilidade.tokens_waze_backup

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | fonte | varchar(20) | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_backup` (CLUSTERED): id

## mobilidade.tokens_waze_test

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test.token_id
- mobilidade.schedule_wazedirect_test.token_id
- mobilidade.schedule_webninja_test.token_id

## mobilidade.tokens_waze_test2

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test2.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test2.token_id
- mobilidade.schedule_wazedirect_test2.token_id
- mobilidade.schedule_webninja_test2.token_id

## mobilidade.tokens_waze_test3

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test3.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test3.token_id
- mobilidade.schedule_wazedirect_test3.token_id
- mobilidade.schedule_webninja_test3.token_id

## mobilidade.tokens_waze_test4

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test4.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test4.token_id
- mobilidade.schedule_wazedirect_test4.token_id
- mobilidade.schedule_webninja_test4.token_id

## mobilidade.tokens_waze_test5

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test5.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test5.token_id
- mobilidade.schedule_wazedirect_test5.token_id
- mobilidade.schedule_webninja_test5.token_id

## mobilidade.tokens_waze_test6

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | id_fonte | int | N |  |  |  |
| 4 | token | varchar(200) | N |  |  |  |
| 5 | url | nvarchar(500) | S |  |  |  |
| 6 | descricao | varchar(200) | S |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |
| 8 | limite_requisicoes_mes | int | S |  |  |  |
| 9 | data_criacao | datetime2 | N |  | (sysutcdatetime()) |  |

**Índices/Chaves:**
- PK `PK_tokens_waze_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test6.id_fonte

**Referenciada por:**
- mobilidade.controle_importacao_test6.token_id
- mobilidade.schedule_wazedirect_test6.token_id
- mobilidade.schedule_webninja_test6.token_id

