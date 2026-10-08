

CREATE FUNCTION [dbo].[fcn_getRelatorio13MedicaoFluxoVeicular_PorLocal](@Data_Ini DATETIME, @Data_Fim DATETIME)  
RETURNS TABLE  
AS  
RETURN  
(   
  
 --DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'  
 SELECT rel.id_local,  
     rel.serie_equipamento,  
     rel.nome_pista_sentido,  
     rel.latitude,  
     rel.longitude,  
     STUFF((SELECT ', ' + RTRIM(CAST(sub.codigo_equipamento AS VARCHAR(10))) AS [text()]  
      FROM   local_pista_vigente sub  
      WHERE  sub.id_local = rel.id_local  
      FOR XML PATH('')  
     ), 1, 1, '' ) AS codigos_equipamentos,  
  
     SUM(rel.fluxo_veicular) AS fluxo_veicular,  
  
     SUM(rel.[total_detectados]) AS [total_detectados],  
     SUM(rel.[ad_56732]) AS [ad_56732],  
     SUM(rel.[ad_60503]) AS [ad_60503],  
     SUM(rel.[ad_74550]) AS [ad_74550],  
     SUM(rel.[ad_74630]) AS [ad_74630],  
     SUM(rel.[ad_74710]) AS [ad_74710],  
       
     SUM(rel.[total_validos]) AS [total_validos],  
     SUM(rel.[av_56732]) AS [av_56732],  
     SUM(rel.[av_60503]) AS [av_60503],  
     SUM(rel.[av_74550]) AS [av_74550],  
     SUM(rel.[av_74630]) AS [av_74630],  
     SUM(rel.[av_74710]) AS [av_74710]
 FROM   dbo.fcn_getRelatorio13MedicaoFluxoVeicular_PorFaixa(@Data_Ini, @Data_fim) rel  
 GROUP BY  
     rel.id_local,  
     rel.serie_equipamento,  
     rel.nome_pista_sentido,  
     rel.latitude,  
     rel.longitude  
)  

