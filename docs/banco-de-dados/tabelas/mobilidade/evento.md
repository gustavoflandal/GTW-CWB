# Tabelas — schema `mobilidade` — grupo `evento`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.evento_mapa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | S |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | datetime | S |  |  |  |
| 11 | nome_via | nvarchar(255) | S |  |  |  |
| 12 | data_hora_inicio | datetime2 | S |  |  |  |
| 13 | data_hora_fim | datetime2 | S |  |  |  |
| 14 | regiao | nvarchar(100) | S |  |  |  |
| 15 | tipo_id | int | S |  |  |  |
| 16 | data_criacao | datetime | S |  |  |  |
| 17 | data_importacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades.id_cidade
- tipo_id → mobilidade.sis_bloqueio_tipo.id

**Referenciada por:**
- mobilidade.evento_mapa_segmento.evento_id
- mobilidade.evento_mapa_via.evento_id

## mobilidade.evento_mapa_segmento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | evento_id | int | N |  |  |  |
| 3 | segmento_id | int | N |  |  |  |
| 4 | ordem | int | N |  | ((1)) |  |
| 5 | is_forward | bit | S |  |  |  |
| 6 | criado_em | datetime2 | N |  | (getdate()) |  |
| 7 | tipo_bloqueio_id | tinyint | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_evento_mapa_segmento_evento_id` (NONCLUSTERED): evento_id
- IDX `IX_evento_mapa_segmento_segmento_id` (NONCLUSTERED): segmento_id
- PK `PK_evento_mapa_segmento` (CLUSTERED): id

**FKs (saída):**
- evento_id → mobilidade.evento_mapa.id
- segmento_id → mobilidade.segmentos_malha.id
- tipo_bloqueio_id → mobilidade.bloqueio_tipo.id

**Referenciada por:**
- mobilidade.evento_segmento_faixa.evento_mapa_segmento_id

## mobilidade.evento_mapa_test

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test.id_cidade

## mobilidade.evento_mapa_test2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test2` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test2.id_cidade

## mobilidade.evento_mapa_test3

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test3` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test3.id_cidade

## mobilidade.evento_mapa_test4

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test4` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test4.id_cidade

## mobilidade.evento_mapa_test5

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test5` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test5.id_cidade

## mobilidade.evento_mapa_test6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | cidade_id | int | S |  |  |  |
| 3 | nome_evento | nvarchar(255) | N |  |  |  |
| 4 | acao | nvarchar(255) | N |  |  |  |
| 5 | data | nvarchar(50) | N |  |  |  |
| 6 | detalhes | nvarchar(255) | N |  |  |  |
| 7 | latitude | float | N |  |  |  |
| 8 | longitude | float | N |  |  |  |
| 9 | ativo | bit | N |  |  |  |
| 10 | data_resolucao | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_test6` (CLUSTERED): id

**FKs (saída):**
- cidade_id → mobilidade.sis_cidades_test6.id_cidade

## mobilidade.evento_mapa_via

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | evento_id | int | N |  |  |  |
| 3 | nome_via | nvarchar(255) | N |  |  |  |
| 4 | latitude | decimal(10,7) | S |  |  |  |
| 5 | longitude | decimal(10,7) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_evento_mapa_via` (CLUSTERED): id

**FKs (saída):**
- evento_id → mobilidade.evento_mapa.id

## mobilidade.evento_segmento_faixa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | evento_mapa_segmento_id | int | N |  |  |  |
| 3 | faixa_posicao_id | tinyint | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_evento_segmento_faixa_segmento_id` (NONCLUSTERED): evento_mapa_segmento_id
- PK `PK_evento_segmento_faixa` (CLUSTERED): id
- UNIQUE `UQ_esf_segmento_faixa` (NONCLUSTERED): evento_mapa_segmento_id, faixa_posicao_id

**FKs (saída):**
- faixa_posicao_id → mobilidade.bloqueio_faixa_posicao.id
- evento_mapa_segmento_id → mobilidade.evento_mapa_segmento.id

