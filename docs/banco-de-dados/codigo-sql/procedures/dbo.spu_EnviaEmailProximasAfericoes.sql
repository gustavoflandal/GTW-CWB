CREATE PROCEDURE [dbo].[spu_EnviaEmailProximasAfericoes]
AS
	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT dbo.fcn_getProximasAfericoesHTML())

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT 'mariana@consilux.com.br ' + dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[GTW] Próximas Aferições'
	
	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'



