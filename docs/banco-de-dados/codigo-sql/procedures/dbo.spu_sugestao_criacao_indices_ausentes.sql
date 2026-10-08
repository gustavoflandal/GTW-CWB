


CREATE PROCEDURE dbo.spu_sugestao_criacao_indices_ausentes
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 'MISSING' AS fase,
		   OBJECT_SCHEMA_NAME(mid.object_id, mid.database_id) AS schema_name,
		   OBJECT_NAME(mid.object_id, mid.database_id) AS tabela,

		   'CREATE NONCLUSTERED INDEX IX_' +
				OBJECT_NAME(mid.object_id, mid.database_id) + '_' +
				REPLACE(REPLACE( ISNULL(REPLACE(mid.equality_columns, ', ', '_'), '') + ISNULL(REPLACE(mid.inequality_columns, ', ', '_'), ''), '[',''),']','') +
				' ON ' +
				QUOTENAME(OBJECT_SCHEMA_NAME(mid.object_id, mid.database_id)) + '.' +
				QUOTENAME(OBJECT_NAME(mid.object_id, mid.database_id)) +
				' (' +
					ISNULL(mid.equality_columns,'') +
					CASE
						WHEN mid.equality_columns IS NOT NULL
						 AND mid.inequality_columns IS NOT NULL THEN ', '
						ELSE ''
					END +
					ISNULL(mid.inequality_columns,'') +
				')' +
				ISNULL(' INCLUDE (' + mid.included_columns + ')','') +
				';' AS script_create,
		   (migs.avg_total_user_cost * migs.avg_user_impact / 100.0) * (migs.user_seeks + migs.user_scans) AS ganho_estimado
    FROM   sys.dm_db_missing_index_group_stats migs
		   JOIN sys.dm_db_missing_index_groups mig
				ON  migs.group_handle = mig.index_group_handle
		   JOIN sys.dm_db_missing_index_details mid
				ON  mig.index_handle = mid.index_handle
    WHERE  mid.database_id = DB_ID()
		   AND OBJECTPROPERTY(mid.object_id, 'IsUserTable') = 1
    ORDER BY
		   ganho_estimado DESC;
END;
