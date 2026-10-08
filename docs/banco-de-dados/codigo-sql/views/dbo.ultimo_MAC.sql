
CREATE VIEW [dbo].[ultimo_MAC] as
	SELECT 
		ep1.proprietario
		, e1.data_hora
		, SUBSTRING(e1.mensagem,15,17) AS [MAC]
	FROM eventos_csx e1 (nolock)
		INNER JOIN eventos_csx_desc_proprietario ep1 (nolock)
			ON ep1.id_proprietario = e1.id_proprietario
	WHERE	e1.id_evento = 52 --Inventário do equipamento.
		AND e1.id = (-- último evento
						SELECT TOP 1 
							id
						FROM eventos_csx (nolock)
						WHERE	id_evento = 52 --Inventário do equipamento.
							AND mensagem LIKE 'Endereço MAC:%'
							AND mensagem <> 'Endereço MAC: 00:53:45:00:00:00' -- MAC inválido
							AND mensagem <> 'Endereço MAC: EC:9D:E9:F7:F7:F7' -- MAC inválido
							AND id_proprietario = e1.id_proprietario
						ORDER BY
							data_hora DESC
					)


