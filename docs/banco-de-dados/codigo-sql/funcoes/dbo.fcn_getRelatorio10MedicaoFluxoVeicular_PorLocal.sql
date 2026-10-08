
CREATE FUNCTION [dbo].[fcn_getRelatorio10MedicaoFluxoVeicular_PorLocal](@Data_Ini DATETIME, @Data_Fim DATETIME)  
RETURNS TABLE  
AS  
RETURN  
(   
  
 --DECLARE @Data_Ini DATETIME = '2020-09-01 00:00:00.000', @Data_Fim DATETIME = '2020-09-30 23:59:59.000'  
 SELECT result.id_local,  
     result.serie_equipamento,  
     result.nome_pista_sentido,  
     result.latitude,  
     result.longitude,  
     result.codigos_equipamentos,
     STUFF((SELECT '/' + RTRIM(CAST(CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END AS VARCHAR(10))) AS [text()]  
      FROM   local_pista_vigente lpv (NOLOCK)  
       LEFT JOIN configuracao_equipamento_velocidade_limite cevl (NOLOCK)  
         ON  lpv.id_local = cevl.id_local  
          AND lpv.id_pista = cevl.id_pista  
       LEFT JOIN descricao_pista_gst dpg (NOLOCK)  
         ON  dpg.id_local = lpv.id_local  
          AND dpg.id_pista = lpv.id_pista  
      WHERE  lpv.id_local = result.id_local  
      GROUP BY  
          CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END  
      ORDER BY  
          CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END  
      FOR XML PATH('')  
      ), 1, 1, '' ) AS velocidade_permitida,  
     result.fv_1,result.vm_1,result.vmax_1,result.ad_1,result.av_1,  
     result.fv_2,result.vm_2,result.vmax_2,result.ad_2,result.av_2,  
     result.fv_3,result.vm_3,result.vmax_3,result.ad_3,result.av_3,  
     result.fv_4,result.vm_4,result.vmax_4,result.ad_4,result.av_4,  
     result.fv_5,result.vm_5,result.vmax_5,result.ad_5,result.av_5,  
     result.fv_6,result.vm_6,result.vmax_6,result.ad_6,result.av_6,  
     result.fv_7,result.vm_7,result.vmax_7,result.ad_7,result.av_7,  
     result.fv_8,result.vm_8,result.vmax_8,result.ad_8,result.av_8,  
     result.fv_9,result.vm_9,result.vmax_9,result.ad_9,result.av_9,  
     result.fv_10,result.vm_10,result.vmax_10,result.ad_10,result.av_10,  
     result.fv_11,result.vm_11,result.vmax_11,result.ad_11,result.av_11,  
     result.fv_12,result.vm_12,result.vmax_12,result.ad_12,result.av_12,  
     result.fv_13,result.vm_13,result.vmax_13,result.ad_13,result.av_13,  
     result.fv_14,result.vm_14,result.vmax_14,result.ad_14,result.av_14,  
     result.fv_15,result.vm_15,result.vmax_15,result.ad_15,result.av_15,  
     result.fv_16,result.vm_16,result.vmax_16,result.ad_16,result.av_16,  
     result.fv_17,result.vm_17,result.vmax_17,result.ad_17,result.av_17,  
     result.fv_18,result.vm_18,result.vmax_18,result.ad_18,result.av_18,  
     result.fv_19,result.vm_19,result.vmax_19,result.ad_19,result.av_19,  
     result.fv_20,result.vm_20,result.vmax_20,result.ad_20,result.av_20,  
     result.fv_21,result.vm_21,result.vmax_21,result.ad_21,result.av_21,  
     result.fv_22,result.vm_22,result.vmax_22,result.ad_22,result.av_22,  
     result.fv_23,result.vm_23,result.vmax_23,result.ad_23,result.av_23,  
     result.fv_24,result.vm_24,result.vmax_24,result.ad_24,result.av_24,  
     result.fv_25,result.vm_25,result.vmax_25,result.ad_25,result.av_25,  
     result.fv_26,result.vm_26,result.vmax_26,result.ad_26,result.av_26,  
     result.fv_27,result.vm_27,result.vmax_27,result.ad_27,result.av_27,  
     result.fv_28,result.vm_28,result.vmax_28,result.ad_28,result.av_28,  
     result.fv_29,result.vm_29,result.vmax_29,result.ad_29,result.av_29,  
     result.fv_30,result.vm_30,result.vmax_30,result.ad_30,result.av_30,  
     result.fv_31,result.vm_31,result.vmax_31,result.ad_31,result.av_31,  
     result.total_fluxo,  
     result.total_velocidade_media,  
     result.total_velocidade_maxima,  
     result.total_autos_detectados,  
     result.total_autos_validos,
	 result.total_autos_invalidos_tecnicos,
	 result.total_autos_invalidos_nao_tecnicos
 FROM   (  
    --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
    SELECT dados.id_local,  
        dados.serie_equipamento,  
        dados.nome_pista_sentido,  
        --dados.codigo_equipamento, 
		STUFF((SELECT ', ' + RTRIM(CAST(sub.codigo_equipamento AS VARCHAR(10))) AS [text()]  
		  FROM   local_pista_vigente sub  
		  WHERE  sub.id_local = dados.id_local  
		  FOR XML PATH('')  
		), 1, 1, '' ) AS codigos_equipamentos,
        dados.latitude,  
        dados.longitude,  
        --cevl.velocidade_limite AS velocidade_permitida,  
        SUM(dados.fv_1) AS fv_1,CAST(ROUND(AVG(dados.vm_1),0) AS INT) AS vm_1,MAX(dados.vmax_1) AS vmax_1,SUM(dados.ad_1) AS ad_1,SUM(dados.av_1) AS av_1,  
        SUM(dados.fv_2) AS fv_2,CAST(ROUND(AVG(dados.vm_2),0) AS INT) AS vm_2,MAX(dados.vmax_2) AS vmax_2,SUM(dados.ad_2) AS ad_2,SUM(dados.av_2) AS av_2,  
        SUM(dados.fv_3) AS fv_3,CAST(ROUND(AVG(dados.vm_3),0) AS INT) AS vm_3,MAX(dados.vmax_3) AS vmax_3,SUM(dados.ad_3) AS ad_3,SUM(dados.av_3) AS av_3,  
        SUM(dados.fv_4) AS fv_4,CAST(ROUND(AVG(dados.vm_4),0) AS INT) AS vm_4,MAX(dados.vmax_4) AS vmax_4,SUM(dados.ad_4) AS ad_4,SUM(dados.av_4) AS av_4,  
        SUM(dados.fv_5) AS fv_5,CAST(ROUND(AVG(dados.vm_5),0) AS INT) AS vm_5,MAX(dados.vmax_5) AS vmax_5,SUM(dados.ad_5) AS ad_5,SUM(dados.av_5) AS av_5,  
        SUM(dados.fv_6) AS fv_6,CAST(ROUND(AVG(dados.vm_6),0) AS INT) AS vm_6,MAX(dados.vmax_6) AS vmax_6,SUM(dados.ad_6) AS ad_6,SUM(dados.av_6) AS av_6,  
        SUM(dados.fv_7) AS fv_7,CAST(ROUND(AVG(dados.vm_7),0) AS INT) AS vm_7,MAX(dados.vmax_7) AS vmax_7,SUM(dados.ad_7) AS ad_7,SUM(dados.av_7) AS av_7,  
        SUM(dados.fv_8) AS fv_8,CAST(ROUND(AVG(dados.vm_8),0) AS INT) AS vm_8,MAX(dados.vmax_8) AS vmax_8,SUM(dados.ad_8) AS ad_8,SUM(dados.av_8) AS av_8,  
        SUM(dados.fv_9) AS fv_9,CAST(ROUND(AVG(dados.vm_9),0) AS INT) AS vm_9,MAX(dados.vmax_9) AS vmax_9,SUM(dados.ad_9) AS ad_9,SUM(dados.av_9) AS av_9,  
        SUM(dados.fv_10) AS fv_10,CAST(ROUND(AVG(dados.vm_10),0) AS INT) AS vm_10,MAX(dados.vmax_10) AS vmax_10,SUM(dados.ad_10) AS ad_10,SUM(dados.av_10) AS av_10,  
        SUM(dados.fv_11) AS fv_11,CAST(ROUND(AVG(dados.vm_11),0) AS INT) AS vm_11,MAX(dados.vmax_11) AS vmax_11,SUM(dados.ad_11) AS ad_11,SUM(dados.av_11) AS av_11,  
        SUM(dados.fv_12) AS fv_12,CAST(ROUND(AVG(dados.vm_12),0) AS INT) AS vm_12,MAX(dados.vmax_12) AS vmax_12,SUM(dados.ad_12) AS ad_12,SUM(dados.av_12) AS av_12,  
        SUM(dados.fv_13) AS fv_13,CAST(ROUND(AVG(dados.vm_13),0) AS INT) AS vm_13,MAX(dados.vmax_13) AS vmax_13,SUM(dados.ad_13) AS ad_13,SUM(dados.av_13) AS av_13,  
        SUM(dados.fv_14) AS fv_14,CAST(ROUND(AVG(dados.vm_14),0) AS INT) AS vm_14,MAX(dados.vmax_14) AS vmax_14,SUM(dados.ad_14) AS ad_14,SUM(dados.av_14) AS av_14,  
        SUM(dados.fv_15) AS fv_15,CAST(ROUND(AVG(dados.vm_15),0) AS INT) AS vm_15,MAX(dados.vmax_15) AS vmax_15,SUM(dados.ad_15) AS ad_15,SUM(dados.av_15) AS av_15,  
        SUM(dados.fv_16) AS fv_16,CAST(ROUND(AVG(dados.vm_16),0) AS INT) AS vm_16,MAX(dados.vmax_16) AS vmax_16,SUM(dados.ad_16) AS ad_16,SUM(dados.av_16) AS av_16,  
        SUM(dados.fv_17) AS fv_17,CAST(ROUND(AVG(dados.vm_17),0) AS INT) AS vm_17,MAX(dados.vmax_17) AS vmax_17,SUM(dados.ad_17) AS ad_17,SUM(dados.av_17) AS av_17,  
        SUM(dados.fv_18) AS fv_18,CAST(ROUND(AVG(dados.vm_18),0) AS INT) AS vm_18,MAX(dados.vmax_18) AS vmax_18,SUM(dados.ad_18) AS ad_18,SUM(dados.av_18) AS av_18,  
        SUM(dados.fv_19) AS fv_19,CAST(ROUND(AVG(dados.vm_19),0) AS INT) AS vm_19,MAX(dados.vmax_19) AS vmax_19,SUM(dados.ad_19) AS ad_19,SUM(dados.av_19) AS av_19,  
        SUM(dados.fv_20) AS fv_20,CAST(ROUND(AVG(dados.vm_20),0) AS INT) AS vm_20,MAX(dados.vmax_20) AS vmax_20,SUM(dados.ad_20) AS ad_20,SUM(dados.av_20) AS av_20,  
        SUM(dados.fv_21) AS fv_21,CAST(ROUND(AVG(dados.vm_21),0) AS INT) AS vm_21,MAX(dados.vmax_21) AS vmax_21,SUM(dados.ad_21) AS ad_21,SUM(dados.av_21) AS av_21,  
        SUM(dados.fv_22) AS fv_22,CAST(ROUND(AVG(dados.vm_22),0) AS INT) AS vm_22,MAX(dados.vmax_22) AS vmax_22,SUM(dados.ad_22) AS ad_22,SUM(dados.av_22) AS av_22,  
        SUM(dados.fv_23) AS fv_23,CAST(ROUND(AVG(dados.vm_23),0) AS INT) AS vm_23,MAX(dados.vmax_23) AS vmax_23,SUM(dados.ad_23) AS ad_23,SUM(dados.av_23) AS av_23,  
        SUM(dados.fv_24) AS fv_24,CAST(ROUND(AVG(dados.vm_24),0) AS INT) AS vm_24,MAX(dados.vmax_24) AS vmax_24,SUM(dados.ad_24) AS ad_24,SUM(dados.av_24) AS av_24,  
        SUM(dados.fv_25) AS fv_25,CAST(ROUND(AVG(dados.vm_25),0) AS INT) AS vm_25,MAX(dados.vmax_25) AS vmax_25,SUM(dados.ad_25) AS ad_25,SUM(dados.av_25) AS av_25,  
        SUM(dados.fv_26) AS fv_26,CAST(ROUND(AVG(dados.vm_26),0) AS INT) AS vm_26,MAX(dados.vmax_26) AS vmax_26,SUM(dados.ad_26) AS ad_26,SUM(dados.av_26) AS av_26,  
        SUM(dados.fv_27) AS fv_27,CAST(ROUND(AVG(dados.vm_27),0) AS INT) AS vm_27,MAX(dados.vmax_27) AS vmax_27,SUM(dados.ad_27) AS ad_27,SUM(dados.av_27) AS av_27,  
        SUM(dados.fv_28) AS fv_28,CAST(ROUND(AVG(dados.vm_28),0) AS INT) AS vm_28,MAX(dados.vmax_28) AS vmax_28,SUM(dados.ad_28) AS ad_28,SUM(dados.av_28) AS av_28,  
        SUM(dados.fv_29) AS fv_29,CAST(ROUND(AVG(dados.vm_29),0) AS INT) AS vm_29,MAX(dados.vmax_29) AS vmax_29,SUM(dados.ad_29) AS ad_29,SUM(dados.av_29) AS av_29,  
        SUM(dados.fv_30) AS fv_30,CAST(ROUND(AVG(dados.vm_30),0) AS INT) AS vm_30,MAX(dados.vmax_30) AS vmax_30,SUM(dados.ad_30) AS ad_30,SUM(dados.av_30) AS av_30,  
        SUM(dados.fv_31) AS fv_31,CAST(ROUND(AVG(dados.vm_31),0) AS INT) AS vm_31,MAX(dados.vmax_31) AS vmax_31,SUM(dados.ad_31) AS ad_31,SUM(dados.av_31) AS av_31,  
        SUM(dados.total_fluxo) AS total_fluxo,  
        AVG(dados.total_velocidade_media) AS total_velocidade_media,  
        MAX(dados.total_velocidade_maxima) AS total_velocidade_maxima,  
        SUM(dados.total_autos_detectados) AS total_autos_detectados,  
        SUM(dados.total_autos_validos) AS total_autos_validos,  
        SUM(dados.total_autos_invalidos_tecnicos) AS total_autos_invalidos_tecnicos,  
        SUM(dados.total_autos_invalidos_nao_tecnicos) AS total_autos_invalidos_nao_tecnicos
    FROM   dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados(@Data_Ini, @Data_Fim) dados  
    GROUP BY  
        dados.id_local,  
        dados.serie_equipamento,  
        dados.nome_pista_sentido,  
        --dados.codigo_equipamento, 
        dados.latitude,  
        dados.longitude  
    ) result  
  
)  
