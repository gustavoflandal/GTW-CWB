CREATE FUNCTION [muralha].[fn_GeradorAlertaPorCadMonitorado_V3] 
(
	@IdTipoAlertaOcorrencia UNIQUEIDENTIFIER,
	@PlacaEntrada CHAR(7),
	@DataVeiculo DATETIME,
	@Id_Local INT
)
RETURNS @RETORNO TABLE 
(
	id_cad_veic UNIQUEIDENTIFIER,
	placa_cad VARCHAR(7),
	placa_entrada VARCHAR(7),
	erros INT,
	com_semelhanca INT,
	com_semelhanca_erros INT,
	com_semelhanca_desc VARCHAR(120)
)
AS
BEGIN
	-----------------------------------------------------------------------------------------------------------------------------------------------
	-- PARA TESTAR
	-----------------------------------------------------------------------------------------------------------------------------------------------
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '9D31A265-A663-4836-BF00-2309FC0D5E33', @PlacaEntrada CHAR(7) = 'KCY7439', @DataVeiculo DATETIME = '2025-01-10 07:54:35', @Id_Local INT = 100
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '0349F722-DFDE-4080-9E3B-D65F1C058EDC', @PlacaEntrada CHAR(7) = 'POA1441', @DataVeiculo DATETIME = '2025-09-29 16:54:35', @Id_Local INT = 100
	-----------------------------------------------------------------------------------------------------------------------------------------------
	/*
	DECLARE @RETORNO TABLE 
	(
		id_cad_veic UNIQUEIDENTIFIER,
		placa_cad VARCHAR(7),
		placa_entrada VARCHAR(7),
		erros INT,
		com_semelhanca INT,
		com_semelhanca_erros INT,
		com_semelhanca_desc VARCHAR(120)
	)
	*/
	-----------------------------------------------------------------------------------------------------------------------------------------------
	-- CURSOR PARA CADASTRO DE VEICULOS MONITORADOS
	-----------------------------------------------------------------------------------------------------------------------------------------------
	DECLARE CadMonitoradosCursor CURSOR FOR   
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '9D31A265-A663-4836-BF00-2309FC0D5E33', @PlacaEntrada CHAR(7) = 'KCY7439', @DataVeiculo DATETIME = '2025-01-10 09:54:35', @Id_Local INT = 100
	SELECT cad.id,
		   tap.tipo,
		   cad.placa,
		   cad.data_cadastro,
		   cad.erros_permitidos_placa,
		   cad.erros_permitido_ini,
		   cad.erros_permitido_fim,
		   CAST(CASE WHEN EXISTS (SELECT 1 FROM muralha.cad_veiculo_monitorado_equipamento cvme WHERE cvme.id_cad_veiculo_monitorado = cad.id) THEN 1 ELSE 0 END AS BIT) AS possui_config_equipamento,
		   CAST(CASE WHEN EXISTS (SELECT 1 FROM muralha.cad_veiculo_monitorado_periodo cvmp WHERE cvmp.id_cad_veiculo_monitorado = cad.id) THEN 1 ELSE 0 END AS BIT) AS possui_config_periodo
	FROM   muralha.cad_veiculo_monitorado cad
		   INNER JOIN muralha.tipo_alerta_ocorrencia tap
				ON  tap.id = cad.id_tipo_alerta_ocorrencia
	WHERE  (
				(cad.data_fim IS NULL OR CAST(GETDATE() AS DATE) BETWEEN cad.data_inicio AND cad.data_fim)
				AND
				(cad.data_inativacao IS NULL OR GETDATE() <= cad.data_inativacao)
		   )
		   AND cad.id_tipo_alerta_ocorrencia = @IdTipoAlertaOcorrencia
		   --AND cad.id = '01D6AC73-2FC5-4FA6-B0D2-5F21FAD96D1E'
		   --AND cad.id = '576CE03B-2635-4FC4-90B5-A10F948116D4'
	--ORDER BY 
	--	   cad.data_cadastro DESC
  
	-- Inicialização
	OPEN CadMonitoradosCursor 

	-- Variáveis
	DECLARE @Id UNIQUEIDENTIFIER, 
			@TipoAlertaOcorrencia VARCHAR(40),
			@Placa CHAR(7),		
			@DataCadastro DATETIME,
			@Erros_Cad_Monitorado INT,
			@Erros_Permitido_Ini TIME,
			@Erros_Permitido_Fim TIME,
			@Possui_Config_Equipamento BIT,
			@Possui_Config_Periodo BIT,
			@isIgualSemelhante NUMERIC

	DECLARE @validacao_placa TABLE 
	(
		resultado INT,
		semelhante TINYINT,
		diferencas TINYINT,
		diferencas_desc VARCHAR(120)
	)
  
	-- Leitura da primeira linha do cursor
	FETCH NEXT FROM CadMonitoradosCursor   
	INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado, @Erros_Permitido_Ini, @Erros_Permitido_Fim, @Possui_Config_Equipamento, @Possui_Config_Periodo

	-- Início do laço
	WHILE @@FETCH_STATUS = 0  
	BEGIN  
		DELETE FROM @validacao_placa

		-----------------------------------------------------------------------------------------------------------------------------------------------
		-- Quando existir configuração de equipamento para monitoramento
		-- Verifica se o equipamento que registrou a passagem a ser verificada está na lista de equipamentos do cadastro de monitoramento
		-----------------------------------------------------------------------------------------------------------------------------------------------
		IF @Possui_Config_Equipamento = 1
		BEGIN
			IF NOT EXISTS (SELECT 1 FROM muralha.cad_veiculo_monitorado_equipamento cvme WHERE cvme.id_cad_veiculo_monitorado = @Id AND cvme.id_local = @Id_Local)
			BEGIN
				--PRINT('Local não selecionado para monitoramento')
				FETCH NEXT FROM CadMonitoradosCursor   
				INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado, @Erros_Permitido_Ini, @Erros_Permitido_Fim, @Possui_Config_Equipamento, @Possui_Config_Periodo
				CONTINUE
			END
		END

		-----------------------------------------------------------------------------------------------------------------------------------------------
		-- Quando existir configuração de período para monitoramento
		-- Verifica se a data/hora da passagem é correspondente aos dias da semana e períodos cadastrados para monitoramento
		-----------------------------------------------------------------------------------------------------------------------------------------------
		IF @Possui_Config_Periodo = 1
		BEGIN
			IF NOT EXISTS (SELECT 1 FROM muralha.cad_veiculo_monitorado_periodo cvmp WHERE cvmp.id_cad_veiculo_monitorado = @Id AND cvmp.dia_semana = DATEPART(WEEKDAY, @DataVeiculo) AND CAST(@DataVeiculo AS TIME) BETWEEN cvmp.hora_inicio AND cvmp.hora_fim)
			BEGIN
				--PRINT('Periodo não selecionado para monitoramento')
				FETCH NEXT FROM CadMonitoradosCursor   
				INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado, @Erros_Permitido_Ini, @Erros_Permitido_Fim, @Possui_Config_Equipamento, @Possui_Config_Periodo
				CONTINUE
			END
		END

		-----------------------------------------------------------------------------------------------------------------------------------------------
		-- Verificar se existe um período para semelhança de placa do cadastro de monitorado
		-- Se houver, e o veículo estiver fora do período de validade da semelhança, seta para usar a configuração geral do sistema
		-----------------------------------------------------------------------------------------------------------------------------------------------
		IF (@Erros_Permitido_Ini IS NOT NULL AND @Erros_Permitido_Fim IS NOT NULL AND CAST(@DataVeiculo AS TIME) NOT BETWEEN @Erros_Permitido_Ini AND @Erros_Permitido_Fim)
		BEGIN
			--PRINT('Usar config geral de semelhança')
			SET @Erros_Cad_Monitorado = NULL
		END
		--PRINT(@Erros_Cad_Monitorado)

		-----------------------------------------------------------------------------------------------------------------------------------------------
		-- Verifica se há placa igual ou semelhante no Cadastro de Monitorado			
		-- Se for placa parcial (com *), então os caracteres informados no cadastro devem ser idênticos
		-----------------------------------------------------------------------------------------------------------------------------------------------
		IF (CHARINDEX('*', @placa) = 0)
		BEGIN
			INSERT INTO @validacao_placa
			--DECLARE @PlacaEntrada CHAR(7) = 'ABC1234', @Placa CHAR(7) = 'ADC7234', @Erros_Cad_Monitorado TINYINT = 2
			SELECT resultado, semelhante, diferencas, diferencas_desc FROM muralha.fn_ValidarPlacasIguaisSemelhantes_V2(@PlacaEntrada, @Placa, @Erros_Cad_Monitorado)
		END
		ELSE
		BEGIN
			INSERT INTO @validacao_placa
			--DECLARE @PlacaEntrada CHAR(7) = 'ART4D85', @Placa CHAR(7) = 'AR*4*85'
			SELECT resultado, semelhante, diferencas, diferencas_desc FROM muralha.fn_ValidarPlacasParciaisIguais_V2(@PlacaEntrada, @Placa)
		END
		-----------------------------------------------------------------------------------------------------------------------------------------------

		SET @isIgualSemelhante = (SELECT TOP 1 resultado FROM @validacao_placa)
    
		IF (@isIgualSemelhante >= 0)
		BEGIN
			INSERT INTO @RETORNO (id_cad_veic, placa_cad, placa_entrada, erros, com_semelhanca, com_semelhanca_erros, com_semelhanca_desc)
			SELECT @Id AS id_cad_veic, @Placa AS placa_cad, @PlacaEntrada AS placa_entrada, resultado AS erros, semelhante AS com_semelhanca, diferencas AS com_semelhanca_erros, diferencas_desc AS com_semelhanca_desc FROM @validacao_placa
		END

		-- Leitura da próxima linha do cursor
		FETCH NEXT FROM CadMonitoradosCursor   
		INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado, @Erros_Permitido_Ini, @Erros_Permitido_Fim, @Possui_Config_Equipamento, @Possui_Config_Periodo

	END   

	-- Encerramento
	CLOSE CadMonitoradosCursor 
	DEALLOCATE CadMonitoradosCursor 

	--SELECT * FROM @RETORNO

RETURN
END
