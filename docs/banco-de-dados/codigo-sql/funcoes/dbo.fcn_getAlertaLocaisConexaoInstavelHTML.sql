CREATE FUNCTION [dbo].[fcn_getAlertaLocaisConexaoInstavelHTML]()
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN
    	
	DECLARE @tableHTML NVARCHAR(MAX)
	
	SET @tableHTML =	N'<b>Alerta de Locais com conexão instável ( mais de 5 desconexões nas últimas 24h )</b>' +
						N'<table border="1">' +
						N'<tr><th>ID Local</th><th>Nome Local</th><th>Núm. Desconexões</th>' +
						CAST ( (SELECT 
									td =[id_local], 
									'',
									td = [nome], 
									'',
									td = [Num. Conexões], 
									''
								FROM 
									fcn_getAlertaLocaisConexaoInstavel()
								ORDER BY 
									[Num. Conexões] DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 
	
	RETURN @tableHTML

END



