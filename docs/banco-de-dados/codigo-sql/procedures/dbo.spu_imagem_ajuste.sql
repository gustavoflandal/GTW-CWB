
CREATE PROCEDURE [dbo].[spu_imagem_ajuste]
	@idImagem INT,
	@brilho INT,
	@contraste FLOAT
AS 

DELETE imagem_ajuste WHERE id_imagem = @idImagem 

INSERT INTO imagem_ajuste(id_imagem, brilho, contraste) VALUES (@idImagem, @brilho, @contraste)

