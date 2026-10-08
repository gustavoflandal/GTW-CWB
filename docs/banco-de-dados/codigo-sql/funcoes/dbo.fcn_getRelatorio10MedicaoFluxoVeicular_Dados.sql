
CREATE FUNCTION [dbo].[fcn_getRelatorio10MedicaoFluxoVeicular_Dados](@Data_Ini DATETIME, @Data_Fim DATETIME)  
RETURNS TABLE  
AS  
RETURN  
(   
  
 --DECLARE @Data_Ini DATETIME = '2020-09-01 00:00:00.000', @Data_Fim DATETIME = '2020-09-30 23:59:59.000'  
 SELECT dados_local.id_local,
		dados_local.serie_equipamento,
		dados_local.nome_pista_sentido,
		dados_local.id_pista,
		dados_local.faixa,
		dados_local.nome_pista_sentido_faixa,
		dados_local.posicao_lat AS latitude,
		dados_local.posicao_lon AS longitude,
		dados_local.codigo_equipamento,

		fluxo.[1] AS [fv_1],fluxo.[2] AS [fv_2],fluxo.[3] AS [fv_3],fluxo.[4] AS [fv_4],fluxo.[5] AS [fv_5],fluxo.[6] AS [fv_6],fluxo.[7] AS [fv_7],
		fluxo.[8] AS [fv_8],fluxo.[9] AS [fv_9],fluxo.[10] AS [fv_10],fluxo.[11] AS [fv_11],fluxo.[12] AS [fv_12],fluxo.[13] AS [fv_13],fluxo.[14] AS [fv_14],
		fluxo.[15] AS [fv_15],fluxo.[16] AS [fv_16],fluxo.[17] AS [fv_17],fluxo.[18] AS [fv_18],fluxo.[19] AS [fv_19],fluxo.[20] AS [fv_20],
		fluxo.[21] AS [fv_21],fluxo.[22] AS [fv_22],fluxo.[23] AS [fv_23],fluxo.[24] AS [fv_24],fluxo.[25] AS [fv_25],fluxo.[26] AS [fv_26],
		fluxo.[27] AS [fv_27],fluxo.[28] AS [fv_28],fluxo.[29] AS [fv_29],fluxo.[30] AS [fv_30],fluxo.[31] AS [fv_31],
       
		vel_media.[1] AS [vm_1],vel_media.[2] AS [vm_2],vel_media.[3] AS [vm_3],vel_media.[4] AS [vm_4],vel_media.[5] AS [vm_5],vel_media.[6] AS [vm_6],
		vel_media.[7] AS [vm_7],vel_media.[8] AS [vm_8],vel_media.[9] AS [vm_9],vel_media.[10] AS [vm_10],vel_media.[11] AS [vm_11],vel_media.[12] AS [vm_12],
		vel_media.[13] AS [vm_13],vel_media.[14] AS [vm_14],vel_media.[15] AS [vm_15],vel_media.[16] AS [vm_16],vel_media.[17] AS [vm_17],
		vel_media.[18] AS [vm_18],vel_media.[19] AS [vm_19],vel_media.[20] AS [vm_20],vel_media.[21] AS [vm_21],vel_media.[22] AS [vm_22],
		vel_media.[23] AS [vm_23],vel_media.[24] AS [vm_24],vel_media.[25] AS [vm_25],vel_media.[26] AS [vm_26],vel_media.[27] AS [vm_27],
		vel_media.[28] AS [vm_28],vel_media.[29] AS [vm_29],vel_media.[30] AS [vm_30],vel_media.[31] AS [vm_31],
       
		CAST(vel_maxima.[1] AS INT) AS [vmax_1],CAST(vel_maxima.[2] AS INT) AS [vmax_2],CAST(vel_maxima.[3] AS INT) AS [vmax_3],CAST(vel_maxima.[4] AS INT) AS [vmax_4],
		CAST(vel_maxima.[5] AS INT) AS [vmax_5],CAST(vel_maxima.[6] AS INT) AS [vmax_6],CAST(vel_maxima.[7] AS INT) AS [vmax_7],CAST(vel_maxima.[8] AS INT) AS [vmax_8],
		CAST(vel_maxima.[9] AS INT) AS [vmax_9],CAST(vel_maxima.[10] AS INT) AS [vmax_10],CAST(vel_maxima.[11] AS INT) AS [vmax_11],CAST(vel_maxima.[12] AS INT) AS [vmax_12],
		CAST(vel_maxima.[13] AS INT) AS [vmax_13],CAST(vel_maxima.[14] AS INT) AS [vmax_14],CAST(vel_maxima.[15] AS INT) AS [vmax_15],CAST(vel_maxima.[16] AS INT) AS [vmax_16],
		CAST(vel_maxima.[17] AS INT) AS [vmax_17],CAST(vel_maxima.[18] AS INT) AS [vmax_18],CAST(vel_maxima.[19] AS INT) AS [vmax_19],CAST(vel_maxima.[20] AS INT) AS [vmax_20],
		CAST(vel_maxima.[21] AS INT) AS [vmax_21],CAST(vel_maxima.[22] AS INT) AS [vmax_22],CAST(vel_maxima.[23] AS INT) AS [vmax_23],CAST(vel_maxima.[24] AS INT) AS [vmax_24],
		CAST(vel_maxima.[25] AS INT) AS [vmax_25],CAST(vel_maxima.[26] AS INT) AS [vmax_26],CAST(vel_maxima.[27] AS INT) AS [vmax_27],CAST(vel_maxima.[28] AS INT) AS [vmax_28],
		CAST(vel_maxima.[29] AS INT) AS [vmax_29],CAST(vel_maxima.[30] AS INT) AS [vmax_30],CAST(vel_maxima.[31] AS INT) AS [vmax_31],
  
		autos_detectados.[1] AS [ad_1],autos_detectados.[2] AS [ad_2],autos_detectados.[3] AS [ad_3],autos_detectados.[4] AS [ad_4],
		autos_detectados.[5] AS [ad_5],autos_detectados.[6] AS [ad_6],autos_detectados.[7] AS [ad_7],autos_detectados.[8] AS [ad_8],
		autos_detectados.[9] AS [ad_9],autos_detectados.[10] AS [ad_10],autos_detectados.[11] AS [ad_11],autos_detectados.[12] AS [ad_12],
		autos_detectados.[13] AS [ad_13],autos_detectados.[14] AS [ad_14],autos_detectados.[15] AS [ad_15],autos_detectados.[16] AS [ad_16],
		autos_detectados.[17] AS [ad_17],autos_detectados.[18] AS [ad_18],autos_detectados.[19] AS [ad_19],autos_detectados.[20] AS [ad_20],
		autos_detectados.[21] AS [ad_21],autos_detectados.[22] AS [ad_22],autos_detectados.[23] AS [ad_23],autos_detectados.[24] AS [ad_24],
		autos_detectados.[25] AS [ad_25],autos_detectados.[26] AS [ad_26],autos_detectados.[27] AS [ad_27],autos_detectados.[28] AS [ad_28],
		autos_detectados.[29] AS [ad_29],autos_detectados.[30] AS [ad_30],autos_detectados.[31] AS [ad_31],
  
		autos_validos.[1] AS [av_1],autos_validos.[2] AS [av_2],autos_validos.[3] AS [av_3],autos_validos.[4] AS [av_4],autos_validos.[5] AS [av_5],
		autos_validos.[6] AS [av_6],autos_validos.[7] AS [av_7],autos_validos.[8] AS [av_8],autos_validos.[9] AS [av_9],autos_validos.[10] AS [av_10],
		autos_validos.[11] AS [av_11],autos_validos.[12] AS [av_12],autos_validos.[13] AS [av_13],autos_validos.[14] AS [av_14],autos_validos.[15] AS [av_15],
		autos_validos.[16] AS [av_16],autos_validos.[17] AS [av_17],autos_validos.[18] AS [av_18],autos_validos.[19] AS [av_19],autos_validos.[20] AS [av_20],
		autos_validos.[21] AS [av_21],autos_validos.[22] AS [av_22],autos_validos.[23] AS [av_23],autos_validos.[24] AS [av_24],autos_validos.[25] AS [av_25],
		autos_validos.[26] AS [av_26],autos_validos.[27] AS [av_27],autos_validos.[28] AS [av_28],autos_validos.[29] AS [av_29],autos_validos.[30] AS [av_30],
		autos_validos.[31] AS [av_31],
  
		(SELECT SUM(c)
		 FROM (VALUES(fluxo.[1]),(fluxo.[2]),(fluxo.[3]),(fluxo.[4]),(fluxo.[5]),(fluxo.[6]),(fluxo.[7]),(fluxo.[8]),(fluxo.[9]),(fluxo.[10]),(fluxo.[11]),
					 (fluxo.[12]),(fluxo.[13]),(fluxo.[14]),(fluxo.[15]),(fluxo.[16]),(fluxo.[17]),(fluxo.[18]),(fluxo.[19]),(fluxo.[20]),(fluxo.[21]),
					 (fluxo.[22]),(fluxo.[23]),(fluxo.[24]),(fluxo.[25]),(fluxo.[26]),(fluxo.[27]),(fluxo.[28]),(fluxo.[29]),(fluxo.[30]),(fluxo.[31])) T (c)) AS total_fluxo,
     
		(SELECT CAST(ROUND(AVG(c), 0) AS INT)
		 FROM (VALUES(vel_media.[1]),(vel_media.[2]),(vel_media.[3]),(vel_media.[4]),(vel_media.[5]),(vel_media.[6]),(vel_media.[7]),(vel_media.[8]),(vel_media.[9]),
					 (vel_media.[10]),(vel_media.[11]),(vel_media.[12]),(vel_media.[13]),(vel_media.[14]),(vel_media.[15]),(vel_media.[16]),(vel_media.[17]),(vel_media.[18]),
					 (vel_media.[19]),(vel_media.[20]),(vel_media.[21]),(vel_media.[22]),(vel_media.[23]),(vel_media.[24]),(vel_media.[25]),(vel_media.[26]),(vel_media.[27]),
				     (vel_media.[28]),(vel_media.[29]),(vel_media.[30]),(vel_media.[31])) T (c)) AS total_velocidade_media,

		(SELECT CAST(MAX(c) AS INT)
		 FROM (VALUES(vel_maxima.[1]),(vel_maxima.[2]),(vel_maxima.[3]),(vel_maxima.[4]),(vel_maxima.[5]),(vel_maxima.[6]),(vel_maxima.[7]),(vel_maxima.[8]),(vel_maxima.[9]),
					 (vel_maxima.[10]),(vel_maxima.[11]),(vel_maxima.[12]),(vel_maxima.[13]),(vel_maxima.[14]),(vel_maxima.[15]),(vel_maxima.[16]),(vel_maxima.[17]),(vel_maxima.[18]),
					 (vel_maxima.[19]),(vel_maxima.[20]),(vel_maxima.[21]),(vel_maxima.[22]),(vel_maxima.[23]),(vel_maxima.[24]),(vel_maxima.[25]),(vel_maxima.[26]),(vel_maxima.[27]),
					 (vel_maxima.[28]),(vel_maxima.[29]),(vel_maxima.[30]),(vel_maxima.[31])) T (c)) AS total_velocidade_maxima,

		(SELECT SUM(c)
		 FROM (VALUES(autos_detectados.[1]),(autos_detectados.[2]),(autos_detectados.[3]),(autos_detectados.[4]),(autos_detectados.[5]),(autos_detectados.[6]),(autos_detectados.[7]),
					 (autos_detectados.[8]),(autos_detectados.[9]),(autos_detectados.[10]),(autos_detectados.[11]),(autos_detectados.[12]),(autos_detectados.[13]),
					 (autos_detectados.[14]),(autos_detectados.[15]),(autos_detectados.[16]),(autos_detectados.[17]),(autos_detectados.[18]),(autos_detectados.[19]),
					 (autos_detectados.[20]),(autos_detectados.[21]),(autos_detectados.[22]),(autos_detectados.[23]),(autos_detectados.[24]),(autos_detectados.[25]),
					 (autos_detectados.[26]),(autos_detectados.[27]),(autos_detectados.[28]),(autos_detectados.[29]),(autos_detectados.[30]),
					 (autos_detectados.[31])) T (c)) AS total_autos_detectados,

		(SELECT SUM(c)
		 FROM (VALUES(autos_validos.[1]),(autos_validos.[2]),(autos_validos.[3]),(autos_validos.[4]),(autos_validos.[5]),(autos_validos.[6]),(autos_validos.[7]),
					 (autos_validos.[8]),(autos_validos.[9]),(autos_validos.[10]),(autos_validos.[11]),(autos_validos.[12]),(autos_validos.[13]),
					 (autos_validos.[14]),(autos_validos.[15]),(autos_validos.[16]),(autos_validos.[17]),(autos_validos.[18]),(autos_validos.[19]),
					 (autos_validos.[20]),(autos_validos.[21]),(autos_validos.[22]),(autos_validos.[23]),(autos_validos.[24]),(autos_validos.[25]),
					 (autos_validos.[26]),(autos_validos.[27]),(autos_validos.[28]),(autos_validos.[29]),(autos_validos.[30]),
					 (autos_validos.[31])) T (c)) AS total_autos_validos,

		(SELECT SUM(c)
		 FROM (VALUES(autos_invalidos_pt.[1]),(autos_invalidos_pt.[2]),(autos_invalidos_pt.[3]),(autos_invalidos_pt.[4]),(autos_invalidos_pt.[5]),(autos_invalidos_pt.[6]),(autos_invalidos_pt.[7]),
					 (autos_invalidos_pt.[8]),(autos_invalidos_pt.[9]),(autos_invalidos_pt.[10]),(autos_invalidos_pt.[11]),(autos_invalidos_pt.[12]),(autos_invalidos_pt.[13]),
					 (autos_invalidos_pt.[14]),(autos_invalidos_pt.[15]),(autos_invalidos_pt.[16]),(autos_invalidos_pt.[17]),(autos_invalidos_pt.[18]),(autos_invalidos_pt.[19]),
					 (autos_invalidos_pt.[20]),(autos_invalidos_pt.[21]),(autos_invalidos_pt.[22]),(autos_invalidos_pt.[23]),(autos_invalidos_pt.[24]),(autos_invalidos_pt.[25]),
					 (autos_invalidos_pt.[26]),(autos_invalidos_pt.[27]),(autos_invalidos_pt.[28]),(autos_invalidos_pt.[29]),(autos_invalidos_pt.[30]),
					 (autos_invalidos_pt.[31])) T (c)) AS total_autos_invalidos_tecnicos,

		(SELECT SUM(c)
		 FROM (VALUES(autos_invalidos_pnt.[1]),(autos_invalidos_pnt.[2]),(autos_invalidos_pnt.[3]),(autos_invalidos_pnt.[4]),(autos_invalidos_pnt.[5]),(autos_invalidos_pnt.[6]),(autos_invalidos_pnt.[7]),
					 (autos_invalidos_pnt.[8]),(autos_invalidos_pnt.[9]),(autos_invalidos_pnt.[10]),(autos_invalidos_pnt.[11]),(autos_invalidos_pnt.[12]),(autos_invalidos_pnt.[13]),
					 (autos_invalidos_pnt.[14]),(autos_invalidos_pnt.[15]),(autos_invalidos_pnt.[16]),(autos_invalidos_pnt.[17]),(autos_invalidos_pnt.[18]),(autos_invalidos_pnt.[19]),
					 (autos_invalidos_pnt.[20]),(autos_invalidos_pnt.[21]),(autos_invalidos_pnt.[22]),(autos_invalidos_pnt.[23]),(autos_invalidos_pnt.[24]),(autos_invalidos_pnt.[25]),
					 (autos_invalidos_pnt.[26]),(autos_invalidos_pnt.[27]),(autos_invalidos_pnt.[28]),(autos_invalidos_pnt.[29]),(autos_invalidos_pnt.[30]),
					 (autos_invalidos_pnt.[31])) T (c)) AS total_autos_invalidos_nao_tecnicos
 FROM   (   
		   SELECT lv.id_local,
				  lv.serie_equipamento,
				  lv.nome AS nome_pista_sentido,
				  lpv.id_pista,
				  lpv.cod_pista_alternativo AS faixa,
				  lpv.nome AS nome_pista_sentido_faixa,
				  lv.posicao_lat,
				  lv.posicao_lon,
				  lpv.codigo_equipamento  
		   FROM   local_vigente lv (NOLOCK)  
				  INNER JOIN local_pista_vigente lpv (NOLOCK)  
						ON  lpv.id_configuracao_equipamento = lv.id_configuracao_equipamento  
			WHERE  lv.desativado = 0  
				   --AND CAST(lv.data_inicio AS DATE) <= CAST(GETDATE() AS DATE)  
		   GROUP BY  
				  lv.id_local,
				  lv.serie_equipamento,
				  lv.nome,
				  lpv.id_pista,
				  lpv.cod_pista_alternativo,
				  lpv.nome,
				  lv.posicao_lat,
				  lv.posicao_lon,
				  lpv.codigo_equipamento  
       ) AS dados_local   
	   LEFT JOIN (  
					  --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.veiculos_detectados  
								 FROM   dbo.fcn_getVeiculoSumarizadoRelatorio(@Data_Ini, @Data_Fim) vs  
						) fv  
					  PIVOT (  
								SUM(fv.veiculos_detectados)  
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							) contagem_fluxo  
	   ) AS fluxo  
			ON  fluxo.id_local = dados_local.id_local  
				AND fluxo.id_pista = dados_local.id_pista  
       LEFT JOIN (  
					  --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.velocidade_media  
								 FROM   dbo.fcn_getVeiculoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs  
								 GROUP BY  
										vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia),
										vs.velocidade_media
							 ) vm  
					  PIVOT  (  
								 AVG(vm.velocidade_media)  
								 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							  ) contagem_vm  
       ) AS vel_media  
			ON  vel_media.id_local = dados_local.id_local  
				AND vel_media.id_pista = dados_local.id_pista  
       LEFT JOIN (  
					  --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.velocidade_maxima
								 FROM   dbo.fcn_getVeiculoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs 
								 GROUP BY  
										vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia),
										vs.velocidade_maxima
							 ) vm  
					  PIVOT  (  
								 MAX(vm.velocidade_maxima)  
								 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							 ) contagem_vm  
	   ) AS vel_maxima  
			ON  vel_maxima.id_local = dados_local.id_local  
				AND vel_maxima.id_pista = dados_local.id_pista  
       LEFT JOIN (  
					  --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2020-09-01 00:00:00.000', @Data_Fim DATETIME = '2020-09-30 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.infracoes_registradas
								 FROM   dbo.fcn_getInfracaoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs  
							 ) ad  
					  PIVOT  (  
								 SUM(ad.infracoes_registradas)  
								 FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							 ) contagem_ad  
       ) AS autos_detectados  
			ON  autos_detectados.id_local = dados_local.id_local  
				AND autos_detectados.id_pista = dados_local.id_pista  
       LEFT JOIN (
					  --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2020-09-01 00:00:00.000', @Data_Fim DATETIME = '2020-09-30 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.infracoes_validas
								 FROM   dbo.fcn_getInfracaoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs  
							 ) av  
					  PIVOT  (  
								SUM(av.infracoes_validas)  
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							 ) contagem_av  
       ) AS autos_validos  
			ON  autos_validos.id_local = dados_local.id_local  
				AND autos_validos.id_pista = dados_local.id_pista  

       LEFT JOIN (
					  --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.invalidas_tecnicos
								 FROM   dbo.fcn_getInfracaoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs  
							 ) aipt 
					  PIVOT  (  
								SUM(aipt.invalidas_tecnicos)  
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							 ) contagem_aipt  
       ) AS autos_invalidos_pt
			ON  autos_invalidos_pt.id_local = dados_local.id_local  
				AND autos_invalidos_pt.id_pista = dados_local.id_pista  

       LEFT JOIN (
					  --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
					  SELECT *  
					  FROM   (  
								 --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
								 SELECT vs.id_local,
										vs.id_pista,
										DATEPART(DAY, vs.dia) AS dia,
										vs.invalidas_nao_tecnicos
								 FROM   dbo.fcn_getInfracaoSumarizadoRelatorio(@Data_Ini,@Data_Fim) vs  
							 ) aipnt 
					  PIVOT  (  
								SUM(aipnt.invalidas_nao_tecnicos)  
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
							 ) contagem_aipnt
       ) AS autos_invalidos_pnt
			ON  autos_invalidos_pnt.id_local = dados_local.id_local  
				AND autos_invalidos_pnt.id_pista = dados_local.id_pista  

)
