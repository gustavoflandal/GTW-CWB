# Tabelas — schema `dbo` — grupo `configuracao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.configuracao_alerta_cad_arquivos_importados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | arquivo | varchar(100) | N |  |  |  |
| 2 | tipoData | int | S |  |  |  |
| 3 | verificado | bit | S |  |  |  |
| 4 | max_dias_sem_arqui | int | S |  |  |  |

## dbo.configuracao_equipamento

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N | S |  |  |
| 2 | serie_equipamento | int | S |  |  |  |
| 3 | ativo | bit | N |  |  |  |
| 4 | obs | varchar(100) | S |  |  |  |
| 5 | watch_dog | tinyint | S |  |  |  |
| 6 | controladora | tinyint | S |  |  |  |
| 7 | iluminador | tinyint | S |  |  |  |
| 8 | data_inicio | datetime | S |  |  |  |
| 9 | data_fim | datetime | S |  |  |  |
| 10 | id_produto | int | S |  |  |  |
| 11 | chave_publica | varchar(255) | S |  |  |  |
| 12 | com_controladora | tinyint | N |  |  |  |
| 13 | com_auxiliar | tinyint | N |  |  |  |
| 14 | com_iluminador | tinyint | S |  |  |  |
| 15 | em_operacao | bit | N |  | ((0)) |  |
| 16 | id_grupo_equipamento | int | N |  |  |  |
| 17 | controle_online | bit | N |  | ((1)) |  |
| 18 | flag_opcao | bigint | S |  |  |  |
| 19 | id_usuario | int | N |  | ((12)) |  |
| 20 | data_modificacao | datetime | N |  |  |  |
| 21 | categoria | int | S |  |  |  |
| 22 | distancia_equipamento | int | N |  | ((0)) |  |
| 23 | tempo_ciclagem | int | N |  | ((0)) |  |
| 24 | ativar_montante | bit | N |  | ((0)) |  |
| 25 | ativar_jusante | bit | N |  | ((0)) |  |
| 26 | porta_montante | int | N |  | ((0)) |  |
| 27 | codigo_montante | int | N |  | ((0)) |  |
| 28 | cod_GIT_Contrato | int | S |  |  |  |
| 29 | cod_GIT_Ponto | int | S |  |  |  |
| 30 | TempoTotalVideo | tinyint | S |  |  |  |
| 31 | TempoVideoAntesInfracao | tinyint | S |  |  |  |
| 32 | tempo_adicional_faixa_exclusiva | smallint | S |  |  |  |
| 33 | tempo_fluxo_zero | smallint | S |  |  |  |
| 34 | diferenca_percentual_bloqueio_faixa | int | S |  |  |  |
| 35 | CodigoEquipCliente | varchar(11) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_configuracao_equipamento_ativo` (NONCLUSTERED): ativo
- IDX `IX_configuracao_equipamento_configuracao_equipamento_serie_equipamento_usuario` (NONCLUSTERED): id_configuracao_equipamento, serie_equipamento, id_usuario
- IDX `IX_configuracao_equipamento_data_modificacao` (NONCLUSTERED): data_modificacao
- IDX `IX_configuracao_equipamento_serie_equipamento` (NONCLUSTERED): serie_equipamento
- PK `PK_configuracao_equipamento` (NONCLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_grupo_equipamento → dbo.grupo_equipamento.id_grupo_equipamento
- id_produto → dbo.produto.id_produto
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.configuracao_equipamento_afericao.id_configuracao_equipamento
- dbo.configuracao_equipamento_agd.id_configuracao_equipamento
- dbo.configuracao_equipamento_agenda_camera.id_configuracao_equipamento
- dbo.configuracao_equipamento_camera.id_configuracao_equipamento
- dbo.configuracao_equipamento_div.id_configuracao_equipamento
- dbo.configuracao_equipamento_horario.id_configuracao_equipamento
- dbo.configuracao_equipamento_nivel_video.id_configuracao_equipamento
- dbo.configuracao_equipamento_painel.id_configuracao_equipamento
- dbo.configuracao_equipamento_painel_geral.id_configuracao_equipamento
- dbo.configuracao_equipamento_parametros_adicionais.id_configuracao_equipamento
- dbo.configuracao_equipamento_pista.id_configuracao_equipamento
- dbo.configuracao_equipamento_regra_infracao.id_configuracao_equipamento
- dbo.configuracao_equipamento_relevante.id_configuracao_equipamento
- dbo.configuracao_equipamento_rodizio.id_configuracao_equipamento
- dbo.configuracao_equipamento_rodovia.id_configuracao_equipamento
- dbo.configuracao_equipamento_servidor.id_configuracao_equipamento
- dbo.local.id_configuracao_equipamento

## dbo.configuracao_equipamento_afericao

Linhas: ~1477

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_afericao | int | N |  |  |  |
| 3 | id_pista | int | S |  |  |  |
| 4 | referencia | char(50) | S |  |  |  |
| 5 | selagem | char(10) | S |  |  |  |
| 6 | laudo | int | S |  |  |  |
| 7 | data | datetime | N |  |  |  |
| 8 | data_validade | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_configuracao_equipamento_afericao_afericao_data` (NONCLUSTERED): id_afericao
- PK `PK_configuracao_equipamento_afericao` (CLUSTERED): id_configuracao_equipamento, id_afericao

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_agd

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | road_side | char(15) | S |  |  |  |
| 3 | vertical_angle | char(5) | S |  |  |  |
| 4 | horizontal_angle | char(5) | S |  |  |  |
| 5 | port_name | char(20) | S |  |  |  |
| 6 | baud_rate | int | S |  |  |  |
| 7 | parity | char(5) | S |  |  |  |
| 8 | data_bits | tinyint | S |  |  |  |
| 9 | stop_bits | char(3) | S |  |  |  |
| 10 | high_range_threshold | char(5) | S |  |  |  |
| 11 | high_speed_threshold | char(5) | S |  |  |  |
| 12 | low_range_threshold | char(5) | S |  |  |  |
| 13 | low_speed_threshold | char(5) | S |  |  |  |
| 14 | power_threshold | char(10) | S |  |  |  |
| 15 | channel | int | S |  |  |  |
| 16 | sense | int | S |  |  |  |
| 17 | tracking_mode | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_agd` (CLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_agd_pista

Linhas: ~1007

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | capture_distance | char(5) | S |  |  |  |
| 4 | starting_border | char(5) | S |  |  |  |
| 5 | ending_border | char(5) | S |  |  |  |
| 6 | direction | char(12) | S |  |  |  |
| 7 | min_samples_for_projection | tinyint | S |  |  |  |
| 8 | speed_samples | tinyint | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_agd_pista` (CLUSTERED): id_configuracao_equipamento, id_pista

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento_pista.id_configuracao_equipamento
- id_pista → dbo.configuracao_equipamento_pista.id_pista

