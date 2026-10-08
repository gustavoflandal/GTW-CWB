CREATE PROCEDURE [dbo].[spu_digitacao_confirmada]
	@id_infracao int
AS

	DECLARE @id_incosistencia INT
	DECLARE @enquadramento INT
	DECLARE @placa CHAR(7)
	DECLARE @data DATETIME
	
	SELECT 
		@enquadramento = id_enquadramento,
		@placa = i.placa,
		@data = data
	FROM 
		infracao i (nolock)
	WHERE 
		id_infracao = @id_infracao
		
	IF @id_incosistencia > 0
		RETURN
	
	IF @enquadramento = 57462 --Rodizio

		BEGIN

			IF EXISTS (	SELECT 
							placa 
						FROM 
							cad_isento ci (nolock)	--Verificando se é isento...
						WHERE	id_enquadramento = @enquadramento 
							AND placa = @placa 
							AND @data BETWEEN data_inicio AND data_fim 
						/*	AND ((CAST(@data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
								OR (ci.horario_inicio > ci.horario_fim AND (CAST(@data AS time) > ci.horario_inicio OR CAST(@data AS time) < ci.horario_fim))) -- Caso o período extrapole às 00:00
						*/
					  )

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 6 -- Veículo isento.
				WHERE id_infracao = @id_infracao

			ELSE IF @placa LIKE '______[12]' AND NOT DATEPART(WEEKDAY, @data) = 2 --Não é segunda-feira? 

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			ELSE IF @placa LIKE '______[34]' AND NOT DATEPART(WEEKDAY, @data) = 3 --Não é terça-feira? 

				UPDATE infracao 
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			ELSE IF @placa LIKE '______[56]' AND NOT DATEPART(WEEKDAY, @data) = 4 --Não é quarta-feira? 

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			ELSE IF @placa LIKE '______[78]' AND NOT DATEPART(WEEKDAY, @data) = 5 --Não é quinta-feira? 

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			ELSE IF @placa LIKE '______[90]' AND NOT DATEPART(WEEKDAY, @data) = 6 --Não é sexta-feira? 

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			/******************
			FERIADOS - RODÍZIO
			******************/

		/*	ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Carnaval
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-02-21 00:00:00' AND '2009-02-25 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Sexta-Feira Santa
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-04-10 00:00:00' AND '2009-04-10 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 7 -- Final de placa liberado.
				WHERE id_infracao = @id_infracao 

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Tiradentes
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-04-20 00:00:00' AND '2009-04-21 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao 

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Dia do trabalho
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-05-01 00:00:00' AND '2009-05-01 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Corpus Christ
							WHERE	id_infracao = @id_infracao 
								AND	data BETWEEN '2009-06-11 00:00:00' AND '2009-06-11 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
								FROM infracao i (nolock)
									INNER JOIN veiculo v (nolock)
										ON v.id_veiculo = i.id_veiculo -- Corpus Christ (recesso)
									WHERE	id_infracao = @id_infracao 
										AND i.data BETWEEN '2009-06-12 00:00:00' AND '2009-06-12 23:59:59' 
										AND NOT v.id_classe = 'C')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao
		*/
			ELSE IF EXISTS (SELECT 
								id_infracao 
							FROM 
								infracao i (nolock)	-- 9 de julho
							WHERE	id_infracao = @id_infracao 
								AND	data BETWEEN '2009-07-09 00:00:00' AND '2009-07-09 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT 
								id_infracao 
							FROM infracao i (nolock)
								JOIN veiculo v (nolock)
									ON v.id_veiculo = i.id_veiculo -- 9 de julho(recesso)
							WHERE	id_infracao = @id_infracao 
								AND i.data BETWEEN '2009-07-10 00:00:00' AND '2009-07-10 23:59:59' 
								AND NOT v.id_classe = 'C')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Independência
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-09-07 00:00:00' AND '2009-09-07 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Nossa Sra. Aparecida
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-10-12 00:00:00' AND '2009-10-12 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Finados
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-11-02 00:00:00' AND '2009-11-02 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Natal
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-12-25 00:00:00' AND '2009-12-25 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Ano novo
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2010-01-01 00:00:00' AND '2010-01-01 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT i.id_infracao 
							FROM infracao i (nolock)
								INNER JOIN veiculo v (nolock)
									ON v.id_veiculo = i.id_veiculo
								LEFT JOIN cad_veiculo cv (nolock)
									ON cv.placa = i.placa
							WHERE	i.id_infracao = @id_infracao 
								AND i.id_enquadramento in (57462) --Rodizio
								AND i.data >= '2010-02-15 00:00:00'and i.Data <= '2010-02-17 23:59:59' -- carnaval
								AND cv.id_tipo not in (14,17) -- caminhão não esta isento no feriado
							)

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i	(nolock)	-- Já aplicado rodízio.
							WHERE	id_enquadramento = @enquadramento 
								AND i.placa = @placa AND NOT id_infracao = @id_infracao 
								AND data BETWEEN DATEADD(MINUTE, -180, @data) AND @data)

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 9 -- Infração de rodízio já aplicada.
				WHERE id_infracao = @id_infracao

	END

	ELSE IF @enquadramento = 57463 --ZMRC

		BEGIN

			IF EXISTS (	SELECT placa 
						FROM cad_veiculo (nolock)    --Verificando se é caminhão mesmo...
						WHERE placa = @placa AND id_tipo NOT IN (14, 17))

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 10 -- Não é caminhão.
				WHERE id_infracao = @id_infracao

			IF EXISTS (	SELECT placa 
						FROM cad_isento ci (nolock)   --Verificando se é isento...
						WHERE	id_enquadramento = @enquadramento 
							AND	placa = @placa 
							AND @data BETWEEN data_inicio AND data_fim 
							AND	((CAST(@data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
								OR (ci.horario_inicio > ci.horario_fim AND (CAST(@data AS time) > ci.horario_inicio OR CAST(@data AS time) < ci.horario_fim)))) -- Caso o período extrapole às 00:00	

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 13 -- Veículo isento.
				WHERE id_infracao = @id_infracao

			/*
			*****************
			FERIADOS - ZMRC
			*****************
			*/

		/*	ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Corpus Christ
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-06-11 00:00:00' AND '2009-06-11 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao
		*/
			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- 9 de julho
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-07-09 00:00:00' AND '2009-07-09 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Independência
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-09-07 00:00:00' AND '2009-09-07 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Nossa Sra. Aparecida
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-10-12 00:00:00' AND '2009-10-12 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Finados
							WHERE id_infracao = @id_infracao 
								AND data BETWEEN '2009-11-02 00:00:00' AND '2009-11-02 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Natal
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-12-25 00:00:00' AND '2009-12-25 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Ano novo
							WHERE id_infracao = @id_infracao 
								AND data BETWEEN '2010-01-01 00:00:00' AND '2010-01-01 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

		END

	ELSE IF @enquadramento = 57461 --ZMRF

		BEGIN

			IF EXISTS (	SELECT placa 
						FROM cad_veiculo (nolock)    --Verificando se é ônibus mesmo...
						WHERE	placa = @placa 
							AND id_tipo NOT IN (7, 8))

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 22 -- Não é caminhão.
				WHERE id_infracao = @id_infracao

			IF EXISTS (	SELECT placa 
						FROM cad_isento ci (nolock)   --Verificando se é isento...
						WHERE	id_enquadramento = @enquadramento 
							AND placa = @placa 
							AND @data BETWEEN data_inicio AND data_fim 
							AND ((CAST(@data AS time) BETWEEN ci.horario_inicio AND (CASE ci.horario_fim WHEN '00:00:00' THEN '23:59:59' ELSE ci.horario_fim END)) 
								OR (ci.horario_inicio > ci.horario_fim AND (CAST(@data AS time) > ci.horario_inicio OR CAST(@data AS time) < ci.horario_fim)))) -- Caso o período extrapole às 00:00
	
				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 23 -- Veículo isento.
				WHERE id_infracao = @id_infracao
			
			/*
			*****************
			FERIADOS - ZMRF
			*****************
			*/

		/*	ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Corpus Christ
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-06-11 00:00:00' AND '2009-06-11 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao
		*/
			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- 9 de julho
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-07-09 00:00:00' AND '2009-07-09 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Independência
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-09-07 00:00:00' AND '2009-09-07 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Nossa Sra. Aparecida
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-10-12 00:00:00' AND '2009-10-12 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Finados
							WHERE	id_infracao = @id_infracao 
								AND	data BETWEEN '2009-11-02 00:00:00' AND '2009-11-02 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Natal
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2009-12-25 00:00:00' AND '2009-12-25 23:59:59')

				UPDATE infracao with (rowlock)
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao

			ELSE IF EXISTS (SELECT id_infracao 
							FROM infracao i (nolock)	-- Ano novo
							WHERE	id_infracao = @id_infracao 
								AND data BETWEEN '2010-01-01 00:00:00' AND '2010-01-01 23:59:59')

				UPDATE infracao with (rowlock) 
				SET id_inconsistencia = 14 -- Infração suspensa - CET.
				WHERE id_infracao = @id_infracao
			
		END

	IF EXISTS (	SELECT v.id_veiculo 
				FROM infracao i (nolock)	-- Moto passando junto com carro.
					INNER JOIN veiculo v (nolock)
						ON	v.id_veiculo = i.id_veiculo
						AND v.id_classe = 'M' 
						AND i.id_infracao = @id_infracao)

			UPDATE infracao with (rowlock) 
			SET id_inconsistencia = 11 -- Imagem frontal de motocicleta.
			WHERE id_infracao = @id_infracao



