CREATE PROCEDURE [dbo].[spu_desativar_inibicao_infracao] (@id_inibicao_infracao int, @id_usuario int)
AS
	DECLARE @ret INT = NULL
	DECLARE @id_filtro INT = NULL
	
	BEGIN TRY 
		 
		BEGIN TRANSACTION 

			IF NOT EXISTS (SELECT id_inibicao_infracao FROM cad_inibicao_infracao)
				RAISERROR('Não foi possível encontrar a inibição indicada.', 16, 1)

			SELECT 
				@id_filtro = id_filtro_relacionado 
			FROM 
				cad_inibicao_infracao (nolock)
			WHERE 
				id_inibicao_infracao = @id_inibicao_infracao

			UPDATE cad_inibicao_infracao  with (rowlock)
			SET data_cancelado=GETDATE(), 
				id_usuario_cancelado=@id_usuario
			WHERE 
				id_inibicao_infracao = @id_inibicao_infracao

			SET @ret = @@IDENTITY
		
			IF (@id_filtro IS NOT NULL)
				UPDATE filtro with (rowlock) 
				SET data_validade = GETDATE()
				WHERE id_filtro = @id_filtro
		   
		COMMIT
		
		RETURN @ret
		   
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH
   



