CREATE FUNCTION [dbo].[fcn_getRelatorioEventoManual](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
	(
		--DECLARE @dataInicio DATE = '2015-12-14', @dataFim DATE = '2015-12-14'
		SELECT TOP 100 PERCENT
			   ec.id AS [Nº]
			  ,proprietario.proprietario AS [Série Equipamento]
			  ,ec.data_hora AS [Data/Hora]
			  --,ecde.id_evento
			  ,ecde.evento AS [Desc. Evento]
			  --,ecdc.id_categoria
			  ,ecdc.categoria AS [Categoria]
			  --,ecdn.id_nivel
			  ,ecdn.nivel AS [Nível]
			  --,ecdp.id_prioridade
			  ,ecdp.prioridade AS [Prioridade]
			  ,ec.mensagem AS [Mensagem]
			  ,ec.usuario AS [Usuário]
		FROM   eventos_csx ec (NOLOCK)
			   INNER JOIN eventos_csx_desc_evento ecde (NOLOCK)
					ON  ecde.id_evento = ec.id_evento
			   INNER JOIN eventos_csx_desc_categoria ecdc (NOLOCK)
					ON  ecdc.id_categoria = ec.id_categoria
			   INNER JOIN eventos_csx_desc_nivel ecdn (NOLOCK)
					ON  ecdn.id_nivel = ec.id_nivel
			   INNER JOIN eventos_csx_desc_prioridade ecdp (NOLOCK)
					ON  ecdp.id_prioridade = ec.id_prioridade
			   INNER JOIN eventos_csx_desc_proprietario proprietario (NOLOCK)
					ON  proprietario.id_proprietario = ec.id_proprietario
			   INNER JOIN local_vigente lv
					ON  lv.serie_equipamento = proprietario.proprietario
		WHERE  CAST(ec.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim
			   AND ISNUMERIC(proprietario.proprietario) = 1
			   AND evento_manual = 1
		ORDER BY
			   proprietario.proprietario
			  ,ec.id
			  ,ec.data_hora

	)

