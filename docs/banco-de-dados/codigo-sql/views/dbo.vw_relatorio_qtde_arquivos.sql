CREATE VIEW [dbo].[vw_relatorio_qtde_arquivos]
AS
	SELECT lv.serie_equipamento AS [Nº série equipamento],
		   d.Data AS [Data],
		   COALESCE(arq.qtde_arquivos, 0) AS [Quantidade de arquivos]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -14, GETDATE()), GETDATE()) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   LEFT JOIN (
						SELECT l.serie_equipamento,
							   CAST(ai.data_arquivo AS DATE) AS data,
							   COUNT(*) AS qtde_arquivos
						FROM   arquivos_importados ai (NOLOCK)
							   INNER JOIN local_vigente l (NOLOCK)
									ON  l.id_local = ai.id_local
						WHERE  CAST(ai.data_arquivo AS DATE) BETWEEN CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(GETDATE() AS DATE)
							   AND ai.arquivo_ok = 1
							   AND ai.nome_arquivo LIKE '%.csx5'
						GROUP BY
							   l.serie_equipamento,
							   CAST(ai.data_arquivo AS DATE)
		   ) AS arq
				ON  arq.serie_equipamento = lv.serie_equipamento
					AND arq.data = d.Data
	WHERE  lv.desativado = 0
