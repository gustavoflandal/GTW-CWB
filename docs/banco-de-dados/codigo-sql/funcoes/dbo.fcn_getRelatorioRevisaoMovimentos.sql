
CREATE FUNCTION [dbo].[fcn_getRelatorioRevisaoMovimentos](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
( 

SELECT TOP 100 PERCENT r.id_remessa, r.tipo, r.codigo_externo, r.data, r.data_inicial, r.revisao - 1 AS [revisao_atual], r.revisao proxima_revisao
, 'LM' + RTRIM(LTRIM(r.tipo)) + 
RIGHT(REPLICATE('0',6) + 
LTRIM(RTRIM(codigo_externo)), 6) + 
CONVERT(VARCHAR, data, 112) + '.TXT' 
AS nome_arquivo FROM remessa r (NOLOCK) 
LEFT JOIN movimento_arquivo ma (NOLOCK) ON r.codigo_externo = ma.id_movimento AND r.tipo = SUBSTRING(ma.nome_arquivo, 3,2) 
LEFT JOIN movimentos_erro er ON r.tipo = er.tipo AND r.codigo_externo = er.id_movimento 
WHERE ma.id_movimento_arquivo is null and er.id_movimento is null 
ORDER BY 1

)
