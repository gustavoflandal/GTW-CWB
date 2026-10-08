CREATE FUNCTION [dbo].[fcn_getRelatorioImagensAuditadas](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @dataInicio DATE = '2019-10-30', @dataFim DATE = '2019-10-30'
	SELECT
		CAST(T2.DIA AS DATETIME) AS DIA,
		u.nome AS AUDITOR,
		SUM (CASE WHEN T2.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS CONSISTENTES,
		SUM (CASE WHEN T2.id_inconsistencia > 0 THEN 1 ELSE 0 END) AS INCONSISTENTES,
		COUNT(*) AS TOTAL
	FROM (	SELECT
				CAST(T1.data AS DATE) AS DIA,
				T1.id_usuario,
				T1.id_inconsistencia
			FROM (	-- T2query que pega o último processamento de validação de uma infração
					SELECT
						ip.data,
						ip.id_usuario,
						ip.id_inconsistencia,
						Rank() OVER (PARTITION BY ip.id_infracao, ip.id_processo ORDER BY ip.id_infracao_processo DESC) AS Rank
					FROM
						infracao_processo ip (nolock)
					WHERE	ip.status_processo = 0 -- Processamentos 'válidos'
						AND ip.id_processo = 3 -- No processo 'Validação'
						AND ip.tempo > 0 -- Que seja um processamento manual (nada de processamento automático)
						AND CAST(ip.data AS DATE) BETWEEN @dataInicio AND @dataFim
				) AS T1
			WHERE
				T1.Rank = 1 -- Pega o 'primeiro colocado' (ou seja: o último processamento desta infração)
		) AS T2
		INNER JOIN sis_usuario u (nolock)
			ON T2.id_usuario = u.id_usuario
		WHERE
			u.cod_agente IS NOT NULL -- Somente agentes
		GROUP BY
			T2.dia,
			u.nome
)
