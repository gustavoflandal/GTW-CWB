
CREATE PROCEDURE [dbo].[PMESP_ObterTempoOffline](@id_local INT)
AS

DECLARE @tempo_desconectado INT

SELECT @tempo_desconectado = SUM(sub1.Desconectado) FROM 
(
SELECT CONVERT(VARCHAR, dp.Data, 120) Data, 
CASE WHEN NOT EXISTS(SELECT 1 FROM pmesp_evento_conexao (NOLOCK) WHERE id_local = 7113 AND dp.Data BETWEEN data_conexao AND data_ultimo_movimento) THEN 1 ELSE 0 END AS Desconectado
FROM fcn_ObterDatasMinutosPeriodo( GETDATE() - 1, GETDATE() ) dp 
) AS sub1
OPTION (MAXRECURSION 0)

RETURN @tempo_desconectado

