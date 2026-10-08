CREATE FUNCTION [dbo].[fcn_getRelatorioDadosModem](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		--DECLARE @dataInicio DATE = '2015-11-27', @dataFim DATE = '2015-12-03'
		SELECT ROW_NUMBER() OVER(ORDER BY id_local) AS ordem,
			   *
		FROM   (
					SELECT lv.id_local
						  ,lv.serie_equipamento
						  ,CAST(ecde.id_evento AS VARCHAR(4)) + ' - ' + LTRIM(RTRIM(ecde.evento)) AS evento
						  ,LTRIM(RTRIM(ec.mensagem)) AS mensagem
					FROM   eventos_csx ec (NOLOCK)
						   INNER JOIN (
								SELECT MAX(aux.id) AS id
									  ,aux.id_proprietario
									  ,aux.id_evento
								FROM   eventos_csx aux (NOLOCK)
								WHERE  aux.id_evento IN (4003, 4004, 4005, 4006, 4007)
								GROUP BY
									   aux.id_proprietario
									  ,aux.id_evento
						   ) maior_evento_proprietario
								ON  maior_evento_proprietario.id = ec.id
						   INNER JOIN eventos_csx_desc_evento ecde (NOLOCK)
								ON  ecde.id_evento = ec.id_evento
						   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
								ON  ecdp.id_proprietario = ec.id_proprietario
						   INNER JOIN local_vigente lv (NOLOCK)
								ON  lv.serie_equipamento = ecdp.proprietario
					WHERE  ec.id_evento IN (4003,4004,4005,4006,4007)
						   AND ISNUMERIC(ecdp.proprietario) = 1
					GROUP BY
						   lv.id_local
						  ,lv.serie_equipamento
						  ,ecde.id_evento
						  ,ecde.evento
						  ,ec.mensagem
		) AS dados
		PIVOT (
				MAX(dados.mensagem)
				FOR evento IN ([4003 - Modem 3G detectado],[4004 - Número do telefone do modem 3G.],[4005 - Número IMEI do modem 3G.],[4006 - Nome da operadora do modem 3G.],[4007 - ICCID do Sim Card.])
			  ) AS contagem_dados
	)
