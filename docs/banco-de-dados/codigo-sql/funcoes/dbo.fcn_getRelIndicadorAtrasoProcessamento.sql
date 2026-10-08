CREATE FUNCTION [dbo].[fcn_getRelIndicadorAtrasoProcessamento](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	--DECLARE @dataInicio DATE = '2017-01-01', @dataFim DATE = '2017-07-31'
	SELECT ROW_NUMBER() OVER(ORDER BY dados.[Ano], dados.[Mes], dados.[AtrasoMedioPonderado]) AS ordem,
		   *
	FROM   (
				SELECT DATEPART(YEAR, r.data) AS [Ano],
					   DATEPART(MONTH, r.data) AS [Mes],
					   --SUM(CAST(r.total_infracao * DATEDIFF(DAY, r.data_inicial, r.data) AS NUMERIC(15,1))) / SUM(r.total_infracao) AS [Atraso médio ponderado],
					   MIN(DATEDIFF(DAY, r.data_inicial, r.data)) AS [AtrasoMinimo],
					   ROUND(CAST(SUM(CAST(r.total_infracao * DATEDIFF(DAY, r.data_inicial, r.data) AS FLOAT)) / SUM(r.total_infracao) AS FLOAT), 2) AS [AtrasoMedioPonderado],
					   --AVG(CAST(DATEDIFF(DAY, sa.data_imagens, sa.data_geracao) AS NUMERIC(15,1))) AS atraso_medio_total,
					   MAX(DATEDIFF(DAY, r.data_inicial, r.data)) AS [AtrasoMaximo]
					   --SUM(CASE WHEN DATEDIFF(DAY, r.data_inicial, r.data) > 8
								--THEN (DATEDIFF(DAY, r.data_inicial, r.data) - 8) * r.total_infracao
								--ELSE 0
						  -- END) AS [Imagem dia atrasadas]
				FROM   remessa r (NOLOCK)
					   INNER JOIN infracao_remessa ir (NOLOCK)
							ON  ir.id_remessa = r.id_remessa
					   INNER JOIN infracao i (NOLOCK)
							ON  i.id_infracao = ir.id_infracao
				WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
				GROUP BY
					   DATEPART(YEAR, r.data),
					   DATEPART(MONTH, r.data)
				--ORDER BY
				--	   1, 2, 3
	) dados

)
