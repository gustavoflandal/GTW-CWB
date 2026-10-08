CREATE VIEW [dbo].[v_veiculo_completo_gtw]
AS
	SELECT vtr.id_veiculo_unic AS id,
		   vtr.id_veiculo,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   RTRIM(lv.nome) AS nome,
		   vtr.pista AS id_pista,
		   lv.cod_pista_alternativo AS faixa,
		   lv.posicao_lat AS latitude,
		   lv.posicao_lon AS longitude,
		   vtr.comprimento,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END AS velocidade,
		   --CASE WHEN vtr.classificacao LIKE '[a-zA-Z]' THEN COALESCE(vtr.classificacao,'') ELSE 'P' END AS classificacao,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(cv.id_classe,'') ELSE 'P' END AS id_classe,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(RTRIM(cv.descricao),'') ELSE 'Veíc. Passeio' END AS classificacao,
		   NULL AS enviado_cliente,
		   SUM(CASE WHEN vtri.id_veiculo IS NOT NULL THEN 1 ELSE 0 END) AS com_imagem,
		   CASE WHEN lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL THEN 1 ELSE 0 END AS possui_coordenadas,
		   vtr.com_pesagem,
		   0 AS possui_alerta,
		   cad.id_marca_cet AS id_marca, cad.marca_cet AS marca,
		   cad.id_marca AS id_modelo, cad.marca AS modelo,
		   cad.id_cor, cad.cor,
		   cad.ano AS ano_modelo,
		   cad.id_tipo, cad.tipo,
		   cad.id_localidade, cad.localidade, cad.uf,
		   cad.ano_fabricacao,cad.renavam,cad.chassi,cad.restricao,
		   vp.pbt, vpe.pbtc, vpe.qtde_eixos AS numero_eixos, NULL AS rodagem_dupla, NULL AS categoria,
		   vpe.E1, vpe.E2, vpe.E3, vpe.E4, vpe.E5, vpe.E6, vpe.E7, vpe.E8, vpe.E9,
		   vpde.distancia_E1E2, vpde.distancia_E2E3, vpde.distancia_E3E4, vpde.distancia_E4E5, vpde.distancia_E5E6, vpde.distancia_E6E7, vpde.distancia_E7E8, vpde.distancia_E8E9,
		   vtr.placa_mercosul,
		   vtr.classificacao AS classificacao_art96
	FROM   veiculo_pesquisa vtr (NOLOCK)
		   INNER JOIN local_pista_vigente lv (NOLOCK)
				ON  lv.id_local = vtr.id_local
					AND lv.id_pista = vtr.pista
		   LEFT JOIN classe_veiculo cv
				ON  cv.id_classe = vtr.id_classe
		   LEFT JOIN (SELECT id_veiculo FROM veiculo_imagem (NOLOCK) GROUP BY id_veiculo) vtri
				ON  vtri.id_veiculo = vtr.id_veiculo
		   LEFT JOIN v_veiculo_pesagem vp
				ON  vp.id_veiculo_unic = vtr.id_veiculo_unic
		   LEFT JOIN v_veiculo_pesagem_eixo vpe
				ON  vpe.id_veiculo_unic = vtr.id_veiculo_unic
		   LEFT JOIN v_veiculo_pesagem_distancia_eixos vpde
				ON  vpde.id_veiculo_unic = vtr.id_veiculo_unic
		   LEFT JOIN cadastro_veiculo cad
				ON  cad.placa = vtr.placa
	--WHERE  vtr.data BETWEEN '2025-05-15 00:00:00' AND '2025-06-24 14:29:59' AND cad.id_marca_cet IS NOT NULL
	GROUP BY
		   vtr.id_veiculo_unic,
		   vtr.id_veiculo,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   RTRIM(lv.nome),
		   vtr.pista,
		   lv.cod_pista_alternativo,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   vtr.comprimento,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END,
		   cv.id_classe,
		   cv.descricao,
		   cad.id_marca_cet, cad.marca_cet,
		   cad.id_marca, cad.marca,
		   cad.id_cor, cad.cor,
		   cad.ano,
		   cad.id_tipo, cad.tipo, cad.tipo_cet,
		   cad.id_localidade, cad.localidade, cad.uf,
		   cad.ano_fabricacao,cad.renavam,cad.chassi,cad.restricao,
		   vtr.com_pesagem,
		   vp.pbt, vpe.pbtc, vpe.qtde_eixos,vtr.rodagem_dupla,vtr.categoria,
		   vpe.E1, vpe.E2, vpe.E3, vpe.E4, vpe.E5, vpe.E6, vpe.E7, vpe.E8, vpe.E9,
		   vpde.distancia_E1E2, vpde.distancia_E2E3, vpde.distancia_E3E4, vpde.distancia_E4E5, vpde.distancia_E5E6, vpde.distancia_E6E7, vpde.distancia_E7E8, vpde.distancia_E8E9,
		   vtr.placa_mercosul,
		   vtr.classificacao
