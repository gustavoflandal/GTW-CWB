
CREATE FUNCTION [dbo].[fcn_getRelatorio14MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
	SELECT dados.hora,
		   dados.hora_desc,

		   dados.[fv_1],dados.[fv_2],dados.[fv_3],dados.[fv_4],dados.[fv_5],dados.[fv_6],dados.[fv_7],dados.[fv_8],dados.[fv_9],dados.[fv_10],dados.[fv_11],
		   dados.[fv_12],dados.[fv_13],dados.[fv_14],dados.[fv_15],dados.[fv_16],dados.[fv_17],dados.[fv_18],dados.[fv_19],dados.[fv_20],dados.[fv_21],
		   dados.[fv_22],dados.[fv_23],dados.[fv_24],dados.[fv_25],dados.[fv_26],dados.[fv_27],dados.[fv_28],dados.[fv_29],dados.[fv_30],dados.[fv_31],

		   dados.[ad_avs_1],dados.[ad_avs_2],dados.[ad_avs_3],dados.[ad_avs_4],dados.[ad_avs_5],dados.[ad_avs_6],dados.[ad_avs_7],dados.[ad_avs_8],dados.[ad_avs_9],
		   dados.[ad_avs_10],dados.[ad_avs_11],dados.[ad_avs_12],dados.[ad_avs_13],dados.[ad_avs_14],dados.[ad_avs_15],dados.[ad_avs_16],dados.[ad_avs_17],
		   dados.[ad_avs_18],dados.[ad_avs_19],dados.[ad_avs_20],dados.[ad_avs_21],dados.[ad_avs_22],dados.[ad_avs_23],dados.[ad_avs_24],dados.[ad_avs_25],
		   dados.[ad_avs_26],dados.[ad_avs_27],dados.[ad_avs_28],dados.[ad_avs_29],dados.[ad_avs_30],dados.[ad_avs_31],

		   dados.[ad_avs_tve_2_1],dados.[ad_avs_tve_2_2],dados.[ad_avs_tve_2_3],dados.[ad_avs_tve_2_4],dados.[ad_avs_tve_2_5],dados.[ad_avs_tve_2_6],
		   dados.[ad_avs_tve_2_7],dados.[ad_avs_tve_2_8],dados.[ad_avs_tve_2_9],dados.[ad_avs_tve_2_10],dados.[ad_avs_tve_2_11],dados.[ad_avs_tve_2_12],
		   dados.[ad_avs_tve_2_13],dados.[ad_avs_tve_2_14],dados.[ad_avs_tve_2_15],dados.[ad_avs_tve_2_16],dados.[ad_avs_tve_2_17],dados.[ad_avs_tve_2_18],
		   dados.[ad_avs_tve_2_19],dados.[ad_avs_tve_2_20],dados.[ad_avs_tve_2_21],dados.[ad_avs_tve_2_22],dados.[ad_avs_tve_2_23],dados.[ad_avs_tve_2_24],
		   dados.[ad_avs_tve_2_25],dados.[ad_avs_tve_2_26],dados.[ad_avs_tve_2_27],dados.[ad_avs_tve_2_28],dados.[ad_avs_tve_2_29],dados.[ad_avs_tve_2_30],dados.[ad_avs_tve_2_31],

		   dados.[ad_avs_tve_2_5_1],dados.[ad_avs_tve_2_5_2],dados.[ad_avs_tve_2_5_3],dados.[ad_avs_tve_2_5_4],dados.[ad_avs_tve_2_5_5],dados.[ad_avs_tve_2_5_6],
		   dados.[ad_avs_tve_2_5_7],dados.[ad_avs_tve_2_5_8],dados.[ad_avs_tve_2_5_9],dados.[ad_avs_tve_2_5_10],dados.[ad_avs_tve_2_5_11],dados.[ad_avs_tve_2_5_12],
		   dados.[ad_avs_tve_2_5_13],dados.[ad_avs_tve_2_5_14],dados.[ad_avs_tve_2_5_15],dados.[ad_avs_tve_2_5_16],dados.[ad_avs_tve_2_5_17],dados.[ad_avs_tve_2_5_18],
		   dados.[ad_avs_tve_2_5_19],dados.[ad_avs_tve_2_5_20],dados.[ad_avs_tve_2_5_21],dados.[ad_avs_tve_2_5_22],dados.[ad_avs_tve_2_5_23],dados.[ad_avs_tve_2_5_24],
		   dados.[ad_avs_tve_2_5_25],dados.[ad_avs_tve_2_5_26],dados.[ad_avs_tve_2_5_27],dados.[ad_avs_tve_2_5_28],dados.[ad_avs_tve_2_5_29],dados.[ad_avs_tve_2_5_30],dados.[ad_avs_tve_2_5_31],

		   dados.[ad_avs_tve_5_10_1],dados.[ad_avs_tve_5_10_2],dados.[ad_avs_tve_5_10_3],dados.[ad_avs_tve_5_10_4],dados.[ad_avs_tve_5_10_5],dados.[ad_avs_tve_5_10_6],
		   dados.[ad_avs_tve_5_10_7],dados.[ad_avs_tve_5_10_8],dados.[ad_avs_tve_5_10_9],dados.[ad_avs_tve_5_10_10],dados.[ad_avs_tve_5_10_11],dados.[ad_avs_tve_5_10_12],
		   dados.[ad_avs_tve_5_10_13],dados.[ad_avs_tve_5_10_14],dados.[ad_avs_tve_5_10_15],dados.[ad_avs_tve_5_10_16],dados.[ad_avs_tve_5_10_17],dados.[ad_avs_tve_5_10_18],
		   dados.[ad_avs_tve_5_10_19],dados.[ad_avs_tve_5_10_20],dados.[ad_avs_tve_5_10_21],dados.[ad_avs_tve_5_10_22],dados.[ad_avs_tve_5_10_23],dados.[ad_avs_tve_5_10_24],
		   dados.[ad_avs_tve_5_10_25],dados.[ad_avs_tve_5_10_26],dados.[ad_avs_tve_5_10_27],dados.[ad_avs_tve_5_10_28],dados.[ad_avs_tve_5_10_29],dados.[ad_avs_tve_5_10_30],dados.[ad_avs_tve_5_10_31],

		   dados.[ad_avs_tve_10_1],dados.[ad_avs_tve_10_2],dados.[ad_avs_tve_10_3],dados.[ad_avs_tve_10_4],dados.[ad_avs_tve_10_5],dados.[ad_avs_tve_10_6],dados.[ad_avs_tve_10_7],
		   dados.[ad_avs_tve_10_8],dados.[ad_avs_tve_10_9],dados.[ad_avs_tve_10_10],dados.[ad_avs_tve_10_11],dados.[ad_avs_tve_10_12],dados.[ad_avs_tve_10_13],dados.[ad_avs_tve_10_14],
		   dados.[ad_avs_tve_10_15],dados.[ad_avs_tve_10_16],dados.[ad_avs_tve_10_17],dados.[ad_avs_tve_10_18],dados.[ad_avs_tve_10_19],dados.[ad_avs_tve_10_20],dados.[ad_avs_tve_10_21],
		   dados.[ad_avs_tve_10_22],dados.[ad_avs_tve_10_23],dados.[ad_avs_tve_10_24],dados.[ad_avs_tve_10_25],dados.[ad_avs_tve_10_26],dados.[ad_avs_tve_10_27],dados.[ad_avs_tve_10_28],
		   dados.[ad_avs_tve_10_29],dados.[ad_avs_tve_10_30],dados.[ad_avs_tve_10_31],

		   dados.[av_avs_1],dados.[av_avs_2],dados.[av_avs_3],dados.[av_avs_4],dados.[av_avs_5],dados.[av_avs_6],dados.[av_avs_7],dados.[av_avs_8],dados.[av_avs_9],
		   dados.[av_avs_10],dados.[av_avs_11],dados.[av_avs_12],dados.[av_avs_13],dados.[av_avs_14],dados.[av_avs_15],dados.[av_avs_16],dados.[av_avs_17],
		   dados.[av_avs_18],dados.[av_avs_19],dados.[av_avs_20],dados.[av_avs_21],dados.[av_avs_22],dados.[av_avs_23],dados.[av_avs_24],dados.[av_avs_25],
		   dados.[av_avs_26],dados.[av_avs_27],dados.[av_avs_28],dados.[av_avs_29],dados.[av_avs_30],dados.[av_avs_31],

		   dados.[av_avs_tve_2_1],dados.[av_avs_tve_2_2],dados.[av_avs_tve_2_3],dados.[av_avs_tve_2_4],dados.[av_avs_tve_2_5],dados.[av_avs_tve_2_6],
		   dados.[av_avs_tve_2_7],dados.[av_avs_tve_2_8],dados.[av_avs_tve_2_9],dados.[av_avs_tve_2_10],dados.[av_avs_tve_2_11],dados.[av_avs_tve_2_12],
		   dados.[av_avs_tve_2_13],dados.[av_avs_tve_2_14],dados.[av_avs_tve_2_15],dados.[av_avs_tve_2_16],dados.[av_avs_tve_2_17],dados.[av_avs_tve_2_18],
		   dados.[av_avs_tve_2_19],dados.[av_avs_tve_2_20],dados.[av_avs_tve_2_21],dados.[av_avs_tve_2_22],dados.[av_avs_tve_2_23],dados.[av_avs_tve_2_24],
		   dados.[av_avs_tve_2_25],dados.[av_avs_tve_2_26],dados.[av_avs_tve_2_27],dados.[av_avs_tve_2_28],dados.[av_avs_tve_2_29],dados.[av_avs_tve_2_30],dados.[av_avs_tve_2_31],

		   dados.[av_avs_tve_2_5_1],dados.[av_avs_tve_2_5_2],dados.[av_avs_tve_2_5_3],dados.[av_avs_tve_2_5_4],dados.[av_avs_tve_2_5_5],dados.[av_avs_tve_2_5_6],
		   dados.[av_avs_tve_2_5_7],dados.[av_avs_tve_2_5_8],dados.[av_avs_tve_2_5_9],dados.[av_avs_tve_2_5_10],dados.[av_avs_tve_2_5_11],dados.[av_avs_tve_2_5_12],
		   dados.[av_avs_tve_2_5_13],dados.[av_avs_tve_2_5_14],dados.[av_avs_tve_2_5_15],dados.[av_avs_tve_2_5_16],dados.[av_avs_tve_2_5_17],dados.[av_avs_tve_2_5_18],
		   dados.[av_avs_tve_2_5_19],dados.[av_avs_tve_2_5_20],dados.[av_avs_tve_2_5_21],dados.[av_avs_tve_2_5_22],dados.[av_avs_tve_2_5_23],dados.[av_avs_tve_2_5_24],
		   dados.[av_avs_tve_2_5_25],dados.[av_avs_tve_2_5_26],dados.[av_avs_tve_2_5_27],dados.[av_avs_tve_2_5_28],dados.[av_avs_tve_2_5_29],dados.[av_avs_tve_2_5_30],dados.[av_avs_tve_2_5_31],

		   dados.[av_avs_tve_5_10_1],dados.[av_avs_tve_5_10_2],dados.[av_avs_tve_5_10_3],dados.[av_avs_tve_5_10_4],dados.[av_avs_tve_5_10_5],dados.[av_avs_tve_5_10_6],
		   dados.[av_avs_tve_5_10_7],dados.[av_avs_tve_5_10_8],dados.[av_avs_tve_5_10_9],dados.[av_avs_tve_5_10_10],dados.[av_avs_tve_5_10_11],dados.[av_avs_tve_5_10_12],
		   dados.[av_avs_tve_5_10_13],dados.[av_avs_tve_5_10_14],dados.[av_avs_tve_5_10_15],dados.[av_avs_tve_5_10_16],dados.[av_avs_tve_5_10_17],dados.[av_avs_tve_5_10_18],
		   dados.[av_avs_tve_5_10_19],dados.[av_avs_tve_5_10_20],dados.[av_avs_tve_5_10_21],dados.[av_avs_tve_5_10_22],dados.[av_avs_tve_5_10_23],dados.[av_avs_tve_5_10_24],
		   dados.[av_avs_tve_5_10_25],dados.[av_avs_tve_5_10_26],dados.[av_avs_tve_5_10_27],dados.[av_avs_tve_5_10_28],dados.[av_avs_tve_5_10_29],dados.[av_avs_tve_5_10_30],dados.[av_avs_tve_5_10_31],

		   dados.[av_avs_tve_10_1],dados.[av_avs_tve_10_2],dados.[av_avs_tve_10_3],dados.[av_avs_tve_10_4],dados.[av_avs_tve_10_5],dados.[av_avs_tve_10_6],dados.[av_avs_tve_10_7],
		   dados.[av_avs_tve_10_8],dados.[av_avs_tve_10_9],dados.[av_avs_tve_10_10],dados.[av_avs_tve_10_11],dados.[av_avs_tve_10_12],dados.[av_avs_tve_10_13],dados.[av_avs_tve_10_14],
		   dados.[av_avs_tve_10_15],dados.[av_avs_tve_10_16],dados.[av_avs_tve_10_17],dados.[av_avs_tve_10_18],dados.[av_avs_tve_10_19],dados.[av_avs_tve_10_20],dados.[av_avs_tve_10_21],
		   dados.[av_avs_tve_10_22],dados.[av_avs_tve_10_23],dados.[av_avs_tve_10_24],dados.[av_avs_tve_10_25],dados.[av_avs_tve_10_26],dados.[av_avs_tve_10_27],dados.[av_avs_tve_10_28],
		   dados.[av_avs_tve_10_29],dados.[av_avs_tve_10_30],dados.[av_avs_tve_10_31]
		   
	FROM   dbo.fcn_getRelatorio14MedicaoFluxoVeicular_Dados(@Data_Ini, @Data_Fim, @Id_Local, @Id_Pista) dados

)

