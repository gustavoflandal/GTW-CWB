CREATE PROCEDURE [muralha].[spu_getFluxoDiaHorarioGrafico]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME,
	@Id_Local INT
AS
	--DECLARE @Data_Ini DATETIME = '2023-06-19 00:00:00', @Data_Fim DATETIME = '2023-06-19 09:14:59', @Id_Local INT = 10
	DECLARE @hora VARCHAR(5), @aux CHAR(1), @inicio CHAR(1),
				@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
				@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
    
	SET NOCOUNT ON;
     
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''
    
	DECLARE horas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATETIME = '2023-06-19 00:00:00', @Data_Fim DATETIME = '2023-06-19 09:14:59', @Id_Local INT = 10
	SELECT CONVERT(VARCHAR(5), d.Data, 8) AS hora FROM dbo.fcn_ObterDatasHorasPeriodo(@Data_Ini, DATEADD(MINUTE, -59, @Data_Fim)) d ORDER BY d.Data OPTION(MAXRECURSION 0)
	--SELECT CONVERT(VARCHAR(8), d.Data, 3) +' '+ CONVERT(VARCHAR(5), d.Data, 8) AS data FROM dbo.fcn_ObterDatasHorasPeriodo(@Data_Ini, DATEADD(SECOND, -59, @Data_Fim)) d ORDER BY d.Data OPTION(MAXRECURSION 0)
    
	OPEN horas_cursor
    
	FETCH NEXT FROM horas_cursor
	INTO @hora
     
	WHILE @@FETCH_STATUS = 0
	BEGIN
		SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@hora)) + ']' + @aux
		SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + 'ISNULL([' + LTRIM(RTRIM(@hora)) + '], 0) AS [' + LTRIM(RTRIM(@hora)) + ']' + @aux
         
		FETCH NEXT FROM horas_cursor
		INTO @hora
     
	END
	CLOSE horas_cursor;
	DEALLOCATE horas_cursor;
     
	SET @sqlPivotAux = @sqlPivotAux + ')'
	SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')
	SET @sqlPivotAux = ' PIVOT (SUM(quantidade) FOR hora IN ' + @sqlPivotAux + ') AS cont  OPTION(MAXRECURSION 0)'
     
	SET @colunas_format = @colunas_format + ')'
	SET @colunas_format = REPLACE(@colunas_format, ',)', '')
     
	--PRINT (@colunas_format)
	--PRINT (@sqlPivotAux)
    
    
	SET @sqlParam = 'DECLARE @Data_Ini DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Ini, 126) + ''', @Data_Fim DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Fim, 126) +
	  ''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL'))
    
	SET @sqlTempTable = '
	DECLARE @temp_label AS TABLE (id_label TINYINT, label_dataset VARCHAR(50))
	INSERT INTO @temp_label VALUES (1, ''Fluxo'')
	INSERT INTO @temp_label VALUES (2, ''Vel. média'')
	
	DECLARE @temp_dados AS TABLE (id_label TINYINT, hora VARCHAR(5), quantidade INT)
    INSERT INTO @temp_dados
	SELECT 1 AS id_label,
		   RIGHT(''00''+CAST(DATEPART(HOUR, vtr.data) AS VARCHAR(2)), 2) + '':00'' AS hora,
		   COUNT(*) AS valor
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
	WHERE  vtr.data BETWEEN @Data_Ini AND @Data_Fim
		   AND vtr.id_local = @Id_Local
	GROUP BY
		   vtr.id_local,
		   DATEPART(HOUR, vtr.data) 
		   
	INSERT INTO @temp_dados
	SELECT 2 AS id_label,
		   RIGHT(''00''+CAST(DATEPART(HOUR, vtr.data) AS VARCHAR(2)), 2) + '':00'' AS hora,
		   CAST(ROUND(AVG(CASE WHEN vtr.velocidade BETWEEN 5 AND 200 THEN vtr.velocidade ELSE NULL END),0) AS INT) AS valor
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
	WHERE  vtr.data BETWEEN @Data_Ini AND @Data_Fim
		   AND vtr.id_local = @Id_Local
	GROUP BY
		   vtr.id_local,
		   DATEPART(HOUR, vtr.data) '

    
	SET @sqlSelect = '
	SELECT label_dataset, '
    
	SET @sqlFrom = '
	FROM   (
				SELECT df.hora,
					   df.label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
						   SELECT CONVERT(VARCHAR(5), d.Data, 8) AS hora,
								  lbl.id_label,
								  lbl.label_dataset
						   FROM   dbo.fcn_ObterDatasHorasPeriodo(@Data_Ini, DATEADD(MINUTE, -59, @Data_Fim)) d
								  CROSS JOIN @temp_label lbl
						   GROUP BY
								  d.Data,
								  lbl.id_label,
								  lbl.label_dataset
					   ) df
					   LEFT JOIN @temp_dados t
							ON  t.hora = df.hora
								AND t.id_label = df.id_label
		   ) r '
    
	--PRINT (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)    
	EXEC (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)    
