CREATE PROCEDURE [dbo].[spu_EnviaEmailAtualizacaoSoftware]
AS
	DECLARE @html NVARCHAR(MAX), @data DATETIME

	SET @data = GETDATE() -7

	SET @html = (SELECT dbo.fcn_getAlertaEventoHTML('Alerta de locais com atualização de software (de '+CONVERT(char(11),@data,103)+' até agora).', 12, @data,default))

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[GTW] Atualização de software'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'


