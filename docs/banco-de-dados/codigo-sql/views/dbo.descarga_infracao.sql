
CREATE VIEW [dbo].[descarga_infracao] AS
SELECT 
	i.id_infracao,
	i.velocidade_limite,
	ir.serie AS serie_auto,
	i.id_processo_concluido,
	i.placa AS placa_digitada,
	i.id_processo,
	ir.auto AS numero_auto,
	i.tempo_vermelho_detec,
	i.segundos_tolerancia,
	i.id_imagem_local,
	i.velocidade_considerada,
	i.id_usuario_final,
	i.id_veiculo,
	i.id_enquadramento,
	i.id_inconsistencia,
	r.tipo AS tipo_remessa,
	(CASE WHEN ipcl.id_inconsistencia IS NULL THEN NULL
	     WHEN ipcl.id_inconsistencia = 0 THEN 'S'
	     ELSE 'N' END) AS eh_consistente,
	(CASE WHEN i.id_inconsistencia IS NULL THEN NULL
	     WHEN i.id_inconsistencia = 0 THEN 'S'
	     ELSE 'N' END) AS eh_valida,
    ii.id_imagem_obj
FROM veiculo_descarga d (nolock)
	INNER JOIN infracao i (nolock)
		ON i.id_veiculo = d.id_veiculo
	LEFT JOIN infracao_remessa ir (nolock)
		ON ir.id_infracao = i.id_infracao
	LEFT JOIN remessa r (nolock)
		ON r.id_remessa = ir.id_remessa
	LEFT JOIN infracao_imagem ii (nolock)
		ON ii.id_infracao = i.id_infracao
	LEFT JOIN infracao_processo_concluido ipcl 
		ON ipcl.id_infracao = i.id_infracao AND ipcl.id_processo = 11


