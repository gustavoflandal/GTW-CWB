# Tabelas — schema `dbo` — grupo `outros`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.agendamento_processamento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_processo | int | N |  |  |  |
| 3 | data_requisicao | datetime | N |  |  |  |
| 4 | id_usuario | int | N |  |  |  |
| 5 | status_agendamento | int | N |  |  |  |
| 6 | id_inconsistencia | int | S |  |  |  |
| 7 | msg_erro | nvarchar(200) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_agendamento_processamento` (CLUSTERED): id_infracao

**FKs (saída):**
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_infracao → dbo.infracao.id_infracao
- id_processo → dbo.processo.id_processo
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.alerta

Linhas: ~31

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_alerta | int | N | S |  |  |
| 2 | nome_alerta | nchar(70) | N |  |  |  |
| 3 | sql_criterio | varchar(1000) | S |  |  |  |
| 4 | bloqueante | bit | N |  | ((0)) |  |
| 5 | mensagem | varchar(150) | S |  |  |  |
| 6 | consistente | bit | N |  | ((1)) |  |
| 7 | inconsistente | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK_alerta` (CLUSTERED): id_alerta

## dbo.amostra_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo | bigint | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | serie_equipamento | int | N |  |  |  |
| 4 | cod_pista | int | N |  |  |  |
| 5 | id_enquadramento | int | N |  |  |  |
| 6 | id_imagem | int | N |  |  |  |
| 7 | score_total | int | N |  |  |  |
| 8 | tipo | char(2) | N |  |  |  |
| 9 | metrologica | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_amostra_imagem_data_metrologica` (NONCLUSTERED): data, metrologica
- PK `PK_amostra_imagem` (CLUSTERED): id_veiculo

**FKs (saída):**
- id_veiculo → dbo.veiculo.id_veiculo

## dbo.amostra_imagem_manual

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | date | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | id_pista | tinyint | N |  |  |  |
| 4 | metrologica | bit | N |  |  |  |
| 5 | id_veiculo | bigint | N |  |  |  |
| 6 | id_usuario | int | S |  |  |  |
| 7 | aplicavel | bit | S |  |  |  |
| 8 | data_criacao | datetime | S |  | (getdate()) |  |
| 9 | score_total | int | S |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_amostra_imagem_manual_veiculo` (NONCLUSTERED): id_veiculo
- PK `PK_amostra_imagem_manual` (CLUSTERED): data, id_local, id_pista, metrologica

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_veiculo → dbo.veiculo.id_veiculo

## dbo.amostragem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | tamanho_inicial | int | N |  |  |  |
| 2 | tamanho_final | int | N |  |  |  |
| 3 | id_nivel | tinyint | N |  |  |  |
| 4 | codigo | char(1) | S |  |  |  |

**FKs (saída):**
- id_nivel → dbo.amostragem_nivel.id_nivel

## dbo.amostragem_nivel

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_nivel | tinyint | N |  |  |  |
| 2 | nivel | varchar(3) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_amostragem_nivel` (CLUSTERED): id_nivel

**Referenciada por:**
- dbo.amostragem.id_nivel

## dbo.amostragem_tamanho

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | codigo | char(1) | N |  |  |  |
| 2 | tamanho_amostra | smallint | N |  |  |  |
| 3 | nqa | float | N |  |  |  |
| 4 | Ac | tinyint | N |  |  |  |
| 5 | Re | tinyint | N |  |  |  |

## dbo.anexo_email_enviar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_anexo_email_enviar | int | N | S |  |  |
| 2 | id_email_enviar | int | N |  |  |  |
| 3 | content_type | varchar(20) | N |  |  |  |
| 4 | nome_arquivo | nvarchar(255) | N |  |  |  |
| 5 | bytes_arquivo | varbinary(max) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_anexo_email_enviar` (CLUSTERED): id_anexo_email_enviar

**FKs (saída):**
- id_email_enviar → dbo.email_enviar.id_email_enviar

## dbo.chave_valor

Linhas: ~62

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | chave | nchar(50) | N |  |  |  |
| 2 | valor | nchar(100) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_chave_valor` (CLUSTERED): chave

## dbo.classe_veiculo

Linhas: ~10

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classe | char(1) | N |  |  |  |
| 2 | descricao | char(15) | S |  |  |  |
| 3 | id_classe_git | tinyint | S |  |  |  |
| 4 | id_classe_tr | char(1) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_classe_veiculo_descricao` (NONCLUSTERED): descricao
- PK `PK_classe_veiculo` (NONCLUSTERED): id_classe

**Referenciada por:**
- dbo.cad_inibicao_infracao.id_classe
- dbo.configuracao_equipamento_regra_infracao.id_classe
- dbo.veiculo.id_classe
- dbo.veiculo_estatistica.id_classe
- dbo.veiculo_importacao.id_classe
- dbo.veiculo_invalido.id_classe
- dbo.veiculo_monitorado.id_classe
- dbo.veiculo_sumarizado.id_classe
- ia.veiculo_caracteristica.id_classificacao

## dbo.classificacao_veiculo_ref

Linhas: ~5

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_classe_ref | char(1) | N |  |  |  |
| 2 | descricao | char(30) | S |  |  |  |
| 3 | referencia_ini | float | S |  |  |  |
| 4 | referencia_fim | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_classificacao_veiculo_ref` (NONCLUSTERED): id_classe_ref

## dbo.config_equip_medicao_cav

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao_equipamento_medicao | int | S |  |  |  |
| 2 | id_local | int | S |  |  |  |
| 3 | cod_pista | int | S |  |  |  |
| 4 | descricao | varchar(100) | S |  |  |  |
| 5 | data_inicio | datetime | S |  |  |  |
| 6 | id_produto | int | S |  |  |  |
| 7 | cod_pista_alternativo | int | S |  |  |  |
| 8 | cod_pista_prodam | int | S |  |  |  |
| 9 | serie_equipamento | int | S |  |  |  |
| 10 | qtde_equipamentos | int | S |  |  |  |
| 11 | id_local_principal | int | S |  |  |  |

## dbo.configuracoes_relatorios

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_configuracao | int | N | S |  |  |
| 2 | chave | varchar(50) | N |  |  |  |
| 3 | valor | varchar(255) | N |  |  |  |
| 4 | descricao | varchar(255) | S |  |  |  |
| 5 | ultima_atualizacao | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__configur__E61E1249A3090E74` (CLUSTERED): id_configuracao
- UNIQUE `UQ__configur__52ACE05A057D6316` (NONCLUSTERED): chave

