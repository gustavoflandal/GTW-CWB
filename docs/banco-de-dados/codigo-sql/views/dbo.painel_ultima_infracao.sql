
CREATE VIEW [dbo].[painel_ultima_infracao] AS
SELECT 
	serie_equipamento, --TODO: remover
	id_local,
	id_pista,
	MAX(data_ultima_infracao) AS data_ultima_infracao
FROM (	SELECT 
			lv.serie_equipamento, --TODO: remover
			inf.id_local,
			inf.pista as id_pista,
			MAX(inf.data) AS data_ultima_infracao
		FROM infracao inf (nolock)
			INNER JOIN local_vigente lv (nolock) --TODO: remover
				ON lv.id_local = inf.id_local
		WHERE inf.data < GETDATE()
		GROUP BY
			lv.serie_equipamento,
			inf.id_local,
			inf.pista
		UNION
		-- imagens com a importação não finalizada
		SELECT 
			lv.serie_equipamento, --TODO: remover
			vi.id_local,
			vi.pista as id_pista,
			MAX(vi.data) AS data_ultima_infracao
		FROM infracao_importacao ii (nolock)
			INNER JOIN veiculo_importacao vi
				ON vi.id_veiculo_unic = ii.id_veiculo_unic
			INNER JOIN local_vigente lv 
				ON lv.id_local = vi.id_local
		WHERE vi.data < GETDATE()
		GROUP BY
			lv.serie_equipamento,
			vi.id_local,
			vi.pista
	) AS ultima_infracao
GROUP BY serie_equipamento, id_local, id_pista



