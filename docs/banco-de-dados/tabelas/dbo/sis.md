# Tabelas — schema `dbo` — grupo `sis`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.sis_documento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_documento | int | N | S |  |  |
| 2 | identificador_externo | char(10) | S |  |  |  |
| 3 | nome_arquivo | varchar(64) | N |  |  |  |
| 4 | id_classificador | int | N |  |  |  |
| 5 | data_criacao | datetime | N |  |  |  |
| 6 | id_usuario_criacao | int | N |  |  |  |
| 7 | data_exclusao | datetime | S |  |  |  |
| 8 | id_usuario_exclusao | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_documento` (CLUSTERED): id_documento

**FKs (saída):**
- id_usuario_criacao → dbo.sis_usuario.id_usuario
- id_usuario_exclusao → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.sis_documento_conteudo.id_documento

## dbo.sis_documento_conteudo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_documento | int | N |  |  |  |
| 2 | md5_documento | char(32) | S |  |  |  |
| 3 | conteudo | varbinary(max) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_documento_conteudo` (CLUSTERED): id_documento

**FKs (saída):**
- id_documento → dbo.sis_documento.id_documento

## dbo.sis_fcm_token

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | S |  |  |  |
| 3 | fcm_token | varchar(256) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_fcm___3213E83F4D19B093` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_grupo

Linhas: ~27

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_grupo | int | N |  |  |  |
| 2 | descricao | char(50) | N |  |  |  |
| 3 | id_grupo_pai | int | S |  |  |  |
| 4 | pagina_inicial | varchar(50) | S |  |  |  |
| 5 | nivel_ligacao | int | S |  |  |  |
| 6 | config_muralha | bit | S |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK_sis_grupo` (NONCLUSTERED): id_grupo

**FKs (saída):**
- id_grupo_pai → dbo.sis_grupo.id_grupo

**Referenciada por:**
- dbo.sis_grupo.id_grupo_pai
- dbo.sis_menu_direitos.id_grupo
- dbo.sis_menu_direitos_mobilidade.id_grupo
- dbo.sis_relatorio_direitos.id_grupo
- dbo.sis_usuario_grupo.id_grupo
- mobilidade.sis_grupos_cidades.id_grupo
- mobilidade.sis_grupos_cidades_test.id_grupo
- mobilidade.sis_grupos_cidades_test2.id_grupo
- mobilidade.sis_grupos_cidades_test3.id_grupo
- mobilidade.sis_grupos_cidades_test4.id_grupo
- mobilidade.sis_grupos_cidades_test5.id_grupo
- mobilidade.sis_grupos_cidades_test6.id_grupo
- muralha.alerta_notificacao.id_grupo
- muralha.cad_veiculo_monitorado_grupo.id_grupo
- muralha.config_grupo_permissao.id_grupo
- muralha.ocorrencia_notificacao.id_grupo
- muralha.ocorrencia_notificacao_historico.id_grupo
- muralha.registro_fato_usuario_grupo.id_grupo

## dbo.sis_localizacao

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | latitude | decimal(20,10) | S |  |  |  |
| 4 | longitude | decimal(20,10) | S |  |  |  |
| 5 | timestamp | varchar(32) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_loca__3213E83F03609FFE` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_log

Linhas: ~1078779

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_log | int | N | S |  |  |
| 2 | descricao | varchar(150) | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | tipo | char(3) | S |  |  |  |
| 5 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_sis_log_data_tipo_log` (NONCLUSTERED): data, tipo, id_log
- PK `PK_sis_log` (NONCLUSTERED): id_log

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.sis_log_detalhe.id_log

## dbo.sis_log_detalhe

Linhas: ~1078779

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_log | int | N |  |  |  |
| 2 | detalhe | varchar(1000) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_log_detalhe` (CLUSTERED): id_log

**FKs (saída):**
- id_log → dbo.sis_log.id_log

## dbo.sis_logon_logoff_usuario

Linhas: ~8903

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_logon | int | N | S |  |  |
| 2 | data_logon | datetime | S |  |  |  |
| 3 | maquina | char(40) | S |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | data_logoff | datetime | S |  |  |  |
| 6 | app | char(5) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_logon_logoff_usuario` (NONCLUSTERED): id_logon

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_menu

Linhas: ~172

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_menu | int | N |  |  |  |
| 2 | descricao | char(100) | N |  |  |  |
| 3 | acao | char(250) | S |  |  |  |
| 4 | tipo | char(3) | N |  |  |  |
| 5 | nivel | smallint | N |  |  |  |
| 6 | id_pai_menu | int | S |  |  |  |
| 7 | menu | char(100) | N |  |  |  |
| 8 | Ativo | int | S |  | ((1)) |  |
| 9 | nome_sistema | varchar(200) | S |  | ('GTW') |  |

