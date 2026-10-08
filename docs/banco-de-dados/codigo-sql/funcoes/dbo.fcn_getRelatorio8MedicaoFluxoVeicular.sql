
CREATE FUNCTION [dbo].[fcn_getRelatorio8MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
	SELECT horarios.hora
		  ,horarios.hora_desc
		  ,ISNULL([SEGUNDA], 0) AS [segunda]
		  ,ISNULL([TERCA], 0) AS [terca]
		  ,ISNULL([QUARTA], 0) AS [quarta]
		  ,ISNULL([QUINTA], 0) AS [quinta]
		  ,ISNULL([SEXTA], 0) AS [sexta]
		  ,ISNULL([SABADO], 0) AS [sabado]
		  ,ISNULL([DOMINGO], 0) AS [domingo]
	FROM   hora horarios (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT vp.hora,
										   CASE WHEN DATEPART(WEEKDAY, vp.dia) = 1 THEN 'DOMINGO'
												WHEN DATEPART(WEEKDAY, vp.dia) = 2 THEN 'SEGUNDA'
												WHEN DATEPART(WEEKDAY, vp.dia) = 3 THEN 'TERCA'
												WHEN DATEPART(WEEKDAY, vp.dia) = 4 THEN 'QUARTA'
												WHEN DATEPART(WEEKDAY, vp.dia) = 5 THEN 'QUINTA'
												WHEN DATEPART(WEEKDAY, vp.dia) = 6 THEN 'SEXTA'
												WHEN DATEPART(WEEKDAY, vp.dia) = 7 THEN 'SABADO'
										   END AS dia_semana_desc,
										   vp.id_local,
										   vp.veiculos_detectados
									FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp
									WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) AS fv
						PIVOT
							   (
									SUM(fv.veiculos_detectados)
									FOR dia_semana_desc IN ([SEGUNDA],[TERCA],[QUARTA],[QUINTA],[SEXTA],[SABADO],[DOMINGO])
							   ) AS contagem_fluxo
			   ) AS fluxo
					ON  fluxo.hora = horarios.hora
)

