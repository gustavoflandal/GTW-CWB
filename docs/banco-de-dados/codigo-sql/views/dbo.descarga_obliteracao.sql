

CREATE VIEW [dbo].[descarga_obliteracao] AS
SELECT
	iob.id_infracao,
	iob.altura,
	iob.largura,
	iob.x,
	iob.y
FROM veiculo_descarga d  (nolock)
	INNER JOIN infracao i  (nolock)
		ON i.id_veiculo = d.id_veiculo
	INNER JOIN infracao_obliteracao iob  (nolock)
		ON iob.id_infracao = i.id_infracao


