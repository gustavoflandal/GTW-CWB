CREATE FUNCTION [dbo].[fcn_ObterDadosTarja]  
(	
	@id_infracao INT 
)
RETURNS TABLE 
AS
RETURN 
(
--  DECLARE @id_infracao INT = 3
	SELECT DISTINCT 
		CONVERT(VARCHAR(10), sub1.data, 103) AS [DT INFR], 
		RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(HOUR, sub1.data)), 2) + 'h' + RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(MINUTE, sub1.data)), 2)  
		+ 'min' + RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(SECOND, sub1.data)), 2) + 's' AS [HOR INFR], 
		CASE 
			WHEN sub1.porteVeiculo IS NOT NULL
				THEN sub1.porteVeiculo
			WHEN sub1.id_tipo IS NULL 
				THEN 
					CASE 
						WHEN sub1.id_classe IN ('C', 'O') 
							THEN 'pesado' 
						ELSE 
							'leve' 
					END 
			ELSE 
				CASE 
					WHEN sub1.id_tipo IN (7, 8, 10, 11, 14, 17, 18, 20, 22, 26) 
						THEN 'pesado' 
					ELSE 
						'leve' 
				END 
		END AS [CLASSIF], 
		sub1.nome_pista AS [LOCAL/SENTIDO], 
		RIGHT('000000000' + CONVERT(VARCHAR(10), sub1.serie_equipamento), 10) AS [COD EQUIP], 
		CONVERT(VARCHAR(10), sub1.data_afericao, 103) AS [DT AFER], 
		sub1.pista AS [FX ROL], 
		RIGHT('00' + CONVERT(VARCHAR(3), sub1.velocidade_limite), 3) + ' km/h' AS [VEL REG], 
		RIGHT('00' + CONVERT(VARCHAR(3), CONVERT(INT,sub1.velocidade)), 3) + ' km/h' AS [VEL MEDIDA], 
		RIGHT('00' + CONVERT(VARCHAR(3), sub1.velocidade_considerada), 3) + ' km/h' AS [VEL CONS], 
		RIGHT('000000' + CONVERT(VARCHAR(7), sub1.id_imagem_local), 7) AS [No. SEQ REG], 
		sub1.id_enquadramento AS [COD ENQ], 
		sub1.descricao AS [DESCRIÇÃO], 
		RIGHT('000000000' + CONVERT(VARCHAR(10), sub1.codigo_montante), 10) AS [COD EQUIP MONT], 
		sub1.serie_equipamento AS [COD EQUIP JUSAN], 
		CASE DATEPART(W, sub1.data) 
			WHEN 1 
				THEN 'dom' 
			WHEN 2 
				THEN '2ª f' 
			WHEN 3 
				THEN '3ª f' 
			WHEN 4 
				THEN '4ª f' 
			WHEN 5 
				THEN '5ª f' 
			WHEN 6 
				THEN '6ª f' 
			WHEN 7 
				THEN 'sáb' 
		END 
		AS [DIA SEM], 
		RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(HOUR, sub1.hora_ini)), 2) + 'h' + RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(MINUTE, sub1.hora_ini)), 2) 
		+ 'min' + ' - ' + RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(HOUR, sub1.hora_fim)), 2) + 'h' + RIGHT('0' + CONVERT(VARCHAR(2), DATEPART(MINUTE, sub1.hora_fim)), 2) 
		+ 'min' AS [HORÁRIO PROIBIDO], 
		CASE 
			WHEN sub1.id_tipo IS NULL 
				THEN 'não' 
			ELSE 'sim' 
		END AS [CADASTRO SP], 
		RIGHT('0000' + CONVERT(VARCHAR, CONVERT(INT, sub1.tempo_vermelho_detec)), 4) + 's' AS [T.DECOR.VERM], 
		RIGHT('000' + CONVERT(VARCHAR, CONVERT(INT, sub1.tolerancia_vermelho)), 3) + 's' AS [T. RETAR], 
		RIGHT('000' + CONVERT(VARCHAR, CONVERT(INT, sub1.segundos)), 3) + 's' AS [T. PERM] 
	FROM (--  DECLARE @id_infracao INT = 3
			SELECT 
				i.data, 
				v.id_classe, 
				v.porteVeiculo,
				COALESCE(cv.id_tipo, 0) AS id_tipo, 
				l.nome, 
				cep.nome_pista,
				COALESCE(v.serie_equipamento, ce.serie_equipamento) AS serie_equipamento,
				i.data_afericao AS [data_afericao], 
				COALESCE(cep.cod_pista_alternativo, i.pista) AS pista, 
				v.velocidade, 
				i.velocidade_considerada, 
				i.velocidade_limite, 
				i.id_imagem_local,
				i.id_enquadramento, 
				e.descricao, 
				COALESCE(ceri.hora_ini,  '00:00:00') AS hora_ini,
				COALESCE(ceri.hora_fim,  '23:59:00') AS hora_fim,
				ceri.tipo, 
				v.segundos, 
				i.tempo_vermelho_detec, 
				ceri.tolerancia_vermelho AS tolerancia_vermelho,
				ceri.tolerancia,
				COALESCE(v.serie_equipamento_Montante, ce.codigo_montante) AS codigo_montante
			FROM infracao i	(nolock) 
				JOIN enquadramento e (nolock) 
					ON i.id_enquadramento = e.id_enquadramento 
				JOIN veiculo  v (nolock) 
					ON i.id_veiculo = v.id_veiculo 
				LEFT JOIN cad_veiculo cv (nolock) 
					ON v.placa = cv.placa 
				--	JOIN local_vigente lv (nolock) 
				--	ON lv.id_local = i.id_local 
				LEFT JOIN local l (nolock) 
					ON	l.id_local = i.id_local 
					AND l.sequencia_local = i.sequencia_local 
				LEFT JOIN configuracao_equipamento ce (nolock) 
					ON l.id_configuracao_equipamento = ce.id_configuracao_equipamento 
				LEFT JOIN configuracao_equipamento_pista cep (nolock) 
					ON	l.id_configuracao_equipamento = cep.id_configuracao_equipamento 
					AND cep.id_pista = i.pista 
				LEFT JOIN configuracao_equipamento_afericao cea	(nolock) 
					ON	l.id_configuracao_equipamento = cea.id_configuracao_equipamento 
					AND cea.id_pista = i.pista  
				LEFT JOIN configuracao_equipamento_regra_infracao ceri (nolock) 
					ON l.id_configuracao_equipamento = ceri.id_configuracao_equipamento  
				LEFT JOIN enquadramento_regra_infracao rte (nolock) 
					ON rte.tipo = ceri.tipo 
					AND rte.id_enquadramento = i.id_enquadramento 
				--LEFT JOIN infracao_remessa ir (NOLOCK) 
				--    ON i.id_infracao = ir.id_infracao
				--LEFT JOIN remessa r (NOLOCK)
				--	ON ir.id_remessa = r.id_remessa
				--LEFT JOIN movimento_arquivo mat (NOLOCK)
				--	ON mat.id_movimento = r.codigo_externo
				--	AND mat.sequencia = ir.sequencia 
				--	AND mat.id_tipo = 4
				--	AND mat.indice_imagem = 0
				--LEFT JOIN movimento_tarja mt (NOLOCK)
				--	ON mt.id_veiculo_local = v.id_veiculo_local
				--	AND mt.id_enquadramento = i.id_enquadramento
				--	AND mt.data_infracao = i.data
				--LEFT JOIN movimento_importacao mi (NOLOCK)
				--	ON mi.id_movimento = r.codigo_externo
				--	AND mi.id_enquadramento = r.id_enquadramento
				--	AND mi.sequencia = ir.sequencia
			WHERE i.id_infracao = @id_infracao 
		) AS sub1 
)
