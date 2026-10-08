# Tabelas — schema `dbo` — grupo `agenda`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.agenda_camera

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera | int | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_criacao_agenda | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera` (CLUSTERED): id_agenda_camera

**Referenciada por:**
- dbo.agenda_camera_periodo.id_agenda_camera
- dbo.agenda_camera_pumatronix_camera.id_agenda_camera

## dbo.agenda_camera_item

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera_item | int | N | S |  |  |
| 2 | id_agenda_camera | int | N |  |  |  |
| 3 | id_periodo | int | N |  |  |  |
| 4 | nome | nchar(20) | N |  |  |  |
| 5 | valor | nchar(30) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_agenda_camera_item_agenda_camera_periodo_nonme` (NONCLUSTERED): id_agenda_camera, id_periodo, nome
- PK `PK_agenda_camera_item` (CLUSTERED): id_agenda_camera_item

**FKs (saída):**
- id_agenda_camera → dbo.agenda_camera_periodo.id_agenda_camera
- id_periodo → dbo.agenda_camera_periodo.id_periodo

## dbo.agenda_camera_periodo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera | int | N |  |  |  |
| 2 | id_periodo | int | N |  |  |  |
| 3 | hora_inicio | time | N |  |  |  |
| 4 | hora_fim | time | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera_periodo` (CLUSTERED): id_agenda_camera, id_periodo

**FKs (saída):**
- id_agenda_camera → dbo.agenda_camera.id_agenda_camera

**Referenciada por:**
- dbo.agenda_camera_item.id_agenda_camera
- dbo.agenda_camera_item.id_periodo

## dbo.agenda_camera_pumatronix_camera

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera_pumatronix_camera | int | N | S |  |  |
| 2 | id_agenda_camera | int | N |  |  |  |
| 3 | id_camera_equipamento | int | N |  |  |  |
| 4 | endereco_camera | varchar(60) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera_pumatronix_camera` (CLUSTERED): id_agenda_camera_pumatronix_camera

**FKs (saída):**
- id_agenda_camera → dbo.agenda_camera.id_agenda_camera

**Referenciada por:**
- dbo.agenda_camera_pumatronix_periodo.id_agenda_camera_pumatronix_camera

## dbo.agenda_camera_pumatronix_periodo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera_pumatronix_periodo | int | N | S |  |  |
| 2 | id_agenda_camera_pumatronix_camera | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera_pumatronix_periodo` (CLUSTERED): id_agenda_camera_pumatronix_periodo

**FKs (saída):**
- id_agenda_camera_pumatronix_camera → dbo.agenda_camera_pumatronix_camera.id_agenda_camera_pumatronix_camera

**Referenciada por:**
- dbo.agenda_camera_pumatronix_propriedade_periodo.id_agenda_camera_pumatronix_periodo

## dbo.agenda_camera_pumatronix_propriedade_periodo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera_pumatronix_propriedade_periodo | int | N | S |  |  |
| 2 | id_agenda_camera_pumatronix_periodo | int | N |  |  |  |
| 3 | id_agenda_camera_pumatronix_tipo_propriedade | int | N |  |  |  |
| 4 | nome_propriedade | varchar(60) | N |  |  |  |
| 5 | valor_propriedade | varchar(60) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera_pumatronix_propriedade_periodo` (CLUSTERED): id_agenda_camera_pumatronix_propriedade_periodo

**FKs (saída):**
- id_agenda_camera_pumatronix_periodo → dbo.agenda_camera_pumatronix_periodo.id_agenda_camera_pumatronix_periodo
- id_agenda_camera_pumatronix_tipo_propriedade → dbo.agenda_camera_pumatronix_tipo_propriedade.id_agenda_camera_pumatronix_tipo_propriedade

## dbo.agenda_camera_pumatronix_tipo_propriedade

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_camera_pumatronix_tipo_propriedade | int | N | S |  |  |
| 2 | descricao | varchar(60) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_camera_pumatronix_tipo_propriedade` (CLUSTERED): id_agenda_camera_pumatronix_tipo_propriedade

**Referenciada por:**
- dbo.agenda_camera_pumatronix_propriedade_periodo.id_agenda_camera_pumatronix_tipo_propriedade

## dbo.agenda_estatico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_estatico | int | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_referencia | date | N |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | data_criacao_agenda | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_agenda_estatico` (CLUSTERED): id_agenda_estatico

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.agenda_estatico_item.id_agenda_estatico

## dbo.agenda_estatico_item

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_item | int | N | S |  |  |
| 2 | id_agenda_estatico | int | N |  |  |  |
| 3 | data_operacao | date | N |  |  |  |
| 4 | hora_inicio | time | N |  |  |  |
| 5 | hora_fim | time | N |  |  |  |
| 6 | horas_funcionamento | int | S |  |  |  |
| 7 | sequencia | int | N |  |  |  |
| 8 | status | int | N |  |  |  |
| 9 | dt_ult_alteracao | datetime | S |  |  |  |
| 10 | id_usuario_alter | int | S |  |  |  |
| 11 | id_agenda_item_origem | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_estatico_item` (CLUSTERED): id_agenda_item

**FKs (saída):**
- id_agenda_estatico → dbo.agenda_estatico.id_agenda_estatico
- id_agenda_item_origem → dbo.agenda_estatico_item.id_agenda_item
- id_usuario_alter → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.agenda_estatico_item.id_agenda_item_origem
- dbo.agenda_estatico_validacao.id_agenda_estatico_item

## dbo.agenda_estatico_validacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_agenda_validacao | int | N |  |  |  |
| 2 | id_agenda_estatico_item | int | N |  |  |  |
| 3 | data | date | N |  |  |  |
| 4 | liberacao | bit | N |  | ((0)) |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_agenda_validacao` (CLUSTERED): id_agenda_validacao

**FKs (saída):**
- id_agenda_estatico_item → dbo.agenda_estatico_item.id_agenda_item
- id_usuario → dbo.sis_usuario.id_usuario

