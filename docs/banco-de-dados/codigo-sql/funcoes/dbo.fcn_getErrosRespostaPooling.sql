
CREATE FUNCTION [dbo].[fcn_getErrosRespostaPooling] (@dataInicio DateTime)
RETURNS TABLE
AS
RETURN 
(
	SELECT 
		e.proprietario, 
		case 
			when e.evento is null 
				then ' ' 
			else 
				e.evento 
		end as evento,
		mensagem,
		COUNT(*) as Total
	FROM
		eventos_csx_pesquisa e (nolock)
	WHERE
		e.id_evento = 32 and
		e.data_hora >= @dataInicio
	GROUP BY mensagem,proprietario,evento
)



