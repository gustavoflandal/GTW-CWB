CREATE FUNCTION [dbo].[fcn_getRelatorioImagemDia]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS

RETURN
(
	SELECT
		lv.serie_equipamento [N/S],
		cep.cod_pista_alternativo AS [FX],
		cep.nome_pista [NOME],
		sub.id_enquadramento AS CTB,
		sub.DIA AS [DIA],
		sub.[HORA],
		sub.TOTAL
	FROM (	SELECT
				i.id_local,
				i.pista,
				i.id_enquadramento,
				CAST(i.data AS DATE) AS [DIA],
				DATEPART(hh,i.data) AS [HORA],
				COUNT(*) AS [TOTAL]           
			FROM
				infracao i (nolock)
			WHERE
				CAST(i.data as DATE) BETWEEN @dataInicio AND @dataFim
			GROUP BY
				i.id_local, 
				i.pista, 
				i.id_enquadramento,
				CAST(i.data AS DATE), 
				DATEPART(hh,i.data)
		) AS sub
		INNER JOIN local_vigente lv (nolock)
			ON lv.id_local = sub.id_local
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
			AND cep.id_pista = sub.pista   
	WHERE
		CAST(lv.data_inicio AS DATE) < @dataInicio
		AND (lv.data_fim IS NULL OR CAST(lv.data_fim AS DATE) >= @dataFim)
)








