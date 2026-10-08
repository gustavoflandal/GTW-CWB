
--DROP PROCEDURE spu_movimento_inserir
--GO

CREATE PROCEDURE [dbo].[spu_movimento_inserir] 
	 @id_evento_conexao INT,
	 @id_equipamento INT,
	 @placa CHAR(7),
	 @data_movimento  DATETIME,
	 @data_recebido   DATETIME
AS
BEGIN

INSERT INTO pmesp_movimento
           (id_evento_conexao,id_equipamento,placa,data_movimento,data_recebido)
     VALUES
           (@id_evento_conexao,@id_equipamento,@placa,@data_movimento,@data_recebido)

RETURN CAST(SCOPE_IDENTITY() AS BIGINT)

END

