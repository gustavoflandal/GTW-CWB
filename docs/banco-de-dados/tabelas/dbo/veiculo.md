# Tabelas — schema `dbo` — grupo `veiculo`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.veiculo

Linhas: ~74459

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N | S |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | velocidade | decimal(6,1) | N |  |  |  |
| 6 | comprimento | decimal(6,1) | N |  |  |  |
| 7 | pista | tinyint | N |  |  |  |
| 8 | flag | int | N |  |  |  |
| 9 | segundos | decimal(8,3) | S |  |  |  |
| 10 | id_veiculo_unic | bigint | S |  |  |  |
| 11 | id_classe | char(1) | S |  |  |  |
| 12 | id_local | int | S |  |  |  |
| 13 | sequencia_local | tinyint | S |  | ((1)) |  |
| 14 | ocupacao | int | S |  |  |  |
| 15 | id_arquivo | bigint | S |  |  |  |
| 16 | velocidade_media | int | S |  |  |  |
| 17 | serie_equipamento_Montante | int | S |  |  |  |
| 18 | id_veiculo_Local_Montante | int | S |  |  |  |
| 19 | porteVeiculo | varchar(6) | S |  |  |  |
| 20 | cadastro | varchar(3) | S |  |  |  |
| 21 | serie_equipamento | int | S |  |  |  |
| 22 | codigo_prodam | int | S |  |  |  |
| 23 | entre_faixa | int | S |  |  |  |
| 24 | classificacao_veiculo | varchar(10) | S |  |  |  |
| 25 | velocidade_aux | decimal(6,2) | S |  |  |  |
| 26 | seq_deteccao_doppler | int | S |  |  |  |
| 27 | validacao_velocidade_target | tinyint | S |  |  |  |
| 28 | numero_eixos | int | S |  |  |  |
| 29 | rodagem_dupla | bit | S |  |  |  |
| 30 | categoria | int | S |  |  |  |
| 31 | placa_mercosul | bit | N |  | ((0)) |  |
| 32 | com_pesagem | bit | N |  | ((0)) |  |
| 33 | classificacao | varchar(100) | S |  |  |  |
| 34 | id_captura | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_classe` (NONCLUSTERED): id_classe
- IDX `IX_veiculo_com_pesagem` (NONCLUSTERED): com_pesagem
- IDX `IX_veiculo_data_local` (NONCLUSTERED): data, id_local
- IDX `IX_veiculo_data_local_flag_pista` (NONCLUSTERED): data, id_local, flag, pista
- IDX `IX_veiculo_data_V2` (NONCLUSTERED): data
- IDX `IX_veiculo_entre_faixa` (NONCLUSTERED): entre_faixa
- IDX `IX_veiculo_id_captura` (NONCLUSTERED): id_captura
- IDX `IX_veiculo_local_data` (NONCLUSTERED): id_local, data
- IDX `IX_veiculo_local_veiculo_veiculo_local_data` (NONCLUSTERED): id_local, id_veiculo, id_veiculo_local, data
- IDX `IX_veiculo_placa_data` (NONCLUSTERED): placa, data
- PK `PK_veiculo` (CLUSTERED): id_veiculo
- UNIQUE `UK_veiculo_veiculo_unic` (NONCLUSTERED): id_veiculo_unic

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- sequencia_local → dbo.local.sequencia_local
- id_local → dbo.local.id_local
- id_captura → ia.veiculo_caracteristica.id_captura

**Referenciada por:**
- dbo.amostra_imagem.id_veiculo
- dbo.amostra_imagem_manual.id_veiculo
- dbo.infracao.id_veiculo
- dbo.perfil.id_veiculo_unic
- dbo.processo_medicao_veiculo.id_veiculo
- dbo.veiculo_descarga.id_veiculo
- dbo.veiculo_imagem.id_veiculo
- dbo.veiculo_video.id_veiculo
- dbo.veiculo_visualizados.id_veiculo

## dbo.veiculo_alterado_splice

Linhas: ~249

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | sequencial_trafego | int | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | velocidade | decimal(6,1) | N |  |  |  |
| 6 | velocidade_considerada | int | S |  |  |  |
| 7 | velocidade_limite | int | S |  |  |  |
| 8 | comprimento | decimal(6,1) | N |  |  |  |
| 9 | comprimentoCM | int | S |  |  |  |
| 10 | pista | tinyint | N |  |  |  |
| 11 | id_classe | char(1) | S |  |  |  |
| 12 | id_enquadramento | int | N |  |  |  |
| 13 | id_inconsistencia | int | S |  |  |  |
| 14 | inconsistencia | varchar(70) | S |  |  |  |

## dbo.veiculo_descarga

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N |  |  |  |
| 2 | id_descarga | int | N |  |  |  |
| 3 | id_pasta | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculos_descarga_descarga` (NONCLUSTERED): id_descarga
- PK `PK_veiculo_descarga` (CLUSTERED): id_veiculo