## dbo.contestacao_ligacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo_contestacao | tinyint | N |  |  |  |
| 2 | decisao | tinyint | N |  |  |  |
| 3 | id_processo_contestacao_dest | tinyint | N |  |  |  |
| 4 | id_processo | tinyint | S |  |  |  |

## dbo.controle_ait

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | S |  |  |  |
| 2 | id_status | smallint | S |  |  |  |
| 3 | data_exportacao | datetime | S |  |  |  |

## dbo.controle_ar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_status | smallint | N |  |  |  |
| 3 | data_emissao | date | S |  |  |  |
| 4 | data_retorno | date | S |  |  |  |
| 5 | observacao | varchar(300) | S |  |  |  |
| 6 | tipo_ar | varchar(3) | S |  |  |  |

## dbo.correlacionamento_automatico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | placa_alvo | varchar(10) | N |  |  |  |
| 3 | placa_correlacionada | varchar(10) | N |  |  |  |
| 4 | data_cadastro | datetime | N |  |  |  |
| 5 | nivel_correlacao | varchar(50) | S |  |  |  |

## dbo.correlacionamentos_registro_fato

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_registro_fato | int | N |  |  |  |
| 3 | id_passagem_veiculo_registro_fato | uniqueidentifier | N |  |  |  |
| 4 | id_passagem_veiculo_correlacionado | uniqueidentifier | N |  |  |  |
| 5 | data | datetime | N |  | (getdate()) |  |
| 6 | idUsuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__correlac__3213E83F2F2243D2` (CLUSTERED): id

## dbo.data_hora_15min

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | hora_ini | time | N |  |  |  |
| 3 | hora_fim | time | N |  |  |  |

## dbo.descarga

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_descarga | int | N | S |  |  |
| 2 | dia_inicio | date | N |  |  |  |
| 3 | dia_fim | date | N |  |  |  |
| 4 | data_criacao | datetime | N |  |  |  |
| 5 | total_veiculos | int | N |  |  |  |
| 6 | total_imagens | int | N |  |  |  |
| 7 | total_infracoes | int | N |  |  |  |
| 8 | id_usuario | int | N |  |  |  |
| 9 | data_confirmacao | datetime | S |  |  |  |
| 10 | id_usuario_confirmacao | int | S |  |  |  |
| 11 | data_exportacao | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_descarga_data_confirmacao` (NONCLUSTERED): data_confirmacao
- IDX `IX_descarga_DataFim` (NONCLUSTERED): dia_fim, dia_inicio
- IDX `IX_descarga_DataInicio` (NONCLUSTERED): dia_inicio, dia_fim
- PK `PK_descarga` (NONCLUSTERED): id_descarga

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_usuario_confirmacao → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.veiculo_descarga.id_descarga

## dbo.descricao_pista_gst

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | GST-Pista-sentido | varchar(9) | S |  |  |  |
| 2 | GST-Pista-sentido-faixa | varchar(10) | S |  |  |  |
| 3 | faixa | smallint | S |  |  |  |
| 4 | Equipamento | varchar(7) | S |  |  |  |
| 5 | id_local | smallint | S |  |  |  |
| 6 | pista-sentido-faixa | varchar(140) | S |  |  |  |
| 7 | Referencia | varchar(65) | S |  |  |  |
| 8 | Bairro | varchar(30) | S |  |  |  |
| 9 | id_pista | int | S |  |  |  |
| 10 | nome_abreviado | varchar(30) | S |  |  |  |
| 11 | numero | int | S |  |  |  |
| 12 | sentido | varchar(20) | S |  |  |  |
| 13 | complemento | varchar(20) | S |  |  |  |
| 14 | pista_descricao | varchar(15) | S |  |  |  |
| 15 | velocidade_regulamentada | int | S |  |  |  |

## dbo.destino_email_enviar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_destino_email_enviar | int | N | S |  |  |
| 2 | id_email_enviar | int | N |  |  |  |
| 3 | endereco_email | varchar(255) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_destino_email_enviar` (CLUSTERED): id_destino_email_enviar

**FKs (saída):**
- id_email_enviar → dbo.email_enviar.id_email_enviar

## dbo.diretorio

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_diretorio | int | N | S |  |  |
| 2 | diretorio | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__diretori__C278CA25D2BDC635` (CLUSTERED): id_diretorio

## dbo.email_enviar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_email_enviar | int | N | S |  |  |
| 2 | data_criacao | datetime | N |  |  |  |
| 3 | data_envio | datetime | S |  |  |  |
| 4 | remetente | varchar(255) | N |  |  |  |
| 5 | assunto_email | nvarchar(1024) | N |  |  |  |
| 6 | corpo_email | nvarchar(max) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_email_enviar_data_criacao_data_envio` (NONCLUSTERED): data_criacao, data_envio
- PK `PK_email_enviar` (CLUSTERED): id_email_enviar

**Referenciada por:**
- dbo.anexo_email_enviar.id_email_enviar
- dbo.destino_email_enviar.id_email_enviar

## dbo.equipamento_estatico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | serie_equipamento | int | N |  |  |  |
| 2 | codigo_prodam | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_equipamento_estatico` (CLUSTERED): serie_equipamento

**Referenciada por:**
- dbo.veiculo_importacao_estatico.serie_equipamento

