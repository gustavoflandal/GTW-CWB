
CREATE Function [dbo].[fcn_FormataNumero] (@Numero bigint)
RETURNS VARCHAR(50)
AS

BEGIN
	Declare @Tamanho	int,
			@Resultado	varchar(50),
			@Negativo	bit 

	If left(@Numero,1) = '-'
		begin
			set @Tamanho	= LEN(@Numero)-1
			set @Negativo	= 1
			set @Numero		= RIGHT(@Numero,@Tamanho)
		end
	Else
		begin
			set @Tamanho	= LEN(@Numero)
			set @Negativo	= 0
		end

	set @Resultado = 
	CASE
		WHEN @Tamanho <= 3 or @Tamanho > 18
			then cast(@Numero as varchar)
		WHEN @Tamanho = 4 -- 1234
			then substring(cast(@Numero as varchar),1,1) + '.' + 
				 substring(cast(@Numero as varchar),2,3)
		WHEN @Tamanho = 5 -- 12345
			then substring(cast(@Numero as varchar),1,2) + '.' + 
				 substring(cast(@Numero as varchar),3,3)
		WHEN @Tamanho = 6 -- 123456
			then substring(cast(@Numero as varchar),1,3) + '.' + 
				 substring(cast(@Numero as varchar),4,3)
		WHEN @Tamanho = 7 -- 1234567
			then substring(cast(@Numero as varchar),1,1) + '.' + 
				 substring(cast(@Numero as varchar),2,3) + '.' + 
				 substring(cast(@Numero as varchar),5,3)
		WHEN @Tamanho = 8 -- 12345678
			then substring(cast(@Numero as varchar),1,2) + '.' + 
				 substring(cast(@Numero as varchar),3,3) + '.' + 
				 substring(cast(@Numero as varchar),6,3) 
		WHEN @Tamanho = 9 -- 123456789
			then substring(cast(@Numero as varchar),1,3) + '.' + 
				 substring(cast(@Numero as varchar),4,3) + '.' + 
				 substring(cast(@Numero as varchar),7,3)
		WHEN @Tamanho = 10 -- 1234567890
			then substring(cast(@Numero as varchar),01,1) + '.' + 
				 substring(cast(@Numero as varchar),02,3) + '.' + 
				 substring(cast(@Numero as varchar),05,3) + '.' + 
				 substring(cast(@Numero as varchar),08,3)
		WHEN @Tamanho = 11 -- 12345678901
			then substring(cast(@Numero as varchar),01,2) + '.' + 
				 substring(cast(@Numero as varchar),03,3) + '.' + 
				 substring(cast(@Numero as varchar),06,3) + '.' + 
				 substring(cast(@Numero as varchar),09,3)
		WHEN @Tamanho = 12 -- 123456789012
			then substring(cast(@Numero as varchar),01,3) + '.' + 
				 substring(cast(@Numero as varchar),04,3) + '.' + 
				 substring(cast(@Numero as varchar),07,3) + '.' + 
				 substring(cast(@Numero as varchar),10,3)
		WHEN @Tamanho = 13 -- 1234567890123
			then substring(cast(@Numero as varchar),01,1) + '.' + 
				 substring(cast(@Numero as varchar),02,3) + '.' + 
				 substring(cast(@Numero as varchar),05,3) + '.' + 
				 substring(cast(@Numero as varchar),08,3) + '.' + 
				 substring(cast(@Numero as varchar),11,3)
		WHEN @Tamanho = 14 -- 12345678901234
			then substring(cast(@Numero as varchar),01,2) + '.' + 
				 substring(cast(@Numero as varchar),03,3) + '.' + 
				 substring(cast(@Numero as varchar),06,3) + '.' + 
				 substring(cast(@Numero as varchar),09,3) + '.' + 
				 substring(cast(@Numero as varchar),12,3)
		WHEN @Tamanho = 15 -- 123456789012345
			then substring(cast(@Numero as varchar),01,3) + '.' + 
				 substring(cast(@Numero as varchar),04,3) + '.' + 
				 substring(cast(@Numero as varchar),07,3) + '.' + 
				 substring(cast(@Numero as varchar),10,3) + '.' + 
				 substring(cast(@Numero as varchar),13,3)
		WHEN @Tamanho = 16 -- 1234567890123456
			then substring(cast(@Numero as varchar),01,1) + '.' + 
				 substring(cast(@Numero as varchar),02,3) + '.' + 
				 substring(cast(@Numero as varchar),05,3) + '.' + 
				 substring(cast(@Numero as varchar),08,3) + '.' + 
				 substring(cast(@Numero as varchar),11,3) + '.' + 
				 substring(cast(@Numero as varchar),14,3)
		WHEN @Tamanho = 17 -- 123456789012344567
			then substring(cast(@Numero as varchar),01,2) + '.' + 
				 substring(cast(@Numero as varchar),03,3) + '.' + 
				 substring(cast(@Numero as varchar),06,3) + '.' + 
				 substring(cast(@Numero as varchar),09,3) + '.' + 
				 substring(cast(@Numero as varchar),12,3) + '.' + 
				 substring(cast(@Numero as varchar),15,3)
		WHEN @Tamanho = 18 -- 123456789012345678
			then substring(cast(@Numero as varchar),01,3) + '.' + 
				 substring(cast(@Numero as varchar),04,3) + '.' + 
				 substring(cast(@Numero as varchar),07,3) + '.' + 
				 substring(cast(@Numero as varchar),10,3) + '.' + 
				 substring(cast(@Numero as varchar),13,3) + '.' + 
				 substring(cast(@Numero as varchar),16,3)
	END 

	If @Negativo = 1
		set @Resultado = '-' + @Resultado 
		
	RETURN @Resultado

END


