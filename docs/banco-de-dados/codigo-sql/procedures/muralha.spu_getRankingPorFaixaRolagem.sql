CREATE PROCEDURE [muralha].[spu_getRankingPorFaixaRolagem]
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
	--DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-10', @Id_Local INT = 18, @Tipo_Info TINYINT = 1, @Id_Municipio INT = 4123, @Id_Regiao TINYINT = 2
	SET NOCOUNT ON;
  
	DECLARE @temp_periodo AS TABLE (data DATE)
	DECLARE @temp_equipamentos AS TABLE (id_local INT, id_pista TINYINT, cod_pista_alternativo TINYINT, nome CHAR(100), posicao_lat DECIMAL(19,17), posicao_lon DECIMAL(19,17), id_localidade INT, id_regiao TINYINT)
	DECLARE @temp_dados AS TABLE (id_pista TINYINT, quantidade INT)
	DECLARE @temp_resultado AS TABLE (ranking TINYINT, faixa VARCHAR(10), quantidade INT)

	INSERT INTO @temp_periodo
	SELECT d.Data AS data FROM dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) d ORDER BY d.Data

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
		SELECT vp.pista AS id_pista,
			   SUM(vp.trafego) AS trafego
		FROM   veiculo_sumarizado vp
			   JOIN @temp_equipamentos lv
					ON  lv.id_local = vp.id_local
						AND lv.id_pista = vp.pista
		WHERE  vp.data BETWEEN @Data_Ini AND @Data_Fim
		GROUP BY
			   vp.pista
	END
	IF (@Tipo_Info = 2)
	BEGIN
		INSERT INTO @temp_dados
		SELECT i.pista AS id_pista,
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
			   i.pista
	END
	IF (@Tipo_Info = 3)
	BEGIN
		INSERT INTO @temp_dados
		SELECT l.id_pista,
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
			   l.id_pista
	END

	INSERT INTO @temp_resultado
	SELECT RANK() OVER(ORDER BY r.quantidade DESC, r.cod_pista_alternativo) AS ranking,
		   r.label_dataset,
		   r.quantidade
	FROM   (
				SELECT df.cod_pista_alternativo,
					   'Faixa ' + RTRIM(CAST(df.cod_pista_alternativo AS CHAR(2))) AS label_dataset,
					   SUM(COALESCE(t.quantidade,0)) AS quantidade
				FROM   (
							SELECT lpv.id_pista,
								   lpv.cod_pista_alternativo
							FROM   @temp_equipamentos lpv
							GROUP BY
								   lpv.id_pista,
								   lpv.cod_pista_alternativo
						) df
						LEFT JOIN @temp_dados t
							ON  t.id_pista = df.id_pista
				GROUP BY
					   df.cod_pista_alternativo
		   ) r

	SELECT faixa AS label_dataset, quantidade AS Total FROM @temp_resultado ORDER BY ranking
