CREATE FUNCTION [dbo].[fcn_getClassificacaoVeiculoRef] (@comprimento FLOAT)
RETURNS CHAR(1)
AS
BEGIN

	--DECLARE @comprimento FLOAT = 18.5
	DECLARE @id_classe_ref CHAR(1)

	IF @comprimento IS NULL
	BEGIN
		SET @id_classe_ref = NULL
	END
	ELSE
	BEGIN
		SET @id_classe_ref = (
								SELECT cvr.id_classe_ref
								FROM   classificacao_veiculo_ref cvr
								WHERE  (
											(cvr.referencia_ini IS NULL AND cvr.referencia_fim IS NOT NULL AND @comprimento < cvr.referencia_fim)
											OR
											(cvr.referencia_ini IS NOT NULL AND cvr.referencia_fim IS NOT NULL AND @comprimento BETWEEN cvr.referencia_ini AND cvr.referencia_fim)
											OR
											(cvr.referencia_ini IS NOT NULL AND cvr.referencia_fim IS NULL AND @comprimento >= cvr.referencia_ini)
										)
								)
	END

	--SELECT @id_classe_ref

	RETURN @id_classe_ref

END
