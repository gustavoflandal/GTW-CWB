CREATE VIEW [dbo].[vw_relatorio_versao_captura]
AS
	SELECT lv.serie_equipamento AS [Nº série equipamento],
		   d.Data AS [Data],
		   COALESCE(evt.versao, '') AS [Versão]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -9, GETDATE()), GETDATE()) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   LEFT JOIN (
						SELECT lv.id_local,
							   --RTRIM(lv.nome) AS endereco,
							   CAST(ec.data_hora AS DATE) AS data,
							   MAX(LTRIM(RTRIM(SUBSTRING(ec.mensagem, (CHARINDEX('Consilux Captura versão ', ec.mensagem) + LEN('Consilux Captura versão ')), CHARINDEX('tickcount', ec.mensagem) - (CHARINDEX('Consilux Captura versão ', ec.mensagem)+ LEN('Consilux Captura versão ')))))) AS versao
						FROM   eventos_csx ec (NOLOCK)
							   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
									ON  ecdp.id_proprietario = ec.id_proprietario
							   INNER JOIN local_vigente lv (NOLOCK)
									ON  lv.serie_equipamento = ecdp.proprietario
						WHERE  ISNUMERIC(ecdp.proprietario) = 1
							   AND CAST(ec.data_hora AS DATE) BETWEEN CAST(DATEADD(DAY, -10, GETDATE()) AS DATE) AND CAST(GETDATE() AS DATE)
							   AND ec.id_evento = 2
							   AND ec.mensagem LIKE 'Consilux Captura%'
							   AND lv.desativado = 0
						GROUP BY
							   lv.id_local,
							   CAST(ec.data_hora AS DATE)
		   ) evt
				ON  evt.id_local = lv.id_local
					AND evt.data = d.Data
	WHERE  lv.desativado = 0
