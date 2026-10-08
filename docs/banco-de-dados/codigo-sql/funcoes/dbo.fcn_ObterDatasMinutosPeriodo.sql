
CREATE FUNCTION [dbo].[fcn_ObterDatasMinutosPeriodo] (@DataInicial DATETIME, @DataFinal DATETIME)
RETURNS TABLE
AS
	RETURN
	(
		WITH CTE AS
		(
			SELECT @DataInicial AS Data

			UNION ALL

			SELECT DATEADD(MINUTE, 1, Data)
			FROM CTE 
			WHERE Data < @DataFinal    
		)

		SELECT * FROM CTE

	)
