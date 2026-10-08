# Tabelas — schema `dbo` — grupo `movimento`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.movimento_arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento_arquivo | bigint | N | S |  |  |
| 2 | id_tipo | int | N |  |  |  |
| 3 | id_movimento | int | N |  |  |  |
| 4 | sequencia | int | S |  |  |  |
| 5 | indice_imagem | int | S |  |  |  |
| 6 | data_movimento | date | S |  |  |  |
| 7 | ds_caminho | varchar(255) | N |  |  |  |
| 8 | nome_arquivo | varchar(30) | N |  |  |  |
| 9 | revisao | int | S |  |  |  |
| 10 | numero_registros | int | S |  |  |  |
| 11 | data_validacao | date | S |  |  |  |
| 12 | data_arquivo | datetime | S |  |  |  |
| 13 | data_importacao | datetime | S |  |  |  |
| 14 | crc | varbinary(32) | S |  |  |  |

**Índices/Chaves:**
- IDX `movimento_arquivo_id_nome_validacao_arquivo` (NONCLUSTERED): id_movimento
- PK `PK_movimento_arquivo` (CLUSTERED): id_movimento_arquivo

**Referenciada por:**
- dbo.movimento_importacao.id_movimento_arquivo
- dbo.movimento_tarja.id_movimento_arquivo
- dbo.remessa.id_movimento_arquivo

## dbo.movimento_importacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento_arquivo | bigint | N |  |  |  |
| 2 | data_movimento | date | N |  |  |  |
| 3 | id_movimento | int | N |  |  |  |
| 4 | sequencia | int | N |  |  |  |
| 5 | id_veiculo_local | int | N |  |  |  |
| 6 | placa | char(7) | N |  |  |  |
| 7 | pais | int | N |  |  |  |
| 8 | id_marca_cet | int | N |  |  |  |
| 9 | id_especie | int | N |  |  |  |
| 10 | id_enquadramento | int | N |  |  |  |
| 11 | id_local | int | N |  |  |  |
| 12 | descricao_local | varchar(80) | N |  |  |  |
| 13 | cod_pista_prodam | int | N |  |  |  |
| 14 | data_hora | datetime | N |  |  |  |
| 15 | velocidade_constatada | int | N |  |  |  |
| 16 | velocidade_considerada | int | N |  |  |  |
| 17 | velocidade_regulamentada | int | N |  |  |  |
| 18 | pista | int | N |  |  |  |
| 19 | cod_operador | int | N |  |  |  |
| 20 | data_analise | date | N |  |  |  |
| 21 | registro_montante | int | N |  |  |  |
| 22 | consistencia | bit | N |  |  |  |
| 23 | id_inconsistencia | int | N |  |  |  |
| 24 | indice_imagem | int | N |  |  |  |
| 25 | validacao | bit | S |  |  |  |
| 26 | data_validacao | date | S |  |  |  |
| 27 | codigo_agente | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_movimento_importacao_contestacao` (NONCLUSTERED): id_movimento_arquivo, sequencia
- IDX `IX_movimento_importacao_sequencia_id_enquadramento` (NONCLUSTERED): sequencia, id_enquadramento
- PK `PK_movimento_importacao` (CLUSTERED): id_movimento, id_enquadramento, sequencia

**FKs (saída):**
- id_enquadramento → dbo.enquadramento.id_enquadramento
- id_especie → dbo.cad_especie.id_especie
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_marca_cet → dbo.cad_marca_cet.id_marca_cet
- id_movimento_arquivo → dbo.movimento_arquivo.id_movimento_arquivo

## dbo.movimento_importacao_vm

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento_arquivo | bigint | N |  |  |  |
| 2 | id_movimento | int | N |  |  |  |
| 3 | sequencia | int | N |  |  |  |
| 4 | descricao_marca | varchar(20) | N |  |  |  |
| 5 | descricao_especie | varchar(20) | N |  |  |  |
| 6 | local_trecho | int | N |  |  |  |
| 7 | descricao_trecho | varchar(80) | N |  |  |  |
| 8 | local_inicio | int | N |  |  |  |
| 9 | descricao_inicio | varchar(80) | N |  |  |  |
| 10 | data_inicio | datetime | N |  |  |  |
| 11 | velocidade_inicio | int | N |  |  |  |
| 12 | equipamento_inicio | int | N |  |  |  |
| 13 | serie_inicio | int | N |  |  |  |
| 14 | data_afericao_inicio | date | N |  |  |  |
| 15 | local_fim | int | N |  |  |  |
| 16 | descricao_fim | varchar(80) | N |  |  |  |
| 17 | data_fim | datetime | N |  |  |  |
| 18 | velocidade_fim | int | N |  |  |  |
| 19 | equipamento_fim | int | N |  |  |  |
| 20 | serie_fim | int | N |  |  |  |
| 21 | data_afericao_fim | date | N |  |  |  |
| 22 | descricao | varchar(200) | N |  |  |  |

## dbo.movimento_tarja

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento_arquivo | bigint | N |  |  |  |
| 2 | data_infracao | datetime | N |  |  |  |
| 3 | classif | varchar(10) | S |  |  |  |
| 4 | local_sentido | varchar(80) | N |  |  |  |
| 5 | codigo_equipamento | int | N |  |  |  |
| 6 | tempo_decor_verm | int | N |  |  |  |
| 7 | tempo_perm | int | N |  |  |  |
| 8 | tempo_retar | int | N |  |  |  |
| 9 | data_afericao | date | S |  |  |  |
| 10 | hora_inicio | time | S |  |  |  |
| 11 | hora_fim | time | S |  |  |  |
| 12 | id_pista | int | N |  |  |  |
| 13 | velocidade_regul | int | N |  |  |  |
| 14 | velocidade_medida | int | N |  |  |  |
| 15 | velocidade_considerada | int | N |  |  |  |
| 16 | cadastro_sp | bit | S |  |  |  |
| 17 | dia_sem | int | N |  |  |  |
| 18 | id_veiculo_local | int | N |  |  |  |
| 19 | id_enquadramento | int | N |  |  |  |
| 20 | descricao | varchar(100) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_movimento_tarja_data_infracao_codigo_equipamento_veiculo_local` (NONCLUSTERED): data_infracao

**FKs (saída):**
- id_enquadramento → dbo.enquadramento.id_enquadramento
- id_movimento_arquivo → dbo.movimento_arquivo.id_movimento_arquivo

## dbo.movimento_tipo_arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_tipo | smallint | N |  |  |  |
| 2 | descricao | varchar(20) | N |  |  |  |
| 3 | inicial | char(2) | N |  |  |  |
| 4 | extensao | char(3) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_movimento_tipo_arquivo` (CLUSTERED): id_tipo

