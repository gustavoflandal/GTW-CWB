# Tabelas — schema `muralha` — grupo `vtr`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas. Tabelas novas ou alteradas atualizadas em 2026-10-20.

## muralha.vtr_complemento

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_vtr | uniqueidentifier | N |  |  |  |
| 2 | status_analise | varchar(20) | S |  |  |  |
| 3 | latencia_ms | int | S |  |  |  |
| 4 | dt_preclass | datetime2 | S |  |  |  |

**Índices/Chaves:**
- PK `PK_vtr_complemento` (CLUSTERED): id_vtr

## muralha.vtr_correcao_justificativa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_correcao | int | N |  |  |  |
| 2 | id_motivo | int | N |  |  |  |
| 3 | justificativa | nvarchar(500) | S |  |  |  |
| 4 | origem_intervencao | varchar(15) | N |  |  |  |
| 5 | id_revisao | bigint | S |  |  |  |
| 6 | ip | varchar(45) | S |  |  |  |
| 7 | dt | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- PK `PK_vtr_correcao_justificativa` (CLUSTERED): id_correcao

## muralha.vtr_imagem_complemento

Linhas: ~142000

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | uniqueidentifier | N |  |  |  |
| 2 | sha256 | char(64) | S |  |  |  |
| 3 | status_integridade | varchar(20) | N |  | ('OK') |  |
| 4 | obliterada | bit | N |  | ((0)) |  |
| 5 | id_original | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- PK `PK_vtr_imagem_complemento` (CLUSTERED): id_imagem

## muralha.vtr_imagem_hash_origem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | uniqueidentifier | N |  |  |  |
| 2 | sha256_origem | varchar(128) | N |  |  |  |
| 3 | dt_recepcao | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- PK `PK_vtr_imagem_hash_origem` (CLUSTERED): id_imagem

## muralha.vtr_imagem_integridade

Linhas: ~200000

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_imagem | uniqueidentifier | N |  |  |  |
| 2 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 3 | indice_imagem | tinyint | S |  |  |  |
| 4 | id_local | int | N |  |  |  |
| 5 | dt_captura | datetime | N |  |  |  |
| 6 | dt_recepcao | datetime | S |  |  |  |
| 7 | dt_verificacao | datetime2 | N |  | (sysdatetime()) |  |
| 8 | status_integridade | varchar(20) | N |  |  |  |
| 9 | origem_hash | varchar(120) | N |  |  |  |
| 10 | sha256_calculado | char(64) | S |  |  |  |
| 11 | hash_origem | varchar(128) | S |  |  |  |
| 12 | motivo | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_vii_captura` (NONCLUSTERED): dt_captura
- IDX `IX_vii_local_captura` (NONCLUSTERED): id_local, dt_captura
- IDX `IX_vii_passagem` (NONCLUSTERED): id_veiculo_tempo_real
- PK `PK_vtr_imagem_integridade` (CLUSTERED): id_imagem

## muralha.vtr_imagem_obliterada

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_imagem_original | uniqueidentifier | N |  |  |  |
| 3 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 4 | imagem | image | N |  |  |  |
| 5 | dt_criacao | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_vio_original` (NONCLUSTERED): id_imagem_original
- PK `PK__vtr_imag__3213E83F203250A0` (CLUSTERED): id

## muralha.vtr_ocr_avaliacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 2 | id_local | int | N |  |  |  |
| 3 | dt_passagem | datetime | N |  |  |  |
| 4 | dt_avaliacao | datetime2 | N |  | (sysdatetime()) |  |
| 5 | score_minimo | decimal(5,2) | S |  |  |  |
| 6 | limiar | decimal(5,2) | N |  |  |  |
| 7 | motivo | varchar(30) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_voa_local_passagem` (NONCLUSTERED): id_local, dt_passagem
- PK `PK_vtr_ocr_avaliacao` (CLUSTERED): id_veiculo_tempo_real

## muralha.vtr_placa_leitura

Linhas: ~847315

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 3 | ordem | tinyint | N |  |  |  |
| 4 | tipo | varchar(15) | N |  |  |  |
| 5 | placa | varchar(10) | S |  |  |  |
| 6 | score | decimal(5,2) | S |  |  |  |
| 7 | origem | varchar(10) | N |  |  |  |
| 8 | dt | datetime2 | N |  | (sysdatetime()) |  |
| 9 | id_usuario | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_vpl_passagem` (NONCLUSTERED): id_veiculo_tempo_real
- PK `PK_vtr_placa_leitura` (CLUSTERED): id
- UNIQUE `UQ_vpl_passagem` (NONCLUSTERED): id_veiculo_tempo_real, origem, ordem

## muralha.vtr_revisao_placa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | dt_passagem | datetime | N |  |  |  |
| 5 | motivo | varchar(30) | N |  |  |  |
| 6 | score_minimo | decimal(5,2) | S |  |  |  |
| 7 | limiar | decimal(5,2) | S |  |  |  |
| 8 | detalhe | varchar(200) | S |  |  |  |
| 9 | situacao | varchar(15) | N |  | ('PENDENTE') |  |
| 10 | dt_entrada | datetime2 | N |  | (sysdatetime()) |  |
| 11 | dt_tratamento | datetime2 | S |  |  |  |
| 12 | id_usuario_tratamento | int | S |  |  |  |
| 13 | origem_intervencao | varchar(15) | S |  |  |  |
| 14 | id_correcao | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_vrp_local_passagem` (NONCLUSTERED): id_local, dt_passagem
- IDX `IX_vrp_situacao_passagem` (NONCLUSTERED): situacao, dt_passagem
- PK `PK_vtr_revisao_placa` (CLUSTERED): id
- UNIQUE `UQ_vrp_passagem` (NONCLUSTERED): id_veiculo_tempo_real

## muralha.vtr_status_analise

Linhas: ~30

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 2 | status_analise | varchar(25) | N |  | ('AGUARDANDO_ANALISE') |  |

**Índices/Chaves:**
- IDX `ix_vsa_status` (NONCLUSTERED): status_analise
- PK `PK__vtr_stat__16D79F50118AEB73` (CLUSTERED): id_veiculo_tempo_real

