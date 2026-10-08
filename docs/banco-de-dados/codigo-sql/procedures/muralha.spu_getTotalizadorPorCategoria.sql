CREATE PROCEDURE [muralha].[spu_getTotalizadorPorCategoria]
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
	SET NOCOUNT ON;
	
	DECLARE @temp_periodo AS TABLE (data DATE)
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_categoria AS TABLE (id_categoria TINYINT, categoria VARCHAR(20))
	DECLARE @temp_dados AS TABLE (id_categoria TINYINT, total INT)

	INSERT INTO @temp_periodo
	SELECT d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d ORDER BY d.Data

	INSERT INTO @temp_categoria
	SELECT 1 AS id_categoria, 'passagens' AS categoria
	INSERT INTO @temp_categoria
	SELECT 2 AS id_categoria, 'infrações' AS categoria
	INSERT INTO @temp_categoria
	SELECT 3 AS id_categoria, 'irregularidades' AS categoria


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
		SELECT 1 AS id_categoria,
			   SUM(vp.trafego) AS total
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vp.id_local
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1
		SELECT 2 AS id_categoria,
			   COUNT(*) AS total
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = i.id_local
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1
		SELECT 3 AS id_categoria,
			   COUNT(*) AS total
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
	END

	SELECT c.categoria, COALESCE(d.total, 0) AS total FROM @temp_dados d JOIN @temp_categoria c ON c.id_categoria = d.id_categoria
