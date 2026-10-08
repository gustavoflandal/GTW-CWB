CREATE PROCEDURE [dbo].[spu_executarReindex]
AS

	DECLARE @script_indice NVARCHAR(MAX)

	DECLARE cursor_rebuild CURSOR FOR 
	SELECT script_reindex
	FROM   reindex
	WHERE  tipo = 'REBUILD'

	OPEN cursor_rebuild

	FETCH NEXT FROM cursor_rebuild 
	INTO @script_indice

	WHILE @@FETCH_STATUS = 0
	BEGIN

		EXEC sp_executesql @script_indice
    
		FETCH NEXT FROM cursor_rebuild 
		INTO @script_indice

	END 
	CLOSE cursor_rebuild;
	DEALLOCATE cursor_rebuild;



	DECLARE cursor_reorganize CURSOR FOR 
	SELECT script_reindex
	FROM   reindex
	WHERE  tipo = 'REORGANIZE'

	OPEN cursor_reorganize

	FETCH NEXT FROM cursor_reorganize 
	INTO @script_indice

	WHILE @@FETCH_STATUS = 0
	BEGIN

		EXEC sp_executesql @script_indice

		FETCH NEXT FROM cursor_reorganize 
		INTO @script_indice

	END 
	CLOSE cursor_reorganize;
	DEALLOCATE cursor_reorganize;
