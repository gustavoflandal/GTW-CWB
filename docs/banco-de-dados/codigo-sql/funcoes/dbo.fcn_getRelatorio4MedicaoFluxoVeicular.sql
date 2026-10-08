CREATE FUNCTION [dbo].[fcn_getRelatorio4MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00', @Data_Fim DATETIME = '2018-06-30 23:59:59', @Id_Local INT = 2, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,
		   moto.[moto_1],pequeno.[pequeno_1],medio.[medio_1],grande.[grande_1],sem_id.[sem_id_1],fluxo.[total_1],
		   moto.[moto_2],pequeno.[pequeno_2],medio.[medio_2],grande.[grande_2],sem_id.[sem_id_2],fluxo.[total_2],
		   moto.[moto_3],pequeno.[pequeno_3],medio.[medio_3],grande.[grande_3],sem_id.[sem_id_3],fluxo.[total_3],
		   moto.[moto_4],pequeno.[pequeno_4],medio.[medio_4],grande.[grande_4],sem_id.[sem_id_4],fluxo.[total_4],
		   moto.[moto_5],pequeno.[pequeno_5],medio.[medio_5],grande.[grande_5],sem_id.[sem_id_5],fluxo.[total_5],
		   moto.[moto_6],pequeno.[pequeno_6],medio.[medio_6],grande.[grande_6],sem_id.[sem_id_6],fluxo.[total_6],
		   moto.[moto_7],pequeno.[pequeno_7],medio.[medio_7],grande.[grande_7],sem_id.[sem_id_7],fluxo.[total_7],
		   moto.[moto_8],pequeno.[pequeno_8],medio.[medio_8],grande.[grande_8],sem_id.[sem_id_8],fluxo.[total_8],
		   moto.[moto_9],pequeno.[pequeno_9],medio.[medio_9],grande.[grande_9],sem_id.[sem_id_9],fluxo.[total_9],
		   moto.[moto_10],pequeno.[pequeno_10],medio.[medio_10],grande.[grande_10],sem_id.[sem_id_10],fluxo.[total_10],
		   moto.[moto_11],pequeno.[pequeno_11],medio.[medio_11],grande.[grande_11],sem_id.[sem_id_11],fluxo.[total_11],
		   moto.[moto_12],pequeno.[pequeno_12],medio.[medio_12],grande.[grande_12],sem_id.[sem_id_12],fluxo.[total_12],
		   moto.[moto_13],pequeno.[pequeno_13],medio.[medio_13],grande.[grande_13],sem_id.[sem_id_13],fluxo.[total_13],
		   moto.[moto_14],pequeno.[pequeno_14],medio.[medio_14],grande.[grande_14],sem_id.[sem_id_14],fluxo.[total_14],
		   moto.[moto_15],pequeno.[pequeno_15],medio.[medio_15],grande.[grande_15],sem_id.[sem_id_15],fluxo.[total_15],
		   moto.[moto_16],pequeno.[pequeno_16],medio.[medio_16],grande.[grande_16],sem_id.[sem_id_16],fluxo.[total_16],
		   moto.[moto_17],pequeno.[pequeno_17],medio.[medio_17],grande.[grande_17],sem_id.[sem_id_17],fluxo.[total_17],
		   moto.[moto_18],pequeno.[pequeno_18],medio.[medio_18],grande.[grande_18],sem_id.[sem_id_18],fluxo.[total_18],
		   moto.[moto_19],pequeno.[pequeno_19],medio.[medio_19],grande.[grande_19],sem_id.[sem_id_19],fluxo.[total_19],
		   moto.[moto_20],pequeno.[pequeno_20],medio.[medio_20],grande.[grande_20],sem_id.[sem_id_20],fluxo.[total_20],
		   moto.[moto_21],pequeno.[pequeno_21],medio.[medio_21],grande.[grande_21],sem_id.[sem_id_21],fluxo.[total_21],
		   moto.[moto_22],pequeno.[pequeno_22],medio.[medio_22],grande.[grande_22],sem_id.[sem_id_22],fluxo.[total_22],
		   moto.[moto_23],pequeno.[pequeno_23],medio.[medio_23],grande.[grande_23],sem_id.[sem_id_23],fluxo.[total_23],
		   moto.[moto_24],pequeno.[pequeno_24],medio.[medio_24],grande.[grande_24],sem_id.[sem_id_24],fluxo.[total_24],
		   moto.[moto_25],pequeno.[pequeno_25],medio.[medio_25],grande.[grande_25],sem_id.[sem_id_25],fluxo.[total_25],
		   moto.[moto_26],pequeno.[pequeno_26],medio.[medio_26],grande.[grande_26],sem_id.[sem_id_26],fluxo.[total_26],
		   moto.[moto_27],pequeno.[pequeno_27],medio.[medio_27],grande.[grande_27],sem_id.[sem_id_27],fluxo.[total_27],
		   moto.[moto_28],pequeno.[pequeno_28],medio.[medio_28],grande.[grande_28],sem_id.[sem_id_28],fluxo.[total_28],
		   moto.[moto_29],pequeno.[pequeno_29],medio.[medio_29],grande.[grande_29],sem_id.[sem_id_29],fluxo.[total_29],
		   moto.[moto_30],pequeno.[pequeno_30],medio.[medio_30],grande.[grande_30],sem_id.[sem_id_30],fluxo.[total_30],
		   moto.[moto_31],pequeno.[pequeno_31],medio.[medio_31],grande.[grande_31],sem_id.[sem_id_31],fluxo.[total_31],
		   
		   ISNULL(totais.total_fluxo_veicular, 0) AS total_fluxo,
		   ISNULL(totais.total_moto, 0) AS total_moto,
		   ISNULL(totais.total_pequeno, 0) AS total_pequeno,
		   ISNULL(totais.total_medio, 0) AS total_medio,
		   ISNULL(totais.total_grande, 0) AS total_grande,
		   ISNULL(totais.total_sem_id, 0) AS total_sem_id
	FROM   hora horarios (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'total_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total
									FROM   veiculo_sumarizado vs (NOLOCK)
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'total_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv
						PIVOT (
								SUM(fv.total)
								FOR dia IN ([total_1],[total_2],[total_3],[total_4],[total_5],[total_6],[total_7],[total_8],[total_9],[total_10],[total_11],[total_12],[total_13],[total_14],[total_15],[total_16],[total_17],[total_18],[total_19],[total_20],[total_21],[total_22],[total_23],[total_24],[total_25],[total_26],[total_27],[total_28],[total_29],[total_30],[total_31])
							  ) contagem
		   ) AS fluxo
				ON  fluxo.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'moto_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total_moto
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND LTRIM(RTRIM(cv.id_classe)) IN ('M','B')
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'moto_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv_moto
						PIVOT (
								SUM(fv_moto.total_moto)
								FOR dia IN ([moto_1],[moto_2],[moto_3],[moto_4],[moto_5],[moto_6],[moto_7],[moto_8],[moto_9],[moto_10],[moto_11],[moto_12],[moto_13],[moto_14],[moto_15],[moto_16],[moto_17],[moto_18],[moto_19],[moto_20],[moto_21],[moto_22],[moto_23],[moto_24],[moto_25],[moto_26],[moto_27],[moto_28],[moto_29],[moto_30],[moto_31])
							  ) contagem_moto
		   ) AS moto
				ON  moto.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'pequeno_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total_pequeno
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND LTRIM(RTRIM(cv.id_classe)) IN ('P')
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'pequeno_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv_pequeno
						PIVOT (
								SUM(fv_pequeno.total_pequeno)
								FOR dia IN ([pequeno_1],[pequeno_2],[pequeno_3],[pequeno_4],[pequeno_5],[pequeno_6],[pequeno_7],[pequeno_8],[pequeno_9],[pequeno_10],[pequeno_11],[pequeno_12],[pequeno_13],[pequeno_14],[pequeno_15],[pequeno_16],[pequeno_17],[pequeno_18],[pequeno_19],[pequeno_20],[pequeno_21],[pequeno_22],[pequeno_23],[pequeno_24],[pequeno_25],[pequeno_26],[pequeno_27],[pequeno_28],[pequeno_29],[pequeno_30],[pequeno_31])
							  ) contagem_pequeno
		   ) AS pequeno
				ON  pequeno.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'medio_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total_medio
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND LTRIM(RTRIM(cv.id_classe)) IN ('T','F','V')
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'medio_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv_medio
						PIVOT (
								SUM(fv_medio.total_medio)
								FOR dia IN ([medio_1],[medio_2],[medio_3],[medio_4],[medio_5],[medio_6],[medio_7],[medio_8],[medio_9],[medio_10],[medio_11],[medio_12],[medio_13],[medio_14],[medio_15],[medio_16],[medio_17],[medio_18],[medio_19],[medio_20],[medio_21],[medio_22],[medio_23],[medio_24],[medio_25],[medio_26],[medio_27],[medio_28],[medio_29],[medio_30],[medio_31])
							  ) contagem_medio
		   ) AS medio
				ON  medio.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'grande_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total_grande
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND LTRIM(RTRIM(cv.id_classe)) IN ('C', 'O')
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'grande_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv_grande
						PIVOT (
								SUM(fv_grande.total_grande)
								FOR dia IN ([grande_1],[grande_2],[grande_3],[grande_4],[grande_5],[grande_6],[grande_7],[grande_8],[grande_9],[grande_10],[grande_11],[grande_12],[grande_13],[grande_14],[grande_15],[grande_16],[grande_17],[grande_18],[grande_19],[grande_20],[grande_21],[grande_22],[grande_23],[grande_24],[grande_25],[grande_26],[grande_27],[grande_28],[grande_29],[grande_30],[grande_31])
							  ) contagem_grande
		   ) AS grande
				ON  grande.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'sem_id_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2)))) AS dia,
										   SUM(vs.trafego) AS total_sem_id
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND LTRIM(RTRIM(cv.id_classe)) IN ('')
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
									GROUP BY
										   vs.hora,
										   vs.id_local,
										   'sem_id_' + LTRIM(RTRIM(CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))))
							   )fv_sem_id
						PIVOT (
								SUM(fv_sem_id.total_sem_id)
								FOR dia IN ([sem_id_1],[sem_id_2],[sem_id_3],[sem_id_4],[sem_id_5],[sem_id_6],[sem_id_7],[sem_id_8],[sem_id_9],[sem_id_10],[sem_id_11],[sem_id_12],[sem_id_13],[sem_id_14],[sem_id_15],[sem_id_16],[sem_id_17],[sem_id_18],[sem_id_19],[sem_id_20],[sem_id_21],[sem_id_22],[sem_id_23],[sem_id_24],[sem_id_25],[sem_id_26],[sem_id_27],[sem_id_28],[sem_id_29],[sem_id_30],[sem_id_31])
							  ) contagem_sem_id
		   ) AS sem_id
				ON  sem_id.hora = horarios.hora
	      LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   CASE WHEN LTRIM(RTRIM(cv.id_classe)) IN ('M') THEN 'total_moto'
												WHEN LTRIM(RTRIM(cv.id_classe)) IN ('P') THEN 'total_pequeno'
												WHEN LTRIM(RTRIM(cv.id_classe)) IN ('T') THEN 'total_medio'
												WHEN LTRIM(RTRIM(cv.id_classe)) IN ('C', 'O') THEN 'total_grande'
												WHEN LTRIM(RTRIM(cv.id_classe)) IN ('') THEN 'total_sem_id'
										   END AS classificacao,
										   vs.trafego
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END

									UNION ALL

									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vs.hora,
										   vs.id_local,
										   'total_fluxo_veicular' AS classificacao,
										   vs.trafego
									FROM   veiculo_sumarizado vs (NOLOCK)
										   INNER JOIN classe_veiculo cv (NOLOCK)
												ON  LTRIM(RTRIM(cv.id_classe)) = LTRIM(RTRIM(vs.id_classe))
									WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND vs.id_local = @Id_Local
										   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
							   )fv_totais
						PIVOT (
								SUM(fv_totais.trafego)
								FOR classificacao IN ([total_moto],[total_pequeno],[total_medio],[total_grande],[total_sem_id],[total_fluxo_veicular])
							  ) contagem_totais
		   ) AS totais
				ON  totais.hora = horarios.hora

)
