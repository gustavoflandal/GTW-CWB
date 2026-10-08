CREATE FUNCTION [dbo].[fcn_getRelReinicializacaoCapturaMedidor](@dataInicio DATE, @dataFim DATE)    
RETURNS TABLE    
AS    
RETURN    
(    
  
	--DECLARE @dataInicio DATE = '2021-04-01', @dataFim DATE = '2021-05-07'
	SELECT ROW_NUMBER() OVER(ORDER BY d.Data, lv.serie_equipamento) AS [Nº],
		   lv.serie_equipamento AS [Nº série equipamento],
		   CONVERT(VARCHAR(10), d.Data, 103) AS [Data],
		   COALESCE(evt.[Captura esta sendo iniciado...], 0) AS [Captura],
		   COALESCE(evt.[Sistema medidor iniciado], 0) AS [Medidor]
	--SELECT *
	FROM   dbo.fcn_ObterDatasPeriodo(@dataInicio, @dataFim) d
		   CROSS JOIN local_vigente lv (NOLOCK)
		   LEFT JOIN (
						SELECT r.serie_equipamento,
							   r.data,
							   SUM(CASE WHEN r.id_evento = 2 THEN 1 ELSE 0 END) AS [Captura esta sendo iniciado...],
							   SUM(CASE WHEN r.id_evento = 9999 THEN 1 ELSE 0 END) AS [Sistema medidor iniciado],
							   COUNT(r.id) AS quantidade
						FROM   (
									SELECT lv.serie_equipamento,
										   CAST(ec.data_hora AS DATE) AS data,
										   CASE WHEN ecde2.id_evento = 9 AND ec.mensagem LIKE 'Socket de Imagens Conectado' THEN 9999 ELSE ecde2.id_evento END AS id_evento,
										   RTRIM(ec.mensagem) AS mensagem,
										   ec.id
									FROM   eventos_csx ec (NOLOCK)
										   INNER JOIN eventos_csx_desc_evento ecde2 (NOLOCK)
												ON  ecde2.id_evento = ec.id_evento
										   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
												ON  ecdp.id_proprietario = ec.id_proprietario
										   INNER JOIN local_vigente lv (NOLOCK)
												ON  lv.serie_equipamento = ecdp.proprietario
									WHERE  ISNUMERIC(ecdp.proprietario) = 1
										   AND CAST(ec.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim
										   AND (
													ecde2.id_evento = 2
													OR
													(ecde2.id_evento = 9 AND ec.mensagem LIKE 'Socket de Imagens Conectado')
											   )
							   ) r
						GROUP BY
							   r.serie_equipamento,
							   r.data
		   ) evt
				ON  evt.serie_equipamento = lv.serie_equipamento
					AND evt.data = d.Data
	WHERE  lv.desativado = 0
		   AND lv.serie_equipamento BETWEEN 2100000 AND 2199999
		   --AND ecde.id_evento IN (1,2,3,9,10,26,37,36)

)  
