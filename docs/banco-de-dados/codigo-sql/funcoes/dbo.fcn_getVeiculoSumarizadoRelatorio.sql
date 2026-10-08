 
CREATE FUNCTION [dbo].[fcn_getVeiculoSumarizadoRelatorio](@dataIni DATETIME, @dataFim DATETIME) RETURNS TABLE AS RETURN (  
  
SELECT   
id_local,  
dia,  
hora,  
id_pista,  
velocidade_media,  
velocidade_maxima,  
veiculos_detectados,  
infracoes_registradas,  
infracoes_validas  
  
FROM veiculo_sumarizado_relatorio vp (NOLOCK)  
  
WHERE vp.dia BETWEEN @dataIni AND @dataFim  
  
)  
