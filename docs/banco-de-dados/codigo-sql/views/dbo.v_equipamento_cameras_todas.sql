CREATE VIEW [dbo].[v_equipamento_cameras_todas]
AS  
	SELECT r.id_local,
		   r.id_camera,
		   r.ip_camera,
		   r.tipo_camera,
		   r.relevante
	--SELECT *
	FROM   (
				SELECT lv.id_local,
					   cec.id_camera,
					   cec.endereco AS ip_camera,
					   CASE WHEN cec.id_camera = cep.id_camera_frontal THEN 'Objetiva frontal'
							WHEN cec.id_camera = cep.id_camera_traseira THEN 'Objetiva traseira'
							WHEN cec.id_camera = cep.id_camera_pan_1 THEN 'Panoramica traseira'
							WHEN cec.id_camera = cep.id_camera_pan_2 THEN 'Panoramica frontal'
							ELSE NULL
					   END AS tipo_camera,
					   cep.id_camera_frontal,
					   cep.id_camera_traseira,
					   cep.id_camera_pan_1,
					   cep.id_camera_pan_2,
					   cec.relevante
				FROM   local_pista_vigente lv
					   JOIN configuracao_equipamento_pista cep
							ON  cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
								AND cep.id_pista = lv.id_pista
					   JOIN configuracao_equipamento_camera cec
							ON  cec.id_configuracao_equipamento = cep.id_configuracao_equipamento
				WHERE  cec.id_camera > 0
					   AND lv.desativado = 0
					   --AND lv.id_local = 1
		   ) AS r
	WHERE  r.tipo_camera IS NOT NULL AND r.ip_camera != ''
	GROUP BY
		   r.id_local,
		   r.id_camera,
		   r.ip_camera,
		   r.tipo_camera,
		   r.relevante
	--ORDER BY
	--	   r.id_local,
	--	   r.id_camera,
	--	   r.ip_camera,
	--	   r.tipo_camera
