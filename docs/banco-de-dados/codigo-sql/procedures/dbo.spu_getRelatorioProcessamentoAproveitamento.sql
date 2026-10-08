CREATE PROCEDURE [dbo].[spu_getRelatorioProcessamentoAproveitamento]
	@data_inicial DATETIME,
	@data_final DATETIME
AS

	--DECLARE @data_inicial DATETIME = '2017-02-01 00:00:00', @data_final DATETIME = '2017-02-10 23:59:59'
	DECLARE @inconsistencias CHAR(100), @result VARCHAR(MAX), @aux CHAR(1), @inicio CHAR(1), @sql_query VARCHAR(MAX),
			@colunas_format VARCHAR(MAX), @ini_colunas_format CHAR(1)
	
	SET NOCOUNT ON;

	SET @inicio = '('
	SET @aux = ','
	SET @ini_colunas_format = ''

	DECLARE inc_cursor CURSOR FOR 
	SELECT inconsitencias.inconsistencia
	FROM   (
				SELECT inc.id_inconsistencia,
					   inc.razao_tecnica,
					   RIGHT('00' + CAST(inc.id_inconsistencia AS VARCHAR(2)), 2) + ' - ' + LTRIM(RTRIM(inc.descricao)) AS inconsistencia
				FROM   inconsistencia inc (NOLOCK)
				WHERE  inc.id_inconsistencia > 0 AND inc.razao_tecnica < 2
				UNION
				SELECT 99 AS id_inconsistencia,
					   0 AS razao_tecnica,
					   'TOTAL NT'
			
				UNION
				SELECT inc.id_inconsistencia,
					   inc.razao_tecnica,
					   RIGHT('00' + CAST(inc.id_inconsistencia AS VARCHAR(2)), 2) + ' - ' + LTRIM(RTRIM(inc.descricao)) AS inconsistencia
				FROM   inconsistencia inc (NOLOCK)
				WHERE  inc.id_inconsistencia > 0 AND inc.razao_tecnica = 2
				UNION
				SELECT 99 id_inconsistencia,
					   2 AS razao_tecnica,
					   'TOTAL PT'
	) AS inconsitencias
	ORDER BY
		   inconsitencias.razao_tecnica,
		   inconsitencias.id_inconsistencia;

	OPEN inc_cursor

	FETCH NEXT FROM inc_cursor 
	INTO @inconsistencias

	WHILE @@FETCH_STATUS = 0
	BEGIN

		SELECT @result = ISNULL(@result, @inicio)  + '[' + LTRIM(RTRIM(@inconsistencias)) + ']' + @aux
		SELECT @colunas_format = ISNULL(@colunas_format, @ini_colunas_format) + 'ISNULL([' + LTRIM(RTRIM(@inconsistencias)) + '], 0) AS [' + LTRIM(RTRIM(@inconsistencias)) + ']' + @aux + 'ISNULL([' + LTRIM(RTRIM(@inconsistencias)) + ']' + ' / CAST(imagens_recebidas_no_CPI AS FLOAT), 0) AS [(%) '+ LTRIM(RTRIM(@inconsistencias)) + ']' + @aux 
    
		FETCH NEXT FROM inc_cursor 
		INTO @inconsistencias

	END 
	CLOSE inc_cursor;
	DEALLOCATE inc_cursor;

	SET @result = @result + ')'
	SET @result = REPLACE(@result, '],)', '])')

	SET @colunas_format = @colunas_format + ')'
	SET @colunas_format = REPLACE(@colunas_format, ',)', '')

	--PRINT (@result)
	--PRINT (@colunas_format)

	SET @sql_query = '
	SELECT id_local,
		   nome,
		   imagens_recebidas_no_CPI,
		   imagens_consistentes_pela_empresa,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(imagens_consistentes_pela_empresa AS FLOAT) / CAST(imagens_recebidas_no_CPI AS FLOAT) END AS porcentagem_imagens_consistentes_pela_empresa,
		   imagens_inconsistentes_pela_empresa,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(imagens_inconsistentes_pela_empresa AS FLOAT) / CAST(imagens_recebidas_no_CPI AS FLOAT) END AS porcentagem_imagens_inconsistentes_pela_empresa,
		   consistente_validas,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(consistente_validas AS FLOAT) / CAST(imagens_consistentes_pela_empresa AS FLOAT) END AS porcentagem_consistente_validas,
		   consistente_invalidas,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(consistente_invalidas AS FLOAT) / CAST(imagens_consistentes_pela_empresa AS FLOAT) END AS porcentagem_consistente_invalidas,
		   inconsistente_validas,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(inconsistente_validas AS FLOAT) / CAST(imagens_inconsistentes_pela_empresa AS FLOAT) END AS porcentagem_inconsistente_validas,
		   inconsistente_invalidas,
		   CASE WHEN imagens_consistentes_pela_empresa = 0 THEN 0 ELSE CAST(inconsistente_invalidas AS FLOAT) / CAST(imagens_inconsistentes_pela_empresa AS FLOAT) END AS porcentagem_inconsistente_invalidas,
		   CAST(consistente_validas + consistente_invalidas_pnt + inconsistente_validas + inconsistente_invalidas_pnt AS FLOAT) / CAST(imagens_recebidas_no_CPI AS FLOAT) AS porcentagem_aproveitamento_operacinal,
		   consistente_invalidas_pnt,
		   inconsistente_invalidas_pnt,
		   invalido_pt,
		   ' + @colunas_format + '
	FROM   dbo.fcn_getRelatorioProcessamentoAproveitamentoTotais('''+CONVERT(VARCHAR(10), @data_inicial, 120)+ ' ' + CONVERT(VARCHAR(10), @data_inicial, 108) + ''','''+CONVERT(VARCHAR(10), @data_final, 120) + ' ' + CONVERT(VARCHAR(10), @data_final, 108) + ''') AS totais
		   LEFT JOIN (
						SELECT l.id_local AS id_local_inc,
							   l.nome AS nome_inc,
							   RIGHT(''00'' + CAST(inc.id_inconsistencia AS VARCHAR(2)), 2) + '' - '' + LTRIM(RTRIM(inc.descricao)) AS inconsistencia,
							   COUNT(*) AS Quantidade
						FROM   infracao inf (NOLOCK)
							   INNER JOIN local l (NOLOCK)
									ON  l.id_local = inf.id_local
										AND l.sequencia_local = inf.sequencia_local
							   INNER JOIN (
											SELECT id_inconsistencia,
												   descricao,
												   CASE WHEN razao_tecnica = 0 THEN ''Não-técnica''
														WHEN razao_tecnica = 2 THEN ''Técnica''
														WHEN razao_tecnica = 1 THEN ''Não-técnica *''
														ELSE NULL END AS Problema,
												   razao_tecnica
											FROM   inconsistencia (NOLOCK)
								) inc
									ON  inf.id_inconsistencia = inc.id_inconsistencia
						WHERE  inf.data BETWEEN ''' + CONVERT(VARCHAR(10), @data_inicial, 120) + ' ' + CONVERT(VARCHAR(10), @data_inicial, 108) + '''' + ' AND ' + '''' +
													CONVERT(VARCHAR(10), @data_final, 120) + ' ' + CONVERT(VARCHAR(10), @data_final, 108) + '''
							   AND (inf.id_processo <> 99 AND inf.id_processo_concluido <> 99)
							   AND id_enquadramento <> 1
							   AND inf.id_inconsistencia > 0
						GROUP BY
							   l.id_local,
							   l.nome,
							   RIGHT(''00'' + CAST(inc.id_inconsistencia AS VARCHAR(2)), 2) + '' - '' + LTRIM(RTRIM(inc.descricao))
						UNION
						SELECT l.id_local AS id_local_inc,
							   l.nome AS nome_inc,
							   ''TOTAL NT'' AS inconsistencia,
							   COUNT(*) AS Quantidade
						FROM   infracao inf (NOLOCK)
							   INNER JOIN local l (NOLOCK)
									ON  l.id_local = inf.id_local
										AND l.sequencia_local = inf.sequencia_local
							   INNER JOIN (
											SELECT id_inconsistencia,
												   descricao,
												   CASE WHEN razao_tecnica = 0 THEN ''Não-técnica''
														WHEN razao_tecnica = 2 THEN ''Técnica''
														WHEN razao_tecnica = 1 THEN ''Não-técnica *''
														ELSE NULL END AS Problema,
												   razao_tecnica
											FROM   inconsistencia (NOLOCK)
								) inc
									ON  inf.id_inconsistencia = inc.id_inconsistencia
						WHERE  inf.data BETWEEN ''' + CONVERT(VARCHAR(10), @data_inicial, 120) + ' ' + CONVERT(VARCHAR(10), @data_inicial, 108) + '''' + ' AND ' + '''' +
													CONVERT(VARCHAR(10), @data_final, 120) + ' ' + CONVERT(VARCHAR(10), @data_final, 108) + '''
							   AND (inf.id_processo <> 99 AND inf.id_processo_concluido <> 99)
							   AND id_enquadramento <> 1
							   AND inf.id_inconsistencia > 0 AND inc.razao_tecnica < 2
						GROUP BY
							   l.id_local,
							   l.nome
						UNION
						SELECT l.id_local AS id_local_inc,
							   l.nome AS nome_inc,
							   ''TOTAL PT'' AS inconsistencia,
							   COUNT(*) AS Quantidade
						FROM   infracao inf (NOLOCK)
							   INNER JOIN local l (NOLOCK)
									ON  l.id_local = inf.id_local
										AND l.sequencia_local = inf.sequencia_local
							   INNER JOIN (
											SELECT id_inconsistencia,
												   descricao,
												   CASE WHEN razao_tecnica = 0 THEN ''Não-técnica''
														WHEN razao_tecnica = 2 THEN ''Técnica''
														WHEN razao_tecnica = 1 THEN ''Não-técnica *''
														ELSE NULL END AS Problema,
												   razao_tecnica
											FROM   inconsistencia (NOLOCK)
								) inc
									ON  inf.id_inconsistencia = inc.id_inconsistencia
						WHERE  inf.data BETWEEN ''' + CONVERT(VARCHAR(10), @data_inicial, 120) + ' ' + CONVERT(VARCHAR(10), @data_inicial, 108) + '''' + ' AND ' + '''' +
													CONVERT(VARCHAR(10), @data_final, 120) + ' ' + CONVERT(VARCHAR(10), @data_final, 108) + '''
							   AND (inf.id_processo <> 99 AND inf.id_processo_concluido <> 99)
							   AND id_enquadramento <> 1
							   AND inf.id_inconsistencia > 0 AND inc.razao_tecnica = 2
						GROUP BY
							   l.id_local,
							   l.nome
		   ) AS inconsist
				ON  inconsist.id_local_inc = totais.id_local
	PIVOT (
			SUM(inconsist.Quantidade)
			FOR inconsistencia IN ' + @result + '
		  ) AS contagem_inconsist
	ORDER BY
		   id_local'

	EXEC(@sql_query)



