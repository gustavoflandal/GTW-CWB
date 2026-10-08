
CREATE VIEW [dbo].[local_status_energia] AS
SELECT  
	se.id_status_energia,   
	se.id_local,
	se.status,
	se.data_atualizacao
FROM status_energia se (nolock)
	INNER JOIN (SELECT 
					id_local,
					MAX(id_status_energia) as ultimo_id_status_energia
				FROM status_energia (nolock)
				GROUP BY
					id_local
				) AS t1 
		ON	se.id_local = t1.id_local 
		AND se.id_status_energia = t1.ultimo_id_status_energia


