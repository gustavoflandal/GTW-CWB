
CREATE PROCEDURE [dbo].[spu_cria_Relatorios_Volume_Hora_PMG]
	@DataInicio datetime,
	@DataFinal  datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	declare @ID_Relatorio INT
	
	SET @Id_Relatorio = 2 -- Número do ID do relatório PMG

	SET @DataInicio = CONVERT(char(10),@DataInicio,120)	
	SET @DataFinal = CONVERT(char(10),@DataFinal,120)	
	SET @HoraInicio = CONVERT(char(5),@HoraInicio,108)
	SET @HoraFinal = CONVERT(char(5),@HoraFinal,108)

	DECLARE @ID_Gerado INT

	IF @DataInicio = @DataFinal
		
		BEGIN
			EXEC @ID_Gerado = spu_cria_Relatorios_Diario_Volume_Hora_PMG @DataInicio, @HoraInicio, @HoraFinal, @Usuario
		END

	ELSE

		BEGIN
			EXEC @ID_Gerado = spu_cria_Relatorios_Periodo_Volume_Hora_PMG @DataInicio, @DataFinal, @HoraInicio, @HoraFinal, @Usuario
		END
	
	SELECT @ID_Gerado