**FKs (saída):**
- id_descarga → dbo.descarga.id_descarga
- id_veiculo → dbo.veiculo.id_veiculo

## dbo.veiculo_estatistica

Linhas: ~3130567

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | N |  |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | placa | char(7) | S |  |  |  |
| 4 | velocidade | decimal(6,1) | N |  |  |  |
| 5 | comprimento | decimal(6,1) | N |  |  |  |
| 6 | pista | tinyint | N |  |  |  |
| 7 | flag | int | N |  |  |  |
| 8 | segundos | decimal(8,3) | S |  |  |  |
| 9 | id_veiculo_unic | bigint | S |  |  |  |
| 10 | id_classe | char(1) | S |  |  |  |
| 11 | id_local | int | S |  |  |  |
| 12 | sequencia_local | tinyint | N |  | ((1)) |  |
| 13 | ocupacao | int | S |  |  |  |
| 14 | id_arquivo | int | S |  |  |  |
| 15 | entre_faixa | int | S |  |  |  |
| 16 | velocidade_aux | decimal(6,2) | S |  |  |  |
| 17 | seq_deteccao_doppler | int | S |  |  |  |
| 18 | velocidade_media | int | S |  |  |  |
| 19 | serie_equipamento_Montante | int | S |  |  |  |
| 20 | id_veiculo_Local_Montante | int | S |  |  |  |
| 21 | validacao_velocidade_target | tinyint | S |  |  |  |
| 22 | numero_eixos | int | S |  |  |  |
| 23 | rodagem_dupla | bit | S |  |  |  |
| 24 | categoria | int | S |  |  |  |
| 25 | placa_mercosul | bit | N |  | ((0)) |  |
| 26 | com_pesagem | bit | N |  | ((0)) |  |
| 27 | classificacao | varchar(100) | S |  |  |  |
| 28 | id_captura | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_estatistica_com_pesagem` (NONCLUSTERED): com_pesagem
- IDX `IX_veiculo_estatistica_data` (NONCLUSTERED): data
- IDX `IX_veiculo_estatistica_data_v2` (NONCLUSTERED): data
- IDX `IX_veiculo_estatistica_id_captura` (NONCLUSTERED): id_captura
- IDX `IX_veiculo_estatistica_id_unic` (NONCLUSTERED): id_veiculo_unic
- IDX `IX_veiculo_estatistica_id_unic_pesagem` (NONCLUSTERED): id_veiculo_unic

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- id_captura → ia.veiculo_caracteristica.id_captura

## dbo.veiculo_estatistica_bkp_placa

Linhas: ~2399

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | N |  |  |  |
| 2 | placa | char(7) | S |  |  |  |
| 3 | id_veiculo_unic | bigint | S |  |  |  |
| 4 | rn | bigint | S |  |  |  |

## dbo.veiculo_estatistica_integracao_importacao_dt

Linhas: ~25

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | N |  |  |  |
| 2 | id_local | smallint | N |  |  |  |
| 3 | id_pista | tinyint | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | id_veiculo_local | int | N |  |  |  |
| 6 | velocidade | int | N |  |  |  |
| 7 | id_classe | char(1) | N |  |  |  |
| 8 | id_arquivo | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__veiculo___B79FACA31AFE31D7` (CLUSTERED): data, id_local, id_veiculo_local

