CREATE VIEW [dbo].[vw_relatorio_qtde_infracao_completo]
AS
	SELECT lv.serie_equipamento AS [Nº série equipamento],
		   d.Data AS [Data],
		   RTRIM(CAST(enq.id_enquadramento AS VARCHAR)) + ' - ' + RTRIM(enq.descricao) AS [Enquadramento],
		   RTRIM(cv.descricao) AS [Tipo do veículo],
		   COALESCE(inf.qtde_infracao, 0) AS [Quantidade de infrações]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(DATEADD(DAY, -14, GETDATE()), GETDATE()) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   CROSS JOIN (
							SELECT e.id_enquadramento, RTRIM(e.descricao) AS descricao
							FROM   local_vigente lv2 (NOLOCK)
								   INNER JOIN configuracao_equipamento_regra_infracao ceri (NOLOCK)
										ON  ceri.id_configuracao_equipamento = lv2.id_configuracao_equipamento
								   INNER JOIN enquadramento_regra_infracao eri (NOLOCK)
										ON  eri.tipo = ceri.tipo
								   INNER JOIN enquadramento e (NOLOCK)
										ON  e.id_enquadramento = eri.id_enquadramento
							WHERE  lv2.desativado = 0
								   AND ceri.ativo = 1
								   AND eri.id_enquadramento > 1
							GROUP BY
								   e.id_enquadramento, e.descricao
		   ) enq
		   CROSS JOIN classe_veiculo cv (NOLOCK)
		   LEFT JOIN (
						SELECT l.serie_equipamento,
							   CAST(i.data AS DATE) AS data,
							   i.id_enquadramento,
							   v.id_classe,
							   COUNT(*) AS qtde_infracao
						FROM   infracao i (NOLOCK)
							   INNER JOIN veiculo v (NOLOCK)
									ON  v.id_veiculo = i.id_veiculo
							   INNER JOIN local_vigente l (NOLOCK)
									ON  l.id_local = i.id_local
						WHERE  i.id_enquadramento > 1
							   AND CAST(i.data AS DATE) BETWEEN CAST(DATEADD(DAY, -14, GETDATE()) AS DATE) AND CAST(GETDATE() AS DATE)
							   AND l.desativado = 0
						GROUP BY
							   l.serie_equipamento,
							   CAST(i.data AS DATE),
							   i.id_enquadramento,
							   v.id_classe
		   ) inf
				ON  inf.serie_equipamento = lv.serie_equipamento
					AND inf.data = d.Data
					AND inf.id_enquadramento = enq.id_enquadramento
					AND inf.id_classe = cv.id_classe
	WHERE  lv.desativado = 0
