CREATE PROCEDURE [dbo].[spu_busca_amostras_periodo]
	@dataInicio DATE,
	@dataFinal DATE
AS

-- PARTE 1: Tabela Dummy com datas
WITH sub1(data,id_local,id_pista, id_configuracao_equipamento,metrologica,[manual],tipo,id_veiculo,id_infracao,id_imagem,score_total)
AS (
	SELECT
		CAST(dat.Data AS DATE) AS data,
		lcl.id_local,
		lcl.pista AS id_pista,
		pst.id_configuracao_equipamento,
		pst.metrologica,
		CAST(0 AS BIT) AS [manual],
		CAST(NULL AS VARCHAR(2)) AS tipo,
		CAST(NULL AS BIGINT) AS id_veiculo,
		NULL AS id_infracao,
		NULL AS id_imagem,
		NULL AS score_total
	FROM Date_Table(@dataInicio,@dataFinal) dat
		CROSS APPLY fcn_Locais_Vigentes_Na_Data(dat.Data) lcl
		LEFT JOIN (	SELECT DISTINCT
						eri.id_configuracao_equipamento,
						eri.id_pista,
						CASE 
							WHEN eri.tipo = 'VL'
								THEN 1 
							ELSE 
								0
						END AS metrologica
					FROM configuracao_equipamento_regra_infracao eri (nolock)
				  ) AS pst
			ON	pst.id_configuracao_equipamento = lcl.id_configuracao_equipamento
			AND (pst.id_pista = lcl.pista 
				OR pst.id_pista IS NULL)
),

-- PARTE 2: Amostras geradas pelo sistema
sub2(data,id_local,id_pista,id_configuracao_equipamento, metrologica,[manual],tipo,id_veiculo,id_infracao,id_imagem,score_total)
AS (
	SELECT
		CAST(ai.data AS DATE),
		inf.id_local,
		inf.pista AS id_pista,
		lvg.id_configuracao_equipamento,
		ai.metrologica,
		CAST(0 AS BIT) AS [manual],
		ai.tipo,
		ai.id_veiculo,
		inf.id_infracao,
		COALESCE(ii.id_imagem_obj, (
				SELECT TOP 1 vi.id_imagem
				FROM veiculo_imagem vi WITH (NOLOCK)
				WHERE vi.id_veiculo = ai.id_veiculo
			)
		) AS id_imagem,
		ai.score_total
	FROM amostra_imagem ai (nolock)
		JOIN infracao inf (nolock)
			ON inf.id_veiculo = ai.id_veiculo
		JOIN [local_vigente] lvg (nolock)
			ON lvg.id_local = inf.id_local
		LEFT JOIN infracao_imagem ii (nolock)
			ON inf.id_infracao = ii.id_infracao
	WHERE
		CAST(ai.data AS DATE) BETWEEN @dataInicio AND @dataFinal
),

-- PARTE 3: Amostras definidas manualmente
sub3(data,id_local,id_pista,id_configuracao_equipamento,metrologica,[manual],tipo,id_veiculo,id_infracao,id_imagem,score_total)
AS (
	SELECT
		CAST(amm.data AS DATE),
		amm.id_local,
		amm.id_pista,
		lvg.id_configuracao_equipamento,
		amm.metrologica,
		CAST(1 AS BIT) AS [manual],
		'MA' AS tipo,
		amm.id_veiculo,
		inf.id_infracao,
		COALESCE(ii.id_imagem_obj, (
					SELECT TOP 1 vi.id_imagem
					FROM veiculo_imagem vi WITH (NOLOCK)
					WHERE vi.id_veiculo = amm.id_veiculo
					)
		) AS id_imagem,
		NULL AS score_total
	FROM amostra_imagem_manual amm (nolock)
		JOIN infracao inf (nolock)
			ON inf.id_veiculo = amm.id_veiculo
		JOIN [local_vigente] lvg (nolock)
			ON lvg.id_local = inf.id_local
		LEFT JOIN infracao_imagem ii (nolock)
			ON inf.id_infracao = ii.id_infracao
	WHERE
		amm.data BETWEEN @dataInicio AND @dataFinal
),

-- PARTE 4 : Junta tudo
sub4(data,id_local,id_pista,id_configuracao_equipamento,metrologica,[manual],tipo,id_veiculo,id_infracao,id_imagem,score_total)
AS (
	SELECT * FROM sub1
	UNION ALL
	SELECT * FROM sub2
	UNION ALL
	SELECT * FROM sub3
)

-- PARTE 5: Finaliza
SELECT
	ble.data, ble.id_local, ble.id_pista, ble.metrologica,
	ble.[manual], ble.tipo, ble.id_veiculo, ble.id_infracao,
	ble.id_imagem, ble.score_total, ce.serie_equipamento,
	cep.nome_pista,	cep.cod_pista_alternativo, cep.cod_pista_prodam,
	cep.cod_pista
FROM (	SELECT
			bla.data, 
			bla.id_local, 
			bla.id_pista, 
			bla.metrologica,
			bla.[manual], 
			bla.tipo, 
			bla.id_veiculo, 
			bla.id_infracao,
			bla.id_imagem, 
			bla.score_total, 
			bla.id_configuracao_equipamento
		FROM (SELECT
				ROW_NUMBER() OVER (	PARTITION BY
										sub4.data,
										sub4.id_local,
										sub4.id_pista,
										sub4.metrologica
									ORDER BY [manual] DESC, id_veiculo DESC) rn,
						sub4.*
					FROM sub4
				) AS bla
		WHERE
			bla.rn = 1
	) AS ble
	INNER JOIN configuracao_equipamento ce (nolock)
		ON ce.id_configuracao_equipamento = ble.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_pista cep (nolock)
		ON	cep.id_configuracao_equipamento = ble.id_configuracao_equipamento
		AND cep.id_pista = ble.id_pista
WHERE
	-- Para evitar equipamentos que ainda não estão funcionando
	ce.data_inicio >= @dataInicio
	OR ce.data_inicio <= @dataFinal
ORDER BY
	ble.id_local ASC,
	cep.cod_pista_prodam ASC,
	ble.metrologica ASC,
	ble.data ASC



