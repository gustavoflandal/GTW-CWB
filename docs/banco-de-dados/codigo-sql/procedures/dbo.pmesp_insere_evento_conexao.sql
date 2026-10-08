
--DROP PROCEDURE pmesp_insere_evento_conexao
--GO

CREATE PROCEDURE [dbo].[pmesp_insere_evento_conexao] 
	 @data_conexao DATETIME,
	 @endereco_ip VARCHAR(15)
AS
BEGIN

INSERT INTO pmesp_evento_conexao
           (data_conexao, endereco_ip)
     VALUES
           (@data_conexao, @endereco_ip)

RETURN CAST(SCOPE_IDENTITY() AS INT)

END

