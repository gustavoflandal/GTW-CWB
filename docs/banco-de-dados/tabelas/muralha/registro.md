# Tabelas — schema `muralha` — grupo `registro`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.registro_fato

Linhas: ~54

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_tipo | int | N |  |  |  |
| 3 | id_status | int | N |  |  |  |
| 4 | tem_boletim | int | N |  | ((0)) |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data_criacao | datetime | N |  |  |  |
| 7 | data_encerramento | datetime | S |  |  |  |
| 8 | privado | int | N |  | ((0)) |  |
| 9 | id_tipo_natureza | int | S |  |  |  |
| 10 | data_modificacao | datetime | S |  |  |  |
| 11 | data_evento | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F8BAA58FD` (CLUSTERED): id

**FKs (saída):**
- id_tipo_natureza → muralha.registro_fato_natureza.id
- id_status → muralha.registro_fato_status.id
- id_tipo → muralha.registro_fato_tipo.id
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- muralha.atendimento.id_registro_fato
- muralha.blitz_abordagem.id_registro_fato
- muralha.boletim.id_registro_fato
- muralha.cad_veiculo_monitorado.id_registro_fato
- muralha.fato.id_registro_fato
- muralha.registro_fato_anotacao.id_registro_fato
- muralha.registro_fato_documento.id_registro_fato
- muralha.registro_fato_endereco.id_registro_fato
- muralha.registro_fato_historico.id_registro
- muralha.registro_fato_individuo.id_registro_fato
- muralha.registro_fato_link.id_registro_fato
- muralha.registro_fato_objeto.id_registro_fato
- muralha.registro_fato_passagem_veic.id_registro_fato
- muralha.registro_fato_usuario_grupo.id_registro_fato
- muralha.registro_fato_veiculo.id_registro_fato

## muralha.registro_fato_anotacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | texto | varchar(500) | N |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | data_criacao | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FDCE83670` (CLUSTERED): id

**FKs (saída):**
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_documento

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | tipo | varchar(20) | N |  |  |  |
| 4 | dir_arquivo | varchar(200) | N |  |  |  |
| 5 | detalhamento | varchar(300) | S |  |  |  |
| 6 | id_atendimento | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FFE42F8E5` (CLUSTERED): id

**FKs (saída):**
- id_atendimento → muralha.atendimento_documento.id
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_endereco

Linhas: ~52

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_tipo_evento | int | N |  |  |  |
| 4 | id_cidade | int | N |  |  |  |
| 5 | cep | varchar(12) | N |  |  |  |
| 6 | bairro | varchar(100) | N |  |  |  |
| 7 | rua | varchar(500) | N |  |  |  |
| 8 | numero | int | N |  |  |  |
| 9 | complemento | varchar(100) | N |  |  |  |
| 10 | latitude | decimal(20,15) | S |  |  |  |
| 11 | longitude | decimal(20,15) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FA908D651` (CLUSTERED): id

**FKs (saída):**
- id_cidade → muralha.cidade.id
- id_tipo_evento → muralha.registro_fato_endereco_evento.id
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_endereco_evento

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(300) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FBF9231F3` (CLUSTERED): id

**Referenciada por:**
- muralha.registro_fato_endereco.id_tipo_evento

## muralha.registro_fato_historico

Linhas: ~183

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_historico | bigint | N | S |  |  |
| 2 | id_registro | bigint | N |  |  |  |
| 3 | dados_anteriores | nvarchar(max) | S |  |  |  |
| 4 | dados_novos | nvarchar(max) | S |  |  |  |
| 5 | tipo_operacao | nvarchar(20) | N |  |  |  |
| 6 | id_usuario | int | S |  |  |  |
| 7 | data_alteracao | datetime2 | S |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `IX_registro_fato_historico_id_registro` (NONCLUSTERED): id_registro
- PK `PK__registro__76E62AC3C7F73846` (CLUSTERED): id_historico

**FKs (saída):**
- id_registro → muralha.registro_fato.id

## muralha.registro_fato_individuo

Linhas: ~55

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_tipo_envolvimento | int | N |  |  |  |
| 4 | detalhe_envolvimento | varchar(1000) | S |  |  |  |
| 5 | nome | varchar(200) | N |  |  |  |
| 6 | cpf | varchar(20) | N |  |  |  |
| 7 | ddd | int | S |  |  |  |
| 8 | telefone | varchar(20) | S |  |  |  |
| 9 | email | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F916AB3A4` (CLUSTERED): id

**FKs (saída):**
- id_tipo_envolvimento → muralha.registro_fato_individuo_tipo.id
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_individuo_tipo

Linhas: ~15

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F812C3F9A` (CLUSTERED): id

**Referenciada por:**
- muralha.registro_fato_individuo.id_tipo_envolvimento

## muralha.registro_fato_link

Linhas: ~10

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | url | varchar(300) | N |  |  |  |
| 4 | detalhamento | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F4882B634` (CLUSTERED): id

**FKs (saída):**
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_natureza

Linhas: ~25

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | id_registro_tipo | int | N |  |  |  |
| 3 | natureza_desc | varchar(255) | N |  |  |  |
| 4 | requer_bo | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FBC3076C9` (CLUSTERED): id

**FKs (saída):**
- id_registro_tipo → muralha.registro_fato_tipo.id

**Referenciada por:**
- muralha.registro_fato.id_tipo_natureza
- muralha.registro_fato_natureza_delituosa.id_registro_natureza

## muralha.registro_fato_natureza_delituosa

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | id_registro_natureza | int | N |  |  |  |
| 3 | natureza_delituosa_desc | varchar(255) | N |  |  |  |
| 4 | codigo_penal_lei | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F5BE0446A` (CLUSTERED): id

**FKs (saída):**
- id_registro_natureza → muralha.registro_fato_natureza.id

## muralha.registro_fato_objeto

Linhas: ~33

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | tipo | varchar(20) | N |  |  |  |
| 4 | descricao | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83FF2923415` (CLUSTERED): id

**FKs (saída):**
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_passagem_veic

Linhas: ~22

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_veiculo | uniqueidentifier | N |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | data | datetime | N |  |  |  |
| 6 | valido | bit | S |  | ((0)) |  |
| 7 | origem | varchar(max) | S |  | (NULL) |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F96A52AE7` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_veiculo → muralha.veiculo_tempo_real.id
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_status

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(30) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F775C8EE4` (CLUSTERED): id

**Referenciada por:**
- muralha.registro_fato.id_status

## muralha.registro_fato_tipo

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | tipo_desc | varchar(255) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F7CA7D24D` (CLUSTERED): id

**Referenciada por:**
- muralha.registro_fato.id_tipo
- muralha.registro_fato_natureza.id_registro_tipo

## muralha.registro_fato_usuario_grupo

Linhas: ~108

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_usuario | int | S |  |  |  |
| 4 | id_grupo | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F22E609BE` (CLUSTERED): id

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_usuario → dbo.sis_usuario.id_usuario
- id_registro_fato → muralha.registro_fato.id

## muralha.registro_fato_veiculo

Linhas: ~40

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | placa | varchar(7) | N |  |  |  |
| 4 | cor | varchar(50) | N |  |  |  |
| 5 | marca | varchar(20) | N |  |  |  |
| 6 | modelo | varchar(60) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__registro__3213E83F7AAC0748` (CLUSTERED): id

**FKs (saída):**
- id_registro_fato → muralha.registro_fato.id

