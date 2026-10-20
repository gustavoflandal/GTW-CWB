# Tabelas — schema `muralha` — grupo `motivo`

Gerado de GTW_MURALHA_DEV em 2026-10-20. Linhas aproximadas.

## muralha.motivo_correcao_placa

Linhas: ~7

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |
| 3 | exige_texto | bit | N |  | ((0)) |  |
| 4 | confirmacao | bit | N |  | ((0)) |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK_motivo_correcao_placa` (CLUSTERED): id

## muralha.motivo_descarte

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(40) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_motivo_descarte` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta.id_motivo_descarte

## muralha.motivo_invalido_correlacionamento_automatico_placa

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(max) | N |  |  |  |

## muralha.motivo_solicitacao_relatorio

Linhas: ~1749

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | motivo | varchar(255) | N |  |  |  |
| 4 | data_criacao | datetime | N |  |  |  |
| 5 | placa | char(7) | S |  |  |  |
| 6 | tipo_solicitacao | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__motivo_s__3213E83FB3354665` (CLUSTERED): id

