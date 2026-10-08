CREATE VIEW [dbo].[infracao_completa]  
AS  

	SELECT i.id_infracao,
		   i.id_local,
		   i.sequencia_local,
		   --COALESCE(pt.cod_pista_prodam, mi.cod_pista_prodam) AS cod_pista_prodam,
		   pt.cod_pista_prodam, -- Não faz sentido usar movimento_importacao no CAI
		   --COALESCE(ce.id_produto,
		   -- CASE (7806 / 100) % 10
		   --  WHEN 0 THEN 1
		   --  WHEN 1 THEN 1
		   --  WHEN 2 THEN 1
		   --  WHEN 8 THEN 1
		   --  WHEN 7 THEN 3
		   --  ELSE 2 END) AS tipo_equipamento,
		   ce.id_produto AS tipo_equipamento,
		   i.id_imagem_local,
		   i.id_inconsistencia,
		   i.pista,
		   --COALESCE(NULLIF(RTRIM(pt.nome_pista),''), l.nome, mi.[descricao_local]) AS nome_pista,
		   RTRIM(COALESCE(pt.nome_pista, l.nome)) AS nome_pista,
		   i.id_processo,
		   p.nome AS nome_processo,
		   i.id_usuario_atual,
		   u.usuario AS usuario_atual,
		   --i.placa,
		   v.placa,
		   i.data AS data_veiculo,
		   i.id_enquadramento,
		   v.velocidade AS velocidade_veiculo,
		   i.velocidade_limite,
		   v.segundos AS segundos_veiculo,
		   i.segundos_tolerancia,
		   im.id_imagem_pan,
		   v.comprimento as comprimento_veiculo,
		   im.id_imagem_obj,
		   ir.auto,
		   ir.serie,
		   r.tipo AS tipo_remessa,
		   i.espera,
		   r.id_remessa,
		   (
				SELECT COALESCE(MAX(id_infracao_processo), 0) AS id_infracao_processo  
				FROM   dbo.infracao_processo_usuario AS ip (NOLOCK)  
				WHERE  (id_infracao = i.id_infracao)  
				       AND (id_usuario = i.id_usuario_atual)  
				       AND (id_processo = i.id_processo)
		   ) AS id_infracao_processo,
		   v.id_classe,
		   aproveitavel = CASE WHEN i.id_inconsistencia = 0 THEN 1 ELSE 0 END,
		   validavel = CASE WHEN i.id_processo = 3 THEN 1 ELSE 0 END,
		   COALESCE(mi.validacao, 0) AS valida,
		   --valida = CASE WHEN (
		   --  SELECT TOP(1) id_inconsistencia
		   --  FROM infracao_processo (NOLOCK)
		   --  WHERE id_processo = 3 AND id_infracao = i.id_infracao
		   --  ORDER BY id_infracao_processo DESC
		   -- ) = 0 THEN 1 ELSE 0 END,
		   v.id_veiculo,
		   equipamento_captura_frontal = CASE WHEN ce.flag_opcao & 128 > 0 THEN 1 ELSE 0 END,
		   --(
		   --         EXISTS (SELECT c.id_configuracao_equipamento
		   --           FROM veiculo v (NOLOCK)
		   --            INNER JOIN local l (NOLOCK)
		   --             ON l.id_local = i.id_local AND l.sequencia_local = i.sequencia_local
		   --            INNER JOIN configuracao_equipamento c (NOLOCK)
		   --             ON c.id_configuracao_equipamento = l.id_configuracao_equipamento
		   --           WHERE id_veiculo = i.id_veiculo
		   --            AND (c.flag_opcao & 128) > 0)) THEN 1
		   --         ELSE 0 END,
		   equipamento_captura_traseira = CASE WHEN ce.flag_opcao & 256 > 0 THEN 1 ELSE 0 END,
		   --(
		   --        EXISTS (SELECT c.id_configuracao_equipamento
		   --          FROM veiculo v (NOLOCK)
		   --           JOIN local l (NOLOCK)
		   --            ON l.id_local = i.id_local AND l.sequencia_local = i.sequencia_local
		   --           JOIN configuracao_equipamento c (NOLOCK)
		   --            ON c.id_configuracao_equipamento = l.id_configuracao_equipamento
		   --          WHERE id_veiculo = i.id_veiculo
		   --           AND (c.flag_opcao & 256) > 0)) THEN 1
		   --        ELSE 0 END,
		   pt.cod_area AS area_pista,
		   v.codigo_prodam, 
		   --veiculo_estatistica.velocidade AS velocidade_ponto_A,
		   0 AS velocidade_ponto_A, -- Não existe no contrato C011
		   v.velocidade AS velocidade_ponto_B,
		   v.velocidade_media AS velocidade_media,
		   i.tempo_vermelho_detec,
		   COALESCE(v.porteVeiculo, '') AS porteVeiculo,
		   i.data_entrada,
		   i.velocidade_considerada,
		   NULL AS todas_classificacao_qfv,
		   CASE WHEN vp.id_veiculo_unic IS NOT NULL THEN 1 ELSE 0 END AS com_pesagem
	FROM   infracao AS i (NOLOCK)
		   INNER JOIN dbo.veiculo AS v (NOLOCK)
				ON  v.id_veiculo = i.id_veiculo
		  -- LEFT JOIN dbo.veiculo_estatistica veiculo_estatistica (NOLOCK)
				--ON  veiculo_estatistica.id_veiculo_local = v.id_veiculo_Local_Montante
				--	AND veiculo_estatistica.id_local = v.serie_equipamento_Montante
				--	AND CAST(veiculo_estatistica.data AS DATE) = CAST(v.data AS DATE)
		   LEFT JOIN dbo.local AS l (NOLOCK)
				ON  v.id_local = l.id_local  
					AND v.sequencia_local = l.sequencia_local  
		   LEFT JOIN dbo.configuracao_equipamento AS ce (NOLOCK)
				ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento
		   LEFT JOIN dbo.configuracao_equipamento_pista AS pt (NOLOCK)
				ON  pt.id_configuracao_equipamento = l.id_configuracao_equipamento
					AND pt.id_pista = i.pista
		   LEFT JOIN dbo.processo AS p (NOLOCK)
				ON  p.id_processo = i.id_processo
		   LEFT JOIN dbo.sis_usuario AS u (NOLOCK)
				ON  u.id_usuario = i.id_usuario_atual
		   LEFT JOIN dbo.infracao_imagem AS im (NOLOCK)
				ON  im.id_infracao = i.id_infracao
		   LEFT JOIN dbo.infracao_remessa AS ir (NOLOCK)
				ON  ir.id_infracao = i.id_infracao
		   LEFT JOIN dbo.remessa AS r (NOLOCK)
				ON  r.id_remessa = ir.id_remessa
		   LEFT JOIN dbo.movimento_importacao AS mi (NOLOCK)
				ON  r.codigo_externo = mi.id_movimento
					AND i.id_enquadramento = mi.id_enquadramento
					AND ir.sequencia = mi.sequencia
		   LEFT JOIN veiculo_pesagem vp (NOLOCK)
				ON  vp.id_veiculo_unic = v.id_veiculo_unic
					AND vp.pbt > 0
					AND vp.pesagem_valida = 1
	WHERE  COALESCE(i.id_processo,0) != 99 
		   --AND v.data >= '2025-10-13 13:00:00:00'
