CREATE FUNCTION [dbo].[fcn_getRelDistribuicaoFaixaVelocidade](@Data DATE, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data DATE = '2021-12-01', @Id_Local INT = 18, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,
		   [0 - 9] AS [1],
		   [10 - 19] AS [2],
		   [20 - 29] AS [3],
		   [30 - 39] AS [4],
		   [40 - 49] AS [5],
		   [50 - 59] AS [6],
		   [60 - 69] AS [7],
		   [70 - 79] AS [8],
		   [80 - 89] AS [9],
		   [90 - 99] AS [10],
		   [100 - 109] AS [11],
		   [110 - 119] AS [12],
		   [120 - 129] AS [13],
		   [130 - 139] AS [14],
		   [140 - 149] AS [15],
		   [150 - 159] AS [16],
		   [> 160] AS [17],
		   (SELECT SUM(c)
		    FROM (VALUES(fluxo.[0 - 9]),(fluxo.[10 - 19]),(fluxo.[20 - 29]),(fluxo.[30 - 39]),(fluxo.[40 - 49]),(fluxo.[50 - 59]),(fluxo.[60 - 69]),(fluxo.[70 - 79]),(fluxo.[80 - 89]),
						(fluxo.[90 - 99]),(fluxo.[100 - 109]),(fluxo.[110 - 119]),(fluxo.[120 - 129]),(fluxo.[130 - 139]),(fluxo.[140 - 149]),(fluxo.[150 - 159]),(fluxo.[> 160])) T (c)) AS [18]
	FROM   hora horarios (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Data DATE = '2021-12-01', @Id_Local INT = 18, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data DATE = '2021-12-01', @Id_Local INT = 18, @Id_Pista INT = NULL
									SELECT vp.hora,
										   vp.id_local,
										   LTRIM(RTRIM(fx.descricao)) AS desc_faixa_velocidade,
										   vp.trafego
									--SELECT *
									FROM   veiculo_sumarizado vp (NOLOCK)
										   INNER JOIN faixa_velocidade fx (NOLOCK)
												ON  fx.id_faixa_velocidade = vp.id_faixa_velocidade
									WHERE  vp.data = @Data
										   AND vp.id_local = @Id_Local
										   AND vp.pista = CASE WHEN @Id_Pista IS NULL THEN vp.pista ELSE @Id_Pista END
							   ) AS fluxo
						PIVOT
							(
								SUM(trafego)
								FOR desc_faixa_velocidade IN
									(
										[0 - 9],[10 - 19],[20 - 29],[30 - 39],[40 - 49],[50 - 59],
										[60 - 69],[70 - 79],[80 - 89],[90 - 99],[100 - 109],[110 - 119],
										[120 - 129],[130 - 139],[140 - 149],[150 - 159],[> 160]
									)
							) AS contagem_fluxo
		   ) AS fluxo
				ON  fluxo.hora = horarios.hora

)
