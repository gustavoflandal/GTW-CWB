CREATE PROCEDURE [muralha].[spu_getDistribuicaoPorFaixaRolagem]
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

	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 5
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
			@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
 
	SET NOCOUNT ON;
  
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE datas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-31', @Id_Local INT = 18
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


	SET @sqlParam = 'DECLARE @Data_Ini DATE = ''' + CONVERT(VARCHAR(10), @Data_Ini, 120) + ''', @Data_Fim DATE = ''' + CONVERT(VARCHAR(10), @Data_Fim, 120) +
						''', @Id_Local INT = ' + RTRIM(ISNULL(CAST(@Id_Local AS VARCHAR), 'NULL')) + ', @Tipo_Info TINYINT = ' + RTRIM(CAST(@Tipo_Info AS VARCHAR)) +
						',@Id_Municipio INT = ' + RTRIM(ISNULL(CAST(@Id_Municipio AS VARCHAR), 'NULL')) + ', @Id_Regiao TINYINT = ' + RTRIM(ISNULL(CAST(@Id_Regiao AS VARCHAR), 'NULL'))

	SET @sqlTempTable = '
	DECLARE @temp_equipamentos AS TABLE (id_local INT, id_pista TINYINT, cod_pista_alternativo TINYINT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (data DATE, id_pista TINYINT, quantidade INT)

	INSERT INTO @temp_equipamentos
	SELECT lv.id_local,
		   lv.id_pista,
		   lv.cod_pista_alternativo,
		   RTRIM(lv.nome) AS nome,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   lv.id_localidade,
		   lv.id_regiao
	FROM   local_pista_vigente lv
	WHERE  lv.desativado = 0
		   AND lv.id_localidade = ISNULL(@Id_Municipio, lv.id_localidade)
		   AND lv.id_regiao = ISNULL(@Id_Regiao, lv.id_regiao) 
		   AND lv.id_local = ISNULL(@Id_Local, lv.id_local)

	IF (@Tipo_Info = 1)
	BEGIN
		INSERT INTO @temp_dados
		SELECT vp.data,
			   vp.pista AS id_pista,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
		   JOIN @temp_equipamentos lv
				ON  lv.id_local = vp.id_local
					AND lv.id_pista = vp.pista
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vp.data,
			   vp.pista
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		SELECT CAST(i.data AS DATE) AS data,
			   i.pista AS id_pista,
			   COUNT(*) AS qtde
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
		   JOIN @temp_equipamentos lv
				ON  lv.id_local = i.id_local
					AND lv.id_pista = i.pista
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   CAST(i.data AS DATE),
			   i.pista
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		SELECT CAST(a.data AS DATE) AS data,
			   l.id_pista,
			   COUNT(*) AS qtde
		FROM   muralha.alerta a
			   JOIN (
						SELECT av.id_alerta,
							   MIN(av.id_veiculo_tempo_real) AS id_veiculo_tempo_real,
							   MIN(vtr.data) AS data_veiculo,
							   MIN(vtr.id_local) AS id_local,
							   MIN(vtr.id_pista) AS id_pista
						FROM   muralha.alerta_veiculo av
							   JOIN muralha.veiculo_tempo_real vtr
									ON  vtr.id = av.id_veiculo_tempo_real
						GROUP BY
							   av.id_alerta
			   ) AS l
					ON  l.id_alerta = a.id
		   JOIN @temp_equipamentos lv
				ON  lv.id_local = l.id_local
					AND lv.id_pista = l.id_pista
		WHERE  CAST(a.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   CAST(a.data AS DATE),
			   l.id_pista
	END
	'

	SET @sqlSelect = '
	SELECT label_dataset, '

	SET @sqlFrom = '
	FROM   (
				SELECT CONVERT(VARCHAR(8), df.data, 3) AS data,
					   df.faixa AS label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
							SELECT d.Data AS data,
								   lpv.id_pista,
								   ''Faixa '' + RTRIM(CAST(lpv.cod_pista_alternativo AS CHAR(2))) AS faixa
							FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
								   CROSS JOIN @temp_equipamentos lpv
							GROUP BY
								   d.Data,
								   lpv.id_pista,
								   lpv.cod_pista_alternativo
					   ) df
					   LEFT JOIN @temp_dados t
							ON  t.data = df.data
								AND t.id_pista = df.id_pista
		   ) r '


	--PRINT (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
	EXEC (@sqlParam + @sqlTempTable + @sqlSelect + @colunas_format + @sqlFrom + @sqlPivotAux)
