# Tabelas — schema `dbo` — grupo `exportacao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.exportacao_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_exportacao_imagem | int | N | S |  |  |
| 2 | data_criacao | datetime | N |  |  |  |
| 3 | total_imagens | int | S |  |  |  |
| 4 | data_exportacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_exportacao_imagem` (CLUSTERED): id_exportacao_imagem

**Referenciada por:**
- dbo.exportacao_imagem_imagem.id_exportacao_imagem

## dbo.exportacao_imagem_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_exportacao_imagem | int | N |  |  |  |
| 2 | id_imagem | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_exportacao_imagem_imagem` (CLUSTERED): id_exportacao_imagem, id_imagem

**FKs (saída):**
- id_exportacao_imagem → dbo.exportacao_imagem.id_exportacao_imagem
- id_imagem → dbo.imagem_info.id_imagem

## dbo.exportacao_trafego

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_exportacao_trafego | int | N | S |  |  |
| 2 | data_criacao | datetime | N |  |  |  |
| 3 | total_trafego | int | S |  |  |  |
| 4 | data_exportacao | datetime | S |  |  |  |
| 5 | data_trafego | date | N |  |  |  |

**Índices/Chaves:**
- PK `PK_exportacao_trafego` (CLUSTERED): id_exportacao_trafego

**Referenciada por:**
- dbo.exportacao_trafego_arquivo.id_exportacao_trafego

## dbo.exportacao_trafego_arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_exportacao_trafego | int | N |  |  |  |
| 2 | id_arquivo | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_exportacao_trafego_arquivo_id_arquivo` (NONCLUSTERED): id_arquivo
- PK `PK_exportacao_trafego_arquivo` (CLUSTERED): id_exportacao_trafego, id_arquivo

**FKs (saída):**
- id_arquivo → dbo.arquivos_importados.id_arquivo
- id_exportacao_trafego → dbo.exportacao_trafego.id_exportacao_trafego

