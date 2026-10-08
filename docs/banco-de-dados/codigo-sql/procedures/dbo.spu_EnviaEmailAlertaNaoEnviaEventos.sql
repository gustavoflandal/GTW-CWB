CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaNaoEnviaEventos]
AS
	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT dbo.fcn_getAlertaNaoEnviaEventosHTML())

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Locais que não estão gerando eventos'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 




