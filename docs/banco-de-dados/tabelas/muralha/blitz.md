# Tabelas — schema `muralha` — grupo `blitz`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.blitz_abordagem

Linhas: ~72

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_blitz_digital | int | N |  |  |  |
| 3 | id_agente | int | N |  |  |  |
| 4 | id_alerta | uniqueidentifier | S |  |  |  |
| 5 | id_local | int | S |  |  |  |
| 6 | placa_veiculo | varchar(10) | S |  |  |  |
| 7 | data_abordagem | datetime | S |  | (getdate()) |  |
| 8 | latitude | decimal(11,8) | S |  |  |  |
| 9 | longitude | decimal(11,8) | S |  |  |  |
| 11 | motivo_cancelamento | varchar(200) | S |  |  |  |
| 12 | observacoes | text | S |  |  |  |
| 13 | id_registro_fato | bigint | S |  |  |  |
| 14 | data_criacao | datetime | S |  | (getdate()) |  |
| 15 | id_veiculo_tempo_real | uniqueidentifier | S |  |  |  |
| 16 | id_abordagem_origem | tinyint | S |  |  |  |
| 17 | id_status | int | S |  |  |  |
| 18 | id_resultado | int | S |  |  |  |
| 19 | marca_veiculo | varchar(50) | S |  |  |  |
| 20 | modelo_veiculo | varchar(50) | S |  |  |  |
| 21 | tipo_veiculo | varchar(30) | S |  |  |  |
| 22 | cor_veiculo | varchar(30) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__blitz_ab__3213E83FBAE31932` (CLUSTERED): id

**FKs (saída):**
- id_agente → dbo.sis_usuario.id_usuario
- id_alerta → muralha.alerta.id
- id_blitz_digital → muralha.blitz_digital.id
- id_abordagem_origem → muralha.abordagem_origem.id
- id_registro_fato → muralha.registro_fato.id
- id_resultado → muralha.blitz_abordagem_resultado.id
- id_status → muralha.blitz_abordagem_status.id
- id_veiculo_tempo_real → muralha.veiculo_tempo_real.id

**Referenciada por:**
- muralha.blitz_abordagem_imagem.id_abordagem
- muralha.blitz_documento.id_abordagem
- muralha.blitz_pessoa_envolvida.id_abordagem

## muralha.blitz_abordagem_imagem

Linhas: ~92

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_abordagem | bigint | N |  |  |  |
| 3 | nome_arquivo | varchar(255) | N |  |  |  |
| 4 | caminho_arquivo | varchar(500) | N |  |  |  |
| 5 | tipo_arquivo | varchar(100) | S |  |  |  |
| 6 | data_criacao | datetime | S |  | (getdate()) |  |
| 7 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__blitz_ab__3213E83FEC299402` (CLUSTERED): id

**FKs (saída):**
- id_abordagem → muralha.blitz_abordagem.id

## muralha.blitz_abordagem_resultado

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | codigo | varchar(30) | N |  |  |  |
| 3 | descricao | varchar(150) | N |  |  |  |
| 4 | id_status | int | N |  |  |  |
| 5 | ativo | bit | S |  | ((1)) |  |
| 6 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_blitz_abordagem_resultado` (CLUSTERED): id

**FKs (saída):**
- id_status → muralha.blitz_abordagem_status.id

**Referenciada por:**
- muralha.blitz_abordagem.id_resultado

## muralha.blitz_abordagem_status

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | S |  | ((1)) |  |
| 5 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_ab__3213E83FF312FA29` (CLUSTERED): id

**Referenciada por:**
- muralha.blitz_abordagem.id_status
- muralha.blitz_abordagem_resultado.id_status

## muralha.blitz_digital

