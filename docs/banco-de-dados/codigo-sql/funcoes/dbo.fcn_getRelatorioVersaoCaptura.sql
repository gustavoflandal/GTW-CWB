
CREATE FUNCTION [dbo].[fcn_getRelatorioVersaoCaptura](@dataInicio DATE, @dataFim DATE)    
RETURNS TABLE    
AS    
RETURN    
(    
  
SELECT lv.serie_equipamento, ev.data_hora, ev.mensagem FROM eventos_csx ev (NOLOCK)   
JOIN eventos_csx_desc_proprietario evp (NOLOCK)  
ON ev.id_proprietario = evp.id_proprietario  
JOIN local_vigente lv (NOLOCK) ON ISNUMERIC(evp.proprietario) = 1 AND evp.proprietario = lv.serie_equipamento  
WHERE   
ev.id IN   
(  
SELECT MAX(ev.id) FROM eventos_csx ev (NOLOCK)  
WHERE   
ev.id_evento = 2  
AND ev.mensagem LIKE 'Consilux Captura%'  
GROUP BY ev.id_proprietario  
)  
--AND   
--lv.desativado = 0  
--AND   
--lv.data_inicio < GETDATE()  
  
)
