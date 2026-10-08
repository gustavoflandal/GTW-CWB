CREATE FUNCTION [dbo].[fcn_getAproveitamentoImagensAgrupado]
  ( @dataLimite date )
RETURNS nvarchar(max)
AS

BEGIN

	DECLARE @resultado NVARCHAR(MAX)

	SET @resultado = N''
	
	DECLARE @ultimoDiaFechado DATE

	SET @ultimoDiaFechado = (SELECT TOP 1
								sub1.data
							FROM (	SELECT 
										CAST(inf.data AS DATE) AS data, COUNT(*) AS qtd
									FROM 
										infracao inf (nolock)
									WHERE	CAST(inf.data AS DATE) >= @dataLimite
										AND inf.id_enquadramento != 1 			
									GROUP BY CAST(inf.data AS DATE)
								) AS sub1
								INNER JOIN (SELECT 
												CAST(inf.data AS DATE) AS data, COUNT(*) AS qtd
											FROM infracao inf (nolock)
												INNER JOIN processo pro (nolock)
													ON pro.id_processo = inf.id_processo
											WHERE	CAST(inf.data AS DATE) >= @dataLimite
												AND pro.ativo = 1 
												AND  pro.id_processo_proximo IS NULL 				
												AND inf.id_enquadramento != 1 			
											GROUP BY CAST(inf.data AS DATE)
											) AS sub2
									ON sub1.data = sub2.data
							WHERE sub1.qtd = sub2.qtd
							ORDER BY sub1.data DESC)

	IF (@ultimoDiaFechado IS NULL)

		BEGIN

			SET @resultado = N'<br><b>Erro:</b> Não foi possível encontrar um dia com processamento'
							+ ' totalmente finalizado que seja posterior à data limite.'
							+ '<br><b>Data Limite:</b> ' + CONVERT(NVARCHAR(20), @dataLimite, 103)

			RETURN @resultado

		END

	DECLARE @inicioJanela DATE

	SET @inicioJanela = DATEADD(dd, -7, @ultimoDiaFechado)
	
	SET @resultado = '<h3>Relatório Agrupado de Aproveitamento Imagens</h3><br><b>'
					+ 'De: ' + CONVERT(CHAR(10), @inicioJanela, 103)  + '</b> <b>até: '
					+ CONVERT(CHAR(10), @ultimoDiaFechado, 103) + ' (último dia processado)</b><br>'

	SET @resultado = @resultado + dbo.fcn_getAproveitamentoImagensAgrupadoPeriodo(@inicioJanela, @ultimoDiaFechado)

    RETURN @resultado

END


