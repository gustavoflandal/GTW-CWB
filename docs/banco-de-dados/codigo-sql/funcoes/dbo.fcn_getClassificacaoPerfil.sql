CREATE FUNCTION [dbo].[fcn_getClassificacaoPerfil] (@classe CHAR(1))
RETURNS INT
AS
BEGIN

-- 0 - PLACA NULA
-- 1 - LEVE
-- 2 - PESADO

	DECLARE @Resultado INT

	IF @classe IS NULL OR LTRIM(RTRIM(@classe)) = ''
		BEGIN
			SET @Resultado = 0
		END
	ELSE
		BEGIN
			SET @Resultado = (
								SELECT CASE WHEN @classe NOT IN ('C', 'O')
											THEN 1
											WHEN @classe IN ('C', 'O')
											THEN 2
											ELSE 0
									   END AS resultado
							)
		END


		
	RETURN @Resultado

END
