
CREATE PROCEDURE [dbo].[spu_pmesp_atualiza_perda] AS

DECLARE @locais TABLE (id_local INT)
INSERT INTO @locais
SELECT id_local FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1 GROUP BY id_local

DECLARE @placas TABLE (data DATETIME, id_local INT, pista INT, placa CHAR(7))

INSERT INTO @placas
SELECT data, id_local, pista, placa FROM veiculo_estatistica (NOLOCK)
WHERE id_local IN (SELECT id_local FROM @locais) AND data BETWEEN DATEADD(DAY,-1,GETDATE()) AND GETDATE() AND placa IS NOT NULL

INSERT INTO @placas
SELECT data, id_local, pista, placa FROM veiculo (NOLOCK)
WHERE id_local IN (SELECT id_local FROM @locais) AND data BETWEEN DATEADD(DAY,-1,GETDATE()) AND GETDATE() AND placa IS NOT NULL

DELETE pmesp_perda

INSERT INTO pmesp_perda
SELECT lv.id_local, COUNT(pl.placa) placa_lida, 
--0 AS placa_enviada
COUNT(pm.placa) placa_enviada 
FROM @placas pl 
JOIN local_vigente lv (NOLOCK) ON pl.id_local = lv.id_local
JOIN configuracao_equipamento_pista cep (NOLOCK) 
	ON lv.id_configuracao_equipamento = cep.id_configuracao_equipamento
	AND cep.id_pista = pl.pista
LEFT JOIN pmesp_movimento pm (NOLOCK)
	ON cep.cod_pista_prodam = pm.id_equipamento
	AND pl.data = pm.data_movimento
	AND pl.placa = pm.placa
GROUP BY lv.id_local

