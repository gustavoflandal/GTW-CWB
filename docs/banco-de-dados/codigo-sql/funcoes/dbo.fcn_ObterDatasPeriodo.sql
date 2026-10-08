CREATE FUNCTION [dbo].[fcn_ObterDatasPeriodo] (@DataInicial DATE, @DataFinal DATE)
RETURNS TABLE
AS
	RETURN
	(
		SELECT TOP (DATEDIFF(DAY, @DataInicial, @DataFinal) + 1) DATEADD(DAY, ROW_NUMBER() OVER(ORDER BY a.object_id) - 1, @DataInicial) AS Data
		FROM   sys.all_objects a
			   CROSS JOIN sys.all_objects b
	)
