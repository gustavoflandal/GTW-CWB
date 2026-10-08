

CREATE PROCEDURE [dbo].[spu_EnviaEmailFalhaSequenciaImagem]
AS

	DECLARE @html NVARCHAR(MAX), 
			@ontem DATE, 
			@dataMaisAntiga DATE

	SET @html = N''

	SET @ontem = CAST(DATEADD(dd, -1, GetDate()) AS DATE)

	-- Primeira parte: buracos na sequencia	
	SET @dataMaisAntiga = (	SELECT 
								MIN([data_imagem_antes])
	 						FROM falha_sequencia_imagem t1 (nolock)
								INNER JOIN local_vigente lv (nolock)
								 ON lv.id_local = t1.id_local
							WHERE	id_imagem_local_antes < id_imagem_local_depois
								AND CAST([data_imagem_antes] AS DATE) BETWEEN CAST(DATEADD(dd, -12, GetDate()) AS DATE) AND @ontem
						   )

	IF (@dataMaisAntiga IS NOT NULL)

		BEGIN

			SET @html = @html + N'<b>Equipamentos com espaços na sequência de imagens:</b>'
			SET @html = @html +  N'</table><br>Quantidade Imagens Faltando: '
					+ CONVERT (char(10), (	SELECT 
												SUM([id_imagem_local_depois] - [id_imagem_local_antes] - 1)
											FROM falha_sequencia_imagem t1 (nolock) 
												INNER JOIN local_vigente lv (nolock) 
													ON lv.id_local = t1.id_local
											WHERE	id_imagem_local_antes < id_imagem_local_depois
												AND id_imagem_local_depois > id_imagem_local_antes 
												AND CAST(data_imagem_antes AS DATE) BETWEEN CAST(DATEADD(dd, -12, GetDate()) AS DATE) AND @ontem
											))
			
			SET @html = @html + N'<br><table border="1"><tr><th>ID Local</th>'
						+ N'<th>Id Imagem Local</th><th>Quantidade</th><th>Data Inicio</th><th>'
						+ N'Data Final</th><th>Houve Manutenção</th>' +
						CAST ( (SELECT 
									td = t1.[id_local], 
									'',
									td = '[' + CAST([id_imagem_local_antes] AS char(10)) + ' - ' +
										CAST([id_imagem_local_depois] AS char(10)) + ']', '',
									td =	CASE 
												WHEN([id_imagem_local_depois] > [id_imagem_local_antes]) 
													THEN id_imagem_local_depois - id_imagem_local_antes - 1
												ELSE
													0
											END,
									'',
									td = CONVERT(char(20), [data_imagem_antes] , 120), '',
									td = CONVERT(char(20), [data_imagem_depois] , 120), '',
									td = (	CASE 
												WHEN Exists(SELECT id_evento 
															FROM dbo.eventos_csx_pesquisa ep (nolock)
															WHERE	ep.id_evento in (15,16) 
																AND ep.data_hora >= t1.data_imagem_antes 
																AND ep.data_hora <= t1.data_imagem_depois 
																AND ep.proprietario = CAST(lv.serie_equipamento AS NVARCHAR(7))
															)
													 THEN 
														'SIM'
												 ELSE 
													'NÃO'
											 END
										),
									   ''		 
								FROM falha_sequencia_imagem t1  (nolock)
									INNER JOIN local_vigente lv  (nolock)
											ON lv.id_local = t1.id_local
								WHERE id_imagem_local_antes < id_imagem_local_depois
									AND CAST([data_imagem_antes] AS DATE) BETWEEN CAST(DATEADD(dd, -12, GetDate()) AS DATE) AND @ontem
								ORDER BY 
									CAST(data_imagem_antes AS DATE), 
									5 DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +  N'</table>'
				
			SET @html = @html +  N'<br>Data mais antiga: '
					 + CONVERT (char(20), @dataMaisAntiga, 120)		
						 
		END
	
	-- Segunda parte: falhas "desconhecidas" na sequencia	de imagens
	SET @dataMaisAntiga = (	SELECT 
								MIN([data_imagem_antes])
	 						FROM falha_sequencia_imagem t1  (nolock)
								INNER JOIN local_vigente lv   (nolock)
									ON lv.id_local = t1.id_local
							WHERE	id_imagem_local_antes >= id_imagem_local_depois
								AND CAST([data_imagem_antes] AS DATE) BETWEEN CAST(DATEADD(dd, -12, GetDate()) AS DATE) AND @ontem
						   )

	IF (@dataMaisAntiga IS NOT NULL)

		BEGIN

			SET @html = @html + N'<br><br><br><b>Equipamentos com numeração problemática:</b>'

			SET @html = @html + N'<br><table border="1"><tr><th>ID Local</th>'
					+ N'<th>Id Imagem Local</th><th>Quantidade</th><th>Data Inicio</th><th>'
					+ N'Data Final</th><th>Houve Manutenção</th>' +
					CAST ( (SELECT 
								td = t1.[id_local], 
								'',
								td = '[' + CAST([id_imagem_local_antes] AS char(10)) + ' - ' +
								CAST([id_imagem_local_depois] AS char(10)) + ']', 
								'',
								td =	CASE 
											WHEN([id_imagem_local_depois] > [id_imagem_local_antes]) 
												THEN [id_imagem_local_depois] - [id_imagem_local_antes] - 1
											ELSE
												0
										END, 
								'',
								td = CONVERT(char(20), [data_imagem_antes] , 120), 
								'',
								td = CONVERT(char(20), [data_imagem_depois] , 120), 
								'',
								td = (	CASE 
											WHEN Exists(SELECT id_evento 
														FROM eventos_csx_pesquisa ep (nolock)
														WHERE ep.id_evento in (15,16) 
															AND ep.data_hora >= t1.data_imagem_antes 
															AND ep.data_hora <= t1.data_imagem_depois 
															AND ep.proprietario = CAST(lv.serie_equipamento AS NVARCHAR(7))
														)
										THEN 
											'SIM'
										ELSE 
											'NÃO'
										END)
								, ''		 
							FROM falha_sequencia_imagem t1 (nolock)
								INNER JOIN local_vigente lv (nolock) 
									ON lv.id_local = t1.id_local
							WHERE	id_imagem_local_antes >= id_imagem_local_depois
								AND CAST([data_imagem_antes] AS DATE) BETWEEN CAST(DATEADD(dd, -12, GetDate()) AS DATE) AND @ontem
							ORDER BY 
								CAST(data_imagem_antes AS DATE), 
								5 DESC
							FOR XML PATH('tr'), TYPE 
							) AS NVARCHAR(MAX) 
						) +  N'</table>'
				
			SET @html = @html +  N'<br>Data mais antiga: '
					+ CONVERT (char(20), @dataMaisAntiga, 120)		
						 
		END	
	
	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())	
	
    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[Importador] Relatório Sequência Imagens'

	IF (LEN(@html) > 0)

		BEGIN
	
			--select @tableHTML
			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @html,
				@body_format = 'HTML' 
		
		END



