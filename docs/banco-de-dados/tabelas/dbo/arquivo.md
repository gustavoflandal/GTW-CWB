# Tabelas — schema `dbo` — grupo `arquivo`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome | nvarchar(40) | S |  |  |  |
| 2 | id_tipo_arquivo | int | S |  |  |  |
| 3 | id_tipo_conteudo | int | S |  |  |  |
| 4 | data_arquivo | datetime2 | S |  |  |  |
| 5 | data_adicionado | datetime2 | S |  |  |  |
| 6 | data_enviado | datetime2 | S |  |  |  |
| 7 | tentativas | int | S |  |  |  |
| 8 | prioridade | bit | S |  |  |  |

## dbo.arquivo_dt

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | bigint | N | S |  |  |
| 2 | id_diretorio | int | S |  |  |  |
| 3 | nome_arquivo | char(32) | N |  |  |  |
| 4 | data_arquivo | datetime | N |  |  |  |
| 5 | id_local | int | N |  |  |  |
| 6 | processado | tinyint | N |  |  |  |
| 7 | data_processado | datetime | S |  |  |  |
| 8 | crc | binary | N |  |  |  |
| 9 | data_importacao | datetime | S |  | (getdate()) |  |
| 10 | data_criacao | datetime | S |  |  |  |
| 11 | data_modificacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__arquivo___F5CD27A27FDBC6E8` (CLUSTERED): id_arquivo

## dbo.arquivo_log

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo_log | int | N | S |  |  |
| 2 | nome_arquivo | varchar(200) | N |  |  |  |
| 3 | caminho_arquivo | varchar(200) | N |  |  |  |
| 4 | data_inicio | datetime | N |  |  |  |
| 5 | data_fim | datetime | N |  |  |  |
| 6 | quantidade | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_arquivo_log` (NONCLUSTERED): id_arquivo_log

**FKs (saída):**
- nome_arquivo → dbo.arquivos_cai_para_cav.nome_arquivo

**Referenciada por:**
- dbo.arquivo_log_item_log.id_arquivo_log

## dbo.arquivo_log_item_log

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo_log | int | N |  |  |  |
| 2 | id_evento_csx | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_arquivo_log_item_log` (NONCLUSTERED): id_arquivo_log, id_evento_csx

**FKs (saída):**
- id_arquivo_log → dbo.arquivo_log.id_arquivo_log
- id_evento_csx → dbo.eventos_csx.id

## dbo.arquivo_placa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(23) | S |  |  |  |
| 2 | placa | varchar(7) | S |  |  |  |

