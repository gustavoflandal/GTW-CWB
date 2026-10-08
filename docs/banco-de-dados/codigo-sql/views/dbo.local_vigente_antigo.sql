CREATE VIEW [dbo].[local_vigente_antigo]
AS

	SELECT	L.id_local, 
			L.sequencia_local, 
			L.nome, 
			MAX(a.data) AS data_afericao, 
			L.data_atualizacao, 
			L.id_configuracao_equipamento, 
			L.tipo, 
			L.id_localidade, 
			ce.serie_equipamento, 
			dbo.sis_usuario.usuario, 
			ce.em_operacao,
			L.posicao_lat,
			L.posicao_lon,
			ce.data_inicio,
			ce.data_fim,
			(CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END) AS desativado -- EQUIPAMENTO desativado
	FROM local AS L (nolock)
		INNER JOIN (
						SELECT conf.serie_equipamento,
							   MAX(conf.id_configuracao_equipamento) AS id_configuracao_equipamento,
							   MAX(conf.data_modificacao) AS data_modificacao
						FROM   configuracao_equipamento conf (NOLOCK)
							   INNER JOIN local_vigente lv (NOLOCK)
									ON  lv.serie_equipamento = conf.serie_equipamento
						WHERE  CAST(conf.data_modificacao AS DATE) < '2017-01-24'
						GROUP BY
							   conf.serie_equipamento
		) config
			ON  config.id_configuracao_equipamento = L.id_configuracao_equipamento
		INNER JOIN dbo.configuracao_equipamento AS ce (nolock) 
			ON ce.id_configuracao_equipamento = config.id_configuracao_equipamento 
			--AND ce.ativo = 1 
		INNER JOIN dbo.sis_usuario (nolock) 
			ON ce.id_usuario = dbo.sis_usuario.id_usuario 
		LEFT OUTER JOIN	dbo.configuracao_equipamento_afericao AS a (nolock) 
			ON a.id_configuracao_equipamento = ce.id_configuracao_equipamento 
			--AND a.id_afericao = 1
	GROUP BY 
			L.id_local, 
			L.sequencia_local, 
			L.nome, 
			L.data_atualizacao, 
			L.id_configuracao_equipamento, 
			L.tipo, 
			L.id_localidade, 
			ce.serie_equipamento, 
			dbo.sis_usuario.usuario, 
			ce.em_operacao,
			L.posicao_lat,
			L.posicao_lon,
			ce.data_inicio,
			ce.data_fim,
			ce.flag_opcao
