
CREATE FUNCTION [dbo].[fcn_getErrosProcessamentoHTML]( @data DATETIME )
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN
	DECLARE @tableHTML NVARCHAR(MAX)
	
	SET @tableHTML =	N'<b>Erros de Processamento: dia ' + convert(NVARCHAR(10), @data , 120 ) + '</b>' +
						N'<table border="1">' +
						N'<tr><th>Data</th><th>id_inconsistencia</th><th>Descrição</th><th>Inconsistentes</th><th>total</th><th>%(erro / total)</th>' +
						CAST ( (SELECT 
									td = [data], 
									'',
									td = CASE 
											WHEN [id_inconsistencia] IS NOT NULL 
												THEN [id_inconsistencia] 
											ELSE 
												'' 
										END, 
										'',
									td = CASE 
											WHEN [descricao] IS NOT NULL 
												THEN [descricao] 
											ELSE 
												'.TOTAL' 
										END, 
										'',
									td = CASE 
											WHEN [conta_inconsistente] IS NOT NULL 
												THEN [conta_inconsistente] 
											ELSE 
												'' 
										END, '',
									td = CASE 
											WHEN [total] IS NOT NULL 
												THEN [total] 
											ELSE 
												'' 
										END, 
										'',
									td = CASE 
											WHEN [%total] IS NOT NULL 
												THEN [%total] 
											ELSE 
												'' 
										END, 
										''
								FROM fcn_getErrosProcessamento( @data )
								ORDER BY 1 desc, 3 desc,5 desc
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX)
							) +	N'</table>'
		
	RETURN @tableHTML

END



