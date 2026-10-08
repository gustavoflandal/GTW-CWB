CREATE PROCEDURE [muralha].[spu_verifica_anomalia_irregularidades]
AS
BEGIN

	DECLARE @data_ini DATETIME = DATEADD(DAY, -10, CAST(GETDATE() AS DATE)), @data_fim DATETIME = DATEADD(MINUTE, -1441, CAST(CAST(GETDATE() AS DATE) AS DATETIME)), @data_ini_qtde DATE = DATEADD(DAY, -1, GETDATE()),
			@porcentagem_aumento FLOAT = 1.6, @serie_equipamento INT, @msg VARCHAR(1000) = NULL

	DECLARE @temp_alerta AS TABLE (id_local INT, data DATE, hora TINYINT, total INT)
	DECLARE @temp_alerta_media_dez_dias AS TABLE (id_local INT, media INT)
	DECLARE @temp_alerta_qtde AS TABLE (id_local INT, qtde INT)

	--SELECT @data_ini, @data_fim, @data_ini_qtde

	INSERT INTO @temp_alerta
	SELECT l.id_local,
		   CAST(a.data AS DATE) AS data,
		   DATEPART(HOUR, a.data) AS hora,
		   COUNT(*) AS total
	FROM   muralha.alerta a
		   JOIN (
					SELECT av.id_alerta,
						   MIN(vtr.data) AS data_veiculo,
						   MIN(vtr.id_local) AS id_local
					FROM   muralha.alerta_veiculo av
						   JOIN muralha.veiculo_tempo_real vtr
								ON  vtr.id = av.id_veiculo_tempo_real
					GROUP BY
						   av.id_alerta
		   ) AS l
				ON  l.id_alerta = a.id
	WHERE  CAST(a.data AS DATE) BETWEEN @data_ini AND @data_fim
	GROUP BY
		   l.id_local,
		   CAST(a.data AS DATE),
		   DATEPART(HOUR, a.data)

	/*
	SELECT r.id_local, AVG(r.vdm) AS media
	FROM (
		SELECT id_local, data, AVG(total) AS vdm FROM @temp_alerta GROUP BY id_local, data
	) r
	GROUP BY r.id_local
	ORDER BY r.id_local
	*/

	INSERT INTO @temp_alerta_qtde
	SELECT l.id_local,
		   COUNT(*) AS total
	FROM   muralha.alerta a
		   JOIN (
					SELECT av.id_alerta,
						   MIN(vtr.data) AS data_veiculo,
						   MIN(vtr.id_local) AS id_local
					FROM   muralha.alerta_veiculo av
						   JOIN muralha.veiculo_tempo_real vtr
								ON  vtr.id = av.id_veiculo_tempo_real
					GROUP BY
						   av.id_alerta
		   ) AS l
				ON  l.id_alerta = a.id
	WHERE  CAST(a.data AS DATE) = @data_ini_qtde
	GROUP BY
		   l.id_local

	--SELECT * FROM @temp_alerta_qtde ORDER BY id_local 

	/*
	SELECT lv.serie_equipamento
	FROM   local_vigente lv
		   JOIN @temp_alerta_qtde t
				ON  t.id_local = lv.id_local
		   JOIN (
					SELECT r.id_local, AVG(r.vdm) AS media
					FROM (
						SELECT id_local, data, AVG(total) AS vdm FROM @temp_alerta GROUP BY id_local, data
					) r
					GROUP BY r.id_local
		   ) m
				ON  m.id_local = t.id_local
	WHERE  lv.desativado = 0
		   AND CEILING(t.qtde * @porcentagem_aumento) > m.media
	*/

	DECLARE irregularidade_cursor CURSOR FOR 
	SELECT lv.serie_equipamento
	FROM   local_vigente lv
		   JOIN @temp_alerta_qtde t
				ON  t.id_local = lv.id_local
		   JOIN (
					SELECT r.id_local, AVG(r.vdm) AS media
					FROM (
						SELECT id_local, data, AVG(total) AS vdm FROM @temp_alerta GROUP BY id_local, data
					) r
					GROUP BY r.id_local
		   ) m
				ON  m.id_local = t.id_local
	WHERE  lv.desativado = 0
		   AND CEILING(t.qtde * @porcentagem_aumento) > m.media
		   AND lv.serie_equipamento NOT IN (2200018, 2200020, 2200027)

	OPEN irregularidade_cursor

	FETCH NEXT FROM irregularidade_cursor 
	INTO @serie_equipamento

	WHILE @@FETCH_STATUS = 0
	BEGIN
	
		SET @msg = CASE WHEN @msg IS NULL THEN CAST(@serie_equipamento AS VARCHAR) ELSE @msg + ', ' + CAST(@serie_equipamento AS VARCHAR) END
    
		FETCH NEXT FROM irregularidade_cursor 
		INTO @serie_equipamento

	END 
	CLOSE irregularidade_cursor;
	DEALLOCATE irregularidade_cursor;

	--SELECT @msg

	BEGIN TRY
	
		BEGIN TRAN

		UPDATE muralha.anomalia SET possui_anomalia = 0, desc_anomalia = NULL, data_update = GETDATE() WHERE id = 3

		IF (@msg IS NOT NULL AND @msg != '')
		BEGIN
			UPDATE muralha.anomalia
			SET	   possui_anomalia = 1,
				   desc_anomalia = 'Aumento significativo no número de irregularidades nas últimas 24h: Equipamentos: ' + @msg,
				   data_update = GETDATE()
			WHERE  id = 3
		END

		COMMIT;

	END TRY

	BEGIN CATCH

		ROLLBACK;
		THROW;
	
	END CATCH

	--SELECT id, tipo, possui_anomalia, desc_anomalia, data_update FROM muralha.anomalia

END
