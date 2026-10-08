

CREATE PROCEDURE [dbo].[spu_EnviaEmailParamMetroDivergentes]
AS

	DECLARE @html NVARCHAR(MAX), @data DATETIME

	SET @data = GETDATE() -1

	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta de locais com parâmetros metrológicos divergentes (de '+CONVERT(char(11),@data,103)+' até agora).', 34, @data, default))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Locais com parâmetros metrológicos divergentes'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'



