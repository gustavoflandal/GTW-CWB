
CREATE FUNCTION [dbo].[fcn_getArquivosPendentes](@id_local INT)            
RETURNS TABLE            
AS            
RETURN            
(            
-- DECLARE @id_local INT    = 1      
SELECT isi.nome_arquivo AS nome_arquivo_origem FROM integracao_sequencia_imagem_gct isi (NOLOCK)      
JOIN integracao_imagem_gct_info inf (NOLOCK) ON isi.nome_arquivo = inf.nome_arquivo      
WHERE       
isi.id_local = CASE WHEN @id_local IS NULL THEN isi.id_local ELSE @id_local END AND      
--inf.verificado = 1 AND   
inf.erro_verificacao = 1       
      
);          
