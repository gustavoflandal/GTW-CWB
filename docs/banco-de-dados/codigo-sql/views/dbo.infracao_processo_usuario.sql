
CREATE VIEW [dbo].[infracao_processo_usuario]
AS
SELECT 
	id_infracao,
	id_processo,
	id_usuario, 
	max(id_infracao_processo) as id_infracao_processo
FROM infracao_processo (nolock)
WHERE status_processo = 0
GROUP BY id_infracao, id_processo, id_usuario


