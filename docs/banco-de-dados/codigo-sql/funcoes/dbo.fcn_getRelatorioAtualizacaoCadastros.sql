
CREATE FUNCTION [dbo].[fcn_getRelatorioAtualizacaoCadastros](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  

SELECT evp.proprietario AS [N.o Serie Equipamento], MAX(ev.data_hora) AS [Atualizado Em] FROM eventos_csx ev (NOLOCK)
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ev.id_proprietario = evp.id_proprietario
WHERE id_evento = 54
GROUP BY evp.proprietario

);
