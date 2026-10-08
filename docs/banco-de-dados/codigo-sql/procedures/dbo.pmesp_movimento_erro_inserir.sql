
--DROP PROCEDURE pmesp_movimento_erro_inserir
--GO

CREATE PROCEDURE [dbo].[pmesp_movimento_erro_inserir]
	 @id_movimento BIGINT
	,@id_equipamento INT
	,@id_evento_conexao INT
	,@codigo INT
	,@placa_retorno CHAR(7)
	,@mensagem VARCHAR(1000)
AS
BEGIN

INSERT INTO pmesp_movimento_erro (id_movimento, id_equipamento, id_evento_conexao, codigo, placa_retorno, mensagem)
VALUES							(@id_movimento,@id_equipamento,@id_evento_conexao,@codigo,@placa_retorno,@mensagem)

RETURN @@rowcount

END

