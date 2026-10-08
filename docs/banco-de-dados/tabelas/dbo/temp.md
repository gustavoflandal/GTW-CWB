# Tabelas — schema `dbo` — grupo `temp`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.temp_imagens_exportar_dados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(300) | S |  |  |  |
| 2 | id_imagem | int | S |  |  |  |

## dbo.temp_imagens_exportar_dados_bkp

Linhas: ~156

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | nome_arquivo | varchar(300) | S |  |  |  |
| 2 | id_imagem | int | S |  |  |  |

## dbo.temp_infracao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N | S |  |  |
| 2 | id_imagem_local | int | N |  |  |  |
| 3 | placa | char(7) | S |  |  |  |
| 4 | id_enquadramento | int | N |  |  |  |
| 5 | id_inconsistencia | int | S |  |  |  |
| 6 | id_processo_concluido | int | S |  |  |  |
| 7 | id_processo | int | S |  |  |  |
| 8 | id_veiculo | bigint | N |  |  |  |
| 9 | id_local | int | N |  |  |  |
| 10 | sequencia_local | tinyint | S |  |  |  |
| 11 | pista | tinyint | N |  |  |  |
| 12 | data | datetime | N |  |  |  |
| 13 | id_usuario_atual | int | S |  |  |  |
| 14 | id_usuario_final | int | S |  |  |  |
| 15 | velocidade_limite | int | S |  |  |  |
| 16 | velocidade_considerada | int | S |  |  |  |
| 17 | segundos_tolerancia | decimal(8,3) | S |  |  |  |
| 18 | id_liberacao | int | S |  |  |  |
| 19 | espera | bit | S |  |  |  |
| 20 | tempo_vermelho_detec | decimal(8,3) | S |  |  |  |
| 21 | status_bloqueio | int | N |  |  |  |
| 22 | data_entrada | datetime | S |  |  |  |
| 23 | data_afericao | datetime | S |  |  |  |

## dbo.temp_infracao_classificacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | S |  |  |  |

## dbo.temp_infracao_classificacao_moto

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | S |  |  |  |

