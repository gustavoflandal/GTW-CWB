
CREATE FUNCTION dbo.fcn_relatorioVeiculoCargaAnalitico(@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-10-09'
	WITH Fluxo AS (
		--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-10-09'
        SELECT vs.id_local,
               vs.data,
               SUM(vs.trafego) AS fluxo
        FROM   veiculo_sumarizado vs (NOLOCK)
        WHERE  vs.data BETWEEN @dataInicio AND @dataFim
			   AND vs.id_classe IN ('C','O','Q')
        GROUP BY
			   vs.id_local,
			   vs.data
    )

    SELECT ROW_NUMBER() OVER(ORDER BY l.data, l.id_local) AS [#],
		   l.id_local AS [Id. Local],
           l.serie_equipamento AS [Nº Série],
		   l.nome AS [Endereço],
		   l.data AS [Data],
           vtr.fluxo AS [Fluxo]
    FROM   (
				--DECLARE @dataInicio DATE = '2025-09-01', @dataFim DATE = '2025-09-30'
				SELECT lv.id_local,
					   lv.serie_equipamento,
					   RTRIM(lv.nome) AS nome,
					   d.Data AS data
				FROM   local_vigente lv
					   CROSS JOIN dbo.fcn_ObterDatasPeriodo(@dataInicio, @dataFim) d
				WHERE  lv.desativado = 0
					   --AND lv.id_local = 100
				
		   ) l
		   LEFT JOIN Fluxo vtr
				ON  vtr.id_local = l.id_local
					AND vtr.data = l.data
)
