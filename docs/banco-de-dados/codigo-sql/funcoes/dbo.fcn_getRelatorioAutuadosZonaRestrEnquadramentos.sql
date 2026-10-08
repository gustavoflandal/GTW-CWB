CREATE FUNCTION [dbo].[fcn_getRelatorioAutuadosZonaRestrEnquadramentos](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
	SELECT TOP 100 PERCENT
		inf.id_enquadramento AS [Cód. Enquadramento],
		inf.placa AS [Placa],
		CAST(inf.data AS DATE) [Data],
		CAST(inf.data AS TIME) AS [Hora]
	FROM infracao inf (nolock)
		INNER JOIN solicitacao_auditoria_infracao sai (nolock) 
			ON sai.id_infracao = inf.id_infracao
	WHERE	inf.id_enquadramento IN (57461,57462,57463) 
		AND	inf.id_inconsistencia = 0 
		AND CAST(inf.data AS DATE) BETWEEN @dataInicio and @dataFim
	ORDER BY
		inf.id_enquadramento
)
