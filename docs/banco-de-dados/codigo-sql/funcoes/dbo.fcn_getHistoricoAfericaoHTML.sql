
CREATE FUNCTION [dbo].[fcn_getHistoricoAfericaoHTML]()
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT
					N'<b>Histórico de aferições (últimos 3 dias)</b>' +
					N'<table border="1">' +
					N'<tr>'+
					N'	<th>Proprietário</th>'+
					N'	<th>Dia</th>'+
					N'	<th>Modo aferição ligado</th>'+
					N'	<th>Modo aferição desligado</th>' +
					CAST ( (SELECT	
								td = [proprietario], 
								'',
								td = [dia] , 
								'',
								td = [modo_afericao_ligado],
								'',
								td = [modo_afericao_desligado]
							FROM 
								fcn_getHistoricoAfericao()
							ORDER BY 
								dia DESC
							FOR XML PATH('tr'), TYPE 
							) AS NVARCHAR(MAX) 
						) + N'</table>')

	RETURN @html

END



