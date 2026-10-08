# Tabelas — schema `dbo` — grupo `painel`

Gerado a partir de GTW_MURALHA_DEV (SQL Server 2016). Contagens de linhas são aproximadas (data da extração: 2026-10-08).

## dbo.painel_contrato

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dataGeracao | datetime | N |  |  |  |
| 2 | numeroSerie | int | N |  |  |  |
| 3 | idPista | tinyint | N |  |  |  |
| 4 | codigoFaixa | int | N |  |  |  |
| 5 | nomeFaixa | nvarchar(100) | N |  |  |  |
| 6 | dataUltimaInfracao | datetime | S |  |  |  |
| 7 | idLocal | int | N |  |  |  |
| 8 | dataUltimaDesconexao | datetime | S |  |  |  |
| 9 | dataUltimoArquivo | datetime | S |  |  |  |
| 10 | statusDiv | int | S |  |  |  |
| 11 | imagDefDia | int | S |  |  |  |
| 12 | imagDefNoite | int | S |  |  |  |
| 13 | dataUltimoEvento | datetime | S |  |  |  |
| 14 | dataUltimaAgenda | datetime | S |  |  |  |
| 15 | dataUltimoSincHorario | datetime | S |  |  |  |
| 16 | dataUltimaAtualizacaoBD | datetime | S |  |  |  |
| 17 | dataUltimaDifRelogioServidor | datetime | S |  |  |  |
| 18 | dataUltimaManutencao | datetime | S |  |  |  |
| 19 | iccid | varchar(512) | S |  |  |  |
| 20 | dataICCID | datetime | S |  |  |  |
| 21 | mac | varchar(17) | S |  |  |  |
| 22 | dataMAC | datetime | S |  |  |  |
| 23 | percOffline | int | S |  |  |  |
| 24 | semRecPlacaDia | int | S |  |  |  |
| 25 | semRecPlacaNoite | int | S |  |  |  |
| 26 | numeroVezesCapturaIniciado | int | S |  |  |  |
| 27 | statusConfigEquip | int | S |  |  |  |
| 28 | dataUltimaSemaforoOK | datetime | S |  |  |  |
| 29 | regraInfracaoDesabilitada | int | S |  |  |  |
| 30 | imagDefTrans | int | S |  |  |  |
| 31 | semRecPlacaTrans | int | S |  |  |  |
| 32 | infracoesMedia | int | S |  |  |  |
| 33 | infracoesMaximo | int | S |  |  |  |
| 34 | infracoesCoeficiente | int | S |  |  |  |
| 35 | eventosSemaforo | int | S |  | ((0)) |  |
| 36 | statusSincConfigRelevante | int | S |  |  |  |
| 37 | versaoFirmwareCamera | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_painel_contrato_numeroSerie` (NONCLUSTERED): numeroSerie
- IDX `IX_painel_contrato_pista_local` (NONCLUSTERED): idPista, idLocal
- PK `PK_painel_contrato` (NONCLUSTERED): numeroSerie, idPista

## dbo.painel_contrato_alerta

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | id_painel_contrato_alerta | int | N | S |  |  |
| 2 | serie_equipamento | int | N |  |  |  |
| 3 | id_pista | int | N |  |  |  |
| 4 | data_inclusao | datetime | N |  | (getdate()) |  |
| 5 | data_atualizacao | datetime | S |  |  |  |
| 6 | id_usuario_atualizacao | int | S |  |  |  |
| 7 | ativo | bit | S |  | ((1)) |  |
| 8 | informacao_adicional | varchar(1000) | S |  |  |  |
| 9 | motivo_ext_energia | bit | S |  |  |  |
| 10 | motivo_ext_pavimento | bit | S |  |  |  |
| 11 | motivo_ext_vandalismo | bit | S |  |  |  |
| 12 | motivo_falso_positivo | bit | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_painel_contrato_alerta_ativo` (NONCLUSTERED): ativo
- PK `PK_painel_contrato_alerta` (CLUSTERED): id_painel_contrato_alerta

**FKs (saída):**
- id_usuario_atualizacao → dbo.sis_usuario.id_usuario

## dbo.painel_principal

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | dias_atraso_atual | int | S |  |  |  |
| 2 | dias_atraso_remessa | int | S |  |  |  |
| 3 | dias_atraso_validacao | int | S |  |  |  |
| 4 | total_atraso | int | N |  |  |  |
| 5 | total_atraso_ant | int | N |  |  |  |
| 6 | total_erro | int | N |  |  |  |
| 7 | total_erro_ant | int | N |  |  |  |
| 8 | dias_atraso_cad_isento | int | S |  |  |  |
| 9 | data_adicionado | datetime | S |  | (getdate()) |  |
| 10 | dias_atraso_reprovado | int | S |  |  |  |
| 11 | tempo_consulta | int | S |  | ((0)) |  |
| 12 | arquivos_verificados | int | S |  |  |  |
| 13 | data_arquivos_cav | datetime | S |  |  |  |
| 14 | erros_remessa_automatico | int | S |  |  |  |

**Índices/Chaves:**
- IDX `IX_painel_principal_data_adicionado` (NONCLUSTERED): data_adicionado

## dbo.painel_principal_erros

Linhas: ~0

| # | Coluna | Tipo | Null | Ident. | Default | Descrição |
|---|---|---|---|---|---|---|
| 1 | data | datetime | N |  | (getdate()) |  |
| 2 | erros_mes_atual | int | S |  |  |  |
| 3 | erros_mes_anterior | int | S |  |  |  |

**Índices/Chaves:**
- PK `PK__painel_p__D9DE21E0916DDB3C` (CLUSTERED): data

