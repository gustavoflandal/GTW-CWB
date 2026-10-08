# Tabelas — schema `muralha` — grupo `outros`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.abordagem_origem

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | tinyint | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | descricao | varchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_abordagem_origem` (CLUSTERED): id
- UNIQUE `UQ_abordagem_origem_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- muralha.blitz_abordagem.id_abordagem_origem

## muralha.agente_localizacao_atual

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | int | N |  |  |  |
| 2 | latitude | decimal(20,10) | N |  |  |  |
| 3 | longitude | decimal(20,10) | N |  |  |  |
| 4 | data_ultima_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__agente_l__4E3E04ADB0771CA0` (CLUSTERED): id_usuario

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.agente_localizacao_hist

Linhas: ~7050

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | latitude | decimal(20,10) | N |  |  |  |
| 4 | longitude | decimal(20,10) | N |  |  |  |
| 5 | data_registro | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- IDX `IDX_historico_usuario_data` (NONCLUSTERED): id_usuario, data_registro
- PK `PK__agente_l__3213E83FC6437AD6` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.anomalia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | tipo | varchar(100) | N |  |  |  |
| 3 | possui_anomalia | int | N |  |  |  |
| 4 | desc_anomalia | varchar(1000) | S |  |  |  |
| 5 | data_update | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__anomalia__3213E83F7BC20BA3` (CLUSTERED): id

## muralha.anotacao_contributiva

Linhas: ~167

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_alerta | uniqueidentifier | N |  |  |  |
| 3 | descricao | varchar(300) | N |  |  |  |
| 4 | data_cadastro | datetime | N |  | (getdate()) |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_anotacao_contributiva` (CLUSTERED): id

**FKs (saída):**
- id_alerta → muralha.alerta.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.antecedentes_criminais

Linhas: ~14

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_proprietario | int | N |  |  |  |
| 3 | tipo_crime | varchar(100) | N |  |  |  |
| 4 | data_ocorrencia | date | N |  |  |  |
| 5 | local_ocorrencia | varchar(255) | S |  |  |  |
| 6 | descricao | varchar(max) | S |  |  |  |
| 7 | sentenca | varchar(max) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_antecedentes_criminais_id_proprietario` (NONCLUSTERED): id_proprietario

## muralha.area_monitorada

Linhas: ~43

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | nvarchar(200) | N |  |  |  |
| 3 | data_cadastro | datetime | S |  | (getdate()) |  |
| 4 | data_atualizacao | datetime | S |  |  |  |
| 5 | dados_json | nvarchar(max) | N |  |  |  |
| 6 | deletado | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_AreaMonitorada` (NONCLUSTERED): id
- PK `PK__area_mon__3213E83FB70102C7` (CLUSTERED): id

**Referenciada por:**
- muralha.equipamentos_area_monitorada.id_area_monitorada

## muralha.boletim

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_situacao | int | N |  |  |  |
| 4 | data_hora_evento | datetime | N |  |  |  |
| 5 | detalhamento | varchar(5000) | S |  |  |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_criacao | datetime | N |  |  |  |
| 8 | data_encerramento | datetime | S |  |  |  |
| 9 | permite_atendimento | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__boletim__3213E83F599EDF4D` (CLUSTERED): id

**FKs (saída):**
- id_situacao → muralha.boletim_situacao.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_registro_fato → muralha.registro_fato.id

**Referenciada por:**
- muralha.boletim_apreensao.id_boletim

## muralha.boletim_apreensao

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_boletim | int | N |  |  |  |
| 3 | tipo | varchar(20) | N |  |  |  |
| 4 | descricao | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__boletim___3213E83F8C7FAE8C` (CLUSTERED): id

**FKs (saída):**
- id_boletim → muralha.boletim.id

## muralha.boletim_situacao

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(300) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__boletim___3213E83FF42ED149` (CLUSTERED): id

**Referenciada por:**
- muralha.boletim.id_situacao

