
CREATE FUNCTION [dbo].[fcn_getProdutividadeOperadoresHTML]()
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN

	DECLARE @tableHTML NVARCHAR(MAX)
	
	SET @tableHTML =	N'<b>Produtividade (Processo dos digitadores por dia e processo)</b>' +
						N'<table border="1">' +
						N'<tr><th>Data</th><th>Nome do Usuário</th><th>Nome do Processo</th></th><th>Infrações Processadas</th>' +
						CAST ( (SELECT 
									td=[data], 
									'',
									td=[Usuario], 
									'',
									td=[Processo], 
									'',
									td=[Total], 
									''
								FROM 
									fcn_getProdutividadeOperadores()
								WHERE 
									[data] >= GETDATE() - 3
								ORDER BY 
									id_processo, 
									data DESC, 
									Total DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) + N'</table>' 
		
	RETURN @tableHTML

END



