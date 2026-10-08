CREATE PROCEDURE [dbo].[spu_altera_modo_operacao_ptz] @id_modo_operacao TINYINT, @id_usuario INT
AS

	/*
	* 1 - Escaneamento de vagas
	* 2 - Visualização de vagas
	* 3 - Visualização da pista do semáforo
	* 4 - PTZ em uso pelo agente
	*/

	DECLARE @retorno INT = 0
	
	IF (@id_usuario IS NULL OR @id_usuario = 0)
	BEGIN
		SET @id_usuario = 1 --administrador
	END
		
	BEGIN TRY
		 
		BEGIN TRAN

		UPDATE ptz_configuracao_operacao SET data_fim = GETDATE() WHERE data_fim IS NULL
		INSERT INTO ptz_configuracao_operacao
			(
				id_ptz_modo_operacao,
				data_inicio,
				data_fim,
				id_usuario
			)
		VALUES
			(
				@id_modo_operacao,
				GETDATE(),
				NULL,
				@id_usuario
			)

		SET @retorno = @@ROWCOUNT

		COMMIT;

	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0) 
			ROLLBACK;

		EXEC spu_replica_erro 
		 
	END CATCH

	RETURN @retorno
