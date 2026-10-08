
CREATE FUNCTION [dbo].[fcn_getAlertaExcessoEventos] (@threshold AS INT)
RETURNS TABLE
AS
RETURN 
(  
	SELECT TOP 100 
		proprietario,
		evento,
		COUNT(*) as total
	FROM eventos_csx_pesquisa (nolock)
	WHERE	cast(data_hora as date) = cast(GETDATE() as date) 
		AND id_evento <> 24
	GROUP BY evento, proprietario
	HAVING COUNT(*) >= @threshold
	ORDER BY 3 DESC
)





