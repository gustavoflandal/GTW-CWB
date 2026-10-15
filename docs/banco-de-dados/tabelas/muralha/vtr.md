# Tabelas — schema `muralha` — grupo `vtr`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas.

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

## muralha.vtr_status_analise

Linhas: ~30

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |
| 2 | status_analise | varchar(25) | N |  | ('AGUARDANDO_ANALISE') |  |

**Índices/Chaves:**
- IDX `ix_vsa_status` (NONCLUSTERED): status_analise
- PK `PK__vtr_stat__16D79F50118AEB73` (CLUSTERED): id_veiculo_tempo_real

