

CREATE VIEW [dbo].[descarga_imagem] AS
SELECT
	vi.id_veiculo,
	vi.id_imagem,
	ti.nome AS tipo,
	ti.numero,
	img.imagem,
	vd.id_pasta,
	vi.id_imagem_local,
	(
		CONVERT(CHAR(8), v.data, 112) + -- yyyymmdd
		REPLACE(CONVERT(CHAR(8), v.data, 108), ':', '') + -- hh:mi:ss
		'_' +
		CONVERT(VARCHAR(10), cep.cod_pista) + 
		'_' +
		CONVERT(VARCHAR(10), cep.cod_pista_alternativo) +
		'_' +
		CONVERT(VARCHAR(10), vi.id_imagem) +
		'.jpg' 
	) AS nome_arquivo,
	NULL AS md5_imagem
FROM veiculo_descarga vd (nolock)
	JOIN veiculo v (nolock)
		ON v.id_veiculo = vd.id_veiculo
	JOIN veiculo_imagem vi (nolock)
		ON vi.id_veiculo = vd.id_veiculo
	JOIN imagem_info iif (nolock)
		ON iif.id_imagem = vi.id_imagem
	JOIN tipo_imagem ti (nolock)
		ON ti.id_tipo_imagem = iif.id_tipo_imagem
	JOIN imagem img (nolock)
		ON img.id_imagem = iif.id_imagem
	JOIN local lcl (nolock)
		ON lcl.id_local = v.id_local
		AND lcl.sequencia_local = v.sequencia_local
	JOIN configuracao_equipamento_pista cep (nolock)
		ON cep.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		AND cep.id_pista = v.pista





