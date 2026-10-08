
CREATE VIEW [dbo].[local_status_conexao] 
AS
SELECT
	-- ********************************************************** 
	lv.id_local,
	-- **********************************************************
	lv.nome,
	-- **********************************************************
	(SELECT 
		TOP 1 [status] 
	 FROM status_conexao sc (nolock) 
	 WHERE sc.id_local = lv.id_local 
	 ORDER BY id_status_conexao DESC) as [status],
	 -- **********************************************************
	(SELECT 
		TOP 1 [data_atualizacao]
	 FROM status_conexao sc (nolock) 
	 WHERE sc.id_local = lv.id_local 
	 ORDER BY id_status_conexao DESC) as [data_atualizacao],
	-- **********************************************************
	(SELECT 
		TOP 1 [ip]
	 FROM status_conexao sc (nolock) 
	 WHERE	sc.id_local = lv.id_local 
		AND	sc.status = 1
	 ORDER BY id_status_conexao DESC) as [ip]
FROM local_vigente lv (nolock)


