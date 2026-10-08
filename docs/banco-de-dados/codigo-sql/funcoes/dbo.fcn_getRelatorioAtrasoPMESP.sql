CREATE FUNCTION [dbo].[fcn_getRelatorioAtrasoPMESP](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
SELECT	CONVERT(VARCHAR, pm.data_recebido, 120) data_recebido, 
		CONVERT(VARCHAR, pm.data_movimento, 120) data_movimento, 
		pec.id_local, pm.id_movimento, 
		DATEDIFF(MILLISECOND, pm.data_movimento, pm.data_recebido) atraso 
FROM pmesp_movimento pm (NOLOCK)
JOIN pmesp_evento_conexao pec (NOLOCK) ON pm.id_evento_conexao = pec.id_evento_conexao
WHERE	pec.id_local IS NOT NULL
		AND CAST(pm.data_recebido AS DATE) BETWEEN @dataInicio AND @dataFim

)

