CREATE FUNCTION [dbo].[fcn_getRelatorioNumeroTriagemDigitacao]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS

RETURN
( 
	--DECLARE @dataInicio DATE = '2016-07-09', @dataFim DATE = '2016-07-09'

	SELECT relatorio.id_infracao
		  ,relatorio.triagem
		  ,relatorio.digitacao
	FROM   (
				SELECT i.id_infracao
					  ,COUNT(DISTINCT ip_triagem.id_infracao_processo) AS triagem
					  ,COUNT(DISTINCT ip_digitacao.id_infracao_processo) AS digitacao
				FROM   infracao i (NOLOCK)
					   INNER JOIN infracao_processo ip_triagem (NOLOCK)
							ON  ip_triagem.id_infracao = i.id_infracao
								AND ip_triagem.id_processo = 1
								AND ip_triagem.tempo_cliente > 0
								AND ip_triagem.status_processo = 0
					   INNER JOIN infracao_processo ip_digitacao (NOLOCK)
							ON  ip_digitacao.id_infracao = i.id_infracao
								AND ip_digitacao.id_processo = 2
								AND ip_digitacao.tempo_cliente > 0
								AND ip_digitacao.status_processo = 0
				WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
				GROUP BY
					   i.id_infracao
	) relatorio
	WHERE  relatorio.digitacao > 0

)
