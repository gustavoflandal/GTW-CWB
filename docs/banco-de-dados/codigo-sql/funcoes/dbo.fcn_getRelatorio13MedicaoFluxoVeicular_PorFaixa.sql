
CREATE FUNCTION [dbo].[fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa](@Data_Ini DATETIME, @Data_Fim DATETIME)  
RETURNS TABLE  
AS  
RETURN  
(     
    
 --DECLARE @Data_Ini DATETIME = '2019-05-01 00:00:00.000', @Data_Fim DATETIME = '2019-05-31 23:59:59.000'  
 SELECT dados_local.id_local,  
        dados_local.serie_equipamento,  
  dados_local.nome_pista_sentido,  
  dados_local.id_pista,  
  dados_local.faixa,  
  dados_local.nome_pista_sentido_faixa,  
  dados_local.posicao_lat AS latitude,  
  dados_local.posicao_lon AS longitude,  
  dados_local.codigo_equipamento,  
    
        fluxo.fluxo_veicular,  
    
  (SELECT SUM(c)  
   FROM (VALUES(autos_detectados.[ad_56732]),(autos_detectados.[ad_60503]),(autos_detectados.[ad_74550]),  
      (autos_detectados.[ad_74630]),(autos_detectados.[ad_74710])) T (c)) AS [total_detectados],  
  autos_detectados.[ad_56732],autos_detectados.[ad_60503],autos_detectados.[ad_74550],  
  autos_detectados.[ad_74630],autos_detectados.[ad_74710],  
         
     (SELECT SUM(c)  
   FROM (VALUES(autos_validos.[av_56732]),(autos_validos.[av_60503]),(autos_validos.[av_74550]),  
      (autos_validos.[av_74630]),(autos_validos.[av_74710])) T (c)) AS [total_validos],  
        autos_validos.[av_56732],autos_validos.[av_60503],  
  autos_validos.[av_74550],autos_validos.[av_74630],autos_validos.[av_74710]
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
       --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000'    
       SELECT vs.id_local,  
        vs.id_pista,  
        SUM(vs.veiculos_detectados) AS fluxo_veicular    
       FROM   dbo.fcn_getVeiculoSumarizadoRelatorio(@Data_Ini, @Data_Fim) vs    
       GROUP BY  
        vs.id_local,  
        vs.id_pista    
       ) AS fluxo    
   ON  fluxo.id_local = dados_local.id_local    
    AND fluxo.id_pista = dados_local.id_pista    
       LEFT JOIN (    
       --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000'    
       SELECT *    
       FROM   (    
         --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000'    
         SELECT vs.id_local,  
             vs.id_pista,  
          'ad_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
          vs.infracoes_registradas    
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioEnquadramento(@Data_Ini,@Data_Fim) vs    
         WHERE  vs.id_enquadramento IN (56732,60503,74550,74630,74710)    
           ) ad    
       PIVOT  (  
        SUM(infracoes_registradas)    
        FOR enquadramento IN ([ad_56732],[ad_60503],[ad_74550],[ad_74630],[ad_74710])    
           ) contagem_ad    
       ) AS autos_detectados    
   ON  autos_detectados.id_local = dados_local.id_local    
    AND autos_detectados.id_pista = dados_local.id_pista    
       LEFT JOIN (    
       --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000'    
       SELECT *    
       FROM   (    
         --DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000'    
         SELECT vs.id_local,  
          vs.id_pista,  
          'av_' + CAST(vs.id_enquadramento AS VARCHAR(10)) AS enquadramento,  
          vs.infracoes_validas    
         FROM   dbo.fcn_getVeiculoSumarizadoRelatorioEnquadramento(@Data_Ini,@Data_Fim) vs    
         WHERE  vs.id_enquadramento IN (56732,60503,74550,74630,74710)  
           ) av    
       PIVOT  (  
        SUM(infracoes_validas)    
        FOR enquadramento IN ([av_56732],[av_60503],[av_74550],[av_74630],[av_74710])    
           ) contagem_av    
       ) AS autos_validos    
   ON  autos_validos.id_local = dados_local.id_local    
    AND autos_validos.id_pista = dados_local.id_pista    
    
	--WHERE dados_local.id_local = 36

)  

