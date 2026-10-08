CREATE FUNCTION [dbo].[fcn_getRelatorioStatusEwfUwf](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE  
AS  
RETURN  
(  

	--DECLARE @dataInicio DATE = '2020-10-13', @dataFim DATE = '2020-10-13'
	SELECT ROW_NUMBER() OVER(ORDER BY ec.data_hora, lv.serie_equipamento) AS ordem,
		   ec.data_hora,
		   lv.serie_equipamento,
		   LTRIM(RTRIM(REPLACE(ec.mensagem, 'Informação Filtro:', ''))) AS mensagem
	FROM   eventos_csx ec (NOLOCK)
		   INNER JOIN eventos_csx_desc_evento ecde (NOLOCK)
				ON  ecde.id_evento = ec.id_evento
		   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
				ON  ecdp.id_proprietario = ec.id_proprietario
		   INNER JOIN local_vigente lv (NOLOCK)
				ON  lv.serie_equipamento = ecdp.proprietario
	WHERE  ISNUMERIC(ecdp.proprietario) = 1
		   AND lv.desativado = 0
		   AND ecde.id_evento = 52 --> Inventário do equipamento.
		   AND (ec.mensagem LIKE 'Info%Filtro%EWF%' OR ec.mensagem LIKE 'Info%Filtro%UWF%')
		   AND CAST(ec.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim

) 
