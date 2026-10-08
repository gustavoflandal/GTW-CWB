CREATE PROCEDURE [dbo].[spu_valida_processo]
	@id_infracao_processo int
AS

	DECLARE @id_infracao int
	DECLARE @id_processo int

	DECLARE @id_processo_concluido int
	DECLARE @id_usuario int
	DECLARE @id_inconsistencia int
	DECLARE @id_enquadramento int

	/*
	-- RAISERROR (<mens>, <severidade>, 1)...onde a severidade até 10 nao var parar a query, de 11 a 16 vai parar a query.
	-- Primeiro, testando se a imagem é consistente ou inconsistente...
		IF (EXISTS (SELECT id_infracao_processo FROM infracao_processo ip
 				WHERE id_infracao_processo = @id_infracao_processo AND id_inconsistencia = 0))
		BEGIN
	-- Verificando a existencia de obliteracao (Caso todas as imagens exijam obliteração):
			IF (NOT EXISTS (SELECT id_infracao_processo FROM infracao_processo_obliteracao
					WHERE id_infracao_processo = @id_infracao_processo)) 
			BEGIN
				RAISERROR('IMAGEM SEM OBLITERAÇÃO!', 1, 1) --Esta fazendo na interface.
			END
		END
	*/

	SELECT 
		@id_infracao = ip.id_infracao, 
		@id_inconsistencia = ip.id_inconsistencia, 
		@id_processo = i.id_processo, 
		@id_enquadramento = id_enquadramento, 
		@id_processo_concluido = id_processo_concluido, 
		@id_usuario = ip.id_usuario
	FROM infracao_processo ip (nolock)
		INNER JOIN infracao i (nolock)
			ON i.id_infracao = ip.id_infracao 
	WHERE 
		id_infracao_processo = @id_infracao_processo
	
	IF (@id_enquadramento IN (74550, 74630, 74710) AND --Velocidade. 
		@id_inconsistencia IN (6,7,8,9,10,13))

		BEGIN

			RAISERROR('INCONSISTÊNCIA SELECIONADA NÃO É APLICÁVEL A ESSE ENQUADRAMENTO!', 1, 1) --Esta fazendo na interface.

		END

	ELSE IF (@id_enquadramento IN (57462) AND --Rodízio. 
		@id_inconsistencia IN (10, 13))

		BEGIN

			RAISERROR('INCONSISTÊNCIA SELECIONADA NÃO É APLICÁVEL A ESSE ENQUADRAMENTO!', 1, 1) --Esta fazendo na interface.

		END

	ELSE IF (@id_enquadramento IN (57463) AND --ZMRC. 
		@id_inconsistencia IN (6,7,8,9))

		BEGIN

			RAISERROR('INCONSISTÊNCIA SELECIONADA NÃO É APLICÁVEL A ESSE ENQUADRAMENTO!', 1, 1) --Esta fazendo na interface.

		END

	IF (@id_inconsistencia = 0) -- Se aprovou...

		BEGIN

			IF (@id_enquadramento = 57462 AND NOT EXISTS (	SELECT 
																id_infracao 
															FROM 
																infracao (nolock) --Verificando o horário do rodízio.
															WHERE	id_infracao = @id_infracao 
																AND (CAST(data AS time) BETWEEN '07:11' AND '09:49' 
																	OR CAST(data AS time) BETWEEN '17:11' AND '19:49') 
																AND	DATEPART(weekday, data) BETWEEN 2 AND 6)
				)

				BEGIN

					RAISERROR('INFRAÇÃO DE RODÍZIO FORA DO HORÁRIO!', 1, 1) --Esta fazendo na interface.

				END

			ELSE IF (@id_enquadramento = 57461 AND NOT EXISTS (	SELECT 
																	id_infracao 
																FROM 
																	infracao (nolock) --Verificando o horário da Fretado.
																WHERE	id_infracao = @id_infracao 
																	AND id_enquadramento = 57461 
																	AND	(DATEPART(hour, data) BETWEEN 5 AND 20 -- Dia de semana
																		AND  DATEPART(weekday, data) BETWEEN 2 AND 6) 
																	OR	(DATEPART(hour, data) BETWEEN 10 AND 13 -- Sábado.
																		AND DATEPART(weekday, data) = 7))
						)

				BEGIN

					RAISERROR('INFRAÇÃO DE FRETADO FORA DO HORÁRIO!', 1, 1) --Esta fazendo na interface.
			
				END
			
			ELSE IF (@id_enquadramento = 57463 AND NOT EXISTS (	SELECT 
																	id_infracao 
																FROM 
																	infracao (nolock) --Verificando o horário da ZMRC.
																WHERE	id_infracao = @id_infracao 
																	AND id_enquadramento = 57463 
																	AND	(DATEPART(hour, data) BETWEEN 5 AND 20  -- Dia de semana
																		AND DATEPART(weekday, data) BETWEEN 2 AND 6) 
																	OR	(DATEPART(hour, data) BETWEEN 10 AND 13 -- Sábado.
																		AND DATEPART(weekday, data) = 7))
					)

				BEGIN

					RAISERROR('INFRAÇÃO DE ZMRC FORA DO HORÁRIO!', 1, 1) --Esta fazendo na interface.

				END

			ELSE IF (NOT EXISTS(SELECT 
									id_infracao 
								FROM infracao i (nolock)	-- Verificando se a data de aferição maior que a validade e não esta no futuro.
									INNER JOIN local l (nolock) 
										ON	l.id_local = i.id_local 
										AND l.sequencia_local = i.sequencia_local
									INNER JOIN configuracao_equipamento_afericao a (nolock) 
										ON	a.id_configuracao_equipamento = l.id_configuracao_equipamento 
										AND a.id_pista = i.pista
									INNER JOIN enquadramento e (nolock) 
										ON e.id_enquadramento = i.id_enquadramento 
								WHERE	i.id_infracao = @id_infracao 
									AND ( e.infracao_metrologia = 0 OR i.data BETWEEN a.data AND a.data_validade ))
					)

				BEGIN

					RAISERROR('VERIFICAR DATA DE AFERIÇÃO!', 16, 1) --Esta fazendo na interface.

				END
		
			IF (EXISTS (SELECT distinct 
							p.id_processo 
						FROM processo p (nolock)	--VERIFICANDO SE ESTÁ NO ULTIMO PROCESSO ANTES DA REMESSA VALIDAS...(VALIDAÇÃO).
							INNER JOIN processo p_prox (nolock)
								ON	(p.id_processo = p_prox.id_processo_anterior 
									OR p.id_processo_proximo = p_prox.id_processo)
								AND p_prox.id_processo_proximo IS NULL 
								AND p_prox.recebe_consistentes_inconsistentes = 1
								AND p.id_processo = @id_processo_concluido)
				)

				BEGIN

								SELECT 
									id_usuario
								FROM 
									sis_usuario u (nolock)	-- Verificando se será possível buscar o código do agente na remessa. 
								WHERE 
									u.id_usuario = @id_usuario AND u.cod_agente > 0

					IF (NOT EXISTS (SELECT 
										id_usuario
									FROM 
										sis_usuario u (nolock)	-- Verificando se será possível buscar o código do agente na remessa. 
									WHERE 
										u.id_usuario = @id_usuario AND u.cod_agente > 0)
						)

						BEGIN

							RAISERROR('USUÁRIO NÃO É AGENTE!', 11, 1) --Esta fazendo na interface.

						END

				END

		END



