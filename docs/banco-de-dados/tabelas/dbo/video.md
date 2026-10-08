# Tabelas — schema `dbo` — grupo `video`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.video

Linhas: ~11447

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_video | int | N |  |  |  |
| 2 | video | image | S |  |  |  |
| 3 | id_uniq | uniqueidentifier | N |  | (newid()) |  |
| 4 | ds_caminho | varchar(255) | S |  |  |  |
| 5 | video_removido | bit | S |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_video_caminho` (NONCLUSTERED): ds_caminho
- PK `PK_video` (CLUSTERED): id_video

**FKs (saída):**
- id_video → dbo.video_info.id_video

## dbo.video_importacao

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_video | int | N | S |  |  |
| 2 | id_veiculo_unic | bigint | S |  |  |  |
| 3 | numero | int | N |  |  |  |
| 4 | nome | char(15) | N |  |  |  |
| 5 | formato | char(5) | N |  |  |  |
| 6 | video | image | N |  |  |  |

**Índices/Chaves:**
- PK `PK_video_importacao` (CLUSTERED): id_video
- UNIQUE `UK_video_importacao_veiculo_unic_nome_numero` (NONCLUSTERED): id_veiculo_unic, nome, numero

## dbo.video_info

Linhas: ~11447

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_video | int | N |  |  |  |
| 2 | id_tipo_video | int | N |  |  |  |
| 3 | formato | char(5) | S |  |  |  |
| 4 | md5 | char(40) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_video_info` (CLUSTERED): id_video

**FKs (saída):**
- id_tipo_video → dbo.tipo_video.id_tipo_video

**Referenciada por:**
- dbo.veiculo_video.id_video
- dbo.video.id_video

## dbo.video_removido

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_video | int | N |  |  |  |
| 2 | data_removido | datetime | N |  |  |  |