## dbo.configuracao_equipamento_agenda_camera

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | nome_agenda | char(20) | N |  |  |  |
| 3 | agenda_camera | text | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_agenda_camera` (CLUSTERED): id_configuracao_equipamento, nome_agenda

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_camera

Linhas: ~1728

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_camera | tinyint | N |  |  |  |
| 3 | tipo | tinyint | N |  |  |  |
| 4 | endereco | char(20) | S |  |  |  |
| 5 | relevante | bit | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_camera` (CLUSTERED): id_configuracao_equipamento, id_camera

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

**Referenciada por:**
- dbo.configuracao_equipamento_pista.id_configuracao_equipamento
- dbo.configuracao_equipamento_pista.id_camera_frontal
- dbo.configuracao_equipamento_pista.id_camera_pan_1
- dbo.configuracao_equipamento_pista.id_camera_pan_2
- dbo.configuracao_equipamento_pista.id_camera_traseira

## dbo.configuracao_equipamento_captura_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | id_captura_imagem | int | N |  |  |  |
| 4 | camera | tinyint | S |  |  |  |
| 5 | tipo_camera | tinyint | S |  |  |  |
| 6 | placa_captura | tinyint | S |  |  |  |
| 7 | com_controle | tinyint | S |  |  |  |
| 8 | host | char(20) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_captura_imagem` (NONCLUSTERED): id_configuracao_equipamento, id_pista, id_captura_imagem

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento_pista.id_configuracao_equipamento
- id_pista → dbo.configuracao_equipamento_pista.id_pista

## dbo.configuracao_equipamento_captura_veiculo

Linhas: ~1007

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | distancia_laco | decimal(6,3) | S |  |  |  |
| 4 | largura_laco | decimal(6,3) | S |  |  |  |
| 5 | com_perfil_magnetico | tinyint | S |  |  |  |
| 6 | com_placa_laco | tinyint | S |  |  |  |
| 7 | num_lacos | tinyint | S |  |  |  |
| 8 | trigger_infra_vermelho | tinyint | S |  |  |  |
| 9 | num_canal | tinyint | S |  |  |  |
| 10 | num_imagens_pos_laco | int | S |  |  |  |
| 11 | interv_imagens_pos_laco | decimal(6,3) | S |  |  |  |
| 12 | distancia_panoramica_pos_laco | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_captura_veiculo` (NONCLUSTERED): id_configuracao_equipamento, id_pista

