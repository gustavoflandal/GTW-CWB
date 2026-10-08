
CREATE FUNCTION [dbo].[fcn_ListaPistasAmostra] ( 
  @data_ini datetime,
  @data_fim datetime 
)
RETURNS TABLE
AS

RETURN
(
	SELECT 
		sub.id_local, 
		cep.cod_pista, 
		cep.cod_pista_alternativo, 
		RTRIM(cep.nome_pista) AS nome_pista, 
		sub.id_pista, 
		sub.serie_equipamento, 
		cep.cod_pista_prodam, 
		sub.metrologica, 
		sub.data_inicio 
	FROM (	SELECT 
				sub2.serie_equipamento, 
				sub2.id_local, 
				sub2.id_pista, 
				sub2.id_configuracao_equipamento,	 
				sub2.metrologica, 
				MAX(sub2.data_inicio) AS data_inicio 
			FROM (	SELECT 
						lvg.serie_equipamento, 
						lriv.id_local, 
						lriv.id_pista, 
						lvg.id_configuracao_equipamento, 
						CAST(lvg.data_inicio AS DATE) AS data_inicio, 
						CASE 
							WHEN (lriv.tipo NOT IN ('VL','MT','TS')) 
								THEN CAST(0 AS BIT) 
							WHEN (lriv.tipo = 'VL')
								THEN CAST(1 AS BIT)
							ELSE CAST(NULL AS BIT) 
						END AS metrologica 
					FROM local_vigente lvg (nolock) 
						JOIN configuracao_equipamento ce (nolock) 
							ON ce.id_configuracao_equipamento = lvg.id_configuracao_equipamento 
						JOIN local_regra_infracao_vigente lriv (nolock) 
							ON lvg.id_configuracao_equipamento = lriv.id_configuracao_equipamento 
					WHERE (ce.data_fim IS NULL AND CAST(ce.data_inicio AS DATE) <= @data_fim
							OR (ce.data_fim IS NOT NULL AND CAST(ce.data_fim AS DATE) > @data_fim )) 
				) AS sub2 
			WHERE 
				sub2.metrologica IS NOT NULL
			GROUP BY 
				sub2.serie_equipamento, 
				sub2.id_local, 
				sub2.id_pista, 
				sub2.id_configuracao_equipamento,	 
				sub2.metrologica 
			) AS sub 
		INNER JOIN configuracao_equipamento_pista cep (nolock) 
			ON	cep.id_configuracao_equipamento = sub.id_configuracao_equipamento 
			AND cep.id_pista = sub.id_pista 
)


