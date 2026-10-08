# Tabelas — schema `dbo` — grupo `infracao`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.infracao

Linhas: ~73587

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N | S |  |  |
| 2 | id_imagem_local | int | N |  |  |  |
| 3 | placa | char(7) | S |  |  |  |
| 4 | id_enquadramento | int | N |  |  |  |
| 5 | id_inconsistencia | int | S |  |  |  |
| 6 | id_processo_concluido | int | S |  |  |  |
| 7 | id_processo | int | S |  |  |  |
| 8 | id_veiculo | bigint | N |  |  |  |
| 9 | id_local | int | N |  |  |  |
| 10 | sequencia_local | tinyint | S |  |  |  |
| 11 | pista | tinyint | N |  |  |  |
| 12 | data | datetime | N |  |  |  |
| 13 | id_usuario_atual | int | S |  |  |  |
| 14 | id_usuario_final | int | S |  |  |  |
| 15 | velocidade_limite | int | S |  |  |  |
| 16 | velocidade_considerada | int | S |  |  |  |
| 17 | segundos_tolerancia | decimal(8,3) | S |  |  |  |
| 18 | id_liberacao | int | S |  |  |  |
| 19 | espera | bit | S |  |  |  |
| 20 | tempo_vermelho_detec | decimal(8,3) | S |  |  |  |
| 21 | status_bloqueio | int | N |  | ((0)) |  |
| 22 | data_entrada | datetime | S |  | (getdate()) |  |
| 23 | data_afericao | datetime | S |  |  |  |
| 24 | id_captura | uniqueidentifier | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_infracao_data` (NONCLUSTERED): data
- IDX `IX_infracao_data_local_sequencia` (NONCLUSTERED): data
- IDX `IX_infracao_data_placa_inconsistencia` (NONCLUSTERED): data
- IDX `IX_infracao_data_processo` (NONCLUSTERED): data, id_processo
- IDX `IX_infracao_enquadramento_data` (NONCLUSTERED): id_enquadramento, data
- IDX `IX_infracao_enquadramento_processo_concluido_proc_data` (NONCLUSTERED): id_enquadramento, id_processo_concluido, id_processo, data
- IDX `IX_infracao_enquadramento_veiculo` (NONCLUSTERED): id_enquadramento, id_veiculo
- IDX `IX_infracao_id_captura` (NONCLUSTERED): id_captura
- IDX `IX_infracao_imagem_local` (NONCLUSTERED): id_imagem_local, id_local
- IDX `IX_infracao_inconsistencia_data_enquadramento` (NONCLUSTERED): id_inconsistencia, data, id_enquadramento
- IDX `IX_infracao_inconsistencia_enquadramento_processo_concluido_processo_data` (NONCLUSTERED): id_inconsistencia, id_enquadramento, id_processo_concluido, id_processo, data
- IDX `IX_infracao_local_data_enquadramento` (NONCLUSTERED): data, id_local
- IDX `IX_infracao_local_pista_data` (NONCLUSTERED): id_local, pista, data
- IDX `IX_infracao_placa_enquadramento_data` (NONCLUSTERED): placa, id_enquadramento, data
- IDX `IX_infracao_processo_data` (NONCLUSTERED): id_processo, data
- IDX `IX_infracao_processo_espera_usuario_atual_bloqueio` (NONCLUSTERED): id_processo, espera, id_usuario_atual, status_bloqueio
- IDX `IX_infracao_processo_usuario_atual` (NONCLUSTERED): id_processo, id_usuario_atual
- IDX `IX_infracao_veiculo` (NONCLUSTERED): id_veiculo
- PK `PK_infracao` (CLUSTERED): id_infracao
- UNIQUE `UK_infracao_veiculo_enquadramento` (NONCLUSTERED): id_veiculo, id_enquadramento

**FKs (saída):**
- id_enquadramento → dbo.enquadramento.id_enquadramento
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_local → dbo.local.id_local
- sequencia_local → dbo.local.sequencia_local
- id_processo → dbo.processo.id_processo
- id_processo_concluido → dbo.processo.id_processo
- id_usuario_atual → dbo.sis_usuario.id_usuario
- id_usuario_final → dbo.sis_usuario.id_usuario
- id_veiculo → dbo.veiculo.id_veiculo
- id_captura → ia.veiculo_caracteristica.id_captura

**Referenciada por:**
- dbo.agendamento_processamento.id_infracao
- dbo.imagem_ar.id_infracao
- dbo.infracao_imagem.id_infracao
- dbo.infracao_janela.id_infracao
- dbo.infracao_notificacao.id_infracao
- dbo.infracao_obliteracao.id_infracao
- dbo.infracao_processo.id_infracao
- dbo.infracao_processo_concluido.id_infracao
- dbo.infracao_rejeita_amostra.id_infracao
- dbo.infracao_remessa.id_infracao
- dbo.infracao_remessa_excluida.id_infracao
- dbo.solicitacao_auditoria_infracao.id_infracao

## dbo.infracao_contestacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | id_local | int | N |  |  |  |
| 4 | descricao_local | varchar(80) | N |  |  |  |
| 5 | cod_pista_prodam | int | N |  |  |  |
| 6 | pista | int | N |  |  |  |
| 7 | id_enquadramento | int | N |  |  |  |
| 8 | descricao_enquadramento | varchar(100) | N |  |  |  |
| 9 | data_analise_cai | date | N |  |  |  |
| 10 | placa_cai | char(7) | S |  |  |  |
| 11 | inconsistencia_cai | int | S |  |  |  |
| 12 | data_analise_cav | date | S |  |  |  |
| 13 | placa_cav | char(7) | N |  |  |  |
| 14 | inconsistencia_cav | int | N |  |  |  |
| 15 | marca_cai | int | S |  |  |  |
| 16 | marca_cav | int | N |  |  |  |
| 17 | especie_cai | int | S |  |  |  |
| 18 | especie_cav | int | N |  |  |  |
| 19 | erro_placa | int | N |  |  |  |
| 20 | erro_consistencia | int | N |  |  |  |
| 21 | id_usuario_cai | int | N |  |  |  |
| 22 | id_usuario_cav | int | S |  |  |  |
| 23 | id_processo_contestacao | tinyint | N |  | ((1)) |  |
| 24 | decisao | tinyint | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_infracao_contestacao_processo` (NONCLUSTERED): id_processo_contestacao
- PK `PK__infracao__CDA77983EFA41CB1` (CLUSTERED): id_infracao

