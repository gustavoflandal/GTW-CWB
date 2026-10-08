CREATE FUNCTION [dbo].[fcn_getRelatorioImagemDiaInconsistencia]
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
		sub.id_inconsistencia as [Cod. Inc],
		sub.TOTAL
	FROM (	SELECT
				i.id_local,
				i.pista,
				i.id_enquadramento,
				CAST(i.data AS DATE) AS [DIA],
				i.id_inconsistencia,
				COUNT(*) AS [TOTAL]           
			FROM
				infracao i (nolock)
			WHERE
				CAST(i.data as DATE) BETWEEN @dataInicio AND @dataFim
				AND i.id_enquadramento > 1
			GROUP BY
				i.id_local, i.pista, i.id_enquadramento,CAST(i.data AS DATE), i.id_inconsistencia

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









