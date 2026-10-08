

CREATE FUNCTION [dbo].[fcn_ObterDatasHorasPeriodo] (@DataInicial DATETIME, @DataFinal DATETIME)
RETURNS TABLE
AS
	RETURN
	(
		WITH CTE AS
		(
			SELECT @DataInicial AS Data

			UNION ALL

			SELECT DATEADD(HOUR, 1, Data)
			FROM CTE 
			WHERE Data < @DataFinal    
		)

		SELECT * FROM CTE

	)
