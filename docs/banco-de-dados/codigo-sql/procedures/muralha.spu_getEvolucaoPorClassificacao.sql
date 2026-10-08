CREATE PROCEDURE [muralha].[spu_getEvolucaoPorClassificacao]
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
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = NULL, @Tipo_Info TINYINT = 2, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
	DECLARE @data VARCHAR(10), @aux CHAR(1), @inicio CHAR(1),
			@sqlParam VARCHAR(MAX), @sqlTempTable VARCHAR(MAX), @sqlSelect VARCHAR(MAX), @sqlFrom VARCHAR(MAX), @sqlLabelCategoria VARCHAR(MAX), @sqlPivotAux VARCHAR(MAX), @sqlOrderBy VARCHAR(100),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
 
	SET NOCOUNT ON;
  
	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE datas_cursor CURSOR FOR
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
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
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (id_label INT, data DATE, quantidade INT)
	DECLARE @temp_tipo AS TABLE (id TINYINT, id_tipo UNIQUEIDENTIFIER, descricao VARCHAR(20))

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
		SELECT p.id AS id_label,
			   vp.data,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON lv.id_local = vp.id_local
			   JOIN muralha.v_porte_veiculo_ref p
					ON  p.id_classe = vp.id_classe
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   p.id,
			   vp.data
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		SELECT eri.id_enquadramento AS id_label,
			   CAST(i.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON lv.id_local = i.id_local
			   JOIN muralha.v_enquadramentos_dashboard eri
					ON  eri.id_enquadramento = i.id_enquadramento
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   eri.id_enquadramento,
			   CAST(i.data AS DATE)
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_tipo
		SELECT ROW_NUMBER() OVER(ORDER BY tao.descricao_sms) AS id, tao.id AS id_tipo, tao.descricao_sms FROM muralha.tipo_alerta_ocorrencia tao

		INSERT INTO @temp_dados
		SELECT t.id AS id_label,
			   CAST(a.data AS DATE) AS data,
			   COUNT(*) AS qtde
		FROM   muralha.alerta a
			   JOIN @temp_tipo t
					ON  t.id_tipo = a.id_tipo_alerta_ocorrencia
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
					ON lv.id_local = l.id_local
		WHERE  CAST(a.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   t.id,
			   CAST(a.data AS DATE)
	END
	'
 
	SET @sqlSelect = '
	SELECT label_dataset, '

	IF (@Tipo_Info = 1)
	BEGIN
		SET @sqlLabelCategoria = ' CROSS JOIN (SELECT p.id AS id_label, p.porte AS label_dataset FROM muralha.v_porte_veiculo_ref p GROUP BY p.id, p.porte) AS lbl '
	END
	IF (@Tipo_Info = 2)
	BEGIN
		SET @sqlLabelCategoria = ' CROSS JOIN (SELECT eri.id_enquadramento AS id_label, dbo.InitCap(eri.descricao) AS label_dataset FROM muralha.v_enquadramentos_dashboard eri) AS lbl '
	END
	IF (@Tipo_Info = 3)
	BEGIN
		SET @sqlLabelCategoria = ' CROSS JOIN (SELECT t.id AS id_label, t.descricao AS label_dataset FROM @temp_tipo t) AS lbl '
	END

	SET @sqlFrom = '
	FROM   (
				SELECT CONVERT(VARCHAR(8), df.data, 3) AS data,
					   df.label_dataset,
					   ISNULL(t.quantidade,0) AS quantidade
				FROM   (
							SELECT d.Data AS data,
								   lbl.id_label,
								   lbl.label_dataset
							FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d ' +
								   @sqlLabelCategoria + '
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
