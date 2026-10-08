CREATE PROCEDURE [dbo].[spu_limpa_janela_validacao]
	@id_remessa int
AS
BEGIN

	BEGIN TRY
	
		BEGIN TRAN

		UPDATE	infracao WITH(ROWLOCK) 
		SET		id_usuario_atual = NULL 
		WHERE	id_infracao IN (SELECT id_infracao FROM infracao_remessa (NOLOCK) WHERE id_remessa = @id_remessa)
		AND		id_usuario_atual IS NOT NULL

		DELETE 
		FROM	infracao_processo WITH(ROWLOCK)
		WHERE	id_infracao IN (SELECT id_infracao FROM infracao_remessa (NOLOCK) WHERE id_remessa = @id_remessa)
		AND		status_processo = 1 

		DELETE
		FROM	infracao_janela WITH(ROWLOCK)
		WHERE	id_infracao IN (SELECT id_infracao FROM infracao_remessa (NOLOCK) WHERE id_remessa = @id_remessa)

		COMMIT;

	END TRY

	BEGIN CATCH

		ROLLBACK;
		THROW;
	
	END CATCH

END