## dbo.veiculo_flag_01

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | id_veiculo | bigint | S |  |  |  |
| 3 | TIMEOK | bit | N |  |  |  |
| 4 | MANUTENCAO | bit | N |  |  |  |
| 5 | IMG | bit | N |  |  |  |
| 6 | OPCAO_MANUT | bit | N |  |  |  |
| 7 | IMGEXT_PAN | bit | N |  |  |  |
| 8 | TEST_IMAGE | bit | N |  |  |  |
| 9 | INVALID | bit | N |  |  |  |
| 10 | INVALID_METROLOGIC | bit | N |  |  |  |
| 11 | DEFECTIVE_IMAGE | bit | N |  |  |  |
| 12 | CAPTURED_IMAGE | bit | N |  |  |  |
| 13 | OCR_PROCESSED | bit | N |  |  |  |
| 14 | CLOCK_NOT_RELIABLE | bit | N |  |  |  |
| 15 | SHOW_DIV | bit | N |  |  |  |
| 16 | OCR | bit | N |  |  |  |
| 17 | SPEED | bit | N |  |  |  |
| 18 | RED | bit | N |  |  |  |
| 19 | WRONG_WAY | bit | N |  |  |  |
| 20 | RODIZIO | bit | N |  |  |  |
| 21 | CIRC_EXCL | bit | N |  |  |  |
| 22 | FORA_FAIXA | bit | N |  |  |  |
| 23 | PARADAFAIXA | bit | N |  |  |  |
| 24 | LOCALHORARIO_CARGA | bit | N |  |  |  |
| 25 | CONV_DIR | bit | N |  |  |  |
| 26 | CONV_ESQ | bit | N |  |  |  |
| 27 | RET_PROIB | bit | N |  |  |  |
| 28 | LOCALHORARIO_REGUL | bit | N |  |  |  |
| 29 | CIRC_EXCL_DIR | bit | N |  |  |  |
| 30 | FAIXA_TRANSP_PUBLICO | bit | N |  |  |  |
| 31 | CICLOFAIXA | bit | N |  |  |  |
| 32 | VELOCIDADE_MEDIA | bit | N |  |  |  |
| 33 | EVASAO_PEDAGIO | bit | N |  |  |  |
| 34 | PESAGEM_VEICULO | bit | N |  |  |  |
| 35 | BLOQUEIO_VIARIO | bit | N |  |  |  |
| 36 | LENTO_MAIOR_PORTE | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_flag_01_id_unic` (CLUSTERED): id_veiculo_unic

## dbo.veiculo_flag_02

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | id_veiculo | bigint | S |  |  |  |
| 3 | TIMEOK | bit | N |  |  |  |
| 4 | MANUTENCAO | bit | N |  |  |  |
| 5 | IMG | bit | N |  |  |  |
| 6 | OPCAO_MANUT | bit | N |  |  |  |
| 7 | IMGEXT_PAN | bit | N |  |  |  |
| 8 | TEST_IMAGE | bit | N |  |  |  |
| 9 | INVALID | bit | N |  |  |  |
| 10 | INVALID_METROLOGIC | bit | N |  |  |  |
| 11 | DEFECTIVE_IMAGE | bit | N |  |  |  |
| 12 | CAPTURED_IMAGE | bit | N |  |  |  |
| 13 | OCR_PROCESSED | bit | N |  |  |  |
| 14 | CLOCK_NOT_RELIABLE | bit | N |  |  |  |
| 15 | SHOW_DIV | bit | N |  |  |  |
| 16 | OCR | bit | N |  |  |  |
| 17 | SPEED | bit | N |  |  |  |
| 18 | RED | bit | N |  |  |  |
| 19 | WRONG_WAY | bit | N |  |  |  |
| 20 | RODIZIO | bit | N |  |  |  |
| 21 | CIRC_EXCL | bit | N |  |  |  |
| 22 | FORA_FAIXA | bit | N |  |  |  |
| 23 | PARADAFAIXA | bit | N |  |  |  |
| 24 | LOCALHORARIO_CARGA | bit | N |  |  |  |
| 25 | CONV_DIR | bit | N |  |  |  |
| 26 | CONV_ESQ | bit | N |  |  |  |
| 27 | RET_PROIB | bit | N |  |  |  |
| 28 | LOCALHORARIO_REGUL | bit | N |  |  |  |
| 29 | CIRC_EXCL_DIR | bit | N |  |  |  |
| 30 | FAIXA_TRANSP_PUBLICO | bit | N |  |  |  |
| 31 | CICLOFAIXA | bit | N |  |  |  |
| 32 | VELOCIDADE_MEDIA | bit | N |  |  |  |
| 33 | EVASAO_PEDAGIO | bit | N |  |  |  |
| 34 | PESAGEM_VEICULO | bit | N |  |  |  |
| 35 | BLOQUEIO_VIARIO | bit | N |  |  |  |
| 36 | LENTO_MAIOR_PORTE | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_flag_02_id_unic` (CLUSTERED): id_veiculo_unic

