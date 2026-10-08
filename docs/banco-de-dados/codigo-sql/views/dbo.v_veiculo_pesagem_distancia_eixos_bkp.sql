CREATE VIEW [dbo].[v_veiculo_pesagem_distancia_eixos_bkp]
AS
	SELECT id_veiculo_unic,
		   distancia_E1E2,distancia_E2E3,distancia_E3E4,distancia_E4E5,distancia_E5E6,distancia_E6E7,distancia_E7E8,distancia_E8E9
	FROM   (
				SELECT vpi.id_veiculo_unic,
					   'distancia_E' + CAST((vpei.eixo - 1) AS CHAR(1)) + 'E' + CAST(vpei.eixo AS CHAR(1)) AS distancia,
					   vpei.distancia_eixo_anterior
				FROM   bkp_veiculo_pesagem vpi
					   JOIN bkp_veiculo_pesagem_eixo vpei
							ON  vpei.id_veiculo_unic = vpi.id_veiculo_unic
				WHERE  vpi.pesagem_valida = 1
					   AND vpei.distancia_eixo_anterior > 0.0
		   ) e
	PIVOT  (
				SUM(e.distancia_eixo_anterior)
				FOR distancia IN ([distancia_E1E2],[distancia_E2E3],[distancia_E3E4],[distancia_E4E5],[distancia_E5E6],[distancia_E6E7],[distancia_E7E8],[distancia_E8E9])
		   ) p
