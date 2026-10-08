
CREATE PROCEDURE [dbo].[spu_processa_obliteracao]  
	@id_infracao_processo int,  
	@id_imagem int,  
	@sequencia_obliteracao int,  
	@x_obliteracao int,  
	@y_obliteracao int,  
	@largura_obliteracao int,  
	@altura_obliteracao int
AS  
  
	SET NOCOUNT ON  
	  
	BEGIN TRY  
	  
		BEGIN TRANSACTION  
		
			INSERT INTO infracao_processo_obliteracao with (rowlock) (
				id_infracao_processo, 
				id_imagem, 
				sequencia_obliteracao, 
				x, 
				y, 
				largura, 
				altura)  
			VALUES (
				@id_infracao_processo, 
				@id_imagem, 
				@sequencia_obliteracao, 
				@x_obliteracao, 
				@y_obliteracao, 
				@largura_obliteracao, 
				@altura_obliteracao)  
			  
			COMMIT  
		  
	END TRY  

	BEGIN CATCH  
  
		IF (@@TRANCOUNT > 0) 
			ROLLBACK  
			  
		EXEC spu_replica_erro  
		  
		RETURN 0  
		  
	END CATCH




