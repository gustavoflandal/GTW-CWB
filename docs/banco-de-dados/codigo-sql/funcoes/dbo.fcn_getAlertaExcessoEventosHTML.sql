

CREATE FUNCTION [dbo].[fcn_getAlertaExcessoEventosHTML]()
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @tableHTML NVARCHAR(MAX)

	SET @tableHTML =	N'<b>Alerta de Locais que estão gerando muitos eventos ( mais de 1200 no dia )</b>' +
						N'<table border="1">' +
						N'<tr><th>ID Local</th><th>Evento</th><th>Nº Eventos</th>' +
						CAST ( (SELECT 
									td =[proprietario], 
									'',
									td = [evento], 
									'',
									td = [total], 
									''
								FROM fcn_getAlertaExcessoEventos(1200)
								ORDER BY total DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 

	RETURN @tableHTML

END



