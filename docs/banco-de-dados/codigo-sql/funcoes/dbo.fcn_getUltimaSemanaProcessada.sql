

CREATE FUNCTION [dbo].[fcn_getUltimaSemanaProcessada]
  (  )
RETURNS int
AS

BEGIN
	
	DECLARE @SemanaProcessada Int
	DECLARE @Contador Int
	DECLARE @FINISHED Int

	SET @SemanaProcessada = 0

	SET @Contador = 0

	SET @FINISHED = 0
	
	WHILE (not(@FINISHED = @Contador and @FINISHED > 0 and @Contador > 0))

		BEGIN

			SET @SemanaProcessada = @SemanaProcessada + 1

			SELECT
				@FINISHED = SUM(	CASE 
										WHEN p.ativo = 1 and p.id_processo_proximo IS NULL
											THEN 1 
										ELSE 
											0 
									END),
				@Contador = COUNT(*)
			FROM infracao i (nolock)
				LEFT JOIN processo p (nolock) 
					ON i.id_processo = p.id_processo
			WHERE 
				datepart( wk , data) = datepart( wk , getdate() ) - @SemanaProcessada
		END

	RETURN @SemanaProcessada

END



