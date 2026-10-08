# Tabelas — schema `dbo` — grupo `ppv`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.ppv_classificacao_distancia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classificacao_distancia | int | N |  |  |  |
| 2 | id_classificacao_pesagem | int | N |  |  |  |
| 3 | descricao | varchar(300) | N |  |  |  |
| 4 | operador | varchar(100) | N |  |  |  |
| 5 | menor_distancia | int | N |  |  |  |
| 6 | maior_distancia | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_classificacao_distancia` (CLUSTERED): id_classificacao_distancia

**FKs (saída):**
- id_classificacao_pesagem → dbo.ppv_classificacao_pesagem.id_classificacao_pesagem

## dbo.ppv_classificacao_grupos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classificacao_grupo | int | N |  |  |  |
| 2 | id_classificacao_pesagem | int | N |  |  |  |
| 3 | chave | varchar(100) | N |  |  |  |
| 4 | valor | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_classificacao_grupos` (CLUSTERED): id_classificacao_grupo

**FKs (saída):**
- id_classificacao_pesagem → dbo.ppv_classificacao_pesagem.id_classificacao_pesagem

## dbo.ppv_classificacao_pesagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classificacao_pesagem | int | N |  |  |  |
| 2 | descricao | int | N |  |  |  |
| 3 | figura_ilustrativa | varbinary(1) | S |  |  |  |
| 4 | peso_maximo | float | N |  |  |  |
| 5 | numEixos | int | N |  |  |  |
| 6 | numGrupos | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_classificacao_pesagem` (CLUSTERED): id_classificacao_pesagem

**Referenciada por:**
- dbo.ppv_classificacao_distancia.id_classificacao_pesagem
- dbo.ppv_classificacao_grupos.id_classificacao_pesagem

## dbo.ppv_ligacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_ligacao | int | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | placa | varchar(10) | N |  |  |  |
| 4 | id_SAI | int | N |  |  |  |
| 5 | id_veiculo_patio | int | S |  |  |  |
| 6 | id_status_ligacao | int | S |  |  |  |
| 7 | data_inicio_condutor | datetime | S |  |  |  |
| 8 | data_fim_condutor | datetime | S |  |  |  |
| 9 | data_inicio_agente | datetime | S |  |  |  |
| 10 | data_fim_agente | datetime | S |  |  |  |
| 11 | observacao | varchar(1000) | S |  |  |  |
| 12 | segundosLigacaoAgente | int | N |  | ((0)) |  |
| 13 | segundosLigacaoCondutor | int | N |  | ((0)) |  |
| 14 | ipExternoCondutor | varchar(100) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ligacao_SAI` (CLUSTERED): id_ligacao

**FKs (saída):**
- id_status_ligacao → dbo.ppv_ligacao_tp_status.id_tp_status
- id_SAI → dbo.ppv_SAI.id_SAI

## dbo.ppv_ligacao_tp_status

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tp_status | int | N |  |  |  |
| 2 | descricao_status | varchar(300) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_liga__999B73511CB04B53` (CLUSTERED): id_tp_status

**Referenciada por:**
- dbo.ppv_ligacao.id_status_ligacao

## dbo.ppv_monitoramento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_monitoramento | int | N |  |  |  |
| 2 | id_SAI | int | N |  |  |  |
| 3 | descricao | varchar(100) | N |  |  |  |
| 4 | ip_camera | int | N |  |  |  |
| 5 | id_tp_camera | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_monitoramento` (CLUSTERED): id_monitoramento

**FKs (saída):**
- id_SAI → dbo.ppv_SAI.id_SAI
- id_tp_camera → dbo.ppv_tp_camera.id_tp_camera

**Referenciada por:**
- dbo.ppv_monitoramento_ptz.id_monitoramento
- dbo.ppv_monitoramento_Puma.id_monitoramento

## dbo.ppv_monitoramento_ptz

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_monitoramento_ptz | int | N |  |  |  |
| 2 | posicao | int | N |  |  |  |
| 3 | descricao | varchar(100) | S |  |  |  |
| 4 | id_monitoramento | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_monitoramento_ptz` (CLUSTERED): id_monitoramento_ptz

**FKs (saída):**
- id_monitoramento → dbo.ppv_monitoramento.id_monitoramento

## dbo.ppv_monitoramento_Puma

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_monitoramento_puma | int | N |  |  |  |
| 2 | ip | varchar(100) | N |  |  |  |
| 3 | ip_local | varchar(100) | N |  |  |  |
| 4 | qualidade | int | N |  |  |  |
| 5 | frame_rate | int | N |  |  |  |
| 6 | resolution | int | N |  |  |  |
| 7 | tp | int | N |  |  |  |
| 8 | pista | int | N |  |  |  |
| 9 | id_monitoramento | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_monitoramento_Puma` (CLUSTERED): id_monitoramento_puma

