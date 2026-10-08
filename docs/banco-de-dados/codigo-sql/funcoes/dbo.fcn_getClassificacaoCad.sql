CREATE FUNCTION [dbo].[fcn_getClassificacaoCad] (@placa CHAR(7))
RETURNS INT
AS
BEGIN

-- 0 - PLACA NULA
-- 1 - LEVE
-- 2 - PESADO

	DECLARE @Resultado INT

	IF @placa IS NULL OR LTRIM(RTRIM(@placa)) = ''
		BEGIN
			SET @Resultado = 0
		END
	ELSE
		BEGIN
			SET @Resultado = (
								SELECT CASE WHEN ct.id_tipo NOT IN (7,8,10,11,14,17,18,20,22,26)--(7, 8, 14, 17)
											THEN 1
											WHEN ct.id_tipo IN (7,8,10,11,14,17,18,20,22,26)--(7, 8, 14, 17)
											THEN 2
									   END AS resultado
								FROM   cad_veiculo cv
									   INNER JOIN cad_tipo ct
											ON  ct.id_tipo = cv.id_tipo
								WHERE  cv.placa = @placa
							)
		END

	IF @Resultado IS NULL
		BEGIN
			SET @Resultado = 0
		END
		
	RETURN @Resultado

END
