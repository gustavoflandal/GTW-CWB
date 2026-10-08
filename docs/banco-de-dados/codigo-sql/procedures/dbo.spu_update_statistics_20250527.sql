CREATE PROCEDURE [dbo].[spu_update_statistics_20250527]
AS
BEGIN

	SET NOCOUNT ON
	-- Sai da rotina quando a janela de manutenção é finalizada
	IF GETDATE() > DATEADD(mi, +30, DATEADD(hh, +05, CAST(FLOOR(CAST(GETDATE() AS FLOAT)) AS DATETIME))) --hora > 05:30
	BEGIN
		PRINT 'FORA DA JANELA DE MANUTENÇÃO (02h00 à 04h30)'
		RETURN
	END

	IF OBJECT_ID('tempdb..#Tamanho_Tabelas') IS NOT NULL
	BEGIN
		DROP TABLE #Tamanho_Tabelas
	END

	IF OBJECT_ID('tempdb..#Atualiza_Estatisticas') IS NOT NULL
	BEGIN
		DROP TABLE #Atualiza_Estatisticas
	END

	CREATE TABLE #Tamanho_Tabelas([name] SYSNAME, [rows] BIGINT, [schema] SYSNAME)
	CREATE TABLE #Atualiza_Estatisticas(Id_Estatistica INT IDENTITY(1,1), Ds_Comando VARCHAR(4000), Nr_Linha INT)

	INSERT INTO #Tamanho_Tabelas
	SELECT obj.name, prt.rows, sch.name AS [schema]
	FROM   sys.objects obj
		   JOIN sys.schemas sch ON sch.schema_id = obj.schema_id
		   JOIN sys.indexes idx on obj.object_id= idx.object_id
		   JOIN sys.partitions prt on obj.object_id= prt.object_id
		   JOIN sys.allocation_units alloc on alloc.container_id= prt.partition_id
	WHERE  obj.type = 'U'
		   AND idx.index_id IN (0, 1)
		   AND prt.rows > 1000
		   --AND sch.name = 'muralha'
	GROUP BY
		   obj.name, prt.rows, sch.name

	INSERT INTO #Atualiza_Estatisticas (Ds_Comando, Nr_Linha)
	SELECT 'UPDATE STATISTICS ' + D.[schema] + '.' + B.name + ' ' + A.name + ' WITH FULLSCAN' COLLATE SQL_Latin1_General_CP1_CI_AS
		  ,D.rows
	FROM   sys.stats A
		   JOIN sys.sysobjects B ON A.object_id = B.id
		   JOIN sys.sysindexes C ON C.id = B.id AND A.name = C.Name
		   JOIN #Tamanho_Tabelas D ON B.name COLLATE SQL_Latin1_General_CP1_CI_AS = D.Name COLLATE SQL_Latin1_General_CP1_CI_AS
	WHERE  C.rowmodctr > 100
		   AND C.rowmodctr > D.rows*.005
		   AND SUBSTRING(B.name, 1, 3) NOT IN ('sys','dtp')
	ORDER BY
		   --D.[schema] DESC,
		   D.rows

	DECLARE @Loop INT, @Comando NVARCHAR(4000)
	SET @Loop = 1

	WHILE EXISTS(SELECT TOP 1 NULL FROM #Atualiza_Estatisticas)
	BEGIN

		IF GETDATE() > DATEADD(mi, +30, DATEADD(hh, +05, CAST(FLOOR(CAST(GETDATE() AS FLOAT)) AS DATETIME))) --hora > 05:30 am
		BEGIN
			PRINT 'FORA DA JANELA DE MANUTENÇÃO (02h00 à 04h30)'
			BREAK --Sai do loop quando acabar a janela de manutenção
		END

		SELECT @Comando = Ds_Comando
		FROM   #Atualiza_Estatisticas
		WHERE  Id_Estatistica = @Loop

		EXECUTE sp_executesql @Comando

		DELETE FROM #Atualiza_Estatisticas WHERE Id_Estatistica = @Loop

		SET @Loop= @Loop + 1

	END
END
