
CREATE FUNCTION [dbo].[fcn_getRelatorioAvancoParada](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	
	--DECLARE @dataInicio DATE = '2018-07-09', @dataFim DATE = '2018-07-20'
	SELECT
	i1.id_infracao AS [Infração Parada], p1.nome AS [Processo Parada],
	i2.id_infracao AS [Infração Avanço], p2.nome AS [Processo Avanço]
	FROM infracao i1 (NOLOCK)
	JOIN infracao i2 (NOLOCK)
		ON  i1.data				=  i2.data
		AND i1.id_imagem_local	=  i2.id_imagem_local
		AND i1.id_local			=  i2.id_local
		AND i1.id_infracao		<> i2.id_infracao
	JOIN processo p1 (NOLOCK) ON i1.id_processo = p1.id_processo
	JOIN processo p2 (NOLOCK) ON i2.id_processo = p2.id_processo
	WHERE CAST(i1.data AS DATE) BETWEEN @dataInicio AND @dataFim
	AND   i1.id_enquadramento = 56732
	AND   i1.id_inconsistencia = 0
	AND   i2.id_enquadramento = 60503
	AND   i2.id_inconsistencia = 0

)
