CREATE VIEW [dbo].[vw_relatorio_qtde_img_teste]
AS
	SELECT lv.serie_equipamento AS [Nº série equipamento],
		   d.Data AS [Data],
		   COALESCE(img.quantidade_img_teste, 0) AS [Quantidade de imagens teste]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -14, GETDATE()), GETDATE()) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   LEFT JOIN (
						SELECT l.serie_equipamento,
							   CAST(i.data AS DATE) AS data,
							   COUNT(*) AS quantidade_img_teste
						FROM   infracao i (NOLOCK)
							   INNER JOIN local_vigente l (NOLOCK)
									ON  l.id_local = i.id_local
						WHERE  i.id_enquadramento = 1
							   AND CAST(i.data AS DATE) BETWEEN CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(GETDATE() AS DATE)
						GROUP BY
							   l.serie_equipamento,
							   CAST(i.data AS DATE)
		   ) img
				ON  img.serie_equipamento = lv.serie_equipamento
					AND img.data = d.Data
	WHERE  lv.desativado = 0