## muralha.cidade

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | id_estado | int | N |  |  |  |
| 3 | nome | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__cidade__3213E83F18DE159E` (CLUSTERED): id

**FKs (saída):**
- id_estado → muralha.estado.id

**Referenciada por:**
- muralha.registro_fato_endereco.id_cidade

## muralha.controla_execucao_job

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ultima_geracao_comboio | datetime | S |  |  |  |
| 2 | ultima_geracao_roubo | datetime | S |  |  |  |
| 3 | ultima_geracao_transporte | datetime | S |  |  |  |

## muralha.equipamentos_area_monitorada

Linhas: ~54

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_area_monitorada | int | N |  |  |  |
| 3 | id_equipamento | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_EAM_Equipamento` (NONCLUSTERED): id_equipamento, id_area_monitorada
- IDX `IX_EAM_id_equipamento` (NONCLUSTERED): id_equipamento
- IDX `IX_EquipamentosAreaMonitorada` (NONCLUSTERED): id_equipamento, id_area_monitorada
- PK `PK__equipame__3213E83F41FCA4A8` (CLUSTERED): id

**FKs (saída):**
- id_area_monitorada → muralha.area_monitorada.id

## muralha.estado

Linhas: ~27

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | nome | varchar(5) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__estado__3213E83F5A2CF4E5` (CLUSTERED): id

**Referenciada por:**
- muralha.cidade.id_estado

## muralha.fato

Linhas: ~21

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | bigint | N |  |  |  |
| 3 | id_situacao | int | N |  |  |  |
| 4 | data_hora_evento | datetime | N |  |  |  |
| 5 | detalhamento | varchar(5000) | S |  |  |  |
| 6 | existe_arma_envolvida | int | N |  |  |  |
| 7 | id_usuario | int | N |  |  |  |
| 8 | data_criacao | datetime | N |  |  |  |
| 9 | data_encerramento | datetime | S |  |  |  |
| 10 | permite_atendimento | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__fato__3213E83FF70FFCF8` (CLUSTERED): id

**FKs (saída):**
- id_situacao → muralha.fato_situacao.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_registro_fato → muralha.registro_fato.id

## muralha.fato_situacao

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(30) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__fato_sit__3213E83F60BC1B6E` (CLUSTERED): id

**Referenciada por:**
- muralha.fato.id_situacao

## muralha.historico_config_grupo_permissao

Linhas: ~82

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_usuario_responsavel | int | N |  |  |  |
| 3 | data_acao | datetime2 | N |  |  |  |
| 4 | dados_json | nvarchar(max) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__historic__3213E83FA216E794` (CLUSTERED): id

## muralha.historico_correlacionamento_inibido

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | data | datetime | N |  | (getdate()) |  |
| 3 | id_correlacionamento | int | N |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | motivo | varchar(max) | N |  |  |  |

## muralha.historico_passagens_correlacionadas

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_correlacionamento | int | N |  |  |  |
| 2 | id_passagem_placa_alvo | uniqueidentifier | N |  |  |  |
| 3 | id_passagem_placa_correlacionada | uniqueidentifier | N |  |  |  |

## muralha.monitoramento_ao_vivo_grupo_exibicao

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_grupo_exibicao | int | N |  |  |  |

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

Linhas: ~1748

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

## muralha.ocorrencia

Linhas: ~58

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_alerta | uniqueidentifier | N |  |  |  |
| 3 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 4 | id_status_ocorrencia | uniqueidentifier | N |  |  |  |
| 5 | data | datetime | N |  | (getdate()) |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_modificacao | datetime | S |  |  |  |
| 8 | id_usuario_modificacao | int | S |  |  |  |
| 9 | observacao | varchar(200) | S |  |  |  |
| 11 | permite_atendimento | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_ocorrencia_id_alerta` (NONCLUSTERED): id_alerta
- PK `PK_muralha_ocorrencia` (CLUSTERED): id

**FKs (saída):**
- id_alerta → muralha.alerta.id
- id_status_ocorrencia → muralha.status_ocorrencia.id
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_modificacao → dbo.sis_usuario.id_usuario

**Referenciada por:**
- muralha.atendimento.id_ocorrencia
- muralha.ocorrencia_notificacao.id_ocorrencia
- muralha.ocorrencia_notificacao_historico.id_ocorrencia

## muralha.ocorrencia_notificacao

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | id_grupo | int | N |  |  |  |
| 4 | id_tipo_notificacao | uniqueidentifier | N |  |  |  |
| 5 | id_status_notificacao | uniqueidentifier | N |  | ('99AF55C6-2446-4B98-BBCD-83663C504C79') |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_cadastro | datetime | N |  | (getdate()) |  |
| 8 | data_processado | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_ocorrencia_notificacao` (CLUSTERED): id

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_ocorrencia → muralha.ocorrencia.id
- id_status_notificacao → muralha.status_notificacao.id
- id_tipo_notificacao → muralha.tipo_notificacao.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.ocorrencia_notificacao_historico

