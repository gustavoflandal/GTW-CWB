
CREATE PROCEDURE [dbo].[spu_manutencao_comentario_inserir] 
	 @id_manutencao INT
	,@id_usuario	INT
	,@comentario	VARCHAR(300)
AS
BEGIN

INSERT INTO manutencao_comentarios (id_manutencao, id_usuario, comentario) VALUES (@id_manutencao, @id_usuario, @comentario)

RETURN SCOPE_IDENTITY()

END

