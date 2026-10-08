CREATE FUNCTION [dbo].[fcn_listar_sugestoes_amostras] (@id_local INT, @id_pista TINYINT, @dia DATE, @metrologica BIT, @page_offset INT, @page_limit INT)
RETURNS TABLE
AS
RETURN 
(
	SELECT
		sub2.data,
		sub2.id_local,
		sub2.id_pista,
		sub2.metrologica,
		sub2.manual,
		CAST(0 AS BIT) as fixada,
		sub2.tipo,
		sub2.id_veiculo,
		sub2.id_infracao,
		sub2.score_total,
		sub2.serie_equipamento,
		sub2.nome_pista,
		sub2.cod_pista_alternativo,
		sub2.cod_pista_prodam,
		sub2.cod_pista,
		sub2.id_thumbnail,
		sub2.aplicavel
	FROM (	SELECT
				sub.*,
				ROW_NUMBER() OVER(ORDER BY sub.data ASC) AS rowNumber
			FROM (	SELECT DISTINCT
						CAST(ia.data AS DATE) AS data,
						ia.id_local,
						ia.id_pista,
						ia.metrologica,
						CAST(0 AS BIT) AS [manual],
						NULL AS tipo,
						ia.id_veiculo,
						ia.id_infracao,
						ia.id_imagem AS id_thumbnail,
						dbo.fcn_pontua_infracao(ia.id_infracao) AS score_total,
						ia.serie_equipamento,
						ia.nome_pista,
						ia.cod_pista_alternativo,
						ia.cod_pista_prodam,
						ia.cod_pista,
						CASE WHEN (ira.id_infracao IS NOT NULL)
						THEN CAST(0 AS BIT) ELSE CAST(1 AS BIT) END AS aplicavel
					FROM infracao_amostra ia (nolock)
						LEFT JOIN infracao_rejeita_amostra ira (nolock)
							ON ia.id_infracao = ira.id_infracao
					WHERE ia.id_local = @id_local
						AND ia.id_pista = @id_pista
						AND CAST(ia.data AS DATE) = @dia
						AND		((@metrologica = 1 AND ia.tipo IN ('TS', 'VL'))
							OR	(@metrologica = 0 AND ia.tipo != 'VL'))
				) AS sub
			) AS sub2
	WHERE (@page_offset IS NULL OR @page_limit IS NULL)
		OR sub2.rowNumber BETWEEN @page_offset AND @page_limit
)




