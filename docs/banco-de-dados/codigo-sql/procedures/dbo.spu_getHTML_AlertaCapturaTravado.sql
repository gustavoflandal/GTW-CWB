
CREATE PROCEDURE [dbo].[spu_getHTML_AlertaCapturaTravado]
	  @tableHTML  NVARCHAR(MAX) OUTPUT
AS

    SET NOCOUNT ON

	SET @tableHTML =	N'<b>Alerta dos Locais com o software Captura travado e reiniciado</b>' +
						N'<table border="1">' +
						N'<tr><th>[proprietario]</th><th>Evento</th><th>Total</th>' +
						CAST ( (SELECT	
									td = proprietario, 
									'',
									td = Evento , 
									'',
									td = Total
								FROM AlertaCapturaTravado (nolock)
								ORDER BY 3
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 
    
    