Linhas: ~72

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | id_grupo | int | N |  |  |  |
| 4 | id_tipo_notificacao | uniqueidentifier | N |  |  |  |
| 5 | id_status_notificacao | uniqueidentifier | N |  |  |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_cadastro | datetime | N |  |  |  |
| 8 | data_processado | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_ocorrencia_notificacao_historico` (CLUSTERED): id

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_ocorrencia → muralha.ocorrencia.id
- id_status_notificacao → muralha.status_notificacao.id
- id_tipo_notificacao → muralha.tipo_notificacao.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.ponto_interesse

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | nome | varchar(300) | N |  |  |  |
| 3 | descricao | varchar(2000) | N |  |  |  |
| 4 | id_tipo | int | N |  |  |  |
| 5 | latitude | decimal(19,17) | N |  |  |  |
| 6 | longitude | decimal(19,17) | N |  |  |  |
| 7 | data_cadastro | datetime | N |  |  |  |
| 8 | ativo | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ponto_in__3213E83FFC989053` (CLUSTERED): id

**FKs (saída):**
- id_tipo → muralha.ponto_interesse_tipos.id

**Referenciada por:**
- muralha.alerta.id_ponto_interesse
- muralha.ponto_interesse_equipamentos.id_ponto_interesse

## muralha.ponto_interesse_equipamentos

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_ponto_interesse | uniqueidentifier | N |  |  |  |
| 3 | id_local | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ponto_in__3213E83F96888CFE` (CLUSTERED): id

**FKs (saída):**
- id_ponto_interesse → muralha.ponto_interesse.id

## muralha.ponto_interesse_tipos

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ponto_in__3213E83F98237771` (CLUSTERED): id

**Referenciada por:**
- muralha.ponto_interesse.id_tipo

## muralha.proprietario

Linhas: ~7

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(100) | N |  |  |  |
| 3 | sobrenome | varchar(100) | N |  |  |  |
| 4 | cpf | varchar(14) | N |  |  |  |
| 5 | data_nascimento | date | S |  |  |  |
| 6 | endereco | varchar(255) | S |  |  |  |
| 7 | telefone | varchar(20) | S |  |  |  |
| 8 | email | varchar(100) | S |  |  |  |

## muralha.proprietario_veiculo

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_proprietario | int | N |  |  |  |
| 3 | placa | varchar(7) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_proprietario_veiculo_placa` (NONCLUSTERED): placa
- IDX `IX_proprietario_veiculo_placa_proprietario` (NONCLUSTERED): placa

## muralha.relatorio_imagens_exportadas

Linhas: ~19

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | placa | nvarchar(10) | N |  |  |  |
| 5 | data_hora_exportacao | datetime2 | N |  |  |  |
| 6 | data_hora_passagem | datetime2 | N |  |  |  |
| 7 | data_insercao | datetime2 | N |  | (sysdatetime()) |  |
| 9 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |

**Índices/Chaves:**
- PK `PK__relatori__3213E83F10B1C0F8` (CLUSTERED): id

**FKs (saída):**
- id_veiculo_tempo_real → muralha.veiculo_tempo_real.id

## muralha.token_acesso_mobilidade_urbana

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | key | nvarchar(255) | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_criacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_token_acesso_mobilidade_urbana` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.veiculo_tempo_real

