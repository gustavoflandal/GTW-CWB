CREATE FUNCTION [muralha].[fcn_ObterDadosAlertaOcorrencia](@idAlerta UNIQUEIDENTIFIER)
RETURNS TABLE
AS
	RETURN
	(
		--DECLARE @idAlerta UNIQUEIDENTIFIER = '7d2b2c9d-6131-42bf-8175-6afd81a3d6ea' --'5b9383e2-7c5d-4cdf-a2f4-c231cea6b7c4' --509EF7AA-138A-4A82-AE0A-1221AFF17AEF
		SELECT a.id,
			   a.id_tipo_alerta_ocorrencia,
			   tao.tipo AS tipo_alerta_ocorrencia,
			   a.id_cad_veiculo_monitorado,
			   cvm.placa AS placa_cadastro,
			   cvm.privado AS cvm_privado,
			   su.id_usuario,
			   sa.descricao AS status_alerta,
			   a.data AS data_alerta,
			   vtr.id AS id_veiculo_tempo_real,
			   vtr.placa AS placa_veiculo,
			   vtr.data AS data_veiculo,
			   lv.id_local,
			   lv.serie_equipamento,
			   RTRIM(lv.nome) AS equipamento,
			   vtr.id_pista,
			   vtr.velocidade,
			   lv.posicao_lat AS latitude,
			   lv.posicao_lon AS longitude,
			   md.id AS id_motivo_descarte,
			   RTRIM(md.descricao) AS motivo_descarte,
			   RTRIM(a.observacao) AS observacao,
			   CASE WHEN a.id_motivo_descarte IS NOT NULL THEN 1 ELSE 0 END AS descartado,
			   o.id AS id_ocorrencia,
			   CASE WHEN o.id IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_gerada,
			   CASE WHEN ocn.id_ocorrencia IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_com_notificacao,
			   CASE WHEN o.id IS NOT NULL THEN tr_o.id ELSE tr_a.id END AS id_tipo_registro,
			   RTRIM(CASE WHEN o.id IS NOT NULL THEN tr_o.descricao ELSE tr_a.descricao END) AS tipo_registro,
			   o.id_status_ocorrencia,
			   os.descricao AS status_ocorrencia,
			   CASE WHEN sfo.id_status IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_finalizada,
			   o.observacao AS obs_finalizar_ocorrencia,
			   a.id_ponto_interesse,
			   pt.nome AS nome_ponto_interesse,
			   a.alerta_vinculado,
			   a.id_alerta_vinculado
		FROM   muralha.alerta a (NOLOCK) 
			   INNER JOIN muralha.tipo_alerta_ocorrencia tao (NOLOCK) 
		  			ON  tao.id = a.id_tipo_alerta_ocorrencia 
			   INNER JOIN muralha.status_alerta sa (NOLOCK) 
		  			ON  sa.id = a.id_status_alerta 
			   INNER JOIN muralha.alerta_veiculo av 
		  			ON  av.id_alerta = a.id 
		  				--AND av.id_veiculo_tempo_real = ( 
		  				--	SELECT TOP 1 av2.id_veiculo_tempo_real
		  				--	FROM   muralha.alerta_veiculo av2 
		  				--	WHERE  av2.id_alerta = a.id 
		  				--) 
			   INNER JOIN muralha.veiculo_tempo_real vtr 
		  			ON  vtr.id = av.id_veiculo_tempo_real 
			   INNER JOIN local_vigente lv 
		  			ON  lv.id_local = vtr.id_local 
			   LEFT JOIN muralha.cad_veiculo_monitorado cvm (NOLOCK) 
		  			ON  cvm.id = a.id_cad_veiculo_monitorado 
			   LEFT JOIN muralha.motivo_descarte md 
					ON  md.id = a.id_motivo_descarte 
			   LEFT JOIN sis_usuario su 
		 	 		ON  su.id_usuario = a.id_usuario 
			   LEFT JOIN muralha.ocorrencia o 
					ON  o.id_alerta = a.id 
			   LEFT JOIN muralha.status_ocorrencia os
					ON  os.id = o.id_status_ocorrencia
			   LEFT JOIN (
							SELECT s.id_status,
								   s.descricao,
								   t.id_tipo_ocorrencia
							FROM   muralha.v_status_ocorrencia_finalizacao s
								   JOIN muralha.tipo_ocorrencia_status t
										ON  t.id_status_ocorrencia = s.id_status
			   ) sfo
					ON  sfo.id_tipo_ocorrencia = o.id_tipo_alerta_ocorrencia
						AND sfo.id_status = o.id_status_ocorrencia
			   LEFT JOIN (
							SELECT id_ocorrencia
							FROM   muralha.ocorrencia_notificacao
							GROUP BY
								   id_ocorrencia
			   ) ocn
					ON  ocn.id_ocorrencia = o.id
			   JOIN muralha.tipo_registro tr_a
					ON  tr_a.id = 'E7D115B9-E6B3-4E86-9083-F347A1917045'
			   JOIN muralha.tipo_registro tr_o
					ON  tr_o.id = '5511CEF5-C1A0-450B-99B3-6FCA8668D243'
			   LEFT JOIN muralha.ponto_interesse pt
					ON  pt.id = a.id_ponto_interesse
		WHERE  a.id = @idAlerta
		--ORDER BY vtr.data
	)
