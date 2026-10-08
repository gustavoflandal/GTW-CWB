
CREATE PROCEDURE [dbo].[spu_EnviaEmailFalhaArquivosImportados]
AS

	DECLARE @ontem DATE
	DECLARE @deltaDias INT
	
	SET @ontem = CAST(DATEADD(dd, -1, GetDate()) AS DATE)
	SET @deltaDias = -12

	DECLARE @html  NVARCHAR(MAX)
	SET @html =	N'<b>Equipamentos com espaços na sequência de arquivos.</b>' +
				N'<br><b>Período Pesquisado: ' + CONVERT (char(10), DATEADD(dd, @deltaDias, GetDate()), 103) + 
				N' até ' + CONVERT (char(10), @ontem, 103) + N'</b><br>'

	DECLARE @cnt INT

	SET @cnt = (SELECT 
					COUNT (*) 
				FROM
					falha_arquivos_importados
				WHERE 
					CAST([data_arquivo_antes] AS DATE) BETWEEN CAST(DATEADD(dd, @deltaDias, GetDate()) AS DATE) AND @ontem)

	IF @cnt = 0

		SET @html = @html + N'Não existem espaços na sequência de arquivos deste período.'

	ELSE

		IF @cnt > 0

			BEGIN

				SET @html = @html + N'<table border="1">'
							+ N'<tr><th>ID Local</th><th>Id Arquivo Local</th><th>Quantidade</th><th>Data Inicio</th><th>Data Final</th>'
							+ CAST ( (	SELECT 
											td = t1.[id_local], 
											'',
											td = '[' + CAST([numero_arquivo_antes] AS char(10)) + ' - ' +
												CAST([numero_arquivo_depois] AS char(10)) + ']', '',
											td =	CASE 
														WHEN ([numero_arquivo_depois] > [numero_arquivo_antes]) 
															THEN [numero_arquivo_depois] - [numero_arquivo_antes] - 1					  
														ELSE
															0
													END, 
											'',
											td = CONVERT(char(20), [data_arquivo_antes] , 120), '',
											td = CONVERT(char(20), [data_arquivo_depois] , 120), ''
										FROM falha_arquivos_importados t1 (nolock)
										   INNER JOIN local_vigente lv (nolock)
												ON lv.id_local = t1.id_local
										WHERE 
											CAST([data_arquivo_antes] AS DATE) BETWEEN CAST(DATEADD(dd, @deltaDias, GetDate()) AS DATE) AND @ontem
										ORDER BY 
											CAST(data_arquivo_antes AS DATE)
										FOR XML PATH('tr'), TYPE 
										) AS NVARCHAR(MAX) 
									)
							+  N'</table><br>Quantidade Arquivos Faltando: '
							+ CONVERT (char(10), (	SELECT 
														SUM([numero_arquivo_depois] - [numero_arquivo_antes] - 1)
													FROM falha_arquivos_importados t1 (nolock)
														INNER JOIN local_vigente lv (nolock)
															ON lv.id_local = t1.id_local
  													WHERE	[numero_arquivo_depois] > [numero_arquivo_antes]
														AND CAST([data_arquivo_antes] AS DATE) BETWEEN CAST(DATEADD(dd, @deltaDias, GetDate()) AS DATE) AND @ontem)
							) + N'<br>Dos pesquisados, o mais antigo é: '
							+ CONVERT (char(20),(	SELECT 
														MIN([data_arquivo_antes])
													FROM falha_arquivos_importados t1 (nolock)
														INNER JOIN local_vigente lv (nolock)
															ON lv.id_local = t1.id_local
  													WHERE 
														CAST([data_arquivo_antes] AS DATE) BETWEEN CAST(DATEADD(dd, @deltaDias, GetDate()) AS DATE) AND @ontem), 120)		
			END
	
	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[Importador] Relatório Sequência Arquivos'
	
	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 



/****** Object:  StoredProcedure [dbo].[spu_EnviaEmailFalhaSequenciaImagem]    Script Date: 03/26/2010 09:40:11 ******/
SET ANSI_NULLS ON



