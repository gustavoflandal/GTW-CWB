CREATE PROCEDURE [dbo].[spu_adic_periodo_medicao]
	@id_processo_medicao INT,
	@data_inicio DATETIME,
	@data_final DATETIME,
	@complementar BIT = 0
AS 

	SET NOCOUNT ON 

	DECLARE @ultimo_dia_fixado DATETIME = NULL

	IF (@complementar = 0)

		BEGIN

			SELECT 
				@ultimo_dia_fixado = MAX(v.data)
			FROM processo_medicao_veiculo pmv (nolock)
				INNER JOIN veiculo v 
					ON v.id_veiculo = pmv.id_veiculo
			WHERE pmv.id_processo_medicao = @id_processo_medicao

			IF @ultimo_dia_fixado IS NOT NULL 

				BEGIN
					IF @data_inicio <= CAST(@ultimo_dia_fixado AS DATE)

						BEGIN
							RAISERROR('A DATA INICIAL JÁ FOI FIXADA ANTERIORMENTE!', 11, 1)
						END

					ELSE IF @data_inicio > (DATEADD(day,1,CAST(@ultimo_dia_fixado AS DATE)))

						BEGIN
							RAISERROR('A DATA INICIAL NÃO ESTABELECE UM PERÍODO LINEAR NA MEDIAÇÂO!', 11, 1)
						END

				END

			INSERT INTO processo_medicao_veiculo with (rowlock)
				SELECT @id_processo_medicao, COALESCE(aim.id_veiculo, ai.id_veiculo), 1,
					COALESCE(aim.metrologica, ai.metrologica),
					COALESCE(aim.score_total, ai.score_total)
				FROM amostra_imagem ai (nolock)
					INNER JOIN veiculo vai (nolock) 
						ON vai.id_veiculo = ai.id_veiculo
					LEFT JOIN amostra_imagem_manual aim (nolock) 
						ON	CAST(aim.data AS DATE) = CAST(ai.data AS DATE) 
						AND	aim.id_local = vai.id_local 
						AND	aim.id_pista = vai.pista 
						AND	aim.metrologica = ai.metrologica
					LEFT JOIN veiculo vaim (nolock) 
						ON vaim.id_veiculo = aim.id_veiculo
				WHERE ai.data between @data_inicio AND @data_final
				AND (aim.id_veiculo IS NULL OR aim.aplicavel = 1)
		END

	ELSE IF (@complementar = 1)

		BEGIN

			--Apagando o complementar anterior.
			DELETE 
				FROM processo_medicao_veiculo with (rowlock)
				WHERE id_processo_medicao = @id_processo_medicao 
					AND	etapa = 2 
					AND	id_veiculo IN (	SELECT 
											pmv.id_veiculo 
										FROM processo_medicao_veiculo pmv (nolock)
											JOIN veiculo v (nolock) 
												ON v.id_veiculo = pmv.id_veiculo 
										WHERE pmv.id_processo_medicao = @id_processo_medicao 
											AND	pmv.etapa = 2 
											AND	data BETWEEN @data_inicio AND @data_final)
		
			INSERT INTO processo_medicao_veiculo with (rowlock) 
				SELECT 
					@id_processo_medicao, 
					aim.id_veiculo, 
					2,
					aim.metrologica,
					aim.score_total
				FROM amostra_imagem_manual aim (nolock)
					JOIN veiculo vaim (nolock) 
						ON vaim.id_veiculo = aim.id_veiculo
				WHERE	aim.data between @data_inicio AND @data_final
					AND aim.data_criacao > (SELECT 
												data_exportacao 
											FROM processo_medicao (nolock)
											WHERE id_processo_medicao = @id_processo_medicao)
					AND aim.aplicavel = 1

		END



