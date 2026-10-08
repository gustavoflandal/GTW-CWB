CREATE FUNCTION [dbo].[fcn_getRelatorioTempoVidaCaptura](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE  
AS  
RETURN  
(  

	/*
	* SELECT * FROM eventos_csx_desc_evento ORDER BY id_evento
	* 01 - Captura não foi finalizado corretamente!
	* 02 - Captura esta sendo iniciado...
	* 03 - Captura esta sendo finalizado...
	* 09 - Conectado ao servidor.
	* 10 - Desconectado do servidor.
	*/

	--DECLARE @dataInicio DATE = '2020-10-10', @dataFim DATE = '2020-10-13'
	SELECT ROW_NUMBER() OVER(ORDER BY lv.serie_equipamento, ec.data_hora) AS ordem,
		   lv.serie_equipamento,
		   LTRIM(RTRIM(ecde.evento)) AS evento,
		   LTRIM(RTRIM(ec.mensagem)) AS mensagem,
		   CONVERT(VARCHAR(20), ec.data_hora, 120) AS captura_iniciado,
		   (SELECT CONVERT(VARCHAR(20), MIN(data_hora), 120)
		    FROM   eventos_csx ec_aux (NOLOCK)
				   INNER JOIN eventos_csx_desc_proprietario ecdp_aux (NOLOCK)
						ON  ecdp_aux.id_proprietario = ec_aux.id_proprietario
			WHERE  ISNUMERIC(ecdp_aux.proprietario) = 1
				   AND ec_aux.id != ec.id
				   AND ec_aux.id_evento IN (1,2,3)
				   AND ecdp_aux.id_proprietario = ecdp.id_proprietario
				   AND ec_aux.data_hora > ec.data_hora) AS captura_finalizado
	FROM   eventos_csx ec (NOLOCK)
		   INNER JOIN eventos_csx_desc_evento ecde (NOLOCK)
				ON  ecde.id_evento = ec.id_evento
		   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
				ON  ecdp.id_proprietario = ec.id_proprietario
		   INNER JOIN local_vigente lv (NOLOCK)
				ON  lv.serie_equipamento = ecdp.proprietario
	WHERE  ISNUMERIC(ecdp.proprietario) = 1
		   AND lv.desativado = 0
		   AND ecde.id_evento = 2 --> Captura esta sendo iniciado...
		   AND ec.mensagem LIKE 'Consilux Captura vers%'
		   AND CAST(ec.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim

)
