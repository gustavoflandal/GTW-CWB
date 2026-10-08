

CREATE PROCEDURE [dbo].[spu_info_status_conexao]
	@serie_equipamento int = 0
AS

	DECLARE @STATUS_CONEXAO INT
	DECLARE @STATUS_DESCONEXAO INT

	SET @STATUS_CONEXAO = 1
	SET @STATUS_DESCONEXAO = 2

	SELECT 
		l.id_local, 
		ce.serie_equipamento AS serie,
		(
		SELECT TOP 1 
			sc_c.data_atualizacao
		FROM 
			status_conexao sc_c (nolock)
		WHERE	sc_c.status = @STATUS_CONEXAO 
			AND sc_c.id_local = l.id_local
		ORDER BY sc_c.data_atualizacao DESC 
		) as DataConexao,
		(
		SELECT TOP 1 
			sc_c.data_atualizacao
		FROM 
			status_conexao sc_c (nolock)
		WHERE	sc_c.status = @STATUS_DESCONEXAO 
			AND sc_c.id_local = l.id_local
		ORDER BY 
			sc_c.data_atualizacao DESC 
		) as DataDesconexao,
		(
		SELECT TOP 1 
			sc_c.IP
		FROM 
			status_conexao sc_c (nolock)
		WHERE	sc_c.status = @STATUS_CONEXAO 
			AND sc_c.id_local = l.id_local
		ORDER BY 
			sc_c.data_atualizacao DESC 
		) as IP
	FROM 	local_vigente l (nolock)
		INNER JOIN configuracao_equipamento ce (nolock)
			ON ce.id_configuracao_equipamento = l.id_configuracao_equipamento
	WHERE	ce.serie_equipamento = @serie_equipamento 
		OR	@serie_equipamento = 0



