
CREATE FUNCTION [dbo].[fcn_getRelatorioContestacao](@dataInicio DATE, @dataFim DATE)    
RETURNS TABLE    
AS    
RETURN    
(    

--DECLARE @dataInicio DATE = '2018-01-01', @dataFim DATE = '2018-01-31'

SELECT sub1.[MÊS], sub1.[ANO], 
sub1.[ANALISADOS] + sub1.[QTD. RESTANTE] AS [QTD. REGISTROS], 
sub1.[ANALISADOS], sub1.[QTD. RESTANTE], sub1.[APROVADOS], sub1.[REPROVADOS],    
CASE WHEN sub1.pendente = 0 AND sub1.[QTD. RESTANTE] = 0 THEN 'CONCLUÍDO' ELSE 'EM ABERTO' END AS [STATUS],    
CASE WHEN sub1.pendente = 0 AND sub1.[QTD. RESTANTE] = 0 AND ip.data IS NOT NULL THEN CONVERT(VARCHAR,ip.data,103) ELSE 'N/D' END AS DT_FECHAMENTO,    
CASE WHEN sub1.pendente = 0 AND sub1.[QTD. RESTANTE] = 0 AND su.nome IS NOT NULL THEN su.nome ELSE 'N/D' END AS [FECHADO POR]    
FROM (   
SELECT MONTH(ic.data) AS [MÊS],YEAR(ic.data) AS [ANO],COUNT(*) AS [QTD. REGISTROS],    
SUM(CASE WHEN ic.id_processo_contestacao IN (5,4,3) AND sub2.id_infracao IS NOT NULL THEN 1 ELSE 0 END) AS [ANALISADOS],    
SUM(CASE WHEN ic.id_processo_contestacao = 2 THEN 1 ELSE 0 END) AS [QTD. RESTANTE],    
SUM(CASE WHEN ic.id_processo_contestacao = 4 AND sub2.id_infracao IS NOT NULL THEN 1 ELSE 0 END) AS [APROVADOS],    
SUM(CASE WHEN ic.id_processo_contestacao = 3 AND sub2.id_infracao IS NOT NULL THEN 1 ELSE 0 END) AS [REPROVADOS],    
SUM(CASE WHEN ic.id_processo_contestacao = 1 THEN 1 ELSE 0 END) AS pendente,    
MAX(sub2.id_infracao_processo) AS id_infracao_processo    
FROM infracao_contestacao ic (NOLOCK)  
--JOIN infracao i (NOLOCK) ON ic.id_infracao = i.id_infracao   
JOIN (
SELECT id_infracao, MAX(id_infracao_processo) AS id_infracao_processo 
FROM infracao_processo_contestacao ipc_cai (NOLOCK) 
WHERE ipc_cai.id_processo_contestacao = 1 AND ipc_cai.decisao = 2
GROUP BY ipc_cai.id_infracao
) AS sub1
ON ic.id_infracao = sub1.id_infracao   
LEFT JOIN (
SELECT id_infracao, MAX(id_infracao_processo) AS id_infracao_processo FROM infracao_processo_contestacao ipc (NOLOCK)
WHERE ipc.id_processo_contestacao = 2
GROUP BY ipc.id_infracao
) AS sub2
ON ic.id_infracao = sub2.id_infracao
GROUP BY MONTH(ic.data),YEAR(ic.data)  
) AS sub1    
LEFT JOIN infracao_processo ip (NOLOCK) ON sub1.id_infracao_processo = ip.id_infracao_processo    
LEFT JOIN sis_usuario su (NOLOCK) ON ip.id_usuario = su.id_usuario    

--ORDER BY 2,1

) 

