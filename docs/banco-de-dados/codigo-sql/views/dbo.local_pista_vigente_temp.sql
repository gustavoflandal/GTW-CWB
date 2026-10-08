
CREATE VIEW [dbo].[local_pista_vigente_temp]
AS
	SELECT l.id_local,
		   l.sequencia_local,
		   cep.nome_pista AS nome,
		   cep.id_pista,
		   CASE WHEN (cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0) AND cep.id_pista = cep.cod_pista_alternativo
				THEN 1
       			WHEN cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0 OR cep.pista_7_transversal > 0 OR cep.pista_8_transversal > 0
       			THEN cep.cod_pista_alternativo ELSE cep.id_pista
		   END id_pista_regra,
		   cep.cod_pista_alternativo,
		   cep.cod_pista,
		   cep.cod_pista_prodam,
		   l.id_configuracao_equipamento,
		   ce.serie_equipamento,
		   su.usuario,
		   ce.em_operacao,
		   l.posicao_lat,
		   l.posicao_lon,
		   ce.data_inicio,
		   CASE WHEN ( CAST(ISNULL(cep.faixa_exclusiva_direita,0) AS INT) + CAST(ISNULL(cep.faixa_exclusiva_esquerda,0) AS INT) ) > 0 THEN 1 ELSE 0 END AS faixa_exclusiva,
		   (CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END) AS desativado,
		   MAX(a.data) AS data_afericao,
		   LTRIM(STR(ce.serie_equipamento)) AS codigo_equipamento,
		   0 AS codigo_GIT,
		   CASE WHEN cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0 OR cep.pista_7_transversal > 0 OR cep.pista_8_transversal > 0
				THEN 1
				ELSE 0
		   END AS pista_transversal
	FROM   local AS l (NOLOCK) 
		   INNER JOIN dbo.configuracao_equipamento AS ce (NOLOCK)
				ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento
					AND ce.ativo = 1
		   INNER JOIN dbo.sis_usuario su (NOLOCK)
				ON  ce.id_usuario = su.id_usuario
		   INNER JOIN configuracao_equipamento_pista cep (NOLOCK)
				ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
		   LEFT OUTER JOIN dbo.configuracao_equipamento_afericao AS a (nolock)     
				ON  a.id_configuracao_equipamento = ce.id_configuracao_equipamento     
					AND (a.id_pista IS NULL OR a.id_pista = cep.id_pista)
	 --WHERE l.id_local = 14
	GROUP BY
		   l.id_local,
		   l.sequencia_local,
		   cep.nome_pista,
		   l.id_configuracao_equipamento,
		   ce.serie_equipamento,
		   su.usuario,
		   ce.em_operacao,
		   l.posicao_lat,
		   l.posicao_lon,
		   ce.data_inicio,
		   cep.id_pista,
		   CASE WHEN (cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0) AND cep.id_pista = cep.cod_pista_alternativo
       			THEN 1
       			WHEN cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0 OR cep.pista_7_transversal > 0 OR cep.pista_8_transversal > 0
       			THEN cep.cod_pista_alternativo ELSE cep.id_pista
		   END,
		   cep.cod_pista,    
		   cep.cod_pista_prodam,    
		   cep.cod_pista_alternativo,
		   cep.faixa_exclusiva_direita,
		   cep.faixa_exclusiva_esquerda,
		   (CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END),
		   CASE WHEN cep.pista_1_transversal > 0 OR cep.pista_2_transversal > 0 OR cep.pista_3_transversal > 0 OR cep.pista_4_transversal > 0 AND cep.pista_5_transversal > 0 OR cep.pista_6_transversal > 0 OR cep.pista_7_transversal > 0 OR cep.pista_8_transversal > 0
       			THEN 1
				ELSE 0
		   END
	--ORDER BY
	--	   l.id_local,
	--       cep.id_pista
