

CREATE PROCEDURE [muralha].[spu_gerar_ocorrencia]
   @idOcorrencia UNIQUEIDENTIFIER,
   @idAlerta UNIQUEIDENTIFIER,
   @idTipoAlerta UNIQUEIDENTIFIER,
   @idStatusAlertaOcorrenciaGerada UNIQUEIDENTIFIER,
   @idStatusOcorrenciaPendente UNIQUEIDENTIFIER,
   @data DATETIME,
   @idUsuario INT,
   @alertaVinculado BIT = 0,
   @idAlertaVinculado UNIQUEIDENTIFIER
AS

	DECLARE @retorno INT = 0
	
	BEGIN
    	BEGIN TRY

			UPDATE muralha.alerta
			SET    id_status_alerta = @idStatusAlertaOcorrenciaGerada,
				   id_usuario = @idUsuario,
				   data_modificacao = @data,
				   lembrete_visualizado = 2,
				   alerta_vinculado = @alertaVinculado,
				   id_alerta_vinculado = @idAlertaVinculado
			WHERE id = @idAlerta

			INSERT INTO muralha.ocorrencia (id, id_alerta, id_tipo_alerta_ocorrencia, id_status_ocorrencia, data, id_usuario)
				VALUES (@idOcorrencia, @idAlerta, @idTipoAlerta, @idStatusOcorrenciaPendente, @data, @idUsuario)

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
