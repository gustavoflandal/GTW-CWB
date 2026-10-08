
CREATE PROCEDURE [dbo].[spu_execute_index_defrag_all_indexes]
	@TableNameLike VARCHAR(MAX) = NULL
AS

	SET NOCOUNT ON

	-- Declare variables
	DECLARE @tablename VARCHAR (128)
	DECLARE @execstr   VARCHAR (255)

	-- Declare cursor
	DECLARE tables CURSOR FOR
		SELECT 
			TABLE_NAME
		FROM 
			INFORMATION_SCHEMA.TABLES
		WHERE	TABLE_TYPE = 'BASE TABLE'
			AND (TABLE_NAME like @TableNameLike OR @TableNameLike IS NULL)

	-- Open the cursor
	OPEN tables

	-- Loop through all the tables in the database
	FETCH NEXT
	   FROM tables
	   INTO @tablename

	WHILE @@FETCH_STATUS = 0

		BEGIN

		   PRINT 'Executing dbcc indexdefrag (0,' + RTRIM(@tablename) + ')'
		   SELECT @execstr = 'dbcc indexdefrag (0,' + RTRIM(@tablename) + ')'
	   
		   EXEC (@execstr)

		   FETCH NEXT
			  FROM tables
			  INTO @tablename
		END

	-- Close and deallocate the cursor
	CLOSE tables
	DEALLOCATE tables



