
CREATE FUNCTION [dbo].[fcn_getAlertaFalhaCarregamentoAgendaHTML]()
RETURNS NVARCHAR(MAX)
WITH EXECUTE AS CALLER
AS
BEGIN

    DECLARE @tableHTML NVARCHAR(MAX)

    SET @tableHTML =	N'<b>Alerta de Falha de Carregamento das Agendas das Câmeras (24h)</b>' +
						N'<table border="1">' +
						N'<tr><th>NS</th><th>MENSAGEM</th><th>OCORRENCIAS</th>' +
						CAST ( ( 
								SELECT
									td = e.proprietario, 
									'',
									td = e.mensagem, 
									'',
									td = COUNT(e.id), 
									''
								FROM
									eventos_csx_pesquisa e (nolock)
								WHERE	e.id_evento in (3006)
									AND e.data_hora >= GETDATE() - 1 --últimas 24h
									AND e.proprietario <> ''
								GROUP BY
									e.proprietario,
									e.mensagem
								ORDER BY
									5 DESC
								FOR XML PATH('tr'), TYPE 
								) AS NVARCHAR(MAX) 
							) +	N'</table>' 

    RETURN @tableHTML

END




