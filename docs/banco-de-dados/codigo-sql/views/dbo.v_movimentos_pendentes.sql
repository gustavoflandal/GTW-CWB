CREATE VIEW [dbo].[v_movimentos_pendentes] AS

	SELECT rem.id_remessa
		  ,codigo_externo
		  ,rem.data
		  ,total_infracao
		  ,auto_inicial
		  ,auto_final
		  ,rem.id_enquadramento
		  ,rem.id_processo
		  ,RTRIM(tipo) AS tipo
		  ,revisao
		  ,rem.reprovado
		  ,rem.data_exportacao
		  ,rem.data_confirmacao
		  ,rem.id_usuario
		  ,data_validacao
		  ,rem.data_inicial
		  ,rem.data_final
		  ,rem.id_movimento_arquivo
		  ,CASE WHEN EXISTS(SELECT TOP(1) 1 FROM remessa_amostragem (NOLOCK) WHERE id_remessa = rem.id_remessa) THEN 1 ELSE 0 END AS amostra
		  ,SUM(CASE WHEN i.id_processo = 3 THEN 1 ELSE 0 END) AS infracoes_validaveis
		  ,COALESCE(ri.tipo_iteracao,0) AS qtde
		  ,suri.nome AS nome_usuario_janela
		  --,''  AS nome_usuario_janela
		  ,ISNULL(amostraremessa.infracoes, 0) as total_real_amostra
		  ,ISNULL(amostraremessa.validaveis, 0) as validaveis_real_amostra
	--INTO movimentos_pendentes
	FROM   remessa rem (NOLOCK)
		   JOIN infracao_remessa ir (NOLOCK)
				ON  rem.id_remessa = ir.id_remessa
		   JOIN infracao i (NOLOCK)
				ON  ir.id_infracao = i.id_infracao
		   LEFT JOIN remessa_iteracao ri (NOLOCK)
				ON rem.id_remessa = ri.id_remessa
				AND ri.data_fim IS NULL --AND ri.tipo_iteracao = 1
		   LEFT JOIN sis_usuario suri (NOLOCK) 
				ON ri.id_usuario = suri.id_usuario
		   LEFT JOIN (
						SELECT ra.id_remessa
							  ,COUNT(DISTINCT ra.id_infracao) AS infracoes
							  ,SUM(CASE WHEN ip.id_infracao IS NULL THEN 1 ELSE 0 END) AS validaveis
						FROM   remessa r (NOLOCK) 
						JOIN   remessa_amostragem ra (NOLOCK)
								ON	 r.id_remessa = ra.id_remessa 
						LEFT JOIN infracao_processo ip (NOLOCK)
								ON  ra.id_infracao = ip.id_infracao
									AND ip.id_processo = 3
									AND ip.status_processo = 0
						WHERE r.data_confirmacao IS NULL 
						GROUP BY
							   ra.id_remessa
   	  	   ) amostraremessa
				ON  amostraremessa.id_remessa = rem.id_remessa
	WHERE  rem.data_confirmacao IS NULL
	GROUP BY
		   rem.id_remessa
		  ,codigo_externo
		  ,rem.data
		  ,total_infracao
		  ,auto_inicial
		  ,auto_final
		  ,rem.id_enquadramento
		  ,rem.id_processo
		  ,tipo
		  ,revisao
		  ,rem.reprovado
		  ,rem.data_exportacao
		  ,rem.data_confirmacao
		  ,rem.id_usuario
		  ,ri.tipo_iteracao
		  ,suri.nome
		  ,data_validacao
		  ,rem.data_inicial
		  ,rem.data_final
		  ,rem.id_movimento_arquivo
		  ,ISNULL(amostraremessa.infracoes, 0)
		  ,ISNULL(amostraremessa.validaveis, 0)
--ORDER BY data_inicial
