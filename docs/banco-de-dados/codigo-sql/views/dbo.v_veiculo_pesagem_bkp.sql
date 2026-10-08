CREATE VIEW [dbo].[v_veiculo_pesagem_bkp]
AS
	SELECT vpi.id_veiculo_unic,
		   vp.id_veiculo,
		   vp.id_local,
		   vp.data,
		   vp.placa,
		   CAST(vp.velocidade AS FLOAT) AS velocidade,
		   CAST(vp.comprimento AS FLOAT) AS comprimento,
		   vp.id_classe,
		   ROUND(vpi.pbt, 0) AS pbt,
		   ROUND(vpi.temperatura_pavimento, 2) AS temperatura_pavimento,
		   ROUND(vpi.velocidade_piezo, 2) AS velocidade_piezo,
		   vp.classificacao
	FROM   bkp_veiculo_pesagem vpi
		   JOIN veiculo_pesquisa vp (NOLOCK)
				ON  vp.id_veiculo_unic = vpi.id_veiculo_unic
	WHERE  vpi.pesagem_valida = 1
