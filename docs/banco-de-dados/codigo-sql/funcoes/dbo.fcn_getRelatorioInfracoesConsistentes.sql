
CREATE FUNCTION [dbo].[fcn_getRelatorioInfracoesConsistentes](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(

	SELECT TOP 100 PERCENT
		   CONVERT(VARCHAR(10), i.data, 103) AS [Data]
		  ,COUNT(i.id_infracao) AS [Total de Infrações]
		  ,SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END) as [Total de Infrações Consistentes]
	FROM   infracao i
		   INNER JOIN infracao_remessa ir
				ON  ir.id_infracao = i.id_infracao
			INNER join remessa r
		on r.id_remessa = ir.id_remessa
	WHERE  
		CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim

		--Alterado O.S 101 - Auditoria CET
		--Luiz Amaral 22/07/2015
		--AND cast(i.data as date) > dateadd(day, -45, cast(getdate() as date))
		--AND r.data_validacao is null


	GROUP BY
		   CONVERT(VARCHAR(10), i.data, 103)
	ORDER BY
		   CONVERT(VARCHAR(10), i.data, 103) ASC
)


