


/****** Object:  View [dbo].[painel_ultima_camera_carregada]    Script Date: 02/04/2011 11:31:40 ******/
CREATE VIEW [dbo].[painel_ultima_camera_carregada] AS
SELECT
	sub.proprietario AS [proprietario],
	MIN(sub.data_hora) as [data_ultima_agenda]
FROM (	select 
			p.proprietario,
			SUBSTRING( e.mensagem, 1, CHARINDEX('(',e.mensagem) -1) as camera, -- Camera: 3 (19:20 até 06:30) carregada. --> Camera: 3 
			MAX(e.data_hora) as data_hora
		from eventos_csx e (nolock)
			join eventos_csx_desc_proprietario p (nolock)
				ON p.id_proprietario = e.id_proprietario
		WHERE	e.id_evento = 3005 -- "Período da agenda executado com sucesso."
			and e.mensagem like 'Camera:%' -- ult. versão a mensagem possui a camera e o periodo
			and data_hora >= GETDATE() - 30
		group by p.proprietario, SUBSTRING( e.mensagem, 1, CHARINDEX('(',e.mensagem) -1)
	) AS sub	
GROUP BY sub.proprietario


