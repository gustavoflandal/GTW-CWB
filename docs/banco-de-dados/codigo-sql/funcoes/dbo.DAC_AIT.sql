
CREATE FUNCTION [dbo].[DAC_AIT]
(
	-- Add the parameters for the function here
	@tipo CHAR(2),
	@serie CHAR(2),
	@auto INTEGER
)
RETURNS SMALLINT
AS
BEGIN
	DECLARE @dac INT
	DECLARE @resto INT
	DECLARE @sAuto CHAR(6)
	DECLARE @tipoSerie CHAR(4)
	DECLARE @sAutoCompleto CHAR(14)
	DECLARE @fator INT
	DECLARE @soma INT
	DECLARE @mult INT
	DECLARE	@i INTEGER = 0
	DECLARE	@chr CHAR
	DECLARE	@convChr INT
	DECLARE	@sTipoSerie CHAR(8)
	
	SET @sAuto = REPLICATE('0', 6 - LEN(@auto))+RTRIM(@auto)
	SET @tipoSerie = @tipo+@serie
	SET @sTipoSerie = ''

	WHILE @i < 4 

		BEGIN
		  SET @chr = SUBSTRING(@tipoSerie,@i+1,1)

		  IF (ISNUMERIC(@chr) = 1)
			SET @convChr = ASCII(@chr)-48
		  ELSE
			SET @convChr = ASCII(@chr)-55

		  SET @sTipoSerie = RTRIM(@sTipoSerie) + REPLICATE('0', 2 - LEN(@convChr))+RTRIM(@convChr)
	  
		  SET @i = @i + 1
		END
	
	SET @sAutoCompleto = @sTipoSerie + @sAuto
	
	SET @fator = 5
	SET @soma = 0
	SET @i = 0

	WHILE @i < LEN(@sAutoCompleto) 

		BEGIN
			SET @mult = CAST(SUBSTRING(@sAutoCompleto,@i+1,1) AS SMALLINT) * @fator
			SET @fator = @fator - 1
			SET @soma = @soma + @mult
			IF (@fator = 0)
				SET @fator = 10
		  SET @i = @i + 1
		END

	SET @resto = @soma - ((@soma / 11) * 11)

	SET @dac = 11 - @resto
	
	IF (@dac > 9)
		SET @dac = 0
		
	RETURN @dac

END





