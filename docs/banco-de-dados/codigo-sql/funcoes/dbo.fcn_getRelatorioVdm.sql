CREATE FUNCTION [dbo].[fcn_getRelatorioVdm](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	--DECLARE @dataInicio DATE = '2016-07-23', @dataFim DATE = '2016-07-23'
	SELECT sub1.[N/S],
		   sub1.[FX],
		   sub1.[NOME],
		   AVG(sub1.[VOLUME]) AS VDM
	FROM   fcn_getRelatorioVolume(@dataInicio, @dataFim) AS sub1
		   INNER JOIN (
						--DECLARE @dataInicio DATE = '2016-07-23', @dataFim DATE = '2016-07-23'
						SELECT vol.[N/S],
							   vol.[FX],
							   AVG(vol.[VOLUME]) AS MEDIA_COMPLETA
						FROM   fcn_getRelatorioVolume(@dataInicio, @dataFim) AS vol
						GROUP BY
							   [N/S],
							   [FX]
		   ) AS sub2
				ON  sub1.[N/S] = sub2.[N/S]
					AND sub1.FX = sub2.FX

	WHERE  CAST(sub1.VOLUME AS FLOAT) / CAST(sub2.MEDIA_COMPLETA AS FLOAT) > 0.3
	GROUP BY
		   sub1.[N/S],
		   sub1.[FX],
		   sub1.[NOME]

)
