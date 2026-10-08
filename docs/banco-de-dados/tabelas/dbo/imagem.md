# Tabelas — schema `dbo` — grupo `imagem`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.imagem

Linhas: ~107771

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_uniq | uniqueidentifier | N |  | (newid()) |  |
| 3 | imagem | image | S |  |  |  |
| 4 | indice_imagem | int | S |  |  |  |
| 5 | ds_caminho | varchar(255) | S |  |  |  |
| 6 | imagem_removida | bit | S |  | ((0)) |  |
| 7 | imagem_inmetro | image | S |  |  |  |
| 8 | assinatura_digital | image | S |  |  |  |
| 9 | chave_publica | image | S |  |  |  |
| 10 | assinatura_valida | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_imagem_ds_caminho` (NONCLUSTERED): ds_caminho
- IDX `IX_imagem_img_removida_id` (NONCLUSTERED): imagem_removida
- IDX `IX_imagem_indice_id` (NONCLUSTERED): indice_imagem
- PK `PK_imagem` (NONCLUSTERED): id_imagem
- UNIQUE `UK_imagem_uniq` (NONCLUSTERED): id_uniq

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem

## dbo.imagem_ajuste

Linhas: ~6337

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | brilho | int | S |  |  |  |
| 3 | contraste | float | S |  |  |  |

**Índices/Chaves:**
- PK `PK_imagem_ajuste` (CLUSTERED): id_imagem

## dbo.imagem_ar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_usuario | int | S |  |  |  |
| 3 | tipo_ar | char(3) | N |  |  |  |
| 4 | imagem | image | S |  |  |  |
| 5 | data | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_imagem_ar` (NONCLUSTERED): id_infracao, tipo_ar

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.imagem_filestream

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_uniq | uniqueidentifier | N |  | (newid()) |  |
| 3 | imagem | varbinary(max) | S |  |  |  |
| 4 | indice_imagem | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK_imagem_filestream` (NONCLUSTERED): id_imagem
- UNIQUE `UK_imagem_filestream_uniq` (NONCLUSTERED): id_uniq

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem

## dbo.imagem_importacao

Linhas: ~63

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N | S |  |  |
| 2 | id_imagem_local | int | N |  |  |  |
| 3 | id_veiculo_unic | bigint | S |  |  |  |
| 4 | indice_imagem | int | N |  |  |  |
| 5 | numero | int | N |  |  |  |
| 6 | nome | char(15) | N |  |  |  |
| 7 | formato | char(5) | N |  |  |  |
| 8 | imagem | image | N |  |  |  |
| 9 | ds_caminho | varchar(255) | S |  |  |  |
| 10 | movimento_lote | int | S |  |  |  |
| 11 | sequencia | int | S |  |  |  |
| 12 | imagem_inmetro | image | S |  |  |  |
| 13 | assinatura_digital | image | S |  |  |  |
| 14 | chave_publica | image | S |  |  |  |
| 15 | assinatura_valida | bit | S |  |  |  |

**Índices/Chaves:**
- PK `PK_imagem_importacao` (CLUSTERED): id_imagem
- UNIQUE `UK_imagem_importacao_veiculo_unic_nome_numero` (NONCLUSTERED): id_veiculo_unic, nome, numero

## dbo.imagem_info

Linhas: ~107771

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_tipo_imagem | int | N |  |  |  |
| 3 | formato | char(5) | S |  |  |  |
| 4 | md5 | char(40) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_imagem_info_tipo_imagem` (NONCLUSTERED): id_tipo_imagem
- PK `PK_imagem_info` (NONCLUSTERED): id_imagem

**FKs (saída):**
- id_tipo_imagem → dbo.tipo_imagem.id_tipo_imagem

**Referenciada por:**
- dbo.exportacao_imagem_imagem.id_imagem
- dbo.imagem.id_imagem
- dbo.imagem_filestream.id_imagem
- dbo.infracao_imagem.id_imagem_obj
- dbo.infracao_imagem.id_imagem_pan
- dbo.infracao_imagem.id_imagem_pan2
- dbo.infracao_obliteracao.id_imagem
- dbo.infracao_processo.id_imagem
- dbo.infracao_processo_concluido.id_imagem
- dbo.veiculo_imagem.id_imagem
- dbo.veiculo_invalido_imagem.id_imagem
- dbo.veiculo_monitorado_imagem.id_imagem

## dbo.imagem_monitorado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem_monitorado | int | N | S |  |  |
| 2 | id_tipo_imagem | int | N |  |  |  |
| 3 | formato | char(5) | S |  |  |  |
| 4 | imagem | image | S |  |  |  |

**Índices/Chaves:**
- PK `PK_imagem_monitorado` (CLUSTERED): id_imagem_monitorado

**Referenciada por:**
- dbo.veiculo_monitorado_imagem.id_imagem

## dbo.imagem_removida

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | data_removida | datetime | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_imagem_removida_id_imagem` (NONCLUSTERED): id_imagem

## dbo.imagem_teste

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_cai | int | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | cod_pista_alternativo | int | S |  |  |  |
| 5 | id_veiculo | bigint | N |  |  |  |
| 6 | id_produto | int | N |  |  |  |
| 7 | id_imagem | int | N |  |  |  |

