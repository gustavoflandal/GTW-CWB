CREATE procedure [dbo].[spu_relatorio_aproveitamento_semanal_datas_ini_fim]
AS

	DECLARE @PrimeiraSemana INT

	SET @PrimeiraSemana = [dbo].[fcn_getUltimaSemanaProcessada] ()
	
	DECLARE @SemanaInicial Int
	DECLARE @SemanaFinal Int
	DECLARE @AnoInicial Int
	DECLARE @AnoFinal Int

	SET @SemanaInicial = datepart( WEEK , GETDATE() ) - (@PrimeiraSemana + 8)
	SET @SemanaFinal = datepart( WEEK , GETDATE() ) - (@PrimeiraSemana)
	SET @AnoInicial = datepart( YEAR , GETDATE() )
	SET @AnoFinal = @AnoInicial

	WHILE @SemanaInicial < 1

		BEGIN

			PRINT @SemanaInicial

			SET @AnoInicial = @AnoInicial - 1
			SET @SemanaInicial = @SemanaInicial + 53

		END 

	WHILE @SemanaFinal < 1

		BEGIN

			print @SemanaFinal

			SET @AnoFinal = @AnoFinal - 1
			SET @SemanaFinal = @SemanaFinal + 53

		END
	
	CREATE TABLE #Tabela_Datas_temp
	(
		menor Date, 
		maior Date
	)
	
	WHILE (@SemanaInicial <= @SemanaFinal)

		BEGIN
		
			DECLARE @PrimeiroDia date

			SET @PrimeiroDia = DATEADD(YEAR, @AnoInicial-DATEPART(YEAR, 0) , 0)
			SET @PrimeiroDia = DATEADD(WEEK, @SemanaInicial - 1, @PrimeiroDia)
		
			/*
			DECLARE @UltimoDia date

			SET @UltimoDia = DATEADD(YEAR, @AnoFinal-DATEPART(YEAR, 0) , 0)
			SET @UltimoDia = DATEADD(WEEK, @SemanaFinal - 1, @UltimoDia)
			*/
		
			INSERT INTO #Tabela_Datas_temp (
				menor, 
				maior) 
			VALUES (
				CAST(DATEADD(wk, DATEDIFF(WK, 6, @PrimeiroDia), 6) AS DATE),
				CAST(DATEADD(wk, DATEDIFF(WK, 5, @PrimeiroDia), 5) AS DATE))
		
			SET @SemanaInicial = @SemanaInicial + 1
		
		END
	
	SELECT 
		menor, 
		maior 
	FROM 
		#Tabela_Datas_temp
	
	DROP TABLE #Tabela_Datas_temp
	



