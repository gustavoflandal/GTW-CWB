CREATE FUNCTION dbo.fcn_relatorioPesagemSintetico(@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-10-09'
	WITH Fluxo AS (
		--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-10-09'
        SELECT vs.id_local,
               CAST(vs.data AS DATE) AS data,
               COUNT(*) AS pesagens
        FROM   v_veiculo_pesagem vs (NOLOCK)
        WHERE  vs.data BETWEEN @dataInicio AND DATEADD(SECOND, -1, CAST(DATEADD(DAY, 1, @dataFim) AS DATETIME))
			   --AND vs.id_classe IN ('C','O','Q')
        GROUP BY
			   vs.id_local,
			   CAST(vs.data AS DATE)
    ),
	LocaisPeriodo AS (
		--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-09-30'
		SELECT lv.id_local,
			   lv.serie_equipamento,
			   RTRIM(lv.nome) AS nome,
			   d.Data AS data
		FROM   local_vigente lv
				CROSS JOIN dbo.fcn_ObterDatasPeriodo(@dataInicio, @dataFim) d
		WHERE  lv.desativado = 0
	)

    SELECT ROW_NUMBER() OVER(ORDER BY l.data) AS [#],
		   l.data AS [Data],
           SUM(vtr.pesagens) AS [Pesagens]
    FROM   LocaisPeriodo l
		   LEFT HASH JOIN Fluxo vtr
				ON  vtr.id_local = l.id_local
					AND vtr.data = l.data
	GROUP BY
		   l.data
)
