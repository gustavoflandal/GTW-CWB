CREATE PROCEDURE [muralha].[spu_getRelExtratoAlertaOcorrenciaPorEquipamento]
	@dataInicio DATETIME,
	@dataFim DATETIME
AS

	--SELECT * FROM muralha.status_alerta
	--DECLARE @dataInicio DATETIME = '2022-01-01 00:00:00', @dataFim DATETIME = '2022-01-31 23:59:59'

	--> Extrato de alertas e processamento
	SELECT r.equipamento AS [Equipamento],
		   r.total_alertas_periodo AS [Total de Alertas do Período],
		   r.total_alertas_pendentes AS [Total de Alertas Pendentes],
		   CASE WHEN r.total_alertas_periodo > 0 THEN CAST(r.total_alertas_pendentes AS FLOAT) / CAST(r.total_alertas_periodo AS FLOAT) ELSE NULL END AS [% de Alertas Pendentes],
		   r.total_alertas_processados AS [Total de Alertas Processados],
		   CASE WHEN r.total_alertas_periodo > 0 THEN CAST(r.total_alertas_processados AS FLOAT) / CAST(r.total_alertas_periodo AS FLOAT) ELSE NULL END AS [% de Alertas Processados],
		   r.total_ocorrencias_geradas AS [Total de Ocorrências Geradas],
		   CASE WHEN r.total_alertas_processados > 0 THEN CAST(r.total_ocorrencias_geradas AS FLOAT) / CAST(r.total_alertas_processados AS FLOAT) ELSE NULL END AS [% de Ocorrências Geradas],
		   r.total_alertas_descartados AS [Total de Alertas Descartados],
		   CASE WHEN r.total_alertas_processados > 0 THEN CAST(r.total_alertas_descartados AS FLOAT) / CAST(r.total_alertas_processados AS FLOAT) ELSE NULL END AS [% de Alertas Descartados]
	FROM   (
				SELECT RTRIM(CAST(lv.serie_equipamento AS VARCHAR)) + ' - ' + RTRIM(lv.nome) AS equipamento,
					   COUNT(*) AS total_alertas_periodo,
					   SUM(CASE WHEN a.id_status_alerta = '5479C6D9-7381-4492-99BE-442EF2E741B0' THEN 1 ELSE 0 END) AS total_alertas_pendentes,
					   SUM(CASE WHEN a.id_status_alerta != '5479C6D9-7381-4492-99BE-442EF2E741B0' THEN 1 ELSE 0 END) AS total_alertas_processados,
					   SUM(CASE WHEN a.id_status_alerta = '15EBBA5F-C805-449E-83CC-227ED3B3AD3C' THEN 1 ELSE 0 END) AS total_ocorrencias_geradas,
					   SUM(CASE WHEN a.id_status_alerta = '298F5A62-C799-4CA9-8220-6B08F8664534' THEN 1 ELSE 0 END) AS total_alertas_descartados
				FROM   muralha.alerta a (NOLOCK)
					   JOIN muralha.tipo_alerta_ocorrencia tao
							ON  tao.id = a.id_tipo_alerta_ocorrencia
					   JOIN (
								SELECT av.id_alerta,
									   MIN(av.id_veiculo_tempo_real) AS id_veiculo_tempo_real,
									   MIN(vtr.data) AS data_veiculo,
									   MIN(vtr.id_local) AS id_local
								FROM   muralha.alerta_veiculo av
									   JOIN muralha.veiculo_tempo_real vtr
											ON  vtr.id = av.id_veiculo_tempo_real
								GROUP BY
									   av.id_alerta
					   ) AS l
							ON  l.id_alerta = a.id
					   JOIN local_vigente lv
							ON  lv.id_local = l.id_local
				WHERE  a.enviado_cliente = 1
					   AND a.data BETWEEN @dataInicio AND @dataFim
				GROUP BY
					   lv.serie_equipamento,
					   lv.nome
		   ) AS r
	ORDER BY
		   r.equipamento
