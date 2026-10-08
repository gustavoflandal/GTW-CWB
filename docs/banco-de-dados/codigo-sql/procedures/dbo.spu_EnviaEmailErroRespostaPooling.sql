

CREATE PROCEDURE [dbo].[spu_EnviaEmailErroRespostaPooling]
AS
	DECLARE @html NVARCHAR(MAX), @data DATETIME

	SET @data = GETDATE() -1

	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta de locais com erros de resposta de pooling (de '+CONVERT(char(11),@data,103)+' até agora).', 32, @data, 1))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Erros de resposta de pooling'
	
	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'



