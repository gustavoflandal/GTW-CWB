# Tabelas — schema `dbo` — grupo `cad`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.cad_arquivos_importados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | data_hora | datetime | S |  |  |  |
| 3 | nome_arquivo | nchar(100) | N |  |  |  |
| 4 | data_importacao | datetime | S |  |  |  |
| 5 | crc | varbinary(32) | S |  |  |  |
| 6 | arq_isento_removido | bit | S |  | ((0)) |  |
| 7 | data_removido | date | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_arquivos_importados_data_importacao` (NONCLUSTERED): data_importacao
- PK `PK_cad_arquivos_importados` (CLUSTERED): id
- UNIQUE `UK_cad_arquivos_importados_nome_arquivo_data` (NONCLUSTERED): nome_arquivo, data_hora

**Referenciada por:**
- dbo.cad_isento.id_arquivo
- dbo.cad_isento_ant.id_arquivo
- dbo.cad_isento_arquivo.id_arquivo

## dbo.cad_categoria

Linhas: ~14

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_categoria | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_categoria` (CLUSTERED): id_categoria

**Referenciada por:**
- dbo.cad_veiculo.id_categoria
- dbo.cad_veiculo_renainf.id_categoria

## dbo.cad_cor

Linhas: ~20

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cor | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_cor` (CLUSTERED): id_cor

**Referenciada por:**
- dbo.cad_veiculo.id_cor
- dbo.cad_veiculo_renainf.id_cor

## dbo.cad_especie

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_especie | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_especie` (CLUSTERED): id_especie

**Referenciada por:**
- dbo.cad_especie_processo.id_especie
- dbo.cad_veiculo.id_especie
- dbo.movimento_importacao.id_especie

## dbo.cad_especie_processo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | id_especie | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_especie_processo` (CLUSTERED): placa

**FKs (saída):**
- id_especie → dbo.cad_especie.id_especie

## dbo.cad_evento_manual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_cad_evento_manual | int | N | S |  |  |
| 2 | tipo_evento | int | N |  |  |  |
| 3 | sequencia_evento | int | N |  |  |  |
| 4 | id_evento | int | N |  |  |  |
| 5 | id_categoria | int | N |  |  |  |
| 6 | mensagem | varchar(512) | S |  |  |  |
| 7 | id_prioridade | int | N |  |  |  |
| 8 | id_nivel | int | N |  |  |  |
| 9 | id_evento_manual_categoria | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_evento_manual` (NONCLUSTERED): id_cad_evento_manual

**FKs (saída):**
- id_evento_manual_categoria → dbo.cad_evento_manual_categoria.id_evento_manual_categoria
- id_categoria → dbo.eventos_csx_desc_categoria.id_categoria
- id_evento → dbo.eventos_csx_desc_evento.id_evento
- id_nivel → dbo.eventos_csx_desc_nivel.id_nivel
- id_prioridade → dbo.eventos_csx_desc_prioridade.id_prioridade

## dbo.cad_evento_manual_categoria

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_evento_manual_categoria | int | N | S |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_evento_manual_categoria` (NONCLUSTERED): id_evento_manual_categoria

**Referenciada por:**
- dbo.cad_evento_manual.id_evento_manual_categoria

