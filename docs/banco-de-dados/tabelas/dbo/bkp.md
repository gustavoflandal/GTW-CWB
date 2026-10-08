# Tabelas — schema `dbo` — grupo `bkp`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.bkp_ipatinga_imagem

Linhas: ~1519

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_uniq | uniqueidentifier | N |  |  |  |
| 3 | imagem | image | S |  |  |  |
| 4 | indice_imagem | int | S |  |  |  |
| 5 | ds_caminho | varchar(255) | S |  |  |  |
| 6 | imagem_removida | bit | S |  |  |  |
| 7 | imagem_inmetro | image | S |  |  |  |
| 8 | assinatura_digital | image | S |  |  |  |
| 9 | chave_publica | image | S |  |  |  |
| 10 | assinatura_valida | bit | S |  |  |  |

## dbo.bkp_ipatinga_imagem_info

Linhas: ~1519

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_tipo_imagem | int | N |  |  |  |
| 3 | formato | char(5) | S |  |  |  |
| 4 | md5 | char(40) | S |  |  |  |

## dbo.bkp_ipatinga_infracao_imagem

Linhas: ~1519

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_imagem_pan | int | S |  |  |  |
| 3 | id_imagem_obj | int | S |  |  |  |
| 4 | id_imagem_pan2 | int | S |  |  |  |

## dbo.bkp_ipatinga_infracao_obliteracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | x | int | N |  |  |  |
| 3 | y | int | N |  |  |  |
| 4 | largura | int | N |  |  |  |
| 5 | altura | int | N |  |  |  |
| 6 | id_imagem | int | N |  |  |  |
| 7 | sequencia_obliteracao | int | N |  |  |  |

## dbo.bkp_ipatinga_infracao_processo_concluido

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo_concluido | int | N |  |  |  |
| 2 | id_infracao | int | N |  |  |  |
| 3 | id_processo | int | N |  |  |  |
| 4 | data_conclusao | datetime | N |  |  |  |
| 5 | id_inconsistencia | int | N |  |  |  |
| 6 | id_imagem | int | S |  |  |  |

## dbo.bkp_ipatinga_veiculo_imagem

Linhas: ~1519

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | int | N |  |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | id_imagem_local | int | S |  |  |  |

## dbo.bkp_veiculo_pesagem

Linhas: ~1332362

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | pesagem_valida | bit | N |  |  |  |
| 3 | pbt | float | S |  |  |  |
| 4 | temperatura_pavimento | float | S |  |  |  |
| 5 | velocidade_piezo | float | S |  |  |  |

## dbo.bkp_veiculo_pesagem_eixo

Linhas: ~52603

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | eixo | tinyint | N |  |  |  |
| 3 | peso | float | S |  |  |  |
| 4 | distancia_eixo_anterior | float | S |  |  |  |

