
CREATE PROCEDURE [dbo].[spu_ProcessarAutomatico] @id_processo INT AS 

DECLARE @id_infracao INT
DECLARE @id_usuario INT = 2
DECLARE @id_inconsistencia INT
DECLARE @id_imagem INT

DECLARE @continuar INT = 1
DECLARE @contador INT = 1
DECLARE @total_infracoes INT 

SELECT 
	@total_infracoes = COUNT(id_infracao) 
FROM 
	infracao (nolock) 
WHERE	id_inconsistencia = 40 
	AND id_processo = @id_processo 
	AND data > '2013-11-28 14:00:00' 

WHILE @continuar > 0 AND @contador <= @total_infracoes 

	BEGIN

	SELECT TOP 1 
		@id_infracao = i.id_infracao, 
		@id_inconsistencia = i.id_inconsistencia, 
		@id_imagem = ii.id_imagem_obj 
	FROM infracao i (nolock) 
		INNER JOIN infracao_imagem ii (nolock) 
			ON i.id_infracao = ii.id_infracao 
	WHERE	id_inconsistencia = 40 
		AND id_processo = @id_processo 
		AND data > '2013-11-28 14:00:00' 

	SET @continuar = @@ROWCOUNT
	SET @contador = @contador + 1

	IF @continuar > 0

		BEGIN

			EXEC spu_processa_infracao 
				@id_infracao, 
				@id_usuario, 
				@id_processo, 
				@id_inconsistencia, 
				@id_imagem, 
				0, 
				0,
				0, 
				0, 
				0, 
				0, 
				0, 
				NULL, 
				0, 
				0 

			EXEC spu_status_infracao 
				@id_infracao 

		END 

END


