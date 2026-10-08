------------------------------------------------------------------------------------------------------------------------------------

CREATE PROCEDURE [dbo].[spu_processa_infracao]
	@id_infracao int,  
	@id_usuario int,  
	@id_processo int,  
	@id_inconsistencia int,  
	@id_imagem int,  
	@x_obliteracao int,  
	@y_obliteracao int,  
	@largura_obliteracao int,  
	@altura_obliteracao int,  
	@tempo_proc int,  
	@tempo_cli int, 
	@codigoAgenteDigitado INT = 0, 
	@AgenteDigitado VARCHAR(50) = NULL,  	  
	@data_proc datetime = 0,  
	@status_processo INT = 1,
	@observacao VARCHAR(MAX) = NULL
	
AS  
  
	SET NOCOUNT ON  
	  
	BEGIN TRY  
	  
		BEGIN TRANSACTION  
  
			DECLARE @id_processo_agora int  
			DECLARE @id_infracao_processo int  
  
			SET @id_processo_agora = NULL  
			SET @id_infracao_processo = NULL  
  
			IF @data_proc = 0  
				SET @data_proc = GetDate()  
 
			IF (@id_imagem > 0 AND NOT EXISTS (	SELECT 
													vi.id_imagem 
												FROM veiculo_imagem vi (nolock) 
													INNER JOIN infracao i (nolock) 
														ON i.id_veiculo=vi.id_veiculo 
												WHERE	id_infracao = @id_infracao 
													AND vi.id_imagem = @id_imagem))
													 
				RAISERROR ('Esta imagem não pertence a este veículo, problema na atualização?', 16, 1) 
			  
			INSERT INTO infracao_processo with (rowlock) (
				data,
				tempo,
				tempo_cliente,
				id_infracao,
				id_processo,
				id_inconsistencia, 
				id_imagem, 
				id_usuario, 
				status_processo)  
			VALUES (
				@data_proc,
				@tempo_proc,
				@tempo_cli,
				@id_infracao,
				@id_processo,
				@id_inconsistencia, 
				@id_imagem, 
				@id_usuario, 
				@status_processo)   

			SET @id_infracao_processo = @@identity  
  
			IF @codigoAgenteDigitado > 0 

				BEGIN

					INSERT INTO infracao_processo_usuario_digitado with (rowlock) (
						id_infracao_processo, 
						codigo_agente, 
						nome_agente)
					VALUES (
						@id_infracao_processo, 
						@codigoAgenteDigitado, 
						@AgenteDigitado) 

				END 

			IF @observacao IS NOT NULL

				BEGIN

					INSERT INTO infracao_processo_observacao WITH (ROWLOCK) (
						id_usuario,
						id_infracao,
						id_processo,
						id_infracao_processo,
						observacao)
					VALUES (
						@id_usuario,
						@id_infracao,
						@id_processo,
						@id_infracao_processo,
						@observacao)

				END

			DELETE 
			FROM infracao_processo_obliteracao with (rowlock)
			WHERE id_infracao_processo = @id_infracao_processo  
			
			IF NOT @x_obliteracao IS NULL  

				INSERT INTO infracao_processo_obliteracao with (rowlock) (
					id_infracao_processo, 
					x, 
					y, 
					largura, 
					altura, 
					id_imagem, 
					sequencia_obliteracao)  
				VALUES (
					@id_infracao_processo, 
					@x_obliteracao,
					@y_obliteracao, 
					@largura_obliteracao, 
					@altura_obliteracao, 
					@id_imagem, 
					1)  
			  
			UPDATE infracao with (rowlock)
			SET espera = NULL 
			WHERE id_infracao = @id_infracao 
			
			UPDATE infracao_janela with (rowlock)
			SET processado = 1 
			WHERE	id_infracao = @id_infracao 
				AND id_processo = @id_processo 
				AND id_usuario = @id_usuario 
						  
		COMMIT  

		RETURN @id_infracao_processo  
		  
	END TRY  

	BEGIN CATCH  
  
		IF (@@TRANCOUNT > 0) 
			ROLLBACK  
			  
		EXEC spu_replica_erro  
		  
		RETURN 0  
		  
	END CATCH
	
