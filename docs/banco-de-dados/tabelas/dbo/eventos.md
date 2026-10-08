# Tabelas — schema `dbo` — grupo `eventos`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.eventos_csx

Linhas: ~495224

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_proprietario | int | N |  |  |  |
| 3 | data_hora | datetime | N |  |  |  |
| 4 | id_categoria | int | N |  |  |  |
| 5 | id_evento | int | N |  |  |  |
| 6 | mensagem | varchar(512) | S |  |  |  |
| 7 | id_prioridade | int | N |  |  |  |
| 8 | id_nivel | int | N |  |  |  |
| 9 | usuario | varchar(25) | S |  |  |  |
| 10 | evento_manual | bit | S |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_evento_manual_evento_manual_data_hora` (NONCLUSTERED): evento_manual, data_hora
- IDX `IX_eventos_csx_data_hora` (NONCLUSTERED): data_hora
- IDX `IX_eventos_csx_evento_data_hora` (NONCLUSTERED): id_evento, data_hora
- IDX `IX_eventos_csx_proprietario_data_hora_evento` (NONCLUSTERED): id_proprietario, data_hora, id_evento
- IDX `IX_eventos_csx_proprietario_evento_mensagem` (NONCLUSTERED): id_proprietario, id_evento, mensagem
- PK `PK_eventos_csx` (CLUSTERED): id

**FKs (saída):**
- id_proprietario → dbo.eventos_csx_desc_proprietario.id_proprietario

**Referenciada por:**
- dbo.arquivo_log_item_log.id_evento_csx

## dbo.eventos_csx_bkp

Linhas: ~5973

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | id_proprietario | int | N |  |  |  |
| 3 | data_hora | datetime | N |  |  |  |
| 4 | id_categoria | int | N |  |  |  |
| 5 | id_evento | int | N |  |  |  |
| 6 | mensagem | varchar(512) | S |  |  |  |
| 7 | id_prioridade | int | N |  |  |  |
| 8 | id_nivel | int | N |  |  |  |
| 9 | usuario | varchar(25) | S |  |  |  |
| 10 | evento_manual | bit | S |  |  |  |

## dbo.eventos_csx_categoria_x_evento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_categoria_cav | int | N |  |  |  |
| 3 | id_evento_cai | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_eventos_csx_categoria_x_evento` (CLUSTERED): id

## dbo.eventos_csx_desc_categoria

Linhas: ~42

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_categoria | int | N |  |  |  |
| 2 | categoria | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_eventos_csx_desc_categoria` (CLUSTERED): id_categoria

**Referenciada por:**
- dbo.cad_evento_manual.id_categoria

## dbo.eventos_csx_desc_evento

Linhas: ~99

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_evento | int | N |  |  |  |
| 2 | evento | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_eventos_csx_desc_evento` (CLUSTERED): id_evento

**Referenciada por:**
- dbo.cad_evento_manual.id_evento

## dbo.eventos_csx_desc_nivel

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_nivel | int | N |  |  |  |
| 2 | nivel | varchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_eventos_csx_desc_nivel` (CLUSTERED): id_nivel

**Referenciada por:**
- dbo.cad_evento_manual.id_nivel

## dbo.eventos_csx_desc_prioridade

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_prioridade | int | N |  |  |  |
| 2 | prioridade | varchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_eventos_csx_desc_prioridade` (CLUSTERED): id_prioridade

**Referenciada por:**
- dbo.cad_evento_manual.id_prioridade

## dbo.eventos_csx_desc_proprietario

Linhas: ~44

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_proprietario | int | N | S |  |  |
| 2 | proprietario | nvarchar(50) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_eventos_csx_desc_proprietario_proprietario` (NONCLUSTERED): proprietario
- PK `PK_eventos_csx_desc_proprietario` (CLUSTERED): id_proprietario

**Referenciada por:**
- dbo.eventos_csx.id_proprietario

## dbo.eventos_csx_legacy

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | proprietario | varchar(50) | N |  |  |  |
| 3 | data_hora | datetime | N |  |  |  |
| 4 | id_categoria | int | N |  |  |  |
| 5 | id_evento | int | N |  |  |  |
| 6 | mensagem | varchar(512) | S |  |  |  |
| 7 | id_prioridade | int | N |  |  |  |
| 8 | id_nivel | int | N |  |  |  |
| 9 | usuario | varchar(25) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_eventos_csx_legacy_data_hora` (NONCLUSTERED): data_hora
- IDX `IX_eventos_csx_legacy_proprietario_data_hora_evento` (NONCLUSTERED): proprietario, data_hora, id_evento

## dbo.eventos_csx_usuarios

Linhas: ~25

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | usuario | varchar(25) | S |  |  |  |

