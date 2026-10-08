# Tabelas — schema `mobilidade` — grupo `controle`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.controle_importacao

Linhas: ~150

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |
| 12 | duracao_segundos | decimal(10,2) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_controle_importacao_sucesso_data` (NONCLUSTERED): sucesso, data_importacao_brasilia
- PK `PK_controle_importacao` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa.id_fonte
- token_id → mobilidade.tokens_waze.id

**Referenciada por:**
- mobilidade.alertas.controle_id
- mobilidade.congestionamentos.controle_id

## mobilidade.controle_importacao_backup

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | fonte | varchar(20) | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_controle_cidade_fonte` (NONCLUSTERED): cidade_id, fonte
- PK `PK_controle_importacao_backup` (CLUSTERED): id

## mobilidade.controle_importacao_test

Linhas: ~130

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_controle_test_cidade_fonte` (NONCLUSTERED): cidade_id, id_fonte
- PK `PK_controle_importacao_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test.id_fonte
- token_id → mobilidade.tokens_waze_test.id

**Referenciada por:**
- mobilidade.alertas_test.controle_id
- mobilidade.congestionamentos_test.controle_id

## mobilidade.controle_importacao_test2

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_controle_importacao_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test2.id_fonte
- token_id → mobilidade.tokens_waze_test2.id

**Referenciada por:**
- mobilidade.alertas_test2.controle_id
- mobilidade.congestionamentos_test2.controle_id

## mobilidade.controle_importacao_test3

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_controle_importacao_test3_sucesso_data` (NONCLUSTERED): sucesso, data_importacao_brasilia
- PK `PK_controle_importacao_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test3.id_fonte
- token_id → mobilidade.tokens_waze_test3.id

**Referenciada por:**
- mobilidade.alertas_test3.controle_id
- mobilidade.congestionamentos_test3.controle_id

## mobilidade.controle_importacao_test4

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |
| 12 | duracao_segundos | decimal(10,2) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_controle_importacao_test4_sucesso_data` (NONCLUSTERED): sucesso, data_importacao_brasilia
- PK `PK_controle_importacao_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test4.id_fonte
- token_id → mobilidade.tokens_waze_test4.id

**Referenciada por:**
- mobilidade.alertas_test4.controle_id
- mobilidade.congestionamentos_test4.controle_id

## mobilidade.controle_importacao_test5

Linhas: ~11

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |
| 12 | duracao_segundos | decimal(10,2) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_controle_importacao_test5_sucesso_data` (NONCLUSTERED): sucesso, data_importacao_brasilia
- PK `PK_controle_importacao_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test5.id_fonte
- token_id → mobilidade.tokens_waze_test5.id

**Referenciada por:**
- mobilidade.alertas_test5.controle_id
- mobilidade.congestionamentos_test5.controle_id

## mobilidade.controle_importacao_test6

Linhas: ~31

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | token_id | int | S |  |  |  |
| 3 | cidade_id | int | N |  |  |  |
| 4 | id_fonte | int | N |  |  |  |
| 5 | area_nome | varchar(100) | S |  |  |  |
| 6 | data_importacao_utc | datetime2 | N |  | (sysutcdatetime()) |  |
| 7 | data_importacao_brasilia | datetime2 | S |  |  |  |
| 8 | quantidade_alertas | int | N |  | ((0)) |  |
| 9 | quantidade_congestionamentos | int | N |  | ((0)) |  |
| 10 | sucesso | bit | N |  | ((1)) |  |
| 11 | mensagem_erro | nvarchar(500) | S |  |  |  |
| 12 | duracao_segundos | decimal(10,2) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_controle_importacao_test6_sucesso_data` (NONCLUSTERED): sucesso, data_importacao_brasilia
- PK `PK_controle_importacao_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade
- id_fonte → mobilidade.sis_fontes_mapa_test6.id_fonte
- token_id → mobilidade.tokens_waze_test6.id

**Referenciada por:**
- mobilidade.alertas_test6.controle_id
- mobilidade.congestionamentos_test6.controle_id

## mobilidade.controle_requisicoes_waze_backup

Linhas: ~2378

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | data_requisicao_utc | datetime2 | S |  | (sysutcdatetime()) |  |
| 3 | data_requisicao_brasilia | datetime2 | S |  |  |  |
| 4 | token_id | int | N |  |  |  |
| 5 | quantidade_alertas | int | S |  | ((0)) |  |
| 6 | quantidade_congestionamentos | int | S |  | ((0)) |  |
| 7 | sucesso | bit | S |  | ((1)) |  |
| 8 | mensagem_erro | nvarchar(500) | S |  |  |  |
| 9 | area_nome | varchar(100) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__controle__3213E83FA8B5B9AC` (CLUSTERED): id

