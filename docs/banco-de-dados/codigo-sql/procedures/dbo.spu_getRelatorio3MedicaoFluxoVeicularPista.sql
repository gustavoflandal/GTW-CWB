
CREATE PROCEDURE [dbo].[spu_getRelatorio3MedicaoFluxoVeicularPista]  
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
	SELECT LTRIM(RTRIM(ISNULL(lpv.codigo_equipamento,''))) + '_' + LTRIM(STR(lpv.serie_equipamento)) + '_' + SUBSTRING(LTRIM(RTRIM(REPLACE(lpv.nome, ' - ', '-'))), 1, 90) + ', faixa ' + LTRIM(STR(lpv.cod_pista_alternativo)) AS nome_pista  
	FROM   local_pista_vigente lpv (NOLOCK)  
	WHERE  lpv.desativado = 0  
	--AND CAST(lpv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE)  
	GROUP BY  
	LTRIM(RTRIM(ISNULL(lpv.codigo_equipamento,''))) + '_' + LTRIM(STR(lpv.serie_equipamento)) + '_' + SUBSTRING(LTRIM(RTRIM(REPLACE(lpv.nome, ' - ', '-'))), 1, 90) + ', faixa ' + LTRIM(STR(lpv.cod_pista_alternativo)),  
	lpv.id_local,  
	lpv.cod_pista_alternativo  
	ORDER BY  
	lpv.id_local,  
	lpv.cod_pista_alternativo  
       
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
LEFT JOIN (SELECT vs.dia,LTRIM(RTRIM(lpv.codigo_equipamento)) + ''_'' + LTRIM(STR(lpv.serie_equipamento)) + ''_'' + SUBSTRING(LTRIM(RTRIM(REPLACE(lpv.nome, '' - '', ''-''))), 1, 90) + '', faixa '' + LTRIM(STR(lpv.cod_pista_alternativo)) AS nome_pista,
SUM(vs.veiculos_detectados) AS fluxo_veicular 
FROM veiculo_sumarizado_relatorio vs (NOLOCK) JOIN local_pista_vigente lpv (NOLOCK) ON vs.id_local = lpv.id_local AND vs.id_pista = lpv.id_pista   
WHERE vs.dia BETWEEN ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + '''' + ' AND ' + '''' + CONVERT(VARCHAR(10), @Data_Fim, 120) + '''  
--AND lpv.desativado = 0  
--AND CAST(lpv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE)  
GROUP BY vs.dia,LTRIM(RTRIM(lpv.codigo_equipamento)) + ''_'' + LTRIM(STR(lpv.serie_equipamento)) + ''_'' + SUBSTRING(LTRIM(RTRIM(REPLACE(lpv.nome, '' - '', ''-''))), 1, 90) + '', faixa '' + LTRIM(STR(lpv.cod_pista_alternativo))
) AS fluxo ON  fluxo.dia = datas.Data '

	SET @sqlOrderBy = ' ORDER BY Data '
  
	--PRINT(LEN(@sqlPivotAux))
	EXEC(@sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux + @sqlOrderBy)
