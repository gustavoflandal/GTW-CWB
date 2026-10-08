CREATE FUNCTION [muralha].[fn_ValidarPlacasParciaisIguais_V2]
(
	@placaEntrada CHAR(7), 
	@placaParcial CHAR(7)
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
	--DECLARE @placaEntrada CHAR(7) = 'ART4D85', @placaParcial CHAR(7) = 'AR*4*85'
	DECLARE @iterador INT = 1, @isIgual INT = 0, @caracterIgual BIT = 0

	IF ( ( (@placaEntrada IS NULL OR @placaParcial IS NULL) OR (LEN(@placaEntrada) < 7 OR LEN(@placaParcial) < 7) ) OR (CHARINDEX('*', @placaParcial) = 0) )
	BEGIN
		SET @isIgual = -1;
	END
	ELSE
	BEGIN
		WHILE (@iterador <= 7)
		BEGIN

			IF (SUBSTRING(@placaParcial, @iterador, 1) = '*')
			BEGIN
				SET @iterador = @iterador + 1;
				CONTINUE;
			END

			--PRINT('POSIÇÃO: ' + CAST(@iterador AS VARCHAR(1)))

			-----------------------------------------------------------------------------------------------------------------------------------------------
			-- Faz a verificação de igualdade caracter a caracter
			-----------------------------------------------------------------------------------------------------------------------------------------------
			SELECT @caracterIgual = MAX(CASE WHEN (SUBSTRING(@placaEntrada, @iterador, 1) = SUBSTRING(@placaParcial, @iterador, 1)) THEN 1 ELSE 0 END)

			-----------------------------------------------------------------------------------------------------------------------------------------------
			-- Se identificar caracter diferente, interrompe verificação e retorna resposta
			-----------------------------------------------------------------------------------------------------------------------------------------------
			IF (@caracterIgual = 0)
			BEGIN 
				SET @isIgual = -1;
				BREAK;
			END

			-----------------------------------------------------------------------------------------------------------------------------------------------
			---- Iteração para cada caracter da placa
			-----------------------------------------------------------------------------------------------------------------------------------------------
			SET @iterador = @iterador + 1;
		END
	END

	--PRINT ('RESULTADO: ' + CAST(@isIgual AS VARCHAR(2)))

	-----------------------------------------------------------------------------------------------------------------------------------------------
	-- Retorno -1  ==> Não há semelhança ou igualdade 
	--		    0  ==> Placas identicas
	-----------------------------------------------------------------------------------------------------------------------------------------------
	--SELECT  @isIgual

	INSERT INTO @RETORNO
	SELECT @isIgual AS resultado, NULL AS semelhante, NULL AS diferencas, NULL AS diferencas_desc

	RETURN;
END
