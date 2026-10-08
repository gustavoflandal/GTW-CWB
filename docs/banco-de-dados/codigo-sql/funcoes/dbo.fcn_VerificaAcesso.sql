
CREATE FUNCTION [dbo].[fcn_VerificaAcesso]
(	
	@acao CHAR(150), @id_usuario INT
)
RETURNS TABLE 
AS
RETURN 
(
	SELECT COUNT(*) cnt, SUM(CASE WHEN sub1.id_usuario = @id_usuario THEN 1 ELSE 0 END) AS acesso
	FROM 
	(
		SELECT sm.acao, COALESCE(sug.id_usuario, smd.id_usuario) AS id_usuario
		FROM sis_menu sm (NOLOCK) 
		JOIN sis_menu_direitos smd (NOLOCK) ON sm.id_menu = smd.id_menu
		LEFT JOIN sis_usuario_grupo sug (NOLOCK) ON smd.id_grupo = sug.id_grupo
		WHERE sm.acao = @acao
	) AS sub1
)
