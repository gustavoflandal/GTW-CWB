CREATE FUNCTION [dbo].[fcn_getEventosCsxDescNivelCAV]()
RETURNS TABLE
AS
RETURN
(
	SELECT ecdn.id_nivel
		  ,ecdn.nivel
	FROM   eventos_csx_desc_nivel ecdn (NOLOCK)
)
