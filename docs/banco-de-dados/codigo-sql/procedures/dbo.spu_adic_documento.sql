
CREATE PROCEDURE [dbo].[spu_adic_documento]
	@identificador_externo CHAR(10),
	@nome_arquivo VARCHAR(64),
	@id_classificador INT,
	@id_usuario_criacao INT,
	@md5_documento CHAR(32),
	@conteudo varbinary(max)
AS 

	DECLARE @id_documento INT

	SET NOCOUNT ON 

	INSERT INTO sis_documento with (rowlock) 
			   (identificador_externo,
			   nome_arquivo,
			   id_classificador,
			   data_criacao,
			   id_usuario_criacao)
		 VALUES
			   (@identificador_externo,
			   @nome_arquivo,
			   @id_classificador,
			   GETDATE(),
			   @id_usuario_criacao)

	SET @id_documento = @@IDENTITY

	INSERT INTO sis_documento_conteudo with (rowlock) 
			   (id_documento,
			   md5_documento,
			   conteudo)
		 VALUES
			   (@id_documento,
			   @md5_documento,
			   @conteudo)
			   
	RETURN @id_documento



