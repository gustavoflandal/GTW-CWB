CREATE PROCEDURE [dbo].[spu_info_lista_credencial_usuarios]
AS
	
	SELECT 
		DISTINCT
		usuario,
		senha
	FROM sis_usuario u (nolock)
		INNER JOIN sis_usuario_grupo ug (nolock) 
			ON ug.id_usuario = u.id_usuario
	WHERE	u.ativo = 1	
		AND ug.id_grupo in (7,9,10)



