CREATE FUNCTION [dbo].[fcn_getVeiculoSumarizadoRelatorioEnquadramento](@dataIni DATETIME, @dataFim DATETIME)  
RETURNS TABLE AS RETURN (  
--DECLARE @dataIni DATETIME = '2018-01-01 00:00:00.000', @dataFim DATETIME = '2018-01-31 23:59:59.000'  
SELECT   
i.id_local,  
CAST(i.data AS DATE) AS dia,  
DATEPART(HOUR,i.data) AS hora,  
i.pista AS id_pista,  
SUM(CASE WHEN i.id_enquadramento > 1 THEN 1 ELSE 0 END) AS infracoes_registradas,  
SUM(CASE WHEN i.id_enquadramento > 1 AND i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS infracoes_validas,  
i.id_enquadramento,  
i.tempo_vermelho_detec  
  
FROM infracao i (NOLOCK)  
  
WHERE i.data BETWEEN @dataIni AND @dataFim  
   AND i.id_processo <> 99  
  
GROUP BY   
i.id_local,CAST(i.data AS DATE),DATEPART(HOUR,i.data),i.pista,i.id_enquadramento,i.tempo_vermelho_detec  
  
)  
