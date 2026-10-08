CREATE FUNCTION [dbo].[fcn_getAlertaNaoEnviaEventos]()
RETURNS TABLE
AS
RETURN 
(
	SELECT TOP 100 
		dp.proprietario,
		CAST(((GETDATE()-1) - MAX(data_hora)) AS INT) AS dias
	FROM local_status ls (nolock)
		INNER JOIN eventos_csx_desc_proprietario dp (nolock)
			ON SUBSTRING(dp.proprietario,4,4) = CAST(ls.id_local AS VARCHAR(4))
		INNER JOIN eventos_csx e (nolock)
			ON dp.id_proprietario = e.id_proprietario
	WHERE ls.status_conexao = 1
	GROUP BY
		dp.proprietario
	HAVING
		(GETDATE()-1) - MAX(data_hora) >= 1
	ORDER BY 2 DESC
)


