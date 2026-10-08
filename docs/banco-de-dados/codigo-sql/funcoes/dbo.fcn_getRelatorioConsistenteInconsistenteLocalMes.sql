CREATE FUNCTION [dbo].[fcn_getRelatorioConsistenteInconsistenteLocalMes](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
	SELECT TOP 100 PERCENT
		cep.cod_pista AS [Cód. local], 
		cep.nome_pista AS [Desc. local],
		CONVERT(VARCHAR(7), inf.data, 126) AS [Ano-Mês],
		SUM(CASE WHEN inf.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS [Consistentes],
		sum(CASE WHEN inf.id_inconsistencia > 0 THEN 1 ELSE 0 END) AS [Inconsistente],
		COUNT(inf.id_inconsistencia) AS [Total]
	FROM infracao inf (nolock)
		INNER JOIN local l (nolock)
			ON	l.id_local = inf.id_local 
			AND l.sequencia_local = inf.sequencia_local
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON	cep.id_configuracao_equipamento = l.id_configuracao_equipamento
			AND cep.id_pista = inf.pista
		INNER JOIN configuracao_equipamento ce (nolock) 
			ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
		LEFT JOIN infracao_remessa ir (NOLOCK)
			ON ir.id_infracao = inf.id_infracao
		LEFT JOIN remessa r (NOLOCK)
			ON r.id_remessa = ir.id_remessa
	WHERE	(cast(inf.data as date) BETWEEN @dataInicio AND @dataFim) 
		AND inf.id_enquadramento <> 1 
		AND ce.data_inicio <= inf.data
		
		
		-- Alterado O.S. 101 - Auditoria CET
		-- Thiago Surgik - 22/07/2015
		AND CAST(inf.data AS DATE) >= CAST(DATEADD(DAY, -45, GETDATE()) AS DATE)
		AND r.data_validacao IS NULL

	GROUP BY 
		cep.cod_pista, 
		cep.nome_pista, 
		CONVERT(VARCHAR(7), inf.data, 126)
	ORDER BY 
		CONVERT(VARCHAR(7), inf.data, 126)
)



