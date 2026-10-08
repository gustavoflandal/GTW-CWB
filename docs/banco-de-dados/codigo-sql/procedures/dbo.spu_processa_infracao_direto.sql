
CREATE PROCEDURE [dbo].[spu_processa_infracao_direto]  
	@id_infracao int,  
	@id_usuario int,  
	@id_processo int, 
	@id_inconsistencia int	= NULL 
AS  
 
    DECLARE @x INT  
    DECLARE @y INT  
    DECLARE @largura INT  
    DECLARE @altura INT  
  
	BEGIN TRY  
  
		BEGIN TRANSACTION  

			EXEC sp_getapplock @Resource = '[spu_processa_infracao_direto]', @LockMode = 'Exclusive'	  
		  
			SET @x = NULL  
			SET @y = NULL  
			SET @largura = NULL  
			SET @altura = NULL  
		  
			DECLARE @sequencia_obliteracao int  
			DECLARE @id_infracao_processo int  
			DECLARE @id_imagem int  
 
 
			IF (@id_inconsistencia IS NULL) 
			
				BEGIN 

					SELECT 
						@id_inconsistencia = i.id_inconsistencia 
					FROM   
						infracao i (nolock) 
					WHERE 
						i.id_infracao = @id_infracao  

				END 
 
			IF (@id_inconsistencia IS NULL)  
				RAISERROR('INCONSISTÊNCIA INCOMPLETA! CONTATE O ADMINISTRADOR.', 1, 1)   
  
			SELECT   
				@id_imagem = ii.id_imagem_obj  
			FROM   
				infracao_imagem ii (nolock)
			WHERE 
				ii.id_infracao = @id_infracao  
		  
			EXEC @id_infracao_processo = spu_processa_infracao   
				@id_infracao, 
				@id_usuario, 
				@id_processo, 
				@id_inconsistencia, 
				@id_imagem,   
				NULL, 
				NULL, 
				NULL,
				NULL, 
				0, 
				NULL, 
				0, 
				0 
  
			DECLARE cursor_obliteracao CURSOR 
			FOR 
			SELECT 
				id_imagem,
				sequencia_obliteracao,
				x, 
				y, 
				largura, 
				altura
			FROM 
				infracao_obliteracao (nolock)
			WHERE 
				id_infracao = @id_infracao
		
			OPEN cursor_obliteracao
		
			FETCH NEXT FROM cursor_obliteracao
			INTO 
				@id_imagem,
				@sequencia_obliteracao,
				@x, @y, @largura, @altura
		
			WHILE @@FETCH_STATUS = 0

				BEGIN

					EXEC spu_processa_obliteracao  
						@id_infracao_processo, 
						@id_imagem, 
						@sequencia_obliteracao,  
						@x, 
						@y, 
						@largura, 
						@altura
		
					FETCH NEXT FROM cursor_obliteracao
					INTO 
						@id_imagem,
						@sequencia_obliteracao,
						@x, @y, @largura, @altura

				END
		
			CLOSE cursor_obliteracao
			DEALLOCATE cursor_obliteracao	
  
			EXEC spu_conclui_infracao_processo @id_infracao_processo  
		  
		COMMIT  
		  
	END TRY  

	BEGIN CATCH  
  
		IF (@@TRANCOUNT > 0)  
			ROLLBACK  
 			  
		EXEC spu_replica_erro  
		  
	END CATCH  
  
	RETURN @id_infracao_processo




