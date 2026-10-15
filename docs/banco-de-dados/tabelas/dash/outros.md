# Tabelas — schema `dash` — grupo `outros`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas.

## dash.catalogo_coluna

Linhas: ~30

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_tabela | int | N |  |  |  |
| 3 | nome_fisico | varchar(128) | N |  |  |  |
| 4 | rotulo | nvarchar(120) | N |  |  |  |
| 5 | tipo | varchar(20) | N |  |  |  |
| 6 | sensivel | bit | N |  | ((0)) |  |
| 7 | papel_sugerido | varchar(20) | S |  |  |  |
| 8 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__catalogo__3213E83F5FEA2376` (CLUSTERED): id
- UNIQUE `uq_coluna` (NONCLUSTERED): id_tabela, nome_fisico

**FKs (saída):**
- id_tabela → dash.catalogo_tabela.id

**Referenciada por:**
- dash.dataset_campo.id_coluna

## dash.catalogo_relacao

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_tabela_origem | int | N |  |  |  |
| 3 | id_tabela_destino | int | N |  |  |  |
| 4 | cardinalidade | varchar(20) | N |  |  |  |
| 5 | coluna_origem | varchar(128) | N |  |  |  |
| 6 | coluna_destino | varchar(128) | N |  |  |  |
| 7 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__catalogo__3213E83FFA9D1E6F` (CLUSTERED): id

**FKs (saída):**
- id_tabela_destino → dash.catalogo_tabela.id
- id_tabela_origem → dash.catalogo_tabela.id

## dash.catalogo_tabela

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome_fisico | varchar(128) | N |  |  |  |
| 3 | schema_fisico | varchar(128) | N |  | ('dbo') |  |
| 4 | rotulo | nvarchar(120) | N |  |  |  |
| 5 | descricao | nvarchar(500) | S |  |  |  |
| 6 | chave_primaria | varchar(128) | N |  |  |  |
| 7 | sensivel | bit | N |  | ((0)) |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | criado_em | datetime2 | N |  | (sysdatetime()) |  |
| 10 | atualizado_em | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK__catalogo__3213E83F336DF9E4` (CLUSTERED): id
- UNIQUE `uq_catalogo_tabela` (NONCLUSTERED): schema_fisico, nome_fisico

**Referenciada por:**
- dash.catalogo_coluna.id_tabela
- dash.catalogo_relacao.id_tabela_destino
- dash.catalogo_relacao.id_tabela_origem
- dash.dataset.id_tabela_base
- dash.dataset_relacao.id_tabela_destino
- dash.dataset_relacao.id_tabela_origem
- dash.dataset_tabela.id_tabela

## dash.compartilhamento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | tipo_recurso | varchar(15) | N |  |  |  |
| 3 | id_recurso | int | N |  |  |  |
| 4 | id_usuario | int | S |  |  |  |
| 5 | id_perfil | int | S |  |  |  |
| 6 | nivel | varchar(10) | N |  | ('ver') |  |
| 7 | criado_em | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_compartilhamento` (NONCLUSTERED): tipo_recurso, id_recurso
- PK `PK__comparti__3213E83FD20CB23A` (CLUSTERED): id

## dash.consulta_log

Linhas: ~261

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | usuario_id | int | N |  |  |  |
| 3 | dataset_id | int | N |  |  |  |
| 4 | widget_id | int | S |  |  |  |
| 5 | painel_id | int | S |  |  |  |
| 6 | consulta_json | nvarchar(max) | S |  |  |  |
| 7 | tempo_ms | int | N |  |  |  |
| 8 | linhas_retornadas | int | N |  | ((0)) |  |
| 9 | cache_hit | bit | N |  | ((0)) |  |
| 10 | lenta | bit | N |  | ((0)) |  |
| 11 | erro | varchar(500) | S |  |  |  |
| 12 | criado_em | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_consulta_log_criado` (NONCLUSTERED): criado_em
- IDX `ix_consulta_log_dataset` (NONCLUSTERED): dataset_id, criado_em
- IDX `ix_consulta_log_lenta` (NONCLUSTERED): lenta, criado_em
- PK `PK__consulta__3213E83F1F1CE445` (CLUSTERED): id

## dash.dashboard

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | nvarchar(120) | N |  |  |  |
| 3 | descricao | nvarchar(500) | S |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | tenant_id | int | N |  | ((1)) |  |
| 6 | layout_json | nvarchar(max) | S |  |  |  |
| 7 | filtros_json | nvarchar(max) | S |  |  |  |
| 8 | refresh_seg | int | N |  | ((0)) |  |
| 9 | versao | int | N |  | ((1)) |  |
| 10 | ativo | bit | N |  | ((1)) |  |
| 11 | criado_em | datetime2 | N |  | (sysdatetime()) |  |
| 12 | atualizado_em | datetime2 | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_dashboard_tenant` (NONCLUSTERED): tenant_id, ativo
- IDX `ix_dashboard_usuario` (NONCLUSTERED): id_usuario
- PK `PK__dashboar__3213E83F3749BAD5` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dash.widget.id_dashboard

## dash.historico_versao

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | tipo_recurso | varchar(15) | N |  |  |  |
| 3 | id_recurso | int | N |  |  |  |
| 4 | versao | int | N |  |  |  |
| 5 | snapshot_json | nvarchar(max) | N |  |  |  |
| 6 | id_autor | int | N |  |  |  |
| 7 | criado_em | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_historico` (NONCLUSTERED): tipo_recurso, id_recurso, versao
- PK `PK__historic__3213E83F7073EE40` (CLUSTERED): id

## dash.widget

Linhas: ~16

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_dashboard | int | N |  |  |  |
| 3 | id_dataset | int | N |  |  |  |
| 4 | titulo | nvarchar(120) | N |  |  |  |
| 5 | tipo | varchar(20) | N |  |  |  |
| 6 | consulta_json | nvarchar(max) | N |  |  |  |
| 7 | visual_json | nvarchar(max) | S |  |  |  |
| 8 | grid_x | int | N |  | ((0)) |  |
| 9 | grid_y | int | N |  | ((0)) |  |
| 10 | grid_w | int | N |  | ((4)) |  |
| 11 | grid_h | int | N |  | ((3)) |  |
| 12 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `ix_widget_dashboard` (NONCLUSTERED): id_dashboard
- PK `PK__widget__3213E83FD87744F9` (CLUSTERED): id

**FKs (saída):**
- id_dashboard → dash.dashboard.id
- id_dataset → dash.dataset.id

