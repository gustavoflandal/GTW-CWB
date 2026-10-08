
CREATE PROCEDURE [dbo].[spu_processa_infracao_contestacao]
	@id_infracao int,  
	@id_usuario int,  
	@id_processo int,  
	@decisao int

AS  
  
	SET NOCOUNT ON  
	  
	BEGIN TRY  
	  
		BEGIN TRANSACTION  
  
			DECLARE @id_processo_agora int  
			DECLARE @id_infracao_processo int  
  
			SET @id_processo_agora = NULL  
			SET @id_infracao_processo = NULL  
			  
			DECLARE @id_inconsistencia INT
			SELECT @id_inconsistencia = id_inconsistencia FROM infracao (NOLOCK) WHERE id_infracao = @id_infracao

			-- DECLARE @id_processo INT = 91, @decisao INT = 3
			DECLARE @id_processo_contestacao INT
			DECLARE @id_processo_contestacao_dest INT
			SELECT 
			@id_processo_contestacao = id_processo_contestacao,
			@id_processo_contestacao_dest = id_processo_contestacao_dest 
			FROM contestacao_ligacao (NOLOCK) 
			WHERE id_processo = @id_processo AND decisao = @decisao

			--SELECT @id_processo_contestacao, @id_processo_contestacao_dest

			INSERT INTO infracao_processo with (rowlock) (
				data,
				tempo,
				tempo_cliente,
				id_infracao,
				id_processo,
				id_inconsistencia, 
				id_imagem, 
				id_usuario, 
				status_processo)  
			VALUES (
				GETDATE(),
				0,
				0,
				@id_infracao,
				@id_processo,
				@id_inconsistencia, 
				NULL, 
				@id_usuario, 
				0)   

			SET @id_infracao_processo = @@identity  

			INSERT INTO infracao_processo_contestacao VALUES (@id_infracao, @id_infracao_processo, @id_processo_contestacao, @decisao)

			UPDATE infracao_contestacao WITH (ROWLOCK) 
				SET id_processo_contestacao = COALESCE(@id_processo_contestacao_dest,1) ,
					decisao = @decisao
				WHERE id_infracao = @id_infracao

			UPDATE infracao with (rowlock)
			SET espera = NULL 
			WHERE id_infracao = @id_infracao 
			
			UPDATE infracao_janela with (rowlock)
			SET processado = 1 
			WHERE	id_infracao = @id_infracao 
				AND id_processo = @id_processo 
				AND id_usuario = @id_usuario 
						  
		COMMIT  

		RETURN @id_infracao_processo  
		  
	END TRY  

	BEGIN CATCH  
  
		IF (@@TRANCOUNT > 0) 
			ROLLBACK  
			  
		EXEC spu_replica_erro  
		  
		RETURN 0  
		  
	END CATCH
	



