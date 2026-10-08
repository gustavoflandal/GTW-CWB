CREATE PROCEDURE [dbo].[spu_remover_agendamento]
	@id_infracao INT
AS 

	BEGIN TRY 
 
		BEGIN TRANSACTION 
 
			-- Deleta o agendamento 
			DELETE 
			FROM agendamento_processamento with (rowlock)
			WHERE id_infracao = @id_infracao
		
			--	Desmarca esta infração, ela não está mais "bloqueada"
			--	UPDATE infracao SET status_bloqueio = 0 WHERE id_infracao = @id_infracao
		
			--	Remove qualquer sobra da janela
			--	DELETE FROM infracao_janela WHERE id_infracao = @id_infracao
		
		COMMIT

		RETURN CAST(1 AS BIT)

	END TRY 
	
	BEGIN CATCH 

		--  Não deu certo. Faz o rollback
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
		
 		-- Replica o erro 
		EXEC spu_replica_erro 
		
		RETURN CAST(0 AS BIT)

	END CATCH



