CREATE FUNCTION [dbo].[fcn_getEventosCsxDescProprietarioCAV]()
RETURNS TABLE
AS
RETURN
(
	SELECT ecdp.id_proprietario
		  ,STR(ecdp.proprietario) proprietario
	FROM   eventos_csx_desc_proprietario ecdp (NOLOCK)
		   INNER JOIN (
							SELECT serie_equipamento
							FROM   local_vigente
							UNION
							SELECT serie_equipamento
							FROM   equipamento_estatico
		   ) AS tse
				ON  tse.serie_equipamento = ecdp.proprietario
	WHERE  ISNUMERIC(ecdp.proprietario) = 1
)
