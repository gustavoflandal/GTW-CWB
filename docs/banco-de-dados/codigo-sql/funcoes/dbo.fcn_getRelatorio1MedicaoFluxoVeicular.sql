
CREATE FUNCTION [dbo].[fcn_getRelatorio1MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)   
RETURNS TABLE   
AS   
RETURN   
(    
   
 --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
 SELECT horarios.hora,   
     horarios.hora_desc,  
     fluxo.[1] AS fv_1,   vel_media.[1] AS vm_1,   vel_max.[1] AS vmax_1,   autos_detec.[1] AS ad_1,   autos_validos.[1] AS av_1,  
     fluxo.[2] AS fv_2,   vel_media.[2] AS vm_2,   vel_max.[2] AS vmax_2,   autos_detec.[2] AS ad_2,   autos_validos.[2] AS av_2,  
     fluxo.[3] AS fv_3,   vel_media.[3] AS vm_3,   vel_max.[3] AS vmax_3,   autos_detec.[3] AS ad_3,   autos_validos.[3] AS av_3,  
     fluxo.[4] AS fv_4,   vel_media.[4] AS vm_4,   vel_max.[4] AS vmax_4,   autos_detec.[4] AS ad_4,   autos_validos.[4] AS av_4,  
     fluxo.[5] AS fv_5,   vel_media.[5] AS vm_5,   vel_max.[5] AS vmax_5,   autos_detec.[5] AS ad_5,   autos_validos.[5] AS av_5,  
     fluxo.[6] AS fv_6,   vel_media.[6] AS vm_6,   vel_max.[6] AS vmax_6,   autos_detec.[6] AS ad_6,   autos_validos.[6] AS av_6,  
     fluxo.[7] AS fv_7,   vel_media.[7] AS vm_7,   vel_max.[7] AS vmax_7,   autos_detec.[7] AS ad_7,   autos_validos.[7] AS av_7,  
     fluxo.[8] AS fv_8,   vel_media.[8] AS vm_8,   vel_max.[8] AS vmax_8,   autos_detec.[8] AS ad_8,   autos_validos.[8] AS av_8,  
     fluxo.[9] AS fv_9,   vel_media.[9] AS vm_9,   vel_max.[9] AS vmax_9,   autos_detec.[9] AS ad_9,   autos_validos.[9] AS av_9,  
     fluxo.[10] AS fv_10, vel_media.[10] AS vm_10, vel_max.[10] AS vmax_10, autos_detec.[10] AS ad_10, autos_validos.[10] AS av_10,  
     fluxo.[11] AS fv_11, vel_media.[11] AS vm_11, vel_max.[11] AS vmax_11, autos_detec.[11] AS ad_11, autos_validos.[11] AS av_11,  
     fluxo.[12] AS fv_12, vel_media.[12] AS vm_12, vel_max.[12] AS vmax_12, autos_detec.[12] AS ad_12, autos_validos.[12] AS av_12,  
     fluxo.[13] AS fv_13, vel_media.[13] AS vm_13, vel_max.[13] AS vmax_13, autos_detec.[13] AS ad_13, autos_validos.[13] AS av_13,  
     fluxo.[14] AS fv_14, vel_media.[14] AS vm_14, vel_max.[14] AS vmax_14, autos_detec.[14] AS ad_14, autos_validos.[14] AS av_14,  
     fluxo.[15] AS fv_15, vel_media.[15] AS vm_15, vel_max.[15] AS vmax_15, autos_detec.[15] AS ad_15, autos_validos.[15] AS av_15,  
     fluxo.[16] AS fv_16, vel_media.[16] AS vm_16, vel_max.[16] AS vmax_16, autos_detec.[16] AS ad_16, autos_validos.[16] AS av_16,  
     fluxo.[17] AS fv_17, vel_media.[17] AS vm_17, vel_max.[17] AS vmax_17, autos_detec.[17] AS ad_17, autos_validos.[17] AS av_17,  
     fluxo.[18] AS fv_18, vel_media.[18] AS vm_18, vel_max.[18] AS vmax_18, autos_detec.[18] AS ad_18, autos_validos.[18] AS av_18,  
     fluxo.[19] AS fv_19, vel_media.[19] AS vm_19, vel_max.[19] AS vmax_19, autos_detec.[19] AS ad_19, autos_validos.[19] AS av_19,  
     fluxo.[20] AS fv_20, vel_media.[20] AS vm_20, vel_max.[20] AS vmax_20, autos_detec.[20] AS ad_20, autos_validos.[20] AS av_20,  
     fluxo.[21] AS fv_21, vel_media.[21] AS vm_21, vel_max.[21] AS vmax_21, autos_detec.[21] AS ad_21, autos_validos.[21] AS av_21,  
     fluxo.[22] AS fv_22, vel_media.[22] AS vm_22, vel_max.[22] AS vmax_22, autos_detec.[22] AS ad_22, autos_validos.[22] AS av_22,  
     fluxo.[23] AS fv_23, vel_media.[23] AS vm_23, vel_max.[23] AS vmax_23, autos_detec.[23] AS ad_23, autos_validos.[23] AS av_23,  
     fluxo.[24] AS fv_24, vel_media.[24] AS vm_24, vel_max.[24] AS vmax_24, autos_detec.[24] AS ad_24, autos_validos.[24] AS av_24,  
     fluxo.[25] AS fv_25, vel_media.[25] AS vm_25, vel_max.[25] AS vmax_25, autos_detec.[25] AS ad_25, autos_validos.[25] AS av_25,  
     fluxo.[26] AS fv_26, vel_media.[26] AS vm_26, vel_max.[26] AS vmax_26, autos_detec.[26] AS ad_26, autos_validos.[26] AS av_26,  
     fluxo.[27] AS fv_27, vel_media.[27] AS vm_27, vel_max.[27] AS vmax_27, autos_detec.[27] AS ad_27, autos_validos.[27] AS av_27,  
     fluxo.[28] AS fv_28, vel_media.[28] AS vm_28, vel_max.[28] AS vmax_28, autos_detec.[28] AS ad_28, autos_validos.[28] AS av_28,  
     fluxo.[29] AS fv_29, vel_media.[29] AS vm_29, vel_max.[29] AS vmax_29, autos_detec.[29] AS ad_29, autos_validos.[29] AS av_29,  
     fluxo.[30] AS fv_30, vel_media.[30] AS vm_30, vel_max.[30] AS vmax_30, autos_detec.[30] AS ad_30, autos_validos.[30] AS av_30,  
     fluxo.[31] AS fv_31, vel_media.[31] AS vm_31, vel_max.[31] AS vmax_31, autos_detec.[31] AS ad_31, autos_validos.[31] AS av_31,  
  
     ISNULL(totais.fluxo_veicular, 0) AS total_fluxo,  
     ROUND(CAST(ISNULL(totais.velocidade_media, 0) AS FLOAT), 0) AS total_velocidade_media,  
     ROUND(CAST(ISNULL(totais.velocidade_maxima, 0) AS FLOAT), 0) AS total_velocidade_maxima,  
     ISNULL(totais.detectados, 0) AS total_autos_detectados,  
     ISNULL(totais.validos, 0) AS total_autos_validos  
 FROM   hora AS horarios  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.veiculos_detectados  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
        ) fv  
      PIVOT   
        (  
         SUM(fv.veiculos_detectados)  
         FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
        ) contagem_fluxo  
     ) AS fluxo  
    ON  fluxo.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.velocidade_media  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
          ) vm  
      PIVOT  
         (  
        AVG(vm.velocidade_media)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_vm  
     ) AS vel_media  
    ON  vel_media.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.velocidade_maxima  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
          ) vm  
      PIVOT  
         (  
        MAX(vm.velocidade_maxima)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_vm  
     ) AS vel_max  
    ON  vel_max.hora = horarios.hora  
     LEFT JOIN (   
      --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL        
	  SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
          ) ad  
      PIVOT  
         (  
        SUM(ad.infracoes_registradas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_ad  
     ) AS autos_detec  
    ON  autos_detec.hora = horarios.hora  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
         SELECT DATEPART(DAY,vp.dia) AS dia,  
             vp.hora,  
             vp.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
         WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
          ) av  
      PIVOT  
         (  
        SUM(av.infracoes_validas)  
        FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])  
         ) contagem_av  
     ) AS autos_validos  
    ON  autos_validos.hora = horarios.hora  
       LEFT JOIN (  
    --DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL  
    SELECT vp.hora,  
        SUM(vp.veiculos_detectados) AS fluxo_veicular,  
        AVG(vp.velocidade_media) AS velocidade_media,  
        MAX(vp.velocidade_maxima) AS velocidade_maxima,  
        SUM(vp.infracoes_registradas) AS detectados,  
        SUM(vp.infracoes_validas) AS validos  
    FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
    WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
    GROUP BY  
        vp.hora  
     ) AS totais  
    ON  totais.hora = horarios.hora  
  
)  
