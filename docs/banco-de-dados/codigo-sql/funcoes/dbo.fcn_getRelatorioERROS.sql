CREATE FUNCTION [dbo].[fcn_getRelatorioERROS](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
( 

/*
	SELECT TOP 100 PERCENT
		   rel.mensagem AS ' '
	FROM   (
				SELECT 1 AS ordem, 'Relatório em ajuste...' AS mensagem
				UNION
				SELECT 2 AS ordem, 'Utilizar o relatório de medição Erros de Validação.' AS mensagem
				UNION
				SELECT 3 AS ordem, 'Em caso de dúvidas, contate o administrador do sistema.' AS mensagem
	) rel
	ORDER BY
		   rel.ordem
*/	

	--DECLARE @dataInicio DATE = '2016-09-01', @dataFim DATE = '2016-09-02'
	SELECT id_local
		  ,serie_equipamento
		  ,nome
		  ,sub1.id_infracao
		  ,id_enquadramento
		  ,data
		  ,hora
		  ,tipo
		  ,codigo_externo
		  ,placa_processo
		  ,inconsistencia_processo
		  ,inconsistencia_validacao
		  ,usuario_CAI
		  ,codigo_agente
		  ,agente
		  ,considerar
	FROM (
			SELECT l.id_local
				  ,ce.serie_equipamento
				  ,l.nome
				  ,i.id_infracao
				  ,i.id_enquadramento
				  ,i.data
				  ,convert(VARCHAR(8), i.data, 114) AS hora
				  ,r.tipo
				  ,r.codigo_externo
				  ,COALESCE(ipd.placa, i.placa, v.placa, 'N/D') AS placa_processo
				  ,mi.placa AS placa_validacaco
				  ,inc_p.descricao AS inconsistencia_processo
				  ,inc_v.descricao AS inconsistencia_validacao
				  ,mi.codigo_agente
				  ,COALESCE(su.nome, 'N/D') AS agente
				  ,'' AS considerar
			FROM   infracao i (NOLOCK)
				   INNER JOIN infracao_remessa ir (NOLOCK)
						ON  i.id_infracao = ir.id_infracao
				   INNER JOIN remessa r (NOLOCK)
						ON  ir.id_remessa = r.id_remessa
				   INNER JOIN local l (NOLOCK)
						ON  i.id_local = l.id_local
							AND i.sequencia_local = l.sequencia_local
				   INNER JOIN configuracao_equipamento ce (NOLOCK)
						ON  l.id_configuracao_equipamento = ce.id_configuracao_equipamento
				   INNER JOIN veiculo v (NOLOCK)
						ON  i.id_veiculo = v.id_veiculo
				   INNER JOIN movimento_importacao mi (NOLOCK)
						ON  r.id_movimento_arquivo = mi.id_movimento_arquivo
							AND i.id_enquadramento = mi.id_enquadramento
							AND ir.sequencia = mi.sequencia

					-- SELECT * FROM processo WHERE id_processo IN (1,2,21,24) 

				   INNER JOIN (
								   SELECT ipx.id_infracao
										 ,MAX(ipx.id_infracao_processo) AS id_infracao_processo 
								   FROM   infracao_processo ipx (NOLOCK) 
								   WHERE  ipx.id_processo IN (1,2,21,24)
								   GROUP BY
										  ipx.id_infracao 
				   ) AS sub1
						ON  i.id_infracao = sub1.id_infracao

				   INNER JOIN infracao_processo ip (NOLOCK)
						ON ip.id_infracao_processo  = sub1.id_infracao_processo
				   LEFT JOIN infracao_processo_digitacao ipd (NOLOCK)
						ON ipd.id_infracao_processo = sub1.id_infracao_processo
				   INNER JOIN inconsistencia inc_p (NOLOCK)
						ON  ip.id_inconsistencia = inc_p.id_inconsistencia
				   INNER JOIN inconsistencia inc_v (NOLOCK)
						ON  mi.id_inconsistencia = inc_v.id_inconsistencia
				   LEFT JOIN sis_usuario su (NOLOCK)
						ON  su.cod_agente = mi.codigo_agente
				  -- LEFT JOIN veiculo_imagem vi (NOLOCK)
						--ON  vi.id_veiculo = i.id_veiculo
				  -- LEFT JOIN imagem img (NOLOCK)
						--ON  img.id_imagem = vi.id_imagem
			WHERE  CAST(i.data AS DATE) BETWEEN @dataInicio AND @dataFim 
				 --  AND (
					--		(ip.id_inconsistencia > 0 AND mi.validacao = 1)
					--	OR
					--		(ip.id_inconsistencia = 0 AND mi.validacao = 0)
					--	OR
					--		(i.placa IS NOT NULL AND i.placa <> mi.placa)
					--	OR 
					--		(ipd.placa IS NOT NULL AND ipd.placa <> mi.placa)
					--)

					AND (
							(
								(i.id_inconsistencia = 0 AND mi.id_inconsistencia <> 0)
								OR (mi.id_inconsistencia = 0 AND i.id_inconsistencia <> 0)
							)
							OR (
									mi.placa <> REPLICATE(' ', 7)
									AND i.placa IS NOT NULL 
									AND mi.placa <> i.placa
								)
							--OR (
							--		ipd.id_marca_cet IS NOT NULL
							--		AND mi.id_marca_cet <> ipd.id_marca_cet
							--	)
						)
			GROUP BY
				   l.id_local
				  ,ce.serie_equipamento
				  ,l.nome
				  ,i.id_infracao
				  ,i.id_enquadramento
				  ,i.data
				  ,r.tipo
				  ,r.codigo_externo
				  ,ipd.placa
				  ,i.placa
				  ,v.placa
				  ,mi.placa
				  ,inc_p.descricao
				  ,inc_v.descricao
				  ,mi.codigo_agente
				  ,su.nome

		) AS sub1
		JOIN (
				SELECT i.id_infracao
					  ,su_cai.nome AS usuario_CAI
				FROM   infracao i (NOLOCK)
					   INNER JOIN (
									SELECT ipx.id_infracao
										  ,MAX(ipx.id_infracao_processo) AS id_infracao_processo 
									FROM   infracao_processo ipx (NOLOCK) 
									WHERE  ipx.id_processo IN (1,2,21,24)
										   AND ipx.id_usuario <> 2
									GROUP BY
										   ipx.id_infracao
					   ) AS sub1
							ON  i.id_infracao = sub1.id_infracao
					   INNER JOIN infracao_processo ip (NOLOCK)
							ON  ip.id_infracao_processo = sub1.id_infracao_processo
					   INNER JOIN sis_usuario su_cai (NOLOCK)
							ON  ip.id_usuario = su_cai.id_usuario
		) AS sub2 
			ON  sub1.id_infracao = sub2.id_infracao

)
