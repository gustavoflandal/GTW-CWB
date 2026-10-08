CREATE PROCEDURE [muralha].[spu_getComparativoFluxoInfracao]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME,
	@Id_Local INT,
	@Id_Municipio INT,
	@Id_Regiao TINYINT
AS
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
			@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
 
	SET NOCOUNT ON;
  
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE datas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
	SELECT CONVERT(VARCHAR(8), d.Data, 3) AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d ORDER BY d.Data
  
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
	SET @sqlPivotAux = ' PIVOT (SUM(quantidade) FOR data IN ' + @sqlPivotAux + ') AS cont '
  
	SET @colunas_format = @colunas_format + ')'
	SET @colunas_format = REPLACE(@colunas_format, ',)', '')
  
	--PRINT (@colunas_format)
	--PRINT (@sqlPivotAux)

	/**
	* LABEL
	* 1 --> Fluxo
	* 2 --> Infração
	*/

	SET @sqlParam = 'DECLARE @Data_Ini DATE = ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + ''', @Data_Fim DATE = ''' + CONVERT(VARCHAR(10), @Data_Fim, 120) + ''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL')) +
						',@Id_Municipio INT = ' + RTRIM(ISNULL(CAST(@Id_Municipio AS VARCHAR), 'NULL')) + ', @Id_Regiao TINYINT = ' + RTRIM(ISNULL(CAST(@Id_Regiao AS VARCHAR), 'NULL'))

	SET @sqlTempTable = '
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (id_label TINYINT, data DATE, id_pista TINYINT, quantidade INT)

	INSERT INTO @temp_equipamentos
	SELECT lv.id_local,
		   RTRIM(lv.nome) AS nome,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   lv.id_localidade,
		   lv.id_regiao
	FROM   local_vigente lv
	WHERE  lv.desativado = 0
		   AND lv.id_localidade = ISNULL(@Id_Municipio, lv.id_localidade)
		   AND lv.id_regiao = ISNULL(@Id_Regiao, lv.id_regiao) 
		   AND lv.id_local = ISNULL(@Id_Local, lv.id_local)

	INSERT INTO @temp_dados
	SELECT 1 AS id_label,
		   vp.data,
		   vp.pista AS id_pista,
		   SUM(vp.trafego) AS trafego
	FROM   veiculo_sumarizado vp
		   JOIN @temp_equipamentos lv
				ON lv.id_local = vp.id_local
	WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
	GROUP BY
		   vp.data,
		   vp.pista

	INSERT INTO @temp_dados
	SELECT 2 AS id_label,
		   CAST(i.data AS DATE) AS data,
		   i.pista AS id_pista,
		   COUNT(*) AS qtde
	FROM   infracao i
		   JOIN veiculo v
				ON  v.id_veiculo = i.id_veiculo
		   JOIN @temp_equipamentos lv
				ON lv.id_local = i.id_local
	WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
		   AND i.id_processo NOT IN (99,98)
	GROUP BY
		   CAST(i.data AS DATE),
		   i.pista
	'
 
	SET @sqlSelect = '
	SELECT label_dataset, '

	SET @sqlFrom = '
	FROM   (
				SELECT CONVERT(VARCHAR(8), df.data, 3) AS data,
					   df.label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
							SELECT d.Data AS data,
								   lbl.id_label,
								   lbl.label_dataset
							FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
								   CROSS JOIN (SELECT 1 AS id_label, ''Fluxo'' AS label_dataset UNION SELECT 2 AS id_label, ''Infração'' AS label_dataset) AS lbl
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
