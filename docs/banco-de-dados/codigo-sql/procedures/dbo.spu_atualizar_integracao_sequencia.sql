
CREATE PROCEDURE [dbo].[spu_atualizar_integracao_sequencia] AS    
        
UPDATE integracao_sequencia_imagem_gct SET id_infracao_GTW = sub1.id_infracao     
FROM     
(    
SELECT isi.nome_arquivo, isi.id_imagem_local,isi.data_imagem,isi.id_enquadramento,isi.id_local,isi.id_pista,i.id_infracao FROM integracao_sequencia_imagem_gct isi (NOLOCK)    
JOIN infracao i (NOLOCK) ON     
isi.id_imagem_local = i.id_imagem_local AND     
isi.data_imagem = i.data AND     
isi.id_enquadramento = i.id_enquadramento AND     
isi.id_local = i.id_local AND     
isi.id_pista = i.pista    
WHERE isi.id_infracao_GTW IS NULL    
) AS sub1    
WHERE     
integracao_sequencia_imagem_gct.nome_arquivo = sub1.nome_arquivo AND     
integracao_sequencia_imagem_gct.id_imagem_local = sub1.id_imagem_local AND     
integracao_sequencia_imagem_gct.data_imagem = sub1.data_imagem AND     
integracao_sequencia_imagem_gct.id_enquadramento = sub1.id_enquadramento AND     
integracao_sequencia_imagem_gct.id_local = sub1.id_local AND     
integracao_sequencia_imagem_gct.id_pista = sub1.id_pista 
