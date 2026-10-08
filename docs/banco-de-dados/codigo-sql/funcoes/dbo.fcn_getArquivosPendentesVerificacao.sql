
CREATE FUNCTION [dbo].[fcn_getArquivosPendentesVerificacao]()          
RETURNS TABLE          
AS          
RETURN          
(          
SELECT isi.nome_arquivo AS nome_arquivo_origem, inf.diretorio, inf.tamanho_bytes, inf.verificacao FROM integracao_sequencia_imagem_gct isi (NOLOCK)    
JOIN integracao_imagem_gct_info inf (NOLOCK) ON isi.nome_arquivo = inf.nome_arquivo    
WHERE     
inf.verificado = 0   OR inf.erro_verificacao = 1  
    
);        
