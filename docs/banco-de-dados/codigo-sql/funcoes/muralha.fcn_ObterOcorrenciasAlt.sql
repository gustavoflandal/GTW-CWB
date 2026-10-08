CREATE   FUNCTION [muralha].[fcn_ObterOcorrenciasAlt]()
RETURNS TABLE
AS
	RETURN
	(
		SELECT o.id,
			   tp.id AS id_tipo_alerta_ocorrencia,
			   RTRIM(tp.tipo) AS tipo_alerta_ocorrencia,
			   cvm.id AS id_cad_veiculo_monitorado,
			   lv.id_local,
			   s.id AS id_status,
			   RTRIM(s.descricao) AS status,
			   o.data,
			   cvm.placa,
			   vtr.placa AS placa_lida,
			   md.id AS id_motivo_descarte,
			   RTRIM(md.descricao) AS motivo_descarte,
			   su.id_usuario,
			   RTRIM(su.usuario) AS usuario,
			   reg.id AS id_tipo_registro,
			   RTRIM(reg.descricao) AS tipo_registro,
			   a.id AS id_alerta
		--SELECT *
		FROM   muralha.ocorrencia o
			   INNER JOIN muralha.alerta a
					ON  a.id = o.id_alerta
			   INNER JOIN muralha.tipo_alerta_ocorrencia tp
					ON  tp.id = o.id_tipo_alerta_ocorrencia
			   INNER JOIN muralha.status_ocorrencia s
					ON  s.id = o.id_status_ocorrencia
			   INNER JOIN muralha.alerta_veiculo av
					ON  av.id_alerta = a.id
		  				--AND av.id_veiculo_tempo_real = (
		  				--		SELECT TOP 1 av2.id_veiculo_tempo_real
		  				--		FROM   muralha.alerta_veiculo av2
		  				--		WHERE  av2.id_alerta = a.id
		  				--) 
			   INNER JOIN muralha.veiculo_tempo_real vtr
					ON  vtr.id = av.id_veiculo_tempo_real
			   INNER JOIN local_vigente lv
					ON  lv.id_local = vtr.id_local
			   INNER JOIN muralha.tipo_registro reg
					ON  reg.id = '5511CEF5-C1A0-450B-99B3-6FCA8668D243'
			   LEFT JOIN muralha.cad_veiculo_monitorado cvm
					ON  cvm.id = a.id_cad_veiculo_monitorado
			   LEFT JOIN muralha.motivo_descarte md
					ON  md.id = a.id_motivo_descarte
			   LEFT JOIN sis_usuario su
					ON  su.id_usuario = a.id_usuario
		--WHERE  a.enviado_cliente = 1
	)