**Índices/Chaves:**
- IDX `IX_menu_acao_id` (NONCLUSTERED): acao, id_menu
- PK `PK_sis_menu` (NONCLUSTERED): id_menu

**FKs (saída):**
- id_pai_menu → dbo.sis_menu.id_menu

**Referenciada por:**
- dbo.sis_menu.id_pai_menu
- dbo.sis_menu_direitos.id_menu
- dbo.sis_menu_graficos_infos.id_menu
- dbo.sis_menu_infos.id_menu
- dbo.sis_menu_mobilidade.id_sis_menu
- dbo.sis_menu_relatorio_infos.id_menu
- dbo.sis_menu_url.id_menu

## dbo.sis_menu_direitos

Linhas: ~328

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_menu_direito | int | N | S |  |  |
| 2 | id_grupo | int | S |  |  |  |
| 3 | id_usuario | int | S |  |  |  |
| 4 | id_menu | int | N |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_sis_menu_direitos_menu_usuario_grupo` (NONCLUSTERED): id_menu, id_usuario, id_grupo
- PK `PK_sis_menu_direitos` (NONCLUSTERED): id_menu_direito

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_menu → dbo.sis_menu.id_menu
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_menu_direitos_mobilidade

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_menu_direito | int | N | S |  |  |
| 2 | id_grupo | int | S |  |  |  |
| 3 | id_usuario | int | S |  |  |  |
| 4 | id_menu | int | N |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_sis_menu_direitos_mobilidade_menu_usuario_grupo` (NONCLUSTERED): id_menu, id_usuario, id_grupo
- PK `PK_sis_menu_direitos_mobilidade` (CLUSTERED): id_menu_direito

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_menu → dbo.sis_menu_mobilidade.id
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_menu_graficos_infos

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infos | int | N | S |  |  |
| 2 | id_menu | int | N |  |  |  |
| 3 | src | varchar(100) | S |  |  |  |
| 4 | descricao | varchar(50) | N |  |  |  |
| 5 | href | varchar(100) | N |  |  |  |
| 6 | descricao_detalhada | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_menu_graficos_infos` (CLUSTERED): id_infos

**FKs (saída):**
- id_menu → dbo.sis_menu.id_menu

## dbo.sis_menu_infos

Linhas: ~32

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infos | int | N |  |  |  |
| 2 | id_menu | int | N |  |  |  |
| 3 | src | varchar(100) | N |  |  |  |
| 4 | descricao | varchar(100) | N |  |  |  |
| 5 | href | varchar(100) | N |  |  |  |
| 6 | descricao_detalhada | varchar(100) | N |  |  |  |
| 7 | ordenacao | int | N |  | ((99)) |  |
| 8 | menu_pai | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_menu__168F41B6DB0668FD` (CLUSTERED): id_infos

**FKs (saída):**
- id_menu → dbo.sis_menu.id_menu

## dbo.sis_menu_mobilidade

Linhas: ~27

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | descricao | varchar(255) | N |  |  |  |
| 3 | acao | varchar(500) | N |  |  |  |
| 4 | nivel | int | N |  |  |  |
| 5 | id_menu_pai | int | S |  |  |  |
| 6 | menu | varchar(100) | N |  |  |  |
| 7 | ativo | bit | N |  |  |  |
| 8 | id_sis_menu | int | S |  |  |  |
| 9 | ordem | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_menu_mobilidade` (CLUSTERED): id

**FKs (saída):**
- id_menu_pai → dbo.sis_menu_mobilidade.id
- id_sis_menu → dbo.sis_menu.id_menu

**Referenciada por:**
- dbo.sis_menu_direitos_mobilidade.id_menu
- dbo.sis_menu_mobilidade.id_menu_pai

## dbo.sis_menu_relatorio_infos

Linhas: ~26

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infos | int | N | S |  |  |
| 2 | id_menu | int | N |  |  |  |
| 3 | src | varchar(100) | S |  |  |  |
| 4 | descricao | varchar(150) | N |  |  |  |
| 5 | href | varchar(100) | N |  |  |  |
| 6 | descricao_detalhada | varchar(150) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_menu_relatorio_infos` (CLUSTERED): id_infos

**FKs (saída):**
- id_menu → dbo.sis_menu.id_menu

