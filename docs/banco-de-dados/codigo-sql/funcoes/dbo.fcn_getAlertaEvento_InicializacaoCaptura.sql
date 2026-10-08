CREATE FUNCTION [dbo].[fcn_getAlertaEvento_InicializacaoCaptura] (@dataInicio DateTime, @mensagem bit = 0)      
RETURNS TABLE      
AS      
RETURN      
(  -- DECLARE @dataInicio DateTime = '2020-04-09 00:00:00'    
 SELECT       
  *,      
  COUNT(*) as Total      
 FROM (     
 -- DECLARE @dataInicio DateTime = '2020-04-09 00:00:00'    
 SELECT       
    e.proprietario,  e.mensagem    
   FROM      
    eventos_csx_pesquisa e (nolock)      
   WHERE e.id_evento = 2      
    and e.data_hora >= @dataInicio      
 and e.mensagem like 'Consilux Captura%'    
  ) as t      
 GROUP BY t.proprietario,t.mensagem      
)      
      
