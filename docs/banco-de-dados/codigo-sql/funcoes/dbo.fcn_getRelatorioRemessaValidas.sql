CREATE FUNCTION [dbo].[fcn_getRelatorioRemessaValidas] (@dataInicio DATE , @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT TOP 100 PERCENT
		c.serie_equipamento AS [NS],
		cep.cod_pista_alternativo AS [Faixa],
		cep.nome_pista [Local],
		i.id_enquadramento AS [Enquadramento],
		CAST(i.data AS DATE) AS [Data],
		COUNT(*) AS [Válidas]
	FROM infracao i (nolock)
		INNER JOIN infracao_remessa ir (nolock) 
			ON ir.id_infracao = i.id_infracao
		INNER JOIN local l (nolock) 
			ON	i.id_local = l.id_local 
			AND i.sequencia_local =	l.sequencia_local
		INNER JOIN configuracao_equipamento c (nolock) 
			ON c.id_configuracao_equipamento = l.id_configuracao_equipamento
		INNER JOIN configuracao_equipamento_pista cep (nolock) 
			ON cep.id_configuracao_equipamento = c.id_configuracao_equipamento 
			AND i.pista = cep.id_pista
	WHERE	CAST(i.data AS DATE) >= @dataInicio 
		AND CAST(i.data AS DATE) < @dataFim
		AND ir.auto <> 0
	GROUP BY
	   c.serie_equipamento,
	   cep.cod_pista_alternativo,
	   cep.nome_pista,
	   i.id_enquadramento ,
	   CAST(i.data AS DATE)
	ORDER BY
	   c.serie_equipamento
)
