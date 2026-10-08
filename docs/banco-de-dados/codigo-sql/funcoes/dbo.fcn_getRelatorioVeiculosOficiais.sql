

CREATE FUNCTION [dbo].[fcn_getRelatorioVeiculosOficiais](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	-- SELECT * FROM cad_categoria 
	--DECLARE @dataInicio DATE = '2018-07-10', @dataFim DATE = '2018-07-10'
	SELECT i.id_infracao, i.placa, i.data, i.id_inconsistencia, inc.descricao, p.nome AS processo FROM infracao i (NOLOCK) 
	JOIN cad_veiculo cv (NOLOCK) ON i.placa = cv.placa
	JOIN inconsistencia inc (NOLOCK) ON i.id_inconsistencia = inc.id_inconsistencia
	JOIN processo p (NOLOCK) ON i.id_processo = p.id_processo
	WHERE 
	CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
	AND 
	cv.id_categoria = 3


)
