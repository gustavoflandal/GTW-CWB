CREATE VIEW [dbo].[vw_relatorio_eventos_equipamentos]
AS
	SELECT lv.serie_equipamento AS [Nº série equipamento],
		   d.Data AS [Data],
		   RTRIM(ecde.evento) AS [Evento],
		   COALESCE(evt.qtde_evento, 0) AS [Quantidade de eventos]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -14, GETDATE()), GETDATE()) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   CROSS JOIN (SELECT id_evento, evento FROM eventos_csx_desc_evento (NOLOCK) WHERE id_evento IN (1,2,3,9,10,26,37,36) UNION SELECT 9999 AS id_evento, 'Sistema medidor iniciado' AS evento) ecde
		   LEFT JOIN (
						SELECT lv.serie_equipamento,
							   CAST(ec.data_hora AS DATE) AS data,
							   CASE WHEN ecde2.id_evento = 9 AND ec.mensagem LIKE 'Socket de Imagens Conectado' THEN 9999
									ELSE ecde2.id_evento
							   END AS id_evento,
							   COUNT(*) AS qtde_evento
						FROM   eventos_csx ec (NOLOCK)
							   INNER JOIN eventos_csx_desc_evento ecde2 (NOLOCK)
									ON  ecde2.id_evento = ec.id_evento
							   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
									ON  ecdp.id_proprietario = ec.id_proprietario
							   INNER JOIN local_vigente lv (NOLOCK)
									ON  lv.serie_equipamento = ecdp.proprietario
						WHERE  ISNUMERIC(ecdp.proprietario) = 1
							   AND CAST(ec.data_hora AS DATE) BETWEEN CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(GETDATE() AS DATE)
							   AND ecde2.id_evento IN (1,2,3,9,10,26,37,36)
							   AND lv.desativado = 0
						GROUP BY
							   lv.serie_equipamento,
							   CAST(ec.data_hora AS DATE),
							   CASE WHEN ecde2.id_evento = 9 AND ec.mensagem LIKE 'Socket de Imagens Conectado' THEN 9999
									ELSE ecde2.id_evento
							   END
		   ) evt
				ON  evt.serie_equipamento = lv.serie_equipamento
					AND evt.data = d.Data
					AND evt.id_evento = ecde.id_evento
	WHERE  lv.desativado = 0
		   AND lv.serie_equipamento BETWEEN 2100000 AND 2199999
		   --AND ecde.id_evento IN (1,2,3,9,10,26,37,36)
