
CREATE PROCEDURE [dbo].[spu_EnviaEmailAlertaSemVeiculosValidos]
AS

	DECLARE @ontem DATETIME
	DECLARE @assuntoEmail NVARCHAR(MAX)
	DECLARE @emails NVARCHAR(MAX)
	DECLARE @html NVARCHAR(MAX)
	DECLARE @qtdEventos INT

	SET @ontem = DATEADD(dd, -1, GETDATE())

	SET @qtdEventos = (	SELECT 
							COUNT(*) 
						FROM 
							eventos_csx (nolock)
						WHERE	id_evento = 36 -- Nenhum veículo válido detectado.
							AND data_hora > @ontem)

	IF (@qtdEventos > 1)

		BEGIN

			SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

			SET @assuntoEmail = '[' + (SELECT dbo.fcn_getNomeContrato()) + ']'
				+ '[ALERTA] Nenhum veículo válido detectado'

			SET @html = (SELECT
							N'<b>Alerta: Nenhum veículo válido detectado (nas últimas 24 horas)</b><br/><br/>' +
							N'<table border="1">' +
							N'<tr><th>Proprietario</th><th>Pista</th><th>Qtd. desligamentos</th>' +
							CAST ( (SELECT
										td = dp.[proprietario], '',
										td = sub.[pista], '',
										td = sub.[qtd]
									FROM (	SELECT
												e.id_proprietario,
												SUBSTRING(e.mensagem, 65, 1) AS pista,
												COUNT (*) AS qtd
											FROM
												eventos_csx e (nolock)
											WHERE	e.id_evento = 36 -- Nenhum veículo válido detectado.
												AND data_hora > @ontem
											GROUP BY
												e.id_proprietario,
												SUBSTRING(e.mensagem, 65, 1)
											) AS sub
										INNER JOIN eventos_csx_desc_proprietario dp (nolock)
											ON dp.id_proprietario = sub.id_proprietario						
									ORDER BY
										1 ASC, 3 ASC
									FOR XML PATH('tr'), TYPE 
									) AS NVARCHAR(MAX) ) + N'</table>'
						)

			EXEC msdb.dbo.sp_send_dbmail 
				@recipients = @emails,
				@subject = @assuntoEmail,
				@body = @html,
				@body_format = 'HTML' 	
	
		END



