

CREATE FUNCTION [dbo].[fcn_getAlertaLocaisConexaoInstavel]()
RETURNS TABLE
AS
RETURN
(
	SELECT TOP 100 PERCENT	
		lv.id_local , 
		lv.nome,
		COUNT(*) as [Num. Conexões]
	FROM status_conexao sc  (nolock)
		INNER JOIN local_vigente lv (nolock) 
			ON lv.id_local = sc.id_local
	WHERE	sc.data_atualizacao > GETDATE() - 1
		AND sc.[status] = 1
	GROUP BY 
		lv.id_local, 
		lv.nome
	HAVING 
		COUNT(*) >= 5
)



