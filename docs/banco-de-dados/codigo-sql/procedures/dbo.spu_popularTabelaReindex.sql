CREATE PROCEDURE [dbo].[spu_popularTabelaReindex]
AS
	SET NOCOUNT ON;

	DECLARE @nome_indice VARCHAR(200), @nome_objeto VARCHAR(120), @nome_schema VARCHAR(30), @script_indice VARCHAR(MAX), @tipo VARCHAR(10);
	DECLARE @temp_indice AS TABLE (nome_indice VARCHAR(200), nome_objeto VARCHAR(120), avg_fragmentation_in_percent FLOAT, page_count BIGINT, fragment_count BIGINT, nome_schema VARCHAR(30))

	INSERT INTO @temp_indice
	SELECT TOP 20
		   B.name AS nome_indice,
		   OBJECT_NAME(A.object_id) AS nome_objeto,
		   A.avg_fragmentation_in_percent,
		   A.page_count,
		   A.fragment_count,
		   S.name AS nome_schema
	FROM   sys.dm_db_index_physical_stats(db_id(),NULL,NULL,NULL,'LIMITED') A
		   INNER JOIN sys.objects O
				ON  O.object_id = a.object_id
		   INNER JOIN sys.schemas S
				ON  s.schema_id = O.schema_id
		   INNER JOIN sys.indexes B
				ON  A.object_id = B.object_id
					AND A.index_id = B.index_id
	WHERE  A.avg_fragmentation_in_percent > 15
		   AND B.name IS NOT NULL
		  -- AND OBJECT_NAME(A.object_id) IN
				--(
				--	'cad_isento', 'cad_isento_arquivo', 'infracao', 'infracao_imagem', 'infracao_janela', 'infracao_obliteracao', 'infracao_processo', 'infracao_processo_concluido', 'infracao_processo_digitacao', 'infracao_processo_filtro',
				--	'infracao_processo_obliteracao', 'infracao_remessa', 'remessa', 'gera_remessa_automatico', 'gera_remessa_automatico_log', 'remessa_tipo', 'veiculo', 'veiculo_imagem', 'cad_veiculo'
				--)
		  -- AND (
				--	OBJECT_NAME(A.object_id) NOT IN ('eventos_csx', 'veiculo_estatistica', 'cad_isento', 'cad_isento_arquivo')
				--	OR A.object_id IN (SELECT object_id FROM sys.tables WHERE name LIKE 'infracao%' OR name LIKE 'veiculo%' OR name LIKE 'imagem%')
				--)
			--AND (
			--		S.name = 'dbo' AND OBJECT_NAME(A.object_id) NOT IN ('eventos_csx', 'veiculo_estatistica', 'cad_isento', 'cad_isento_arquivo')
			--	)
			AND S.name = 'muralha'

	TRUNCATE TABLE reindex
	
	SET @tipo = 'REBUILD'
	
	DECLARE cursor_rebuild CURSOR FOR 
	SELECT t.nome_indice,
		   t.nome_objeto,
		   t.nome_schema
	FROM   @temp_indice t
	WHERE  t.avg_fragmentation_in_percent > 40
		   AND t.page_count > 1000
	ORDER BY
		   t.avg_fragmentation_in_percent DESC,
		   t.fragment_count DESC;

	OPEN cursor_rebuild

	FETCH NEXT FROM cursor_rebuild 
	INTO @nome_indice, @nome_objeto, @nome_schema

	WHILE @@FETCH_STATUS = 0
	BEGIN

		SET @script_indice = 'ALTER INDEX [' + @nome_indice + '] ON [' + @nome_schema + '].[' + @nome_objeto + '] REBUILD'
		INSERT INTO reindex (tipo, script_reindex, data_atualizacao) VALUES (@tipo, @script_indice, GETDATE())
    
		FETCH NEXT FROM cursor_rebuild 
		INTO @nome_indice, @nome_objeto, @nome_schema

	END 
	CLOSE cursor_rebuild;
	DEALLOCATE cursor_rebuild;



	SET @tipo = 'REORGANIZE'

	DECLARE cursor_reorganize CURSOR FOR 
	SELECT t.nome_indice,
		   t.nome_objeto,
		   t.nome_schema
	FROM   @temp_indice t
	WHERE  (t.avg_fragmentation_in_percent > 15 AND t.avg_fragmentation_in_percent < 40)
	ORDER BY
			t.avg_fragmentation_in_percent DESC,
			t.fragment_count DESC

	OPEN cursor_reorganize

	FETCH NEXT FROM cursor_reorganize 
	INTO @nome_indice, @nome_objeto, @nome_schema

	WHILE @@FETCH_STATUS = 0
	BEGIN

		SET @script_indice = 'ALTER INDEX [' + @nome_indice + '] ON [' + @nome_schema + '].[' + @nome_objeto + '] REORGANIZE WITH (LOB_COMPACTION = ON)'
		INSERT INTO reindex (tipo, script_reindex, data_atualizacao) VALUES (@tipo, @script_indice, GETDATE())

		FETCH NEXT FROM cursor_reorganize 
		INTO @nome_indice, @nome_objeto, @nome_schema

	END 
	CLOSE cursor_reorganize;
	DEALLOCATE cursor_reorganize;
