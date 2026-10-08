
CREATE FUNCTION [dbo].[fcn_ImagensProcessamento] (@id_processo int , @consistentes bit, @inconsistentes bit )
RETURNS TABLE
AS
RETURN 
(
	SELECT	TOP 100 PERCENT
		Data = CAST( i.data as date), 
		Total = COUNT(*)
	FROM infracao i (nolock) 
		INNER JOIN processo p (nolock) 
			ON p.id_processo = i.id_processo
	WHERE	p.id_processo = @id_processo
		AND (( i.id_inconsistencia = 0 AND @consistentes = 1 )
			OR ( i.id_inconsistencia > 0 AND @inconsistentes = 1 )
			OR (i.id_inconsistencia is NULL))
	GROUP BY 
		CAST( i.data AS DATE )
	ORDER BY 
		Data
)





