# Tabelas — schema `dbo` — grupo `inconsistencia`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.inconsistencia

Linhas: ~67

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inconsistencia | int | N |  |  |  |
| 2 | descricao | char(70) | S |  |  |  |
| 3 | razao_tecnica | tinyint | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_inconsistencia_descricao` (NONCLUSTERED): descricao
- PK `PK_inconsistencia` (CLUSTERED): id_inconsistencia

**Referenciada por:**
- dbo.agendamento_processamento.id_inconsistencia
- dbo.enquadramento.id_inconsistencia_isencao
- dbo.enquadramento_inconsistencia.id_inconsistencia
- dbo.infracao.id_inconsistencia
- dbo.infracao_processo.id_inconsistencia
- dbo.infracao_processo_concluido.id_inconsistencia
- dbo.movimento_importacao.id_inconsistencia
- dbo.processo_inconsistencia.id_inconsistencia
- dbo.remessa.id_inconsistencia

## dbo.inconsistencia_apait

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inconsistencia_apait | int | S |  |  |  |
| 2 | descricao | varchar(200) | S |  |  |  |

## dbo.inconsistencia_bkp

Linhas: ~67

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inconsistencia | int | N |  |  |  |
| 2 | descricao | char(70) | S |  |  |  |
| 3 | razao_tecnica | tinyint | N |  |  |  |

## dbo.inconsistencia_cav

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inconsistencia_cav | int | N |  |  |  |
| 2 | id_inconsistencia_cai | int | N |  |  |  |
| 3 | data_inicio | date | N |  | (getdate()) |  |
| 4 | data_fim | date | S |  |  |  |

**Índices/Chaves:**
- PK `PK_inconsistencia_cav` (CLUSTERED): id_inconsistencia_cav, id_inconsistencia_cai, data_inicio
- UNIQUE `UK_inconsistencia_cav_id_inconsistencia_cai` (NONCLUSTERED): id_inconsistencia_cai

## dbo.inconsistencia_isento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inconsistencia | int | N |  |  |  |
| 2 | descricao | char(70) | S |  |  |  |
| 3 | razao_tecnica | tinyint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_inconsistencia_isento` (CLUSTERED): id_inconsistencia

## dbo.inconsistencia_processo_enquadramento

Linhas: ~784

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo | int | N |  |  |  |
| 2 | id_enquadramento | int | N |  |  |  |
| 3 | id_inconsistencia | int | N |  |  |  |
| 4 | quantidade | int | N |  |  |  |

