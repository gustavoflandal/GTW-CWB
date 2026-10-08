CREATE FUNCTION [dbo].[fcn_getHistoricoManutencaoHTML]()
RETURNS NVARCHAR(MAX)
AS
BEGIN

	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT
					N'<b>Histórico de manutenções abertas/fechadas (últimos 3 dias)</b>' +
					N'<table border="1">' +
					N'<tr>'+
					N'	<th>Proprietário</th>'+
					N'	<th>Dia</th>'+
					N'	<th>Manutenções Abertas</th>'+
					N'	<th>Manutenções Fechadas</th>' +
					CAST ( (SELECT	
								td = [proprietario], 
								'',
								td = [dia] , 
								'',
								td = [manutencoes_abertas],
								'',
								td = [manutencoes_fechadas]
							FROM 
								fcn_getHistoricoManutencao()
							ORDER BY 
								dia DESC
							FOR XML PATH('tr'), TYPE 
							) AS NVARCHAR(MAX) 
						) + N'</table>')

	RETURN @html

END



