CREATE VIEW [dbo].[eventos_csx_CAV]
AS

	SELECT ec.id
		  ,ec.id_proprietario
		  ,ec.data_hora
		  ,ecce.id_categoria_cav AS id_categoria
		  ,ec.id_evento
		  ,ec.mensagem
		  ,ec.id_prioridade
		  ,ec.id_nivel
		  ,ec.usuario
	FROM   eventos_csx ec (NOLOCK)
		   INNER JOIN eventos_csx_categoria_x_evento ecce (NOLOCK)
				ON  ecce.id_evento_cai = ec.id_evento
		   INNER JOIN fcn_getEventosCsxDescProprietarioCAV() AS proprietario
				ON  proprietario.id_proprietario = ec.id_proprietario
	