**FKs (saída):**
- id_pista → dbo.configuracao_equipamento_pista.id_pista
- id_configuracao_equipamento → dbo.configuracao_equipamento_pista.id_configuracao_equipamento

## dbo.configuracao_equipamento_controlador

Linhas: ~530

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id | int | N |  |  |  |
| 3 | porta | varchar(10) | S |  |  |  |
| 4 | bitsPorSegundo | int | S |  |  |  |
| 5 | bitsDados | int | S |  |  |  |
| 6 | bitsParada | int | S |  |  |  |
| 7 | paridade | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__46B49FE51C829EAA` (CLUSTERED): id_configuracao_equipamento, id

## dbo.configuracao_equipamento_controlador_canais

Linhas: ~2120

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id | int | N |  |  |  |
| 3 | item | int | N |  |  |  |
| 4 | modoHabilitar | int | S |  |  |  |
| 5 | modoSensibilidade | int | S |  |  |  |
| 6 | sensibilidadeEntrada | int | S |  |  |  |
| 7 | sensibilidadeSaida | int | S |  |  |  |
| 8 | configOscilador | int | S |  |  |  |
| 9 | eventosMonitorados | int | S |  |  |  |
| 10 | divisorPerfil | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__6C2D276730398B35` (CLUSTERED): id_configuracao_equipamento, id, item

## dbo.configuracao_equipamento_controlador_canaisv2

Linhas: ~19080

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_controlador | int | N |  |  |  |
| 3 | id | int | N |  |  |  |
| 4 | canal | int | N |  |  |  |
| 5 | operacao | int | N |  |  |  |
| 6 | registrador | int | N |  |  |  |
| 7 | valor | bigint | N |  |  |  |

## dbo.configuracao_equipamento_controlador_pesagem

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id | int | N |  |  |  |
| 3 | porta_S1 | int | S |  |  |  |
| 4 | bitsPorSegundo_S1 | int | S |  |  |  |
| 5 | bitsDados_S1 | int | S |  |  |  |
| 6 | bitsParada_S1 | int | S |  |  |  |
| 7 | paridade_S1 | varchar(10) | S |  |  |  |
| 8 | tempoReconexao_S1 | int | S |  |  |  |
| 9 | tamanhoBuffer_S1 | int | S |  |  |  |
| 10 | porta_S2 | int | S |  |  |  |
| 11 | bitsPorSegundo_S2 | int | S |  |  |  |
| 12 | bitsDados_S2 | int | S |  |  |  |
| 13 | bitsParada_S2 | int | S |  |  |  |
| 14 | paridade_S2 | varchar(10) | S |  |  |  |
| 15 | tempoReconexao_S2 | int | S |  |  |  |
| 16 | tamanhoBuffer_S2 | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__46B49FE5A54DB041` (CLUSTERED): id_configuracao_equipamento, id

## dbo.configuracao_equipamento_controlador_pesagem_canais

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id | int | N |  |  |  |
| 3 | item | int | N |  |  |  |
| 4 | canalFisico | tinyint | S |  |  |  |
| 5 | offSet | smallint | S |  |  |  |
| 6 | inverterPolaridade | bit | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__6C2D2767404DB822` (CLUSTERED): id_configuracao_equipamento, id, item