Linhas: ~32

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome_blitz | varchar(100) | N |  |  |  |
| 3 | titulo_notificacao | varchar(20) | S |  |  |  |
| 4 | descricao | varchar(255) | S |  |  |  |
| 5 | data_inicio | datetime | S |  | (getdate()) |  |
| 6 | data_fim | datetime | S |  |  |  |
| 7 | data_criacao | datetime | S |  | (getdate()) |  |
| 8 | ativo | bit | S |  | ((1)) |  |
| 9 | notificar_agentes_proximos | bit | N |  | ((0)) |  |
| 10 | raio_notificacao_km | decimal(8,2) | S |  |  |  |
| 11 | id_tipo_blitz | int | S |  | ((1)) |  |
| 12 | endereco | varchar(255) | S |  |  |  |
| 13 | data_atualizacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_di__3213E83FA31A6ED0` (CLUSTERED): id

**FKs (saída):**
- id_tipo_blitz → muralha.blitz_tipo.id

**Referenciada por:**
- muralha.blitz_abordagem.id_blitz_digital
- muralha.blitz_guarnicao.id_blitz_digital
- muralha.blitz_local.id_blitz_digital
- muralha.blitz_tipo_alerta.id_blitz_digital
- muralha.blitz_usuario.id_blitz_digital

## muralha.blitz_documento

Linhas: ~38

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_abordagem | bigint | N |  |  |  |
| 3 | id_pessoa | bigint | S |  |  |  |
| 4 | tipo_documento | varchar(50) | S |  |  |  |
| 5 | numero_documento | varchar(100) | S |  |  |  |
| 6 | nome_titular | varchar(200) | S |  |  |  |
| 7 | validade | date | S |  |  |  |
| 8 | situacao | varchar(50) | S |  |  |  |
| 9 | observacoes | text | S |  |  |  |
| 10 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_do__3213E83FB265B7DC` (CLUSTERED): id

**FKs (saída):**
- id_abordagem → muralha.blitz_abordagem.id
- id_pessoa → muralha.blitz_pessoa_envolvida.id

**Referenciada por:**
- muralha.blitz_documento_arquivo.id_documento

## muralha.blitz_documento_arquivo

Linhas: ~49

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_documento | bigint | N |  |  |  |
| 3 | caminho_arquivo | varchar(500) | N |  |  |  |
| 4 | nome_arquivo_original | varchar(255) | N |  |  |  |
| 5 | tipo_arquivo | varchar(50) | S |  |  |  |
| 6 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_do__3213E83FD3B25A97` (CLUSTERED): id

**FKs (saída):**
- id_documento → muralha.blitz_documento.id

## muralha.blitz_guarnicao

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_blitz_digital | int | N |  |  |  |
| 3 | id_guarnicao | int | N |  |  |  |
| 4 | data_associacao | datetime | S |  | (getdate()) |  |
| 5 | ativo | bit | S |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__blitz_gu__3213E83F90AC1AFD` (CLUSTERED): id

**FKs (saída):**
- id_blitz_digital → muralha.blitz_digital.id
- id_guarnicao → muralha.guarnicao.id

## muralha.blitz_local

Linhas: ~339

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_blitz_digital | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | data_associacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_lo__3213E83F1D5DC2A1` (CLUSTERED): id

**FKs (saída):**
- id_blitz_digital → muralha.blitz_digital.id

## muralha.blitz_pessoa_envolvida

Linhas: ~48

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_abordagem | bigint | N |  |  |  |
| 3 | cpf | varchar(14) | S |  |  |  |
| 4 | nome_completo | varchar(200) | S |  |  |  |
| 5 | data_nascimento | date | S |  |  |  |
| 6 | tipo_envolvimento | varchar(50) | S |  |  |  |
| 7 | telefone | varchar(20) | S |  |  |  |
| 8 | sexo | varchar(20) | S |  |  |  |
| 9 | email | varchar(200) | S |  |  |  |
| 10 | observacoes | text | S |  |  |  |
| 11 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_pe__3213E83F06032D62` (CLUSTERED): id

**FKs (saída):**
- id_abordagem → muralha.blitz_abordagem.id

**Referenciada por:**
- muralha.blitz_documento.id_pessoa

## muralha.blitz_tipo

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ativo | bit | S |  | ((1)) |  |
| 5 | data_criacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__blitz_tipo` (CLUSTERED): id
- UNIQUE `UQ_blitz_tipo_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- muralha.blitz_digital.id_tipo_blitz

## muralha.blitz_tipo_alerta

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_blitz_digital | int | N |  |  |  |
| 3 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 4 | data_associacao | datetime | S |  | (getdate()) |  |
| 5 | ativo | bit | S |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__blitz_tipo_alerta` (CLUSTERED): id

**FKs (saída):**
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_blitz_digital → muralha.blitz_digital.id

## muralha.blitz_usuario

Linhas: ~31

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_blitz_digital | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_associacao | datetime | S |  | (getdate()) |  |
| 5 | ativo | bit | S |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK__blitz_us__3213E83F8BE5B60C` (CLUSTERED): id

**FKs (saída):**
- id_blitz_digital → muralha.blitz_digital.id
- id_usuario → dbo.sis_usuario.id_usuario

