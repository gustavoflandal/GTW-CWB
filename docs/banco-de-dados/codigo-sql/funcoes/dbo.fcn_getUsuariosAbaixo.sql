
CREATE FUNCTION [dbo].[fcn_getUsuariosAbaixo]
  ( @id_usuario int )
RETURNS TABLE
AS

RETURN
(
   SELECT 
		id_usuario, 
		usuario, 
		nome, 
		senha, 
		email, 
		alterar_senha, 
		ativo, 
		id_grupo_equipamento, 
		cod_agente, 
		uf_agente 
	FROM
		sis_usuario (nolock)
	WHERE id_usuario IN (	SELECT ug.id_usuario 		
								FROM sis_usuario_grupo ug (nolock)
								WHERE ug.id_grupo IN (	SELECT gh2.id_grupo
															FROM grupo_hierarquia gh (nolock) 
																INNER JOIN grupo_hierarquia gh2 (nolock) 
																	ON gh2.Level > gh.Level
														WHERE gh.id_grupo IN (	SELECT id_grupo 
																					FROM sis_usuario_grupo (nolock) 
																					WHERE id_usuario = @id_usuario)
													)
						) 
		AND id_usuario NOT IN (	SELECT ug.id_usuario 		
									FROM sis_usuario_grupo ug (nolock)
									WHERE ug.id_grupo IN (	SELECT gh.id_grupo
																FROM grupo_hierarquia gh (nolock) 
																WHERE gh.Level <= (	SELECT MIN(Level) 
																						FROM grupo_hierarquia (nolock) 
																						WHERE id_grupo IN (	SELECT id_grupo 
																												FROM sis_usuario_grupo (nolock) 
																												WHERE id_usuario = @id_usuario)
																					)
														)
							) 
		OR id_usuario = @id_usuario
)




