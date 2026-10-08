CREATE FUNCTION [dbo].[fcn_getAlertaEventoHTML] 
(
	@title nvarchar(max), 
	@idEvento int , 
	@dataInicio datetime, 
	@mensagem bit = 0
)
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN

	DECLARE @html nvarchar(max)

	IF @mensagem = 0

		BEGIN

			SET @html = (SELECT
							N'<b>'+@title+'</b>' +
							N'<table border="1">' +
							N'<tr><th>Proprietario</th><th>Evento</th><th>Total</th>' +
							CAST ( (SELECT	
										td = [proprietario], 
										'',
										td = [Evento] , 
										'',
										td = [Total]
									FROM fcn_getAlertaEvento(@idEvento, @dataInicio, @mensagem)
									ORDER BY 5 DESC
									FOR XML PATH('tr'), TYPE 
									) AS NVARCHAR(MAX) 
								) + N'</table>'
						)

		END

	ELSE

		BEGIN

			SET @html = (SELECT
							N'<b>'+@title+'</b>' +
							N'<table border="1">' +
							N'<tr><th>Proprietario</th><th>Evento</th><th>Mensagem</th><th>Total</th>' +
							CAST ( ( 
									SELECT	td = [proprietario], '',
											td = [Evento] , '',
											td = [mensagem], '',
											td = [Total]
									FROM fcn_getAlertaEvento(@idEvento, @dataInicio, @mensagem)
									ORDER BY 7 DESC
									FOR XML PATH('tr'), TYPE 

									) AS NVARCHAR(MAX) 
								) + N'</table>'
						)

		END

	RETURN @html

END



