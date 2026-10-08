

CREATE VIEW [dbo].[descarga_veiculo] AS
SELECT
	vd.id_veiculo,
	v.segundos,
	v.sequencia_local,
	v.velocidade,
	v.id_arquivo,
	v.data,
	v.id_veiculo_local,
	v.id_local,
	v.id_veiculo_unic,
	(l.id_configuracao_equipamento * 10) + v.pista AS id_pista_derby,
	v.flag,
	v.placa,
	v.ocupacao,
	v.comprimento,
	v.id_classe,
	vd.id_descarga
FROM veiculo_descarga vd (nolock)
	INNER JOIN veiculo v (nolock)
		ON v.id_veiculo = vd.id_veiculo
	INNER JOIN local l (nolock)
		ON v.id_local = l.id_local
		AND v.sequencia_local = l.sequencia_local
WHERE
	vd.id_veiculo IN (SELECT id_veiculo 
						FROM veiculo_imagem (nolock))


