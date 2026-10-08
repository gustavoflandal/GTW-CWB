CREATE FUNCTION [dbo].[fcn_IndicadoresFaixa] ( 
  @data_ini datetime,
  @data_fim datetime 
)
RETURNS TABLE
AS

RETURN
(
	SELECT TOP 100 PERCENT lv.serie_equipamento, cep.cod_pista, cep.cod_pista_alternativo, dt.data, 
		COALESCE(info_tipo_metro.ok,0) AS eh_metro, 
		COALESCE(info_tipo_nmetro.ok,0) AS eh_nmetro, 
		COALESCE(info_tipo_excl.ok,0) AS eh_excl, 
		COALESCE(info_amostra.metro_ok,0) AS metro_ok, 
		COALESCE(info_amostra.nmetro_ok,0) AS nmetro_ok, 
		pca.motivo_ext_energia,pca.motivo_ext_vandalismo,pca.motivo_ext_pavimento
	FROM local_vigente lv (nolock)
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
		INNER JOIN date_table(@data_ini,@data_fim) dt 
			ON 1=1
		LEFT JOIN (	SELECT CAST(1 AS BIT) AS ok, lri.id_local, lri.id_pista
						FROM local_regra_infracao_vigente lri (nolock)
						WHERE lri.tipo = 'VL'
						GROUP BY lri.id_local, lri.id_pista
				) AS info_tipo_metro 
			ON	info_tipo_metro.id_local = lv.id_local 
			AND info_tipo_metro.id_pista = cep.id_pista
		LEFT JOIN (	SELECT CAST(1 AS BIT) AS ok, lri.id_local, lri.id_pista
						FROM local_regra_infracao_vigente lri (nolock)
						WHERE lri.tipo NOT IN ('TS','VL','MT')
						GROUP BY lri.id_local, lri.id_pista
				) AS info_tipo_nmetro 
			ON	info_tipo_nmetro.id_local = lv.id_local 
			AND info_tipo_nmetro.id_pista = cep.id_pista
		LEFT JOIN (	SELECT CAST(1 AS BIT) AS ok, lri.id_local, lri.id_pista
						FROM local_regra_infracao_vigente lri (nolock)
						WHERE lri.tipo IN ('FX','FP')
						GROUP BY lri.id_local, lri.id_pista
				) AS info_tipo_excl 
			ON	info_tipo_excl.id_local = lv.id_local 
			AND info_tipo_excl.id_pista = cep.id_pista
		LEFT JOIN (	SELECT i.id_local, i.pista, CAST(i.data AS date) as data,
						MAX(CASE WHEN am.aplicavel IS NULL THEN (CASE a.metrologica WHEN 1 THEN 1 ELSE 0 END) ELSE(CASE WHEN am.aplicavel = 1 AND am.metrologica = 1 THEN 1 ELSE 0 END) END) AS metro_ok,
						MAX(CASE WHEN am.aplicavel IS NULL THEN (CASE a.metrologica WHEN 0 THEN 1 ELSE 0 END) ELSE(CASE WHEN am.aplicavel = 1 AND am.metrologica = 0 THEN 1 ELSE 0 END) END) AS nmetro_ok
					FROM infracao i (nolock) 
						INNER JOIN amostra_imagem a (nolock) 
							ON a.id_veiculo = i.id_veiculo
						LEFT JOIN amostra_imagem_manual am (nolock) 
							ON am.id_veiculo = i.id_veiculo
					WHERE 
						i.data BETWEEN @data_ini AND @data_fim
					GROUP BY 
						i.id_local, 
						i.pista, 
						CAST(i.data AS date)
				) as info_amostra 
			ON	info_amostra.id_local = lv.id_local 
			AND info_amostra.pista = cep.id_pista 
			AND info_amostra.data=CAST(dt.data AS DATE)
		LEFT JOIN painel_contrato_alerta pca (nolock) 
			ON	pca.serie_equipamento = lv.serie_equipamento 
			AND pca.id_pista=cep.id_pista AND dt.data >= pca.data_inclusao 
			AND (dt.data <= pca.data_atualizacao OR ativo = 1) --and 1=0
	WHERE
		NOT lv.desativado = 1 AND dt.data >= lv.data_inicio
	ORDER BY 
		serie_equipamento, 
		cep.cod_pista, 
		cep.cod_pista_alternativo, 
		dt.data
)




