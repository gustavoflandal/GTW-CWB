/* VELHO */
CREATE  PROCEDURE [dbo].[spu_insere_arquivo_recebido]
	@serie_equipamento INT,
	@nome_arquivo   VARCHAR(50),
	@data_criacao_arquivo DATETIME,
	@estado_arquivo INT
AS
	

	INSERT INTO 
		[arquivos]

		( [nome_arquivo], [data_recebimento_arquivo], [estado_arquivo])
	VALUES
		( @nome_arquivo, GETDATE(), @estado_arquivo);





