
--DECLARE @data_ini datetime = '2015-10-15 00:00:00', @data_fim datetime = '2015-10-30 23:59:59'

--SELECT p.id_processo, p.nome, 
--MIN(i.id_infracao) id_infracao_inicial, 
--MIN(i.data) data_inicial,
--MAX(i.id_infracao) id_infracao_final, 
--MAX(i.data) data_final,
--COUNT(*) AS conta FROM infracao i (NOLOCK)
--JOIN processo p (NOLOCK) ON i.id_processo = p.id_processo 
----LEFT JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao 
--WHERE i.data BETWEEN @data_ini AND @data_fim 
----AND ir.id_infracao IS NULL -- não estão em Movimento de Lote
--GROUP BY p.id_processo, p.nome

--EXEC spu_estado_processamento 4444, NULL, NULL

CREATE PROCEDURE [dbo].[spu_estado_processamento]
	@id_local int,
	@data_ini datetime,
	@data_fim datetime
AS
BEGIN

	SET NOCOUNT ON

	SELECT p.id_processo, p.nome, 
	MIN(i.id_infracao) id_infracao_inicial, 
	MIN(i.data) data_inicial,
	MAX(i.id_infracao) id_infracao_final, 
	MAX(i.data) data_final,
	COUNT(*) AS conta FROM infracao i (NOLOCK)
	JOIN processo p (NOLOCK) ON i.id_processo = p.id_processo 
	--LEFT JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao 
	WHERE i.data BETWEEN @data_ini AND @data_fim 
	--AND ir.id_infracao IS NULL -- não estão em Movimento de Lote
	GROUP BY p.id_processo, p.nome

	--DECLARE @id_local int, @data_ini datetime, @data_fim datetime
	/*
	SELECT 
		p.id_processo,
		p.nome,
		i1.id_infracao as id_infracao_inicial,
		i1.data as data_inicial,
		i2.id_infracao as id_infracao_final,
		i2.data as data_final,
		count(*) as conta 
	FROM processo p (nolock), 
		infracao i (nolock), 
		infracao i1 (nolock), 
		infracao i2 (nolock)
	WHERE	i.data BETWEEN @data_ini AND @data_fim 
		AND i.id_processo = p.id_processo 
		AND NOT i.id_infracao IN (	SELECT 
										id_infracao 
									FROM 
										infracao_remessa (nolock) 
									WHERE infracao_remessa.id_infracao = i.id_infracao
								) 
		AND (@id_local = 0 OR i.id_local = @id_local) 
		AND	i1.id_infracao = (	SELECT top 1 
									id_infracao 
								FROM 
									infracao _i (nolock)
								WHERE	_i.data BETWEEN @data_ini AND @data_fim 
									AND _i.id_processo = p.id_processo 
									AND	NOT _i.id_infracao IN (	SELECT 
																	id_infracao 
																FROM 
																	infracao_remessa (nolock) 
																WHERE	
																	infracao_remessa.id_infracao = _i.id_infracao) 
									AND (@id_local = 0 OR _i.id_local = @id_local)
								ORDER BY data
							) 
		AND i2.id_infracao = (	SELECT top 1 
									id_infracao 
								FROM 
									infracao _i (nolock)
								WHERE	_i.data BETWEEN @data_ini AND @data_fim 
									AND _i.id_processo = p.id_processo 
									AND	NOT _i.id_infracao IN (	SELECT 
																	id_infracao 
																FROM 
																	infracao_remessa (nolock) 
																WHERE 
																	infracao_remessa.id_infracao = _i.id_infracao) 
									AND (@id_local = 0 OR _i.id_local = @id_local)
								ORDER BY 
									data desc
							)
	GROUP BY
		p.id_processo,
		p.nome,
		i1.id_infracao,
		i1.data,
		i2.id_infracao,
		i2.data
	ORDER BY p.id_processo
	*/
END




