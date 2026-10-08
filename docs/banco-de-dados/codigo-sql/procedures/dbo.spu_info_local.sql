
CREATE      PROCEDURE [dbo].[spu_info_local]
	@serie_equipamento int = 0
AS
	
	SELECT 
		l.id_local AS id_local, 
		l.nome AS nome, 
		ce.serie_equipamento AS serie, 
		data_atualizacao AS data_atualizacao, 
		controle_online AS controle_online, 
		CAST(CASE 
				WHEN (ce.flag_opcao & 1 <> 0) 
					THEN 1 
				ELSE 
					0 
			END AS BIT) AS ativo
	FROM local_vigente l (nolock)
		INNER JOIN configuracao_equipamento ce (nolock) 
			ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
	WHERE 	ce.serie_equipamento = @serie_equipamento 
		OR	@serie_equipamento = 0
		AND ce.flag_opcao & 1 <> 0 -- EQUIPAMENTO ATIVO
	ORDER BY 
		id_local



