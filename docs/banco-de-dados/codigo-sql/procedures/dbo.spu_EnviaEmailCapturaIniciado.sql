

CREATE PROCEDURE [dbo].[spu_EnviaEmailCapturaIniciado]
AS
	DECLARE @html NVARCHAR(MAX), @data DATETIME

	SET @data = GETDATE() -1

	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta "Captura sendo iniciado"  (de '+CONVERT(char(11),@data,103)+' até agora).', 2, @data, default))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Captura sendo iniciado'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'




