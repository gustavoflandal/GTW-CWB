CREATE FUNCTION [dbo].[fcn_getRelatorioEnvioPMESP](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	SELECT CONVERT(VARCHAR, dh.Data, 120) Data, ec.id_local, COUNT(pm.id_movimento) AS movimentos FROM dbo.fcn_ObterDatasHorasPeriodo(@dataInicio,@dataFim) AS dh
	JOIN pmesp_movimento pm (NOLOCK) ON pm.data_recebido BETWEEN dh.Data AND DATEADD(HOUR, 1, dh.Data)
	JOIN pmesp_evento_conexao ec (NOLOCK) ON pm.id_evento_conexao = ec.id_evento_conexao
	GROUP BY dh.Data, ec.id_local

)

