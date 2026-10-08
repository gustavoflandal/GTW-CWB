CREATE PROCEDURE [muralha].[spu_obterCamerasMonitoramentoAoVivo]
AS
	SELECT lv.id_local,
		   lv.serie_equipamento,
		   CAST(lv.nome AS VARCHAR(100)) AS nome,
		   cmc.ip,
		   cmc.ip_local,
		   cmc.url_stream,
		   cmc.descricao_camera
	FROM   muralha.config_monitoramento_ao_vivo_cameras cmc
		   JOIN local_vigente lv
				ON  lv.id_local = cmc.id_local
	WHERE  lv.desativado = 0
		   AND cmc.ativo = 1
	GROUP BY
		   lv.id_local,
		   lv.serie_equipamento,
		   lv.nome,
		   cmc.ip,
		   cmc.ip_local,
		   cmc.url_stream,
		   cmc.descricao_camera
	ORDER BY
		   lv.id_local,
		   cmc.ip_local
