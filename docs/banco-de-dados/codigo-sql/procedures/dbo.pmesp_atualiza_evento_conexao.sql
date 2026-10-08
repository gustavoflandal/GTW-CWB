
CREATE PROCEDURE [dbo].[pmesp_atualiza_evento_conexao] 
	 @id_evento_conexao INT
	,@id_local INT
	,@data_desconexao DATETIME
	,@movimentos_recebidos INT
	,@movimentos_transmitidos INT
	,@movimentos_invalidos INT
	,@data_ultimo_movimento DATETIME = NULL
AS
BEGIN

UPDATE pmesp_evento_conexao
SET         id_local = @id_local,
			data_desconexao = @data_desconexao,
			movimentos_recebidos = @movimentos_recebidos,
			movimentos_transmitidos = @movimentos_transmitidos,
			movimentos_invalidos = @movimentos_invalidos,
			data_ultimo_movimento = @data_ultimo_movimento,
			data_atualizado = GETDATE()
WHERE		id_evento_conexao		= @id_evento_conexao

RETURN @@rowcount

END

