
CREATE FUNCTION [dbo].[fcn_getRelatorioVelocidadeMedia1](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	--DECLARE @dataInicio DATE = '2017-12-14', @dataFim DATE = '2017-12-14'

SELECT 
sub1.[Data],
sub1.[Quantidade de Veículos 00:00 as 23:59],
COALESCE(sub2.infracoes, 0) AS [Excesso de velocidade média 00:00 as 23:59],
sub3.montante AS  [Quantidade de Infrações VELOCIDADE PONTUAL A mais 23M da R Porto Martins (Ponto A)],
sub3.jusante AS   [Quantidade de Infrações VELOCIDADE PONTUAL A menos 197M do número 2040 (Ponto B)]
FROM 
(
	--DECLARE @dataInicio DATE = '2017-11-07', @dataFim DATE = '2017-11-07'
	SELECT 
	CAST(vm2.data AS DATE) AS [Data],
	COUNT(*) AS [Quantidade de Veículos 00:00 as 23:59]
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
	GROUP BY 
	CAST(vm2.data AS DATE)

) AS sub1
LEFT JOIN (
	--DECLARE @dataInicio DATE = '2017-11-07', @dataFim DATE = '2017-11-07'
	SELECT CAST(i.data AS DATE) AS data, COUNT(*) AS infracoes FROM infracao i (NOLOCK)
	WHERE CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
	AND	  i.id_enquadramento = 99999 
	AND   i.id_inconsistencia != 45
	GROUP BY CAST(i.data AS DATE)
) AS sub2
ON sub1.Data = sub2.data
JOIN (
	--DECLARE @dataInicio DATE = '2017-11-07', @dataFim DATE = '2017-11-07'
	SELECT CAST(i.data AS DATE) AS data, 
	SUM(CASE WHEN i.id_local = 7203 THEN 1 ELSE 0 END) AS montante,
	SUM(CASE WHEN i.id_local = 7237 THEN 1 ELSE 0 END) AS jusante
	FROM infracao i (NOLOCK) 
	JOIN enquadramento e (NOLOCK) ON i.id_enquadramento = e.id_enquadramento
	WHERE i.id_local IN (7203, 7237)
	AND CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
	AND e.infracao_metrologia = 1
	GROUP BY CAST(i.data AS DATE)
) AS sub3 
ON sub1.Data = sub3.data

)