## dbo.veiculo_flag_atualiza_painel

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | CAPTURED_IMAGE | bit | N |  |  |  |
| 3 | OCR_PROCESSED | bit | N |  |  |  |
| 4 | DEFECTIVE_IMAGE | bit | N |  |  |  |

## dbo.veiculo_imagem

Linhas: ~107771

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | id_imagem_local | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_imagem_imagem` (NONCLUSTERED): id_imagem
- PK `PK_veiculo_imagem` (CLUSTERED): id_veiculo, id_imagem

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_veiculo → dbo.veiculo.id_veiculo

## dbo.veiculo_importacao

Linhas: ~20551

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N | S |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | velocidade | decimal(6,1) | S |  |  |  |
| 6 | comprimento | decimal(6,1) | S |  |  |  |
| 7 | pista | tinyint | N |  |  |  |
| 8 | flag | int | N |  |  |  |
| 9 | segundos | decimal(8,3) | S |  |  |  |
| 10 | id_veiculo_unic | bigint | N |  |  |  |
| 11 | id_classe | char(1) | N |  |  |  |
| 12 | id_local | int | N |  |  |  |
| 13 | sequencia_local | tinyint | S |  |  |  |
| 14 | ocupacao | int | N |  |  |  |
| 15 | nome_arquivo | varchar(200) | N |  |  |  |
| 16 | registro_valido | bit | N |  |  |  |
| 17 | monitorado | bit | N |  |  |  |
| 18 | tipo_registro | bit | S |  |  |  |
| 19 | velocidade_media | int | S |  |  |  |
| 20 | serie_equipamento_Montante | int | S |  |  |  |
| 21 | id_veiculo_Local_Montante | int | S |  |  |  |
| 22 | porteVeiculo | varchar(6) | S |  |  |  |
| 23 | cadastro | varchar(3) | S |  |  |  |
| 24 | importar | bit | N |  | ((1)) |  |
| 25 | entre_faixa | int | S |  |  |  |
| 26 | velocidade_aux | decimal(6,2) | S |  |  |  |
| 27 | seq_deteccao_doppler | int | S |  |  |  |
| 28 | validacao_velocidade_target | tinyint | S |  |  |  |
| 29 | numero_eixos | int | S |  |  |  |
| 30 | rodagem_dupla | bit | S |  |  |  |
| 31 | categoria | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_importacao_importar_seq_local_tipo_reg` (NONCLUSTERED): importar, sequencia_local, tipo_registro
- IDX `IX_veiculo_importacao_nome_arquivo` (NONCLUSTERED): nome_arquivo
- IDX `IX_veiculo_importacao_sequencia_local_nome_arquivo` (NONCLUSTERED): sequencia_local
- PK `PK_veiculo_importacao` (CLUSTERED): id_veiculo
- UNIQUE `UK_veiculo_importacao_veiculo_unic` (NONCLUSTERED): id_veiculo_unic

