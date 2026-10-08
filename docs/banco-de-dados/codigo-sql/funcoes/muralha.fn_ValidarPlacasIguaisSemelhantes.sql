CREATE FUNCTION [muralha].[fn_ValidarPlacasIguaisSemelhantes] 
(
	--------------------------
	-- PARAMETROS
	--------------------------
	@placa1 CHAR(7), 
	@placa2 CHAR(7)
)
RETURNS INT

AS
BEGIN
	-------------------------------------------------------------------------
	-- PARA TESTAR
	--DECLARE @placa1 CHAR(7) = 'SFI4F08', @placa2 CHAR(7) = 'SFI4F03'
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
	SET @qtdeErrosPermitido = (SELECT erros_permitidos AS erros_permitidos FROM muralha.config_semelhanca_placa WHERE data_cadastro IN (SELECT MAX(data_cadastro) AS data_cadastro FROM muralha.config_semelhanca_placa WHERE data_exclusao IS NULL))
	--SELECT @qtdeErrosPermitido
	-------------------------------------------------------------------------
	-------------------------------------------------------------------------

	DECLARE @iterador TINYINT		= 1, 
			@caracterSemelhante BIT	= 0,
			@cont_validos TINYINT	= 0, 
			@cont_invalidos TINYINT = 0,
			@tamanho_placa TINYINT	= 7

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

			-------------------------------------------------------------------------
			-- Faz a verificação de igualdade caracter a caracter
			-------------------------------------------------------------------------
			SELECT @caracterSemelhante = MAX(CASE WHEN (SUBSTRING(@placa1, @iterador, 1) = SUBSTRING(@placa2, @iterador, 1)) THEN 1 ELSE 0 END)

			-------------------------------------------------------------------------
			-- Faz contagem de erros e acertos de caracteres
			-------------------------------------------------------------------------

			IF (@caracterSemelhante = 1)	BEGIN SET @cont_validos = @cont_validos + 1;		END
			ELSE							BEGIN SET @cont_invalidos = @cont_invalidos + 1;	END

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

	-----------------------------------------------------------------------------------------------
	-- Retorno -1  ==> Não há semelhança ou igualdade 
	--		    0  ==> Placas identicas
	--          1+ ==> Placas semlehantes de acordo com o cadastro de semelhanças permitidas 
	-----------------------------------------------------------------------------------------------
	IF (@isIgualSemelhante >= 0) 
	BEGIN 
		SET @isIgualSemelhante = @cont_invalidos;
	END

	--SELECT  @isIgualSemelhante

	RETURN @isIgualSemelhante;

END
