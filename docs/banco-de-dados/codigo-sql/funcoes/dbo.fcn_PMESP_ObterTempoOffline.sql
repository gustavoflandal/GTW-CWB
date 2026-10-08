
CREATE FUNCTION [dbo].[fcn_PMESP_ObterTempoOffline]
(
	@id_local INT
)
RETURNS INT
AS
BEGIN
	DECLARE @ret INT

	-- Add the T-SQL statements to compute the return value here
	SELECT @ret = SUM(sub1.Desconectado) FROM 
	(
	-- DECLARE @id_local INT = 1111
	SELECT CONVERT(VARCHAR, dp.Data, 120) Data, 
	CASE WHEN EXISTS(SELECT 1 FROM pmesp_evento_conexao (NOLOCK) WHERE id_local = @id_local AND dp.Data BETWEEN data_conexao AND data_ultimo_movimento) THEN 0 ELSE 1 END AS Desconectado
	FROM fcn_ObterDatasMinutosPeriodo( GETDATE() - 1, GETDATE() ) dp 
	) AS sub1
	OPTION (MAXRECURSION 0)

	-- Return the result of the function
	RETURN @ret

END