**FKs (saída):**
- nome_arquivo → dbo.arquivos_importados.nome_arquivo
- id_classe → dbo.classe_veiculo.id_classe

## dbo.veiculo_importacao_erro

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N | S |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | velocidade | decimal(6,1) | S |  |  |  |
| 6 | comprimento | decimal(6,1) | S |  |  |  |
| 7 | pista | tinyint | N |  |  |  |
| 8 | flag | int | N |  |  |  |
| 9 | segundos | decimal(8,3) | S |  |  |  |
| 10 | id_veiculo_unic | bigint | N |  |  |  |
| 11 | id_classe | char(1) | N |  |  |  |
| 12 | id_local | int | N |  |  |  |
| 13 | sequencia_local | tinyint | S |  |  |  |
| 14 | ocupacao | int | N |  |  |  |
| 15 | nome_arquivo | varchar(200) | N |  |  |  |
| 16 | registro_valido | bit | N |  |  |  |
| 17 | monitorado | bit | N |  |  |  |
| 18 | tipo_registro | bit | S |  |  |  |
| 19 | velocidade_media | int | S |  |  |  |
| 20 | serie_equipamento_Montante | int | S |  |  |  |
| 21 | id_veiculo_Local_Montante | int | S |  |  |  |
| 22 | porteVeiculo | varchar(6) | S |  |  |  |
| 23 | cadastro | varchar(3) | S |  |  |  |

## dbo.veiculo_importacao_estatico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | serie_equipamento | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_importacao_estatico` (CLUSTERED): id_veiculo_unic

**FKs (saída):**
- serie_equipamento → dbo.equipamento_estatico.serie_equipamento

## dbo.veiculo_importacao_flag

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | TIMEOK | bit | N |  |  |  |
| 3 | MANUTENCAO | bit | N |  |  |  |
| 4 | IMG | bit | N |  |  |  |
| 5 | OPCAO_MANUT | bit | N |  |  |  |
| 6 | IMGEXT_PAN | bit | N |  |  |  |
| 7 | TEST_IMAGE | bit | N |  |  |  |
| 8 | INVALID | bit | N |  |  |  |
| 9 | INVALID_METROLOGIC | bit | N |  |  |  |
| 10 | DEFECTIVE_IMAGE | bit | N |  |  |  |
| 11 | CAPTURED_IMAGE | bit | N |  |  |  |
| 12 | OCR_PROCESSED | bit | N |  |  |  |
| 13 | CLOCK_NOT_RELIABLE | bit | N |  |  |  |
| 14 | SHOW_DIV | bit | N |  |  |  |
| 15 | OCR | bit | N |  |  |  |
| 16 | SPEED | bit | N |  |  |  |
| 17 | RED | bit | N |  |  |  |
| 18 | WRONG_WAY | bit | N |  |  |  |
| 19 | RODIZIO | bit | N |  |  |  |
| 20 | CIRC_EXCL | bit | N |  |  |  |
| 21 | FORA_FAIXA | bit | N |  |  |  |
| 22 | PARADAFAIXA | bit | N |  |  |  |
| 23 | LOCALHORARIO_CARGA | bit | N |  |  |  |
| 24 | CONV_DIR | bit | N |  |  |  |
| 25 | CONV_ESQ | bit | N |  |  |  |
| 26 | RET_PROIB | bit | N |  |  |  |
| 27 | LOCALHORARIO_REGUL | bit | N |  |  |  |
| 28 | CIRC_EXCL_DIR | bit | N |  |  |  |
| 29 | FAIXA_TRANSP_PUBLICO | bit | N |  |  |  |
| 30 | CICLOFAIXA | bit | N |  |  |  |
| 31 | VELOCIDADE_MEDIA | bit | N |  |  |  |
| 32 | EVASAO_PEDAGIO | bit | N |  |  |  |
| 33 | PESAGEM_VEICULO | bit | N |  |  |  |
| 34 | BLOQUEIO_VIARIO | bit | N |  |  |  |
| 35 | LENTO_MAIOR_PORTE | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK__veiculo___E69F6C62F1616AB5` (CLUSTERED): id_veiculo_unic

## dbo.veiculo_invalido

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_invalido | bigint | N | S |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | velocidade | decimal(6,1) | N |  |  |  |
| 5 | comprimento | decimal(6,1) | N |  |  |  |
| 6 | pista | tinyint | N |  |  |  |
| 7 | flag | int | N |  |  |  |
| 8 | segundos | decimal(6,3) | S |  |  |  |
| 9 | id_veiculo_unic | bigint | S |  |  |  |
| 10 | id_classe | char(1) | S |  |  |  |
| 11 | id_local | int | S |  |  |  |
| 12 | sequencia_local | tinyint | S |  | ((1)) |  |
| 13 | ocupacao | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_invalido` (NONCLUSTERED): id_veiculo_invalido

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- id_local → dbo.local.id_local
- sequencia_local → dbo.local.sequencia_local

