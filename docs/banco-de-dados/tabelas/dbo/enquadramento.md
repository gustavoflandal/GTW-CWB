# Tabelas — schema `dbo` — grupo `enquadramento`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.enquadramento

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_enquadramento | int | N |  |  |  |
| 2 | descricao | varchar(100) | N |  |  |  |
| 3 | tipo_info_especifica | char(1) | N |  | (' ') |  |
| 4 | infracao_metrologia | bit | N |  | ((0)) |  |
| 5 | id_tipo_imagem_pan | int | S |  |  |  |
| 6 | id_tipo_imagem_obj | int | S |  |  |  |
| 7 | id_tipo_imagem_pan2 | int | S |  |  |  |
| 8 | id_inconsistencia_isencao | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_enquadramento_enquadramento_tipo_info_especifica` (NONCLUSTERED): id_enquadramento, tipo_info_especifica
- PK `PK_enquadramento` (CLUSTERED): id_enquadramento

**FKs (saída):**
- id_inconsistencia_isencao → dbo.inconsistencia.id_inconsistencia
- id_tipo_imagem_pan → dbo.tipo_imagem.id_tipo_imagem
- id_tipo_imagem_obj → dbo.tipo_imagem.id_tipo_imagem
- id_tipo_imagem_pan2 → dbo.tipo_imagem.id_tipo_imagem

**Referenciada por:**
- dbo.cad_inibicao_infracao.id_enquadramento
- dbo.cad_isento.id_enquadramento
- dbo.cad_isento_ant.id_enquadramento
- dbo.cad_isento_arquivo.id_enquadramento
- dbo.enquadramento_inconsistencia.id_enquadramento
- dbo.infracao.id_enquadramento
- dbo.infracao_importacao.id_enquadramento
- dbo.movimento_importacao.id_enquadramento
- dbo.movimento_tarja.id_enquadramento

## dbo.enquadramento_ativo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_local | int | N |  |  |  |
| 2 | sequencia_local | tinyint | N |  |  |  |
| 3 | data_inicio | date | S |  |  |  |
| 4 | data_fim | date | S |  |  |  |
| 5 | id_enquadramento | int | N |  |  |  |
| 6 | descricao_apait | varchar(30) | S |  |  |  |
| 7 | ativo | int | S |  |  |  |
| 8 | id_pista | tinyint | N |  |  |  |
| 9 | cod_pista_alternativo | int | S |  |  |  |
| 10 | cod_pista | int | S |  |  |  |
| 11 | cod_pista_prodam | int | S |  |  |  |

## dbo.enquadramento_inconsistencia

Linhas: ~520

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_enquadramento | int | N |  |  |  |
| 2 | id_inconsistencia | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_enquadramento_inconsistencia` (NONCLUSTERED): id_enquadramento, id_inconsistencia

**FKs (saída):**
- id_enquadramento → dbo.enquadramento.id_enquadramento
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia

## dbo.enquadramento_inconsistencia_bkp

Linhas: ~520

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_enquadramento | int | N |  |  |  |
| 2 | id_inconsistencia | int | N |  |  |  |

## dbo.enquadramento_regra_infracao

Linhas: ~16

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | tipo | char(2) | N |  |  |  |
| 2 | id_enquadramento | int | N |  |  |  |
| 3 | tipo_apait | char(2) | S |  |  |  |
| 4 | descricao_apait | varchar(100) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_enquadramento_regra_infracao` (CLUSTERED): tipo, id_enquadramento

