CREATE FUNCTION [dbo].[fcn_getRelatorioMedicaoPMESPEnvioPlacas](@Data_Ini DATETIME, @Data_Fim DATETIME)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2016-09-01 00:00:00.000', @Data_Fim DATETIME = '2016-09-01 23:59:59.000'
	SELECT ROW_NUMBER() OVER (ORDER BY sub1.cod_pista DESC, sub1.cod_pista_prodam) AS item,
		   sub1.serie_equipamento,
		   sub1.cod_pista,
		   sub1.cod_pista_prodam,
		   sub1.cod_pista_str AS faixa,
		   sub1.nome_pista,
		   sub1.[1],sub1.[2],sub1.[3],sub1.[4],sub1.[5],sub1.[6],sub1.[7],sub1.[8],sub1.[9],sub1.[10],
		   sub1.[11],sub1.[12],sub1.[13],sub1.[14],sub1.[15],sub1.[16],sub1.[17],sub1.[18],sub1.[19],sub1.[20],
		   sub1.[21],sub1.[22],sub1.[23],sub1.[24],sub1.[25],sub1.[26],sub1.[27],sub1.[28],sub1.[29],sub1.[30],sub1.[31],
		   sub3.dias_ok,
		   --sub2.atraso_ok,
		   --sub2.atraso_nok,
		   0 AS atraso_ok,
		   0 AS atraso_nok,
		   sub3.movimentos,
		   --sub2.porc_atraso_ok,
		   --sub2.porc_atraso_nok
		   0 AS porc_atraso_ok,
		   0 AS porc_atraso_nok
	FROM   (
				SELECT pvt.serie_equipamento,
					   pvt.cod_pista,
					   pvt.cod_pista_prodam,
					   pvt.cod_pista_str,
					   pvt.nome_pista,
					   COALESCE([1], 0) AS [1],
					   COALESCE([2], 0) AS [2],
					   COALESCE([3], 0) AS [3],
					   COALESCE([4], 0) AS [4],
					   COALESCE([5], 0) AS [5],
					   COALESCE([6], 0) AS [6],
					   COALESCE([7], 0) AS [7],
					   COALESCE([8], 0) AS [8],
					   COALESCE([9], 0) AS [9],
					   COALESCE([10], 0) AS [10],
					   COALESCE([11], 0) AS [11],
					   COALESCE([12], 0) AS [12],
					   COALESCE([13], 0) AS [13],
					   COALESCE([14], 0) AS [14],
					   COALESCE([15], 0) AS [15],
					   COALESCE([16], 0) AS [16],
					   COALESCE([17], 0) AS [17],
					   COALESCE([18], 0) AS [18],
					   COALESCE([19], 0) AS [19],
					   COALESCE([20], 0) AS [20],
					   COALESCE([21], 0) AS [21],
					   COALESCE([22], 0) AS [22],
					   COALESCE([23], 0) AS [23],
					   COALESCE([24], 0) AS [24],
					   COALESCE([25], 0) AS [25],
					   COALESCE([26], 0) AS [26],
					   COALESCE([27], 0) AS [27],
					   COALESCE([28], 0) AS [28],
					   COALESCE([29], 0) AS [29],
					   COALESCE([30], 0) AS [30],
					   COALESCE([31], 0) AS [31]
				FROM   (
							SELECT lv.serie_equipamento,
								   lv.cod_pista,
								   lv.cod_pista_prodam,
								   lv.cod_pista_str,
								   lv.nome_pista,
								   sub1.dia,
								   sub1.porcentagem
							FROM   v_locais_pmesp lv
								   LEFT JOIN (
											SELECT sub1.id_equipamento,
												   DATEPART(DAY, sub1.dia) dia,
												   ROUND((CAST(sub1.atraso_ok AS FLOAT) / CAST(sub1.movimentos AS FLOAT)) * 100.0, 0) AS porcentagem
											FROM   pmesp_movimentos_atraso_dia AS sub1
											WHERE  sub1.dia BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										) AS sub1
											ON  lv.cod_pista_prodam = sub1.id_equipamento
						) AS sub1
				PIVOT (
						AVG(sub1.porcentagem)
						FOR sub1.dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
					  ) AS pvt
		   ) AS sub1
				--JOIN (
				--		SELECT sub1.id_equipamento,
				--			   sub1.atraso_ok,
				--			   sub1.movimentos - sub1.atraso_ok AS atraso_nok,
				--			   sub1.movimentos,
				--			   ROUND((CAST(sub1.atraso_ok AS FLOAT) / CAST(sub1.movimentos AS FLOAT)) * 100.0, 0) AS porc_atraso_ok,
				--			   ROUND((CAST((sub1.movimentos - sub1.atraso_ok) / CAST(sub1.movimentos AS FLOAT) AS FLOAT)) * 100.0, 0) AS porc_atraso_nok
				--		FROM   (
				--					SELECT pm.id_equipamento,
				--						   COUNT(pm.id_movimento) AS movimentos,
				--						   SUM(CASE WHEN DATEDIFF(SECOND, pm.data_movimento, pm.data_transmitido) <= 4
				--									THEN 1
				--									ELSE 0
				--							   END) AS atraso_ok
				--					FROM   pmesp_movimento pm (NOLOCK)
				--					WHERE  pm.data_movimento BETWEEN @Data_Ini AND @Data_Fim
				--					GROUP BY
				--						   pm.id_equipamento
				--			   ) AS sub1
				--	 ) AS sub2
				--		ON  sub1.cod_pista_prodam = sub2.id_equipamento
				LEFT JOIN (
						SELECT sub2.id_equipamento,
							   SUM(CASE WHEN sub2.porcentagem < 90 THEN 0 ELSE 1 END) AS dias_ok, SUM(sub2.movimentos) AS movimentos
						FROM   (
									SELECT sub1.id_equipamento,
										   DATEPART(DAY, sub1.dia) dia,
										   ROUND((CAST(sub1.atraso_ok AS FLOAT) / CAST(sub1.movimentos AS FLOAT)) * 100.0, 0) AS porcentagem,
										   sub1.movimentos
									FROM   pmesp_movimentos_atraso_dia AS sub1
									WHERE  sub1.dia BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
							   ) AS sub2
						GROUP BY
							   sub2.id_equipamento
					  ) AS sub3
						ON  sub1.cod_pista_prodam = sub3.id_equipamento

)
