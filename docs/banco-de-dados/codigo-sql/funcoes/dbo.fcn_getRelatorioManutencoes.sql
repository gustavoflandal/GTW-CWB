CREATE FUNCTION [dbo].[fcn_getRelatorioManutencoes](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(

SELECT 4 AS [LOTE], lv.nome AS [ENDEREÇO], sub1.sentido AS [SENTIDO], sub2.referencia AS [REFERÊNCIA],
sub3.descricao AS [TIPO EQUIPAMENTO], CONVERT (VARCHAR(50), lv.posicao_lat,128) + ' ' + CONVERT (VARCHAR(50), lv.posicao_lon,128) AS [LATITUDE LONGITUDE],
'' AS [LIGADO], '' AS [DATA DESLIGAMENTO], m.descricao AS [MOTIVO]
FROM manutencao m (NOLOCK)
JOIN local_vigente lv (NOLOCK) ON m.id_local = lv.id_local
JOIN (SELECT sub1.id_local, MAX(SUBSTRING(sub1.nome_pista, sub1.ini + 1, sub1.fim - sub1.ini - 1)) AS sentido FROM
(SELECT lv.id_local, cep.nome_pista, CHARINDEX('(', cep.nome_pista) AS ini, 
CHARINDEX(')', cep.nome_pista) AS fim FROM local_vigente lv (NOLOCK) 
JOIN configuracao_equipamento_pista cep (NOLOCK) ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento 
WHERE lv.id_local > 2000) AS sub1 GROUP BY sub1.id_local ) AS sub1 ON lv.id_local = sub1.id_local 
JOIN (SELECT sub1.id_local,MAX(sub1.ini) AS referencia FROM
(SELECT lv.id_local ,CASE WHEN (LEN(cep.nome_pista) - CHARINDEX(')', cep.nome_pista)) = 0 THEN '' ELSE 
 LTRIM(RIGHT(RTRIM(cep.nome_pista), LEN(cep.nome_pista) - CHARINDEX(')', cep.nome_pista) - 1)) END AS ini 
FROM local_vigente lv (NOLOCK) JOIN configuracao_equipamento_pista cep (NOLOCK) 
ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento 
WHERE lv.id_local > 2000) AS sub1 GROUP BY sub1.id_local
) AS sub2 ON lv.id_local = sub2.id_local
JOIN (SELECT lv.id_local, p.descricao FROM local_vigente lv (NOLOCK)
JOIN configuracao_equipamento ce (NOLOCK) ON lv.id_configuracao_equipamento = ce.id_configuracao_equipamento 
JOIN produto p (NOLOCK) ON ce.id_produto = p.id_produto) AS sub3 ON lv.id_local = sub3.id_local
WHERE m.encaminhar = 1

)

