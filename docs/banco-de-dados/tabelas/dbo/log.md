# Tabelas — schema `dbo` — grupo `log`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.log_alerta

Linhas: ~45

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_tipo | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | serial | varchar(15) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__log_aler__3213E83F5EB1F38B` (CLUSTERED): id

**FKs (saída):**
- id_tipo → dbo.log_tipo.id

## dbo.log_arquivo

Linhas: ~10

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | arquivo | varchar(200) | N |  |  |  |
| 3 | caminho | varchar(256) | N |  |  |  |
| 4 | formato_valido | int | N |  |  |  |
| 5 | data_arquivo | datetime | N |  |  |  |
| 6 | data_insercao | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__log_arqu__3213E83F3F86B583` (CLUSTERED): id

**FKs (saída):**
- formato_valido → dbo.log_erro.id

**Referenciada por:**
- dbo.log_conteudo.id_log

## dbo.log_conteudo

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_log | bigint | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | datahora | datetime | N |  |  |  |
| 5 | datahora_final | datetime | S |  |  |  |
| 6 | ocorrencias | int | N |  |  |  |
| 7 | id_tipo | int | N |  |  |  |
| 8 | mensagem | varchar(350) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__log_cont__3213E83FF707A43C` (CLUSTERED): id

**FKs (saída):**
- id_log → dbo.log_arquivo.id
- id_tipo → dbo.log_tipo.id

## dbo.log_erro

Linhas: ~7

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__log_erro__3213E83F500BDCCB` (CLUSTERED): id

**Referenciada por:**
- dbo.log_arquivo.formato_valido

## dbo.log_finaliza

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_log | int | N | S |  |  |
| 2 | id_arquivo | int | N |  |  |  |
| 3 | hora_inicio | datetime | N |  |  |  |
| 4 | tempo_execucao | time | S |  |  |  |
| 5 | erro | bit | N |  | ((0)) |  |
| 6 | finaliza_movimento | bit | N |  | ((0)) |  |
| 7 | texto_log | varchar(max) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_log_finaliza_id_log` (NONCLUSTERED): id_log
- IDX `IX_log_finaliza_movimento_id_log` (NONCLUSTERED): finaliza_movimento

## dbo.log_finaliza_detalhe

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_log_detalhe | int | N | S |  |  |
| 2 | id_log | int | N |  |  |  |
| 3 | nome_procedure | varchar(50) | S |  |  |  |
| 4 | data_inicio | datetime | S |  |  |  |
| 5 | texto_log | varchar(500) | S |  |  |  |
| 6 | quantidade | int | S |  |  |  |
| 7 | erro | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_log_finaliza_detalhe_id_log_nome_procedure_id_detalhe` (NONCLUSTERED): id_log, nome_procedure
- IDX `IX_log_finaliza_detalhe_nome_procedure` (NONCLUSTERED): nome_procedure
- IDX `IX_log_finaliza_detalhe_nome_procedure_id` (NONCLUSTERED): nome_procedure

## dbo.log_finaliza_resumo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dia | date | N |  |  |  |
| 2 | infracao | int | S |  |  |  |
| 3 | remessa | int | S |  |  |  |
| 4 | imagens | int | S |  |  |  |
| 5 | video | int | S |  |  |  |

## dbo.log_pendente

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_evento_csx | int | N |  |  |  |

## dbo.log_processos

Linhas: ~1315847

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(60) | N |  |  |  |
| 3 | data_inicio | datetime | N |  | (getdate()) |  |
| 4 | registros | int | N |  | ((0)) |  |
| 5 | iteracoes | int | N |  | ((0)) |  |
| 6 | total | int | N |  | ((0)) |  |
| 7 | data_atualizado | datetime | N |  | (getdate()) |  |
| 8 | data_finalizado | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__log_proc__3213E83F1515A419` (CLUSTERED): id

## dbo.log_processos_detalhe

Linhas: ~169

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | arquivos_adicionados | int | N |  |  |  |
| 4 | arquivos | int | N |  |  |  |
| 5 | tempo_exec | time | N |  |  |  |

## dbo.log_tipo

Linhas: ~22

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | tipo | int | N |  |  |  |
| 3 | subtipo | int | N |  |  |  |
| 4 | descricao | varchar(45) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__log_tipo__3213E83F9660CB17` (CLUSTERED): id

**Referenciada por:**
- dbo.log_alerta.id_tipo
- dbo.log_conteudo.id_tipo

