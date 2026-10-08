
CREATE FUNCTION [dbo].[fcn_getProximasAfericoesHTML]()
RETURNS NVARCHAR(MAX)
AS

BEGIN

	DECLARE @html NVARCHAR(MAX)

	SET @html = (SELECT	N'<b>Próximas aferições (próximos 45 dias)</b>' +
						N'<table border="1">' +
						N'<tr>'+
						N'	<th>Local</th>'+
						N'	<th>Nome Local</th>'+
						N'	<th>Pista</th>'+
						N'	<th>Nome Pista</th>' +
						N'	<th>Última Aferição</th>' +
						N'	<th>Validade</th>' +
						CAST ( (SELECT	
									td = [id_local], 
									'',
									td = [nome_local] , 
									'',
									td = [id_pista] , 
									'',
									td = [nome_pista] , 
									'',
									td = convert(CHAR(10),[data_ultima_afericao], 103) , 
									'',
									td = convert(CHAR(10),[data_validade], 103)
								FROM fcn_getProximasAfericoes()
								ORDER BY data_validade, id_local, id_pista
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) + N'</table>')

	RETURN @html

END





