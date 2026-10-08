

CREATE FUNCTION [dbo].[fcn_getAlertaCapturaTravado]()
RETURNS TABLE
AS
RETURN
(
	SELECT
		proprietario,
		Evento,
		COUNT(*) as Total
	FROM eventos_csx_pesquisa (nolock)
	WHERE	id_evento in ( 1 ) 
		AND data_hora > GETDATE() - 1
	GROUP BY proprietario,evento
)



