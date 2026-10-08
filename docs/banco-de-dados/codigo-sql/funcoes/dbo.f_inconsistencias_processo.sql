CREATE FUNCTION [dbo].[f_inconsistencias_processo]
(
	@id_processo INT,
	@id_enquadramento INT = NULL
)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @id_processo INT = 1, @id_enquadramento INT = 60411
	SELECT inc.id_inconsistencia,
		   inc.descricao,
		   SUM(ipe.quantidade) AS cnt
	FROM   inconsistencia_processo_enquadramento ipe (NOLOCK)
		   INNER JOIN inconsistencia inc (NOLOCK)
				ON  inc.id_inconsistencia = ipe.id_inconsistencia
	WHERE  ipe.id_processo = @id_processo
		   AND (@id_enquadramento IS NULL OR ipe.id_enquadramento = @id_enquadramento)
	GROUP BY
		   inc.id_inconsistencia,
		   inc.descricao
)
