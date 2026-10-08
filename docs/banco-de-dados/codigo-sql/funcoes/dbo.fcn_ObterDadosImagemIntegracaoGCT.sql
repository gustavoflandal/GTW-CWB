
    
CREATE FUNCTION [dbo].[fcn_ObterDadosImagemIntegracaoGCT](@id_infracao INT)      
RETURNS TABLE      
AS      
RETURN      
(      
-- DECLARE @id_infracao INT = 2463    
SELECT TOP(10)  
img.assinatura_digital, img.imagem, ii.id_tipo_imagem, ti.nome , img.imagem_inmetro  
FROM infracao i (NOLOCK)  
JOIN veiculo_imagem vi (NOLOCK) ON i.id_veiculo = vi.id_veiculo  
JOIN imagem_info ii (NOLOCK) ON vi.id_imagem = ii.id_imagem  
JOIN tipo_imagem ti (NOLOCK) ON ii.id_tipo_imagem = ti.id_tipo_imagem  
JOIN imagem img (NOLOCK) ON ii.id_imagem = img.id_imagem  
WHERE i.id_infracao = @id_infracao  
ORDER BY ii.id_tipo_imagem  
);  
  
