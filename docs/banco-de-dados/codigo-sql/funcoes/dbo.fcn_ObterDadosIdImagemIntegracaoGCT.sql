
CREATE FUNCTION [dbo].[fcn_ObterDadosIdImagemIntegracaoGCT](@id_infracao INT)        
RETURNS TABLE        
AS        
RETURN        
(        
-- DECLARE @id_infracao INT = 74      
SELECT id_imagem_obj FROM infracao_imagem (NOLOCK) WHERE id_infracao = @id_infracao    
); 
