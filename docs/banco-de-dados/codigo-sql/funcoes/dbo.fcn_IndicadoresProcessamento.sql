CREATE FUNCTION [dbo].[fcn_IndicadoresProcessamento]
  ( @data_ini datetime,
    @data_fim datetime,
    @max_dia_atraso int )
RETURNS TABLE
AS

RETURN
(
	SELECT 
		CAST(dt.data AS date) AS data, 
		info_atraso.imagem_dia_atraso, 
		info_erro.imagem_dia_erro
	FROM Date_Table(@data_ini,@data_fim) dt
		LEFT JOIN (	SELECT 
						CAST(sa.data_geracao AS DATE) as data,
						SUM(CASE WHEN DATEDIFF(day,dateadd(day,@max_dia_atraso,sa.data_imagens),sa.data_geracao) > 0 THEN (DATEDIFF(day,dateadd(day,@max_dia_atraso,sa.data_imagens),sa.data_geracao)*sa.total_imagens) ELSE 0 END) AS imagem_dia_atraso
					FROM 
						solicitacao_auditoria sa
					WHERE
						sa.data_geracao BETWEEN @data_ini AND @data_fim
					GROUP BY 
						CAST(sa.data_geracao AS DATE)
				) AS info_atraso 
			ON info_atraso.data = dt.data
		LEFT JOIN (	SELECT 
						CAST(ipc_val.data_conclusao AS DATE) AS data,
						COUNT(i.id_infracao) AS imagem_dia_erro
					FROM infracao i (nolock)
						INNER JOIN infracao_processo_concluido ipc_lib (nolock) 
							ON	ipc_lib.id_infracao = i.id_infracao 
							AND ipc_lib.id_processo = 11
						INNER JOIN infracao_processo_concluido ipc_val (nolock) 
							ON	ipc_val.id_infracao = i.id_infracao 
							AND ipc_val.id_processo = 3
					WHERE	ipc_val.data_conclusao BETWEEN @data_ini AND @data_fim 
						AND ipc_lib.id_inconsistencia <> ipc_val.id_inconsistencia
					GROUP BY 
						CAST(ipc_val.data_conclusao AS DATE)
				) AS info_erro 
			ON info_erro.data = dt.data
)





