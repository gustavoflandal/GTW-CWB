
CREATE FUNCTION [dbo].[fcn_getRelatorio5MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000', @Id_Local INT = 43, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,

		   fluxo.[1],fluxo.[2],fluxo.[3],fluxo.[4],fluxo.[5],fluxo.[6],fluxo.[7],fluxo.[8],fluxo.[9],fluxo.[10],
		   fluxo.[11],fluxo.[12],fluxo.[13],fluxo.[14],fluxo.[15],fluxo.[16],fluxo.[17],fluxo.[18],fluxo.[19],fluxo.[20],
		   fluxo.[21],fluxo.[22],fluxo.[23],fluxo.[24],fluxo.[25],fluxo.[26],fluxo.[27],fluxo.[28],fluxo.[29],fluxo.[30],fluxo.[31],

		   (SELECT SUM(c)
			FROM (VALUES(fluxo.[1]),(fluxo.[2]),(fluxo.[3]),(fluxo.[4]),(fluxo.[5]),(fluxo.[6]),(fluxo.[7]),(fluxo.[8]),(fluxo.[9]),
						(fluxo.[10]),(fluxo.[11]),(fluxo.[12]),(fluxo.[13]),(fluxo.[14]),(fluxo.[15]),(fluxo.[16]),(fluxo.[17]),(fluxo.[18]),
						(fluxo.[19]),(fluxo.[20]),(fluxo.[21]),(fluxo.[22]),(fluxo.[23]),(fluxo.[24]),(fluxo.[25]),(fluxo.[26]),(fluxo.[27]),
						(fluxo.[28]),(fluxo.[29]),(fluxo.[30]),(fluxo.[31])) T (c)) AS total_hora

	FROM   hora AS horarios
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000', @Id_Local INT = 43, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000', @Id_Local INT = 43, @Id_Pista INT = NULL
									SELECT DATEPART(DAY,vp.dia) AS dia
										  ,vp.hora
										  ,vp.id_local
										  ,vp.veiculos_detectados
									FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp
									WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) fv
						PIVOT 
							  (
								SUM(fv.veiculos_detectados)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_fluxo
		   ) AS fluxo
				ON  fluxo.hora = horarios.hora
)