## dbo.configuracao_equipamento_data_modificacao

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  |  |  |
| 3 | data_modificacao | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `config_equip_data_modificacao_local_data` (NONCLUSTERED): id_local, data_modificacao

## dbo.configuracao_equipamento_dimensoes_ml

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | habilitarMedicaoML | bit | N |  |  |  |
| 3 | timerHabilitar | bit | N |  |  |  |
| 4 | timerIntervalo | int | N |  |  |  |
| 5 | pontosVirtuais | text | S |  |  |  |

## dbo.configuracao_equipamento_div

Linhas: ~75

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_div | int | N |  |  |  |
| 3 | endereco | int | N |  |  |  |
| 4 | porta_com | int | S |  |  |  |
| 5 | versao | int | S |  |  |  |
| 6 | numero_digitos | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_div` (CLUSTERED): id_configuracao_equipamento, id_div

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_geral_divs

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | verdeVermelhoTolerancia | bit | S |  |  |  |
| 3 | tipoTolerancia | int | S |  |  |  |
| 4 | mostrarVelocidade | bit | S |  |  |  |
| 5 | velocidadeSeparador | int | S |  |  |  |
| 6 | toleranciaFixa | int | S |  |  |  |
| 7 | toleranciaPercentual | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__A595A166125FFF86` (CLUSTERED): id_configuracao_equipamento

## dbo.configuracao_equipamento_horario

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_horario | tinyint | N |  |  |  |
| 3 | id_pista | tinyint | S |  |  |  |
| 4 | horario_inicio | datetime | N |  |  |  |
| 5 | horario_fim | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_horario` (CLUSTERED): id_configuracao_equipamento, id_horario

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N |  |  |  |
| 2 | data_configuracao | datetime | N |  |  |  |
| 3 | xml_configuracao | text | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_configuracao_equipamento_importacao_data_config` (NONCLUSTERED): data_configuracao
- PK `PK_configuracao_equipamento_importacao` (CLUSTERED): id_arquivo

**FKs (saída):**
- id_arquivo → dbo.arquivos_importados.id_arquivo

## dbo.configuracao_equipamento_laco_virtual_ml

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | habilitarLV_ML | bit | N |  |  |  |
| 3 | pontosVirtuais | text | S |  |  |  |

**Índices/Chaves:**
- PK `PK__configur__A595A1662A9F5055` (CLUSTERED): id_configuracao_equipamento

## dbo.configuracao_equipamento_medicao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento_medicao | int | N | S |  |  |
| 2 | id_local | int | S |  |  |  |
| 3 | cod_pista | int | S |  |  |  |
| 4 | descricao | char(100) | S |  |  |  |
| 5 | data_inicio | datetime | S |  |  |  |
| 6 | id_produto | int | S |  |  |  |
| 7 | cod_pista_alternativo | int | S |  |  |  |
| 8 | cod_pista_prodam | int | S |  |  |  |
| 9 | serie_equipamento | int | S |  |  |  |
| 10 | qtde_equipamentos | int | S |  | ((1)) |  |
| 11 | id_local_principal | int | S |  |  |  |
| 12 | cod_area | int | S |  | ((0)) |  |
| 13 | cod_pista_tarja | int | S |  |  |  |
| 14 | entre_faixa | int | S |  |  |  |
| 15 | id_pista | tinyint | S |  |  |  |
| 16 | atualizar | bit | S |  | ((1)) |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_medicao` (NONCLUSTERED): id_configuracao_equipamento_medicao

## dbo.configuracao_equipamento_nivel_video

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_nivel_video | int | N |  |  |  |
| 3 | id_pista | tinyint | S |  |  |  |
| 4 | camera | int | S |  |  |  |
| 5 | horario_inicio | datetime | N |  |  |  |
| 6 | horario_fim | datetime | N |  |  |  |
| 7 | valor | decimal(6,2) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_nivel_video` (CLUSTERED): id_configuracao_equipamento, id_nivel_video

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_painel

