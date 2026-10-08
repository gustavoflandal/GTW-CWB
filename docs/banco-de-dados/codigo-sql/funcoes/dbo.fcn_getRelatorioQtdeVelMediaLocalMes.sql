CREATE FUNCTION [dbo].[fcn_getRelatorioQtdeVelMediaLocalMes](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
	SELECT TOP 100 PERCENT 
		cep.cod_pista AS [Cód. pista], 
		cep.nome_pista AS [Nome pista], 
		CONVERT(VARCHAR(7), vs.data, 126) AS [Ano-Mês], 
		SUM(trafego) AS [Qtde. carros], 
		AVG(media_velocidade) AS [Velocidade média]
	FROM veiculo_sumarizado vs (nolock)
		INNER JOIN local_vigente lv (nolock) 
			ON lv.id_local = vs.id_local
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON	cep.id_pista = vs.pista 
			AND cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
	WHERE 
		(cast(vs.data as date) BETWEEN @dataInicio AND @dataFim)
	GROUP BY 
		cep.cod_pista, 
		cep.nome_pista, 
		CONVERT(VARCHAR(7), vs.data, 126)
	ORDER BY 
		CONVERT(VARCHAR(7), vs.data, 126)
)


