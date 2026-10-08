

CREATE FUNCTION [dbo].[fcn_getImagensProcessamentoHTML](
	@id_processo int, @consistentes bit, @inconsistentes bit 
)
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @nome_processo NVARCHAR(50)

	SELECT 
		@nome_processo = (RTRIM(nome) +	CASE 
											WHEN @consistentes = 1 AND @inconsistentes = 0 
												THEN ' Consistente'
											WHEN @consistentes = 0 AND @inconsistentes = 1
												THEN ' Inconsistente'
											ELSE ''
										END
						)
	 FROM 
		processo (nolock)
	 WHERE 
		id_processo = @id_processo
	
	DECLARE @tableHTML NVARCHAR(MAX)

	SET @tableHTML = ''

	SET @tableHTML =	N'<b>Processo: ' + @nome_processo + '</b>' +
						N'<table border="1">' +
						N'<tr><th>Data</th><th>Total</th>' +
						ISNULL(	CAST ( (SELECT 
											td = Data, '',
											td = Total, ''
										FROM fcn_ImagensProcessamento(@id_processo , @consistentes , @inconsistentes) 
										ORDER BY Data 
										FOR XML PATH('tr'), TYPE 
										) AS NVARCHAR(MAX) 
									) +
								CAST ( (SELECT 
											td = 'TOTAL', '',
											td = sum(Total), ''
										FROM fcn_ImagensProcessamento(@id_processo , @consistentes , @inconsistentes) 
										--ORDER BY Data 
										FOR XML PATH('tr'), TYPE 
										) AS NVARCHAR(MAX) 
									)
								,''
								) + N'</table>' 
		
	RETURN @tableHTML
	
END