**FKs (saída):**
- id_monitoramento → dbo.ppv_monitoramento.id_monitoramento

## dbo.ppv_ocorrencias

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_ocorrencia | int | N |  |  |  |
| 2 | id_SAI | int | N |  |  |  |
| 3 | id_tpocorrencia | int | N |  |  |  |
| 4 | data | datetime | N |  |  |  |
| 5 | observacoes | varchar(1000) | N |  |  |  |
| 6 | id_veiculo_interno | bigint | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ocorrencias` (CLUSTERED): id_ocorrencia

**FKs (saída):**
- id_SAI → dbo.ppv_SAI.id_SAI
- id_tpocorrencia → dbo.ppv_tp_ocorrencia.id_tpocorrencia

## dbo.ppv_patio_vagas_ptz

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_registro | int | N | S |  |  |
| 2 | id_vaga | int | N |  |  |  |
| 3 | data_entrada | datetime | N |  | (getdate()) |  |
| 4 | data_saida | datetime | S |  |  |  |
| 5 | placa_lida | char(7) | S |  |  |  |
| 6 | placa_digitada | char(7) | S |  |  |  |
| 7 | imagem | varbinary(max) | N |  |  |  |
| 8 | imagem_placa | varbinary(max) | S |  |  |  |
| 9 | id_veiculo | int | S |  |  |  |
| 10 | classificacaoQFV | varchar(50) | S |  |  |  |
| 11 | id_registro_patio_simulado | int | S |  |  |  |
| 12 | contem_veiculo | bit | S |  |  |  |
| 13 | idVeiculoSAI | bigint | S |  |  |  |
| 14 | dataVeiculoSAI | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_pati__48155C1F9EBCCF95` (CLUSTERED): id_registro

## dbo.ppv_qfv_distancias

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_distancia | tinyint | N |  |  |  |
| 2 | limite_inferior | decimal(5,3) | N |  |  |  |
| 3 | limite_superior | decimal(5,3) | N |  |  |  |
| 4 | descricao | varchar(30) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_qfv___112BD1A4CCE81336` (CLUSTERED): id_distancia

## dbo.ppv_qfv_grupo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_grupo | tinyint | N |  |  |  |
| 2 | eixos | tinyint | N |  |  |  |
| 3 | id_conf_eixos | tinyint | N |  |  |  |
| 4 | descricao | varchar(20) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_qfv___8B68D688892D5199` (CLUSTERED): id_grupo

## dbo.ppv_qfv_grupo_config

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_conf_eixos | tinyint | N |  |  |  |
| 2 | descrição | varchar(80) | N |  |  |  |
| 3 | tipo | varchar(12) | N |  |  |  |
| 4 | carga | decimal(5,3) | N |  |  |  |
| 5 | carga_tolerancia | decimal(5,3) | N |  |  |  |
| 6 | eixos | tinyint | N |  |  |  |
| 7 | rodado_simples | tinyint | N |  |  |  |
| 8 | rodado_duplo | tinyint | N |  |  |  |
| 9 | eixo_1 | tinyint | N |  |  |  |
| 10 | eixo_2 | tinyint | N |  |  |  |
| 11 | eixo_3 | tinyint | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_qfv___ADF5DB279343F734` (CLUSTERED): id_conf_eixos

## dbo.ppv_qfv_tipos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classificacao_qfv | int | N | S |  |  |
| 2 | id_classificacao | char(3) | N |  |  |  |
| 3 | codigo | tinyint | N |  |  |  |
| 4 | numero_grupos | tinyint | N |  |  |  |
| 5 | numero_eixos | tinyint | N |  |  |  |
| 6 | pbt | decimal(5,3) | N |  |  |  |
| 7 | pbt_tolerancia | decimal(5,3) | N |  |  |  |
| 8 | comprimento | tinyint | S |  |  |  |
| 9 | comprimento_aet | tinyint | S |  |  |  |
| 10 | descricao | varchar(80) | S |  |  |  |
| 11 | requer_aet | bit | N |  |  |  |
| 12 | grupo_1 | tinyint | N |  |  |  |
| 13 | grupo_2 | tinyint | N |  |  |  |
| 14 | grupo_3 | tinyint | S |  |  |  |
| 15 | grupo_4 | tinyint | S |  |  |  |
| 16 | grupo_5 | tinyint | S |  |  |  |
| 17 | grupo_6 | tinyint | S |  |  |  |
| 18 | grupo_7 | tinyint | S |  |  |  |
| 19 | D12 | tinyint | N |  |  |  |
| 20 | D23 | tinyint | S |  |  |  |
| 21 | D34 | tinyint | S |  |  |  |
| 22 | D45 | tinyint | S |  |  |  |
| 23 | D56 | tinyint | S |  |  |  |
| 24 | D67 | tinyint | S |  |  |  |
| 25 | D78 | tinyint | S |  |  |  |
| 26 | D89 | tinyint | S |  |  |  |
| 27 | imagem | image | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_qfv_tipos` (CLUSTERED): id_classificacao_qfv

