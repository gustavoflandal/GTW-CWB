CREATE PROCEDURE [muralha].[spu_getVeiculosPorClassificacao]    
 @Data_Ini DATETIME,    
 @Data_Fim DATETIME,    
 @Id_Local INT  
AS    
 /**    
 * @Tipo_Info TINYINT    
 * 1 => FLUXO    
 * 2 => INFRAÇÃO    
 * 3 => IRREGULARIDADE    
 */    
    
 --DECLARE @Data_Ini DATETIME = '2021-12-14 08:40:00', @Data_Fim DATETIME = '2021-12-14 08:45:59', @Id_Local INT = 18, @Tipo_Info TINYINT = 1    
 DECLARE @data VARCHAR(14), @aux CHAR(1), @inicio CHAR(1),    
   @sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),    
   @colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)    
     
 SET NOCOUNT ON;    
      
 SET @inicio = '('    
 SET @aux = ','    
 SET @ini_colunas_format = ''    
    
 DECLARE datas_cursor CURSOR FOR    
 ---- DECLARE @Data_Ini DATETIME = '2021-12-14 08:40:00', @Data_Fim DATETIME = '2021-12-14 08:45:59', @Id_Local INT = 18, @Tipo_Info TINYINT = 1   
 SELECT CONVERT(VARCHAR(8), d.Data, 3) +' '+ CONVERT(VARCHAR(5), d.Data, 8) AS data FROM dbo.fcn_ObterDatasMinutosPeriodo(@Data_Ini, DATEADD(SECOND, -59, @Data_Fim)) d ORDER BY d.Data   OPTION(MAXRECURSION 0)  
      
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
 SET @sqlPivotAux = ' PIVOT (SUM(quantidade) FOR data IN ' + @sqlPivotAux + ') AS cont  OPTION(MAXRECURSION 0)'    
      
 SET @colunas_format = @colunas_format + ')'    
 SET @colunas_format = REPLACE(@colunas_format, ',)', '')    
      
 --PRINT (@colunas_format)    
 --PRINT (@sqlPivotAux)    
    
    
 SET @sqlParam = 'DECLARE @Data_Ini DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Ini, 126) + ''', @Data_Fim DATETIME = ''' + CONVERT(VARCHAR(19), @Data_Fim, 126) +    
      ''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL'))
    
 SET @sqlTempTable = '    
 DECLARE @temp_dados AS TABLE (id_label TINYINT, data CHAR(14), quantidade INT)    
    
  INSERT INTO @temp_dados    
  SELECT p.id AS id_label,    
      CONVERT(VARCHAR(8), vp.Data, 3) +'' ''+ CONVERT(VARCHAR(5), vp.Data, 8) AS data,    
      COUNT(*) AS trafego    
  FROM   veiculo_pesquisa vp    
      JOIN muralha.v_porte_veiculo_ref p    
     ON  p.id_classe = vp.id_classe    
  WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim '
  IF (@Id_Local IS NOT NULL)
  BEGIN
	SET @sqlTempTable = @sqlTempTable + '
	AND vp.id_local = @Id_Local'
  END
  SET @sqlTempTable = @sqlTempTable + '
  GROUP BY    
      p.id,    
      CONVERT(VARCHAR(8), vp.Data, 3) +'' ''+ CONVERT(VARCHAR(5), vp.Data, 8)   
 '    
     
 SET @sqlSelect = '    
 SELECT label_dataset, '    
    
 SET @sqlFrom = '    
 FROM   (    
    SELECT df.data,   
        df.label_dataset,    
        ISNULL(t.quantidade,0) AS quantidade    
    FROM   (    
       SELECT CONVERT(VARCHAR(8), d.Data, 3) +'' ''+ CONVERT(VARCHAR(5), d.Data, 8) AS data,      
           lbl.id_label,    
           lbl.label_dataset    
       FROM   dbo.fcn_ObterDatasMinutosPeriodo(@Data_Ini, @Data_Fim) d    
           CROSS JOIN (SELECT p.id AS id_label, p.porte AS label_dataset FROM muralha.v_porte_veiculo_ref p GROUP BY p.id, p.porte) AS lbl    
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
