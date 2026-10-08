CREATE FUNCTION [muralha].[fn_GeradorAlertaPorCadMonitorado] 
(
	@IdTipoAlertaOcorrencia UNIQUEIDENTIFIER,
	@PlacaEntrada CHAR(7)
)
RETURNS  @RETORNO TABLE 
(
	id_cad_veic UNIQUEIDENTIFIER,
	placa_cad VARCHAR(7),
	placa_entrada VARCHAR(7),
	erros INT
)
AS
BEGIN
	-----------------------------------------------------------------------------------------------------------------------------------------------
	-- PARA TESTAR
	-----------------------------------------------------------------------------------------------------------------------------------------------
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '95631582-96B2-4220-9612-12131BE4923C', @PlacaEntrada CHAR(7) = 'CSX7707'
	-----------------------------------------------------------------------------------------------------------------------------------------------

	-----------------------------------------------------------------------------------------------------------------------------------------------
	-- CURSOR PARA CADASTRO DE VEICULOS MONITORADOS
	-----------------------------------------------------------------------------------------------------------------------------------------------
	DECLARE CadMonitoradosCursor CURSOR FOR   
	--DECLARE @IdTipoAlertaOcorrencia uniqueidentifier = '0349F722-DFDE-4080-9E3B-D65F1C058EDC', @PlacaEntrada CHAR(7) = 'PXA3453'
	SELECT cad.id,
		   tap.tipo,
		   cad.placa,
		   cad.data_cadastro
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
			@isIgualSemelhante NUMERIC
  
	-- Leitura da primeira linha do cursor
	FETCH NEXT FROM CadMonitoradosCursor   
	INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro 

	-- Início do laço
	WHILE @@FETCH_STATUS = 0  
	BEGIN  
		-----------------------------------------------------------------------------------------------------------------------------------------------
		-- Verifica se há placa igual ou semelhante no Cadastro de Monitorado			
		-- Se for placa parcial (com *), então os caracteres informados no cadastro devem ser idênticos
		-----------------------------------------------------------------------------------------------------------------------------------------------
		IF (CHARINDEX('*', @placa) = 0)
		BEGIN
			SET @isIgualSemelhante = muralha.fn_ValidarPlacasIguaisSemelhantes (@PlacaEntrada, @Placa)
		END
		ELSE
		BEGIN
			SET @isIgualSemelhante = muralha.fn_ValidarPlacasParciaisIguais (@PlacaEntrada, @Placa)
		END
		-----------------------------------------------------------------------------------------------------------------------------------------------

    
		IF (@isIgualSemelhante >= 0)
		BEGIN
			INSERT INTO @RETORNO (id_cad_veic, placa_cad, placa_entrada, erros) VALUES (@Id, @Placa, @PlacaEntrada, @isIgualSemelhante)
		END

		-- Leitura da próxima linha do cursor
		FETCH NEXT FROM CadMonitoradosCursor   
		INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro 

	END   

	-- Encerramento
	CLOSE CadMonitoradosCursor 
	DEALLOCATE CadMonitoradosCursor 

RETURN
END
