CREATE PROCEDURE [dbo].[spu_EnviaEmailAproveitamentoImagens]
AS
	DECLARE @html NVARCHAR(MAX)
	
	SET @html = (SELECT dbo.fcn_getAproveitamentoImagens(CAST(DATEADD(DAY, -20, GetDate()) AS DATE)))
	
	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
											+ '[GTW] Relatório Aproveitamento Imagens'
	
	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'



