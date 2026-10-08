

CREATE FUNCTION [dbo].[fcn_getRelatorio7MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)  
RETURNS TABLE  
AS  
RETURN  
(   
  
 --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
 SELECT horarios.hora,  
     horarios.hora_desc,  
  
     fluxo.[1] AS [fv_1],fluxo.[2] AS [fv_2],fluxo.[3] AS [fv_3],fluxo.[4] AS [fv_4],fluxo.[5] AS [fv_5],fluxo.[6] AS [fv_6],fluxo.[7] AS [fv_7],  
     fluxo.[8] AS [fv_8],fluxo.[9] AS [fv_9],fluxo.[10] AS [fv_10],fluxo.[11] AS [fv_11],fluxo.[12] AS [fv_12],fluxo.[13] AS [fv_13],fluxo.[14] AS [fv_14],  
     fluxo.[15] AS [fv_15],fluxo.[16] AS [fv_16],fluxo.[17] AS [fv_17],fluxo.[18] AS [fv_18],fluxo.[19] AS [fv_19],fluxo.[20] AS [fv_20],  
     fluxo.[21] AS [fv_21],fluxo.[22] AS [fv_22],fluxo.[23] AS [fv_23],fluxo.[24] AS [fv_24],fluxo.[25] AS [fv_25],fluxo.[26] AS [fv_26],  
     fluxo.[27] AS [fv_27],fluxo.[28] AS [fv_28],fluxo.[29] AS [fv_29],fluxo.[30] AS [fv_30],fluxo.[31] AS [fv_31],  
  
     registro_ocr.[1] AS [ocr_1],registro_ocr.[2] AS [ocr_2],registro_ocr.[3] AS [ocr_3],registro_ocr.[4] AS [ocr_4],registro_ocr.[5] AS [ocr_5],  
     registro_ocr.[6] AS [ocr_6],registro_ocr.[7] AS [ocr_7],registro_ocr.[8] AS [ocr_8],registro_ocr.[9] AS [ocr_9],registro_ocr.[10] AS [ocr_10],  
     registro_ocr.[11] AS [ocr_11],registro_ocr.[12] AS [ocr_12],registro_ocr.[13] AS [ocr_13],registro_ocr.[14] AS [ocr_14],registro_ocr.[15] AS [ocr_15],  
     registro_ocr.[16] AS [ocr_16],registro_ocr.[17] AS [ocr_17],registro_ocr.[18] AS [ocr_18],registro_ocr.[19] AS [ocr_19],registro_ocr.[20] AS [ocr_20],  
     registro_ocr.[21] AS [ocr_21],registro_ocr.[22] AS [ocr_22],registro_ocr.[23] AS [ocr_23],registro_ocr.[24] AS [ocr_24],registro_ocr.[25] AS [ocr_25],  
     registro_ocr.[26] AS [ocr_26],registro_ocr.[27] AS [ocr_27],registro_ocr.[28] AS [ocr_28],registro_ocr.[29] AS [ocr_29],registro_ocr.[30] AS [ocr_30],  
     registro_ocr.[31] AS [ocr_31],  
  
     autos_detec_56732.[1]  AS ad_56732_1,  autos_detec_56732.[2]  AS ad_56732_2,  autos_detec_56732.[3]  AS ad_56732_3,  autos_detec_56732.[4]  AS ad_56732_4,  
     autos_detec_56732.[5]  AS ad_56732_5,  autos_detec_56732.[6]  AS ad_56732_6,  autos_detec_56732.[7]  AS ad_56732_7,  autos_detec_56732.[8]  AS ad_56732_8,  
     autos_detec_56732.[9]  AS ad_56732_9,  autos_detec_56732.[10] AS ad_56732_10, autos_detec_56732.[11] AS ad_56732_11, autos_detec_56732.[12] AS ad_56732_12,  
     autos_detec_56732.[13] AS ad_56732_13, autos_detec_56732.[14] AS ad_56732_14, autos_detec_56732.[15] AS ad_56732_15, autos_detec_56732.[16] AS ad_56732_16,  
     autos_detec_56732.[17] AS ad_56732_17, autos_detec_56732.[18] AS ad_56732_18, autos_detec_56732.[19] AS ad_56732_19, autos_detec_56732.[20] AS ad_56732_20,  
     autos_detec_56732.[21] AS ad_56732_21, autos_detec_56732.[22] AS ad_56732_22, autos_detec_56732.[23] AS ad_56732_23, autos_detec_56732.[24] AS ad_56732_24,  
     autos_detec_56732.[25] AS ad_56732_25, autos_detec_56732.[26] AS ad_56732_26, autos_detec_56732.[27] AS ad_56732_27, autos_detec_56732.[28] AS ad_56732_28,  
     autos_detec_56732.[29] AS ad_56732_29, autos_detec_56732.[30] AS ad_56732_30, autos_detec_56732.[31] AS ad_56732_31,  
  
     autos_detec_60503.[1]  AS ad_60503_1,  autos_detec_60503.[2]  AS ad_60503_2,  autos_detec_60503.[3]  AS ad_60503_3,  autos_detec_60503.[4]   AS ad_60503_4,  
     autos_detec_60503.[5]  AS ad_60503_5,  autos_detec_60503.[6]  AS ad_60503_6,  autos_detec_60503.[7]  AS ad_60503_7,  autos_detec_60503.[8]   AS ad_60503_8,  
     autos_detec_60503.[9]  AS ad_60503_9,  autos_detec_60503.[10] AS ad_60503_10, autos_detec_60503.[11] AS ad_60503_11, autos_detec_60503.[12]  AS ad_60503_12,  
     autos_detec_60503.[13] AS ad_60503_13, autos_detec_60503.[14] AS ad_60503_14, autos_detec_60503.[15] AS ad_60503_15, autos_detec_60503.[16]  AS ad_60503_16,  
     autos_detec_60503.[17] AS ad_60503_17, autos_detec_60503.[18] AS ad_60503_18, autos_detec_60503.[19] AS ad_60503_19, autos_detec_60503.[20]  AS ad_60503_20,  
     autos_detec_60503.[21] AS ad_60503_21, autos_detec_60503.[22] AS ad_60503_22, autos_detec_60503.[23] AS ad_60503_23, autos_detec_60503.[24]  AS ad_60503_24,  
     autos_detec_60503.[25] AS ad_60503_25, autos_detec_60503.[26] AS ad_60503_26, autos_detec_60503.[27] AS ad_60503_27, autos_detec_60503.[28]  AS ad_60503_28,  
     autos_detec_60503.[29] AS ad_60503_29, autos_detec_60503.[30] AS ad_60503_30, autos_detec_60503.[31] AS ad_60503_31,  
  
     autos_detec_74550.[1] AS ad_74550_1, autos_detec_74550.[2] AS ad_74550_2, autos_detec_74550.[3] AS ad_74550_3, autos_detec_74550.[4] AS ad_74550_4,  
     autos_detec_74550.[5] AS ad_74550_5, autos_detec_74550.[6] AS ad_74550_6, autos_detec_74550.[7] AS ad_74550_7, autos_detec_74550.[8] AS ad_74550_8,  
     autos_detec_74550.[9] AS ad_74550_9, autos_detec_74550.[10] AS ad_74550_10, autos_detec_74550.[11] AS ad_74550_11, autos_detec_74550.[12] AS ad_74550_12,  
     autos_detec_74550.[13] AS ad_74550_13, autos_detec_74550.[14] AS ad_74550_14, autos_detec_74550.[15] AS ad_74550_15, autos_detec_74550.[16] AS ad_74550_16,  
     autos_detec_74550.[17] AS ad_74550_17, autos_detec_74550.[18] AS ad_74550_18, autos_detec_74550.[19] AS ad_74550_19, autos_detec_74550.[20] AS ad_74550_20,  
     autos_detec_74550.[21] AS ad_74550_21, autos_detec_74550.[22] AS ad_74550_22, autos_detec_74550.[23] AS ad_74550_23, autos_detec_74550.[24] AS ad_74550_24,  
     autos_detec_74550.[25] AS ad_74550_25, autos_detec_74550.[26] AS ad_74550_26, autos_detec_74550.[27] AS ad_74550_27, autos_detec_74550.[28] AS ad_74550_28,  
     autos_detec_74550.[29] AS ad_74550_29, autos_detec_74550.[30] AS ad_74550_30, autos_detec_74550.[31] AS ad_74550_31,  
  
     autos_detec_74630.[1] AS ad_74630_1, autos_detec_74630.[2] AS ad_74630_2, autos_detec_74630.[3] AS ad_74630_3, autos_detec_74630.[4] AS ad_74630_4,  
     autos_detec_74630.[5] AS ad_74630_5, autos_detec_74630.[6] AS ad_74630_6, autos_detec_74630.[7] AS ad_74630_7, autos_detec_74630.[8] AS ad_74630_8,  
     autos_detec_74630.[9] AS ad_74630_9, autos_detec_74630.[10] AS ad_74630_10, autos_detec_74630.[11] AS ad_74630_11, autos_detec_74630.[12] AS ad_74630_12,  
     autos_detec_74630.[13] AS ad_74630_13, autos_detec_74630.[14] AS ad_74630_14, autos_detec_74630.[15] AS ad_74630_15, autos_detec_74630.[16] AS ad_74630_16,  
     autos_detec_74630.[17] AS ad_74630_17, autos_detec_74630.[18] AS ad_74630_18, autos_detec_74630.[19] AS ad_74630_19, autos_detec_74630.[20] AS ad_74630_20,  
     autos_detec_74630.[21] AS ad_74630_21, autos_detec_74630.[22] AS ad_74630_22, autos_detec_74630.[23] AS ad_74630_23, autos_detec_74630.[24] AS ad_74630_24,  
     autos_detec_74630.[25] AS ad_74630_25, autos_detec_74630.[26] AS ad_74630_26, autos_detec_74630.[27] AS ad_74630_27, autos_detec_74630.[28] AS ad_74630_28,  
     autos_detec_74630.[29] AS ad_74630_29, autos_detec_74630.[30] AS ad_74630_30, autos_detec_74630.[31] AS ad_74630_31,  
  
     autos_detec_74710.[1] AS ad_74710_1, autos_detec_74710.[2] AS ad_74710_2, autos_detec_74710.[3] AS ad_74710_3, autos_detec_74710.[4] AS ad_74710_4,  
     autos_detec_74710.[5] AS ad_74710_5, autos_detec_74710.[6] AS ad_74710_6, autos_detec_74710.[7] AS ad_74710_7, autos_detec_74710.[8] AS ad_74710_8,  
     autos_detec_74710.[9] AS ad_74710_9, autos_detec_74710.[10] AS ad_74710_10, autos_detec_74710.[11] AS ad_74710_11, autos_detec_74710.[12] AS ad_74710_12,  
     autos_detec_74710.[13] AS ad_74710_13, autos_detec_74710.[14] AS ad_74710_14, autos_detec_74710.[15] AS ad_74710_15, autos_detec_74710.[16] AS ad_74710_16,  
     autos_detec_74710.[17] AS ad_74710_17, autos_detec_74710.[18] AS ad_74710_18, autos_detec_74710.[19] AS ad_74710_19, autos_detec_74710.[20] AS ad_74710_20,  
     autos_detec_74710.[21] AS ad_74710_21, autos_detec_74710.[22] AS ad_74710_22, autos_detec_74710.[23] AS ad_74710_23, autos_detec_74710.[24] AS ad_74710_24,  
     autos_detec_74710.[25] AS ad_74710_25, autos_detec_74710.[26] AS ad_74710_26, autos_detec_74710.[27] AS ad_74710_27, autos_detec_74710.[28] AS ad_74710_28,  
     autos_detec_74710.[29] AS ad_74710_29, autos_detec_74710.[30] AS ad_74710_30, autos_detec_74710.[31] AS ad_74710_31,  
  
     ISNULL(total_fluxo.[veiculos_detectados], 0) AS total_fluxo, ISNULL(total_registro_ocr.[total_ocr], 0) AS total_ocr, 
	 ISNULL(totais.[total_ad_56732], 0) AS total_ad_56732, ISNULL(totais.[total_ad_60503], 0) AS total_ad_60503,  
     ISNULL(totais.[total_ad_74550], 0) AS total_ad_74550, ISNULL(totais.[total_ad_74630], 0) AS total_ad_74630,  
     ISNULL(totais.[total_ad_74710], 0) AS total_ad_74710
  
 FROM   hora horarios (NOLOCK)  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.id_local,  
             vp.veiculos_detectados  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
          ) fv  
      PIVOT (  
        SUM(veiculos_detectados)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_fluxo  
     ) AS fluxo  
    ON  fluxo.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY, v.data) AS dia,  
             DATEPART(HOUR, v.data) AS hora,  
             v.id_local,  
             v.id_veiculo  
         FROM   veiculo v (NOLOCK)  
             LEFT JOIN infracao i (NOLOCK)  
            ON  i.id_veiculo = v.id_veiculo  
         WHERE  v.data BETWEEN @Data_Ini AND @Data_Fim  
             AND v.id_local = @Id_Local  
             AND v.pista = CASE WHEN @Id_Pista IS NULL THEN v.pista ELSE @Id_Pista END  
             AND v.placa = i.placa  
          ) ocr  
      PIVOT (  
        COUNT(id_veiculo)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ocr  
     ) AS registro_ocr  
    ON  registro_ocr.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 56732  
          ) ad_56732  
      PIVOT  
         (  
        SUM(infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad_56732  
     ) AS autos_detec_56732 
    ON  autos_detec_56732.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 60503  
          ) ad_60503  
      PIVOT  
         (  
        SUM(infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad_60503  
     ) AS autos_detec_60503  
    ON  autos_detec_60503.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74550  
          ) ad_74550  
      PIVOT  
         (  
        SUM(infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad_74550  
     ) AS autos_detec_74550 
    ON  autos_detec_74550.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74630  
          ) ad_74630  
      PIVOT  
         (  
        SUM(infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad_74630  
     ) AS autos_detec_74630  
    ON  autos_detec_74630.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74710  
          ) ad_74710  
      PIVOT  
         (  
        SUM(infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad_74710  
     ) AS autos_detec_74710  
    ON  autos_detec_74710.hora = horarios.hora  
    
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT vp.hora,  
          SUM(vp.veiculos_detectados) AS veiculos_detectados  
      FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
      WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
      GROUP BY  
          vp.hora  
     ) AS total_fluxo  
    ON  total_fluxo.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT DATEPART(HOUR, v.data) AS hora,  
          COUNT(v.id_veiculo) AS total_ocr  
      FROM   veiculo v (NOLOCK)  
          LEFT JOIN infracao i (NOLOCK)  
         ON  i.id_veiculo = v.id_veiculo  
      WHERE  v.data BETWEEN @Data_Ini AND @Data_Fim  
          AND v.id_local = @Id_Local  
          AND v.pista = CASE WHEN @Id_Pista IS NULL THEN v.pista ELSE @Id_Pista END  
          AND v.placa = i.placa  
      GROUP BY  
          DATEPART(HOUR, v.data)  
     ) AS total_registro_ocr  
    ON  total_registro_ocr.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT vs.hora,  
             'total_ad_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
             vs.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim, @Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56732,60503,74550,74630,74710)  
          ) ad  
      PIVOT  (  
         SUM(infracoes_registradas)  
         FOR enquadramento IN ([total_ad_56732],[total_ad_60503],[total_ad_74550],[total_ad_74630],[total_ad_74710])  
          ) contagem_ad  
     ) AS totais  
    ON  totais.hora = horarios.hora  
  
)  
  
