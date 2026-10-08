
CREATE VIEW [dbo].[local_status_tempo_real] 
AS
SELECT	
	l.id_local,
	l.serie_equipamento,
	l.nome,
	ls.status_conexao,
	ls.status_energia,
	ls.status_div,
	ls.ip,
	lstr.ultima_deteccao,
	lstr.versao,
	CASE WHEN status_conexao = 1 THEN dbo.TEMPO_DECORRIDO(ls.data_atualizacao_conexao) ELSE '' END AS tempo_conectado,
	CASE WHEN status_conexao = 2 THEN dbo.TEMPO_DECORRIDO(ls.data_atualizacao_conexao) ELSE '' END AS tempo_desconectado,
	lstr.tempo_executando,
	CAST(lstr.status_copia AS VARCHAR(30)) AS status_copia,
	(CASE WHEN vm.num_veic_detectados > 0 THEN 't' ELSE 'f' END ) AS veiculo_irregular, 
	l.posicao_lat,
	l.posicao_lon,
	vmd.data_atualizacao as data_atualizacao_veiculo_monitorado,
	ls.data_atualizacao as data_atualizacao_status,
	dbo.MAX_DATE( vmd.data_atualizacao , ls.data_atualizacao) as [data_atualizacao],
	c.data_inicio as data_inicio_operacao
FROM local_vigente l (nolock)
	LEFT JOIN (	SELECT id_local, COUNT(*) as [num_veic_detectados]
				FROM (	SELECT TOP 20 
							id_local, falsoPositivo, data
						FROM veiculo_monitorado (nolock) 
						ORDER BY data DESC ) AS [vm_20]
				WHERE --data >= GETDATE() - CONVERT(TIME, '00:30:00') and 
					falsoPositivo IS NULL
				GROUP BY id_local
				) AS vm 
		ON vm.id_local = l.id_local
	LEFT JOIN (SELECT id_local, MAX(dataAtualizacao) as data_atualizacao
					FROM veiculo_monitorado (nolock)
					--WHERE data >= GETDATE() - CONVERT(TIME, '00:30:00')
					GROUP BY id_local
				) AS vmd 
		ON vmd.id_local = l.id_local
	INNER JOIN local_status ls (nolock) 
		ON (l.serie_equipamento = ls.serie_equipamento)
	LEFT JOIN status_tempo_real lstr (nolock) 
		ON (lstr.serie_equipamento = ls.serie_equipamento)
	INNER JOIN configuracao_equipamento c (nolock) 
		ON	(c.serie_equipamento = l.serie_equipamento 
		AND l.id_configuracao_equipamento = c.id_configuracao_equipamento)
WHERE l.desativado = 0


