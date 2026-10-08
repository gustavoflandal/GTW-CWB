CREATE PROCEDURE [dbo].[spu_readic_imagem]
	@id_imagem INT,
	@md5_imagem CHAR(32),
	@conteudo varbinary(max)
AS 

	DECLARE @id_veiculo INT

	SET NOCOUNT ON
	
	IF (NOT EXISTS (SELECT 
						id_imagem 
					FROM 
						imagem_info (nolock)
					WHERE	id_imagem = @id_imagem 
						AND md5 = @md5_imagem))

			RAISERROR('Não foi possível confirmar a existência da imagem via MD5.', 16, 1) 

	IF (EXISTS (SELECT 
					id_imagem 
				FROM 
					imagem (nolock)
				WHERE 
					id_imagem = @id_imagem))

			RAISERROR('Não é possível readicionar uma imagem já existente no BD.', 16, 1)
			
	INSERT INTO imagem with (rowlock) (
		id_imagem, 
		imagem)
	VALUES (
		@id_imagem, 
		@conteudo)

	SELECT TOP 1 
		@id_veiculo = id_veiculo 
	FROM veiculo_imagem (nolock)

	RETURN @id_veiculo




