
CREATE FUNCTION [dbo].[fcn_getRelatorio14MedicaoFluxoVeicular_Dados](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
	SELECT horarios.hora,
		   horarios.hora_desc,

		   fluxo.[1] AS [fv_1],fluxo.[2] AS [fv_2],fluxo.[3] AS [fv_3],fluxo.[4] AS [fv_4],fluxo.[5] AS [fv_5],fluxo.[6] AS [fv_6],fluxo.[7] AS [fv_7],
		   fluxo.[8] AS [fv_8],fluxo.[9] AS [fv_9],fluxo.[10] AS [fv_10],fluxo.[11] AS [fv_11],fluxo.[12] AS [fv_12],fluxo.[13] AS [fv_13],fluxo.[14] AS [fv_14],
		   fluxo.[15] AS [fv_15],fluxo.[16] AS [fv_16],fluxo.[17] AS [fv_17],fluxo.[18] AS [fv_18],fluxo.[19] AS [fv_19],fluxo.[20] AS [fv_20],
		   fluxo.[21] AS [fv_21],fluxo.[22] AS [fv_22],fluxo.[23] AS [fv_23],fluxo.[24] AS [fv_24],fluxo.[25] AS [fv_25],fluxo.[26] AS [fv_26],
		   fluxo.[27] AS [fv_27],fluxo.[28] AS [fv_28],fluxo.[29] AS [fv_29],fluxo.[30] AS [fv_30],fluxo.[31] AS [fv_31],

		   autos_detec_avanco.[1] AS [ad_avs_1],autos_detec_avanco.[2] AS [ad_avs_2],autos_detec_avanco.[3] AS [ad_avs_3],autos_detec_avanco.[4] AS [ad_avs_4],
		   autos_detec_avanco.[5] AS [ad_avs_5],autos_detec_avanco.[6] AS [ad_avs_6],autos_detec_avanco.[7] AS [ad_avs_7],autos_detec_avanco.[8] AS [ad_avs_8],
		   autos_detec_avanco.[9] AS [ad_avs_9],autos_detec_avanco.[10] AS [ad_avs_10],autos_detec_avanco.[11] AS [ad_avs_11],autos_detec_avanco.[12] AS [ad_avs_12],
		   autos_detec_avanco.[13] AS [ad_avs_13],autos_detec_avanco.[14] AS [ad_avs_14],autos_detec_avanco.[15] AS [ad_avs_15],autos_detec_avanco.[16] AS [ad_avs_16],
		   autos_detec_avanco.[17] AS [ad_avs_17],autos_detec_avanco.[18] AS [ad_avs_18],autos_detec_avanco.[19] AS [ad_avs_19],autos_detec_avanco.[20] AS [ad_avs_20],
		   autos_detec_avanco.[21] AS [ad_avs_21],autos_detec_avanco.[22] AS [ad_avs_22],autos_detec_avanco.[23] AS [ad_avs_23],autos_detec_avanco.[24] AS [ad_avs_24],
		   autos_detec_avanco.[25] AS [ad_avs_25],autos_detec_avanco.[26] AS [ad_avs_26],autos_detec_avanco.[27] AS [ad_avs_27],autos_detec_avanco.[28] AS [ad_avs_28],
		   autos_detec_avanco.[29] AS [ad_avs_29],autos_detec_avanco.[30] AS [ad_avs_30],autos_detec_avanco.[31] AS [ad_avs_31],

		   autos_detec_tve_2.[1] AS [ad_avs_tve_2_1],autos_detec_tve_2.[2] AS [ad_avs_tve_2_2],autos_detec_tve_2.[3] AS [ad_avs_tve_2_3],autos_detec_tve_2.[4] AS [ad_avs_tve_2_4],
		   autos_detec_tve_2.[5] AS [ad_avs_tve_2_5],autos_detec_tve_2.[6] AS [ad_avs_tve_2_6],autos_detec_tve_2.[7] AS [ad_avs_tve_2_7],autos_detec_tve_2.[8] AS [ad_avs_tve_2_8],
		   autos_detec_tve_2.[9] AS [ad_avs_tve_2_9],autos_detec_tve_2.[10] AS [ad_avs_tve_2_10],autos_detec_tve_2.[11] AS [ad_avs_tve_2_11],autos_detec_tve_2.[12] AS [ad_avs_tve_2_12],
		   autos_detec_tve_2.[13] AS [ad_avs_tve_2_13],autos_detec_tve_2.[14] AS [ad_avs_tve_2_14],autos_detec_tve_2.[15] AS [ad_avs_tve_2_15],autos_detec_tve_2.[16] AS [ad_avs_tve_2_16],
		   autos_detec_tve_2.[17] AS [ad_avs_tve_2_17],autos_detec_tve_2.[18] AS [ad_avs_tve_2_18],autos_detec_tve_2.[19] AS [ad_avs_tve_2_19],autos_detec_tve_2.[20] AS [ad_avs_tve_2_20],
		   autos_detec_tve_2.[21] AS [ad_avs_tve_2_21],autos_detec_tve_2.[22] AS [ad_avs_tve_2_22],autos_detec_tve_2.[23] AS [ad_avs_tve_2_23],autos_detec_tve_2.[24] AS [ad_avs_tve_2_24],
		   autos_detec_tve_2.[25] AS [ad_avs_tve_2_25],autos_detec_tve_2.[26] AS [ad_avs_tve_2_26],autos_detec_tve_2.[27] AS [ad_avs_tve_2_27],autos_detec_tve_2.[28] AS [ad_avs_tve_2_28],
		   autos_detec_tve_2.[29] AS [ad_avs_tve_2_29],autos_detec_tve_2.[30] AS [ad_avs_tve_2_30],autos_detec_tve_2.[31] AS [ad_avs_tve_2_31],

		   autos_detec_tve_2_5.[1] AS [ad_avs_tve_2_5_1],autos_detec_tve_2_5.[2] AS [ad_avs_tve_2_5_2],autos_detec_tve_2_5.[3] AS [ad_avs_tve_2_5_3],autos_detec_tve_2_5.[4] AS [ad_avs_tve_2_5_4],
		   autos_detec_tve_2_5.[5] AS [ad_avs_tve_2_5_5],autos_detec_tve_2_5.[6] AS [ad_avs_tve_2_5_6],autos_detec_tve_2_5.[7] AS [ad_avs_tve_2_5_7],autos_detec_tve_2_5.[8] AS [ad_avs_tve_2_5_8],
		   autos_detec_tve_2_5.[9] AS [ad_avs_tve_2_5_9],autos_detec_tve_2_5.[10] AS [ad_avs_tve_2_5_10],autos_detec_tve_2_5.[11] AS [ad_avs_tve_2_5_11],autos_detec_tve_2_5.[12] AS [ad_avs_tve_2_5_12],
		   autos_detec_tve_2_5.[13] AS [ad_avs_tve_2_5_13],autos_detec_tve_2_5.[14] AS [ad_avs_tve_2_5_14],autos_detec_tve_2_5.[15] AS [ad_avs_tve_2_5_15],autos_detec_tve_2_5.[16] AS [ad_avs_tve_2_5_16],
		   autos_detec_tve_2_5.[17] AS [ad_avs_tve_2_5_17],autos_detec_tve_2_5.[18] AS [ad_avs_tve_2_5_18],autos_detec_tve_2_5.[19] AS [ad_avs_tve_2_5_19],autos_detec_tve_2_5.[20] AS [ad_avs_tve_2_5_20],
		   autos_detec_tve_2_5.[21] AS [ad_avs_tve_2_5_21],autos_detec_tve_2_5.[22] AS [ad_avs_tve_2_5_22],autos_detec_tve_2_5.[23] AS [ad_avs_tve_2_5_23],autos_detec_tve_2_5.[24] AS [ad_avs_tve_2_5_24],
		   autos_detec_tve_2_5.[25] AS [ad_avs_tve_2_5_25],autos_detec_tve_2_5.[26] AS [ad_avs_tve_2_5_26],autos_detec_tve_2_5.[27] AS [ad_avs_tve_2_5_27],autos_detec_tve_2_5.[28] AS [ad_avs_tve_2_5_28],
		   autos_detec_tve_2_5.[29] AS [ad_avs_tve_2_5_29],autos_detec_tve_2_5.[30] AS [ad_avs_tve_2_5_30],autos_detec_tve_2_5.[31] AS [ad_avs_tve_2_5_31],

		   autos_detec_tve_5_10.[1] AS [ad_avs_tve_5_10_1],autos_detec_tve_5_10.[2] AS [ad_avs_tve_5_10_2],autos_detec_tve_5_10.[3] AS [ad_avs_tve_5_10_3],autos_detec_tve_5_10.[4] AS [ad_avs_tve_5_10_4],
		   autos_detec_tve_5_10.[5] AS [ad_avs_tve_5_10_5],autos_detec_tve_5_10.[6] AS [ad_avs_tve_5_10_6],autos_detec_tve_5_10.[7] AS [ad_avs_tve_5_10_7],autos_detec_tve_5_10.[8] AS [ad_avs_tve_5_10_8],
		   autos_detec_tve_5_10.[9] AS [ad_avs_tve_5_10_9],autos_detec_tve_5_10.[10] AS [ad_avs_tve_5_10_10],autos_detec_tve_5_10.[11] AS [ad_avs_tve_5_10_11],autos_detec_tve_5_10.[12] AS [ad_avs_tve_5_10_12],
		   autos_detec_tve_5_10.[13] AS [ad_avs_tve_5_10_13],autos_detec_tve_5_10.[14] AS [ad_avs_tve_5_10_14],autos_detec_tve_5_10.[15] AS [ad_avs_tve_5_10_15],autos_detec_tve_5_10.[16] AS [ad_avs_tve_5_10_16],
		   autos_detec_tve_5_10.[17] AS [ad_avs_tve_5_10_17],autos_detec_tve_5_10.[18] AS [ad_avs_tve_5_10_18],autos_detec_tve_5_10.[19] AS [ad_avs_tve_5_10_19],autos_detec_tve_5_10.[20] AS [ad_avs_tve_5_10_20],
		   autos_detec_tve_5_10.[21] AS [ad_avs_tve_5_10_21],autos_detec_tve_5_10.[22] AS [ad_avs_tve_5_10_22],autos_detec_tve_5_10.[23] AS [ad_avs_tve_5_10_23],autos_detec_tve_5_10.[24] AS [ad_avs_tve_5_10_24],
		   autos_detec_tve_5_10.[25] AS [ad_avs_tve_5_10_25],autos_detec_tve_5_10.[26] AS [ad_avs_tve_5_10_26],autos_detec_tve_5_10.[27] AS [ad_avs_tve_5_10_27],autos_detec_tve_5_10.[28] AS [ad_avs_tve_5_10_28],
		   autos_detec_tve_5_10.[29] AS [ad_avs_tve_5_10_29],autos_detec_tve_5_10.[30] AS [ad_avs_tve_5_10_30],autos_detec_tve_5_10.[31] AS [ad_avs_tve_5_10_31],

		   autos_detec_tve_10.[1] AS [ad_avs_tve_10_1],autos_detec_tve_10.[2] AS [ad_avs_tve_10_2],autos_detec_tve_10.[3] AS [ad_avs_tve_10_3],autos_detec_tve_10.[4] AS [ad_avs_tve_10_4],
		   autos_detec_tve_10.[5] AS [ad_avs_tve_10_5],autos_detec_tve_10.[6] AS [ad_avs_tve_10_6],autos_detec_tve_10.[7] AS [ad_avs_tve_10_7],autos_detec_tve_10.[8] AS [ad_avs_tve_10_8],
		   autos_detec_tve_10.[9] AS [ad_avs_tve_10_9],autos_detec_tve_10.[10] AS [ad_avs_tve_10_10],autos_detec_tve_10.[11] AS [ad_avs_tve_10_11],autos_detec_tve_10.[12] AS [ad_avs_tve_10_12],
		   autos_detec_tve_10.[13] AS [ad_avs_tve_10_13],autos_detec_tve_10.[14] AS [ad_avs_tve_10_14],autos_detec_tve_10.[15] AS [ad_avs_tve_10_15],autos_detec_tve_10.[16] AS [ad_avs_tve_10_16],
		   autos_detec_tve_10.[17] AS [ad_avs_tve_10_17],autos_detec_tve_10.[18] AS [ad_avs_tve_10_18],autos_detec_tve_10.[19] AS [ad_avs_tve_10_19],autos_detec_tve_10.[20] AS [ad_avs_tve_10_20],
		   autos_detec_tve_10.[21] AS [ad_avs_tve_10_21],autos_detec_tve_10.[22] AS [ad_avs_tve_10_22],autos_detec_tve_10.[23] AS [ad_avs_tve_10_23],autos_detec_tve_10.[24] AS [ad_avs_tve_10_24],
		   autos_detec_tve_10.[25] AS [ad_avs_tve_10_25],autos_detec_tve_10.[26] AS [ad_avs_tve_10_26],autos_detec_tve_10.[27] AS [ad_avs_tve_10_27],autos_detec_tve_10.[28] AS [ad_avs_tve_10_28],
		   autos_detec_tve_10.[29] AS [ad_avs_tve_10_29],autos_detec_tve_10.[30] AS [ad_avs_tve_10_30],autos_detec_tve_10.[31] AS [ad_avs_tve_10_31],
		   
		   autos_validos_avanco.[1] AS [av_avs_1],autos_validos_avanco.[2] AS [av_avs_2],autos_validos_avanco.[3] AS [av_avs_3],autos_validos_avanco.[4] AS [av_avs_4],
		   autos_validos_avanco.[5] AS [av_avs_5],autos_validos_avanco.[6] AS [av_avs_6],autos_validos_avanco.[7] AS [av_avs_7],autos_validos_avanco.[8] AS [av_avs_8],
		   autos_validos_avanco.[9] AS [av_avs_9],autos_validos_avanco.[10] AS [av_avs_10],autos_validos_avanco.[11] AS [av_avs_11],autos_validos_avanco.[12] AS [av_avs_12],
		   autos_validos_avanco.[13] AS [av_avs_13],autos_validos_avanco.[14] AS [av_avs_14],autos_validos_avanco.[15] AS [av_avs_15],autos_validos_avanco.[16] AS [av_avs_16],
		   autos_validos_avanco.[17] AS [av_avs_17],autos_validos_avanco.[18] AS [av_avs_18],autos_validos_avanco.[19] AS [av_avs_19],autos_validos_avanco.[20] AS [av_avs_20],
		   autos_validos_avanco.[21] AS [av_avs_21],autos_validos_avanco.[22] AS [av_avs_22],autos_validos_avanco.[23] AS [av_avs_23],autos_validos_avanco.[24] AS [av_avs_24],
		   autos_validos_avanco.[25] AS [av_avs_25],autos_validos_avanco.[26] AS [av_avs_26],autos_validos_avanco.[27] AS [av_avs_27],autos_validos_avanco.[28] AS [av_avs_28],
		   autos_validos_avanco.[29] AS [av_avs_29],autos_validos_avanco.[30] AS [av_avs_30],autos_validos_avanco.[31] AS [av_avs_31],

		   autos_validos_tve_2.[1] AS [av_avs_tve_2_1],autos_validos_tve_2.[2] AS [av_avs_tve_2_2],autos_validos_tve_2.[3] AS [av_avs_tve_2_3],autos_validos_tve_2.[4] AS [av_avs_tve_2_4],
		   autos_validos_tve_2.[5] AS [av_avs_tve_2_5],autos_validos_tve_2.[6] AS [av_avs_tve_2_6],autos_validos_tve_2.[7] AS [av_avs_tve_2_7],autos_validos_tve_2.[8] AS [av_avs_tve_2_8],
		   autos_validos_tve_2.[9] AS [av_avs_tve_2_9],autos_validos_tve_2.[10] AS [av_avs_tve_2_10],autos_validos_tve_2.[11] AS [av_avs_tve_2_11],autos_validos_tve_2.[12] AS [av_avs_tve_2_12],
		   autos_validos_tve_2.[13] AS [av_avs_tve_2_13],autos_validos_tve_2.[14] AS [av_avs_tve_2_14],autos_validos_tve_2.[15] AS [av_avs_tve_2_15],autos_validos_tve_2.[16] AS [av_avs_tve_2_16],
		   autos_validos_tve_2.[17] AS [av_avs_tve_2_17],autos_validos_tve_2.[18] AS [av_avs_tve_2_18],autos_validos_tve_2.[19] AS [av_avs_tve_2_19],autos_validos_tve_2.[20] AS [av_avs_tve_2_20],
		   autos_validos_tve_2.[21] AS [av_avs_tve_2_21],autos_validos_tve_2.[22] AS [av_avs_tve_2_22],autos_validos_tve_2.[23] AS [av_avs_tve_2_23],autos_validos_tve_2.[24] AS [av_avs_tve_2_24],
		   autos_validos_tve_2.[25] AS [av_avs_tve_2_25],autos_validos_tve_2.[26] AS [av_avs_tve_2_26],autos_validos_tve_2.[27] AS [av_avs_tve_2_27],autos_validos_tve_2.[28] AS [av_avs_tve_2_28],
		   autos_validos_tve_2.[29] AS [av_avs_tve_2_29],autos_validos_tve_2.[30] AS [av_avs_tve_2_30],autos_validos_tve_2.[31] AS [av_avs_tve_2_31],

		   autos_validos_tve_2_5.[1] AS [av_avs_tve_2_5_1],autos_validos_tve_2_5.[2] AS [av_avs_tve_2_5_2],autos_validos_tve_2_5.[3] AS [av_avs_tve_2_5_3],autos_validos_tve_2_5.[4] AS [av_avs_tve_2_5_4],
		   autos_validos_tve_2_5.[5] AS [av_avs_tve_2_5_5],autos_validos_tve_2_5.[6] AS [av_avs_tve_2_5_6],autos_validos_tve_2_5.[7] AS [av_avs_tve_2_5_7],autos_validos_tve_2_5.[8] AS [av_avs_tve_2_5_8],
		   autos_validos_tve_2_5.[9] AS [av_avs_tve_2_5_9],autos_validos_tve_2_5.[10] AS [av_avs_tve_2_5_10],autos_validos_tve_2_5.[11] AS [av_avs_tve_2_5_11],autos_validos_tve_2_5.[12] AS [av_avs_tve_2_5_12],
		   autos_validos_tve_2_5.[13] AS [av_avs_tve_2_5_13],autos_validos_tve_2_5.[14] AS [av_avs_tve_2_5_14],autos_validos_tve_2_5.[15] AS [av_avs_tve_2_5_15],autos_validos_tve_2_5.[16] AS [av_avs_tve_2_5_16],
		   autos_validos_tve_2_5.[17] AS [av_avs_tve_2_5_17],autos_validos_tve_2_5.[18] AS [av_avs_tve_2_5_18],autos_validos_tve_2_5.[19] AS [av_avs_tve_2_5_19],autos_validos_tve_2_5.[20] AS [av_avs_tve_2_5_20],
		   autos_validos_tve_2_5.[21] AS [av_avs_tve_2_5_21],autos_validos_tve_2_5.[22] AS [av_avs_tve_2_5_22],autos_validos_tve_2_5.[23] AS [av_avs_tve_2_5_23],autos_validos_tve_2_5.[24] AS [av_avs_tve_2_5_24],
		   autos_validos_tve_2_5.[25] AS [av_avs_tve_2_5_25],autos_validos_tve_2_5.[26] AS [av_avs_tve_2_5_26],autos_validos_tve_2_5.[27] AS [av_avs_tve_2_5_27],autos_validos_tve_2_5.[28] AS [av_avs_tve_2_5_28],
		   autos_validos_tve_2_5.[29] AS [av_avs_tve_2_5_29],autos_validos_tve_2_5.[30] AS [av_avs_tve_2_5_30],autos_validos_tve_2_5.[31] AS [av_avs_tve_2_5_31],

		   autos_validos_tve_5_10.[1] AS [av_avs_tve_5_10_1],autos_validos_tve_5_10.[2] AS [av_avs_tve_5_10_2],autos_validos_tve_5_10.[3] AS [av_avs_tve_5_10_3],autos_validos_tve_5_10.[4] AS [av_avs_tve_5_10_4],
		   autos_validos_tve_5_10.[5] AS [av_avs_tve_5_10_5],autos_validos_tve_5_10.[6] AS [av_avs_tve_5_10_6],autos_validos_tve_5_10.[7] AS [av_avs_tve_5_10_7],autos_validos_tve_5_10.[8] AS [av_avs_tve_5_10_8],
		   autos_validos_tve_5_10.[9] AS [av_avs_tve_5_10_9],autos_validos_tve_5_10.[10] AS [av_avs_tve_5_10_10],autos_validos_tve_5_10.[11] AS [av_avs_tve_5_10_11],autos_validos_tve_5_10.[12] AS [av_avs_tve_5_10_12],
		   autos_validos_tve_5_10.[13] AS [av_avs_tve_5_10_13],autos_validos_tve_5_10.[14] AS [av_avs_tve_5_10_14],autos_validos_tve_5_10.[15] AS [av_avs_tve_5_10_15],autos_validos_tve_5_10.[16] AS [av_avs_tve_5_10_16],
		   autos_validos_tve_5_10.[17] AS [av_avs_tve_5_10_17],autos_validos_tve_5_10.[18] AS [av_avs_tve_5_10_18],autos_validos_tve_5_10.[19] AS [av_avs_tve_5_10_19],autos_validos_tve_5_10.[20] AS [av_avs_tve_5_10_20],
		   autos_validos_tve_5_10.[21] AS [av_avs_tve_5_10_21],autos_validos_tve_5_10.[22] AS [av_avs_tve_5_10_22],autos_validos_tve_5_10.[23] AS [av_avs_tve_5_10_23],autos_validos_tve_5_10.[24] AS [av_avs_tve_5_10_24],
		   autos_validos_tve_5_10.[25] AS [av_avs_tve_5_10_25],autos_validos_tve_5_10.[26] AS [av_avs_tve_5_10_26],autos_validos_tve_5_10.[27] AS [av_avs_tve_5_10_27],autos_validos_tve_5_10.[28] AS [av_avs_tve_5_10_28],
		   autos_validos_tve_5_10.[29] AS [av_avs_tve_5_10_29],autos_validos_tve_5_10.[30] AS [av_avs_tve_5_10_30],autos_validos_tve_5_10.[31] AS [av_avs_tve_5_10_31],

		   autos_validos_tve_10.[1] AS [av_avs_tve_10_1],autos_validos_tve_10.[2] AS [av_avs_tve_10_2],autos_validos_tve_10.[3] AS [av_avs_tve_10_3],autos_validos_tve_10.[4] AS [av_avs_tve_10_4],
		   autos_validos_tve_10.[5] AS [av_avs_tve_10_5],autos_validos_tve_10.[6] AS [av_avs_tve_10_6],autos_validos_tve_10.[7] AS [av_avs_tve_10_7],autos_validos_tve_10.[8] AS [av_avs_tve_10_8],
		   autos_validos_tve_10.[9] AS [av_avs_tve_10_9],autos_validos_tve_10.[10] AS [av_avs_tve_10_10],autos_validos_tve_10.[11] AS [av_avs_tve_10_11],autos_validos_tve_10.[12] AS [av_avs_tve_10_12],
		   autos_validos_tve_10.[13] AS [av_avs_tve_10_13],autos_validos_tve_10.[14] AS [av_avs_tve_10_14],autos_validos_tve_10.[15] AS [av_avs_tve_10_15],autos_validos_tve_10.[16] AS [av_avs_tve_10_16],
		   autos_validos_tve_10.[17] AS [av_avs_tve_10_17],autos_validos_tve_10.[18] AS [av_avs_tve_10_18],autos_validos_tve_10.[19] AS [av_avs_tve_10_19],autos_validos_tve_10.[20] AS [av_avs_tve_10_20],
		   autos_validos_tve_10.[21] AS [av_avs_tve_10_21],autos_validos_tve_10.[22] AS [av_avs_tve_10_22],autos_validos_tve_10.[23] AS [av_avs_tve_10_23],autos_validos_tve_10.[24] AS [av_avs_tve_10_24],
		   autos_validos_tve_10.[25] AS [av_avs_tve_10_25],autos_validos_tve_10.[26] AS [av_avs_tve_10_26],autos_validos_tve_10.[27] AS [av_avs_tve_10_27],autos_validos_tve_10.[28] AS [av_avs_tve_10_28],
		   autos_validos_tve_10.[29] AS [av_avs_tve_10_29],autos_validos_tve_10.[30] AS [av_avs_tve_10_30],autos_validos_tve_10.[31] AS [av_avs_tve_10_31]

	FROM   hora horarios (NOLOCK)
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT DATEPART(DAY,vp.dia) AS dia,
										   vp.hora,
										   vp.id_local,
										   vp.veiculos_detectados
									FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp
									WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END
							   ) fv
						PIVOT (
								SUM(fv.veiculos_detectados)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_fluxo
		   ) AS fluxo
				ON  fluxo.hora = horarios.hora
		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_registradas) AS autos_detectados_avanco
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_detectados_avanco)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_detec_avanco
				ON  autos_detec_avanco.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_registradas) AS autos_detectados_tve_2
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec <=  2.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_detectados_tve_2)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_detec_tve_2
				ON  autos_detec_tve_2.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_registradas) AS autos_detectados_tve_2_5
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 2.0 AND i.tempo_vermelho_detec <= 5.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_detectados_tve_2_5)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_detec_tve_2_5
				ON  autos_detec_tve_2_5.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_registradas) AS autos_detectados_tve_5_10
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 5.0 AND i.tempo_vermelho_detec <= 10.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_detectados_tve_5_10)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_detec_tve_5_10
				ON  autos_detec_tve_5_10.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_registradas) AS autos_detectados_tve_10
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 10.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_detectados_tve_10)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_detec_tve_10
				ON  autos_detec_tve_10.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_validas) AS autos_validos_avanco
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) avavs
						PIVOT (
								SUM(avavs.autos_validos_avanco)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_avavs
		   ) AS autos_validos_avanco
				ON  autos_validos_avanco.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_validas) AS autos_validos_tve_2
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec <=  2.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_validos_tve_2)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_validos_tve_2
				ON  autos_validos_tve_2.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_validas) AS autos_validos_tve_2_5
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 2.0 AND i.tempo_vermelho_detec <= 5.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_validos_tve_2_5)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_validos_tve_2_5
				ON  autos_validos_tve_2_5.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_validas) AS autos_validos_tve_5_10
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 5.0 AND i.tempo_vermelho_detec <= 10.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_validos_tve_5_10)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_validos_tve_5_10
				ON  autos_validos_tve_5_10.hora = fluxo.hora

		   LEFT JOIN (
						--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
						SELECT *
						FROM   (
									--DECLARE @Data_Ini DATETIME = '2018-01-01 00:00:00', @Data_Fim DATETIME = '2018-01-31 23:59:59', @Id_Local INT = 9038, @Id_Pista INT = NULL
									SELECT i.hora,
										   DATEPART(DAY, i.dia) AS dia,
										   i.id_local,
										   SUM(i.infracoes_validas) AS autos_validos_tve_10
									FROM   fcn_getVeiculoSumarizadoRelatorioLocalEnquadramento(@Data_Ini, @Data_Fim, @Id_Local) i
									WHERE  i.id_enquadramento = 60503
										   AND i.tempo_vermelho_detec > 10.0
									GROUP BY
										   i.hora,
										   DATEPART(DAY, i.dia),
										   i.id_local
							   ) adavs
						PIVOT (
								SUM(adavs.autos_validos_tve_10)
								FOR dia IN ([1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31])
							  ) contagem_adavs
		   ) AS autos_validos_tve_10
				ON  autos_validos_tve_10.hora = fluxo.hora

)

