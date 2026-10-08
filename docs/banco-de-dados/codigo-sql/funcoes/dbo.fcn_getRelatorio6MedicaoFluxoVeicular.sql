
CREATE FUNCTION [dbo].[fcn_getRelatorio6MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-06-01 00:00:00.000', @Data_Fim DATETIME = '2018-06-30 23:59:59.000', @Id_Local INT = 2, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,
		   CAST(ROUND(vel_media.[1], 0) AS INT) AS [1],
		   CAST(ROUND(vel_media.[2], 0) AS INT) AS [2],
		   CAST(ROUND(vel_media.[3], 0) AS INT) AS [3],
		   CAST(ROUND(vel_media.[4], 0) AS INT) AS [4],
		   CAST(ROUND(vel_media.[5], 0) AS INT) AS [5],
		   CAST(ROUND(vel_media.[6], 0) AS INT) AS [6],
		   CAST(ROUND(vel_media.[7], 0) AS INT) AS [7],
		   CAST(ROUND(vel_media.[8], 0) AS INT) AS [8],
		   CAST(ROUND(vel_media.[9], 0) AS INT) AS [9],
		   CAST(ROUND(vel_media.[10], 0) AS INT) AS [10],
		   CAST(ROUND(vel_media.[11], 0) AS INT) AS [11],
		   CAST(ROUND(vel_media.[12], 0) AS INT) AS [12],
		   CAST(ROUND(vel_media.[13], 0) AS INT) AS [13],
		   CAST(ROUND(vel_media.[14], 0) AS INT) AS [14],
		   CAST(ROUND(vel_media.[15], 0) AS INT) AS [15],
		   CAST(ROUND(vel_media.[16], 0) AS INT) AS [16],
		   CAST(ROUND(vel_media.[17], 0) AS INT) AS [17],
		   CAST(ROUND(vel_media.[18], 0) AS INT) AS [18],
		   CAST(ROUND(vel_media.[19], 0) AS INT) AS [19],
		   CAST(ROUND(vel_media.[20], 0) AS INT) AS [20],
		   CAST(ROUND(vel_media.[21], 0) AS INT) AS [21],
		   CAST(ROUND(vel_media.[22], 0) AS INT) AS [22],
		   CAST(ROUND(vel_media.[23], 0) AS INT) AS [23],
		   CAST(ROUND(vel_media.[24], 0) AS INT) AS [24],
		   CAST(ROUND(vel_media.[25], 0) AS INT) AS [25],
		   CAST(ROUND(vel_media.[26], 0) AS INT) AS [26],
		   CAST(ROUND(vel_media.[27], 0) AS INT) AS [27],
		   CAST(ROUND(vel_media.[28], 0) AS INT) AS [28],
		   CAST(ROUND(vel_media.[29], 0) AS INT) AS [29],
		   CAST(ROUND(vel_media.[30], 0) AS INT) AS [30],
		   CAST(ROUND(vel_media.[31], 0) AS INT) AS [31]
	FROM   hora AS horarios
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00.000', @Data_Fim DATETIME = '2018-01-31 23:59:59.000', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT DATEPART(DAY, vp.dia) AS dia,
										   vp.hora,
										   vp.velocidade_media,
										   vp.id_local
									FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp
									WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) vm
						PIVOT 
							  (
								AVG(vm.velocidade_media)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_vm
		   ) AS vel_media
				ON  vel_media.hora = horarios.hora

)

