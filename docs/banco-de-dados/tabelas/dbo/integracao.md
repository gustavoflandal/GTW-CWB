# Tabelas — schema `dbo` — grupo `integracao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.integracao_arquivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | char(53) | N |  |  |  |
| 2 | tipo_arquivo | tinyint | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | id_pista | tinyint | N |  |  |  |
| 5 | data_inicio | datetime | N |  |  |  |
| 6 | data_fim | datetime | N |  |  |  |
| 7 | id_arquivo | char(12) | N |  |  |  |
| 8 | data_recebimento | datetime | S |  |  |  |
| 9 | data_processamento | datetime | S |  |  |  |
| 10 | resultado | bit | S |  |  |  |

**Índices/Chaves:**
- PK `PK__integrac__5ACBFC966B8DB38E` (CLUSTERED): nome_arquivo

## dbo.integracao_arquivo_erro

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | char(53) | N |  |  |  |
| 2 | sequencia | int | N |  |  |  |
| 3 | codigo | int | S |  |  |  |
| 4 | descricao | varchar(300) | S |  |  |  |

## dbo.integracao_arquivo_tipo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | tipo_arquivo | tinyint | N |  |  |  |
| 2 | descricao | varchar(40) | N |  |  |  |

## dbo.integracao_imagem_gct_info

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(120) | N |  |  |  |
| 2 | diretorio | char(17) | N |  |  |  |
| 3 | tamanho_bytes | bigint | N |  |  |  |
| 4 | verificacao | char(47) | N |  |  |  |
| 5 | verificado | bit | N |  |  |  |
| 6 | erro_verificacao | bit | N |  |  |  |

**Índices/Chaves:**
- PK `PK__integrac__5ACBFC96504AF7A8` (CLUSTERED): nome_arquivo

## dbo.integracao_sequencia_imagem_gct

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(120) | N |  |  |  |
| 2 | id_imagem_local | int | N |  |  |  |
| 3 | data_imagem | datetime | N |  |  |  |
| 4 | id_enquadramento | int | N |  |  |  |
| 5 | id_local | int | N |  |  |  |
| 6 | id_pista | int | N |  |  |  |
| 7 | id_infracao_GTW | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__integrac__5ACBFC96103293DE` (CLUSTERED): nome_arquivo

## dbo.integracao_sequencia_imagem_gct_info6

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | S |  |  |  |
| 2 | nome_arquivo | varchar(96) | S |  |  |  |

## dbo.integracao_sequencia_imagem_gct_info7

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | S |  |  |  |
| 2 | nome_arquivo | varchar(96) | S |  |  |  |

## dbo.integracao_sequencia_imagem_gct_info8

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | S |  |  |  |
| 2 | data_imagem | datetime | N |  |  |  |
| 3 | nome_arquivo | varchar(96) | S |  |  |  |
| 4 | dif | int | S |  |  |  |
| 5 | id_infracao | int | N |  |  |  |
| 6 | id_enquadramento | int | N |  |  |  |

