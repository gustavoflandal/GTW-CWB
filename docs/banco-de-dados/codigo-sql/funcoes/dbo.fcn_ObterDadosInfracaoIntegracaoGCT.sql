
CREATE FUNCTION [dbo].[fcn_ObterDadosInfracaoIntegracaoGCT](@id_infracao INT)      
RETURNS TABLE      
AS      
RETURN      
(      
    
-- DECLARE @id_infracao INT = 2463    
SELECT     
i.id_enquadramento,    
i.id_local,    
i.data_afericao,    
i.data,    
i.pista,    
cea.selagem,    
ce.serie_equipamento,    
COALESCE(v.placa,'') AS placa,    
i.id_imagem_local,    
    
CASE WHEN i.id_enquadramento = 56732 THEN     
ROUND(i.segundos_tolerancia + 1 + i.tempo_vermelho_detec,2)     
ELSE     
v.segundos    
END AS tempo,    
COALESCE(i.segundos_tolerancia,0) AS segundos_tolerancia,    
i.tempo_vermelho_detec,    
    
i.velocidade_considerada,    
v.velocidade,    
i.velocidade_limite    
    
FROM infracao i (NOLOCK)    
JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo    
JOIN local lv (NOLOCK) ON i.id_local = lv.id_local     
AND i.sequencia_local = lv.sequencia_local    
JOIN configuracao_equipamento ce (NOLOCK)     
ON lv.id_configuracao_equipamento = ce.id_configuracao_equipamento    
JOIN configuracao_equipamento_afericao cea (NOLOCK)     
ON lv.id_configuracao_equipamento = cea.id_configuracao_equipamento    
AND i.pista = cea.id_pista    
    
WHERE i.id_infracao = @id_infracao    
    
);
