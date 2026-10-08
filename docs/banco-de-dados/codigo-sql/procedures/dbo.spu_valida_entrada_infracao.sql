CREATE PROCEDURE [dbo].[spu_valida_entrada_infracao]
	@id_infracao int
AS
DECLARE @flag_opcao_captura_traseira int = 256 --100000000
--												  |----------Bit de captura traseira.
BEGIN

	SET NOCOUNT ON

	IF EXISTS (	SELECT 
					id_infracao 
				FROM 
					infracao (nolock)              --BLOQUEIO ZMRF..até dia 27/07
				WHERE	id_enquadramento = 57461 
					AND id_infracao = @id_infracao 
					and data < '2009-07-27 00:00:00')

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 14, 
				id_processo = 10, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	IF EXISTS (	SELECT 
					id_infracao 
				FROM infracao i (nolock)            --Se for moto, já joga para a liberação (11), CASO o equipamento não tenha captura traseira!
					INNER JOIN veiculo v (nolock)
						ON v.id_veiculo = i.id_veiculo
					INNER JOIN local l (nolock)
						ON l.id_local = i.id_local AND l.sequencia_local = i.sequencia_local
					INNER JOIN configuracao_equipamento c 
						ON c.id_configuracao_equipamento = l.id_configuracao_equipamento
				WHERE	v.id_classe = 'M' 
					AND id_infracao = @id_infracao 
					AND (c.flag_opcao & @flag_opcao_captura_traseira) = 0)

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 11, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	ELSE IF EXISTS(	SELECT i.id_infracao 
					FROM infracao i (nolock)	--Se for infracao de ZMRC e nao é caminhao, já joga para a liberação (11)
						INNER JOIN veiculo v (nolock)
							ON v.id_veiculo = i.id_veiculo
						INNER JOIN cad_veiculo cv (nolock)
							ON cv.placa = v.placa --cruzamento pela placa OCR.
					WHERE cv.id_tipo NOT IN (14, 17)				
					AND i.id_enquadramento = 57463
					AND i.id_infracao = @id_infracao)

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 10, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	ELSE IF EXISTS(	SELECT 
						i.id_infracao 
					FROM infracao i (nolock)              --Se for infracao de ZMRF e nao é onibus, já joga para a liberação (11)
						JOIN veiculo v (nolock) 
							ON v.id_veiculo = i.id_veiculo
						JOIN cad_veiculo cv (nolock)
							ON cv.placa = v.placa --cruzamento pela placa OCR.
					WHERE	cv.id_tipo NOT IN (7, 8)				
						AND i.id_enquadramento = 57461
						AND i.id_infracao = @id_infracao)

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 22, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	ELSE IF EXISTS (SELECT 
						i.id_infracao 
					FROM infracao i (nolock)	--Se for infracao de Rodízio e esta no cadastro de isento vai para liberação (11)
						INNER JOIN veiculo v (nolock)
							ON v.id_veiculo = i.id_veiculo
						INNER JOIN cad_isento ci (nolock)
							ON	ci.placa = v.placa 
							AND ci.id_enquadramento = i.id_enquadramento 
							AND i.data BETWEEN ci.data_inicio AND ci.data_fim 
							/*
							AND	((CAST(i.data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
									OR (ci.horario_inicio > ci.horario_fim AND (CAST(i.data AS time) > ci.horario_inicio OR CAST(i.data AS time) < ci.horario_fim))) -- Caso o período extrapole às 00:00
							*/
					WHERE 	i.id_enquadramento = 57462
						AND i.id_infracao = @id_infracao)

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 6, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	ELSE IF EXISTS (SELECT 
						i.id_infracao 
					FROM infracao i (nolock)	--Se for infracao de ZMRC e esta no cadastro de isento vai para liberação (11)
						INNER JOIN veiculo v (nolock)
							ON v.id_veiculo = i.id_veiculo
						INNER JOIN cad_isento ci (nolock)
							ON	ci.placa = v.placa 
							AND ci.id_enquadramento = i.id_enquadramento 
							AND i.data BETWEEN ci.data_inicio AND ci.data_fim 
							/*
							AND ((CAST(i.data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
									OR (ci.horario_inicio > ci.horario_fim AND (CAST(i.data AS time) > ci.horario_inicio OR CAST(i.data AS time) < ci.horario_fim))) -- Caso o período extrapole às 00:00
							*/
						WHERE 	i.id_enquadramento = 57463
							AND i.id_infracao = @id_infracao)

		BEGIN

			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 13, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao

		END

	ELSE IF EXISTS (SELECT 
						i.id_infracao 
					FROM infracao i (nolock)	--Se for infracao de ZMRF e esta no cadastro de isento vai para liberação (11)
						INNER JOIN veiculo v (nolock) 
							ON v.id_veiculo = i.id_veiculo
						INNER JOIN cad_isento ci (nolock) 
							ON ci.placa = v.placa 
							AND ci.id_enquadramento = i.id_enquadramento 
							AND i.data BETWEEN ci.data_inicio AND ci.data_fim 
							/*
							AND ((CAST(i.data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
								OR (ci.horario_inicio > ci.horario_fim AND (CAST(i.data AS time) > ci.horario_inicio OR CAST(i.data AS time) < ci.horario_fim))) -- Caso o período extrapole às 00:00
							*/
					WHERE 	i.id_enquadramento = 57461
						AND i.id_infracao = @id_infracao)
	
		BEGIN
		
			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 23, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao
		
		END

	ELSE IF EXISTS (SELECT 
						id_infracao
					FROM 
						infracao (nolock)
					WHERE 	data >= '2010-02-13 23:00' and data <= '2010-02-14 03:40' -- problemas no horario de verão
						AND id_enquadramento <> 1
						AND id_infracao = @id_infracao
					)
		BEGIN
	
			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 5, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao
			
		END

	ELSE IF EXISTS (SELECT 
						id_infracao
					FROM 
						infracao (nolock)
					WHERE 	data >= '2010-02-20 23:00' and data <= '2010-02-21 02:00' -- problemas no horario de verão
						AND id_local in (2009, 2013, 2025, 2039)
						AND id_enquadramento <> 1
						AND id_infracao = @id_infracao
					)
		BEGIN
	
			UPDATE infracao with (rowlock)
			SET id_inconsistencia = 5, 
				id_processo = 11, 
				id_processo_concluido = NULL
			WHERE id_infracao = @id_infracao
			
		END
	
	/******************
	FERIADOS
	******************/

	-- Aniversario de São Paulo (25/01)
	UPDATE infracao with (rowlock)
	SET id_inconsistencia = 14, 
		id_processo = 11, 
		id_processo_concluido = NULL
	WHERE id_infracao = @id_infracao 
		AND id_enquadramento in (57461, 57462, 57463) --ZMRF, Rodizio, ZMRC
		AND month(data) = 1 AND day(data) = 25


	-- Carnaval (15,16,17 de Fev) Rodizio Automóvel
	UPDATE infracao with (rowlock) 
	SET id_inconsistencia = 14, 
		id_processo = 11, 
		id_processo_concluido = NULL
	WHERE id_infracao in (	SELECT 
								id_infracao
							FROM infracao i (nolock)
								INNER JOIN veiculo v (nolock)
									ON v.id_veiculo = i.id_veiculo
							WHERE 	i.id_infracao = @id_infracao 
								AND i.id_enquadramento in (57462) --Rodizio
								AND i.data >= '2010-02-15 00:00:00'and i.Data <= '2010-02-17 23:59:59'
								AND v.id_classe <> 'C')

	-- Sexta-Feira Santa (02/04/2010)
	UPDATE infracao with (rowlock) 
	SET id_inconsistencia = 14, 
		id_processo = 11, 
		id_processo_concluido = NULL
	WHERE	id_infracao = @id_infracao 
		AND id_enquadramento in (57461, 57462, 57463) --ZMRF, Rodizio, ZMRC
		AND data >= '2010-04-02 00:00:00' and data <= '2010-04-02 23:59:59'

	-- Tiradentes (21/04)
	UPDATE infracao with (rowlock) 
	SET id_inconsistencia = 14, 
		id_processo = 11, 
		id_processo_concluido = NULL
	WHERE	id_infracao = @id_infracao 
		AND id_enquadramento in (57461, 57462, 57463) --ZMRF, Rodizio, ZMRC
		AND month(data) = 4 AND day(data) = 21 
	
	-- Dia do trabalho (01/05)
	UPDATE infracao with (rowlock) 
	SET id_inconsistencia = 14, 
		id_processo = 11, 
		id_processo_concluido = NULL
	WHERE 	id_infracao = @id_infracao 
		AND id_enquadramento in (57461, 57462, 57463) --ZMRF, Rodizio, ZMRC
		AND month(data) = 5 AND day(data) = 1 

END



