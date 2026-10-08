CREATE PROCEDURE [dbo].[spu_EnviaEmailErrosProcessamento]
AS

	DECLARE @data DATETIME
	DECLARE @first DATETIME

	DECLARE @tableHTML NVARCHAR(MAX)
	DECLARE @html NVARCHAR(MAX)

	DECLARE @ultimaDataProcessada DATETIME
	
	SELECT 
		@ultimaDataProcessada = MAX(data) 
	FROM 
		infracao_processo_finalizada (nolock)

	SET @data = CAST( @ultimaDataProcessada as DATE )
	SET @first = CAST( @ultimaDataProcessada-12 as DATE )
	
	SET @html = ''

	WHILE (@data >= @first)

		BEGIN

			SET @tableHTML = (SELECT dbo.fcn_getErrosProcessamentoHTML( @data ))
		
			IF @tableHTML <> ''
				SET @html = @html + @tableHTML
		
			SET @data = @data - 1

		END

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
										+ '[GTW] Erros de Processamento'

	IF @html <> '' 

		BEGIN

			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @html,
				@body_format = 'HTML'
		
		END

	ELSE

		print 'Email não enviado'




