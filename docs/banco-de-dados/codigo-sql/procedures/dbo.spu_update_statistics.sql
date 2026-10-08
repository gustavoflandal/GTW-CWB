CREATE PROCEDURE [dbo].[spu_update_statistics]
	@MinModificationPercent FLOAT = 0.5,     -- Percentual mínimo de modificação
    @MinRowCount BIGINT = 1000,              -- Número mínimo de linhas na tabela
    @SamplePercent INT = 20,                 -- Percentual de amostragem (se FULLSCAN não for usado)
    @UseFullScan BIT = 0,                    -- 1 para FULLSCAN, 0 para amostragem
    @MaxDOP INT = 4,                         -- Máximo grau de paralelismo
    @MaintenanceWindowEnd DATETIME = NULL,   -- Fim da janela de manutenção
    @Debug BIT = 0                           -- Modo debug
AS
BEGIN
    SET NOCOUNT ON;

    -- Verifica janela de manutenção
    IF @MaintenanceWindowEnd IS NOT NULL AND GETDATE() > @MaintenanceWindowEnd
    BEGIN
        PRINT 'Fora da janela de manutenção.';
        RETURN;
    END;

    DECLARE @SQL NVARCHAR(MAX);
    DECLARE @SchemaName SYSNAME;
    DECLARE @TableName SYSNAME;
    DECLARE @StatsName SYSNAME;
    DECLARE @ModificationPercent FLOAT;

    -- Tabela temporária para armazenar estatísticas a serem atualizadas
    IF OBJECT_ID('tempdb..#StatsToUpdate') IS NOT NULL DROP TABLE #StatsToUpdate;
    
	CREATE TABLE #StatsToUpdate
	(
		SchemaName SYSNAME,
        TableName SYSNAME,
        StatsName SYSNAME,
        ModificationPercent FLOAT
    );

    -- Coleta estatísticas no banco de dados atual
    SET @SQL = N'
    INSERT INTO #StatsToUpdate (SchemaName, TableName, StatsName, ModificationPercent)
    SELECT s.name AS SchemaName,
		   t.name AS TableName,
		   st.name AS StatsName,
		   CASE WHEN sp.rows = 0 THEN 0 ELSE (CAST(sp.modification_counter AS FLOAT) / sp.rows) * 100 END AS ModificationPercent
    FROM   sys.tables t
		   INNER JOIN sys.schemas s
				ON  t.schema_id = s.schema_id
		   INNER JOIN sys.stats st
				ON  t.object_id = st.object_id
		   INNER JOIN sys.partitions p
				ON  t.object_id = p.object_id
					AND p.index_id IN (0, 1)
		   CROSS APPLY sys.dm_db_stats_properties(t.object_id, st.stats_id) sp
    WHERE  sp.rows > @MinRowCount
		   AND sp.modification_counter > 100
		   AND (CAST(sp.modification_counter AS FLOAT) / sp.rows) * 100 >= @MinModificationPercent
		   AND t.name NOT LIKE ''sys%'' AND t.name NOT LIKE ''dtp%''
    ORDER BY
		   (CAST(sp.modification_counter AS FLOAT) / sp.rows) DESC;';

    EXEC sp_executesql @SQL, 
        N'@MinRowCount BIGINT, @MinModificationPercent FLOAT', 
        @MinRowCount, @MinModificationPercent;

    -- Cursor para atualizar estatísticas
    DECLARE stats_cursor CURSOR LOCAL FAST_FORWARD
	FOR
        SELECT SchemaName,
			   TableName,
			   StatsName,
			   N'UPDATE STATISTICS ' + QUOTENAME(SchemaName) + N'.' + QUOTENAME(TableName) + N' ' + QUOTENAME(StatsName) + 
					CASE WHEN @UseFullScan = 1 THEN N' WITH FULLSCAN' ELSE N' WITH SAMPLE ' + CAST(@SamplePercent AS NVARCHAR(10)) + N' PERCENT' END + N', MAXDOP = ' + CAST(@MaxDOP AS NVARCHAR(10)) AS Command
        FROM   #StatsToUpdate
        ORDER BY
			   ModificationPercent DESC;

    OPEN stats_cursor;
    FETCH NEXT FROM stats_cursor INTO @SchemaName, @TableName, @StatsName, @SQL;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- Verifica janela de manutenção dentro do loop
        IF @MaintenanceWindowEnd IS NOT NULL AND GETDATE() > @MaintenanceWindowEnd
        BEGIN
            PRINT 'Fora da janela de manutenção durante atualização de estatísticas.';
            BREAK;
        END;

        IF @Debug = 1
        BEGIN
            PRINT @SQL;
        END
        ELSE
        BEGIN
            BEGIN TRY
                EXEC sp_executesql @SQL;
            END TRY
            BEGIN CATCH
                PRINT 'Erro ao atualizar estatísticas para ' + 
                      QUOTENAME(@SchemaName) + N'.' + 
                      QUOTENAME(@TableName) + N'.' + 
                      QUOTENAME(@StatsName) + N': ' + 
                      ERROR_MESSAGE();
            END CATCH;
        END;

        FETCH NEXT FROM stats_cursor INTO @SchemaName, @TableName, @StatsName, @SQL;
    END;

    CLOSE stats_cursor;
    DEALLOCATE stats_cursor;

    -- Limpeza
    DROP TABLE #StatsToUpdate;

    IF @Debug = 1
        PRINT 'Modo debug ativado. Nenhum comando foi executado.';
    ELSE
        PRINT 'Atualização de estatísticas concluída.';
END;
