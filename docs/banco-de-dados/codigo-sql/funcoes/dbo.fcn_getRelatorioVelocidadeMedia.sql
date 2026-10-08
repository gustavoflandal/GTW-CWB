

CREATE FUNCTION [dbo].[fcn_getRelatorioVelocidadeMedia](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	--DECLARE @dataInicio DATE = '2017-11-07', @dataFim DATE = '2017-11-07'

SELECT vm1.placa AS [PLACA], 
CONVERT(VARCHAR,vm1.data, 120) AS [DATA/HORA MONTANTE], 
CONVERT(VARCHAR,vm2.data, 120) AS [DATA/HORA JUSANTE],
DATEDIFF(SECOND, vm1.data, vm2.data) AS [SEGUNDOS (DIFERENÇA)],
CAST(1571.5 / DATEDIFF(SECOND, vm1.data, vm2.data) * 3.6 AS INT) AS [VELOCIDADE MÉDIA CALCULADA]
FROM (
	SELECT id_local, placa, data 
	FROM veiculo_pesquisa (NOLOCK) 
	WHERE id_local = 7203
	AND CAST(data AS DATE) BETWEEN @dataInicio AND @dataFim
) AS vm1
JOIN (
	SELECT id_local, placa, data 
	FROM veiculo_pesquisa (NOLOCK) 
	WHERE id_local = 7237
	AND CAST(data AS DATE) BETWEEN @dataInicio AND @dataFim
) AS vm2 
ON vm1.placa = vm2.placa
WHERE 
vm1.data < vm2.data
AND 
(1571.5 / DATEDIFF(SECOND, vm1.data, vm2.data) * 3.6) >= 5.0


)
