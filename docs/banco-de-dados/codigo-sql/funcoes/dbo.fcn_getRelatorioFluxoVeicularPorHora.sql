
CREATE FUNCTION [dbo].[fcn_getRelatorioFluxoVeicularPorHora](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT) 
RETURNS TABLE 
AS 
RETURN 
(  
 
	--DECLARE @Data_Ini AS DATETIME = '2018-10-01 00:00:00', @Data_Fim AS DATETIME = '2018-10-31 23:59:59', @Id_Local INT = 5, @Id_Pista INT = NULL
	SELECT h.hora,
		   h.hora_desc,
		   v.[1],v.[2],v.[3],v.[4],v.[5],v.[6],v.[7],v.[8],v.[9],v.[10],
		   v.[11],v.[12],v.[13],v.[14],v.[15],v.[16],v.[17],v.[18],v.[19],v.[20],
		   v.[21],v.[22],v.[23],v.[24],v.[25],v.[26],v.[27],v.[28],v.[29],v.[30],v.[31],

		   (SELECT SUM(c)
			FROM (VALUES(v.[1]),(v.[2]),(v.[3]),(v.[4]),(v.[5]),(v.[6]),(v.[7]),(v.[8]),(v.[9]),
						(v.[10]),(v.[11]),(v.[12]),(v.[13]),(v.[14]),(v.[15]),(v.[16]),(v.[17]),(v.[18]),
						(v.[19]),(v.[20]),(v.[21]),(v.[22]),(v.[23]),(v.[24]),(v.[25]),(v.[26]),(v.[27]),
						(v.[28]),(v.[29]),(v.[30]),(v.[31])) T (c)) AS total_hora
	FROM   (
				SELECT hora,
					   hora_desc
				FROM   hora
				UNION
				SELECT 99 AS hora,
					   'TOTAL' AS hora_desc
		   ) h
		   LEFT JOIN (
						--DECLARE @Data_Ini AS DATETIME = '2018-11-01 00:00:00', @Data_Fim AS DATETIME = '2018-11-30 23:59:59', @Id_Local INT = 5, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini AS DATETIME = '2018-11-01 00:00:00', @Data_Fim AS DATETIME = '2018-11-30 23:59:59', @Id_Local INT = 5, @Id_Pista INT = NULL
									SELECT DATEPART(DAY, vsr.dia) AS dia,
										   vsr.hora,
										   vsr.veiculos_detectados
									FROM   veiculo_sumarizado_relatorio vsr (NOLOCK)
										   INNER JOIN local_pista_vigente lpv (NOLOCK)
												ON  lpv.id_local = vsr.id_local
													AND lpv.id_pista = vsr.id_pista
									WHERE  vsr.dia BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND vsr.id_local = @Id_Local
										   AND vsr.id_pista = CASE WHEN @Id_Pista IS NULL THEN vsr.id_pista ELSE @Id_Pista END
							  ) v
						PIVOT (
								SUM(veiculos_detectados)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) cont_v

						UNION

						--DECLARE @Data_Ini AS DATETIME = '2018-11-01 00:00:00', @Data_Fim AS DATETIME = '2018-11-30 23:59:59', @Id_Local INT = 5, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini AS DATETIME = '2018-11-01 00:00:00', @Data_Fim AS DATETIME = '2018-11-30 23:59:59', @Id_Local INT = 5, @Id_Pista INT = NULL
									SELECT DATEPART(DAY, vsr.dia) AS dia,
										   99 AS hora,
										   vsr.veiculos_detectados
									FROM   veiculo_sumarizado_relatorio vsr (NOLOCK)
										   INNER JOIN local_pista_vigente lpv (NOLOCK)
												ON  lpv.id_local = vsr.id_local
													AND lpv.id_pista = vsr.id_pista
									WHERE  vsr.dia BETWEEN CAST(@Data_Ini AS DATE) AND CAST(@Data_Fim AS DATE)
										   AND vsr.id_local = @Id_Local
										   AND vsr.id_pista = CASE WHEN @Id_Pista IS NULL THEN vsr.id_pista ELSE @Id_Pista END
							  ) v
						PIVOT (
								SUM(veiculos_detectados)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) cont_v
		   ) v
				ON  v.hora = h.hora

)
