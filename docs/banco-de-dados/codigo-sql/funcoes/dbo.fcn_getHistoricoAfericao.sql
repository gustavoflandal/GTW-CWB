
CREATE FUNCTION [dbo].[fcn_getHistoricoAfericao]()
RETURNS TABLE
AS
RETURN
(
	SELECT
		e.proprietario, 
		CONVERT(char(14),e.data_hora,103) as dia,
		SUM(CASE WHEN e.id_evento = 30 THEN 1 ELSE 0 END) as modo_afericao_ligado,
		SUM(CASE WHEN e.id_evento = 31 THEN 1 ELSE 0 END) as modo_afericao_desligado
	FROM
		eventos_csx_pesquisa e (nolock)
	WHERE	(e.id_evento = 30 or e.id_evento = 31)
		and  e.data_hora >= GETDATE()-3
	GROUP BY 
		CONVERT(char(14),e.data_hora,103), proprietario
)



