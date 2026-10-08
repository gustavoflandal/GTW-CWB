
--sp_helptext 'muralha.spu_getQtdeFluxoFaixaVelocidade'

CREATE PROCEDURE [muralha].[spu_getQtdeFluxoFaixaVelocidade]  
 @Data_Ini DATETIME,  
 @Data_Fim DATETIME,  
 @Id_Local INT  
AS  
 --DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-31', @Id_Local INT = 18  
 DECLARE @faixasVel VARCHAR(400), @sqlPivotAux VARCHAR(MAX), @aux CHAR(1), @inicio CHAR(1),  
   @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlOrderBy VARCHAR(100),  
   @colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)  
   
 SET NOCOUNT ON;  
    
 SET @inicio = '('    
 SET @aux = ','    
 SET @ini_colunas_format = ''    
    
 DECLARE faixas_vel_cursor CURSOR FOR     
 SELECT LTRIM(RTRIM(fv.descricao)) FROM faixa_velocidade fv ORDER BY fv.id_faixa_velocidade  
    
 OPEN faixas_vel_cursor    
    
 FETCH NEXT FROM faixas_vel_cursor     
 INTO @faixasVel    
    
 WHILE @@FETCH_STATUS = 0    
 BEGIN    
    
 SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@faixasVel)) + ']' + @aux    
 SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + '[' + LTRIM(RTRIM(@faixasVel)) + ']' + @aux    
        
 FETCH NEXT FROM faixas_vel_cursor     
 INTO @faixasVel    
    
 END     
 CLOSE faixas_vel_cursor;    
 DEALLOCATE faixas_vel_cursor;    
    
 SET @sqlPivotAux = @sqlPivotAux + ')'    
 SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')  
 SET @sqlPivotAux = ' PIVOT (SUM(fluxo.quantidade) FOR faixa_vel IN ' + @sqlPivotAux + ') AS contagem_fluxo '  
    
 SET @colunas_format = @colunas_format + ')'    
 SET @colunas_format = REPLACE(@colunas_format, ',)', '')    
    
 --PRINT (@colunas_format)    
 --PRINT (@sqlPivotAux)  
   
 SET @sqlSelect = 'SELECT label_dataset, '  
 SET @sqlFrom = ' FROM   (  
        SELECT ''Fluxo'' AS label_dataset,  
          LTRIM(RTRIM(fx.descricao)) AS faixa_vel,  
          vs.trafego AS quantidade  
        FROM   faixa_velocidade fx (NOLOCK)  
          LEFT JOIN (  
              SELECT vp.id_faixa_velocidade,  
                  vp.trafego  
              FROM   veiculo_sumarizado vp (NOLOCK)  
              WHERE  vp.data BETWEEN ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + '''' + ' AND ' + '''' + CONVERT(VARCHAR(10), @Data_Fim, 120) + ''''
IF @Id_Local IS NOT NULL 			   
BEGIN 
SET @sqlFrom = @sqlFrom + ' AND vp.id_local = ' + RTRIM(CAST(@Id_Local AS VARCHAR)) 
END
SET @sqlFrom = @sqlFrom + 
          ') AS vs  
           ON  fx.id_faixa_velocidade = vs.id_faixa_velocidade  
       ) AS fluxo'  
  
 --PRINT((@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux))  
 EXEC(@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
