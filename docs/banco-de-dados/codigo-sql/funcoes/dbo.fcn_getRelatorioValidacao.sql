
---------------------------------------------------------------------------------------------------------------------------------------
-- Email Felipe - 22/07/2014
--

CREATE FUNCTION [dbo].[fcn_getRelatorioValidacao](@id_remessa INT) 
RETURNS TABLE AS  

RETURN 
--DECLARE @id_remessa INT = 3031 
SELECT CONVERT(VARCHAR, sub1.data_remessa, 103) AS data_remessa,
	tipo_apait AS codigo_empresa, 
	RIGHT(REPLICATE('0', 6) + RTRIM(CONVERT(VARCHAR(6), sub1.codigo_externo)), 6) AS numero_lote, 
	RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR(4), sub1.sequencia), 4) AS registro_lote, 
	RIGHT(REPLICATE('0', 7) + CONVERT(VARCHAR(7), sub1.id_imagem_local), 7) AS registro_equipamento,
	sub1.placa, sub1.pais, RIGHT(REPLICATE('0', 3) + CONVERT(VARCHAR(3), sub1.id_marca), 3) AS marca, 
	RIGHT(REPLICATE('0', 3) + CONVERT(VARCHAR(3), sub1.id_especie), 3) AS especie,
	sub1.id_enquadramento AS enquadramento, 
	RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR(4), sub1.cod_pista), 4) AS [local], sub1.nome_pista AS [descricao],
	RIGHT(REPLICATE('0', 4) + CONVERT(VARCHAR(4), sub1.cod_pista_prodam), 4) AS equipamento, 
	CONVERT(VARCHAR, sub1.data, 103) AS data_registro,  
	CONVERT(VARCHAR, sub1.data, 108) AS hora_registro, 
	RIGHT(REPLICATE('0', 3) + CONVERT(VARCHAR(3), CONVERT(INT, sub1.velocidade)), 3) AS velocidade,
	RIGHT(REPLICATE('0', 6) + CONVERT(VARCHAR(6), CONVERT(INT, sub1.cod_agente)), 6) AS cod_agente,
	REPLICATE('0', 4) AS montante, CASE WHEN sub1.id_inconsistencia = 0 THEN 1 ELSE 0 END AS consistencia,
	'0' AS imagem_notif, sub1.inconsistencia_liberacao, sub1.inconsistencia_validacao, sub1.placa_digitada, sub1.marca_cet, 
	COALESCE(sub1.marca_cet_val, 'N/D') AS marca_cet_val,
	CASE WHEN sub1.erro_oblit = 1 THEN 'Sim' ELSE 'Não' END AS erro_obliteracao
