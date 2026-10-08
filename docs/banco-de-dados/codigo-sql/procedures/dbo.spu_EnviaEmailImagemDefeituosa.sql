

CREATE PROCEDURE [dbo].[spu_EnviaEmailImagemDefeituosa]
AS
	DECLARE @html NVARCHAR(MAX), @data DATETIME
	
	SET @data = GETDATE() -1
	
	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta de locais com imagens defeituosas detectadas (de '+CONVERT(char(11),@data,103)+' até agora).', 25, @data, 1))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())	

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Imagens defeituosas'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'


