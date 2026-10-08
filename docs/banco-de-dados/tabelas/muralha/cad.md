# Tabelas — schema `muralha` — grupo `cad`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.cad_veiculo_exclusao

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | S |  |  |  |
| 2 | datacad | datetime | S |  |  |  |

## muralha.cad_veiculo_monitorado

Linhas: ~487

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | placa | char(7) | S |  |  |  |
| 3 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 4 | descricao | varchar(300) | S |  |  |  |
| 5 | data_inicio | date | N |  |  |  |
| 6 | data_fim | date | S |  |  |  |
| 7 | data_cadastro | datetime | N |  | (getdate()) |  |
| 8 | id_usuario | int | N |  |  |  |
| 9 | data_exclusao | datetime | S |  |  |  |
| 10 | id_usuario_exclusao | int | S |  |  |  |
| 11 | motivo_exclusao | varchar(50) | S |  |  |  |
| 12 | id_usuario_atualizacao | int | S |  |  |  |
| 13 | data_atualizacao | datetime | S |  |  |  |
| 14 | nome | varchar(200) | S |  |  |  |
| 15 | data_inativacao | datetime | S |  |  |  |
| 16 | id_usuario_inativacao | int | S |  |  |  |
| 17 | privado | bit | S |  | ((0)) |  |
| 18 | supervisionado | bit | S |  | ((0)) |  |
| 20 | id_registro_fato | bigint | S |  |  |  |
| 21 | id_usuario_responsavel | int | S |  |  |  |
| 22 | monitorar_somente_este | int | S |  |  |  |
| 23 | erros_permitidos_placa | int | S |  |  |  |
| 24 | erros_permitido_ini | time | S |  |  |  |
| 25 | erros_permitido_fim | time | S |  |  |  |
| 26 | id_classe | char(1) | S |  |  |  |
| 27 | id_cor | int | S |  |  |  |
| 28 | id_marca | int | S |  |  |  |
| 29 | id_modelo | int | S |  |  |  |
| 30 | texto_adesivo | varchar(25) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_veiculo_monitorado_placa` (NONCLUSTERED): placa
- PK `PK_muralha_cad_veiculo_monitorado` (CLUSTERED): id

**FKs (saída):**
- id_registro_fato → muralha.registro_fato.id
- id_usuario_responsavel → dbo.sis_usuario.id_usuario
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_atualizacao → dbo.sis_usuario.id_usuario
- id_usuario_exclusao → dbo.sis_usuario.id_usuario
- id_usuario_inativacao → dbo.sis_usuario.id_usuario

**Referenciada por:**
- muralha.alerta.id_cad_veiculo_monitorado
- muralha.cad_veiculo_monitorado_equipamento.id_cad_veiculo_monitorado
- muralha.cad_veiculo_monitorado_grupo.id_cad_veiculo_monitorado
- muralha.cad_veiculo_monitorado_historico.id_cad_veiculo_monitorado
- muralha.cad_veiculo_monitorado_periodo.id_cad_veiculo_monitorado

## muralha.cad_veiculo_monitorado_c037

Linhas: ~385

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | placa | char(7) | S |  |  |  |
| 3 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 4 | descricao | varchar(300) | S |  |  |  |
| 5 | data_inicio | date | N |  |  |  |
| 6 | data_fim | date | S |  |  |  |
| 7 | data_cadastro | smalldatetime | N |  |  |  |
| 8 | id_usuario | int | N |  |  |  |
| 9 | data_exclusao | smalldatetime | S |  |  |  |
| 10 | id_usuario_exclusao | int | S |  |  |  |
| 11 | motivo_exclusao | varchar(50) | S |  |  |  |
| 12 | id_usuario_atualizacao | int | S |  |  |  |
| 13 | data_atualizacao | smalldatetime | S |  |  |  |
| 14 | nome | varchar(200) | S |  |  |  |
| 15 | data_inativacao | smalldatetime | S |  |  |  |
| 16 | id_usuario_inativacao | int | S |  |  |  |

## muralha.cad_veiculo_monitorado_equipamento

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_cad_veiculo_monitorado | uniqueidentifier | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | data_cadastro | datetime | N |  | (getdate()) |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_cad_veiculo_monitorado_equipamento` (CLUSTERED): id

**FKs (saída):**
- id_cad_veiculo_monitorado → muralha.cad_veiculo_monitorado.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.cad_veiculo_monitorado_grupo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_cad_veiculo_monitorado | uniqueidentifier | N |  |  |  |
| 3 | id_grupo | int | N |  |  |  |
| 4 | data_cadastro | datetime | N |  | (getdate()) |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_cad_veiculo_monitorado_grupo` (CLUSTERED): id

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_cad_veiculo_monitorado → muralha.cad_veiculo_monitorado.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.cad_veiculo_monitorado_historico

Linhas: ~131

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_cad_veiculo_monitorado | uniqueidentifier | N |  |  |  |
| 3 | id_usuario_responsavel | int | N |  |  |  |
| 4 | data_acao | datetime | N |  | (getdate()) |  |
| 5 | dados_json | nvarchar(max) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_cad_veiculo_monitorado_historico` (CLUSTERED): id

**FKs (saída):**
- id_cad_veiculo_monitorado → muralha.cad_veiculo_monitorado.id
- id_usuario_responsavel → dbo.sis_usuario.id_usuario

## muralha.cad_veiculo_monitorado_periodo

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_cad_veiculo_monitorado | uniqueidentifier | N |  |  |  |
| 3 | dia_semana | int | N |  |  |  |
| 4 | hora_inicio | time | S |  |  |  |
| 5 | hora_fim | time | S |  |  |  |
| 6 | data_cadastro | datetime | N |  | (getdate()) |  |
| 7 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_cad_veiculo_monitorado_periodo` (CLUSTERED): id

**FKs (saída):**
- id_cad_veiculo_monitorado → muralha.cad_veiculo_monitorado.id
- id_usuario → dbo.sis_usuario.id_usuario

