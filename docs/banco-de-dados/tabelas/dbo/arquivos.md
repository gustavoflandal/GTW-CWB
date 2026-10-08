# Tabelas — schema `dbo` — grupo `arquivos`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.arquivos

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N | S |  |  |
| 2 | nome_arquivo | varchar(50) | N |  |  |  |
| 3 | estado_arquivo | int | N |  |  |  |
| 4 | data_recebimento_arquivo | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_arquivos_nome_arquivo` (NONCLUSTERED): nome_arquivo
- PK `PK_arquivos` (CLUSTERED): id_arquivo

## dbo.arquivos_cai_para_cav

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(200) | N |  |  |  |
| 2 | id_tipo | smallint | S |  |  |  |
| 3 | data_arquivo | datetime | S |  |  |  |
| 4 | data_atualizacao | datetime | S |  |  |  |
| 5 | arquivo_cai | bit | S |  |  |  |
| 6 | arquivo_cav | bit | S |  |  |  |
| 7 | data_cai | datetime | S |  |  |  |
| 8 | data_cav | datetime | S |  |  |  |
| 9 | crc_cai | varbinary(32) | S |  |  |  |
| 10 | crc_cav | varbinary(32) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_arquivos_cai_para_cav` (NONCLUSTERED): nome_arquivo

**Referenciada por:**
- dbo.arquivo_log.nome_arquivo

## dbo.arquivos_cai_para_cav_import

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(200) | N |  |  |  |
| 2 | crc_cai | varbinary(32) | S |  |  |  |

## dbo.arquivos_importados

Linhas: ~15091

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_arquivo | int | N | S |  |  |
| 2 | nome_arquivo | varchar(200) | N |  |  |  |
| 3 | data_importacao | datetime | N |  |  |  |
| 4 | id_veiculo_local_inicial | int | S |  |  |  |
| 5 | id_veiculo_local_final | int | S |  |  |  |
| 6 | numero_arquivo | int | S |  |  |  |
| 7 | data_arquivo | datetime | S |  |  |  |
| 8 | id_local | int | N |  |  |  |
| 9 | arquivo_ok | bit | S |  |  |  |
| 10 | caminho | char(256) | S |  |  |  |
| 11 | id_veiculo_inicial | bigint | S |  |  |  |
| 12 | id_veiculo_final | bigint | S |  |  |  |
| 13 | descricao_erro | varchar(300) | S |  |  |  |
| 14 | data_finalizacao | datetime | S |  |  |  |
| 15 | crc | varbinary(32) | S |  |  |  |
| 16 | data_criacao | datetime | S |  |  |  |
| 17 | data_modificacao | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_arquivos_importados_data_arquivo` (NONCLUSTERED): data_arquivo
- IDX `IX_arquivos_importados_data_importacao_data_arquivo_local` (NONCLUSTERED): data_importacao, data_arquivo, id_local
- PK `PK_arquivos_importados` (CLUSTERED): id_arquivo
- UNIQUE `UK_arquivos_importados_nome_arquivo` (NONCLUSTERED): nome_arquivo

**Referenciada por:**
- dbo.configuracao_equipamento_importacao.id_arquivo
- dbo.exportacao_trafego_arquivo.id_arquivo
- dbo.veiculo_importacao.nome_arquivo

