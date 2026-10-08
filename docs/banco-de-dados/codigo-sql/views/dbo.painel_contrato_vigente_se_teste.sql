CREATE VIEW [dbo].[painel_contrato_vigente_se_teste]
AS
SELECT
	pc.[numeroSerie],
	pc.[idPista],
	pc.[codigoFaixa],
	RTRIM(pc.[nomeFaixa]) AS [nomeFaixa],
	pc.[dataGeracao] as [dataUltimaAtualizacao],
		
	pc.[dataUltimaDesconexao] AS [dataUltimaDesconexao],
	pc.[dataUltimoArquivo] as [dataUltimoArquivo],
	pc.[dataUltimaInfracao] as [dataUltimaInfracao],

	CAST(pc.[statusDiv] AS int)as [statusDiv],

	pc.[imagDefDia] as [imagDefDia],
	pc.[imagDefNoite] as [imagDefNoite],
	pc.[imagDefTrans] as [imagDefTrans],
		
	pc.semRecPlacaDia as [semRecPlacaDia],
	pc.semRecPlacaNoite as [semRecPlacaNoite],
	pc.semRecPlacaTrans as [semRecPlacaTrans],
		
	pc.percOffline as [percOffline],

	pc.numeroVezesCapturaIniciado as [vezesCapturaIniciado],
	pc.statusConfigEquip as [statusConfigEquip],
	pc.regraInfracaoDesabilitada as [regraInfracaoDesabilitada],
		
	(CASE WHEN (pc.dataUltimaDesconexao IS NULL) OR (pc.dataUltimaDesconexao >= GETDATE() - 1) THEN
			pc.[dataUltimoEvento]
		ELSE
			CAST('2199-12-31' AS DATETIME) -- Date().getTime == 0 java
		END) as [dataUltimoEvento],

	(CASE WHEN pc.[dataUltimoEvento] >= GETDATE() - 1 THEN
			pc.[dataUltimaAgenda]
		ELSE
			CAST('2199-12-31' AS DATETIME) -- Date().getTime == 0 java
		END) as [dataUltimaAgenda],
		
	(CASE WHEN pc.[dataUltimoEvento] >= GETDATE() - 1 THEN
			pc.[dataUltimoSincHorario] 
		ELSE
			CAST('2199-12-31' AS DATETIME) -- Date().getTime == 0 java
		END) as [dataUltimoSincHorario],
		
	(CASE WHEN pc.[dataUltimoEvento] >= GETDATE() - 1 THEN
			pc.[dataUltimaAtualizacaoBD]
		ELSE
			CAST('2199-12-31' AS DATETIME) -- Date().getTime == 0 java
		END) as [dataUltimaAtualizacaoBD],

	(CASE WHEN pc.[dataUltimoEvento] >= GETDATE() - 1 THEN
			pc.[dataUltimaDifRelogioServidor]
		ELSE
			CAST('2199-12-31' AS DATETIME) -- Date().getTime == 0 java
		END) as [dataUltimaDifRelogioServidor],

	pc.[dataUltimaManutencao] as [dataUltimaManutencao],
	pc.dataUltimaSemaforoOK,
		
	pc.[iccid] as [iccid],
	pc.[dataICCID] as [dataICCID],
	pc.[mac] as [mac],
	pc.[dataMAC] as [dataMAC],

	-- Alt. em 21/10/15
	CAST(COALESCE(pc.[infracoesMedia],0) AS INT) AS [infracoesMedia],
	CAST(COALESCE(pc.[infracoesMaximo],0) AS INT) AS [infracoesMaximo],
	CAST(COALESCE(pc.[infracoesCoeficiente],0) AS INT) AS [infracoesCoeficiente],

	pca.[informacao_adicional] AS [informacaoAdicional],
	CAST(pca.motivo_ext_pavimento AS INT) as motivo_ext_pavimento,
	CAST(pca.motivo_ext_vandalismo AS INT) as motivo_ext_vandalismo,
	CAST(COALESCE(pca.ativo, 0) AS INT) as [alertaAtivo],
	score =CAST(
		COALESCE( DATEDIFF(HOUR, dataUltimaInfracao, GETDATE()) * 100 / 24.0, 0)
		+ COALESCE(imagDefDia,0)
		+ COALESCE(imagDefNoite ,0)
		+ COALESCE(DATEDIFF(HOUR, dataUltimaAgenda, GETDATE()) * 100 / (7 * 24.0),0)
		+ COALESCE(semRecPlacaDia,0)
		+ COALESCE(semRecPlacaNoite,0) 
		AS INT)		
FROM painel_contrato pc (nolock)
	LEFT JOIN painel_contrato_alerta pca (nolock)
		ON	pca.serie_equipamento = pc.numeroSerie 
			AND pca.id_pista = pc.idPista 
			AND pca.ativo = 1 -- somente ativos
WHERE pc.numeroSerie > 9907000 AND pc.numeroSerie < 2014000000
