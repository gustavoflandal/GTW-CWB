
CREATE FUNCTION [dbo].[fcn_getHistoricoManutencao]()
RETURNS TABLE
AS
RETURN
(
	SELECT
		e.proprietario, 
		CONVERT(char(14),e.data_hora,103) as dia,
		SUM(CASE WHEN e.id_evento = 15 THEN 1 ELSE 0 END) as manutencoes_abertas,
		SUM(CASE WHEN e.id_evento = 16 THEN 1 ELSE 0 END) as manutencoes_fechadas
	FROM
		eventos_csx_pesquisa e (nolock)
	WHERE	(e.id_evento = 15 or e.id_evento = 16)
		and  e.data_hora >= GETDATE()-3
	GROUP BY 
		CONVERT(char(14),
		e.data_hora,103), 
		proprietario
)


