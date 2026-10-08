
CREATE PROCEDURE [dbo].[spu_info_pista]
	@serie_equipamento int = 0
AS
	SELECT 
		ce.serie_equipamento AS serie, 
		id_pista AS pista 
	FROM configuracao_equipamento_pista cep (nolock)
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = cep.id_configuracao_equipamento
		INNER JOIN local_vigente l (nolock)
			ON l.id_configuracao_equipamento = ce.id_configuracao_equipamento
	WHERE	ce.serie_equipamento = @serie_equipamento 
		or @serie_equipamento = 0
	ORDER BY 
		1








