
CREATE FUNCTION [dbo].[fcn_getRelatorioIsentos]
(	
)
RETURNS TABLE 
AS
RETURN 
(
	
SELECT 
sub1.id_enquadramento,
sub1.id_arquivo,
cai.data_hora,
cai.nome_arquivo,
sub2.nome_arquivo nome_arquivo_cav,
cai.data_importacao data_cai,
sub2.data_cav,
CASE WHEN cai.crc = sub2.crc_cav THEN 1 ELSE 0 END AS verif_arquivo,
cai.crc crc_cai,
sub2.crc_cav,
GETDATE() data_atualizacao_cai,
DATEADD(HOUR, 3, sub3.data_hora) data_arquivo_cav,
sub3.data_importacao data_atualizacao_cav 
FROM
(
SELECT id_enquadramento, MAX(id_arquivo) id_arquivo 
FROM cad_isento_arquivo (NOLOCK)
GROUP BY id_enquadramento
) AS sub1
JOIN 
cad_arquivos_importados cai (NOLOCK) 
ON sub1.id_arquivo = cai.id
JOIN 
(
SELECT
CASE WHEN sub1.nome_arquivo LIKE '%CAD-VEIC-ISEN-ROD.TXT%' THEN 57462
ELSE CASE WHEN sub1.nome_arquivo LIKE '%PFRETADO_VALIDO.TXT%' THEN 57461 
ELSE CASE WHEN sub1.nome_arquivo LIKE '%PCAMIN_VALIDO.TXT%' THEN 57463
ELSE 0 END END END AS id_enquadramento,
sub1.* 
FROM 
(
SELECT * FROM arquivos_cai_para_cav (NOLOCK) 
WHERE nome_arquivo 
IN ('PCAMIN_VALIDO.TXT', 
'PFRETADO_VALIDO.TXT', 
'CAD-VEIC-ISEN-ROD.TXT') 
) AS sub1) AS sub2
ON sub1.id_enquadramento = sub2.id_enquadramento
JOIN
(SELECT TOP(1) * FROM cad_arquivos_importados (NOLOCK) WHERE nome_arquivo LIKE 'arquivos_importados%' ORDER BY id DESC)
AS sub3 ON 1 = 1


)
