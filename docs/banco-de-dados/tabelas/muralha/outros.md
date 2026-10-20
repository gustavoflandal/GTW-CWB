# Tabelas — schema `muralha` — grupo `outros`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas. Tabelas novas ou alteradas atualizadas em 2026-10-20.

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

## muralha.dashboard_dataset

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(80) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | nome_view | varchar(128) | N |  |  |  |
| 5 | colunas_json | varchar(max) | N |  |  |  |
| 6 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_dashboard_dataset` (CLUSTERED): id

**Referenciada por:**
- muralha.dashboard_widget.id_dataset

## muralha.dashboard_painel

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(80) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | refresh_segundos | int | N |  | ((60)) |  |
| 6 | publico | bit | N |  | ((0)) |  |
| 7 | criado_em | datetime | N |  | (getdate()) |  |
| 8 | atualizado_em | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_dp_usuario` (NONCLUSTERED): id_usuario
- PK `PK_dashboard_painel` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- muralha.dashboard_widget.id_painel

## muralha.dashboard_widget

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_painel | int | N |  |  |  |
| 3 | tipo | varchar(10) | N |  |  |  |
| 4 | titulo | varchar(80) | N |  |  |  |
| 5 | id_dataset | int | N |  |  |  |
| 6 | coluna_valor | varchar(60) | S |  |  |  |
| 7 | coluna_label | varchar(60) | S |  |  |  |
| 8 | coluna_lat | varchar(60) | S |  |  |  |
| 9 | coluna_lng | varchar(60) | S |  |  |  |
| 10 | agregacao | varchar(10) | N |  | ('NONE') |  |
| 11 | filtros_json | varchar(max) | S |  |  |  |
| 12 | tipo_grafico | varchar(15) | S |  |  |  |
| 13 | colunas_tabela | varchar(max) | S |  |  |  |
| 14 | threshold_ok | float | S |  |  |  |
| 15 | threshold_warn | float | S |  |  |  |
| 16 | grid_col | int | N |  | ((1)) |  |
| 17 | grid_row | int | N |  | ((1)) |  |
| 18 | grid_width | int | N |  | ((4)) |  |
| 19 | grid_height | int | N |  | ((2)) |  |
| 20 | ordem | int | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_dw_painel` (NONCLUSTERED): id_painel
- PK `PK_dashboard_widget` (CLUSTERED): id

**FKs (saída):**
- id_dataset → muralha.dashboard_dataset.id
- id_painel → muralha.dashboard_painel.id

## muralha.equipamento_disponibilidade

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | dt_verificacao | datetime2 | N |  | (sysdatetime()) |  |
| 3 | id_local | int | N |  |  |  |
| 4 | disponivel | bit | N |  |  |  |
| 5 | ultima_passagem | datetime2 | S |  |  |  |
| 6 | minutos_offline | int | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_eq_disp_local` (NONCLUSTERED): id_local, dt_verificacao
- PK `PK__equipame__3213E83F9D3E8B0B` (CLUSTERED): id

## muralha.equipamento_integridade_config

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | origem_hash | varchar(20) | N |  |  |  |
| 3 | campo | varchar(100) | S |  |  |  |
| 4 | ativo | bit | N |  | ((1)) |  |
| 5 | id_usuario | int | S |  |  |  |
| 6 | data_modificacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_equipamento_integridade_config` (CLUSTERED): id_local

## muralha.equipamento_ocr_config

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | origem_score | varchar(20) | S |  |  |  |
| 3 | campo | varchar(100) | S |  |  |  |
| 4 | limiar | decimal(5,2) | S |  |  |  |
| 5 | ativo | bit | N |  | ((1)) |  |
| 6 | id_usuario | int | S |  |  |  |
| 7 | data_modificacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_equipamento_ocr_config` (CLUSTERED): id_local

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

## muralha.expurgo_log

