# Tabelas — schema `dbo` — grupo `status`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.status_conexao

Linhas: ~11314

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  |  |  |
| 3 | id_status_conexao | int | N | S |  |  |
| 4 | data_atualizacao | datetime | N |  |  |  |
| 5 | status | tinyint | N |  |  |  |
| 6 | ip | nchar(15) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_status_conexao_local_data_atualizacao_status` (NONCLUSTERED): id_local, data_atualizacao, status
- IDX `IX_status_conexao_local_status` (NONCLUSTERED): id_local, status
- IDX `IX_status_conexao_status_data_atualizacao` (NONCLUSTERED): status, data_atualizacao
- IDX `IX_status_infracao_data_atualizacao_local_status` (NONCLUSTERED): data_atualizacao, id_local, status
- PK `PK_status_conexao` (CLUSTERED): id_local, sequencia_local, id_status_conexao

**FKs (saída):**
- id_local → dbo.local.id_local
- sequencia_local → dbo.local.sequencia_local

## dbo.status_div

Linhas: ~189176

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  |  |  |
| 3 | id_status_DIV | int | N | S |  |  |
| 4 | data_atualizacao | datetime | N |  |  |  |
| 5 | codigo_DIV | tinyint | N |  | ((0)) |  |
| 6 | status | tinyint | N |  |  |  |
| 7 | comunicacao_ok | bit | N |  | ((0)) |  |
| 8 | grupo_centena_ok | bit | N |  | ((0)) |  |
| 9 | grupo_decena_ok | bit | N |  | ((0)) |  |
| 10 | grupo_unidade_ok | bit | N |  | ((0)) |  |
| 11 | grupo_vermelho_ok | bit | N |  | ((0)) |  |
| 12 | grupo_amarelo_ok | bit | N |  | ((0)) |  |
| 13 | grupo_verde_ok | bit | N |  | ((0)) |  |
| 14 | endereco_DIV | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_status_div_local_status_codigo` (NONCLUSTERED): id_local, id_status_DIV, codigo_DIV
- PK `PK_status_DIV` (CLUSTERED): id_local, sequencia_local, id_status_DIV

**FKs (saída):**
- sequencia_local → dbo.local.sequencia_local
- id_local → dbo.local.id_local

## dbo.status_energia

Linhas: ~464

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  |  |  |
| 3 | id_status_energia | int | N |  |  |  |
| 4 | data_atualizacao | datetime | N |  |  |  |
| 5 | status | tinyint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_status_energia` (CLUSTERED): id_local, sequencia_local, id_status_energia

**FKs (saída):**
- id_local → dbo.local.id_local
- sequencia_local → dbo.local.sequencia_local

## dbo.status_tempo_real

Linhas: ~35

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | serie_equipamento | int | N |  |  |  |
| 2 | versao | nchar(20) | S |  |  |  |
| 3 | tempo_executando | bigint | S |  |  |  |
| 4 | status_copia | nchar(250) | S |  |  |  |
| 5 | ultima_deteccao | datetime | S |  |  |  |
| 6 | data_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- IDX `IX_status_tempo_real_serie_equipamento` (NONCLUSTERED): serie_equipamento

## dbo.status_traffic

Linhas: ~44

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | traffic | int | N |  |  |  |

