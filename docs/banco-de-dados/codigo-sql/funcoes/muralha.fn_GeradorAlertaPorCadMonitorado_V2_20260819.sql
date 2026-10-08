CREATE FUNCTION [muralha].[fn_GeradorAlertaPorCadMonitorado_V2_20260819] 
(
	@IdTipoAlertaOcorrencia UNIQUEIDENTIFIER,
	@PlacaEntrada CHAR(7)
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
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '0349F722-DFDE-4080-9E3B-D65F1C058EDC', @PlacaEntrada CHAR(7) = 'TDR7A52'
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
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '0349F722-DFDE-4080-9E3B-D65F1C058EDC', @PlacaEntrada CHAR(7) = 'ATM0H19'
	SELECT cad.id,
		   tap.tipo,
		   cad.placa,
		   cad.data_cadastro,
		   cad.erros_permitidos_placa
	FROM   muralha.cad_veiculo_monitorado cad
		   INNER JOIN muralha.tipo_alerta_ocorrencia tap
				ON  tap.id = cad.id_tipo_alerta_ocorrencia
	WHERE  (
				(cad.data_fim IS NULL OR CAST(GETDATE() AS DATE) BETWEEN cad.data_inicio AND cad.data_fim)
				AND
				(cad.data_inativacao IS NULL OR GETDATE() <= cad.data_inativacao)
		   )
		   AND cad.id_tipo_alerta_ocorrencia = @IdTipoAlertaOcorrencia
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
	INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado

	-- Início do laço
	WHILE @@FETCH_STATUS = 0  
	BEGIN  
		DELETE FROM @validacao_placa
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
		INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro, @Erros_Cad_Monitorado

	END   

	-- Encerramento
	CLOSE CadMonitoradosCursor 
	DEALLOCATE CadMonitoradosCursor 

	--SELECT * FROM @RETORNO

RETURN
END
