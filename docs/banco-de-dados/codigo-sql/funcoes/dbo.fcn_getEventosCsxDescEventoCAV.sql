/****************************************************************************************************************************************************
* eventos_csx_desc_evento
****************************************************************************************************************************************************/
CREATE FUNCTION [dbo].[fcn_getEventosCsxDescEventoCAV]()
RETURNS TABLE
AS
RETURN
(
	SELECT ecde.id_evento
		  ,ecde.evento
	FROM   eventos_csx_desc_evento ecde (NOLOCK)
	WHERE  ecde.id_evento IN (SELECT DISTINCT id_evento_cai FROM eventos_csx_categoria_x_evento)
	GROUP BY
		   ecde.id_evento
		  ,ecde.evento
)
