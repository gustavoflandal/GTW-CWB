CREATE FUNCTION [dbo].[fn_gera_alerta]()
RETURNS  @RETORNO TABLE 
(
	id_cad_veic uniqueidentifier,
	placa_cad varchar(7),
	placa_entrada varchar(7),	
	erros int
)
AS
BEGIN

		DECLARE 
			@IdTipoAlertaOcorrencia uniqueidentifier,
			@PlacaEntrada CHAR(7)


		set @IdTipoAlertaOcorrencia = '95631582-96B2-4220-9612-12131BE4923C'
		set @PlacaEntrada = 'CSX7707'

		DECLARE CadMonitoradosCursor CURSOR FOR   
			SELECT 
				cad.id,
				tap.tipo,
				cad.placa,
				cad.data_cadastro		
			FROM 
				muralha.cad_veiculo_monitorado cad
			INNER JOIN muralha.tipo_alerta_ocorrencia tap
				On tap.id = cad.id_tipo_alerta_ocorrencia
			WHERE
				cad.data_exclusao is NULL
				AND cad.id_tipo_alerta_ocorrencia IN ( @IdTipoAlertaOcorrencia )
			ORDER BY 
				cad.data_cadastro desc
  
		-- Inicialização
		OPEN CadMonitoradosCursor 

		-- Variáveis
		DECLARE @Id uniqueidentifier, 
				@TipoAlertaOcorrencia varchar(40),
				@Placa CHAR(7),		
				@DataCadastro datetime,
				@isIgualSemelhante numeric
  
		-- Leitura da primeira linha do cursor
		FETCH NEXT FROM CadMonitoradosCursor   
		INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro 

		-- Início do laço
		WHILE @@FETCH_STATUS = 0  
		BEGIN  

			--select 'Entrada: ' + @PlacaEntrada + ', Cadastro: ' + @Placa + ' ==> ' + @TipoAlertaOcorrencia;
			-------------------------------------------------------------------------------------------------
			-- Verifica se há placa igual ou semelhante no Cadastro de Monitorado			
			-------------------------------------------------------------------------------------------------
			set @isIgualSemelhante = muralha.fcn_ValidarPlacasIguaisSemelhantes (@PlacaEntrada, @Placa)
			-------------------------------------------------------------------------------------------------

    
			IF (  @isIgualSemelhante >= 0  )
			BEGIN
				INSERT INTO @RETORNO ( id_cad_veic, placa_cad, placa_entrada, erros) VALUES(@Id, @Placa, @PlacaEntrada, @isIgualSemelhante)
			END

			-- Leitura da próxima linha do cursor
			FETCH NEXT FROM CadMonitoradosCursor   
			INTO @Id, @TipoAlertaOcorrencia, @Placa, @DataCadastro 

		END   

		-- Encerramento
		CLOSE CadMonitoradosCursor 
		DEALLOCATE CadMonitoradosCursor 

return
END
