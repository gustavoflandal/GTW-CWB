CREATE VIEW [dbo].[sis_usuario_eventos] AS  
  
SELECT ROW_NUMBER() OVER(ORDER BY sub1.usuario ASC) AS id_usuario, LTRIM(RTRIM(sub1.usuario)) AS usuario FROM  
(SELECT ev.usuario FROM eventos_csx_usuarios ev (NOLOCK) GROUP BY ev.usuario) AS sub1   
WHERE LEN(sub1.usuario) > 0  
  
--SELECT id_usuario, usuario FROM sis_usuario WHERE 1 = 2  
