

CREATE VIEW [dbo].[descarga_cad_veiculo] AS
SELECT
	i.placa,
	RTRIM(COALESCE(cmcet2.descricao, cmcet.descricao, cm.descricao, '')) AS descricao_marca,
	RTRIM(COALESCE(ce2.descricao, ce.descricao, '')) AS descricao_especie
FROM veiculo_descarga d (nolock)
	JOIN infracao i (nolock)
		ON i.id_veiculo = d.id_veiculo
	LEFT JOIN cad_veiculo cv (nolock)
		ON i.placa = cv.placa
	LEFT JOIN cad_marca cm (nolock)
		ON cm.id_marca = cv.id_marca
	LEFT JOIN cad_marca_cet cmcet (nolock)
		ON cmcet.id_marca_cet = cv.id_marca_cet
	LEFT JOIN cad_marca_cet_processo cmcetp (nolock)
		ON cmcetp.placa = i.placa
	LEFT JOIN cad_marca_cet cmcet2 (nolock)
		ON cmcet2.id_marca_cet = cmcetp.id_marca_cet
	LEFT JOIN cad_especie ce (nolock)
		ON ce.id_especie = cv.id_especie
	LEFT JOIN cad_especie_processo cep (nolock)
		ON cep.placa = i.placa
	LEFT JOIN cad_especie ce2 (nolock)
		ON ce2.id_especie = cep.id_especie	
WHERE
	i.placa IS NOT NULL


