CREATE PROCEDURE [muralha].[spu_getCalendarioIntensidade]
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

	DECLARE @temp_periodo AS TABLE (data DATE, ano SMALLINT, semana_ano SMALLINT, dia_semana TINYINT, dia_semana_desc VARCHAR(20))
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (data DATE, ano SMALLINT, semana_ano SMALLINT, dia_semana TINYINT, quantidade INT)
	DECLARE @temp_resultado AS TABLE (ano SMALLINT, semana_ano SMALLINT, dia_semana_desc VARCHAR(20), valor VARCHAR(40))

	SET NOCOUNT ON;

	SET LANGUAGE 'Brazilian'
	INSERT INTO @temp_periodo
	SELECT d.Data AS data,
		   YEAR(d.Data) AS ano,
		   DATEPART(WEEK, d.Data) AS semana_ano,
		   DATEPART(WEEKDAY, d.Data) AS dia_semana,
		   REPLACE(REPLACE(
			   CASE WHEN CHARINDEX('-', LOWER(DATENAME(WEEKDAY, d.Data))) = 0
					THEN LOWER(DATENAME(WEEKDAY, d.Data))
					ELSE SUBSTRING(LOWER(DATENAME(WEEKDAY, d.Data)), 0, CHARINDEX('-', LOWER(DATENAME(WEEKDAY, d.Data))))
			   END, 'á','a'), 'ç', 'c')
	FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d
	ORDER BY
		   d.Data

	--SELECT * FROM @temp_periodo


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
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1
		SELECT vs.data,
			   YEAR(vs.data) AS ano,
			   DATEPART(WEEK, vs.data) AS semana_ano,
			   DATEPART(WEEKDAY, vs.data) AS dia_semana,
			   SUM(vs.trafego) AS quantidade
		FROM   veiculo_sumarizado vs
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vs.id_local
		WHERE  vs.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vs.data,
			   YEAR(vs.data),
			   DATEPART(WEEK, vs.data),
			   DATEPART(WEEKDAY, vs.data)
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1
		SELECT CAST(i.data AS DATE) AS data,
			   YEAR(i.data) AS ano,
			   DATEPART(WEEK, i.data) AS semana_ano,
			   DATEPART(WEEKDAY, i.data) AS dia_semana,
			   COUNT(*) AS quantidade
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = i.id_local
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   CAST(i.data AS DATE),
			   YEAR(i.data),
			   DATEPART(WEEK, i.data),
			   DATEPART(WEEKDAY, i.data)
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1
		--DECLARE @Data_Ini DATE = '2022-01-01', @Data_Fim DATE = '2022-01-20', @Id_Local INT = 1, @Tipo_Info TINYINT = 3
		SELECT CAST(a.data AS DATE) AS data,
			   YEAR(a.data) AS ano,
			   DATEPART(WEEK, a.data) AS semana_ano,
			   DATEPART(WEEKDAY, a.data) AS dia_semana,
			   COUNT(*) AS quantidade
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
			   CAST(a.data AS DATE),
			   YEAR(a.data),
			   DATEPART(WEEK, a.data),
			   DATEPART(WEEKDAY, a.data)
	END

	--SELECT * FROM @temp_dados

	INSERT INTO @temp_resultado
	SELECT p.ano,
		   p.semana_ano,
		   p.dia_semana_desc,
		   CONVERT(VARCHAR(10), p.data, 103) + ';' + CAST(ISNULL(d.quantidade,0) AS VARCHAR(10)) AS valor
	FROM   @temp_periodo p
		   LEFT JOIN @temp_dados d
				ON  d.data = p.data

	SELECT semana_ano,
		   [domingo],[segunda],[terca],[quarta],[quinta],[sexta],[sabado]
	FROM   @temp_resultado
	PIVOT  (
				MAX(valor)
				FOR dia_semana_desc IN ([domingo],[segunda],[terca],[quarta],[quinta],[sexta],[sabado])
		   ) cont
	ORDER BY
		   ano,
		   semana_ano
