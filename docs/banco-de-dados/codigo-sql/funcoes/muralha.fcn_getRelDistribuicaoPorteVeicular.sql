CREATE FUNCTION [muralha].[fcn_getRelDistribuicaoPorteVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 
	--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-31 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
	SELECT r.hora,
		   r.hora_desc,
		   r.[moto_1],r.[pequeno_1],r.[medio_1],r.[grande_1],r.[total_1],
		   r.[moto_2],r.[pequeno_2],r.[medio_2],r.[grande_2],r.[total_2],
		   r.[moto_3],r.[pequeno_3],r.[medio_3],r.[grande_3],r.[total_3],
		   r.[moto_4],r.[pequeno_4],r.[medio_4],r.[grande_4],r.[total_4],
		   r.[moto_5],r.[pequeno_5],r.[medio_5],r.[grande_5],r.[total_5],
		   r.[moto_6],r.[pequeno_6],r.[medio_6],r.[grande_6],r.[total_6],
		   r.[moto_7],r.[pequeno_7],r.[medio_7],r.[grande_7],r.[total_7],
		   r.[moto_8],r.[pequeno_8],r.[medio_8],r.[grande_8],r.[total_8],
		   r.[moto_9],r.[pequeno_9],r.[medio_9],r.[grande_9],r.[total_9],
		   r.[moto_10],r.[pequeno_10],r.[medio_10],r.[grande_10],r.[total_10],
		   r.[moto_11],r.[pequeno_11],r.[medio_11],r.[grande_11],r.[total_11],
		   r.[moto_12],r.[pequeno_12],r.[medio_12],r.[grande_12],r.[total_12],
		   r.[moto_13],r.[pequeno_13],r.[medio_13],r.[grande_13],r.[total_13],
		   r.[moto_14],r.[pequeno_14],r.[medio_14],r.[grande_14],r.[total_14],
		   r.[moto_15],r.[pequeno_15],r.[medio_15],r.[grande_15],r.[total_15],
		   r.[moto_16],r.[pequeno_16],r.[medio_16],r.[grande_16],r.[total_16],
		   r.[moto_17],r.[pequeno_17],r.[medio_17],r.[grande_17],r.[total_17],
		   r.[moto_18],r.[pequeno_18],r.[medio_18],r.[grande_18],r.[total_18],
		   r.[moto_19],r.[pequeno_19],r.[medio_19],r.[grande_19],r.[total_19],
		   r.[moto_20],r.[pequeno_20],r.[medio_20],r.[grande_20],r.[total_20],
		   r.[moto_21],r.[pequeno_21],r.[medio_21],r.[grande_21],r.[total_21],
		   r.[moto_22],r.[pequeno_22],r.[medio_22],r.[grande_22],r.[total_22],
		   r.[moto_23],r.[pequeno_23],r.[medio_23],r.[grande_23],r.[total_23],
		   r.[moto_24],r.[pequeno_24],r.[medio_24],r.[grande_24],r.[total_24],
		   r.[moto_25],r.[pequeno_25],r.[medio_25],r.[grande_25],r.[total_25],
		   r.[moto_26],r.[pequeno_26],r.[medio_26],r.[grande_26],r.[total_26],
		   r.[moto_27],r.[pequeno_27],r.[medio_27],r.[grande_27],r.[total_27],
		   r.[moto_28],r.[pequeno_28],r.[medio_28],r.[grande_28],r.[total_28],
		   r.[moto_29],r.[pequeno_29],r.[medio_29],r.[grande_29],r.[total_29],
		   r.[moto_30],r.[pequeno_30],r.[medio_30],r.[grande_30],r.[total_30],
		   r.[moto_31],r.[pequeno_31],r.[medio_31],r.[grande_31],r.[total_31],
		   (SELECT SUM(c)
		   FROM (VALUES(r.[total_1]),(r.[total_2]),(r.[total_3]),(r.[total_4]),(r.[total_5]),(r.[total_6]),(r.[total_7]),(r.[total_8]),(r.[total_9]),
		   			(r.[total_10]),(r.[total_11]),(r.[total_12]),(r.[total_13]),(r.[total_14]),(r.[total_15]),(r.[total_16]),(r.[total_17]),
		   			(r.[total_18]),(r.[total_19]),(r.[total_20]),(r.[total_21]),(r.[total_22]),(r.[total_23]),(r.[total_24]),(r.[total_25]),
		   			(r.[total_26]),(r.[total_27]),(r.[total_28]),(r.[total_29]),(r.[total_30]),(r.[total_31])) T (c)) AS total_fluxo,
		   r.total_moto,
		   r.total_pequeno,
		   r.total_medio,
		   r.total_grande
	FROM   (
				SELECT horarios.hora,
					   horarios.hora_desc,
					   fluxo.[moto_1],fluxo.[pequeno_1],fluxo.[medio_1],fluxo.[grande_1],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_1]),(fluxo.[pequeno_1]),(fluxo.[medio_1]),(fluxo.[grande_1])) T (c)) AS [total_1],

					   fluxo.[moto_2],fluxo.[pequeno_2],fluxo.[medio_2],fluxo.[grande_2],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_2]),(fluxo.[pequeno_2]),(fluxo.[medio_2]),(fluxo.[grande_2])) T (c)) AS [total_2],

					   fluxo.[moto_3],fluxo.[pequeno_3],fluxo.[medio_3],fluxo.[grande_3],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_3]),(fluxo.[pequeno_3]),(fluxo.[medio_3]),(fluxo.[grande_3])) T (c)) AS [total_3],

					   fluxo.[moto_4],fluxo.[pequeno_4],fluxo.[medio_4],fluxo.[grande_4],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_4]),(fluxo.[pequeno_4]),(fluxo.[medio_4]),(fluxo.[grande_4])) T (c)) AS [total_4],

					   fluxo.[moto_5],fluxo.[pequeno_5],fluxo.[medio_5],fluxo.[grande_5],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_5]),(fluxo.[pequeno_5]),(fluxo.[medio_5]),(fluxo.[grande_5])) T (c)) AS [total_5],

					   fluxo.[moto_6],fluxo.[pequeno_6],fluxo.[medio_6],fluxo.[grande_6],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_6]),(fluxo.[pequeno_6]),(fluxo.[medio_6]),(fluxo.[grande_6])) T (c)) AS [total_6],

					   fluxo.[moto_7],fluxo.[pequeno_7],fluxo.[medio_7],fluxo.[grande_7],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_7]),(fluxo.[pequeno_7]),(fluxo.[medio_7]),(fluxo.[grande_7])) T (c)) AS [total_7],

					   fluxo.[moto_8],fluxo.[pequeno_8],fluxo.[medio_8],fluxo.[grande_8],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_8]),(fluxo.[pequeno_8]),(fluxo.[medio_8]),(fluxo.[grande_8])) T (c)) AS [total_8],

					   fluxo.[moto_9],fluxo.[pequeno_9],fluxo.[medio_9],fluxo.[grande_9],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_9]),(fluxo.[pequeno_9]),(fluxo.[medio_9]),(fluxo.[grande_9])) T (c)) AS [total_9],

					   fluxo.[moto_10],fluxo.[pequeno_10],fluxo.[medio_10],fluxo.[grande_10],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_10]),(fluxo.[pequeno_10]),(fluxo.[medio_10]),(fluxo.[grande_10])) T (c)) AS [total_10],

					   fluxo.[moto_11],fluxo.[pequeno_11],fluxo.[medio_11],fluxo.[grande_11],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_11]),(fluxo.[pequeno_11]),(fluxo.[medio_11]),(fluxo.[grande_11])) T (c)) AS [total_11],

					   fluxo.[moto_12],fluxo.[pequeno_12],fluxo.[medio_12],fluxo.[grande_12],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_12]),(fluxo.[pequeno_12]),(fluxo.[medio_12]),(fluxo.[grande_12])) T (c)) AS [total_12],

					   fluxo.[moto_13],fluxo.[pequeno_13],fluxo.[medio_13],fluxo.[grande_13],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_13]),(fluxo.[pequeno_13]),(fluxo.[medio_13]),(fluxo.[grande_13])) T (c)) AS [total_13],

					   fluxo.[moto_14],fluxo.[pequeno_14],fluxo.[medio_14],fluxo.[grande_14],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_14]),(fluxo.[pequeno_14]),(fluxo.[medio_14]),(fluxo.[grande_14])) T (c)) AS [total_14],

					   fluxo.[moto_15],fluxo.[pequeno_15],fluxo.[medio_15],fluxo.[grande_15],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_15]),(fluxo.[pequeno_15]),(fluxo.[medio_15]),(fluxo.[grande_15])) T (c)) AS [total_15],

					   fluxo.[moto_16],fluxo.[pequeno_16],fluxo.[medio_16],fluxo.[grande_16],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_16]),(fluxo.[pequeno_16]),(fluxo.[medio_16]),(fluxo.[grande_16])) T (c)) AS [total_16],

					   fluxo.[moto_17],fluxo.[pequeno_17],fluxo.[medio_17],fluxo.[grande_17],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_17]),(fluxo.[pequeno_17]),(fluxo.[medio_17]),(fluxo.[grande_17])) T (c)) AS [total_17],

					   fluxo.[moto_18],fluxo.[pequeno_18],fluxo.[medio_18],fluxo.[grande_18],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_18]),(fluxo.[pequeno_18]),(fluxo.[medio_18]),(fluxo.[grande_18])) T (c)) AS [total_18],

					   fluxo.[moto_19],fluxo.[pequeno_19],fluxo.[medio_19],fluxo.[grande_19],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_19]),(fluxo.[pequeno_19]),(fluxo.[medio_19]),(fluxo.[grande_19])) T (c)) AS [total_19],

					   fluxo.[moto_20],fluxo.[pequeno_20],fluxo.[medio_20],fluxo.[grande_20],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_20]),(fluxo.[pequeno_20]),(fluxo.[medio_20]),(fluxo.[grande_20])) T (c)) AS [total_20],

					   fluxo.[moto_21],fluxo.[pequeno_21],fluxo.[medio_21],fluxo.[grande_21],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_21]),(fluxo.[pequeno_21]),(fluxo.[medio_21]),(fluxo.[grande_21])) T (c)) AS [total_21],

					   fluxo.[moto_22],fluxo.[pequeno_22],fluxo.[medio_22],fluxo.[grande_22],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_22]),(fluxo.[pequeno_22]),(fluxo.[medio_22]),(fluxo.[grande_22])) T (c)) AS [total_22],

					   fluxo.[moto_23],fluxo.[pequeno_23],fluxo.[medio_23],fluxo.[grande_23],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_23]),(fluxo.[pequeno_23]),(fluxo.[medio_23]),(fluxo.[grande_23])) T (c)) AS [total_23],

					   fluxo.[moto_24],fluxo.[pequeno_24],fluxo.[medio_24],fluxo.[grande_24],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_24]),(fluxo.[pequeno_24]),(fluxo.[medio_24]),(fluxo.[grande_24])) T (c)) AS [total_24],

					   fluxo.[moto_25],fluxo.[pequeno_25],fluxo.[medio_25],fluxo.[grande_25],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_25]),(fluxo.[pequeno_25]),(fluxo.[medio_25]),(fluxo.[grande_25])) T (c)) AS [total_25],

					   fluxo.[moto_26],fluxo.[pequeno_26],fluxo.[medio_26],fluxo.[grande_26],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_26]),(fluxo.[pequeno_26]),(fluxo.[medio_26]),(fluxo.[grande_26])) T (c)) AS [total_26],

					   fluxo.[moto_27],fluxo.[pequeno_27],fluxo.[medio_27],fluxo.[grande_27],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_27]),(fluxo.[pequeno_27]),(fluxo.[medio_27]),(fluxo.[grande_27])) T (c)) AS [total_27],

					   fluxo.[moto_28],fluxo.[pequeno_28],fluxo.[medio_28],fluxo.[grande_28],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_28]),(fluxo.[pequeno_28]),(fluxo.[medio_28]),(fluxo.[grande_28])) T (c)) AS [total_28],

					   fluxo.[moto_29],fluxo.[pequeno_29],fluxo.[medio_29],fluxo.[grande_29],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_29]),(fluxo.[pequeno_29]),(fluxo.[medio_29]),(fluxo.[grande_29])) T (c)) AS [total_29],

					   fluxo.[moto_30],fluxo.[pequeno_30],fluxo.[medio_30],fluxo.[grande_30],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_30]),(fluxo.[pequeno_30]),(fluxo.[medio_30]),(fluxo.[grande_30])) T (c)) AS [total_30],

					   fluxo.[moto_31],fluxo.[pequeno_31],fluxo.[medio_31],fluxo.[grande_31],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_31]),(fluxo.[pequeno_31]),(fluxo.[medio_31]),(fluxo.[grande_31])) T (c)) AS [total_31],
		   
					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[moto_1]),(fluxo.[moto_2]),(fluxo.[moto_3]),(fluxo.[moto_4]),(fluxo.[moto_5]),(fluxo.[moto_6]),(fluxo.[moto_7]),(fluxo.[moto_8]),(fluxo.[moto_9]),
									(fluxo.[moto_10]),(fluxo.[moto_11]),(fluxo.[moto_12]),(fluxo.[moto_13]),(fluxo.[moto_14]),(fluxo.[moto_15]),(fluxo.[moto_16]),(fluxo.[moto_17]),
									(fluxo.[moto_18]),(fluxo.[moto_19]),(fluxo.[moto_20]),(fluxo.[moto_21]),(fluxo.[moto_22]),(fluxo.[moto_23]),(fluxo.[moto_24]),(fluxo.[moto_25]),
									(fluxo.[moto_26]),(fluxo.[moto_27]),(fluxo.[moto_28]),(fluxo.[moto_29]),(fluxo.[moto_30]),(fluxo.[moto_31])) T (c)) AS total_moto,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[pequeno_1]),(fluxo.[pequeno_2]),(fluxo.[pequeno_3]),(fluxo.[pequeno_4]),(fluxo.[pequeno_5]),(fluxo.[pequeno_6]),(fluxo.[pequeno_7]),(fluxo.[pequeno_8]),(fluxo.[pequeno_9]),
									(fluxo.[pequeno_10]),(fluxo.[pequeno_11]),(fluxo.[pequeno_12]),(fluxo.[pequeno_13]),(fluxo.[pequeno_14]),(fluxo.[pequeno_15]),(fluxo.[pequeno_16]),(fluxo.[pequeno_17]),
									(fluxo.[pequeno_18]),(fluxo.[pequeno_19]),(fluxo.[pequeno_20]),(fluxo.[pequeno_21]),(fluxo.[pequeno_22]),(fluxo.[pequeno_23]),(fluxo.[pequeno_24]),(fluxo.[pequeno_25]),
									(fluxo.[pequeno_26]),(fluxo.[pequeno_27]),(fluxo.[pequeno_28]),(fluxo.[pequeno_29]),(fluxo.[pequeno_30]),(fluxo.[pequeno_31])) T (c)) AS total_pequeno,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[medio_1]),(fluxo.[medio_2]),(fluxo.[medio_3]),(fluxo.[medio_4]),(fluxo.[medio_5]),(fluxo.[medio_6]),(fluxo.[medio_7]),(fluxo.[medio_8]),(fluxo.[medio_9]),
									(fluxo.[medio_10]),(fluxo.[medio_11]),(fluxo.[medio_12]),(fluxo.[medio_13]),(fluxo.[medio_14]),(fluxo.[medio_15]),(fluxo.[medio_16]),(fluxo.[medio_17]),
									(fluxo.[medio_18]),(fluxo.[medio_19]),(fluxo.[medio_20]),(fluxo.[medio_21]),(fluxo.[medio_22]),(fluxo.[medio_23]),(fluxo.[medio_24]),(fluxo.[medio_25]),
									(fluxo.[medio_26]),(fluxo.[medio_27]),(fluxo.[medio_28]),(fluxo.[medio_29]),(fluxo.[medio_30]),(fluxo.[medio_31])) T (c)) AS total_medio,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[grande_1]),(fluxo.[grande_2]),(fluxo.[grande_3]),(fluxo.[grande_4]),(fluxo.[grande_5]),(fluxo.[grande_6]),(fluxo.[grande_7]),(fluxo.[grande_8]),(fluxo.[grande_9]),
									(fluxo.[grande_10]),(fluxo.[grande_11]),(fluxo.[grande_12]),(fluxo.[grande_13]),(fluxo.[grande_14]),(fluxo.[grande_15]),(fluxo.[grande_16]),(fluxo.[grande_17]),
									(fluxo.[grande_18]),(fluxo.[grande_19]),(fluxo.[grande_20]),(fluxo.[grande_21]),(fluxo.[grande_22]),(fluxo.[grande_23]),(fluxo.[grande_24]),(fluxo.[grande_25]),
									(fluxo.[grande_26]),(fluxo.[grande_27]),(fluxo.[grande_28]),(fluxo.[grande_29]),(fluxo.[grande_30]),(fluxo.[grande_31])) T (c)) AS total_grande

				FROM   hora horarios (NOLOCK)
					   LEFT JOIN (
									--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-31 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
									SELECT *
									FROM   (
												--SELECT * FROM muralha.v_porte_veiculo_ref
												--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-01 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
												SELECT vs.hora,
													   vs.id_local,
													   CASE WHEN cv.id = 1 THEN 'moto_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 2 THEN 'pequeno_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 3 THEN 'medio_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 4 THEN 'grande_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
													   END AS dia,
													   SUM(vs.trafego) AS total
												FROM   veiculo_sumarizado vs (NOLOCK)
													   INNER JOIN muralha.v_porte_veiculo_ref cv (NOLOCK)
															ON  cv.id_classe = vs.id_classe
												WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
													   AND vs.id_local = @Id_Local
													   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
												GROUP BY
													   vs.hora,
													   vs.id_local,
													   CASE WHEN cv.id = 1 THEN 'moto_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 2 THEN 'pequeno_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 3 THEN 'medio_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id = 4 THEN 'grande_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
													   END
										   ) fv
									PIVOT (
											SUM(fv.total)
											FOR dia IN (
															[moto_1],[moto_2],[moto_3],[moto_4],[moto_5],[moto_6],[moto_7],[moto_8],[moto_9],[moto_10],[moto_11],[moto_12],[moto_13],[moto_14],[moto_15],[moto_16],[moto_17],[moto_18],[moto_19],[moto_20],[moto_21],[moto_22],
															[moto_23],[moto_24],[moto_25],[moto_26],[moto_27],[moto_28],[moto_29],[moto_30],[moto_31],

															[pequeno_1],[pequeno_2],[pequeno_3],[pequeno_4],[pequeno_5],[pequeno_6],[pequeno_7],[pequeno_8],[pequeno_9],[pequeno_10],[pequeno_11],[pequeno_12],[pequeno_13],[pequeno_14],[pequeno_15],[pequeno_16],[pequeno_17],
															[pequeno_18],[pequeno_19],[pequeno_20],[pequeno_21],[pequeno_22],[pequeno_23],[pequeno_24],[pequeno_25],[pequeno_26],[pequeno_27],[pequeno_28],[pequeno_29],[pequeno_30],[pequeno_31],

															[medio_1],[medio_2],[medio_3],[medio_4],[medio_5],[medio_6],[medio_7],[medio_8],[medio_9],[medio_10],[medio_11],[medio_12],[medio_13],[medio_14],[medio_15],[medio_16],[medio_17],[medio_18],[medio_19],[medio_20],
															[medio_21],[medio_22],[medio_23],[medio_24],[medio_25],[medio_26],[medio_27],[medio_28],[medio_29],[medio_30],[medio_31],

															[grande_1],[grande_2],[grande_3],[grande_4],[grande_5],[grande_6],[grande_7],[grande_8],[grande_9],[grande_10],[grande_11],[grande_12],[grande_13],[grande_14],[grande_15],[grande_16],[grande_17],[grande_18],
															[grande_19],[grande_20],[grande_21],[grande_22],[grande_23],[grande_24],[grande_25],[grande_26],[grande_27],[grande_28],[grande_29],[grande_30],[grande_31]
														)
										  ) contagem
					   ) AS fluxo
							ON  fluxo.hora = horarios.hora
		   ) r
)
