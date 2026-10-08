
CREATE VIEW [dbo].[Relatorio_DataInfracao_DataProcesso_FinalPlaca]
AS
SELECT
	SUBSTRING( PLACA , 6 , 1) As final_placa,
	CAST( i.Data AS DATE ) AS data_infracao,
	CAST( ip.Data AS DATE ) AS data_processamento,
	COUNT( * ) AS Total
FROM infracao i (nolock)
	LEFT JOIN infracao_processo ip  (nolock)
	ON	ip.id_infracao = i.id_infracao
		AND i.id_processo_concluido = ip.id_processo --AND i.id_processo in ( 7,8,9,10,13 )
GROUP BY 
	SUBSTRING( PLACA, 6, 1),
	CAST( i.Data AS DATE ),
	CAST( ip.Data AS DATE )


