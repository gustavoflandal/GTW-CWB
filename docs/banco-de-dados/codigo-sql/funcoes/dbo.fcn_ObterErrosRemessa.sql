---------------------------------------------------------------------------------------------------------------------------------------
-- Email Felipe - 22/07/2014
--

CREATE FUNCTION [dbo].[fcn_ObterErrosRemessa] 
(
	@id_remessa int
)
RETURNS int
AS
BEGIN

DECLARE @erros INT

SELECT @erros = COUNT(i.id_infracao) 
	FROM infracao i (NOLOCK)
		INNER JOIN infracao_remessa ir (NOLOCK) 
			ON i.id_infracao = ir.id_infracao
		INNER JOIN remessa r (NOLOCK) 
			ON ir.id_remessa = r.id_remessa
		INNER JOIN movimento_importacao mi (NOLOCK)
			ON r.codigo_externo = mi.id_movimento
			AND i.id_enquadramento = mi.id_enquadramento 
			AND ir.sequencia = mi.sequencia
		INNER JOIN infracao_processo ip_v (NOLOCK) 
			ON i.id_infracao = ip_v.id_infracao AND ip_v.id_processo = 3
		INNER JOIN (
				SELECT id_infracao, MAX(id_infracao_processo) AS id_infracao_processo 
					FROM infracao_processo (NOLOCK) 
					WHERE	id_processo = 3 
						AND status_processo = 0
					GROUP BY id_infracao
			) AS sub1 
			ON	sub1.id_infracao = i.id_infracao 
			AND sub1.id_infracao_processo = ip_v.id_infracao_processo
		LEFT JOIN infracao_processo_digitacao ipd (NOLOCK) 
			ON ip_v.id_infracao_processo = ipd.id_infracao_processo 
		INNER JOIN remessa_amostragem ra (NOLOCK) -- considera apenas infrações na amostra
			ON i.id_infracao = ra.id_infracao 
	WHERE r.id_remessa = @id_remessa
		AND (   (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet)
		OR (ipd.placa		  IS NOT NULL AND mi.placa <> ipd.placa)
		OR (CASE WHEN ip_v.id_inconsistencia = 0 THEN 0 ELSE 1 END ^ CASE WHEN mi.id_inconsistencia = 0 THEN 0 ELSE 1 END) > 0    
		OR (ip_v.erro_oblit = 1)
		)

RETURN @erros
	 
END

