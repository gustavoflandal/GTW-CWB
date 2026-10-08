CREATE FUNCTION [dbo].[fcn_lista_amostras_periodo_local_pista]
  ( @dataInicio date,
    @dataFinal date,
    @idLocal int,
    @idPista int,
    @metrologica bit )
RETURNS TABLE
AS

RETURN 
(
	SELECT TOP 100 PERCENT
		CAST(inf_base.data AS DATE) AS data,
		inf_base.id_local,
		inf_base.pista AS id_pista,
		COALESCE(pmv.metrologica, aim.metrologica, ai.metrologica) AS metrologica,
		CASE WHEN pmv.id_veiculo IS NULL THEN 0 ELSE 1 END as fixada,
		CASE WHEN aim.id_veiculo IS NULL THEN 0 ELSE 1 END as manual,
		CASE WHEN pmv.id_veiculo IS NOT NULL THEN 'XX' WHEN aim.id_veiculo IS NOT NULL THEN 'MA' ELSE ai.tipo END as tipo,
		inf_final.id_veiculo, 
		inf_final.id_infracao, 
		ii.id_imagem_obj as id_imagem, 
		COALESCE(pmv.score_total, aim.score_total, ai.score_total) AS score_total, --Se não tem amostra manual nem foi fixado então mostra o score.
		lv.serie_equipamento,
		cp.nome_pista,
		cp.cod_pista_alternativo,
		cp.cod_pista_prodam,
		cp.cod_pista,
		CASE WHEN pmv.id_veiculo IS NOT NULL THEN 1 ELSE COALESCE(aim.aplicavel,1) END AS aplicavel
	FROM amostra_imagem ai (nolock)
		INNER JOIN infracao inf_base (nolock) 
			ON inf_base.id_veiculo = ai.id_veiculo
		LEFT JOIN amostra_imagem_manual aim (nolock) 
			ON	aim.data = CAST(inf_base.data AS date) 
			AND aim.id_local = inf_base.id_local 
			AND	aim.id_pista = inf_base.pista 
			AND	aim.metrologica = ai.metrologica
		LEFT JOIN processo_medicao_veiculo pmv (nolock) 
			ON EXISTS (	SELECT top 1 
							id_veiculo 
						FROM veiculo v (nolock)
						WHERE	CAST(v.data AS DATE) = CAST(inf_base.data AS date) 
							AND	v.id_local = inf_base.id_local 
							AND	v.pista = inf_base.pista 
							AND	v.id_veiculo = pmv.id_veiculo
					) 
			AND	pmv.metrologica = ai.metrologica
		LEFT JOIN infracao inf_final (nolock) 
			ON	--	inf_final.id_veiculo = COALESCE(pmv.id_veiculo, aim.id_veiculo, ai.id_veiculo)
				(inf_final.id_veiculo = pmv.id_veiculo 
				OR (pmv.id_veiculo IS NULL AND inf_final.id_veiculo = aim.id_veiculo) 
				OR (pmv.id_veiculo IS NULL AND aim.id_veiculo IS NULL AND inf_final.id_veiculo = ai.id_veiculo))
		LEFT JOIN infracao_imagem ii (nolock) 
			ON ii.id_infracao = inf_final.id_infracao
		LEFT JOIN local_vigente lv (nolock) 
			ON lv.id_local = inf_base.id_local
		LEFT JOIN configuracao_equipamento_pista cp (nolock) 
			ON	cp.id_pista = inf_base.pista 
			AND cp.id_configuracao_equipamento = lv.id_configuracao_equipamento
	WHERE	CAST(inf_base.data AS DATE) BETWEEN @dataInicio AND @dataFinal
		AND ai.metrologica = @metrologica
		AND inf_base.id_local = @idLocal
		AND inf_base.pista = @idPista
	ORDER BY 
		1
)




