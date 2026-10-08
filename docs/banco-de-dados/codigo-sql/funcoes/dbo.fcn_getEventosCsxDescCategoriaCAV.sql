CREATE FUNCTION [dbo].[fcn_getEventosCsxDescCategoriaCAV]()
RETURNS TABLE
AS
RETURN
(
	-- Acima de 10000 categorias CAV
	SELECT id_categoria
		  ,categoria
	FROM   eventos_csx_desc_categoria
	WHERE  id_categoria > 10000
)
