CREATE FUNCTION [dbo].[fcn_getRelatorioConsistenteInconsistenteMes](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
	SELECT
		TOP 100 PERCENT
		CONVERT(VARCHAR(7), inf.data, 126) AS [Ano-Mês],
		SUM(CASE WHEN inf.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS [Consistentes],
		SUM(CASE WHEN inf.id_inconsistencia > 0 THEN 1 ELSE 0 END) AS [Inconsistentes],
		COUNT(inf.id_inconsistencia) AS [Total]
	FROM infracao inf (nolock)
		INNER JOIN local l (nolock) 
			ON	l.id_local = inf.id_local 
			AND l.sequencia_local = inf.sequencia_local
		INNER JOIN configuracao_equipamento_pista cep (nolock) 
			ON cep.id_configuracao_equipamento = l.id_configuracao_equipamento 
			AND cep.id_pista = inf.pista
		INNER JOIN configuracao_equipamento ce (nolock) 
			ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
	WHERE	CAST(inf.data AS date) BETWEEN @dataInicio AND @dataFim 
		AND id_enquadramento <> 1 
		AND	ce.data_inicio <= inf.data
	GROUP BY 
		CONVERT(VARCHAR(7), inf.data, 126)
	ORDER BY 
		CONVERT(VARCHAR(7), inf.data, 126)
)


