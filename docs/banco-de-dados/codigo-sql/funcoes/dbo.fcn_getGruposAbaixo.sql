
CREATE FUNCTION [dbo].[fcn_getGruposAbaixo]
  ( @id_usuario int )
RETURNS TABLE
AS

RETURN
(
	SELECT 
		id_grupo,
		descricao
	FROM
		sis_grupo (nolock)
	WHERE
		id_grupo IN (	SELECT 
							id_grupo
						FROM 
							sis_usuario_grupo ug (nolock)
						WHERE ug.id_usuario = @id_usuario
						UNION
						SELECT 
							gh2.id_grupo
						FROM grupo_hierarquia gh (nolock) 
							INNER JOIN grupo_hierarquia gh2 (nolock) 
								ON gh2.Level > gh.Level
						WHERE gh.id_grupo IN (	SELECT id_grupo 
													FROM sis_usuario_grupo (nolock) 
													WHERE id_usuario = @id_usuario)
					)
)




