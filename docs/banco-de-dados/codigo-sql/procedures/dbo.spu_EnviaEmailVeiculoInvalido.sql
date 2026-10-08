
CREATE PROCEDURE [dbo].[spu_EnviaEmailVeiculoInvalido]
AS

	DECLARE @html NVARCHAR(MAX), @data DATETIME, @limiteAceitavel FLOAT

	SET @data = GETDATE() - 1 -- de ontem até hoje

	SET @limiteAceitavel = 0.1 --10%
	
	SET @html = (	SELECT
						N'<b>Alerta de locais com veículos inválidos detectados (de ' + 
						CONVERT(char(11), @data, 103) + ' até agora).' + '</b>' +
						N'<br>Limite tolerado: ' + CONVERT(NVARCHAR(MAX), 100 * @limiteAceitavel) + '%' +
						N'<br><table border="1">' +
						N'<tr><th>Local</th><th>Inválidos</th><th>Total</th><th>Participação (%)</th>' +
						CAST ( (SELECT
									td = sub1.id_local, 
									'',
									td = COALESCE (sub1.QTD_RUIM, 0), 
									'',
									td = COALESCE (sub2.QTD_TOTAL, 0), 
									'',
									td = CONVERT(NVARCHAR(MAX), 100 * (CONVERT(FLOAT, sub1.QTD_RUIM ) / CONVERT(FLOAT, sub2.QTD_TOTAL))) + '%'
								FROM (	SELECT
											lvg.id_local,
											COUNT(evt.id_proprietario) AS QTD_RUIM
										FROM eventos_csx evt (nolock)
											INNER JOIN eventos_csx_desc_proprietario dp (nolock)
												ON evt.id_proprietario = dp.id_proprietario
											INNER JOIN local_vigente lvg (nolock)
												ON dp.proprietario = CONVERT(VARCHAR(50), lvg.serie_equipamento)
											WHERE	evt.id_evento = 24
												AND evt.data_hora >= @data
											GROUP BY 
												lvg.id_local
										)AS sub1
									INNER JOIN (SELECT
													vei.id_local,
													COUNT (*) AS QTD_TOTAL
												FROM
													veiculo_pesquisa vei (nolock)
												WHERE
													vei.data >= @data
												GROUP BY 
													vei.id_local
												) AS sub2
										ON sub1.id_local = sub2.id_local
								WHERE
									CONVERT(FLOAT, sub1.QTD_RUIM) / CONVERT(FLOAT, sub2.QTD_TOTAL) >= @limiteAceitavel
								ORDER BY 
									1 DESC
								FOR XML PATH('tr'), TYPE 

								) AS NVARCHAR(MAX) 
							) + N'</table>')

	DECLARE @emails NVARCHAR(MAX)

	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
										+ '[ALERTA] Veículos inválidos'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML'



