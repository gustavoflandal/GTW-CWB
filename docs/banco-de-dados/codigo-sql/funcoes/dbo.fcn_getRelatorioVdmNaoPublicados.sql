CREATE FUNCTION [dbo].[fcn_getRelatorioVdmNaoPublicados](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		--DECLARE @dataInicio DATE = '2017-10-24', @dataFim DATE = '2017-10-31'
		SELECT TOP 100 PERCENT
			   result.[Cod. Equipamento]
			  ,result.[Descrição do Local]
			  ,result.[Data do Fluxo]
			  ,result.Pista
			  ,SUM(result.Quantidade) AS Quantidade
		FROM   (
					SELECT lv.id_local AS [Cod. Equipamento]
						  ,lv.nome AS [Descrição do Local]
						  ,CONVERT(VARCHAR(10), CAST(v.data AS DATE), 103) AS [Data do Fluxo]
						  ,v.pista AS [Pista]
						  ,COUNT(*) AS [Quantidade]
						  ,CAST(v.data AS DATE) AS data
					FROM   local_vigente lv (NOLOCK)
						   INNER JOIN veiculo_pesquisa v (NOLOCK)
								ON  lv.id_local = v.id_local
					WHERE  CAST(v.data AS DATE) BETWEEN @dataInicio AND @dataFim
						   AND lv.data_inicio > CAST(CAST(GETDATE() AS DATE) AS DATETIME)
						   --AND lv.id_local = 7267
					GROUP BY
						   lv.id_local
						  ,lv.nome
						  ,v.pista
						  ,CAST(v.data AS DATE)
		) result

		GROUP BY
			   result.[Cod. Equipamento]
			  ,result.[Descrição do Local]
			  ,result.[Data do Fluxo]
			  ,result.Pista
			  ,result.data
		ORDER BY
			   result.[Cod. Equipamento]
			  ,result.data
			  ,result.pista 
	)
