CREATE VIEW [dbo].[local_pista_vigente]      
AS      
	SELECT l.id_local,      
		   l.sequencia_local,      
		   cep.nome_pista AS nome,      
		   cep.id_pista,      
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
		   MAX(a.data) AS data_afericao  ,  
		   ce.CodigoEquipCliente AS codigo_equipamento,
		   0 AS codigo_GIT ,
		   lmr.id_localidade,
		   lmr.id_regiao,
		   cep.sentido
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
		   LEFT JOIN local_municipio_regiao lmr
				ON lmr.id_local = l.id_local
	WHERE  cep.pista_1_transversal = 0 AND cep.pista_2_transversal = 0 AND cep.pista_3_transversal = 0 AND cep.pista_4_transversal = 0      
			AND cep.pista_5_transversal = 0 AND cep.pista_6_transversal = 0 AND cep.pista_7_transversal = 0 AND cep.pista_8_transversal = 0      
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
		   cep.cod_pista,    
		   cep.cod_pista_prodam,    
		   cep.cod_pista_alternativo,      
		   cep.faixa_exclusiva_direita,      
		   cep.faixa_exclusiva_esquerda,      
		   (CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END)    ,
		   lmr.id_localidade,
		   lmr.id_regiao,
		   ce.CodigoEquipCliente,
		   cep.sentido
