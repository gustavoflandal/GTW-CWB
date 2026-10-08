
CREATE FUNCTION [dbo].[fcn_getRelatorioMovimentosImportados](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
( 
-- DECLARE @dataInicio DATE = '2017-04-17', @dataFim DATE = '2017-04-19'
SELECT CAST(sub1.data AS DATE) data, sub1.id_remessa, sub1.tipo, sub1.codigo_externo, sub1.nome_arquivo FROM
(
-- DECLARE @dataInicio DATE = '2017-04-17', @dataFim DATE = '2017-04-19'
SELECT data, remessa.tipo, remessa.id_remessa, codigo_externo, 
'LM' + RTRIM(LTRIM(remessa.tipo)) + 
RIGHT(REPLICATE('0',6) + 
LTRIM(RTRIM(codigo_externo)), 6) + 
CONVERT(VARCHAR, data, 112) + '.TXT' 
AS nome_arquivo FROM remessa (NOLOCK) 
LEFT JOIN movimentos_erro me (NOLOCK) ON remessa.id_remessa = me.id_remessa
WHERE data BETWEEN  @dataInicio AND @dataFim AND data_validacao IS NULL AND me.id_remessa IS NULL
) AS sub1 
JOIN 
arquivos_cai_para_cav ai (NOLOCK) 
ON sub1.nome_arquivo = ai.nome_arquivo 
WHERE ai.arquivo_cav = 0 
UNION 
-- DECLARE @dataInicio DATE = '2017-04-17', @dataFim DATE = '2017-04-19'
SELECT CAST(sub1.data AS DATE) data, sub1.id_remessa, sub1.tipo, sub1.codigo_externo, sub1.nome_arquivo FROM
(
SELECT r.data, r.tipo, r.id_remessa, r.codigo_externo, 
'TX' + RTRIM(LTRIM(r.tipo)) + 
RIGHT(REPLICATE('0',6) + LTRIM(RTRIM(r.codigo_externo)), 6) + CONVERT(VARCHAR, r.data, 112) + 
RIGHT(REPLICATE('0',4) + CONVERT(VARCHAR, ir.sequencia), 4) + CONVERT(VARCHAR, img.indice_imagem) + '.TXT' 
AS nome_arquivo FROM remessa r (NOLOCK) 
LEFT JOIN movimentos_erro me (NOLOCK) ON r.id_remessa = me.id_remessa
JOIN infracao_remessa ir (NOLOCK) 
ON r.id_remessa = ir.id_remessa 
JOIN infracao i (NOLOCK) 
ON ir.id_infracao = i.id_infracao 
JOIN veiculo_imagem vi (NOLOCK) 
ON i.id_veiculo = vi.id_veiculo 
JOIN imagem img (NOLOCK) 
ON vi.id_imagem = img.id_imagem 
WHERE r.data BETWEEN  @dataInicio AND @dataFim AND data_validacao IS NULL AND me.id_remessa IS NULL
--AND img.indice_imagem = 0
) AS sub1 
JOIN 
arquivos_cai_para_cav ai (NOLOCK) 
ON sub1.nome_arquivo = ai.nome_arquivo 
WHERE ai.arquivo_cav = 0 
UNION 
-- DECLARE @dataInicio DATE = '2017-04-17', @dataFim DATE = '2017-04-19'
SELECT CAST(sub1.data AS DATE) data, sub1.id_remessa, sub1.tipo, sub1.codigo_externo, sub1.nome_arquivo FROM
(
SELECT r.data, r.tipo, r.id_remessa, r.codigo_externo, 
'IM' + RTRIM(LTRIM(r.tipo)) + 
RIGHT(REPLICATE('0',6) + LTRIM(RTRIM(r.codigo_externo)), 6) + CONVERT(VARCHAR, r.data, 112) + 
RIGHT(REPLICATE('0',4) + CONVERT(VARCHAR, ir.sequencia), 4) + CONVERT(VARCHAR, img.indice_imagem) + '.JPG' 
AS nome_arquivo FROM remessa r (NOLOCK) 
LEFT JOIN movimentos_erro me (NOLOCK) ON r.id_remessa = me.id_remessa
JOIN infracao_remessa ir (NOLOCK) 
ON r.id_remessa = ir.id_remessa 
JOIN infracao i (NOLOCK) 
ON ir.id_infracao = i.id_infracao 
JOIN veiculo_imagem vi (NOLOCK) 
ON i.id_veiculo = vi.id_veiculo 
JOIN imagem img (NOLOCK) 
ON vi.id_imagem = img.id_imagem 
WHERE r.data BETWEEN  @dataInicio AND @dataFim AND data_validacao IS NULL AND me.id_remessa IS NULL
) AS sub1 
JOIN 
arquivos_cai_para_cav ai (NOLOCK) 
ON sub1.nome_arquivo = ai.nome_arquivo 
WHERE ai.arquivo_cav = 0 

)


