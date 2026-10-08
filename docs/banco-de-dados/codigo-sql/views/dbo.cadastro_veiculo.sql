CREATE VIEW [dbo].[cadastro_veiculo]
AS
SELECT
	cv.placa, 
	cv.id_marca, 
	cmar.descricao AS marca, 
	cmarcet.id_marca_cet, 
	cmarcet.descricao AS marca_cet, 
	cc.id_cor,
	cc.descricao AS cor, 
	cv.ano_modelo AS ano, 
	ce.id_especie, 
	ce.descricao AS especie, 
	ct.id_tipo,
	ct.descricao AS tipo, 
	CASE WHEN ct.id_tipo IS NULL THEN 'N/D'
		 WHEN ct.id_tipo IN (7,8,10,11,14,17,18,20,22,26) THEN 'Pesado'
		 ELSE 'Leve'
	END AS classificacao_cad,
	ctipocet.descricao AS tipo_cet, 
	ca.id_categoria,
	ca.descricao AS categoria, 
	cs.descricao AS situacao, 
	cl.id_localidade,
	cl.nome AS localidade, 
	cl.uf AS uf, 
	cv.atualizado_em AS data_atualizacao, 
	cmp.id_marca_cet AS id_marca_processo, 
	_cmp.descricao AS marca_processo, 
	cep.id_especie AS id_especie_processo, 
	_cep.descricao AS especie_processo, 
	cup.uf AS uf_processo,
	cvia.ano_fabricacao,cvia.renavam,cvia.chassi,cvia.restricao,cvia.tipo_combustivel
FROM dbo.cad_veiculo AS cv (NOLOCK) 
	LEFT JOIN cad_marca AS cmar (NOLOCK) 
		ON cmar.id_marca = cv.id_marca 
	LEFT JOIN cad_marca_cet AS cmarcet (NOLOCK) 
		ON cmarcet.id_marca_cet = cv.id_marca_cet 
	LEFT JOIN cad_marca_cet_processo AS cmp (NOLOCK) 
		ON cmp.placa = cv.placa 
	LEFT JOIN cad_marca_cet AS _cmp (NOLOCK) 
		ON _cmp.id_marca_cet = cmp.id_marca_cet 
	LEFT JOIN cad_cor AS cc (NOLOCK) 
		ON cc.id_cor = cv.id_cor 
	LEFT JOIN cad_especie AS ce (NOLOCK) 
		ON ce.id_especie = cv.id_especie 
	LEFT JOIN cad_especie_processo AS cep (NOLOCK) 
		ON cep.placa = cv.placa 
	LEFT JOIN cad_especie AS _cep (NOLOCK) 
		ON _cep.id_especie = cep.id_especie 
	LEFT JOIN cad_uf_processo AS cup (NOLOCK) 
		ON cup.placa = cv.placa 
	LEFT JOIN cad_tipo AS ct (NOLOCK) 
		ON ct.id_tipo = cv.id_tipo 
	LEFT JOIN cad_tipo_cet AS ctipocet (NOLOCK) 
		ON ctipocet.id_tipo_cet = cv.id_tipo_cet 
	LEFT JOIN cad_categoria AS ca (NOLOCK) 
		ON ca.id_categoria = cv.id_categoria 
	LEFT JOIN cad_situacao AS cs (NOLOCK) 
		ON cs.id_situacao = cv.id_situacao 
	LEFT JOIN cad_localidade AS cl (NOLOCK) 
		ON cl.id_localidade = cv.id_localidade
	LEFT JOIN cad_veiculo_info_aux cvia (NOLOCK)
		ON cvia.placa = cv.placa
