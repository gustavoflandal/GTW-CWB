
CREATE FUNCTION [dbo].[fcn_getRelatorio10MedicaoFluxoVeicular_PorFaixa](@Data_Ini DATETIME, @Data_Fim DATETIME)
RETURNS TABLE
AS
RETURN
( 

	--DECLARE @Data_Ini DATETIME = '2018-12-01 00:00:00.000', @Data_Fim DATETIME = '2018-12-31 23:59:59.000'
	SELECT dados.id_local,
		   dados.serie_equipamento,
		   dados.faixa,
		   dados.nome_pista_sentido_faixa,
		   dados.latitude,
		   dados.longitude,
		   dados.codigo_equipamento,
		   CASE WHEN cevl.velocidade_limite IS NULL OR cevl.velocidade_limite = 0 THEN dpg.velocidade_regulamentada ELSE cevl.velocidade_limite END AS velocidade_permitida,
		   dados.fv_1,CAST(ROUND(dados.vm_1, 0) AS INT) AS vm_1,dados.vmax_1,dados.ad_1,dados.av_1,
		   dados.fv_2,CAST(ROUND(dados.vm_2, 0) AS INT) AS vm_2,dados.vmax_2,dados.ad_2,dados.av_2,
		   dados.fv_3,CAST(ROUND(dados.vm_3, 0) AS INT) AS vm_3,dados.vmax_3,dados.ad_3,dados.av_3,
		   dados.fv_4,CAST(ROUND(dados.vm_4, 0) AS INT) AS vm_4,dados.vmax_4,dados.ad_4,dados.av_4,
		   dados.fv_5,CAST(ROUND(dados.vm_5, 0) AS INT) AS vm_5,dados.vmax_5,dados.ad_5,dados.av_5,
		   dados.fv_6,CAST(ROUND(dados.vm_6, 0) AS INT) AS vm_6,dados.vmax_6,dados.ad_6,dados.av_6,
		   dados.fv_7,CAST(ROUND(dados.vm_7, 0) AS INT) AS vm_7,dados.vmax_7,dados.ad_7,dados.av_7,
		   dados.fv_8,CAST(ROUND(dados.vm_8, 0) AS INT) AS vm_8,dados.vmax_8,dados.ad_8,dados.av_8,
		   dados.fv_9,CAST(ROUND(dados.vm_9, 0) AS INT) AS vm_9,dados.vmax_9,dados.ad_9,dados.av_9,
		   dados.fv_10,CAST(ROUND(dados.vm_10, 0) AS INT) AS vm_10,dados.vmax_10,dados.ad_10,dados.av_10,
		   dados.fv_11,CAST(ROUND(dados.vm_11, 0) AS INT) AS vm_11,dados.vmax_11,dados.ad_11,dados.av_11,
		   dados.fv_12,CAST(ROUND(dados.vm_12, 0) AS INT) AS vm_12,dados.vmax_12,dados.ad_12,dados.av_12,
		   dados.fv_13,CAST(ROUND(dados.vm_13, 0) AS INT) AS vm_13,dados.vmax_13,dados.ad_13,dados.av_13,
		   dados.fv_14,CAST(ROUND(dados.vm_14, 0) AS INT) AS vm_14,dados.vmax_14,dados.ad_14,dados.av_14,
		   dados.fv_15,CAST(ROUND(dados.vm_15, 0) AS INT) AS vm_15,dados.vmax_15,dados.ad_15,dados.av_15,
		   dados.fv_16,CAST(ROUND(dados.vm_16, 0) AS INT) AS vm_16,dados.vmax_16,dados.ad_16,dados.av_16,
		   dados.fv_17,CAST(ROUND(dados.vm_17, 0) AS INT) AS vm_17,dados.vmax_17,dados.ad_17,dados.av_17,
		   dados.fv_18,CAST(ROUND(dados.vm_18, 0) AS INT) AS vm_18,dados.vmax_18,dados.ad_18,dados.av_18,
		   dados.fv_19,CAST(ROUND(dados.vm_19, 0) AS INT) AS vm_19,dados.vmax_19,dados.ad_19,dados.av_19,
		   dados.fv_20,CAST(ROUND(dados.vm_20, 0) AS INT) AS vm_20,dados.vmax_20,dados.ad_20,dados.av_20,
		   dados.fv_21,CAST(ROUND(dados.vm_21, 0) AS INT) AS vm_21,dados.vmax_21,dados.ad_21,dados.av_21,
		   dados.fv_22,CAST(ROUND(dados.vm_22, 0) AS INT) AS vm_22,dados.vmax_22,dados.ad_22,dados.av_22,
		   dados.fv_23,CAST(ROUND(dados.vm_23, 0) AS INT) AS vm_23,dados.vmax_23,dados.ad_23,dados.av_23,
		   dados.fv_24,CAST(ROUND(dados.vm_24, 0) AS INT) AS vm_24,dados.vmax_24,dados.ad_24,dados.av_24,
		   dados.fv_25,CAST(ROUND(dados.vm_25, 0) AS INT) AS vm_25,dados.vmax_25,dados.ad_25,dados.av_25,
		   dados.fv_26,CAST(ROUND(dados.vm_26, 0) AS INT) AS vm_26,dados.vmax_26,dados.ad_26,dados.av_26,
		   dados.fv_27,CAST(ROUND(dados.vm_27, 0) AS INT) AS vm_27,dados.vmax_27,dados.ad_27,dados.av_27,
		   dados.fv_28,CAST(ROUND(dados.vm_28, 0) AS INT) AS vm_28,dados.vmax_28,dados.ad_28,dados.av_28,
		   dados.fv_29,CAST(ROUND(dados.vm_29, 0) AS INT) AS vm_29,dados.vmax_29,dados.ad_29,dados.av_29,
		   dados.fv_30,CAST(ROUND(dados.vm_30, 0) AS INT) AS vm_30,dados.vmax_30,dados.ad_30,dados.av_30,
		   dados.fv_31,CAST(ROUND(dados.vm_31, 0) AS INT) AS vm_31,dados.vmax_31,dados.ad_31,dados.av_31,
		   dados.total_fluxo,
		   CAST(ROUND(dados.total_velocidade_media, 0) AS INT) AS total_velocidade_media,
		   CAST(dados.total_velocidade_maxima AS INT) AS total_velocidade_maxima,
		   dados.total_autos_detectados,
		   dados.total_autos_validos,
		   dados.total_autos_invalidos_tecnicos,
		   dados.total_autos_invalidos_nao_tecnicos
	FROM   dbo.fcn_getRelatorio10MedicaoFluxoVeicular_Dados(@Data_Ini, @Data_Fim) dados
		   INNER JOIN local_pista_vigente lpv (NOLOCK)
				ON lpv.id_local = dados.id_local
					AND lpv.id_pista = dados.id_pista
		   LEFT JOIN configuracao_equipamento_velocidade_limite cevl (NOLOCK)
				ON cevl.id_local = lpv.id_local
					AND cevl.id_pista = lpv.id_pista
		   LEFT JOIN descricao_pista_gst dpg (NOLOCK)
				ON  dpg.id_local = lpv.id_local
					AND dpg.id_pista = lpv.id_pista

)
