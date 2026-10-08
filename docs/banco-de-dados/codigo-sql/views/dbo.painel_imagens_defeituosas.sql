
/****** Object:  View [dbo].[painel_imagens_defeituosas]    Script Date: 02/04/2011 11:31:40 ******/
CREATE VIEW [dbo].[painel_imagens_defeituosas] AS
SELECT
	ep.proprietario AS [serie_equipamento],
	DATEPART(hh,e.data_hora) as [hora],
	CAST(SUBSTRING(e.mensagem,8,1) AS INT) AS [id_pista],
	COUNT(*) AS [total_eventos]
FROM eventos_csx e (nolock)
	INNER JOIN eventos_csx_desc_proprietario ep (nolock)
		ON ep.id_proprietario = e.id_proprietario
WHERE	e.id_evento = 25 --Imagem defeituosa detectada.
	AND e.data_hora >= DATEADD(HH,-24,GETDATE()) -- ÚLTIMAS 24 H
GROUP BY
	ep.proprietario,
	DATEPART(hh,e.data_hora),
	e.mensagem


