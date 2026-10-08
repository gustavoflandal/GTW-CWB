# Tabelas — schema `mobilidade` — grupo `sis`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## mobilidade.sis_bloqueio_categoria

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | nvarchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_bloqueio_categoria` (CLUSTERED): id

**Referenciada por:**
- mobilidade.sis_bloqueio_tipo.categoria_id

## mobilidade.sis_bloqueio_tipo

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | categoria_id | int | N |  |  |  |
| 3 | nome | nvarchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_bloqueio_tipo` (CLUSTERED): id

**FKs (saída):**
- categoria_id → mobilidade.sis_bloqueio_categoria.id

**Referenciada por:**
- mobilidade.evento_mapa.tipo_id

## mobilidade.sis_cidades

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados.id_estado

**Referenciada por:**
- mobilidade.alertas.cidade_id
- mobilidade.area_webninja.cidade_id
- mobilidade.buraco.cidade_id
- mobilidade.congestionamentos.cidade_id
- mobilidade.controle_importacao.cidade_id
- mobilidade.evento_mapa.cidade_id
- mobilidade.incidente.cidade_id
- mobilidade.radar.cidade_id
- mobilidade.regra_trafego.cidade_id
- mobilidade.sis_grupos_cidades.id_cidade
- mobilidade.tokens_waze.cidade_id
- mobilidade.via.cidade_id

## mobilidade.sis_cidades_test

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_sis_cidades_test_estado` (NONCLUSTERED): id_estado
- PK `PK_sis_cidades_test` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test.id_estado

**Referenciada por:**
- mobilidade.alertas_test.cidade_id
- mobilidade.area_webninja_test.cidade_id
- mobilidade.buraco_test.cidade_id
- mobilidade.congestionamentos_test.cidade_id
- mobilidade.controle_importacao_test.cidade_id
- mobilidade.evento_mapa_test.cidade_id
- mobilidade.incidente_test.cidade_id
- mobilidade.radar_test.cidade_id
- mobilidade.regra_trafego_test.cidade_id
- mobilidade.sis_grupos_cidades_test.id_cidade
- mobilidade.tokens_waze_test.cidade_id
- mobilidade.via_test.cidade_id

## mobilidade.sis_cidades_test2

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades_test2` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test2.id_estado

**Referenciada por:**
- mobilidade.alertas_test2.cidade_id
- mobilidade.area_webninja_test2.cidade_id
- mobilidade.buraco_test2.cidade_id
- mobilidade.congestionamentos_test2.cidade_id
- mobilidade.controle_importacao_test2.cidade_id
- mobilidade.evento_mapa_test2.cidade_id
- mobilidade.incidente_test2.cidade_id
- mobilidade.radar_test2.cidade_id
- mobilidade.regra_trafego_test2.cidade_id
- mobilidade.sis_grupos_cidades_test2.id_cidade
- mobilidade.tokens_waze_test2.cidade_id
- mobilidade.via_test2.cidade_id

## mobilidade.sis_cidades_test3

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades_test3` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test3.id_estado

**Referenciada por:**
- mobilidade.alertas_test3.cidade_id
- mobilidade.area_webninja_test3.cidade_id
- mobilidade.buraco_test3.cidade_id
- mobilidade.congestionamentos_test3.cidade_id
- mobilidade.controle_importacao_test3.cidade_id
- mobilidade.evento_mapa_test3.cidade_id
- mobilidade.incidente_test3.cidade_id
- mobilidade.radar_test3.cidade_id
- mobilidade.regra_trafego_test3.cidade_id
- mobilidade.sis_grupos_cidades_test3.id_cidade
- mobilidade.tokens_waze_test3.cidade_id
- mobilidade.via_test3.cidade_id

## mobilidade.sis_cidades_test4

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades_test4` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test4.id_estado

**Referenciada por:**
- mobilidade.alertas_test4.cidade_id
- mobilidade.area_webninja_test4.cidade_id
- mobilidade.buraco_test4.cidade_id
- mobilidade.congestionamentos_test4.cidade_id
- mobilidade.controle_importacao_test4.cidade_id
- mobilidade.evento_mapa_test4.cidade_id
- mobilidade.incidente_test4.cidade_id
- mobilidade.radar_test4.cidade_id
- mobilidade.regra_trafego_test4.cidade_id
- mobilidade.sis_grupos_cidades_test4.id_cidade
- mobilidade.tokens_waze_test4.cidade_id
- mobilidade.via_test4.cidade_id

## mobilidade.sis_cidades_test5

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades_test5` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test5.id_estado

**Referenciada por:**
- mobilidade.alertas_test5.cidade_id
- mobilidade.area_webninja_test5.cidade_id
- mobilidade.buraco_test5.cidade_id
- mobilidade.congestionamentos_test5.cidade_id
- mobilidade.controle_importacao_test5.cidade_id
- mobilidade.evento_mapa_test5.cidade_id
- mobilidade.incidente_test5.cidade_id
- mobilidade.radar_test5.cidade_id
- mobilidade.regra_trafego_test5.cidade_id
- mobilidade.sis_grupos_cidades_test5.id_cidade
- mobilidade.tokens_waze_test5.cidade_id
- mobilidade.via_test5.cidade_id

## mobilidade.sis_cidades_test6

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cidade | int | N | S |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | lat_centro | float | S |  |  |  |
| 6 | lng_centro | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_cidades_test6` (CLUSTERED): id_cidade

