

CREATE FUNCTION [dbo].[fcn_getErrosProcessamento]( @Data Date )
RETURNS TABLE
AS
RETURN
(

	SELECT 
		*
	FROM 
		(SELECT 
			infracao_inconsistente.data_inconsistente as [Data], 
			infracao_inconsistente.descricao, 
			infracao_inconsistente.conta_inconsistente,
			infracao_inconsistente.id_inconsistencia,
			(CASE 
				WHEN infracao_inconsistente.descricao IS NULL 
					THEN infracao_consistente.conta_consistente+infracao_inconsistente.conta_inconsistente 
				ELSE 
					NULL 
			END) AS total, 
			(CAST(CAST(infracao_inconsistente.conta_inconsistente AS NUMERIC(15,3))*100 /(infracao_consistente.conta_consistente+infracao_inconsistente.conta_inconsistente) AS NUMERIC(15,2) )) as [%total]
		FROM
			(SELECT 
				CAST(i1.data AS date) AS data_inconsistente, 
				inc.descricao,
				COUNT(i1.id_infracao) as conta_inconsistente,
				ip2.id_inconsistencia 
			 FROM infracao i1 (nolock)
				INNER JOIN infracao_processo ip1 (nolock)
					ON	i1.id_infracao = ip1.id_infracao 
					AND ip1.id_processo = 11
				INNER JOIN infracao_processo ip2 (nolock) 
					ON i1.id_infracao = ip2.id_infracao
					AND ip2.id_processo = 3
				INNER JOIN inconsistencia inc 
					on inc.id_inconsistencia = ip2.id_inconsistencia
			WHERE ((ip1.id_inconsistencia = 0 AND ip2.id_inconsistencia > 0) 
				OR (ip1.id_inconsistencia > 0 AND ip2.id_inconsistencia = 0))
			GROUP BY cast(i1.data AS date), inc.descricao, ip2.id_inconsistencia
			WITH ROLLUP) AS infracao_inconsistente,
			(SELECT 
				CAST(i2.data AS date) AS data_consistente,
				COUNT(i2.id_infracao) AS conta_consistente 
			FROM 
				infracao i2 (nolock)
			WHERE 
				i2.id_processo in (7,8,9)
			GROUP BY cast(i2.data AS date)
			) AS infracao_consistente
		WHERE 
			infracao_inconsistente.data_inconsistente = infracao_consistente.data_consistente AND
			infracao_consistente.data_consistente = @Data
		) as T1
	WHERE 
		T1.id_inconsistencia IS NOT NULL OR
		T1.total IS NOT NULL 
	
);



