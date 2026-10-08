# Tabelas — schema `muralha` — grupo `alerta`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## muralha.alerta

Linhas: ~11555

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_tipo_alerta_ocorrencia | uniqueidentifier | N |  |  |  |
| 3 | id_cad_veiculo_monitorado | uniqueidentifier | S |  |  |  |
| 4 | id_status_alerta | uniqueidentifier | N |  |  |  |
| 5 | data | datetime | N |  | (getdate()) |  |
| 6 | id_motivo_descarte | uniqueidentifier | S |  |  |  |
| 7 | observacao | varchar(200) | S |  |  |  |
| 8 | id_usuario | int | S |  |  |  |
| 9 | enviado_cliente | bit | N |  | ((0)) |  |
| 10 | data_enviado | datetime | S |  | (getdate()) |  |
| 11 | data_descarte | datetime | S |  |  |  |
| 12 | origem | varchar(200) | S |  |  |  |
| 13 | lembrete_visualizado | int | N |  | ((0)) |  |
| 14 | id_ponto_interesse | uniqueidentifier | S |  |  |  |
| 15 | alerta_vinculado | bit | S |  | ((0)) |  |
| 16 | id_alerta_vinculado | uniqueidentifier | S |  |  |  |
| 17 | data_modificacao | datetime | S |  |  |  |
| 19 | enviado_mobile | int | N |  | ((0)) |  |
| 20 | data_mobile | datetime | S |  |  |  |
| 21 | assinado | bit | S |  | ((0)) |  |
| 22 | com_semelhanca | int | S |  |  |  |
| 23 | com_semelhanca_erros | int | S |  |  |  |
| 24 | com_semelhanca_desc | varchar(120) | S |  |  |  |
| 25 | enviado_blitz_mobile | bit | S |  |  |  |
| 26 | data_blitz_mobile_processado | datetime | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_alerta_data_cad_veiculo` (NONCLUSTERED): id_cad_veiculo_monitorado, data
- IDX `IX_alerta_enviado_cliente` (NONCLUSTERED): enviado_cliente
- IDX `IX_alerta_id_cad_veiculo_monitorado` (NONCLUSTERED): id_cad_veiculo_monitorado
- IDX `IX_alerta_lembrete_visualizado` (NONCLUSTERED): lembrete_visualizado
- PK `PK_muralha_alerta` (CLUSTERED): id

**FKs (saída):**
- id_ponto_interesse → muralha.ponto_interesse.id
- id_ponto_interesse → muralha.ponto_interesse.id
- id_cad_veiculo_monitorado → muralha.cad_veiculo_monitorado.id
- id_motivo_descarte → muralha.motivo_descarte.id
- id_status_alerta → muralha.status_alerta.id
- id_tipo_alerta_ocorrencia → muralha.tipo_alerta_ocorrencia.id
- id_usuario → dbo.sis_usuario.id_usuario
- id_alerta_vinculado → muralha.alerta.id

**Referenciada por:**
- muralha.alerta.id_alerta_vinculado
- muralha.alerta_notificacao.id_alerta
- muralha.alerta_veiculo.id_alerta
- muralha.anotacao_contributiva.id_alerta
- muralha.blitz_abordagem.id_alerta
- muralha.ocorrencia.id_alerta

## muralha.alerta_notificacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_alerta | uniqueidentifier | N |  |  |  |
| 3 | id_grupo | int | N |  |  |  |
| 4 | id_tipo_notificacao | uniqueidentifier | N |  |  |  |
| 5 | id_status_notificacao | uniqueidentifier | N |  | ('99AF55C6-2446-4B98-BBCD-83663C504C79') |  |
| 6 | id_usuario | int | N |  |  |  |
| 7 | data_cadastro | datetime | N |  | (getdate()) |  |
| 8 | data_processado | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_muralha_alerta_notificacao` (CLUSTERED): id

**FKs (saída):**
- id_alerta → muralha.alerta.id
- id_grupo → dbo.sis_grupo.id_grupo
- id_status_notificacao → muralha.status_notificacao.id
- id_tipo_notificacao → muralha.tipo_notificacao.id
- id_usuario → dbo.sis_usuario.id_usuario

## muralha.alerta_questionario

Linhas: ~8

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | obrigatorio | bit | N |  | ((0)) |  |
| 3 | pergunta | varchar(max) | N |  |  |  |
| 4 | deletado | bit | N |  |  |  |
| 5 | data_criacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__alerta_q__3213E83F959DA789` (CLUSTERED): id

**Referenciada por:**
- muralha.alerta_questionario_resposta.id_questionario_alerta

## muralha.alerta_questionario_resposta

Linhas: ~34

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_alerta | uniqueidentifier | S |  |  |  |
| 3 | id_questionario_alerta | int | N |  |  |  |
| 4 | id_usuario_resposta | int | N |  |  |  |
| 5 | resposta_simples | varchar(3) | S |  |  |  |
| 6 | resposta_livre_usuario | varchar(max) | S |  |  |  |
| 7 | data_criacao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__alerta_q__3213E83F74503060` (CLUSTERED): id

**FKs (saída):**
- id_questionario_alerta → muralha.alerta_questionario.id

## muralha.alerta_usuario_visualiza

Linhas: ~74

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | id_alerta | uniqueidentifier | S |  |  |  |
| 3 | id_usuario | int | S |  |  |  |
| 4 | data_acesso | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK__alerta_u__3213E83FEFDDA064` (CLUSTERED): id

## muralha.alerta_veiculo

Linhas: ~11554

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | uniqueidentifier | N |  | (newid()) |  |
| 2 | id_alerta | uniqueidentifier | N |  |  |  |
| 3 | id_veiculo_tempo_real | uniqueidentifier | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_alerta_veiculo_id_alerta` (NONCLUSTERED): id_alerta
- IDX `IX_alerta_veiculo_id_veiculo_tempo_real` (NONCLUSTERED): id_veiculo_tempo_real
- PK `PK_muralha_alerta_veiculo` (CLUSTERED): id

**FKs (saída):**
- id_alerta → muralha.alerta.id
- id_veiculo_tempo_real → muralha.veiculo_tempo_real.id

