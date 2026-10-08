
CREATE VIEW [dbo].[ultimo_iccid] as
SELECT 
	ep.proprietario AS [PROPRIETARIO]
	,RTRIM(e.mensagem) AS [ICCID]
	,e.data_hora	 AS [DATA_HORA]
FROM eventos_csx e (nolock)
	INNER JOIN eventos_csx_desc_proprietario ep (nolock)
		ON ep.id_proprietario = e.id_proprietario
WHERE	e.id_evento = 4007
	and e.id = (SELECT TOP 1 id
					FROM eventos_csx (nolock)
					WHERE id_evento = 4007 and id_proprietario = e.id_proprietario
					ORDER BY data_hora DESC				
				)


