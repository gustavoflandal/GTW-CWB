
CREATE PROCEDURE [dbo].[spu_EnviaEmailImportacaoAtrasada]
AS

	DECLARE @html NVARCHAR(MAX)
	DECLARE @ultimoVeiculo DATETIME
	SET @ultimoVeiculo = GETDATE() -- considerar agora como última importação


	SET @html =	N'<b>Último veículo (geral) importado:</b> ' + CONVERT(CHAR(10), @ultimoVeiculo, 103) + ' '
				+ CONVERT(CHAR(12), @ultimoVeiculo, 114) + N'' +
				N'<br>Locais com importação atrasada (mais que 24 horas em relação ao último veículo):' + 
				N'<br><table border="1">' +
				N'<tr><th>Local</th><th>Pista</th><th>Data último veículo</th>' +
				CAST ((	SELECT
							td = sub.id_local, 
							'',
							td = sub.pista, 
							'',
							td = CONVERT(CHAR(10), sub.data, 103) + ' ',
							+ CONVERT(CHAR(12), sub.data, 114) + ''				
							FROM (	SELECT
										vp.id_local, vp.pista, MAX(vp.data) AS data
									FROM veiculo_pesquisa vp (nolock)
										INNER JOIN local_vigente lv (nolock)
											ON lv.id_local = vp.id_local 
										INNER JOIN configuracao_equipamento_pista cep (nolock)
											ON	cep.id_configuracao_equipamento = lv.id_configuracao_equipamento
											AND cep.id_pista = vp.pista
									WHERE 
										lv.data_inicio <= GetDate() -- locais em funcionamento
									GROUP BY
										vp.id_local,
										vp.pista
									HAVING MAX(data) <= DATEADD(DAY, -1, @ultimoVeiculo)
								) AS sub
							ORDER BY
								sub.data DESC, 
								sub.id_local ASC, 
								sub.pista ASC
							FOR XML PATH('tr'), TYPE
						) AS NVARCHAR(MAX)
					) + N'</table>' 

	DECLARE @emails NVARCHAR(MAX)
	
	SET @emails = (SELECT dbo.fcn_getDestinatariosAlerta())	

    DECLARE @assuntoEmail NVARCHAR(MAX) = '['+(SELECT dbo.fcn_getNomeContrato())+']'+
		+ '[Importador] Locais com importação atrasada'

	EXEC msdb.dbo.sp_send_dbmail 
		@recipients = @emails,
		@subject = @assuntoEmail,
		@body = @html,
		@body_format = 'HTML' 



