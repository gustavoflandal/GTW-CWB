CREATE VIEW [dbo].[eventos_csx_pesquisa_CAV]
AS
	
	SELECT ec.id
		  ,dpro.proprietario
		  ,ec.data_hora
		  ,ec.id_categoria
		  ,ec.id_evento
		  ,ec.mensagem
		  ,ec.id_prioridade
		  ,ec.id_nivel
		  ,ca.categoria
		  ,dp.prioridade
		  ,de.evento
		  ,dn.nivel
		  ,ec.usuario
	FROM   eventos_csx_CAV ec (NOLOCK)
		   LEFT JOIN dbo.fcn_getEventosCsxDescCategoriaCAV() ca
				ON  ec.id_categoria = ca.id_categoria
		   LEFT JOIN dbo.fcn_getEventosCsxDescEventoCAV() de
				ON  ec.id_evento = de.id_evento
		   LEFT JOIN dbo.fcn_getEventosCsxDescNivelCAV() dn
				ON  ec.id_nivel = dn.id_nivel
		   LEFT JOIN dbo.fcn_getEventosCsxDescPrioridadeCAV() dp
				ON	ec.id_prioridade = dp.id_prioridade
		   LEFT JOIN dbo.fcn_getEventosCsxDescProprietarioCAV() dpro
				ON  ec.id_proprietario = dpro.id_proprietario
	
	UNION
	
	SELECT el.id
		  ,el.proprietario
		  ,el.data_hora
		  ,el.id_categoria
		  ,el.id_evento
		  ,el.mensagem
		  ,el.id_prioridade
		  ,el.id_nivel
		  ,ca.categoria
		  ,dp.prioridade
		  ,de.evento
		  ,dn.nivel
		  ,el.usuario
	FROM   eventos_csx_legacy el (NOLOCK)
		   LEFT JOIN eventos_csx_desc_categoria ca (NOLOCK)
				ON  el.id_categoria = ca.id_categoria
		   LEFT JOIN eventos_csx_desc_evento de (NOLOCK)
				ON  el.id_evento = de.id_evento
		   LEFT JOIN eventos_csx_desc_nivel dn (NOLOCK)
				ON  el.id_nivel = dn.id_nivel
		   LEFT JOIN eventos_csx_desc_prioridade dp (NOLOCK)
				ON  el.id_prioridade = dp.id_prioridade

