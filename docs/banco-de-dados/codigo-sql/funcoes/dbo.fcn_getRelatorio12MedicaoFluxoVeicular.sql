

CREATE FUNCTION [dbo].[fcn_getRelatorio12MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)   
RETURNS TABLE   
AS   
RETURN   
(    
   
 --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
 SELECT CONVERT(VARCHAR(10), datas.Data, 103) AS dia,  
     DATEPART(WEEKDAY, datas.Data) AS dia_semana,  
     CASE WHEN DATEPART(WEEKDAY, datas.Data) = 1 THEN 'Domingo'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 2 THEN 'Segunda'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 3 THEN 'Terça'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 4 THEN 'Quarta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 5 THEN 'Quinta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 6 THEN 'Sexta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 7 THEN 'Sábado'  
     END AS dia_semana_desc,  
  
     fluxo.fluxo_veicular,  
     ocr.validos_ocr,  
  
     autos_detectados.[total_detectados],  
     autos_detectados.[ad_56732],autos_detectados.[ad_60503],
	 autos_detectados.[ad_74550],autos_detectados.[ad_74630],autos_detectados.[ad_74710],  
       
     autos_validos.[total_validos],
	 autos_validos.[av_56732],autos_validos.[av_60503],  
     autos_validos.[av_74550],autos_validos.[av_74630],autos_validos.[av_74710]
	   
 FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) AS datas  
     LEFT JOIN (  
       --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
       SELECT vs.dia,  
           SUM(vs.veiculos_detectados) AS fluxo_veicular  
       FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vs  
       WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
       GROUP BY  
           vs.dia  
     ) AS fluxo  
    ON  fluxo.dia = datas.Data  
     LEFT JOIN (  
       --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
       SELECT CAST(v.data AS DATE) AS dia,  
           COUNT(*) AS validos_ocr  
       FROM   veiculo v (NOLOCK)  
           LEFT JOIN infracao i (NOLOCK)  
          ON  i.id_veiculo = v.id_veiculo  
       WHERE  v.data BETWEEN @Data_Ini AND @Data_Fim  
           AND v.id_local = @Id_Local  
           AND v.pista = CASE WHEN @Id_Pista IS NULL THEN v.pista ELSE @Id_Pista END  
           AND v.placa = i.placa  
       GROUP BY  
           CAST(v.data AS DATE)  
     ) AS ocr  
    ON  ocr.dia = datas.Data  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT vs.dia,  
             'ad_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
             vs.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim,@Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56732,60503,74550,74630,74710) 
         UNION ALL  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT vs.dia,  
             'total_detectados' AS enquadramento,  
             vs.infracoes_registradas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim,@Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56732,60503,74550,74630,74710) 
          ) ad  
      PIVOT  (  
         SUM(infracoes_registradas)  
         FOR enquadramento IN ([total_detectados],[ad_56732],[ad_60503],[ad_74550],[ad_74630],[ad_74710])  
          ) contagem_ad  
     ) AS autos_detectados  
    ON  autos_detectados.dia = datas.Data  
     LEFT JOIN (  
      --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
      SELECT *  
      FROM   (  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT vs.dia,  
             'av_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
             vs.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim,@Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56732,60503,74550,74630,74710) 
         UNION ALL  
         --DECLARE @Data_Ini DATETIME = '2017-07-01 00:00:00.000', @Data_Fim DATETIME = '2017-07-31 23:59:59.000', @Id_Local INT = 9002, @Id_Pista INT = NULL  
         SELECT vs.dia,  
             'total_validos' AS enquadramento,  
             vs.infracoes_validas  
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini,@Data_Fim,@Id_Local) vs  
         WHERE  vs.id_pista = CASE WHEN @Id_Pista IS NULL THEN vs.id_pista ELSE @Id_Pista END  
             AND vs.id_enquadramento IN (56732,60503,74550,74630,74710)  
          ) av  
      PIVOT  (  
         SUM(infracoes_validas)  
         FOR enquadramento IN ([total_validos],[av_56732],[av_60503],[av_74550],[av_74630],[av_74710])  
          ) contagem_av  
     ) AS autos_validos  
    ON  autos_validos.dia = datas.Data  
)  
  
