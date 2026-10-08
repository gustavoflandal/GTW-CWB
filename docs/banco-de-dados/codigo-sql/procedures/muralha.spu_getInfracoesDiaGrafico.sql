

CREATE PROCEDURE [muralha].[spu_getInfracoesDiaGrafico]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME,
	@Id_Local INT
AS
	--DECLARE @Data_Ini DATETIME = '2023-06-19 00:00:00', @Data_Fim DATETIME = '2023-06-19 23:59:59', @Id_Local INT = 10
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
				@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
				@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
    
	SET NOCOUNT ON;
     
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''
    
	DECLARE horas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATETIME = '2023-06-19 00:00:00', @Data_Fim DATETIME = '2023-06-19 23:59:59', @Id_Local INT = 10
	--SELECT CONVERT(VARCHAR(5), d.Data, 8) AS hora FROM dbo.fcn_ObterDatasHorasPeriodo(@Data_Ini, DATEADD(MINUTE, -60, @Data_Fim)) d ORDER BY d.Data OPTION(MAXRECURSION 0)
	SELECT CONVERT(VARCHAR(10), d.Data, 103) AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, DATEADD(MINUTE, -60, @Data_Fim)) d ORDER BY d.Data OPTION(MAXRECURSION 0)
    
	OPEN horas_cursor
    
	FETCH NEXT FROM horas_cursor
	INTO @data
     
	WHILE @@FETCH_STATUS = 0
	BEGIN
		SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@data)) + ']' + @aux
		SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + 'ISNULL([' + LTRIM(RTRIM(@data)) + '], 0) AS [' + LTRIM(RTRIM(@data)) + ']' + @aux
         
		FETCH NEXT FROM horas_cursor
		INTO @data
     
	END
	CLOSE horas_cursor;
	DEALLOCATE horas_cursor;
     
	SET @sqlPivotAux = @sqlPivotAux + ')'
	SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')
	SET @sqlPivotAux = ' PIVOT (SUM(quantidade) FOR data IN ' + @sqlPivotAux + ') AS cont  OPTION(MAXRECURSION 0)'
     
	SET @colunas_format = @colunas_format + ')'
	SET @colunas_format = REPLACE(@colunas_format, ',)', '')
     
	--PRINT (@colunas_format)
	--PRINT (@sqlPivotAux)
    
    
	SET @sqlParam = 'DECLARE @Data_Ini DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Ini, 126) + ''', @Data_Fim DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Fim, 126) +
	  ''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL'))
    
	SET @sqlTempTable = '
	DECLARE @temp_label AS TABLE (id_label TINYINT, label_dataset VARCHAR(50))
	INSERT INTO @temp_label VALUES (1, ''Infração'')
	
	DECLARE @temp_dados AS TABLE (id_label TINYINT, data VARCHAR(10), quantidade INT)
    INSERT INTO @temp_dados
	SELECT 1 AS id_label,
		   CONVERT(VARCHAR(10), CAST(i.data AS DATE), 103) AS data,
		   COUNT(*) AS valor
	FROM   infracao i (NOLOCK)
	WHERE  i.data BETWEEN @Data_Ini AND @Data_Fim
		   AND i.id_local = @Id_Local
	GROUP BY
		   i.id_local,
		   CAST(i.data AS DATE) '

    
	SET @sqlSelect = '
	SELECT label_dataset, '
    
	SET @sqlFrom = '
	FROM   (
				SELECT df.data,
					   df.label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
						   SELECT CONVERT(VARCHAR(10), d.Data, 103) AS data,
								  lbl.id_label,
								  lbl.label_dataset
						   FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, DATEADD(MINUTE, -60, @Data_Fim)) d
								  CROSS JOIN @temp_label lbl
						   GROUP BY
								  d.Data,
								  lbl.id_label,
								  lbl.label_dataset
					   ) df
					   LEFT JOIN @temp_dados t
							ON  t.data = df.data
								AND t.id_label = df.id_label
		   ) r '
    
	--PRINT (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)    
	EXEC (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
