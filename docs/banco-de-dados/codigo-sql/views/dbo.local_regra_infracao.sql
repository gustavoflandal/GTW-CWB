

CREATE VIEW [dbo].[local_regra_infracao]
AS
SELECT
	lcl.id_configuracao_equipamento,
	lcl.id_local,
	cep.id_pista,
	ceri.tipo,
	ce.data_modificacao,
	ce.data_inicio,
	ce.data_fim,
	ce.serie_equipamento,
	ceri.usar_panoramica,
	cep.cod_pista
FROM local lcl (nolock)
	INNER JOIN configuracao_equipamento ce (nolock)
		ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_pista cep (nolock)
		ON cep.id_configuracao_equipamento = lcl.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_regra_infracao ceri (nolock)
		ON (ceri.id_configuracao_equipamento = lcl.id_configuracao_equipamento)
		AND (ceri.id_pista = cep.id_pista OR ceri.id_pista IS NULL)
UNION
SELECT
	lcl.id_configuracao_equipamento,
	lcl.id_local,
	cep.id_pista,	
	'RO',
	ce.data_modificacao,
	ce.data_inicio,
	ce.data_fim,
	ce.serie_equipamento,
	CAST (0 AS BIT) AS usar_panoramica, -- Config. de rodízio não possui a flag. Fica hardcoded.
	cep.cod_pista
FROM local lcl (nolock)
	JOIN configuracao_equipamento ce (nolock)
		ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
	JOIN configuracao_equipamento_pista cep (nolock)
		ON cep.id_configuracao_equipamento = lcl.id_configuracao_equipamento
WHERE
	ce.flag_opcao & 4 = 4 -- RODIZIO


