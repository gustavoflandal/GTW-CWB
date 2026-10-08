CREATE VIEW [muralha].[v_veiculo_tempo_real_teste]
AS
	SELECT vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   RTRIM(lv.nome) AS nome,
		   vtr.id_pista,
		   lv.cod_pista_alternativo AS faixa,
		   lv.posicao_lat AS latitude,
		   lv.posicao_lon AS longitude,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END AS velocidade,
		   COALESCE(vtr.classificacao,'') AS classificacao,
		   vtr.enviado_cliente,
		   SUM(CASE WHEN vtri.id_veiculo_tempo_real IS NOT NULL THEN 1 ELSE 0 END) AS com_imagem,
		   CASE WHEN lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL THEN 1 ELSE 0 END AS possui_coordenadas,
		   CASE WHEN a.id_veiculo_tempo_real IS NOT NULL THEN 1 ELSE 0 END AS possui_alerta
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
		   INNER JOIN local_pista_vigente lv (NOLOCK)
				ON  lv.id_local = vtr.id_local
					AND lv.id_pista = vtr.id_pista
		   LEFT JOIN (SELECT id_veiculo_tempo_real FROM muralha.veiculo_tempo_real_imagem (NOLOCK) GROUP BY id_veiculo_tempo_real) vtri
				ON  vtri.id_veiculo_tempo_real = vtr.id
		   LEFT JOIN (SELECT id_veiculo_tempo_real, COUNT(*) AS qtde FROM muralha.alerta_veiculo (NOLOCK) GROUP BY id_veiculo_tempo_real) AS a
				ON  a.id_veiculo_tempo_real = vtr.id
	GROUP BY
		   vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   RTRIM(lv.nome),
		   vtr.id_pista,
		   lv.cod_pista_alternativo,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END,
		   COALESCE(vtr.classificacao,''),
		   vtr.enviado_cliente,
		   a.id_veiculo_tempo_real