**Referenciada por:**
- dbo.veiculo_invalido_imagem.id_veiculo_invalido

## dbo.veiculo_invalido_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_veiculo_invalido | bigint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_invalido_imagem` (NONCLUSTERED): id_imagem, id_veiculo_invalido

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_veiculo_invalido → dbo.veiculo_invalido.id_veiculo_invalido

## dbo.veiculo_monitorado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_monitorado | bigint | N | S |  |  |
| 2 | id_veiculo_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | placa | char(7) | S |  |  |  |
| 5 | velocidade | decimal(6,1) | N |  |  |  |
| 6 | comprimento | decimal(6,1) | N |  |  |  |
| 7 | pista | tinyint | N |  |  |  |
| 8 | flag | int | N |  |  |  |
| 9 | id_veiculo_unic | bigint | S |  |  |  |
| 10 | id_classe | char(1) | S |  |  |  |
| 11 | id_local | int | S |  |  |  |
| 12 | sequencia_local | tinyint | S |  | ((1)) |  |
| 13 | falsoPositivo | bit | S |  |  |  |
| 14 | dataAtualizacao | datetime | N |  | (getdate()) |  |
| 15 | id_email_enviar | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_monitorado_data_atualizacao_enviar_email` (NONCLUSTERED): dataAtualizacao, id_email_enviar
- PK `PK_Veiculo_monitorado` (NONCLUSTERED): id_veiculo_monitorado
- UNIQUE `UK_veiculo_monitorado_local_veiculo_local_data` (NONCLUSTERED): id_local, id_veiculo_local, data

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- sequencia_local → dbo.local.sequencia_local
- id_local → dbo.local.id_local

**Referenciada por:**
- dbo.veiculo_monitorado_imagem.id_veiculo_monitorado

## dbo.veiculo_monitorado_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_veiculo_monitorado | bigint | N |  |  |  |
| 3 | id_imagem_local | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_monitorado_imagem` (NONCLUSTERED): id_imagem, id_veiculo_monitorado

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_imagem → dbo.imagem_monitorado.id_imagem_monitorado
- id_veiculo_monitorado → dbo.veiculo_monitorado.id_veiculo_monitorado

## dbo.veiculo_montante

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_local | int | N |  |  |  |
| 2 | id_local | int | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | velocidade | decimal(6,1) | N |  |  |  |
| 5 | pista | tinyint | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_montante_data_local` (NONCLUSTERED): data
- IDX `IX_veiculo_montante_veiculo_local_id_local_data_vel_pista` (NONCLUSTERED): id_veiculo_local

## dbo.veiculo_pesagem

Linhas: ~1345178

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pesagem_valida | bit | N |  | ((1)) |  |
| 3 | pbt | float | S |  |  |  |
| 4 | temperatura_pavimento | float | S |  |  |  |
| 5 | velocidade_piezo | float | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_pesagem_id_unic` (NONCLUSTERED): id_veiculo_unic
- IDX `IX_veiculo_pesagem_pesagem_valida` (NONCLUSTERED): pesagem_valida

## dbo.veiculo_pesagem_complemento

