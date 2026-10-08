
CREATE FUNCTION [dbo].[f_inconsistencias_processo_teste]
(	
	@id_processo INT, @id_enquadramento INT = NULL
)
RETURNS TABLE 
AS
RETURN 
(
-- DECLARE @id_processo INT = 1, @id_enquadramento INT = 57461
	SELECT TOP(1000) inc.id_inconsistencia, inc.descricao, 
	--CASE WHEN inc.id_inconsistencia = 0 THEN 0 ELSE 1 END AS inconsistente, 
	COUNT(sub1.id_inconsistencia) cnt FROM
	inconsistencia inc (NOLOCK) 
	JOIN processo_inconsistencia pi (NOLOCK) 
		ON inc.id_inconsistencia = pi.id_inconsistencia AND pi.id_processo = @id_processo
	JOIN enquadramento_inconsistencia ei (NOLOCK) 
		ON inc.id_inconsistencia = ei.id_inconsistencia AND (@id_enquadramento IS NULL OR ei.id_enquadramento = @id_enquadramento)
	LEFT JOIN 
	(
	-- DECLARE @id_processo INT = 1, @id_enquadramento INT = 57461
		SELECT TOP(1000) ip.id_inconsistencia FROM infracao_processo ip (NOLOCK)
		JOIN infracao i (NOLOCK) ON ip.id_infracao = i.id_infracao
		WHERE ip.id_inconsistencia > 0 AND ip.id_processo = @id_processo
		AND   (@id_enquadramento IS NULL OR i.id_enquadramento = @id_enquadramento) 
		ORDER BY ip.id_infracao_processo DESC 
	) AS sub1 
	ON inc.id_inconsistencia = sub1.id_inconsistencia
	WHERE inc.id_inconsistencia > 0
	GROUP BY inc.id_inconsistencia, inc.descricao
	ORDER BY 3 DESC, 1 ASC
)

