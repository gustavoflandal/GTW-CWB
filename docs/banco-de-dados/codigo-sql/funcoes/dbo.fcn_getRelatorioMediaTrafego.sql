CREATE FUNCTION [dbo].[fcn_getRelatorioMediaTrafego]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS

RETURN
(
	SELECT TOP 100 PERCENT
		SN,
		[Descr Local],
		AVG([Tráfego]) AS [Média p/ Dia]
	FROM
		[fcn_getRelatorioVolumeTrafego](@dataInicio, @dataFim)
	GROUP BY 
		SN,
		[Descr Local]
	ORDER BY
		SN
)






