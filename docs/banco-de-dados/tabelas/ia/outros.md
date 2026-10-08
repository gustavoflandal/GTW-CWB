# Tabelas — schema `ia` — grupo `outros`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## ia.texto_adesivo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_captura | uniqueidentifier | N |  |  |  |
| 3 | texto | varchar(255) | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_texto_adesivo_veiculo_caracteristica_id_captura_texto` (NONCLUSTERED): id_captura, texto
- PK `PK_texto_adesivo` (CLUSTERED): id

**FKs (saída):**
- id_captura → ia.veiculo_caracteristica.id_captura

## ia.veiculo_caracteristica

Linhas: ~8185846

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_captura | uniqueidentifier | N |  |  |  |
| 3 | id_cor | int | S |  |  |  |
| 4 | id_classificacao | char(1) | S |  |  |  |
| 5 | id_marca | int | S |  |  |  |
| 6 | id_modelo | int | S |  |  |  |
| 7 | id_tipo_especial | bigint | S |  |  |  |
| 8 | dado_bruto | nvarchar(max) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_veiculo_caracteristica` (CLUSTERED): id
- UNIQUE `UQ_veiculo_caracteristica_id_captura` (NONCLUSTERED): id_captura

**FKs (saída):**
- id_tipo_especial → ia.cad_tipo_especial.id
- id_classificacao → dbo.classe_veiculo.id_classe
- id_cor → ia.cad_cor.id
- id_marca → ia.cad_marca.id
- id_modelo → ia.cad_modelo.id

**Referenciada por:**
- dbo.infracao.id_captura
- dbo.veiculo.id_captura
- dbo.veiculo_estatistica.id_captura
- ia.texto_adesivo.id_captura
- muralha.veiculo_tempo_real.id_captura

