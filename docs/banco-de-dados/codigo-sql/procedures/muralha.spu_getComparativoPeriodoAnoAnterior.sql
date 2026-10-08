CREATE PROCEDURE [muralha].[spu_getComparativoPeriodoAnoAnterior]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME,
	@Id_Local INT,
	@Tipo_Info TINYINT,
	@Id_Municipio INT,
	@Id_Regiao TINYINT
AS
	/**
	* @Tipo_Info TINYINT
	* 1 => FLUXO
	* 2 => INFRAÇÃO
	* 3 => IRREGULARIDADE
	*/

	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = NULL
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
			@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1),
			@Data_Ini_Ant DATE, @Data_Fim_Ant DATE

	SET @Data_Ini_Ant = DATEADD(YEAR, -1, @Data_Ini)
	SET @Data_Fim_Ant = DATEADD(YEAR, -1, @Data_Fim)

	--SELECT @Data_Ini AS Data_Ini, @Data_Fim AS Data_Fim, @Data_Ini_Ant AS Data_Ini_Ant, @Data_Fim_Ant AS Data_Fim_Ant
	SET NOCOUNT ON;

	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE datas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 5
	SELECT LEFT(CONVERT(VARCHAR(8), data, 3), 5) AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d ORDER BY d.Data
  
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


	SET @sqlParam = 'DECLARE @Data_Ini DATE = ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + ''', @Data_Fim DATE = ''' + CONVERT(VARCHAR(10), @Data_Fim, 120) +
						''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL')) + ', @Tipo_Info TINYINT = ' + RTRIM(CAST(@Tipo_Info AS VARCHAR)) +
						', @Data_Ini_Ant DATE = ''' + CONVERT(VARCHAR(10), @Data_Ini_Ant, 120) + ''', @Data_Fim_Ant DATE = ''' + CONVERT(VARCHAR(10), @Data_Fim_Ant, 120) + '''' +
						',@Id_Municipio INT = ' + RTRIM(ISNULL(CAST(@Id_Municipio AS VARCHAR), 'NULL')) + ', @Id_Regiao TINYINT = ' + RTRIM(ISNULL(CAST(@Id_Regiao AS VARCHAR), 'NULL'))
						
	SET @sqlTempTable = '
	DECLARE @temp_periodo AS TABLE (data DATE)
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (id_label TINYINT, data DATE, quantidade INT)

	INSERT INTO @temp_periodo
	SELECT Data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini_Ant, @Data_Fim_Ant)
	INSERT INTO @temp_periodo
	SELECT Data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim)
	

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


	IF (@Tipo_Info = 1)
	BEGIN
		INSERT INTO @temp_dados
		SELECT 1 AS id_label,
			   vp.data,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vp.id_local
		WHERE  vp.data BETWEEN @Data_Ini_Ant AND @Data_Fim_Ant
		GROUP BY
			   vp.data

		INSERT INTO @temp_dados
		SELECT 2 AS id_label,
			   vp.data,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vp.id_local
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vp.data
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		SELECT 1 AS id_label,
			   CAST(i.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = i.id_local
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini_Ant AND @Data_Fim_Ant
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   CAST(i.data AS DATE)

		INSERT INTO @temp_dados
		SELECT 2 AS id_label,
			   CAST(i.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = i.id_local
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   CAST(i.data AS DATE)
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		SELECT 1 AS id_label,
			   CAST(a.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   muralha.alerta a
			   JOIN (
						SELECT av.id_alerta,
							   MIN(av.id_veiculo_tempo_real) AS id_veiculo_tempo_real,
							   MIN(vtr.data) AS data_veiculo,
							   MIN(vtr.id_local) AS id_local
						FROM   muralha.alerta_veiculo av
							   JOIN muralha.veiculo_tempo_real vtr
									ON  vtr.id = av.id_veiculo_tempo_real
						GROUP BY
							   av.id_alerta
			   ) AS l
					ON  l.id_alerta = a.id
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = l.id_local
		WHERE  CAST(a.data AS DATE) BETWEEN @Data_Ini_Ant AND @Data_Fim_Ant
		GROUP BY
			   CAST(a.data AS DATE)

		INSERT INTO @temp_dados
		SELECT 2 AS id_label,
			   CAST(a.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   muralha.alerta a
			   JOIN (
						SELECT av.id_alerta,
							   MIN(av.id_veiculo_tempo_real) AS id_veiculo_tempo_real,
							   MIN(vtr.data) AS data_veiculo,
							   MIN(vtr.id_local) AS id_local
						FROM   muralha.alerta_veiculo av
							   JOIN muralha.veiculo_tempo_real vtr
									ON  vtr.id = av.id_veiculo_tempo_real
						GROUP BY
							   av.id_alerta
			   ) AS l
					ON  l.id_alerta = a.id
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = l.id_local
		WHERE  CAST(a.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   CAST(a.data AS DATE)
	END
	'
 
	SET @sqlSelect = '
	SELECT label_dataset, '

	SET @sqlFrom = '
	FROM   (
				SELECT LEFT(CONVERT(VARCHAR(8), df.data, 3), 5) AS data,
					   df.label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
							SELECT d.Data AS data,
								   lbl.id_label,
								   lbl.label_dataset
							FROM   @temp_periodo d
								   CROSS JOIN (SELECT 1 AS id_label, ''Ano anterior'' AS label_dataset UNION SELECT 2 AS id_label, ''Ano atual'' AS label_dataset) AS lbl
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
