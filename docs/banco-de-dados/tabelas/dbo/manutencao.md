# Tabelas — schema `dbo` — grupo `manutencao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.manutencao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_manutencao | int | N | S |  |  |
| 2 | id_status | int | S |  | ((0)) |  |
| 3 | descricao | nvarchar(50) | S |  |  |  |
| 4 | id_local | int | S |  |  |  |
| 5 | serie_equipamento | int | S |  |  |  |
| 6 | id_pista | int | S |  |  |  |
| 7 | id_enquadramento | int | S |  |  |  |
| 8 | tipo_grupo_autuador | varchar(30) | S |  |  |  |
| 9 | data_ocorrencia | datetime | S |  |  |  |
| 10 | data_cadastro | datetime | S |  | (getdate()) |  |
| 11 | data_inicio | datetime | S |  |  |  |
| 12 | data_previsto | datetime | S |  |  |  |
| 13 | id_ocorrencia | int | S |  |  |  |
| 14 | numero_oficio | int | S |  |  |  |
| 15 | ano_oficio | int | S |  |  |  |
| 16 | id_tecnico | int | S |  |  |  |
| 17 | id_auxiliar | int | S |  |  |  |
| 18 | data_conclusao | datetime | S |  |  |  |
| 19 | data_ultima_alteracao | datetime | S |  | (getdate()) |  |
| 20 | id_ultimo_usuario | int | S |  |  |  |
| 21 | encaminhar | bit | S |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__manutenc__5F9D64EE798EB9AB` (CLUSTERED): id_manutencao

## dbo.manutencao_atividade

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_manutencao_atividade | int | N | S |  |  |
| 2 | id_manutencao | int | N |  |  |  |
| 3 | pista | tinyint | S |  |  |  |
| 4 | id_manutencao_descricao | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_manutencao_atividade` (CLUSTERED): id_manutencao_atividade

**FKs (saída):**
- id_manutencao_descricao → dbo.manutencao_descricao.id_manutencao_descricao

## dbo.manutencao_causa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_manutencao | int | N |  |  |  |
| 2 | id_causa | tinyint | N |  |  |  |

## dbo.manutencao_causa_desc

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_causa | tinyint | N |  |  |  |
| 2 | descricao | varchar(50) | S |  |  |  |
| 3 | causa_tecnica | bit | N |  | ((0)) |  |
| 4 | requer_oficio | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- PK `PK__manutenc__D200EA9B13B2018A` (CLUSTERED): id_causa

## dbo.manutencao_cav

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_manutencao | int | N |  |  |  |
| 2 | cod_pista | int | N |  |  |  |
| 3 | cod_pista_alternativo | int | N |  |  |  |
| 4 | cod_pista_prodam | int | N |  |  |  |
| 5 | id_enquadramento | int | S |  |  |  |
| 6 | tipo_grupo_autuador | varchar(30) | N |  |  |  |
| 7 | data_inicio | date | N |  |  |  |
| 8 | descricao | varchar(50) | N |  |  |  |
| 9 | data_fim | date | N |  |  |  |
| 10 | estado | varchar(10) | N |  |  |  |

## dbo.manutencao_comentarios

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_comentario | int | N | S |  |  |
| 2 | id_manutencao | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data | datetime | N |  | (getdate()) |  |
| 5 | comentario | nvarchar(300) | S |  |  |  |

**Índices/Chaves:**
- PK `PK__manutenc__1BA6C6F430E8C7D2` (CLUSTERED): id_comentario

## dbo.manutencao_descricao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_manutencao_descricao | int | N | S |  |  |
| 2 | descricao | nchar(50) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_manutencao_descricao` (CLUSTERED): id_manutencao_descricao

**Referenciada por:**
- dbo.manutencao_atividade.id_manutencao_descricao

## dbo.manutencao_status

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_status | tinyint | N |  |  |  |
| 2 | descricao | varchar(40) | N |  |  |  |