**FKs (saída):**
- id_estado → mobilidade.sis_estados_test6.id_estado

**Referenciada por:**
- mobilidade.alertas_test6.cidade_id
- mobilidade.area_webninja_test6.cidade_id
- mobilidade.buraco_test6.cidade_id
- mobilidade.congestionamentos_test6.cidade_id
- mobilidade.controle_importacao_test6.cidade_id
- mobilidade.evento_mapa_test6.cidade_id
- mobilidade.incidente_test6.cidade_id
- mobilidade.radar_test6.cidade_id
- mobilidade.regra_trafego_test6.cidade_id
- mobilidade.sis_grupos_cidades_test6.id_cidade
- mobilidade.tokens_waze_test6.cidade_id
- mobilidade.via_test6.cidade_id

## mobilidade.sis_estados

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades.id_estado

## mobilidade.sis_estados_test

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test.id_estado

## mobilidade.sis_estados_test2

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test2` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test2_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test2.id_estado

## mobilidade.sis_estados_test3

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test3` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test3_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test3.id_estado

## mobilidade.sis_estados_test4

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test4` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test4_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test4.id_estado

## mobilidade.sis_estados_test5

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test5` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test5_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test5.id_estado

## mobilidade.sis_estados_test6

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_estado | int | N | S |  |  |
| 2 | uf | char(2) | N |  |  |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_estados_test6` (CLUSTERED): id_estado
- UNIQUE `UQ_sis_estados_test6_uf` (NONCLUSTERED): uf

**Referenciada por:**
- mobilidade.sis_cidades_test6.id_estado

## mobilidade.sis_fontes_mapa

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas.id_fonte
- mobilidade.congestionamentos.id_fonte
- mobilidade.controle_importacao.id_fonte
- mobilidade.tokens_waze.id_fonte

## mobilidade.sis_fontes_mapa_test

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test.id_fonte
- mobilidade.congestionamentos_test.id_fonte
- mobilidade.controle_importacao_test.id_fonte
- mobilidade.tokens_waze_test.id_fonte

## mobilidade.sis_fontes_mapa_test2

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test2` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test2_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test2.id_fonte
- mobilidade.congestionamentos_test2.id_fonte
- mobilidade.controle_importacao_test2.id_fonte
- mobilidade.tokens_waze_test2.id_fonte

## mobilidade.sis_fontes_mapa_test3

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test3` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test3_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test3.id_fonte
- mobilidade.congestionamentos_test3.id_fonte
- mobilidade.controle_importacao_test3.id_fonte
- mobilidade.tokens_waze_test3.id_fonte

## mobilidade.sis_fontes_mapa_test4

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test4` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test4_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test4.id_fonte
- mobilidade.congestionamentos_test4.id_fonte
- mobilidade.controle_importacao_test4.id_fonte
- mobilidade.tokens_waze_test4.id_fonte

## mobilidade.sis_fontes_mapa_test5

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test5` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test5_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test5.id_fonte
- mobilidade.congestionamentos_test5.id_fonte
- mobilidade.controle_importacao_test5.id_fonte
- mobilidade.tokens_waze_test5.id_fonte

## mobilidade.sis_fontes_mapa_test6

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_fonte | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_fontes_mapa_test6` (CLUSTERED): id_fonte
- UNIQUE `UQ_sis_fontes_mapa_test6_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- mobilidade.alertas_test6.id_fonte
- mobilidade.congestionamentos_test6.id_fonte
- mobilidade.controle_importacao_test6.id_fonte
- mobilidade.tokens_waze_test6.id_fonte

## mobilidade.sis_grupos_cidades

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test

Linhas: ~7

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- IDX `ix_sis_grupos_cidades_test_cidade` (NONCLUSTERED): id_cidade
- IDX `ix_sis_grupos_cidades_test_grupo` (NONCLUSTERED): id_grupo
- PK `PK_sis_grupos_cidades_test` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test2

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades_test2` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test2_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test2.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test3

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades_test3` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test3_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test3.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test4

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades_test4` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test4_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test4.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test5

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades_test5` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test5_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test5.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_grupos_cidades_test6

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_grupo | int | N |  |  |  |
| 3 | id_cidade | int | N |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupos_cidades_test6` (CLUSTERED): id
- UNIQUE `UQ_sis_grupos_cidades_test6_grupo` (NONCLUSTERED): id_grupo, id_cidade

**FKs (saída):**
- id_cidade → mobilidade.sis_cidades_test6.id_cidade
- id_grupo → dbo.sis_grupo.id_grupo

## mobilidade.sis_regiao

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_regi__3213E83F0C0982B6` (CLUSTERED): id

