CREATE PROCEDURE [dbo].[spu_insere_evento_csx_manual]
   @proprietario VARCHAR(50),
   @data_hora DATETIME,
   @usuario VARCHAR(25),
   @tipo_evento INT,
   @id_categoria_evento_manual INT
   
AS
	
	DECLARE @id_proprietario INT, @retorno INT = 0

	BEGIN TRY
		
		BEGIN TRAN

		SET @id_proprietario = (SELECT TOP 1 id_proprietario
								FROM   eventos_csx_desc_proprietario (NOLOCK)
								WHERE  proprietario = @proprietario)

		IF (@id_proprietario IS NULL)
		BEGIN
			INSERT INTO eventos_csx_desc_proprietario WITH (ROWLOCK) (proprietario) VALUES (@proprietario)
			SET @id_proprietario = SCOPE_IDENTITY()
		END


		INSERT INTO eventos_csx WITH (ROWLOCK)
			(
			   id_proprietario,
			   data_hora,
			   id_categoria,
			   id_evento,   
			   mensagem,
			   id_prioridade,
			   id_nivel,
			   usuario,
			   evento_manual
			)
		--DECLARE @id_proprietario INT = 1, @data_hora DATETIME = GETDATE(), @usuario VARCHAR(25) = 'adm', @tipo_evento INT = 1, @id_categoria_evento_manual INT = 1
		SELECT @id_proprietario AS id_proprietario
			  ,@data_hora AS data_hora
			  ,cem.id_categoria
			  ,cem.id_evento
			  ,cem.mensagem
			  ,cem.id_prioridade
			  ,cem.id_nivel
			  ,'SISTEMA' AS usuario
			  ,1 AS evento_manual
		FROM   cad_evento_manual cem
		WHERE  cem.id_evento_manual_categoria = @id_categoria_evento_manual
			   AND cem.tipo_evento = @tipo_evento
		ORDER BY
			   cem.sequencia_evento
		
		SET @retorno = @@ROWCOUNT

		COMMIT;

	END TRY

	BEGIN CATCH

		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'
				
		ROLLBACK;
		
		EXEC spu_replica_erro

	END CATCH

	RETURN @retorno

