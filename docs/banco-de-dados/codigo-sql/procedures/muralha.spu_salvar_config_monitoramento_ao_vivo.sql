CREATE PROCEDURE [muralha].[spu_salvar_config_monitoramento_ao_vivo]
   @segundos INT,
   @idUsuario INT
AS

	DECLARE @retorno INT = 0
	
	BEGIN
    	BEGIN TRY

			--> INATIVAR REGISTRO VIGENTE
			UPDATE muralha.config_monitoramento_ao_vivo SET ativo = 0 WHERE ativo = 1

			--> INSERIR NOVA CONFIGURAÇÃO VIGENTE
			INSERT INTO muralha.config_monitoramento_ao_vivo (segundos, id_usuario) VALUES (@segundos, @idUsuario)

			SET @retorno = @@ROWCOUNT

		END TRY
		BEGIN CATCH
			PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
			SET @retorno = 0
			
			IF @@TRANCOUNT > 0
				ROLLBACK
      
			EXEC spu_replica_erro
    
		END CATCH
	END

	RETURN @retorno
