


CREATE PROCEDURE dbo.spu_index_tuning_framework
(
    @fase VARCHAR(20) = 'ALL'
)
AS
BEGIN
    SET NOCOUNT ON;

    --FASES:
    --- CLASSIFICACAO
    --- MERGE
    --- MISSING
    --- ALL

    -- ====================================================== 
	-- FASE 1 — CLASSIFICAÇÃO DE ÍNDICES (KEEP / MERGE / DROP)
    -- ======================================================
    IF @fase IN ('CLASSIFICACAO', 'ALL')
    BEGIN
        SELECT 'CLASSIFICACAO' AS fase,
			   OBJECT_SCHEMA_NAME(i.object_id) AS schema_name,
			   OBJECT_NAME(i.object_id) AS tabela,
			   i.name AS indice,
			   ISNULL(s.user_seeks,0) AS seeks,
			   ISNULL(s.user_scans,0) AS scans,
			   ISNULL(s.user_lookups,0) AS lookups,
			   ISNULL(s.user_updates,0) AS updates,
			   CASE WHEN (ISNULL(s.user_seeks,0) + ISNULL(s.user_scans,0) + ISNULL(s.user_lookups,0)) = 0 AND ISNULL(s.user_updates,0) > 0 THEN 'DROP'
					WHEN ISNULL(s.user_updates,0) > (ISNULL(s.user_seeks,0) + ISNULL(s.user_scans,0) + ISNULL(s.user_lookups,0)) THEN 'MERGE'
					ELSE 'KEEP'
				END AS recomendacao
        FROM   sys.indexes i
			   LEFT JOIN sys.dm_db_index_usage_stats s
					ON  s.object_id = i.object_id
						AND s.index_id = i.index_id
						AND s.database_id = DB_ID()
        WHERE  OBJECTPROPERTY(i.object_id, 'IsUserTable') = 1
			   AND i.type_desc = 'NONCLUSTERED'
			   AND i.is_primary_key = 0
			   AND i.is_unique_constraint = 0
			   AND i.index_id NOT IN
			   (
					SELECT kc.unique_index_id
					FROM   sys.foreign_keys fk
						   JOIN sys.key_constraints kc
								ON  fk.referenced_object_id = kc.parent_object_id
			   );
    END

    -- ======================================
    -- FASE 2 — ÍNDICES CANDIDATOS A MERGE
    -- ======================================
    IF @fase IN ('MERGE', 'ALL')
    BEGIN
        EXEC dbo.spu_indices_candidatos_merge;
    END

    -- ======================================
    -- FASE 3 — MISSING INDEXES (CREATE)
    -- ======================================
    IF @fase IN ('MISSING', 'ALL')
    BEGIN
     
   EXEC dbo.spu_sugestao_criacao_indices_ausentes;
    END
END;
