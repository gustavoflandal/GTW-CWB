CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaLocaisConexaoInstavel]
AS

	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT dbo.fcn_getAlertaLocaisConexaoInstavelHTML())

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[ALERTA] Locais com Conexão Instável'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 




