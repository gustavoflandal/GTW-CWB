CREATE PROCEDURE [dbo].[spu_pmesp_atualiza_atraso] AS

DECLARE @locais TABLE (id_local INT)
INSERT INTO @locais
SELECT id_local FROM v_locais_pmesp WHERE ENVIAR_VEICULOS_PM = 1 GROUP BY id_local

DECLARE @atraso TABLE 
(
id_local INT NOT NULL,
tempo_medio BIGINT NOT NULL,
tempo_maximo BIGINT NOT NULL
)

INSERT INTO @atraso
SELECT sub1.id_local, AVG(COALESCE(CAST(DATEDIFF(MILLISECOND, sub1.data_movimento, sub1.data_transmitido) AS BIGINT), 0)) atraso,
MAX(COALESCE(CAST(DATEDIFF(MILLISECOND, sub1.data_movimento, sub1.data_transmitido) AS BIGINT), 0)) maximo FROM 
(SELECT pec.id_local, pm.data_movimento, pm.data_transmitido FROM pmesp_movimento pm (NOLOCK)
JOIN pmesp_evento_conexao pec (NOLOCK) ON pm.id_evento_conexao = pec.id_evento_conexao
WHERE pm.data_movimento > GETDATE()- 1 --AND GETDATE()
) AS sub1
WHERE sub1.id_local IS NOT NULL
GROUP BY sub1.id_local

DELETE pmesp_atraso

INSERT INTO pmesp_atraso
SELECT * FROM @atraso