## dbo.ppv_quantitativos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_SAI | int | N |  |  |  |
| 2 | mes | int | N |  |  |  |
| 3 | ano | int | N |  |  |  |
| 4 | qtde_passagens | bigint | N |  |  |  |
| 5 | qtde_passagens_veic_pesado | bigint | N |  |  |  |
| 6 | qtde_infracoes | bigint | N |  |  |  |
| 7 | data_atualizacao | datetime | N |  |  |  |

**Índices/Chaves:**
- UNIQUE `UC_id_sai_mes_ano` (NONCLUSTERED): id_SAI, mes, ano

## dbo.ppv_quantitativos_config

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_SAI | int | N |  |  |  |
| 2 | id_local_conta_passagem | int | N |  |  |  |
| 3 | dtCadastro | datetime | N |  |  |  |
| 4 | ativo | int | N |  |  |  |

**Índices/Chaves:**
- UNIQUE `UC_QuantitativosConfig_idSAI_idLocal` (NONCLUSTERED): id_SAI, id_local_conta_passagem

## dbo.ppv_quatitativos_infracoes_local

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_SAI | int | N |  |  |  |
| 2 | mes | int | N |  |  |  |
| 3 | ano | int | N |  |  |  |
| 4 | id_local | int | N |  |  |  |
| 5 | qtde_infracoes | bigint | N |  |  |  |

## dbo.ppv_SAI

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_SAI | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |
| 3 | localizacao | varchar(300) | N |  |  |  |
| 4 | CGOF_desc | varchar(100) | N |  |  |  |
| 5 | qtde_vagas_estacionamento | int | N |  | ((8)) |  |

**Índices/Chaves:**
- PK `PK_SAI` (CLUSTERED): id_SAI

**Referenciada por:**
- dbo.ppv_ligacao.id_SAI
- dbo.ppv_monitoramento.id_SAI
- dbo.ppv_ocorrencias.id_SAI

## dbo.ppv_tp_camera

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tp_camera | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tp_camera` (CLUSTERED): id_tp_camera

**Referenciada por:**
- dbo.ppv_monitoramento.id_tp_camera

## dbo.ppv_tp_ocorrencia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tpocorrencia | int | N |  |  |  |
| 2 | descricao | varchar(300) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tp_ocorrencia` (CLUSTERED): id_tpocorrencia

**Referenciada por:**
- dbo.ppv_ocorrencias.id_tpocorrencia

## dbo.ppv_tp_processo_veiculo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tp_processo | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_tp_processo` (CLUSTERED): id_tp_processo

## dbo.ppv_veiculo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N |  |  |  |
| 2 | id_sai | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | id_pista | int | N |  |  |  |
| 5 | placa | varchar(100) | S |  |  |  |
| 6 | dados_bruto | text | S |  |  |  |
| 7 | simulado | int | N |  | ((0)) |  |
| 8 | data | datetime | N |  | ('1970-01-01 00:00:00') |  |
| 9 | id_tp_sinalizacao | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__5AA41B11A2D8BBC7` (CLUSTERED): id_veiculo

**FKs (saída):**
- id_tp_sinalizacao → dbo.ppv_veiculo_tp_sinalizacao.id_tp_sinalizacao

**Referenciada por:**
- dbo.ppv_veiculo_pesagem.id_veiculo
- dbo.ppv_veiculo_XML.id_veiculo
- dbo.ppv_veiculos_interacao_XML_detalhes.id_veiculo

## dbo.ppv_veiculo_pesagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_pesagem | int | N | S |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | pbt | int | N |  |  |  |
| 4 | num_eixos | int | N |  |  |  |
| 5 | altura | float | N |  |  |  |
| 6 | comprimento | float | N |  |  |  |
| 7 | largura | float | N |  |  |  |
| 8 | id_classificacao | varchar(20) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_veiculo_pesagem` (CLUSTERED): id_pesagem