## dbo.faixa_velocidade

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_faixa_velocidade | int | N |  |  |  |
| 2 | descricao | varchar(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_faixa_velocidade` (CLUSTERED): id_faixa_velocidade

**Referenciada por:**
- dbo.veiculo_sumarizado.id_faixa_velocidade

## dbo.faixa_velocidade_relatorio_rj

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_faixa_velocidade | int | N |  |  |  |
| 2 | descricao | varchar(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_faixa_velocidade_relatorio_rj` (CLUSTERED): id_faixa_velocidade

## dbo.falha_arquivos_importados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | numero_arquivo_antes | int | N |  |  |  |
| 3 | numero_arquivo_depois | int | N |  |  |  |
| 4 | data_arquivo_antes | datetime | N |  |  |  |
| 5 | data_arquivo_depois | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK_falha_arquivos_importados` (CLUSTERED): id_local, numero_arquivo_antes, numero_arquivo_depois, data_arquivo_antes

## dbo.falha_sequencia_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ano | int | N |  |  |  |
| 2 | mes | int | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | id_imagem_local_antes | int | N |  |  |  |
| 5 | id_imagem_local_depois | int | N |  |  |  |
| 6 | data_imagem_antes | datetime | N |  |  |  |
| 7 | data_imagem_depois | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IC_falha_sequencia_imagem_data_imagem_antes_local_imagem_local_antes` (CLUSTERED): data_imagem_antes, id_local, id_imagem_local_antes

## dbo.FileTable

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | stream_id | uniqueidentifier | N |  | (newsequentialid()) |  |
| 2 | file_stream | varbinary(max) | S |  |  |  |
| 3 | name | nvarchar(255) | N |  |  |  |
| 4 | path_locator | hierarchyid | N |  | (convert(hierarchyid, '/' +     convert(varchar(20), convert(bigint, substring(convert(binary(16), newid()), 1, 6))) + '.' +     convert(varchar(20), convert(bigint, substring(convert(binary(16), newid()), 7, 6))) + '.' +     convert(varchar(20), convert(b |  |
| 5 | parent_path_locator | hierarchyid | S |  |  |  |
| 6 | file_type | nvarchar(255) | S |  |  |  |
| 7 | cached_file_size | bigint | S |  |  |  |
| 8 | creation_time | datetimeoffset | N |  | (sysdatetimeoffset()) |  |
| 9 | last_write_time | datetimeoffset | N |  | (sysdatetimeoffset()) |  |
| 10 | last_access_time | datetimeoffset | S |  | (sysdatetimeoffset()) |  |
| 11 | is_directory | bit | N |  | ((0)) |  |
| 12 | is_offline | bit | N |  | ((0)) |  |
| 13 | is_hidden | bit | N |  | ((0)) |  |
| 14 | is_readonly | bit | N |  | ((0)) |  |
| 15 | is_archive | bit | N |  | ((1)) |  |
| 16 | is_system | bit | N |  | ((0)) |  |
| 17 | is_temporary | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__FileTabl__5A5B77D5A6C4D576` (NONCLUSTERED): path_locator
- UNIQUE `UQ__FileTabl__9DD95BAFA80C058E` (NONCLUSTERED): stream_id
- UNIQUE `UQ__FileTabl__A236CBB37B8F291E` (NONCLUSTERED): parent_path_locator, name

**FKs (saída):**
- parent_path_locator → dbo.FileTable.path_locator

**Referenciada por:**
- dbo.FileTable.parent_path_locator

## dbo.filtro

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_filtro | int | N | S |  |  |
| 2 | nome_filtro | nchar(50) | N |  |  |  |
| 3 | id_enquadramento | int | S |  |  |  |
| 4 | id_processo | int | S |  |  |  |
| 5 | id_local | int | S |  |  |  |
| 6 | id_classe | char(1) | S |  |  |  |
| 7 | data_ini | datetime | S |  |  |  |
| 8 | data_fim | datetime | S |  |  |  |
| 9 | sql_criterio | varchar(1000) | S |  |  |  |
| 10 | set_id_inconsistencia | int | S |  |  |  |
| 11 | set_espera | bit | S |  |  |  |
| 12 | data_validade | datetime | S |  |  |  |
| 13 | prioridade | int | N |  | ((0)) |  |
| 14 | data_modificacao | datetime | S |  | (getdate()) |  |
| 15 | id_usuario | int | S |  |  |  |
| 16 | id_pista | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_filtro` (CLUSTERED): id_filtro

**FKs (saída):**
- id_processo → dbo.processo.id_processo
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.cad_inibicao_infracao.id_filtro_relacionado
- dbo.infracao_processo_filtro.id_filtro

## dbo.filtro_bkp

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_filtro | int | N | S |  |  |
| 2 | nome_filtro | nchar(50) | N |  |  |  |
| 3 | id_enquadramento | int | S |  |  |  |
| 4 | id_processo | int | S |  |  |  |
| 5 | id_local | int | S |  |  |  |
| 6 | id_classe | char(1) | S |  |  |  |
| 7 | data_ini | datetime | S |  |  |  |
| 8 | data_fim | datetime | S |  |  |  |
| 9 | sql_criterio | varchar(1000) | S |  |  |  |
| 10 | set_id_inconsistencia | int | S |  |  |  |
| 11 | set_espera | bit | S |  |  |  |
| 12 | data_validade | datetime | S |  |  |  |
| 13 | prioridade | int | N |  |  |  |
| 14 | data_modificacao | datetime | S |  |  |  |
| 15 | id_usuario | int | S |  |  |  |
| 16 | id_pista | int | S |  |  |  |

## dbo.gera_remessa_automatico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa_automatico | int | N | S |  |  |
| 2 | data_solicitacao | datetime | N |  | (getdate()) |  |
| 3 | data_remessa | datetime | N |  |  |  |
| 4 | data_inicial | datetime | S |  |  |  |
| 5 | data_final | datetime | S |  |  |  |
| 6 | infracoes_por_lote | int | S |  | ((0)) |  |
| 7 | total_infracoes | int | S |  | ((0)) |  |
| 8 | qtde_aprox_lotes | int | S |  |  |  |
| 9 | id_usuario | int | N |  |  |  |
| 10 | flag_geracao | int | N |  | ((0)) |  |
| 11 | data_geracao | datetime | S |  |  |  |
| 12 | flag_exportacao | int | N |  | ((0)) |  |
| 13 | data_exportacao | datetime | S |  |  |  |
| 14 | flag_exportacao_hom | int | S |  | ((0)) |  |
| 15 | data_exportacao_hom | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_gera_remessa_automatico` (NONCLUSTERED): id_remessa_automatico

**Referenciada por:**
- dbo.gera_remessa_automatico_log.id_remessa_automatico
- dbo.remessa.id_remessa_automatico

## dbo.gera_remessa_automatico_log

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa_automatico_log | int | N | S |  |  |
| 2 | id_remessa_automatico | int | N |  |  |  |
| 3 | id_remessa | int | S |  |  |  |
| 4 | id_status | int | S |  |  |  |
| 5 | id_tipo | int | S |  |  |  |
| 6 | id_log_processos | int | S |  |  |  |
| 7 | mensagem | varchar(300) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_gera_remessa_automatico_log` (NONCLUSTERED): id_remessa_automatico_log

**FKs (saída):**
- id_remessa_automatico → dbo.gera_remessa_automatico.id_remessa_automatico

## dbo.gerencia_contrato_alerta

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_gerencia_contrato_alerta | int | N | S |  |  |
| 2 | serie_equipamento | int | N |  |  |  |
| 3 | id_pista | int | S |  |  |  |
| 4 | id_alerta | int | N |  |  |  |
| 5 | valor | nchar(50) | N |  |  |  |
| 6 | detalhe | nchar(500) | S |  |  |  |
| 7 | data_inclusao | datetime | N |  | (getdate()) |  |
| 8 | ativo | bit | N |  | ((1)) |  |
| 9 | alerta | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK_gerencia_contrato_alerta` (CLUSTERED): id_gerencia_contrato_alerta

**FKs (saída):**
- id_alerta → dbo.gerencia_contrato_cad_alerta.id_alerta

## dbo.gerencia_contrato_cad_alerta

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_alerta | int | N |  |  |  |
| 2 | nome | nchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_gerencia_contrato_cad_alerta` (CLUSTERED): id_alerta

**Referenciada por:**
- dbo.gerencia_contrato_alerta.id_alerta

## dbo.grupo_equipamento

Linhas: ~2

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_grupo_equipamento | int | N |  |  |  |
| 2 | nome | char(25) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_grupo_equipamento` (NONCLUSTERED): id_grupo_equipamento

**Referenciada por:**
- dbo.configuracao_equipamento.id_grupo_equipamento
- dbo.sis_usuario.id_grupo_equipamento

## dbo.hora

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | hora | int | N |  |  |  |
| 2 | hora_desc | varchar(15) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_hora` (CLUSTERED): hora

## dbo.imagens_sinalizacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_entrada | datetime | N |  |  |  |
| 4 | data_imagens | datetime | N |  |  |  |
| 5 | caminho_arquivo | varchar(300) | N |  |  |  |
| 6 | id_usuario_upload | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__imagens___F5CD27A22ECAD244` (CLUSTERED): id_arquivo

## dbo.indicadores_estatisticas_tempo_real

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | recebido | int | S |  |  |  |
| 2 | enviado | int | S |  |  |  |
| 3 | id | bigint | S |  |  |  |

## dbo.indicadores_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | cod_pista_prodam | int | S |  |  |  |
| 3 | serie_equipamento | int | S |  |  |  |
| 4 | faixa | tinyint | N |  |  |  |
| 5 | codigo_pista | int | S |  |  |  |
| 6 | local | char(100) | S |  |  |  |
| 7 | data_publicacao | datetime | S |  |  |  |
| 8 | flag_funcionamento | int | S |  |  |  |
| 9 | mes | int | S |  |  |  |
| 10 | ano | int | S |  |  |  |
| 11 | data_inicio_dados | date | S |  |  |  |
| 12 | data_fim_dados | date | S |  |  |  |
| 13 | 1 | int | S |  |  |  |
| 14 | 2 | int | S |  |  |  |
| 15 | 3 | int | S |  |  |  |
| 16 | 4 | int | S |  |  |  |
| 17 | 5 | int | S |  |  |  |
| 18 | 6 | int | S |  |  |  |
| 19 | 7 | int | S |  |  |  |
| 20 | 8 | int | S |  |  |  |
| 21 | 9 | int | S |  |  |  |
| 22 | 10 | int | S |  |  |  |
| 23 | 11 | int | S |  |  |  |
| 24 | 12 | int | S |  |  |  |
| 25 | 13 | int | S |  |  |  |
| 26 | 14 | int | S |  |  |  |
| 27 | 15 | int | S |  |  |  |
| 28 | 16 | int | S |  |  |  |
| 29 | 17 | int | S |  |  |  |
| 30 | 18 | int | S |  |  |  |
| 31 | 19 | int | S |  |  |  |
| 32 | 20 | int | S |  |  |  |
| 33 | 21 | int | S |  |  |  |
| 34 | 22 | int | S |  |  |  |
| 35 | 23 | int | S |  |  |  |
| 36 | 24 | int | S |  |  |  |
| 37 | 25 | int | S |  |  |  |
| 38 | 26 | int | S |  |  |  |
| 39 | 27 | int | S |  |  |  |
| 40 | 28 | int | S |  |  |  |
| 41 | 29 | int | S |  |  |  |
| 42 | 30 | int | S |  |  |  |
| 43 | 31 | int | S |  |  |  |

## dbo.laudo_afericao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_entrada | datetime | N |  |  |  |
| 4 | nome_arquivo | varchar(100) | N |  |  |  |
| 5 | extensao | varchar(5) | N |  |  |  |
| 6 | tipo | varchar(50) | S |  |  |  |
| 7 | dados | varchar(max) | S |  |  |  |
| 8 | data_afericao | datetime | S |  |  |  |
| 9 | numero_laudo | int | S |  |  |  |
| 10 | numero_cert | int | S |  |  |  |
| 11 | sequencia_local | int | S |  |  |  |

## dbo.lista_arquivos_dt

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | char(32) | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | sequencia_arquivo | int | N |  |  |  |
| 4 | data_arquivo | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__lista_ar__5ACBFC960BBBE24D` (CLUSTERED): nome_arquivo

## dbo.local

Linhas: ~470

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  | ((1)) |  |
| 3 | id_contrato_cliente | int | N |  | ((1)) |  |
| 4 | codigo_local_cliente | char(10) | S |  |  |  |
| 5 | nome | char(100) | N |  |  |  |
| 6 | data_atualizacao | datetime | N |  |  |  |
| 7 | id_configuracao_equipamento | int | N |  |  |  |
| 8 | tipo | char(1) | S |  |  |  |
| 9 | id_localidade | int | S |  |  |  |
| 10 | posicao_lat | decimal(19,17) | S |  |  |  |
| 11 | posicao_lon | decimal(19,17) | S |  |  |  |
| 12 | cep | int | S |  |  |  |
| 13 | complemento | varchar(35) | S |  |  |  |
| 14 | dataEnsaioNaoMetrol | datetime | S |  |  |  |
| 15 | ip_gpw | varchar(40) | S |  |  |  |
| 16 | ip_kistler | varchar(40) | S |  |  |  |
| 17 | ip_msi | varchar(40) | S |  |  |  |
| 18 | modo_simulacao | bit | S |  |  |  |
| 19 | pista_pesagem | int | S |  |  |  |
| 20 | porta_socket | int | S |  |  |  |
| 21 | porta_ws | int | S |  |  |  |
| 22 | porta_balanca_precisao | int | S |  |  |  |
| 23 | sai | int | S |  |  |  |
| 24 | tipo_funcionamento | int | S |  |  |  |
| 25 | localidade_desc | varchar(50) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_local_configuracao_equipamento` (NONCLUSTERED): id_configuracao_equipamento
- IDX `IX_local_data_atualizacao_sequencia_local` (NONCLUSTERED): data_atualizacao
- IDX `IX_local_local_configuracao_equipamento` (NONCLUSTERED): id_local, id_configuracao_equipamento
- PK `PK_local` (CLUSTERED): id_local, sequencia_local

**FKs (saída):**
- id_localidade → dbo.cad_localidade.id_localidade
- id_configuracao_equipamento → dbo.configuracao_equipamento.id_configuracao_equipamento

**Referenciada por:**
- dbo.infracao.id_local
- dbo.infracao.sequencia_local
- dbo.status_conexao.id_local
- dbo.status_conexao.sequencia_local
- dbo.status_div.sequencia_local
- dbo.status_div.id_local
- dbo.status_energia.id_local
- dbo.status_energia.sequencia_local
- dbo.veiculo.sequencia_local
- dbo.veiculo.id_local
- dbo.veiculo_invalido.id_local
- dbo.veiculo_invalido.sequencia_local
- dbo.veiculo_monitorado.sequencia_local
- dbo.veiculo_monitorado.id_local
- dbo.veiculo_sumarizado.id_local
- dbo.veiculo_sumarizado.sequencia_local

## dbo.local_municipio_regiao

Linhas: ~44

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | id_localidade | int | S |  |  |  |
| 3 | id_regiao | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_local_municipio_regiao_id_local` (NONCLUSTERED): id_local

## dbo.local_pista_croqui

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | pista | tinyint | N |  |  |  |
| 3 | imagem | image | N |  |  |  |

**Índices/Chaves:**
- PK `PK_local_pista_croqui` (CLUSTERED): id_local, pista

## dbo.LogProcessamentoScript

Linhas: ~4435

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | IdLog | int | N | S |  |  |
| 2 | DataHora | datetime | S |  | (getdate()) |  |
| 3 | Mensagem | nvarchar(1000) | S |  |  |  |
| 4 | RegistrosAfetados | int | S |  |  |  |
| 5 | Erro | bit | S |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__LogProce__0C54DBC69B9F210F` (CLUSTERED): IdLog

## dbo.lote_reprovado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | S |  |  |  |
| 2 | id_remessa_cav | int | S |  |  |  |
| 3 | tipo | char(2) | S |  |  |  |
| 4 | codigo_externo | int | S |  |  |  |
| 5 | data_inicial | datetime | S |  |  |  |
| 6 | revisao | int | S |  |  |  |
| 7 | data_processo | datetime | S |  |  |  |
| 8 | data_atualizacao | datetime | N |  | (getdate()) |  |
| 9 | mensagem | varchar(100) | S |  |  |  |
| 10 | ativo | bit | N |  | ((1)) |  |

## dbo.lote_reprovado_detalhe

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | S |  |  |  |
| 2 | tipo | char(2) | S |  |  |  |
| 3 | codigo_externo | int | S |  |  |  |
| 4 | sequencia | int | S |  |  |  |
| 5 | placa_cai | char(7) | S |  |  |  |
| 6 | placa_cav | char(7) | S |  |  |  |
| 7 | id_inconsistencia_cai | int | S |  |  |  |
| 8 | id_inconsistencia_cav | int | S |  |  |  |
| 9 | id_marca_cet_cai | int | S |  |  |  |
| 10 | id_marca_cet_cav | int | S |  |  |  |
| 11 | erro_obliteracao | bit | S |  |  |  |
| 12 | cod_agente | int | S |  |  |  |

## dbo.medicao_historico_geracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_medicao_historico_geracao | int | N | S |  |  |
| 2 | tipo_relatorio | char(2) | N |  |  |  |
| 3 | nome_relatorio | varchar(120) | N |  |  |  |
| 4 | data_inicio | date | S |  |  |  |
| 5 | data_fim | date | S |  |  |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_geracao | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK_medicao_historico_geracao` (NONCLUSTERED): id_medicao_historico_geracao

## dbo.modem3g

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ICCID | nchar(25) | N |  |  |  |
| 2 | operadora | nchar(15) | N |  |  |  |
| 3 | MSISDN | nchar(10) | N |  |  |  |
| 4 | localizacao | nchar(30) | S |  |  |  |

## dbo.mosaicoVideos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | idMosaico | int | N | S |  |  |
| 2 | idLocal | int | S |  |  |  |
| 3 | descEquipamento | varchar(200) | N |  |  |  |
| 4 | ativo | int | N |  |  |  |
| 5 | frameRate | int | N |  |  |  |
| 6 | qualidade | int | N |  |  |  |
| 7 | ip | varchar(50) | N |  |  |  |
| 8 | width | int | N |  |  |  |
| 9 | height | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__mosaicoV__045560867A8A1906` (CLUSTERED): idMosaico

## dbo.movimentos_erro

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | tipo | char(2) | N |  |  |  |
| 2 | id_movimento | int | N |  |  |  |
| 3 | id_remessa | int | S |  |  |  |

## dbo.ocorrencia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_ocorrencia | int | N | S |  |  |
| 2 | id_local | int | S |  |  |  |
| 3 | serie_equipamento | int | S |  |  |  |
| 4 | data_hora | datetime | S |  |  |  |
| 5 | cod_pista | int | S |  |  |  |
| 6 | motivo_resumido | varchar(80) | S |  |  |  |
| 7 | nome_arquivo | varchar(100) | S |  |  |  |
| 8 | caminho_arquivo | varchar(200) | S |  |  |  |
| 9 | usuario_cadastro | varchar(25) | S |  |  |  |
| 10 | data_cadastro | datetime | S |  |  |  |
| 11 | tipo_mime | varchar(80) | S |  |  |  |
| 12 | extensao_arquivo | varchar(10) | S |  |  |  |
| 13 | numero_oficio | int | S |  |  |  |
| 14 | ano_oficio | smallint | S |  |  |  |
| 15 | estado | varchar(20) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ocorrencia` (NONCLUSTERED): id_ocorrencia

## dbo.percurso

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_percurso | int | N | S |  |  |
| 2 | nome_percurso | varchar(80) | S |  |  |  |
| 3 | velocidade_media_regulamentada | int | S |  |  |  |
| 4 | id_tipo_percurso | int | S |  |  |  |
| 5 | codigo_prodam_percurso | int | S |  |  |  |
| 6 | id_local_origem | int | S |  |  |  |
| 7 | id_local_destino | int | S |  |  |  |
| 8 | codigo_prodam_origem | int | S |  |  |  |
| 9 | codigo_prodam_destino | int | S |  |  |  |
| 10 | descricao_origem | varchar(80) | S |  |  |  |
| 11 | descricao_destino | varchar(80) | S |  |  |  |
| 12 | codigo_equipamento_origem | int | S |  |  |  |
| 13 | codigo_equipamento_destino | int | S |  |  |  |
| 14 | serie_equipamento_origem | int | S |  |  |  |
| 15 | serie_equipamento_destino | int | S |  |  |  |
| 16 | data_afericao_origem | date | S |  |  |  |
| 17 | data_afericao_destino | date | S |  |  |  |
| 18 | distancia | float | S |  |  |  |
| 19 | data_gravacao | datetime | S |  | (getdate()) |  |
| 20 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_percurso` (CLUSTERED): id_percurso

**FKs (saída):**
- id_tipo_percurso → dbo.tipo_percurso.id_tipo_percurso

## dbo.percurso_aux

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_percurso | int | N |  |  |  |
| 2 | id_pista_orig | int | S |  |  |  |
| 3 | id_pista_dest | int | S |  |  |  |
| 4 | codigo_equipamento_orig | int | S |  |  |  |
| 5 | codigo_equipamento_dest | int | S |  |  |  |

## dbo.perfil

Linhas: ~131246

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pista | tinyint | N |  |  |  |
| 3 | sensor | int | N |  |  |  |
| 4 | quantidade_amostras | int | S |  |  |  |
| 5 | tamanho_amostra | numeric(15,3) | S |  |  |  |
| 6 | inicio_disparo | int | S |  |  |  |
| 7 | final_disparo | int | S |  |  |  |
| 8 | perfil | image | S |  |  |  |
| 9 | id_uniq | uniqueidentifier | N |  | (newid()) |  |

**Índices/Chaves:**
- PK `PK_perfil` (CLUSTERED): id_veiculo_unic, pista, sensor

**FKs (saída):**
- id_veiculo_unic → dbo.veiculo.id_veiculo_unic

## dbo.perfil_importacao

Linhas: ~56

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pista | tinyint | N |  |  |  |
| 3 | sensor | int | N |  |  |  |
| 4 | quantidade_amostras | int | S |  |  |  |
| 5 | tamanho_amostra | numeric(15,3) | S |  |  |  |
| 6 | inicio_disparo | int | S |  |  |  |
| 7 | final_disparo | int | S |  |  |  |
| 8 | perfil | image | S |  |  |  |

**Índices/Chaves:**
- PK `PK_perfil_importacao` (CLUSTERED): id_veiculo_unic, pista, sensor

## dbo.pistas_transversais

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | id_pista | tinyint | N |  |  |  |

## dbo.placa_irregular

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_placa_irregular | int | N |  |  |  |
| 2 | placa | char(7) | N |  |  |  |
| 3 | id_situacao_placa_irregular | int | N |  |  |  |
| 4 | descricao | char(30) | S |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data | datetime | N |  |  |  |
| 7 | data_exclusao | datetime | S |  |  |  |
| 8 | descricao_exclusao | char(30) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_placa_irregular` (CLUSTERED): id_placa_irregular

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario
- id_situacao_placa_irregular → dbo.situacao_placa_irregular.id_situacao_placa_irregular

## dbo.placas_mg

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | varchar(7) | S |  |  |  |

## dbo.produto

Linhas: ~14

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_produto | int | N |  |  |  |
| 2 | descricao | char(50) | N |  |  |  |
| 3 | modelo | char(20) | S |  |  |  |
| 4 | sigla | char(3) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_produto` (NONCLUSTERED): id_produto

**Referenciada por:**
- dbo.configuracao_equipamento.id_produto

## dbo.ptz_configuracao_operacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_ptz_configuracao_operacao | int | N | S |  |  |
| 2 | id_ptz_modo_operacao | int | N |  |  |  |
| 3 | data_inicio | datetime | N |  |  |  |
| 4 | data_fim | datetime | S |  |  |  |
| 5 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ptz_configuracao_operacao` (CLUSTERED): id_ptz_configuracao_operacao

**FKs (saída):**
- id_ptz_modo_operacao → dbo.ptz_modo_operacao.id_ptz_modo_operacao
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.ptz_controle_posicoes

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_posicao | int | N | S |  |  |
| 2 | posicao | int | N |  |  |  |
| 3 | pos0 | float | N |  |  |  |
| 4 | pos1 | float | N |  |  |  |
| 5 | pos2 | float | N |  |  |  |

**Índices/Chaves:**
- PK `PK_ptz_controle_posicoes` (NONCLUSTERED): id_posicao

## dbo.ptz_modo_operacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_ptz_modo_operacao | int | N |  |  |  |
| 2 | descricao | varchar(60) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_ptz_modo_operacao` (CLUSTERED): id_ptz_modo_operacao

**Referenciada por:**
- dbo.ptz_configuracao_operacao.id_ptz_modo_operacao

## dbo.regra_importacao_veiculo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_regra | int | N |  |  |  |
| 2 | id_veiculo_unic | bigint | N |  |  |  |
| 3 | data | datetime | N |  | (getdate()) |  |

## dbo.regras_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_regra | int | N | S |  |  |
| 2 | data | date | S |  |  |  |
| 3 | dia_semana | int | S |  |  |  |
| 4 | hora_ini | time | S |  |  |  |
| 5 | hora_fim | time | S |  |  |  |
| 6 | atraso | int | N |  |  |  |
| 7 | data_cadastro | datetime | N |  | (getdate()) |  |

## dbo.reindex

Linhas: ~9

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | tipo | nchar(10) | N |  |  |  |
| 2 | script_reindex | nvarchar(300) | N |  |  |  |
| 3 | data_atualizacao | datetime | N |  |  |  |

## dbo.rel_fragmentacao_indices

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | indice | varchar(200) | S |  |  |  |
| 2 | tabela | varchar(120) | S |  |  |  |
| 3 | fragmentacao | float | S |  |  |  |
| 4 | fragmentacao_cont | bigint | S |  |  |  |
| 5 | tipo | varchar(10) | S |  |  |  |

## dbo.relatorio_atraso_dt

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dia | date | S |  |  |  |
| 2 | DT_30_seg | int | S |  |  |  |
| 3 | DT_1_min | int | S |  |  |  |
| 4 | DT_10_min | int | S |  |  |  |
| 5 | DT_1_hor | int | S |  |  |  |
| 6 | DT_1_dia | int | S |  |  |  |
| 7 | DT_mais_1_dia | int | S |  |  |  |

## dbo.relatorio_edital_rj

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_relatorio_edital_rj | int | N |  |  |  |
| 2 | id_tipo_relatorio_edital_rj | int | N |  |  |  |
| 3 | nome_relatorio | nvarchar(120) | N |  |  |  |
| 4 | filtro_por_local | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK_relatorio_edital_rj` (CLUSTERED): id_relatorio_edital_rj, id_tipo_relatorio_edital_rj

## dbo.relatorio_infracoes_consistentes

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | mes | int | S |  |  |  |
| 2 | ano | int | S |  |  |  |
| 3 | cod_pista | int | S |  |  |  |
| 4 | descricao | char(100) | S |  |  |  |
| 5 | dia_1 | int | S |  |  |  |
| 6 | dia_2 | int | S |  |  |  |
| 7 | dia_3 | int | S |  |  |  |
| 8 | dia_4 | int | S |  |  |  |
| 9 | dia_5 | int | S |  |  |  |
| 10 | dia_6 | int | S |  |  |  |
| 11 | dia_7 | int | S |  |  |  |
| 12 | dia_8 | int | S |  |  |  |
| 13 | dia_9 | int | S |  |  |  |
| 14 | dia_10 | int | S |  |  |  |
| 15 | dia_11 | int | S |  |  |  |
| 16 | dia_12 | int | S |  |  |  |
| 17 | dia_13 | int | S |  |  |  |
| 18 | dia_14 | int | S |  |  |  |
| 19 | dia_15 | int | S |  |  |  |
| 20 | dia_16 | int | S |  |  |  |
| 21 | dia_17 | int | S |  |  |  |
| 22 | dia_18 | int | S |  |  |  |
| 23 | dia_19 | int | S |  |  |  |
| 24 | dia_20 | int | S |  |  |  |
| 25 | dia_21 | int | S |  |  |  |
| 26 | dia_22 | int | S |  |  |  |
| 27 | dia_23 | int | S |  |  |  |
| 28 | dia_24 | int | S |  |  |  |
| 29 | dia_25 | int | S |  |  |  |
| 30 | dia_26 | int | S |  |  |  |
| 31 | dia_27 | int | S |  |  |  |
| 32 | dia_28 | int | S |  |  |  |
| 33 | dia_29 | int | S |  |  |  |
| 34 | dia_30 | int | S |  |  |  |
| 35 | dia_31 | int | S |  |  |  |
| 36 | total_geral | int | S |  |  |  |

## dbo.sentido_tipo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | sentido | varchar(30) | N |  |  |  |
| 2 | tipo | char(1) | N |  |  |  |

## dbo.sistemas_consilux

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N |  |  |  |
| 2 | descricao | varchar(300) | N |  |  |  |
| 3 | descricao_detalhes | varchar(1000) | N |  |  |  |
| 4 | mostrar_painel | bit | N |  |  |  |
| 5 | url_externa | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sistemas__3213E83F917D1097` (CLUSTERED): id

## dbo.situacao_placa_irregular

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_situacao_placa_irregular | int | N |  |  |  |
| 2 | descricao | char(30) | N |  |  |  |
| 3 | com_confirmacao | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK_situacao_placa_irregular` (CLUSTERED): id_situacao_placa_irregular

**Referenciada por:**
- dbo.placa_irregular.id_situacao_placa_irregular

## dbo.solicitacao_auditoria

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_solicitacao_auditoria | int | N | S |  |  |
| 2 | data_imagens | date | N |  |  |  |
| 3 | data_geracao | datetime | N |  | (getdate()) |  |
| 4 | total_consistentes | int | N |  | ((0)) |  |
| 5 | total_inconsistentes | int | N |  | ((0)) |  |
| 6 | total_imagens | int | N |  | ((0)) |  |
| 7 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_solicitacao_auditoria` (CLUSTERED): id_solicitacao_auditoria

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.solicitacao_auditoria_infracao.id_solicitacao_auditoria

## dbo.solicitacao_auditoria_infracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_solicitacao_auditoria | int | N |  |  |  |
| 2 | id_infracao | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_solicitacao_auditoria_infracao_infracao` (NONCLUSTERED): id_infracao
- PK `PK_solicitacao_auditoria_infracao` (CLUSTERED): id_solicitacao_auditoria, id_infracao

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_solicitacao_auditoria → dbo.solicitacao_auditoria.id_solicitacao_auditoria

## dbo.sysdiagrams

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | name | sysname | N |  |  |  |
| 2 | principal_id | int | N |  |  |  |
| 3 | diagram_id | int | N | S |  |  |
| 4 | version | int | S |  |  |  |
| 5 | definition | varbinary(max) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__sysdiagr__C2B05B618CE06D57` (CLUSTERED): diagram_id
- UNIQUE `UK_principal_name` (NONCLUSTERED): principal_id, name

## dbo.tamanho_base_historico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | varchar(128) | N |  |  |  |
| 3 | nome_fisico | varchar(2000) | N |  |  |  |
| 4 | tamanho | int | N |  |  |  |
| 5 | dblog | bit | N |  |  |  |
| 6 | data | datetime | N |  |  |  |

**Índices/Chaves:**
- PK `PK__tamanho___3213E83FFD3BD1E0` (CLUSTERED): id

## dbo.tempo_processamento

Linhas: ~46774

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tempo_processamento | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | data_inicio_cliente | datetime | N |  |  |  |
| 4 | tempo_gasto | int | N |  |  |  |
| 5 | classificacao | int | N |  |  |  |
| 6 | identificador | int | S |  |  |  |
| 7 | sub_identificador | char(50) | S |  |  |  |
| 8 | data_servidor | datetime | S |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK_tempo_processamento` (CLUSTERED): id_tempo_processamento

**FKs (saída):**
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.teste_busca_remessa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa | int | N |  |  |  |
| 2 | codigo_externo | char(20) | S |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | total_infracao | int | S |  |  |  |
| 5 | auto_inicial | int | S |  |  |  |
| 6 | auto_final | int | S |  |  |  |
| 7 | id_enquadramento | int | S |  |  |  |
| 8 | id_processo | int | S |  |  |  |
| 9 | tipo | varchar(5) | S |  |  |  |
| 10 | revisao | int | N |  |  |  |
| 11 | data_exportacao | datetime | S |  |  |  |
| 12 | data_confirmacao | datetime | S |  |  |  |
| 13 | id_usuario | int | S |  |  |  |
| 14 | data_validacao | datetime | S |  |  |  |
| 15 | data_inicial | datetime | N |  |  |  |
| 16 | data_final | datetime | N |  |  |  |
| 17 | id_movimento_arquivo | bigint | S |  |  |  |
| 18 | amostra | int | N |  |  |  |
| 19 | infracoes_validaveis | int | S |  |  |  |
| 20 | qtde | int | S |  |  |  |
| 21 | nome_usuario_janela | char(60) | S |  |  |  |
| 22 | total_real_amostra | int | N |  |  |  |
| 23 | validaveis_real_amostra | int | N |  |  |  |

## dbo.tmp_imagens_exportar_v2

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | imagem | image | S |  |  |  |
| 2 | nome_arquivo | varchar(120) | S |  |  |  |

## dbo.versao_config_equip_app

Linhas: ~1

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | versao | int | N |  |  |  |
| 2 | data_atualizacao | datetime | S |  |  |  |
| 3 | descricao_atualizacao | varchar(500) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__versao_c__4BD7D02A42A3B59A` (CLUSTERED): versao

## dbo.videos_fis

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N | S |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data_entrada | datetime | N |  |  |  |
| 4 | data_video | datetime | N |  |  |  |
| 5 | nome_arquivo | varchar(100) | N |  |  |  |
| 6 | tipo | varchar(50) | S |  |  |  |
| 7 | caminho_arquivo | varchar(300) | S |  |  |  |
| 8 | id_usuario_upload | int | S |  |  |  |
| 9 | id_usuario_valida | int | S |  |  |  |
| 10 | data_valida | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__videos_f__F5CD27A261E9FE62` (CLUSTERED): id_arquivo

