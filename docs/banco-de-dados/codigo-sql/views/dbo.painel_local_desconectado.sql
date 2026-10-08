
CREATE VIEW [dbo].[painel_local_desconectado] AS
	SELECT 
		lsc.id_local,
		lsc.data_atualizacao AS [data_ultima_desconexao]
	FROM local_status_conexao lsc (nolock)
	WHERE lsc.status = 2


