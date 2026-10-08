# Tabelas — schema `dbo` — grupo `pmesp`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.pmesp_atraso

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | tempo_medio | int | N |  |  |  |
| 3 | tempo_maximo | int | N |  |  |  |

## dbo.pmesp_desconectado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | tempo_desconectado | int | N |  |  |  |

## dbo.pmesp_evento_conexao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_evento_conexao | int | N | S |  |  |
| 2 | id_local | int | S |  |  |  |
| 3 | data_conexao | datetime | N |  |  |  |
| 4 | data_desconexao | datetime | S |  |  |  |
| 5 | endereco_ip | varchar(15) | N |  |  |  |
| 6 | movimentos_recebidos | int | N |  | ((0)) |  |
| 7 | movimentos_transmitidos | int | N |  | ((0)) |  |
| 8 | movimentos_invalidos | int | N |  | ((0)) |  |
| 9 | data_atualizado | datetime | S |  | (getdate()) |  |
| 10 | data_ultimo_movimento | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_pmesp_evento_conexao_local_data_movimento` (NONCLUSTERED): id_local, data_conexao, data_ultimo_movimento
- PK `PK__pmesp_ev__0F4B15853EE8CC0B` (CLUSTERED): id_evento_conexao

## dbo.pmesp_medicao_lida_enviada

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | dia | date | N |  |  |  |
| 3 | hora | tinyint | N |  |  |  |
| 4 | placa_lida | int | N |  |  |  |
| 5 | placa_enviada | int | N |  |  |  |

## dbo.pmesp_movimento_2016

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento | bigint | N | S |  |  |
| 2 | id_evento_conexao | int | N |  |  |  |
| 3 | id_equipamento | int | N |  |  |  |
| 4 | placa | char(7) | N |  |  |  |
| 5 | data_movimento | datetime | N |  |  |  |
| 6 | data_recebido | datetime | N |  |  |  |
| 7 | data_registrado | datetime | N |  | (getdate()) |  |
| 8 | data_transmitido | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_pmesp_movimento_data_evento_transmitido` (NONCLUSTERED): data_movimento
- IDX `IX_pmesp_movimento_equipamento_placa_data` (NONCLUSTERED): id_equipamento, placa, data_movimento
- PK `PK__pmesp_mo__3E9C86B53AF14C47` (CLUSTERED): id_movimento

## dbo.pmesp_movimento_2017

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento | bigint | N |  |  |  |
| 2 | id_evento_conexao | int | N |  |  |  |
| 3 | id_equipamento | int | N |  |  |  |
| 4 | placa | char(7) | N |  |  |  |
| 5 | data_movimento | datetime | N |  |  |  |
| 6 | data_recebido | datetime | N |  |  |  |
| 7 | data_registrado | datetime | N |  | (getdate()) |  |
| 8 | data_transmitido | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_pmesp_movimento_data_evento_transmitido_2017_4` (NONCLUSTERED): data_movimento
- IDX `IX_pmesp_movimento_equipamento_placa_data_2017_4` (NONCLUSTERED): id_equipamento, placa, data_movimento
- PK `PK__pmesp_mo__3E9C86B5EA40D26C` (CLUSTERED): id_movimento

## dbo.pmesp_movimento_erro

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_movimento_erro | bigint | N | S |  |  |
| 2 | id_movimento | bigint | S |  |  |  |
| 3 | id_equipamento | int | S |  |  |  |
| 4 | id_evento_conexao | int | N |  |  |  |
| 5 | codigo | int | S |  |  |  |
| 6 | placa_retorno | char(7) | S |  |  |  |
| 7 | mensagem | varchar(1000) | S |  |  |  |
| 8 | data_registro | datetime | N |  | (getdate()) |  |

**Índices/Chaves:**
- PK `PK__pmesp_mo__996CEDAAE2B0B4B6` (CLUSTERED): id_movimento_erro

## dbo.pmesp_movimentos_atraso_dia

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_equipamento | int | N |  |  |  |
| 2 | dia | date | N |  |  |  |
| 3 | movimentos | int | N |  |  |  |
| 4 | atraso_ok | int | N |  |  |  |
| 5 | atraso_ok1 | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_equipamento_dia_movimentos_atraso` (NONCLUSTERED): id_equipamento

## dbo.pmesp_perda

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | placa_lida | int | N |  |  |  |
| 3 | placa_enviada | int | N |  |  |  |