FROM ( 	
	--  DECLARE @id_remessa INT = 3031
		SELECT 
			r.data AS data_remessa, 
			eri.tipo_apait,
			r.codigo_externo, 
			ir.sequencia, 
			i.id_imagem_local, 
			COALESCE(mi.placa, v.placa, REPLICATE(' ', 7)) AS placa, 
			i.placa AS placa_digitada,
			'00' AS pais, 
			mi.id_marca_cet as id_marca, 
			cmc.descricao AS marca_cet,
			cmc_d.descricao AS marca_cet_val,
			COALESCE(ep.id_especie, mi.id_especie, cv.id_especie, 990) AS id_especie, 
			u.cod_agente, 
			i.id_enquadramento, 
			COALESCE(p.cod_pista, mi.id_local) AS cod_pista, 
			COALESCE(p.nome_pista, mi.descricao_local) AS nome_pista,
			COALESCE(p.cod_pista_prodam, mi.cod_pista_prodam) AS cod_pista_prodam,
			i.data, 
			v.velocidade, 
			i.id_inconsistencia, 
			inc_l.descricao AS inconsistencia_liberacao, 
			inc_v.descricao AS inconsistencia_validacao,
			ip_v.erro_oblit   
		FROM infracao i (nolock) 
			INNER JOIN infracao_remessa ir (nolock) 
				ON i.id_infracao = ir.id_infracao 
			INNER JOIN remessa r (nolock) 
				ON r.id_remessa = ir.id_remessa 
			INNER JOIN movimento_importacao mi (NOLOCK)
				ON r.codigo_externo = mi.id_movimento
				AND r.id_enquadramento = mi.id_enquadramento
				AND ir.sequencia = mi.sequencia
			INNER JOIN veiculo v (nolock) 
				ON i.id_veiculo = v.id_veiculo  
			INNER JOIN enquadramento_regra_infracao eri (NOLOCK) 
				ON eri.id_enquadramento = r.id_enquadramento
			LEFT JOIN cad_veiculo cv  (nolock) 
				ON cv.placa = i.placa 
			LEFT JOIN cad_veiculo cvv (nolock) 
				ON cvv.placa = v.placa 
			LEFT JOIN cad_marca_cet_processo cmcetp (nolock) 
				ON cmcetp.placa = i.placa 
			LEFT JOIN cad_especie_processo ep (nolock) 
				ON ep.placa = i.placa 
			LEFT JOIN local l (nolock) 
				ON	l.id_local = i.id_local 
				AND l.sequencia_local = i.sequencia_local 
			LEFT JOIN configuracao_equipamento_pista p (nolock) 
				ON	p.id_configuracao_equipamento = l.id_configuracao_equipamento 
				AND p.id_pista = i.pista 
			LEFT JOIN infracao_processo_concluido ipc_l (nolock) 
				ON	i.id_infracao = ipc_l.id_infracao 
				AND ipc_l.id_processo = 11 
			JOIN infracao_processo ip_v (nolock) 
				ON	ir.id_infracao = ip_v.id_infracao 
				AND ip_v.id_processo = 3
			JOIN (	SELECT 
						MAX(id_infracao_processo) AS id_infracao_processo, 
						id_infracao, 
						id_processo 
					FROM 
						infracao_processo ip (nolock) 
					GROUP BY 
						id_infracao, 
						id_processo
				) AS sub1 
				ON	ir.id_infracao = sub1.id_infracao 
				AND sub1.id_processo = 3 
				AND ip_v.id_infracao_processo = sub1.id_infracao_processo
		JOIN inconsistencia inc_l (nolock) 
			ON inc_l.id_inconsistencia = COALESCE(ipc_l.id_inconsistencia, mi.[id_inconsistencia]) 
		LEFT JOIN sis_usuario u (nolock) 
			ON COALESCE(i.id_usuario_final, ip_v.id_usuario) = u.id_usuario  
		JOIN inconsistencia inc_v (nolock) 
			ON inc_v.id_inconsistencia = ip_v.id_inconsistencia 
		LEFT JOIN infracao_processo_digitacao ipd (NOLOCK) 
			ON ip_v.id_infracao_processo = ipd.id_infracao_processo 
		JOIN cad_marca_cet cmc (NOLOCK) 
			ON mi.id_marca_cet = cmc.id_marca_cet  
		LEFT JOIN cad_marca_cet cmc_d (NOLOCK)
			ON cmc_d.id_marca_cet = ipd.id_marca_cet
		WHERE r.id_remessa = @id_remessa
		AND i.id_processo = 25
		AND (((CASE WHEN i.id_inconsistencia = 0 THEN 0 ELSE 1 END ^ CASE WHEN mi.id_inconsistencia = 0 THEN 0 ELSE 1 END) > 0)
		OR (mi.placa <> REPLICATE(' ', 7) AND i.placa <> REPLICATE(' ', 7) AND mi.placa <> i.placa)
		OR (ipd.id_marca_cet IS NOT NULL AND mi.id_marca_cet <> ipd.id_marca_cet)
		OR (ip_v.erro_oblit = 1) )
	) AS sub1 