Linhas: ~471

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_painel | int | N |  |  |  |
| 3 | endereco | int | N |  |  |  |
| 4 | porta_com | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_painel` (CLUSTERED): id_configuracao_equipamento, id_painel

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_painel_geral

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | usar_ldr | bit | N |  |  |  |
| 3 | id_painel_watchdog | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_painel_geral` (CLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_parametros_adicionais

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | parametros_adicionais | text | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_parametros_adicionais` (CLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_pesagem

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | pesagemHabilitada | bit | N |  |  |  |
| 3 | configConvAd | text | S |  |  |  |
| 4 | configPesagemParam | text | S |  |  |  |
| 5 | dataUltimaCalibracao | datetime | N |  |  |  |

## dbo.configuracao_equipamento_pista

Linhas: ~1007

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | cod_pista | int | S |  |  |  |
| 4 | nome_pista | char(100) | S |  |  |  |
| 5 | sentido | char(50) | S |  |  |  |
| 6 | conector | tinyint | N |  |  |  |
| 7 | id_div | int | S |  |  |  |
| 8 | id_div_display | int | S |  |  |  |
| 9 | trigger_infravermelho | int | S |  |  |  |
| 10 | id_painel_infravermelho | int | S |  |  |  |
| 11 | id_painel_ldr | int | S |  |  |  |
| 12 | pin_ldr | int | S |  |  |  |
| 13 | cod_pista_alternativo | int | S |  |  |  |
| 14 | cod_pista_prodam | int | S |  |  |  |
| 15 | semaforo_painel_id | int | S |  |  |  |
| 16 | pista_1_transversal | bit | S |  |  |  |
| 17 | pista_2_transversal | bit | S |  |  |  |
| 18 | pista_3_transversal | bit | S |  |  |  |
| 19 | pista_4_transversal | bit | S |  |  |  |
| 20 | captura_obj_laco | int | N |  | ((1)) |  |
| 21 | captura_obj_tras | bit | N |  | ((0)) |  |
| 22 | captura_obj_frente | bit | N |  | ((0)) |  |
| 23 | pista_8_transversal | bit | S |  |  |  |
| 24 | pista_7_transversal | bit | S |  |  |  |
| 25 | pista_6_transversal | bit | S |  |  |  |
| 26 | pista_5_transversal | bit | S |  |  |  |
| 27 | cod_area | int | S |  |  |  |
| 28 | tipo_disparo | bit | S |  |  |  |
| 29 | ctrl_nivel_iluminador | bit | S |  |  |  |
| 30 | nivel_inicial | int | S |  |  |  |
| 31 | nivel_final | int | S |  |  |  |
| 32 | lista_niveis | varchar(100) | S |  |  |  |
| 33 | camera_iluminador | int | S |  |  |  |
| 34 | cod_pista_tarja | int | N |  | ((0)) |  |
| 35 | cod_local_prodam_auxiliar | int | N |  | ((0)) |  |
| 36 | paradaFaixaL1 | bit | S |  | ((1)) |  |
| 37 | paradaFaixaL2 | bit | S |  | ((1)) |  |
| 38 | faixa_exclusiva_direita | bit | S |  |  |  |
| 39 | faixa_exclusiva_esquerda | bit | S |  |  |  |
| 40 | entre_faixa | int | S |  |  |  |
| 41 | cod_GIT_Logradouro | int | S |  |  |  |
| 42 | cod_GIT_Sentido | int | S |  |  |  |
| 43 | cod_GIT_Faixa | int | S |  |  |  |
| 44 | TempoMinimoAmarelo | tinyint | S |  |  |  |
| 45 | TempoMaximoAmarelo | tinyint | S |  |  |  |
| 46 | TempoMaximoVermelho | smallint | S |  |  |  |
| 47 | cod_GIT_Pista | int | S |  |  |  |
| 48 | id_camera_frontal | tinyint | S |  |  |  |
| 49 | id_camera_traseira | tinyint | S |  |  |  |
| 50 | id_camera_pan_1 | tinyint | S |  |  |  |
| 51 | id_camera_pan_2 | tinyint | S |  |  |  |
| 52 | pista_relevante | bit | S |  |  |  |
| 53 | captura_reversa | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_configuracao_equipamento_pista_configuracao_equipamento_pista` (NONCLUSTERED): id_configuracao_equipamento, id_pista
- IDX `IX_configuracao_equipamento_pista_faixa_exclusiva_direita_esquerda` (NONCLUSTERED): faixa_exclusiva_direita, faixa_exclusiva_esquerda
- IDX `IX_configuracao_equipamento_pista_pista_cod_pista_cod_pista_alternativo` (NONCLUSTERED): id_pista, cod_pista, cod_pista_alternativo
- IDX `IX_configuracao_equipamento_pista_prodam` (NONCLUSTERED): cod_pista_prodam
- PK `PK_configuracao_equipamento_pista` (CLUSTERED): id_configuracao_equipamento, id_pista

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento_camera.id_configuracao_equipamento
- id_camera_frontal → dbo.configuracao_equipamento_camera.id_camera
- id_camera_pan_1 → dbo.configuracao_equipamento_camera.id_camera
- id_configuracao_equipamento → dbo.configuracao_equipamento_camera.id_configuracao_equipamento
- id_configuracao_equipamento → dbo.configuracao_equipamento_camera.id_configuracao_equipamento
- id_camera_pan_2 → dbo.configuracao_equipamento_camera.id_camera
- id_camera_traseira → dbo.configuracao_equipamento_camera.id_camera
- id_configuracao_equipamento → dbo.configuracao_equipamento_camera.id_configuracao_equipamento
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

**Referenciada por:**
- dbo.configuracao_equipamento_agd_pista.id_configuracao_equipamento
- dbo.configuracao_equipamento_agd_pista.id_pista
- dbo.configuracao_equipamento_captura_imagem.id_configuracao_equipamento
- dbo.configuracao_equipamento_captura_imagem.id_pista
- dbo.configuracao_equipamento_captura_veiculo.id_pista
- dbo.configuracao_equipamento_captura_veiculo.id_configuracao_equipamento

## dbo.configuracao_equipamento_pista_pesagem

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | distSegundoLacoSensor | float | N |  |  |  |

## dbo.configuracao_equipamento_pmv

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pmv | tinyint | N |  |  |  |
| 3 | descricao_pmv | varchar(100) | S |  |  |  |
| 4 | ativo | bit | N |  |  |  |
| 5 | endereco_ip | varchar(20) | N |  |  |  |
| 6 | porta | varchar(10) | N |  |  |  |

## dbo.configuracao_equipamento_pmv_circunstancias

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pmv | tinyint | N |  |  |  |
| 3 | id_circunstancia | tinyint | N |  |  |  |
| 4 | indice | int | N |  |  |  |
| 5 | avancado | varchar(100) | N |  |  |  |

## dbo.configuracao_equipamento_regra_infracao

Linhas: ~1261

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_regra_infracao | int | N |  |  |  |
| 3 | id_pista | tinyint | S |  |  |  |
| 4 | hora_ini | datetime | N |  |  |  |
| 5 | hora_fim | datetime | N |  |  |  |
| 6 | dia_ini | tinyint | S |  |  |  |
| 7 | dia_fim | tinyint | S |  |  |  |
| 8 | velocidade_limite | int | S |  |  |  |
| 9 | tolerancia | int | S |  |  |  |
| 10 | tolerancia_portaria | int | S |  |  |  |
| 11 | comprimento_ini | decimal(6,2) | S |  |  |  |
| 12 | comprimento_fim | decimal(6,2) | S |  |  |  |
| 13 | tipo | char(2) | N |  |  |  |
| 14 | ativo | bit | N |  |  |  |
| 15 | id_classe | char(1) | S |  |  |  |
| 16 | tolerancia_vermelho | decimal(6,2) | S |  |  |  |
| 17 | tolerancia_faixa | decimal(6,2) | S |  |  |  |
| 18 | usar_panoramica | bit | S |  |  |  |
| 19 | opcao_panoramica | int | S |  |  |  |
| 20 | fiscalizar_fase | int | S |  |  |  |
| 21 | num_imagens_pos_laco | int | S |  |  |  |
| 22 | intervalo_imagens_pos_laco | decimal(6,2) | S |  |  |  |
| 23 | tolerancia_transversal | int | S |  |  |  |
| 24 | intervalo | int | N |  | ((0)) |  |
| 25 | final_placa | int | S |  |  |  |
| 26 | remover_isencao_taxi | bit | N |  | ((0)) |  |
| 27 | ini_regra_taxi | float | N |  | ((0)) |  |
| 28 | fim_regra_taxi | float | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_configuracao_equipamento_regra_infracao_ativo` (NONCLUSTERED): ativo
- IDX `IX_configuracao_equipamento_regra_infracao_ativo2` (NONCLUSTERED): id_configuracao_equipamento, ativo
- PK `PK_configuracao_equipamento_regra_infracao` (NONCLUSTERED): id_regra_infracao, id_configuracao_equipamento

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_relevante

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | time_zone | char(30) | S |  |  |  |
| 3 | numero_imagens | tinyint | S |  |  |  |
| 4 | tempo_autonomia_nobreak | int | S |  |  |  |
| 5 | endereco_sistema_relevante | varchar(20) | S |  |  |  |
| 6 | delta_minimo_para_filtro | int | S |  |  |  |
| 7 | tempo_min_aciona_laco | int | S |  |  |  |
| 8 | diferenca_perc_delta_max | int | S |  |  |  |
| 9 | socket_controlador_1 | int | S |  |  |  |
| 10 | socket_controlador_2 | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_relevante` (CLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_resolucao_imagem

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | larguraImagemInfracao | int | N |  |  |  |
| 3 | alturaImagemInfracao | int | N |  |  |  |
| 4 | larguraImagemOcr | int | N |  |  |  |
| 5 | alturaImagemOcr | int | N |  |  |  |

## dbo.configuracao_equipamento_rodizio

Linhas: ~140

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_rodizio | int | N |  |  |  |
| 3 | horario_inicio | datetime | N |  |  |  |
| 4 | horario_fim | datetime | N |  |  |  |
| 5 | dia_semana | tinyint | N |  |  |  |
| 6 | final_Placa | tinyint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_rodizio` (CLUSTERED): id_configuracao_equipamento, id_rodizio

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_rodovia

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | sigla | char(2) | N |  |  |  |
| 3 | numero | int | N |  |  |  |
| 4 | acesso | char(10) | S |  |  |  |
| 5 | km | int | S |  |  |  |
| 6 | metros | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_rodovia` (CLUSTERED): id_configuracao_equipamento

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_sensor_piezo

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | ord | tinyint | N |  |  |  |
| 4 | id | int | N |  |  |  |
| 5 | factorCal | float | N |  |  |  |

## dbo.configuracao_equipamento_servidor

Linhas: ~1410

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento | int | N |  |  |  |
| 2 | id_servidor | int | N |  |  |  |
| 3 | nome | char(20) | S |  |  |  |
| 4 | host | char(100) | N |  |  |  |
| 5 | port | int | S |  |  |  |
| 6 | tipo_conexao | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_equipamento_servidor` (NONCLUSTERED): id_configuracao_equipamento, id_servidor

**FKs (saída):**
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

## dbo.configuracao_equipamento_velocidade_limite

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | id_pista | int | N |  |  |  |
| 3 | velocidade_limite | int | S |  |  |  |

## dbo.configuracao_local_controle

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | serie_equipamento | int | N |  |  |  |
| 2 | chave | nvarchar(50) | N |  |  |  |
| 3 | valor | nvarchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_configuracao_local_controle` (CLUSTERED): serie_equipamento, chave

## dbo.configuracao_relatorios_medicao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo | int | N |  |  |  |
| 2 | habilitado | bit | N |  |  |  |
| 3 | diretorio_saida | varchar(100) | N |  |  |  |
| 4 | hora | time | N |  |  |  |
| 5 | email | varchar(200) | S |  |  |  |
| 6 | dia_semana | varchar(15) | S |  |  |  |

## dbo.configuracao_relatorios_medicao_desc

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo | int | N |  |  |  |
| 2 | descricao | varchar(50) | N |  |  |  |

## dbo.configuracao_relatorios_rj

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao | int | N | S |  |  |
| 2 | nome_relatorio | varchar(50) | N |  |  |  |
| 3 | query_executar | text | N |  |  |  |
| 4 | data_atualizacao | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__configur__E61E1249393C620E` (CLUSTERED): id_configuracao

## dbo.configuracao_semelhanca_placa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | caracter | char(1) | S |  |  |  |
| 2 | valor | char(1) | S |  |  |  |

