# Tabelas — schema `dbo` — grupo `remessa`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.remessa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | N | S |  |  |
| 2 | codigo_externo | char(20) | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | data_confirmacao | datetime | S |  |  |  |
| 5 | id_processo | int | S |  |  |  |
| 6 | data_inicial | datetime | N |  |  |  |
| 7 | data_final | datetime | N |  |  |  |
| 8 | total_infracao | int | S |  | ((0)) |  |
| 9 | auto_inicial | int | S |  | ((0)) |  |
| 10 | serie_inicial | char(10) | S |  |  |  |
| 11 | auto_final | int | S |  | ((0)) |  |
| 12 | serie_final | char(10) | S |  |  |  |
| 13 | tipo | char(5) | S |  |  |  |
| 14 | revisao | int | N |  | ((0)) | Revisão do Lote, incrementado quando reprovado por erro |
| 15 | data_exportacao | datetime | S |  |  |  |
| 16 | data_validacao | datetime | S |  |  |  |
| 17 | id_usuario | int | S |  |  |  |
| 18 | id_enquadramento | int | S |  |  |  |
| 19 | id_movimento_arquivo | bigint | S |  |  |  |
| 20 | reprovado | bit | N |  | ((0)) |  |
| 21 | id_inconsistencia | int | S |  |  |  |
| 22 | id_remessa_automatico | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_remessa_codigo_externo` (NONCLUSTERED): id_remessa, codigo_externo
- IDX `IX_remessa_data` (NONCLUSTERED): data
- IDX `IX_remessa_data_confirmacao_id_remessa` (NONCLUSTERED): data_confirmacao
- IDX `IX_remessa_data_validacao` (NONCLUSTERED): data_validacao
- IDX `IX_remessa_data_validacao_data_id_remessa_codigo_tipo` (NONCLUSTERED): data_validacao, data
- IDX `IX_remessa_id_remessa_automatico_id_remessa_tipo` (NONCLUSTERED): id_remessa_automatico
- IDX `IX_remessa_movimento_arquivo` (NONCLUSTERED): id_movimento_arquivo
- IDX `IX_remessa_tipo` (NONCLUSTERED): tipo
- IDX `IX_remessa_tipo_codigo_externo` (NONCLUSTERED): tipo
- IDX `IX_remessa_tipo_id_remessa_codigo_externo` (NONCLUSTERED): tipo
- PK `PK_remessa` (NONCLUSTERED): id_remessa

**FKs (saída):**
- id_movimento_arquivo → dbo.movimento_arquivo.id_movimento_arquivo
- id_remessa_automatico → dbo.gera_remessa_automatico.id_remessa_automatico
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia

**Referenciada por:**
- dbo.infracao_remessa.id_remessa

## dbo.remessa_amostragem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | N |  |  |  |
| 2 | id_infracao | int | N |  |  |  |

## dbo.remessa_excluida

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | N |  |  |  |
| 2 | codigo_externo | char(20) | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | data_confirmacao | datetime | S |  |  |  |
| 5 | id_processo | int | S |  |  |  |
| 6 | data_inicial | datetime | N |  |  |  |
| 7 | data_final | datetime | N |  |  |  |
| 8 | total_infracao | int | S |  |  |  |
| 9 | auto_inicial | int | S |  |  |  |
| 10 | serie_inicial | char(10) | S |  |  |  |
| 11 | auto_final | int | S |  |  |  |
| 12 | serie_final | char(10) | S |  |  |  |
| 13 | tipo | char(5) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_remessa_excluida` (CLUSTERED): id_remessa

**Referenciada por:**
- dbo.infracao_remessa_excluida.id_remessa

## dbo.remessa_iteracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_iteracao | int | N | S |  |  |
| 2 | id_remessa | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_inicio | datetime | N |  | (getdate()) |  |
| 5 | data_fim | datetime | S |  |  |  |
| 6 | tipo_iteracao | int | N |  | ((1)) |  |

## dbo.remessa_nao_metrologico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | registro | int | S |  |  |  |
| 2 | data | datetime | S |  |  |  |
| 3 | data_valido | datetime | S |  |  |  |

## dbo.remessa_nip

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa_nip | int | N | S |  |  |
| 2 | data | datetime | S |  |  |  |
| 3 | total_nip | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_remessa_nip` (CLUSTERED): id_remessa_nip

