# Tabelas — schema `dbo` — grupo `tipo`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.tipo_camera

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_camera | int | N |  |  |  |
| 2 | descricao | nvarchar(20) | N |  |  |  |

**Índices/Chaves:**
- UNIQUE `UK_tipo_camera_id` (NONCLUSTERED): id_tipo_camera

## dbo.tipo_imagem

Linhas: ~14

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_imagem | int | N | S |  |  |
| 2 | nome | char(15) | N |  |  |  |
| 3 | numero | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_tipo_imagem_nome_numero` (NONCLUSTERED): nome, numero
- PK `PK_tipo_imagem` (NONCLUSTERED): id_tipo_imagem

**Referenciada por:**
- dbo.enquadramento.id_tipo_imagem_pan
- dbo.enquadramento.id_tipo_imagem_obj
- dbo.enquadramento.id_tipo_imagem_pan2
- dbo.imagem_info.id_tipo_imagem

## dbo.tipo_percurso

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_percurso | int | N | S |  |  |
| 2 | descricao | nvarchar(15) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_percurso` (CLUSTERED): id_tipo_percurso

**Referenciada por:**
- dbo.percurso.id_tipo_percurso

## dbo.tipo_relatorio_edital_rj

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_relatorio_edital_rj | int | N | S |  |  |
| 2 | descricao | nvarchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_relatorio_edital_rj` (CLUSTERED): id_tipo_relatorio_edital_rj

## dbo.tipo_validacao_velocidade_target

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_validacao_velocidade_target | int | N |  |  |  |
| 2 | descricao | nvarchar(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_validacao_velocidade_target` (CLUSTERED): id_validacao_velocidade_target

## dbo.tipo_video

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_video | int | N |  |  |  |
| 2 | nome | char(15) | N |  |  |  |
| 3 | numero | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_video` (CLUSTERED): id_tipo_video

**Referenciada por:**
- dbo.video_info.id_tipo_video

