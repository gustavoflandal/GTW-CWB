CREATE FUNCTION [dbo].[fcn_getErrosRespostaPoolingHTML]
(
	@title nvarchar(max),
    @dataInicio datetime 
)
RETURNS nvarchar(max)
AS
BEGIN

	DECLARE @html nvarchar(max)

	SET @html = (	SELECT
						N'<b>'+@title+'</b>' +
						N'<table border="1">' +
						N'<tr><th>Proprietario</th><th>Evento</th><th>Mensagem</th><th>Total</th>' +
						CAST ( (SELECT	
									td = [proprietario], 
									'',
									td = [evento] , 
									'',
                       				td = [mensagem], 
									'',
									td = [Total]
								FROM fcn_getErrosRespostaPooling(@dataInicio)
								ORDER BY 1 DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>')

	RETURN @html

END



