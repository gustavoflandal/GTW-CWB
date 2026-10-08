CREATE PROCEDURE [dbo].[spu_mover_infracao_arquivo_morto]
( 
	@id_infracao INT
)
AS

 	BEGIN TRY
		
		BEGIN TRANSACTION

		-- Tenta fazer um UPDATE
		--DECLARE @id_infracao INT = 1234
		UPDATE infracao WITH (ROWLOCK)
		SET	   id_processo = 99,
			   id_processo_concluido = 99,
			   id_inconsistencia = 0
		WHERE  id_infracao = @id_infracao

		-- Realiza o commit
		COMMIT;
		
	END TRY
	BEGIN CATCH

		IF (@@TRANCOUNT > 0)
			ROLLBACK

		EXEC spu_replica_erro
		
	END CATCH

