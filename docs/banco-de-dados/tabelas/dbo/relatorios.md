# Tabelas — schema `dbo` — grupo `relatorios`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.relatorios_gerados

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ID_Gerado | int | N | S |  |  |
| 2 | Data | datetime | N |  |  |  |
| 3 | ID_Relatorio | int | N |  |  |  |
| 4 | DataInicio | datetime | N |  |  |  |
| 5 | DataFinal | datetime | N |  |  |  |
| 6 | HoraInicio | datetime | N |  |  |  |
| 7 | HoraFinal | datetime | N |  |  |  |
| 8 | Gerador | char(30) | N |  |  |  |
| 9 | TempoGeracao | datetime | S |  |  |  |

## dbo.relatorios_mediavelocidade_hora

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ID_Gerado | int | N |  |  |  |
| 2 | local | int | N |  |  |  |
| 3 | h0 | decimal(6,1) | S |  |  |  |
| 4 | h1 | decimal(6,1) | S |  |  |  |
| 5 | h2 | decimal(6,1) | S |  |  |  |
| 6 | h3 | decimal(6,1) | S |  |  |  |
| 7 | h4 | decimal(6,1) | S |  |  |  |
| 8 | h5 | decimal(6,1) | S |  |  |  |
| 9 | h6 | decimal(6,1) | S |  |  |  |
| 10 | h7 | decimal(6,1) | S |  |  |  |
| 11 | h8 | decimal(6,1) | S |  |  |  |
| 12 | h9 | decimal(6,1) | S |  |  |  |
| 13 | h10 | decimal(6,1) | S |  |  |  |
| 14 | h11 | decimal(6,1) | S |  |  |  |
| 15 | h12 | decimal(6,1) | S |  |  |  |
| 16 | h13 | decimal(6,1) | S |  |  |  |
| 17 | h14 | decimal(6,1) | S |  |  |  |
| 18 | h15 | decimal(6,1) | S |  |  |  |
| 19 | h16 | decimal(6,1) | S |  |  |  |
| 20 | h17 | decimal(6,1) | S |  |  |  |
| 21 | h18 | decimal(6,1) | S |  |  |  |
| 22 | h19 | decimal(6,1) | S |  |  |  |
| 23 | h20 | decimal(6,1) | S |  |  |  |
| 24 | h21 | decimal(6,1) | S |  |  |  |
| 25 | h22 | decimal(6,1) | S |  |  |  |
| 26 | h23 | decimal(6,1) | S |  |  |  |
| 27 | Total | decimal(6,1) | S |  |  |  |

## dbo.relatorios_pt_pnt

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | Codigo | bigint | N | S |  |  |
| 2 | ID_Gerado | int | N |  |  |  |
| 3 | Local | int | N |  |  |  |
| 4 | Tipo | char(1) | N |  |  |  |
| 5 | Categoria | char(15) | N |  |  |  |
| 6 | CodInconsistencia | int | N |  |  |  |
| 7 | DescrInconsistencia | varchar(50) | N |  |  |  |
| 8 | Total | int | S |  |  |  |

## dbo.relatorios_trafego_pista

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | Codigo | bigint | N | S |  |  |
| 2 | ID_Gerado | int | N |  |  |  |
| 3 | Local | int | N |  |  |  |
| 4 | pista | int | S |  |  |  |
| 5 | v00_40 | int | S |  |  |  |
| 6 | v41_48 | int | S |  |  |  |
| 7 | v49_67 | int | S |  |  |  |
| 8 | v68_79 | int | S |  |  |  |
| 9 | v80_97 | int | S |  |  |  |
| 10 | v98_999 | int | S |  |  |  |
| 11 | v68_77 | int | S |  |  |  |
| 12 | v78_91 | int | S |  |  |  |
| 13 | v92_113 | int | S |  |  |  |
| 14 | v114_999 | int | S |  |  |  |
| 15 | qt_Infratores | int | S |  |  |  |
| 16 | qt_RejeitadoPT | int | S |  |  |  |
| 17 | qt_RejeitadoPNT | int | S |  |  |  |
| 18 | qt_InvalidadoPT | int | S |  |  |  |
| 19 | qt_InvalidadoPNT | int | S |  |  |  |
| 20 | qt_Veic_Oficiais | int | S |  |  |  |
| 21 | qt_Validos | int | S |  |  |  |
| 22 | qt_Infr_Red | int | S |  |  |  |

## dbo.relatorios_volume_hora_pmg

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | Codigo | bigint | N | S |  |  |
| 2 | ID_Gerado | int | N |  |  |  |
| 3 | Local | int | N |  |  |  |
| 4 | pista | int | S |  |  |  |
| 5 | ClassePorTamanho | char(1) | S |  |  |  |
| 6 | h0 | int | S |  |  |  |
| 7 | h1 | int | S |  |  |  |
| 8 | h2 | int | S |  |  |  |
| 9 | h3 | int | S |  |  |  |
| 10 | h4 | int | S |  |  |  |
| 11 | h5 | int | S |  |  |  |
| 12 | h6 | int | S |  |  |  |
| 13 | h7 | int | S |  |  |  |
| 14 | h8 | int | S |  |  |  |
| 15 | h9 | int | S |  |  |  |
| 16 | h10 | int | S |  |  |  |
| 17 | h11 | int | S |  |  |  |
| 18 | h12 | int | S |  |  |  |
| 19 | h13 | int | S |  |  |  |
| 20 | h14 | int | S |  |  |  |
| 21 | h15 | int | S |  |  |  |
| 22 | h16 | int | S |  |  |  |
| 23 | h17 | int | S |  |  |  |
| 24 | h18 | int | S |  |  |  |
| 25 | h19 | int | S |  |  |  |
| 26 | h20 | int | S |  |  |  |
| 27 | h21 | int | S |  |  |  |
| 28 | h22 | int | S |  |  |  |
| 29 | h23 | int | S |  |  |  |
| 30 | Total | int | S |  |  |  |

