CREATE PROCEDURE dbo.spu_indices_candidatos_merge
AS
BEGIN
    SET NOCOUNT ON;

	WITH IndexCols AS
	(
		SELECT i.object_id,
			   i.index_id,
			   i.name AS indice,
			   ic.is_included_column,
			   ic.key_ordinal,
			   c.name AS coluna
		FROM   sys.indexes i
			   JOIN sys.index_columns ic
					ON  i.object_id = ic.object_id
						AND i.index_id = ic.index_id
			   JOIN sys.columns c
					ON  c.object_id = ic.object_id
						AND c.column_id = ic.column_id
		WHERE  i.type_desc = 'NONCLUSTERED'
			   AND i.is_primary_key = 0
			   AND i.is_unique_constraint = 0
			   AND OBJECTPROPERTY(i.object_id, 'IsUserTable') = 1
			   AND i.index_id NOT IN
			   (
					SELECT kc.unique_index_id
					FROM   sys.foreign_keys fk
						   JOIN sys.key_constraints kc
								ON  fk.referenced_object_id = kc.parent_object_id
			   )
	),
	IndexDef AS
	(
		SELECT DISTINCT
			   a.object_id,
			   a.indice,
			   
			   -- colunas chave
			   STUFF
				(
					(
						SELECT ', ' + b.coluna
						FROM   IndexCols b
						WHERE  b.object_id = a.object_id
							   AND b.indice = a.indice
							   AND b.is_included_column = 0
						ORDER BY
							   b.key_ordinal
						FOR XML PATH(''), TYPE
					).value('.', 'NVARCHAR(MAX)')
				,1,2,'') AS chaves,
			   
			   -- colunas INCLUDE
			   STUFF
				(
					(
						SELECT ', ' + b.coluna
						FROM   IndexCols b
						WHERE  b.object_id = a.object_id
							   AND b.indice = a.indice
							   AND b.is_included_column = 1
						FOR XML PATH(''), TYPE
					).value('.', 'NVARCHAR(MAX)')
				,1,2,'') AS includes
		FROM   IndexCols a
	),
	GruposMerge AS
	(
		SELECT object_id,
			   chaves,
			   COUNT(*) AS qtd_indices
		FROM   IndexDef
		GROUP BY
			   object_id,
			   chaves
		HAVING COUNT(*) > 1
	),
	IncludesMerge AS
	(
		SELECT d.object_id,
			   d.chaves,
			   STUFF
				(
					(
						SELECT DISTINCT ', ' + d2.includes
						FROM   IndexDef d2
						WHERE  d2.object_id = d.object_id
							   AND d2.chaves = d.chaves
							   AND d2.includes IS NOT NULL
						FOR XML PATH(''), TYPE
					).value('.', 'NVARCHAR(MAX)')
				,1,2,'') AS includes_merge
		FROM   IndexDef d
			   JOIN GruposMerge g
					ON  g.object_id = d.object_id
						AND g.chaves = d.chaves
		GROUP BY
			   d.object_id,
			   d.chaves
	)

	SELECT 'MERGE' AS fase,
		   OBJECT_SCHEMA_NAME(g.object_id) AS schema_name,
		   OBJECT_NAME(g.object_id) AS tabela,
		   g.chaves,
		   
		   -- CREATE consolidado
		   'CREATE NONCLUSTERED INDEX IX_' +
				OBJECT_NAME(g.object_id) + '_' +
				REPLACE(g.chaves, ', ', '_') +
				' ON ' +
				QUOTENAME(OBJECT_SCHEMA_NAME(g.object_id)) + '.' +
				QUOTENAME(OBJECT_NAME(g.object_id)) +
				' (' + g.chaves + ')' +
				CASE WHEN im.includes_merge IS NOT NULL THEN ' INCLUDE (' + im.includes_merge + ')' ELSE '' END + ';'
			AS script_create,
		   
		   -- DROP dos índices antigos
		   STUFF
			(
				(
					SELECT CHAR(10) +
						   'DROP INDEX ' + QUOTENAME(d.indice) +
						   ' ON ' +
						   QUOTENAME(OBJECT_SCHEMA_NAME(d.object_id)) + '.' +
						   QUOTENAME(OBJECT_NAME(d.object_id)) + ';'
					FROM   IndexDef d
					WHERE  d.object_id = g.object_id
						   AND d.chaves = g.chaves
					FOR XML PATH(''), TYPE
				).value('.', 'NVARCHAR(MAX)')
			,1,1,'') AS script_drop

	FROM   GruposMerge g
		   JOIN IncludesMerge im
				ON  im.object_id = g.object_id
					AND im.chaves = g.chaves
	ORDER BY
		   schema_name,
		   tabela;

END;
