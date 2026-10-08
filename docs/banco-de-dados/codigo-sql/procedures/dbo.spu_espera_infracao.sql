CREATE PROCEDURE [dbo].[spu_espera_infracao]
	@id_infracao int,
	@id_usuario int = null,
	@id_processo int = null
AS

BEGIN

	SET NOCOUNT ON

	IF (@id_usuario IS NOT NULL AND @id_processo IS NOT NULL) 		
		UPDATE infracao_processo with (rowlock)
		SET status_processo = 1 
		WHERE id_usuario = @id_usuario 
			AND id_processo = @id_processo 
			AND id_infracao = @id_infracao 
	
	UPDATE infracao 
	SET espera = 1 
	WHERE id_infracao = @id_infracao
	
	EXEC spu_status_infracao @id_infracao
	
	RETURN 0

END



