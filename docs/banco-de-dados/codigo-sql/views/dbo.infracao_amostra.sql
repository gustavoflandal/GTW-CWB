
CREATE VIEW [dbo].[infracao_amostra] AS

SELECT
    sub.*
FROM
	(
	SELECT
		inf.id_veiculo,
		inf.id_infracao,
		vei.id_classe,
		inf.id_local,
		inf.sequencia_local,
		cfe.id_configuracao_equipamento,
		cfe.serie_equipamento,
		inf.pista AS id_pista,
		vei.velocidade,
		vei.placa AS placa_ocr,
		inf.id_inconsistencia,
		inf.data,
		cfp.cod_pista_alternativo,
		cfp.cod_pista_prodam,
		cfp.cod_pista,
		enq.infracao_metrologia AS metrologica,
		cfp.nome_pista,
		iim.id_imagem_obj AS id_imagem,
		inf.id_enquadramento,      
		inf.id_processo,
		inf.placa AS placa_digitada,
		CASE
			WHEN (inf.id_enquadramento IN (74550, 74630, 74710)) THEN 'VL'
 
			WHEN (inf.id_enquadramento = 57462) THEN 'RO'
 
			WHEN (inf.id_enquadramento = 57463) THEN 'LH'
 
			WHEN (inf.id_enquadramento = 57461) THEN 'LR'

			WHEN (inf.id_enquadramento = 56900) THEN 'FX'
 
			 WHEN (inf.id_enquadramento = 57030 ) THEN 'FF'         
 
			 WHEN (inf.id_enquadramento = 56732) THEN 'PF'
 
			 WHEN (inf.id_enquadramento = 60503) THEN 'AV'
 
			 WHEN (inf.id_enquadramento = 59910 ) THEN 'RP'
 
			 WHEN (inf.id_enquadramento IN (60411, 60412)) THEN 'CV'
 
			 ELSE 'TS'
		END AS tipo,
		CASE WHEN ((vei.flag & 2097152) = 2097152) THEN 1 ELSE 0 END as imagem_captura_ruim     
    FROM infracao inf (nolock)
		INNER JOIN veiculo vei (nolock)
			ON vei.id_veiculo = inf.id_veiculo
		INNER JOIN [local] lcl (nolock)
			ON lcl.id_local = inf.id_local
			AND lcl.sequencia_local = inf.sequencia_local
		INNER JOIN configuracao_equipamento cfe (nolock)
			ON cfe.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		INNER JOIN configuracao_equipamento_pista cfp (nolock)
			ON cfp.id_configuracao_equipamento = cfe.id_configuracao_equipamento
			AND cfp.id_pista = inf.pista
		INNER JOIN enquadramento enq (nolock)
			ON enq.id_enquadramento = inf.id_enquadramento
		LEFT JOIN configuracao_equipamento_parametros_adicionais cepa (nolock)
			ON cepa.id_configuracao_equipamento = lcl.id_configuracao_equipamento
		LEFT JOIN infracao_imagem iim (nolock)
			ON iim.id_infracao = inf.id_infracao
	WHERE
				GETDATE() >= cfe.data_inicio AND  		inf.id_inconsistencia NOT IN (5, 48, 49)		AND ( 
						(
				inf.id_enquadramento = 1 AND 
				(					(NOT cepa.parametros_adicionais LIKE '%SHOW_PLATE_TEST_IMAGE_LABEL=1%' OR						(inf.placa IS NULL OR 							( 								SUBSTRING(vei.placa,4,4) = SUBSTRING(inf.placa,4,4) AND 								(SUBSTRING(vei.placa,1,3) = SUBSTRING(inf.placa,1,3) OR 									(vei.placa LIKE '[DQO][DQO][DQO]____' AND inf.placa LIKE '[DQO][DQO][DQO]____') 								)
							)
						)
					)
				)
			)
			
						
			
			OR (
				inf.id_enquadramento > 1 
							/*AND (
					vei.placa IS NULL OR 
					NOT EXISTS (
						SELECT ci.placa FROM fcn_pesquisaIsento(inf.placa,inf.id_enquadramento, inf.data) ci
						WHERE inf.data BETWEEN ci.data_inicio AND ci.data_fim AND (ci.area = 0 OR ci.area = cfp.cod_area)
						UNION
						SELECT ci.placa FROM fcn_pesquisaIsento(vei.placa,inf.id_enquadramento, inf.data) ci
						WHERE inf.data BETWEEN ci.data_inicio AND ci.data_fim AND (ci.area = 0 OR ci.area = cfp.cod_area)

					)
				)*/
			)
		)
    ) AS sub
WHERE
    sub.id_imagem IS NOT NULL



