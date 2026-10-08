

CREATE FUNCTION [dbo].[fcn_getAlertaLocaisOfflineHTML]()
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN
	DECLARE @tableHTML NVARCHAR(MAX)

	SET @tableHTML =	N'<b>Alerta de Locais OffLine</b>' +
						N'<table border="1">' +
						N'<tr><th>ID Local</th><th>Nome Local</th><th>Desconectado em</th><th>Tempo Desconectado</th>' +
						CAST ( (SELECT 
									td = [id_local], 
									'',
									td = [nome], 
									'',
									td = CONVERT(char(20), [Desconectado_em] , 113), 
									'',
									td = [Tempo_Desconectado], 
									''
								FROM 
									fcn_getAlertaLocaisOffLine()
								ORDER BY 
									1
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 
	
	RETURN @tableHTML

END



