

CREATE VIEW [dbo].[descarga_pista] AS

SELECT
	(sub.id_configuracao_equipamento * 10) + sub.id_pista_gtw AS id_pista_derby,
	sub.*
FROM
	(
	SELECT DISTINCT
		lcl.id_configuracao_equipamento,
		v.pista AS id_pista_gtw,
		ce.serie_equipamento,
		cep.cod_pista,
		RTRIM(cep.nome_pista) AS nome_pista,
		cea.data AS data_afericao,
		cep.cod_pista_alternativo,
		cep.cod_pista_prodam AS cod_pista2
	FROM veiculo_descarga d (nolock)
		INNER JOIN veiculo v (nolock)
			ON v.id_veiculo = d.id_veiculo
		INNER JOIN local lcl (nolock)
			ON v.id_local = lcl.id_local
			AND v.sequencia_local = lcl.sequencia_local
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		INNER JOIN configuracao_equipamento_afericao cea (nolock)
			ON cea.id_configuracao_equipamento = lcl.id_configuracao_equipamento
			AND cea.id_pista = v.pista
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = lcl.id_configuracao_equipamento
			AND cep.id_pista = v.pista
	) AS sub