## dbo.cad_inibicao_infracao

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_inibicao_infracao | int | N | S |  |  |
| 2 | descricao | varchar(200) | N |  |  |  |
| 3 | pista | int | S |  |  |  |
| 4 | serie_equipamento | int | S |  |  |  |
| 5 | id_enquadramento | int | N |  |  |  |
| 6 | id_classe | char(1) | S |  |  |  |
| 7 | data_inicio | date | N |  |  |  |
| 8 | data_fim | date | N |  |  |  |
| 9 | horario_inicio | time | N |  |  |  |
| 10 | horario_fim | time | N |  |  |  |
| 11 | id_usuario | int | N |  |  |  |
| 12 | data_criacao | datetime | N |  | (getdate()) |  |
| 13 | id_usuario_cancelado | int | S |  |  |  |
| 14 | data_cancelado | datetime | S |  |  |  |
| 15 | id_filtro_relacionado | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_inibicao_infracao` (CLUSTERED): id_inibicao_infracao

**FKs (saída):**
- id_classe → dbo.classe_veiculo.id_classe
- id_enquadramento → dbo.enquadramento.id_enquadramento
- id_filtro_relacionado → dbo.filtro.id_filtro
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_cancelado → dbo.sis_usuario.id_usuario

## dbo.cad_isento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | id_enquadramento | int | N |  |  |  |
| 3 | area | int | N |  | ((0)) |  |
| 4 | id_localidade | int | S |  |  |  |
| 5 | modalidade | char(2) | S |  |  |  |
| 6 | data_inicio | date | S |  |  |  |
| 7 | data_fim | date | S |  |  |  |
| 8 | horario_inicio | time | S |  |  |  |
| 9 | horario_fim | time | S |  |  |  |
| 10 | data_atualizacao | datetime | S |  | (getdate()) |  |
| 11 | id_arquivo | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_isento_data_atualizacao` (NONCLUSTERED): data_atualizacao
- IDX `IX_cad_isento_enquadramento_data_atualizacao` (NONCLUSTERED): id_enquadramento, data_atualizacao
- IDX `IX_cad_isento_placa_enquadramento_data_atualizacao` (NONCLUSTERED): placa, id_enquadramento, data_atualizacao
- PK `PK_cad_isento` (CLUSTERED): id_arquivo, placa, id_enquadramento, area

**FKs (saída):**
- id_arquivo → dbo.cad_arquivos_importados.id
- id_enquadramento → dbo.enquadramento.id_enquadramento

## dbo.cad_isento_ant

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | id_enquadramento | int | N |  |  |  |
| 3 | area | int | N |  | ((0)) |  |
| 4 | id_localidade | int | S |  |  |  |
| 5 | modalidade | char(2) | S |  |  |  |
| 6 | data_inicio | date | S |  |  |  |
| 7 | data_fim | date | S |  |  |  |
| 8 | horario_inicio | time | S |  |  |  |
| 9 | horario_fim | time | S |  |  |  |
| 10 | data_atualizacao | datetime | S |  | (getdate()) |  |
| 11 | id_arquivo | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_isento_ant_data_atualizacao` (NONCLUSTERED): data_atualizacao
- IDX `IX_cad_isento_ant_enquadramento_data_atualizacao` (NONCLUSTERED): id_enquadramento, data_atualizacao
- IDX `IX_cad_isento_ant_placa_enquadramento_data_atualizacao` (NONCLUSTERED): placa, id_enquadramento, data_atualizacao
- PK `PK_cad_isento_ant` (CLUSTERED): id_arquivo, placa, id_enquadramento, area

**FKs (saída):**
- id_arquivo → dbo.cad_arquivos_importados.id
- id_enquadramento → dbo.enquadramento.id_enquadramento

## dbo.cad_isento_arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N |  |  |  |
| 2 | id_enquadramento | int | N |  |  |  |
| 3 | data_hora | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_isento_arquivo_enquadramento_data_hora` (NONCLUSTERED): id_enquadramento, data_hora
- PK `PK_cad_isento_arquivo` (CLUSTERED): id_arquivo

**FKs (saída):**
- id_arquivo → dbo.cad_arquivos_importados.id
- id_enquadramento → dbo.enquadramento.id_enquadramento

## dbo.cad_localidade

Linhas: ~5596

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_localidade | int | N |  |  |  |
| 2 | nome | char(30) | N |  |  |  |
| 3 | uf | char(2) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_localidade_nome` (NONCLUSTERED): nome
- PK `PK_cad_localidade` (CLUSTERED): id_localidade

**Referenciada por:**
- dbo.cad_veiculo.id_localidade
- dbo.local.id_localidade

## dbo.cad_marca

Linhas: ~47088

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_marca | int | N |  |  |  |
| 2 | descricao | char(35) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_marca` (CLUSTERED): id_marca

**Referenciada por:**
- dbo.cad_veiculo.id_marca
- dbo.cad_veiculo_renainf.id_marca

## dbo.cad_marca_cet

