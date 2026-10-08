CREATE PROCEDURE [muralha].[spu_getVeiculosPorPeriodo]    
 @Data_Ini DATETIME,    
 @Data_Fim DATETIME,    
 @Id_Local INT,  
 @Id_Tipo INT  
AS    
 /**    
 * @Tipo_Info TINYINT    
 * 1 => MINUTO    
 * 2 => HORA    
 * 3 => DIA    
 */    
   
 --DECLARE @Data_Ini DATETIME = '2021-12-16 16:50:00', @Data_Fim DATETIME = '2021-12-16 16:59:59', @Id_Local INT = 18, @Id_Tipo TINYINT = 1
 --DECLARE @Data_Ini DATETIME = '2021-12-16 10:00:00', @Data_Fim DATETIME = '2021-12-16 16:59:59', @Id_Local INT = 18, @Id_Tipo TINYINT = 2
 --DECLARE @Data_Ini DATETIME = '2021-12-10 10:00:00', @Data_Fim DATETIME = '2021-12-16 16:59:59', @Id_Local INT = 18, @Id_Tipo TINYINT = 3
 DECLARE @tamanhoLabel INT  
  
 IF @Id_Tipo = 1   
 SET @tamanhoLabel = 14  
  
 IF @Id_Tipo = 2   
 SET @tamanhoLabel = 11  
  
 IF @Id_Tipo = 3  
 SET @tamanhoLabel = 8  
    
 DECLARE @data VARCHAR(14), @aux CHAR(1), @inicio CHAR(1),    
   @sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),    
   @colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)    
     
 SET NOCOUNT ON;    
      
 SET @inicio = '('    
 SET @aux = ','    
 SET @ini_colunas_format = ''    
    
 DECLARE datas_cursor CURSOR FOR    
 ---- DECLARE @Data_Ini DATETIME = '2021-12-10 10:45:00', @Data_Fim DATETIME = '2021-12-11 11:45:00', @Id_Local INT = 18, @Tipo_Info TINYINT = 1, @tamanhoLabel INT = 8   
 SELECT DISTINCT LEFT( CONVERT(VARCHAR(8), d.Data, 3) +' '+ CONVERT(VARCHAR(5), d.Data, 8) , @tamanhoLabel ) AS data FROM dbo.fcn_ObterDatasMinutosPeriodo(@Data_Ini, DATEADD(SECOND, -59, @Data_Fim)) d ORDER BY 1 OPTION(MAXRECURSION 0)  
      
 OPEN datas_cursor    
      
 FETCH NEXT FROM datas_cursor    
 INTO @data    
      
 WHILE @@FETCH_STATUS = 0    
 BEGIN    
      
  SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@data)) + ']' + @aux    
  SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + 'ISNULL([' + LTRIM(RTRIM(@data)) + '], 0) AS [' + LTRIM(RTRIM(@data)) + ']' + @aux    
          
 FETCH NEXT FROM datas_cursor    
 INTO @data    
      
 END    
 CLOSE datas_cursor;    
 DEALLOCATE datas_cursor;    
      
 SET @sqlPivotAux = @sqlPivotAux + ')'    
 SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')    
 SET @sqlPivotAux = ' PIVOT (SUM(quantidade) FOR data IN ' + @sqlPivotAux + ') AS cont OPTION(MAXRECURSION 0) '    
      
 SET @colunas_format = @colunas_format + ')'    
 SET @colunas_format = REPLACE(@colunas_format, ',)', '')    
      
 PRINT (@colunas_format)    
 PRINT (@sqlPivotAux)    
    

 SET @sqlParam = 'DECLARE @Data_Ini DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Ini, 126) + ''', @Data_Fim DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Fim, 126) +    
      ''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL')) + ', @tamanhoLabel INT = ' + RTRIM(CAST(@tamanhoLabel AS VARCHAR)) + ',@Id_Tipo INT = ' + RTRIM(CAST(@Id_Tipo AS VARCHAR))
    
 SET @sqlTempTable = '    
 DECLARE @temp_periodo AS TABLE (data DATETIME)
 DECLARE @temp_dados AS TABLE (data VARCHAR(14), quantidade INT)

 IF (@Id_Tipo = 1)
 BEGIN
	INSERT INTO @temp_periodo
	SELECT Data FROM dbo.fcn_ObterDatasMinutosPeriodo(@Data_Ini, @Data_Fim) d OPTION(MAXRECURSION 0) 
 END

 IF (@Id_Tipo = 2)
 BEGIN
	INSERT INTO @temp_periodo
	SELECT Data FROM dbo.fcn_ObterDatasHorasPeriodo(@Data_Ini, @Data_Fim) d OPTION(MAXRECURSION 0) 
 END

 IF (@Id_Tipo = 3)
 BEGIN
	INSERT INTO @temp_periodo
	SELECT Data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
 END
    
  INSERT INTO @temp_dados    
  SELECT    
      LEFT( CONVERT(VARCHAR(8), vp.Data, 3) +'' ''+ CONVERT(VARCHAR(5), vp.Data, 8) , @tamanhoLabel ) AS data,    
      COUNT(*) AS trafego    
  FROM   veiculo_pesquisa vp    
  WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim '
  IF (@Id_Local IS NOT NULL)
  BEGIN
      SET @sqlTempTable = @sqlTempTable + ' 
	  AND vp.id_local = @Id_Local '
  END
  SET @sqlTempTable = @sqlTempTable + '
  GROUP BY    
      LEFT( CONVERT(VARCHAR(8), vp.Data, 3) +'' ''+ CONVERT(VARCHAR(5), vp.Data, 8) , @tamanhoLabel )   
 '    
     
 SET @sqlSelect = '    
 SELECT label_dataset, '    
    
 SET @sqlFrom = '    
 FROM   (    
    SELECT df.data,   
        df.label_dataset,    
        ISNULL(t.quantidade,0) AS quantidade    
    FROM   (    
       SELECT LEFT( CONVERT(VARCHAR(8), d.Data, 3) +'' ''+ CONVERT(VARCHAR(5), d.Data, 8), @tamanhoLabel ) AS data,      
           0 AS id_label,    
           ''Fluxo Veicular'' AS label_dataset    
       FROM   @temp_periodo d     
       GROUP BY    
           d.Data  
        ) df    
        LEFT JOIN @temp_dados t    
       ON  t.data = df.data    
     ) r  '    
    
 --PRINT (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)    
 EXEC (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)    
