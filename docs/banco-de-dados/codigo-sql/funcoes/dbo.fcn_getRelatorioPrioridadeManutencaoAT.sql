CREATE FUNCTION [dbo].[fcn_getRelatorioPrioridadeManutencaoAT](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		REPLACE(convert(char(19), dataInclusao, 126),'T',' ') dataInclusao,
		numeroSerie,
		idPista,
		codigoFaixa,
		nomeFaixa,
		CASE 
			WHEN [dataUltimaDesconexao] < '2199-12-31 00:00:00.000'
				THEN [dataUltimaDesconexao] 
			ELSE 
				NULL 
		END AS [dataUltimaDesconexao],
		CASE 
			WHEN [dataUltimoArquivo] < '2199-12-31 00:00:00.000' 
				THEN [dataUltimoArquivo] 
			ELSE 
				NULL 
		END AS [dataUltimoArquivo],
		CASE 
			WHEN [dataUltimaInfracao] < '2199-12-31 00:00:00.000' 
				THEN [dataUltimaInfracao] 
			ELSE 
				NULL 
		END AS [dataUltimaInfracao],
		-- status DIV -------------------------------
		CASE 
			WHEN [statusDiv] = 2 
				THEN 'Defeituoso'
			WHEN [statusDiv] = 1 
				THEN 'Operacional'
			ELSE ''
		END AS [statusDiv],
		----------------------------------------------
		--[eventosSemaforo],
		-- status ConfigEquip -------------------------------
		CASE 
			WHEN statusConfigEquip = 0 
				THEN 'Desatualizado'
			WHEN statusConfigEquip = 1 
				THEN 'OK'
			ELSE ''
		END AS [statusConfigEquip],
		----------------------------------------------
		-- regra infracao desabilitada -------------------------------
		CASE 
			WHEN regraInfracaoDesabilitada = 0 
				THEN 'OK'
			WHEN regraInfracaoDesabilitada = 1 
				THEN 'Desatualizado'
			ELSE ''
		END AS [regraInfracaoDesabilitada],
		----------------------------------------------
		imagDefDia,
		imagDefNoite,
		imagDefTrans,
		semRecPlacaDia,
		semRecPlacaNoite,
		semRecPlacaTrans,
		CASE 
			WHEN dataUltimoEvento < '2199-12-31 00:00:00.000' 
				THEN [dataUltimoEvento] 
			ELSE 
				NULL 
		END AS [dataUltimoEvento],
		CASE 
			WHEN dataUltimaAgenda < '2199-12-31 00:00:00.000' 
				THEN [dataUltimaAgenda] 
			ELSE 
				NULL 
		END AS [dataUltimaAgenda],
		CASE 
			WHEN dataUltimoSincHorario < '2199-12-31 00:00:00.000' 
				THEN dataUltimoSincHorario 
			ELSE 
				NULL 
		END AS dataUltimoSincHorario,
		CASE 
			WHEN dataUltimaAtualizacaoBD < '2199-12-31 00:00:00.000' 
				THEN [dataUltimaAtualizacaoBD] 
			ELSE 
				NULL 
		END AS [dataUltimaAtualizacaoBD],
		CASE 
			WHEN dataUltimaDifRelogioServidor < '2199-12-31 00:00:00.000' 
				THEN [dataUltimaDifRelogioServidor] 
		ELSE 
			NULL 
		END AS [dataUltimaDifRelogioServidor],
		dataUltimaSemaforoOK,
		dataUltimaManutencao,
		iccid,
		dataICCID,
		mac,
		dataMAC,
		eventosSemaforo,

		CASE WHEN
		alertaAtivo = 1 AND COALESCE(motivo_falso_positivo,0) = 0 
		AND ( (COALESCE(motivo_ext_energia, 0) + COALESCE(motivo_ext_pavimento, 0) + COALESCE(motivo_ext_vandalismo, 0)) = 0 )
		AND
		(
		dataUltimaDesconexao < DATEADD(HOUR, -12, GETDATE()) --*
		OR dataUltimoArquivo < DATEADD(HOUR, -12, GETDATE()) --*
		OR dataUltimaInfracao < DATEADD(HOUR, -12, GETDATE()) --*
		OR imagDefDia > 15 --*
		OR imagDefNoite > 40 --*
		--OR imagDefTrans > 40
		--OR dataUltimaSemaforoOK < GETDATE() - 2
		OR statusDiv = 2 --OR statusDiv IS NULL --*
		--OR statusConfigEquip = 0
		--OR regraInfracaoDesabilitada = 1
		OR dataUltimaAgenda < GETDATE() - 1 --*
		OR dataUltimoSincHorario < GETDATE() - 1 --*
		OR DATEDIFF(HOUR, dataUltimaAtualizacaoBD ,GETDATE()) > 84 --*
		OR dataUltimoEvento < GETDATE() - 1 --*
		OR semRecPlacaDia > 60 --*
		OR semRecPlacaNoite > 85 --*
		--OR semRecPlacaTrans > 85
		--OR statusDiv = 2
		--OR regraInfracaoDesabilitada = 1
		--OR statusConfigEquip = 0
		--OR [dataUltimaDifRelogioServidor] > GETDATE() - 3
		--OR semRecPlacaDia > 60
		--OR semRecPlacaNoite > 85
		--OR semRecPlacaTrans > 85
		OR eventosSemaforo > 100 --*
		)
		THEN 'CRITICO' ELSE 'ALERTA' END AS TipoAlerta

	FROM 
		painel_contrato_vigente_se (nolock) 
)