Linhas: ~45

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | dt_execucao | datetime2 | N |  | (sysdatetime()) |  |
| 3 | tabela | varchar(100) | N |  |  |  |
| 4 | registros_movidos | int | N |  |  |  |
| 5 | retencao_anos | int | N |  |  |  |
| 6 | dt_corte | date | N |  |  |  |
| 7 | status | varchar(20) | N |  |  |  |
| 8 | mensagem | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__expurgo___3213E83FE3799565` (CLUSTERED): id

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

## muralha.incidente_externo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | fonte | varchar(20) | N |  |  |  |
| 3 | id_externo | varchar(100) | S |  |  |  |
| 4 | tipo | varchar(50) | N |  |  |  |
| 5 | descricao | nvarchar(500) | S |  |  |  |
| 6 | latitude | decimal(10,7) | N |  |  |  |
| 7 | longitude | decimal(10,7) | N |  |  |  |
| 8 | severidade | int | S |  |  |  |
| 9 | dt_ocorrencia | datetime2 | N |  |  |  |
| 10 | dt_importacao | datetime2 | N |  | (sysdatetime()) |  |
| 11 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- IDX `ix_incidente_dt` (NONCLUSTERED): dt_ocorrencia, ativo
- PK `PK__incident__3213E83FE75E052E` (CLUSTERED): id
- UNIQUE `uq_incidente` (NONCLUSTERED): fonte, id_externo, dt_ocorrencia

## muralha.infracao_analise

Linhas: ~60

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_infracao | uniqueidentifier | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | sequencia | tinyint | N |  |  |  |
| 5 | classificacao | varchar(20) | N |  |  |  |
| 6 | justificativa | varchar(500) | S |  |  |  |
| 7 | dt_analise | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_ia_infracao` (NONCLUSTERED): id_infracao
- IDX `ix_ia_usuario` (NONCLUSTERED): id_usuario
- PK `PK__infracao__3213E83F2191E52E` (CLUSTERED): id
- UNIQUE `uq_infracao_usuario` (NONCLUSTERED): id_infracao, id_usuario

## muralha.infracao_imagem_obliteracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_imagem_original | uniqueidentifier | N |  |  |  |
| 3 | id_imagem_obliterada | uniqueidentifier | S |  |  |  |
| 4 | tipo | char(1) | N |  |  |  |
| 5 | coordenadas_json | varchar(max) | N |  |  |  |
| 6 | dt_aplicacao | datetime2 | N |  | (sysdatetime()) |  |
| 7 | id_usuario_aplicou | int | N |  |  |  |
| 8 | revertida | bit | N |  | ((0)) |  |
| 9 | dt_reversao | datetime2 | S |  |  |  |
| 10 | id_usuario_reverteu | int | S |  |  |  |
| 11 | justificativa_reversao | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__infracao__3213E83F5DB22341` (CLUSTERED): id

## muralha.kpi_config

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(80) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | query_sql | varchar(max) | N |  |  |  |
| 5 | unidade | varchar(20) | S |  |  |  |
| 6 | threshold_ok | float | S |  |  |  |
| 7 | threshold_warn | float | S |  |  |  |
| 8 | ordem | int | N |  | ((0)) |  |
| 9 | ativo | bit | N |  | ((1)) |  |
| 10 | tipo_visual | varchar(20) | N |  | ('card') |  |
| 11 | cor_personalizada | varchar(7) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__kpi_conf__3213E83F7E71461E` (CLUSTERED): id

**Referenciada por:**
- muralha.kpi_config_meta.id_kpi
- muralha.kpi_historico.id_kpi

## muralha.kpi_config_meta

Linhas: ~12

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_kpi | int | N |  |  |  |
| 2 | meta | float | S |  |  |  |
| 3 | meta_minima | float | S |  |  |  |
| 4 | categoria | varchar(60) | N |  | ('geral') |  |
| 5 | gerar_alerta | bit | N |  | ((1)) |  |
| 6 | dt_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_kpi_config_meta` (CLUSTERED): id_kpi

**FKs (saída):**
- id_kpi → muralha.kpi_config.id

## muralha.kpi_historico

Linhas: ~573

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_kpi | int | N |  |  |  |
| 3 | valor | float | N |  |  |  |
| 4 | status | varchar(10) | N |  |  |  |
| 5 | dt_coleta | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- IDX `IX_kpi_historico_kpi_dt` (NONCLUSTERED): id_kpi, dt_coleta
- PK `PK_kpi_historico` (CLUSTERED): id

**FKs (saída):**
- id_kpi → muralha.kpi_config.id

## muralha.lote_infracao

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | codigo | varchar(30) | N |  |  |  |
| 3 | descricao | varchar(200) | S |  |  |  |
| 4 | status | varchar(20) | N |  | ('RASCUNHO') |  |
| 5 | dt_criacao | datetime2 | N |  | (sysdatetime()) |  |
| 6 | dt_envio | datetime2 | S |  |  |  |
| 7 | dt_confirmacao | datetime2 | S |  |  |  |
| 8 | id_usuario_criou | int | N |  |  |  |
| 9 | id_usuario_enviou | int | S |  |  |  |
| 10 | observacao | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_lote_status` (NONCLUSTERED): status
- PK `PK__lote_inf__3213E83FDBF1DBAF` (CLUSTERED): id
- UNIQUE `UQ__lote_inf__40F9A20630516969` (NONCLUSTERED): codigo

