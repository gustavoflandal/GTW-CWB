
CREATE PROCEDURE [dbo].[spu_exporta_amostras_periodo]
	@dataInicio DATE,
	@dataFinal DATE
AS

SELECT 
	sub2.serie_equipamento,
	sub2.cod_pista,
	sub2.cod_pista_prodam,
	sub2.data,
	sub2.metrologica,
	sub2.qualificador,
	sub2.id_imagem_obj,
	sub2.img_obj,	
	sub2.id_imagem_pan,
	sub2.img_pan
FROM (	SELECT
			sub1.serie_equipamento,
			sub1.cod_pista,
			sub1.cod_pista_prodam,
			sub1.data,
			sub1.metrologica,
			sub1.qualificador,
			sub1.id_imagem_obj,
			img1.imagem AS img_obj,	
			sub1.id_imagem_pan,
			img2.imagem AS img_pan,
			ROW_NUMBER() OVER(PARTITION BY CAST(sub1.data AS DATE), sub1.serie_equipamento, sub1.pista, sub1.metrologica ORDER BY sub1.[manual] DESC, sub1.id_veiculo DESC) AS rn
		FROM ( -- PARTE 1: Pega as amostras que tiveram 'override manual'	
				SELECT
					ce.serie_equipamento,
					inf.pista,		
					cep.cod_pista,
					cep.cod_pista_prodam,
					inf.data,
					aim.metrologica,
					inf.id_veiculo,
					CAST(1 AS BIT) AS [manual],
					CASE
						WHEN (inf.id_enquadramento = 1) 
							THEN 'TST'					 -- É uma amostra de imagem-teste
						WHEN ( (SELECT COUNT(id_infracao) 
								FROM infracao_remessa (nolock)
								WHERE id_infracao = inf.id_infracao) > 0) 
							THEN 'INF' -- É uma amostra de infração(que gerou auto, que está em uma remessa)
						ELSE 
							'OUT' 
					END AS qualificador,									 -- Qualquer outra coisa		
					COALESCE( ( SELECT 
									iim.id_imagem_obj 
								FROM 
									infracao_imagem iim  (nolock)
								WHERE 
									iim.id_infracao = inf.id_infracao),
								(SELECT TOP 1 
									vim.id_imagem 
								FROM veiculo_imagem vim  (nolock)
									INNER JOIN imagem_info ii  (nolock) 
										ON vim.id_imagem = ii.id_imagem
									INNER JOIN tipo_imagem ti  (nolock) 
										ON ti.id_tipo_imagem = ii.id_tipo_imagem
								WHERE	ti.nome = 'OBJ' 
									AND vim.id_veiculo = inf.id_veiculo 
								ORDER BY 
									ti.numero)
							) AS id_imagem_obj,
					COALESCE( (	SELECT TOP 1 
									iim.id_imagem_pan 
								FROM 
									infracao_imagem iim  (nolock)
								WHERE iim.id_infracao = inf.id_infracao),

								(SELECT TOP 1 
									vim.id_imagem 
								FROM veiculo_imagem vim  (nolock)
									INNER JOIN imagem_info ii  (nolock) 
										ON vim.id_imagem = ii.id_imagem
									INNER JOIN tipo_imagem ti  (nolock) 
										ON ti.id_tipo_imagem = ii.id_tipo_imagem
								WHERE	ti.nome = 'PAN' 
									AND vim.id_veiculo = inf.id_veiculo 
								ORDER BY ti.numero)

							) AS id_imagem_pan	
				FROM amostra_imagem_manual aim  (nolock)
					INNER JOIN infracao inf  (nolock)
						ON inf.id_veiculo = aim.id_veiculo
					INNER JOIN local lcl  (nolock)
						ON	lcl.id_local = inf.id_local
						AND lcl.sequencia_local = inf.sequencia_local
					INNER JOIN configuracao_equipamento ce  (nolock)
						ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
					INNER JOIN configuracao_equipamento_pista cep  (nolock)
						ON	cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
						AND cep.id_pista = inf.pista
					LEFT JOIN infracao_imagem ii  (nolock)
						ON inf.id_infracao = ii.id_infracao
				WHERE
					CAST(aim.data AS DATE) BETWEEN @dataInicio AND @dataFinal
			
			UNION
		
				-- PARTE 2: Pega as amostras que não tiveram 'override manual'
				SELECT
					ai.serie_equipamento,
					inf.pista,
					ai.cod_pista,
					cep.cod_pista_prodam,
					ai.data AS data,
					ai.metrologica,
					inf.id_veiculo,			
					CAST(0 AS BIT) AS [manual],
					CASE
						WHEN (ai.id_enquadramento = 1) 
							THEN 'TST'					 -- É uma amostra de imagem-teste
						WHEN ( (SELECT 
									COUNT(id_infracao) 
								FROM 
									infracao_remessa
								WHERE id_infracao = inf.id_infracao) > 0) 
							THEN 'INF' -- É uma amostra de infração(que gerou auto, que está em uma remessa)
						ELSE 
							'OUT' 
					END AS qualificador,									 -- Qualquer outra coisa
					COALESCE( (	SELECT 
									iim.id_imagem_obj 
								FROM 
									infracao_imagem iim  (nolock)
								WHERE 
									iim.id_infracao = inf.id_infracao),
								(SELECT TOP 1 
									vim.id_imagem 
								FROM veiculo_imagem vim  (nolock)
									INNER JOIN imagem_info ii  (nolock) 
										ON vim.id_imagem = ii.id_imagem
									INNER JOIN tipo_imagem ti  (nolock) 
										ON ti.id_tipo_imagem = ii.id_tipo_imagem
								WHERE	ti.nome = 'OBJ' 
									AND vim.id_veiculo = inf.id_veiculo 
								ORDER BY 
									ti.numero)

							) AS id_imagem_obj,
					COALESCE( (	SELECT TOP 1 
									iim.id_imagem_pan 
								FROM 
									infracao_imagem iim  (nolock)
								WHERE iim.id_infracao = inf.id_infracao),
								(SELECT TOP 1 
									vim.id_imagem 
								FROM 
									veiculo_imagem vim  (nolock)
								JOIN imagem_info ii (nolock) 
									ON vim.id_imagem = ii.id_imagem
								JOIN tipo_imagem ti (nolock) 
									ON ti.id_tipo_imagem = ii.id_tipo_imagem
								WHERE ti.nome = 'PAN' AND vim.id_veiculo = inf.id_veiculo ORDER BY ti.numero)

							) AS id_imagem_pan
				FROM amostra_imagem ai  (nolock)
					INNER JOIN infracao inf  (nolock)
						ON ai.id_veiculo = inf.id_veiculo
					INNER JOIN local lcl  (nolock)
						ON	lcl.id_local = inf.id_local
						AND lcl.sequencia_local = inf.sequencia_local
					INNER JOIN configuracao_equipamento ce  (nolock)
						ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
					JOIN configuracao_equipamento_pista cep  (nolock)
						ON	cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
						AND cep.id_pista = inf.pista
					WHERE	CAST(ai.data AS DATE) BETWEEN @dataInicio AND @dataFinal
						AND ai.id_veiculo NOT IN (	SELECT 
														id_veiculo 
													FROM 
														amostra_imagem_manual aim  (nolock)
													WHERE 
														CAST(aim.data AS DATE) BETWEEN @dataInicio AND @dataFinal)
			) AS sub1
			LEFT JOIN imagem img1  (nolock)
				ON img1.id_imagem = sub1.id_imagem_obj
			LEFT JOIN imagem img2  (nolock)
				ON img2.id_imagem = sub1.id_imagem_pan
		) AS sub2
	WHERE
		sub2.rn = 1




