CREATE procedure [dbo].[spu_relatorio_aproveitamento_semanal_datas]
AS
	DECLARE @PrimeiraSemana INT
	SET @PrimeiraSemana = [dbo].[fcn_getUltimaSemanaProcessada] ()
	
	
	print @PrimeiraSemana
	
	
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
		SET @AnoInicial = @AnoInicial - 1;
		SET @SemanaInicial = @SemanaInicial + 53;
	END


	WHILE @SemanaFinal < 1
	BEGIN
		SET @AnoFinal = @AnoFinal - 1;
		SET @SemanaFinal = @SemanaFinal + 53;
	END 

	DECLARE @PrimeiroDia date;
	SET @PrimeiroDia = DATEADD(YEAR, @AnoInicial-DATEPART(YEAR, 0) , 0);
	SET @PrimeiroDia = DATEADD(WW, @SemanaInicial-DATEPART(WW, @PrimeiroDia), @PrimeiroDia);
	SET @PrimeiroDia = DATEADD(DW, 1-DATEPART(DW, @PrimeiroDia), @PrimeiroDia);
	
	DECLARE @UltimoDia date;
	SET @UltimoDia = DATEADD(YEAR, @AnoFinal-DATEPART(YEAR, 0) , 0);
	SET @UltimoDia = DATEADD(WW, @SemanaFinal-DATEPART(WW, @UltimoDia), @UltimoDia);
	SET @UltimoDia = DATEADD(DW, 7-DATEPART(DW, @UltimoDia), @UltimoDia);

	SELECT [Local]
      ,[Pista]
      ,[Ano]
      ,'Semana ' + cast([NumSemana] as varchar) + ' - ' + cast(CONVERT(VARCHAR(10),[Data], 3) as varchar) AS NumSemana
      ,[Validas]
      ,[PTL]
      ,[PNT]
      ,[PTG]
      ,[Total]
      ,[LocalEPista]
      ,CONVERT( INT,(CASE WHEN [PTG] > 0 THEN ([Validas] / CONVERT( DECIMAL(6,2),[Validas] + [PTG])) ELSE 1 END) * 100) AS AprovLigPTG
      ,CONVERT( INT,(CASE WHEN [PTG] > 0 THEN ([Validas] / CONVERT( DECIMAL(6,2),[Validas] + [PTG] + [PTL])) ELSE 1 END) * 100) AS AprovLigPT
	FROM 
		[relatorio_aproveitamento_semanal] ras
	WHERE
		Data between @PrimeiroDia and @UltimoDia
		
	order by local, Pista, NumSemana



