CREATE FUNCTION [dbo].[fcn_totalErrosPlacas] (@placa1 CHAR(7), @placa2 CHAR(7))
RETURNS TINYINT
AS

BEGIN
	DECLARE @cont TINYINT = 1,
			@tamanho_placa TINYINT = 7,
			@erros TINYINT = 0

	IF (@placa1 IS NULL OR @placa2 IS NULL)
	BEGIN
		SET @erros = 7;
	END
	ELSE
	BEGIN
		WHILE (@cont <= 7)
		BEGIN
	
			IF (SUBSTRING(@placa1, @cont, 1) <> SUBSTRING(@placa2, @cont, 1))
			BEGIN
				SET @erros = @erros + 1;
			END

			SET @cont = @cont + 1;
		END
	END
		
	RETURN @erros

END
