
CREATE PROCEDURE [dbo].[spu_exportar_imagens_comprovacao]
	@id_processo_medicao INT,
	@complementar BIT = 0
AS

	SELECT
		ce.serie_equipamento,
		cep.cod_pista,
		cep.cod_pista_prodam,
		inf.data,
		COALESCE(aim.metrologica,ai.metrologica) AS metrologica,
		CASE
			WHEN (inf.id_enquadramento = 1) 
				THEN 'TST'					 -- É uma amostra de imagem-teste
			WHEN ( (SELECT 
						COUNT(id_infracao) 
					FROM 
						infracao_remessa (nolock)
					WHERE id_infracao = inf.id_infracao) > 0) 
				THEN 'INF' -- É uma amostra de infração(que gerou auto, que está em uma remessa)
			ELSE 
				'OUT' 
		END AS qualificador,									 -- Qualquer outra coisa		
		ii.id_imagem_obj,
		img_obj.imagem AS img_obj,	
		ii.id_imagem_pan,
		img_pan.imagem AS img_pan	
	FROM processo_medicao_veiculo pmv (nolock)
		INNER JOIN infracao inf (nolock)
			ON inf.id_veiculo = pmv.id_veiculo
		LEFT JOIN amostra_imagem_manual aim (nolock) 
			ON aim.id_veiculo = pmv.id_veiculo
		LEFT JOIN amostra_imagem ai (nolock) 
			ON ai.id_veiculo = pmv.id_veiculo
		INNER JOIN local lcl (nolock)
			ON lcl.id_local = inf.id_local
			AND lcl.sequencia_local = inf.sequencia_local
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
			AND cep.id_pista = inf.pista
		INNER JOIN infracao_imagem ii (nolock)
			ON inf.id_infracao = ii.id_infracao
		INNER JOIN imagem img_obj (nolock)
			ON img_obj.id_imagem = ii.id_imagem_obj
		LEFT JOIN imagem img_pan (nolock)
			ON img_pan.id_imagem = ii.id_imagem_pan
	WHERE	pmv.id_processo_medicao = @id_processo_medicao 
		AND pmv.etapa = (	CASE 
								WHEN @complementar = 0 
									THEN 1 
								WHEN @complementar = 1 
									THEN 2 
							END)



