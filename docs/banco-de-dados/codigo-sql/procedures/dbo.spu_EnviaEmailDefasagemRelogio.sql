CREATE PROCEDURE [dbo].[spu_EnviaEmailDefasagemRelogio]
AS
	DECLARE @html NVARCHAR(MAX), @data DATETIME

	SET @data = GETDATE() -1

	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta de locais com defasagem entre relógio local e servidor maior que a tolerância(de '+CONVERT(char(11),@data,103)+' até agora).', 51, @data, 1))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Defasagem entre relógio local e servidor maior que a tolerância'
	
	IF @html <> '' 
		EXEC msdb.dbo.sp_send_dbmail 
			@recipients = @emails,
			@subject = @assuntoEmail,
			@body = @html,
			@body_format = 'HTML'