## dbo.sis_menu_url

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_menu | int | N |  |  |  |
| 2 | id_url | int | N |  |  |  |
| 3 | descricao | char(50) | S |  |  |  |
| 4 | url | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_menu_url` (CLUSTERED): id_menu, id_url

**FKs (saída):**
- id_menu → dbo.sis_menu.id_menu

## dbo.sis_relatorio

Linhas: ~11

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_relatorio | int | N | S |  |  |
| 2 | nome | nvarchar(50) | N |  |  |  |
| 3 | executa_function | nvarchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_relatorio` (CLUSTERED): id_relatorio

**Referenciada por:**
- dbo.sis_relatorio_direitos.id_relatorio

## dbo.sis_relatorio_direitos

Linhas: ~41

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_relatorio_direitos | int | N | S |  |  |
| 2 | id_relatorio | int | N |  |  |  |
| 3 | id_grupo | int | S |  |  |  |
| 4 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_relatorio_direitos` (CLUSTERED): id_relatorio_direitos

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_relatorio → dbo.sis_relatorio.id_relatorio
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_usuario

Linhas: ~37

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | int | N | S |  |  |
| 2 | usuario | char(25) | N |  |  |  |
| 3 | senha | char(40) | N |  |  |  |
| 4 | nome | char(60) | N |  |  |  |
| 5 | email | char(50) | S |  |  |  |
| 6 | ativo | bit | N |  |  |  |
| 7 | alterar_senha | bit | N |  |  |  |
| 8 | id_grupo_equipamento | int | S |  |  |  |
| 9 | cod_agente | int | S |  |  |  |
| 10 | uf_agente | char(2) | S |  |  |  |
| 11 | pagina_inicial | varchar(50) | S |  |  |  |
| 12 | telefone | varchar(11) | S |  |  |  |
| 13 | google_id | varchar(255) | S |  |  |  |
| 14 | federated_provider | varchar(50) | S |  |  |  |
| 15 | google_id_token_temp | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_sis_usuario_nome` (NONCLUSTERED): nome
- IDX `IX_sis_usuario_usuario_agente` (NONCLUSTERED): id_usuario
- IDX `IX_sis_usuario_usuario_nome_cod_agente` (NONCLUSTERED): usuario, nome, cod_agente
- PK `PK_sis_usuario` (NONCLUSTERED): id_usuario
- UNIQUE `UK_sis_usuario_usuario` (NONCLUSTERED): usuario

**FKs (saída):**
- id_grupo_equipamento → dbo.grupo_equipamento.id_grupo_equipamento

**Referenciada por:**
- dbo.agenda_estatico.id_usuario
- dbo.agenda_estatico_item.id_usuario_alter
- dbo.agenda_estatico_validacao.id_usuario
- dbo.agendamento_processamento.id_usuario
- dbo.amostra_imagem_manual.id_usuario
- dbo.cad_inibicao_infracao.id_usuario
- dbo.cad_inibicao_infracao.id_usuario_cancelado
- dbo.configuracao_equipamento.id_usuario
- dbo.descarga.id_usuario
- dbo.descarga.id_usuario_confirmacao
- dbo.filtro.id_usuario
- dbo.imagem_ar.id_usuario
- dbo.infracao.id_usuario_atual
- dbo.infracao.id_usuario_final
- dbo.infracao_janela.id_usuario
- dbo.infracao_processo.id_usuario
- dbo.painel_contrato_alerta.id_usuario_atualizacao
- dbo.placa_irregular.id_usuario
- dbo.processo_medicao.id_usuario_responsavel
- dbo.ptz_configuracao_operacao.id_usuario
- dbo.sis_documento.id_usuario_criacao
- dbo.sis_documento.id_usuario_exclusao
- dbo.sis_fcm_token.id_usuario
- dbo.sis_localizacao.id_usuario
- dbo.sis_log.id_usuario
- dbo.sis_logon_logoff_usuario.id_usuario
- dbo.sis_menu_direitos.id_usuario
- dbo.sis_menu_direitos_mobilidade.id_usuario
- dbo.sis_relatorio_direitos.id_usuario
- dbo.sis_usuario_grupo.id_usuario
- dbo.sis_usuario_recupera_senha.id_usuario
- dbo.sis_usuario_status.id_usuario
- dbo.solicitacao_auditoria.id_usuario
- dbo.tempo_processamento.id_usuario
- dbo.veiculo_visualizados.id_usuario
- muralha.agente_localizacao_atual.id_usuario
- muralha.agente_localizacao_hist.id_usuario
- muralha.alerta.id_usuario
- muralha.alerta_notificacao.id_usuario
- muralha.anotacao_contributiva.id_usuario
- muralha.atendimento.id_usuario_criacao
- muralha.atendimento_guarnicao.id_responsavel_app
- muralha.atendimento_historico.id_usuario
- muralha.blitz_abordagem.id_agente
- muralha.blitz_usuario.id_usuario
- muralha.boletim.id_usuario
- muralha.cad_veiculo_monitorado.id_usuario_responsavel
- muralha.cad_veiculo_monitorado.id_usuario
- muralha.cad_veiculo_monitorado.id_usuario_atualizacao
- muralha.cad_veiculo_monitorado.id_usuario_exclusao
- muralha.cad_veiculo_monitorado.id_usuario_inativacao
- muralha.cad_veiculo_monitorado_equipamento.id_usuario
- muralha.cad_veiculo_monitorado_grupo.id_usuario
- muralha.cad_veiculo_monitorado_historico.id_usuario_responsavel
- muralha.cad_veiculo_monitorado_periodo.id_usuario
- muralha.config_alerta.id_usuario
- muralha.config_alerta.id_usuario_exclusao
- muralha.config_alerta_transp_clandestino.id_usuario_alteracao
- muralha.config_alerta_transp_clandestino.id_usuario
- muralha.config_envio_tempo_real.id_usuario
- muralha.config_envio_tempo_real.id_usuario_exclusao
- muralha.config_grupo_permissao.id_usuario
- muralha.config_mobile_silencio.id_usuario
- muralha.config_monitoramento_ao_vivo.id_usuario
- muralha.config_semelhanca_placa.id_usuario
- muralha.config_semelhanca_placa.id_usuario_exclusao
- muralha.fato.id_usuario
- muralha.guarnicao.id_usuario_responsavel
- muralha.guarnicao.id_usuario_criacao
- muralha.guarnicao.id_usuario_alt
- muralha.guarnicao_diario.id_usuario
- muralha.guarnicao_integrante.id_usuario
- muralha.ocorrencia.id_usuario
- muralha.ocorrencia.id_usuario_modificacao
- muralha.ocorrencia_notificacao.id_usuario
- muralha.ocorrencia_notificacao_historico.id_usuario
- muralha.registro_fato.id_usuario
- muralha.registro_fato_passagem_veic.id_usuario
- muralha.registro_fato_usuario_grupo.id_usuario
- muralha.token_acesso_mobilidade_urbana.id_usuario
- muralha.veiculo_tempo_real_correcao.id_usuario

