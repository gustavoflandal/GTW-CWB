CREATE PROCEDURE [muralha].[spu_getFluxoEquipamentoPorCategoria]
	@Data_Ini DATETIME,
	@Data_Fim DATETIME,
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

	--DECLARE @Data_Ini DATE = '2025-06-01', @Data_Fim DATE = '2025-06-30', @Tipo_Info TINYINT = 2, @Id_Municipio INT = 7535, @Id_Regiao TINYINT = 5
	SET NOCOUNT ON;
	
	DECLARE @temp_equipamentos AS TABLE (id_local INT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (id_local INT, total INT)

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

	--SELECT * FROM @temp_equipamentos


	IF (@Tipo_Info = 1)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2025-07-01', @Data_Fim DATE = '2025-07-02', @Tipo_Info TINYINT = 1, @Id_Municipio INT = 7535, @Id_Regiao TINYINT = 5
		SELECT vp.id_local,
			   SUM(vp.trafego) AS total
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON lv.id_local = vp.id_local
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vp.id_local
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2025-07-01', @Data_Fim DATE = '2025-07-02', @Tipo_Info TINYINT = 1, @Id_Municipio INT = 7535, @Id_Regiao TINYINT = 5
		SELECT i.id_local,
			   COUNT(*) AS total
		FROM   infracao i
			   JOIN veiculo v
					ON  v.id_veiculo = i.id_veiculo
			  -- JOIN @temp_equipamentos lv
					--ON lv.id_local = i.id_local
		WHERE  CAST(i.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
			   AND i.id_processo NOT IN (99,98)
		GROUP BY
			   i.id_local
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
		SELECT l.id_local,
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
					ON lv.id_local = l.id_local
		WHERE  CAST(a.data AS DATE) BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   l.id_local
	END

	SELECT lv.id_local,
		   RTRIM(lv.nome) AS nome,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   COALESCE(d.total, 0) AS total
	FROM   @temp_equipamentos lv
		   JOIN @temp_dados d
				ON d.id_local = lv.id_local
