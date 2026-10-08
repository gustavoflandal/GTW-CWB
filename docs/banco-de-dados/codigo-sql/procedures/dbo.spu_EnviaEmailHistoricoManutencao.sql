

CREATE PROCEDURE [dbo].[spu_EnviaEmailHistoricoManutencao]
AS
	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT dbo.fcn_getHistoricoManutencaoHTML())

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())		

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[GTW] Histórico Manutenções Abertas/Fechadas'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'





