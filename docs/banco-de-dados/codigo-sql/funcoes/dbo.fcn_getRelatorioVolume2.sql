CREATE FUNCTION [dbo].[fcn_getRelatorioVolume2](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(-- Cálculo Baseado na tabela 'veiculo sumarizado'

	--DECLARE @dataInicio DATE = '2015-03-11', @dataFim DATE = '2015-03-11'
	SELECT
		cem.serie_equipamento [N/S],
		cem.cod_pista_alternativo AS [FX],
		cem.descricao AS [NOME],
		sub.DIA AS [DIA],
		sub.[HORA],
		sub.VOLUME
	FROM
		(SELECT
			vs.id_local,
			CASE WHEN vs.id_local = 7811 AND vs.pista = 1 THEN 5
				 WHEN vs.id_local = 7811 AND vs.pista = 2 THEN 6
				 WHEN vs.id_local = 7812 AND vs.pista = 3 THEN 9
				 ELSE vs.pista
			END pista,
			vs.data AS [DIA],
			vs.hora AS [HORA],
			SUM (vs.trafego) AS [VOLUME]			
		FROM
			veiculo_sumarizado vs (nolock)
		WHERE
			vs.data BETWEEN @dataInicio AND @dataFim
			
			--Alterado O.S 101 - Auditoria CET
			--Luiz Amaral 22/07/2015
			--and cast(vs.data as date) > cast(dateadd(day, -45, getdate()) as date)

		GROUP BY
			vs.id_local, 
			vs.pista, 
			vs.data, 
			vs.hora
		) AS sub
		INNER JOIN configuracao_equipamento_medicao cem (nolock)
			ON  cem.id_local = sub.id_local
				AND cem.cod_pista_alternativo = sub.pista
		WHERE cem.serie_equipamento IN (2014093001, 2014093002, 2014093003)

)
