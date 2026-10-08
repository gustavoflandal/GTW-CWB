CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaCapturaTravado]
AS

	DECLARE @html NVARCHAR(MAX)
	DECLARE @emails NVARCHAR(MAX)
	
	SET @html = (SELECT dbo.fcn_getAlertaCapturaTravadoHTML())

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())
	
    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Locais com o software Captura travado e reiniciado'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 




