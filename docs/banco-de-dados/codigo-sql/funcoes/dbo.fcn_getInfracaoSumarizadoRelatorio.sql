CREATE FUNCTION [dbo].[fcn_getInfracaoSumarizadoRelatorio](@dataIni DATETIME, @dataFim DATETIME)  
RETURNS TABLE  
AS  
RETURN (  
  
 --DECLARE @dataIni DATETIME = '2018-12-01 00:00:00.000', @dataFim DATETIME = '2018-12-31 23:59:59.000'  
 SELECT vp.id_local,  
     CAST(vp.data AS DATE) AS dia,  
     DATEPART(HOUR,vp.data) AS hora,  
     vp.pista AS id_pista,  
     AVG(CASE WHEN vp.velocidade BETWEEN 5 AND 200 THEN vp.velocidade ELSE NULL END) AS velocidade_media,  
     MAX(CASE WHEN vp.velocidade BETWEEN 5 AND 200 THEN vp.velocidade ELSE NULL END) AS velocidade_maxima,  
     COUNT(vp.id_veiculo_unic) AS veiculos_detectados,  
     SUM(CASE WHEN i.id_infracao IS NOT NULL AND i.id_enquadramento > 1 THEN 1 ELSE 0 END) AS infracoes_registradas,  
     SUM(CASE WHEN i.id_infracao IS NOT NULL AND i.id_enquadramento > 1 AND i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS infracoes_validas,  
     SUM(CASE WHEN i.id_enquadramento > 1 AND i.id_inconsistencia > 0 AND inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS invalidas_tecnicos,  
     SUM(CASE WHEN i.id_enquadramento > 1 AND i.id_inconsistencia > 0 AND inc.razao_tecnica < 2 THEN 1 ELSE 0 END) AS invalidas_nao_tecnicos  
  
 FROM   infracao i (NOLOCK)   
     JOIN veiculo vp (NOLOCK)  
    ON  i.id_veiculo = vp.id_veiculo  
     JOIN inconsistencia inc (NOLOCK)  
    ON  inc.id_inconsistencia = i.id_inconsistencia  
  
 WHERE  vp.data BETWEEN @dataIni AND @dataFim  
     AND i.id_processo NOT IN (99,98)  
  
 GROUP BY   
     vp.id_local,  
     CAST(vp.data AS DATE),  
     DATEPART(HOUR,vp.data),  
     vp.pista  
  
)  
