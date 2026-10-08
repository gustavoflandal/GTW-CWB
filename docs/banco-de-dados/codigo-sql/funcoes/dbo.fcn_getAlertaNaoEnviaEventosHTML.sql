CREATE FUNCTION [dbo].[fcn_getAlertaNaoEnviaEventosHTML]()
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @tableHTML NVARCHAR(MAX)

	SET @tableHTML =	N'<b>Alerta de Locais que não estão gerando eventos ( a mais de 1 dia )</b>' +
						N'<table border="1">' +
						N'<tr><th>ID Local</th><th>Dias</th>' +
						CAST ( (SELECT 
									td =[proprietario], 
									'',
									td = [dias], 
									''
								FROM 
									fcn_getAlertaNaoEnviaEventos()
								ORDER BY 
									dias DESC
								FOR XML PATH('tr'), TYPE
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 

	RETURN @tableHTML

END



