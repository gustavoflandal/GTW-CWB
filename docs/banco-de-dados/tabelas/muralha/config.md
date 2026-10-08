# Tabelas — schema `muralha` — grupo `config`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.config_alarme

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_tipo | int | N |  |  |  |
| 3 | cor | varchar(50) | N |  |  |  |
| 4 | som | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83F724D9A84` (CLUSTERED): id

**FKs (saída):**
- id_tipo → muralha.config_alarme_tipo.id

## muralha.config_alarme_tipo

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | tipo | varchar(100) | N |  |  |  |
| 3 | prioridade | smallint | N |  |  |  |
| 4 | habilitado | bit | N |  |  |  |
| 5 | id_tipo_alerta | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83F631CF7C0` (CLUSTERED): id

**Referenciada por:**
- muralha.config_alarme.id_tipo

## muralha.config_alerta

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | tempo_exibicao | smallint | N |  |  |  |
| 4 | cor_exibicao | varchar(10) | S |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data_cadastro | datetime | N |  | (getdate()) |  |
| 7 | id_usuario_exclusao | int | S |  |  |  |
| 8 | data_exclusao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_config_alerta` (CLUSTERED): id

**FKs (saída):**
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_exclusao → dbo.sis_usuario.id_usuario

## muralha.config_alerta_clonado

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | tempo_consulta_max_segundos | int | N |  |  |  |
| 3 | velocidade_max | int | N |  |  |  |
| 4 | data_cadastro | datetime | N |  |  |  |
| 5 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83FA59CDB9F` (CLUSTERED): id

## muralha.config_alerta_comboio

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | tempo_entre_passagens_sec | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_criacao | datetime | N |  |  |  |
| 5 | id_usuario_alteracao | int | S |  |  |  |
| 6 | data_alteracao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83F0ABEC387` (CLUSTERED): id

## muralha.config_alerta_roubo_ponto_interesse

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | tempo_entre_passagens_sec | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_criacao | datetime | N |  |  |  |
| 5 | id_usuario_alteracao | int | S |  |  |  |
| 6 | data_alteracao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83F7569A031` (CLUSTERED): id

## muralha.config_alerta_transp_clandestino

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | qtde_passagens | int | N |  |  |  |
| 3 | periodo_inicial | time | N |  |  |  |
| 4 | periodo_final | time | N |  |  |  |
| 5 | classificacao | char(1) | S |  |  |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_criacao | datetime | N |  |  |  |
| 8 | id_usuario_alteracao | int | S |  |  |  |
| 9 | data_alteracao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__config_a__3213E83F16B40C39` (CLUSTERED): id

**FKs (saída):**
- id_usuario_alteracao → dbo.sis_usuario.id_usuario
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.config_chave_valor

Linhas: ~13

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | chave | varchar(100) | N |  |  |  |
| 2 | valor | varchar(255) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__config_c__52ACE05BDFA7CD37` (CLUSTERED): chave

## muralha.config_chave_valor_hist

Linhas: ~63

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | data_atualizacao | datetime | S |  |  |  |
| 4 | evento | varchar(255) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__config_c__3213E83FB00CBEF8` (CLUSTERED): id

## muralha.config_envio_tempo_real

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | enviar_todos | bit | N |  | ((0)) |  |
| 3 | data_inicio | datetime | N |  | (getdate()) |  |
| 4 | data_fim | datetime | S |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data_cadastro | datetime | N |  | (getdate()) |  |
| 7 | id_usuario_exclusao | int | S |  |  |  |
| 8 | data_exclusao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_config_envio_tempo_real` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_exclusao → dbo.sis_usuario.id_usuario

## muralha.config_envio_tempo_real_equipamento

Linhas: ~32

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_cadastro | datetime | N |  | (getdate()) |  |
| 4 | enviar | bit | S |  | ((0)) |  |
| 5 | data_exibicao_inicial_usuarios | datetime | N |  | (getdate()) |  |
| 6 | data_exibicao_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_muralha_config_envio_tempo_real_equipamento` (CLUSTERED): id

## muralha.config_grupo_permissao

Linhas: ~277

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_tipo_registro | uniqueidentifier | N |  |  |  |
| 3 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 4 | id_grupo | int | N |  |  |  |
| 5 | id_tipo_notificacao | uniqueidentifier | N |  |  |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_cadastro | datetime | N |  | (getdate()) |  |
| 8 | data_atualizacao | datetime | S |  |  |  |
| 9 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_muralha_config_grupo_permissao` (CLUSTERED): id

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_tipo_notificacao → muralha.tipo_notificacao.id
- id_tipo_registro → muralha.tipo_registro.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.config_intervalo_envio_tempo_real

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | segundos | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_config_intervalo_envio_tempo_real` (CLUSTERED): id

## muralha.config_mobile_silencio

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | data_inicio | datetime | N |  |  |  |
| 4 | data_fim | datetime | N |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | data_criacao | datetime | N |  | (getdate()) |  |
| 7 | comportamento | tinyint | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__config_m__3213E83FC41600A5` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.config_monitoramento_ao_vivo

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newsequentialid()) |  |
| 2 | segundos | int | N |  |  |  |
| 3 | data_configuracao | datetime | N |  | (getdate()) |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_config_monitoramento_ao_vivo` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.config_monitoramento_ao_vivo_cameras

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newsequentialid()) |  |
| 2 | id_local | int | N |  |  |  |
| 3 | ip | varchar(30) | N |  |  |  |
| 4 | ip_local | varchar(30) | N |  |  |  |
| 5 | url_stream | varchar(100) | S |  |  |  |
| 6 | descricao_camera | varchar(50) | N |  |  |  |
| 7 | qualidade | int | S |  |  |  |
| 8 | frame_rate | int | S |  |  |  |
| 9 | resolucao | varchar(20) | S |  |  |  |
| 10 | tipo_camera | varchar(20) | S |  |  |  |
| 11 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_muralha_config_monitoramento_ao_vivo_cameras` (CLUSTERED): id

## muralha.config_semelhanca_placa

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | erros_permitidos | int | N |  | ((0)) |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_cadastro | datetime | N |  | (getdate()) |  |
| 5 | id_usuario_exclusao | int | S |  |  |  |
| 6 | data_exclusao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_config_semelhanca_placa` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_exclusao → dbo.sis_usuario.id_usuario

