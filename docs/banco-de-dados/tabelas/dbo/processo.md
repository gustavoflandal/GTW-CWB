# Tabelas — schema `dbo` — grupo `processo`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.processo

Linhas: ~13

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo | int | N |  |  |  |
| 2 | nome | char(30) | S |  |  |  |
| 3 | id_processo_anterior | int | S |  |  |  |
| 4 | id_processo_proximo | int | S |  |  |  |
| 5 | numero_iteracoes_consistentes | int | S |  |  |  |
| 6 | numero_iteracoes_inconsistentes | int | S |  |  |  |
| 7 | janela | tinyint | N |  | ((10)) |  |
| 8 | sql_criterio_entrada | varchar(500) | S |  |  |  |
| 9 | recebe_consistentes_inconsistentes | bit | S |  | ((0)) |  |
| 10 | ativo | bit | S |  | ((0)) |  |
| 11 | aceita_consistente | bit | N |  | ((1)) |  |
| 12 | aceita_inconsistente | bit | N |  | ((1)) |  |
| 13 | execucao_automatica | bit | N |  |  |  |
| 14 | spu_execucao_automatica | varchar(200) | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_processo_iteracoes` (NONCLUSTERED): id_processo, numero_iteracoes_consistentes, numero_iteracoes_inconsistentes
- IDX `IX_Processo_Processo` (NONCLUSTERED): id_processo
- PK `PK_processo` (NONCLUSTERED): id_processo

**Referenciada por:**
- dbo.agendamento_processamento.id_processo
- dbo.filtro.id_processo
- dbo.infracao.id_processo
- dbo.infracao.id_processo_concluido
- dbo.infracao_janela.id_processo
- dbo.infracao_processo_concluido.id_processo
- dbo.processo_inconsistencia.id_processo
- dbo.processo_ligacao.id_processo_destino
- dbo.processo_ligacao.id_processo_origem

## dbo.processo_excluir

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK__processo__3C7ED8A727416F2D` (CLUSTERED): id_processo

## dbo.processo_inconsistencia

Linhas: ~402

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo | int | N |  |  |  |
| 2 | id_inconsistencia | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_processo_inconsistencia` (NONCLUSTERED): id_processo, id_inconsistencia

**FKs (saída):**
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_processo → dbo.processo.id_processo

## dbo.processo_inconsistencia_bkp

Linhas: ~402

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo | int | N |  |  |  |
| 2 | id_inconsistencia | int | N |  |  |  |

## dbo.processo_ligacao

Linhas: ~18

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo_ligacao | int | N | S |  |  |
| 2 | prioridade | int | N |  |  |  |
| 3 | id_processo_origem | int | S |  |  |  |
| 4 | id_processo_destino | int | S |  |  |  |

**Índices/Chaves:**
- UNIQUE `UK_processo_ligacao_origem_destino` (NONCLUSTERED): id_processo_origem, id_processo_destino

**FKs (saída):**
- id_processo_origem → dbo.processo_ligacao.id_processo_origem
- id_processo_destino → dbo.processo_ligacao.id_processo_destino
- id_processo_destino → dbo.processo.id_processo
- id_processo_origem → dbo.processo.id_processo

**Referenciada por:**
- dbo.processo_ligacao.id_processo_origem
- dbo.processo_ligacao.id_processo_destino

## dbo.processo_medicao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo_medicao | int | N | S |  |  |
| 2 | mes | smallint | N |  |  |  |
| 3 | ano | smallint | N |  |  |  |
| 4 | periodo_ini | datetime | S |  |  |  |
| 5 | periodo_fim | datetime | S |  |  |  |
| 6 | data_criacao | datetime | N |  |  |  |
| 7 | data_exportacao | datetime | S |  |  |  |
| 8 | data_protocolo_exportacao | datetime | S |  |  |  |
| 9 | data_retorno_exportacao | datetime | S |  |  |  |
| 10 | data_complemento | datetime | S |  |  |  |
| 11 | data_protocolo_complemento | datetime | S |  |  |  |
| 12 | data_planilha_quantitativos | datetime | S |  |  |  |
| 13 | id_usuario_responsavel | int | S |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_processo_medicao_mes_ano` (NONCLUSTERED): mes, ano
- PK `PK_processo_medicao` (CLUSTERED): id_processo_medicao

**FKs (saída):**
- id_usuario_responsavel → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.processo_medicao_veiculo.id_processo_medicao

## dbo.processo_medicao_veiculo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo_medicao | int | N |  |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | etapa | tinyint | N |  | ((1)) |  |
| 4 | metrologica | bit | N |  |  |  |
| 5 | score_total | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_processo_medicao_veiculo` (CLUSTERED): id_processo_medicao, id_veiculo, etapa

**FKs (saída):**
- id_processo_medicao → dbo.processo_medicao.id_processo_medicao
- id_veiculo → dbo.veiculo.id_veiculo