Linhas: ~137

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | S |  |  |  |
| 2 | id_veiculo | bigint | S |  |  |  |
| 3 | id_local | int | S |  |  |  |
| 4 | data | datetime | S |  |  |  |
| 5 | placa | varchar(7) | S |  |  |  |
| 6 | velocidade | int | S |  |  |  |
| 7 | comprimento | decimal(28,0) | S |  |  |  |
| 8 | id_classe | varchar(1) | S |  |  |  |
| 9 | pbt | float | S |  |  |  |
| 10 | temperatura_pavimento | float | S |  |  |  |
| 11 | velocidade_piezo | float | S |  |  |  |
| 12 | classificacao | varchar(100) | S |  |  |  |
| 13 | qtde_eixos | int | S |  |  |  |
| 14 | E1 | float | S |  |  |  |
| 15 | E2 | float | S |  |  |  |
| 16 | E3 | float | S |  |  |  |
| 17 | E4 | float | S |  |  |  |
| 18 | E5 | float | S |  |  |  |
| 19 | E6 | float | S |  |  |  |
| 20 | E7 | float | S |  |  |  |
| 21 | E8 | float | S |  |  |  |
| 22 | E9 | float | S |  |  |  |
| 23 | distancia_E1E2 | float | S |  |  |  |
| 24 | distancia_E2E3 | float | S |  |  |  |
| 25 | distancia_E3E4 | float | S |  |  |  |
| 26 | distancia_E4E5 | float | S |  |  |  |
| 27 | distancia_E5E6 | float | S |  |  |  |
| 28 | distancia_E6E7 | float | S |  |  |  |
| 29 | distancia_E7E8 | float | S |  |  |  |
| 30 | distancia_E8E9 | float | S |  |  |  |

## dbo.veiculo_pesagem_controle

Linhas: ~1482156

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pesagem_valida | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- IDX `IX_veiculo_pesagem_controle_id_unic` (NONCLUSTERED): id_veiculo_unic

## dbo.veiculo_pesagem_eixo

Linhas: ~66076

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | eixo | tinyint | N |  |  |  |
| 3 | peso | float | S |  |  |  |
| 4 | distancia_eixo_anterior | float | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_pesagem_eixo_distancia_eixo_anterior` (NONCLUSTERED): distancia_eixo_anterior
- IDX `IX_veiculo_pesagem_eixo_id_unic` (NONCLUSTERED): id_veiculo_unic
- IDX `IX_veiculo_pesagem_eixo_peso` (NONCLUSTERED): peso

## dbo.veiculo_pesagem_eixo_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | eixo | tinyint | N |  |  |  |
| 3 | peso | float | S |  |  |  |
| 4 | distancia_eixo_anterior | float | S |  |  |  |

## dbo.veiculo_pesagem_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pesagem_valida | bit | N |  | ((1)) |  |
| 3 | pbt | float | S |  |  |  |
| 4 | temperatura_pavimento | float | S |  |  |  |
| 5 | velocidade_piezo | float | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_pesagem_importacao_id_unic` (NONCLUSTERED): id_veiculo_unic

## dbo.veiculo_sumarizado

