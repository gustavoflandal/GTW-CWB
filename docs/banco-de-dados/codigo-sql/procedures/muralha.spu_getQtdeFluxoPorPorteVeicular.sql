CREATE PROCEDURE [muralha].[spu_getQtdeFluxoPorPorteVeicular]  
 @Data_Ini DATETIME,  
 @Data_Fim DATETIME,  
 @Id_Local INT  
AS  
  
 --DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-01', @Id_Local INT = 18  
 DECLARE @classificacoes VARCHAR(400), @sqlPivotAux VARCHAR(MAX), @aux CHAR(1), @inicio CHAR(1),  
   @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlOrderBy VARCHAR(100),  
   @colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)  
   
 SET NOCOUNT ON;  
    
 SET @inicio = '('    
 SET @aux = ','    
 SET @ini_colunas_format = ''    
    
 DECLARE locais_cursor CURSOR FOR     
 SELECT vpvr.porte FROM muralha.v_porte_veiculo_ref vpvr GROUP BY vpvr.id, vpvr.porte ORDER BY vpvr.id  
    
 OPEN locais_cursor    
    
 FETCH NEXT FROM locais_cursor     
 INTO @classificacoes    
    
 WHILE @@FETCH_STATUS = 0    
 BEGIN    
    
 SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@classificacoes)) + ']' + @aux    
 SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + '[' + LTRIM(RTRIM(@classificacoes)) + ']' + @aux    
        
 FETCH NEXT FROM locais_cursor     
 INTO @classificacoes    
    
 END     
 CLOSE locais_cursor;    
 DEALLOCATE locais_cursor;    
    
 SET @sqlPivotAux = @sqlPivotAux + ')'    
 SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')  
 SET @sqlPivotAux = ' PIVOT (SUM(fluxo.quantidade) FOR porte IN ' + @sqlPivotAux + ') AS contagem_fluxo '  
    
 SET @colunas_format = @colunas_format + ')'    
 SET @colunas_format = REPLACE(@colunas_format, ',)', '')    
    
 --PRINT (@colunas_format)    
 --PRINT (@sqlPivotAux)  
   
 SET @sqlSelect = 'SELECT '  
 SET @sqlFrom = ' FROM   (  
        SELECT cv.porte,  
            ISNULL(SUM(fluxo.trafego), 0) AS quantidade  
        FROM   muralha.v_porte_veiculo_ref cv  
            LEFT JOIN (  
             SELECT vs.id_classe,  
                 vs.trafego  
             FROM   veiculo_sumarizado vs  
             WHERE  vs.data BETWEEN ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + '''' + ' AND ' + '''' + CONVERT(VARCHAR(10), @Data_Fim, 120) + ''''
			 IF (@Id_Local IS NOT NULL)
			 BEGIN
                 SET @sqlFrom = @sqlFrom + '
				 AND vs.id_local = ' + RTRIM(CAST(@Id_Local AS VARCHAR))
			END
			SET @sqlFrom = @sqlFrom + '
            ) fluxo  
           ON  fluxo.id_classe = cv.id_classe  
        GROUP BY  
            cv.porte  
         ) AS fluxo'  
  
 --PRINT((@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux))  
 EXEC(@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
