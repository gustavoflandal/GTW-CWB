CREATE FUNCTION [muralha].[fn_ValidarPlacasIguaisSemelhantes_V2] 
(
	--------------------------
	-- PARAMETROS
	--------------------------
	@placa1 CHAR(7), 
	@placa2 CHAR(7),
	@erros_cad_monitorado TINYINT = NULL
)
RETURNS @RETORNO TABLE 
(
	resultado INT,
	semelhante TINYINT,
	diferencas TINYINT,
	diferencas_desc VARCHAR(120)
)
AS
BEGIN
	-------------------------------------------------------------------------
	-- PARA TESTAR
	--DECLARE @placa1 CHAR(7) = 'SFI4F08', @placa2 CHAR(7) = 'SFI4F03', @erros_cad_monitorado TINYINT = NULL
	--DECLARE @placa1 CHAR(7) = 'ABC1234', @placa2 CHAR(7) = 'ADC7234', @erros_cad_monitorado TINYINT = 2
	-------------------------------------------------------------------------
	-------------------------------------------------------------------------

	-------------------------------------------------------------------------
	-- Inicia afirmando que as placas são identicas
	-------------------------------------------------------------------------
	DECLARE @isIgualSemelhante INT	= 0

	-------------------------------------------------------------------------
	-- Carrega máximo de erros permitidos e configurados
	-------------------------------------------------------------------------
	DECLARE @qtdeErrosPermitido INT;

	IF (@erros_cad_monitorado IS NOT NULL)
		SET @qtdeErrosPermitido = @erros_cad_monitorado
	ELSE
		SET @qtdeErrosPermitido = (SELECT erros_permitidos AS erros_permitidos FROM muralha.config_semelhanca_placa WHERE data_cadastro IN (SELECT MAX(data_cadastro) AS data_cadastro FROM muralha.config_semelhanca_placa WHERE data_exclusao IS NULL))
	
	--SELECT @qtdeErrosPermitido
	-------------------------------------------------------------------------
	-------------------------------------------------------------------------

	DECLARE @iterador TINYINT		= 1, 
			@caracterSemelhante BIT	= 0,
			@cont_validos TINYINT	= 0, 
			@cont_invalidos TINYINT = 0,
			@tamanho_placa TINYINT	= 7,
			@diferencas_placas VARCHAR(120) = ''

	IF (@qtdeErrosPermitido IS NULL)
	BEGIN
		SET @isIgualSemelhante = -1;
	END
	ELSE IF ( (@placa1 IS NULL OR @placa2 IS NULL) OR (LEN(@placa1) < 7 OR LEN(@placa2) < 7) )
	BEGIN
		SET @isIgualSemelhante = -1;
	END
	ELSE
	BEGIN
		WHILE (@iterador <= 7)
		BEGIN

			DECLARE @caracter_1 CHAR(1) = SUBSTRING(@placa1, @iterador, 1)
			DECLARE @caracter_2 CHAR(1) = SUBSTRING(@placa2, @iterador, 1)
			-------------------------------------------------------------------------
			-- Faz a verificação de igualdade caracter a caracter
			-------------------------------------------------------------------------
			SELECT @caracterSemelhante = MAX(CASE WHEN (@caracter_1 = @caracter_2) THEN 1 ELSE 0 END)

			-------------------------------------------------------------------------
			-- Faz contagem de erros e acertos de caracteres
			-------------------------------------------------------------------------

			IF (@caracterSemelhante = 1)
			BEGIN
				SET @cont_validos = @cont_validos + 1;
			END
			ELSE
			BEGIN
				SET @cont_invalidos = @cont_invalidos + 1;
				SET @diferencas_placas =  @diferencas_placas + '; ' + 'Pos ' + CAST(@iterador AS VARCHAR) + ': ' + @caracter_1 + ' vs ' + @caracter_2
			END

			-------------------------------------------------------------------------
			-- Verifica se quantidade de erros é permitido
			-- Se não, então aborta para economizar processamento
			-------------------------------------------------------------------------
			IF (@cont_invalidos > @qtdeErrosPermitido) 
			BEGIN 
				SET @isIgualSemelhante = -1;
				BREAK; 
			END

			-------------------------------------------------------------------------
			-- Iteração para cada caracter da placa
			-------------------------------------------------------------------------
			SET @iterador = @iterador + 1;

		END
	END

	SET @diferencas_placas = STUFF((@diferencas_placas), 1, 2, '')

	-----------------------------------------------------------------------------------------------
	-- Retorno -1  ==> Não há semelhança ou igualdade 
	--		    0  ==> Placas identicas
	--          1+ ==> Placas semelhantes de acordo com o cadastro de semelhanças permitidas 
	-----------------------------------------------------------------------------------------------
	IF (@isIgualSemelhante >= 0) 
	BEGIN 
		SET @isIgualSemelhante = @cont_invalidos;
	END

	INSERT INTO @RETORNO
	SELECT @isIgualSemelhante AS resultado,
		   CASE WHEN @isIgualSemelhante > 0 THEN 1 ELSE NULL END AS semelhante,
		   CASE WHEN @isIgualSemelhante > 0 THEN @isIgualSemelhante ELSE NULL END AS diferencas,
		   CASE WHEN @isIgualSemelhante > 0 THEN @diferencas_placas ELSE NULL END AS diferencas_desc

	RETURN
END