## dbo.sis_usuario_eventos_CAV

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | bigint | S |  |  |  |
| 2 | usuario | varchar(25) | S |  |  |  |

## dbo.sis_usuario_grupo

Linhas: ~192

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | int | N |  |  |  |
| 2 | id_grupo | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_usuario_grupo` (NONCLUSTERED): id_usuario, id_grupo

**FKs (saída):**
- id_grupo → dbo.sis_grupo.id_grupo
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_usuario_imp

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | int | N | S |  |  |
| 2 | usuario | char(25) | N |  |  |  |
| 3 | senha | char(40) | N |  |  |  |
| 4 | nome | char(60) | N |  |  |  |
| 5 | email | char(50) | S |  |  |  |
| 6 | ativo | bit | N |  |  |  |
| 7 | alterar_senha | bit | N |  |  |  |
| 8 | id_grupo_equipamento | int | S |  |  |  |
| 9 | cod_agente | int | S |  |  |  |
| 10 | uf_agente | char(2) | S |  |  |  |

## dbo.sis_usuario_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_usuario | int | N |  |  |  |
| 2 | usuario | char(25) | N |  |  |  |
| 3 | nome | char(60) | N |  |  |  |
| 4 | email | char(50) | S |  |  |  |
| 5 | id_usuario_local | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_sis_usuario_importacao` (NONCLUSTERED): id_usuario

## dbo.sis_usuario_recupera_senha

Linhas: ~72

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | token | uniqueidentifier | N |  |  |  |
| 4 | ativo | int | N |  | ((1)) |  |
| 5 | data_criacao | datetime | N |  |  |  |
| 6 | data_utilizacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_usua__3213E83F8BA68BA0` (CLUSTERED): id

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_usuario_status

Linhas: ~3

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_status | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | status_login | char(1) | N |  |  |  |
| 4 | data_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_sis_usuario_status` (CLUSTERED): id_status
- UNIQUE `UQ_sis_usuario_status_usuario` (NONCLUSTERED): id_usuario

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.sis_usuario_token

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_token | bigint | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | token | uniqueidentifier | N |  | (newid()) |  |
| 4 | data_entrada | datetime | S |  | (getdate()) |  |
| 5 | data_saida | datetime | S |  |  |  |
| 6 | data_verificado | datetime | N |  | (getdate()) |  |
| 7 | endereco_ip | varchar(17) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sis_usua__3C2FA9C460F8D31F` (CLUSTERED): id_token

