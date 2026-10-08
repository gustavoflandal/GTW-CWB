
CREATE PROCEDURE [dbo].[spu_getRelatorio3MedicaoFluxoVeicular]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME
AS

	--DECLARE @Data_Ini DATETIME = '2019-03-01 00:00:00', @Data_Fim DATETIME = '2019-03-31 23:59:59'
	DECLARE @locais VARCHAR(400), @sqlPivotAux VARCHAR(MAX), @aux CHAR(1), @inicio CHAR(1),
			@sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
 
	SET NOCOUNT ON;
  
	SET @inicio = '('  
	SET @aux = ','  
	SET @ini_colunas_format = ''  
  
	DECLARE locais_cursor CURSOR FOR   
	SELECT LTRIM(RTRIM(ISNULL(lv.codigos_equipamentos,''))) + '_' + LTRIM(STR(lv.serie_equipamento)) + '_' + SUBSTRING(LTRIM(RTRIM(REPLACE(lv.nome, ' - ', '-'))), 1, 90) AS nome_pista  
	FROM   local_vigente lv (NOLOCK)  
	WHERE  lv.desativado = 0  
		   --AND CAST(lv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE)  
	GROUP BY  
		   LTRIM(RTRIM(ISNULL(lv.codigos_equipamentos,''))) + '_' + LTRIM(STR(lv.serie_equipamento)) + '_' + SUBSTRING(LTRIM(RTRIM(REPLACE(lv.nome, ' - ', '-'))), 1, 90), lv.id_local  
	ORDER BY  
		   lv.id_local  
  
	OPEN locais_cursor  
  
	FETCH NEXT FROM locais_cursor   
	INTO @locais  
  
	WHILE @@FETCH_STATUS = 0  
	BEGIN  
  
	SELECT @sqlPivotAux = ISNULL(@sqlPivotAux, @inicio)  + '[' + LTRIM(RTRIM(@locais)) + ']' + @aux  
	SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + '[' + LTRIM(RTRIM(@locais)) + ']' + @aux  
      
	FETCH NEXT FROM locais_cursor   
	INTO @locais  
  
	END   
	CLOSE locais_cursor;  
	DEALLOCATE locais_cursor;  
  
	SET @sqlPivotAux = @sqlPivotAux + ')'  
	SET @sqlPivotAux = REPLACE(@sqlPivotAux, '],)', '])')
	SET @sqlPivotAux = ' PIVOT (SUM(fluxo.fluxo_veicular) FOR nome_pista IN ' + @sqlPivotAux + ') AS contagem_fluxo '
  
	SET @colunas_format = @colunas_format + ')'  
	SET @colunas_format = REPLACE(@colunas_format, ',)', '')  
  
	--PRINT (@colunas_format)  
	--PRINT (@sqlPivotAux)
  
	SET @sqlSelect = 'SELECT CONVERT(VARCHAR(10), Data, 103) AS Data,  
     DATEPART(WEEKDAY, Data) AS dia_semana,  
     CASE WHEN DATEPART(WEEKDAY, Data) = 1 THEN ''Domingo''  
    WHEN DATEPART(WEEKDAY, Data) = 2 THEN ''Segunda''  
    WHEN DATEPART(WEEKDAY, Data) = 3 THEN ''Terça''  
    WHEN DATEPART(WEEKDAY, Data) = 4 THEN ''Quarta''  
    WHEN DATEPART(WEEKDAY, Data) = 5 THEN ''Quinta''  
    WHEN DATEPART(WEEKDAY, Data) = 6 THEN ''Sexta''  
    WHEN DATEPART(WEEKDAY, Data) = 7 THEN ''Sábado''  
     END AS dia_semana_desc, '

	SET @sqlFrom = 'FROM dbo.fcn_ObterDatasPeriodo('''+CONVERT(VARCHAR(10), @Data_Ini, 120)+''','''+CONVERT(VARCHAR(10), @Data_Fim, 120)+''') datas
LEFT JOIN (SELECT vs.dia,LTRIM(RTRIM(ISNULL(lv.codigos_equipamentos,''''))) + ''_'' + LTRIM(STR(lv.serie_equipamento)) + ''_'' + SUBSTRING(LTRIM(RTRIM(REPLACE(lv.nome, '' - '', ''-''))),1,90) AS nome_pista,
SUM(vs.veiculos_detectados) AS fluxo_veicular
FROM veiculo_sumarizado_relatorio vs (NOLOCK) JOIN local_vigente lv (NOLOCK) ON vs.id_local = lv.id_local
WHERE vs.dia BETWEEN ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + '''' + ' AND ' + '''' + CONVERT(VARCHAR(10), @Data_Fim, 120) + '''
--AND lv.desativado = 0
--AND CAST(lv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE)
GROUP BY vs.dia,LTRIM(RTRIM(ISNULL(lv.codigos_equipamentos,''''))) + ''_'' + LTRIM(STR(lv.serie_equipamento)) + ''_'' + SUBSTRING(LTRIM(RTRIM(REPLACE(lv.nome, '' - '', ''-''))),1,90)
) AS fluxo ON  fluxo.dia = datas.Data '

	SET @sqlOrderBy = ' ORDER BY Data '

	--PRINT(LEN(@sqlPivotAux))
	EXEC(@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux + @sqlOrderBy)