Linhas: ~1000

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_marca_cet | int | N |  |  |  |
| 2 | descricao | char(35) | N |  |  |  |
| 3 | dg_marca_cet | char(1) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_marca_cet` (CLUSTERED): id_marca_cet

**Referenciada por:**
- dbo.cad_marca_cet_processo.id_marca_cet
- dbo.cad_veiculo.id_marca_cet
- dbo.cad_veiculo_renainf.id_marca_cet
- dbo.movimento_importacao.id_marca_cet

## dbo.cad_marca_cet_processo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | id_marca_cet | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_marca_cet_processo_id_marca_cet` (NONCLUSTERED): id_marca_cet
- PK `PK_cad_marca_cet_processo` (CLUSTERED): placa

**FKs (saída):**
- id_marca_cet → dbo.cad_marca_cet.id_marca_cet

## dbo.cad_modalidade_isento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_modalidade | char(2) | N |  |  |  |
| 2 | descricao | char(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_modalidade_isento` (CLUSTERED): id_modalidade

## dbo.cad_modelo

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_modelo | int | N |  |  |  |
| 2 | descricao | char(35) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_modelo` (CLUSTERED): id_modelo

**Referenciada por:**
- dbo.cad_veiculo.id_modelo

## dbo.cad_municipio

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_municipio | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_municipio` (CLUSTERED): id_municipio

## dbo.cad_regiao

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_regiao | int | N |  |  |  |
| 2 | descricao | varchar(6) | N |  |  |  |

## dbo.cad_situacao

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_situacao | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_situacao` (CLUSTERED): id_situacao

**Referenciada por:**
- dbo.cad_veiculo.id_situacao

## dbo.cad_tipo

Linhas: ~30

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_tipo` (CLUSTERED): id_tipo

**Referenciada por:**
- dbo.cad_veiculo.id_tipo
- dbo.cad_veiculo_renainf.id_tipo

## dbo.cad_tipo_cet

Linhas: ~6

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo_cet | int | N |  |  |  |
| 2 | descricao | char(35) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_tipo_cet` (CLUSTERED): id_tipo_cet

**Referenciada por:**
- dbo.cad_veiculo.id_tipo_cet

## dbo.cad_uf_processo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | uf | char(2) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_uf_processo` (CLUSTERED): placa

## dbo.cad_veiculo

Linhas: ~32223

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | ano_modelo | int | S |  |  |  |
| 3 | atualizado_em | datetime | S |  |  |  |
| 4 | id_categoria | int | S |  |  |  |
| 5 | id_tipo | int | S |  |  |  |
| 6 | id_cor | int | S |  |  |  |
| 7 | id_situacao | int | S |  |  |  |
| 8 | id_localidade | int | S |  |  |  |
| 9 | id_especie | int | S |  |  |  |
| 10 | id_modelo | int | S |  |  |  |
| 11 | id_marca | int | S |  |  |  |
| 12 | id_marca_cet | int | S |  |  |  |
| 13 | id_tipo_cet | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_veiculo_ano_modelo` (NONCLUSTERED): ano_modelo
- IDX `IX_cad_veiculo_categoria_cor_tipo` (NONCLUSTERED): id_categoria, id_cor, id_tipo
- IDX `IX_cad_veiculo_especie` (NONCLUSTERED): id_especie
- IDX `IX_cad_veiculo_marca` (NONCLUSTERED): id_marca
- IDX `IX_cad_veiculo_marca_cet` (NONCLUSTERED): id_marca_cet
- PK `PK_cad_veiculo` (CLUSTERED): placa

**FKs (saída):**
- id_categoria → dbo.cad_categoria.id_categoria
- id_cor → dbo.cad_cor.id_cor
- id_especie → dbo.cad_especie.id_especie
- id_localidade → dbo.cad_localidade.id_localidade
- id_marca → dbo.cad_marca.id_marca
- id_marca_cet → dbo.cad_marca_cet.id_marca_cet
- id_modelo → dbo.cad_modelo.id_modelo
- id_situacao → dbo.cad_situacao.id_situacao
- id_tipo → dbo.cad_tipo.id_tipo
- id_tipo_cet → dbo.cad_tipo_cet.id_tipo_cet

## dbo.cad_veiculo_aux_tab

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | desc_modelo | varchar(25) | S |  |  |  |
| 3 | tipo | tinyint | N |  |  |  |
| 4 | data_registro | datetime | N |  |  |  |
| 5 | tipo_registro | char(1) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_veiculo_aux_tab_placa` (CLUSTERED): placa

## dbo.cad_veiculo_dif

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | ano_modelo | int | S |  |  |  |
| 3 | atualizado_em | datetime | S |  |  |  |
| 4 | id_categoria | int | S |  |  |  |
| 5 | id_tipo | int | S |  |  |  |
| 6 | id_cor | int | S |  |  |  |
| 7 | id_situacao | int | S |  |  |  |
| 8 | id_localidade | int | S |  |  |  |
| 9 | id_especie | int | S |  |  |  |
| 10 | id_modelo | int | S |  |  |  |
| 11 | id_marca | int | S |  |  |  |
| 12 | id_marca_cet | int | S |  |  |  |
| 13 | id_tipo_cet | int | S |  |  |  |

## dbo.cad_veiculo_info_aux

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | ano_fabricacao | int | S |  |  |  |
| 3 | renavam | varchar(11) | S |  |  |  |
| 4 | chassi | varchar(17) | S |  |  |  |
| 5 | restricao | varchar(100) | S |  |  |  |
| 6 | atualizado_em | datetime | S |  |  |  |
| 7 | tipo_combustivel | varchar(20) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_veiculo_info_aux` (CLUSTERED): placa

## dbo.cad_veiculo_monitorado

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_monitorado | int | N | S |  |  |
| 2 | placa | char(10) | N |  |  |  |
| 3 | id_situacao | int | N |  |  |  |
| 4 | descricao | varchar(100) | N |  |  |  |
| 5 | data_cadastro | datetime | N |  | (getdate()) |  |
| 6 | data_exclusao | datetime | S |  |  |  |
| 7 | id_usuario | int | N |  |  |  |
| 8 | descricao_exclusao | varchar(100) | S |  |  |  |
| 9 | id_usuario_exclusao | int | S |  |  |  |
| 10 | id_email_enviar | int | S |  |  |  |
| 11 | email_destino | varchar(255) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_cad_veiculo_monitorado_placa_veiculo_monitorado_data_exclusao` (NONCLUSTERED): placa, id_veiculo_monitorado, data_exclusao
- PK `PK_cad_veiculo_monitorado` (CLUSTERED): id_veiculo_monitorado

## dbo.cad_veiculo_proprietario

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | id_marca | int | S |  |  |  |
| 3 | marca | varchar(35) | S |  |  |  |
| 4 | id_tipo | int | S |  |  |  |
| 5 | tipo | varchar(35) | S |  |  |  |
| 6 | proprietario | varchar(100) | S |  |  |  |
| 7 | observacao | varchar(100) | S |  |  |  |
| 8 | imagem | image | S |  |  |  |
| 9 | data_atualizado | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__cad_veic__0C05742488F785D4` (CLUSTERED): placa

## dbo.cad_veiculo_renainf

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | N |  |  |  |
| 2 | atualizado_em | datetime | S |  |  |  |
| 3 | id_categoria | int | S |  |  |  |
| 4 | id_tipo | int | S |  |  |  |
| 5 | id_cor | int | S |  |  |  |
| 6 | id_localidade | int | S |  |  |  |
| 7 | id_marca | int | S |  |  |  |
| 8 | id_marca_cet | int | S |  |  |  |
| 9 | id_especie | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_cad_veiculo_renainf` (CLUSTERED): placa

**FKs (saída):**
- id_categoria → dbo.cad_categoria.id_categoria
- id_cor → dbo.cad_cor.id_cor
- id_marca → dbo.cad_marca.id_marca
- id_marca_cet → dbo.cad_marca_cet.id_marca_cet
- id_tipo → dbo.cad_tipo.id_tipo

## dbo.cad_veiculo_verificar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | varchar(7) | S |  |  |  |

