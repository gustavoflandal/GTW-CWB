CREATE PROCEDURE [dbo].[spu_processa_filtros] 
	@id_processo INT, 
	@id_usuario INT 

AS 
 
	SET NOCOUNT ON 
 
	DECLARE @id_infracao INT = null 
	DECLARE @id_filtro INT 
	DECLARE @id_inconsistencia INT 
	DECLARE @espera BIT
	DECLARE @data_exec DATETIME = getDate() 
	 
	EXEC @id_infracao = spu_busca_infracao_processamento @id_usuario, @id_processo, null, null, null, null, null, 0, 3  
 
	WHILE @id_infracao > 0 

		BEGIN  

			EXEC [spu_verificar_filtros] @id_infracao , @id_processo, @id_filtro OUTPUT, @id_inconsistencia OUTPUT , @espera OUTPUT

			IF @id_filtro IS NULL 			
			
				BEGIN

					SELECT 
						@id_inconsistencia = id_inconsistencia, 
						@espera = espera
					FROM 
						infracao (nolock)
					WHERE 
						id_infracao = @id_infracao 

				END
					 
			EXEC spu_processa_infracao_filtro @id_infracao, @id_usuario, @id_processo, @id_inconsistencia, @espera, @id_filtro 
		 
			IF DATEDIFF(minute,@data_exec,getdate()) > 9

				BEGIN

					PRINT 'Tempo máximo excedido para processar o filtro, finalizando...'

					BREAK 

				END
		 
			EXEC @id_infracao = spu_busca_infracao_processamento @id_usuario, @id_processo, null, null, null, null, null, 0, 3  

		END 
	 
	EXEC spu_ajusta_janela @id_usuario, @id_processo



