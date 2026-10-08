
CREATE      PROCEDURE [dbo].[spu_cria_Relatorios_Periodo_MediaVelocidade_Hora]
	@DataInicio datetime,
	@DataFinal  datetime,
	@HoraInicio datetime,
	@HoraFinal  datetime,
	@Usuario char(30)
AS
	DECLARE @DataStart DateTime
	DECLARE @ID_Relatorio INT
	
	SET @Id_Relatorio = 4 -- Número do ID do relatório PMG

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
			FROM	
				Relatorios_Gerados (nolock) 
			WHERE 	id_relatorio = @Id_Relatorio 
				and dataInicio >= @dataInicio 
				and dataInicio < @DataFinal + 1 
				and horaInicio = @horaInicio 
				and horafinal = @HoraFinal
				and dataInicio = dataFinal
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

	DECLARE @ID_Gerado INT

	INSERT INTO Relatorios_Gerados with (rowlock)
		(Data,ID_Relatorio,DataInicio,DataFinal,HoraInicio,HoraFinal,Gerador)
	VALUES 
		(GETDATE(), @Id_Relatorio, @DataInicio, @DataFinal,@HoraInicio,@HoraFinal,@Usuario)
	
	SELECT 
		@ID_Gerado = Max(ID_Gerado) 
	FROM 
		Relatorios_Gerados (nolock)

	INSERT INTO dbo.Relatorios_MediaVelocidade_Hora with (rowlock)
		([ID_Gerado],[Local],[h0],[h1],[h2],[h3],[h4],[h5],[h6],[h7],[h8],[h9],[h10],
		[h11],[h12],[h13],[h14],[h15],[h16],[h17],[h18],[h19],[h20],[h21],[h22],[h23],[Total])
	SELECT  @ID_Gerado, 
		[Local],  
		AVG(h0), 
		AVG(h1), 
		AVG(h2), 
		AVG(h3), 
		AVG(h4), 
		AVG(h5), 
		AVG(h6), 
		AVG(h7), 
		AVG(h8), 
		AVG(h9), 
		AVG(h10), 
		AVG(h11),
		AVG(h12), 
		AVG(h13), 
		AVG(h14), 
		AVG(h15), 
		AVG(h16), 
		AVG(h17), 
		AVG(h18), 
		AVG(h19), 
		AVG(h20),
		AVG(h21),
		AVG(h22),
		AVG(h23),
		AVG(Total)
	FROM Relatorios_MediaVelocidade_Hora (nolock)
	WHERE 	ID_Gerado in (	SELECT 	
								Max(ID_Gerado)
							FROM
								Relatorios_Gerados 
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
	GROUP BY [Local]

	UPDATE Relatorios_Gerados with (rowlock)
	SET TempoGeracao = GETDATE() - @DataStart 
	WHERE ID_Gerado = @ID_Gerado
	
	RETURN @ID_Gerado



