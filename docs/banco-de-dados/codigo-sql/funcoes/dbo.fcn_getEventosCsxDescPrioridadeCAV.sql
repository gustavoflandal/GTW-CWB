CREATE FUNCTION [dbo].[fcn_getEventosCsxDescPrioridadeCAV]()
RETURNS TABLE
AS
RETURN
(
	SELECT ecdp.id_prioridade
		  ,ecdp.prioridade
	FROM   eventos_csx_desc_prioridade ecdp (NOLOCK)
)
