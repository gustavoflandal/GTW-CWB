CREATE VIEW [dbo].[v_veiculo_sumarizado_relatorio_temp] AS
    
SELECT     
vp.id_local,    
CAST(vp.data AS DATE) AS dia,    
DATEPART(HOUR,vp.data) AS hora,    
vp.pista AS id_pista,
AVG(CASE WHEN vp.velocidade BETWEEN 5 AND 200 THEN vp.velocidade ELSE NULL END) AS velocidade_media,    
MAX(CASE WHEN vp.velocidade BETWEEN 5 AND 200 THEN vp.velocidade ELSE NULL END) AS velocidade_maxima,    
COUNT(vp.id_veiculo_unic) AS veiculos_detectados,    
SUM(CASE WHEN i.id_infracao IS NOT NULL AND i.id_enquadramento > 1 THEN 1 ELSE 0 END) AS infracoes_registradas,    
SUM(CASE WHEN i.id_infracao IS NOT NULL AND i.id_enquadramento > 1 AND i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS infracoes_validas,    
SUM(CASE WHEN vp.placa IS NOT NULL THEN 1 ELSE 0 END) AS placa_lida,    
SUM(CASE WHEN i.id_infracao IS NOT NULL AND i.id_enquadramento = 1 THEN 1 ELSE 0 END) AS imagens_teste_registradas
    
FROM veiculo_pesquisa_sumariza_temp vp (NOLOCK)    
LEFT JOIN infracao i (NOLOCK) ON vp.id_veiculo = i.id_veiculo  --AND i.id_processo NOT IN (99,98)
    
GROUP BY     
vp.id_local,CAST(vp.data AS DATE),DATEPART(HOUR,vp.data),vp.pista
