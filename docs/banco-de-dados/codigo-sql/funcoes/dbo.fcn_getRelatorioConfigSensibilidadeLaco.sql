CREATE FUNCTION [dbo].[fcn_getRelatorioConfigSensibilidadeLaco](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		SELECT ROW_NUMBER() OVER(ORDER BY l.id_local, l.sequencia_local) AS [Ordem],
			   l.serie_equipamento AS [Nº Série],
			   LTRIM(RTRIM(p.descricao)) AS [Tipo equipamento],
			   CONVERT(VARCHAR(10), l.data_inicio, 103) AS [Data inÍcio operação],
			   CONVERT(VARCHAR(10), l.data_atualizacao, 103) + ' ' + CONVERT(VARCHAR(10), l.data_atualizacao, 108) AS [Data atualizacao],
			   cep.id_pista AS [iD. Pista],
			   cs.conector AS [conector],
			   cs.item AS [Canal],
			   cs.modoHabilitar AS [Modo habilitar],
			   cs.modoSensibilidade [Modo sensibilidade],
			   cs.sensibilidadeEntrada AS [Sensibilidade entrada],
			   cs.sensibilidadeSaida AS [Sensibilidade saída],
			   cs.configOscilador AS [Config. Oscilador],
			   cs.eventosMonitorados AS [Eventos monitorados],
			   cs.divisorPerfil AS [Divisor perfil],
			   CASE WHEN status_sinc.id_local IS NULL THEN 'Sem informação de sincronização' ELSE status_sinc.mensagem END AS [Status de sincronização de configuração]
		--SELECT *
		FROM   local_vigente l (NOLOCK)
			   INNER JOIN configuracao_equipamento ce (NOLOCK)
					ON  ce.id_configuracao_equipamento = l.id_configuracao_equipamento
			   INNER JOIN configuracao_equipamento_pista cep (NOLOCK)
					ON  cep.id_configuracao_equipamento = ce.id_configuracao_equipamento
			   INNER JOIN (
							SELECT lv.id_configuracao_equipamento,
								   CASE WHEN cec.id = 1 AND cecc.item IN (0,1) THEN 1
										WHEN cec.id = 1 AND cecc.item IN (2,3) THEN 2
										WHEN cec.id = 2 AND cecc.item IN (0,1) THEN 3
										WHEN cec.id = 2 AND cecc.item IN (2,3) THEN 4
										ELSE NULL
								   END AS conector,
								   cec.id,
								   cec.porta,
								   cec.bitsPorSegundo,
								   cec.bitsDados,
								   cec.bitsParada,
								   cec.paridade,
								   cecc.item,
								   cecc.modoHabilitar,
								   cecc.modoSensibilidade,
								   cecc.sensibilidadeEntrada,
								   cecc.sensibilidadeSaida,
								   cecc.configOscilador,
								   cecc.eventosMonitorados,
								   cecc.divisorPerfil
							FROM   local_vigente lv (NOLOCK)
								   INNER JOIN configuracao_equipamento_controlador cec (NOLOCK)
										ON  cec.id_configuracao_equipamento = lv.id_configuracao_equipamento
								   INNER JOIN configuracao_equipamento_controlador_canais cecc (NOLOCK)
										ON  cecc.id_configuracao_equipamento = cec.id_configuracao_equipamento
											AND cecc.id = cec.id
							--WHERE  lv.id_configuracao_equipamento = 242
			   ) cs
					ON  cs.id_configuracao_equipamento = cep.id_configuracao_equipamento
						AND cs.conector = cep.conector
			   INNER JOIN sis_usuario su (NOLOCK)
					ON  su.id_usuario = ce.id_usuario
			   INNER JOIN produto p
					ON  p.id_produto = ce.id_produto
			   LEFT JOIN (
							SELECT lv.id_local,
								   lv.serie_equipamento,
								   RTRIM(ecde.evento) AS evento,
								   RTRIM(ec.mensagem) AS mensagem,
								   ec.data_hora
							FROM   eventos_csx ec (NOLOCK)
								   INNER JOIN eventos_csx_desc_evento ecde (NOLOCK)
										ON  ecde.id_evento = ec.id_evento
								   INNER JOIN eventos_csx_desc_proprietario ecdp (NOLOCK)
										ON  ecdp.id_proprietario = ec.id_proprietario
								   INNER JOIN local_vigente lv
										ON  lv.serie_equipamento = ecdp.proprietario
								   INNER JOIN (
												SELECT MAX(ec.id) AS id,
													   ec.id_proprietario
												FROM   eventos_csx ec (NOLOCK)
												WHERE  ec.id_evento = 34
													   AND ec.mensagem LIKE '%Sincronizado com medidor%'
												GROUP BY
													   ec.id_proprietario
								   ) e
										ON  e.id = ec.id
							WHERE  ISNUMERIC(ecdp.proprietario) = 1
			   ) AS status_sinc
					ON  status_sinc.id_local = l.id_local
	)
