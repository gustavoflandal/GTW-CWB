CREATE PROCEDURE [muralha].[spu_verifica_anomalia_infracao]
AS
BEGIN

	DECLARE @serie_equipamento INT, @msg VARCHAR(1000) = NULL

	DECLARE infracao_recente_cursor CURSOR FOR 
	SELECT lv.serie_equipamento
	FROM   local_vigente lv
		   LEFT JOIN (
						SELECT id_local, COUNT(*) AS total
						FROM   infracao
						WHERE  data > DATEADD(HOUR, -24, GETDATE())
						GROUP BY
							   id_local
		   ) i
				ON  i.id_local = lv.id_local
	WHERE  lv.desativado = 0
		   AND i.total IS NULL
		   AND lv.serie_equipamento NOT IN (2200018, 2200020, 2200027)
	ORDER BY
		   lv.serie_equipamento

	OPEN infracao_recente_cursor

	FETCH NEXT FROM infracao_recente_cursor 
	INTO @serie_equipamento

	WHILE @@FETCH_STATUS = 0
	BEGIN
	
		SET @msg = CASE WHEN @msg IS NULL THEN CAST(@serie_equipamento AS VARCHAR) ELSE @msg + ', ' + CAST(@serie_equipamento AS VARCHAR) END
    
		FETCH NEXT FROM infracao_recente_cursor 
		INTO @serie_equipamento

	END 
	CLOSE infracao_recente_cursor;
	DEALLOCATE infracao_recente_cursor;


	BEGIN TRY
	
		BEGIN TRAN

		UPDATE muralha.anomalia SET possui_anomalia = 0, desc_anomalia = NULL, data_update = GETDATE() WHERE id = 2

		IF (@msg IS NOT NULL AND @msg != '')
		BEGIN
			UPDATE muralha.anomalia
			SET	   possui_anomalia = 1,
				   desc_anomalia = 'Não há infração nas ultimas 24h: Equipamentos: ' + @msg,
				   data_update = GETDATE()
			WHERE  id = 2
		END

		COMMIT;

	END TRY

	BEGIN CATCH

		ROLLBACK;
		THROW;
	
	END CATCH

	--SELECT id, tipo, possui_anomalia, desc_anomalia, data_update FROM muralha.anomalia

END
