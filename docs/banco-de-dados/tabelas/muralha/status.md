# Tabelas — schema `muralha` — grupo `status`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.status_alerta

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(20) | N |  |  |  |
| 3 | descricao_detalhada | varchar(300) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_status_alerta` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta.id_status_alerta

## muralha.status_correlacionamento_automatico

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(max) | N |  |  |  |

## muralha.status_notificacao

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_status_notificacao` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta_notificacao.id_status_notificacao
- muralha.ocorrencia_notificacao.id_status_notificacao
- muralha.ocorrencia_notificacao_historico.id_status_notificacao

## muralha.status_ocorrencia

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_status_ocorrencia` (CLUSTERED): id

**Referenciada por:**
- muralha.ocorrencia.id_status_ocorrencia
- muralha.tipo_ocorrencia_status.id_status_ocorrencia

