CREATE   VIEW muralha.vw_resumo_correlacionamento_placas 
	AS
	SELECT	
		acrf.id_correlacionamento, 
		cp.placa_base, 
		cp.placa_correlacionada,
		count(*) as total_passagens
	FROM muralha.correlacionamento_placas cp
	JOIN muralha.analise_correlacionamento_placas_registro_fato acrf
		ON acrf.id_correlacionamento = cp.id
	--WHERE acrf.data_inicio_analise >= DATEADD(DAY, -30, GETDATE())
	GROUP BY
		acrf.id_correlacionamento, 
		cp.placa_base, 
		cp.placa_correlacionada