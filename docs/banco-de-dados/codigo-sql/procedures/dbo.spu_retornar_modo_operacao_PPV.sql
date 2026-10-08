CREATE PROCEDURE [dbo].[spu_retornar_modo_operacao_PPV]
AS

	/*
	* 1 - Modo de operação coercitiva
	* 2 - Modo de operação educativa
	* 3 - Repesagem
	* 4 - Liberação de veículo
	* 5 - Informação operacional de controle
	*/

	DECLARE @modo_operacao INT, @desc_modo_operacao VARCHAR(60), @data_liberacao DATETIME, @id_ocorrencia BIGINT, @retorno INT = 0

	SELECT @modo_operacao = id_tpocorrencia,
		   @desc_modo_operacao = tipo_ocorrencia,
		   @data_liberacao = data
	FROM   fcn_getModoOperacaoGeralPPV()

	--SELECT @modo_operacao AS modo_operacao, @desc_modo_operacao AS desc_modo_operacao, @data_liberacao AS data_lib, DATEDIFF(SECOND, @data_liberacao, GETDATE()) AS tempo_decorrido

	IF ( (@modo_operacao = 4) AND (DATEDIFF(SECOND, @data_liberacao, GETDATE()) > 180) )
	BEGIN
	
		BEGIN TRY

			BEGIN TRAN

			SET @id_ocorrencia = (SELECT MAX(id_ocorrencia) + 1 FROM ppv_ocorrencias (NOLOCK))
	
			INSERT INTO ppv_ocorrencias (id_ocorrencia, id_SAI, id_tpocorrencia, data, observacoes, id_veiculo_interno)
			SELECT @id_ocorrencia AS id_ocorrencia,
				   id_SAI,
				   id_tpocorrencia,
				   GETDATE() AS data,
				   'Retorno AUTOMATICO para ' + LTRIM(RTRIM(UPPER(tipo_ocorrencia))) AS observacoes,
				   CASE WHEN id_veiculo_interno = 0 THEN NULL ELSE id_veiculo_interno END AS id_veiculo_interno
			FROM   dbo.fcn_getModoOperacaoPesagemPPV()
		
			SET @retorno = @@ROWCOUNT

			PRINT('Retornado para modo de operação normal');

			COMMIT;
	
		END TRY

		BEGIN CATCH
 
			IF (@@TRANCOUNT > 0)
				ROLLBACK;

			EXEC spu_replica_erro
		 
		END CATCH

	END
	ELSE
	BEGIN
		
		IF ((@modo_operacao = 4) AND (DATEDIFF(SECOND, @data_liberacao, GETDATE()) <= 180))
		BEGIN
			PRINT('Liberação de veículo dentro do prazo de vigência')
		END
		ELSE
		BEGIN
			PRINT('Sem liberação de veículo vigente')
		END
	END

	RETURN @retorno
