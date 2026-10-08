
CREATE PROCEDURE [dbo].[spu_movimento_inserir_2017]
	 @id_movimento BIGINT,
	 @id_evento_conexao INT,
	 @id_equipamento INT,
	 @placa CHAR(7),
	 @data_movimento  DATETIME,
	 @data_recebido   DATETIME,
	 @data_transmitido DATETIME
AS
BEGIN

INSERT INTO pmesp_movimento_2017
           (id_movimento, id_evento_conexao,id_equipamento,placa,data_movimento,data_recebido,data_transmitido)
     VALUES
           (@id_movimento, @id_evento_conexao,@id_equipamento,@placa,@data_movimento,@data_recebido,@data_transmitido)

END
