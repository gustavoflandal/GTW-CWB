CREATE FUNCTION [dbo].[fcn_getAlertaLocaisOffLine]()
RETURNS TABLE
AS
RETURN
(
	SELECT	
		lsc.id_local,
		lsc.nome,
		lsc.data_atualizacao AS [Desconectado_em],
		dbo.TEMPO_DECORRIDO( lsc.data_atualizacao ) AS [Tempo_Desconectado]
	FROM local_status_conexao lsc (nolock)
		INNER JOIN local_vigente lv (nolock) 
			ON lv.id_local = lsc.id_local
	WHERE	status <> 1
		and lsc.data_atualizacao < GETDATE() - '01:00:00'
		and lv.data_inicio <= getdate()
)



