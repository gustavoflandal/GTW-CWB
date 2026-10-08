

CREATE PROCEDURE [dbo].[spu_info_lista_local]
	@id_grupo int
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
			END AS BIT) AS ativo,
		CAST(CASE 
				WHEN (l.data_inicio <= GETDATE()) 
					THEN 1 
				ELSE 
					0 
			END AS BIT ) AS em_operacao
	FROM local_vigente l (nolock)
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
	WHERE	ce.flag_opcao & 1 <> 0		-- equipamento ativo
		AND	ce.id_grupo_equipamento = @id_grupo 
	ORDER BY 1
	




