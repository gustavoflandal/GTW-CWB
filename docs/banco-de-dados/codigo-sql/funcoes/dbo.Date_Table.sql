CREATE FUNCTION [dbo].[Date_Table]
  ( @data_inicial datetime,
    @data_fim datetime )
RETURNS @retDataTable TABLE (
  [Data] datetime NOT NULL
)
AS

BEGIN
	
	DECLARE @data_atual DATETIME
	SET @data_atual = @data_inicial
	
	WHILE @data_atual <= @data_fim
		BEGIN
			INSERT INTO @retDataTable VALUES ( @data_atual )
			SET @data_atual = @data_atual + 1
		END
	
	RETURN
	
END




