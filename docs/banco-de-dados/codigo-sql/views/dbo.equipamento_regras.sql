
CREATE VIEW [dbo].[equipamento_regras] AS

SELECT
	-- Descobre as regras-infração de cada pista
	lcl.id_local,
	lcl.sequencia_local,
	cep.id_configuracao_equipamento,
	cep.id_pista,
	cep.cod_pista,
	ceri.tipo
FROM local lcl (nolock)
	INNER JOIN configuracao_equipamento_pista cep (nolock)
		ON lcl.id_configuracao_equipamento = cep.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_regra_infracao ceri (nolock)
		ON  cep.id_configuracao_equipamento = ceri.id_configuracao_equipamento
		AND (ceri.id_pista IS NULL OR cep.id_pista = ceri.id_pista)
WHERE
	ceri.ativo = 1
UNION
-- Descobre se o equipamento (como um todo) infraciona rodízio
SELECT
	lcl.id_local,
	lcl.sequencia_local,
	cep.id_configuracao_equipamento,	
	cep.id_pista,
	cep.cod_pista,
	'RO' AS tipo
FROM local lcl (nolock)
	INNER JOIN configuracao_equipamento ce (nolock)
		ON lcl.id_configuracao_equipamento = ce.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_pista cep (nolock)
		ON cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
WHERE
	ce.flag_opcao & 4 = 4


