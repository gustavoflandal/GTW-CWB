# Tabelas — schema `dash` — grupo `dataset`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas.

## dash.dataset

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | nvarchar(120) | N |  |  |  |
| 3 | descricao | nvarchar(500) | S |  |  |  |
| 4 | id_tabela_base | int | N |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | tenant_id | int | N |  | ((1)) |  |
| 7 | visibilidade | varchar(10) | N |  | ('privado') |  |
| 8 | status | varchar(15) | N |  | ('rascunho') |  |
| 9 | versao | int | N |  | ((1)) |  |
| 10 | criado_em | datetime2 | N |  | (sysdatetime()) |  |
| 11 | atualizado_em | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_dataset_tenant` (NONCLUSTERED): tenant_id, status
- IDX `ix_dataset_usuario` (NONCLUSTERED): id_usuario
- PK `PK__dataset__3213E83F8BB59D2D` (CLUSTERED): id

**FKs (saída):**
- id_tabela_base → dash.catalogo_tabela.id
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dash.dataset_campo.id_dataset
- dash.dataset_estatistica.dataset_id
- dash.dataset_filtro_fixo.id_dataset
- dash.dataset_modelador.id_dataset
- dash.dataset_relacao.id_dataset
- dash.dataset_tabela.id_dataset
- dash.widget.id_dataset

## dash.dataset_campo

Linhas: ~29

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_dataset | int | N |  |  |  |
| 3 | id_coluna | int | S |  |  |  |
| 4 | nome_alias | varchar(80) | N |  |  |  |
| 5 | rotulo | nvarchar(120) | N |  |  |  |
| 6 | expressao | nvarchar(500) | S |  |  |  |
| 7 | agregacao | varchar(20) | N |  | ('NONE') |  |
| 8 | formato | varchar(30) | S |  |  |  |
| 9 | tipo_campo | varchar(15) | N |  | ('dimensao') |  |
| 10 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__dataset___3213E83F6F33476F` (CLUSTERED): id

**FKs (saída):**
- id_coluna → dash.catalogo_coluna.id
- id_dataset → dash.dataset.id

## dash.dataset_estatistica

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | dataset_id | int | N |  |  |  |
| 3 | total_consultas | bigint | N |  | ((0)) |  |
| 4 | tempo_medio_ms | int | N |  | ((0)) |  |
| 5 | cache_hit_count | bigint | N |  | ((0)) |  |
| 6 | consultas_lentas | bigint | N |  | ((0)) |  |
| 7 | ultima_consulta | datetime2 | S |  |  |  |
| 8 | atualizado_em | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_estatistica_dataset` (NONCLUSTERED): dataset_id
- PK `PK__dataset___3213E83F98E756B8` (CLUSTERED): id
- UNIQUE `UQ__dataset___545317FC6ABFC94D` (NONCLUSTERED): dataset_id

**FKs (saída):**
- dataset_id → dash.dataset.id

## dash.dataset_filtro_fixo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_dataset | int | N |  |  |  |
| 3 | coluna | varchar(128) | N |  |  |  |
| 4 | operador | varchar(20) | N |  |  |  |
| 5 | valor | nvarchar(500) | N |  |  |  |
| 6 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `ix_filtro_dataset` (NONCLUSTERED): id_dataset
- PK `PK__dataset___3213E83FA84F46B6` (CLUSTERED): id

**FKs (saída):**
- id_dataset → dash.dataset.id

## dash.dataset_modelador

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_dataset | int | N |  |  |  |
| 2 | layout_json | nvarchar(max) | N |  | ('{}') |  |
| 3 | data_criacao | datetime | N |  | (getdate()) |  |
| 4 | data_modificacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__dataset___6F6BFB4951B9BC31` (CLUSTERED): id_dataset

**FKs (saída):**
- id_dataset → dash.dataset.id

## dash.dataset_relacao

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_dataset | int | N |  |  |  |
| 3 | id_tabela_origem | int | N |  |  |  |
| 4 | id_tabela_destino | int | N |  |  |  |
| 5 | coluna_origem | nvarchar(200) | N |  |  |  |
| 6 | coluna_destino | nvarchar(200) | N |  |  |  |
| 7 | cardinalidade | varchar(20) | N |  | ('many_to_one') |  |
| 8 | origem | varchar(20) | N |  | ('usuario') |  |
| 9 | ativo | bit | N |  | ((1)) |  |
| 10 | data_criacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__dataset___3213E83F90DFF563` (CLUSTERED): id

**FKs (saída):**
- id_dataset → dash.dataset.id
- id_tabela_destino → dash.catalogo_tabela.id
- id_tabela_origem → dash.catalogo_tabela.id

## dash.dataset_tabela

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_dataset | int | N |  |  |  |
| 3 | id_tabela | int | N |  |  |  |
| 4 | alias | varchar(80) | N |  |  |  |
| 5 | tipo | varchar(10) | N |  | ('join') |  |
| 6 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `ix_ds_tabela_dataset` (NONCLUSTERED): id_dataset
- PK `PK__dataset___3213E83F18FED1C6` (CLUSTERED): id
- UNIQUE `uq_ds_tabela` (NONCLUSTERED): id_dataset, id_tabela

**FKs (saída):**
- id_tabela → dash.catalogo_tabela.id
- id_dataset → dash.dataset.id

