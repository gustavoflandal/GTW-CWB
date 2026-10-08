
CREATE FUNCTION [dbo].[fcn_getImagensEmProcessamento] ()
RETURNS TABLE 
AS
RETURN 
(
	SELECT	
		i.id_processo, 
		i.id_enquadramento, 
		p.nome AS nome_processo, 
		CAST(i.data AS DATE) AS data, 
		i.espera, 
		COUNT(*) AS total 
	FROM infracao i (nolock)
	JOIN processo p (nolock)
		ON p.id_processo = i.id_processo 
	LEFT JOIN infracao_remessa ir (NOLOCK) 
		ON i.id_infracao = ir.id_infracao 
	LEFT JOIN remessa r (NOLOCK)
		ON r.id_remessa = ir.id_remessa 
	--LEFT JOIN movimento_importacao mi (NOLOCK) 
	--	ON mi.id_movimento = r.codigo_externo 
	--	AND mi.sequencia = ir.sequencia 
	--	AND mi.id_enquadramento = i.id_enquadramento 
	LEFT JOIN movimentos_erro me (NOLOCK) 
		ON me.id_remessa = r.id_remessa 
		--ON r.tipo = me.tipo 
		--AND r.codigo_externo = me.id_movimento 
	WHERE	(p.id_processo IN (20, 21, 24, 1, 2, 3, 11) OR (p.id_processo = 4 AND r.id_remessa IS NULL))
		AND r.data_validacao IS NULL 
		--AND mi.data_validacao IS NULL 
		AND me.id_movimento IS NULL 
	GROUP BY 
		i.id_processo, 
		p.nome, 
		i.id_enquadramento,
		CAST(i.data AS DATE), 
		i.espera 
	--OPTION(MAXDOP 1)
)




