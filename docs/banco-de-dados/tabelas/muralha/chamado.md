# Tabelas — schema `muralha` — grupo `chamado`

Gerado de GTW_MURALHA_DEV em 2026-10-15. Linhas aproximadas.

## muralha.chamado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | protocolo | varchar(20) | N |  |  |  |
| 3 | titulo | nvarchar(150) | N |  |  |  |
| 4 | descricao | nvarchar(max) | N |  |  |  |
| 5 | id_categoria | int | N |  |  |  |
| 6 | id_prioridade | int | N |  |  |  |
| 7 | id_prioridade_solicitada | int | N |  |  |  |
| 8 | status | varchar(30) | N |  | ('ABERTO') |  |
| 9 | origem | varchar(20) | N |  | ('PORTAL') |  |
| 10 | id_local | int | S |  |  |  |
| 11 | id_usuario_solicitante | int | N |  |  |  |
| 12 | id_usuario_responsavel | int | S |  |  |  |
| 13 | dt_abertura | datetime2 | N |  | (sysdatetime()) |  |
| 14 | dt_prazo_atendimento | datetime2 | N |  |  |  |
| 15 | dt_prazo_resolucao | datetime2 | N |  |  |  |
| 16 | dt_primeiro_atendimento | datetime2 | S |  |  |  |
| 17 | dt_resolucao | datetime2 | S |  |  |  |
| 18 | dt_fechamento | datetime2 | S |  |  |  |
| 19 | minutos_pausados | int | N |  | ((0)) |  |
| 20 | dt_inicio_pausa | datetime2 | S |  |  |  |
| 21 | sla_atendimento | varchar(10) | N |  | ('NO_PRAZO') |  |
| 22 | sla_resolucao | varchar(10) | N |  | ('NO_PRAZO') |  |
| 23 | avaliacao_nota | tinyint | S |  |  |  |
| 24 | avaliacao_comentario | nvarchar(1000) | S |  |  |  |
| 25 | dt_avaliacao | datetime2 | S |  |  |  |
| 26 | fechado_automaticamente | bit | N |  | ((0)) |  |
| 27 | dt_atualizacao | datetime2 | N |  | (sysdatetime()) |  |

**Índices/Chaves:**
- IDX `ix_chamado_atualizacao` (NONCLUSTERED): dt_atualizacao
- IDX `ix_chamado_categoria` (NONCLUSTERED): id_categoria
- IDX `ix_chamado_local` (NONCLUSTERED): id_local
- IDX `ix_chamado_solicitante` (NONCLUSTERED): id_usuario_solicitante, dt_abertura
- IDX `ix_chamado_status_abertura` (NONCLUSTERED): status, dt_abertura
- PK `pk_chamado` (CLUSTERED): id
- UNIQUE `uq_chamado_protocolo` (NONCLUSTERED): protocolo

**FKs (saída):**
- id_categoria → muralha.chamado_categoria.id
- id_prioridade → muralha.chamado_prioridade.id
- id_prioridade_solicitada → muralha.chamado_prioridade.id

**Referenciada por:**
- muralha.chamado_anexo.id_chamado
- muralha.chamado_historico.id_chamado
- muralha.chamado_notificacao.id_chamado

## muralha.chamado_anexo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_chamado | bigint | N |  |  |  |
| 3 | id_historico | bigint | S |  |  |  |
| 4 | nome_original | nvarchar(255) | N |  |  |  |
| 5 | caminho | nvarchar(400) | N |  |  |  |
| 6 | tipo_mime | varchar(100) | N |  |  |  |
| 7 | tamanho_bytes | bigint | N |  |  |  |
| 8 | sha256 | char(64) | N |  |  |  |
| 9 | dt_envio | datetime2 | N |  | (sysdatetime()) |  |
| 10 | id_usuario | int | N |  |  |  |

**Índices/Chaves:**
- IDX `ix_chamado_anexo_chamado` (NONCLUSTERED): id_chamado
- PK `pk_chamado_anexo` (CLUSTERED): id

**FKs (saída):**
- id_chamado → muralha.chamado.id
- id_historico → muralha.chamado_historico.id

## muralha.chamado_categoria

Linhas: ~22

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | nome | nvarchar(100) | N |  |  |  |
| 3 | id_pai | int | S |  |  |  |
| 4 | exige_equipamento | bit | N |  | ((0)) |  |
| 5 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `pk_chamado_categoria` (CLUSTERED): id

**FKs (saída):**
- id_pai → muralha.chamado_categoria.id

**Referenciada por:**
- muralha.chamado.id_categoria
- muralha.chamado_categoria.id_pai

## muralha.chamado_historico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_chamado | bigint | N |  |  |  |
| 3 | dt_evento | datetime2 | N |  | (sysdatetime()) |  |
| 4 | id_usuario | int | S |  |  |  |
| 5 | tipo | varchar(20) | N |  |  |  |
| 6 | status_anterior | varchar(30) | S |  |  |  |
| 7 | status_novo | varchar(30) | S |  |  |  |
| 8 | texto | nvarchar(2000) | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_chamado_historico_chamado` (NONCLUSTERED): id_chamado, dt_evento
- PK `pk_chamado_historico` (CLUSTERED): id

**FKs (saída):**
- id_chamado → muralha.chamado.id

**Referenciada por:**
- muralha.chamado_anexo.id_historico

## muralha.chamado_notificacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | bigint | N | S |  |  |
| 2 | id_chamado | bigint | N |  |  |  |
| 3 | tipo | varchar(30) | N |  |  |  |
| 4 | destinatario | varchar(200) | S |  |  |  |
| 5 | status | varchar(10) | N |  | ('PENDENTE') |  |
| 6 | tentativas | tinyint | N |  | ((0)) |  |
| 7 | dt_criacao | datetime2 | N |  | (sysdatetime()) |  |
| 8 | dt_envio | datetime2 | S |  |  |  |
| 9 | erro | nvarchar(500) | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_chamado_notificacao_status` (NONCLUSTERED): status, dt_criacao
- PK `pk_chamado_notificacao` (CLUSTERED): id

**FKs (saída):**
- id_chamado → muralha.chamado.id

## muralha.chamado_prioridade

Linhas: ~4

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id | int | N | S |  |  |
| 2 | codigo | varchar(20) | N |  |  |  |
| 3 | nome | nvarchar(50) | N |  |  |  |
| 4 | ordem | tinyint | N |  |  |  |
| 5 | minutos_atendimento | int | N |  |  |  |
| 6 | minutos_resolucao | int | N |  |  |  |
| 7 | cor | varchar(7) | N |  | ('#6c757d') |  |
| 8 | ativo | bit | N |  | ((1)) |  |

**Índices/Chaves:**
- PK `pk_chamado_prioridade` (CLUSTERED): id
- UNIQUE `uq_chamado_prioridade_codigo` (NONCLUSTERED): codigo

**Referenciada por:**
- muralha.chamado.id_prioridade
- muralha.chamado.id_prioridade_solicitada

## muralha.chamado_protocolo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | ano | smallint | N |  |  |  |
| 2 | ultimo | int | N |  |  |  |

**Índices/Chaves:**
- PK `pk_chamado_protocolo` (CLUSTERED): ano

