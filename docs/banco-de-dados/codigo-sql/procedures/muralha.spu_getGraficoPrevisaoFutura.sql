CREATE PROCEDURE [muralha].[spu_getGraficoPrevisaoFutura]
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

	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 2, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 5
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
			@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlTempTableCursor VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
 
	SET NOCOUNT ON;
  
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE @Data_Ini_Futuro DATE = DATEADD(DAY, 1, @Data_Fim)
	DECLARE @Data_Fim_Futuro DATE = DATEADD(DAY, 7, @Data_Ini_Futuro)
	DECLARE @temp_periodo AS TABLE (id_periodo TINYINT, data DATE)

	INSERT INTO @temp_periodo
	SELECT 1 AS id_periodo, d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
	INSERT INTO @temp_periodo
	SELECT 2 AS id_periodo, d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini_Futuro, @Data_Fim_Futuro) d

	DECLARE datas_cursor CURSOR FOR
	SELECT CONVERT(VARCHAR(8), d.Data, 3) AS data FROM @temp_periodo d ORDER BY d.Data
  
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
						',@Id_Municipio INT = ' + RTRIM(ISNULL(CAST(@Id_Municipio AS VARCHAR), 'NULL')) + ', @Id_Regiao TINYINT = ' + RTRIM(ISNULL(CAST(@Id_Regiao AS VARCHAR), 'NULL'))

	SET @sqlTempTableCursor = '
	DECLARE @Data_Ini_Futuro DATE = DATEADD(DAY, 1, @Data_Fim)
	DECLARE @Data_Fim_Futuro DATE = DATEADD(DAY, 7, @Data_Ini_Futuro), @data_futura DATE
	DECLARE @temp_periodo AS TABLE (id_periodo TINYINT, data DATE)
	DECLARE @temp_periodo_futuro AS TABLE (data_futura DATE, data_comparar DATE)

	INSERT INTO @temp_periodo
	SELECT 1 AS id_periodo, d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
	INSERT INTO @temp_periodo
	SELECT 2 AS id_periodo, d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini_Futuro, @Data_Fim_Futuro) d

	DECLARE datas_futuras_cursor CURSOR FOR
	SELECT d.data FROM @temp_periodo d WHERE d.id_periodo = 2 ORDER BY d.data
  
	OPEN datas_futuras_cursor
  
	FETCH NEXT FROM datas_futuras_cursor
	INTO @data_futura
  
	WHILE @@FETCH_STATUS = 0
	BEGIN
  
		INSERT INTO @temp_periodo_futuro
		SELECT TOP 5 @data_futura AS data_futura, d.Data AS data_comparar
		FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -40, @data_futura), @data_futura) d
			   LEFT JOIN @temp_periodo_futuro t ON t.data_futura = d.Data
		WHERE  DATEPART(WEEKDAY, @data_futura) = DATEPART(WEEKDAY, d.Data)
			   AND t.data_comparar IS NULL
		ORDER BY d.Data DESC

	FETCH NEXT FROM datas_futuras_cursor
	INTO @data_futura
  
	END
	CLOSE datas_futuras_cursor;
	DEALLOCATE datas_futuras_cursor;
	'

	SET @sqlTempTable = '
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados_futuro AS TABLE (id_label INT, data_futura DATE, data DATE, quantidade INT)
	DECLARE @temp_dados AS TABLE (id_label INT, data DATE, quantidade INT)

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
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vp.data

		INSERT INTO @temp_dados_futuro
		SELECT 2 AS id_label,
			   t.data_futura,
			   vp.data,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vp.id_local
			   JOIN @temp_periodo_futuro t
					ON  t.data_comparar = vp.data
		GROUP BY
			   t.data_futura,
			   vp.data

		INSERT INTO @temp_dados
		SELECT id_label,
			   data_futura,
			   AVG(quantidade) AS quantidade
		FROM   @temp_dados_futuro
		GROUP BY
			   id_label,
			   data_futura

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
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   CAST(i.data AS DATE)

		INSERT INTO @temp_dados_futuro
		SELECT 2 AS id_label,
			   t.data_futura,
			   CAST(i.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = i.id_local
			   JOIN @temp_periodo_futuro t
					ON  t.data_comparar = CAST(i.data AS DATE)
		GROUP BY
			   t.data_futura,
			   CAST(i.data AS DATE)

		INSERT INTO @temp_dados
		SELECT id_label,
			   data_futura,
			   AVG(quantidade) AS quantidade
		FROM   @temp_dados_futuro
		GROUP BY
			   id_label,
			   data_futura
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

		INSERT INTO @temp_dados_futuro
		SELECT 2 AS id_label,
			   t.data_futura,
			   CAST(a.data AS DATE) AS data,
			   CAST(ROUND(COUNT(*) * 0.86, 0) AS INT) AS qtde
		FROM   muralha.alerta a
			   JOIN @temp_periodo_futuro t
					ON  t.data_comparar = CAST(a.data AS DATE)
			   JOIN (
						SELECT av.id_alerta,
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
		GROUP BY
			   t.data_futura,
			   CAST(a.data AS DATE)

		INSERT INTO @temp_dados
		SELECT id_label,
			   data_futura,
			   AVG(quantidade) AS quantidade
		FROM   @temp_dados_futuro
		GROUP BY
			   id_label,
			   data_futura
	END
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
							FROM   @temp_periodo d JOIN (SELECT 1 AS id_label, ''Período atual'' AS label_dataset UNION SELECT 2 AS id_label, ''Período futuro'' AS label_dataset) lbl ON lbl.id_label = d.id_periodo
							GROUP BY
								   d.Data,
								   lbl.id_label,
								   lbl.label_dataset
					   ) df
					   LEFT JOIN @temp_dados t
							ON  t.data = df.data
								AND t.id_label = df.id_label
		   ) r '

	--PRINT (@sqlParam + @sqlTempTableCursor + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
	EXEC (@sqlParam + @sqlTempTableCursor + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
