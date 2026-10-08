
CREATE VIEW [dbo].[local_status_div] AS
SELECT 
	t1.id_local,
	MAX(t1.status) as status,
	MAX(t1.data_atualizacao) as data_atualizacao
FROM (	SELECT  
			sd.id_status_DIV,   
			sd.id_local,
			sd.codigo_DIV,
			sd.status,
			sd.data_atualizacao
		FROM status_DIV sd (nolock)
			INNER JOIN (SELECT 
							id_local,
							codigo_DIV,
							MAX(id_status_DIV) as ultimo_id_status_DIV
						FROM status_DIV (nolock)
						GROUP BY id_local, codigo_DIV
						) AS t1 
				ON sd.codigo_DIV = t1.codigo_DIV 
				AND sd.id_local = t1.id_local 
				AND sd.id_status_DIV = t1.ultimo_id_status_DIV
	) AS T1
GROUP BY t1.id_local


