
CREATE FUNCTION [dbo].[fcn_ObterDadosMiniaturaImagemIntegracaoGCT](@id_infracao INT)        
RETURNS TABLE        
AS        
RETURN        
(        
-- DECLARE @id_infracao INT = 55      
SELECT TOP(0) null id_imagem_principal, null id_imagem_miniatura, null id_posicao, null id_tamanho   
); 
