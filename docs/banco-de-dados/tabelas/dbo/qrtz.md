# Tabelas — schema `dbo` — grupo `qrtz`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.qrtz_blob_triggers

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_name | varchar(200) | N |  |  |  |
| 2 | trigger_group | varchar(200) | N |  |  |  |
| 3 | blob_data | image | S |  |  |  |

## dbo.qrtz_calendars

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | calendar_name | varchar(200) | N |  |  |  |
| 2 | calendar | image | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_calendars` (CLUSTERED): calendar_name

## dbo.qrtz_cron_triggers

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_name | varchar(200) | N |  |  |  |
| 2 | trigger_group | varchar(200) | N |  |  |  |
| 3 | cron_expression | varchar(120) | N |  |  |  |
| 4 | time_zone_id | varchar(80) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_cron_triggers` (CLUSTERED): trigger_name, trigger_group

**FKs (saída):**
- trigger_group → dbo.qrtz_triggers.trigger_group
- trigger_name → dbo.qrtz_triggers.trigger_name

## dbo.qrtz_fired_triggers

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | entry_id | varchar(95) | N |  |  |  |
| 2 | trigger_name | varchar(200) | N |  |  |  |
| 3 | trigger_group | varchar(200) | N |  |  |  |
| 4 | is_volatile | varchar(1) | N |  |  |  |
| 5 | instance_name | varchar(200) | N |  |  |  |
| 6 | fired_time | bigint | N |  |  |  |
| 7 | priority | int | N |  |  |  |
| 8 | state | varchar(16) | N |  |  |  |
| 9 | job_name | varchar(200) | S |  |  |  |
| 10 | job_group | varchar(200) | S |  |  |  |
| 11 | is_stateful | varchar(1) | S |  |  |  |
| 12 | requests_recovery | varchar(1) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_fired_triggers` (CLUSTERED): entry_id

## dbo.qrtz_job_details

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | job_name | varchar(200) | N |  |  |  |
| 2 | job_group | varchar(200) | N |  |  |  |
| 3 | description | varchar(250) | S |  |  |  |
| 4 | job_class_name | varchar(250) | N |  |  |  |
| 5 | is_durable | varchar(1) | N |  |  |  |
| 6 | is_volatile | varchar(1) | N |  |  |  |
| 7 | is_stateful | varchar(1) | N |  |  |  |
| 8 | requests_recovery | varchar(1) | N |  |  |  |
| 9 | job_data | image | S |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_job_details` (CLUSTERED): job_name, job_group

**Referenciada por:**
- dbo.qrtz_job_listeners.job_group
- dbo.qrtz_job_listeners.job_name
- dbo.qrtz_triggers.job_name
- dbo.qrtz_triggers.job_group

## dbo.qrtz_job_listeners

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | job_name | varchar(200) | N |  |  |  |
| 2 | job_group | varchar(200) | N |  |  |  |
| 3 | job_listener | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_job_listeners` (CLUSTERED): job_name, job_group, job_listener

**FKs (saída):**
- job_group → dbo.qrtz_job_details.job_group
- job_name → dbo.qrtz_job_details.job_name

## dbo.qrtz_locks

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | lock_name | varchar(40) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_locks` (CLUSTERED): lock_name

## dbo.qrtz_paused_trigger_grps

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_group | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_paused_trigger_grps` (CLUSTERED): trigger_group

## dbo.qrtz_scheduler_state

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | instance_name | varchar(200) | N |  |  |  |
| 2 | last_checkin_time | bigint | N |  |  |  |
| 3 | checkin_interval | bigint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_scheduler_state` (CLUSTERED): instance_name

## dbo.qrtz_simple_triggers

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_name | varchar(200) | N |  |  |  |
| 2 | trigger_group | varchar(200) | N |  |  |  |
| 3 | repeat_count | bigint | N |  |  |  |
| 4 | repeat_interval | bigint | N |  |  |  |
| 5 | times_triggered | bigint | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_simple_triggers` (CLUSTERED): trigger_name, trigger_group

**FKs (saída):**
- trigger_group → dbo.qrtz_triggers.trigger_group
- trigger_name → dbo.qrtz_triggers.trigger_name

## dbo.qrtz_trigger_listeners

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_name | varchar(200) | N |  |  |  |
| 2 | trigger_group | varchar(200) | N |  |  |  |
| 3 | trigger_listener | varchar(200) | N |  |  |  |

**Índices/Chaves:**
- PK `PK_qrtz_trigger_listeners` (CLUSTERED): trigger_name, trigger_group, trigger_listener

**FKs (saída):**
- trigger_name → dbo.qrtz_triggers.trigger_name
- trigger_group → dbo.qrtz_triggers.trigger_group

## dbo.qrtz_triggers

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | trigger_name | varchar(200) | N |  |  |  |
| 2 | trigger_group | varchar(200) | N |  |  |  |
| 3 | job_name | varchar(200) | N |  |  |  |
| 4 | job_group | varchar(200) | N |  |  |  |
| 5 | is_volatile | varchar(1) | N |  |  |  |
| 6 | description | varchar(250) | S |  |  |  |
| 7 | next_fire_time | bigint | S |  |  |  |
| 8 | prev_fire_time | bigint | S |  |  |  |
| 9 | priority | int | S |  |  |  |
| 10 | trigger_state | varchar(16) | N |  |  |  |
| 11 | trigger_type | varchar(8) | N |  |  |  |
| 12 | start_time | bigint | N |  |  |  |
| 13 | end_time | bigint | S |  |  |  |
| 14 | calendar_name | varchar(200) | S |  |  |  |
| 15 | misfire_instr | smallint | S |  |  |  |
| 16 | job_data | image | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_qrtz_triggers_next_fire_time_trigger_state` (NONCLUSTERED): next_fire_time, trigger_state
- PK `PK_qrtz_triggers` (CLUSTERED): trigger_name, trigger_group

**FKs (saída):**
- job_name → dbo.qrtz_job_details.job_name
- job_group → dbo.qrtz_job_details.job_group

**Referenciada por:**
- dbo.qrtz_cron_triggers.trigger_group
- dbo.qrtz_cron_triggers.trigger_name
- dbo.qrtz_simple_triggers.trigger_group
- dbo.qrtz_simple_triggers.trigger_name
- dbo.qrtz_trigger_listeners.trigger_name
- dbo.qrtz_trigger_listeners.trigger_group

