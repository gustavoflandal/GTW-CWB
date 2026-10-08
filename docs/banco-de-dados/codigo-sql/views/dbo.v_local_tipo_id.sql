
CREATE VIEW [dbo].[v_local_tipo_id]
AS
	SELECT r.id_local,
		   r.pista_dupla,
		   r.tipo,
		   r.id_pista,
		   r.faixa,
		   r.tipo + LTRIM(STR(r.faixa)) AS pista
	FROM   (
				SELECT l.id_local,
					   l.pista_dupla,
					   REPLICATE(CHAR(64 + ((((l.tipo+2) - 1) % 26) + 1)),((((l.tipo+2) - 1)/ 26) + 1)) AS tipo,
					   l.id_pista,
					   l.cod_pista_alternativo AS faixa
				FROM   (
							SELECT lpv.id_local,
								   lpv.id_pista,
								   lpv.cod_pista_alternativo,
								   CASE WHEN l.qtde_faixas_distintas < l.qtde_faixas THEN 1 ELSE 0 END AS pista_dupla,
								   ROW_NUMBER() OVER(PARTITION BY lpv.id_local, lpv.cod_pista_alternativo ORDER BY lpv.id_local, lpv.cod_pista_alternativo, lpv.id_pista) AS tipo
							FROM   local_pista_vigente lpv
								   JOIN (
												SELECT lv.id_local,
													   COUNT(DISTINCT lv.id_pista) AS qtde_faixas,
													   COUNT(DISTINCT lv.cod_pista_alternativo) AS qtde_faixas_distintas
												FROM   local_pista_vigente lv
												GROUP BY
													   lv.id_local
										) AS l
											ON  l.id_local = lpv.id_local
							--WHERE  l.pista_dupla = 1
					   ) l
		   ) r
	--ORDER BY
	--	   r.id_local,
	--	   r.faixa,
	--	   r.id_pista
