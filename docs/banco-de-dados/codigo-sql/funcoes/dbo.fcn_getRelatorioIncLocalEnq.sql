
CREATE FUNCTION [dbo].[fcn_getRelatorioIncLocalEnq](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		TOP 100 PERCENT lv.id_local, 
		lv.serie_equipamento, 
		DATEPART(DAY, i.data) dia_mes, 
		CASE WHEN CAST(i.data AS TIME) BETWEEN '08:00:00' AND '18:00:00' THEN 'DIURNO' ELSE 'NOTURNO' END AS periodo,
		SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS consistente,
		SUM(CASE WHEN i.id_inconsistencia = 0 THEN 0 ELSE 1 END) AS inconsistente
	FROM infracao i (NOLOCK) 
	JOIN local_vigente lv (NOLOCK) 
		ON i.id_local = lv.id_local 
	left join infracao_remessa ir
		on ir.id_infracao = i.id_infracao	
	left join remessa r
		on r.id_remessa = ir.id_remessa
	WHERE 
		CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim 

		--Alterado O.S 101 - Auditoria CET
		--Luiz Amaral 22/07/2015
		AND cast(i.data as date) > dateadd(day, -45, cast(getdate() as date))
		AND r.data_validacao is null

	GROUP BY 
		lv.id_local, 
		lv.serie_equipamento, 
		DATEPART(DAY, i.data), 
		CASE WHEN CAST(i.data AS TIME) BETWEEN '08:00:00' AND '18:00:00' THEN 'DIURNO' ELSE 'NOTURNO' END
	ORDER BY 3,1,2,4
)
