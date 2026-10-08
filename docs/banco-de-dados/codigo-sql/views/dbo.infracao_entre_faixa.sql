

CREATE VIEW [dbo].[infracao_entre_faixa]
AS
SELECT r.tipo, r.codigo_externo, ir.sequencia, v.entre_faixa FROM infracao i (NOLOCK) 
JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo
JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao
JOIN remessa r (NOLOCK) ON ir.id_remessa = r.id_remessa
WHERE v.entre_faixa IS NOT NULL AND r.data_confirmacao IS NULL
--AND r.data >= (
--SELECT CAST(MAX(data_arquivo) AS DATE) data_arquivo FROM arquivos_cai_para_cav (NOLOCK) 
--WHERE nome_arquivo LIKE 'infracao_entre_faixa-%'
--AND data_arquivo IS NOT NULL 
--AND arquivo_cav = 1
--)
