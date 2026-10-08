
CREATE PROCEDURE [dbo].[spu_janela_processamento]
	@id_local int = 0,
	@data_ini datetime,
	@data_fim datetime
AS

BEGIN

	SET NOCOUNT ON

	SELECT 
		u.id_usuario, 
		u.usuario, 
		p.id_processo, 
		p.nome AS nome_processo, 
		count(*) AS conta
	FROM infracao (nolock) 
		INNER JOIN sis_usuario u (nolock) 
			ON u.id_usuario = infracao.id_usuario_atual
		INNER JOIN processo p (nolock) 
			ON p.id_processo = infracao.id_processo
	WHERE	data BETWEEN @data_ini AND @data_fim 
		AND	(@id_local = 0 OR id_local = @id_local)
	GROUP BY 
		u.id_usuario, 
		u.usuario, 
		p.id_processo, 
		p.nome

END



