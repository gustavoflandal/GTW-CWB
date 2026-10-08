CREATE FUNCTION [dbo].[fcn_getRelatorioDivergenciaAuditMes](@dataInicio DATE, @dataFim DATE) RETURNS TABLE AS RETURN 
( 
	SELECT
     	CONVERT(VARCHAR(7), inf.data, 126) AS [Ano-Mês,
     	inf.auditor,
     	COUNT(*) AS [Total],
     	COUNT((CASE WHEN ipc_lib.id_inconsistencia > 0 AND 
		ipc_val.id_inconsistencia = 0 THEN 1 END)) AS [Divergencia consistente],
     	COUNT((CASE WHEN ipc_lib.id_inconsistencia = 0 AND 
		ipc_val.id_inconsistencia > 0 THEN 1 END)) AS [Divergencia inconsistente],
     	COUNT((CASE WHEN ipc_lib.id_inconsistencia > 0 AND 
		ipc_val.id_inconsistencia > 0 AND ipc_val.id_inconsistencia <> 
		ipc_lib.id_inconsistencia THEN 1 END)) AS [Inconsistente diferentes]
	FROM (	SELECT
				i.id_infracao,
				i.data,
				i.placa,
				auditor = (	SELECT su.nome
								FROM infracao_processo ipp (nolock)
									INNER JOIN sis_usuario su (nolock)
										ON su.id_usuario = ipp.id_usuario
								WHERE ipp.id_infracao_processo = (	SELECT max(id_infracao_processo)
																		FROM infracao_processo ip (nolock)
																		WHERE	ip.id_infracao=i.id_infracao 
																			AND ip.id_processo = 3 )
							)
			FROM infracao i (nolock)
				INNER JOIN local_vigente lv (nolock) 
					ON lv.id_local = i.id_local
			WHERE
				i.id_enquadramento <> 1 AND
				lv.data_inicio <= i.data AND
				CAST(i.data AS DATE) >= @dataInicio AND CAST(i.data AS DATE) < @dataFim
		 ) AS Inf
		INNER JOIN infracao_processo_concluido ipc_val (nolock)
			ON	ipc_val.id_infracao = inf.id_infracao 
			AND ipc_val.id_processo = 3
		INNER JOIN infracao_processo_concluido ipc_lib (nolock)
			ON ipc_lib.id_infracao = inf.id_infracao 
			AND ipc_lib.id_processo = 11
	GROUP BY
     	CONVERT(VARCHAR(7), inf.data, 126), inf.auditor
)
