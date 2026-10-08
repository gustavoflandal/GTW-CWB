CREATE FUNCTION [dbo].[fcn_getDataInicioSumariza] ()
RETURNS DATE
AS
BEGIN
	--RETURN '2025-07-01'
	
	DECLARE @data_inicio DATE = CAST(GETDATE() AS DATE)
	DECLARE @dia INT = DATEPART(DAY, @data_inicio)

	IF (@dia >= 5)
	BEGIN
		SET @data_inicio = CAST(DATEPART(YEAR, @data_inicio) AS VARCHAR(4)) + '-' +
							RIGHT('00' + CAST(DATEPART(MONTH, @data_inicio) AS VARCHAR(2)), 2) + '-' +
							'01'
	END
	ELSE
	BEGIN
		SET @data_inicio = CAST(DATEPART(YEAR, @data_inicio) AS VARCHAR(4)) + '-' +
							RIGHT('00' + CAST(DATEPART(MONTH, DATEADD(MONTH,-1,@data_inicio)) AS VARCHAR(2)), 2) + '-' +
							'01'
	END
 
	RETURN @data_inicio;
	--SELECT @data_inicio, @dia
	
END
