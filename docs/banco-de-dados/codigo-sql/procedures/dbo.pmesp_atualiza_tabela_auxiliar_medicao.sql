
CREATE PROCEDURE [dbo].[pmesp_atualiza_tabela_auxiliar_medicao] AS

DECLARE @data_hora_ref DATETIME
DECLARE @data_ini DATETIME

SET @data_hora_ref = (SELECT MAX(data_movimento) FROM pmesp_movimento (NOLOCK))
SET @data_ini = CAST(@data_hora_ref - 1 AS DATE)

DELETE pmesp_movimentos_atraso_dia WHERE dia BETWEEN CAST(@data_ini AS DATE) AND CAST(@data_hora_ref AS DATE)

--TRUNCATE TABLE pmesp_movimentos_atraso_dia

INSERT INTO pmesp_movimentos_atraso_dia
SELECT pm.id_equipamento,
	   CAST(pm.data_movimento AS DATE) AS dia,
	   --COUNT(pm.id_movimento) AS movimentos,
	   SUM(CASE WHEN DATEDIFF(MILLISECOND, pm.data_movimento, pm.data_transmitido) < 7000
				THEN 1
				ELSE 0
		   END) AS movimentos,
	   SUM(CASE WHEN DATEDIFF(MILLISECOND, pm.data_movimento, pm.data_transmitido) < 5000
				THEN 1
				ELSE 0
		   END) AS atraso_ok
	     ,NULL  AS atraso_ok1
	   --,SUM(CASE WHEN DATEDIFF(SECOND, pm.data_movimento, pm.data_transmitido) <= 4
				--THEN 1
				--ELSE 0
		  -- END) AS atraso_ok1
FROM   pmesp_movimento pm (NOLOCK)
WHERE  pm.data_movimento BETWEEN @data_ini AND @data_hora_ref
--WHERE  pm.data_movimento >= '2017-12-01 00:00:00'
GROUP BY
	   pm.id_equipamento,
	   CAST(pm.data_movimento AS DATE)

DELETE pmesp_movimentos_atraso_dia WHERE movimentos = 0

