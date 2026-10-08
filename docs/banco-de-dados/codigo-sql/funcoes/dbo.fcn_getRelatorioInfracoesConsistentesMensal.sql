CREATE FUNCTION [dbo].[fcn_getRelatorioInfracoesConsistentesMensal](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		--DECLARE @dataInicio DATE = '2017-07-01', @dataFim DATE = '2017-07-12'
		--SET @dataInicio = CAST( (CAST(YEAR(@dataInicio) AS VARCHAR(4)) + '-' + RIGHT('00' + CAST(MONTH(@dataInicio) AS VARCHAR(2)), 2) + '-01') AS DATE)
		--SET @dataFim = DATEADD(DAY, -1, DATEADD(MONTH, 1, @dataInicio))
		--SELECT @dataInicio, @dataFim
		
		SELECT ROW_NUMBER() OVER(ORDER BY total_geral DESC) AS [Ordem],
			   cod_pista AS [Cod. Prodam],
			   descricao AS [Local],
			   [1] AS [Dia 1],[2] AS [Dia 2],[3] AS [Dia 3],[4] AS [Dia 4],[5] AS [Dia 5],[6] AS [Dia 6],[7] AS [Dia 7],[8] AS [Dia 8],[9] AS [Dia 9],
			   [10] AS [Dia 10],[11] AS [Dia 11],[12] AS [Dia 12],[13] AS [Dia 13],[14] AS [Dia 14],[15] AS [Dia 15],[16] AS [Dia 16],[17] AS [Dia 17],
			   [18] AS [Dia 18],[19] AS [Dia 19],[20] AS [Dia 20],[21] AS [Dia 21],[22] AS [Dia 22],[23] AS [Dia 23],[24] AS [Dia 24],[25] AS [Dia 25],
			   [26] AS [Dia 26],[27] AS [Dia 27],[28] AS [Dia 28],[29] AS [Dia 29],[30] AS [Dia 30],[31] AS [Dia 31],
			   total_geral AS [Total Geral]
		FROM   (
					SELECT cod_pista,
						   descricao,
						   [1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31],
						   ([1]+[2]+[3]+[4]+[5]+[6]+[7]+[8]+[9]+[10]+[11]+[12]+[13]+[14]+[15]+[16]+[17]+
							[18]+[19]+[20]+[21]+[22]+[23]+[24]+[25]+[26]+[27]+[28]+[29]+[30]+[31]) AS total_geral
					FROM   (
								SELECT cod_pista,
									   descricao,
									   SUM([1]) AS [1],SUM([2]) AS [2],SUM([3]) AS [3],SUM([4]) AS [4],SUM([5]) AS [5],SUM([6]) AS [6],SUM([7]) AS [7],SUM([8]) AS [8],SUM([9]) AS [9],
									   SUM([10]) AS [10],SUM([11]) AS [11],SUM([12]) AS [12],SUM([13]) AS [13],SUM([14]) AS [14],SUM([15]) AS [15],SUM([16]) AS [16],SUM([17]) AS [17],
									   SUM([18]) AS [18],SUM([19]) AS [19],SUM([20]) AS [20],SUM([21]) AS [21],SUM([22]) AS [22],SUM([23]) AS [23],SUM([24]) AS [24],SUM([25]) AS [25],
									   SUM([26]) AS [26],SUM([27]) AS [27],SUM([28]) AS [28],SUM([29]) AS [29],SUM([30]) AS [30],SUM([31]) AS [31]
								FROM   (
											SELECT DAY(i.data) AS dia,
												   0 AS cod_pista,
												   'TOTAL DIA' AS descricao,
												   i.id_infracao
											FROM   configuracao_equipamento_medicao cem (NOLOCK)
												   INNER JOIN infracao i (NOLOCK)
														ON  i.id_local = cem.id_local
															AND i.pista = cem.id_pista
												   INNER JOIN infracao_remessa ir (NOLOCK)
														ON  ir.id_infracao = i.id_infracao
												   INNER JOIN remessa r (NOLOCK)
														ON  r.id_remessa = ir.id_remessa
												   LEFT JOIN movimento_importacao mi (NOLOCK)
														ON  mi.id_movimento_arquivo = r.id_movimento_arquivo
															AND mi.sequencia = ir.sequencia
											WHERE  cem.atualizar = 1
												   AND i.id_enquadramento > 1
												   AND CAST(i.data AS DATE) BETWEEN CAST(@dataInicio AS DATE) AND CAST(@dataFim AS DATE)
												   --AND i.id_inconsistencia = 0
												   AND (
															(mi.id_inconsistencia = 0 AND mi.id_movimento_arquivo IS NOT NULL)
															OR
															(i.id_inconsistencia = 0 AND mi.id_movimento_arquivo IS NULL)
													   )
									   ) AS dados
								PIVOT  (
											COUNT(dados.id_infracao)
											FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
									   ) AS contagem_dados
								GROUP BY
									   cod_pista,
									   descricao

								UNION

								SELECT *
								FROM   (
											SELECT DAY(i.data) AS dia,
												   cem.cod_pista,
												   LTRIM(RTRIM(cem.descricao)) AS descricao,
												   i.id_infracao
											FROM   configuracao_equipamento_medicao cem (NOLOCK)
												   INNER JOIN infracao i (NOLOCK)
														ON  i.id_local = cem.id_local
															AND i.pista = cem.id_pista
												   INNER JOIN infracao_remessa ir (NOLOCK)
														ON  ir.id_infracao = i.id_infracao
												   INNER JOIN remessa r (NOLOCK)
														ON  r.id_remessa = ir.id_remessa
												   LEFT JOIN movimento_importacao mi (NOLOCK)
														ON  mi.id_movimento_arquivo = r.id_movimento_arquivo
															AND mi.sequencia = ir.sequencia
											WHERE  cem.atualizar = 1
												   AND i.id_enquadramento > 1
												   AND CAST(i.data AS DATE) BETWEEN CAST(@dataInicio AS DATE) AND CAST(@dataFim AS DATE)
												   --AND i.id_inconsistencia = 0
												   AND (
															(mi.id_inconsistencia = 0 AND mi.id_movimento_arquivo IS NOT NULL)
															OR
															(i.id_inconsistencia = 0 AND mi.id_movimento_arquivo IS NULL)
													   )
									   ) AS dados
								PIVOT  (
											COUNT(dados.id_infracao)
											FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
									   ) AS contagem_dados

					) AS dados_consolidados
		) AS resultado
	)
