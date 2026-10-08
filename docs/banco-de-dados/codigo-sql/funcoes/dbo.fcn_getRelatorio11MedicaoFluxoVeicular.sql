

CREATE FUNCTION [dbo].[fcn_getRelatorio11MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)   
RETURNS TABLE   
AS   
RETURN   
(    
   
 --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
 SELECT horarios.hora,   
     horarios.hora_desc,  
  
     autos_validos_56732.[1]  AS av_56732_1,  autos_validos_56732.[2]  AS av_56732_2,  autos_validos_56732.[3]  AS av_56732_3,  autos_validos_56732.[4]  AS av_56732_4,  
     autos_validos_56732.[5]  AS av_56732_5,  autos_validos_56732.[6]  AS av_56732_6,  autos_validos_56732.[7]  AS av_56732_7,  autos_validos_56732.[8]  AS av_56732_8,  
     autos_validos_56732.[9]  AS av_56732_9,  autos_validos_56732.[10] AS av_56732_10, autos_validos_56732.[11] AS av_56732_11, autos_validos_56732.[12] AS av_56732_12,  
     autos_validos_56732.[13] AS av_56732_13, autos_validos_56732.[14] AS av_56732_14, autos_validos_56732.[15] AS av_56732_15, autos_validos_56732.[16] AS av_56732_16,  
     autos_validos_56732.[17] AS av_56732_17, autos_validos_56732.[18] AS av_56732_18, autos_validos_56732.[19] AS av_56732_19, autos_validos_56732.[20] AS av_56732_20,  
     autos_validos_56732.[21] AS av_56732_21, autos_validos_56732.[22] AS av_56732_22, autos_validos_56732.[23] AS av_56732_23, autos_validos_56732.[24] AS av_56732_24,  
     autos_validos_56732.[25] AS av_56732_25, autos_validos_56732.[26] AS av_56732_26, autos_validos_56732.[27] AS av_56732_27, autos_validos_56732.[28] AS av_56732_28,  
     autos_validos_56732.[29] AS av_56732_29, autos_validos_56732.[30] AS av_56732_30, autos_validos_56732.[31] AS av_56732_31,  
  
     autos_validos_60503.[1]  AS av_60503_1,  autos_validos_60503.[2]  AS av_60503_2,  autos_validos_60503.[3]  AS av_60503_3,  autos_validos_60503.[4]  AS av_60503_4,  
     autos_validos_60503.[5]  AS av_60503_5,  autos_validos_60503.[6]  AS av_60503_6,  autos_validos_60503.[7]  AS av_60503_7,  autos_validos_60503.[8]  AS av_60503_8,  
     autos_validos_60503.[9]  AS av_60503_9,  autos_validos_60503.[10] AS av_60503_10, autos_validos_60503.[11] AS av_60503_11, autos_validos_60503.[12] AS av_60503_12,  
     autos_validos_60503.[13] AS av_60503_13, autos_validos_60503.[14] AS av_60503_14, autos_validos_60503.[15] AS av_60503_15, autos_validos_60503.[16] AS av_60503_16,  
     autos_validos_60503.[17] AS av_60503_17, autos_validos_60503.[18] AS av_60503_18, autos_validos_60503.[19] AS av_60503_19, autos_validos_60503.[20] AS av_60503_20,  
     autos_validos_60503.[21] AS av_60503_21, autos_validos_60503.[22] AS av_60503_22, autos_validos_60503.[23] AS av_60503_23, autos_validos_60503.[24] AS av_60503_24,  
     autos_validos_60503.[25] AS av_60503_25, autos_validos_60503.[26] AS av_60503_26, autos_validos_60503.[27] AS av_60503_27, autos_validos_60503.[28] AS av_60503_28,  
     autos_validos_60503.[29] AS av_60503_29, autos_validos_60503.[30] AS av_60503_30, autos_validos_60503.[31] AS av_60503_31,  
  
     autos_validos_74550.[1] AS av_74550_1, autos_validos_74550.[2] AS av_74550_2, autos_validos_74550.[3] AS av_74550_3, autos_validos_74550.[4] AS av_74550_4,  
     autos_validos_74550.[5] AS av_74550_5, autos_validos_74550.[6] AS av_74550_6, autos_validos_74550.[7] AS av_74550_7, autos_validos_74550.[8] AS av_74550_8,  
     autos_validos_74550.[9] AS av_74550_9, autos_validos_74550.[10] AS av_74550_10, autos_validos_74550.[11] AS av_74550_11, autos_validos_74550.[12] AS av_74550_12,  
     autos_validos_74550.[13] AS av_74550_13, autos_validos_74550.[14] AS av_74550_14, autos_validos_74550.[15] AS av_74550_15, autos_validos_74550.[16] AS av_74550_16,  
     autos_validos_74550.[17] AS av_74550_17, autos_validos_74550.[18] AS av_74550_18, autos_validos_74550.[19] AS av_74550_19, autos_validos_74550.[20] AS av_74550_20,  
     autos_validos_74550.[21] AS av_74550_21, autos_validos_74550.[22] AS av_74550_22, autos_validos_74550.[23] AS av_74550_23, autos_validos_74550.[24] AS av_74550_24,  
     autos_validos_74550.[25] AS av_74550_25, autos_validos_74550.[26] AS av_74550_26, autos_validos_74550.[27] AS av_74550_27, autos_validos_74550.[28] AS av_74550_28,  
     autos_validos_74550.[29] AS av_74550_29, autos_validos_74550.[30] AS av_74550_30, autos_validos_74550.[31] AS av_74550_31,  
  
     autos_validos_74630.[1] AS av_74630_1, autos_validos_74630.[2] AS av_74630_2, autos_validos_74630.[3] AS av_74630_3, autos_validos_74630.[4] AS av_74630_4,  
     autos_validos_74630.[5] AS av_74630_5, autos_validos_74630.[6] AS av_74630_6, autos_validos_74630.[7] AS av_74630_7, autos_validos_74630.[8] AS av_74630_8,  
     autos_validos_74630.[9] AS av_74630_9, autos_validos_74630.[10] AS av_74630_10, autos_validos_74630.[11] AS av_74630_11, autos_validos_74630.[12] AS av_74630_12,  
     autos_validos_74630.[13] AS av_74630_13, autos_validos_74630.[14] AS av_74630_14, autos_validos_74630.[15] AS av_74630_15, autos_validos_74630.[16] AS av_74630_16,  
     autos_validos_74630.[17] AS av_74630_17, autos_validos_74630.[18] AS av_74630_18, autos_validos_74630.[19] AS av_74630_19, autos_validos_74630.[20] AS av_74630_20,  
     autos_validos_74630.[21] AS av_74630_21, autos_validos_74630.[22] AS av_74630_22, autos_validos_74630.[23] AS av_74630_23, autos_validos_74630.[24] AS av_74630_24,  
     autos_validos_74630.[25] AS av_74630_25, autos_validos_74630.[26] AS av_74630_26, autos_validos_74630.[27] AS av_74630_27, autos_validos_74630.[28] AS av_74630_28,  
     autos_validos_74630.[29] AS av_74630_29, autos_validos_74630.[30] AS av_74630_30, autos_validos_74630.[31] AS av_74630_31,  
  
     autos_validos_74710.[1] AS av_74710_1, autos_validos_74710.[2] AS av_74710_2, autos_validos_74710.[3] AS av_74710_3, autos_validos_74710.[4] AS av_74710_4,  
     autos_validos_74710.[5] AS av_74710_5, autos_validos_74710.[6] AS av_74710_6, autos_validos_74710.[7] AS av_74710_7, autos_validos_74710.[8] AS av_74710_8,  
     autos_validos_74710.[9] AS av_74710_9, autos_validos_74710.[10] AS av_74710_10, autos_validos_74710.[11] AS av_74710_11, autos_validos_74710.[12] AS av_74710_12,  
     autos_validos_74710.[13] AS av_74710_13, autos_validos_74710.[14] AS av_74710_14, autos_validos_74710.[15] AS av_74710_15, autos_validos_74710.[16] AS av_74710_16,  
     autos_validos_74710.[17] AS av_74710_17, autos_validos_74710.[18] AS av_74710_18, autos_validos_74710.[19] AS av_74710_19, autos_validos_74710.[20] AS av_74710_20,  
     autos_validos_74710.[21] AS av_74710_21, autos_validos_74710.[22] AS av_74710_22, autos_validos_74710.[23] AS av_74710_23, autos_validos_74710.[24] AS av_74710_24,  
     autos_validos_74710.[25] AS av_74710_25, autos_validos_74710.[26] AS av_74710_26, autos_validos_74710.[27] AS av_74710_27, autos_validos_74710.[28] AS av_74710_28,  
     autos_validos_74710.[29] AS av_74710_29, autos_validos_74710.[30] AS av_74710_30, autos_validos_74710.[31] AS av_74710_31,  
  
     ISNULL(totais.[total_av_56732], 0) AS total_av_56732, ISNULL(totais.[total_av_60503], 0) AS total_av_60503,  
     ISNULL(totais.[total_av_74550], 0) AS total_av_74550, ISNULL(totais.[total_av_74630], 0) AS total_av_74630,  
     ISNULL(totais.[total_av_74710], 0) AS total_av_74710

 FROM   hora AS horarios  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 56732  
          ) av_56810  
      PIVOT  
         (  
        SUM(infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av_56732 
     ) AS autos_validos_56732  
    ON  autos_validos_56732.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 60503  
          ) av_60503  
      PIVOT  
         (  
        SUM(infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av_60503  
     ) AS autos_validos_60503  
    ON  autos_validos_60503.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74550  
          ) av_74550  
      PIVOT  
         (  
        SUM(infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av_74550  
     ) AS autos_validos_74550  
    ON  autos_validos_74550.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74630  
          ) av_74630  
      PIVOT  
         (  
        SUM(infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av_74630  
     ) AS autos_validos_74630  
    ON  autos_validos_74630.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
             AND vp.id_enquadramento = 74710  
          ) av_74710  
      PIVOT  
         (  
        SUM(infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av_74710  
     ) AS autos_validos_74710  
    ON  autos_validos_74710.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9001, @Id_Pista INT = NULL  
         SELECT vs.hora,  
             'total_av_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
             vs.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim, @Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56810,56900,57030,57461,74550,74630,74710,60501,56731,60411,60412,59911,75870)  
          ) av  
      PIVOT  (  
         SUM(infracoes_validas)  
         FOR enquadramento IN ([total_av_56732],[total_av_60503],[total_av_74550],[total_av_74630],[total_av_74710])  
          ) contagem_av  
     ) AS totais  
    ON  totais.hora = horarios.hora  
)  
  
