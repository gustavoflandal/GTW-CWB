
CREATE      PROCEDURE [dbo].[spu_cria_Relatorios_Periodo_Volume_Hora_PMG]
	@DataInicio datetime,
	@DataFinal  datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	DECLARE @DataStart DateTime
	DECLARE @ID_Relatorio INT
	
	SET @Id_Relatorio = 2 -- Número do ID do relatório PMG

	SET @DataStart = GETDATE()

	SET @DataInicio = CONVERT(char(10),@DataInicio,120)	
	SET @DataFinal = CONVERT(char(10),@DataFinal,120)	
	SET @HoraInicio = CONVERT(char(5),@HoraInicio,108)
	SET @HoraFinal = CONVERT(char(5),@HoraFinal,108)

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
			FROM Relatorios_Gerados (nolock) 
			WHERE 	id_relatorio = @Id_Relatorio 
				AND dataInicio >= @dataInicio 
				AND dataInicio < @DataFinal + 1 
				AND dataInicio = dataFinal
			GROUP BY 
				DataInicio, 
				DataFinal, 
				HoraInicio, 
				HoraFinal
		) AS T1

	
	IF @numDiasDesejado <> @numDiasExistente

		BEGIN
			RAISERROR (':Existem dias não gerados no período desejado!', 16, 1)
			RETURN 0
		END

	DECLARE @ID_Gerado INT

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GETDATE(), @Id_Relatorio, @DataInicio, @DataFinal,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO Relatorios_Volume_Hora_PMG with (rowlock)
		([ID_Gerado],[Local],[pista], [ClassePorTamanho],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],
		[h9],[h10],[h11],[h12],[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total])
	SELECT  @ID_Gerado, 
		[Local], [pista], [ClassePorTamanho], 
		SUM(h0), 
		SUM(h1), 
		SUM(h2), 
		SUM(h3), 
		SUM(h4), 
		SUM(h5), 
		SUM(h6), 
		SUM(h7), 
		SUM(h8), 
		SUM(h9), 
		SUM(h10), 
		SUM(h11),
		SUM(h12), 
		SUM(h13), 
		SUM(h14), 
		SUM(h15), 
		SUM(h16), 
		SUM(h17), 
		SUM(h18), 
		SUM(h19), 
		SUM(h20),
		SUM(h21),
		SUM(h22),
		SUM(h23),
		SUM(Total)
	FROM Relatorios_Volume_Hora_PMG (nolock)
	WHERE 	ID_Gerado in (	SELECT 	Max(ID_Gerado)
							FROM Relatorios_Gerados (nolock)
							WHERE 	id_relatorio = @Id_Relatorio 
								AND dataInicio >= @dataInicio 
								AND dataInicio < @DataFinal + 1 
								AND dataInicio = dataFinal
							GROUP BY DataInicio, DataFinal, HoraInicio, HoraFinal
							)
	GROUP BY 
		[Local], 
		pista, 
		ClassePorTamanho 

	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GETDATE() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado

	RETURN @ID_Gerado



