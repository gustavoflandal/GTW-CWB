CREATE FUNCTION [muralha].[fcn_getRelFluxoMensalPorClassificacao](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 
	--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-31 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
	SELECT r.hora,
		   r.hora_desc,
		   r.[moto_1],r.[passeio_1],r.[utilitario_1],r.[caminhao_1],r.[onibus_1],r.[total_1],
		   r.[moto_2],r.[passeio_2],r.[utilitario_2],r.[caminhao_2],r.[onibus_2],r.[total_2],
		   r.[moto_3],r.[passeio_3],r.[utilitario_3],r.[caminhao_3],r.[onibus_3],r.[total_3],
		   r.[moto_4],r.[passeio_4],r.[utilitario_4],r.[caminhao_4],r.[onibus_4],r.[total_4],
		   r.[moto_5],r.[passeio_5],r.[utilitario_5],r.[caminhao_5],r.[onibus_5],r.[total_5],
		   r.[moto_6],r.[passeio_6],r.[utilitario_6],r.[caminhao_6],r.[onibus_6],r.[total_6],
		   r.[moto_7],r.[passeio_7],r.[utilitario_7],r.[caminhao_7],r.[onibus_7],r.[total_7],
		   r.[moto_8],r.[passeio_8],r.[utilitario_8],r.[caminhao_8],r.[onibus_8],r.[total_8],
		   r.[moto_9],r.[passeio_9],r.[utilitario_9],r.[caminhao_9],r.[onibus_9],r.[total_9],
		   r.[moto_10],r.[passeio_10],r.[utilitario_10],r.[caminhao_10],r.[onibus_10],r.[total_10],
		   r.[moto_11],r.[passeio_11],r.[utilitario_11],r.[caminhao_11],r.[onibus_11],r.[total_11],
		   r.[moto_12],r.[passeio_12],r.[utilitario_12],r.[caminhao_12],r.[onibus_12],r.[total_12],
		   r.[moto_13],r.[passeio_13],r.[utilitario_13],r.[caminhao_13],r.[onibus_13],r.[total_13],
		   r.[moto_14],r.[passeio_14],r.[utilitario_14],r.[caminhao_14],r.[onibus_14],r.[total_14],
		   r.[moto_15],r.[passeio_15],r.[utilitario_15],r.[caminhao_15],r.[onibus_15],r.[total_15],
		   r.[moto_16],r.[passeio_16],r.[utilitario_16],r.[caminhao_16],r.[onibus_16],r.[total_16],
		   r.[moto_17],r.[passeio_17],r.[utilitario_17],r.[caminhao_17],r.[onibus_17],r.[total_17],
		   r.[moto_18],r.[passeio_18],r.[utilitario_18],r.[caminhao_18],r.[onibus_18],r.[total_18],
		   r.[moto_19],r.[passeio_19],r.[utilitario_19],r.[caminhao_19],r.[onibus_19],r.[total_19],
		   r.[moto_20],r.[passeio_20],r.[utilitario_20],r.[caminhao_20],r.[onibus_20],r.[total_20],
		   r.[moto_21],r.[passeio_21],r.[utilitario_21],r.[caminhao_21],r.[onibus_21],r.[total_21],
		   r.[moto_22],r.[passeio_22],r.[utilitario_22],r.[caminhao_22],r.[onibus_22],r.[total_22],
		   r.[moto_23],r.[passeio_23],r.[utilitario_23],r.[caminhao_23],r.[onibus_23],r.[total_23],
		   r.[moto_24],r.[passeio_24],r.[utilitario_24],r.[caminhao_24],r.[onibus_24],r.[total_24],
		   r.[moto_25],r.[passeio_25],r.[utilitario_25],r.[caminhao_25],r.[onibus_25],r.[total_25],
		   r.[moto_26],r.[passeio_26],r.[utilitario_26],r.[caminhao_26],r.[onibus_26],r.[total_26],
		   r.[moto_27],r.[passeio_27],r.[utilitario_27],r.[caminhao_27],r.[onibus_27],r.[total_27],
		   r.[moto_28],r.[passeio_28],r.[utilitario_28],r.[caminhao_28],r.[onibus_28],r.[total_28],
		   r.[moto_29],r.[passeio_29],r.[utilitario_29],r.[caminhao_29],r.[onibus_29],r.[total_29],
		   r.[moto_30],r.[passeio_30],r.[utilitario_30],r.[caminhao_30],r.[onibus_30],r.[total_30],
		   r.[moto_31],r.[passeio_31],r.[utilitario_31],r.[caminhao_31],r.[onibus_31],r.[total_31],
		   (SELECT SUM(c)
		   FROM (VALUES(r.[total_1]),(r.[total_2]),(r.[total_3]),(r.[total_4]),(r.[total_5]),(r.[total_6]),(r.[total_7]),(r.[total_8]),(r.[total_9]),
		   			(r.[total_10]),(r.[total_11]),(r.[total_12]),(r.[total_13]),(r.[total_14]),(r.[total_15]),(r.[total_16]),(r.[total_17]),
		   			(r.[total_18]),(r.[total_19]),(r.[total_20]),(r.[total_21]),(r.[total_22]),(r.[total_23]),(r.[total_24]),(r.[total_25]),
		   			(r.[total_26]),(r.[total_27]),(r.[total_28]),(r.[total_29]),(r.[total_30]),(r.[total_31])) T (c)) AS total_fluxo,
		   r.total_moto,
		   r.total_passeio,
		   r.total_utilitario,
		   r.total_caminhao,
		   r.total_onibus
	FROM   (
				SELECT horarios.hora,
					   horarios.hora_desc,
					   fluxo.[moto_1],fluxo.[passeio_1],fluxo.[utilitario_1],fluxo.[caminhao_1],fluxo.[onibus_1],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_1]),(fluxo.[passeio_1]),(fluxo.[utilitario_1]),(fluxo.[caminhao_1]),(fluxo.[onibus_1])) T (c)) AS [total_1],
					                          
					   fluxo.[moto_2],fluxo.[passeio_2],fluxo.[utilitario_2],fluxo.[caminhao_2],fluxo.[onibus_2],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_2]),(fluxo.[passeio_2]),(fluxo.[utilitario_2]),(fluxo.[caminhao_2]),(fluxo.[onibus_2])) T (c)) AS [total_2],
					   																										                  
					   fluxo.[moto_3],fluxo.[passeio_3],fluxo.[utilitario_3],fluxo.[caminhao_3], fluxo.[onibus_3], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_3]),(fluxo.[passeio_3]),(fluxo.[utilitario_3]),(fluxo.[caminhao_3]),(fluxo.[onibus_3])) T (c)) AS [total_3],
					   																										                  
					   fluxo.[moto_4],fluxo.[passeio_4],fluxo.[utilitario_4],fluxo.[caminhao_4], fluxo.[onibus_4], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_4]),(fluxo.[passeio_4]),(fluxo.[utilitario_4]),(fluxo.[caminhao_4]),(fluxo.[onibus_4])) T (c)) AS [total_4],
					   																										                  
					   fluxo.[moto_5],fluxo.[passeio_5],fluxo.[utilitario_5],fluxo.[caminhao_5], fluxo.[onibus_5], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_5]),(fluxo.[passeio_5]),(fluxo.[utilitario_5]),(fluxo.[caminhao_5]),(fluxo.[onibus_5])) T (c)) AS [total_5],
					   																										                  
					   fluxo.[moto_6],fluxo.[passeio_6],fluxo.[utilitario_6],fluxo.[caminhao_6], fluxo.[onibus_6], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_6]),(fluxo.[passeio_6]),(fluxo.[utilitario_6]),(fluxo.[caminhao_6]),(fluxo.[onibus_6])) T (c)) AS [total_6],
					   																										                  
					   fluxo.[moto_7],fluxo.[passeio_7],fluxo.[utilitario_7],fluxo.[caminhao_7], fluxo.[onibus_7], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_7]),(fluxo.[passeio_7]),(fluxo.[utilitario_7]),(fluxo.[caminhao_7]),(fluxo.[onibus_7])) T (c)) AS [total_7],
					   																										                  
					   fluxo.[moto_8],fluxo.[passeio_8],fluxo.[utilitario_8],fluxo.[caminhao_8], fluxo.[onibus_8], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_8]),(fluxo.[passeio_8]),(fluxo.[utilitario_8]),(fluxo.[caminhao_8]),(fluxo.[onibus_8])) T (c)) AS [total_8],
					   																										                  
					   fluxo.[moto_9],fluxo.[passeio_9],fluxo.[utilitario_9],fluxo.[caminhao_9], fluxo.[onibus_9], 
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_9]),(fluxo.[passeio_9]),(fluxo.[utilitario_9]),(fluxo.[caminhao_9]),(fluxo.[onibus_9])) T (c)) AS [total_9],
					   
					   fluxo.[moto_10],fluxo.[passeio_10],fluxo.[utilitario_10],fluxo.[caminhao_10],fluxo.[onibus_10],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_10]),(fluxo.[passeio_10]),(fluxo.[utilitario_10]),(fluxo.[caminhao_10]),(fluxo.[onibus_10])) T (c)) AS [total_10],
					                              
					   fluxo.[moto_11],fluxo.[passeio_11],fluxo.[utilitario_11],fluxo.[caminhao_11],fluxo.[onibus_11],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_11]),(fluxo.[passeio_11]),(fluxo.[utilitario_11]),(fluxo.[caminhao_11]),(fluxo.[onibus_11])) T (c)) AS [total_11],
					                              
					   fluxo.[moto_12],fluxo.[passeio_12],fluxo.[utilitario_12],fluxo.[caminhao_12],fluxo.[onibus_12],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_12]),(fluxo.[passeio_12]),(fluxo.[utilitario_12]),(fluxo.[caminhao_12]),(fluxo.[onibus_12])) T (c)) AS [total_12],
					                              
					   fluxo.[moto_13],fluxo.[passeio_13],fluxo.[utilitario_13],fluxo.[caminhao_13],fluxo.[onibus_13],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_13]),(fluxo.[passeio_13]),(fluxo.[utilitario_13]),(fluxo.[caminhao_13]),(fluxo.[onibus_13])) T (c)) AS [total_13],
					                              
					   fluxo.[moto_14],fluxo.[passeio_14],fluxo.[utilitario_14],fluxo.[caminhao_14],fluxo.[onibus_14],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_14]),(fluxo.[passeio_14]),(fluxo.[utilitario_14]),(fluxo.[caminhao_14]),(fluxo.[onibus_14])) T (c)) AS [total_14],
					                              
					   fluxo.[moto_15],fluxo.[passeio_15],fluxo.[utilitario_15],fluxo.[caminhao_15],fluxo.[onibus_15],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_15]),(fluxo.[passeio_15]),(fluxo.[utilitario_15]),(fluxo.[caminhao_15]),(fluxo.[onibus_15])) T (c)) AS [total_15],
					                              
					   fluxo.[moto_16],fluxo.[passeio_16],fluxo.[utilitario_16],fluxo.[caminhao_16],fluxo.[onibus_16],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_16]),(fluxo.[passeio_16]),(fluxo.[utilitario_16]),(fluxo.[caminhao_16]),(fluxo.[onibus_16])) T (c)) AS [total_16],
					                              
					   fluxo.[moto_17],fluxo.[passeio_17],fluxo.[utilitario_17],fluxo.[caminhao_17],fluxo.[onibus_17],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_17]),(fluxo.[passeio_17]),(fluxo.[utilitario_17]),(fluxo.[caminhao_17]),(fluxo.[onibus_17])) T (c)) AS [total_17],
					                              
					   fluxo.[moto_18],fluxo.[passeio_18],fluxo.[utilitario_18],fluxo.[caminhao_18],fluxo.[onibus_18],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_18]),(fluxo.[passeio_18]),(fluxo.[utilitario_18]),(fluxo.[caminhao_18]),(fluxo.[onibus_18])) T (c)) AS [total_18],
					                              
					   fluxo.[moto_19],fluxo.[passeio_19],fluxo.[utilitario_19],fluxo.[caminhao_19],fluxo.[onibus_19],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_19]),(fluxo.[passeio_19]),(fluxo.[utilitario_19]),(fluxo.[caminhao_19]),(fluxo.[onibus_19])) T (c)) AS [total_19],
					                              
					   fluxo.[moto_20],fluxo.[passeio_20],fluxo.[utilitario_20],fluxo.[caminhao_20],fluxo.[onibus_20],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_20]),(fluxo.[passeio_20]),(fluxo.[utilitario_20]),(fluxo.[caminhao_20]),(fluxo.[onibus_20])) T (c)) AS [total_20],
					                              
					   fluxo.[moto_21],fluxo.[passeio_21],fluxo.[utilitario_21],fluxo.[caminhao_21],fluxo.[onibus_21],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_21]),(fluxo.[passeio_21]),(fluxo.[utilitario_21]),(fluxo.[caminhao_21]),(fluxo.[onibus_21])) T (c)) AS [total_21],
					                              
					   fluxo.[moto_22],fluxo.[passeio_22],fluxo.[utilitario_22],fluxo.[caminhao_22],fluxo.[onibus_22],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_22]),(fluxo.[passeio_22]),(fluxo.[utilitario_22]),(fluxo.[caminhao_22]),(fluxo.[onibus_22])) T (c)) AS [total_22],
					                              
					   fluxo.[moto_23],fluxo.[passeio_23],fluxo.[utilitario_23],fluxo.[caminhao_23],fluxo.[onibus_23],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_23]),(fluxo.[passeio_23]),(fluxo.[utilitario_23]),(fluxo.[caminhao_23]),(fluxo.[onibus_23])) T (c)) AS [total_23],
					                              
					   fluxo.[moto_24],fluxo.[passeio_24],fluxo.[utilitario_24],fluxo.[caminhao_24],fluxo.[onibus_24],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_24]),(fluxo.[passeio_24]),(fluxo.[utilitario_24]),(fluxo.[caminhao_24]),(fluxo.[onibus_24])) T (c)) AS [total_24],
					                              
					   fluxo.[moto_25],fluxo.[passeio_25],fluxo.[utilitario_25],fluxo.[caminhao_25],fluxo.[onibus_25],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_25]),(fluxo.[passeio_25]),(fluxo.[utilitario_25]),(fluxo.[caminhao_25]),(fluxo.[onibus_25])) T (c)) AS [total_25],
					                              
					   fluxo.[moto_26],fluxo.[passeio_26],fluxo.[utilitario_26],fluxo.[caminhao_26],fluxo.[onibus_26],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_26]),(fluxo.[passeio_26]),(fluxo.[utilitario_26]),(fluxo.[caminhao_26]),(fluxo.[onibus_26])) T (c)) AS [total_26],
					                              
					   fluxo.[moto_27],fluxo.[passeio_27],fluxo.[utilitario_27],fluxo.[caminhao_27],fluxo.[onibus_27],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_27]),(fluxo.[passeio_27]),(fluxo.[utilitario_27]),(fluxo.[caminhao_27]),(fluxo.[onibus_27])) T (c)) AS [total_27],
					                              
					   fluxo.[moto_28],fluxo.[passeio_28],fluxo.[utilitario_28],fluxo.[caminhao_28],fluxo.[onibus_28],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_28]),(fluxo.[passeio_28]),(fluxo.[utilitario_28]),(fluxo.[caminhao_28]),(fluxo.[onibus_28])) T (c)) AS [total_28],
					                              
					   fluxo.[moto_29],fluxo.[passeio_29],fluxo.[utilitario_29],fluxo.[caminhao_29],fluxo.[onibus_29],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_29]),(fluxo.[passeio_29]),(fluxo.[utilitario_29]),(fluxo.[caminhao_29]),(fluxo.[onibus_29])) T (c)) AS [total_29],
					                              
					   fluxo.[moto_30],fluxo.[passeio_30],fluxo.[utilitario_30],fluxo.[caminhao_30],fluxo.[onibus_30],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_30]),(fluxo.[passeio_30]),(fluxo.[utilitario_30]),(fluxo.[caminhao_30]),(fluxo.[onibus_30])) T (c)) AS [total_30],
					                              
					   fluxo.[moto_31],fluxo.[passeio_31],fluxo.[utilitario_31],fluxo.[caminhao_31],fluxo.[onibus_31],
					   (SELECT SUM(c) FROM (VALUES(fluxo.[moto_31]),(fluxo.[passeio_31]),(fluxo.[utilitario_31]),(fluxo.[caminhao_31]),(fluxo.[onibus_31])) T (c)) AS [total_31],
					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[moto_1]),(fluxo.[moto_2]),(fluxo.[moto_3]),(fluxo.[moto_4]),(fluxo.[moto_5]),(fluxo.[moto_6]),(fluxo.[moto_7]),(fluxo.[moto_8]),(fluxo.[moto_9]),
									(fluxo.[moto_10]),(fluxo.[moto_11]),(fluxo.[moto_12]),(fluxo.[moto_13]),(fluxo.[moto_14]),(fluxo.[moto_15]),(fluxo.[moto_16]),(fluxo.[moto_17]),
									(fluxo.[moto_18]),(fluxo.[moto_19]),(fluxo.[moto_20]),(fluxo.[moto_21]),(fluxo.[moto_22]),(fluxo.[moto_23]),(fluxo.[moto_24]),(fluxo.[moto_25]),
									(fluxo.[moto_26]),(fluxo.[moto_27]),(fluxo.[moto_28]),(fluxo.[moto_29]),(fluxo.[moto_30]),(fluxo.[moto_31])) T (c)) AS total_moto,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[passeio_1]),(fluxo.[passeio_2]),(fluxo.[passeio_3]),(fluxo.[passeio_4]),(fluxo.[passeio_5]),(fluxo.[passeio_6]),(fluxo.[passeio_7]),(fluxo.[passeio_8]),(fluxo.[passeio_9]),
									(fluxo.[passeio_10]),(fluxo.[passeio_11]),(fluxo.[passeio_12]),(fluxo.[passeio_13]),(fluxo.[passeio_14]),(fluxo.[passeio_15]),(fluxo.[passeio_16]),(fluxo.[passeio_17]),
									(fluxo.[passeio_18]),(fluxo.[passeio_19]),(fluxo.[passeio_20]),(fluxo.[passeio_21]),(fluxo.[passeio_22]),(fluxo.[passeio_23]),(fluxo.[passeio_24]),(fluxo.[passeio_25]),
									(fluxo.[passeio_26]),(fluxo.[passeio_27]),(fluxo.[passeio_28]),(fluxo.[passeio_29]),(fluxo.[passeio_30]),(fluxo.[passeio_31])) T (c)) AS total_passeio,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[utilitario_1]),(fluxo.[utilitario_2]),(fluxo.[utilitario_3]),(fluxo.[utilitario_4]),(fluxo.[utilitario_5]),(fluxo.[utilitario_6]),(fluxo.[utilitario_7]),(fluxo.[utilitario_8]),(fluxo.[utilitario_9]),
									(fluxo.[utilitario_10]),(fluxo.[utilitario_11]),(fluxo.[utilitario_12]),(fluxo.[utilitario_13]),(fluxo.[utilitario_14]),(fluxo.[utilitario_15]),(fluxo.[utilitario_16]),(fluxo.[utilitario_17]),
									(fluxo.[utilitario_18]),(fluxo.[utilitario_19]),(fluxo.[utilitario_20]),(fluxo.[utilitario_21]),(fluxo.[utilitario_22]),(fluxo.[utilitario_23]),(fluxo.[utilitario_24]),(fluxo.[utilitario_25]),
									(fluxo.[utilitario_26]),(fluxo.[utilitario_27]),(fluxo.[utilitario_28]),(fluxo.[utilitario_29]),(fluxo.[utilitario_30]),(fluxo.[utilitario_31])) T (c)) AS total_utilitario,

					   (SELECT SUM(c)
						FROM (VALUES(fluxo.[caminhao_1]),(fluxo.[caminhao_2]),(fluxo.[caminhao_3]),(fluxo.[caminhao_4]),(fluxo.[caminhao_5]),(fluxo.[caminhao_6]),(fluxo.[caminhao_7]),(fluxo.[caminhao_8]),(fluxo.[caminhao_9]),
									(fluxo.[caminhao_10]),(fluxo.[caminhao_11]),(fluxo.[caminhao_12]),(fluxo.[caminhao_13]),(fluxo.[caminhao_14]),(fluxo.[caminhao_15]),(fluxo.[caminhao_16]),(fluxo.[caminhao_17]),
									(fluxo.[caminhao_18]),(fluxo.[caminhao_19]),(fluxo.[caminhao_20]),(fluxo.[caminhao_21]),(fluxo.[caminhao_22]),(fluxo.[caminhao_23]),(fluxo.[caminhao_24]),(fluxo.[caminhao_25]),
									(fluxo.[caminhao_26]),(fluxo.[caminhao_27]),(fluxo.[caminhao_28]),(fluxo.[caminhao_29]),(fluxo.[caminhao_30]),(fluxo.[caminhao_31])) T (c)) AS total_caminhao,
						
						(SELECT SUM(c)
						FROM (VALUES(fluxo.[onibus_1]),(fluxo.[onibus_2]),(fluxo.[onibus_3]),(fluxo.[onibus_4]),(fluxo.[onibus_5]),(fluxo.[onibus_6]),(fluxo.[onibus_7]),(fluxo.[onibus_8]),(fluxo.[onibus_9]),
									(fluxo.[onibus_10]),(fluxo.[onibus_11]),(fluxo.[onibus_12]),(fluxo.[onibus_13]),(fluxo.[onibus_14]),(fluxo.[onibus_15]),(fluxo.[onibus_16]),(fluxo.[onibus_17]),
									(fluxo.[onibus_18]),(fluxo.[onibus_19]),(fluxo.[onibus_20]),(fluxo.[onibus_21]),(fluxo.[onibus_22]),(fluxo.[onibus_23]),(fluxo.[onibus_24]),(fluxo.[onibus_25]),
									(fluxo.[onibus_26]),(fluxo.[onibus_27]),(fluxo.[onibus_28]),(fluxo.[onibus_29]),(fluxo.[onibus_30]),(fluxo.[onibus_31])) T (c)) AS total_onibus

				FROM   hora horarios (NOLOCK)
					   LEFT JOIN (
									--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-31 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
									SELECT *
									FROM   (
												--SELECT * FROM muralha.v_porte_veiculo_ref
												--DECLARE @Data_Ini DATETIME = '2021-12-01 00:00:00', @Data_Fim DATETIME = '2021-12-01 23:59:59', @Id_Local INT = 18, @Id_Pista INT = NULL
												SELECT vs.hora,
													   vs.id_local,
													   CASE WHEN cv.id_classe = 'M' THEN 'moto_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe IN ('P','') THEN 'passeio_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'T' THEN 'utilitario_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'C' THEN 'caminhao_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'O' THEN 'onibus_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
													   END AS dia,
													   SUM(vs.trafego) AS total
												FROM   veiculo_sumarizado vs (NOLOCK)
													   INNER JOIN dbo.classe_veiculo cv (NOLOCK) --muralha.v_porte_veiculo_ref cv (NOLOCK)
															ON  cv.id_classe = vs.id_classe
												WHERE  vs.data BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
													   AND vs.id_local = @Id_Local
													   AND vs.pista = CASE WHEN @Id_Pista IS NULL THEN vs.pista ELSE @Id_Pista END
												GROUP BY
													   vs.hora,
													   vs.id_local,
													   CASE WHEN cv.id_classe = 'M' THEN 'moto_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe IN ('P','') THEN 'passeio_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'T' THEN 'utilitario_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'C' THEN 'caminhao_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
															WHEN cv.id_classe = 'O' THEN 'onibus_' + CAST(DATEPART(DAY, vs.data) AS VARCHAR(2))
													   END
										   ) fv
									PIVOT (
											SUM(fv.total)
											FOR dia IN (
															[moto_1],[moto_2],[moto_3],[moto_4],[moto_5],[moto_6],[moto_7],[moto_8],[moto_9],[moto_10],[moto_11],[moto_12],[moto_13],[moto_14],[moto_15],[moto_16],[moto_17],[moto_18],[moto_19],[moto_20],[moto_21],[moto_22],
															[moto_23],[moto_24],[moto_25],[moto_26],[moto_27],[moto_28],[moto_29],[moto_30],[moto_31],

															[passeio_1],[passeio_2],[passeio_3],[passeio_4],[passeio_5],[passeio_6],[passeio_7],[passeio_8],[passeio_9],[passeio_10],[passeio_11],[passeio_12],[passeio_13],[passeio_14],[passeio_15],[passeio_16],[passeio_17],
															[passeio_18],[passeio_19],[passeio_20],[passeio_21],[passeio_22],[passeio_23],[passeio_24],[passeio_25],[passeio_26],[passeio_27],[passeio_28],[passeio_29],[passeio_30],[passeio_31],

															[utilitario_1],[utilitario_2],[utilitario_3],[utilitario_4],[utilitario_5],[utilitario_6],[utilitario_7],[utilitario_8],[utilitario_9],[utilitario_10],[utilitario_11],[utilitario_12],[utilitario_13],[utilitario_14],[utilitario_15],[utilitario_16],[utilitario_17],[utilitario_18],[utilitario_19],[utilitario_20],
															[utilitario_21],[utilitario_22],[utilitario_23],[utilitario_24],[utilitario_25],[utilitario_26],[utilitario_27],[utilitario_28],[utilitario_29],[utilitario_30],[utilitario_31],

															[caminhao_1],[caminhao_2],[caminhao_3],[caminhao_4],[caminhao_5],[caminhao_6],[caminhao_7],[caminhao_8],[caminhao_9],[caminhao_10],[caminhao_11],[caminhao_12],[caminhao_13],[caminhao_14],[caminhao_15],[caminhao_16],[caminhao_17],[caminhao_18],
															[caminhao_19],[caminhao_20],[caminhao_21],[caminhao_22],[caminhao_23],[caminhao_24],[caminhao_25],[caminhao_26],[caminhao_27],[caminhao_28],[caminhao_29],[caminhao_30],[caminhao_31],

															[onibus_1],[onibus_2],[onibus_3],[onibus_4],[onibus_5],[onibus_6],[onibus_7],[onibus_8],[onibus_9],[onibus_10],[onibus_11],[onibus_12],[onibus_13],[onibus_14],[onibus_15],[onibus_16],[onibus_17],[onibus_18],
															[onibus_19],[onibus_20],[onibus_21],[onibus_22],[onibus_23],[onibus_24],[onibus_25],[onibus_26],[onibus_27],[onibus_28],[onibus_29],[onibus_30],[onibus_31]

														)
										  ) contagem
					   ) AS fluxo
							ON  fluxo.hora = horarios.hora
		   ) r
	)