**Referenciada por:**
- muralha.lote_infracao_item.id_lote

## muralha.lote_infracao_item

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_lote | bigint | N |  |  |  |
| 3 | id_infracao | uniqueidentifier | N |  |  |  |
| 4 | dt_inclusao | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_lote_item_lote` (NONCLUSTERED): id_lote
- PK `PK__lote_inf__3213E83F298AB7E4` (CLUSTERED): id
- UNIQUE `uq_lote_item` (NONCLUSTERED): id_infracao

**FKs (saída):**
- id_lote → muralha.lote_infracao.id

## muralha.monitoramento_ao_vivo_grupo_exibicao

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_grupo_exibicao | int | N |  |  |  |

## muralha.notificacao_retry

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_notificacao | uniqueidentifier | N |  |  |  |
| 3 | tipo_registro | varchar(20) | N |  |  |  |
| 4 | tentativa | int | N |  |  |  |
| 5 | dt_tentativa | datetime2 | N |  | (sysdatetime()) |  |
| 6 | erro | varchar(500) | S |  |  |  |
| 7 | sucesso | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_notificacao_retry_notif` (NONCLUSTERED): id_notificacao, tipo_registro
- PK `PK_notificacao_retry` (CLUSTERED): id

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

## muralha.parametro_historico

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | dt | datetime2 | N |  | (sysdatetime()) |  |
| 3 | tipo_recurso | varchar(40) | N |  |  |  |
| 4 | id_recurso | varchar(100) | N |  |  |  |
| 5 | parametro | varchar(200) | N |  |  |  |
| 6 | valor_anterior | nvarchar(1000) | S |  |  |  |
| 7 | valor_novo | nvarchar(1000) | S |  |  |  |
| 8 | id_usuario | int | S |  |  |  |
| 9 | login | varchar(50) | S |  |  |  |
| 10 | ip | varchar(45) | S |  |  |  |
| 11 | origem | varchar(60) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_ph_dt` (NONCLUSTERED): dt
- IDX `IX_ph_recurso` (NONCLUSTERED): tipo_recurso, id_recurso, dt
- PK `PK_parametro_historico` (CLUSTERED): id

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

## muralha.relatorio_periodico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | tipo | varchar(40) | N |  |  |  |
| 3 | titulo | varchar(200) | N |  |  |  |
| 4 | periodo_inicio | date | N |  |  |  |
| 5 | periodo_fim | date | N |  |  |  |
| 6 | dados_json | varchar(max) | N |  |  |  |
| 7 | dt_geracao | datetime | N |  | (getdate()) |  |
| 8 | gerado_por | varchar(60) | N |  | ('SISTEMA') |  |

**Índices/Chaves:**
- IDX `IX_relatorio_periodico_tipo_dt` (NONCLUSTERED): tipo, dt_geracao
- PK `PK_relatorio_periodico` (CLUSTERED): id

## muralha.timelapse_cache

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | dt_inicio | datetime2 | N |  |  |  |
| 4 | dt_fim | datetime2 | N |  |  |  |
| 5 | total_frames | int | N |  |  |  |
| 6 | dt_geracao | datetime2 | N |  | (sysdatetime()) |  |
| 7 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_timelapse_cache_dt` (NONCLUSTERED): dt_geracao
- PK `PK_timelapse_cache` (CLUSTERED): id

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