## dbo.infracao_contestacao_cav

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | S |  |  |  |
| 2 | tipo | varchar(2) | S |  |  |  |
| 3 | codigo_externo | int | S |  |  |  |
| 4 | sequencia | smallint | S |  |  |  |
| 5 | id_infracao_cai | int | S |  |  |  |
| 6 | id_remessa_cai | int | S |  |  |  |

## dbo.infracao_contestacao_decisao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_decisao | int | N |  |  |  |
| 2 | descricao | varchar(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__infracao__C18DD8421C0660F5` (CLUSTERED): id_decisao

## dbo.infracao_contestacao_processo

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_processo_contestacao | int | N |  |  |  |
| 2 | descricao | varchar(30) | N |  |  |  |

**Índices/Chaves:**
- PK `PK__infracao__FF24C69939E5EA61` (CLUSTERED): id_processo_contestacao

## dbo.infracao_contestacao_sem_imagem

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_veiculo | bigint | N |  |  |  |
| 3 | data | datetime | N |  |  |  |
| 4 | imagem | int | N |  |  |  |
| 5 | id_imagem | int | N |  |  |  |
| 6 | URL | varchar(132) | S |  |  |  |

## dbo.infracao_erro_desconsiderar

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N | S |  |  |

## dbo.infracao_imagem

Linhas: ~72061

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_imagem_pan | int | S |  |  |  |
| 3 | id_imagem_obj | int | S |  |  |  |
| 4 | id_imagem_pan2 | int | S |  |  |  |

**Índices/Chaves:**
- IDX `ix_infracao_imagem_infracao_imagem_pan` (NONCLUSTERED): id_infracao, id_imagem_pan
- PK `PK_infracao_imagem` (CLUSTERED): id_infracao

**FKs (saída):**
- id_imagem_obj → dbo.imagem_info.id_imagem
- id_imagem_pan → dbo.imagem_info.id_imagem
- id_imagem_pan2 → dbo.imagem_info.id_imagem
- id_infracao → dbo.infracao.id_infracao

## dbo.infracao_importacao

Linhas: ~34

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_veiculo_unic | bigint | N |  |  |  |
| 2 | id_imagem_local | int | N |  |  |  |
| 3 | id_enquadramento | int | N |  |  |  |
| 4 | velocidade_limite | int | S |  |  |  |
| 5 | velocidade_considerada | int | S |  |  |  |
| 6 | segundos_tolerancia | decimal(8,3) | S |  |  |  |
| 7 | tempo_vermelho_detec | decimal(8,3) | S |  |  |  |
| 8 | data_afericao | datetime | S |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_importacao` (CLUSTERED): id_veiculo_unic

**FKs (saída):**
- id_enquadramento → dbo.enquadramento.id_enquadramento

## dbo.infracao_janela

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_processo | int | N |  |  |  |
| 3 | id_usuario | int | N |  |  |  |
| 4 | data_infracao_janela | datetime | N |  | (getdate()) |  |
| 5 | id_janela_seq | bigint | N | S |  |  |
| 6 | processado | bit | N |  | ((0)) |  |

**Índices/Chaves:**
- IDX `IX_infracao_janela_usuario` (NONCLUSTERED): id_usuario
- PK `PK_infracao_janela` (CLUSTERED): id_infracao

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_processo → dbo.processo.id_processo
- id_usuario → dbo.sis_usuario.id_usuario

## dbo.infracao_notificacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data_nai | datetime | N |  |  |  |
| 2 | data_nio | datetime | S |  |  |  |
| 3 | data_maxdefesa | datetime | N |  |  |  |
| 4 | data_vencimento | datetime | S |  |  |  |
| 5 | data_entrada | datetime | S |  |  |  |
| 6 | desc_defesa | char(50) | S |  |  |  |
| 7 | proprietario | char(50) | N |  |  |  |
| 8 | cpf_cnpj | char(18) | S |  |  |  |
| 9 | rg | char(8) | S |  |  |  |
| 10 | endereco | char(60) | N |  |  |  |
| 11 | cep | char(8) | S |  |  |  |
| 12 | cidade | char(30) | S |  |  |  |
| 13 | uf | char(2) | S |  |  |  |
| 14 | telefone | char(20) | S |  |  |  |
| 15 | infrator | char(50) | S |  |  |  |
| 16 | condutor | char(50) | S |  |  |  |
| 17 | cnh_doc | char(10) | S |  |  |  |
| 18 | cnh_reg | char(10) | S |  |  |  |
| 19 | cnh_uf | char(2) | S |  |  |  |
| 20 | id_infracao | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_notificacao` (NONCLUSTERED): id_infracao

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao

## dbo.infracao_obliteracao

Linhas: ~24

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | x | int | N |  |  |  |
| 3 | y | int | N |  |  |  |
| 4 | largura | int | N |  |  |  |
| 5 | altura | int | N |  |  |  |
| 6 | id_imagem | int | N |  |  |  |
| 7 | sequencia_obliteracao | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_infracao_obliteracao_id_imagem` (NONCLUSTERED): id_imagem
- PK `PK_infracao_obliteracao` (CLUSTERED): id_infracao, id_imagem, sequencia_obliteracao

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_infracao → dbo.infracao.id_infracao

## dbo.infracao_processo

Linhas: ~100917

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo | int | N | S |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | tempo | int | N |  |  |  |
| 4 | id_infracao | int | N |  |  |  |
| 5 | id_processo | int | N |  |  |  |
| 6 | id_inconsistencia | int | S |  |  |  |
| 7 | id_imagem | int | S |  |  |  |
| 8 | id_usuario | int | N |  |  |  |
| 9 | status_processo | int | N |  |  |  |
| 10 | tempo_cliente | int | S |  |  |  |
| 11 | erro_oblit | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_infracao_processo_data_tempo` (NONCLUSTERED): data, tempo
- IDX `IX_infracao_processo_id_inconsistencia_id_processo` (NONCLUSTERED): id_inconsistencia, id_processo
- IDX `IX_infracao_processo_id_processo_status` (NONCLUSTERED): id_processo, status_processo
- IDX `IX_infracao_processo_id_usuario` (NONCLUSTERED): id_usuario
- IDX `IX_infracao_processo_infracao` (NONCLUSTERED): id_infracao
- IDX `IX_infracao_processo_infracao_processo_usuario` (NONCLUSTERED): id_infracao, id_processo, id_usuario
- IDX `IX_infracao_processo_processo` (NONCLUSTERED): id_processo
- PK `PK_infracao_processo` (CLUSTERED): id_infracao_processo

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_infracao → dbo.infracao.id_infracao
- id_usuario → dbo.sis_usuario.id_usuario

**Referenciada por:**
- dbo.infracao_processo_digitacao.id_infracao_processo
- dbo.infracao_processo_filtro.id_infracao_processo
- dbo.infracao_processo_obliteracao.id_infracao_processo

## dbo.infracao_processo_concluido

Linhas: ~80635

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo_concluido | int | N | S |  |  |
| 2 | id_infracao | int | N |  |  |  |
| 3 | id_processo | int | N |  |  |  |
| 4 | data_conclusao | datetime | N |  |  |  |
| 5 | id_inconsistencia | int | N |  |  |  |
| 6 | id_imagem | int | S |  |  |  |

**Índices/Chaves:**
- UNIQUE `IX_infracao_processo_concluido_infracao_processo` (NONCLUSTERED): id_infracao, id_processo
- PK `PK_infracao_processo_concluido` (CLUSTERED): id_infracao_processo_concluido

**FKs (saída):**
- id_imagem → dbo.imagem_info.id_imagem
- id_inconsistencia → dbo.inconsistencia.id_inconsistencia
- id_infracao → dbo.infracao.id_infracao
- id_processo → dbo.processo.id_processo

## dbo.infracao_processo_contestacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_infracao_processo | int | N |  |  |  |
| 3 | id_processo_contestacao | int | N |  |  |  |
| 4 | decisao | tinyint | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_infracao_processo_contestacao_decisao` (NONCLUSTERED): id_processo_contestacao, decisao
- IDX `IX_infracao_processo_contestacao_id_processo` (NONCLUSTERED): id_processo_contestacao
- IDX `IX_infracao_processo_contestacao_infracacao_id_processo` (NONCLUSTERED): id_infracao, id_processo_contestacao

## dbo.infracao_processo_digitacao

Linhas: ~17

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | placa | char(7) | S |  |  |  |
| 2 | id_infracao_processo | int | N |  |  |  |
| 3 | id_marca_cet | int | S |  |  |  |
| 4 | id_especie | int | S |  |  |  |
| 5 | uf | char(2) | S |  |  |  |
| 6 | id_marca | int | S |  |  |  |
| 7 | id_tipo | int | S |  |  |  |
| 8 | id_categoria | int | S |  |  |  |
| 9 | classificacao_veiculo | varchar(10) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_processo_digitacao` (CLUSTERED): id_infracao_processo

**FKs (saída):**
- id_infracao_processo → dbo.infracao_processo.id_infracao_processo

## dbo.infracao_processo_filtro

Linhas: ~1089

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo | int | N |  |  |  |
| 2 | id_filtro | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_processo_filtro` (CLUSTERED): id_infracao_processo

**FKs (saída):**
- id_filtro → dbo.filtro.id_filtro
- id_infracao_processo → dbo.infracao_processo.id_infracao_processo

## dbo.infracao_processo_historico

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo | int | N |  |  |  |
| 2 | data | datetime | N |  |  |  |
| 3 | id_infracao | int | N |  |  |  |
| 4 | id_enquadramento | int | N |  |  |  |
| 5 | id_processo | int | N |  |  |  |
| 6 | desc_processo | char(30) | N |  |  |  |
| 7 | id_usuario | int | N |  |  |  |
| 8 | nome_usuario | char(60) | N |  |  |  |
| 9 | id_inconsistencia | int | S |  |  |  |
| 10 | desc_inconsistencia | char(70) | N |  |  |  |
| 11 | status_processo | int | N |  |  |  |
| 12 | data_gravacao | datetime | N |  |  |  |

## dbo.infracao_processo_obliteracao

Linhas: ~50

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo | int | N |  |  |  |
| 2 | x | int | N |  |  |  |
| 3 | y | int | N |  |  |  |
| 4 | largura | int | N |  |  |  |
| 5 | altura | int | N |  |  |  |
| 6 | id_imagem | int | N |  |  |  |
| 7 | sequencia_obliteracao | int | N |  |  |  |

**Índices/Chaves:**
- IDX `IX_infracao_obliteracao_id_imagem_processo_sequencia` (NONCLUSTERED): id_imagem
- PK `PK_infracao_processo_obliteracao` (CLUSTERED): id_infracao_processo, id_imagem, sequencia_obliteracao

**FKs (saída):**
- id_infracao_processo → dbo.infracao_processo.id_infracao_processo

## dbo.infracao_processo_observacao

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo_observacao | int | N | S |  |  |
| 2 | id_usuario | int | N |  |  |  |
| 3 | id_infracao | int | N |  |  |  |
| 4 | id_processo | int | N |  |  |  |
| 5 | id_infracao_processo | int | N |  |  |  |
| 6 | observacao | varchar(max) | S |  |  |  |

## dbo.infracao_processo_usuario_digitado

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao_processo | int | S |  |  |  |
| 2 | codigo_agente | int | S |  |  |  |
| 3 | nome_agente | varchar(50) | S |  |  |  |

## dbo.infracao_rejeita_amostra

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_rejeita_amostra` (CLUSTERED): id_infracao

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_infracao → dbo.infracao_rejeita_amostra.id_infracao

**Referenciada por:**
- dbo.infracao_rejeita_amostra.id_infracao

## dbo.infracao_remessa

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_remessa | int | N |  |  |  |
| 3 | auto | int | S |  |  |  |
| 4 | serie | char(10) | S |  |  |  |
| 5 | uf | char(2) | S |  |  |  |
| 6 | data_confirmacao | datetime | S |  |  |  |
| 7 | erro1 | int | S |  |  |  |
| 8 | erro2 | int | S |  |  |  |
| 9 | erro3 | int | S |  |  |  |
| 10 | sigla_infracao_cliente | char(5) | S |  |  |  |
| 11 | sequencia | int | N |  | ((1)) |  |

**Índices/Chaves:**
- IDX `IX_infracao_remessa_remessa_auto` (NONCLUSTERED): id_remessa, auto
- IDX `IX_infracao_remessa_remessa_infracao_auto` (NONCLUSTERED): id_remessa, id_infracao, auto
- IDX `IX_infracao_remessa_serie_414F1` (NONCLUSTERED): serie
- PK `PK_infracao_remessa` (NONCLUSTERED): id_infracao

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_remessa → dbo.remessa.id_remessa

## dbo.infracao_remessa_excluida

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | N |  |  |  |
| 2 | id_remessa | int | N |  |  |  |
| 3 | auto | int | S |  |  |  |
| 4 | serie | char(10) | S |  |  |  |
| 5 | uf | char(2) | S |  |  |  |
| 6 | data_confirmacao | datetime | S |  |  |  |
| 7 | erro1 | int | S |  |  |  |
| 8 | erro2 | int | S |  |  |  |
| 9 | erro3 | int | S |  |  |  |
| 10 | sigla_infracao_cliente | char(5) | S |  |  |  |

**Índices/Chaves:**
- PK `PK_infracao_remessa_excluida` (CLUSTERED): id_infracao, id_remessa

**FKs (saída):**
- id_infracao → dbo.infracao.id_infracao
- id_remessa → dbo.remessa_excluida.id_remessa

## dbo.infracao_remessa_nip

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_remessa_nip | int | S |  |  |  |
| 2 | id_infracao | int | S |  |  |  |
| 3 | id_status | smallint | S |  |  |  |

## dbo.infracao_video

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_infracao | int | S |  |  |  |
| 2 | endereco_video1 | varchar(125) | S |  |  |  |
| 3 | endereco_video2 | varchar(125) | S |  |  |  |

