# Tabelas — schema `muralha` — grupo `guarnicao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.guarnicao

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | ativo | bit | N |  | ((1)) |  |
| 3 | nome | varchar(100) | N |  |  |  |
| 4 | id_usuario_responsavel | int | N |  |  |  |
| 5 | data_criacao | datetime | N |  |  |  |
| 6 | id_usuario_criacao | int | N |  |  |  |
| 7 | data_ult_alt | datetime | S |  |  |  |
| 8 | id_usuario_alt | int | S |  |  |  |
| 9 | disponivel | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__guarnica__3213E83F1EAD61E5` (CLUSTERED): id

**FKs (saída):**
- id_usuario_responsavel → dbo.sis_usuario.id_usuario
- id_usuario_criacao → dbo.sis_usuario.id_usuario
- id_usuario_alt → dbo.sis_usuario.id_usuario

**Referenciada por:**
- muralha.atendimento_guarnicao.id_guarnicao
- muralha.blitz_guarnicao.id_guarnicao
- muralha.guarnicao_diario.id_guarnicao
- muralha.guarnicao_integrante.id_guarnicao
- muralha.guarnicao_meio_deslocamento.id_guarnicao

## muralha.guarnicao_diario

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_guarnicao | int | N |  |  |  |
| 3 | data | date | N |  |  |  |
| 4 | quilometragem | int | N |  |  |  |
| 5 | hora_ini | time | N |  |  |  |
| 6 | hora_fim | time | N |  |  |  |
| 7 | setores_patrulhados | varchar(200) | N |  |  |  |
| 8 | meio_transporte | varchar(200) | N |  |  |  |
| 9 | data_cadastro | datetime | N |  |  |  |
| 10 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__guarnica__3213E83FDE603383` (CLUSTERED): id

**FKs (saída):**
- id_guarnicao → muralha.guarnicao.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.guarnicao_integrante

Linhas: ~15

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_guarnicao | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__guarnica__3213E83F24C827C1` (CLUSTERED): id

**FKs (saída):**
- id_guarnicao → muralha.guarnicao.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.guarnicao_meio_deslocamento

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | id_guarnicao | int | N |  |  |  |
| 3 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__guarnica__3213E83F4903F3FB` (CLUSTERED): id

**FKs (saída):**
- id_guarnicao → muralha.guarnicao.id

