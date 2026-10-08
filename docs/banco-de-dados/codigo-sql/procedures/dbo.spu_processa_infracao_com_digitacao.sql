CREATE PROCEDURE [dbo].[spu_processa_infracao_com_digitacao]
	/*2*/@id_infracao int, 
	/*3*/@id_usuario int, 
	/*4*/@id_processo int, 
	/*5*/@id_inconsistencia int, 
	/*6*/@id_imagem int, 
	/*7*/@x_obliteracao int, 
	/*8*/@y_obliteracao int, 
	/*9*/@largura_obliteracao int, 
	/*10*/@altura_obliteracao int, 
	/*11*/@placa char(7), 
	/*12*/@id_marca_processo int = null, 
	/*13*/@id_especie_processo int = null, 
	/*14*/@uf_processo varchar(2) = null, 
	/*15*/@tempo_proc int, 
	/*16*/@tempo_cli int, 
	/*17*/@codigoAgenteDigitado INT = 0, 
	/*18*/@AgenteDigitado VARCHAR(50) = null,  
	/*19*/@observacao VARCHAR(MAX) = null,
	/*20*/@classificacao_veiculo_processo VARCHAR(10) = null,
	/*21*/@data_proc datetime = 0
	 
AS 

	SET NOCOUNT ON 

	DECLARE @id_marca_cet INT = NULL 
	DECLARE @id_especie INT = NULL
	DECLARE @uf varchar(2) = NULL
	DECLARE @id_marca INT = NULL 
	DECLARE @id_categoria INT = NULL
	DECLARE @id_tipo INT = NULL
	
	BEGIN TRY 
 
		BEGIN TRANSACTION 
		 
			IF @id_marca_processo = 0 
				SET @id_marca_processo = NULL	 
 
			IF @id_especie_processo = 0 
				SET @id_especie_processo = NULL	 
 
			IF @uf_processo = '' 
				SET @uf_processo = NULL	 
 
			DECLARE @id_infracao_processo int 
		 
			EXECUTE @id_infracao_processo = spu_processa_infracao 	 
				@id_infracao, 
				@id_usuario, 
				@id_processo, 
				@id_inconsistencia, 
				@id_imagem, 
				@x_obliteracao, 
				@y_obliteracao, 
				@largura_obliteracao, 
				@altura_obliteracao, 
				@tempo_proc, 
				@tempo_cli, 
				@codigoAgenteDigitado,
				@AgenteDigitado,
				@data_proc,
				1,
				@observacao 
		 
			IF (@placa IS NOT NULL) 

				BEGIN 

					SELECT 
						@id_marca_cet = id_marca_cet, 
						@id_especie = id_especie, 
						@uf = uf, 
						@id_marca = id_marca, 
						@id_tipo = id_tipo, 
						@id_categoria = id_categoria 
					FROM cad_veiculo cv (nolock)
						LEFT JOIN cad_localidade cl (nolock)
							ON cl.id_localidade = cv.id_localidade
					WHERE 
						placa = @placa
		
					INSERT INTO infracao_processo_digitacao with (rowlock) (
						id_infracao_processo,
						placa,
						id_marca_cet,
						id_especie, 
						uf, 
						id_marca, 
						id_tipo, 
						id_categoria,
						classificacao_veiculo)  
					VALUES (
						@id_infracao_processo,
						@placa,
						COALESCE(@id_marca_processo, @id_marca_cet), 
						COALESCE(@id_especie_processo, @id_especie), 
						COALESCE(@uf_processo, @uf), 
						@id_marca, 
						@id_tipo, 
						@id_categoria,
						@classificacao_veiculo_processo) 
				END 
		 
			IF (@id_marca_processo > 0 AND (@placa IS NOT NULL) AND @id_inconsistencia = 0 ) 

				BEGIN 
			 
					UPDATE cad_marca_cet_processo with (rowlock)
					SET id_marca_cet = @id_marca_processo 
					WHERE placa = @placa 
			 
					IF NOT (@@ROWCOUNT > 0) 

						BEGIN 

							INSERT INTO cad_marca_cet_processo with (rowlock) (
								placa, 
								id_marca_cet) 
							VALUES (
								@placa, 
								@id_marca_processo) 

						END 
			 
				END 
 
			IF (@id_especie_processo > 0 AND (@placa IS NOT NULL) AND @id_inconsistencia = 0) 

				BEGIN 
			 
					UPDATE cad_especie_processo with (rowlock)
					SET id_especie = @id_especie_processo 
					WHERE placa = @placa 
			 
					IF NOT (@@ROWCOUNT > 0) 

						BEGIN 

							INSERT INTO cad_especie_processo with (rowlock) (
								placa, 
								id_especie) 
							VALUES (
								@placa, 
								@id_especie_processo) 

						END 
			 
				END 
 
			IF (@uf_processo is not NULL AND (@placa IS NOT NULL) AND @id_inconsistencia = 0 ) 

				BEGIN 

					UPDATE cad_uf_processo with (rowlock)
					SET uf = @uf_processo 
					WHERE placa = @placa 
			 
					IF NOT (@@ROWCOUNT > 0) 

						BEGIN 

							INSERT INTO cad_uf_processo with (rowlock) (
								placa, 
								uf) 
							VALUES (
								@placa, 
								@uf_processo) 

						END 

				END 


			UPDATE veiculo WITH (ROWLOCK)
			SET    classificacao_veiculo = CASE WHEN @classificacao_veiculo_processo IS NULL THEN NULL ELSE @classificacao_veiculo_processo END
			WHERE  id_veiculo = (SELECT id_veiculo FROM infracao (NOLOCK) WHERE id_infracao = @id_infracao)
 
		COMMIT 
		 
		RETURN @id_infracao_processo 
 
	END TRY 

	BEGIN CATCH 
 
		IF (@@TRANCOUNT > 0)  
			ROLLBACK  
			 
		EXEC spu_replica_erro 
		 
		RETURN 0 
		 
	END CATCH


