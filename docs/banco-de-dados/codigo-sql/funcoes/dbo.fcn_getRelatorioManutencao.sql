CREATE FUNCTION [dbo].[fcn_getRelatorioManutencao](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
SELECT sub1.id_local, sub1.serie_equipamento, sub1.usuario, sub1.dia, SUM(sub1.abertura) abertura, SUM(sub1.fechamento) fechamento FROM 
(
SELECT lv.id_local, lv.serie_equipamento, ev.usuario, CAST(ev.data_hora AS DATE) dia, 
CASE WHEN ev.id_evento = 15 THEN 1 ELSE 0 END AS abertura, 
CASE WHEN ev.id_evento = 16 THEN 1 ELSE 0 END AS fechamento 
FROM eventos_csx ev (NOLOCK) 
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ev.id_proprietario = evp.id_proprietario
JOIN local_vigente lv (NOLOCK) ON evp.proprietario = CAST(lv.serie_equipamento AS VARCHAR) 
WHERE CAST(ev.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim
AND ev.id_evento IN (15,16) 
) AS sub1
GROUP BY sub1.id_local, sub1.serie_equipamento, sub1.usuario, sub1.dia
)
