
CREATE VIEW [dbo].[relatorio_amostras]
AS
SELECT
	sub2.data,
	sub2.id_local,
	sub2.id_pista,
	sub2.metrologica,
	lvg.serie_equipamento,
	cep.cod_pista,
	sub2.[manual],
	sub2.score_total
FROM
	(	SELECT
			sub1.*,
			ROW_NUMBER() OVER(PARTITION BY data, id_local, id_pista, metrologica
			ORDER BY [manual] DESC) AS rn
		FROM
			(SELECT
				CAST(ai.data AS DATE) AS data,
				vei.id_local,
				vei.pista AS id_pista,
				ai.metrologica,
				CAST(0 AS BIT) AS [manual],
				ai.score_total
			FROM amostra_imagem ai (nolock)
				INNER JOIN veiculo vei (nolock)
					ON vei.id_veiculo = ai.id_veiculo
		UNION ALL
		SELECT
			aim.data,
			aim.id_local,
			aim.id_pista,
			aim.metrologica,
			CAST(1 AS BIT) AS [manual],
			NULL AS score_total
		FROM amostra_imagem_manual aim (nolock)
		) AS sub1
	) AS sub2
	INNER JOIN local_vigente lvg (nolock)
		ON sub2.id_local = lvg.id_local
	INNER JOIN configuracao_equipamento_pista cep (nolock)
		ON cep.id_configuracao_equipamento = lvg.id_configuracao_equipamento
		AND cep.id_pista = sub2.id_pista
WHERE sub2.rn = 1


