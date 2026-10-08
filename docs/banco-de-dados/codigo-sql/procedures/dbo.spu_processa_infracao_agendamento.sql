
CREATE PROCEDURE [dbo].[spu_processa_infracao_agendamento] 
	@id_infracao int, 
	@id_usuario int, 
	@id_processo int,
	@id_inconsistencia int = NULL
	 
AS 

	BEGIN TRY 
 
		BEGIN TRANSACTION 

			-- Chama a processa direto (como se o usuário estivesse processando).
			EXEC spu_processa_infracao_direto @id_infracao, @id_usuario, @id_processo, @id_inconsistencia
	
			-- Ajusta o status da infração
			EXEC spu_status_infracao @id_infracao

			-- Remove a infração do agendamento
			EXEC spu_remover_agendamento @id_infracao
	
		COMMIT
		
		RETURN CAST(1 AS BIT)

	END TRY 
	
	BEGIN CATCH 

		--  Não deu certo o agendamento. Faz o rollback
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
		
		-- Grava o erro no agendamento
		UPDATE agendamento_processamento with (rowlock)
		SET status_agendamento = 1, 
			msg_erro = LEFT(ERROR_MESSAGE(), 200)
		WHERE 
			id_infracao = @id_infracao
		
 		-- Replica o erro 
		EXEC spu_replica_erro 
		
		RETURN CAST(0 AS BIT)

	END CATCH 




