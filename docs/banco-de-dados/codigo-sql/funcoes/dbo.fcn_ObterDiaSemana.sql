CREATE FUNCTION [dbo].[fcn_ObterDiaSemana] (@Data DATE)
RETURNS TABLE
AS
	RETURN
	(
		--DECLARE @Data DATE = CAST(GETDATE() AS DATE)
		SELECT DATEPART(WEEKDAY, @Data) AS dia_semana,
			   CASE WHEN DATEPART(WEEKDAY, @Data) = 1 THEN 'Domingo'
				    WHEN DATEPART(WEEKDAY, @Data) = 2 THEN 'Segunda-feira'
				    WHEN DATEPART(WEEKDAY, @Data) = 3 THEN 'Terça-feira'
				    WHEN DATEPART(WEEKDAY, @Data) = 4 THEN 'Quarta-feira'
				    WHEN DATEPART(WEEKDAY, @Data) = 5 THEN 'Quinta-feira'
				    WHEN DATEPART(WEEKDAY, @Data) = 6 THEN 'Sexta-feira'
				    WHEN DATEPART(WEEKDAY, @Data) = 7 THEN 'Sábado'
			   END AS dia_semana_extenso,
			   CASE WHEN DATEPART(WEEKDAY, @Data) = 1 THEN 'Domingo'
				    WHEN DATEPART(WEEKDAY, @Data) = 2 THEN 'Segunda'
				    WHEN DATEPART(WEEKDAY, @Data) = 3 THEN 'Terça'
				    WHEN DATEPART(WEEKDAY, @Data) = 4 THEN 'Quarta'
				    WHEN DATEPART(WEEKDAY, @Data) = 5 THEN 'Quinta'
				    WHEN DATEPART(WEEKDAY, @Data) = 6 THEN 'Sexta'
				    WHEN DATEPART(WEEKDAY, @Data) = 7 THEN 'Sábado'
			   END AS dia_semana_abreviado
			   --DATENAME(WEEKDAY, @Data) AS dia_semana_extenso
	)
