CREATE VIEW [dbo].[local_vigente]  
AS  
	SELECT L.id_local,   
		   L.sequencia_local,   
		   L.nome,   
		   MAX(a.data) AS data_afericao,   
		   L.data_atualizacao,   
		   L.id_configuracao_equipamento,   
		   L.tipo,   
		   --L.id_localidade,   
		   lmr.id_localidade,
		   ce.serie_equipamento,   
		   dbo.sis_usuario.usuario,   
		   ce.em_operacao,  
		   L.posicao_lat,  
		   L.posicao_lon,  
		   ce.data_inicio,  
		   ce.data_fim,  
		   (CASE WHEN ce.flag_opcao & 1 = 0 THEN 1 ELSE 0 END) AS desativado ,-- EQUIPAMENTO desativado  
		   ce.CodigoEquipCliente AS codigos_equipamentos,
		   lmr.id_regiao,
		   L.localidade_desc
	FROM   local AS L (nolock)  
		   INNER JOIN dbo.configuracao_equipamento AS ce (nolock)   
				ON  ce.id_configuracao_equipamento = L.id_configuracao_equipamento   
					AND ce.ativo = 1   
		   INNER JOIN dbo.sis_usuario (nolock)   
				ON  ce.id_usuario = dbo.sis_usuario.id_usuario   
		   LEFT OUTER JOIN dbo.configuracao_equipamento_afericao AS a (nolock)   
				ON  a.id_configuracao_equipamento = ce.id_configuracao_equipamento   
					--AND a.id_afericao = 1  
		   LEFT JOIN local_municipio_regiao lmr
				ON  lmr.id_local = l.id_local
	GROUP BY
		   L.id_local,   
		   L.sequencia_local,   
		   L.nome,   
		   L.data_atualizacao,   
		   L.id_configuracao_equipamento,   
		   L.tipo,   
		   --L.id_localidade,   
		   lmr.id_localidade,
		   ce.serie_equipamento,   
		   dbo.sis_usuario.usuario,   
		   ce.em_operacao,  
		   L.posicao_lat,  
		   L.posicao_lon,  
		   ce.data_inicio,  
		   ce.data_fim,  
		   ce.flag_opcao,
		   ce.CodigoEquipCliente,
		   lmr.id_regiao,
		   L.localidade_desc
