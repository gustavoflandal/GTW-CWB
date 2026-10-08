CREATE PROCEDURE [dbo].[spu_atualiza_configuracoes_equipamentos]
AS
SET NOCOUNT ON
BEGIN

	BEGIN TRY

		BEGIN TRAN

		DECLARE @temp_config_inserir AS TABLE (id_configuracao_equipamento INT)
		DECLARE @temp_config_atualizar AS TABLE (id_configuracao_equipamento INT, ativo BIT)

		INSERT INTO @temp_config_inserir
		SELECT ce_gct.id_configuracao_equipamento
		FROM   [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento ce_gct
			   LEFT JOIN configuracao_equipamento ce
					ON  ce.id_configuracao_equipamento = ce_gct.id_configuracao_equipamento
		WHERE  ce.id_configuracao_equipamento IS NULL


		--SELECT * FROM SYS.tables WHERE type = 'U' AND name LIKE 'configuracao_equipamento%' AND name NOT LIKE '%medicao%' ORDER BY name


		------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		--> Inserir novas configurações
		------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		SET IDENTITY_INSERT configuracao_equipamento ON
		
		INSERT INTO configuracao_equipamento
		(
			id_configuracao_equipamento, serie_equipamento, ativo, obs, watch_dog, controladora, iluminador, data_inicio, data_fim, id_produto, chave_publica, com_controladora,
			com_auxiliar, com_iluminador, em_operacao, id_grupo_equipamento, controle_online, flag_opcao, id_usuario, data_modificacao, categoria, distancia_equipamento, tempo_ciclagem,
			ativar_montante, ativar_jusante, porta_montante, codigo_montante, cod_GIT_Contrato, cod_GIT_Ponto, TempoTotalVideo, TempoVideoAntesInfracao, tempo_adicional_faixa_exclusiva,
			tempo_fluxo_zero, diferenca_percentual_bloqueio_faixa, CodigoEquipCliente
		)
		SELECT id_configuracao_equipamento, serie_equipamento, ativo, obs, watch_dog, controladora, iluminador, data_inicio, data_fim, id_produto, chave_publica, com_controladora,
			   com_auxiliar, com_iluminador, em_operacao, id_grupo_equipamento, controle_online, flag_opcao, 2 AS id_usuario, data_modificacao, categoria, distancia_equipamento, tempo_ciclagem,
			   ativar_montante, ativar_jusante, porta_montante, codigo_montante, cod_GIT_Contrato, cod_GIT_Ponto, TempoTotalVideo, TempoVideoAntesInfracao, tempo_adicional_faixa_exclusiva,
			   tempo_fluxo_zero, diferenca_percentual_bloqueio_faixa, CodigoEquipCliente
		FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		
		SET IDENTITY_INSERT configuracao_equipamento OFF


		INSERT INTO configuracao_equipamento_camera
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_camera gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_pista
		SELECT *, 0 as captura_reversa FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_pista gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO local
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.local gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)


		INSERT INTO configuracao_equipamento_afericao
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_afericao gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_agd
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_agd gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_agd_pista
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_agd_pista gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_agenda_camera
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_agenda_camera gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_captura_imagem
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_captura_imagem gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_captura_veiculo
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_captura_veiculo gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_controlador
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_controlador gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_controlador_canais
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_controlador_canais gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_controlador_canaisv2
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_controlador_canaisv2 gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_controlador_pesagem
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_controlador_pesagem gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_controlador_pesagem_canais
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_controlador_pesagem_canais gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_dimensoes_ml
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_dimensoes_ml gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_div
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_div gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_geral_divs
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_geral_divs gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_horario
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_horario gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_laco_virtual_ml
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_laco_virtual_ml gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_nivel_video
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_nivel_video gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_painel
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_painel gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_painel_geral
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_painel_geral gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_parametros_adicionais
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_parametros_adicionais gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_pesagem
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_pesagem gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_pista_pesagem
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_pista_pesagem gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_pmv
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_pmv gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_pmv_circunstancias
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_pmv_circunstancias gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_regra_infracao
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_regra_infracao gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_relevante
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_relevante gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_resolucao_imagem
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_resolucao_imagem gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_rodizio
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_rodizio gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_rodovia
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_rodovia gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_sensor_piezo
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_sensor_piezo gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)
		INSERT INTO configuracao_equipamento_servidor
		SELECT * FROM [MURALHA_GCT].GTW_C037.dbo.configuracao_equipamento_servidor gct WHERE gct.id_configuracao_equipamento IN (SELECT id_configuracao_equipamento FROM @temp_config_inserir)


		------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		--> Inserir novas configurações
		------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		UPDATE configuracao_equipamento SET ativo = 0
		UPDATE configuracao_equipamento SET ativo = 1 WHERE id_configuracao_equipamento IN (SELECT MAX(id_configuracao_equipamento) AS id_configuracao_equipamento FROM configuracao_equipamento (NOLOCK) GROUP BY serie_equipamento)


		COMMIT
	END TRY      
      
	BEGIN CATCH      
          
		PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'      
          
		IF @@TRANCOUNT > 0      
			ROLLBACK      
        
		EXEC spu_replica_erro      
      
	END CATCH      
      
END      
