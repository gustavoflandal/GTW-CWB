CREATE PROCEDURE [dbo].[spu_obterInfoVelocidadeEquipamento]
	@Id_Local INT,
	@Data DATE
AS

	SET NOCOUNT ON;

	--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
	DECLARE @temp_veiculo AS TABLE
	(
		[id_veiculo] INT,
		[id_veiculo_local] INT,
		[data] DATETIME,
		[placa] CHAR(7),
		[velocidade] DECIMAL(6,1),
		[comprimento] DECIMAL(6,1),
		[pista] TINYINT,
		[flag] INT,
		[segundos] DECIMAL(8,3),
		[id_veiculo_unic] BIGINT,
		[id_classe] CHAR(1),
		[id_local] INT,
		[sequencia_local] TINYINT,
		[ocupacao] INT,
		[id_faixa_velocidade] INT
	)

	INSERT INTO @temp_veiculo
	--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
	SELECT id_veiculo,
		   id_veiculo_local,
		   data,
		   placa,
		   velocidade,
		   comprimento,
		   pista,
		   flag,
		   segundos,
		   id_veiculo_unic,
		   id_classe,
		   id_local,
		   sequencia_local,
		   ocupacao,
		   id_faixa_velocidade
	FROM   veiculo_pesquisa vp (NOLOCK)
	WHERE  vp.id_local = @Id_Local
		   AND CAST(vp.data AS DATE) = @Data
		   AND vp.velocidade BETWEEN 5 AND 200

	SET NOCOUNT OFF;

	--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
	SELECT lv.id_local,
		   STUFF((SELECT '/' + RTRIM(CAST(CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END AS VARCHAR(10))) AS [text()]
				  --SELECT *
				  FROM   local_pista_vigente lpv (NOLOCK)
						 LEFT JOIN configuracao_equipamento_velocidade_limite cevl (NOLOCK)
							  ON  cevl.id_local = lpv.id_local
								  AND cevl.id_pista = lpv.id_pista
						 LEFT JOIN descricao_pista_gst dpg (NOLOCK)
							  ON  dpg.id_local = lpv.id_local
								  AND dpg.id_pista = lpv.id_pista
				  WHERE  lpv.id_local = lv.id_local
				  GROUP BY
						 CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END
				  ORDER BY
						 CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END
				  FOR XML PATH('')
		   ), 1, 1, '' ) AS velocidade_regulamentada,
		   vel.velocidade_min,
		   vel.data_min,
		   LEFT(vel.hora_min, 8) AS hora_min,
		   vel.velocidade_max,
		   vel.data_max,
		   LEFT(vel.hora_max, 8) AS hora_max,
		   vel.velocidade_85_percentil,
		   vel.velocidade_media
	FROM   local_vigente lv (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
						SELECT vp.id_local,
							   MIN(vp.velocidade_min) AS velocidade_min,
							   MIN(vp.data_min) AS data_min,
							   MIN(vp.hora_min) AS hora_min,
							   MAX(vp.velocidade_max) AS velocidade_max,
							   MAX(vp.data_max) AS data_max,
							   MAX(vp.hora_max) AS hora_max,
							   MAX(vp.velocidade_media) AS velocidade_media,
							   MAX(vp.velocidade_85) AS velocidade_85_percentil
						FROM   (
									--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
									SELECT vp.id_local,
										   MIN(vp.velocidade) AS velocidade_min,
										   MIN(CAST(vp.data AS DATE)) AS data_min,
										   MIN(CAST(vp.data AS TIME)) AS hora_min,
										   NULL AS velocidade_max,
										   NULL AS data_max,
										   NULL AS hora_max,
										   NULL AS velocidade_media,
										   NULL AS velocidade_85
									FROM   @temp_veiculo vp
									WHERE  vp.velocidade IN (
													--DECLARE @Id_Local INT = 18, @Data DATE = '2021-12-01'
													SELECT MIN(vp.velocidade) AS velocidade_min
													FROM   @temp_veiculo vp
										   )
									GROUP BY
										   vp.id_local
									UNION
									SELECT vp.id_local,
										   NULL velocidade_min,
										   NULL AS data_min,
										   NULL AS hora_min,
										   MAX(vp.velocidade) AS velocidade_max,
										   MAX(CAST(vp.data AS DATE)) AS data_max,
										   MAX(CAST(vp.data AS TIME)) AS hora_max,
										   NULL AS velocidade_media,
										   NULL AS velocidade_85
									FROM   @temp_veiculo vp
									WHERE  vp.velocidade IN (
													--DECLARE @Id_Local INT = 2, @Data DATE = '2018-06-01'
													SELECT MAX(vp.velocidade) AS velocidade_min
													FROM   @temp_veiculo vp
										   )
									GROUP BY
										   vp.id_local
									UNION
									SELECT vp.id_local,
										   NULL velocidade_min,
										   NULL AS data_min,
										   NULL AS hora_min,
										   NULL AS velocidade_max,
										   NULL AS data_max,
										   NULL AS hora_max,
										   CAST(ROUND(AVG(vp.velocidade), 0) AS INT) AS velocidade_media,
										   NULL AS velocidade_85
									FROM   @temp_veiculo vp
									GROUP BY
										   vp.id_local
									UNION
									--DECLARE @Id_Local INT = 2, @Data DATE = '2018-06-01'
									SELECT dados.id_local,
										   NULL AS velocidade_min,
										   NULL AS data_min,
										   NULL AS hora_min,
										   NULL AS velocidade_max,
										   NULL AS data_max,
										   NULL AS hora_max,
										   NULL AS velocidade_media,
										   MAX(dados.velocidade) AS velocidade_85
									FROM   (
												--DECLARE @Id_Local INT = 2, @Data DATE = '2018-06-01'
												SELECT ROW_NUMBER() OVER(ORDER BY vp.velocidade) AS ordem,
													   CAST(vp.velocidade AS INT) AS velocidade,
													   vp.id_local
												FROM   @temp_veiculo vp
										   ) AS dados
									WHERE  dados.ordem = (
															--DECLARE @Id_Local INT = 2, @Data DATE = '2018-06-01'
															SELECT CAST(( (COUNT(*)) * 0.85) AS INT) AS percentil_85
																  --,COUNT(*) AS total
															FROM   @temp_veiculo vp
														  )
									GROUP BY
										   dados.id_local
							   ) vp
						GROUP BY
							   vp.id_local
		   ) vel
				ON  vel.id_local = lv.id_local
	WHERE  lv.id_local = @Id_Local
