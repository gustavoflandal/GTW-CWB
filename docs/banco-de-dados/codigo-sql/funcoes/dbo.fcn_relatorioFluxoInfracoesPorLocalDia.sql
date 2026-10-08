CREATE FUNCTION dbo.fcn_relatorioFluxoInfracoesPorLocalDia(@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(
	--DECLARE @dataInicio DATE = '2025-06-01', @dataFim DATE = '2025-10-06';
	WITH Fluxo AS (
        SELECT vtr.id_local,
               CAST(vtr.data AS DATE) AS data,
               COUNT(*) AS fluxo
        FROM   veiculo_pesquisa vtr (NOLOCK)
        WHERE  CAST(vtr.data AS DATE) BETWEEN @dataInicio AND @dataFim
        GROUP BY
			   vtr.id_local,
			   CAST(vtr.data AS DATE)
    ),
    Infracoes AS (
        SELECT i.id_local,
			   CAST(i.data AS DATE) AS data,
               COUNT(i.id_infracao) AS imagens_registradas,
               SUM(CASE WHEN inc.id_inconsistencia > 0 THEN 1 ELSE 0 END) AS imagens_rejeitadas,
               SUM(CASE WHEN inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS imagens_rejeitadas_tecnicas,
               SUM(CASE WHEN i.id_inconsistencia > 0 AND inc.razao_tecnica < 2 THEN 1 ELSE 0 END) AS imagens_rejeitadas_nao_tecnicas,
               SUM(CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END) AS imagens_validas
               --CAST((1 - (CAST(SUM(CASE WHEN inc.razao_tecnica = 2 THEN 1 ELSE 0 END) AS NUMERIC(15,3)) / COUNT(i.id_infracao))) * 100 AS INT) AS aproveitamento_liquido
        FROM   infracao i WITH (NOLOCK)
			   JOIN inconsistencia inc WITH (NOLOCK)
					ON  inc.id_inconsistencia = i.id_inconsistencia
        WHERE  i.id_enquadramento > 1
			   AND CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim
        GROUP BY
			   i.id_local,
			   CAST(i.data AS DATE)
    )

    SELECT ROW_NUMBER() OVER(ORDER BY l.data, i.id_local) AS [#],
		   l.id_local AS [Id. Local],
           l.serie_equipamento AS [Nº Série],
		   l.nome AS [Endereço],
		   l.data AS [Data],
           vtr.fluxo AS [Fluxo],
           i.imagens_registradas AS [Imagens Registradas],
           i.imagens_rejeitadas AS [Imagens Rejeitadas],
           i.imagens_rejeitadas_tecnicas AS [Imagens Registradas - Motivo Técnico],
           i.imagens_rejeitadas_nao_tecnicas AS [Imagens Registradas - Motivo Não Técnico],
           i.imagens_validas AS [Imagens Válidas],
           --CAST(i.aproveitamento_liquido AS VARCHAR(5)) + '%' AS aproveitamento_liquido,
		   CAST(i.imagens_registradas AS FLOAT) / CAST(vtr.fluxo AS FLOAT) AS [% Autuados / Fluxo],
		   CAST(i.imagens_rejeitadas AS FLOAT) / CAST(vtr.fluxo AS FLOAT) AS [% Rejeitados / Fluxo]
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
		   LEFT JOIN Infracoes i
				ON  i.id_local = l.id_local
					AND i.data = l.data
)