**FKs (saída):**
- id_veiculo → dbo.ppv_veiculo.id_veiculo

**Referenciada por:**
- dbo.ppv_veiculo_pesagem_distancia.id_pesagem
- dbo.ppv_veiculo_pesagem_eixos.id_pesagem
- dbo.ppv_veiculo_pesagem_grupos.id_pesagem

## dbo.ppv_veiculo_pesagem_distancia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_pesagem_distancia | int | N | S |  |  |
| 2 | id_pesagem | int | N |  |  |  |
| 3 | descricao | varchar(60) | N |  |  |  |
| 4 | distancia_medida | float | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_veiculo_pesagem_distancia` (CLUSTERED): id_veiculo_pesagem_distancia

**FKs (saída):**
- id_pesagem → dbo.ppv_veiculo_pesagem.id_pesagem

## dbo.ppv_veiculo_pesagem_eixos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_pesagem_eixo | int | N | S |  |  |
| 2 | id_pesagem | int | N |  |  |  |
| 3 | descricao | varchar(300) | N |  |  |  |
| 4 | peso | float | N |  |  |  |
| 5 | excesso | float | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_veiculo_pesagem_eixos` (CLUSTERED): id_veiculo_pesagem_eixo

**FKs (saída):**
- id_pesagem → dbo.ppv_veiculo_pesagem.id_pesagem

## dbo.ppv_veiculo_pesagem_grupos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_pesagem_grupo | int | N | S |  |  |
| 2 | id_pesagem | int | N |  |  |  |
| 3 | descricao | varchar(300) | N |  |  |  |
| 4 | peso | float | N |  |  |  |
| 5 | excesso | float | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ppv_veiculo_pesagem_grupos` (CLUSTERED): id_veiculo_pesagem_grupo

**FKs (saída):**
- id_pesagem → dbo.ppv_veiculo_pesagem.id_pesagem

## dbo.ppv_veiculo_tp_sinalizacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tp_sinalizacao | int | N |  |  |  |
| 2 | tpEquipamentoPonto | varchar(300) | N |  |  |  |
| 3 | descricao | varchar(500) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__6514D7598490B0D5` (CLUSTERED): id_tp_sinalizacao

**Referenciada por:**
- dbo.ppv_veiculo.id_tp_sinalizacao

## dbo.ppv_veiculo_XML

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_xml | bigint | N | S |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | veiculo_xml | text | S |  |  |  |
| 4 | simulado | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__C4F0F0BDBDF0FE7D` (CLUSTERED): id_veiculo_xml

**FKs (saída):**
- id_veiculo → dbo.ppv_veiculo.id_veiculo

## dbo.ppv_veiculos_interacao_XML_detalhes

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_detalhe | int | N |  |  |  |
| 2 | id_interacao | int | N |  |  |  |
| 3 | id_veiculo | bigint | N |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__4F131145C0EFE9F3` (CLUSTERED): id_detalhe

**FKs (saída):**
- id_veiculo → dbo.ppv_veiculo.id_veiculo

## dbo.ppv_veiculos_interacoes_XML

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_interacao | int | N |  |  |  |
| 2 | id_SAI | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | placa | varchar(10) | N |  |  |  |
| 5 | interacao_xml | text | S |  |  |  |
| 6 | simulado | int | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__FC7DC95E6F1D1F9F` (CLUSTERED): id_interacao

**FKs (saída):**
- id_interacao → dbo.ppv_veiculos_interacoes_XML.id_interacao

**Referenciada por:**
- dbo.ppv_veiculos_interacoes_XML.id_interacao

## dbo.ppv_veiculos_liberados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_liberacao | int | N | S |  |  |
| 2 | placa | char(7) | N |  |  |  |
| 3 | data_cadastro | datetime | N |  | (getdate()) |  |
| 4 | id_veiculo_interno | bigint | S |  |  |  |

**Índices/Chaves:**
- PK `PK__ppv_veic__64911F639BA67B31` (CLUSTERED): id_veiculo_liberacao

