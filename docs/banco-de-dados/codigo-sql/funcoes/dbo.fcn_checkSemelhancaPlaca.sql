CREATE FUNCTION [dbo].[fcn_checkSemelhancaPlaca] (@placa1 CHAR(7), @placa2 CHAR(7))
RETURNS TINYINT
AS

BEGIN
	
	--DECLARE @placa1 CHAR(7) = 'AAA0000', @placa2 CHAR(7) = 'AAT0000'
	DECLARE @cont TINYINT = 1, @cont_validos TINYINT = 0, @cont_invalidos TINYINT = 0,
			@tamanho_placa TINYINT = 7,
			@valida BIT = 1

	IF ( (@placa1 IS NULL OR @placa2 IS NULL) OR (LEN(@placa1) < 7 OR LEN(@placa2) < 7) )
	BEGIN
		SET @valida = 0;
	END
	ELSE
	BEGIN
		WHILE (@cont <= 7)
		BEGIN
			
			SELECT @valida = MAX(CASE WHEN (SUBSTRING(@placa1, @cont, 1) = SUBSTRING(@placa2, @cont, 1)) OR (SUBSTRING(@placa2, @cont, 1) IN (valor)) THEN 1 ELSE 0 END)
			FROM   configuracao_semelhanca_placa
			WHERE  caracter = (SUBSTRING(@placa1, @cont, 1))

			IF (@valida = 1)
			BEGIN
				SET @cont_validos = @cont_validos + 1;
			END
			ELSE
			BEGIN
				SET @cont_invalidos = @cont_invalidos + 1;
			END

			IF (@cont_invalidos > 2)
			BEGIN
				BREAK;
			END

			SET @cont = @cont + 1;
		END
	END
		
	RETURN @valida;
	--SELECT @valida

END
