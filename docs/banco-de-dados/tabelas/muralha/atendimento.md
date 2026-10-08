# Tabelas — schema `muralha` — grupo `atendimento`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.atendimento

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | protocolo | varchar(36) | S |  |  |  |
| 3 | id_origem | int | N |  |  |  |
| 4 | id_registro_fato | bigint | N |  |  |  |
| 7 | id_situacao | int | N |  |  |  |
| 8 | data_criacao | datetime | N |  |  |  |
| 9 | data_encerramento | datetime | S |  |  |  |
| 10 | id_usuario_criacao | int | N |  |  |  |
| 13 | id_ocorrencia | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83FC4491274` (CLUSTERED): id

**FKs (saída):**
- id_ocorrencia → muralha.ocorrencia.id
- id_origem → muralha.atendimento_origem.id
- id_situacao → muralha.atendimento_situacao.id
- id_usuario_criacao → dbo.sis_usuario.id_usuario
- id_registro_fato → muralha.registro_fato.id

**Referenciada por:**
- muralha.atendimento_documento.id_atendimento
- muralha.atendimento_guarnicao.id_atendimento
- muralha.atendimento_historico.id_atendimento

## muralha.atendimento_documento

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_atendimento | int | N |  |  |  |
| 3 | tipo | varchar(20) | N |  |  |  |
| 4 | detalhamento | varchar(300) | S |  |  |  |
| 5 | dir_arquivo | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83FBD7AB923` (CLUSTERED): id

**FKs (saída):**
- id_atendimento → muralha.atendimento.id

**Referenciada por:**
- muralha.registro_fato_documento.id_atendimento

## muralha.atendimento_guarnicao

Linhas: ~21

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_atendimento | int | N |  |  |  |
| 3 | id_situacao | int | N |  |  |  |
| 4 | id_guarnicao | int | S |  |  |  |
| 5 | id_responsavel_app | int | S |  |  |  |
| 6 | observacao | varchar(500) | S |  |  |  |
| 7 | data | datetime | N |  |  |  |
| 8 | enviado_mobile | int | N |  | ((0)) |  |
| 9 | data_envio_mobile | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83FCB491EFD` (CLUSTERED): id

**FKs (saída):**
- id_responsavel_app → dbo.sis_usuario.id_usuario
- id_atendimento → muralha.atendimento.id
- id_guarnicao → muralha.guarnicao.id
- id_situacao → muralha.atendimento_guarnicao_situacao.id

**Referenciada por:**
- muralha.atendimento_guarnicao_presencial.id_atendimento_guarnicao

## muralha.atendimento_guarnicao_presencial

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_atendimento_guarnicao | int | N |  |  |  |
| 3 | avaliacao_inicial | varchar(500) | S |  |  |  |
| 4 | envolvidos | varchar(500) | S |  |  |  |
| 5 | orientacoes | varchar(500) | S |  |  |  |
| 6 | id_tipo_desfecho | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83F3A72090F` (CLUSTERED): id

**FKs (saída):**
- id_atendimento_guarnicao → muralha.atendimento_guarnicao.id
- id_tipo_desfecho → muralha.atendimento_guarnicao_tipo_desfecho.id

## muralha.atendimento_guarnicao_situacao

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83F9F1D30C4` (CLUSTERED): id

**Referenciada por:**
- muralha.atendimento_guarnicao.id_situacao

## muralha.atendimento_guarnicao_tipo_desfecho

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83FF6E43646` (CLUSTERED): id

**Referenciada por:**
- muralha.atendimento_guarnicao_presencial.id_tipo_desfecho

## muralha.atendimento_historico

Linhas: ~60

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_atendimento | int | N |  |  |  |
| 3 | id_tipo_historico | int | N |  |  |  |
| 4 | evento | varchar(500) | S |  |  |  |
| 5 | data | datetime | N |  |  |  |
| 6 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83F702B389F` (CLUSTERED): id

**FKs (saída):**
- id_atendimento → muralha.atendimento.id
- id_tipo_historico → muralha.atendimento_historico_tipo.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.atendimento_historico_tipo

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83FEB2DBA85` (CLUSTERED): id

**Referenciada por:**
- muralha.atendimento_historico.id_tipo_historico

## muralha.atendimento_origem

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83F37327C3C` (CLUSTERED): id

**Referenciada por:**
- muralha.atendimento.id_origem

## muralha.atendimento_situacao

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__atendime__3213E83F39CB15B9` (CLUSTERED): id

**Referenciada por:**
- muralha.atendimento.id_situacao

