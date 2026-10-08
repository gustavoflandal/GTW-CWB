

CREATE FUNCTION [dbo].[fcn_getAlertaEvento] (@idEvento int , @dataInicio DateTime, @mensagem bit = 0)
RETURNS TABLE
AS
RETURN
(
	SELECT 
		*,
		COUNT(*) as Total
	FROM (	SELECT 
				e.proprietario, 
				case 
					when e.evento is not null 
						then e.evento 
					else 
						' ' 
				end as evento,
				case 
					when @mensagem = 0 
						then NULL 
					else 
						e.mensagem 
				end as mensagem
			FROM
				eventos_csx_pesquisa e (nolock)
			WHERE	e.id_evento = @idEvento 
				and	e.data_hora >= @dataInicio
		) as t
	GROUP BY t.proprietario,t.evento,t.mensagem
)


