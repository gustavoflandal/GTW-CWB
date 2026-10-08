
CREATE FUNCTION [dbo].[fcn_getRelatorioInicializacoesCaptura](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  

SELECT evp.proprietario, ev.data_hora, 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,46,LEN(ev.mensagem)-45)) = 1 
THEN DATEADD(MILLISECOND, CAST(SUBSTRING(ev.mensagem,46,LEN(ev.mensagem)-45) AS BIGINT) * -1, ev.data_hora) 
ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,51,LEN(ev.mensagem)-50)) = 1 
THEN DATEADD(MILLISECOND, CAST(SUBSTRING(ev.mensagem,51,LEN(ev.mensagem)-50) AS BIGINT) * -1, ev.data_hora)
ELSE NULL
END END AS data_reinicio,
ev.mensagem
FROM eventos_csx ev (NOLOCK)
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ev.id_proprietario = evp.id_proprietario
WHERE 
ev.id_evento = 2	--Captura esta sendo iniciado...
AND 
CAST(ev.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim
);

