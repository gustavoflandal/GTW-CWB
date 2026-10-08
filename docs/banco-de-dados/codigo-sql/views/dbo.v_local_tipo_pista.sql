CREATE VIEW [dbo].[v_local_tipo_pista]
AS  
	SELECT r.id_local,
		   r.pista_dupla
	FROM   v_local_tipo_id r
	GROUP BY
		   r.id_local,
		   r.pista_dupla
	--ORDER BY
	--	   r.id_local,
	--	   r.pista_dupla
