CREATE FUNCTION [dbo].[fcn_getRelatorioVolumeTrafego]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS

RETURN
(
	SELECT TOP 100 PERCENT
		lv.serie_equipamento AS SN,
		cep.nome_pista AS [Descr Local],
		CAST(vp.data AS DATE) AS [Data],
		COUNT(*) AS [Tráfego]
	FROM veiculo_pesquisa vp (nolock)
		INNER JOIN local_vigente lv (nolock) 
			ON vp.id_local = lv.id_local
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
	WHERE	CAST(vp.data AS DATE) >= @dataInicio 
		AND CAST(vp.data AS DATE) <= @dataFim
	GROUP BY 
		lv.serie_equipamento,
		cep.nome_pista,
		CAST(vp.data AS DATE)
	ORDER BY
		lv.serie_equipamento
)






