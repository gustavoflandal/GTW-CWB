CREATE FUNCTION [dbo].[fcn_getEventosCsxCAV](@data_inicio datetime, @data_fim datetime)
RETURNS TABLE
AS
RETURN
(
-- DECLARE @data_inicio DATETIME, @data_fim DATETIME 
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
					   LEFT JOIN arquivo_log_item_log item_log (NOLOCK)
							ON item_log.id_evento_csx = ec.id 
				WHERE  ec.data_hora BETWEEN @data_inicio AND @data_fim
				AND	   item_log.id_arquivo_log IS NULL 
					   --AND ec.id_evento IN (SELECT DISTINCT id_evento_cai FROM eventos_csx_categoria_x_evento)
			    --GROUP BY
					  -- ec.id
					  --,ec.id_proprietario
					  --,ec.data_hora
					  --,ec.id_categoria
					  --,ec.id_evento
					  --,ec.mensagem
					  --,ec.id_prioridade
					  --,ec.id_nivel
					  --,ec.usuario
)
