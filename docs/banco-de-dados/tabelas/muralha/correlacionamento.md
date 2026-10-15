# Tabelas — schema `muralha` — grupo `correlacionamento`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas.

## muralha.correlacionamento_automatico

Linhas: ~747

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | placa_alvo | varchar(10) | N |  |  |  |
| 3 | placa_correlacionada | varchar(10) | N |  |  |  |
| 4 | data_cadastro | datetime | N |  |  |  |
| 5 | nivel_correlacao | varchar(50) | S |  |  |  |
| 7 | status | int | N |  | ((1)) |  |

## muralha.correlacionamento_automatico_placa

Linhas: ~2975

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_correlacionamento | int | N |  |  |  |
| 2 | id_passagem_placa_alvo | uniqueidentifier | N |  |  |  |
| 3 | id_passagem_placa_correlacionada | uniqueidentifier | N |  |  |  |

## muralha.correlacionamento_automatico_placa_invalida

Linhas: ~7

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_passagem_placa_alvo | uniqueidentifier | N |  |  |  |
| 3 | id_passagem_placa_correlacionada | uniqueidentifier | N |  |  |  |
| 4 | motivo | int | N |  |  |  |
| 5 | id_usuario | int | N |  |  |  |
| 6 | data_registro | datetime | N |  |  |  |
| 7 | id_correlacionamento | int | S |  |  |  |

## muralha.correlacionamento_automatico_processamento

Linhas: ~10693

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | placa_alvo | varchar(10) | N |  |  |  |
| 3 | id_passagem_placa_alvo | uniqueidentifier | N |  |  |  |
| 4 | placa_correlacionada | varchar(10) | N |  |  |  |
| 5 | id_passagem_placa_correlacionada | uniqueidentifier | N |  |  |  |
| 6 | data_registro | datetime | N |  |  |  |

