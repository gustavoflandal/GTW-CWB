CREATE FUNCTION [dbo].[fcn_getRelatorioPMESP_Perda](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(

SELECT sub1.*, CAST(sub1.placa_enviada AS FLOAT) /  CAST(sub1.placa_lida AS FLOAT) AS Porcentagem FROM
(
SELECT sub1.id_local, CAST(sub1.data AS DATE) dia, DATEPART(HOUR, sub1.data) hora,
COUNT(sub1.placa) AS placa_lida, COUNT(pm.placa) AS placa_enviada FROM
(
SELECT id_local, pista, placa, data FROM veiculo_estatistica ve (NOLOCK)
WHERE ve.id_local IN (SELECT id_local FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1 GROUP BY id_local) AND CAST(ve.data AS DATE) >= @dataInicio AND CAST(ve.data AS DATE) < @dataFim
UNION
SELECT id_local, pista, placa, data FROM veiculo v (NOLOCK)
WHERE v.id_local IN (SELECT id_local FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1 GROUP BY id_local) AND CAST(v.data AS DATE) >= @dataInicio AND CAST(v.data AS DATE) < @dataFim
) AS sub1
JOIN (SELECT id_local, id_pista, cod_pista_prodam FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1) AS lp 
ON sub1.id_local = lp.id_local AND sub1.pista = lp.id_pista
LEFT JOIN pmesp_movimento pm (NOLOCK) ON lp.cod_pista_prodam = pm.id_equipamento AND sub1.data = pm.data_movimento AND sub1.placa = pm.placa
GROUP BY sub1.id_local, CAST(sub1.data AS DATE), DATEPART(HOUR, sub1.data)
) AS sub1

)
