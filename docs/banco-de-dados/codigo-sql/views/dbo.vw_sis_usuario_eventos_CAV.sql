CREATE VIEW [dbo].[vw_sis_usuario_eventos_CAV]
AS
	SELECT ROW_NUMBER() OVER(ORDER BY sub1.usuario ASC) AS id_usuario
		  ,sub1.usuario
	FROM  (
				SELECT LTRIM(RTRIM(ev.usuario)) AS usuario
				FROM   eventos_csx_CAV ev (NOLOCK)
				GROUP BY
					   LTRIM(RTRIM(ev.usuario))
		  ) AS sub1
	WHERE LEN(sub1.usuario) > 0
