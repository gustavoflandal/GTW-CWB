CREATE PROCEDURE [dbo].[spu_conclui_infracao_processo] @id_infracao_processo INT, @tempo_proc INT = NULL 
AS 
BEGIN 

	SET NOCOUNT ON 
 
	UPDATE infracao_processo with (rowlock)
	SET status_processo = 0 , 
		tempo = COALESCE(@tempo_proc, tempo)
	WHERE 
		id_infracao_processo = @id_infracao_processo 
	 
	RETURN 1 

END



