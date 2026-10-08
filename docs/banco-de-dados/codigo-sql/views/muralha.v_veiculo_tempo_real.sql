CREATE VIEW [muralha].[v_veiculo_tempo_real]
AS
	SELECT vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   RTRIM(lv.nome) AS nome,
		   vtr.id_pista,
		   lv.cod_pista_alternativo AS faixa,
		   lv.posicao_lat AS latitude,
		   lv.posicao_lon AS longitude,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END AS velocidade,
		   --CASE WHEN vtr.classificacao LIKE '[a-zA-Z]' THEN COALESCE(vtr.classificacao,'') ELSE 'P' END AS classificacao,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(cv.id_classe,'') ELSE 'P' END AS id_classe,
		   CASE WHEN cv.id_classe LIKE '[a-zA-Z]' THEN COALESCE(RTRIM(cv.descricao),'') ELSE 'Veíc. Passeio' END AS classificacao,
		   vtr.enviado_cliente,
		   SUM(CASE WHEN vtri.id_veiculo_tempo_real IS NOT NULL THEN 1 ELSE 0 END) AS com_imagem,
		   CASE WHEN lv.posicao_lat IS NOT NULL AND lv.posicao_lon IS NOT NULL THEN 1 ELSE 0 END AS possui_coordenadas,
		   CASE WHEN a.id_veiculo_tempo_real IS NOT NULL THEN 1 ELSE 0 END AS possui_alerta,
		   cad.id_marca_cet AS id_marca, cad.marca_cet AS marca,
		   cad.id_marca AS id_modelo, cad.marca AS modelo,
		   cad.id_cor, cad.cor,
		   cad.ano AS ano_modelo,
		   cad.id_tipo, cad.tipo,
		   cad.id_localidade, cad.localidade, cad.uf,
		   cad.ano_fabricacao,cad.renavam,cad.chassi,cad.restricao,
		   vtr.numero_eixos,vtr.rodagem_dupla,vtr.categoria,
		   vtr.placa_mercosul
	FROM   muralha.veiculo_tempo_real vtr (NOLOCK)
		   INNER JOIN local_pista_vigente lv (NOLOCK)
				ON  lv.id_local = vtr.id_local
					AND lv.id_pista = vtr.id_pista
		   LEFT JOIN classe_veiculo cv
				ON  cv.id_classe = vtr.classificacao
		   LEFT JOIN (SELECT id_veiculo_tempo_real FROM muralha.veiculo_tempo_real_imagem (NOLOCK) GROUP BY id_veiculo_tempo_real) vtri
				ON  vtri.id_veiculo_tempo_real = vtr.id
		   LEFT JOIN (SELECT id_veiculo_tempo_real, COUNT(*) AS qtde FROM muralha.alerta_veiculo (NOLOCK) GROUP BY id_veiculo_tempo_real) AS a
				ON  a.id_veiculo_tempo_real = vtr.id
		   LEFT JOIN cadastro_veiculo cad
				ON  cad.placa = vtr.placa
	--WHERE  vtr.data BETWEEN '2025-05-15 00:00:00' AND '2025-06-24 14:29:59' AND cad.id_marca_cet IS NOT NULL
	GROUP BY
		   vtr.id,
		   vtr.placa,
		   vtr.data,
		   vtr.id_local,
		   lv.serie_equipamento,
		   lv.codigo_equipamento,
		   RTRIM(lv.nome),
		   vtr.id_pista,
		   lv.cod_pista_alternativo,
		   lv.posicao_lat,
		   lv.posicao_lon,
		   CASE WHEN vtr.velocidade = 0 THEN NULL ELSE vtr.velocidade END,
		   cv.id_classe,
		   cv.descricao,
		   vtr.enviado_cliente,
		   a.id_veiculo_tempo_real,
		   cad.id_marca_cet, cad.marca_cet,
		   cad.id_marca, cad.marca,
		   cad.id_cor, cad.cor,
		   cad.ano,
		   cad.id_tipo, cad.tipo, cad.tipo_cet,
		   cad.id_localidade, cad.localidade, cad.uf,
		   cad.ano_fabricacao,cad.renavam,cad.chassi,cad.restricao,
		   vtr.numero_eixos,vtr.rodagem_dupla,vtr.categoria,
		   vtr.placa_mercosul
