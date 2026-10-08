
CREATE PROCEDURE [dbo].[spu_pmesp_atualiza_desconectado] AS

DECLARE @locais TABLE (id_local INT)
INSERT INTO @locais
SELECT id_local FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1 GROUP BY id_local

DECLARE @desconexao TABLE (id_local INT, desconexao INT)

INSERT INTO @desconexao
SELECT l.id_local, dbo.fcn_PMESP_ObterTempoOffline(l.id_local) desconexao FROM @locais l

DELETE pmesp_desconectado

INSERT INTO pmesp_desconectado
SELECT * FROM @desconexao

