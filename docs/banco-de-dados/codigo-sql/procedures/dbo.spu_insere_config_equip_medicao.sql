CREATE PROCEDURE [dbo].[spu_insere_config_equip_medicao]
AS

	/*******************************************************************************************************************************
	*********************************************************** PASSO 1 ************************************************************
	****************************** INSERE LOCAIS NOVOS NA TABELA dbo.configuracao_equipamento_medicao ******************************
	*******************************************************************************************************************************/
	INSERT INTO dbo.configuracao_equipamento_medicao
	SELECT local_vigente.id_local
	      ,config_equip_pista.cod_pista
	      ,local_vigente.nome AS descricao
		  ,CONVERT(VARCHAR(10) ,local_vigente.data_inicio ,120) AS data_inicio
	      ,produto.id_produto
	      ,config_equip_pista.cod_pista_alternativo
	      ,config_equip_pista.cod_pista_prodam
	      ,local_vigente.serie_equipamento
		  ,1 AS qtde_equipamentos -- Este valor será atualizado no PASSO 3 desta procedure
		  ,local_vigente.id_local
		  ,config_equip_pista.cod_area
		  ,ISNULL(config_equip_pista.cod_pista_tarja, config_equip_pista.cod_pista_alternativo) AS cod_pista_tarja
		  ,ISNULL(config_equip_pista.entre_faixa, 0) AS entre_faixa
		  ,config_equip_pista.id_pista
		  ,1 AS atualizar
	FROM   dbo.local_vigente local_vigente (NOLOCK)
	       INNER JOIN dbo.configuracao_equipamento config_equip (NOLOCK)
	            ON  config_equip.id_configuracao_equipamento = local_vigente.id_configuracao_equipamento
	       INNER JOIN dbo.produto produto (NOLOCK)
	            ON  produto.id_produto = config_equip.id_produto
	       INNER JOIN dbo.configuracao_equipamento_pista config_equip_pista (NOLOCK)
	            ON  config_equip_pista.id_configuracao_equipamento = config_equip.id_configuracao_equipamento
	       LEFT JOIN dbo.configuracao_equipamento_medicao config_equip_med (NOLOCK)
	            ON  config_equip_med.id_local = local_vigente.id_local
	WHERE  config_equip.data_inicio <= GETDATE()
		   AND local_vigente.desativado = 0
	       AND config_equip_med.id_configuracao_equipamento_medicao IS NULL
		   AND (
					(config_equip_pista.pista_1_transversal = 0)
					AND
					(config_equip_pista.pista_2_transversal = 0)
					AND
					(config_equip_pista.pista_3_transversal = 0)
					AND
					(config_equip_pista.pista_4_transversal = 0)
					AND
					(config_equip_pista.pista_5_transversal = 0)
					AND
					(config_equip_pista.pista_6_transversal = 0)
					AND
					(config_equip_pista.pista_7_transversal = 0)
					AND
					(config_equip_pista.pista_8_transversal = 0)
			   )
	GROUP BY
	       local_vigente.id_local
	      ,config_equip_pista.cod_pista
	      ,local_vigente.nome
	      ,CONVERT(VARCHAR(10) ,local_vigente.data_inicio ,120)
	      ,produto.id_produto
	      ,config_equip_pista.cod_pista_alternativo
	      ,config_equip_pista.cod_pista_prodam
	      ,local_vigente.serie_equipamento
		  ,config_equip_pista.cod_area
		  ,config_equip_pista.cod_pista_tarja
		  ,config_equip_pista.entre_faixa
		  ,config_equip_pista.id_pista



	/*******************************************************************************************************************************
	*********************************************************** PASSO 2 ************************************************************
	*************************** ATUALIZA LOCAIS EXISTENTES NA TABELA dbo.configuracao_equipamento_medicao **************************
	*******************************************************************************************************************************/
	UPDATE dbo.configuracao_equipamento_medicao
	SET    cod_pista = config_equip_pista.cod_pista
	      ,descricao = local_vigente.nome
	      ,id_produto = produto.id_produto
	      ,cod_pista_prodam = config_equip_pista.cod_pista_prodam
	      ,serie_equipamento = local_vigente.serie_equipamento
		  ,cod_area = config_equip_pista.cod_area
		  ,cod_pista_tarja = ISNULL(config_equip_pista.cod_pista_tarja, config_equip_pista.cod_pista_alternativo)
		  ,cod_pista_alternativo = config_equip_pista.cod_pista_alternativo
		  ,entre_faixa = ISNULL(config_equip_pista.entre_faixa, 0)
	FROM   dbo.local_vigente local_vigente (NOLOCK)
		   INNER JOIN dbo.configuracao_equipamento config_equip (NOLOCK)
				ON  config_equip.id_configuracao_equipamento = local_vigente.id_configuracao_equipamento
		   INNER JOIN dbo.produto produto (NOLOCK)
				ON  produto.id_produto = config_equip.id_produto
	       INNER JOIN dbo.configuracao_equipamento_pista config_equip_pista (NOLOCK)
				ON  config_equip_pista.id_configuracao_equipamento = config_equip.id_configuracao_equipamento
		   INNER JOIN dbo.configuracao_equipamento_medicao cem (NOLOCK)
				ON  cem.id_local = local_vigente.id_local
					AND cem.id_pista = config_equip_pista.id_pista
	WHERE  config_equip.data_inicio <= GETDATE()
		   AND local_vigente.desativado = 0
		   AND cem.atualizar = 1


	/*******************************************************************************************************************************
	*********************************************************** PASSO 3 ************************************************************
	************************************ ATUALIZA QUANTIDADE DE EQUIPAMENTOS QUE COMPOEM O LOCAL ***********************************
	*******************************************************************************************************************************/
	UPDATE configuracao_equipamento_medicao
	SET    qtde_equipamentos = result.qtde_equipamentos
	--SELECT *
	FROM   configuracao_equipamento_medicao cem (NOLOCK)
		   INNER JOIN (
						SELECT cod_pista
							  ,COUNT(id_local) AS qtde_equipamentos
						FROM   (
									SELECT lv.id_local
										  ,cep.cod_pista
									FROM   local_vigente lv (NOLOCK)
										   INNER JOIN configuracao_equipamento ce (NOLOCK)
												ON  ce.id_configuracao_equipamento = lv.id_configuracao_equipamento
										   INNER JOIN configuracao_equipamento_pista cep (NOLOCK)
												ON  cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
									WHERE  lv.data_inicio <= GETDATE()
										   AND ce.serie_equipamento > 201400000
										   AND lv.desativado = 0
									GROUP BY
										   lv.id_local
										  ,cep.cod_pista
							   ) tab
						GROUP BY
							   cod_pista
						--ORDER BY
						--	   cod_pista
					  ) result
				ON  result.cod_pista = cem.cod_pista

