CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaExcessoEventos]
AS

	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT dbo.fcn_getAlertaExcessoEventosHTML())

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())
	
    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Locais gerando eventos em excesso'


	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 