Linhas: ~8188803

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | placa | char(7) | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | id_local | int | N |  |  |  |
| 5 | id_pista | tinyint | N |  |  |  |
| 6 | velocidade | smallint | N |  |  |  |
| 7 | enviado_cliente | bit | N |  | ((0)) |  |
| 8 | data_enviado | datetime | S |  | (getdate()) |  |
| 9 | classificacao | char(1) | S |  |  |  |
| 10 | estado_veiculo | tinyint | N |  | ((0)) |  |
| 11 | processado_tarefas_alerta | tinyint | N |  | ((0)) |  |
| 12 | data_processado_tarefas_alerta | datetime | S |  |  |  |
| 13 | data_importado | datetime | S |  | (getdate()) |  |
| 14 | id_usuario_alt | int | S |  |  |  |
| 15 | data_alt | datetime | S |  |  |  |
| 16 | dados_alt_orig | varchar(100) | S |  |  |  |
| 17 | enviado_blitz | bit | S |  | ((0)) |  |
| 18 | data_enviado_blitz | datetime | S |  |  |  |
| 19 | perfil_1 | varchar(8000) | S |  |  |  |
| 20 | perfil_2 | varchar(8000) | S |  |  |  |
| 21 | placa_frontal | varchar(7) | S |  |  |  |
| 22 | info_adicional | varchar(1000) | S |  |  |  |
| 23 | numero_eixos | int | S |  |  |  |
| 24 | rodagem_dupla | bit | S |  |  |  |
| 25 | categoria | int | S |  |  |  |
| 26 | placa_mercosul | bit | N |  | ((0)) |  |
| 28 | enviado_blitz_websocket | bit | S |  | ((0)) |  |
| 29 | data_enviado_blitz_websocket | datetime | S |  |  |  |
| 30 | id_captura | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_tempo_real_data` (NONCLUSTERED): data
- IDX `IX_veiculo_tempo_real_data_local_placa` (NONCLUSTERED): data, id_local, placa
- IDX `IX_veiculo_tempo_real_id_captura` (NONCLUSTERED): id_captura
- IDX `IX_veiculo_tempo_real_id_local_data` (NONCLUSTERED): id_local, data
- IDX `IX_veiculo_tempo_real_local_pista_data_placa` (NONCLUSTERED): id_local, id_pista, data, placa
- IDX `IX_veiculo_tempo_real_placa` (NONCLUSTERED): placa
- IDX `IX_veiculo_tempo_real_placa_data_local` (NONCLUSTERED): placa, data, id_local
- IDX `IX_veiculo_tempo_real_placa_mercosul` (NONCLUSTERED): placa_mercosul
- IDX `IX_veiculo_tempo_real_usuario_data_alt_placa` (NONCLUSTERED): id_usuario_alt, data_alt
- IDX `IX_VeiculoTempoReal_Data_Placa` (NONCLUSTERED): data, placa
- IDX `IX_VTR_Data_Placa_Local` (NONCLUSTERED): data, placa, id_local
- IDX `IX_VTR_RelatorioDiario` (NONCLUSTERED): placa, data
- PK `PK_muralha_veiculo_tempo_real` (CLUSTERED): id

**FKs (saída):**
- id_captura → ia.veiculo_caracteristica.id_captura

**Referenciada por:**
- muralha.alerta_veiculo.id_veiculo_tempo_real
- muralha.blitz_abordagem.id_veiculo_tempo_real
- muralha.registro_fato_passagem_veic.id_veiculo
- muralha.relatorio_imagens_exportadas.id_veiculo_tempo_real
- muralha.veiculo_tempo_real_correcao.id_veiculo
- muralha.veiculo_tempo_real_imagem.id_veiculo_tempo_real

## muralha.veiculo_tempo_real_correcao

Linhas: ~139

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_veiculo | uniqueidentifier | N |  |  |  |
| 3 | placa_original | char(7) | S |  |  |  |
| 4 | placa_digitada | char(7) | S |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__veiculo___3213E83FD1A9FB54` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_veiculo → muralha.veiculo_tempo_real.id

## muralha.veiculo_tempo_real_imagem

Linhas: ~10913304

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 3 | imagem | image | N |  |  |  |
| 4 | indice_imagem | tinyint | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_veiculo_tempo_real_imagem_id_veiculo` (NONCLUSTERED): id_veiculo_tempo_real
- IDX `IX_veiculo_tempo_real_imagem_id_veiculo_indice_img` (NONCLUSTERED): id_veiculo_tempo_real
- PK `PK_muralha_veiculo_tempo_real_imagem` (CLUSTERED): id

**FKs (saída):**
- id_veiculo_tempo_real → muralha.veiculo_tempo_real.id

## muralha.video_monitoramento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_local | int | N |  |  |  |
| 3 | ip_camera | varchar(20) | N |  |  |  |
| 4 | data_hora | datetime | N |  |  |  |
| 5 | endereco | varchar(300) | N |  |  |  |
| 6 | formato | varchar(5) | N |  |  |  |
| 7 | data_importacao | datetime | N |  | (getdate()) |  |
| 8 | md5 | binary | N |  |  |  |
| 9 | nome_arquivo | varchar(40) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_video_monitoramento_id_local_data_hora` (NONCLUSTERED): id_local, data_hora
- PK `PK_muralha_video_monitoramento` (CLUSTERED): id
- UNIQUE `UI_nome_arquivo` (NONCLUSTERED): nome_arquivo

## muralha.vinculo_registro_fato_correlacionamento

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_correlacionamento | int | N |  |  |  |
| 2 | id_registro_fato | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_cadastro | datetime | N |  |  |  |

