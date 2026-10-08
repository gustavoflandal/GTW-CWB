CREATE PROCEDURE [dbo].[spu_processa_imagens_teste] 
	@id_processo INT, 
	@id_usuario INT,
	@data_fim DATETIME = NULL 
AS 
 
	SET NOCOUNT ON 

	DECLARE @id_infracao INT = null 
	DECLARE @id_inconsistencia INT = null
	DECLARE @dias_vencimento INT = 7
	DECLARE @data_ini DATETIME = '1899-01-01'
	
	BEGIN TRY 
		 
		IF (@data_fim IS NULL)
			SET @data_fim = dateadd(d, @dias_vencimento*(-1), getdate())
	 
		SELECT 
			@id_inconsistencia = valor 
		FROM 
			chave_valor (nolock)
		WHERE 
			chave = 'id_inconsistencia_padrao_imagem_teste'
			
		IF (@id_inconsistencia IS NULL)
			RAISERROR('Não foi possível determinar uma inconsistência padrão para o processamento.', 16, 1) 

		EXEC @id_infracao = spu_busca_infracao_processamento @id_usuario, @id_processo, null, null, null, @data_ini, @data_fim, 0, 3  
	 
		WHILE @id_infracao > 0 

			BEGIN  
		
				EXEC spu_processa_infracao_direto @id_infracao, @id_usuario, @id_processo, @id_inconsistencia
			 
				EXEC @id_infracao = spu_busca_infracao_processamento @id_usuario, @id_processo, null, null, null, @data_ini, @data_fim, 0, 3
			
			END 
		 
		EXEC spu_ajusta_janela @id_usuario, @id_processo

	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK 
 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH



