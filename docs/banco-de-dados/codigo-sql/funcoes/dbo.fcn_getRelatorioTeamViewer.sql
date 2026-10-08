
CREATE FUNCTION [dbo].[fcn_getRelatorioTeamViewer](@dataInicio DATE, @dataFim DATE)      
RETURNS TABLE      
AS      
RETURN      
(      
SELECT evp.proprietario AS [Série Equipamento], SUBSTRING(ec.mensagem,16,12) AS [ID TeamViewer] FROM eventos_csx ec (NOLOCK)    
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ec.id_proprietario = evp.id_proprietario    
JOIN     
(    
SELECT MAX(id) AS id FROM eventos_csx (NOLOCK)    
WHERE id_evento IN ( 2, 52 ) AND mensagem LIKE 'ID TeamViewer:%'    
GROUP BY id_proprietario    
) AS sub1    
ON ec.id = sub1.id    
)    
    
