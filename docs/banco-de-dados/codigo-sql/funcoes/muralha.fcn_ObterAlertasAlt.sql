CREATE FUNCTION [muralha].[fcn_ObterAlertasAlt]()
RETURNS TABLE
AS
	RETURN
	(
		SELECT a.id,
			   tp.id AS id_tipo_alerta_ocorrencia,
			   RTRIM(tp.tipo) AS tipo_alerta_ocorrencia,
			   cvm.id AS id_cad_veiculo_monitorado,
			   cvm.privado AS cvm_privado,
			   lv.id_local,
			   RTRIM(lv.nome) AS nome_local,
			   CASE WHEN lv.codigos_equipamentos IS NOT NULL AND lv.codigos_equipamentos != '' THEN RTRIM(lv.codigos_equipamentos) + ' - ' ELSE '' END + RTRIM(CAST(lv.serie_equipamento AS VARCHAR(20))) + ' - ' + RTRIM(lv.nome) AS equipamento,
			   s.id AS id_status,
			   RTRIM(s.descricao) AS status,
			   a.data,
			   cvm.placa,
			   vtr.placa AS placa_lida,
			   md.id AS id_motivo_descarte,
			   RTRIM(md.descricao) AS motivo_descarte,
			   su.id_usuario,
			   RTRIM(su.usuario) AS usuario,
			   reg.id AS id_tipo_registro,
			   RTRIM(reg.descricao) AS tipo_registro,
			   a.id AS id_alerta,
			   CASE WHEN a.id_motivo_descarte IS NOT NULL THEN 1 ELSE 0 END AS descartado,
			   CASE WHEN o.id IS NOT NULL THEN 1 ELSE 0 END AS ocorrencia_gerada,
			   a.alerta_vinculado,
			   vtr.id AS id_veiculo_tempo_real,
			   cvm.nome AS nome_cad_monitorado,
			   CASE WHEN cvm.data_fim IS NOT NULL AND cvm.data_fim <= CAST(GETDATE() AS DATE) AND cvm.data_inativacao IS NOT NULL THEN 0 ELSE 1 END AS cad_monitorado_ativo
		FROM   muralha.alerta a
			   INNER JOIN muralha.tipo_alerta_ocorrencia tp
					ON  tp.id = a.id_tipo_alerta_ocorrencia
			   INNER JOIN muralha.status_alerta s
					ON  s.id = a.id_status_alerta
			   INNER JOIN muralha.alerta_veiculo av
					ON  av.id_alerta = a.id
			   INNER JOIN muralha.veiculo_tempo_real vtr
					ON  vtr.id = av.id_veiculo_tempo_real
			   INNER JOIN local_vigente lv
					ON  lv.id_local = vtr.id_local
			   INNER JOIN muralha.tipo_registro reg
					ON  reg.id = 'E7D115B9-E6B3-4E86-9083-F347A1917045'
			   LEFT JOIN muralha.ocorrencia o
					ON  o.id_alerta = a.id
			   LEFT JOIN muralha.cad_veiculo_monitorado cvm
					ON  cvm.id = a.id_cad_veiculo_monitorado
			   LEFT JOIN muralha.motivo_descarte md
					ON  md.id = a.id_motivo_descarte
			   LEFT JOIN sis_usuario su
					ON  su.id_usuario = a.id_usuario
		--WHERE  a.enviado_cliente = 1
	)
