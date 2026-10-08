CREATE PROCEDURE [muralha].[spu_atualizar_envio_tempo_real_equip]
AS
BEGIN

	BEGIN TRY

		DECLARE @data_atual DATETIME = GETDATE()
		DECLARE @segundos INT = (SELECT TOP 1 segundos FROM muralha.config_intervalo_envio_tempo_real)
		--SELECT @data_atual, @segundos

		DECLARE @desabilitar_envio_equipamento AS TABLE (id UNIQUEIDENTIFIER, id_local INT, enviar BIT, data_atual DATETIME, data_exibicao_atualizacao DATETIME, diff_em_segundos INT, limite_segundos INT)

		INSERT INTO @desabilitar_envio_equipamento
		SELECT id,
			   id_local,
			   enviar,
			   data_exibicao_atualizacao,
			   @data_atual AS data_atual,
			   DATEDIFF(SECOND, data_exibicao_atualizacao, @data_atual) AS diff_em_segundos,
			   @segundos AS limite_segundos
		FROM   muralha.config_envio_tempo_real_equipamento
		WHERE  enviar = 1

		BEGIN TRAN					
		
		UPDATE muralha.config_envio_tempo_real_equipamento
		SET    enviar = 0
		WHERE  id IN (SELECT id FROM @desabilitar_envio_equipamento WHERE diff_em_segundos > limite_segundos)

		COMMIT;
		
	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
		ROLLBACK;
		
		EXEC spu_replica_erro

	END CATCH

END
