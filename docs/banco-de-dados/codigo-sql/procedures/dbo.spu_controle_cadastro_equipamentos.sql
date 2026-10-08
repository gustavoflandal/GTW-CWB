
CREATE PROCEDURE [dbo].[spu_controle_cadastro_equipamentos] AS

SELECT
	xpto.cod_pista AS [Código Prodam de Local],
	ce.serie_equipamento AS [Nº de Série],
	CONVERT(VARCHAR(10), (	SELECT TOP 1 
								data 
							FROM 
								configuracao_equipamento_afericao cea (nolock)
							WHERE	cea.id_configuracao_equipamento = xpto.id_configuracao_equipamento
								AND (cea.id_pista = xpto.id_pista 
									OR cea.id_pista IS NULL)
							ORDER BY id_pista DESC
							), 103) AS [Data de Aferição],
	(SELECT
			sub.tipo
	FROM (	SELECT DISTINCT 
				r1.id_pista, 
				SUBSTRING(GroupNames, 1, LEN(GroupNames) - 2) AS tipo
			FROM (	SELECT
						ceri.id_configuracao_equipamento,
						ceri.id_pista,
						ceri.tipo
					FROM
						configuracao_equipamento_regra_infracao ceri (nolock)
					WHERE	ceri.ativo = 1
						AND ceri.id_configuracao_equipamento = 425
						AND (ceri.id_pista = xpto.id_pista OR ceri.id_pista IS NULL)
					UNION
						-- Descobre se o equipamento infraciona rodízio
						SELECT
							ce.id_configuracao_equipamento,
							NULL AS id_pista,
							'RO' AS tipo
						FROM
							configuracao_equipamento ce (nolock)
						WHERE	ce.id_configuracao_equipamento = 425
							AND ce.flag_opcao & 4 = 4
				) AS r1
			CROSS APPLY	(	SELECT 
								tipo + ' - ' 
							FROM (	SELECT DISTINCT
										id_configuracao_equipamento,
										id_pista, 
										CASE 
											WHEN (tipo = 'VL') THEN 'V' -- Velocidade
											WHEN (tipo = 'RO') THEN 'R' -- Rodízio
											WHEN (tipo = 'LH') THEN 'Z' -- Zona Máxima
											WHEN (tipo = 'LR') THEN 'F'	-- Fretado
											WHEN (tipo = 'FX') THEN 'EX'-- Faixa Exclusiva
										END AS tipo,
										CASE 
											WHEN (tipo = 'VL') THEN 1
											WHEN (tipo = 'RO') THEN 2
											WHEN (tipo = 'LH') THEN 3					
											WHEN (tipo = 'LR') THEN 4
											WHEN (tipo = 'FX') THEN 5
										END AS sort
									FROM (	-- Busca as regras-infração de cada pista
											SELECT
												ceri.id_configuracao_equipamento,
												ceri.id_pista,
												ceri.tipo
											FROM
												configuracao_equipamento_regra_infracao ceri (nolock)
											WHERE	ceri.ativo = 1
												AND ceri.id_configuracao_equipamento = xpto.id_configuracao_equipamento
												AND (ceri.id_pista = xpto.id_pista 
													OR ceri.id_pista IS NULL)
											UNION
											-- Descobre se o equipamento infraciona rodízio
											SELECT
												ce.id_configuracao_equipamento,
												NULL AS id_pista,
												'RO' AS tipo
											FROM
												configuracao_equipamento ce (nolock)
											WHERE	ce.id_configuracao_equipamento = xpto.id_configuracao_equipamento
												AND ce.flag_opcao & 4 = 4
											) AS r3
								) AS r2
								ORDER BY
									sort
							FOR XML PATH('')
						)  D (GroupNames)
		) AS sub
	) AS [Enquadramentos],
	xpto.cod_pista_alternativo AS [Faixas],
	'??/2009-DSV.GAB' AS [Portaria Nº],
	'??/??/2009' AS [Data de Publicação],
	xpto.cod_pista_prodam AS [Código Prodam de Equipamento],
	'' AS [Sentido],
	xpto.nome_pista AS [Local],
	COALESCE ((	SELECT TOP 1 
					CAST(velocidade_limite AS VARCHAR(3))
				FROM 
					configuracao_equipamento_regra_infracao ceri (nolock)
				WHERE ceri.tipo = 'VL' 
					AND ceri.id_configuracao_equipamento = xpto.id_configuracao_equipamento
					AND (ceri.id_pista = xpto.id_pista 
						OR ceri.id_pista IS NULL)
				), ''
			 ) AS [Velocidade (km/h)],
	CAST(ce.data_inicio AS DATE) AS [Data Início]
FROM local_vigente lvg (nolock)
	INNER JOIN local lcl (nolock)
		ON lcl.id_local = lvg.id_local
		AND lcl.sequencia_local = lvg.sequencia_local
	INNER JOIN configuracao_equipamento ce (nolock)
		ON ce.id_configuracao_equipamento = lcl.id_configuracao_equipamento
	INNER JOIN configuracao_equipamento_pista xpto (nolock)
		ON xpto.id_configuracao_equipamento = lcl.id_configuracao_equipamento
WHERE
	ce.data_inicio <= GETDATE()
ORDER BY
	1 ASC, 
	2 ASC, 
	5 ASC