Linhas: ~4146565

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | date | N |  |  |  |
| 2 | hora | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | sequencia_local | tinyint | N |  |  |  |
| 5 | pista | tinyint | N |  |  |  |
| 6 | id_faixa_velocidade | int | N |  |  |  |
| 7 | id_classe | char(1) | N |  |  |  |
| 8 | media_velocidade | decimal(6,1) | N |  |  |  |
| 9 | min_velocidade | decimal(6,1) | N |  |  |  |
| 10 | max_velocidade | decimal(6,1) | N |  |  |  |
| 11 | trafego | int | N |  |  |  |
| 12 | ocupacao | int | N |  |  |  |
| 13 | placa_lida | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_sumarizado_id_local_pista_data` (NONCLUSTERED): id_local, pista, data
- PK `PK_veiculo_sumarizado` (CLUSTERED): data, hora, id_local, sequencia_local, pista, id_faixa_velocidade, id_classe

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- id_faixa_velocidade → dbo.faixa_velocidade.id_faixa_velocidade
- id_local → dbo.local.id_local
- sequencia_local → dbo.local.sequencia_local

## dbo.veiculo_sumarizado_faixa_velocidade

Linhas: ~2409354

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dia | date | N |  |  |  |
| 2 | hora | tinyint | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | id_pista | tinyint | N |  |  |  |
| 5 | id_faixa_velocidade | int | N |  |  |  |
| 6 | veiculos_detectados | int | N |  |  |  |

## dbo.veiculo_sumarizado_relatorio

Linhas: ~292372

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | dia | date | N |  |  |  |
| 3 | hora | tinyint | N |  |  |  |
| 4 | id_pista | tinyint | N |  |  |  |
| 5 | velocidade_media | decimal(6,1) | S |  |  |  |
| 6 | velocidade_maxima | decimal(6,1) | S |  |  |  |
| 7 | veiculos_detectados | int | S |  |  |  |
| 8 | infracoes_registradas | int | S |  |  |  |
| 9 | infracoes_validas | int | S |  |  |  |
| 10 | placa_lida | int | S |  |  |  |
| 11 | imagens_teste_registradas | int | S |  |  |  |

## dbo.veiculo_sumarizado_relatorio_vel_85

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |
| 3 | data | date | N |  |  |  |
| 4 | velocidade | int | S |  |  |  |

## dbo.veiculo_target

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_alvo | int | N |  |  |  |
| 2 | seq_deteccao_doppler | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | data_arquivo | datetime | N |  |  |  |
| 5 | id_arquivo | int | N |  |  |  |
| 6 | id_veiculo | bigint | S |  |  |  |
| 7 | nome_arquivo | varchar(200) | S |  |  |  |
| 8 | id_arquivo_csx5 | int | S |  |  |  |

## dbo.veiculo_target_pontos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_alvo | int | N |  |  |  |
| 2 | velocidade | decimal(5,2) | N |  |  |  |
| 3 | direcao | bit | N |  |  |  |
| 4 | posicao_x | decimal(4,2) | N |  |  |  |
| 5 | posicao_y | decimal(4,2) | N |  |  |  |
| 6 | angulo | decimal(4,1) | N |  |  |  |
| 7 | potencia | decimal(3,1) | N |  |  |  |
| 8 | id_arquivo | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_target_pontos_id_alvo_arquivo` (NONCLUSTERED): id_alvo, id_arquivo

## dbo.veiculo_tempo_real_bkp_sem_placa_araxa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | placa | char(7) | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | id_local | int | N |  |  |  |
| 5 | id_pista | tinyint | N |  |  |  |
| 6 | velocidade | smallint | N |  |  |  |
| 7 | enviado_cliente | bit | N |  |  |  |
| 8 | data_enviado | datetime | S |  |  |  |
| 9 | classificacao | char(1) | S |  |  |  |
| 10 | estado_veiculo | tinyint | N |  |  |  |
| 11 | processado_tarefas_alerta | tinyint | N |  |  |  |
| 12 | data_processado_tarefas_alerta | datetime | S |  |  |  |

## dbo.veiculo_tempo_real_imagem_copiar_dev

Linhas: ~4429155

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  |  |  |
| 2 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 3 | imagem | image | N |  |  |  |
| 4 | indice_imagem | tinyint | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_tempo_real_imagem_copiar` (NONCLUSTERED): id_veiculo_tempo_real

## dbo.veiculo_video

Linhas: ~11447

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_video | int | N |  |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_video` (CLUSTERED): id_veiculo, id_video

**FKs (saída):**
- id_veiculo → dbo.veiculo.id_veiculo
- id_video → dbo.video_info.id_video

## dbo.veiculo_visualizados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_visualizado | int | N | S |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_hora | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_veiculo_visualizado_veiculo` (NONCLUSTERED): id_veiculo
- PK `PK_veiculo_visualizados` (CLUSTERED): id_veiculo_visualizado

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_veiculo → dbo.veiculo.id_veiculo

