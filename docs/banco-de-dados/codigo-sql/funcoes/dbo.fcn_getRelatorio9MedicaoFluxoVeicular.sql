
CREATE FUNCTION [dbo].[fcn_getRelatorio9MedicaoFluxoVeicular](@Data DATE, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data DATETIME = '2019-04-04', @Id_Local INT = 77, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,
		   
		   fluxo.ate_5km AS [1],
		   fluxo.entre_6km_e_10km AS [2],
		   fluxo.entre_11km_e_15km AS [3],
		   fluxo.entre_16km_e_20km AS [4],
		   fluxo.entre_21km_e_25km AS [5],
		   fluxo.entre_26km_e_30km AS [6],
		   fluxo.entre_31km_e_35km AS [7],
		   fluxo.entre_36km_e_40km AS [8],
		   fluxo.entre_41km_e_45km AS [9],
		   fluxo.entre_46km_e_50km AS [10],
		   fluxo.entre_51km_e_55km AS [11],
		   fluxo.entre_56km_e_60km AS [12],
		   fluxo.entre_61km_e_65km AS [13],
		   fluxo.entre_66km_e_70km AS [14],
		   fluxo.entre_71km_e_75km AS [15],
		   fluxo.entre_76km_e_80km AS [16],
		   fluxo.entre_81km_e_85km AS [17],
		   fluxo.entre_86km_e_90km AS [18],
		   fluxo.entre_91km_e_95km AS [19],
		   fluxo.entre_96km_e_100km AS [20],
		   fluxo.entre_101km_e_105km AS [21],
		   fluxo.entre_106km_e_110km AS [22],
		   fluxo.entre_111km_e_115km AS [23],
		   fluxo.entre_116km_e_120km AS [24],
		   fluxo.entre_121km_e_125km AS [25],
		   fluxo.entre_126km_e_130km AS [26],
		   fluxo.entre_131km_e_135km AS [27],
		   fluxo.entre_136km_e_140km AS [28],
		   fluxo.entre_141km_e_145km AS [29],
		   fluxo.entre_146km_e_150km AS [30],
		   fluxo.entre_151km_e_155km AS [31],
		   fluxo.entre_156km_e_160km AS [32],
		   fluxo.entre_161km_e_165km AS [33],
		   fluxo.entre_166km_e_170km AS [34],
		   fluxo.entre_171km_e_175km AS [35],
		   fluxo.entre_176km_e_180km AS [36],
		   fluxo.entre_181km_e_185km AS [37],
		   fluxo.entre_186km_e_190km AS [38],
		   fluxo.entre_191km_e_195km AS [39],
		   fluxo.entre_196km_e_200km AS [40],
		   fluxo.acima_de_200km AS [41],
		   (SELECT SUM(c)
		    FROM (VALUES(fluxo.ate_5km),(fluxo.entre_6km_e_10km),(fluxo.entre_11km_e_15km),(fluxo.entre_16km_e_20km),
				 (fluxo.entre_21km_e_25km),(fluxo.entre_26km_e_30km),(fluxo.entre_31km_e_35km),(fluxo.entre_36km_e_40km),
				 (fluxo.entre_41km_e_45km),(fluxo.entre_46km_e_50km),(fluxo.entre_51km_e_55km),(fluxo.entre_56km_e_60km),
				 (fluxo.entre_61km_e_65km),(fluxo.entre_66km_e_70km),(fluxo.entre_71km_e_75km),(fluxo.entre_76km_e_80km),
				 (fluxo.entre_81km_e_85km),(fluxo.entre_86km_e_90km),(fluxo.entre_91km_e_95km),(fluxo.entre_96km_e_100km),
				 (fluxo.entre_101km_e_105km),(fluxo.entre_106km_e_110km),(fluxo.entre_111km_e_115km),(fluxo.entre_116km_e_120km),
				 (fluxo.entre_121km_e_125km),(fluxo.entre_126km_e_130km),(fluxo.entre_131km_e_135km),(fluxo.entre_136km_e_140km),
				 (fluxo.entre_141km_e_145km),(fluxo.entre_146km_e_150km),(fluxo.entre_151km_e_155km),(fluxo.entre_156km_e_160km),
				 (fluxo.entre_161km_e_165km),(fluxo.entre_166km_e_170km),(fluxo.entre_171km_e_175km),(fluxo.entre_176km_e_180km),
				 (fluxo.entre_181km_e_185km),(fluxo.entre_186km_e_190km),(fluxo.entre_191km_e_195km),(fluxo.entre_196km_e_200km),
				 (fluxo.acima_de_200km)) T (c)) AS [42]
	FROM   hora horarios (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Data DATETIME = '2019-04-04', @Id_Local INT = 77, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data DATETIME = '2019-04-04', @Id_Local INT = 77, @Id_Pista INT = NULL
									SELECT vp.hora,
										   vp.id_local,
										   fx.descricao AS desc_faixa_velocidade,
										   vp.veiculos_detectados
									--SELECT *
									FROM   veiculo_sumarizado_faixa_velocidade vp (NOLOCK)
										   INNER JOIN faixa_velocidade_relatorio_rj fx (NOLOCK)
												ON  fx.id_faixa_velocidade = vp.id_faixa_velocidade
									WHERE  vp.dia = @Data
										   AND vp.id_local = @Id_Local
										   AND vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) AS fluxo
						PIVOT
							(
								SUM(veiculos_detectados)
								FOR desc_faixa_velocidade IN
									(
										[ate_5km],[entre_6km_e_10km],[entre_11km_e_15km],[entre_16km_e_20km],[entre_21km_e_25km],[entre_26km_e_30km],
										[entre_31km_e_35km],[entre_36km_e_40km],[entre_41km_e_45km],[entre_46km_e_50km],[entre_51km_e_55km],[entre_56km_e_60km],
										[entre_61km_e_65km],[entre_66km_e_70km],[entre_71km_e_75km],[entre_76km_e_80km],[entre_81km_e_85km],[entre_86km_e_90km],
										[entre_91km_e_95km],[entre_96km_e_100km],[entre_101km_e_105km],[entre_106km_e_110km],[entre_111km_e_115km],
										[entre_116km_e_120km],[entre_121km_e_125km],[entre_126km_e_130km],[entre_131km_e_135km],[entre_136km_e_140km],
										[entre_141km_e_145km],[entre_146km_e_150km],[entre_151km_e_155km],[entre_156km_e_160km],[entre_161km_e_165km],
										[entre_166km_e_170km],[entre_171km_e_175km],[entre_176km_e_180km],[entre_181km_e_185km],[entre_186km_e_190km],
										[entre_191km_e_195km],[entre_196km_e_200km],[acima_de_200km]
									)
							) AS contagem_fluxo
		   ) AS fluxo
				ON  fluxo.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data DATETIME = '2019-04-04', @Id_Local INT = 77, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data DATETIME = '2019-04-04', @Id_Local INT = 77, @Id_Pista INT = NULL
									SELECT vp.hora,
										   vp.id_local,
										   'soma' AS desc_faixa_velocidade,
										   vp.veiculos_detectados
									FROM   veiculo_sumarizado_faixa_velocidade vp (NOLOCK)
										   INNER JOIN faixa_velocidade_relatorio_rj fx (NOLOCK)
												ON  fx.id_faixa_velocidade = vp.id_faixa_velocidade
									WHERE  vp.dia = @Data
										   AND vp.id_local = @Id_Local
										   AND vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) AS fluxo
						PIVOT
							(
								SUM(veiculos_detectados)
								FOR desc_faixa_velocidade IN ([soma])
							) AS contagem_fluxo
		   ) AS fluxo_total
				ON  fluxo_total.hora = horarios.hora

)
