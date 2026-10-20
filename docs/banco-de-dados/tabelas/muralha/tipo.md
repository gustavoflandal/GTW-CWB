# Tabelas — schema `muralha` — grupo `tipo`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas. Tabelas novas ou alteradas atualizadas em 2026-10-20.

## muralha.tipo_alerta_config

Linhas: ~15

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 2 | critico | bit | N |  | ((0)) |  |
| 3 | cor | varchar(30) | S |  |  |  |
| 4 | icone | varchar(50) | S |  |  |  |
| 5 | dt_atualizacao | datetime | N |  | (getdate()) |  |
| 6 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_tipo_alerta_config` (CLUSTERED): id_tipo_alerta_ocorrencia

**FKs (saída):**
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id

## muralha.tipo_alerta_ocorrencia

Linhas: ~15

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | tipo | varchar(40) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | descricao_sms | varchar(20) | S |  |  |  |
| 5 | permite_monitorado_sem_placa | bit | N |  | ((0)) |  |
| 6 | tarefa_ativa | bit | N |  | ((1)) |  |
| 7 | data_alteracao | datetime | S |  |  |  |
| 8 | id_usuario | int | S |  |  |  |
| 9 | nomeinterno | nchar(20) | S |  |  |  |
| 10 | prioridade | int | S |  |  |  |
| 11 | cor | varchar(7) | S |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_tipo_alerta_ocorrencia_cor` (NONCLUSTERED): cor
- PK `PK_muralha_tipo_alerta_ocorrencia` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta.id_tipo_alerta_ocorrencia
- muralha.blitz_tipo_alerta.id_tipo_alerta_ocorrencia
- muralha.cad_veiculo_monitorado.id_tipo_alerta_ocorrencia
- muralha.config_alerta.id_tipo_alerta_ocorrencia
- muralha.config_grupo_permissao.id_tipo_alerta_ocorrencia
- muralha.ocorrencia.id_tipo_alerta_ocorrencia
- muralha.tipo_alerta_config.id_tipo_alerta_ocorrencia
- muralha.tipo_alerta_tratamento.id_tipo_alerta_ocorrencia
- muralha.tipo_ocorrencia_status.id_tipo_ocorrencia

## muralha.tipo_alerta_tratamento

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | tratamento | varchar(40) | N |  |  |  |
| 4 | automatico | bit | N |  | ((0)) |  |
| 5 | parametros_json | varchar(max) | S |  |  |  |
| 6 | ativo | bit | N |  | ((1)) |  |
| 7 | dt_cadastro | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_tipo_alerta_tratamento` (CLUSTERED): id
- UNIQUE `UQ_tat_tipo_trat` (NONCLUSTERED): id_tipo_alerta_ocorrencia, tratamento

**FKs (saída):**
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id

## muralha.tipo_notificacao

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(10) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_tipo_notificacao` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta_notificacao.id_tipo_notificacao
- muralha.config_grupo_permissao.id_tipo_notificacao
- muralha.ocorrencia_notificacao.id_tipo_notificacao
- muralha.ocorrencia_notificacao_historico.id_tipo_notificacao

## muralha.tipo_ocorrencia_status

Linhas: ~32

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_tipo_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | id_status_ocorrencia | uniqueidentifier | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_tipo_ocorrencia_status` (CLUSTERED): id

**FKs (saída):**
- id_status_ocorrencia → muralha.status_ocorrencia.id
- id_tipo_ocorrencia → muralha.tipo_alerta_ocorrencia.id

## muralha.tipo_registro

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | descricao | varchar(20) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_tipo_registro` (CLUSTERED): id

**Referenciada por:**
- muralha.config_grupo_permissao.id_tipo_registro

