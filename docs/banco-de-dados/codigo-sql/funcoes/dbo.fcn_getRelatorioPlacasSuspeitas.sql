CREATE FUNCTION [dbo].[fcn_getRelatorioPlacasSuspeitas](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 

--DECLARE @dataInicio DATE = '2017-11-25', @dataFim DATE = '2017-11-25'
SELECT cvv.placa, i.id_infracao, i.data, i.id_enquadramento, i.id_local, i.pista, p.nome AS processo FROM cad_veiculo_verificar cvv (NOLOCK)
JOIN infracao i (NOLOCK) ON cvv.placa = i.placa
JOIN processo p (NOLOCK) ON i.id_processo = p.id_processo
WHERE 
i.id_inconsistencia = 0
AND CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim

)
