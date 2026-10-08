CREATE PROCEDURE [muralha].[spu_obterListaVideosMonitoramentoExibicao]
	@data_ini DATETIME,
	@data_fim DATETIME,
	@id_local INT
AS
	--DECLARE @data_ini DATETIME = '2022-10-25 14:30:00', @data_fim DATETIME = '2022-10-25 14:32:59', @id_local INT = 1
	DECLARE @periodo_horas INT = 4

	SET NOCOUNT ON

	DECLARE @temp AS TABLE (id UNIQUEIDENTIFIER, id_local INT, serie_equipamento INT, ip_camera VARCHAR(20), data_hora DATETIME, endereco VARCHAR(300), tipo_camera VARCHAR(30))
	INSERT INTO @temp
	--DECLARE @data_ini DATETIME = '2022-10-25 14:30:00', @data_fim DATETIME = '2022-10-25 14:32:59', @id_local INT = 1
	SELECT vm.id,
		   vm.id_local,
		   lv.serie_equipamento,
		   vm.ip_camera,
		   vm.data_hora,
		   RTRIM(vm.endereco) AS endereco,
		   vec.tipo_camera
	FROM   muralha.video_monitoramento vm
		   JOIN local_vigente lv
				ON  lv.id_local = vm.id_local
		   LEFT JOIN v_equipamento_cameras vec
				ON  vec.id_local = vm.id_local
					AND vm.ip_camera = vec.ip_camera
	WHERE  vm.data_hora BETWEEN @data_ini AND @data_fim
		   AND vm.id_local = @id_local

	SELECT t.id_local,
		   t.serie_equipamento,
		   t.ip_camera,
		   t.tipo_camera,
		   STUFF((SELECT ';' + RTRIM(CAST(sub.id AS VARCHAR(100))) AS [text()]
				  FROM   @temp sub
				  WHERE  sub.ip_camera = t.ip_camera
				  ORDER BY
						 sub.data_hora
				  FOR XML PATH('')
		   ), 1, 1, '' ) AS lista_id_videos,
		   STUFF((SELECT ';' + RTRIM(CAST(sub.endereco AS VARCHAR(300))) AS [text()]
				  FROM   @temp sub
				  WHERE  sub.ip_camera = t.ip_camera
				  ORDER BY
						 sub.data_hora
				  FOR XML PATH('')
		   ), 1, 1, '' ) AS lista_endereco_videos
	FROM   @temp t
	GROUP BY
		   t.id_local,
		   t.serie_equipamento,
		   t.ip_camera,
		   t.tipo_camera
	ORDER BY
		   t.id_local,
		   t.ip_camera
