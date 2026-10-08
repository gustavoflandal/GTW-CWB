
CREATE  PROCEDURE [dbo].[spu_cria_Relatorios_Periodo_Trafego_Pista]
	@DataInicio datetime,
	@DataFinal  datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	DECLARE @DataStart DateTime
	DECLARE @ID_Relatorio INT

	SET @DataStart = GetDate()

	SET @Id_Relatorio = 1 -- Número do ID do relatório PMG

	SET @DataInicio = convert(char(10),@DataInicio,120)	
	SET @DataFinal = convert(char(10),@DataFinal,120)	
	SET @HoraInicio = convert(char(5),@HoraInicio,108)
	SET @HoraFinal = convert(char(5),@HoraFinal,108)

	-- Número de dias existentes no período desejado
	DECLARE @numDiasDesejado INT
	SET @numDiasDesejado = cast(@dataFinal - @dataInicio + 1 as Int)

	DECLARE @numDiasExistente INT
	SELECT @numDiasExistente = count(*)
	FROM (	SELECT 	
				DataInicio, 
				DataFinal, 
				HoraInicio, 
				HoraFinal, 
				Max(ID_Gerado) as ID_Gerado
			FROM	
				Relatorios_Gerados (nolock)
			WHERE 	id_relatorio = @Id_Relatorio 
				AND dataInicio >= @dataInicio 
				AND dataInicio < @DataFinal + 1 
				AND horaInicio = @horaInicio 
				AND horafinal = @HoraFinal
				AND dataInicio = dataFinal
			GROUP BY 
				DataInicio, 
				DataFinal, 
				HoraInicio, 
				HoraFinal
		) AS T1

	
	IF @numDiasDesejado <> @numDiasExistente

		BEGIN
			RAISERROR ('Existem dias não gerados no período desejado!', 16, 1)
			RETURN 0
		END


	DECLARE @ID_Gerado int

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GetDate(), @Id_Relatorio, @DataInicio, @DataFinal,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO Relatorios_Trafego_Pista with (rowlock)
		(
		ID_Gerado, Local, Pista, 
		v00_40, v41_48, v49_67, v68_79, v80_97, v98_999, v68_77, v78_91, v92_113, v114_999, 
		qt_Infratores, qt_RejeitadoPT, qt_RejeitadoPNT, qt_InvalidadoPT, qt_InvalidadoPNT, 
		qt_Veic_Oficiais, q1.qt_Validos,qt_Infr_Red
		)
	SELECT 	@ID_Gerado, [Local], [pista], 
		SUM([v00_40]), SUM([v41_48]), SUM([v49_67]), 
		SUM([v68_79]), SUM([v80_97]), SUM([v98_999]),
		SUM([v68_77]), SUM([v78_91]), SUM([v92_113]), SUM([v114_999]), 
		SUM([qt_Infratores]), SUM([qt_RejeitadoPT]), SUM([qt_RejeitadoPNT]), SUM([qt_InvalidadoPT]), SUM([qt_InvalidadoPNT]), 
		SUM([qt_Veic_Oficiais]), SUM([qt_Validos]), SUM([qt_Infr_Red])
	FROM 	Relatorios_Trafego_Pista (nolock)
	WHERE 	ID_Gerado IN (	SELECT 	
								Max(ID_Gerado)
							FROM	
								Relatorios_Gerados (nolock)
							WHERE 	id_relatorio = @Id_Relatorio 
								AND dataInicio >= @dataInicio 
								AND dataInicio < @DataFinal + 1 
								AND horaInicio = @horaInicio 
								AND horafinal = @HoraFinal
								AND dataInicio = dataFinal
							GROUP BY 
								DataInicio, 
								DataFinal, 
								HoraInicio, 
								HoraFinal
							)
	GROUP BY Local, pista
	
	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GetDate() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado

	RETURN @ID_Gerado



